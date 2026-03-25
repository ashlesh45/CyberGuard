package com.example.cyberguard.presentation.ui;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.net.Uri;
import android.os.Bundle;
import android.text.InputType;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.EditText;
import android.widget.Toast;

import androidx.activity.result.ActivityResultLauncher;
import androidx.activity.result.contract.ActivityResultContracts;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatDelegate;
import androidx.fragment.app.Fragment;
import androidx.navigation.Navigation;

import com.example.cyberguard.R;
import com.example.cyberguard.databinding.FragmentSettingsBinding;

import dagger.hilt.android.AndroidEntryPoint;

@AndroidEntryPoint
public class SettingsFragment extends Fragment {

    private FragmentSettingsBinding binding;
    private SharedPreferences prefs;

    private final ActivityResultLauncher<Intent> pickImageLauncher = registerForActivityResult(
            new ActivityResultContracts.StartActivityForResult(),
            result -> {
                if (result.getResultCode() == Activity.RESULT_OK && result.getData() != null) {
                    Uri imageUri = result.getData().getData();
                    if (imageUri != null) {
                        try {
                            requireContext().getContentResolver().takePersistableUriPermission(imageUri,
                                    Intent.FLAG_GRANT_READ_URI_PERMISSION);
                            prefs.edit().putString("profile_pic_uri", imageUri.toString()).apply();
                            binding.imageProfile.setImageURI(imageUri);
                            binding.imageProfile.setImageTintList(null);
                            binding.imageProfile.setPadding(0, 0, 0, 0);
                            binding.imageProfile.setScaleType(android.widget.ImageView.ScaleType.CENTER_CROP);
                            Toast.makeText(getContext(), "Profile picture updated!", Toast.LENGTH_SHORT).show();
                        } catch (Exception e) {
                            e.printStackTrace();
                            binding.imageProfile.setImageURI(imageUri);
                        }
                    }
                }
            }
    );

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        binding = FragmentSettingsBinding.inflate(inflater, container, false);
        return binding.getRoot();
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        prefs = requireContext().getSharedPreferences("cyberguard_prefs", Context.MODE_PRIVATE);
        String username = prefs.getString("username", "User");
        binding.textProfileName.setText(username);

        String profilePicUri = prefs.getString("profile_pic_uri", null);
        if (profilePicUri != null) {
            binding.imageProfile.setImageURI(Uri.parse(profilePicUri));
            binding.imageProfile.setImageTintList(null);
            binding.imageProfile.setPadding(0, 0, 0, 0);
            binding.imageProfile.setScaleType(android.widget.ImageView.ScaleType.CENTER_CROP);
        }

        setupThemeToggle();

        binding.btnChangePhoto.setOnClickListener(v -> {
            Intent intent = new Intent(Intent.ACTION_OPEN_DOCUMENT);
            intent.addCategory(Intent.CATEGORY_OPENABLE);
            intent.setType("image/*");
            pickImageLauncher.launch(intent);
        });

        // 1. Data Breach Checker
        binding.btnDataBreach.setOnClickListener(v -> {
            Navigation.findNavController(v).navigate(R.id.action_settings_to_data_breach);
        });

        // 2. Secure QR Scanner
        binding.btnQrScan.setOnClickListener(v -> {
            Navigation.findNavController(v).navigate(R.id.action_settings_to_qr_scanner);
        });

        // 3. Wi-Fi Security Auditor
        binding.btnWifiAudit.setOnClickListener(v -> {
            Navigation.findNavController(v).navigate(R.id.action_settings_to_wifi_auditor);
        });

        // 4. Panic Button
        binding.btnPanicButton.setOnClickListener(v -> {
            new AlertDialog.Builder(requireContext())
                    .setTitle("🚨 EMERGENCY ACTION PLAN")
                    .setMessage("1. CALL 1930 (Cyber Helpline) immediately.\n2. Freeze all bank accounts via your official bank app.\n3. Change your primary email password.\n4. Take screenshots of all fraudulent activity.")
                    .setPositiveButton("Call 1930", (d, w) -> {
                        Intent intent = new Intent(Intent.ACTION_DIAL, Uri.parse("tel:1930"));
                        startActivity(intent);
                    })
                    .setNegativeButton("Dismiss", null)
                    .show();
        });

