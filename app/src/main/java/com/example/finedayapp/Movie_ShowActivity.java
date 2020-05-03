package com.example.finedayapp;

import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.view.Gravity;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import com.example.finedayapp.StarMovie.L;

import org.jetbrains.annotations.NotNull;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;

import okhttp3.Call;
import okhttp3.Callback;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.Response;

public class Movie_ShowActivity extends AppCompatActivity {

    private String movieid;
    public String responseData;
    private String movieurl;
    private String prefixurl = "https://jx.618g.com/?url=";
    //private String prefixurl = "https://17kyun.com/api.php?url=";
    //private String prefixurl = "https://jx.000180.top/jx/?url=";

    private String finalurl;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.movie_play);
        Intent getIntent = getIntent();
        movieid = getIntent.getStringExtra("movieid");
        try {
            doGet();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public void doGet() throws IOException {
        //1.拿到OkHttpClient对象
        OkHttpClient okHttpClient = new OkHttpClient();
        //2.构造Request
        Request.Builder builder = new Request.Builder();
        Request request = builder.get().url("https://douban.uieee.com/v2/movie/subject/" + movieid).build();
        //3.将Request封装为Call
        Call call = okHttpClient.newCall(request);
        //4.执行call
        call.enqueue(new Callback() {
            @Override
            public void onFailure(@NotNull Call call, @NotNull IOException e) {
                L.e("onFailure" + e.getMessage());
                e.printStackTrace();
                Movie_ShowActivity.this.finish();
                NoMovieSource("连接超时");
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
                        } catch (IOException | JSONException e) {
                            e.printStackTrace();
                        }
                    }
                });
            }
        });
    }

    private boolean parseJsonWithJsonObject() throws IOException, JSONException {
        System.out.println("responseData:" + responseData);
        JSONObject jsonObject = new JSONObject(responseData);
        System.out.println("jsonObject:" + jsonObject);
        if (!jsonObject.isNull("msg") || jsonObject.getJSONArray("videos").isNull(0)) {
            NoMovieSource("对不起，无法提供视频");
            Movie_ShowActivity.this.finish();
            return false;
        } else if (!jsonObject.getJSONArray("videos").isNull(0)) {
            JSONArray videos = jsonObject.getJSONArray("videos");
            for (int i = 0; i < videos.length(); i++) {
                String videoweb = videos.getJSONObject(i).getJSONObject("source").getString("literal");
                if ((videoweb.equals("iqiyi") || videoweb.equals("qq") || videoweb.equals("youku") || videoweb.equals("huanxi"))) {
                    JSONObject video = videos.getJSONObject(i);
                    movieurl = video.getString("sample_link");
                    finalurl = prefixurl + movieurl;
                    System.out.println("finalurl:" + finalurl);
                    /**
                     Uri uri = Uri.parse("http://www.baidu.com"); //浏览器
                     Uri uri =Uri.parse("tel:13838383838"); //拨号程序
                     Uri uri=Uri.parse("geo:38.383838,383.383838"); //打开地图定位
                     **/
                    //通过Intent打开系统浏览器访问网页
                    Uri uri = Uri.parse(finalurl);
                    Intent intent = new Intent(Intent.ACTION_VIEW, uri);
                    startActivity(intent);
                    Movie_ShowActivity.this.finish();
                    return false;
                }

            }
        } else {
            NoMovieSource("对不起，无法提供视频");
            Movie_ShowActivity.this.finish();
            return false;
        }
        return false;
    }

    public void NoMovieSource(String msg) {
        Toast toast = Toast.makeText(getApplicationContext(), msg, Toast.LENGTH_SHORT);
        toast.setGravity(Gravity.CENTER | Gravity.CENTER, 0, 0);
        toast.show();
    }
}
