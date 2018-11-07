package com.example.jesusarias.moviesapp.Adapters;

import android.content.Context;
import android.content.SharedPreferences;
import android.graphics.Color;
import android.support.annotation.NonNull;
import android.support.constraint.ConstraintLayout;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.TextView;

import com.bumptech.glide.Glide;
import com.example.jesusarias.moviesapp.DataClasses.Movie;
import com.example.jesusarias.moviesapp.R;
import com.example.jesusarias.moviesapp.Utils.StringUtils;

import java.util.ArrayList;

public class MovieRecyclerViewAdapter extends RecyclerView.Adapter<MovieRecyclerViewAdapter.ViewHolder> {

    public interface OnNextClickListener {
        void onNextClick(Button button);
    }

    public interface OnPreviousClickListener {
        void onPreviousClick(Button button);
    }

    public interface OnMoreInfoClickListener {
        void onMoreInfoClick(ConstraintLayout movieItem, String movieId);
    }

    public interface OnFavoriteClickListener {
        void onFavoriteClick(ImageView imageView, String movieId);
    }

    public interface LastItemReached {
        void onLastItemReached();
    }

    private OnNextClickListener nextListener;
    private OnPreviousClickListener previousListener;
    private OnMoreInfoClickListener moreInfoClickListener;
    private OnFavoriteClickListener favoriteClickListener;
    private Context mContext;
    private int mPageNumber;
    private ArrayList<Movie> mMoviesList;
    final SharedPreferences pref;

    private LastItemReached lastItemReached;


    public MovieRecyclerViewAdapter(Context context,
                                    ArrayList<Movie> mMoviesList,
                                    OnNextClickListener nextListener,
                                    OnPreviousClickListener previousListener,
                                    OnMoreInfoClickListener onMoreInfoClickListener,
                                    OnFavoriteClickListener favoriteClickListener,
                                    LastItemReached lastItemReached,
                                    int mPageNumber) {
        this.mContext = context;
        this.mMoviesList = mMoviesList;
        this.nextListener = nextListener;
        this.previousListener = previousListener;
        this.moreInfoClickListener = onMoreInfoClickListener;
        this.mPageNumber = mPageNumber;
        this.favoriteClickListener = favoriteClickListener;
        this.lastItemReached = lastItemReached;
        pref = mContext.getSharedPreferences("Movies", 0); // 0 - for private mode
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup viewGroup, int i) {
        View view;
        if (i == R.layout.movie_list_item) {
            view = LayoutInflater.from(viewGroup.getContext()).inflate(R.layout.movie_list_item, viewGroup, false);
        } else {
            view = LayoutInflater.from(viewGroup.getContext()).inflate(R.layout.movies_last_item, viewGroup, false);
        }
        ViewHolder holder = new ViewHolder(view);
        return holder;
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder viewHolder, int i) {
        if (i == mMoviesList.size()) {
            if(i!=0) {
                int x = i;
                lastItemReached.onLastItemReached();
            }
            /*
            if (mPageNumber != 1)
                viewHolder.previousButton.setVisibility(View.VISIBLE);
            else
                viewHolder.previousButton.setVisibility(View.INVISIBLE);
            viewHolder.bindNext(viewHolder.nextButton, nextListener);
            viewHolder.bindPrevious(viewHolder.previousButton, previousListener);
            */
        } else {
            StringUtils stringUtils = new StringUtils();
            String description = stringUtils.formatDescription(mMoviesList.get(i).getOverview());
            int score = (int) mMoviesList.get(i).getVote_average();

            viewHolder.title.setText(mMoviesList.get(i).getTitle());
            viewHolder.releaseDate.setText(mMoviesList.get(i).getRelease_date());
            viewHolder.score.setText(String.valueOf(mMoviesList.get(i).getVote_average()));
            viewHolder.description.setText(description);
            viewHolder.scoreProgressBar.setProgress(score);

            String url = mContext.getString(R.string.base_url) +
                    mContext.getString(R.string.small_size_image) +
                    mMoviesList.get(i).getPoster_path();

            Glide.with(mContext)
                    .asBitmap()
                    .load(url)
                    .into(viewHolder.moviePoster);

            viewHolder.bindMoreInfo(viewHolder.clMovieItem, moreInfoClickListener,
                    String.valueOf(mMoviesList.get(i).getId()));

            String moviesInWacthlist = pref.getString("MOVIE_ID", null);
            String movieId = String.valueOf(mMoviesList.get(i).getId());

            if (moviesInWacthlist != null) {
                if (!moviesInWacthlist.contains(movieId)) {
                    viewHolder.heartView.setColorFilter(Color.parseColor("#D3D3D3"));
                } else {
                    viewHolder.heartView.setColorFilter(Color.parseColor("#D81B60"));
                }
            } else
                viewHolder.heartView.setColorFilter(Color.parseColor("#D3D3D3"));

            viewHolder.bindFavorite(viewHolder.heartView, favoriteClickListener,
                    String.valueOf(mMoviesList.get(i).getId()));
        }
    }

    @Override
    public int getItemCount() {
        return mMoviesList.size() + 1;
    }

    @Override
    public int getItemViewType(int position) {
        return (position == mMoviesList.size()) ? R.layout.movies_last_item : R.layout.movie_list_item;
    }

    public void updateData(ArrayList<Movie> list, int page) {
        mPageNumber = page;
        mMoviesList = list;
    }

    public ArrayList<Movie> getmMoviesList() {
        return mMoviesList;
    }



    public class ViewHolder extends RecyclerView.ViewHolder {

        TextView title;
        TextView releaseDate;
        TextView score;
        TextView description;
        ProgressBar scoreProgressBar;
        ImageView moviePoster;
        ImageView heartView;
        ConstraintLayout clMovieItem;

        Button nextButton;
        Button previousButton;

        public ViewHolder(@NonNull View itemView) {
            super(itemView);
            title = itemView.findViewById(R.id.tv_title);
            releaseDate = itemView.findViewById(R.id.tv_release_date);
            score = itemView.findViewById(R.id.tv_score);
            description = itemView.findViewById(R.id.tv_description);
            scoreProgressBar = itemView.findViewById(R.id.pb_score);
            moviePoster = itemView.findViewById(R.id.iv_movie_poster);
            //nextButton = itemView.findViewById(R.id.btn_next);
            //previousButton = itemView.findViewById(R.id.btn_previous);
            clMovieItem = itemView.findViewById(R.id.cl_movie_item);
            heartView = itemView.findViewById(R.id.iv_heart);
        }

        public void bindMoreInfo(final ConstraintLayout constraintLayout,
                                 final OnMoreInfoClickListener listener,
                                 final String movieId) {
            clMovieItem.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    listener.onMoreInfoClick(constraintLayout, movieId);
                }
            });
        }

        public void bindNext(final Button button, final OnNextClickListener listener) {
            nextButton.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    listener.onNextClick(button);
                }
            });
        }

        public void bindPrevious(final Button button, final OnPreviousClickListener listener) {
            previousButton.setOnClickListener(new View.OnClickListener() {

                @Override
                public void onClick(View v) {
                    listener.onPreviousClick(button);
                }
            });
        }

        public void bindFavorite(final ImageView view, final OnFavoriteClickListener listener,
                                 final String movieId) {
            heartView.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    listener.onFavoriteClick(view, movieId);
                }
            });
        }
    }
}
