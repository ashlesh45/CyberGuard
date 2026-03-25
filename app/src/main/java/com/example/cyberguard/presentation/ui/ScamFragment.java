package com.example.cyberguard.presentation.ui;

import android.content.Intent;
import android.content.res.ColorStateList;
import android.net.Uri;
import android.os.Bundle;
import android.os.Handler;
import android.os.Looper;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Toast;

import androidx.activity.result.ActivityResultLauncher;
import androidx.activity.result.contract.ActivityResultContracts;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.core.content.ContextCompat;
import androidx.fragment.app.Fragment;

import com.example.cyberguard.R;
import com.example.cyberguard.databinding.FragmentScamBinding;
import com.example.cyberguard.domain.scam.ScamDetectionEngine;
import com.google.mlkit.vision.common.InputImage;
import com.google.mlkit.vision.text.TextRecognition;
import com.google.mlkit.vision.text.TextRecognizer;
import com.google.mlkit.vision.text.latin.TextRecognizerOptions;

import java.io.IOException;

import dagger.hilt.android.AndroidEntryPoint;

@AndroidEntryPoint
public class ScamFragment extends Fragment {

    private FragmentScamBinding binding;
    private final ScamDetectionEngine engine = new ScamDetectionEngine();
    private final Handler handler = new Handler(Looper.getMainLooper());
    private TextRecognizer textRecognizer;

    private final ActivityResultLauncher<String> imagePickerLauncher = registerForActivityResult(
            new ActivityResultContracts.GetContent(),
            uri -> {
                if (uri != null) {
                    processImage(uri);
                }
            }
    );

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        binding = FragmentScamBinding.inflate(inflater, container, false);
        textRecognizer = TextRecognition.getClient(TextRecognizerOptions.DEFAULT_OPTIONS);
        return binding.getRoot();
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        binding.btnAnalyze.setOnClickListener(v -> {
            String text = binding.editScamText.getText().toString().trim();
            if (text.isEmpty()) {
                binding.editScamText.setError("Please enter some text or URL");
                return;
            }
            performAnalysis(text);
        });

        binding.btnUploadImage.setOnClickListener(v -> {
            imagePickerLauncher.launch("image/*");
        });
    }

    private void processImage(Uri uri) {
        binding.progressLoading.setVisibility(View.VISIBLE);
        try {
            InputImage image = InputImage.fromFilePath(requireContext(), uri);
            textRecognizer.process(image)
                    .addOnSuccessListener(text -> {
                        binding.progressLoading.setVisibility(View.GONE);
                        String recognizedText = text.getText();
                        if (recognizedText.isEmpty()) {
                            Toast.makeText(getContext(), "No text found in image", Toast.LENGTH_SHORT).show();
                        } else {
                            binding.editScamText.setText(recognizedText);
                            Toast.makeText(getContext(), "Text extracted! Click Analyze.", Toast.LENGTH_SHORT).show();
                        }
                    })
                    .addOnFailureListener(e -> {
                        binding.progressLoading.setVisibility(View.GONE);
                        Toast.makeText(getContext(), "Failed to extract text", Toast.LENGTH_SHORT).show();
                    });
        } catch (IOException e) {
            e.printStackTrace();
            binding.progressLoading.setVisibility(View.GONE);
        }
    }

    private void performAnalysis(String text) {
        binding.btnAnalyze.setEnabled(false);
        binding.btnAnalyze.setText("Analyzing...");
        binding.progressLoading.setVisibility(View.VISIBLE);
        binding.cardScamResult.setVisibility(View.GONE);

        handler.postDelayed(() -> {
            if (binding == null) return;
            
            ScamDetectionEngine.DetectionResult result = engine.analyzeText(text);
            updateUI(result);
            
            binding.btnAnalyze.setEnabled(true);
            binding.btnAnalyze.setText("Analyze");
            binding.progressLoading.setVisibility(View.GONE);
        }, 1200);
    }

    private void updateUI(ScamDetectionEngine.DetectionResult result) {
        binding.cardScamResult.setVisibility(View.VISIBLE);
        binding.textVerdict.setText(result.verdict);
        binding.textScore.setText("Risk Score: " + result.score + "/100");
        
        String reasonsText = result.reasons.isEmpty() ? 
                "No suspicious patterns found. Always verify the sender." : 
                String.join("\n", result.reasons.stream().map(r -> "• " + r).toArray(String[]::new));
        binding.textReasons.setText(reasonsText);

        int color;
        int containerColor;
        int iconRes;
        String advice;

        if (result.score >= 60) {
            color = ContextCompat.getColor(requireContext(), R.color.risk_high);
            containerColor = ContextCompat.getColor(requireContext(), R.color.risk_high_container);
            iconRes = android.R.drawable.ic_dialog_alert;
            advice = "CRITICAL: High probability of scam/phishing. Avoid any interaction.";
        } else if (result.score >= 30) {
            color = ContextCompat.getColor(requireContext(), R.color.risk_medium);
            containerColor = ContextCompat.getColor(requireContext(), R.color.risk_medium_container);
            iconRes = android.R.drawable.ic_dialog_info;
            advice = "CAUTION: Suspicious elements detected. Verify independently before acting.";
        } else {
            color = ContextCompat.getColor(requireContext(), R.color.risk_low);
            containerColor = ContextCompat.getColor(requireContext(), R.color.risk_low_container);
            iconRes = android.R.drawable.checkbox_on_background;
            advice = "LOW RISK: Appears safe, but stay vigilant.";
        }

        binding.textVerdict.setTextColor(color);
        binding.progressRisk.setIndicatorColor(color);
        binding.progressRisk.setProgress(result.score, true);
        binding.imgRiskIcon.setImageResource(iconRes);
        binding.imgRiskIcon.setImageTintList(ColorStateList.valueOf(color));
        binding.layoutResultContainer.setBackgroundColor(containerColor);
        binding.textAdvice.setText(advice);
        
        binding.cardScamResult.post(() -> {
            View parent = (View) binding.cardScamResult.getParent();
            if (parent instanceof androidx.core.widget.NestedScrollView) {
                ((androidx.core.widget.NestedScrollView) parent).smoothScrollTo(0, binding.cardScamResult.getTop());
            }
        });
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        binding = null;
    }
}
