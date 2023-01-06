#ifndef JNILEARN_LOGGER_H
#define JNILEARN_LOGGER_H
#endif //JNILEARN_LOGGER_H
#include <android/log.h>
// log标签
#define  TAG    "JNI_TAG"
// 定义info信息
#define LOGI(...)     __android_log_print(ANDROID_LOG_INFO,TAG,__VA_ARGS__)
// 定义debug信息
#define LOGD(...) __android_log_print(ANDROID_LOG_DEBUG, TAG, __VA_ARGS__)
// 定义error信息
#define LOGE(...) __android_log_print(ANDROID_LOG_ERROR,TAG,__VA_ARGS__)


