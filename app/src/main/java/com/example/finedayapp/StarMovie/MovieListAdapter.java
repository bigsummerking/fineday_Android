package com.example.finedayapp.StarMovie;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.Handler;
import android.os.Message;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.TextView;

import com.example.finedayapp.R;

import java.io.InputStream;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.ArrayList;
import java.util.List;

public class MovieListAdapter extends BaseAdapter {

    private List<Movie> mList;
    private Context mContext;
    private LayoutInflater mLayoutInflater;

    public MovieListAdapter(Context context, List<Movie> list) {
        mList = list;
        mContext = context;
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

     Handler handle = new Handler() {
        public void handleMessage(Message msg) {
            switch (msg.what) {
                case 0:
                    List<Object> bigguy = (List<Object>) msg.obj;
                    Bitmap bmp = (Bitmap)bigguy.get(0);
                    ImageView imageView = (ImageView) bigguy.get(1);
                    imageView.setImageBitmap(bmp);
                    System.out.println("加载图片成功");
                    break;
            }
        }
    };
    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        final ViewHolder viewHolder;
        if (convertView == null) {
            viewHolder = new ViewHolder();
            convertView = mLayoutInflater.inflate(R.layout.movie_list_item, null);
            viewHolder.mIgMovieImg = convertView.findViewById(R.id.imageView);
            viewHolder.mTvMovieTitle = convertView.findViewById(R.id.id_tv_title);
            viewHolder.mTvMovieGenres = convertView.findViewById(R.id.id_tv_genres);
            viewHolder.mTvMovieYear = convertView.findViewById(R.id.id_tv_year);
            viewHolder.mTvMovieAverage = convertView.findViewById(R.id.id_tv_average);
            convertView.setTag(viewHolder);
        } else {
            viewHolder = (ViewHolder) convertView.getTag();
        }
        final Movie bean = mList.get(position);
        new Thread(new Runnable() {
            @Override
            public void run() {
                Bitmap bmp = getURLimage(bean.getImage());
                List<Object> bigguy = new ArrayList<>();
                bigguy.add(bmp);
                bigguy.add(viewHolder.mIgMovieImg);
                Message msg = new Message();
                msg.what = 0;
                msg.obj = bigguy;
                System.out.println("准备加载图片");
                handle.sendMessage(msg);
            }
        }).start();
        viewHolder.mTvMovieTitle.setText(bean.getTitle());
        viewHolder.mTvMovieGenres.setText(bean.getGenres());
        viewHolder.mTvMovieYear.setText(bean.getYear());
        viewHolder.mTvMovieAverage.setText(bean.getAverage());
        return convertView;
    }

    private static class ViewHolder {
        public ImageView mIgMovieImg;
        public TextView mTvMovieTitle;
        public TextView mTvMovieGenres;
        public TextView mTvMovieYear;
        public TextView mTvMovieAverage;
    }



    //加载图片
    public Bitmap getURLimage(String url) {
        Bitmap bmp = null;
        try {
            URL myurl = new URL(url);
            // 获得连接
            HttpURLConnection conn = (HttpURLConnection) myurl.openConnection();
            conn.setConnectTimeout(6000);//设置超时
            conn.setDoInput(true);
            conn.setUseCaches(false);//不缓存
            conn.connect();
            InputStream is = conn.getInputStream();//获得图片的数据流
            bmp = BitmapFactory.decodeStream(is);//读取图像数据
            //读取文本数据
            //byte[] buffer = new byte[100];
            //inputStream.read(buffer);
            //text = new String(buffer);
            is.close();
        } catch (Exception e) {
            e.printStackTrace();
        }
        return bmp;
    }
}
