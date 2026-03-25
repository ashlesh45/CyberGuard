package com.example.cyberguard;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.provider.Settings;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.biometric.BiometricManager;
import androidx.biometric.BiometricPrompt;
import androidx.core.content.ContextCompat;
import androidx.navigation.NavController;
import androidx.navigation.fragment.NavHostFragment;
import androidx.navigation.ui.AppBarConfiguration;
import androidx.navigation.ui.NavigationUI;

import com.example.cyberguard.databinding.ActivityMainBinding;

import java.util.concurrent.Executor;

import dagger.hilt.android.AndroidEntryPoint;

@AndroidEntryPoint
public class MainActivity extends AppCompatActivity {

    private ActivityMainBinding binding;
    private NavController navController;
    private boolean isAuthenticated = false;
    private SharedPreferences prefs;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = ActivityMainBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());

        prefs = getSharedPreferences("cyberguard_prefs", Context.MODE_PRIVATE);
        setSupportActionBar(binding.toolbar);

        NavHostFragment navHostFragment = (NavHostFragment) getSupportFragmentManager()
                .findFragmentById(R.id.nav_host_fragment);
        if (navHostFragment != null) {
            navController = navHostFragment.getNavController();
            
            NavigationUI.setupWithNavController(binding.bottomNavigation, navController);
            
            AppBarConfiguration appBarConfiguration = new AppBarConfiguration.Builder(
                    R.id.navigation_knowledge, R.id.navigation_advisories, 
                    R.id.navigation_scam, R.id.navigation_community, R.id.navigation_triage, R.id.navigation_settings)
                    .build();
            NavigationUI.setupWithNavController(binding.toolbar, navController, appBarConfiguration);

            navController.addOnDestinationChangedListener((controller, destination, arguments) -> {
                updateToolbarProfile();
                if (destination.getId() == R.id.navigation_login) {
                    binding.bottomNavigation.setVisibility(android.view.View.GONE);
                    binding.toolbar.setVisibility(android.view.View.GONE);
                } else {
                    binding.bottomNavigation.setVisibility(android.view.View.VISIBLE);
                    binding.toolbar.setVisibility(android.view.View.VISIBLE);
                    
                    // Check authentication for sensitive areas
                    if (destination.getId() == R.id.navigation_settings || destination.getId() == R.id.navigation_triage) {
                        if (!isAuthenticated) {
                            checkAndShowBiometricPrompt();
                        }
                    }
                }
            });
        }

        binding.toolbarProfileCard.setOnClickListener(v -> {
            if (navController != null) {
                navController.navigate(R.id.navigation_settings);
            }
        });
    }

    @Override
    protected void onResume() {
        super.onResume();
        updateToolbarProfile();
    }

    private void updateToolbarProfile() {
        if (binding == null) return;
        
        String profilePicUri = prefs.getString("profile_pic_uri", null);
        if (profilePicUri != null) {
            binding.toolbarProfileCard.setVisibility(android.view.View.VISIBLE);
            binding.toolbarProfileImage.setImageURI(Uri.parse(profilePicUri));
            binding.toolbarProfileImage.setImageTintList(null);
        } else {
            binding.toolbarProfileCard.setVisibility(android.view.View.VISIBLE);
            binding.toolbarProfileImage.setImageResource(android.R.drawable.ic_menu_myplaces);
            binding.toolbarProfileImage.setImageTintList(ContextCompat.getColorStateList(this, R.color.on_primary));
        }
    }

    private void checkAndShowBiometricPrompt() {
        BiometricManager biometricManager = BiometricManager.from(this);
        int authenticators = BiometricManager.Authenticators.BIOMETRIC_STRONG | BiometricManager.Authenticators.DEVICE_CREDENTIAL;
        
        switch (biometricManager.canAuthenticate(authenticators)) {
            case BiometricManager.BIOMETRIC_SUCCESS:
                showBiometricPrompt(authenticators);
                break;
            case BiometricManager.BIOMETRIC_ERROR_NONE_ENROLLED:
                showEnrollmentDialog(authenticators);
                break;
            case BiometricManager.BIOMETRIC_ERROR_NO_HARDWARE:
                Toast.makeText(this, "No biometric hardware found. Access granted for lab purposes.", Toast.LENGTH_LONG).show();
                isAuthenticated = true; // Bypass for lab if no hardware
                break;
            default:
                Toast.makeText(this, "Biometric authentication currently unavailable.", Toast.LENGTH_SHORT).show();
                navController.navigateUp();
                break;
        }
    }

    private void showEnrollmentDialog(int authenticators) {
        new AlertDialog.Builder(this)
                .setTitle("Security Setup Required")
                .setMessage("No fingerprint or screen lock enrolled. Please set up a PIN, pattern, or fingerprint to access this section.")
                .setPositiveButton("Go to Settings", (dialog, which) -> {
                    final Intent enrollIntent;
                    if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.R) {
                        enrollIntent = new Intent(Settings.ACTION_BIOMETRIC_ENROLL);
                        enrollIntent.putExtra(Settings.EXTRA_BIOMETRIC_AUTHENTICATORS_ALLOWED, authenticators);
                    } else {
                        enrollIntent = new Intent(Settings.ACTION_SECURITY_SETTINGS);
                    }
                    startActivity(enrollIntent);
                    navController.navigateUp();
                })
                .setNegativeButton("Cancel", (dialog, which) -> navController.navigateUp())
                .setCancelable(false)
                .show();
    }

    private void showBiometricPrompt(int authenticators) {
        Executor executor = ContextCompat.getMainExecutor(this);
        BiometricPrompt biometricPrompt = new BiometricPrompt(MainActivity.this,
                executor, new BiometricPrompt.AuthenticationCallback() {
            @Override
            public void onAuthenticationError(int errorCode, @NonNull CharSequence errString) {
                super.onAuthenticationError(errorCode, errString);
                Toast.makeText(getApplicationContext(), "Authentication error: " + errString, Toast.LENGTH_SHORT).show();
                navController.navigateUp(); 
            }

            @Override
            public void onAuthenticationSucceeded(@NonNull BiometricPrompt.AuthenticationResult result) {
                super.onAuthenticationSucceeded(result);
                isAuthenticated = true;
                Toast.makeText(getApplicationContext(), "Authentication succeeded!", Toast.LENGTH_SHORT).show();
            }

            @Override
            public void onAuthenticationFailed() {
                super.onAuthenticationFailed();
                Toast.makeText(getApplicationContext(), "Authentication failed", Toast.LENGTH_SHORT).show();
            }
        });

        BiometricPrompt.PromptInfo promptInfo = new BiometricPrompt.PromptInfo.Builder()
                .setTitle("Security Verification")
                .setSubtitle("Authenticate to access profile/settings")
                .setAllowedAuthenticators(authenticators)
                .build();

        biometricPrompt.authenticate(promptInfo);
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.toolbar_menu, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(@NonNull MenuItem item) {
        int id = item.getItemId();
        if (id == R.id.action_logout) {
            showLogoutConfirmation();
            return true;
        }
        return super.onOptionsItemSelected(item);
    }

    private void showLogoutConfirmation() {
        new AlertDialog.Builder(this)
                .setTitle("Logout")
                .setMessage("Are you sure you want to logout?")
                .setPositiveButton("Logout", (dialog, which) -> {
                    isAuthenticated = false;
                    SharedPreferences prefs = getSharedPreferences("cyberguard_prefs", Context.MODE_PRIVATE);
                    prefs.edit().remove("username").apply();
                    navController.navigate(R.id.action_global_login);
                })
                .setNegativeButton("Cancel", null)
                .show();
    }
}
