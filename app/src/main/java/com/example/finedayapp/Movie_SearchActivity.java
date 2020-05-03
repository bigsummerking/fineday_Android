package com.example.finedayapp;

import android.content.Intent;
import android.os.Bundle;
import android.view.Gravity;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ListView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.SearchView;

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

public class Movie_SearchActivity extends AppCompatActivity implements AdapterView.OnItemClickListener {

    private String moviename;
    public String responseData;
    private List<Movie> mMoiveBeanList;
    private MovieListAdapter adapter;
    private ListView lv_moive;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.movie_activity_search);
        Intent getIntent = getIntent();
        moviename = getIntent.getStringExtra("moviename");
        try {
            doGet();
        } catch (IOException e) {
            e.printStackTrace();
        }
        Toast toast = Toast.makeText(getApplicationContext(), moviename, Toast.LENGTH_SHORT);
        toast.show();
        toast.setGravity(Gravity.CENTER | Gravity.CENTER, 0, 0);
        mMoiveBeanList = new ArrayList<>();
        lv_moive = findViewById(R.id.lv_movie_main);
        adapter = new MovieListAdapter(this, mMoiveBeanList);
        lv_moive.setAdapter(adapter);
        lv_moive.setOnItemClickListener(this);
    }

    public void doGet() throws IOException {
        //1.拿到OkHttpClient对象
        OkHttpClient okHttpClient = new OkHttpClient();
        //2.构造Request
        Request.Builder builder = new Request.Builder();
        //Request request = builder.get().url("https://douban.uieee.com/v2/movie/in_theaters?start=0&count=20").build();
        Request request = builder.get().url("https://movie.douban.com/j/subject_suggest?q=" + moviename).build();
        System.out.println("https://movie.douban.com/j/subject_suggest?q=" + moviename);
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
            JSONArray jsonArray = new JSONArray(responseData);
            System.out.println("jsonArray：" + jsonArray);
            for (int i = 0; i < jsonArray.length(); i++) {
                JSONObject jsonmovie = jsonArray.getJSONObject(i);
                String title = jsonmovie.getString("title");
                String id = jsonmovie.getString("id");
                String img = jsonmovie.getString("img");
                StringBuilder sb = new StringBuilder(img);
                String imgstr = sb.replace(11, 12, "9").toString();
                Movie movie = new Movie();
                movie.setTitle(title);
                movie.setImage(imgstr);
                movie.setMoiveid(id);
                mMoiveBeanList.add(movie);
                adapter.notifyDataSetChanged();
            }
        } catch (JSONException e) {
            e.printStackTrace();
        }
    }

    @Override
    public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
        switch (parent.getId()) {
            case R.id.lv_movie_main:
                Movie_SearchActivity.this.finish();
                Movie curMovie = (Movie) parent.getItemAtPosition(position);
                Intent intent = new Intent(Movie_SearchActivity.this, Movie_ShowActivity.class);
                intent.putExtra("moviename", curMovie.getTitle());
                intent.putExtra("movieid", curMovie.getMoiveid());
                startActivity(intent);
                Toast toast = Toast.makeText(getApplicationContext(), "正在为你查找" + curMovie.getTitle(), Toast.LENGTH_SHORT);
                toast.setGravity(Gravity.CENTER | Gravity.CENTER, 0, 0);
                toast.show();
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
                Intent intent = new Intent(Movie_SearchActivity.this, Movie_SearchActivity.class);
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
