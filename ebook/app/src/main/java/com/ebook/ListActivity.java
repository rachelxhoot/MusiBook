package com.ebook;

import androidx.fragment.app.Fragment;

public class ListActivity extends FragmentActivity {
    @Override
    protected Fragment createFragment() {
        return new ListFragment();
    }

}
