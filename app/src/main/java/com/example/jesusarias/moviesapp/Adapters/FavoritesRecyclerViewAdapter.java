package com.example.jesusarias.moviesapp.Adapters;

import android.content.Context;
import android.support.annotation.NonNull;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.bumptech.glide.Glide;
import com.example.jesusarias.moviesapp.DataClasses.Favorites;
import com.example.jesusarias.moviesapp.R;

import java.util.ArrayList;

public class FavoritesRecyclerViewAdapter extends RecyclerView.Adapter<FavoritesRecyclerViewAdapter.ViewHolder>  {

    private ArrayList<Favorites> mFavoritesList;
    private Context mContext;

    public FavoritesRecyclerViewAdapter(Context context, ArrayList<Favorites> mFavoritesList){
        this.mContext = context;
        this.mFavoritesList = mFavoritesList;
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup viewGroup, int i) {
        View view = LayoutInflater.from(viewGroup.getContext()).inflate(R.layout.favorite_list_item, viewGroup, false);
        ViewHolder viewHolder = new ViewHolder(view);
        return viewHolder;
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder viewHolder, int i) {
        viewHolder.movieTitle.setText(mFavoritesList.get(i).getOriginal_title());

        String url = mContext.getString(R.string.base_url) +
                mContext.getString(R.string.small_size_image) +
                mFavoritesList.get(i).getPoster_path();

        Glide.with(mContext)
                .asBitmap()
                .load(url)
                .into(viewHolder.posterImage);
    }

    @Override
    public int getItemCount() {
        return mFavoritesList.size();
    }

    public void addItem(Favorites favorites){
        mFavoritesList.add(favorites);
    }

    public class ViewHolder extends RecyclerView.ViewHolder{

        ImageView posterImage;
        TextView movieTitle;

        public ViewHolder(@NonNull View itemView) {
            super(itemView);
            posterImage = itemView.findViewById(R.id.iv_favorites_poster);
            movieTitle = itemView.findViewById(R.id.tv_favorites_title);
        }

    }
}
