package com.example.finedayapp.StarMovie;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

import androidx.annotation.Nullable;

public class MovieDatabaseHelper extends SQLiteOpenHelper {


    public static final String MOVIE_TITLE = "movie_title";
    public static final String MOVIE_GENRES = "movie_genres";
    public static final String MOVIE_YEAR = "movie_year";
    public static final String MOVIE_AVERAGE = "movie_average";
    public static final String MOVIE_IMG = "movie_img";
    public static final String MOVIE_ID = "id";
    public static final String MOVIE = "Movie";


    public MovieDatabaseHelper(@Nullable Context context) {
        super(context, "MovieApp", null, 1);
    }

    @Override
    public void onCreate(SQLiteDatabase db) {
        db.execSQL( "create table if not exists Movie("+
                "id integer primary key, " +
                "movie_title varchar, " +
                "movie_genres varchar, " +
                "movie_year varchar, " +
                "movie_average varchar, " +
                "movie_img varchar)");
    }
    public void insertMovie(Movie movie){
        SQLiteDatabase database = getWritableDatabase();
        ContentValues cv = new ContentValues();
        cv.put(MOVIE_TITLE,movie.getTitle());
        cv.put(MOVIE_GENRES,movie.getGenres());
        cv.put(MOVIE_YEAR,movie.getYear());
        cv.put(MOVIE_AVERAGE,movie.getAverage());
        cv.put(MOVIE_IMG,movie.getImage());
        database.insert(MOVIE,null,cv);
    }

    public Cursor getAllMovieData(){
        SQLiteDatabase database = getWritableDatabase();
        return database.query(MOVIE,null,null,null,null,null,MOVIE_ID+" DESC");
    }

    public void deleteAllMovieData(){
        SQLiteDatabase database = getWritableDatabase();
        database.delete(MOVIE,null,null);
    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {

    }
}
