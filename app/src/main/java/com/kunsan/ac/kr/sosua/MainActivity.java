package com.kunsan.ac.kr.sosua;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.Fragment;
import androidx.viewpager.widget.ViewPager;

import android.content.DialogInterface;
import android.content.Intent;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteStatement;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.WindowManager;
import android.widget.AdapterView;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ListView;
import android.widget.Switch;
import android.widget.Toast;

import com.google.android.material.tabs.TabLayout;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;

public class MainActivity extends AppCompatActivity {

    private TabLayout tabLayout;
    private ViewPager viewPager;

    private static ArrayList<Diary> data;
    public static DiaryDBHelper DBHelper;
    public static DiaryPassDBHelper pwDBHelper;
    public static SQLiteDatabase db;

    //create
    private static EditText edit_title;
    private static EditText edit_contents;
    private static Button save_btn;

    //view
    private static Button refresh_btn;
    private static ListView list_diary;
    private static DiaryListAdapter listAdapter;

    //setting
    private static Switch pwSwitch;
    private static EditText pwEditText;
    public static Button pwSaveBtn;

    private static EditText checkPass;
    private static int flag = 0;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        //풀 스크린 설정
        getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN,
                WindowManager.LayoutParams.FLAG_FULLSCREEN);
        //첫 화면은 activity_main.xml
        setContentView(R.layout.activity_main);

        //DBHelper 객체 생성
        DBHelper = new DiaryDBHelper(this);
        pwDBHelper = new DiaryPassDBHelper(this);

        //diary형 리스트
        data = new ArrayList<>();

        //Initializing the TabLayout
        tabLayout = (TabLayout) findViewById(R.id.tabLayout);
        tabLayout.addTab(tabLayout.newTab().setText("CREATE"));
        tabLayout.addTab(tabLayout.newTab().setText("VIEW"));
        tabLayout.addTab(tabLayout.newTab().setText("SETTING"));

        //커스텀 뷰페이지 리소스 연결
        viewPager = (ViewPager) findViewById(R.id.pager);

        //Creating TabPagerAdapter adapter
        tabLayout.addOnTabSelectedListener(new TabLayout.OnTabSelectedListener() {
            @Override
            public void onTabSelected(TabLayout.Tab tab) {
                viewPager.setCurrentItem(tab.getPosition());
            }

            @Override
            public void onTabUnselected(TabLayout.Tab tab) {

            }

            @Override
            public void onTabReselected(TabLayout.Tab tab) {

            }
        });
    }

    //일기 쓰기 페이지 메소드
    public static class Diary_Write extends Fragment {
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
    }

    //DB에 일기 저장 메소드
    public static void InsertDB(){
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

    //Diary_List inner class
    public static class Diary_List extends Fragment{
        @Nullable
        @Override
        public View onCreateView(@NonNull final LayoutInflater inflater, @Nullable final ViewGroup container, @Nullable Bundle savedInstanceState) {
           View view = inflater.inflate(R.layout.view_1,container, false);
           flag = 1;
           refresh_btn = (Button) view.findViewById(R.id.refresh_btn_list);

           refresh_btn.setOnClickListener(new View.OnClickListener() {
               @Override
               public void onClick(final View view) {
                   if(pwSwitch.isChecked()){
                       AlertDialog.Builder alertDialog = new AlertDialog.Builder(getContext());
                       alertDialog.setMessage("비밀번호를 입력하세요");
                       View dialogView = inflater.inflate(R.layout.password, container, false);
                       alertDialog.setView(dialogView);
                       checkPass = (EditText)dialogView.findViewById(R.id.check_pass);

                       alertDialog.setPositiveButton("확인", new DialogInterface.OnClickListener() {
                           @Override
                           public void onClick(DialogInterface dialog, int position) {
                                String cp = checkPass();

                                //비번 같을때 리스트 목록 보이기
                                if(checkPass.getText().toString().equals(cp)){
                                    list_diary = (ListView) view.findViewById(R.id.list_diary);
                                    data = showDB();

                                    //리스트 선택 시 내용 보이기
                                    listAdapter = new DiaryListAdapter(getContext(), R.layout.list_layout,data);
                                    list_diary.setAdapter(listAdapter);
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
                                    listAdapter = null;
                                    list_diary.setAdapter(listAdapter);
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

                       

                   }






               }
           });
        }
    }

    //환경설정 inner class
    public static class Diary_Setting extends Fragment{

    }

    //일기 목록 보기 메소드
    public static ArrayList<Diary> showDB(){
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
    public static void deleteDB(int code){
        db = DBHelper.getReadableDatabase();
        String sql = "delete from diary where code = " + code;
        db.execSQL(sql);
        db.close();
    }

    //비밀번호 입력 메소드
    public static void passDB(String str){
        db = pwDBHelper.getWritableDatabase();
        String sql = "insert into password values('" + str+"')";
        db.execSQL(sql);
        db.close();
        pwEditText.setText("");
    }

    //비밀번호 확인 메소드
    public static String checkPass(){
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
}