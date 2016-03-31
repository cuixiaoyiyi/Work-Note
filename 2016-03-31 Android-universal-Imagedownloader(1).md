学习开源项目Android universal image Downloader 
#### git 地址
https://github.com/nostra13/Android-Universal-Image-Loader

#### 博客解析地址
http://blog.csdn.net/xiaanming/article/details/39057201

#双重缓存
内存缓存 磁盘缓存
## 内存缓存
      
      1. 只使用的是强引用缓存 
          LruMemoryCache（这个类就是这个开源框架默认的内存缓存类，缓存的是bitmap的强引用，下面我会从源码上面分析这个类）
      2.使用强引用和弱引用相结合的缓存有
          UsingFreqLimitedMemoryCache（如果缓存的图片总量超过限定值，先删除使用频率最小的bitmap）
          LRULimitedMemoryCache（这个也是使用的lru算法，和LruMemoryCache不同的是，他缓存的是bitmap的弱引用）
          FIFOLimitedMemoryCache（先进先出的缓存策略，当超过设定值，先删除最先加入缓存的bitmap）
          LargestLimitedMemoryCache(当超过缓存限定值，先删除最大的bitmap对象)
          LimitedAgeMemoryCache（当 bitmap加入缓存中的时间超过我们设定的值，将其删除）
      3.只使用弱引用缓存
          WeakMemoryCache（这个类缓存bitmap的总大小没有限制，唯一不足的地方就是不稳定，缓存的图片容易被回收掉）
## 磁盘缓存
      
      FileCountLimitedDiscCache（可以设定缓存图片的个数，当超过设定值，删除掉最先加入到硬盘的文件）
      LimitedAgeDiscCache（设定文件存活的最长时间，当超过这个值，就删除该文件）
      TotalSizeLimitedDiscCache（设定缓存bitmap的最大值，当超过这个值，删除最先加入到硬盘的文件）
      UnlimitedDiscCache（这个缓存类没有任何的限制）

#其他类解读
##ImageAware解读
      
      ImageAware  Represents image aware view which provides all needed properties and behavior for image processing and displaying
         * through {@link com.nostra13.universalimageloader.core.ImageLoader ImageLoader}.
         * It can wrap any Android {@link android.view.View View} which can be accessed by {@link #getWrappedView()}. Wrapped
         * view is returned in {@link com.nostra13.universalimageloader.core.listener.ImageLoadingListener ImageLoadingListener}'s
         * callbacks.
         为image的处理和展示提供必要的特性(参数)和行为<br/>
         可适配任意的 Android  view 通过 getWrappedView()可以得到. ImageLoadingListener callback中会返回该view
         
###ImageAware成员

        int getWidth();
        int getHeight();
        ViewScaleType getScaleType();
        View getWrappedView();
        boolean isCollected();
        int getId();
        boolean setImageDrawable(Drawable drawable);
        boolean setImageBitmap(Bitmap bitmap);
        
##ViewAware 解读
  Wrapper for Android {@link android.view.View View}. Keeps weak reference of View to prevent memory leaks.
  <br/>implement ImageAware
###ViewAware成员
    
        public static final String WARN_CANT_SET_DRAWABLE = "Can't set a drawable into view. You should call ImageLoader on UI thread for it.";
      	public static final String WARN_CANT_SET_BITMAP = "Can't set a bitmap into view. You should call ImageLoader on UI thread for it.";
      
      	protected Reference<View> viewRef;
      	protected boolean checkActualViewSize;
###ViewAware构造
        public ViewAware(View view, boolean checkActualViewSize) {
      		if (view == null) throw new IllegalArgumentException("view must not be null");
      
      		this.viewRef = new WeakReference<View>(view);
      		this.checkActualViewSize = checkActualViewSize;
      	}
###ViewAware getWidth()
        1.优先获取真实的width
        2.如果获取不到，获取layoutParams中定义的
        @Override
      	public int getWidth() {
      		View view = viewRef.get();
      		if (view != null) {
      			final ViewGroup.LayoutParams params = view.getLayoutParams();
      			int width = 0;
      			if (checkActualViewSize && params != null && params.width != ViewGroup.LayoutParams.WRAP_CONTENT) {
      				width = view.getWidth(); // Get actual image width
      			}
      			if (width <= 0 && params != null) width = params.width; // Get layout width parameter
      			return width;
      		}
      		return 0;
      	}
###ViewAware getHeight()
    同上
