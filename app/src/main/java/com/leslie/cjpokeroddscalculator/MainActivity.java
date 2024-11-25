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

        int startFragmentId = getFragmentId(startFragmentStr);

        navGraph.setStartDestination(startFragmentId);

        navController.setGraph(navGraph);
    }

    private int getFragmentId(String startFragmentStr) {
        int startFragmentId;

        if (Objects.equals(startFragmentStr, "TexasHoldem")) {
            startFragmentId = R.id.TexasHoldemFragment;
        } else if (Objects.equals(startFragmentStr, "OmahaHigh")) {
            startFragmentId = R.id.OmahaHighFragment;
        } else if (Objects.equals(startFragmentStr, "OmahaHiLo")) {
            startFragmentId = R.id.OmahaHiLoFragment;
        } else if (Objects.equals(startFragmentStr, "OmahaHigh5")) {
            startFragmentId = R.id.OmahaHigh5Fragment;
        } else if (Objects.equals(startFragmentStr, "OmahaHiLo5")) {
            startFragmentId = R.id.OmahaHiLo5Fragment;
        } else {
            startFragmentId = R.id.HomeFragment;
        }
        return startFragmentId;
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