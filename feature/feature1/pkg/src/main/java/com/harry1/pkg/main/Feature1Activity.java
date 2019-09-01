package com.harry1.pkg.main;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;

import android.view.View;

import androidx.annotation.Nullable;


import com.harry.auccommon.CommonTitleActivity;
import com.harry1.pkg.R;


public class Feature1Activity extends CommonTitleActivity {

    @Override
    public CharSequence bindTitle() {
        return getString(R.string.feature1_title);
    }

    @Override
    public boolean isSwipeBack() {
        return true;
    }

    @Override
    public int bindLayout() {
        return R.layout.feature1_activity;
    }

    @Override
    public void initView(@Nullable Bundle savedInstanceState, @Nullable View contentView) {

    }

    @Override
    public void doBusiness() {

    }
    public static void start(Context context) {
        context.startActivity(new Intent(context,Feature1Activity.class));

    }
}
