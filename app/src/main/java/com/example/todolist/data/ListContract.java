package com.example.todolist.data;

import android.content.ContentResolver;
import android.net.Uri;
import android.provider.BaseColumns;

/**
 */
public final class ListContract {

    private ListContract() {
    }

    public static final String CONTENT_AUTHORITY = "com.example.todolist";

    public static final Uri BASE_CONTENT_URI = Uri.parse("content://" + CONTENT_AUTHORITY);

    public static final String PATH_LIST = "list5";


    public static final class ListEntry implements BaseColumns {


        public static final Uri CONTENT_URI = Uri.withAppendedPath(BASE_CONTENT_URI, PATH_LIST);

        public static final String CONTENT_LIST_TYPE =
                ContentResolver.CURSOR_DIR_BASE_TYPE + "/" + CONTENT_AUTHORITY + "/" + PATH_LIST;

        public static final String CONTENT_ITEM_TYPE =
                ContentResolver.CURSOR_ITEM_BASE_TYPE + "/" + CONTENT_AUTHORITY + "/" + PATH_LIST;

        public static final String TABLE_NAME = "list5";

        public static final String _ID = BaseColumns._ID;

        public static final String TODOS = "todos";


        public static final String DATE = "date";


        public static final String TIME = "time";

        public static final String ALARM = "alarm";
    }

}
