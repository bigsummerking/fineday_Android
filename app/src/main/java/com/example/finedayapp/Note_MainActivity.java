package com.example.finedayapp;

import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.database.sqlite.SQLiteDatabase;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.os.Bundle;
import android.util.DisplayMetrics;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuItem;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.view.WindowManager;
import android.widget.AdapterView;
import android.widget.ListView;
import android.widget.PopupWindow;
import android.widget.RelativeLayout;
import android.widget.TextView;

import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.widget.SearchView;
import androidx.appcompat.widget.Toolbar;

import com.example.finedayapp.PersonNote.CRUD;
import com.example.finedayapp.PersonNote.Note;
import com.example.finedayapp.PersonNote.NoteAdapter;
import com.example.finedayapp.PersonNote.NoteDatabase;
import com.google.android.material.floatingactionbutton.FloatingActionButton;

import java.util.ArrayList;
import java.util.List;

public class Note_MainActivity extends BaseActivity implements AdapterView.OnItemClickListener {

    private NoteDatabase dbHelper;
    private FloatingActionButton btn;
    private Context context = this;
    private ListView lv;
    private Toolbar myToolbar;
    private NoteAdapter adapter;

    private List<Note> noteList = new ArrayList<Note>();

    //弹出菜单
    private PopupWindow popupWindow;
    private PopupWindow popupCover;
    private ViewGroup customView;
    private ViewGroup coverView;
    private RelativeLayout main;
    private LayoutInflater layoutInflater;
    private WindowManager wm;
    private DisplayMetrics metrics;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.note_main);
        btn = (FloatingActionButton) findViewById(R.id.note_main_fab);
        lv = findViewById(R.id.lv);
        myToolbar = findViewById(R.id.myToolbar);
        adapter = new NoteAdapter(getApplicationContext(), noteList);
        refreshListView();
        lv.setAdapter(adapter);

        setSupportActionBar(myToolbar);
        getSupportActionBar().setHomeButtonEnabled(true);
        //设置toolbar取代actionbar
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);

        initPopUpView();
        myToolbar.setNavigationIcon(R.drawable.ic_menu_black_24dp);
        myToolbar.setNavigationOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                showPopUpWindow();
            }
        });

        lv.setOnItemClickListener(this);

        //添加标签点击事件
        btn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(Note_MainActivity.this, Note_EditActivity.class);
                //mode=4，为新增模式
                intent.putExtra("mode", 4);
                //从编辑页面收集数据
                //onActivityResult()接收返回值
                startActivityForResult(intent, 0);
            }
        });
    }

    //获取屏幕数据
    public void initPopUpView() {
        layoutInflater = (LayoutInflater) Note_MainActivity.this.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        customView = (ViewGroup) layoutInflater.inflate(R.layout.note_setting_layout, null);
        coverView = (ViewGroup) layoutInflater.inflate(R.layout.note_setting_cover, null);
        main = findViewById(R.id.main_layout);

        //获取屏幕宽高
        wm = getWindowManager();
        metrics = new DisplayMetrics();
        wm.getDefaultDisplay().getMetrics(metrics);
    }

    //弹出菜单
    public void showPopUpWindow() {
        int width = metrics.widthPixels;
        int height = metrics.heightPixels;
        popupCover = new PopupWindow(coverView, width, height, false);
        popupWindow = new PopupWindow(customView, (int) (width * 0.7), height, true);
        popupWindow.setBackgroundDrawable(new ColorDrawable((Color.WHITE)));
        //在主界面加载成功之后，显示弹出
        findViewById(R.id.main_layout).post(new Runnable() {
            @Override
            public void run() {
                popupCover.showAtLocation(main, Gravity.NO_GRAVITY, 0, 0);
                popupWindow.showAtLocation(main, Gravity.NO_GRAVITY, 0, 0);

                coverView.setOnTouchListener(new View.OnTouchListener() {
                    @Override
                    public boolean onTouch(View v, MotionEvent event) {
                        popupWindow.dismiss();
                        return true;
                    }
                });

                popupWindow.setOnDismissListener(new PopupWindow.OnDismissListener() {
                    @Override
                    public void onDismiss() {
                        popupCover.dismiss();
                    }
                });
            }
        });
    }

    //查找功能
    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.main_menu, menu);

        //首页查找渲染
        MenuItem mSearch = menu.findItem(R.id.action_search);
        SearchView mSearchView = (SearchView) mSearch.getActionView();
        mSearchView.setQueryHint("寻找记忆");
        mSearchView.setOnQueryTextListener(new SearchView.OnQueryTextListener() {
            @Override
            public boolean onQueryTextSubmit(String query) {
                return false;
            }

            @Override
            public boolean onQueryTextChange(String newText) {
                adapter.getFilter().filter(newText);
                return false;
            }
        });

        return super.onCreateOptionsMenu(menu);
    }


    //接收startActivityForResult的结果
    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        //返回模式
        int returnMode;
        //返回标签id
        long note_Id;
        //接收返回值
        returnMode = data.getExtras().getInt("mode", -1);
        note_Id = data.getExtras().getLong("id", 0);
        //接收编辑页面的便签数据
        //returnMode == 1，更改当前便签
        //update current note
        if (returnMode == 1) {
            String content = data.getExtras().getString("content");
            String time = data.getExtras().getString("time");
            int tag = data.getExtras().getInt("tag", 1);
            Note newNote = new Note(content, time, tag);
            newNote.setId(note_Id);
            CRUD op = new CRUD(context);
            op.open();
            op.updateNote(newNote);
            op.close();
        }
        //returnMode == 0，添加当前便签,create new note
        else if (returnMode == 0) {
            String content = data.getExtras().getString("content");
            String time = data.getExtras().getString("time");
            int tag = data.getExtras().getInt("tag", 1);
            Note newNote = new Note(content, time, tag);
            CRUD op = new CRUD(context);
            op.open();
            op.addNote(newNote);
            op.close();
        }
        //returnMode == 2，删除当前便签,delete
        else if (returnMode == 2) {
            Note curNote = new Note();
            curNote.setId(note_Id);
            CRUD op = new CRUD(context);
            op.open();
            op.removeNote(curNote);
            op.close();
        } else {
        }
        refreshListView();
        super.onActivityResult(requestCode, resultCode, data);
    }

    //删除所有便签
    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case R.id.menu_clear:
                new AlertDialog.Builder(Note_MainActivity.this)
                        .setMessage("要狠心删掉一切么 ?")
                        .setPositiveButton(android.R.string.yes, new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialog, int which) {
                                dbHelper = new NoteDatabase(context);
                                SQLiteDatabase db = dbHelper.getWritableDatabase();
                                db.delete("notes", null, null);
                                db.execSQL("update sqlite_sequence set seq=0 where name='notes'"); //reset id to 1
                                refreshListView();
                            }
                        }).setNegativeButton(android.R.string.no, new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        dialog.dismiss();
                    }
                }).create().show();
                break;
        }
        return super.onOptionsItemSelected(item);
    }

    //刷新数据
    public void refreshListView() {
        CRUD op = new CRUD(context);
        op.open();
        // set adapter
        if (noteList.size() > 0) noteList.clear();
        noteList.addAll(op.getAllNotes());
        op.close();
        adapter.notifyDataSetChanged();
    }

    //Listitem点击事件
    @Override
    public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
        switch (parent.getId()) {
            case R.id.lv:
                Note curNote = (Note) parent.getItemAtPosition(position);
                Intent intent = new Intent(Note_MainActivity.this, Note_EditActivity.class);
                intent.putExtra("content", curNote.getContent());
                intent.putExtra("id", curNote.getId());
                intent.putExtra("time", curNote.getTime());
                //mode=3，为编辑模式
                intent.putExtra("mode", 3);
                intent.putExtra("tag", curNote.getTag());
                //从编辑页面收集数据
                startActivityForResult(intent, 1);
                break;
        }
    }
}
