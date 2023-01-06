package net.hyy.fun.skeyboard;

import android.annotation.SuppressLint;
import android.content.Context;
import android.util.AttributeSet;
import android.view.KeyEvent;
import android.widget.EditText;

/**
 * Created by Mustang on 2019/2/11.
 */

@SuppressLint("AppCompatCustomView")
public class SafeEditText extends EditText {


    public SafeEditText(Context context, AttributeSet attrs) {
        super(context, attrs);
    }

    @Override
    public boolean onTextContextMenuItem(int id) {
        return true;
    }

    @Override
    public void setLongClickable(boolean longClickable) {
        super.setLongClickable(false);
    }

    @Override
    public boolean onKeyLongPress(int keyCode, KeyEvent event) {
        return false;
    }


}
