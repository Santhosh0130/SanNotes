package com.example.sannotes.Adapter;

import android.app.Activity;
import android.app.AlertDialog;
import android.app.Dialog;
import android.content.Context;
import android.content.DialogInterface;
import android.view.LayoutInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
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
import com.example.sannotes.R;

import java.util.List;

public class Adapter extends RecyclerView.Adapter<Adapter.view_holder>{
    Context context;
    Activity activity;
    Animation animation;
    boolean isView;

    List<String> title,note,date;
    DBHelper dbHelper;
    public Adapter(Activity activity,Context context, List<String> title,List<String> note,List<String> date, boolean isView) {
        this.activity = activity;
        this.context = context;
        this.title = title;
        this.note = note;
        this.date = date;
        this.isView = isView;
    }


    @NonNull
    @Override
    public view_holder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        if (isView){
            return new view_holder(LayoutInflater.from(context).inflate(R.layout.grid_layout_notes,parent,false), parent);
        } else {
            return new view_holder(LayoutInflater.from(context).inflate(R.layout.list_layout_notes, parent, false), parent);
        }
    }

    @Override
    public void onBindViewHolder(@NonNull view_holder holder, int position) {

        //Put the List Data's in Recycler View
        holder.title.setText(String.valueOf(title.get(position)));
        holder.notes.setText(String.valueOf(note.get(position)));
        holder.dateview.setText(String.valueOf(date.get(position)));

        if (!isView)
         isListView(holder, position);

        //More button & Listener
        holder.more.setOnClickListener(v->{

            //Create a popup menu for items
            PopupMenu popupMenu = new PopupMenu(context,v);
            popupMenu.inflate(R.menu.more_view);
            popupMenu.show();

            //Full screen Dialog
            Dialog dialog = new Dialog(context, android.R.style.Theme_DeviceDefault_Light_NoActionBar);
            dialog.setContentView(R.layout.new_note);

            EditText get_title = dialog.findViewById(R.id.notes_title);
            EditText get_note = dialog.findViewById(R.id.notes);
            ImageButton update_btn = dialog.findViewById(R.id.note_done);
            ImageButton update_back = dialog.findViewById(R.id.back_done);

            //Popup menu click Listener
            popupMenu.setOnMenuItemClickListener(new PopupMenu.OnMenuItemClickListener() {
                @Override
                public boolean onMenuItemClick(MenuItem item) {
                    if(item.getItemId() == R.id.mor_edit){
                        dialog.show();
                        get_title.setText(title.get(holder.getAdapterPosition()));
                        get_note.setText(note.get(holder.getAdapterPosition()));

                        //Update button & Listener
                        update_btn.setOnClickListener(v->{
                            dbHelper = new DBHelper(context);
                            dbHelper.updateData(title.get(holder.getAdapterPosition()),get_title.getText().toString(),get_note.getText().toString(),date.get(holder.getAdapterPosition()));

                            //recreate the MainActivity using recreate() method
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
                                    dbHelper.deleteNote(String.valueOf(title.get(holder.getAdapterPosition())));

                                    //recreate the MainActivity using recreate() method
                                    activity.recreate();
                                })
                                .setNegativeButton("No", (dialogInterface, i) -> {
                                })
                                .setCancelable(false)
                                .create().show();

                    }
                    return true;
                }
            });
        });
    }

    private void isListView(view_holder holder, int position) {
        //For read a notes with Listener
        holder.arrow.setOnClickListener(v->{
            if(holder.notes.getVisibility() == View.GONE) {
                holder.notes.setVisibility(View.VISIBLE);
                holder.arrow.setImageResource(R.drawable.arrow_up);
                holder.notes.setText(String.valueOf(note.get(holder.getAdapterPosition())));
            }else{
                holder.notes.setVisibility(View.GONE);
                holder.arrow.setImageResource(R.drawable.arrow_down);
            }
        });

        holder.cardView.setOnClickListener(v->{
            if(holder.notes.getVisibility() == View.GONE) {
                holder.notes.setVisibility(View.VISIBLE);
                holder.arrow.setImageResource(R.drawable.arrow_up);
                holder.notes.setText(String.valueOf(note.get(holder.getAdapterPosition())));
            }else{
                holder.notes.setVisibility(View.GONE);
                holder.arrow.setImageResource(R.drawable.arrow_down);
            }
        });
    }

    @Override
    public int getItemCount() {
        return title.size();
    }



    public class view_holder extends RecyclerView.ViewHolder {

        TextView title,notes,dateview;
        ImageButton more,arrow;
        CardView cardView;
        LinearLayout linearLayout;
        RelativeLayout gridLayout;
        view_holder(@NonNull View itemView, ViewGroup parent) {
            super(itemView);
            title = itemView.findViewById(R.id.set_title);
            notes = itemView.findViewById(R.id.set_notes);
            more = itemView.findViewById(R.id.more_btn);
            arrow = itemView.findViewById(R.id.arrow_btn);
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
