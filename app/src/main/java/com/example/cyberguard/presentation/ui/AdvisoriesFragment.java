package com.example.cyberguard.presentation.ui;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.widget.SearchView;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.ViewModelProvider;
import androidx.navigation.Navigation;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.cyberguard.R;
import com.example.cyberguard.databinding.FragmentAdvisoriesBinding;
import com.example.cyberguard.databinding.ItemAdvisoryBinding;
import com.example.cyberguard.domain.model.Advisory;
import com.example.cyberguard.presentation.viewmodel.AdvisoryViewModel;

import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

import dagger.hilt.android.AndroidEntryPoint;

@AndroidEntryPoint
public class AdvisoriesFragment extends Fragment {

    private FragmentAdvisoriesBinding binding;
    private AdvisoryViewModel viewModel;
    private AdvisoryAdapter adapter;
    private List<Advisory> allAdvisoriesList = new ArrayList<>();

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        binding = FragmentAdvisoriesBinding.inflate(inflater, container, false);
        return binding.getRoot();
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        viewModel = new ViewModelProvider(this).get(AdvisoryViewModel.class);
        
        adapter = new AdvisoryAdapter(advisory -> {
            Bundle args = new Bundle();
            args.putString("advisoryId", advisory.getId());
            Navigation.findNavController(requireView()).navigate(R.id.navigation_advisory_detail, args);
        });
        binding.recyclerAdvisories.setLayoutManager(new LinearLayoutManager(getContext()));
        binding.recyclerAdvisories.setAdapter(adapter);

        binding.progressBar.setVisibility(View.VISIBLE);

        viewModel.getAllAdvisories().observe(getViewLifecycleOwner(), advisories -> {
            binding.progressBar.setVisibility(View.GONE);
            if (advisories != null && !advisories.isEmpty()) {
                allAdvisoriesList = advisories;
                filterList(binding.searchView.getQuery().toString());
            }
        });

        binding.searchView.setOnQueryTextListener(new SearchView.OnQueryTextListener() {
            @Override
            public boolean onQueryTextSubmit(String query) {
                filterList(query);
                return true;
            }

            @Override
            public boolean onQueryTextChange(String newText) {
                filterList(newText);
                return true;
            }
        });

        viewModel.refresh();
    }

    private void filterList(String query) {
        List<Advisory> filtered = allAdvisoriesList.stream()
                .filter(a -> a.getTitle().toLowerCase().contains(query.toLowerCase()) || 
                             a.getContent().toLowerCase().contains(query.toLowerCase()))
                .collect(Collectors.toList());

        adapter.setAdvisories(filtered);
        
        if (filtered.isEmpty()) {
            binding.recyclerAdvisories.setVisibility(View.GONE);
            binding.textEmptyState.setVisibility(View.VISIBLE);
        } else {
            binding.recyclerAdvisories.setVisibility(View.VISIBLE);
            binding.textEmptyState.setVisibility(View.GONE);
        }
    }

    private static class AdvisoryAdapter extends RecyclerView.Adapter<AdvisoryAdapter.ViewHolder> {
        private List<Advisory> advisories = new ArrayList<>();
        private final OnItemClickListener listener;

        interface OnItemClickListener {
            void onItemClick(Advisory advisory);
        }

        AdvisoryAdapter(OnItemClickListener listener) {
            this.listener = listener;
        }

        public void setAdvisories(List<Advisory> advisories) {
            this.advisories = advisories;
            notifyDataSetChanged();
        }

        @NonNull
        @Override
        public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
            ItemAdvisoryBinding binding = ItemAdvisoryBinding.inflate(LayoutInflater.from(parent.getContext()), parent, false);
            return new ViewHolder(binding);
        }

        @Override
        public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
            Advisory item = advisories.get(position);
            holder.binding.textAdvisoryTitle.setText(item.getTitle());
            holder.binding.textAdvisorySource.setText("Source: " + item.getSource());
            holder.binding.textAdvisoryContent.setText(item.getContent());
            
            holder.binding.btnReadMore.setOnClickListener(v -> listener.onItemClick(item));
            holder.itemView.setOnClickListener(v -> listener.onItemClick(item));
        }

        @Override
        public int getItemCount() { return advisories.size(); }

        static class ViewHolder extends RecyclerView.ViewHolder {
            final ItemAdvisoryBinding binding;
            ViewHolder(ItemAdvisoryBinding binding) {
                super(binding.getRoot());
                this.binding = binding;
            }
        }
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        binding = null;
    }
}
