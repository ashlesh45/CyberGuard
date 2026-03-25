package com.example.cyberguard.presentation.ui;

import android.content.Context;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.EditText;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AlertDialog;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.ViewModelProvider;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.cyberguard.data.local.entity.PostEntity;
import com.example.cyberguard.databinding.FragmentCommunityBinding;
import com.example.cyberguard.databinding.ItemCommunityPostBinding;
import com.example.cyberguard.presentation.viewmodel.PostViewModel;

import java.util.ArrayList;
import java.util.List;

import dagger.hilt.android.AndroidEntryPoint;

@AndroidEntryPoint
public class CommunityFragment extends Fragment {

    private FragmentCommunityBinding binding;
    private PostViewModel viewModel;
    private PostAdapter adapter;
    private String currentUsername;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        binding = FragmentCommunityBinding.inflate(inflater, container, false);
        return binding.getRoot();
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        viewModel = new ViewModelProvider(this).get(PostViewModel.class);
        
        SharedPreferences prefs = requireContext().getSharedPreferences("cyberguard_prefs", Context.MODE_PRIVATE);
        currentUsername = prefs.getString("username", "Anonymous");

        adapter = new PostAdapter(currentUsername, new PostAdapter.OnPostActionListener() {
            @Override
            public void onEdit(PostEntity post) {
                showEditDialog(post);
            }

            @Override
            public void onDelete(PostEntity post) {
                showDeleteConfirmation(post);
            }
        });
        
        binding.recyclerCommunity.setLayoutManager(new LinearLayoutManager(getContext()));
        binding.recyclerCommunity.setAdapter(adapter);

        viewModel.getAllPosts().observe(getViewLifecycleOwner(), posts -> {
            if (posts != null) {
                adapter.setPosts(posts);
            }
        });

        binding.btnPost.setOnClickListener(v -> {
            String content = binding.editPostContent.getText().toString().trim();
            if (!content.isEmpty()) {
                viewModel.addPost(currentUsername, content);
                binding.editPostContent.setText("");
                Toast.makeText(getContext(), "Post shared!", Toast.LENGTH_SHORT).show();
            } else {
                binding.editPostContent.setError("Please enter your message");
            }
        });
    }

    private void showEditDialog(PostEntity post) {
        EditText editText = new EditText(getContext());
        editText.setText(post.getContent());
        new AlertDialog.Builder(requireContext())
                .setTitle("Edit Post")
                .setView(editText)
                .setPositiveButton("Update", (dialog, which) -> {
                    String newContent = editText.getText().toString().trim();
                    if (!newContent.isEmpty()) {
                        PostEntity updatedPost = new PostEntity(post.getId(), post.getAuthor(), newContent, post.getTimestamp());
                        viewModel.updatePost(updatedPost);
                    }
                })
                .setNegativeButton("Cancel", null)
                .show();
    }

    private void showDeleteConfirmation(PostEntity post) {
        new AlertDialog.Builder(requireContext())
                .setTitle("Delete Post")
                .setMessage("Are you sure you want to delete this post?")
                .setPositiveButton("Delete", (dialog, which) -> viewModel.deletePost(post))
                .setNegativeButton("Cancel", null)
                .show();
    }

    private static class PostAdapter extends RecyclerView.Adapter<PostAdapter.ViewHolder> {
        private List<PostEntity> posts = new ArrayList<>();
        private final String currentUsername;
        private final OnPostActionListener listener;

        interface OnPostActionListener {
            void onEdit(PostEntity post);
            void onDelete(PostEntity post);
        }

        PostAdapter(String currentUsername, OnPostActionListener listener) {
            this.currentUsername = currentUsername;
            this.listener = listener;
        }

        public void setPosts(List<PostEntity> posts) {
            this.posts = posts;
            notifyDataSetChanged();
        }

        @NonNull
        @Override
        public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
            ItemCommunityPostBinding binding = ItemCommunityPostBinding.inflate(LayoutInflater.from(parent.getContext()), parent, false);
            return new ViewHolder(binding);
        }

        @Override
        public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
            PostEntity post = posts.get(position);
            holder.binding.textAuthor.setText(post.getAuthor());
            holder.binding.textContent.setText(post.getContent());

            if (post.getAuthor().equals(currentUsername)) {
                holder.binding.layoutActions.setVisibility(View.VISIBLE);
                holder.binding.btnEdit.setOnClickListener(v -> listener.onEdit(post));
                holder.binding.btnDelete.setOnClickListener(v -> listener.onDelete(post));
            } else {
                holder.binding.layoutActions.setVisibility(View.GONE);
            }
        }

        @Override
        public int getItemCount() { return posts.size(); }

        static class ViewHolder extends RecyclerView.ViewHolder {
            final ItemCommunityPostBinding binding;
            ViewHolder(ItemCommunityPostBinding binding) {
                super(binding.getRoot());
                this.binding = binding;
            }
        }
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        binding = null;
    }
}
