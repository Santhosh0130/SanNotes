package com.example.sannotes.Adapter;

import static com.google.android.material.resources.MaterialResources.getDrawable;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.app.AlertDialog;
import android.app.Dialog;
import android.content.Context;
import android.content.DialogInterface;
import android.graphics.drawable.Drawable;
import android.view.LayoutInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.view.WindowManager;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.EditText;
import android.widget.GridLayout;
import android.widget.ImageButton;
import android.widget.LinearLayout;
import android.widget.PopupMenu;
import android.widget.RelativeLayout;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.cardview.widget.CardView;
import androidx.recyclerview.widget.RecyclerView;

import com.example.sannotes.DBHelper;
import com.example.sannotes.ItemModule;
import com.example.sannotes.R;

import java.util.ArrayList;
import java.util.List;
import java.util.Random;

import soup.neumorphism.NeumorphCardView;

public class Adapter extends RecyclerView.Adapter<Adapter.view_holder>{
    Context context;
    Activity activity;
    Animation animation;
    int idView;
    List<ItemModule> items;
    DBHelper dbHelper;
    public Adapter(Activity activity, Context context, List<ItemModule> items, int idView) {
        this.activity = activity;
        this.context = context;
        this.items = items;
        this.idView = idView;
    }


    @NonNull
    @Override
    public view_holder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        return new view_holder(LayoutInflater.from(context).inflate(idView, parent, false));
    }

    @SuppressLint("UseCompatLoadingForDrawables")
    @Override
    public void onBindViewHolder(@NonNull view_holder holder, int position) {

        if (idView == R.layout.grid_layout_notes) {
            //Put the Grid Data's in Recycler View
            holder.title.setText(String.valueOf(items.get(position).getTitle()));
            holder.notes.setText(String.valueOf(items.get(position).getNotes()));
            holder.dateview.setText(String.valueOf(items.get(position).getDate()));
        } else {
            //Put the List Data's in Recycler View
            holder.title.setText(String.valueOf(items.get(position).getTitle()));
            holder.dateview.setText(String.valueOf(items.get(position).getDate()));
        }
        Dialog showNote = new Dialog(context, android.R.style.Theme_DeviceDefault_Light_NoActionBar);
        showNote.setContentView(R.layout.read_dialog);
        showNote.getWindow().setLayout(WindowManager.LayoutParams.WRAP_CONTENT, WindowManager.LayoutParams.WRAP_CONTENT);
        showNote.getWindow().setBackgroundDrawable(activity.getDrawable(R.drawable.dialog_bg));
        showNote.setCanceledOnTouchOutside(true);

        holder.itemView.setOnClickListener(v -> {
            showNote.show();

            TextView showTitle = showNote.findViewById(R.id.read_title);
            TextView showNotes = showNote.findViewById(R.id.read_notes);
            TextView showDate = showNote.findViewById(R.id.read_date);

            showTitle.setText(String.valueOf(items.get(position).getTitle()));
            showNotes.setText(String.valueOf(items.get(position).getNotes()));
            showDate.setText(String.valueOf(items.get(position).getDate()));
        });

        holder.cardView.setBackgroundColor(holder.itemView.getResources().getColor(getRandomColorCode()));

        //More button & Listener
        holder.more.setOnClickListener(v->{

            //Create a popup menu for items
            PopupMenu popupMenu = new PopupMenu(context,v);
            popupMenu.inflate(R.menu.more_view);
            popupMenu.show();

            //Full screen Dialog
            Dialog dialog = new Dialog(context, android.R.style.Theme_DeviceDefault_DayNight);
            dialog.setContentView(R.layout.new_note);

            EditText get_title = dialog.findViewById(R.id.notes_title);
            EditText get_note = dialog.findViewById(R.id.notes);
            ImageButton update_btn = dialog.findViewById(R.id.note_done);
            ImageButton update_back = dialog.findViewById(R.id.back_done);

            //Popup menu click Listener
            popupMenu.setOnMenuItemClickListener(new PopupMenu.OnMenuItemClickListener() {
                @SuppressLint("NotifyDataSetChanged")
                @Override
                public boolean onMenuItemClick(MenuItem item) {
                    if(item.getItemId() == R.id.mor_edit){
                        dialog.show();
                        get_title.setText(items.get(holder.getAdapterPosition()).getTitle());
                        get_note.setText(items.get(holder.getAdapterPosition()).getNotes());

                        //Update button & Listener
                        update_btn.setOnClickListener(v->{
                            dbHelper = new DBHelper(context);
                            dbHelper.updateData(items.get(holder.getAdapterPosition()).getTitle(),get_title.getText().toString(),get_note.getText().toString(),items.get(holder.getAdapterPosition()).getDate());

                            //recreate the MainActivity using recreate() method
                            notifyDataSetChanged();
                            activity.recreate();
                            dialog.dismiss();
                        });

                        //Back button & Listener
                        update_back.setOnClickListener(new View.OnClickListener() {
                            @Override
                            public void onClick(View v) {
                                dialog.dismiss();
                            }
                        });
                    }

                    //For Delete a note
                    if(item.getItemId() == R.id.mor_delete){

                        AlertDialog.Builder builder = new AlertDialog.Builder(context);
                        builder.setMessage("Are You Sure Want to Delete?")
                                .setTitle("Delete Note")
                                .setPositiveButton("Yes", (dialogInterface, i) -> {

                                    dbHelper = new DBHelper(context);
                                    dbHelper.deleteNote(String.valueOf(items.get(holder.getAdapterPosition()).getTitle()));

                                    //recreate the MainActivity using recreate() method
                                    notifyDataSetChanged();
                                    activity.recreate();
                                })
                                .setNegativeButton("No", (dialogInterface, i) -> {
                                })
                                .setCancelable(true)
                                .create().show();

                    }
                    return true;
                }
            });
        });
    }

    private int getRandomColorCode() {
        List<Integer> colorCode = new ArrayList<>();
        colorCode.add(R.color.note_color_1);
        colorCode.add(R.color.note_color_2);
        colorCode.add(R.color.note_color_3);
        colorCode.add(R.color.note_color_4);
        colorCode.add(R.color.note_color_5);

        return colorCode.get(new Random().nextInt(colorCode.size()));
    }

    @Override
    public int getItemCount() {
        return items.size();
    }

    public static class view_holder extends RecyclerView.ViewHolder {

        TextView title,notes,dateview;
        ImageButton more;
        NeumorphCardView cardView;
        view_holder(@NonNull View itemView) {
            super(itemView);
            title = itemView.findViewById(R.id.set_title);
            notes = itemView.findViewById(R.id.set_notes);
            more = itemView.findViewById(R.id.more_btn);
            dateview = itemView.findViewById(R.id.set_date);
            cardView = itemView.findViewById(R.id.card_view);
//            if (itemView == LayoutInflater.from(context).inflate(R.layout.grid_layout_notes, parent, false)) {
//                gridLayout = itemView.findViewById(R.id.root_relative_layout);
//                animation = AnimationUtils.loadAnimation(context,R.anim.translate_anim);
//                gridLayout.setAnimation(animation);
//            } else {
//                linearLayout = itemView.findViewById(R.id.root_linear_layout);
//                animation = AnimationUtils.loadAnimation(context,R.anim.translate_anim);
//                linearLayout.setAnimation(animation);
//            }
        }
    }
}
