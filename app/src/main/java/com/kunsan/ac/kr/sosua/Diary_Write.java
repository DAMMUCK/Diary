package com.kunsan.ac.kr.sosua;

import android.database.sqlite.SQLiteStatement;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;

import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;

import static com.kunsan.ac.kr.sosua.MainActivity.DBHelper;
import static com.kunsan.ac.kr.sosua.MainActivity.db;

public class Diary_Write extends Fragment {

    private EditText edit_title, edit_contents;
    private Button save_btn;



    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.create_1,container,false);
        edit_title = (EditText) view.findViewById(R.id.edit_title_write);
        edit_contents = (EditText) view.findViewById(R.id.edit_contents_write);
        save_btn = (Button) view.findViewById(R.id.save_btn_write);
        save_btn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                InsertDB();
            }
        });
        return view;
    }

    //DB에 일기 저장 메소드
    public void InsertDB(){
        db = DBHelper.getWritableDatabase();
        //데이터 형식 (제목,날짜,내용)
        String sql = "insert into diary ('title', 'date', 'contents' ) values(?,?,?)";
        SQLiteStatement st = db.compileStatement(sql);
        st.bindString(1, edit_title.getText().toString());
        Calendar calendar = Calendar.getInstance();
        Date date =calendar.getTime();
        SimpleDateFormat simpleDateFormat = new SimpleDateFormat("yy년 MM월 dd일 HH시 mm분 ss초");
        String writeTime = simpleDateFormat.format(date);
        st.bindString(2, writeTime);
        st.bindString(3,edit_contents.getText().toString());
        st.execute();
        db.close();
        edit_title.setText("");
        edit_contents.setText("");
    }
}
