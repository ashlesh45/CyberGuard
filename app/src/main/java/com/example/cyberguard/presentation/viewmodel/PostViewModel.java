package com.example.cyberguard.presentation.viewmodel;

import androidx.lifecycle.LiveData;
import androidx.lifecycle.ViewModel;

import com.example.cyberguard.data.local.AppDatabase;
import com.example.cyberguard.data.local.entity.PostEntity;
import com.example.cyberguard.data.repository.PostRepository;

import java.util.List;

import javax.inject.Inject;

import dagger.hilt.android.lifecycle.HiltViewModel;

@HiltViewModel
public class PostViewModel extends ViewModel {
    private final PostRepository repository;
    private final LiveData<List<PostEntity>> allPosts;

    @Inject
    public PostViewModel(PostRepository repository) {
        this.repository = repository;
        this.allPosts = repository.getAllPosts();
    }

    public LiveData<List<PostEntity>> getAllPosts() {
        return allPosts;
    }

    public void addPost(String author, String content) {
        AppDatabase.databaseWriteExecutor.execute(() -> {
            PostEntity post = new PostEntity(
                String.valueOf(System.currentTimeMillis()),
                author,
                content,
                System.currentTimeMillis()
            );
            repository.insertPost(post);
        });
    }

    public void updatePost(PostEntity post) {
        AppDatabase.databaseWriteExecutor.execute(() -> {
            repository.updatePost(post);
        });
    }

    public void deletePost(PostEntity post) {
        AppDatabase.databaseWriteExecutor.execute(() -> {
            repository.deletePost(post);
        });
    }
}
