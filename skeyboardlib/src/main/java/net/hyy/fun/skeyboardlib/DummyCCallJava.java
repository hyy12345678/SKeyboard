package net.hyy.fun.skeyboardlib;

public class DummyCCallJava {

    public static String encrypt(String content, String encryptKey){

        return "CallBack_from_c1_content:"+content+"_encryptKey:"+encryptKey;
    }

    public static String getDes(String content){

        return "CallBack_from_c2";
    }
}
