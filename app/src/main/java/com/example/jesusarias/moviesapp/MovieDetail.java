package com.example.jesusarias.moviesapp;

import android.content.res.Configuration;
import android.os.Handler;
import android.os.Looper;
import android.support.annotation.NonNull;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.View;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ProgressBar;
import android.widget.TextView;

import com.example.jesusarias.moviesapp.Adapters.ReviewRecyclerViewAdapter;
import com.example.jesusarias.moviesapp.Adapters.TrailerRecyclerViewAdapter;
import com.example.jesusarias.moviesapp.DataClasses.GetReviews;
import com.example.jesusarias.moviesapp.DataClasses.GetVideos;
import com.example.jesusarias.moviesapp.DataClasses.Review;
import com.example.jesusarias.moviesapp.DataClasses.Trailer;

import com.google.gson.Gson;
import com.pierfrancescosoffritti.androidyoutubeplayer.player.YouTubePlayer;
import com.pierfrancescosoffritti.androidyoutubeplayer.player.listeners.AbstractYouTubePlayerListener;
import com.pierfrancescosoffritti.androidyoutubeplayer.player.listeners.YouTubePlayerInitListener;

import java.io.IOException;
import java.util.ArrayList;

import okhttp3.Call;
import okhttp3.Callback;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.Response;

public class MovieDetail extends AppCompatActivity {

    //UI Components
    private RecyclerView mReviewRecyclerView;
    private RecyclerView mTrailerRecyclerView;
    private ProgressBar mLoadingDetails;
    private com.pierfrancescosoffritti.androidyoutubeplayer.player.YouTubePlayerView youtubePlayerView;
    private TextView mNoTrailerAvailable;
    private TextView mNoReviewAvailable;

