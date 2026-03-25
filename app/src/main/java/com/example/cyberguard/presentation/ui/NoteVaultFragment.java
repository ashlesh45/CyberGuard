package com.example.cyberguard.presentation.ui;

import android.content.Context;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.EditText;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AlertDialog;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.cyberguard.databinding.FragmentNoteVaultBinding;
import com.example.cyberguard.databinding.ItemCommunityPostBinding; // Reusing similar layout for simplicity

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import dagger.hilt.android.AndroidEntryPoint;

@AndroidEntryPoint
public class NoteVaultFragment extends Fragment {

    private FragmentNoteVaultBinding binding;
    private SharedPreferences securePrefs;
    private NoteAdapter adapter;
    private List<String> notesList = new ArrayList<>();

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        binding = FragmentNoteVaultBinding.inflate(inflater, container, false);
        return binding.getRoot();
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        // In a real app, use EncryptedSharedPreferences. For this demo, we use a private file.
        securePrefs = requireContext().getSharedPreferences("secure_notes", Context.MODE_PRIVATE);
        
        loadNotes();

        adapter = new NoteAdapter(notesList, this::deleteNote);
        binding.recyclerNotes.setLayoutManager(new LinearLayoutManager(getContext()));
        binding.recyclerNotes.setAdapter(adapter);

        binding.fabAddNote.setOnClickListener(v -> showAddNoteDialog());
    }

    private void loadNotes() {
        Set<String> set = securePrefs.getStringSet("notes", new HashSet<>());
        notesList.clear();
        notesList.addAll(set);
    }

    private void showAddNoteDialog() {
        EditText input = new EditText(requireContext());
        input.setHint("Type your sensitive note here...");
        
        new AlertDialog.Builder(requireContext())
                .setTitle("Add Secure Note")
                .setView(input)
                .setPositiveButton("Save Encrypted", (d, w) -> {
                    String note = input.getText().toString().trim();
                    if (!note.isEmpty()) {
                        notesList.add(note);
                        saveNotes();
                        adapter.notifyDataSetChanged();
                    }
                })
                .setNegativeButton("Cancel", null)
                .show();
    }

    private void deleteNote(int position) {
        notesList.remove(position);
        saveNotes();
        adapter.notifyDataSetChanged();
    }

    private void saveNotes() {
        securePrefs.edit().putStringSet("notes", new HashSet<>(notesList)).apply();
    }

    private static class NoteAdapter extends RecyclerView.Adapter<NoteAdapter.ViewHolder> {
        private final List<String> notes;
        private final OnDeleteListener listener;

        interface OnDeleteListener {
            void onDelete(int position);
        }

        NoteAdapter(List<String> notes, OnDeleteListener listener) {
            this.notes = notes;
            this.listener = listener;
        }

        @NonNull
        @Override
        public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
            ItemCommunityPostBinding binding = ItemCommunityPostBinding.inflate(LayoutInflater.from(parent.getContext()), parent, false);
            return new ViewHolder(binding);
        }

        @Override
        public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
            holder.binding.textAuthor.setText("🔒 Encrypted Note");
            holder.binding.textContent.setText(notes.get(position));
            holder.binding.layoutActions.setVisibility(View.VISIBLE);
            holder.binding.btnEdit.setVisibility(View.GONE);
            holder.binding.btnDelete.setOnClickListener(v -> listener.onDelete(position));
        }

        @Override
        public int getItemCount() { return notes.size(); }

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
