package com.example.cyberguard.presentation.viewmodel;

import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;
import androidx.lifecycle.ViewModel;

import com.example.cyberguard.data.local.AppDatabase;
import com.example.cyberguard.data.local.entity.UserEntity;
import com.example.cyberguard.data.repository.UserRepository;

import javax.inject.Inject;

import dagger.hilt.android.lifecycle.HiltViewModel;

@HiltViewModel
public class AuthViewModel extends ViewModel {
    private final UserRepository userRepository;
    private final MutableLiveData<UserEntity> currentUser = new MutableLiveData<>();
    private final MutableLiveData<String> authError = new MutableLiveData<>();
    private final MutableLiveData<Boolean> signupSuccess = new MutableLiveData<>();

    @Inject
    public AuthViewModel(UserRepository userRepository) {
        this.userRepository = userRepository;
    }

    public LiveData<UserEntity> getCurrentUser() { return currentUser; }
    public LiveData<String> getAuthError() { return authError; }
    public LiveData<Boolean> getSignupSuccess() { return signupSuccess; }

    public void login(String username, String password) {
        AppDatabase.databaseWriteExecutor.execute(() -> {
            UserEntity user = userRepository.login(username, password);
            if (user != null) {
                currentUser.postValue(user);
            } else {
                authError.postValue("Check the login details again.");
            }
        });
    }

    public void signup(String username, String password) {
        AppDatabase.databaseWriteExecutor.execute(() -> {
            boolean success = userRepository.signup(username, password);
            if (success) {
                signupSuccess.postValue(true);
            } else {
                authError.postValue("Username already exists.");
            }
        });
    }
}
