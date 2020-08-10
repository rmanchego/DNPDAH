package com.practica02.proyectonpa.ui.main;

import android.content.Context;

import androidx.annotation.Nullable;
import androidx.annotation.StringRes;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentPagerAdapter;

import com.practica02.proyectonpa.Fragments.Fragment_Fotos;
import com.practica02.proyectonpa.Fragments.Fragment_Pasos;
import com.practica02.proyectonpa.Fragments.Fragment_CodigoQR;
import com.practica02.proyectonpa.Fragments.Fragment_Sensores;
import com.practica02.proyectonpa.R;

/**
 * A [FragmentPagerAdapter] that returns a fragment corresponding to
 * one of the sections/tabs/pages.
 */
public class SectionsPagerAdapter extends FragmentPagerAdapter {

    @StringRes
    private static final int[] TAB_TITLES = new int[]{R.string.tab_text_1, R.string.tab_text_3,R.string.tab_text_4, R.string.tab_text_5,R.string.tab_text_2};
    private final Context mContext;

    public SectionsPagerAdapter(Context context, FragmentManager fm) {
        super(fm);
        mContext = context;
    }

    @Override
    public Fragment getItem(int position) {
        // getItem is called to instantiate the fragment for the given page.
        // Return a PlaceholderFragment (defined as a static inner class below).
        //return PlaceholderFragment.newInstance(position + 1);
        Fragment fragment = null;
        switch (position){
            case 0:
                fragment = new Fragment_Fotos();
                break;
            case 1:
                fragment = new Fragment_Pasos();
                break;
            case 2:
                fragment = new Fragment_CodigoQR();
                break;
            case 3:
                fragment = new Fragment_Sensores();
                break;
        }
        return fragment;
    }

    @Nullable
    @Override
    public CharSequence getPageTitle(int position) {
        return mContext.getResources().getString(TAB_TITLES[position]);
    }

    @Override
    public int getCount() {
        // Show 4 total pages.
        return 4;
    }

}