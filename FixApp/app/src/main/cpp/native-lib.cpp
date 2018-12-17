#include <jni.h>
#include <string>
#include <android/log.h>
#include "art_7_0.h"
#define  LOG_TAG    "AndFix"
#define  LOGD(...)  __android_log_print(ANDROID_LOG_DEBUG,LOG_TAG,__VA_ARGS__)
extern "C" JNIEXPORT jstring JNICALL
Java_com_coffe_shentao_fixapp_MainActivity_stringFromJNI(
        JNIEnv *env,
        jobject /* this */) {
    std::string hello = "Hello from C++";
    return env->NewStringUTF(hello.c_str());
}


//https://blog.csdn.net/weelyy/article/details/78906537  andfix 说明
extern "C"
JNIEXPORT void JNICALL
Java_com_coffe_shentao_fixapp_DxManager_replace(JNIEnv *env, jobject instance, jobject wrongMethod,
                                                jobject rightMethod) {
    //拿到 错误的方法  方法表中的方法
    art::mirror::ArtMethod* smeth=(art::mirror::ArtMethod*)env->FromReflectedMethod(wrongMethod);
    // TODO
            //拿到正确的方法
    art::mirror::ArtMethod *dmeth=(art::mirror::ArtMethod*)env->FromReflectedMethod(rightMethod);
    //替换地址
    smeth->declaring_class_=dmeth->declaring_class_;
    smeth->access_flags_=dmeth->access_flags_;
    smeth->dex_code_item_offset_=dmeth->dex_code_item_offset_;
    smeth->dex_method_index_ = dmeth->dex_method_index_;
    smeth->method_index_ = dmeth->method_index_;
    smeth->hotness_count_ = dmeth->hotness_count_;
    smeth->ptr_sized_fields_.dex_cache_resolved_types_ = dmeth->ptr_sized_fields_.dex_cache_resolved_types_;
    smeth->ptr_sized_fields_.dex_cache_resolved_methods_ = dmeth->ptr_sized_fields_.dex_cache_resolved_methods_;
    smeth->ptr_sized_fields_.entry_point_from_jni_ = dmeth->ptr_sized_fields_.entry_point_from_jni_;
    smeth->ptr_sized_fields_.entry_point_from_quick_compiled_code_ = dmeth->ptr_sized_fields_.entry_point_from_quick_compiled_code_;
}
size_t art_method_length=0;
extern "C"
JNIEXPORT jint JNICALL
Java_com_coffe_shentao_fixapp_DxManager_calcuatSizeArtMthod(JNIEnv *env, jobject instance,jobject method1, jobject method2) {
     if(art_method_length!=0){
         return art_method_length;
     }
     size_t m1=(size_t)env->FromReflectedMethod(method1);
     size_t m2=(size_t)env->FromReflectedMethod(method2);
    art_method_length=m2-m1;
    LOGD("initArtMethoLength end:  %d , %d, %d",
         m1, m2, art_method_length);
    return art_method_length;
    // TODO
}

extern "C"
JNIEXPORT void JNICALL
Java_com_coffe_shentao_fixapp_DxManager_hotReplace(JNIEnv *env, jobject instance,
                                                   jobject wrongMethod, jobject method) {

    LOGD("start hotFixMethod  :  %d",
         art_method_length);
    jmethodID meth = env->FromReflectedMethod(wrongMethod);
    jmethodID target = env->FromReflectedMethod(method);
    memcpy(meth, target, art_method_length);
    LOGD("end hotFixMethod  :  %d, %d , %d , %d",
         art_method_length, sizeof(meth), sizeof(target), sizeof(jmethodID));

}