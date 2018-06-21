package com.bill.textscroll;

import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;

public class MainActivity extends AppCompatActivity {

    private ImageBottomLayout mBottomLayout;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        mBottomLayout = (ImageBottomLayout) findViewById(R.id.bottom_layout);

        mBottomLayout.setTitle("这是一个Title");
        mBottomLayout.setPage("1", "/5");
        mBottomLayout.setContent(getString(R.string.large_text));
        mBottomLayout.invalidate();
    }
}
