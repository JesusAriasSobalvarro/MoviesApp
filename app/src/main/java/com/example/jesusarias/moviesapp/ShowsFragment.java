package com.example.jesusarias.moviesapp;

import android.graphics.Color;
import android.graphics.PorterDuff;
import android.os.Bundle;
import android.os.Handler;
import android.os.Looper;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.TextView;

import com.example.jesusarias.moviesapp.Adapters.ShowRecyclerViewAdapter;
import com.example.jesusarias.moviesapp.DataClasses.DiscoverShows;
import com.example.jesusarias.moviesapp.DataClasses.Show;
import com.google.gson.Gson;

import java.io.IOException;
import java.util.ArrayList;

import okhttp3.Call;
import okhttp3.Callback;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.Response;

public class ShowsFragment extends Fragment {

    private RecyclerView mRecyclerView;
    private ShowRecyclerViewAdapter mShowAdapter;
    private Handler mHandler;
    private OkHttpClient mClient;
    private int mCurrentClickedItem = 0;
    private int mCurrentPage = 1;

    //UI Components
    private TextView mTitle;
    private TextView mScore;
    private TextView mOverview;
    private TextView mCountry;
    private TextView mFirstAirDate;
    private ProgressBar progressBar;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater,
                             @Nullable ViewGroup container,
                             @Nullable Bundle savedInstanceState) {
        final View view = inflater.inflate(R.layout.shows_fragment, container, false);
        //Bind UI components
        mTitle = view.findViewById(R.id.tv_detail_name);
        mScore = view.findViewById(R.id.tv_detail_score);
        mOverview = view.findViewById(R.id.tv_detail_overview);
        mCountry = view.findViewById(R.id.tv_detail_country);
        mFirstAirDate = view.findViewById(R.id.tv_detail_first_air_date);
        progressBar = view.findViewById(R.id.pb_loading_shows);

        mRecyclerView = view.findViewById(R.id.rv_show_list);
        mRecyclerView.setLayoutManager(new LinearLayoutManager(getContext(), LinearLayoutManager.HORIZONTAL, false));
        mShowAdapter = new ShowRecyclerViewAdapter(getContext(), new ArrayList<Show>(),
                new ShowRecyclerViewAdapter.OnImageViewListener() {
                    @Override
                    public void onImageViewClick(ImageView imageView, int position) {
                        setShowDetails(position);
                    }
                },
                new ShowRecyclerViewAdapter.OnMoreShowsListener() {
                    @Override
                    public void onMoreShowsClick(ImageView imageView, MotionEvent event) {
                        switch (event.getAction()) {
                            case MotionEvent.ACTION_DOWN: {
                                imageView.setColorFilter(Color.parseColor("#808080"), PorterDuff.Mode.SRC_IN);
                                mCurrentPage++;
                                loadShowsFromApi();
                                break;
                            }
                            case MotionEvent.ACTION_UP:
                            case MotionEvent.ACTION_CANCEL:{
                                imageView.setColorFilter(Color.parseColor("#D3D3D3"), PorterDuff.Mode.SRC_IN);
                                break;
                            }
                        }
                    }
                });
        mRecyclerView.setAdapter(mShowAdapter);
        mHandler = new Handler(Looper.getMainLooper());
        loadShowsFromApi();
        return view;
    }

    public String getPageUrl(){
        String pageUrl = getContext().getString(R.string.discover_show_base_url);
        pageUrl = pageUrl + String.valueOf(mCurrentPage) + "&api_key=" +
                getResources().getString(R.string.api_key);
        return pageUrl;
    }

    public void setShowDetails(int position){
        mCurrentClickedItem = position;
        String name = mShowAdapter.getmShowsList().get(mCurrentClickedItem).getName();
        mTitle.setText(name);
        String score = Float.toString(mShowAdapter.getmShowsList().get(mCurrentClickedItem).getVote_average());
        mScore.setText(score);
        String overview = mShowAdapter.getmShowsList().get(mCurrentClickedItem).getOverview();
        mOverview.setText(overview);
        String country="";
        for (int i = 0;
             i < mShowAdapter.getmShowsList().get(mCurrentClickedItem).getOrigin_country().length;
             i++){
            country = mShowAdapter.getmShowsList().get(mCurrentClickedItem).getOrigin_country()[i]+
                    " ";
        }
        mCountry.setText(country);
        String firstAirDate = mShowAdapter.getmShowsList().get(mCurrentClickedItem).getFirst_air_date();
        mFirstAirDate.setText(firstAirDate);
    }

    @Override
    public void onSaveInstanceState(@NonNull Bundle outState) {
        super.onSaveInstanceState(outState);
    }

    public void loadShowsFromApi(){
        mCurrentClickedItem = 0;

        mRecyclerView.setVisibility(View.INVISIBLE);
        progressBar.setVisibility(View.VISIBLE);

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
                    final DiscoverShows discoverShows = new Gson().fromJson(myResponse, DiscoverShows.class);

                    mHandler.post(new Runnable() {
                        @Override
                        public void run() {
                            ArrayList<Show> movies = discoverShows.getResults();
                            mShowAdapter.updateData(movies);
                            mShowAdapter.notifyDataSetChanged();
                            setShowDetails(mCurrentClickedItem);
                            progressBar.setVisibility(View.INVISIBLE);
                            mRecyclerView.setVisibility(View.VISIBLE);
                            mRecyclerView.getLayoutManager().scrollToPosition(0);
                        }
                    });
                }
            }
        });
    }
}
