package com.example.cyberguard.domain.triage;

import java.util.ArrayList;
import java.util.List;

public class TriageSystem {

    public enum RiskLevel { LOW, MEDIUM, HIGH, CRITICAL }

    public static class TriageResult {
        public final RiskLevel riskLevel;
        public final String explanation;
        public final List<String> recommendedActions;

        public TriageResult(RiskLevel riskLevel, String explanation, List<String> recommendedActions) {
            this.riskLevel = riskLevel;
            this.explanation = explanation;
            this.recommendedActions = recommendedActions;
        }
    }

    public TriageResult evaluateIncident(boolean lostMoney, boolean sharedCredentials, boolean clickedLink, boolean urgencyPresent) {
        int score = 0;
        List<String> actions = new ArrayList<>();
        StringBuilder explanation = new StringBuilder();

        if (lostMoney) {
            score += 4;
            explanation.append("Financial loss occurred. ");
            actions.add("Contact your bank immediately to freeze accounts.");
            actions.add("Report to local cyber police.");
        }
        if (sharedCredentials) {
            score += 3;
            explanation.append("Credentials might be compromised. ");
            actions.add("Change passwords for all major accounts.");
            actions.add("Enable Multi-Factor Authentication (MFA).");
        }
        if (clickedLink) {
            score += 1;
            explanation.append("A suspicious link was accessed. ");
            actions.add("Run a malware scan on your device.");
        }
        if (urgencyPresent) {
            score += 1;
            explanation.append("The attacker used high-pressure tactics. ");
        }

        RiskLevel level;
        if (score >= 4) level = RiskLevel.CRITICAL;
        else if (score >= 3) level = RiskLevel.HIGH;
        else if (score >= 2) level = RiskLevel.MEDIUM;
        else level = RiskLevel.LOW;

        if (explanation.length() == 0) {
            explanation.append("No immediate signs of high-risk fraud detected.");
        }

        return new TriageResult(level, explanation.toString().trim(), actions);
    }
}
