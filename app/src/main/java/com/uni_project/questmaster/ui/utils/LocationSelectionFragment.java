package com.uni_project.questmaster.ui.utils;

import android.content.Context;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.navigation.fragment.NavHostFragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.uni_project.questmaster.R;

import java.util.Arrays;
import java.util.List;

public class LocationSelectionFragment extends Fragment {

    private RecyclerView recyclerViewCountries;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        return inflater.inflate(R.layout.fragment_location_selection, container, false);
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        recyclerViewCountries = view.findViewById(R.id.recycler_view_countries);
        recyclerViewCountries.setLayoutManager(new LinearLayoutManager(getContext()));

        List<String> countries = Arrays.asList("United Kingdom", "United States", "Canada", "Australia", "Germany", "France", "Spain", "Italy", "Japan", "China");

        CountryAdapter adapter = new CountryAdapter(countries, country -> {
            SharedPreferences sharedPreferences = requireActivity().getSharedPreferences("Settings", Context.MODE_PRIVATE);
            SharedPreferences.Editor editor = sharedPreferences.edit();
            editor.putString("My_Location", country);
            editor.apply();
            NavHostFragment.findNavController(this).navigateUp();
        });

        recyclerViewCountries.setAdapter(adapter);
    }

    static class CountryAdapter extends RecyclerView.Adapter<CountryAdapter.CountryViewHolder> {

        private final List<String> countries;
        private final OnCountrySelectedListener listener;

        public CountryAdapter(List<String> countries, OnCountrySelectedListener listener) {
            this.countries = countries;
            this.listener = listener;
        }

        @NonNull
        @Override
        public CountryViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
            View view = LayoutInflater.from(parent.getContext()).inflate(android.R.layout.simple_list_item_1, parent, false);
            return new CountryViewHolder(view);
        }

        @Override
        public void onBindViewHolder(@NonNull CountryViewHolder holder, int position) {
            String country = countries.get(position);
            holder.bind(country, listener);
        }

        @Override
        public int getItemCount() {
            return countries.size();
        }

        static class CountryViewHolder extends RecyclerView.ViewHolder {
            private final TextView textView;

            public CountryViewHolder(@NonNull View itemView) {
                super(itemView);
                textView = itemView.findViewById(android.R.id.text1);
            }

            public void bind(final String country, final OnCountrySelectedListener listener) {
                textView.setText(country);
                itemView.setOnClickListener(v -> listener.onCountrySelected(country));
            }
        }

        interface OnCountrySelectedListener {
            void onCountrySelected(String country);
        }
    }
}
