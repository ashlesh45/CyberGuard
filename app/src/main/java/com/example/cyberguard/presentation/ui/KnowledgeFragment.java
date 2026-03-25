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

import com.bumptech.glide.Glide;
import com.bumptech.glide.load.resource.drawable.DrawableTransitionOptions;
import com.example.cyberguard.R;
import com.example.cyberguard.databinding.FragmentKnowledgeBinding;
import com.example.cyberguard.databinding.ItemFraudTypeBinding;
import com.example.cyberguard.domain.model.FraudType;
import com.example.cyberguard.presentation.viewmodel.FraudViewModel;

import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

import dagger.hilt.android.AndroidEntryPoint;

@AndroidEntryPoint
public class KnowledgeFragment extends Fragment {

    private FragmentKnowledgeBinding binding;
    private FraudViewModel viewModel;
    private FraudAdapter adapter;
    private List<FraudType> allFraudTypes = new ArrayList<>();

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        binding = FragmentKnowledgeBinding.inflate(inflater, container, false);
        return binding.getRoot();
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        viewModel = new ViewModelProvider(this).get(FraudViewModel.class);
        viewModel.init();

        adapter = new FraudAdapter(fraud -> {
            Bundle args = new Bundle();
            args.putInt("fraudId", fraud.getId());
            Navigation.findNavController(requireView()).navigate(R.id.navigation_fraud_detail, args);
        });
        
        binding.recyclerView.setLayoutManager(new LinearLayoutManager(getContext()));
        binding.recyclerView.setAdapter(adapter);

        viewModel.getAllFraudTypes().observe(getViewLifecycleOwner(), fraudTypes -> {
            if (fraudTypes != null) {
                allFraudTypes = fraudTypes;
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
    }

    private void filterList(String query) {
        List<FraudType> filtered = allFraudTypes.stream()
                .filter(f -> f.getTitle().toLowerCase().contains(query.toLowerCase()) || 
                             f.getDescription().toLowerCase().contains(query.toLowerCase()))
                .collect(Collectors.toList());

        adapter.setFraudTypes(filtered);
        
        if (filtered.isEmpty()) {
            binding.recyclerView.setVisibility(View.GONE);
            binding.textEmptyState.setVisibility(View.VISIBLE);
        } else {
            binding.recyclerView.setVisibility(View.VISIBLE);
            binding.textEmptyState.setVisibility(View.GONE);
        }
    }

    private static class FraudAdapter extends RecyclerView.Adapter<FraudAdapter.ViewHolder> {
        private List<FraudType> fraudTypes = new ArrayList<>();
        private final OnFraudClickListener listener;

        interface OnFraudClickListener {
            void onFraudClick(FraudType fraud);
        }

        FraudAdapter(OnFraudClickListener listener) {
            this.listener = listener;
        }

        public void setFraudTypes(List<FraudType> fraudTypes) {
            this.fraudTypes = fraudTypes;
            notifyDataSetChanged();
        }

        @NonNull
        @Override
        public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
            ItemFraudTypeBinding binding = ItemFraudTypeBinding.inflate(LayoutInflater.from(parent.getContext()), parent, false);
            return new ViewHolder(binding);
        }

        @Override
        public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
            FraudType item = fraudTypes.get(position);
            holder.binding.textTitle.setText(item.getTitle());
            holder.binding.textDescription.setText(item.getDescription());
            
            Glide.with(holder.itemView.getContext())
                    .load(item.getImageUrl())
                    .transition(DrawableTransitionOptions.withCrossFade())
                    .placeholder(R.drawable.bg_gradient)
                    .into(holder.binding.imgFraudIcon);
            
            holder.itemView.setOnClickListener(v -> listener.onFraudClick(item));
        }

        @Override
        public int getItemCount() { return fraudTypes.size(); }

        static class ViewHolder extends RecyclerView.ViewHolder {
            final ItemFraudTypeBinding binding;
            ViewHolder(ItemFraudTypeBinding binding) {
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
