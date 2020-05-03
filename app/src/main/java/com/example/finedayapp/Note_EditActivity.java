package com.example.finedayapp;

import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.view.KeyEvent;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.EditText;

import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.widget.Toolbar;

import java.text.SimpleDateFormat;
import java.util.Date;

public class Note_EditActivity extends BaseActivity {
    private EditText et;
    private String old_content = "";
    private String old_time = "";
    private int old_Tag = 1;
    private long id = 0;
    private int openMode = 0;
    private int tag = 1;
    private boolean tagChange = false;
    private Toolbar myToolbar;
    public Intent intent = new Intent();


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.note_edit_layout);
        myToolbar = (Toolbar) findViewById(R.id.myToolbar);
        et = findViewById(R.id.note_et);
        //接收Intent
        Intent getIntent = getIntent();

        setSupportActionBar(myToolbar);
        getSupportActionBar().setHomeButtonEnabled(true);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);

        myToolbar.setNavigationOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                autoSetMessage();
                //RESULT_OK=-1
                setResult(RESULT_OK, intent);
                finish();
            }
        });

        openMode = getIntent.getIntExtra("mode", 0);

        if (openMode == 3) {//打开已存在的note
            id = getIntent.getLongExtra("id", 0);
            old_content = getIntent.getStringExtra("content");
            old_time = getIntent.getStringExtra("time");
            old_Tag = getIntent.getIntExtra("tag", 1);
            et.setText(old_content);
            et.setSelection(old_content.length());
        }

    }

    @Override
    public boolean onKeyDown(int keyCode, KeyEvent event) {
        //点击home键
        if (keyCode == KeyEvent.KEYCODE_HOME) {
            return true;
        }
        //点击返回键
        else if (keyCode == KeyEvent.KEYCODE_BACK) {
            autoSetMessage();
            setResult(RESULT_OK, intent);
            finish();
            return true;
        }
        return super.onKeyDown(keyCode, event);
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.edit_menu, menu);
        return super.onCreateOptionsMenu(menu);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        final Intent intent = new Intent();
        switch (item.getItemId()) {
            case R.id.delete:
                new AlertDialog.Builder(Note_EditActivity.this)
                        .setMessage("真的要丢弃么 ?")
                        .setPositiveButton(android.R.string.yes, new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialog, int which) {
                                // 4 is add
                                if (openMode == 4) {
                                    intent.putExtra("mode", -1); // delete the note
                                    setResult(RESULT_OK, intent);
                                } else {
                                    //delete exist note
                                    intent.putExtra("mode", 2); // delete the note
                                    intent.putExtra("id", id);
                                    setResult(RESULT_OK, intent);
                                }
                                finish();
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

    public void autoSetMessage() {
        //4 is add
        if (openMode == 4) {
            //文本长度为0，新增取消
            if (et.getText().toString().length() == 0) {
                intent.putExtra("mode", -1); //nothing new happens.
            } else {
                intent.putExtra("mode", 0); // new one note;
                intent.putExtra("content", et.getText().toString());
                intent.putExtra("time", dateToStr());
                intent.putExtra("tag", tag);
            }
        } else {
            //3 is updata
            //文本内容不变，修改取消
            if (et.getText().toString().equals(old_content) && !tagChange)
                intent.putExtra("mode", -1); // edit nothing
            else {
                intent.putExtra("mode", 1); //edit the content
                intent.putExtra("content", et.getText().toString());
                intent.putExtra("time", dateToStr());
                intent.putExtra("id", id);
                intent.putExtra("tag", tag);
            }
        }

    }

    public String dateToStr() {
        Date date = new Date();
        SimpleDateFormat simpleDateFormat = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
        return simpleDateFormat.format(date);
    }

}
