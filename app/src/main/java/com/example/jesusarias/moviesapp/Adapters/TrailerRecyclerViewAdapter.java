package com.example.jesusarias.moviesapp.Adapters;

import android.content.Context;
import android.content.res.ColorStateList;
import android.graphics.Color;
import android.graphics.Typeface;
import android.os.Build;
import android.support.annotation.NonNull;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.bumptech.glide.Glide;
import com.example.jesusarias.moviesapp.DataClasses.Trailer;
import com.example.jesusarias.moviesapp.R;

import java.util.ArrayList;

public class TrailerRecyclerViewAdapter extends RecyclerView.Adapter<TrailerRecyclerViewAdapter.ViewHolder> {

    public interface OnImageClickListener {
        void onImageClick(ImageView movieThumbnail, String videoKey, int position);
    }

    private Context mContext;
    private ArrayList<Trailer> mTrailerList;
    private OnImageClickListener mImageClickListener;
    private int currentClickedItem = 2147483647;

    public TrailerRecyclerViewAdapter(Context context,
                                      ArrayList<Trailer> trailerList,
                                      OnImageClickListener imageClickListener){
        this.mContext = context;
        this.mTrailerList = trailerList;
        this.mImageClickListener = imageClickListener;
    }

    @NonNull @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup viewGroup, int i) {
        View view = LayoutInflater.from(viewGroup.getContext()).inflate(R.layout.trailer_list_item, viewGroup, false);
        ViewHolder viewHolder = new ViewHolder(view);
        return viewHolder;
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder viewHolder, int i) {

        viewHolder.trailerTitle.setText(mTrailerList.get(i).getName());
        viewHolder.bind(viewHolder.trailerImageView, mImageClickListener,
                mTrailerList.get(i).getKey(),
                i);

        if(i == currentClickedItem)
        {
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
                viewHolder.trailerTitle.setTextColor(mContext.getColor(R.color.colorAccent));
                viewHolder.trailerTitle.setTypeface(Typeface.DEFAULT_BOLD);
            }
        } else
            viewHolder.trailerTitle.setTextColor(Color.parseColor("#808080"));
            viewHolder.trailerTitle.setTypeface(Typeface.DEFAULT);


        String url = mContext.getString(R.string.yt_thumbnail_base_url)+
                mTrailerList.get(i).getKey()+
                mContext.getString(R.string.yt_thumbnail_quality);

        Glide.with(mContext)
                .asBitmap()
                .load(url)
                .into(viewHolder.trailerImageView);
    }

    @Override
    public int getItemCount() {
        return mTrailerList.size();
    }

    public String getFirstItemKey(){
        if(mTrailerList.get(0)!=null)
            return mTrailerList.get(0).getKey();
        else
            return "";
    }


    public void setCurrentClickedItem(int currentClickedItem) {
        this.currentClickedItem = currentClickedItem;
    }

    public void updateData(ArrayList<Trailer> list){
        this.mTrailerList = list;
    }

    public class ViewHolder extends RecyclerView.ViewHolder {

        ImageView trailerImageView;
        TextView trailerTitle;

        public ViewHolder(@NonNull View itemView) {
            super(itemView);
            trailerImageView = itemView.findViewById(R.id.iv_trailer_thumbnail);
            trailerTitle = itemView.findViewById(R.id.tv_trailer_title);
        }

        public void bind(final ImageView image, final OnImageClickListener listener, final String key,
                         final int position){
            trailerImageView.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    listener.onImageClick(image, key, position);
                }
            });
        }
    }
}