    private String movieID;
    private Handler mHandler;
    private TrailerRecyclerViewAdapter mTrailerAdapter;
    private ReviewRecyclerViewAdapter mReviewAdapter;
    private OkHttpClient mClient;
    private static com.pierfrancescosoffritti.androidyoutubeplayer.player.YouTubePlayer _youtubePlayer;
    private boolean hasTrailers = false;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_movie_detail);

        //getSupportActionBar().hide();
        initUI();
        initPlayer();
        initReviewRecyclerView();
        initTrailerRecyclerView();
        mClient = new OkHttpClient();
        mHandler = new Handler(Looper.getMainLooper());

        //Get movie ID
        Bundle extras = getIntent().getExtras();
        if (extras != null) {
            movieID = extras.getString("MOVIE_ID");
            loadReviewsFromApi();
            loadTrailersFromApi();
        }

    }

    @Override
    public void onConfigurationChanged(Configuration newConfig) {
        super.onConfigurationChanged(newConfig);

        // Checks the orientation of the screen
        if (newConfig.orientation == Configuration.ORIENTATION_LANDSCAPE) {
            youtubePlayerView.enterFullScreen();
            View decorView = getWindow().getDecorView();
            decorView.setSystemUiVisibility(
                    View.SYSTEM_UI_FLAG_IMMERSIVE
                            // Set the content to appear under the system bars so that the
                            // content doesn't resize when the system bars hide and show.
                            | View.SYSTEM_UI_FLAG_LAYOUT_STABLE
                            | View.SYSTEM_UI_FLAG_LAYOUT_HIDE_NAVIGATION
                            | View.SYSTEM_UI_FLAG_LAYOUT_FULLSCREEN
                            // Hide the nav bar and status bar
                            | View.SYSTEM_UI_FLAG_HIDE_NAVIGATION
                            | View.SYSTEM_UI_FLAG_FULLSCREEN
            );
        } else if (newConfig.orientation == Configuration.ORIENTATION_PORTRAIT) {
            youtubePlayerView.exitFullScreen();
            View decorView = getWindow().getDecorView();
            decorView.setSystemUiVisibility(View.SYSTEM_UI_FLAG_LAYOUT_STABLE);
        }
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        youtubePlayerView.release();
    }

    public void initUI() {
        youtubePlayerView = findViewById(R.id.youtube_player_view);
        mTrailerRecyclerView = findViewById(R.id.rv_trailer_list);
        mReviewRecyclerView = findViewById(R.id.rv_reviews_list);
        mLoadingDetails = findViewById(R.id.pb_loading_details);
        mNoTrailerAvailable = findViewById(R.id.tv_no_trailers);
        mNoReviewAvailable = findViewById(R.id.tv_no_reviews);
    }

    public void initPlayer() {
        youtubePlayerView.initialize(new YouTubePlayerInitListener() {
            @Override
            public void onInitSuccess(@NonNull final com.pierfrancescosoffritti.androidyoutubeplayer.player.YouTubePlayer youTubePlayer) {
                _youtubePlayer = youTubePlayer;
                youTubePlayer.addListener(new AbstractYouTubePlayerListener() {
                    @Override
                    public void onReady() {
                        super.onReady();
                        //Llamada al metodo play para detener el progress bar del player
                        _youtubePlayer.play();
                        hideProgressBar();
                        showRecyclerTrailers();
                        if(hasTrailers == true) {
                            showPlayer();
                        }
                    }
                });
            }
        }, true);
    }

    public void initReviewRecyclerView(){
        mReviewRecyclerView.setLayoutManager(new LinearLayoutManager(this,
                LinearLayoutManager.VERTICAL,
                false));
        mReviewAdapter = new ReviewRecyclerViewAdapter(new ArrayList<Review>());
        mReviewRecyclerView.setAdapter(mReviewAdapter);
    }

    public void initTrailerRecyclerView() {
        mTrailerRecyclerView.setLayoutManager(new LinearLayoutManager(this,
                LinearLayoutManager.HORIZONTAL,
                false));

        mTrailerAdapter = new TrailerRecyclerViewAdapter(this, new ArrayList<Trailer>(),
                new TrailerRecyclerViewAdapter.OnImageClickListener() {
                    @Override
                    public void onImageClick(ImageView movieThumbnail, String videoKey, int position) {
                        _youtubePlayer.loadVideo(videoKey, 0);
                        mTrailerAdapter.setCurrentClickedItem(position);
                        mTrailerAdapter.notifyDataSetChanged();
                    }
                });

        mTrailerRecyclerView.setAdapter(mTrailerAdapter);
    }

    public void loadTrailersFromApi() {

        String url = "https://api.themoviedb.org/3/movie/" +
                movieID + "/videos?language=en-US&api_key=" + this.getString(R.string.api_key);

        Request request = new Request.Builder()
                .url(url)
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
                    final GetVideos getVideos = new Gson().fromJson(myResponse, GetVideos.class);

                    mHandler.post(new Runnable() {
                        @Override
                        public void run() {
                            ArrayList<Trailer> trailers = getVideos.getResults();
                            if (trailers.size() > 0) {
                                mTrailerAdapter.updateData(trailers);
                                mTrailerAdapter.notifyDataSetChanged();
                                hasTrailers = true;
                            } else {
                                hideProgressBar();
                                showNoTrailersLabel();
                            }
                        }
                    });
                }
            }
        });
    }

    public void loadReviewsFromApi(){
        String url = "https://api.themoviedb.org/3/movie/" +
                movieID + "/reviews?language=en-US&api_key=" + this.getString(R.string.api_key);

        Request request = new Request.Builder()
                .url(url)
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
                    final GetReviews getReviews = new Gson().fromJson(myResponse, GetReviews.class);

                    mHandler.post(new Runnable() {
                        @Override
                        public void run() {
                            ArrayList<Review> reviews  = getReviews.getResults();
                            if (reviews.size() > 0) {
                                mReviewAdapter.updateData(reviews);
                                mReviewAdapter.notifyDataSetChanged();
                            } else {
                                mNoReviewAvailable.setVisibility(View.VISIBLE);
                            }
                        }
                    });
                }
            }
        });
    }

    public void showRecyclerTrailers() {
        mTrailerRecyclerView.setVisibility(View.VISIBLE);
    }

    public void showPlayer() {
        youtubePlayerView.setVisibility(View.VISIBLE);
    }

    public void hideProgressBar() {
        mLoadingDetails.setVisibility(View.INVISIBLE);
    }

    public void showNoTrailersLabel(){
        mNoTrailerAvailable.setVisibility(View.VISIBLE);
    }
}
