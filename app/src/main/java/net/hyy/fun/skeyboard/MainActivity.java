package net.hyy.fun.skeyboard;

import androidx.appcompat.app.AppCompatActivity;

import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;

import net.hyy.fun.skeyboardlib.NativeHelper;

public class MainActivity extends AppCompatActivity {

//    // Used to load the 'native-lib' library on application startup.
//    static {
//        System.loadLibrary("native-lib");
//    }



    TextView sample_text;
    Button btn_addKey;
    Button btn_deleteKey;
    Button btn_clearKey;
    Button btn_getEncryptKey;
    Button btn_getDecryptKey;
    Button btn_getEncryptKeyDES;


    String DUMMY_ID = "asdfqwer1234!@#$";

    String DES_KEY = "randromdata";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        // Example of a call to a native method

        sample_text = findViewById(R.id.sample_text);
        btn_addKey = findViewById(R.id.btn_addKey);
        btn_deleteKey = findViewById(R.id.btn_deleteKey);
        btn_clearKey = findViewById(R.id.btn_clearKey);
        btn_getEncryptKey = findViewById(R.id.btn_getEncryptKey);
        btn_getDecryptKey = findViewById(R.id.btn_getDecryptKey);
        btn_getEncryptKeyDES = findViewById(R.id.btn_getEncryptKeyDES);

        sample_text.setText(NativeHelper.stringFromJNI());

        btn_addKey.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                NativeHelper.addKey(DUMMY_ID,"c");
            }
        });

        btn_clearKey.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                NativeHelper.clearKey(DUMMY_ID);
            }
        });

        btn_deleteKey.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                NativeHelper.deleteKey(DUMMY_ID);
            }
        });

        btn_getDecryptKey.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                String decryptStr = NativeHelper.getDecryptKey(DUMMY_ID,"");
                sample_text.setText(decryptStr);
            }
        });

        btn_getEncryptKey.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                String encryptStr = NativeHelper.getEncryptKey(DUMMY_ID,"");
                sample_text.setText(encryptStr);
            }
        });

        btn_getEncryptKeyDES.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String encryptStr = NativeHelper.getEncryptKeyDES(DUMMY_ID,DES_KEY,"");
                sample_text.setText(encryptStr);
            }
        });


    }

//    /**
//     * A native method that is implemented by the 'native-lib' native library,
//     * which is packaged with this application.
//     */
//    public native String stringFromJNI();
}
