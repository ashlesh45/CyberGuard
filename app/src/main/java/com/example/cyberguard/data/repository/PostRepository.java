package com.example.cyberguard.data.repository;

import androidx.lifecycle.LiveData;

import com.example.cyberguard.data.local.dao.PostDao;
import com.example.cyberguard.data.local.entity.PostEntity;

import java.util.List;

import javax.inject.Inject;
import javax.inject.Singleton;

@Singleton
public class PostRepository {
    private final PostDao postDao;

    @Inject
    public PostRepository(PostDao postDao) {
        this.postDao = postDao;
    }

    public LiveData<List<PostEntity>> getAllPosts() {
        return postDao.getAllPosts();
    }

    public void insertPost(PostEntity post) {
        postDao.insert(post);
    }

    public void updatePost(PostEntity post) {
        postDao.update(post);
    }

    public void deletePost(PostEntity post) {
        postDao.delete(post);
    }
}
