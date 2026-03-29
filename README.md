# CyberGuard: Your Digital Safety Shield

## Overview

CyberGuard is a comprehensive Android application designed to enhance personal digital security and cybersecurity awareness. The application provides tools for real-time threat detection along with educational resources, enabling users to safely navigate the digital environment.

---

## Project Description

CyberGuard functions as a proactive security assistant. It leverages modern Android development practices and machine learning techniques to identify potential risks and deliver timely cybersecurity insights.

---

## Core Features

### Intelligent QR Analysis

* Uses Google ML Kit and CameraX to scan QR codes
* Performs heuristic analysis on URLs
* Detects suspicious links such as:

  * Non-HTTPS URLs
  * URL shorteners commonly used in phishing

### Security Advisories

* Provides a real-time feed of cybersecurity alerts
* Keeps users informed about:

  * Data breaches
  * Emerging threats

### Cybersecurity Knowledge Base

* Offers structured educational content
* Helps users understand and mitigate digital risks

### Knowledge Verification

* Includes quiz modules
* Reinforces cybersecurity awareness

### Biometric Authentication

* Secures access using biometric verification
* Protects sensitive application data

---

## Technical Architecture

The application follows the MVVM (Model-View-ViewModel) architecture for scalability and maintainability.

### Presentation Layer

* Built using Fragments and View Binding
* Uses LiveData and ViewModel for UI state management

### Data Layer

* Implements Repository pattern
* Local storage: Room Database
* Remote data: Retrofit

### Dependency Injection

* Hilt (Dagger) for efficient dependency management

---

## Tech Stack

* Platform: Android (Java/Kotlin)
* Machine Learning: Google ML Kit
* Camera: CameraX
* Networking: Retrofit with GSON
* Database: Room
* Dependency Injection: Hilt (Dagger)
* UI: Material Design 3, ConstraintLayout
* Navigation: Jetpack Navigation Component
* Security: Android Biometric Library
* Image Loading: Glide

---

## Screenshots

<p align="center">
  <img src="https://github.com/user-attachments/assets/8972bc3c-2c8e-4e8c-a1ba-0696299df9df" width="250"/>
  <img src="https://github.com/user-attachments/assets/95712eb0-2ca4-4435-8a69-4f2603006fec" width="250"/>
  <img src="https://github.com/user-attachments/assets/a2f77129-cdd6-47e4-a915-302edd1bc166" width="250"/>
</p>

<p align="center">
  <img src="https://github.com/user-attachments/assets/d792b459-284f-48fd-8de0-c37a54c1a781" width="250"/>
  <img src="https://github.com/user-attachments/assets/eed88d00-6f87-42a6-81c9-d19994ff34cf" width="250"/>
  <img src="https://github.com/user-attachments/assets/d9a1d119-31af-4e93-8b97-c9b15a198b93" width="250"/>
</p>

<p align="center">
  <img src="https://github.com/user-attachments/assets/3a87f38b-3352-4a17-97fb-e1e5325f3595" width="250"/>
</p>

---

## Future Enhancements

* Integration of advanced threat detection models
* Real-time phishing detection improvements
* Cloud-based alert synchronization
* Enhanced UI/UX

---

## Conclusion

CyberGuard provides a unified platform for improving digital safety through real-time analysis and user education. The project demonstrates practical implementation of Android architecture, machine learning integration, and security-focused design.

---

## Author

M Ashlesh Mallya


