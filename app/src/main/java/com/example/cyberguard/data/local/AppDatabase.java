package com.example.cyberguard.data.local;

import android.content.Context;

import androidx.annotation.NonNull;
import androidx.room.Database;
import androidx.room.Room;
import androidx.room.RoomDatabase;
import androidx.sqlite.db.SupportSQLiteDatabase;

import com.example.cyberguard.data.local.dao.AdvisoryDao;
import com.example.cyberguard.data.local.dao.FraudDao;
import com.example.cyberguard.data.local.dao.PostDao;
import com.example.cyberguard.data.local.dao.UserDao;
import com.example.cyberguard.data.local.entity.AdvisoryEntity;
import com.example.cyberguard.data.local.entity.FraudEntity;
import com.example.cyberguard.data.local.entity.PostEntity;
import com.example.cyberguard.data.local.entity.UserEntity;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

@Database(entities = {FraudEntity.class, AdvisoryEntity.class, UserEntity.class, PostEntity.class}, version = 7, exportSchema = false)
public abstract class AppDatabase extends RoomDatabase {
    public abstract FraudDao fraudDao();
    public abstract AdvisoryDao advisoryDao();
    public abstract UserDao userDao();
    public abstract PostDao postDao();

    private static volatile AppDatabase INSTANCE;
    private static final int NUMBER_OF_THREADS = 4;
    public static final ExecutorService databaseWriteExecutor =
            Executors.newFixedThreadPool(NUMBER_OF_THREADS);

    public static AppDatabase getDatabase(final Context context) {
        if (INSTANCE == null) {
            synchronized (AppDatabase.class) {
                if (INSTANCE == null) {
                    INSTANCE = Room.databaseBuilder(context.getApplicationContext(),
                                    AppDatabase.class, "cyberguard_db")
                            .fallbackToDestructiveMigration()
                            .addCallback(sRoomDatabaseCallback)
                            .build();
                }
            }
        }
        return INSTANCE;
    }

    private static final RoomDatabase.Callback sRoomDatabaseCallback = new RoomDatabase.Callback() {
        @Override
        public void onCreate(@NonNull SupportSQLiteDatabase db) {
            super.onCreate(db);
            databaseWriteExecutor.execute(() -> {
                AdvisoryDao dao = INSTANCE.advisoryDao();
                dao.deleteAll();

                List<AdvisoryEntity> list = new ArrayList<>();
                
                String[] scamTitles = {
                    "Phishing Scam", "Spear Phishing", "Vishing (Phone Scam)", "Smishing (SMS Scam)",
                    "Credit Card Fraud", "Debit Card Fraud", "OTP Scam", "UPI Scam",
                    "QR Code Scam", "Lottery Scam", "Job Scam", "Investment Scam",
                    "Ponzi Scheme", "Crypto Scam", "Romance Scam", "Fake Shopping Website Scam",
                    "Refund Scam", "Tech Support Scam", "SIM Swap Scam", "Identity Theft",
                    "Loan Scam", "Charity Scam", "Online Auction Scam", "Business Email Compromise (BEC)",
                    "Social Media Scam", "Fake App Scam", "Malware Scam", "Ransomware Attack",
                    "Data Breach Scam", "Giveaway Scam"
                };

                for (int i = 0; i < scamTitles.length; i++) {
                    String title = scamTitles[i];
                    String content = "INTRODUCTION:\n" + title + " is a highly dangerous form of cybercrime that targets innocent individuals globally. This scam typically begins with a deceptive message or call designed to exploit human psychology. Attackers often spend weeks or months refining their tactics to appear as legitimate as possible. They may impersonate reputable brands, government officials, or even close friends to gain your trust. The primary goal is usually to steal sensitive personal information or financial assets directly. These scams are evolving rapidly, using artificial intelligence and automation to reach millions of potential victims. Education and awareness are the only true defenses against such sophisticated digital threats. By understanding the core mechanics of " + title + ", you can protect yourself and your family effectively.\n\nHOW TO STAY AWAY:\nTo remain safe from " + title + ", you must maintain a high level of digital skepticism at all times. Never click on links provided in unsolicited emails, text messages, or social media chats from unknown senders. Always verify the identity of anyone who contacts you asking for money or private information through a secondary, official channel. Use a robust, premium security suite on all your devices to provide real-time protection against malicious websites. Enable multi-factor authentication (MFA) on every single online account, preferably using a hardware key or authenticator app. Regularly update all your software, including your operating system and browsers, to patch any known security vulnerabilities. Educate yourself on the latest social engineering tactics by following reputable cybersecurity news sources and government advisories. Be extremely cautious about sharing personal details like your date of birth, address, or phone number on public platforms. If an offer seems too good to be true, such as an unexpected prize or high-return investment, it is almost certainly a scam.\n\nIF SCAMMED:\nIf you believe you have fallen victim to " + title + ", you must act with extreme speed to minimize the potential damage. Immediately contact your bank and credit card issuers to freeze your accounts and report any unauthorized transactions. Change all your passwords for your email, social media, and financial accounts immediately, using a secure password manager. Document every single detail of the interaction, including screenshots of messages, transaction IDs, and the scammer's contact info. File a formal complaint on the National Cybercrime Reporting Portal (cybercrime.gov.in) or your local law enforcement agency. Alert your friends, family, and professional network so they don't fall for any messages sent from your compromised accounts. Monitor your credit reports closely for several months to detect any signs of identity theft or new unauthorized accounts. Scan all your digital devices with advanced antivirus software to ensure no hidden malware or trackers were installed. Finally, share your experience with others to help raise awareness and prevent them from suffering the same fate.";
                    
                    list.add(new AdvisoryEntity(String.valueOf(i + 1), title, content, System.currentTimeMillis(), "CyberGuard Intelligence"));
                }
                
                dao.insertAll(list);
            });
        }
    };
}
