package com.example.appproject_coronatracker.adapter;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Filter;
import android.widget.Filterable;
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
   //  private ArrayList<Country> mCountryArrayListFull;
    private OnItemListener mOnItemListener;

    public class WordViewHolder extends RecyclerView.ViewHolder implements View.OnClickListener  {

        ImageView imageView;
        TextView txtCountry;
        OnItemListener onItemListener;

        public WordViewHolder(@NonNull View itemView, OnItemListener onItemListener) {
            super(itemView);

            imageView = itemView.findViewById(R.id.flag);
            txtCountry = itemView.findViewById(R.id.txtCountryName);
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
        // this.mCountryArrayListFull = new ArrayList<>(countries);
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
    }

    public void updateData(ArrayList<Country> newList) {
        mCountryArrayList = newList;
        notifyDataSetChanged();
    }

    /*@Override
    public Filter getFilter() {
        return exampleFilter;
    }

    private Filter exampleFilter = new Filter() {
        @Override
        protected FilterResults performFiltering(CharSequence constraint) {
            ArrayList<Country> filteredList = new ArrayList<>();

            if (constraint == null || constraint.length() == 0) {
                filteredList.addAll(mCountryArrayListFull);
            } else {
                String filterPattern = constraint.toString().toLowerCase().trim();

                for (Country item : mCountryArrayListFull) {
                        if (item.getCountry().toLowerCase().contains(filterPattern)) {
                        filteredList.add(item);
                    }
                }
            }

            FilterResults results = new FilterResults();
            results.values = filteredList;

            return results;
        }

        @Override
        protected void publishResults(CharSequence constraint, FilterResults results) {
            mCountryArrayList.clear();
            mCountryArrayList.addAll((ArrayList) results.values);
            notifyDataSetChanged();
        }
    };*/

}
