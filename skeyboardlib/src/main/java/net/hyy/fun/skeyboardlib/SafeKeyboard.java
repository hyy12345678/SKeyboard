package net.hyy.fun.skeyboardlib;

import android.annotation.SuppressLint;
import android.app.Service;
import android.content.Context;
import android.graphics.drawable.Drawable;
import android.inputmethodservice.Keyboard;
import android.inputmethodservice.KeyboardView;
import android.os.Build;
import android.os.Handler;
import android.os.Looper;
import android.os.SystemClock;
import android.os.Vibrator;
import android.text.Editable;
import android.util.DisplayMetrics;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.view.WindowManager;
import android.view.animation.Animation;
import android.view.animation.TranslateAnimation;
import android.view.inputmethod.InputMethodManager;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;


import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.util.ArrayList;
import java.util.LinkedList;
import java.util.List;
import java.util.Random;

/**
 * Created by Mustang on 2019/1/9
 */

public class SafeKeyboard {


    private static final String TAG = "SafeKeyboard";
    private Vibrator vibrator;

    private Context mContext;               //上下文

    private LinearLayout layout;
    private View keyContainer;              //自定义键盘的容器View
    private SafeKeyboardView keyboardView;  //键盘的View
    private Keyboard keyboardNumber;        //数字键盘
    private Keyboard keyboardLetter;        //字母键盘
    private Keyboard keyboardSymbol;        //符号键盘
    private static boolean isCapes = false;
    private boolean isShowStart = false;
    private boolean isHideStart = false;
    private int keyboardType = 1;
    private static final long HIDE_TIME = 300;
    private static final long SHOW_DELAY = 50;
    private static final long SHOW_TIME = 300;
    private static final long DELAY_TIME = 100;
    private Handler showHandler = new Handler(Looper.getMainLooper());
    private Handler hEndHandler = new Handler(Looper.getMainLooper());
    private Handler sEndHandler = new Handler(Looper.getMainLooper());
    private Drawable delDrawable;
    private Drawable lowDrawable;
    private Drawable upDrawable;
    private Drawable logoDrawable;
    private Drawable downDrawable;
    private int keyboardContainerResId;
    private int keyboardResId;

    private TranslateAnimation showAnimation;
    private TranslateAnimation hideAnimation;
    private long lastTouchTime;
    private EditText mEditText;
    private List<EditText> editTextList;
    private boolean isNumberRandom;
    private boolean isLetterRandom;
    private boolean isSymbolRandom;


    private final View mScrollLayout;
    private int[] originalScrollPosInScr;
    private int[] originalScrollPosInPar;
    private int mScreenWidth;
    private int mScreenHeight;
    private float toBackSize;

    private boolean mPreviewEnabled = false;



    public SafeKeyboard(Context mContext, LinearLayout layout, List<EditText> editTextList,View mScrollLayout,
                        boolean isNumberRandom, boolean isLetterRandom, boolean isSymbolRandom,boolean isPreviewEnable) {
//        this.mContext = mContext;
//        this.layout = layout;
//        this.editTextList = editTextList;
//        this.keyboardContainerResId = R.layout.seckey_layout_keyboard_containor;
//        this.keyboardResId = LayoutInflater.from(mContext).inflate(R.layout.seckey_layout_keyboard_containor, null).findViewById(R.id.safeKeyboardLetter).getId();
//        this.isNumberRandom = isNumberRandom;
//        this.isLetterRandom = isLetterRandom;
//        this.isSymbolRandom = isSymbolRandom;
//        vibrator = (Vibrator) mContext.getSystemService(Service.VIBRATOR_SERVICE);
//        delDrawable = mContext.getResources().getDrawable(R.drawable.seckey_icon_del);
//        lowDrawable = mContext.getResources().getDrawable(R.drawable.seckey_icon_capital_default);
//        upDrawable = mContext.getResources().getDrawable(R.drawable.seckey_icon_capital_selected);
//        logoDrawable = mContext.getResources().getDrawable(R.drawable.seckey_logo_anquan);
//        downDrawable = mContext.getResources().getDrawable(R.drawable.seckey_keyboard_done_);
//        initKeyboard();
//        initAnimation();
//        addListeners();

        this(mContext, layout, editTextList,mScrollLayout,
                R.layout.seckey_layout_keyboard_containor,
                LayoutInflater.from(mContext).inflate(R.layout.seckey_layout_keyboard_containor, null).findViewById(R.id.safeKeyboardLetter).getId(),
                mContext.getResources().getDrawable(R.drawable.seckey_icon_del),
                mContext.getResources().getDrawable(R.drawable.seckey_icon_capital_default),
                mContext.getResources().getDrawable(R.drawable.seckey_icon_capital_selected),
                mContext.getResources().getDrawable(R.drawable.seckey_logo_anquan),
                mContext.getResources().getDrawable(R.drawable.seckey_keyboard_done_),
                isNumberRandom, isLetterRandom, isSymbolRandom,isPreviewEnable
        );

    }

