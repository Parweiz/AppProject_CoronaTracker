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

    public class WordViewHolder extends RecyclerView.ViewHolder  {

        ImageView imageView;
        TextView txtCountry, txtTotalCases, txtTodaysCount, txtTotalDeaths, txtTotalCritical, txtTotalRecovered;

        public WordViewHolder(@NonNull View itemView) {
            super(itemView);

            imageView = itemView.findViewById(R.id.flag);
            txtCountry = itemView.findViewById(R.id.txtCountry);
            txtTotalCases = itemView.findViewById(R.id.txtTotalCases);
            txtTodaysCount = itemView.findViewById(R.id.txtTodaysCount);
            txtTotalDeaths = itemView.findViewById(R.id.txtTotalDeath);
            txtTotalCritical = itemView.findViewById(R.id.txtTotalCritical);
            txtTotalRecovered = itemView.findViewById(R.id.txtTotalRecovered);
        }
    }

    public CoronaTrackerAdapter(ArrayList<Country> countries) {
        this.mCountryArrayList = countries;
    }

    @NonNull
    @Override
    public WordViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View v = LayoutInflater.from(parent.getContext()).inflate(R.layout.coronatracker_item, parent, false);
        WordViewHolder wvh = new WordViewHolder(v);
        return wvh;
    }

    @Override
    public void onBindViewHolder(@NonNull WordViewHolder holder, int position) {


        Glide.with(holder.imageView.getContext()).load(mCountryArrayList.get(position).getCountryInfo().getFlag())
                .into(holder.imageView);

        holder.txtCountry.setText(mCountryArrayList.get(position).getCountry());
        holder.txtTotalCases.setText(mCountryArrayList.get(position).getCases());
        holder.txtTodaysCount.setText(mCountryArrayList.get(position).getTodayCases());
        holder.txtTotalDeaths.setText(mCountryArrayList.get(position).getDeaths());
        holder.txtTotalCritical.setText(mCountryArrayList.get(position).getCritical());
        holder.txtTotalRecovered.setText(mCountryArrayList.get(position).getRecovered());
    }

    @Override
    public int getItemCount() {
        if (mCountryArrayList != null) {
            return mCountryArrayList.size();
        } else return 0;
    }

}
