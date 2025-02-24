package com.example.appproject_coronatracker.adapter;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.example.appproject_coronatracker.R;
import com.example.appproject_coronatracker.models.Country;

import java.util.ArrayList;

public class CoronaTrackerAdapter extends RecyclerView.Adapter<CoronaTrackerAdapter.WordViewHolder> {

    private ArrayList<Country> mCountryArrayList;
    private OnItemListener mOnItemListener;

    public class WordViewHolder extends RecyclerView.ViewHolder {

        ImageView imageView, mDeleteImage;
        TextView txtCountry;
        OnItemListener onItemListener;

        public WordViewHolder(@NonNull View itemView, final OnItemListener onItemListener) {
            super(itemView);

            imageView = itemView.findViewById(R.id.flag);
            txtCountry = itemView.findViewById(R.id.txtCountryName);
            mDeleteImage = itemView.findViewById(R.id.mDeleteImage);
            this.onItemListener = onItemListener;

            // itemView.setOnClickListener(this);
            itemView.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    if (onItemListener != null) {
                        int position = getAdapterPosition();
                        if (position != RecyclerView.NO_POSITION) {
                            onItemListener.onItemClick(position);
                        }
                    }
                }
            });

            mDeleteImage.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    if (onItemListener != null) {
                        int position = getAdapterPosition();
                        if (position != RecyclerView.NO_POSITION) {
                            onItemListener.onDeleteClick(position);
                        }
                    }
                }
            });


        }


    }

    public CoronaTrackerAdapter(ArrayList<Country> countries, OnItemListener onItemListener) {
        this.mCountryArrayList = countries;
        this.mOnItemListener = onItemListener;
    }

    @NonNull
    @Override
    public WordViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View v = LayoutInflater.from(parent.getContext()).inflate(R.layout.coronatracker_item, parent, false);
        WordViewHolder wvh = new WordViewHolder(v, mOnItemListener);
        return wvh;
    }

    @Override
    public void onBindViewHolder(@NonNull WordViewHolder holder, int position) {


        Glide.with(holder.imageView.getContext()).load(mCountryArrayList.get(position).getCountryInfo().getFlag())
                .into(holder.imageView);

        holder.txtCountry.setText(mCountryArrayList.get(position).getCountry());
    }

    @Override
    public int getItemCount() {
        if (mCountryArrayList != null) {
            return mCountryArrayList.size();
        } else return 0;
    }

    public interface OnItemListener {
        void onItemClick(int position);

        void onDeleteClick(int position);
    }

    public void updateData(ArrayList<Country> newList) {
        mCountryArrayList = newList;
        notifyDataSetChanged();
    }

}
