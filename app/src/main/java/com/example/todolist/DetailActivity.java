package com.example.todolist;

import android.database.Cursor;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.EditText;

public class DetailActivity extends AppCompatActivity {
    private TodoDB mTodoDB;
    private Long mRowId;
    private EditText mEditTitle;
    private EditText mEditBody;
    private CheckBox mCheckDone;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_detail);

        mEditTitle = findViewById(R.id.editTitle);
        mEditBody = findViewById(R.id.editBody);
        mCheckDone = findViewById(R.id.checkDone);

        mRowId = null;

        if (savedInstanceState == null) {
            Bundle data = getIntent().getExtras();

            if (data != null && data.containsKey("ROW_ID")) {
                mRowId = data.getLong("ROW_ID");
            }
        }

        mTodoDB = new TodoDB(this);
        mTodoDB.open();

        if (mRowId != null) {
            Cursor cursor = mTodoDB.query(mRowId);

            mEditTitle.setText(cursor.getString(TodoDB.TodoEntry.INDEX_TITLE));
            mEditBody.setText(cursor.getString(TodoDB.TodoEntry.INDEX_BODY));
            mCheckDone.setChecked(cursor.getInt(TodoDB.TodoEntry.INDEX_STATE) > 0);

            cursor.close();
        }

        Button button = findViewById(R.id.btnSave);
        button.setOnClickListener(new Button.OnClickListener() {
            @Override
            public void onClick(View view) {
                finish();
            }
        });
    }

    @Override
    protected void onPause() {
        super.onPause();

        String title = mEditTitle.getText().toString();
        String body = mEditBody.getText().toString();
        int done = mCheckDone.isChecked() ? 1 : 0;

        if (mRowId != null) {
            mTodoDB.updateRow(mRowId, mTodoDB.createContentValues(title, body, done));
        } else {
            mRowId = mTodoDB.addRow(mTodoDB.createContentValues(title, body, done));
        }
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();

        mTodoDB.close();
    }
}
