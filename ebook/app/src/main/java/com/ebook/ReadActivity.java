package com.ebook;

import android.content.Context;
import android.content.Intent;
import androidx.fragment.app.Fragment;
import android.view.WindowManager;


public class ReadActivity extends FragmentActivity {
    public static final String EXTRA_BOOK_ID = "EXTRA_BOOK_ID";

    public static Intent newIntent(Context context, int bookId) {
        Intent intent = new Intent(context, ReadActivity.class);
        //将要传递的值附加到Intent对象
        intent.putExtra(EXTRA_BOOK_ID, bookId);
        return intent;
    }

    @Override
    protected void setScreen() {
        getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN,
                WindowManager.LayoutParams.FLAG_FULLSCREEN);
    }


    @Override
    protected Fragment createFragment() {
        int bookId = getIntent().getIntExtra(EXTRA_BOOK_ID, 0);
        return ReadFragment.newInstance(bookId);
    }

}

