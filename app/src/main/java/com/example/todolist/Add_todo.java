package com.example.todolist;

import android.app.AlarmManager;
import android.app.AlertDialog;
import android.app.DatePickerDialog;
import android.app.LoaderManager;
import android.app.PendingIntent;
import android.app.TimePickerDialog;
import android.content.ContentUris;
import android.content.ContentValues;
import android.content.Context;
import android.content.CursorLoader;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.Loader;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.net.Uri;
import android.os.Bundle;
import android.text.format.DateFormat;
import android.view.Menu;
import android.view.MenuItem;
import android.view.MotionEvent;
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
import androidx.core.app.NavUtils;

import com.example.todolist.data.ListContract;
import com.example.todolist.data.ListDbHelper;

import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Locale;
import java.util.Objects;

public class Add_todo extends AppCompatActivity implements LoaderManager.LoaderCallbacks<Cursor> {
    private  EditText list;
    private  EditText date_text;
    private  EditText time_text;
    private ListDbHelper mDbHelper;
    private CheckBox setTimer;
    private int myear;
    private int mmonth;
    private int mday;
    private int mhour;
    private int mmin;
    private boolean mListChange = false;

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

        list= (EditText) findViewById(R.id.editTextList);
        date_text= (EditText) findViewById(R.id.date);
        time_text= (EditText) findViewById(R.id.time);
        list.setOnTouchListener(mTouchListener);
        date_text.setOnTouchListener(mTouchListener);
        time_text.setOnTouchListener(mTouchListener);
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

             startAlarm(myCalendar);
             Toast.makeText(getApplicationContext(),"t"+myCalendar.get(Calendar.HOUR_OF_DAY)+myCalendar.get(Calendar.MINUTE)+myCalendar.get(Calendar.YEAR),Toast.LENGTH_LONG).show();
             insert();
             finish();
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

        DatePickerDialog.OnDateSetListener date = new DatePickerDialog.OnDateSetListener() {

            @Override
            public void onDateSet(DatePicker view, int year, int monthOfYear,
                                  int dayOfMonth) {
                // TODO Auto-generated method stub
                myear=year;
                mmonth=monthOfYear;
                mday=dayOfMonth;
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
        TimePickerDialog.OnTimeSetListener time = new TimePickerDialog.OnTimeSetListener() {
            @Override
            public void onTimeSet(TimePicker view, int hourOfDay, int minute) {
                mhour=hourOfDay;
                mmin=minute;
                myCalendar.set(Calendar.HOUR_OF_DAY, mhour);
                myCalendar.set(Calendar.MINUTE, mmin);
                updatetime();
            }
            private void updatetime() {
                time_text.setText( mhour + ":" + mmin);
            }

        };
        time_text.setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View v) {
                // TODO Auto-generated method stub

                  new TimePickerDialog(Add_todo.this, time,myCalendar
                          .get(Calendar.HOUR_OF_DAY),myCalendar.get(Calendar.MINUTE), DateFormat.is24HourFormat(Add_todo.this)).show();

            }
        });

    }
    private View.OnTouchListener mTouchListener = new View.OnTouchListener() {
        @Override
        public boolean onTouch(View view, MotionEvent motionEvent) {
            mListChange = true;
            return false;
        }
    };


    private void startAlarm(Calendar c) {
        AlarmManager alarmManager = (AlarmManager) getSystemService(Context.ALARM_SERVICE);
        Intent intent = new Intent(this, NotificationPublisher.class);

        final int id = (int) System.currentTimeMillis();
        ContentValues values = new ContentValues();
        String id_string=String.valueOf(id);
        values.put(ListContract.ListEntry.ALARM, id_string);
        PendingIntent pendingIntent = PendingIntent.getBroadcast(this,1, intent, PendingIntent.FLAG_UPDATE_CURRENT);
        Objects.requireNonNull(alarmManager).setExact(AlarmManager.RTC_WAKEUP,
                c.getTimeInMillis(), pendingIntent);
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
    public void onBackPressed() {
        // If the pet hasn't changed, continue with handling back button press
        if (!mListChange) {
            super.onBackPressed();
            return;
        }
        else
        {
            showUnsavedChangesDialog();
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
                showDeleteConfirmationDialog();
                return true;
            case android.R.id.home:
                if (!mListChange) {
                    finish();
                    return true;
                }
                else
                {
                    showUnsavedChangesDialog();
                    return true;
                }

        }

        return super.onOptionsItemSelected(item);
    }
    private void showUnsavedChangesDialog() {

        AlertDialog.Builder builder = new AlertDialog.Builder(this);

        builder.setMessage("Save Changes");
        builder.setPositiveButton("Keep Editing", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int id) {
                // User clicked the "Delete" button, so delete the pet.
                dialog.dismiss();
            }
        });
        builder.setNegativeButton("Discard", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int id) {
                // User clicked the "Cancel" button, so dismiss the dialog
                // and continue editing the pet.
                finish();
            }
        });
        AlertDialog alertDialog = builder.create();
        alertDialog.show();
    }
    private void showDeleteConfirmationDialog() {

        AlertDialog.Builder builder = new AlertDialog.Builder(this);

        builder.setMessage("Delete Todo");
        builder.setPositiveButton("Delete", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int id) {
                // User clicked the "Delete" button, so delete the pet.
                deletePet();
            }
        });
        builder.setNegativeButton("Cancel", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int id) {
                // User clicked the "Cancel" button, so dismiss the dialog
                // and continue editing the pet.
                if (dialog != null) {
                    dialog.dismiss();
                }
            }
        });

        // Create and show the AlertDialog
        AlertDialog alertDialog = builder.create();
        alertDialog.show();
    }

    private void cancelAlarm() {
        AlarmManager alarmManager = (AlarmManager) getSystemService(Context.ALARM_SERVICE);
        Intent intent = new Intent(this, NotificationPublisher.class);

        PendingIntent pendingIntent = PendingIntent.getBroadcast(this, Integer.parseInt(ListContract.ListEntry.ALARM), intent, 0);
        alarmManager.cancel(pendingIntent);

    }

    private void deletePet() {
        // Only perform the delete if this is an existing pet.
        if (List_Uri != null) {
            // Call the ContentResolver to delete the pet at the given content URI.
            // Pass in null for the selection and selection args because the mCurrentPetUri
            // content URI already identifies the pet that we want.
            int rowsDeleted = getContentResolver().delete(List_Uri, null, null);

            // Show a toast message depending on whether or not the delete was successful.
            if (rowsDeleted == 0) {
                // If no rows were deleted, then there was an error with the delete.
                Toast.makeText(this, "geteditor_delete_pet_failedString", Toast.LENGTH_SHORT).show();
            } else {
                // Otherwise, the delete was successful and we can display a toast.
                Toast.makeText(this, "Success", Toast.LENGTH_SHORT).show();
            }

            // Close the activity
            finish();
        }
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
                setTimer.setChecked(false);

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
