#include <jni.h>
//#include <assert.h>
#include <string.h>
#include <stdio.h>
#include <stdlib.h>
#include <iostream>


#include <openssl/hmac.h>
#include <openssl/rsa.h>
#include <openssl/pem.h>
#include <openssl/md5.h>

#include "skeyboad-lib.h"

using namespace std;

//extern "C" JNIEXPORT jstring JNICALL
//Java_net_hyy_fun_skeyboard_MainActivity_stringFromJNI(
//        JNIEnv *env,
//        jobject /* this */) {
//    std::string hello = "Hello from C++";
//    return env->NewStringUTF(hello.c_str());
//}



class HashNode {
public:
    string mKey;
    string mValue;
    HashNode *next;

    HashNode(string key, string value) {
        mKey = key;
        mValue = value;
        next = NULL;
    }

    ~HashNode() {
    }

    HashNode &operator=(const HashNode &node) {
        if (this == &node) return *this;
        mKey = node.mKey;
        mValue = node.mValue;
        next = node.next;
        return *this;
    }
};

class HashMap {
public:
    HashMap(int size);

    ~HashMap();

    bool HMInsert(const string &key, const string &value);

    bool HMDelete(const string &key);

    string &HMFind(const string &key);

    string &operator[](const string &key);

private:
    int hashfunc(const string &key);

    HashNode **mTable;
    int mSize;
    string strnull;
};

HashMap::HashMap(int size) : mSize(size) {
    mTable = new HashNode *[size];
    for (int i = 0; i < mSize; ++i) {
        mTable[i] = NULL;
    }
//    strnull = "NULL";
    strnull = "";
}

HashMap::~HashMap() {
    for (int i = 0; i < mSize; ++i) {
        HashNode *curNode = mTable[i];
        while (curNode) {
            HashNode *temp = curNode;
            curNode = curNode->next;
            delete temp;
        }
    }
    delete mTable;
}

HashMap hashmap(10);

bool HashMap::HMInsert(const string &key, const string &value) {
    int index = hashfunc(key) % mSize;
    HashNode *node = new HashNode(key, value);
    node->next = mTable[index];
    mTable[index] = node;
    return true;
}

