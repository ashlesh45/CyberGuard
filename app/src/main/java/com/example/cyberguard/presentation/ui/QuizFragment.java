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
import androidx.navigation.Navigation;

import com.example.cyberguard.R;
import com.example.cyberguard.databinding.FragmentQuizBinding;

import java.util.ArrayList;
import java.util.List;

import dagger.hilt.android.AndroidEntryPoint;

@AndroidEntryPoint
public class QuizFragment extends Fragment {

    private FragmentQuizBinding binding;
    private int currentQuestionIndex = 0;
    private int score = 0;
    private final Handler handler = new Handler(Looper.getMainLooper());

    private static class Question {
        String text;
        boolean correctAnswer;
        String explanation;

        Question(String text, boolean correctAnswer, String explanation) {
            this.text = text;
            this.correctAnswer = correctAnswer;
            this.explanation = explanation;
        }
    }

    private final List<Question> questions = new ArrayList<Question>() {{
        add(new Question("Is it safe to share your bank OTP with someone claiming to be a bank official?", false, "Banks will NEVER ask for your OTP. Anyone asking for it is a scammer."));
        add(new Question("Should you click links in emails that say your account is locked and require immediate action?", false, "Legitimate banks don't send links for sensitive actions. Always use the official app or website."));
        add(new Question("Is it safe to use the same password for your bank account and your social media?", false, "Using the same password means if one account is hacked, ALL of them are at risk."));
        add(new Question("If a stranger calls and says you won a lottery but need to pay a small 'processing fee', is it a scam?", true, "Real lotteries never ask for money upfront. This is a common advance-fee fraud."));
        add(new Question("Should you ever download remote access apps (like AnyDesk) if a technical support caller asks you to?", false, "Scammers use these apps to take control of your computer and steal your bank details."));
    }};

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        binding = FragmentQuizBinding.inflate(inflater, container, false);
        return binding.getRoot();
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        displayQuestion();

        binding.btnYes.setOnClickListener(v -> checkAnswer(true));
        binding.btnNo.setOnClickListener(v -> checkAnswer(false));
        binding.btnFinishQuiz.setOnClickListener(v -> Navigation.findNavController(v).navigateUp());
    }

    private void displayQuestion() {
        if (currentQuestionIndex < questions.size()) {
            Question q = questions.get(currentQuestionIndex);
            binding.textQuestion.setText(q.text);
            binding.textQuestionCount.setText("Question " + (currentQuestionIndex + 1) + "/" + questions.size());
            binding.quizProgress.setProgress((currentQuestionIndex + 1) * 20);
            
            // Reset buttons
            binding.btnYes.setEnabled(true);
            binding.btnNo.setEnabled(true);
            binding.btnYes.setBackgroundTintList(null);
            binding.btnNo.setBackgroundTintList(null);
        } else {
            showResult();
        }
    }

    private void checkAnswer(boolean userPressedYes) {
        Question q = questions.get(currentQuestionIndex);
        boolean isCorrect = (userPressedYes == q.correctAnswer);
        
        // Disable buttons during feedback
        binding.btnYes.setEnabled(false);
        binding.btnNo.setEnabled(false);

        // Visual feedback
        int green = ContextCompat.getColor(requireContext(), R.color.risk_low);
        int red = ContextCompat.getColor(requireContext(), R.color.risk_high);
        
        if (userPressedYes) {
            binding.btnYes.setBackgroundTintList(ColorStateList.valueOf(isCorrect ? green : red));
        } else {
            binding.btnNo.setBackgroundTintList(ColorStateList.valueOf(isCorrect ? green : red));
        }

        if (isCorrect) {
            score++;
            handler.postDelayed(this::moveToNext, 1000);
        } else {
            // Navigate to explanation screen
            Bundle args = new Bundle();
            args.putString("correctAnswer", q.correctAnswer ? "YES" : "NO");
            args.putString("explanation", q.explanation);
            
            handler.postDelayed(() -> {
                if (getView() != null) {
                    Navigation.findNavController(requireView()).navigate(R.id.action_quiz_to_explanation, args);
                    moveToNext(); // Move index while explanation is showing
                }
            }, 1000);
        }
    }

    private void moveToNext() {
        currentQuestionIndex++;
        if (isAdded()) {
            displayQuestion();
        }
    }

    private void showResult() {
        binding.layoutQuiz.setVisibility(View.GONE);
        binding.layoutResult.setVisibility(View.VISIBLE);

        int percent = (score * 100) / questions.size();
        binding.textScorePercent.setText(percent + "%");
        binding.textSummary.setText("Final Score: " + score + " / " + questions.size());

        binding.pieChartRight.setProgress(percent, true);
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        binding = null;
    }
}
