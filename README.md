# **SKeyboard**
Android自定义安全软键盘，jni方式MD5,DES(DES/ECB/PKCS7Padding)存储加密，ui支持可扩展，使用方便简单安全，防录制，去除内容回显等。

main分支在c层通过jni回调java层算法库进行加密，理论上明文在jni回调时还是会出现在java内存中。
openssl分支在c++层使用openssl的加密库算法，实现真正意义的在java内存中不出现密码明文。

- 依赖

```java
    //main分支
    implementation project(':skeyboardlib')//以module方式引入
    implementation 'org.bouncycastle:bcprov-jdk16:1.46'//使用的加密算法库
    
    //openssl分支
    implementation project(':skeyboardlib')//以module方式引入
    
```


- 使用方式

```java
 safeEdit.setTag("number");
 editList.add(safeEdit);
 editList.add(safeEdit2);
 editList.add(safeEdit3);
 safeKeyboard = new SafeKeyboard(MainActivity.this, keyboardContainer, editList,scroll_layout,
                true, false, false, false);
                
 //onDestroy时调用release方法，确保C++层清除密码记录
 safeKeyboard.release();
 
```
- editList：需要用到安全键盘的EditText，可以添加进此List，
- scroll_layout：目标 EditText 父布局 View
  多个 EditText 共用 SafeKeyboard 但其直接父布局不是同一个 View 时, 传入公共父布局
  传入目的是：当 EditText 需要被顶起的时候, 顶起该布局, 以达到输入时可以显示已输入内容的功能
  注意, 可以是 EditText 本身, 不过需要传入 View 类型的 EditText
- 后面四个布尔值分别表示是否支持数字是否随机，字符是否随机，符号是否随机，是否打开输入预览。
- safeEdit.setTag("number"); 添加此tag，表示此EditText只支持数字输入。不设置表示支持数字，字母，符号三种输入方式。


- 注意事项

```java

//混淆中添加如下：
-keep class net.hyy.fun.skeyboardlib.** {*;}

//build.gradle中增加如下：
packagingOptions {
    doNotStrip "*/arm64-v8a/*.so"
    doNotStrip "*/armeabi-v7a/*.so"
}

```                 



