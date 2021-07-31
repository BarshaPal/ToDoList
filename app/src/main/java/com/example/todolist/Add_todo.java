package com.example.todolist;

import android.app.DatePickerDialog;
import android.app.LoaderManager;
import android.app.TimePickerDialog;
import android.content.ContentValues;
import android.content.CursorLoader;
import android.content.Intent;
import android.content.Loader;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.net.Uri;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.CompoundButton;
import android.widget.DatePicker;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TimePicker;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import com.example.todolist.data.ListContract;
import com.example.todolist.data.ListDbHelper;

import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Locale;

public class Add_todo extends AppCompatActivity implements LoaderManager.LoaderCallbacks<Cursor> {
    private  EditText list;
    private  EditText date_text;
    private  EditText time_text;
    private ListDbHelper mDbHelper;
    private CheckBox setTimer;
    private Uri List_Uri;
    private static final int BOOK_LOADER = 1;

    @Override
    protected void onCreate(Bundle savedInstanceState) {

        super.onCreate(savedInstanceState);
        setContentView(R.layout.add_todo);

        final Calendar myCalendar = Calendar.getInstance();
         setTimer=(CheckBox) findViewById(R.id.setTimer);
        ImageView star=(ImageView)findViewById(R.id.star);
        LinearLayout showtime=(LinearLayout)findViewById(R.id.showtimer);
        showtime.setVisibility(View.INVISIBLE);
     Button addlist=(Button)findViewById(R.id.addtodo_butt);
        Intent intent = getIntent();
        mDbHelper=new ListDbHelper(this);
        List_Uri = intent.getData();
        if(List_Uri==null)
        {
            setTitle("Add ToDo");
            invalidateOptionsMenu();
        }
        else
        {
            setTitle("Edit ToDo");
            addlist.setText("Save Changes");
            getLoaderManager().initLoader(BOOK_LOADER, null, this);
        }
     addlist.setOnClickListener(new View.OnClickListener() {
         @Override
         public void onClick(View v) {
             insert();
         }
     });
setTimer.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
    @Override
    public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
        if(setTimer.isChecked())
        {
            showtime.setVisibility(View.VISIBLE);
        }
        else
        {
            showtime.setVisibility(View.INVISIBLE);

        }
    }
});
         list= (EditText) findViewById(R.id.editTextList);
         date_text= (EditText) findViewById(R.id.date);
         time_text= (EditText) findViewById(R.id.time);
        DatePickerDialog.OnDateSetListener date = new DatePickerDialog.OnDateSetListener() {

            @Override
            public void onDateSet(DatePicker view, int year, int monthOfYear,
                                  int dayOfMonth) {
                // TODO Auto-generated method stub
                myCalendar.set(Calendar.YEAR, year);
                myCalendar.set(Calendar.MONTH, monthOfYear);
                myCalendar.set(Calendar.DAY_OF_MONTH, dayOfMonth);
                updateLabel();
            }
            private void updateLabel() {
                String myFormat = "MM/dd/yy"; //In which you need put here
                SimpleDateFormat sdf = new SimpleDateFormat(myFormat, Locale.US);

                date_text.setText(sdf.format(myCalendar.getTime()));
            }
        };

        date_text.setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View v) {
                // TODO Auto-generated method stub
                new DatePickerDialog(Add_todo.this, date, myCalendar
                        .get(Calendar.YEAR), myCalendar.get(Calendar.MONTH),
                        myCalendar.get(Calendar.DAY_OF_MONTH)).show();
            }
        });

        time_text.setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View v) {
                // TODO Auto-generated method stub

                int hour = myCalendar.get(Calendar.HOUR_OF_DAY);
                int minute = myCalendar.get(Calendar.MINUTE);
                TimePickerDialog mTimePicker;
                mTimePicker = new TimePickerDialog(Add_todo.this, new TimePickerDialog.OnTimeSetListener() {
                    @Override
                    public void onTimeSet(TimePicker timePicker, int selectedHour, int selectedMinute) {
                        time_text.setText( selectedHour + ":" + selectedMinute);
                    }
                }, hour, minute, true);//Yes 24 hour time
                mTimePicker.setTitle("Select Time");
                mTimePicker.show();

            }
        });

    }

    private void insert() {
        String List = list.getText().toString().trim();
        String Date = date_text.getText().toString().trim();
        String Time = time_text.getText().toString();
        if (Date == null || Date.length() == 0)
        {
            Date="Today";
        }

        ContentValues values = new ContentValues();
        values.put(ListContract.ListEntry.TODOS, List);
        values.put(ListContract.ListEntry.DATE, Date);
        values.put(ListContract.ListEntry.TIME, Time);

        if(List_Uri==null) {
            Uri newUri = getContentResolver().insert(
                    ListContract.ListEntry.CONTENT_URI,   // the pet content URI
                    values                  // the values to insert
            );
            if (newUri != null) {
                Toast.makeText(this, "successfull", Toast.LENGTH_LONG).show();
            }
        }
        else
        {
            int rowsAffected = getContentResolver().update(List_Uri, values, null, null);
            if (rowsAffected == 0) {
                // If no rows were affected, then there was an error with the update.
                Toast.makeText(this, "editor_update_book_failed",
                        Toast.LENGTH_SHORT).show();
            } else {
                // Otherwise, the update was successful and we can display a toast.
                Toast.makeText(this, "editor_update_book_successful",
                        Toast.LENGTH_SHORT).show();
            }
            finish();
        }


    }


    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.menu_add,menu);
        return true;
    }
    @Override
    public boolean onPrepareOptionsMenu(Menu menu) {
        super.onPrepareOptionsMenu(menu);
        // If this is a new pet, hide the "Delete" menu item.
        if(List_Uri==null)
        {
            MenuItem menuItem = menu.findItem(R.id.delete);
            menuItem.setVisible(false);
        }
        return true;
    }
    @Override
    public boolean onOptionsItemSelected(@NonNull MenuItem item) {
        switch (item.getItemId())
        {
            case R.id.delete:

                return true;

        }
        return super.onOptionsItemSelected(item);
    }


    @Override
    public Loader<Cursor> onCreateLoader(int id, Bundle args) {
        SQLiteDatabase db = mDbHelper.getReadableDatabase();

        String[] projection = {
                ListContract.ListEntry._ID,
                ListContract.ListEntry.TODOS,
                ListContract.ListEntry.DATE,
                ListContract.ListEntry.TIME};

        return new CursorLoader(this,
                List_Uri,
                projection,
                null,
                null, null);
    }

    @Override
    public void onLoadFinished(Loader<Cursor> loader, Cursor cursor) {
        if (cursor.moveToFirst()) {
            String todos = cursor.getString(cursor.getColumnIndex(ListContract.ListEntry.TODOS));
            String date = cursor.getString(cursor.getColumnIndex(ListContract.ListEntry.DATE));
            String time = cursor.getString(cursor.getColumnIndex(ListContract.ListEntry.TIME));


            list.setText(todos);
            date_text.setText(date);
            time_text.setText(time);
            if (date == null || date.length() == 0 || time == null || time.length() == 0) {
                setTimer.setEnabled(false);

            } else {
                setTimer.setChecked(true);
            }
        }
    }
    @Override
    public void onLoaderReset(Loader<Cursor> loader) {

        list.setText(" ");
        date_text.setText(" ");
        time_text.setText(" ");
    }
}