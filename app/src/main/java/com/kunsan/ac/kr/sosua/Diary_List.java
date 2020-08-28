package com.kunsan.ac.kr.sosua;

import android.app.assist.AssistStructure;
import android.content.DialogInterface;
import android.content.Intent;
import android.database.Cursor;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ListView;
import android.widget.Switch;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AlertDialog;
import androidx.fragment.app.Fragment;

import java.util.ArrayList;

import static com.kunsan.ac.kr.sosua.MainActivity.DBHelper;
import static com.kunsan.ac.kr.sosua.MainActivity.db;
import static com.kunsan.ac.kr.sosua.MainActivity.pwDBHelper;

public class Diary_List extends Fragment{

    private int flag = 0;
    private Button viewListBtn;
    private Switch pwSwitch;
    private EditText checkPass;
    private ListView list_diary;
    private ArrayList<Diary> data;
    private DiaryListAdapter listAdapter;

    @Nullable
    @Override
    public View onCreateView(@NonNull final LayoutInflater inflater, @Nullable final ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.view_1,container, false);
        flag = 1;
        pwSwitch = (Switch) view.findViewById(R.id.switch1);
        //목록보기 버튼
        viewListBtn = (Button) view.findViewById(R.id.view_list);
        viewListBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(final View view) {
                //비밀번호 설정 되어있을 때
                if(pwSwitch.isChecked()){
                    AlertDialog.Builder alertDialog = new AlertDialog.Builder(getContext());
                    alertDialog.setMessage("비밀번호를 입력하세요");
                    //비밀번호 입력하는 알림창 띄우기
                    View dialogView = inflater.inflate(R.layout.password, container, false);
                    alertDialog.setView(dialogView);
                    checkPass = (EditText)dialogView.findViewById(R.id.check_pass);

                    alertDialog.setPositiveButton("확인", new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialog, int position) {
                            //설정한 비번
                            String cp = checkPass();

                            //비번 같을때 리스트 목록 보이기
                            if(checkPass.getText().toString().equals(cp)){
                                list_diary = (ListView) view.findViewById(R.id.list_diary);
                                data = showDB();
                                listAdapter = new DiaryListAdapter(getContext(), R.layout.list_layout, data);
                                list_diary.setAdapter(listAdapter);

                                //리스트 아이템 클릭 이벤트
                                list_diary.setOnItemClickListener(new AdapterView.OnItemClickListener() {
                                    @Override
                                    public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                                        Intent intent = new Intent(getContext(), Diary_Update.class);
                                        int code = data.get(position).getCode();
                                        intent.putExtra("code",code);
                                        startActivity(intent);
                                    }
                                });

                                //리스트 목록을 롱클릭 시 삭제 알림창 띄우기
                                list_diary.setOnItemLongClickListener(new AdapterView.OnItemLongClickListener() {
                                    @Override
                                    public boolean onItemLongClick(AdapterView<?> parent, View view, int position, long id) {
                                        AlertDialog.Builder dropAlertDialog = new AlertDialog.Builder(getContext());
                                        dropAlertDialog.setMessage(data.get(position).getTitle()+"을(를) 삭제하시겠습니까?");
                                        //삭제
                                        dropAlertDialog.setPositiveButton("삭제", new DialogInterface.OnClickListener() {
                                            @Override
                                            public void onClick(DialogInterface dialog, int position) {
                                                int code = data.get(position).getCode();
                                                deleteDB(code);
                                                showDB();
                                                listAdapter.notifyDataSetChanged();
                                            }
                                        });
                                        //취소
                                        dropAlertDialog.setNegativeButton("취소", new DialogInterface.OnClickListener() {
                                            @Override
                                            public void onClick(DialogInterface dialog, int position) {
                                                dialog.cancel();
                                            }
                                        });
                                        dropAlertDialog.show();
                                        return false;
                                    }
                                });
                            } else{ //비밀번호 틀렸을때
                                Toast.makeText(getContext(),"비밀번호가 틀렸습니다.", Toast.LENGTH_SHORT).show();
                                list_diary = (ListView) view.findViewById(R.id.list_diary);
                                data = showDB();
                                //listAdapter = null;
                                list_diary.setAdapter(null);
                            }
                        }
                    });
                    alertDialog.show();
                }else{
                    list_diary = (ListView) view.findViewById(R.id.list_diary);
                    data = showDB();
                    listAdapter = new DiaryListAdapter(getContext(), R.layout.list_layout, data);
                    list_diary.setAdapter(listAdapter);

                    //리스트 아이템을 클릭했을때
                    list_diary.setOnItemClickListener(new AdapterView.OnItemClickListener() {
                        @Override
                        public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                            Intent intent = new Intent(getContext(), Diary_Update.class);
                            int code = data.get(position).getCode();
                            intent.putExtra("code", code);
                            startActivity(intent);
                        }
                    });

                    //리스트 아이템 롱 클릭시 삭제 알림창 뜸
                    list_diary.setOnItemLongClickListener(new AdapterView.OnItemLongClickListener() {
                        @Override
                        public boolean onItemLongClick(AdapterView<?> parent, View view, int position, long id) {
                            final AlertDialog.Builder dropAlertDialog = new AlertDialog.Builder(getContext());
                            dropAlertDialog.setMessage(data.get(position).getTitle()+"을(를) 삭제하시겠습니까?");
                            dropAlertDialog.setPositiveButton("삭제", new DialogInterface.OnClickListener() {
                                @Override
                                public void onClick(DialogInterface dialog, int position) {
                                    int code = data.get(position).getCode();
                                    deleteDB(code);
                                    showDB();
                                    listAdapter.notifyDataSetChanged();
                                }
                            });
                            dropAlertDialog.setNegativeButton("취소", new DialogInterface.OnClickListener() {
                                @Override
                                public void onClick(DialogInterface dialog, int position) {
                                    dialog.cancel();
                                }
                            });
                            dropAlertDialog.show();
                            return false;
                        }
                    });

                }
            }
        });
        if(flag == 1){ return view;}
        else{ return null; }
    }


    //비밀번호 확인 메소드
    public String checkPass(){
        db = pwDBHelper.getReadableDatabase();
        String sql = "select * from password";
        Cursor cursor = db.rawQuery(sql, null);
        String pass = "";
        while(cursor.moveToNext()){
            pass = cursor.getString(0);
        }
        cursor.close();
        db.close();
        return pass;
    }


    //일기 목록 보기 메소드
    public ArrayList<Diary> showDB(){
        data.clear();
        db = DBHelper.getReadableDatabase();
        String sql = "select * from diary";
        Cursor cursor = db.rawQuery(sql, null);
        while(cursor.moveToNext()){
            Diary diary = new Diary();
            diary.setCode(cursor.getInt(0));
            diary.setTitle(cursor.getString(1));
            diary.setDate(cursor.getString(2));
            diary.setContents(cursor.getString(3));
            data.add(diary);
        }
        cursor.close();
        db.close();
        return data;
    }

    //일기 삭제 메소드
    public void deleteDB(int code){
        db = DBHelper.getReadableDatabase();
        String sql = "delete from diary where code = " + code;
        db.execSQL(sql);
        db.close();
    }

}
