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
                MainActivity.Diary_Write create1 = new MainActivity.Diary_Write();
                return create1;
            case 1:
                MainActivity.Diary_List view3 = new MainActivity.Diary_List();
                return view3;
            case 2:
                MainActivity.Diary_Setting setting2 = new MainActivity.Diary_Setting();
                return setting2;
            default:
                return null;
        }
    }

    @Override
    public int getCount() {
        return tabCount;
    }
}
