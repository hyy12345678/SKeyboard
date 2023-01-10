
#include <android/log.h>
#ifndef SKEYBOAD_LIB_H
#define SKEYBOAD_LIB_H

#endif //SKEYBOAD_LIB_H

//导入android日志
#define TAG "skeyboad-lib"
#define LOGI(...) __android_log_print(ANDROID_LOG_INFO, TAG, __VA_ARGS__)
#define LOGD(...) __android_log_print(ANDROID_LOG_DEBUG, TAG, __VA_ARGS__)
#define LOGE(...) __android_log_print(ANDROID_LOG_ERROR, TAG, __VA_ARGS__)
#define LOGF(...) __android_log_print(ANDROID_LOG_FATAL, TAG ,__VA_ARGS__)
#define LOGW(...) __android_log_print(ANDROID_LOG_WARN, TAG ,__VA_ARGS__)

//aes加密的密钥和iv
const char *AES_SECRET_KEY = "JA2F8AKJF3D7HF16";
const char *AES_IV = "ngshaoyu16geziji";


//des加密的密钥和iv
const char *DES_SECRET_KEY = "JA2F8AKJF3D7HF16";
const char *DES_IV = "ngshaoyu16geziji";
