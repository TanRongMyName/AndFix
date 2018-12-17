package com.coffe.shentao.fixapp.ok;

import com.coffe.shentao.fixapp.Replace;

import java.lang.annotation.Retention;

/**
 * 出现异常的方法
 */
public class Caculutor {
    @Replace(clazz="com.coffe.shentao.fixapp.Caculutor",method = "caculator")
    public int caculator(){
        int i=10;
        int j=1;
        return i/j;
    }

    //class ---dex
    // sdk/build-tools/dx.bat  ---dex
    //class 的路径 不要忘记   android class 文件的位置 build intermediates incremental-verifier debug com
    //dex --dex --output= path\filename.dex path
//
//    C:\sdk\sdk\build-tools\26.0.2>dx --dex --output C:\Users\win\Desktop\andfix\fix.
//    dex C:\Users\win\Desktop\andfix
//    dex 放在的位置 C:\Users\win\Desktop\andfix   打包成dex 的class文件在 C:\Users\win\Desktop\andfix 目录下面 同时不能修改包名


}
