package com.example.sannotes.Activities;

import androidx.appcompat.app.AppCompatActivity;
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
import android.os.Bundle;
import android.view.MenuItem;
import android.view.View;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.RelativeLayout;
import android.widget.Toast;

import com.example.sannotes.Adapter.Adapter;
import com.example.sannotes.DBHelper;
import com.example.sannotes.R;

import java.text.DateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.List;

public class MainActivity extends AppCompatActivity {

    List<String> title,note,date;
    Adapter adapter;
    DBHelper dbHelper;
    Dialog dialog;
    EditText notes_title,notes;
    RecyclerView recyclerView;
    ImageButton settings;
    AppCompatButton grid_view, list_view, deleteAll, lightThemeMode, darkThemeMode;
    public static boolean grid = true, list = false;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        //Get the Activity id's
        recyclerView = findViewById(R.id.recycler_view);
        settings = findViewById(R.id.settings);

        Dialog settingsDialog = new Dialog(this);
        settingsDialog.setContentView(R.layout.settings_dialog);
        settingsDialog.setCancelable(true);

        settings.setOnClickListener(v ->{
            settingsDialog.show();

            grid_view = settingsDialog.findViewById(R.id.grid_view);
            list_view = settingsDialog.findViewById(R.id.list_view);
            deleteAll = settingsDialog.findViewById(R.id.delete_all);
            lightThemeMode = settingsDialog.findViewById(R.id.theme_light_mode);
            darkThemeMode = settingsDialog.findViewById(R.id.theme_dark_mode);


             //1.For Delete All the Data's
            deleteAll.setOnClickListener(V -> {
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

            SharedPreferences.Editor editor = getSharedPreferences("view", MODE_PRIVATE).edit();
            //2.For View's
            grid_view.setOnClickListener(V -> {
                editor.putBoolean("gridEnable", true);
                editor.apply();
                settingsDialog.dismiss();
            });
            list_view.setOnClickListener(V -> {
                editor.putBoolean("gridEnable", false);
                editor.apply();
                settingsDialog.dismiss();
            });

            //3.For Theme's
            lightThemeMode.setOnClickListener(V -> {
                Toast.makeText(this, "light", Toast.LENGTH_SHORT).show();
                darkThemeMode.setVisibility(View.VISIBLE);
                lightThemeMode.setVisibility(View.GONE);
            });
            darkThemeMode.setOnClickListener(V -> {
                Toast.makeText(this, "dark", Toast.LENGTH_SHORT).show();
                darkThemeMode.setVisibility(View.GONE);
                lightThemeMode.setVisibility(View.VISIBLE);
            });
        });

        //For full screen dialog
        dialog = new Dialog(this, android.R.style.Theme_DeviceDefault_Light_NoActionBar);
        dialog.setContentView(R.layout.new_note);

        //Calender View
        Calendar calendar = Calendar.getInstance();
        String date = DateFormat.getDateInstance(DateFormat.SHORT).format(calendar.getTime());

        //New note add button & Listener
        CardView add_btn = findViewById(R.id.note_add_btn);
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
                            Toast.makeText(MainActivity.this, "Enter a Title", Toast.LENGTH_SHORT).show();
                        }
                });

                //Back button
                ImageButton back_btn = dialog.findViewById(R.id.back_done);
                back_btn.setOnClickListener(view -> {
                        dialog.dismiss();
                });
        });
        dbHelper = new DBHelper(MainActivity.this);
        title = new ArrayList<>();
        note = new ArrayList<>();
        this.date = new ArrayList<>();

        //Add the values in Individual Array
        setArraylist(title,note, this.date);

    }

    @Override
    protected void onResume() {
        super.onResume();
        SharedPreferences preferences = getSharedPreferences("view", MODE_PRIVATE);
        boolean gridEnabled  = preferences.getBoolean("gridEnable", false);
        if (gridEnabled){
            set_adapter(new GridLayoutManager(this, 2), true);
            Toast.makeText(this, "Grid", Toast.LENGTH_SHORT).show();
        } else {
            set_adapter(new LinearLayoutManager(this), false);
            Toast.makeText(this, "List", Toast.LENGTH_SHORT).show();
        }
    }

    private void setArraylist(List<String> title, List<String> note, List<String> date) {
        Cursor cursor = dbHelper.getdata();
        //When no notes we put the image
        if(cursor.getCount() == 0){
            RelativeLayout noNotes = findViewById(R.id.none_notes);
            noNotes.setVisibility(View.VISIBLE);
        } else{
            while (cursor.moveToNext()){
                title.add(cursor.getString(0));
                note.add(cursor.getString(1));
                date.add(cursor.getString(2));
            }
        }
    }

    public void set_adapter(RecyclerView.LayoutManager layoutManager, boolean view) {
        adapter = new Adapter(MainActivity.this,MainActivity.this,title,note,date,view);
        recyclerView.setAdapter(adapter);
        recyclerView.setLayoutManager(layoutManager);
    }

}