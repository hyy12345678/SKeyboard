#include <jni.h>
#include <assert.h>
#include <string.h>
#include <stdlib.h>
#include <iostream>
#include "MD5.h"
//#include "MD5.cpp"
#include "logger.h"
using namespace std;

//extern "C" JNIEXPORT jstring JNICALL
//Java_net_hyy_fun_skeyboard_MainActivity_stringFromJNI(
//        JNIEnv *env,
//        jobject /* this */) {
//    std::string hello = "Hello from C++";
//    return env->NewStringUTF(hello.c_str());
//}





class HashNode{
public:
    string  mKey;
    string  mValue;
    HashNode *next;

    HashNode(string key, string value){
        mKey   = key;
        mValue = value;
        next = NULL;
    }
    ~HashNode(){
    }
    HashNode& operator=(const HashNode& node){
        if(this == &node) return *this;
        mKey = node.mKey;
        mValue = node.mValue;
        next = node.next;
        return *this;
    }
};

class HashMap{
public:
    HashMap(int size);
    ~HashMap();
    bool HMInsert(const string& key, const string& value);
    bool HMDelete(const string& key);
    string& HMFind(const string& key);
    string& operator[](const string& key);
private:
    int hashfunc(const string& key);
    HashNode ** mTable;
    int mSize;
    string strnull;
};

HashMap::HashMap(int size):mSize(size){
    mTable = new HashNode*[size];
    for(int i=0; i<mSize; ++i){
        mTable[i] = NULL;
    }
//    strnull = "NULL";
    strnull = "";
}

HashMap::~HashMap(){
    for(int i=0; i<mSize; ++i){
        HashNode *curNode = mTable[i];
        while(curNode){
            HashNode *temp = curNode;
            curNode =curNode->next;
            delete temp;
        }
    }
    delete mTable;
}

HashMap hashmap(10);

bool HashMap::HMInsert(const string& key, const string& value)
{
    int index = hashfunc(key)%mSize;
    HashNode *node = new HashNode(key, value);
    node->next = mTable[index];
    mTable[index] = node;
    return true;
}

bool HashMap::HMDelete(const string &key)
{
    int index = hashfunc(key)%mSize;
    HashNode *node = mTable[index];
    HashNode *prev = NULL;
    while(node){
        if(key == node->mKey){
            if(NULL == prev){
                mTable[index] = node->next;
            }else{
                prev->next = node->next;
            }
            delete node;
            return true;
        }
        prev = node;
        node = node->next;
    }
    return false;
}

string& HashMap::HMFind(const string& key)
{
    int index = hashfunc(key)%mSize;
    if(NULL == mTable[index]){
        return strnull;
    }else{
        HashNode *node = mTable[index];
        while(node){
            if(key == node->mKey){
                return node->mValue;
            }
            node = node->next;
        }
    }
    return strnull;
}

string& HashMap::operator[](const string& key)
{
    return HMFind(key);
}

int HashMap::hashfunc(const string& key){
    int hash = 0;
    for(int i=0; i<key.length(); ++i){
        hash = hash << 7^key[i];
    }
    return (hash & 0x7FFFFFFF);
}


string jstring2str(JNIEnv *env, jstring jstr) {
    char *rtn = NULL;
    jclass clsstring = env->FindClass("java/lang/String");
    jstring strencode = env->NewStringUTF("GB2312");
    jmethodID mid = env->GetMethodID(clsstring, "getBytes", "(Ljava/lang/String;)[B");
    jbyteArray barr = (jbyteArray) env->CallObjectMethod(jstr, mid, strencode);
    jsize alen = env->GetArrayLength(barr);
    jbyte *ba = env->GetByteArrayElements(barr, JNI_FALSE);
    if (alen > 0) {
        rtn = (char *) malloc(alen + 1);
        memcpy(rtn, ba, alen);
        rtn[alen] = 0;
    }
    env->ReleaseByteArrayElements(barr, ba, 0);
    std::string stemp(rtn);
    free(rtn);
    return stemp;
}

extern "C"
JNIEXPORT jstring JNICALL
Java_net_hyy_fun_skeyboardlib_NativeHelper_stringFromJNI(JNIEnv *env, jclass type) {
    std::string hello = "Hello from C++";
    return env->NewStringUTF(hello.c_str());
}extern "C"


JNIEXPORT void JNICALL
Java_net_hyy_fun_skeyboardlib_NativeHelper_addKey(JNIEnv *env, jclass type, jstring id_,
                                                  jstring text_) {

//    LOGI("Native-lib addKey：%s",jstring2str(env, text_).c_str());
    // TODO
    hashmap.HMInsert(jstring2str(env, id_),hashmap.HMFind(jstring2str(env, id_)).append(jstring2str(env, text_)));

}


extern "C"
JNIEXPORT void JNICALL
Java_net_hyy_fun_skeyboardlib_NativeHelper_deleteKey(JNIEnv *env, jclass type, jstring id_) {

    // TODO
    string input = hashmap.HMFind(jstring2str(env, id_));
    input.pop_back();
    hashmap.HMInsert(jstring2str(env, id_),input);

}

extern "C"
JNIEXPORT void JNICALL
Java_net_hyy_fun_skeyboardlib_NativeHelper_clearKey(JNIEnv *env, jclass type, jstring id_) {

    // TODO
    string input = hashmap.HMFind(jstring2str(env, id_));
    input.clear();
    hashmap.HMInsert(jstring2str(env, id_),input);

}