bool HashMap::HMDelete(const string &key) {
    int index = hashfunc(key) % mSize;
    HashNode *node = mTable[index];
    HashNode *prev = NULL;
    while (node) {
        if (key == node->mKey) {
            if (NULL == prev) {
                mTable[index] = node->next;
            } else {
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

string &HashMap::HMFind(const string &key) {
    int index = hashfunc(key) % mSize;
    if (NULL == mTable[index]) {
        return strnull;
    } else {
        HashNode *node = mTable[index];
        while (node) {
            if (key == node->mKey) {
                return node->mValue;
            }
            node = node->next;
        }
    }
    return strnull;
}

string &HashMap::operator[](const string &key) {
    return HMFind(key);
}

int HashMap::hashfunc(const string &key) {
    int hash = 0;
    for (int i = 0; i < key.length(); ++i) {
        hash = hash << 7 ^ key[i];
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
    hashmap.HMInsert(jstring2str(env, id_),
                     hashmap.HMFind(jstring2str(env, id_))+(jstring2str(env, text_)));

}


extern "C"
JNIEXPORT void JNICALL
Java_net_hyy_fun_skeyboardlib_NativeHelper_deleteKey(JNIEnv *env, jclass type, jstring id_) {

    // TODO
    string input = hashmap.HMFind(jstring2str(env, id_));
    input.pop_back();
    hashmap.HMInsert(jstring2str(env, id_), input);

}

extern "C"
JNIEXPORT void JNICALL
Java_net_hyy_fun_skeyboardlib_NativeHelper_clearKey(JNIEnv *env, jclass type, jstring id_) {

    // TODO
    string input = hashmap.HMFind(jstring2str(env, id_));
    input.clear();
    hashmap.HMInsert(jstring2str(env, id_), input);

}


extern "C"
JNIEXPORT jstring JNICALL
Java_net_hyy_fun_skeyboardlib_NativeHelper_getEncryptKey(JNIEnv *env, jclass type, jstring id_,
                                                         jstring timestamp_) {


    // TODO
    string input = hashmap.HMFind(jstring2str(env, id_));


    if (input.empty()) {
        return env->NewStringUTF("");
    } else {
//        MD5 md5 = MD5(input);
//        md5Result = md5.hexdigest();

        unsigned char sMD5[16] = {0};
        MD5((const unsigned char *) (input.c_str()), strlen(input.c_str()), sMD5);

        char buf[33];
        for (int i = 0; i < 16; i++)
            sprintf(buf + i * 2, "%02x", sMD5[i]);
        buf[32] = 0;

        //将char *类型转化成jstring返回给Java层
        return env->NewStringUTF((std::string(buf)).c_str());

    }


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
JNIEXPORT jbyteArray JNICALL
Java_net_hyy_fun_skeyboardlib_NativeHelper_aesEncryption(JNIEnv *env, jclass type,
                                                         jbyteArray src_) {
    jbyte *src = env->GetByteArrayElements(src_, NULL);
    jsize src_Len = env->GetArrayLength(src_);

    LOGD("########## Estr = %s", src);

    int outlen = 0, cipherText_len = 0;

    unsigned char *out = (unsigned char *) malloc((src_Len / 16 + 1) * 16);
    //清空内存空间
    memset(out, 0, (src_Len / 16 + 1) * 16);

    EVP_CIPHER_CTX ctx;
    EVP_CIPHER_CTX_init(&ctx);
    //    LOGD("AES->指定加密算法，初始化加密key/iv");
    //这里可以修改签名算法：EVP_aes_128_cbc/EVP_aes_128_ecb/EVP_aes_128_cfb1/EVP_aes_128_cfb8
    EVP_EncryptInit_ex(&ctx, EVP_aes_128_cbc(), NULL, (const unsigned char *) AES_SECRET_KEY,
                       (const unsigned char *) AES_IV);
    //    LOGD("AES->对数据进行加密运算");
    EVP_EncryptUpdate(&ctx, out, &outlen, (const unsigned char *) src, src_Len);
    cipherText_len = outlen;

    //    LOGD("AES->结束加密运算");
    EVP_EncryptFinal_ex(&ctx, out + outlen, &outlen);
    cipherText_len += outlen;

    LOGD("########## Eout = %s", out);

    //    LOGD("AES->EVP_CIPHER_CTX_cleanup");
    EVP_CIPHER_CTX_cleanup(&ctx);

    //    LOGD("AES->从jni释放数据指针");
    env->ReleaseByteArrayElements(src_, src, 0);

    jbyteArray cipher = env->NewByteArray(cipherText_len);
    //    LOGD("AES->在堆中分配ByteArray数组对象成功，将拷贝数据到数组中");
    env->SetByteArrayRegion(cipher, 0, cipherText_len, (jbyte *) out);
    //    LOGD("AES->释放内存");
    free(out);

    return cipher;
}


extern "C"
JNIEXPORT jbyteArray JNICALL
Java_net_hyy_fun_skeyboardlib_NativeHelper_aesCrypt(JNIEnv *env, jclass type, jbyteArray src_) {

    jbyte *src = env->GetByteArrayElements(src_, NULL);
    jsize src_Len = env->GetArrayLength(src_);

    LOGD("########## Dstr = %s", src);

    int outlen = 0, plaintext_len = 0;

    unsigned char *out = (unsigned char *) malloc(src_Len);
    memset(out, 0, src_Len);

    EVP_CIPHER_CTX ctx;
    EVP_CIPHER_CTX_init(&ctx);
//    LOGD("AES->指定解密算法，初始化解密key/iv");
    EVP_DecryptInit_ex(&ctx, EVP_aes_128_cbc(), NULL, (const unsigned char *) AES_SECRET_KEY,
                       (const unsigned char *) AES_IV);
//    LOGD("AES->对数据进行解密运算");
    EVP_DecryptUpdate(&ctx, out, &outlen, (const unsigned char *) src, src_Len);
    plaintext_len = outlen;

//    LOGD("AES->结束解密运算");
    EVP_DecryptFinal_ex(&ctx, out + outlen, &outlen);
    plaintext_len += outlen;

    LOGD("########## Dout = %s", out);

//    LOGD("AES->EVP_CIPHER_CTX_cleanup");
    EVP_CIPHER_CTX_cleanup(&ctx);

//    LOGD("AES->从jni释放数据指针");
    env->ReleaseByteArrayElements(src_, src, 0);

    jbyteArray cipher = env->NewByteArray(plaintext_len);
//    LOGD("AES->在堆中分配ByteArray数组对象成功，将拷贝数据到数组中");
    env->SetByteArrayRegion(cipher, 0, plaintext_len, (jbyte *) out);
//    LOGD("AES->释放内存");
    free(out);

    return cipher;
}


extern "C"
JNIEXPORT jbyteArray JNICALL
Java_net_hyy_fun_skeyboardlib_NativeHelper_test(JNIEnv *env, jclass type, jbyteArray src_) {

    unsigned char sKey[] = "randromdata";
    unsigned char sIV[] = "";

    jbyte *src = env->GetByteArrayElements(src_, NULL);
    jsize src_Len = env->GetArrayLength(src_);


    int outlen = 0, cipherText_len = 0;

    unsigned char *out = (unsigned char *) malloc((src_Len / 16 + 1) * 16);
    //清空内存空间
    memset(out, 0, (src_Len / 16 + 1) * 16);

    EVP_CIPHER_CTX ctx;
    EVP_CIPHER_CTX_init(&ctx);
    //    LOGD("AES->指定加密算法，初始化加密key/iv");
    //这里可以修改签名算法：EVP_aes_128_cbc/EVP_aes_128_ecb/EVP_aes_128_cfb1/EVP_aes_128_cfb8
    EVP_EncryptInit_ex(&ctx, EVP_des_ecb(), NULL, sKey, sIV);
    //    LOGD("AES->对数据进行加密运算");
    EVP_EncryptUpdate(&ctx, out, &outlen, (const unsigned char *) src, src_Len);
    cipherText_len = outlen;

    //    LOGD("AES->结束加密运算");
    EVP_EncryptFinal_ex(&ctx, out + outlen, &outlen);
    cipherText_len += outlen;

//    LOGD("########## Eout = %s", out);

    //    LOGD("AES->EVP_CIPHER_CTX_cleanup");
    EVP_CIPHER_CTX_cleanup(&ctx);

    //    LOGD("AES->从jni释放数据指针");
    env->ReleaseByteArrayElements(src_, src, 0);

    jbyteArray cipher = env->NewByteArray(cipherText_len);
    //    LOGD("AES->在堆中分配ByteArray数组对象成功，将拷贝数据到数组中");
    env->SetByteArrayRegion(cipher, 0, cipherText_len, (jbyte *) out);
    //    LOGD("AES->释放内存");
    free(out);

    return cipher;

}

extern "C"
JNIEXPORT jbyteArray JNICALL
Java_net_hyy_fun_skeyboardlib_NativeHelper_getEncryptKeyDES(JNIEnv *env, jclass type, jstring id_,
                                                            jbyteArray key_) {

    const char *id = env->GetStringUTFChars(id_, 0);
    jbyte *key = env->GetByteArrayElements(key_, NULL);


    string content = hashmap.HMFind(id);

    if(content.empty()){
        env->ReleaseStringUTFChars(id_, id);
        env->ReleaseByteArrayElements(key_, key, 0);

        return env->NewByteArray(0);
    }


    int content_Len = content.length();

    // TODO

    int outlen = 0, cipherText_len = 0;

    unsigned char *out = (unsigned char *) malloc((content_Len / 16 + 1) * 16);
    //清空内存空间
    memset(out, 0, (content_Len / 16 + 1) * 16);

    EVP_CIPHER_CTX ctx;
    EVP_CIPHER_CTX_init(&ctx);
    //    LOGD("AES->指定加密算法，初始化加密key/iv");
    //这里可以修改签名算法：EVP_aes_128_cbc/EVP_aes_128_ecb/EVP_aes_128_cfb1/EVP_aes_128_cfb8
    EVP_EncryptInit_ex(&ctx, EVP_des_ecb(), NULL, (const unsigned char *) key,
                       (const unsigned char *) key);
    //    LOGD("AES->对数据进行加密运算");
    EVP_EncryptUpdate(&ctx, out, &outlen, (const unsigned char *) content.c_str(), content_Len);
    cipherText_len = outlen;

    //    LOGD("AES->结束加密运算");
    EVP_EncryptFinal_ex(&ctx, out + outlen, &outlen);
    cipherText_len += outlen;

//    LOGD("########## Eout = %s", out);

    //    LOGD("AES->EVP_CIPHER_CTX_cleanup");
    EVP_CIPHER_CTX_cleanup(&ctx);


    jbyteArray cipher = env->NewByteArray(cipherText_len);
    //    LOGD("AES->在堆中分配ByteArray数组对象成功，将拷贝数据到数组中");
    env->SetByteArrayRegion(cipher, 0, cipherText_len, (jbyte *) out);
    //    LOGD("AES->释放内存");
    free(out);


    env->ReleaseStringUTFChars(id_, id);
    env->ReleaseByteArrayElements(key_, key, 0);

    return cipher;
}extern "C"
JNIEXPORT void JNICALL
Java_net_hyy_fun_skeyboardlib_NativeHelper_releaseKey(JNIEnv *env, jclass type, jstring id_) {
    const char *id = env->GetStringUTFChars(id_, 0);

    // TODO
    bool re = hashmap.HMDelete(id);
    while(re){
        re = hashmap.HMDelete(id);
    }

    env->ReleaseStringUTFChars(id_, id);
}