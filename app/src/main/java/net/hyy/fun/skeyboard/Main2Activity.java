package net.hyy.fun.skeyboard;

import androidx.appcompat.app.AppCompatActivity;

import android.os.Bundle;
import android.util.Log;
import android.view.KeyEvent;
import android.view.View;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import net.hyy.fun.skeyboardlib.NativeHelper;
import net.hyy.fun.skeyboardlib.SafeKeyboard;

import java.util.ArrayList;
import java.util.List;

public class Main2Activity extends AppCompatActivity {

    private SafeKeyboard safeKeyboard;

//    static {
//        System.loadLibrary("securityKey");
////        System.loadLibrary("libsecurityKey");
//    }

    private EditText safeEdit;
    private EditText safeEdit2;
    private EditText safeEdit3;
    private List<EditText> editList = new ArrayList<>();

    String DES_KEY = "randromdata";


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        //设置当前activity不被录制以及截屏
        getWindow().setFlags(WindowManager.LayoutParams.FLAG_SECURE, WindowManager.LayoutParams.FLAG_SECURE);
        setContentView(R.layout.activity_main2);


        safeEdit = findViewById(R.id.safeEditText);
        safeEdit2 = findViewById(R.id.safeEditText2);
        safeEdit3 = findViewById(R.id.safeEditText3);

        safeEdit.setTag("number");
        editList.add(safeEdit);
        editList.add(safeEdit2);
        editList.add(safeEdit3);

        final Button clck = findViewById(R.id.feed_back);
        clck.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
//                clck.performHapticFeedback(HapticFeedbackConstants.LONG_PRESS, HapticFeedbackConstants.FLAG_IGNORE_GLOBAL_SETTING);

                Toast.makeText(Main2Activity.this,""+safeEdit2.getText().toString(),Toast.LENGTH_SHORT).show();

                String d2 = NativeHelper.getDecryptKey(String.valueOf(safeEdit2.getId()), "1212");
                Log.e("TTTTTTT",d2);
            }
        });
        LinearLayout keyboardContainer = findViewById(R.id.keyboardViewPlace);
        safeKeyboard = new SafeKeyboard(Main2Activity.this, keyboardContainer, editList,
                true, false, false);
       /* safeKeyboard.setDelDrawable(this.getResources().getDrawable(R.drawable.seckey_icon_del));
        safeKeyboard.setLowDrawable(this.getResources().getDrawable(R.drawable.seckey_icon_capital_default));
        safeKeyboard.setUpDrawable(this.getResources().getDrawable(R.drawable.seckey_icon_capital_selected));
        safeKeyboard.setLogoDrawable(this.getResources().getDrawable(R.mipmap.seckey_logo_zy));
        safeKeyboard.setHideDrawable(this.getResources().getDrawable(R.drawable.seckey_keyboard_done_));*/


    }


    // 当点击返回键时, 如果软键盘正在显示, 则隐藏软键盘并是此次返回无效
    @Override
    public boolean onKeyDown(int keyCode, KeyEvent event) {
        if (keyCode == KeyEvent.KEYCODE_BACK && event.getRepeatCount() == 0) {
            if (safeKeyboard.isShow()) {
                safeKeyboard.hideKeyboard();
                StringBuilder builder = new StringBuilder();
                builder.append(NativeHelper.getDecryptKey(String.valueOf(safeEdit.getId()), "1212")+"\n");
                builder.append(NativeHelper.getEncryptKey(String.valueOf(safeEdit.getId()), "1212")+"\n");
                builder.append(NativeHelper.getEncryptKeyDES(String.valueOf(safeEdit.getId()), DES_KEY,"1212")+"\n");
                builder.append(NativeHelper.getDecryptKey(String.valueOf(safeEdit2.getId()), "1212")+"\n");
                builder.append(NativeHelper.getEncryptKey(String.valueOf(safeEdit2.getId()), "1212")+"\n");
                builder.append(NativeHelper.getEncryptKeyDES(String.valueOf(safeEdit2.getId()), DES_KEY,"1212")+"\n");
                builder.append(NativeHelper.getDecryptKey(String.valueOf(safeEdit3.getId()), "1212")+"\n");
                builder.append(NativeHelper.getEncryptKey(String.valueOf(safeEdit3.getId()), "1212")+"\n");
                builder.append(NativeHelper.getEncryptKeyDES(String.valueOf(safeEdit3.getId()),DES_KEY, "1212"));
                ((TextView) findViewById(R.id.tv)).setText(builder.toString());
                return false;
            }

            return super.onKeyDown(keyCode, event);
        }
        return super.onKeyDown(keyCode, event);
    }


}