    SafeKeyboard(Context mContext, LinearLayout layout, List<EditText> editTextList,View mScrollLayout,
                 int id, int keyId, Drawable del, Drawable low, Drawable up, Drawable logo, Drawable down,
                 boolean isNumberRandom, boolean isLetterRandom, boolean isSymbolRandom,boolean isPreviewEnable) {
        this.mContext = mContext;
        this.layout = layout;
        this.editTextList = editTextList;
        this.keyboardContainerResId = id;
        this.keyboardResId = keyId;
        this.delDrawable = del;
        this.lowDrawable = low;
        this.upDrawable = up;
        this.logoDrawable = logo;
        this.downDrawable = down;
        this.isNumberRandom = isNumberRandom;
        this.isLetterRandom = isLetterRandom;
        this.isSymbolRandom = isSymbolRandom;
        vibrator = (Vibrator) mContext.getSystemService(Service.VIBRATOR_SERVICE);

        this.mScrollLayout = mScrollLayout;

        originalScrollPosInScr = new int[]{0, 0, 0, 0};
        originalScrollPosInPar = new int[]{0, 0, 0, 0};

        // 获取 WindowManager 实例, 得到屏幕的操作权
        WindowManager wm = (WindowManager) mContext.getSystemService(Context.WINDOW_SERVICE);
        if (wm != null) {
            // 给 metrics 赋值
            DisplayMetrics metrics = new DisplayMetrics();
            wm.getDefaultDisplay().getMetrics(metrics);
            // 设备屏幕的宽度,高度变量
            mScreenWidth = metrics.widthPixels;
            mScreenHeight = metrics.heightPixels;
        }

        this.mPreviewEnabled = isPreviewEnable;


        initKeyboard();
        initAnimation();
        addListeners();
    }


    private void initAnimation() {
        showAnimation = new TranslateAnimation(Animation.RELATIVE_TO_SELF, 0.0f,
                Animation.RELATIVE_TO_SELF, 0.0f, Animation.RELATIVE_TO_SELF
                , 1.0f, Animation.RELATIVE_TO_SELF, 0.0f);
        hideAnimation = new TranslateAnimation(Animation.RELATIVE_TO_SELF, 0.0f,
                Animation.RELATIVE_TO_SELF, 0.0f, Animation.RELATIVE_TO_SELF
                , 0.0f, Animation.RELATIVE_TO_SELF, 1.0f);
        showAnimation.setDuration(SHOW_TIME);
        hideAnimation.setDuration(HIDE_TIME);

        showAnimation.setAnimationListener(new Animation.AnimationListener() {
            @Override
            public void onAnimationStart(Animation animation) {
                isShowStart = true;
                // 在这里设置可见, 会出现第一次显示键盘时直接闪现出来, 没有动画效果, 后面正常
                // keyContainer.setVisibility(View.VISIBLE);
                // 动画持续时间 SHOW_TIME 结束后, 不管什么操作, 都需要执行, 把 isShowStart 值设为 false; 否则
                // 如果 onAnimationEnd 因为某些原因没有执行, 会影响下一次使用
                sEndHandler.removeCallbacks(showEnd);
                sEndHandler.postDelayed(showEnd, SHOW_TIME);
            }

            @Override
            public void onAnimationEnd(Animation animation) {
                isShowStart = false;
                keyContainer.clearAnimation();
            }

            @Override
            public void onAnimationRepeat(Animation animation) {
            }
        });

        hideAnimation.setAnimationListener(new Animation.AnimationListener() {
            @Override
            public void onAnimationStart(Animation animation) {
                isHideStart = true;
                // 动画持续时间 HIDE_TIME 结束后, 不管什么操作, 都需要执行, 把 isHideStart 值设为 false; 否则
                // 如果 onAnimationEnd 因为某些原因没有执行, 会影响下一次使用
                hEndHandler.removeCallbacks(hideEnd);
                hEndHandler.postDelayed(hideEnd, HIDE_TIME);
            }

            @Override
            public void onAnimationEnd(Animation animation) {
                isHideStart = false;
                if (keyContainer.getVisibility() != View.GONE) {
                    keyContainer.setVisibility(View.GONE);
                }
                keyContainer.clearAnimation();
            }

            @Override
            public void onAnimationRepeat(Animation animation) {
            }
        });
    }

