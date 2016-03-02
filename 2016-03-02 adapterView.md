#adpterview 不支持addview
adpterview （如listView）不支持调用removeView 和 addView操作。调用后抛出以下异常。<br/>
03-02 14:25:48.424: E/AndroidRuntime(3950): java.lang.UnsupportedOperationException: removeView(View) is not supported in AdapterView<br/>
03-02 14:25:48.424: E/AndroidRuntime(3950): 	at android.widget.AdapterView.removeView(AdapterView.java:734)
#adpterview 中的item
一个很奇怪的现象，同一个view实例可以直接当做两个listview 的item进行返回而不会抛任何异常。(两个listview添加同一个banner实例).暂未知道原因