        // 6. 2FA Setup Guide
        binding.btn2faGuide.setOnClickListener(v -> {
            Navigation.findNavController(v).navigate(R.id.action_settings_to_2fa_guide);
        });

        // 8. Cyber News
        binding.btnCyberNews.setOnClickListener(v -> {
            Intent intent = new Intent(Intent.ACTION_VIEW, Uri.parse("https://thehackernews.com/"));
            startActivity(intent);
        });

        binding.btnPasswordCheck.setOnClickListener(v -> showPasswordCheckDialog());
        binding.btnReportCrime.setOnClickListener(v -> {
            startActivity(new Intent(Intent.ACTION_VIEW, Uri.parse("https://www.cybercrime.gov.in/")));
        });
        binding.btnAppAudit.setOnClickListener(v -> Navigation.findNavController(v).navigate(R.id.action_settings_to_audit));
        binding.btnStartQuiz.setOnClickListener(v -> Navigation.findNavController(v).navigate(R.id.action_settings_to_quiz));
        binding.btnAccountSettings.setOnClickListener(v -> Navigation.findNavController(v).navigate(R.id.action_settings_to_account));
        binding.btnLogout.setOnClickListener(v -> showLogoutConfirmation());
    }

    private void setupThemeToggle() {
        int savedTheme = prefs.getInt("app_theme", AppCompatDelegate.MODE_NIGHT_FOLLOW_SYSTEM);
        
        if (savedTheme == AppCompatDelegate.MODE_NIGHT_NO) {
            binding.toggleTheme.check(R.id.btn_light_mode);
        } else if (savedTheme == AppCompatDelegate.MODE_NIGHT_YES) {
            binding.toggleTheme.check(R.id.btn_dark_mode);
        } else {
            binding.toggleTheme.check(R.id.btn_system_mode);
        }

        binding.toggleTheme.addOnButtonCheckedListener((group, checkedId, isChecked) -> {
            if (isChecked) {
                int mode;
                if (checkedId == R.id.btn_light_mode) {
                    mode = AppCompatDelegate.MODE_NIGHT_NO;
                } else if (checkedId == R.id.btn_dark_mode) {
                    mode = AppCompatDelegate.MODE_NIGHT_YES;
                } else {
                    mode = AppCompatDelegate.MODE_NIGHT_FOLLOW_SYSTEM;
                }
                
                prefs.edit().putInt("app_theme", mode).apply();
                AppCompatDelegate.setDefaultNightMode(mode);
            }
        });
    }

    private void showSimpleDialog(String title, String message) {
        new AlertDialog.Builder(requireContext())
                .setTitle(title)
                .setMessage(message)
                .setPositiveButton("OK", null)
                .show();
    }

    private void showPasswordCheckDialog() {
        EditText input = new EditText(requireContext());
        input.setInputType(InputType.TYPE_CLASS_TEXT | InputType.TYPE_TEXT_VARIATION_PASSWORD);
        input.setHint("Enter password to test");
        new AlertDialog.Builder(requireContext())
                .setTitle("Password Strength Meter")
                .setView(input)
                .setPositiveButton("Check", (dialog, which) -> checkPassword(input.getText().toString()))
                .setNegativeButton("Cancel", null)
                .show();
    }

    private void checkPassword(String password) {
        String strength;
        if (password.length() < 6) strength = "VERY WEAK: Too short.";
        else if (password.matches("[a-zA-Z]+") || password.matches("[0-9]+")) strength = "WEAK: Use a mix of letters and numbers.";
        else if (password.length() < 10) strength = "MEDIUM: Add special characters.";
        else strength = "STRONG: Excellent password!";
        showSimpleDialog("Result", strength);
    }

    private void showLogoutConfirmation() {
        new AlertDialog.Builder(requireContext())
                .setTitle("Logout")
                .setMessage("Are you sure?")
                .setPositiveButton("Logout", (dialog, which) -> {
                    prefs.edit().remove("username").apply();
                    Navigation.findNavController(requireView()).navigate(R.id.action_global_login);
                })
                .setNegativeButton("Cancel", null)
                .show();
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        binding = null;
    }
}
