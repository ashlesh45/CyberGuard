package com.example.cyberguard.data.repository;

import androidx.lifecycle.LiveData;
import androidx.lifecycle.Transformations;

import com.example.cyberguard.data.local.AppDatabase;
import com.example.cyberguard.data.local.dao.AdvisoryDao;
import com.example.cyberguard.data.local.entity.AdvisoryEntity;
import com.example.cyberguard.data.remote.AdvisoryApi;
import com.example.cyberguard.domain.model.Advisory;

import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

import javax.inject.Inject;
import javax.inject.Singleton;

@Singleton
public class AdvisoryRepository {
    private final AdvisoryDao advisoryDao;
    private final AdvisoryApi advisoryApi;
    private final LiveData<List<Advisory>> allAdvisories;

    @Inject
    public AdvisoryRepository(AdvisoryDao advisoryDao, AdvisoryApi advisoryApi) {
        this.advisoryDao = advisoryDao;
        this.advisoryApi = advisoryApi;

        allAdvisories = Transformations.map(advisoryDao.getAllAdvisories(), entities ->
                entities.stream().map(entity -> new Advisory(
                        entity.getId(),
                        entity.getTitle(),
                        entity.getContent(),
                        entity.getTimestamp(),
                        entity.getSource()
                )).collect(Collectors.toList())
        );
    }

    public LiveData<List<Advisory>> getAllAdvisories() {
        return allAdvisories;
    }

    public void checkAndPopulateData() {
        AppDatabase.databaseWriteExecutor.execute(() -> {
            // Check if we already have the detailed content
            List<AdvisoryEntity> existing = advisoryDao.getAllAdvisoriesSync();
            if (existing != null && !existing.isEmpty() && existing.get(0).getContent().contains("ACTION PLAN FOR PRESSURE TACTICS:")) {
                return; // Already populated with new content
            }

            advisoryDao.deleteAll();
            
            String[] titles = {
                "Phishing Scam", "Spear Phishing", "Vishing (Phone Scam)", "Smishing (SMS Scam)",
                "Credit Card Fraud", "Debit Card Fraud", "OTP Scam", "UPI Scam",
                "QR Code Scam", "Lottery Scam", "Job Scam", "Investment Scam",
                "Ponzi Scheme", "Crypto Scam", "Romance Scam", "Fake Shopping Website Scam",
                "Refund Scam", "Tech Support Scam", "SIM Swap Scam", "Identity Theft",
                "Loan Scam", "Charity Scam", "Online Auction Scam", "Business Email Compromise (BEC)",
                "Social Media Scam", "Fake App Scam", "Malware Scam", "Ransomware Attack",
                "Data Breach Scam", "Giveaway Scam"
            };

            List<AdvisoryEntity> list = new ArrayList<>();
            for (int i = 0; i < titles.length; i++) {
                list.add(new AdvisoryEntity(String.valueOf(i + 1), titles[i], generateContent(titles[i]), System.currentTimeMillis(), "CyberGuard Intelligence"));
            }
            advisoryDao.insertAll(list);
        });
    }

    private String generateContent(String title) {
        return "INTRODUCTION:\n" +
               title + " is one of the most sophisticated and widely practiced forms of cybercrime in the modern digital landscape. " +
               "Scammers use complex blends of social engineering and technical deception to bypass security measures. " +
               "The ultimate objective is always the same: to gain unauthorized access to your sensitive personal data or financial assets.\n\n" +
               
               "HOW TO STAY AWAY:\n" +
               "Adopt a policy of 'zero trust' for any unsolicited communication. " +
               "Never click on links or download attachments from unknown emails. " +
               "Enable Multi-Factor Authentication (MFA) on all accounts and use a reputable password manager.\n\n" +

               "ACTION PLAN FOR PRESSURE TACTICS:\n" +
               "When an attacker uses pressure tactics to force " + title + " upon you, stay calm. " +
               "Recognize that a sense of urgency is a major red flag. " +
               "Slow down the conversation. Tell them you need to verify their claims and hang up. " +
               "Do not respond to threats of legal action or account closure. " +
               "Independently verify by calling the official number of the organization from their verified website.\n\n" +
               
               "IF SCAMMED:\n" +
               "Contact your bank immediately to freeze your accounts. " +
               "Change the passwords for all your critical accounts and enable two-factor authentication. " +
               "File an official report on the National Cybercrime Reporting Portal at cybercrime.gov.in with all evidence gathered.";
    }

    public void refreshAdvisories() {
        checkAndPopulateData();
    }
}
