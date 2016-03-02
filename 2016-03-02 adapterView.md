#adpterview 不支持addview
adpterview （如listView）不支持调用removeView 和 addView操作。调用后抛出以下异常。<br/>
03-02 14:25:48.424: E/AndroidRuntime(3950): java.lang.UnsupportedOperationException: removeView(View) is not supported in AdapterView<br/>
03-02 14:25:48.424: E/AndroidRuntime(3950): 	at android.widget.AdapterView.removeView(AdapterView.java:734)
#adpterview 中的item
一个很奇怪的现象，同一个view实例可以直接当做两个listview 的item进行返回而不会抛任何异常。(两个listview添加同一个banner实例).暂未知道原因
#adpterview 混排
总有些产品要求与listview不同<br/>
例如:<br/>
  上面是固定的功能入口，下面是筛选项及列表,还要求列表可刷新(what a sbd)<br/>
  解决方案
  1. 线性布局，动态添加数据 LinearLayout addView 
  2. 线性布局, 嵌套高度动态变化的ListView(需重写)
  3. ListView 定义不同的子View
      getViewTypeCount()
      getItemViewType(int position)<br/>
  第三种性能会比较好，实现的工作量较大
  
  

