package com.example.sannotes.Activities;

import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.app.AppCompatDelegate;
import androidx.appcompat.content.res.AppCompatResources;
import androidx.appcompat.widget.AppCompatButton;
import androidx.appcompat.widget.PopupMenu;
import androidx.cardview.widget.CardView;
import androidx.core.content.ContextCompat;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.annotation.SuppressLint;
import android.app.AlertDialog;
import android.app.Dialog;
import android.content.DialogInterface;
import android.content.SharedPreferences;
import android.database.Cursor;
import android.graphics.drawable.Drawable;
import android.os.Bundle;
import android.view.MenuItem;
import android.view.View;
import android.view.WindowManager;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.example.sannotes.Adapter.Adapter;
import com.example.sannotes.DBHelper;
import com.example.sannotes.ItemModule;
import com.example.sannotes.R;

import org.w3c.dom.Text;

import java.text.DateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.List;

import soup.neumorphism.NeumorphButton;
import soup.neumorphism.NeumorphCardView;

public class MainActivity extends AppCompatActivity {

    List<ItemModule> items = new ArrayList<>();
    Adapter adapter;
    DBHelper dbHelper;
    Dialog dialog;
    EditText notes_title,notes;
    RecyclerView recyclerView;
    ImageButton settings;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN, WindowManager.LayoutParams.FLAG_FULLSCREEN);

        //Get the Activity id's
        recyclerView = findViewById(R.id.recycler_view);
        settings = findViewById(R.id.settings);

        Dialog settingsDialog = new Dialog(this, android.R.style.Theme_DeviceDefault_DayNight);
        settingsDialog.setContentView(R.layout.settings_dialog);
        settingsDialog.getWindow().setLayout(WindowManager.LayoutParams.WRAP_CONTENT, WindowManager.LayoutParams.WRAP_CONTENT);
        settingsDialog.setCanceledOnTouchOutside(true);

        NeumorphButton themeTitle = settingsDialog.findViewById(R.id.themes);
        NeumorphButton viewTitle = settingsDialog.findViewById(R.id.views);

        if (getSharedPreferences("theme", MODE_PRIVATE).getBoolean("dark", false)){
            AppCompatDelegate.setDefaultNightMode(AppCompatDelegate.MODE_NIGHT_YES);
            Toast.makeText(this, "Dark Applied", Toast.LENGTH_SHORT).show();
            drawableButtonChange(themeTitle, "Light Mode", R.drawable.light_mode);
        } else {
            AppCompatDelegate.setDefaultNightMode(AppCompatDelegate.MODE_NIGHT_NO);
            Toast.makeText(this, "Light Applied", Toast.LENGTH_SHORT).show();
            drawableButtonChange(themeTitle, "Dark Mode", R.drawable.dark_mode);
        }

        settings.setOnClickListener(v ->{
            settingsDialog.show();

            settingsDialog.findViewById(R.id.delete_all).setOnClickListener(V -> {
                AlertDialog.Builder builder = new AlertDialog.Builder(MainActivity.this);
                builder.setMessage("Are You Sure Want to Delete All Notes?")
                        .setTitle("Delete All Notes")
                        .setPositiveButton("Yes", (dialogInterface, i) -> {

                            dbHelper = new DBHelper(MainActivity.this);
                            dbHelper.deleteAll();
                            recreate();
                        })
                        .setNegativeButton("No", (dialogInterface, i) -> {})
                        .setCancelable(false)
                        .create().show();
            });

            SharedPreferences.Editor viewsEditor = getSharedPreferences("view", MODE_PRIVATE).edit();
            viewTitle.setOnClickListener(V -> {
                boolean val = !getSharedPreferences("view", MODE_PRIVATE).getBoolean("grid_view", false);
                viewsEditor.putBoolean("grid_view", val).apply();
                Toast.makeText(this, "Clicked", Toast.LENGTH_SHORT).show();
                settingsDialog.dismiss();
                recreate();
            });

            SharedPreferences.Editor themeEditor = getSharedPreferences("theme", MODE_PRIVATE).edit();
            themeTitle.setOnClickListener(V -> {
                boolean val = !getSharedPreferences("theme",MODE_PRIVATE).getBoolean("dark",false);
                themeEditor.putBoolean("dark", val).apply();
                Toast.makeText(this, "Clicked", Toast.LENGTH_SHORT).show();
                settingsDialog.dismiss();
                recreate();
            });

        });

        //For full screen dialog
        dialog = new Dialog(this, android.R.style.Theme_DeviceDefault_DayNight);
        dialog.setContentView(R.layout.new_note);

        //Calender View
        Calendar calendar = Calendar.getInstance();
        String date = DateFormat.getDateInstance(DateFormat.SHORT).format(calendar.getTime());

        dbHelper = new DBHelper(MainActivity.this);
        //New note add button & Listener
        NeumorphCardView add_btn = findViewById(R.id.note_add_btn);
        add_btn.setOnClickListener(v->{
                dialog.show();

                notes_title = dialog.findViewById(R.id.notes_title);
                notes = dialog.findViewById(R.id.notes);

                notes_title.setCursorVisible(true);

                //Note Done or Save button & Listener
                ImageButton done_btn = dialog.findViewById(R.id.note_done);
                done_btn.setOnClickListener(view -> {
                        if(!notes_title.getText().toString().isEmpty()){

                            //insert the inputted data using dbHelper class
                            dbHelper = new DBHelper(MainActivity.this);
                            dbHelper.insertData(notes_title.getText().toString(),notes.getText().toString(), date);

                            notes_title.setText("");
                            notes.setText("");
                            dialog.dismiss();

                            //Refresh the Activity
                            recreate();
                        }else {
                            Toast.makeText(MainActivity.this, "Enter one field", Toast.LENGTH_SHORT).show();
                        }
                });

                //Back button
                ImageButton back_btn = dialog.findViewById(R.id.back_done);
                back_btn.setOnClickListener(view -> {
                        dialog.dismiss();
                });
        });

        //Add the values in Individual Array
        setArraylist();
        set_adapter(viewTitle);

    }

    private void drawableButtonChange(NeumorphButton id, String text, int icon) {
        id.setText(text);
        id.setCompoundDrawablesRelativeWithIntrinsicBounds(icon, 0, 0, 0);
    }

    private void setArraylist() {
        Cursor cursor = dbHelper.getdata();
        //When no notes we put the image
        if(cursor.getCount() == 0){
            RelativeLayout noNotes = findViewById(R.id.none_notes);
            noNotes.setVisibility(View.VISIBLE);
        } else{
            while (cursor.moveToNext()){
                items.add(new ItemModule(cursor.getString(1), cursor.getString(2), cursor.getString(3)));
            }
        }
    }

    public void set_adapter(NeumorphButton view) {
        if (getSharedPreferences("view", MODE_PRIVATE).getBoolean("grid_view", false)){
            adapter = new Adapter(this, this,items, R.layout.grid_layout_notes);
            recyclerView.setLayoutManager(new GridLayoutManager(this, 2));
            drawableButtonChange(view, "List View", R.drawable.list_view);
        } else {
            adapter = new Adapter(this, this,items, R.layout.list_layout_notes);
            recyclerView.setLayoutManager(new LinearLayoutManager(this));
            drawableButtonChange(view, "Grid View", R.drawable.grid_view);
        }
        recyclerView.setAdapter(adapter);
    }
}