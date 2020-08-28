package com.kunsan.ac.kr.sosua;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.CompoundButton;
import android.widget.EditText;
import android.widget.Switch;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;

import static com.kunsan.ac.kr.sosua.MainActivity.db;
import static com.kunsan.ac.kr.sosua.MainActivity.pwDBHelper;


public class Diary_Setting extends Fragment {

    private Switch pwSwitch;
    private EditText pwEditText;
    private Button pwSaveBtn;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.setting_1, container, false);
        pwSwitch = (Switch) view.findViewById(R.id.switch1);
        pwEditText = (EditText) view.findViewById(R.id.secret_editText);
        pwSaveBtn = (Button) view.findViewById(R.id.secret_save_btn);
        pwSwitch.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton compoundButton, boolean isChecked) {
                if (isChecked) {//on
                    pwEditText.setVisibility(View.VISIBLE);
                } else {//off
                    pwEditText.setVisibility(View.INVISIBLE);
                }
            }
        });
        pwSaveBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                String str = pwEditText.getText().toString();
                passDB(str);
            }
        });
        return view;
    }

    //비밀번호 입력 메소드
    public void passDB(String str){
        db = pwDBHelper.getWritableDatabase();
        String sql = "insert into password values('" + str+"')";
        db.execSQL(sql);
        db.close();
        pwEditText.setText("");
    }
}
