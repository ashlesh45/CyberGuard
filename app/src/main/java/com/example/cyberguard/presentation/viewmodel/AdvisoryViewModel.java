package com.example.cyberguard.presentation.viewmodel;

import androidx.lifecycle.LiveData;
import androidx.lifecycle.ViewModel;

import com.example.cyberguard.data.repository.AdvisoryRepository;
import com.example.cyberguard.domain.model.Advisory;

import java.util.List;

import javax.inject.Inject;

import dagger.hilt.android.lifecycle.HiltViewModel;

@HiltViewModel
public class AdvisoryViewModel extends ViewModel {
    private final AdvisoryRepository repository;
    private final LiveData<List<Advisory>> allAdvisories;

    @Inject
    public AdvisoryViewModel(AdvisoryRepository repository) {
        this.repository = repository;
        allAdvisories = repository.getAllAdvisories();
    }

    public LiveData<List<Advisory>> getAllAdvisories() {
        return allAdvisories;
    }

    public void refresh() {
        repository.refreshAdvisories();
    }
}