extern "C"
JNIEXPORT jstring JNICALL
Java_net_hyy_fun_skeyboardlib_NativeHelper_getEncryptKey(JNIEnv *env, jclass type, jstring id_,
                                                         jstring timestamp_) {


    // TODO
    string input = hashmap.HMFind(jstring2str(env, id_));

    string md5Result;
    if(input.empty()){
        md5Result = "";
    }else{
        MD5 md5 = MD5(input);
        md5Result = md5.hexdigest();
    }

    //将char *类型转化成jstring返回给Java层
    return env->NewStringUTF(md5Result.c_str());


}

extern "C"
JNIEXPORT jstring JNICALL
Java_net_hyy_fun_skeyboardlib_NativeHelper_getDecryptKey(JNIEnv *env, jclass type, jstring id_,
                                                         jstring timestamp_) {

//    LOGI("Native-lib DecryptKey：%s",hashmap.HMFind(jstring2str(env, id_)).c_str());
    // TODO
    return (env)->NewStringUTF(hashmap.HMFind(jstring2str(env, id_)).data());
}

extern "C"
JNIEXPORT jstring JNICALL
Java_net_hyy_fun_skeyboardlib_NativeHelper_getEncryptKeyDES(JNIEnv *env, jclass type, jstring id_,
                                                            jstring key_, jstring timestamp_) {

    const char *id = env->GetStringUTFChars(id_, 0);
    const char *key = env->GetStringUTFChars(key_, 0);
    const char *timestamp = env->GetStringUTFChars(timestamp_, 0);

    // TODO
    if(hashmap.HMFind(id).empty()){
        env->ReleaseStringUTFChars(id_, id);
        env->ReleaseStringUTFChars(key_, key);
        env->ReleaseStringUTFChars(timestamp_, timestamp);
        return env->NewStringUTF("");
    }


    const char* content = hashmap.HMFind(id).c_str();

    //1、获取字节码
    jclass jclazz = (*env).FindClass("net.hyy.fun.skeyboardlib.DESUtils3");
//    jclass jclazz = (*env).FindClass("net.hyy.fun.skeyboardlib.DummyCCallJava");

    //2、获取方法
    jmethodID jmethodId = (*env).GetStaticMethodID(jclazz,"encrypt","(Ljava/lang/String;Ljava/lang/String;)Ljava/lang/String;");


    if (NULL == jmethodId) {
        env->DeleteLocalRef(jclazz);
        return NULL;
    }

    //4、调用方法
    jstring str_arg1 = env->NewStringUTF(content);
    jstring str_arg2 = env->NewStringUTF(key);

    jstring result;
    result = (jstring) env->CallStaticObjectMethod(jclazz, jmethodId, str_arg1, str_arg2);

    const char *result_str = env->GetStringUTFChars(result, NULL);
//    LOGI("获取到Java层返回的数据 : %s", result_str);
//    LOGI("Native-lib step3");

    (env)->DeleteLocalRef(jclazz);
    env->DeleteLocalRef(result);
    env->ReleaseStringUTFChars(id_, id);
    env->ReleaseStringUTFChars(key_, key);
    env->ReleaseStringUTFChars(timestamp_, timestamp);

    return env->NewStringUTF(result_str);




//    //调用某个类的static方法
//
//    //JVM使用一个类时,是需要先判断这个类是否被加载了,如果没被加载则还需要加载一下才能使用
//    //1. 从classpath路径下搜索MyJNIClass这个类,并返回该类的Class对象
//    jclass clazz = env->FindClass("net.hyy.fun.skeyboardlib.DummyCCallJava");
//    //2. 从clazz类中查找getDes方法 得到这个静态方法的方法id
//    jmethodID mid_get_des = env->GetStaticMethodID(clazz, "getDes", "(Ljava/lang/String;)Ljava/lang/String;");
//    //3. 构建入参,调用static方法,获取返回值
//    jstring str_arg = env->NewStringUTF("我是xfhy");
//    jstring result = (jstring) env->CallStaticObjectMethod(clazz, mid_get_des, str_arg);
//    const char *result_str = env->GetStringUTFChars(result, NULL);
//    LOGI("获取到Java层返回的数据 : %s", result_str);
//
//    //4. 移除局部引用
//    env->DeleteLocalRef(clazz);
//    env->DeleteLocalRef(str_arg);
//    env->DeleteLocalRef(result);
//
//    return env->NewStringUTF("lalala");

}

//extern "C"
//JNIEXPORT jstring JNICALL
//Java_net_hyy_fun_skeyboardlib_NativeHelper_test(JNIEnv *env, jclass type, jstring id_, jstring key_,
//                                                jstring timestamp_) {
//    const char *id = env->GetStringUTFChars(id_, 0);
//    const char *key = env->GetStringUTFChars(key_, 0);
//    const char *timestamp = env->GetStringUTFChars(timestamp_, 0);
//
//    // TODO
//
//    env->ReleaseStringUTFChars(id_, id);
//    env->ReleaseStringUTFChars(key_, key);
//    env->ReleaseStringUTFChars(timestamp_, timestamp);
//
//    return env->NewStringUTF(returnValue);
//}