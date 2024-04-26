package com.leslie.cjpokeroddscalculator;

import android.content.Context;

import androidx.datastore.preferences.core.MutablePreferences;
import androidx.datastore.preferences.core.Preferences;
import androidx.datastore.preferences.rxjava3.RxPreferenceDataStoreBuilder;
import androidx.datastore.rxjava3.RxDataStore;

import io.reactivex.rxjava3.core.Single;

public class DataStoreSingleton {
    private static DataStoreSingleton instance = null;
    RxDataStore<Preferences> dataStore;

    private DataStoreSingleton() { }

    public static DataStoreSingleton getInstance(Context ctx) {
        if (instance == null) {
            instance = new DataStoreSingleton();
            instance.dataStore = new RxPreferenceDataStoreBuilder(ctx, "general").build();
        }
        return instance;
    }

    public void writeToDataStore(Preferences.Key<String> key, String value) {
        dataStore.updateDataAsync(prefsIn -> {
            MutablePreferences mutablePreferences = prefsIn.toMutablePreferences();
            mutablePreferences.set(key, value);
            return Single.just(mutablePreferences);
        });
    }

    public String getDataFromDataStoreIfExist(Preferences.Key<String> key) {
        if (dataStore.data().map(prefs -> prefs.contains(key)).blockingFirst()) {
            return dataStore.data().map(prefs -> prefs.get(key)).blockingFirst();
        } else {
            return null;
        }
    }

    public void deleteKeyFromDataStore(Preferences.Key<String> key) {
        dataStore.updateDataAsync(prefsIn -> {
            MutablePreferences mutablePreferences = prefsIn.toMutablePreferences();
            mutablePreferences.remove(key);
            return Single.just(mutablePreferences);
        });
    }
}
