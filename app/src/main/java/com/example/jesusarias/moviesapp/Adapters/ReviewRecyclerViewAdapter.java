package com.example.jesusarias.moviesapp.Adapters;

import android.support.annotation.NonNull;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.example.jesusarias.moviesapp.DataClasses.Review;
import com.example.jesusarias.moviesapp.R;

import java.util.ArrayList;

public class ReviewRecyclerViewAdapter extends RecyclerView.Adapter<ReviewRecyclerViewAdapter.ViewHolder> {

    private ArrayList<Review> mReviewList;

    public ReviewRecyclerViewAdapter(ArrayList<Review> reviewList){
        this.mReviewList = reviewList;
    }

    @NonNull @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup viewGroup, int i) {
        View view = LayoutInflater.from(viewGroup.getContext()).inflate(R.layout.review_list_item, viewGroup, false);
        ViewHolder viewHolder = new ViewHolder(view);
        return viewHolder;
    }

    public void onBindViewHolder(@NonNull ViewHolder viewHolder, int i) {
        viewHolder.author.setText(mReviewList.get(i).getAuthor());
        viewHolder.content.setText(mReviewList.get(i).getContent());
    }

    @Override
    public int getItemCount() {
        return mReviewList.size();
    }

    public void updateData(ArrayList<Review> reviewList){
        mReviewList = reviewList;
    }

    public class ViewHolder extends RecyclerView.ViewHolder {

        TextView author;
        TextView content;

        public ViewHolder(@NonNull View itemView) {
            super(itemView);
            author = itemView.findViewById(R.id.tv_review_author);
            content = itemView.findViewById(R.id.tv_review_content);
        }
    }

}
