package com.feature.mock;

import android.content.Context;

import com.blankj.utilcode.util.ApiUtils;
import com.harry1.export.Feature1Api;
import com.harry1.export.Feature1Param;
import com.harry1.export.Feature1Result;

@ApiUtils.Api(isMock = true)
public class Feature1ApiMock extends Feature1Api {
    @Override
    public Feature1Result startFeature1Activity(Context context, Feature1Param param) {
        return new Feature1Result("Mock Result");
    }
}