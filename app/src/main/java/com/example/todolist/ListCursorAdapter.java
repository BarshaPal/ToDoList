package com.example.todolist;

import android.content.Context;
import android.database.Cursor;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.CursorAdapter;
import android.widget.ImageButton;
import android.widget.TextView;
import android.widget.Toast;

import com.example.todolist.data.ListContract;

public class ListCursorAdapter extends CursorAdapter {


    public ListCursorAdapter(Context context, Cursor c) {
            super(context, c, 0);
        }



        @Override
        public void bindView(View view, Context context, Cursor cursor) {
            // Find individual views that we want to modify in list item layout
            final int id = cursor.getInt(cursor.getColumnIndex(ListContract.ListEntry._ID));
            TextView Todos = view.findViewById(R.id.Listview);
            TextView Date = view.findViewById(R.id.show_date);
            TextView Time = view.findViewById(R.id.show_time);
            ImageButton star = (ImageButton) view.findViewById(R.id.star);

            String TodoList = cursor.getString(cursor.getColumnIndex(ListContract.ListEntry.TODOS));
            String currtime = cursor.getString(cursor.getColumnIndex(ListContract.ListEntry.TIME));
            String currDate = cursor.getString(cursor.getColumnIndex(ListContract.ListEntry.DATE));

            Todos.setText(TodoList);
            Date.setText(currDate);
            Time.setText(currtime);

        }

    @Override
    public View newView(Context context, Cursor cursor, ViewGroup parent) {
        return LayoutInflater.from(context).inflate(R.layout.list, parent, false);
    }
}

