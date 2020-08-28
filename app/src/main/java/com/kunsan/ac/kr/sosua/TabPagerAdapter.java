package com.kunsan.ac.kr.sosua;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentStatePagerAdapter;

public class TabPagerAdapter extends FragmentStatePagerAdapter {
    private int tabCount;

    TabPagerAdapter(FragmentManager fm, int tabCount){
        super(fm);
        this.tabCount = tabCount;
    }

    @NonNull
    @Override
    public Fragment getItem(int position) {
        //Returning the current tabs
        switch(position){
            case 0:
                Diary_Write create = new Diary_Write();
                return create;
            case 1:
                Diary_List view = new Diary_List();
                return view;
            case 2:
                Diary_Setting setting = new Diary_Setting();
                return setting;
            default:
                return null;
        }
    }

    @Override
    public int getCount() {
        return tabCount;
    }
}
