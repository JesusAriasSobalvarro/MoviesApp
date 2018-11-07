package com.example.jesusarias.moviesapp;

import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Color;
import android.os.Bundle;
import android.os.Handler;
import android.os.Looper;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.constraint.ConstraintLayout;
import android.support.v4.app.Fragment;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.Toast;

import com.example.jesusarias.moviesapp.Adapters.MovieRecyclerViewAdapter;
import com.example.jesusarias.moviesapp.DataClasses.DiscoverMovies;
import com.example.jesusarias.moviesapp.DataClasses.Movie;
import com.google.gson.Gson;

import java.io.IOException;
import java.util.ArrayList;

import okhttp3.Call;
import okhttp3.Callback;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.Response;

public class MoviesFragment extends Fragment {

    private static final String CURRENT_PAGE_KEY = "current_page";
    private ProgressBar progressBar;
    private Handler mHandler;
    private MovieRecyclerViewAdapter mMovieRecyclerViewAdapter;
    private int mCurrentPage;
    private RecyclerView mRecyclerView;
    private OkHttpClient mClient;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater,
                             @Nullable ViewGroup container,
                             @Nullable Bundle savedInstanceState) {

        if (savedInstanceState != null)
            mCurrentPage = savedInstanceState.getInt(CURRENT_PAGE_KEY);
        else
            mCurrentPage = 1;

        final View view = inflater.inflate(R.layout.movies_fragment, container, false);

        final SharedPreferences pref = getContext().getSharedPreferences("Movies", 0); // 0 - for private mode
        final SharedPreferences.Editor editor = pref.edit();

        //Bind UI components
        mRecyclerView = view.findViewById(R.id.rv_video_list);
        progressBar = view.findViewById(R.id.pb_loading_movies);

        mHandler = new Handler(Looper.getMainLooper());

        mRecyclerView.setLayoutManager(new LinearLayoutManager(getContext()));
        mMovieRecyclerViewAdapter = new MovieRecyclerViewAdapter(getContext(), new ArrayList<Movie>(),
                new MovieRecyclerViewAdapter.OnNextClickListener() {
                    @Override
                    public void onNextClick(Button button) {
                        mCurrentPage++;
                        loadMoviesFromApi();
                        mRecyclerView.getLayoutManager().scrollToPosition(0);
                    }
                },
                new MovieRecyclerViewAdapter.OnPreviousClickListener() {
                    @Override
                    public void onPreviousClick(Button button) {
                        if (mCurrentPage > 1) {
                            mCurrentPage--;
                            loadMoviesFromApi();
                            mRecyclerView.getLayoutManager().scrollToPosition(0);
                        }
                    }
                },
                new MovieRecyclerViewAdapter.OnMoreInfoClickListener() {
                    @Override
                    public void onMoreInfoClick(ConstraintLayout movieItem, String movieId) {

                        Intent intent = new Intent(getActivity(), MovieDetail.class);
                        intent.putExtra("MOVIE_ID", movieId);
                        startActivity(intent);
                    }
                },
                new MovieRecyclerViewAdapter.OnFavoriteClickListener() {
                    @Override
                    public void onFavoriteClick(ImageView imageView, String movieId) {
                        String moviesInWacthlist = pref.getString("MOVIE_ID", null);
                        if (moviesInWacthlist == null) {
                            editor.putString("MOVIE_ID", movieId.concat(";"));
                            imageView.setColorFilter(Color.parseColor("#D81B60"));
                            Toast.makeText(getContext(), "Added to favorites", Toast.LENGTH_SHORT).show();
                        } else {
                            if (!moviesInWacthlist.contains(movieId)) {
                                moviesInWacthlist = moviesInWacthlist + movieId + ";";
                                editor.putString("MOVIE_ID", moviesInWacthlist);
                                imageView.setColorFilter(Color.parseColor("#D81B60"));
                                Toast.makeText(getContext(), "Added to favorites", Toast.LENGTH_SHORT).show();
                            }
                        }
                        editor.apply();
                    }
                }, new MovieRecyclerViewAdapter.LastItemReached() {
                    @Override
                    public void onLastItemReached() {
                        //Toast.makeText(getContext(), "Last Item Reached", Toast.LENGTH_SHORT).show();
                        mCurrentPage++;
                        loadMoviesFromApi();
                    }
            },
                mCurrentPage);
        mRecyclerView.setAdapter(mMovieRecyclerViewAdapter);
        loadMoviesFromApi();
        return view;
    }

    @Override
    public void onActivityCreated(@Nullable Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);
    }

    @Override
    public void onSaveInstanceState(@NonNull Bundle outState) {
        outState.putInt(CURRENT_PAGE_KEY, mCurrentPage);
        super.onSaveInstanceState(outState);

    }

    public String getPageUrl() {
        String pageUrl = getContext().getString(R.string.discover_movie_base_url);
        pageUrl = pageUrl + String.valueOf(mCurrentPage) + "&api_key=" +
                getResources().getString(R.string.api_key);
        return pageUrl;
    }

    public void loadMoviesFromApi() {

        if(mCurrentPage == 1) {
            mRecyclerView.setVisibility(View.INVISIBLE);
            progressBar.setVisibility(View.VISIBLE);
        }

        String pageUrl = getPageUrl();

        mClient = new OkHttpClient();
        Request request = new Request.Builder()
                .url(pageUrl)
                .build();

        mClient.newCall(request).enqueue(new Callback() {
            @Override
            public void onFailure(Call call, IOException e) {
                e.printStackTrace();
            }

            @Override
            public void onResponse(Call call, Response response) throws IOException {
                if (response.isSuccessful()) {

                    final String myResponse = response.body().string();
                    final DiscoverMovies discoverMovies = new Gson().fromJson(myResponse, DiscoverMovies.class);


                    mHandler.post(new Runnable() {
                        @Override
                        public void run() {
                            ArrayList<Movie> movies = discoverMovies.getResults();

                            if(mCurrentPage == 1){
                                mMovieRecyclerViewAdapter.updateData(movies, mCurrentPage);
                            } else {
                                mMovieRecyclerViewAdapter.getmMoviesList().addAll(movies);
                            }
                            mMovieRecyclerViewAdapter.notifyDataSetChanged();
                            progressBar.setVisibility(View.INVISIBLE);
                            mRecyclerView.setVisibility(View.VISIBLE);
                        }
                    });
                }
            }
        });
    }
}
