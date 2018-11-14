package com.example.campusquest;

import android.content.Context;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.bumptech.glide.Glide;
import java.util.List;

public class QuestCardAdapter extends RecyclerView.Adapter<QuestCardAdapter.MyViewHolder> {
    private Context mContext;
    private List<QuestCard> questCardList;

    public class MyViewHolder extends RecyclerView.ViewHolder {
        public TextView title, count;
        public ImageView thumbnail;

        public MyViewHolder(View view) {
            super(view);
//            String TAG = "Debug";
//            Log.e(TAG, "view: "+view);
//            Log.e(TAG, "view: "+view.findViewById(R.id.title));
//            title = (TextView) view.findViewById(R.id.title);
//            count = (TextView) view.findViewById(R.id.count);
//            thumbnail = (ImageView) view.findViewById(R.id.thumbnail);
        }
    }

    public QuestCardAdapter(Context mContext, List<QuestCard> questCardList) {
        this.mContext = mContext;
        this.questCardList = questCardList;
    }

    @Override
    public MyViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View itemView = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.content_main, parent, false);
        String TAG = "Debug";
        Log.e(TAG, "view: "+itemView);

        return new MyViewHolder(itemView);
    }

    @Override
    public void onBindViewHolder(final MyViewHolder holder, int position) {
        QuestCard mQuestCard = questCardList.get(position);
        holder.title.setText(mQuestCard.getName());
        holder.count.setText(mQuestCard.getNumOfSongs() + " songs");

        // loading album cover using Glide library
        Glide.with(mContext).load(mQuestCard.getThumbnail()).into(holder.thumbnail);

    }

    @Override
    public int getItemCount() {
        return questCardList.size();
    }
}
