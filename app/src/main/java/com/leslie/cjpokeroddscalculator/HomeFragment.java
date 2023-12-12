package com.leslie.cjpokeroddscalculator;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.navigation.fragment.NavHostFragment;

import com.leslie.cjpokeroddscalculator.databinding.FragmentHomeBinding;

public class HomeFragment extends Fragment {

    private FragmentHomeBinding binding;

    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        binding = FragmentHomeBinding.inflate(inflater, container, false);
        return binding.getRoot();
    }

    public void onViewCreated(@NonNull View view, Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        binding.texasHoldemButton.setOnClickListener(v -> {
            binding.progressBar.setVisibility(View.VISIBLE);
            NavHostFragment.findNavController(HomeFragment.this).navigate(R.id.action_HomeFragment_to_TexasHoldemFragment);
        });

        binding.omahaHighButton.setOnClickListener(v -> {
            binding.progressBar.setVisibility(View.VISIBLE);
            NavHostFragment.findNavController(HomeFragment.this).navigate(R.id.action_HomeFragment_to_OmahaHighFragment);
        });
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        binding = null;
    }
}