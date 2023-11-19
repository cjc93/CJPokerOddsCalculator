package com.leslie.cjpokeroddscalculator;
import android.os.Bundle;
import android.view.MotionEvent;

import androidx.appcompat.app.AppCompatActivity;
import androidx.navigation.fragment.NavHostFragment;

import com.leslie.cjpokeroddscalculator.databinding.ActivityMainBinding;

public class MainActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        com.leslie.cjpokeroddscalculator.databinding.ActivityMainBinding binding = ActivityMainBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());
    }

    @Override
    public boolean dispatchTouchEvent(MotionEvent ev) {
        NavHostFragment navHostFragment = (NavHostFragment) getSupportFragmentManager().findFragmentById(R.id.nav_host_fragment_content_main);
        assert navHostFragment != null;
        TexasHoldemFragment texasHoldemFragment = (TexasHoldemFragment) navHostFragment.getChildFragmentManager().getFragments().get(0);
        texasHoldemFragment.hideCardSelector(ev);
        return super.dispatchTouchEvent(ev);
    }
}