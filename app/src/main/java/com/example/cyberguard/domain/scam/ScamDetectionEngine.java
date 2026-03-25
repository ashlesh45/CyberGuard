package com.example.cyberguard.domain.scam;

import java.util.ArrayList;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class ScamDetectionEngine {

    private final Pattern urlPattern = Pattern.compile("https?://[\\w\\.-]+(?:\\.[\\w\\.-]+)+[\\w\\-\\._~:/?#\\[\\]@!\\$&'\\(\\)\\*\\+,;=.]*");
    private final String[] highRiskKeywords = {
            "prize", "won", "reward", "lottery", "gift card", "crypto", "bitcoin",
            "urgent", "account locked", "verify identity", "bank alert", "unauthorized access",
            "suspicious activity", "action required", "irs", "lawsuit", "arrest", "ebanking"
    };
    
    private final String[] suspiciousTlds = {".zip", ".mov", ".click", ".top", ".xyz", ".work", ".loan", ".support", ".info"};

    public static class DetectionResult {
        public final int score;
        public final String verdict;
        public final List<String> reasons;

        public DetectionResult(int score, String verdict, List<String> reasons) {
            this.score = Math.min(100, score);
            this.verdict = verdict;
            this.reasons = reasons;
        }
    }

    public DetectionResult analyzeText(String content) {
        if (isEmailHeader(content)) {
            return analyzeEmailHeader(content);
        }

        int score = 0;
        List<String> reasons = new ArrayList<>();
        String lowercaseContent = content.toLowerCase();

        // 1. Keyword Analysis
        int keywordMatches = 0;
        for (String keyword : highRiskKeywords) {
            if (lowercaseContent.contains(keyword)) {
                keywordMatches++;
                if (keywordMatches <= 3) {
                    reasons.add("Found high-risk term: \"" + keyword + "\"");
                }
            }
        }
        score += keywordMatches * 15;

        // 2. Link Analysis (URL Deep-Link Scanner logic)
        Matcher matcher = urlPattern.matcher(content);
        boolean foundUrl = false;
        while (matcher.find()) {
            foundUrl = true;
            String url = matcher.group();
            score += 25;
            reasons.add("Detected URL: " + url + " (External links are common in phishing)");
            
            for (String tld : suspiciousTlds) {
                if (url.toLowerCase().endsWith(tld) || url.toLowerCase().contains(tld + "/")) {
                    score += 20;
                    reasons.add("High Risk TLD detected: " + tld);
                    break;
                }
            }
            
            if (url.length() > 50) {
                score += 10;
                reasons.add("Suspiciously long URL (often used to hide real domain)");
            }
        }

        // 3. Urgency and Tone
        if (content.contains("!!") || content.contains("???")) {
            score += 10;
            reasons.add("Excessive punctuation detected (Urgency tactic)");
        }
        
        if (content.matches(".*[A-Z]{5,}.*")) {
            score += 15;
            reasons.add("Detected 'shouting' (ALL CAPS) - common in pressure tactics");
        }

        // 4. Financial/Personal Data requests
        if (lowercaseContent.contains("credit card") || lowercaseContent.contains("password") || 
            lowercaseContent.contains("social security") || lowercaseContent.contains("otp") ||
            lowercaseContent.contains("cvv")) {
            score += 35;
            reasons.add("Requests sensitive personal/financial data (CRITICAL)");
        }

        String verdict;
        if (score >= 60) verdict = "High Risk - Likely Scam";
        else if (score >= 30) verdict = "Moderate Risk - Be Cautious";
        else verdict = "Low Risk - Appears Safe";

        return new DetectionResult(score, verdict, reasons);
    }

    private boolean isEmailHeader(String content) {
        return content.contains("Received:") || content.contains("From:") || content.contains("Return-Path:");
    }

    private DetectionResult analyzeEmailHeader(String header) {
        int score = 40; // Base score for suspicious headers
        List<String> reasons = new ArrayList<>();
        reasons.add("Email Header detected: Analyzing for Spoofing...");

        // Simple spoofing check: Compare 'From' and 'Return-Path' if present
        Pattern fromPattern = Pattern.compile("From:.*?<(.*?)>");
        Pattern returnPathPattern = Pattern.compile("Return-Path:.*?<(.*?)>");

        Matcher fromMatcher = fromPattern.matcher(header);
        Matcher returnMatcher = returnPathPattern.matcher(header);

        if (fromMatcher.find() && returnMatcher.find()) {
            String fromEmail = fromMatcher.group(1).toLowerCase();
            String returnEmail = returnMatcher.group(1).toLowerCase();

            if (!fromEmail.equals(returnEmail)) {
                score += 40;
                reasons.add("CRITICAL: 'From' address (" + fromEmail + ") does not match 'Return-Path' (" + returnEmail + "). Likely spoofed!");
            } else {
                reasons.add("Sender email consistency verified.");
            }
        }

        if (header.contains("X-PHP-Originating-Script")) {
            score += 20;
            reasons.add("Email sent via PHP script - common in bulk phishing.");
        }

        String verdict = (score >= 60) ? "High Risk - Possible Email Spoofing" : "Caution - Technical Header Analyzed";
        return new DetectionResult(score, verdict, reasons);
    }
}
