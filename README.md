CyberGuard: Your Digital Safety Shield
CyberGuard is a comprehensive Android application designed to bolster personal digital security and cybersecurity awareness. The platform provides a suite of tools ranging from real-time threat detection to educational resources, empowering users to navigate the digital landscape safely.
Project Overview
CyberGuard serves as a proactive security assistant. By leveraging modern Android development practices and machine learning, the app identifies potential risks in real-time and provides up-to-date information on the evolving cybersecurity threat landscape.
Core Features
•
Intelligent QR Analysis: Utilizes Google ML Kit and CameraX to scan and evaluate URLs within QR codes. The system performs heuristic analysis to identify suspicious links, such as non-HTTPS connections or known URL shortening services frequently used in phishing.
•
Security Advisories: A dedicated feed of cybersecurity advisories and alerts, keeping users informed about recent data breaches and emerging digital threats.
•
Cybersecurity Knowledge Base: An educational repository featuring detailed information on various security topics to help users understand and mitigate digital risks.
•
Knowledge Verification: Integrated quiz modules designed to test and reinforce the user's understanding of cybersecurity best practices.
•
Biometric Integration: Implementation of biometric authentication to ensure secure access to the application's sensitive data and features.

Technical Architecture
The application follows the recommended Android Architecture Components (MVVM) to ensure a scalable, maintainable, and testable codebase.
•
Presentation Layer: Built with Fragments and View Binding, utilizing LiveData and ViewModel to manage UI state.
•
Domain/Data Layer: Employs a Repository pattern to abstract data sources, managing local persistence via Room and remote data fetching via Retrofit.
•
Dependency Injection: Hilt (Dagger) is used for robust dependency management across the application lifecycle.
Tech Stack
•
Platform: Android (Java/Kotlin)
•
Machine Learning: Google ML Kit (Barcode Scanning)
•
Camera API: CameraX (Core, Camera2, Lifecycle, and View)
•
Networking: Retrofit 2 with GSON conversion
•
Persistence: Room Database
•
Dependency Injection: Hilt (Dagger)
•
UI Components: Material Design 3, ConstraintLayout
•
Navigation: Jetpack Navigation Component
•
Security: Android Biometric Library
•
Image Loading: Glide
•
Asynchrony: Google Guava (ListenableFuture) for CameraX integration

<img width="385" height="848" alt="Screenshot 2026-03-21 100346" src="https://github.com/user-attachments/assets/8972bc3c-2c8e-4e8c-a1ba-0696299df9df" />
<img width="385" height="848" alt="Screenshot 2026-03-21 100346" src="https://github.com/user-attachments/assets/95712eb0-2ca4-4435-8a69-4f2603006fec" />
<img width="385" height="848" alt="Screenshot 2026-03-21 100346" src="https://github.com/user-attachments/assets/a2f77129-cdd6-47e4-a915-302edd1bc166" />
<img width="385" height="848" alt="Screenshot 2026-03-21 100346" src="https://github.com/user-attachments/assets/d792b459-284f-48fd-8de0-c37a54c1a781" />
<img width="385" height="848" alt="Screenshot 2026-03-21 100346" src="https://github.com/user-attachments/assets/eed88d00-6f87-42a6-81c9-d19994ff34cf" />
<img width="385" height="848" alt="Screenshot 2026-03-21 100346" src="https://github.com/user-attachments/assets/d9a1d119-31af-4e93-8b97-c9b15a198b93" />
<img width="385" height="848" alt="Screenshot 2026-03-21 100346" src="https://github.com/user-attachments/assets/3a87f38b-3352-4a17-97fb-e1e5325f3595" />

