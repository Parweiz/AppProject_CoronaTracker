package com.example.appproject_coronatracker.adapter;

import android.content.Context;
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

    public class WordViewHolder extends RecyclerView.ViewHolder implements View.OnClickListener  {

        ImageView imageView;
        TextView txtCountry, txtTotalCases, txtTodaysCount, txtTotalDeaths, txtTotalCritical, txtTotalRecovered;
        OnItemListener onItemListener;

        public WordViewHolder(@NonNull View itemView, OnItemListener onItemListener) {
            super(itemView);

            imageView = itemView.findViewById(R.id.flag);
            txtCountry = itemView.findViewById(R.id.txtCountryName);
           /* txtTotalCases = itemView.findViewById(R.id.txtTotalCases);
            txtTodaysCount = itemView.findViewById(R.id.txtTodaysCount);
            txtTotalDeaths = itemView.findViewById(R.id.txtTotalDeath);
            txtTotalCritical = itemView.findViewById(R.id.txtTotalCritical);
            txtTotalRecovered = itemView.findViewById(R.id.txtTotalRecovered);*/
            this.onItemListener = onItemListener;

            itemView.setOnClickListener(this);
        }

        @Override
        public void onClick(View v) {
            onItemListener.onItemClick(getAdapterPosition());
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
       /* holder.txtTotalCases.setText("Total cases: " + mCountryArrayList.get(position).getCases());
        holder.txtTodaysCount.setText("Today's Count: " + "\n" + mCountryArrayList.get(position).getTodayCases());
        holder.txtTotalDeaths.setText("Total Death: \n" + mCountryArrayList.get(position).getDeaths());
        holder.txtTotalCritical.setText("Total Critical: \n" + mCountryArrayList.get(position).getCritical());
        holder.txtTotalRecovered.setText("Total Recovered: \n" + mCountryArrayList.get(position).getRecovered());*/
    }

    @Override
    public int getItemCount() {
        if (mCountryArrayList != null) {
            return mCountryArrayList.size();
        } else return 0;
    }

    public interface OnItemListener {
        void onItemClick(int position);
    }

    public void updateData(ArrayList<Country> newList) {
        mCountryArrayList = newList;
        notifyDataSetChanged();
    }

}
