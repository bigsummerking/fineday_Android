package com.example.finedayapp;

import android.content.DialogInterface;

import android.content.Intent;
import android.database.Cursor;
import android.os.Bundle;

import com.example.finedayapp.DailyCount.CostBean;
import com.example.finedayapp.DailyCount.CostListAdapter;
import com.example.finedayapp.DailyCount.DatabaseHelper;
import com.google.android.material.floatingactionbutton.FloatingActionButton;

import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;

import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.AdapterView;
import android.widget.DatePicker;
import android.widget.EditText;
import android.widget.ListView;
import android.widget.Toast;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

public class Count_MainActivity extends AppCompatActivity {

    private List<CostBean> mCostBeanList;
    private DatabaseHelper mDatabaseHelper;
    private CostListAdapter adapter;
    private ListView lv_cost;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.count_main);
        Toolbar toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        mDatabaseHelper = new DatabaseHelper(this);
        mCostBeanList = new ArrayList<>();
        initCostDate();
        FloatingActionButton fab = findViewById(R.id.fab);
        fab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                AlertDialog.Builder builder = new AlertDialog.Builder(Count_MainActivity.this);
                LayoutInflater inflater = LayoutInflater.from(Count_MainActivity.this);
                View viewDialog = inflater.inflate(R.layout.count_new_cost, null);
                final EditText title = (EditText) viewDialog.findViewById(R.id.et_cost_title);
                final EditText money = (EditText) viewDialog.findViewById(R.id.et_cost_money);
                final DatePicker date = (DatePicker) viewDialog.findViewById(R.id.dp_cost_date);

                builder.setView(viewDialog);
                builder.setTitle("新增记账");
                builder.setPositiveButton("保存", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        if (title.getText().toString() == null || title.getText().toString().length() == 0) {
                            Toast toast = Toast.makeText(getApplicationContext(), "新增失败，下次记得输入消费备注", Toast.LENGTH_SHORT);
                            toast.setGravity(Gravity.CENTER | Gravity.CENTER, 0, 0);
                            toast.show();
                        }
                        if (money.getText().toString() == null || money.getText().toString().length() == 0) {
                            Toast toast = Toast.makeText(getApplicationContext(), "新增失败，下次记得输入消费金额", Toast.LENGTH_SHORT);
                            toast.setGravity(Gravity.CENTER | Gravity.CENTER, 0, 0);
                            toast.show();
                        } else {
                            CostBean costBean = new CostBean();
                            costBean.setCostTitle(title.getText().toString());
                            costBean.setCostMoney(money.getText().toString());
                            costBean.setCostDate(date.getYear() + "-" + (date.getMonth() + 1) + "-" + date.getDayOfMonth());
                            mDatabaseHelper.insertCost(costBean);
                            mCostBeanList.add(costBean);
                            adapter.notifyDataSetChanged();
                        }
                    }
                });
                builder.setNegativeButton("取消", null);
                builder.create().show();
            }

        });
        lv_cost = findViewById(R.id.lv_main);
        adapter = new CostListAdapter(Count_MainActivity.this, mCostBeanList);
        lv_cost.setAdapter(adapter);
        lv_cost.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                final CostBean costdata = mCostBeanList.get(position);
                AlertDialog.Builder builder = new AlertDialog.Builder(Count_MainActivity.this);
                builder.setTitle("确定删除"+costdata.getCostDate() + "的" + costdata.getCostTitle() + "这一条记录么？");
                builder.setPositiveButton("确定", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        mDatabaseHelper.removeCost(costdata);
                        mCostBeanList.remove(costdata);
                        adapter.notifyDataSetChanged();
                        Toast toast = Toast.makeText(getApplicationContext(), "成功删除" + costdata.getCostDate() + "的" + costdata.getCostTitle() + "的记录", Toast.LENGTH_SHORT);
                        toast.setGravity(Gravity.CENTER | Gravity.CENTER, 0, 0);
                        toast.show();
                    }
                });
                builder.setNegativeButton("取消", null);
                builder.create().show();
            }
        });
    }

    private void initCostDate() {
        Cursor cursor = mDatabaseHelper.getAllCostData();
        if (cursor != null) {
            while (cursor.moveToNext()) {
                CostBean costBean = new CostBean();
                costBean.setCostTitle(cursor.getString(cursor.getColumnIndex("cost_title")));
                costBean.setCostDate(cursor.getString(cursor.getColumnIndex("cost_date")));
                costBean.setCostMoney(cursor.getString(cursor.getColumnIndex("cost_money")));
                mCostBeanList.add(costBean);
            }
            cursor.close();
        }
    }


    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_main, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        int id = item.getItemId();
        if (id == R.id.action_chart) {
            Intent intent = new Intent(Count_MainActivity.this, ChartsActivity.class);
            intent.putExtra("cost_list", (Serializable) mCostBeanList);
            startActivity(intent);
            return true;
        }
        return super.onOptionsItemSelected(item);
    }


//    @Override
//    public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
//        switch (parent.getId()) {
//            case R.id.lv_main:
//                CostBean costdata = (CostBean) parent.getItemAtPosition(position);
//                Toast toast = Toast.makeText(getApplicationContext(), "成功删除" + costdata.getCostDate() + "的" + costdata.getCostTitle() + "的记录", Toast.LENGTH_SHORT);
//                toast.setGravity(Gravity.CENTER | Gravity.CENTER, 0, 0);
//                toast.show();
//                AlertDialog.Builder builder = new AlertDialog.Builder(Count_MainActivity.this);
//                builder.setTitle("确定删除这一条记录么？");
//                builder.setPositiveButton("确定", new DialogInterface.OnClickListener() {
//                    @Override
//                    public void onClick(DialogInterface dialog, int which) {
//                        mDatabaseHelper.removeCost(costdata);
//                        mCostBeanList.remove(costdata);
//                        adapter.notifyDataSetChanged();
//                        Toast toast = Toast.makeText(getApplicationContext(), "成功删除" + costdata.getCostDate() + "的" + costdata.getCostTitle() + "的记录", Toast.LENGTH_SHORT);
//                        toast.setGravity(Gravity.CENTER | Gravity.CENTER, 0, 0);
//                        toast.show();
//                    }
//                });
//                builder.setNegativeButton("取消", null);
//                builder.create().show();
//                break;
//       }
}

