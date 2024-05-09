package com.habib.cocr.ui.notices;

import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;
import androidx.lifecycle.ViewModel;

public class NoticesViewModel extends ViewModel {

    private final MutableLiveData<String> mText;

    public NoticesViewModel() {
        mText = new MutableLiveData<>();
        mText.setValue("This is notices fragment");
    }

    public LiveData<String> getText() {
        return mText;
    }
}