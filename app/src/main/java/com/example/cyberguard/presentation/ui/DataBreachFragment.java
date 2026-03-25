package com.example.cyberguard.presentation.ui;

import android.content.res.ColorStateList;
import android.os.Bundle;
import android.os.Handler;
import android.os.Looper;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.core.content.ContextCompat;
import androidx.fragment.app.Fragment;

import com.example.cyberguard.R;
import com.example.cyberguard.databinding.FragmentDataBreachBinding;

import dagger.hilt.android.AndroidEntryPoint;

@AndroidEntryPoint
public class DataBreachFragment extends Fragment {

    private FragmentDataBreachBinding binding;
    private final Handler handler = new Handler(Looper.getMainLooper());

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        binding = FragmentDataBreachBinding.inflate(inflater, container, false);
        return binding.getRoot();
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        binding.btnCheckBreach.setOnClickListener(v -> {
            String email = binding.editEmail.getText().toString().trim();
            if (email.isEmpty()) {
                binding.editEmail.setError("Please enter an email");
                return;
            }
            performCheck(email);
        });
    }

    private void performCheck(String email) {
        binding.progressLoading.setVisibility(View.VISIBLE);
        binding.cardResult.setVisibility(View.GONE);
        binding.btnCheckBreach.setEnabled(false);

        // Simulating API call to Have I Been Pwned
        handler.postDelayed(() -> {
            if (binding == null) return;
            
            binding.progressLoading.setVisibility(View.GONE);
            binding.cardResult.setVisibility(View.VISIBLE);
            binding.btnCheckBreach.setEnabled(true);

            if (email.contains("scam") || email.length() % 2 == 0) {
                showLeaked();
            } else {
                showSafe();
            }
        }, 2000);
    }

    private void showLeaked() {
        int red = ContextCompat.getColor(requireContext(), R.color.risk_high);
        int redBg = ContextCompat.getColor(requireContext(), R.color.risk_high_container);
        
        binding.layoutResultBg.setBackgroundColor(redBg);
        binding.textVerdict.setText("OH NO — PWNED!");
        binding.textVerdict.setTextColor(red);
        binding.textDetails.setText("This email was found in 3 major data breaches (LinkedIn 2016, Canva, and Adobe). Your data is at risk! Change your passwords immediately.");
    }

    private void showSafe() {
        int green = ContextCompat.getColor(requireContext(), R.color.risk_low);
        int greenBg = ContextCompat.getColor(requireContext(), R.color.risk_low_container);
        
        binding.layoutResultBg.setBackgroundColor(greenBg);
        binding.textVerdict.setText("GOOD NEWS — SAFE!");
        binding.textVerdict.setTextColor(green);
        binding.textDetails.setText("No pwnage found for this email. Your data hasn't been leaked in any known major breaches. Stay vigilant!");
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        binding = null;
    }
}
