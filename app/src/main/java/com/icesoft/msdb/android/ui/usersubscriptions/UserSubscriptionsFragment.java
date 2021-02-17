package com.icesoft.msdb.android.ui.usersubscriptions;

import androidx.lifecycle.ViewModelProvider;

import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.RecyclerView;

import android.text.Editable;
import android.text.TextWatcher;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.EditText;

import com.icesoft.msdb.android.R;

public class UserSubscriptionsFragment extends Fragment {

    private UserSubscriptionsViewModel mViewModel;

    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container,
                             @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_user_subscriptions_list, container, false);

        mViewModel = new ViewModelProvider(requireActivity()).get(UserSubscriptionsViewModel.class);
        RecyclerView.Adapter adapter = new UserSubscriptionsRecyclerViewAdapter(getContext());
        // Set the adapter
        RecyclerView recyclerView = view.findViewById(R.id.userSubscriptionsRecyclerView);
        mViewModel.getMutableUserSubscriptions().observe(getViewLifecycleOwner(), userSubscriptions -> {
            recyclerView.setAdapter(adapter);
        });

        ((EditText) view.findViewById(R.id.filterEditText)).addTextChangedListener(new TextWatcher() {
            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
            }

            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {
            }

            @Override
            public void afterTextChanged(Editable s) {
                String filter = s.toString();
                mViewModel.filterUserSubscriptions(filter);

                adapter.notifyDataSetChanged();
            }
        });

        return view;
    }

}