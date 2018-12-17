package com.coffe.shentao.fixapp;

import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.TextView;

import java.io.File;

public class MainActivity extends AppCompatActivity {

    // Used to load the 'native-lib' library on application startup.
    static {
        System.loadLibrary("native-lib");
    }

    private TextView tv;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        // Example of a call to a native method
        tv=findViewById(R.id.tv);
        tv=findViewById(R.id.tv);
    }

    /**
     * A native method that is implemented by the 'native-lib' native library,
     * which is packaged with this application.
     */
    public native String stringFromJNI();

    public void oncalcer(View view) {
        Caculutor caculutor=new Caculutor();
        tv.setText("-计算结果："+caculutor.caculator());
    }

    public void onFix(View view) {
        DxManager manager=new DxManager(this);
        manager.loadDex(new File("/sdcard/fix.dex"));
    }


    //.so 文件生成
//    app build.gradle  添加
//    ndk{//生成.so库
//        moduleName "native-lib"
//        abiFilters "arm64-v8a","armeabi-v7a","x86","x86_64"
//    }
//    生成的.so 文件在 build intermediates cmake debug obj 下
    //
}
