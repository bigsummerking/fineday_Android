package com.example.finedayapp.DailyCount;

import android.content.Context;
import android.content.SharedPreferences;
import android.preference.PreferenceManager;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.TextView;
import com.example.finedayapp.R;

import java.util.List;

public class CostListAdapter extends BaseAdapter {

    private List<CostBean> mList;
    private Context mContext;
    private LayoutInflater mLayoutInflater;


    public CostListAdapter(Context context, List<CostBean> list) {
        mContext = context;
        mList = list;
        mLayoutInflater = LayoutInflater.from(context);
    }

    @Override
    public int getCount() {
        return mList.size();
    }

    @Override
    public Object getItem(int position) {
        return mList.get(position);
    }

    @Override
    public long getItemId(int position) {
        return position;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        ViewHolder viewHolder;
        if (convertView == null) {
            viewHolder = new ViewHolder();
            convertView = mLayoutInflater.inflate(R.layout.count_list_item, null);
            viewHolder.mTvCostTitle = (TextView) convertView.findViewById(R.id.tv_title);
            viewHolder.mTvCostTitle.setSelected(true);
            viewHolder.mTvCostDate = (TextView) convertView.findViewById(R.id.tv_date);
            viewHolder.mTvCostMoney = (TextView) convertView.findViewById(R.id.tv_cost);
            convertView.setTag(viewHolder);
        } else {
            viewHolder = (ViewHolder) convertView.getTag();
        }
        CostBean bean = mList.get(position);
        viewHolder.mTvCostTitle.setText(bean.getCostTitle());
        viewHolder.mTvCostDate.setText(bean.getCostDate());
        viewHolder.mTvCostMoney.setText(bean.getCostMoney());
        return convertView;
    }

    private static class ViewHolder {
        public TextView mTvCostTitle;
        public TextView mTvCostDate;
        public TextView mTvCostMoney;
    }
}
