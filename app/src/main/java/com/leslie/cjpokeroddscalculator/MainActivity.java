package com.leslie.cjpokeroddscalculator;

import android.os.Bundle;

import androidx.appcompat.app.AppCompatActivity;
import androidx.datastore.preferences.core.Preferences;
import androidx.datastore.preferences.core.PreferencesKeys;
import androidx.navigation.NavController;
import androidx.navigation.NavGraph;
import androidx.navigation.fragment.NavHostFragment;

import com.leslie.cjpokeroddscalculator.databinding.ActivityMainBinding;
import com.leslie.cjpokeroddscalculator.util.DataStoreSingleton;

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

        Bundle startArgs = getStartArgs(startFragmentStr);

        navController.setGraph(navGraph, startArgs);
    }

    private int getFragmentId(String startFragmentStr) {
        int startFragmentId;

        if (Objects.equals(startFragmentStr, "TexasHoldem")) {
            startFragmentId = R.id.TexasHoldemFragment;
        } else if (Objects.equals(startFragmentStr, "OmahaHigh") || Objects.equals(startFragmentStr, "OmahaHigh5") || Objects.equals(startFragmentStr, "OmahaHigh6")) {
            startFragmentId = R.id.OmahaHighFragment;
        } else if (Objects.equals(startFragmentStr, "OmahaHiLo") || Objects.equals(startFragmentStr, "OmahaHiLo5") || Objects.equals(startFragmentStr, "OmahaHiLo6")) {
            startFragmentId = R.id.OmahaHiLoFragment;
        } else {
            startFragmentId = R.id.TexasHoldemFragment;
        }
        return startFragmentId;
    }

    private Bundle getStartArgs(String startFragmentStr) {
        Bundle args = new Bundle();
        if (Objects.equals(startFragmentStr, "OmahaHigh5") || Objects.equals(startFragmentStr, "OmahaHiLo5")) {
            args.putInt("cardsPerHand", 5);
        } else if (Objects.equals(startFragmentStr, "OmahaHigh6") || Objects.equals(startFragmentStr, "OmahaHiLo6")) {
            args.putInt("cardsPerHand", 6);
        } else if (Objects.equals(startFragmentStr, "OmahaHigh") || Objects.equals(startFragmentStr, "OmahaHiLo")) {
            args.putInt("cardsPerHand", 4);
        }
        return args;
    }
}