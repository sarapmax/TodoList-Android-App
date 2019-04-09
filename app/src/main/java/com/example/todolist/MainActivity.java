package com.example.todolist;

import android.annotation.SuppressLint;
import android.app.ListActivity;
import android.app.LoaderManager;
import android.content.CursorLoader;
import android.content.Intent;
import android.content.Loader;
import android.database.Cursor;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.os.Bundle;
import android.support.design.widget.FloatingActionButton;
import android.view.View;
import android.widget.Button;
import android.widget.ListView;
import android.widget.SimpleCursorAdapter;

public class MainActivity extends ListActivity implements LoaderManager.LoaderCallbacks<Cursor> {
    TodoDB mTodoDB;
    SimpleCursorAdapter mCursorAdapter;

    @Override
    protected void onResume() {
        super.onResume();
        getLoaderManager().restartLoader(0, null, this);
    }

    @SuppressLint("StaticFieldLeak")
    @NonNull
    @Override
    public Loader<Cursor> onCreateLoader(int i, @Nullable Bundle bundle) {
        return new CursorLoader(this) {
            @Override
            public Cursor loadInBackground() {
                return mTodoDB.queryAll();
            }
        };
    }

    @Override
    public void onLoadFinished(@NonNull Loader<Cursor> loader, Cursor cursor) {
        mCursorAdapter.swapCursor(cursor);
    }

    @Override
    public void onLoaderReset(@NonNull Loader<Cursor> loader) {
        mCursorAdapter.swapCursor(null);
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        FloatingActionButton button = findViewById(R.id.fab);
        button.setOnClickListener(new Button.OnClickListener() {
            @Override
            public void onClick(View view) {
                startDetail(0, true);
            }
        });

        mTodoDB = new TodoDB(this);
        mTodoDB.open();

        int[] to = new int[] { android.R.id.text1, android.R.id.text2 };
        String[] form = new String[] { TodoDB.TodoEntry.COLUMN_TITLE, TodoDB.TodoEntry.COLUMN_BODY };

        mCursorAdapter = new SimpleCursorAdapter(this, android.R.layout.simple_list_item_2, null, form, to, 0);

        setListAdapter(mCursorAdapter);
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();

        mTodoDB.close();
    }

    public void startDetail(long rowId, boolean created) {
        Intent intent = new Intent(this, DetailActivity.class);

        if (!created) {
            intent.putExtra("ROW_ID", rowId);
        }

        startActivity(intent);
    }

    @Override
    protected void onListItemClick(ListView l, View v, int position, long id) {
        super.onListItemClick(l, v, position, id);

        startDetail(id, false);
    }
}
