package com.example.cyberguard.presentation.viewmodel;

import androidx.lifecycle.LiveData;
import androidx.lifecycle.ViewModel;

import com.example.cyberguard.data.repository.FraudRepository;
import com.example.cyberguard.domain.model.FraudType;

import java.util.List;

import javax.inject.Inject;

import dagger.hilt.android.lifecycle.HiltViewModel;

@HiltViewModel
public class FraudViewModel extends ViewModel {
    private final FraudRepository repository;
    private final LiveData<List<FraudType>> allFraudTypes;

    @Inject
    public FraudViewModel(FraudRepository repository) {
        this.repository = repository;
        allFraudTypes = repository.getAllFraudTypes();
    }

    public LiveData<List<FraudType>> getAllFraudTypes() {
        return allFraudTypes;
    }

    public void init() {
        repository.insertInitialData();
    }
}
