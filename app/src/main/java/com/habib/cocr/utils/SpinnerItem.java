package com.habib.cocr.utils;

import androidx.annotation.NonNull;

public class SpinnerItem {
    private String value;
    private String name;

    public SpinnerItem(String value, String name) {
        this.value = value;
        this.name = name;
    }

    public String getValue() {
        return value;
    }

    public String getName() {
        return name;
    }

    // This is what the spinner will display
    @NonNull
    @Override
    public String toString() {
        return name;
    }
}