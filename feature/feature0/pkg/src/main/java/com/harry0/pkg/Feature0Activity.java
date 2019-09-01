package com.harry0.pkg;

import android.os.Bundle;
import android.view.View;

import androidx.annotation.Nullable;

import com.blankj.utilcode.util.ApiUtils;
import com.blankj.utilcode.util.ToastUtils;
import com.harry.auccommon.CommonTitleActivity;
import com.harry1.export.Feature1Api;
import com.harry1.export.Feature1Param;
import com.harry1.export.Feature1Result;


public class Feature0Activity extends CommonTitleActivity {

    @Override
    public CharSequence bindTitle() {
        return getString(R.string.feature0_title);
    }

    @Override
    public boolean isSwipeBack() {
        return true;
    }

    @Override
    public int bindLayout() {
        return R.layout.feature0_activity;
    }

    @Override
    public void initView(@Nullable Bundle savedInstanceState, @Nullable View contentView) {
        findViewById(R.id.start1btn).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Feature1Result result = ApiUtils.getApi(Feature1Api.class)
                        .startFeature1Activity(Feature0Activity.this, new Feature1Param("Feature1Param"));
                ToastUtils.showLong(result.getName());
            }
        });
    }


    @Override
    public void doBusiness() {

    }
}