    @SuppressLint("ClickableViewAccessibility")
    private void initKeyboard() {
        keyContainer = LayoutInflater.from(mContext).inflate(keyboardContainerResId, layout, true);
        keyContainer.setVisibility(View.GONE);
        keyboardNumber = new Keyboard(mContext, R.xml.seckey_keyboard_num);            //实例化数字键盘
        keyboardLetter = new Keyboard(mContext, R.xml.seckey_keyboard_letter);         //实例化字母键盘
        keyboardSymbol = new Keyboard(mContext, R.xml.seckey_keyboard_symbol);         //实例化符号键盘
        // 由于符号键盘与字母键盘共用一个KeyBoardView, 所以不需要再为符号键盘单独实例化一个KeyBoardView
        keyboardView = keyContainer.findViewById(keyboardResId);
        keyboardView.setDelDrawable(delDrawable);
        keyboardView.setLowDrawable(lowDrawable);
        keyboardView.setUpDrawable(upDrawable);
        keyboardView.setLogoDrawable(logoDrawable);
        keyboardView.setHideDrawable(downDrawable);
        keyboardView.setEnabled(true);
        keyboardView.setPreviewEnabled(mPreviewEnabled && false);
        keyboardView.setOnKeyboardActionListener(listener);

        ImageView done = keyContainer.findViewById(R.id.keyboardDone);
        done.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (isKeyboardShown()) {
                    hideKeyboard();
                }
            }
        });

        keyboardView.setOnTouchListener(new View.OnTouchListener() {
            @Override
            public boolean onTouch(View v, MotionEvent event) {
                return event.getAction() == MotionEvent.ACTION_MOVE;
            }
        });
    }

    /**
     * 键盘数字随机切换
     */
    private void randomNumbers() {
        List<Keyboard.Key> keyList = keyboardNumber.getKeys();
        // 查找出0-9的数字键
        List<Keyboard.Key> newkeyList = new ArrayList<Keyboard.Key>();
        for (int i = 0; i < keyList.size(); i++) {
            if (keyList.get(i).label != null
                    && isNumber(keyList.get(i).label.toString())) {
                newkeyList.add(keyList.get(i));
            }
        }
        // 数组长度
        int count = newkeyList.size();
        // 结果集
        List<KeyModel> resultList = new ArrayList<KeyModel>();
        // 用一个LinkedList作为中介
        LinkedList<KeyModel> temp = new LinkedList<KeyModel>();
        // 初始化temp
        for (int i = 0; i < count; i++) {
            temp.add(new KeyModel(48 + i, i + ""));
        }
       /* temp.add(new KeyModel(44 , "" + (char)44));
        temp.add(new KeyModel(46 , "" + (char)46));*/
        // 取数
        Random rand = new Random();
        for (int i = 0; i < count; i++) {
            int num = rand.nextInt(count - i);
            resultList.add(new KeyModel(temp.get(num).getCode(), temp.get(num)
                    .getLable()));
            temp.remove(num);
        }
        for (int i = 0; i < newkeyList.size(); i++) {
            newkeyList.get(i).label = resultList.get(i).getLable();
            newkeyList.get(i).codes[0] = resultList.get(i).getCode();
        }

        //keyboardView.setKeyboard(keyDig);
    }

    private boolean isNumber(String str) {
        String numStr = mContext.getString(R.string.seckey_zeroTonine);
        return numStr.contains(str.toLowerCase());
    }

    // 设置键盘点击监听
    private KeyboardView.OnKeyboardActionListener listener = new KeyboardView.OnKeyboardActionListener() {

        @SuppressLint("MissingPermission")
        @Override
        public void onPress(int primaryCode) {
            vibrator.vibrate(new long[]{0, 50}, -1);
            if (keyboardType == 3) {
                keyboardView.setPreviewEnabled(mPreviewEnabled && false);
            } else {
                keyboardView.setPreviewEnabled(mPreviewEnabled && true);
                if (primaryCode == -1 || primaryCode == -5 || primaryCode == 32 || primaryCode == -2
                        || primaryCode == 100860 || primaryCode == -35 || primaryCode == -7 || primaryCode == -8) {
                    keyboardView.setPreviewEnabled(mPreviewEnabled && false);
                } else {
                    keyboardView.setPreviewEnabled(mPreviewEnabled && true);
                }
            }
        }

        @Override
        public void onRelease(int primaryCode) {
        }

        @Override
        public void onKey(int primaryCode, int[] keyCodes) {
            try {
                Editable editable = mEditText.getText();
                int start = mEditText.getText().length();
                int end = start;
                mEditText.setSelection(end);
                if (primaryCode == Keyboard.KEYCODE_CANCEL) {
                    // 隐藏键盘
                    hideKeyboard();

                } else if (primaryCode == Keyboard.KEYCODE_DELETE || primaryCode == -35) {

                    // 回退键,删除字符
                    if (editable != null && editable.length() > 0) {
                        if (start == end) { //光标开始和结束位置相同, 即没有选中内容
                            editable.delete(start - 1, start);
                            NativeHelper.deleteKey(String.valueOf(mEditText.getId()));
                        } else { //光标开始和结束位置不同, 即选中EditText中的内容
                            editable.delete(start, end);

                        }
                    }
                } else if (primaryCode == Keyboard.KEYCODE_SHIFT) {
                    // 大小写切换
                    changeKeyboardLetterCase();
                    // 重新setKeyboard, 进而系统重新加载, 键盘内容才会变化(切换大小写)
                    keyboardType = 1;
                    switchKeyboard();
                } else if (primaryCode == Keyboard.KEYCODE_MODE_CHANGE) {
                    // 数字与字母键盘互换
                    if (keyboardType == 3) { //当前为数字键盘
                        if (mEditText.getTag() != null) {
                            return;
                        }
                        keyboardType = 1;
                    } else {        //当前不是数字键盘
                        keyboardType = 3;
                    }
                    switchKeyboard();
                } else if (primaryCode == -7) {

                } else if (primaryCode == -8) {
                    hideKeyboard();
                } else if (primaryCode == 100860) {
                    // 字母与符号切换
                    if (keyboardType == 2) { //当前是符号键盘
                        keyboardType = 1;
                    } else {        //当前不是符号键盘, 那么切换到符号键盘
                        keyboardType = 2;
                    }
                    switchKeyboard();
                } else {
                    // 输入键盘值
                    // editable.insert(start, Character.toString((char) primaryCode));
                    if (mEditText.getText().toString().trim().length() <= 0) {
                        NativeHelper.clearKey(String.valueOf(mEditText.getId()));
                    }
                    editable.replace(start, end, "*");
                    NativeHelper.addKey(String.valueOf(mEditText.getId()), Character.toString((char) primaryCode));
                }
            } catch (Exception e) {
                e.printStackTrace();
            }
        }

        @Override
        public void onText(CharSequence text) {


        }

        @Override
        public void swipeLeft() {
        }

        @Override
        public void swipeRight() {
        }

        @Override
        public void swipeDown() {
        }

        @Override
        public void swipeUp() {
        }
    };


    private void switchKeyboard() {
        switch (keyboardType) {

            case 1:
                if (isLetterRandom && !isCapes) {
                    randomLetter();
                }
                keyboardView.setKeyboard(keyboardLetter);
                break;
            case 2:
                if (isSymbolRandom) {
                    randomSymbolkey();
                }

                keyboardView.setKeyboard(keyboardSymbol);
                break;
            case 3:
                if (isNumberRandom) {
                    randomNumbers();
                }
                keyboardView.setKeyboard(keyboardNumber);
                break;
            default:
                Log.e(TAG, "ERROR keyboard type");
                break;
        }
    }

    private void changeKeyboardLetterCase() {
        List<Keyboard.Key> keyList = keyboardLetter.getKeys();
        if (isCapes) {
            for (Keyboard.Key key : keyList) {
                if (key.label != null && isUpCaseLetter(key.label.toString())) {
                    key.label = key.label.toString().toLowerCase();
                    key.codes[0] += 32;
                }
            }

        } else {
            for (Keyboard.Key key : keyList) {
                if (key.label != null && isLowCaseLetter(key.label.toString())) {
                    key.label = key.label.toString().toUpperCase();
                    key.codes[0] -= 32;
                }
            }

        }
        isCapes = !isCapes;
        keyboardView.setCap(isCapes);


    }

    public void hideKeyboard() {
        keyContainer.clearAnimation();
        keyContainer.startAnimation(hideAnimation);
    }

    /**
     * 只起到延时开始显示的作用
     */
    private final Runnable showRun = new Runnable() {
        @Override
        public void run() {
            showKeyboard();
        }
    };

    private final Runnable hideEnd = new Runnable() {
        @Override
        public void run() {
            isHideStart = false;

            doScrollLayoutBack(true, null);

            if (keyContainer.getVisibility() != View.GONE) {
                keyContainer.setVisibility(View.GONE);
            }
        }
    };

    private final Runnable showEnd = new Runnable() {
        @Override
        public void run() {
            isShowStart = false;
            // 在迅速点击不同输入框时, 造成自定义软键盘和系统软件盘不停的切换, 偶尔会出现停在使用系统键盘的输入框时, 没有隐藏
            // 自定义软键盘的情况, 为了杜绝这个现象, 加上下面这段代码
            if (!mEditText.isFocused()) {
                hideKeyboard();
            }

            doScrollLayout();
        }
    };

    private void showKeyboard() {
        //keyboardType = 1;
        //  keyboardView.setKeyboard(keyboardLetter);
        keyContainer.setVisibility(View.VISIBLE);
        keyContainer.clearAnimation();
        keyContainer.startAnimation(showAnimation);
    }

    private boolean isLowCaseLetter(String str) {
        String letters = "abcdefghijklmnopqrstuvwxyz";
        return letters.contains(str);
    }

    private boolean isUpCaseLetter(String str) {
        String letters = "ABCDEFGHIJKLMNOPQRSTUVWXYZ";
        return letters.contains(str);
    }


    @SuppressLint("ClickableViewAccessibility")
    private void addListeners() {

        for (int i = 0; i < editTextList.size(); i++) {
            editTextList.get(i).setOnTouchListener(new View.OnTouchListener() {
                @Override
                public boolean onTouch(View v, MotionEvent event) {
                    if (v.getTag() == null) {
                        for (int j = 0; j < keyboardNumber.getKeys().size(); j++) {
                            if (" ".equals(keyboardNumber.getKeys().get(j).label)) {
                                keyboardNumber.getKeys().get(j).label = "ABC";
                            }

                        }
                        if (isLetterRandom) {
                            randomLetter();
                            keyboardView.setKeyboard(keyboardLetter);
                        } else {
                            keyboardView.setKeyboard(keyboardLetter);
                        }

                    } else {
                        for (int j = 0; j < keyboardNumber.getKeys().size(); j++) {
                            if ("ABC".equals(keyboardNumber.getKeys().get(j).label)) {
                                keyboardNumber.getKeys().get(j).label = " ";
                            }

                        }
                        if (isNumberRandom) {
                            randomNumbers();
                            keyboardView.setKeyboard(keyboardNumber);
                        } else {
                            keyboardView.setKeyboard(keyboardNumber);
                        }


                    }
                    if (event.getAction() == MotionEvent.ACTION_UP) {
                        hideSystemKeyBoard((EditText) v);
                        if (isKeyboardShown()) {
                            return false;
                        }
                        if (!isKeyboardShown()) {
                            getOriginalScrollLayoutPos();
                            showHandler.removeCallbacks(showRun);
                            showHandler.postDelayed(showRun, SHOW_DELAY);
                        }
                    }
                    return false;
                }

            });

            editTextList.get(i).setOnFocusChangeListener(new View.OnFocusChangeListener() {
                @Override
                public void onFocusChange(View v, boolean hasFocus) {
                    // boolean result = isValidTouch();
                    if (v instanceof EditText) {
                        if (!hasFocus) {
                            boolean isFocus = true;
                            for (int j = 0; j < editTextList.size(); j++) {
                                boolean flag = !editTextList.get(j).isFocused();
                                isFocus = isFocus && flag;
                            }
                            if (isFocus) {
                                if (isKeyboardShown()) {
                                    getOriginalScrollLayoutPos();
                                    hideKeyboard();
                                }

                            }
                        } else {
                            ((EditText) v).setText("");
                            if (!isKeyboardShown()) {
                                getOriginalScrollLayoutPos();
                                showHandler.removeCallbacks(showRun);
                                showHandler.postDelayed(showRun, SHOW_DELAY);
                            }
                        }
                    }
                }
            });
        }

    }

    public boolean isShow() {
        return isKeyboardShown();
    }

    //隐藏系统键盘关键代码
    private void hideSystemKeyBoard(EditText edit) {
        this.mEditText = edit;
        InputMethodManager imm = (InputMethodManager) this.mContext.getSystemService(Context.INPUT_METHOD_SERVICE);
        if (imm == null) {
            return;
        }
        boolean isOpen = imm.isActive();
        if (isOpen) {
            imm.hideSoftInputFromWindow(edit.getWindowToken(), 0);
        }

        int currentVersion = Build.VERSION.SDK_INT;
        String methodName = null;
        if (currentVersion >= 16) {
            methodName = "setShowSoftInputOnFocus";
        } else if (currentVersion >= 14) {
            methodName = "setSoftInputShownOnFocus";
        }

        if (methodName == null) {
            edit.setInputType(0);
        } else {
            try {
                Method setShowSoftInputOnFocus = EditText.class.getMethod(methodName, Boolean.TYPE);
                setShowSoftInputOnFocus.setAccessible(true);
                setShowSoftInputOnFocus.invoke(edit, Boolean.FALSE);
            } catch (NoSuchMethodException e) {
                edit.setInputType(0);
                e.printStackTrace();
            } catch (IllegalAccessException | InvocationTargetException | IllegalArgumentException e) {
                e.printStackTrace();
            }
        }
    }

    private boolean isKeyboardShown() {
        return keyContainer.getVisibility() == View.VISIBLE;
    }

    private boolean isValidTouch() {
        long thisTouchTime = SystemClock.elapsedRealtime();
        if (thisTouchTime - lastTouchTime > 500) {
            lastTouchTime = thisTouchTime;
            return true;
        }
        lastTouchTime = thisTouchTime;
        return false;
    }

    public void setDelDrawable(Drawable delDrawable) {
        this.delDrawable = delDrawable;
        keyboardView.setDelDrawable(delDrawable);
    }

    public void setLowDrawable(Drawable lowDrawable) {
        this.lowDrawable = lowDrawable;
        keyboardView.setLowDrawable(lowDrawable);
    }

    public void setUpDrawable(Drawable upDrawable) {
        this.upDrawable = upDrawable;
        keyboardView.setUpDrawable(upDrawable);
    }


    private void randomLetter() {
        List<Keyboard.Key> keyList = keyboardLetter.getKeys();
        // 查找出a-z的数字键
        List<Keyboard.Key> newkeyList = new ArrayList<Keyboard.Key>();
        for (int i = 0; i < keyList.size(); i++) {
            if (keyList.get(i).label != null
                    && isword(keyList.get(i).label.toString())) {
                newkeyList.add(keyList.get(i));
            }
        }
        // 数组长度
        int count = newkeyList.size();
        // 结果集
        List<KeyModel> resultList = new ArrayList<KeyModel>();
        // 用一个LinkedList作为中介
        LinkedList<KeyModel> temp = new LinkedList<KeyModel>();
        // 初始化temp
        for (int i = 0; i < count; i++) {
            temp.add(new KeyModel(97 + i, "" + (char) (97 + i)));
        }
        //temp.add(new KeyModel(64, "" + (char) 64));//.
        // 取数
        Random rand = new Random();
        for (int i = 0; i < count; i++) {
            int num = rand.nextInt(count - i);
            resultList.add(new KeyModel(temp.get(num).getCode(), temp.get(num)
                    .getLable()));
            temp.remove(num);
        }
        for (int i = 0; i < newkeyList.size(); i++) {
            newkeyList.get(i).label = resultList.get(i).getLable();
            newkeyList.get(i).codes[0] = resultList.get(i).getCode();
        }

        //keyboardView.setKeyboard(keyAlp);
    }

    private boolean isword(String str) {
        String wordstr = "abcdefghijklmnopqrstuvwxyz";
        if (wordstr.indexOf(str.toLowerCase()) > -1) {
            return true;
        }
        return false;
    }

    /**
     * 标点符号键盘-随机
     */

    private void randomSymbolkey() {
        List<Keyboard.Key> keyList = keyboardSymbol.getKeys();

        // 查找出标点符号的数字键
        List<Keyboard.Key> newkeyList = new ArrayList<Keyboard.Key>();
        for (int i = 0; i < keyList.size(); i++) {
            if (keyList.get(i).label != null
                    && isInterpunction(keyList.get(i).label.toString())) {
                newkeyList.add(keyList.get(i));
            }
        }
        // 数组长度
        int count = newkeyList.size();
        // 结果集
        List<KeyModel> resultList = new ArrayList<KeyModel>();
        // 用一个LinkedList作为中介
        LinkedList<KeyModel> temp = new LinkedList<KeyModel>();

        // 初始化temp
        temp.add(new KeyModel(33, "" + (char) 33));
        temp.add(new KeyModel(34, "" + (char) 34));
        temp.add(new KeyModel(35, "" + (char) 35));
        temp.add(new KeyModel(36, "" + (char) 36));
        temp.add(new KeyModel(37, "" + (char) 37));
        temp.add(new KeyModel(38, "" + (char) 38));
        temp.add(new KeyModel(39, "" + (char) 39));
        temp.add(new KeyModel(40, "" + (char) 40));
        temp.add(new KeyModel(41, "" + (char) 41));
        temp.add(new KeyModel(42, "" + (char) 42));
        temp.add(new KeyModel(43, "" + (char) 43));
        temp.add(new KeyModel(45, "" + (char) 45));
        temp.add(new KeyModel(47, "" + (char) 47));
        temp.add(new KeyModel(58, "" + (char) 58));
        temp.add(new KeyModel(59, "" + (char) 59));
        temp.add(new KeyModel(60, "" + (char) 60));
        temp.add(new KeyModel(61, "" + (char) 61));
        temp.add(new KeyModel(62, "" + (char) 62));
        temp.add(new KeyModel(63, "" + (char) 63));
        temp.add(new KeyModel(91, "" + (char) 91));
        temp.add(new KeyModel(92, "" + (char) 92));
        temp.add(new KeyModel(93, "" + (char) 93));
        temp.add(new KeyModel(94, "" + (char) 94));
        temp.add(new KeyModel(95, "" + (char) 95));
        temp.add(new KeyModel(96, "" + (char) 96));
        temp.add(new KeyModel(123, "" + (char) 123));
        temp.add(new KeyModel(124, "" + (char) 124));
        temp.add(new KeyModel(125, "" + (char) 125));
        temp.add(new KeyModel(126, "" + (char) 126));

        // 取数
        Random rand = new Random();
        for (int i = 0; i < count; i++) {
            int num = rand.nextInt(count - i);
            resultList.add(new KeyModel(temp.get(num).getCode(), temp.get(num)
                    .getLable()));
            temp.remove(num);
        }
        for (int i = 0; i < newkeyList.size(); i++) {
            newkeyList.get(i).label = resultList.get(i).getLable();
            newkeyList.get(i).codes[0] = resultList.get(i).getCode();
        }

        //keyboardView.setKeyboard(keyPun);
    }

    private boolean isInterpunction(String str) {
        String wordstr = "!\"#$%&()*+-\\/:;<=>?`'^_[]{|}~";
        if (wordstr.indexOf(str) > -1) {
            return true;
        }
        return false;
    }

    public void setLogoDrawable(Drawable logoDrawable) {
        this.logoDrawable = logoDrawable;
        keyboardView.setLogoDrawable(logoDrawable);
    }

    public void setHideDrawable(Drawable downDrawable) {
        this.downDrawable = downDrawable;
        keyboardView.setHideDrawable(downDrawable);
    }


    /**
     * 更新 mScrollLayout 原始位置, 且只获取一次
     */
    private void getOriginalScrollLayoutPos() {
        if (originalScrollPosInScr[0] == 0 && originalScrollPosInScr[1] == 0) {
            int[] pos = new int[]{0, 0};
            mScrollLayout.getLocationOnScreen(pos);
            originalScrollPosInScr[0] = pos[0];
            originalScrollPosInScr[1] = pos[1];
            originalScrollPosInScr[2] = pos[0] + mScrollLayout.getWidth();
            originalScrollPosInScr[3] = pos[1] + mScrollLayout.getHeight();
        }

        if (originalScrollPosInPar[0] == 0 && originalScrollPosInPar[1] == 0
                && originalScrollPosInPar[2] == 0 && originalScrollPosInPar[3] == 0) {
            originalScrollPosInPar[0] = mScrollLayout.getLeft();
            originalScrollPosInPar[1] = mScrollLayout.getTop();
            originalScrollPosInPar[2] = mScrollLayout.getRight();
            originalScrollPosInPar[3] = mScrollLayout.getBottom();
        }
    }


    private void doScrollLayout() {
        // 计算 SafeKeyboard 显示后是否会遮挡住 EditText
        editNeedScroll(mEditText);
    }

    private void editNeedScroll(EditText mEditText) {
        int keyboardHeight = keyContainer.getHeight();      // 获取键盘布局的高度
        int keyStartY = mScreenHeight - keyboardHeight;
        int[] position = new int[2];
        mEditText.getLocationOnScreen(position);
        int mEditTextBottomY = position[1] + mEditText.getHeight();
        if (mEditTextBottomY > keyStartY) {
            // 说明这个 EditText 的底部在 键盘 View 顶部以下, 即 EditText 被键盘遮挡了
            final float to = keyStartY - mEditTextBottomY - 10; // 为负值, 需要往上移动的距离, 往上为负值, 往下为正值
            if (position[1] + to < originalScrollPosInScr[1]) {
                // 说明, scrollLayout 被往上顶起之后, EditText 所在位置可能会被 scrollLayout 上面的其他 View 遮挡或者重合了导致显示不准确,
                // 那么顶起操作在这里就显示不合适了, 所以这里最好是添加一个长文本显示功能
                // 说明往上顶起之后 mEditText 会被遮挡, 即 mEditText 的 top 距离顶部的距离 小于 要移动的距离
                // 这里就不需要顶起了, 需要显示一个长文本显示页面
                // TODO... 添加一个长文本显示功能, 不过这里的长文本显示似乎没有什么意义
                return;
            }
            toBackSize = to;
            mScrollLayout.animate().translationYBy(toBackSize).setDuration(150).start();
        }
    }


    private boolean doScrollLayoutBack(final boolean isHide, EditText mEditText) {
        int thisScrollY = 0;
        if (!isHide && mEditText != null) {
            // 这种情况说明是点击了一个 EditText, 则需要判断是否需要移动 mScrollLayout 来适应 SafeKeyboard 的显示
            int[] mEditPos = new int[2];
            mEditText.getLocationOnScreen(mEditPos);
            Log.e("SafeKeyboard_Scroll", "0: " + mEditPos[0] + ", 1: " + mEditPos[1]);

            int keyboardHeight = keyContainer.getHeight();
            int keyStartY = mScreenHeight - keyboardHeight;
            getOriginalScrollLayoutPos();

            if (mEditText.getHeight() + 10 > keyStartY - originalScrollPosInScr[1]) {
                // mEditText 的高度 大于 SafeKeyboard 上边界到 mScrollLayout 上边界的距离, 即 mEditText 无法完全显示
                // TODO... 添加一个长文本输入功能

                return false;
            } else {
                // 可以正常显示
                if (mEditPos[1] < originalScrollPosInScr[1]) {
                    // 说明当前的 mEditText 的 top 位置已经被其他布局遮挡, 需要布局往下滑动一点, 使 mEditText 可以完全显示
                    thisScrollY = originalScrollPosInScr[1] - mEditPos[1] + 10; // 正值
                } else if (mEditPos[1] + mEditText.getHeight() > keyStartY) {
                    // 说明当前的 mEditText 的 bottom 位置已经被其他布局遮挡, 需要布局往上滑动一点, 使 mEditText 可以完全显示
                    thisScrollY = keyStartY - mEditPos[1] - mEditText.getHeight(); //负值
                } else {
                    // 各项均正常, 不需要重新滑动
                    Log.i("SafeKeyboard_LOG", "Need not to scroll");
                    return false;
                }
            }
        }

        toBackSize += thisScrollY;
        if (isHide) {
            mScrollLayout.animate().setDuration(150).translationYBy(-toBackSize).start();
            toBackSize = 0;
        } else {
            mScrollLayout.animate().setDuration(150).translationYBy(thisScrollY).start();
        }

        return true;
    }
}
