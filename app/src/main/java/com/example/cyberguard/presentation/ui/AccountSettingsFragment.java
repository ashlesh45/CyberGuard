package com.example.cyberguard.presentation.ui;

import android.content.Context;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.navigation.Navigation;

import com.example.cyberguard.databinding.FragmentAccountSettingsBinding;

import dagger.hilt.android.AndroidEntryPoint;

@AndroidEntryPoint
public class AccountSettingsFragment extends Fragment {

    private FragmentAccountSettingsBinding binding;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        binding = FragmentAccountSettingsBinding.inflate(inflater, container, false);
        return binding.getRoot();
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        SharedPreferences prefs = requireContext().getSharedPreferences("cyberguard_prefs", Context.MODE_PRIVATE);
        
        // Load existing data
        binding.editUsername.setText(prefs.getString("username", ""));
        binding.editEmail.setText(prefs.getString("email", ""));
        binding.editPhone.setText(prefs.getString("phone", ""));
        binding.editAge.setText(prefs.getString("age", ""));

        binding.btnSaveSettings.setOnClickListener(v -> {
            String username = binding.editUsername.getText().toString().trim();
            String email = binding.editEmail.getText().toString().trim();
            String phone = binding.editPhone.getText().toString().trim();
            String age = binding.editAge.getText().toString().trim();

            if (username.isEmpty()) {
                binding.editUsername.setError("Name cannot be empty");
                return;
            }

            // Save all fields
            prefs.edit()
                    .putString("username", username)
                    .putString("email", email)
                    .putString("phone", phone)
                    .putString("age", age)
                    .apply();

            Toast.makeText(getContext(), "Profile updated successfully!", Toast.LENGTH_SHORT).show();
            Navigation.findNavController(requireView()).navigateUp();
        });
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        binding = null;
    }
}
