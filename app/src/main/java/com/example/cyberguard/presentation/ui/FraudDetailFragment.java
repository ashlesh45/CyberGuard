package com.example.cyberguard.presentation.ui;

import android.graphics.Typeface;
import android.os.Bundle;
import android.text.SpannableStringBuilder;
import android.text.Spanned;
import android.text.style.ForegroundColorSpan;
import android.text.style.RelativeSizeSpan;
import android.text.style.StyleSpan;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.core.content.ContextCompat;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.ViewModelProvider;

import com.bumptech.glide.Glide;
import com.bumptech.glide.load.resource.drawable.DrawableTransitionOptions;
import com.example.cyberguard.R;
import com.example.cyberguard.databinding.FragmentFraudDetailBinding;
import com.example.cyberguard.domain.model.FraudType;
import com.example.cyberguard.presentation.viewmodel.FraudViewModel;

import dagger.hilt.android.AndroidEntryPoint;

@AndroidEntryPoint
public class FraudDetailFragment extends Fragment {

    private FragmentFraudDetailBinding binding;
    private FraudViewModel viewModel;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        binding = FragmentFraudDetailBinding.inflate(inflater, container, false);
        return binding.getRoot();
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        viewModel = new ViewModelProvider(this).get(FraudViewModel.class);
        
        int fraudId = getArguments() != null ? getArguments().getInt("fraudId") : -1;

        viewModel.getAllFraudTypes().observe(getViewLifecycleOwner(), fraudTypes -> {
            for (FraudType type : fraudTypes) {
                if (type.getId() == fraudId) {
                    displayDetails(type);
                    break;
                }
            }
        });
    }

    private void displayDetails(FraudType fraud) {
        binding.collapsingToolbar.setTitle(fraud.getTitle());
        
        // Load the same professional image as the home page
        Glide.with(this)
                .load(fraud.getImageUrl())
                .transition(DrawableTransitionOptions.withCrossFade())
                .placeholder(R.drawable.bg_gradient)
                .into(binding.imgFraudHeader);
        
        // Highlight all headers in the description
        binding.textDescription.setText(getHighlightedSpannable(fraud.getDescription()));
        
        // Tactics Section
        SpannableStringBuilder tacticsSsb = new SpannableStringBuilder("HOW SCAMMERS OPERATE:\n");
        for (String tactic : fraud.getCommonTactics()) {
            tacticsSsb.append("• ").append(tactic).append("\n\n");
        }
        highlightSpecificHeader(tacticsSsb, "HOW SCAMMERS OPERATE:");
        binding.textTactics.setText(tacticsSsb);

        // Tips Section
        SpannableStringBuilder tipsSsb = new SpannableStringBuilder("SAFETY CHECKLIST / IF SCAMMED:\n");
        for (String tip : fraud.getPreventionTips()) {
            tipsSsb.append("✓ ").append(tip).append("\n\n");
        }
        highlightSpecificHeader(tipsSsb, "SAFETY CHECKLIST / IF SCAMMED:");
        binding.textTips.setText(tipsSsb);
    }

    private SpannableStringBuilder getHighlightedSpannable(String text) {
        SpannableStringBuilder ssb = new SpannableStringBuilder(text);
        String[] headers = {
            "INTRODUCTION:", 
            "ACTION PLAN FOR PRESSURE TACTICS:", 
            "HOW TO STAY AWAY:",
            "IF SCAMMED:"
        };
        int primaryColor = ContextCompat.getColor(requireContext(), R.color.primary);

        for (String header : headers) {
            int start = text.indexOf(header);
            while (start >= 0) {
                int end = start + header.length();
                ssb.setSpan(new StyleSpan(Typeface.BOLD), start, end, Spanned.SPAN_EXCLUSIVE_EXCLUSIVE);
                ssb.setSpan(new ForegroundColorSpan(primaryColor), start, end, Spanned.SPAN_EXCLUSIVE_EXCLUSIVE);
                ssb.setSpan(new RelativeSizeSpan(1.1f), start, end, Spanned.SPAN_EXCLUSIVE_EXCLUSIVE);
                start = text.indexOf(header, end);
            }
        }
        return ssb;
    }

    private void highlightSpecificHeader(SpannableStringBuilder ssb, String header) {
        int start = ssb.toString().indexOf(header);
        if (start >= 0) {
            int end = start + header.length();
            int primaryColor = ContextCompat.getColor(requireContext(), R.color.primary);
            ssb.setSpan(new StyleSpan(Typeface.BOLD), start, end, Spanned.SPAN_EXCLUSIVE_EXCLUSIVE);
            ssb.setSpan(new ForegroundColorSpan(primaryColor), start, end, Spanned.SPAN_EXCLUSIVE_EXCLUSIVE);
            ssb.setSpan(new RelativeSizeSpan(1.1f), start, end, Spanned.SPAN_EXCLUSIVE_EXCLUSIVE);
        }
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        binding = null;
    }
}
