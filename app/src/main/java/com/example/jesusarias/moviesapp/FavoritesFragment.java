package com.example.jesusarias.moviesapp;

import android.content.SharedPreferences;
import android.os.Bundle;
import android.os.Handler;
import android.os.Looper;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v7.widget.GridLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.example.jesusarias.moviesapp.Adapters.FavoritesRecyclerViewAdapter;
import com.example.jesusarias.moviesapp.DataClasses.Favorites;
import com.google.gson.Gson;

import java.io.IOException;
import java.util.ArrayList;
import java.util.prefs.PreferenceChangeListener;

import okhttp3.Call;
import okhttp3.Callback;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.Response;

public class FavoritesFragment extends Fragment {

    String[] parts;

    private RecyclerView mFavoritesRecyclerView;
    private FavoritesRecyclerViewAdapter mFavoritesAdapter;
    private Handler mHandler;
    private SharedPreferences.OnSharedPreferenceChangeListener listener;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater,
                             @Nullable ViewGroup container,
                             @Nullable Bundle savedInstanceState) {
        super.onCreateView(inflater, container, savedInstanceState);
        final View view = inflater.inflate(R.layout.favorites_fragment, container, false);

        mFavoritesRecyclerView = view.findViewById(R.id.rv_favorite_list);
        mFavoritesRecyclerView.setLayoutManager(new GridLayoutManager(getContext(), 3));
        mFavoritesAdapter = new FavoritesRecyclerViewAdapter(getContext(), new ArrayList<Favorites>());
        mFavoritesRecyclerView.setAdapter(mFavoritesAdapter);
        mFavoritesRecyclerView.setHasFixedSize(true);
        mHandler = new Handler(Looper.getMainLooper());

        SharedPreferences pref = getContext().getSharedPreferences("Movies", 0); // 0 - for private mode
        final SharedPreferences.Editor editor = pref.edit();
        String movieIds = pref.getString("MOVIE_ID", "");

        if (movieIds != "") {
            parts = movieIds.split(";");
            String parsedIds = "";
            for (int i = 0; i < parts.length; i++) {
                parsedIds = parsedIds + parts[i] + "\n";
                loadMovieInfo(parts[i]);
            }
        }

        listener = new SharedPreferences.OnSharedPreferenceChangeListener() {
            @Override
            public void onSharedPreferenceChanged(SharedPreferences sharedPreferences, String key) {
               refreshFavorites();
            }
        };
        pref.registerOnSharedPreferenceChangeListener(listener);

        return view;
    }

    public void refreshFavorites(){
        mFavoritesAdapter.clearList();

        SharedPreferences pref = getContext().getSharedPreferences("Movies", 0); // 0 - for private mode
        final SharedPreferences.Editor editor = pref.edit();
        String movieIds = pref.getString("MOVIE_ID", "");

        if (movieIds != "") {
            parts = movieIds.split(";");
            String parsedIds = "";
            for (int i = 0; i < parts.length; i++) {
                parsedIds = parsedIds + parts[i] + "\n";
                loadMovieInfo(parts[i]);
            }
        }
    }

    public void loadMovieInfo(String movieID) {
        String url = "https://api.themoviedb.org/3/movie/" +
                movieID + "?language=en-US&api_key=" + this.getString(R.string.api_key);

        Request request = new Request.Builder()
                .url(url)
                .build();

        OkHttpClient mClient = new OkHttpClient();
        mClient.newCall(request).enqueue(new Callback() {
            @Override
            public void onFailure(Call call, IOException e) {
                e.printStackTrace();
            }

            @Override
            public void onResponse(Call call, Response response) throws IOException {
                if (response.isSuccessful()) {

                    final String myResponse = response.body().string();
                    final Favorites favorite = new Gson().fromJson(myResponse, Favorites.class);

                    mHandler.post(new Runnable() {
                        @Override
                        public void run() {
                            mFavoritesAdapter.addItem(favorite);
                            mFavoritesAdapter.notifyDataSetChanged();
                        }
                    });

                    Log.d("MOVIEINFO", myResponse);
                }
            }
        });
    }
}
