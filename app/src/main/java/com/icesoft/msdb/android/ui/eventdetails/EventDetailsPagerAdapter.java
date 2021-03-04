package com.icesoft.msdb.android.ui.eventdetails;

import android.content.Context;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.annotation.StringRes;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentPagerAdapter;

import com.icesoft.msdb.android.R;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

/**
 * A [FragmentPagerAdapter] that returns a fragment corresponding to
 * one of the sections/tabs/pages.
 */
public class EventDetailsPagerAdapter extends FragmentPagerAdapter {

    private String infoFragmentTitle;
    private Fragment infoFragment;

    private String participantsFragmentTitle;
    private Fragment participantsFragment;

    public EventDetailsPagerAdapter(@NonNull FragmentManager fm) {
        super(fm, FragmentPagerAdapter.BEHAVIOR_RESUME_ONLY_CURRENT_FRAGMENT);
    }

    public void addInfoFragment(String tabTitle, Fragment fragment) {
        this.infoFragment = fragment;
        this.infoFragmentTitle = tabTitle;
    }

    public void addParticipantsFragment(String tabTitle, Fragment fragment) {
        this.participantsFragment = fragment;
        this.participantsFragmentTitle = tabTitle;
    }

    @Override
    public Fragment getItem(int position) {
        // getItem is called to instantiate the fragment for the given page.
        // Return a PlaceholderFragment (defined as a static inner class below).
        if (position == 0) {
            return infoFragment;
        }
        return participantsFragment;
    }

    @Nullable
    @Override
    public CharSequence getPageTitle(int position) {
        if (position == 0) {
            return infoFragmentTitle;
        }
        return participantsFragmentTitle;
    }

    @Override
    public int getCount() {
        return 2;
    }
}