package net.hyy.fun.skeyboardlib;

public class NativeHelper {

    /**
     * A native method that is implemented by the 'native-lib' native library,
     * which is packaged with this application.
     */
    public static final native String stringFromJNI();


    public static final native void addKey(String id, String text);

    public static final native void deleteKey(String id);

    public static final native void clearKey(String id);

    public static final native String getEncryptKey(String id, String timestamp);

    public static final native String getDecryptKey(String id, String timestamp);


    public static final native String getEncryptKeyDES(String id, String key,String timestamp);



    // Used to load the 'native-lib' library on application startup.
    static {
        System.loadLibrary("skeyboad-lib");
    }




}
