package com.example.cyberguard.presentation.ui;

import android.graphics.Color;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.core.content.ContextCompat;
import androidx.fragment.app.Fragment;

import com.example.cyberguard.R;
import com.example.cyberguard.databinding.FragmentTriageBinding;
import com.example.cyberguard.domain.triage.TriageSystem;

public class TriageFragment extends Fragment {

    private FragmentTriageBinding binding;
    private final TriageSystem triageSystem = new TriageSystem();

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        binding = FragmentTriageBinding.inflate(inflater, container, false);
        return binding.getRoot();
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        binding.btnEvaluate.setOnClickListener(v -> {
            boolean lostMoney = binding.checkLostMoney.isChecked();
            boolean sharedCreds = binding.checkSharedCreds.isChecked();
            boolean clickedLink = binding.checkClickedLink.isChecked();
            boolean urgency = binding.checkUrgency.isChecked();

            TriageSystem.TriageResult result = triageSystem.evaluateIncident(lostMoney, sharedCreds, clickedLink, urgency);
            updateUI(result);
        });
    }

    private void updateUI(TriageSystem.TriageResult result) {
        binding.cardResult.setVisibility(View.VISIBLE);
        binding.textRiskLevel.setText("PRIORITY: " + result.riskLevel.name());
        binding.textExplanation.setText(result.explanation);
        binding.textActions.setText(String.join("\n", result.recommendedActions.stream().map(r -> "• " + r).toArray(String[]::new)));

        int color;
        switch (result.riskLevel) {
            case CRITICAL:
                color = ContextCompat.getColor(requireContext(), R.color.risk_high);
                break;
            case HIGH:
                color = ContextCompat.getColor(requireContext(), R.color.risk_medium); // Using medium for High for visual variety
                break;
            case MEDIUM:
                color = Color.parseColor("#FFC107"); // Yellow
                break;
            default:
                color = ContextCompat.getColor(requireContext(), R.color.risk_low);
                break;
        }

        binding.headerResult.setBackgroundColor(color);
        
        // Auto-scroll to result
        binding.cardResult.post(() -> {
            View parent = (View) binding.cardResult.getParent();
            if (parent instanceof androidx.core.widget.NestedScrollView) {
                ((androidx.core.widget.NestedScrollView) parent).smoothScrollTo(0, binding.cardResult.getTop());
            }
        });
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        binding = null;
    }
}
