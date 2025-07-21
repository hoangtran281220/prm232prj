package com.example.prm232rj.utils;

import android.content.Context;
import android.content.SharedPreferences;

import java.util.HashSet;
import java.util.List;
import java.util.Set;

public class FilterPrefManager {
    private static final String PREF_NAME = "comic_filter_prefs";
    private static final String KEY_TAGS = "filter_tags";
    private static final String KEY_SORT = "filter_sort";
    private static final String KEY_STATUS = "filter_status";

    private final SharedPreferences prefs;

    public FilterPrefManager(Context context) {
        prefs = context.getSharedPreferences(PREF_NAME, Context.MODE_PRIVATE);
    }

    public void saveFilters(List<String> tagIds, String sort, String status) {
        SharedPreferences.Editor editor = prefs.edit();
        editor.putStringSet(KEY_TAGS, new HashSet<>(tagIds));
        editor.putString(KEY_SORT, sort);
        editor.putString(KEY_STATUS, status);
        editor.apply();
    }

    public Set<String> getSavedTagIds() {
        return prefs.getStringSet(KEY_TAGS, new HashSet<>());
    }

    public String getSavedSort() {
        return prefs.getString(KEY_SORT, "UpdatedAt");
    }

    public String getSavedStatus() {
        return prefs.getString(KEY_STATUS, "all");
    }

    public void clearFilters() {
        prefs.edit().clear().apply();
    }
}
