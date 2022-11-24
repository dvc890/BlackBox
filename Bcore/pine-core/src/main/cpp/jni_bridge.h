//
// Created by canyie on 2020/3/18.
//

#ifndef PINE_PINE_H
#define PINE_PINE_H

#include <jni.h>
#include "jvmti.h"

extern "C" {
JNIEXPORT jint JNI_OnLoad(JavaVM* vm, void* reserved);
JNIEXPORT jint JNICALL Agent_OnAttach(JavaVM* vm, char* options, void* reserved);
bool register_Pine(JNIEnv* env, jclass Pine);
bool register_Ruler(JNIEnv* env, jclass Ruler);
extern jvmtiEnv *mJvmtiEnv;

void Ruler_m1(JNIEnv* env, jclass); // used for search ArtMethod members
}

#endif //PINE_PINE_H
