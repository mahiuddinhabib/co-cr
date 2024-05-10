package com.habib.cocr.ui.notices;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.ViewModelProvider;

import com.habib.cocr.MainActivity;
import com.habib.cocr.R;
import com.habib.cocr.databinding.FragmentNoticesBinding;

public class NoticesFragment extends Fragment {

    private FragmentNoticesBinding binding;

    public View onCreateView(@NonNull LayoutInflater inflater,
                             ViewGroup container, Bundle savedInstanceState) {
        // Set the title of the ActionBar
        MainActivity.actionBarTitle.setText(R.string.title_notices);

        NoticesViewModel noticesViewModel =
                new ViewModelProvider(this).get(NoticesViewModel.class);

        binding = FragmentNoticesBinding.inflate(inflater, container, false);
        View root = binding.getRoot();

        final TextView textView = binding.textNotices;
        noticesViewModel.getText().observe(getViewLifecycleOwner(), textView::setText);
        return root;
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        binding = null;
    }
}