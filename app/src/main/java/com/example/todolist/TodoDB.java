package com.example.todolist;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.SQLException;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.provider.BaseColumns;

public final class TodoDB {
    private Context mContext;
    private SQLiteDatabase mDatabase;
    private TodoDBHelper mHelper;

    public TodoDB(Context context) {
        mContext = context;
    }

    public void open() throws SQLException {
        mHelper = new TodoDBHelper(mContext);
        mDatabase = mHelper.getWritableDatabase();
    }

    public void close() throws SQLException {
        mHelper.close();
        mHelper = null;
        mDatabase = null;
    }

    public long addRow(ContentValues values) {
        return mDatabase.insert(TodoEntry.TABLE_NAME, null, values);
    }

    public boolean deleteRow(long rowId) {
        return mDatabase.delete(TodoEntry.TABLE_NAME, TodoEntry._ID + "=" + rowId, null) > 0;
    }

    public boolean updateRow(long rowId, ContentValues values) {
        return mDatabase.update(TodoEntry.TABLE_NAME, values, TodoEntry._ID + "=" + rowId, null) > 0;
    }

    private static String[] projection = {
            TodoEntry._ID,
            TodoEntry.COLUMN_TITLE,
            TodoEntry.COLUMN_BODY,
            TodoEntry.COLUMN_STATE
    };

    public Cursor queryAll() {
        return mDatabase.query(TodoEntry.TABLE_NAME, projection,
                null, null, null, null,
                TodoEntry.COLUMN_TITLE + " ASC");
    }

    public Cursor query(long rowId) throws SQLException {
        Cursor cursor = mDatabase.query(true, TodoEntry.TABLE_NAME, projection,
                TodoEntry._ID + "=" + rowId, null, null, null, null, null);
        cursor.moveToFirst();

        return cursor;
    }

    ContentValues createContentValues(String title, String body, int state) {
        ContentValues values = new ContentValues();

        values.put(TodoEntry.COLUMN_TITLE, title);
        values.put(TodoEntry.COLUMN_BODY, body);
        values.put(TodoEntry.COLUMN_STATE, state);

        return values;
    }

    public static abstract class TodoEntry implements BaseColumns {
        public static final String TABLE_NAME = "todo";
        public static final String COLUMN_TITLE = "title";
        public static final String COLUMN_BODY = "body";
        public static final String COLUMN_STATE = "state";
        public static final int INDEX_ID = 0;
        public static final int INDEX_TITLE = 1;
        public static final int INDEX_BODY = 2;
        public static final int INDEX_STATE = 3;
    }

    public class TodoDBHelper extends SQLiteOpenHelper {
        public static final int DATABASE_VERSION = 2;
        public static final String DATABASE_NAME = "TodoList.db";

        private static final String SQL_CREATE_ENTRIES =
                "create table " + TodoEntry.TABLE_NAME + "(" +
                        TodoEntry._ID + " integer primary key autoincrement, " +
                        TodoEntry.COLUMN_TITLE + " text not null, " +
                        TodoEntry.COLUMN_BODY + " text not null, " +
                        TodoEntry.COLUMN_STATE + " integer " +
                        ");";

        public TodoDBHelper(Context context) {
            super(context, DATABASE_NAME, null, DATABASE_VERSION);
        }

        @Override
        public void onCreate(SQLiteDatabase sqLiteDatabase) {
            sqLiteDatabase.execSQL(SQL_CREATE_ENTRIES);
        }

        @Override
        public void onUpgrade(SQLiteDatabase sqLiteDatabase, int i, int i1) { }
    }
}
