package com.kunsan.ac.kr.sosua;

import android.content.Intent;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteStatement;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;

import androidx.appcompat.app.AppCompatActivity;

public class Diary_Update extends AppCompatActivity {
    private Intent intent;
    private EditText editTitle, editContents;
    private int code;
    private Button updateSaveBtn, updateBackBtn;
    private DiaryDBHelper DBHelper;
    private SQLiteDatabase db;
    private Diary diary = new Diary();

    protected void onCreate(Bundle savedInstanceState){
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_diary_update);

        intent = getIntent();
        DBHelper = new DiaryDBHelper(getApplicationContext());
        editTitle = (EditText) findViewById(R.id.edit_title_update);
        editContents = (EditText) findViewById(R.id.edit_contents_update);

        code = intent.getExtras().getInt("code");
        updateSaveBtn = (Button) findViewById(R.id.save_btn_update);
        updateBackBtn = (Button) findViewById(R.id.back_btn_update);
        searchDiary(code);

        editTitle.setText(diary.getTitle().toString());
        editContents.setText(diary.getContents().toString());

        updateSaveBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                updateDB(code);
                finish();
            }
        });

        updateBackBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                finish();
            }
        });
    }

    public Diary searchDiary(int code){
        db = DBHelper.getReadableDatabase();
        String sql = "select * from diary where code =" + code;
        Cursor cursor = db.rawQuery(sql, null);
        while(cursor.moveToNext()){
            diary.setTitle(cursor.getString(1));
            diary.setContents(cursor.getString(3));
        }
        cursor.close();
        db.close();
        return diary;
    }

    public void updateDB(int code){
        db = DBHelper.getWritableDatabase();
        String sql = "update diary set title =?, contents = ? where code = " + code;
        SQLiteStatement st = db.compileStatement(sql);
        st.bindString(1,editTitle.getText().toString());
        st.bindString(2, editContents.getText().toString());
        st.execute();
        db.close();
    }
}
