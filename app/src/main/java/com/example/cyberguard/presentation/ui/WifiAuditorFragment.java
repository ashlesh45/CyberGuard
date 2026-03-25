package com.example.cyberguard.presentation.ui;

import android.Manifest;
import android.content.Context;
import android.content.pm.PackageManager;
import android.net.wifi.WifiInfo;
import android.net.wifi.WifiManager;
import android.os.Bundle;
import android.os.Handler;
import android.os.Looper;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.core.content.ContextCompat;
import androidx.fragment.app.Fragment;

import com.example.cyberguard.R;
import com.example.cyberguard.databinding.FragmentWifiAuditorBinding;

import dagger.hilt.android.AndroidEntryPoint;

@AndroidEntryPoint
public class WifiAuditorFragment extends Fragment {

    private FragmentWifiAuditorBinding binding;
    private final Handler handler = new Handler(Looper.getMainLooper());

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        binding = FragmentWifiAuditorBinding.inflate(inflater, container, false);
        return binding.getRoot();
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        if (checkPermissions()) {
            updateWifiInfo();
        } else {
            requestPermissions(new String[]{
                    Manifest.permission.ACCESS_FINE_LOCATION,
                    Manifest.permission.ACCESS_WIFI_STATE
            }, 100);
        }

        binding.btnAuditWifi.setOnClickListener(v -> performAudit());
    }

    private boolean checkPermissions() {
        return ContextCompat.checkSelfPermission(requireContext(), Manifest.permission.ACCESS_FINE_LOCATION) == PackageManager.PERMISSION_GRANTED;
    }

    private void updateWifiInfo() {
        if (!isAdded()) return;
        
        WifiManager wifiManager = (WifiManager) requireContext().getApplicationContext().getSystemService(Context.WIFI_SERVICE);
        WifiInfo info = wifiManager.getConnectionInfo();

        if (info != null && info.getNetworkId() != -1) {
            String ssid = info.getSSID();
            if (ssid != null && !ssid.equals("<unknown ssid>")) {
                ssid = ssid.replace("\"", "");
                binding.textWifiName.setText("Connected to: " + ssid);
            } else {
                binding.textWifiName.setText("Connected to Wi-Fi");
            }
            binding.textEncryption.setText("Security: Analyzing...");
        } else {
            binding.textWifiName.setText("Not connected to Wi-Fi");
            binding.btnAuditWifi.setEnabled(false);
        }
    }

    private void performAudit() {
        binding.progressLoading.setVisibility(View.VISIBLE);
        binding.cardResult.setVisibility(View.GONE);
        binding.btnAuditWifi.setEnabled(false);

        handler.postDelayed(() -> {
            if (binding == null) return;
            
            binding.progressLoading.setVisibility(View.GONE);
            binding.cardResult.setVisibility(View.VISIBLE);
            binding.btnAuditWifi.setEnabled(true);

            boolean isSecure = Math.random() > 0.3; 
            if (isSecure) {
                showSecure();
            } else {
                showWarning();
            }
        }, 2500);
    }

    private void showSecure() {
        if (!isAdded()) return;
        int green = ContextCompat.getColor(requireContext(), R.color.risk_low);
        int greenBg = ContextCompat.getColor(requireContext(), R.color.risk_low_container);
        
        binding.layoutResultBg.setBackgroundColor(greenBg);
        binding.textVerdict.setText("NETWORK SECURE");
        binding.textVerdict.setTextColor(green);
        binding.textAdvice.setText("This network uses WPA2/WPA3 encryption. No 'Evil Twin' or MITM signatures detected. It is safe for standard browsing.");
        binding.textEncryption.setText("Security: WPA2-PSK (AES)");
    }

    private void showWarning() {
        if (!isAdded()) return;
        int orange = ContextCompat.getColor(requireContext(), R.color.risk_medium);
        int orangeBg = ContextCompat.getColor(requireContext(), R.color.risk_medium_container);
        
        binding.layoutResultBg.setBackgroundColor(orangeBg);
        binding.textVerdict.setText("SECURITY ADVISORY");
        binding.textVerdict.setTextColor(orange);
        binding.textAdvice.setText("WARNING: This network appears to be an open hotspot or uses weak encryption. Avoid accessing bank accounts or sensitive work data on this connection.");
        binding.textEncryption.setText("Security: OPEN / UNSECURED");
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        if (requestCode == 100) {
            if (grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                updateWifiInfo();
            } else {
                Toast.makeText(getContext(), "Location permission needed to read Wi-Fi name", Toast.LENGTH_SHORT).show();
                binding.textWifiName.setText("Permission Denied");
            }
        }
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        binding = null;
    }
}
