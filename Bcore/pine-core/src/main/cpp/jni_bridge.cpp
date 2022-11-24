//
// Created by canyie on 2020/3/18.
//

#include <cstring>
#include "jni_bridge.h"
#include "utils/macros.h"
#include "utils/scoped_local_ref.h"
#include "utils/log.h"

jint JNI_OnLoad(JavaVM* vm, void* reserved) {
    JNIEnv* env;
    if (UNLIKELY(vm->GetEnv(reinterpret_cast<void**>(&env), JNI_VERSION_1_6) != JNI_OK)) {
        return JNI_ERR;
    }

    {
        ScopedLocalClassRef Pine(env, "top/canyie/pine/Pine");
        if (UNLIKELY(Pine.IsNull())) {
            return JNI_ERR;
        }
        if (UNLIKELY(!register_Pine(env, Pine.Get()))) {
            return JNI_ERR;
        }
    }

    {
        ScopedLocalClassRef Ruler(env, "top/canyie/pine/Ruler");
        if (UNLIKELY(Ruler.IsNull())) {
            return JNI_ERR;
        }
        if (UNLIKELY(!register_Ruler(env, Ruler.Get()))) {
            return JNI_ERR;
        }
    }

    return JNI_VERSION_1_6;
}

#define JVMTI_VERSION_DEBUG JVMTI_VERSION_1_2
#define JVMTI_VERSION_RELEASE 0x70010200
jvmtiEnv *mJvmtiEnv;
extern "C"
JNIEXPORT jint JNICALL
Agent_OnAttach(JavaVM *vm, char *options, void *reserved) {
    //准备JVMTI环境，初始化mJvmtiEnv
    vm->GetEnv((void **) &mJvmtiEnv, JVMTI_VERSION_RELEASE);

    //开启JVMTI的能力：到这一步啦！！
    jvmtiCapabilities caps;
    mJvmtiEnv->GetPotentialCapabilities(&caps);
    mJvmtiEnv->AddCapabilities(&caps);
    LOGE("Agent_OnAttach");

    static auto checkclassFun = [](char* classSignature) ->bool {
        return strstr(classSignature, "Ltop/niunaijun") == nullptr &&
               strstr(classSignature, "/Reflect;") == nullptr &&
               strstr(classSignature, "Lkotlin/") == nullptr &&
               strstr(classSignature, "Llibcore/") == nullptr &&
               strstr(classSignature, "Landroid/") == nullptr &&
               strstr(classSignature, "Landroidx/") == nullptr &&
               strstr(classSignature, "Lcom/android/") == nullptr &&
               strstr(classSignature, "Ldalvik/") == nullptr &&
               strstr(classSignature, "Lblack/") == nullptr &&
               strstr(classSignature, "L$Proxy") == nullptr &&
               strstr(classSignature, "Ljava/") == nullptr &&
               strstr(classSignature, "Lsun/") == nullptr;
    };

    jvmtiEventCallbacks callbacks = { 0 };
    callbacks.VMObjectAlloc = [](jvmtiEnv *jvmti_env, JNIEnv *jni_env, jthread thread,
                                 jobject object, jclass object_klass, jlong size) {
        jvmti_env->SetTag(object, (jlong)object_klass);
        char *classSignature;
        // 获取类签名
        jvmti_env->GetClassSignature(object_klass, &classSignature, nullptr);
        // 过滤条件
//        if(strstr(classSignature, "com/test/memory") != nullptr){
        LOGE("VMObjectAlloc:%s",classSignature);

//        }
        jvmti_env->Deallocate((unsigned char *) classSignature);
    };
    callbacks.ObjectFree = [](jvmtiEnv *jvmti_env,
                              jlong tag) {
        LOGE("ObjectFree: %lld",tag);

    };

    callbacks.MethodEntry = [](jvmtiEnv *jvmti_env,
                               JNIEnv* jni_env,
                               jthread thread,
                               jmethodID method) {
        jvmtiError error;
        jclass clazz;
        char *classSignature;
        char* name;
        char* signature;
        error = jvmti_env->GetMethodDeclaringClass(method, &clazz);
//    // get the signature of the class
        error = jvmti_env->GetClassSignature(clazz, &classSignature, 0);
//    // get method name
        error = jvmti_env->GetMethodName(method, &name, &signature, NULL);

        if(checkclassFun(classSignature)){
            LOGE("MethodEntry: %s#%s(%s)", classSignature, name, signature);
        }
        jvmti_env->Deallocate((unsigned char *) classSignature);
        jvmti_env->Deallocate((unsigned char *) name);
        jvmti_env->Deallocate((unsigned char *) signature);
    };

    callbacks.MethodExit = [](jvmtiEnv *jvmti_env,
                              JNIEnv* jni_env,
                              jthread thread,
                              jmethodID method,
                              jboolean was_popped_by_exception,
                              jvalue return_value) {
        jvmtiError error;
        jclass clazz;
        char* name;
        char *classSignature;
        char* signature;
        error = jvmti_env->GetMethodDeclaringClass(method, &clazz);
//    // get the signature of the class
        error = jvmti_env->GetClassSignature(clazz, &classSignature, 0);
//    // get method name
        error = jvmti_env->GetMethodName(method, &name, &signature, NULL);

        if(checkclassFun(classSignature)){
            LOGE("MethodExit: %s#%s(%s)=>", classSignature, name, signature);
        }

        jvmti_env->Deallocate((unsigned char *) classSignature);
        jvmti_env->Deallocate((unsigned char *) name);
        jvmti_env->Deallocate((unsigned char *) signature);

    };

    //设置回调函数
    mJvmtiEnv->SetEventCallbacks(&callbacks, sizeof(callbacks));
    //开启监听
    mJvmtiEnv->SetEventNotificationMode(JVMTI_DISABLE, JVMTI_EVENT_VM_OBJECT_ALLOC, nullptr);
    mJvmtiEnv->SetEventNotificationMode(JVMTI_DISABLE, JVMTI_EVENT_OBJECT_FREE, nullptr);
    mJvmtiEnv->SetEventNotificationMode(JVMTI_ENABLE, JVMTI_EVENT_METHOD_ENTRY, nullptr);
    mJvmtiEnv->SetEventNotificationMode(JVMTI_ENABLE, JVMTI_EVENT_METHOD_EXIT, nullptr);

    return JNI_OK;
}