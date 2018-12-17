package com.coffe.shentao.fixapp;

import android.content.Context;
import android.util.Log;

import java.io.File;
import java.io.IOException;
import java.lang.reflect.Method;
import java.util.Enumeration;

import dalvik.system.DexFile;

public class DxManager {
    private Context context;
    private String TAG="TanRong";
    public DxManager(Context context){
        this.context=context;
    }

    public void loadDex(File dexFilePath)  {
        File optFile=new File(context.getCacheDir(),dexFilePath.getName());
        if(optFile.exists()){
            optFile.delete();
        }                                //文件路径                     缓存文件  标志位
        try {
            //加载dex
            DexFile dexFile= DexFile.loadDex(dexFilePath.getAbsolutePath(), optFile.getAbsolutePath(),Context.MODE_PRIVATE);
            //遍历dexFile---查找class
            Enumeration<String>entry=dexFile.entries();
            while(entry.hasMoreElements()){
                String className=entry.nextElement();
                //修复好的 realclass   怎么样  找到出bug的class
                Class realClazz=dexFile.loadClass(className,context.getClassLoader());
                Log.i(TAG,"找到类  "+realClazz);
                 fix(realClazz);
            }
        } catch (IOException e) {
            e.printStackTrace();
        }


    }

    private void fix(Class realClazz) {
        Method[] methods=realClazz.getDeclaredMethods();
        for(Method method:methods){
            //拿到注解
            Replace replace =method.getAnnotation(Replace.class);
            if(replace==null){
                continue;
            }
            Log.v(TAG,"replace.clazz==="+replace.clazz());//找到错误的class
            String wrongClazzName=replace.clazz();

            String wrongMethodName=replace.method();

            try {
                Class wrongClass=Class.forName(wrongClazzName);
                //最终拿到错误的method 对象
                Method wrongMethod=wrongClass.getMethod(wrongMethodName,method.getParameterTypes());
                //开始修复---需要考虑兼容的问题
                //replace(wrongMethod,method);
                //不用考虑兼容的问题
                calculationSizeArtMethod();
                hotReplace(wrongMethod,method);

            } catch (ClassNotFoundException e) {
                e.printStackTrace();
            } catch (NoSuchMethodException e) {
                e.printStackTrace();
            }

        }
    }

    public void calculationSizeArtMethod(){
        Method method1= null;
        try {
            method1 = NativeArtMethodCalculator.class.getMethod("method1");
            Method method2=NativeArtMethodCalculator.class.getMethod("method2");
            calcuatSizeArtMthod(method1,method2);
        } catch (NoSuchMethodException e) {
            e.printStackTrace();
        }

    }




    public native int calcuatSizeArtMthod(Method method1, Method method2) ;


    //获取 方法去的 方法间隔的大小
    //每一个类的ArtMethod们在内存中是紧密排列在一起的，所以一个ArtMethod的大小，不就是相邻两个方法所对应的ArtMethod的起始地址的差值吗？
     //只是替换了 虚拟机中的结构体的 方法位置 ----
     private native void replace(Method wrongMethod, Method method);

     //替换方法去的方法的整个结构体 可以兼容版本 不用 --考虑 7.0 8.0 9.0
     private native void hotReplace(Method wrongMethod, Method method);



    //通过Java方法来操作内存，将ArtMethod的Native指针进行替换  java8 --去掉了 UNSAFE_CLASS = "sun.misc.Unsafe"; 类 可能会存在风险
    public static void startFixByJava(Context context,Method srcMethod,Method dstMethod) {
        try {
            Method method1 = NativeArtMethodCalculator.class.getMethod("method1");
            Method method2 = NativeArtMethodCalculator.class.getMethod("method2");
            long method1Address = MenoryWrapper.getMethodAddress(method1);
            long method2Address = MenoryWrapper.getMethodAddress(method2);
            long sizeOfArtMethod = method2Address - method1Address;  //等同于调用JNI方法：sizeOfArtMethod = getArtMethoLength(method1, method2);


            long dstAddress = MenoryWrapper.getMethodAddress(dstMethod);
            long srcAddress = MenoryWrapper.getMethodAddress(srcMethod);

            MenoryWrapper.memcpy(dstAddress, srcAddress, sizeOfArtMethod); //等同于调用JNI方法： memcpy(dstAddress, srcAddress, art_method_length);
        } catch (NoSuchMethodException e) {
            e.printStackTrace();
        }
    }

}
