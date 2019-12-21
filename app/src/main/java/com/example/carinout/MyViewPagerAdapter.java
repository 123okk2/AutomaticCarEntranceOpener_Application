package com.example.carinout;

import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentPagerAdapter;

public class MyViewPagerAdapter extends FragmentPagerAdapter {  // Tab Captions//
    private String tabCaption[] = new String[]{"차량 입출입 내역", "허가 차량 관리"};

    public MyViewPagerAdapter(FragmentManager fragmentManager) {
        super(fragmentManager);
    }

    @Override
    public int getCount() {
        return tabCaption.length;
    }

    @Override
    public Fragment getItem(int position) {
        switch (position) {
            case 0:
                return new CarLog();
            case 1:
                return new CarPermission();
        }
        return null;
    }

    @Override
    public CharSequence getPageTitle(int position) {
        return tabCaption[position];  // return tab caption  }
    }
}