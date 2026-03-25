package com.example.cyberguard.presentation.ui;

import android.content.pm.ApplicationInfo;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.cyberguard.databinding.FragmentAppAuditBinding;
import com.example.cyberguard.databinding.ItemAppAuditBinding;

import java.util.ArrayList;
import java.util.List;

import dagger.hilt.android.AndroidEntryPoint;

@AndroidEntryPoint
public class AppAuditFragment extends Fragment {

    private FragmentAppAuditBinding binding;

    public static class AppRisk {
        public String name;
        public String packageName;
        public List<String> dangerousPermissions;

        public AppRisk(String name, String packageName, List<String> dangerousPermissions) {
            this.name = name;
            this.packageName = packageName;
            this.dangerousPermissions = dangerousPermissions;
        }
    }

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        binding = FragmentAppAuditBinding.inflate(inflater, container, false);
        return binding.getRoot();
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        
        binding.recyclerApps.setLayoutManager(new LinearLayoutManager(getContext()));
        performAudit();
    }

    private void performAudit() {
        PackageManager pm = requireContext().getPackageManager();
        List<PackageInfo> packages = pm.getInstalledPackages(PackageManager.GET_PERMISSIONS);
        List<AppRisk> highRiskApps = new ArrayList<>();

        for (PackageInfo pkg : packages) {
            // Filter out system apps for clarity
            if ((pkg.applicationInfo.flags & ApplicationInfo.FLAG_SYSTEM) != 0) continue;

            List<String> dangerous = new ArrayList<>();
            if (pkg.requestedPermissions != null) {
                for (String perm : pkg.requestedPermissions) {
                    if (perm.contains("READ_SMS")) dangerous.add("Can read your SMS (OTP theft risk)");
                    if (perm.contains("RECORD_AUDIO")) dangerous.add("Can record your voice/calls");
                    if (perm.contains("ACCESS_FINE_LOCATION")) dangerous.add("Can track your exact location");
                    if (perm.contains("CAMERA")) dangerous.add("Can use your camera");
                }
            }

            if (!dangerous.isEmpty()) {
                highRiskApps.add(new AppRisk(pm.getApplicationLabel(pkg.applicationInfo).toString(), pkg.packageName, dangerous));
            }
        }

        binding.textAuditCount.setText("Found " + highRiskApps.size() + " apps with sensitive permissions");
        binding.recyclerApps.setAdapter(new AuditAdapter(highRiskApps));
        binding.progressAudit.setVisibility(View.GONE);
    }

    private static class AuditAdapter extends RecyclerView.Adapter<AuditAdapter.ViewHolder> {
        private final List<AppRisk> apps;

        AuditAdapter(List<AppRisk> apps) { this.apps = apps; }

        @NonNull
        @Override
        public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
            ItemAppAuditBinding binding = ItemAppAuditBinding.inflate(LayoutInflater.from(parent.getContext()), parent, false);
            return new ViewHolder(binding);
        }

        @Override
        public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
            AppRisk app = apps.get(position);
            holder.binding.textAppName.setText(app.name);
            holder.binding.textPackageName.setText(app.packageName);
            holder.binding.textPermissions.setText(String.join("\n", app.dangerousPermissions));
        }

        @Override
        public int getItemCount() { return apps.size(); }

        static class ViewHolder extends RecyclerView.ViewHolder {
            final ItemAppAuditBinding binding;
            ViewHolder(ItemAppAuditBinding binding) {
                super(binding.getRoot());
                this.binding = binding;
            }
        }
    }
}
