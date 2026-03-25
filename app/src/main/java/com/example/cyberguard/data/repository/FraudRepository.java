package com.example.cyberguard.data.repository;

import androidx.lifecycle.LiveData;
import androidx.lifecycle.Transformations;

import com.example.cyberguard.data.local.AppDatabase;
import com.example.cyberguard.data.local.dao.FraudDao;
import com.example.cyberguard.data.local.entity.FraudEntity;
import com.example.cyberguard.domain.model.FraudType;

import java.util.Arrays;
import java.util.List;
import java.util.stream.Collectors;

import javax.inject.Inject;
import javax.inject.Singleton;

@Singleton
public class FraudRepository {
    private final FraudDao fraudDao;
    private final LiveData<List<FraudType>> allFraudTypes;

    @Inject
    public FraudRepository(FraudDao fraudDao) {
        this.fraudDao = fraudDao;
        
        allFraudTypes = Transformations.map(fraudDao.getAllFraudTypes(), entities -> 
            entities.stream().map(entity -> new FraudType(
                entity.getId(),
                entity.getTitle(),
                entity.getDescription(),
                Arrays.asList(entity.getPreventionTips().split("\\|")),
                Arrays.asList(entity.getCommonTactics().split("\\|")),
                getImageUrlForFraud(entity.getId())
            )).collect(Collectors.toList())
        );
    }

    private String getImageUrlForFraud(int id) {
        switch (id) {
            case 1: return "https://images.unsplash.com/photo-1563986768609-322da13575f3?auto=format&fit=crop&q=80&w=500";
            case 2: return "https://images.unsplash.com/photo-1512428559087-560fa5ceab42?auto=format&fit=crop&q=80&w=500";
            case 3: return "https://images.unsplash.com/photo-1620714223084-8fcacc6dfd8d?auto=format&fit=crop&q=80&w=500"; // Banking Updated
            case 4: return "https://images.unsplash.com/photo-1550751827-4bd374c3f58b?auto=format&fit=crop&q=80&w=500";
            case 5: return "https://images.unsplash.com/photo-1553729459-efe14ef6055d?auto=format&fit=crop&q=80&w=500"; // Job/Investment Updated
            default: return "https://images.unsplash.com/photo-1550751827-4bd374c3f58b?auto=format&fit=crop&q=80&w=500";
        }
    }

    public LiveData<List<FraudType>> getAllFraudTypes() {
        return allFraudTypes;
    }

    public void insertInitialData() {
        AppDatabase.databaseWriteExecutor.execute(() -> {
            List<FraudEntity> existing = fraudDao.getAllFraudTypesSync();
            boolean needsRefresh = existing == null || existing.isEmpty();

            if (needsRefresh) {
                fraudDao.deleteAll();
                List<FraudEntity> initialList = Arrays.asList(
                    createFraud(1, "🛡️ General Safety Tips", "Essential habits to protect your digital life.", 
                        "Never share OTP, PIN, or passwords|Use strong unique passwords|Enable 2FA", 
                        "Social engineering|Password guessing|Outdated software"),
                    
                    createFraud(2, "📱 Phone / SMS / WhatsApp Scams", "Protection against Vishing and Smishing attempts.", 
                        "Don't trust unknown calls asking for bank details|Never click links in SMS", 
                        "Impersonating officials|Sense of urgency|Fake prize alerts"),
                    
                    createFraud(3, "💳 Banking & Payment Scams", "Keep your financial accounts safe.", 
                        "Never scan unknown QR codes to receive money|Check UPI ID before sending", 
                        "Fake payment requests|QR code trickery|Skimming"),
                    
                    createFraud(4, "🌐 Online & Website Scams", "Safe browsing and shopping practices.", 
                        "Only shop on trusted websites (https)|Avoid too-good-to-be-true deals", 
                        "Fake websites|Malicious downloads|Popup warnings"),
                    
                    createFraud(5, "💼 Job / Investment Scams", "Avoid fraudulent employment schemes.", 
                        "Never pay money for a job offer|Verify company details", 
                        "Advanced payment requests|High return promises|Fake recruiters")
                );
                fraudDao.insertAll(initialList);
            }
        });
    }

    private FraudEntity createFraud(int id, String title, String intro, String tips, String tactics) {
        String fullDescription = "INTRODUCTION:\n" + intro + "\n\n" +
            "HOW SCAMMERS OPERATE:\n" + tactics.replace("|", "\n• ") + "\n\n" +
            "ACTION PLAN FOR PRESSURE TACTICS:\n" +
            "When an attacker uses pressure tactics, STAY CALM. Scammers create a false sense of urgency or fear to make you panic. " +
            "Take a deep breath and deliberately slow down. Tell them you need to verify their claims and hang up immediately. " +
            "Do not respond to threats of legal action. Legitimate organizations will NEVER rush you.\n\n" +
            "IF SCAMMED:\n" +
            "Immediately contact your bank's fraud department to freeze your accounts. " +
            "Change all your passwords and enable 2FA. Report on cybercrime.gov.in.";
            
        return new FraudEntity(id, title, fullDescription, tips, tactics);
    }
}
