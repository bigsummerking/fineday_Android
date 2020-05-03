package com.example.finedayapp;

import android.content.Intent;
import android.database.Cursor;
import android.os.Bundle;
import android.os.Handler;
import android.view.Gravity;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.AbsListView;
import android.widget.AdapterView;
import android.widget.ListView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.SearchView;
import androidx.swiperefreshlayout.widget.SwipeRefreshLayout;

import com.example.finedayapp.StarMovie.L;
import com.example.finedayapp.StarMovie.Movie;
import com.example.finedayapp.StarMovie.MovieListAdapter;

import org.jetbrains.annotations.NotNull;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import okhttp3.Call;
import okhttp3.Callback;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.Response;

public class Movie_MainActivity extends AppCompatActivity implements SwipeRefreshLayout.OnRefreshListener, AdapterView.OnItemClickListener {

    public String responseData;
    private List<Movie> mMoiveBeanList;
    private MovieListAdapter adapter;
    private SwipeRefreshLayout refreshLayout;
    private ListView lv_moive;
    private AbsListView.OnScrollListener scrollListener = new AbsListView.OnScrollListener() {
        @Override
        public void onScrollStateChanged(AbsListView view, int scrollState) {
        }

        @Override
        public void onScroll(AbsListView view, int firstVisibleItem, int visibleItemCount, int totalItemCount) {
          //解决SwipeRefreshLayout 和ListView 刷新冲突
            boolean enable = false;
            //通过滚动时动态判断是达到顶部来屏蔽
            if (view != null && view.getChildCount() > 0) {
                boolean firstItemVisible = view.getFirstVisiblePosition() == 0;
                boolean topOfFirstItemVisible = view.getChildAt(0).getTop() == 0;
                enable = firstItemVisible && topOfFirstItemVisible;
            }
            refreshLayout.setEnabled(enable);
        }
    };

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.movie_activity_main);
        getSupportActionBar().setHomeButtonEnabled(true);
        try {
            doGet();
        } catch (IOException e) {
            e.printStackTrace();
        }
        mMoiveBeanList = new ArrayList<>();
        lv_moive = findViewById(R.id.lv_movie_main);
        adapter = new MovieListAdapter(this, mMoiveBeanList);
        lv_moive.setAdapter(adapter);
        lv_moive.setOnItemClickListener(this);
        refreshLayout = (SwipeRefreshLayout) findViewById(R.id.swipe);
        refreshLayout.setDistanceToTriggerSync(400);
        refreshLayout.setOnRefreshListener(this);
        lv_moive.setOnScrollListener(scrollListener);
    }

    @Override
    public void onRefresh() {
        //设置刷新动画
        refreshLayout.setRefreshing(true);
        //TODO something you want to do
        new Handler().postDelayed(new Runnable() {
            @Override
            public void run() {
                refreshLayout.setRefreshing(false);
                try {
                    doGet();
                } catch (IOException e) {
                    e.printStackTrace();
                }
                Toast toast = Toast.makeText(getApplicationContext(), "刷新成功", Toast.LENGTH_SHORT);
                toast.setGravity(Gravity.CENTER | Gravity.CENTER, 0, 0);
                toast.show();
            }
        }, 3000);
    }


    public void doGet() throws IOException {
        //1.拿到OkHttpClient对象
        OkHttpClient okHttpClient = new OkHttpClient();
        //2.构造Request
        Request.Builder builder = new Request.Builder();
        //通过System.currentTimeMillis()来获取随机数。实际上是获取当前时间毫秒数，它是long类型。
        final long l = System.currentTimeMillis();
        //获取[0, 100)之间的int整数
        final int i = (int) (l % 249);
        //Request request = builder.get().url("https://douban.uieee.com/v2/movie/in_theaters?start=0&count=20").build();
        Request request = builder.get().url("https://api.douban.com/v2/movie/top250?apikey=0df993c66c0c636e29ecbb5344252a4a&start=" + i + "&count=1").build();

        //3.将Request封装为Call
        Call call = okHttpClient.newCall(request);
        // Response response = call.execute();
        //4.执行call
        call.enqueue(new Callback() {
            @Override
            public void onFailure(@NotNull Call call, @NotNull IOException e) {
                L.e("onFailure" + e.getMessage());
                e.printStackTrace();
            }

            @Override
            public void onResponse(@NotNull Call call, @NotNull Response response) throws IOException {
                L.e("onResponse");
                final String res = response.body().string();
                L.e(res);
                runOnUiThread(new Runnable() {
                    @Override
                    public void run() {

                        //responseData = (String) mtvRes.getText();
                        responseData = res;
                        try {
                            parseJsonWithJsonObject();
                        } catch (IOException e) {
                            e.printStackTrace();
                        }
                    }
                });
            }
        });
    }

    private void parseJsonWithJsonObject() throws IOException {
        System.out.println("responseData：" + responseData);
        try {
            JSONObject jsonObject = new JSONObject(responseData);
            System.out.println("jsonObject：" + jsonObject);
            JSONArray subjects = jsonObject.getJSONArray("subjects");
            System.out.println("subjects：" + subjects);
            for (int i = 0; i < subjects.length(); i++) {
                JSONObject jsonmovie = subjects.getJSONObject(i);
                String title = jsonmovie.getString("title");
                String genres = jsonmovie.getString("genres");
                String year = jsonmovie.getString("year");
                String id = jsonmovie.getString("id");
                JSONObject rating = jsonmovie.getJSONObject("rating");
                String average = rating.getString("average");
                JSONObject images = jsonmovie.getJSONObject("images");
                String image = images.getString("large");
                Movie movie = new Movie();
                movie.setTitle(title);
                movie.setGenres(genres);
                movie.setYear(year);
                movie.setAverage(average);
                movie.setImage(image);
                movie.setMoiveid(id);
                //mDatabaseHelper.insertMovie(movie);
                if (mMoiveBeanList.size() > 0) {
                    mMoiveBeanList.add(0, movie);
                } else {
                    mMoiveBeanList.add(movie);
                }
                adapter.notifyDataSetChanged();
                System.out.println(title + " " + genres + " " + year + " " + average + " " + image);
            }
        } catch (JSONException e) {
            e.printStackTrace();
        }
    }

    @Override
    public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
        switch (parent.getId()) {
            case R.id.lv_movie_main:
                Movie curMovie = (Movie) parent.getItemAtPosition(position);
                Intent intent = new Intent(Movie_MainActivity.this, Movie_ShowActivity.class);
                intent.putExtra("moviename", curMovie.getTitle());
                intent.putExtra("movieid", curMovie.getMoiveid());
                Toast toast = Toast.makeText(getApplicationContext(), "正在为你查找" + curMovie.getTitle(), Toast.LENGTH_SHORT);
                toast.setGravity(Gravity.CENTER | Gravity.CENTER, 0, 0);
                toast.show();
                startActivity(intent);
                break;
        }
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.movie_menu, menu);
        //首页查找渲染
        MenuItem mSearch = menu.findItem(R.id.action_search);
        SearchView mSearchView = (SearchView) mSearch.getActionView();
        mSearchView.setQueryHint("输入电影名称");
        mSearchView.setOnQueryTextListener(new SearchView.OnQueryTextListener() {
            @Override
            public boolean onQueryTextSubmit(String query) {
                Intent intent = new Intent(Movie_MainActivity.this, Movie_SearchActivity.class);
                intent.putExtra("moviename", query);
                Toast toast = Toast.makeText(getApplicationContext(), query, Toast.LENGTH_SHORT);
                startActivity(intent);
                toast.show();
                toast.setGravity(Gravity.CENTER | Gravity.CENTER, 0, 0);
                return false;
            }

            @Override
            public boolean onQueryTextChange(String newText) {
                return false;
            }
        });

        return super.onCreateOptionsMenu(menu);
    }
}
