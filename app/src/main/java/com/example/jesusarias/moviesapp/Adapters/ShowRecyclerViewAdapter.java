package com.example.jesusarias.moviesapp.Adapters;

import android.content.Context;
import android.support.annotation.NonNull;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.TextView;

import com.bumptech.glide.Glide;
import com.example.jesusarias.moviesapp.R;
import com.example.jesusarias.moviesapp.DataClasses.Show;

import java.util.ArrayList;

public class ShowRecyclerViewAdapter extends RecyclerView.Adapter<ShowRecyclerViewAdapter.ViewHolder> {

    public interface OnImageViewListener {
        void onImageViewClick(ImageView imageView, int position);
    }

    /*
    public interface OnMoreShowsListener {
        void onMoreShowsClick(ImageView imageView);
    }
    */

    public interface OnMoreShowsListener {
        void onMoreShowsClick(ImageView imageView, MotionEvent event);
    }

    private ArrayList<Show> mShowsList;
    private Context mContext;
    private OnImageViewListener imageViewListener;
    private OnMoreShowsListener moreShowsListener;

    public ShowRecyclerViewAdapter(Context context,
                                   ArrayList<Show> showList,
                                   OnImageViewListener imageViewListener,
                                   OnMoreShowsListener onMoreShowsListener){
        this.mShowsList = showList;
        this.mContext = context;
        this.imageViewListener = imageViewListener;
        this.moreShowsListener = onMoreShowsListener;
    }

    @NonNull @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup viewGroup, int i) {
        View view;
        if(i == R.layout.show_last_item)
            view = LayoutInflater.from(viewGroup.getContext()).inflate(R.layout.show_last_item, viewGroup, false);
        else
            view = LayoutInflater.from(viewGroup.getContext()).inflate(R.layout.show_list_item, viewGroup, false);
        ViewHolder holder = new ViewHolder(view);
        return holder;
    }

    @Override
    public void onBindViewHolder(final @NonNull ViewHolder viewHolder, int i) {
        if(i!=mShowsList.size()){
            int score = (int) mShowsList.get(i).getVote_average();

            viewHolder.showTitle.setText(mShowsList.get(i).getName());
            viewHolder.showScoreProgressB.setProgress(score);
            viewHolder.bindImageView(viewHolder.showPoster, imageViewListener, i);

            String url = mContext.getString(R.string.base_url) +
                    mContext.getString(R.string.small_size_image) +
                    mShowsList.get(i).getPoster_path();

            Glide.with(mContext)
                    .asBitmap()
                    .load(url)
                    .into(viewHolder.showPoster);
        }
        else {
            viewHolder.bindMoreShows(viewHolder.moreShows, moreShowsListener);
        }
    }

    @Override
    public int getItemCount() {
        return mShowsList.size() + 1;
    }

    public void updateData(ArrayList<Show> list) {
        mShowsList = list;
    }

    @Override
    public int getItemViewType(int position) {
        return (position == mShowsList.size()) ? R.layout.show_last_item : R.layout.show_list_item;
    }

    public class ViewHolder extends RecyclerView.ViewHolder{

        ImageView showPoster;
        TextView showTitle;
        ProgressBar showScoreProgressB;

        ImageView moreShows;

        public ViewHolder(@NonNull View itemView) {
            super(itemView);
            showPoster = itemView.findViewById(R.id.iv_show_poster);
            showTitle = itemView.findViewById(R.id.tv_show_title);
            showScoreProgressB = itemView.findViewById(R.id.pb_more_shows);
            moreShows = itemView.findViewById(R.id.iv_more_shows);
        }

        public void bindImageView(final ImageView imageView,
                                  final OnImageViewListener listener,
                                  final int position){
            showPoster.setOnClickListener(new View.OnClickListener(){
                @Override
                public void onClick(View v) {
                    listener.onImageViewClick(imageView, position);
                }
            });
        }

        /*public void bindMoreShows(final ImageView imageView,
                                  final OnMoreShowsListener listener){
            moreShows.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    listener.onMoreShowsClick(imageView);
                }
            });
        }
        */

        public void bindMoreShows(final ImageView imageView,
                                  final OnMoreShowsListener listener){
            moreShows.setOnTouchListener(new View.OnTouchListener() {
                @Override
                public boolean onTouch(View v, MotionEvent event) {
                    listener.onMoreShowsClick(imageView, event);
                    return true;
                }
            });
        }
    }

    public ArrayList<Show> getmShowsList() {
        return mShowsList;
    }

    public void setmShowsList(ArrayList<Show> mShowsList) {
        this.mShowsList = mShowsList;
    }
}
