package com.example.toursimapp.Adapters;

import android.content.Context;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentPagerAdapter;

import com.example.toursimapp.Fragments.SummaryBookFragment;

@SuppressWarnings("all")
public class SummaryBookFragAdapter extends FragmentPagerAdapter {

    Context context;
    int tabCounts;

    public SummaryBookFragAdapter(@NonNull FragmentManager fm, Context context, int tabCounts) {
        super(fm);
        this.context = context;
        this.tabCounts = tabCounts;
    }

    @NonNull
    @Override
    public Fragment getItem(int position) {
        Bundle bundle = new Bundle();
        switch (position) {
            case 0:
                SummaryBookFragment bookFragment1 = new SummaryBookFragment();
                bundle.putBoolean("summary_frag", true);
                bookFragment1.setArguments(bundle);
                return bookFragment1;
            case 1:
                SummaryBookFragment bookFragment2 = new SummaryBookFragment();
                bundle.putBoolean("summary_frag", false);
                bookFragment2.setArguments(bundle);
                return bookFragment2;
            default:
                return null;
        }
    }

    @Override
    public int getCount() {
        return tabCounts;
    }

}
