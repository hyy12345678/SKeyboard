package net.hyy.fun.skeyboardlib;

import android.util.Base64;
import android.util.Log;

import java.nio.charset.StandardCharsets;

import javax.crypto.Cipher;
import javax.crypto.spec.SecretKeySpec;

/**
 * @author huangyy
 * @date 2022/10/11
 */
public class DESUtils3 {

    private static final String TAG = "DESUtils3";

    /**
     * 加密算法DES
     */
    private static final String KEY_ALGORITHM = "DES";

    /**
     * key的长度，64
     *
     */
    private static final Integer KEY_LENGTH = 16 * 4;

    /**
     * 算法名称/加密模式/数据填充方式
     * 默认：DES/ECB/PKCS7Padding
     */
    private static final String ALGORITHMS = "DES/ECB/PKCS7Padding";

    /**
     * 后端DES的key，由静态代码块赋值
     */
    public static String key;





    /**
     * 解密
     *
     * @param encryptStr 解密的字符串
     * @param decryptKey 解密的key值
     */
    public static String decrypt(String encryptStr, String decryptKey) throws Exception {

        byte[] bytes1 = decryptKey.getBytes(StandardCharsets.UTF_8);
        byte[] tmpKey = new byte[8];
        if(bytes1.length < 8){
            throw new Exception("Key too short, it should be 8 byte");
        }else{
            System.arraycopy(bytes1,0,tmpKey,0,8);
        }

        //base64格式的key字符串转byte
        byte[] decodeBase64 = Base64.decode(encryptStr, Base64.DEFAULT);

        //设置Cipher对象
//        Cipher cipher = Cipher.getInstance(ALGORITHMS,new BouncyCastleProvider());
        //fix memory leak risk, using singleton model replace new BouncyCastleProvider every time
        Cipher cipher = Cipher.getInstance(ALGORITHMS,getInstance());
        cipher.init(Cipher.DECRYPT_MODE, new SecretKeySpec(tmpKey, KEY_ALGORITHM));

        //调用doFinal解密
        byte[] decryptBytes = cipher.doFinal(decodeBase64);
        return new String(decryptBytes);
    }


// this function is not using now, it prepare for future purpose,do not delete it
    /**
     * 加密
     *
     * @param content    加密的字符串
     * @param encryptKey key值
     */
    public static String encrypt(String content, String encryptKey) throws Exception {

        byte[] bytes1 = encryptKey.getBytes(StandardCharsets.UTF_8);
        byte[] tmpKey = new byte[8];
        if(bytes1.length < 8){
            throw new Exception("Key too short, it should be 8 byte");
        }else{
            System.arraycopy(bytes1,0,tmpKey,0,8);
        }

        //设置Cipher对象
//        Cipher cipher = Cipher.getInstance(ALGORITHMS,new BouncyCastleProvider());
        Cipher cipher = Cipher.getInstance(ALGORITHMS,getInstance());
        cipher.init(Cipher.ENCRYPT_MODE, new SecretKeySpec(tmpKey, KEY_ALGORITHM));

        //调用doFinal
        byte[] b = cipher.doFinal(content.getBytes(StandardCharsets.UTF_8));

        // 转base64
        return Base64.encodeToString(b,Base64.DEFAULT);

    }


    private static org.bouncycastle.jce.provider.BouncyCastleProvider bouncyCastleProvider = null;

    public static synchronized org.bouncycastle.jce.provider.BouncyCastleProvider getInstance() {
        if (bouncyCastleProvider == null) {
            bouncyCastleProvider = new org.bouncycastle.jce.provider.BouncyCastleProvider();
        }
        return bouncyCastleProvider;
    }


}

