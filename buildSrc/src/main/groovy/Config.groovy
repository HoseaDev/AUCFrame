

class Config{
    static applicationId='com.harry.aucframe'
    static  appName='AucFrame'

    static compileSdkVersion = 28                           // TODO: MODIFY
    static minSdkVersion = 21                               // TODO: MODIFY
    static targetSdkVersion = 28                            // TODO: MODIFY
    static versionCode = 1_000_000                          // TODO: MODIFY
    static versionName = '1.0.0'// E.g. 1.9.72 => 1,009,072 // TODO: MODIFY

    static kotlin_version = '1.3.10'
    static support_version = '27.1.1'
    static leakcanary_version = '1.6.3'

//    static depConfig = [
//            plugin       : [
//                    gradle: "com.android.tools.build:gradle:3.3.0",
//                    kotlin: "org.jetbrains.kotlin:kotlin-gradle-plugin:$kotlin_version",
//            ],
//            support      : [
//                    appcompat_v7: "com.android.support:appcompat-v7:$support_version",
//                    design      : "com.android.support:design:$support_version",
//                    multidex    : "com.android.support:multidex:1.0.2",
//                    constraint  : "com.android.support.constraint:constraint-layout:1.1.3",
//            ],
//            kotlin       : "org.jetbrains.kotlin:kotlin-stdlib-jdk7:$kotlin_version",
//            utilcode     : "com.blankj:utilcode:1.25.0",
//            free_proguard: "com.blankj:free-proguard:1.0.1",
//            swipe_panel  : "com.blankj:swipe-panel:1.1",
//
//            leakcanary   : [
//                    android         : "com.squareup.leakcanary:leakcanary-android:$leakcanary_version",
//                    android_no_op   : "com.squareup.leakcanary:leakcanary-android-no-op:$leakcanary_version",
//                    support_fragment: "com.squareup.leakcanary:leakcanary-support-fragment:$leakcanary_version",
//            ],
//    ]

// appConfig 配置的是可以跑app的模块，git提交务必只包含launcher
//    static appConfig = ['launcher', 'feature0', 'feature1']
    static appConfig = ['launcher']
// pkgConfig 配置的是依赖的功能包，为空则依赖全部，git提交 务必为空
    static pkgConfig = ['feature0']
    static depConfig = [
            feature : [
                    launcher: [
                            app: new DepConfig(":feature:launcher:app")
                    ],
                    feature0: [
                            app : new DepConfig(":feature:feature0:app"),
                            pkg : new DepConfig(true, ":feature:feature0:pkg",
                                    "com.blankj:feature-feature0-pkg:1.0", true),
                            export: new DepConfig(":feature:feature0:export"),
                    ],
                    feature1: [
                            app : new DepConfig(":feature:feature1:app"),
                            pkg : new DepConfig(":feature:feature1:pkg"),
                            export: new DepConfig(":feature:feature1:export"),
                    ],
                    mock    : new DepConfig(":feature:mock"),
            ],
            lib : [
                    base : new DepConfig(":lib:base"),
                    common: new DepConfig(":lib:common"),
            ],
            support : [
                    appcompat_v7: new DepConfig("com.android.support:appcompat-v7:$support_version"),
                    design : new
                            DepConfig("com.android.support:design:$support_version"),
                    multidex : new
                            DepConfig("com.android.support:multidex:1.0.2"),
                    constraint : new
                            DepConfig("com.android.support.constraint:constraint-layout:1.1.3"),
            ],
            kotlin : new DepConfig("org.jetbrains.kotlin:kotlin-stdlib-jdk7:$kotlin_version"),
            utilcode : new DepConfig("com.blankj:utilcode:1.25.0"),
            free_proguard: new DepConfig("com.blankj:free-proguard:1.0.1"),
            swipe_panel : new DepConfig("com.blankj:swipe-panel:1.1"),
            leakcanary   : [
                    android         : new DepConfig("com.squareup.leakcanary:leakcanary-android:$leakcanary_version"),
                    android_no_op   : new DepConfig("com.squareup.leakcanary:leakcanary-android-no-op:$leakcanary_version"),
                    support_fragment: new DepConfig("com.squareup.leakcanary:leakcanary-support-fragment:$leakcanary_version"),
            ],
            plugin       : [
                    gradle: new DepConfig("com.android.tools.build:gradle:3.3.0"),
                    kotlin: new DepConfig("org.jetbrains.kotlin:kotlin-gradle-plugin:$kotlin_version"),
                    api   : new DepConfig("com.blankj:api-gradle-plugin:1.0"),
            ],
    ]
}