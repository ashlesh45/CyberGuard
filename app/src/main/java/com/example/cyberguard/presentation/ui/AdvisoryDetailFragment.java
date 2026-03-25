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

import com.example.cyberguard.R;
import com.example.cyberguard.databinding.FragmentAdvisoryDetailBinding;
import com.example.cyberguard.domain.model.Advisory;
import com.example.cyberguard.presentation.viewmodel.AdvisoryViewModel;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Locale;

import dagger.hilt.android.AndroidEntryPoint;

@AndroidEntryPoint
public class AdvisoryDetailFragment extends Fragment {

    private FragmentAdvisoryDetailBinding binding;
    private AdvisoryViewModel viewModel;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        binding = FragmentAdvisoryDetailBinding.inflate(inflater, container, false);
        return binding.getRoot();
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        viewModel = new ViewModelProvider(this).get(AdvisoryViewModel.class);
        
        String advisoryId = getArguments() != null ? getArguments().getString("advisoryId") : null;

        if (advisoryId != null) {
            viewModel.getAllAdvisories().observe(getViewLifecycleOwner(), advisories -> {
                for (Advisory advisory : advisories) {
                    if (advisory.getId().equals(advisoryId)) {
                        displayDetails(advisory);
                        break;
                    }
                }
            });
        }
    }

    private void displayDetails(Advisory advisory) {
        binding.collapsingToolbar.setTitle(advisory.getTitle());
        
        // Dynamic Header Image based on Advisory Type
        binding.imgAdvisoryHeader.setImageResource(getIconForAdvisory(advisory.getTitle()));
        
        String content = advisory.getContent();
        binding.textContent.setText(getHighlightedSpannable(content));
        
        binding.textSource.setText(getString(R.string.source_format, advisory.getSource()));
        
        SimpleDateFormat sdf = new SimpleDateFormat("MMM dd, yyyy • HH:mm", Locale.getDefault());
        binding.textDate.setText(getString(R.string.published_format, sdf.format(new Date(advisory.getTimestamp()))));
    }

    private int getIconForAdvisory(String title) {
        String t = title.toLowerCase();
        if (t.contains("phishing") || t.contains("email")) return android.R.drawable.ic_dialog_email;
        if (t.contains("phone") || t.contains("call") || t.contains("vishing")) return android.R.drawable.ic_menu_call;
        if (t.contains("sms") || t.contains("message") || t.contains("smishing")) return android.R.drawable.stat_notify_chat;
        if (t.contains("bank") || t.contains("card") || t.contains("payment") || t.contains("upi")) return android.R.drawable.ic_lock_idle_lock;
        if (t.contains("job") || t.contains("investment") || t.contains("work")) return android.R.drawable.ic_menu_info_details;
        if (t.contains("malware") || t.contains("virus") || t.contains("ransomware")) return android.R.drawable.ic_lock_lock;
        if (t.contains("social") || t.contains("facebook") || t.contains("whatsapp")) return android.R.drawable.ic_menu_share;
        return android.R.drawable.ic_dialog_alert;
    }

    private SpannableStringBuilder getHighlightedSpannable(String text) {
        SpannableStringBuilder ssb = new SpannableStringBuilder(text);
        
        String[] headers = {
            "INTRODUCTION:", 
            "HOW TO STAY AWAY:", 
            "ACTION PLAN FOR PRESSURE TACTICS:", 
            "IF SCAMMED:"
        };
        int primaryColor = ContextCompat.getColor(requireContext(), R.color.primary);

        for (String header : headers) {
            int start = text.indexOf(header);
            while (start >= 0) {
                int end = start + header.length();
                
                // Bold
                ssb.setSpan(new StyleSpan(Typeface.BOLD), start, end, Spanned.SPAN_EXCLUSIVE_EXCLUSIVE);
                // Color
                ssb.setSpan(new ForegroundColorSpan(primaryColor), start, end, Spanned.SPAN_EXCLUSIVE_EXCLUSIVE);
                // Slightly larger
                ssb.setSpan(new RelativeSizeSpan(1.1f), start, end, Spanned.SPAN_EXCLUSIVE_EXCLUSIVE);
                
                start = text.indexOf(header, end);
            }
        }
        return ssb;
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        binding = null;
    }
}
