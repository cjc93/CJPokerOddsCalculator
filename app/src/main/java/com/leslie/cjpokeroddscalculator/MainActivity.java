package com.leslie.cjpokeroddscalculator;

import android.os.Bundle;
import android.view.MotionEvent;

import androidx.appcompat.app.AppCompatActivity;
import androidx.datastore.preferences.core.Preferences;
import androidx.datastore.preferences.core.PreferencesKeys;
import androidx.fragment.app.Fragment;
import androidx.navigation.NavController;
import androidx.navigation.NavGraph;
import androidx.navigation.fragment.NavHostFragment;

import com.leslie.cjpokeroddscalculator.databinding.ActivityMainBinding;
import com.leslie.cjpokeroddscalculator.fragment.EquityCalculatorFragment;

import java.util.Objects;

public class MainActivity extends AppCompatActivity {
    public DataStoreSingleton dataStore;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        com.leslie.cjpokeroddscalculator.databinding.ActivityMainBinding binding = ActivityMainBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());

        dataStore = DataStoreSingleton.getInstance(this);

        Preferences.Key<String> START_FRAGMENT_KEY = PreferencesKeys.stringKey("start_fragment");

        String startFragmentStr = dataStore.getDataFromDataStoreIfExist(START_FRAGMENT_KEY);

        NavHostFragment navHostFragment = (NavHostFragment) getSupportFragmentManager().findFragmentById(R.id.nav_host_fragment_content_main);
        assert navHostFragment != null;
        NavController navController = navHostFragment.getNavController();
        NavGraph navGraph = navController.getNavInflater().inflate(R.navigation.nav_graph);

        if (Objects.equals(startFragmentStr, "TexasHoldem")) {
            navGraph.setStartDestination(R.id.TexasHoldemFragment);
        } else if (Objects.equals(startFragmentStr, "OmahaHigh")) {
            navGraph.setStartDestination(R.id.OmahaHighFragment);
        } else if (Objects.equals(startFragmentStr, "OmahaHiLo")) {
            navGraph.setStartDestination(R.id.OmahaHiLoFragment);
        } else {
            navGraph.setStartDestination(R.id.HomeFragment);
        }

        navController.setGraph(navGraph);
    }

    @Override
    public boolean dispatchTouchEvent(MotionEvent ev) {
        NavHostFragment navHostFragment = (NavHostFragment) getSupportFragmentManager().findFragmentById(R.id.nav_host_fragment_content_main);
        assert navHostFragment != null;
        Fragment f = navHostFragment.getChildFragmentManager().getFragments().get(0);
        if (f instanceof EquityCalculatorFragment) {
            EquityCalculatorFragment equityCalculatorFragment = (EquityCalculatorFragment) f;
            equityCalculatorFragment.checkClickToHideCardSelector(ev);
        }
        return super.dispatchTouchEvent(ev);
    }
}