package com.leslie.cjpokeroddscalculator.fragment;

import static com.leslie.cjpokeroddscalculator.GlobalStatic.navControllerNavigate;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;

import com.leslie.cjpokeroddscalculator.R;
import com.leslie.cjpokeroddscalculator.databinding.FragmentHomeBinding;

public class HomeFragment extends Fragment {

    private FragmentHomeBinding binding;

    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        binding = FragmentHomeBinding.inflate(inflater, container, false);
        return binding.getRoot();
    }

    @Override
    public void onViewCreated(@NonNull View view, Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        binding.texasHoldemButton.setOnClickListener(v -> {
            binding.progressBar.setVisibility(View.VISIBLE);
            navControllerNavigate(this, R.id.HomeFragment, R.id.action_HomeFragment_to_TexasHoldemFragment);
        });

        binding.omahaHighButton.setOnClickListener(v -> {
            binding.progressBar.setVisibility(View.VISIBLE);
            navControllerNavigate(this, R.id.HomeFragment, R.id.action_HomeFragment_to_OmahaHighFragment);
        });

        binding.omahaHiLoButton.setOnClickListener(v -> {
            binding.progressBar.setVisibility(View.VISIBLE);
            navControllerNavigate(this, R.id.HomeFragment, R.id.action_HomeFragment_to_OmahaHiLoFragment);
        });

        binding.omahaHi5Button.setOnClickListener(v -> {
            binding.progressBar.setVisibility(View.VISIBLE);
            navControllerNavigate(this, R.id.HomeFragment, R.id.action_HomeFragment_to_OmahaHigh5Fragment);
        });

        binding.omahaHiLo5Button.setOnClickListener(v -> {
            binding.progressBar.setVisibility(View.VISIBLE);
            navControllerNavigate(this, R.id.HomeFragment, R.id.action_HomeFragment_to_OmahaHiLo5Fragment);
        });

        binding.omahaHi6Button.setOnClickListener(v -> {
            binding.progressBar.setVisibility(View.VISIBLE);
            navControllerNavigate(this, R.id.HomeFragment, R.id.action_HomeFragment_to_OmahaHigh6Fragment);
        });

        binding.omahaHiLo6Button.setOnClickListener(v -> {
            binding.progressBar.setVisibility(View.VISIBLE);
            navControllerNavigate(this, R.id.HomeFragment, R.id.action_HomeFragment_to_OmahaHiLo6Fragment);
        });
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        binding = null;
    }
}