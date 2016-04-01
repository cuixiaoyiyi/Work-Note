几个回调接口

##ImageLoadingListener
监听图片加载过程
  Listener for image loading process
        
        /**
        	 * Is called when image loading task was started
        	 *
        	 * @param imageUri Loading image URI
        	 * @param view     View for image
        	 */
        	void onLoadingStarted(String imageUri, View view);
        
        	/**
        	 * Is called when an error was occurred during image loading
        	 *
        	 * @param imageUri   Loading image URI
        	 * @param view       View for image. Can be <b>null</b>.
        	 * @param failReason {@linkplain com.nostra13.universalimageloader.core.assist.FailReason The reason} why image
        	 *                   loading was failed
        	 */
        	void onLoadingFailed(String imageUri, View view, FailReason failReason);
        
        	/**
        	 * Is called when image is loaded successfully (and displayed in View if one was specified)
        	 *
        	 * @param imageUri    Loaded image URI
        	 * @param view        View for image. Can be <b>null</b>.
        	 * @param loadedImage Bitmap of loaded and decoded image
        	 */
        	void onLoadingComplete(String imageUri, View view, Bitmap loadedImage);
        
        	/**
        	 * Is called when image loading task was cancelled because View for image was reused in newer task
        	 *
        	 * @param imageUri Loading image URI
        	 * @param view     View for image. Can be <b>null</b>.
        	 */
        	void onLoadingCancelled(String imageUri, View view);
##ImageLoadingProgressListener
监听图片加载进度
        void onProgressUpdate(String imageUri, View view, int current, int total);
##SimpleImageLoadingListener
ImageLoadingListener的最简单实现，do nothing
##PauseOnScrollListener
实现OnScrollListener，监听listview，防止不必要的(多余的)图片加载，在listview滚动时暂停ImageLoader的下载任务，停止时恢复<br/>
如果用户还有其他的需要监听，实现externalListener
        
        /**
         * Listener-helper for {@linkplain AbsListView list views} ({@link ListView}, {@link GridView}) which can
         * {@linkplain ImageLoader#pause() pause ImageLoader's tasks} while list view is scrolling (touch scrolling and/or
         * fling). It prevents redundant loadings.<br />
         * Set it to your list view's {@link AbsListView#setOnScrollListener(OnScrollListener) setOnScrollListener(...)}.<br />
         * This listener can wrap your custom {@linkplain OnScrollListener listener}.
         *
         * @author Sergey Tarasevich (nostra13[at]gmail[dot]com)
         * @since 1.7.0
         */
        public class PauseOnScrollListener implements OnScrollListener {
        
        	private ImageLoader imageLoader;
        
        	private final boolean pauseOnScroll;
        	private final boolean pauseOnFling;
        	private final OnScrollListener externalListener;
        
        	/**
        	 * Constructor
        	 *
        	 * @param imageLoader   {@linkplain ImageLoader} instance for controlling
        	 * @param pauseOnScroll Whether {@linkplain ImageLoader#pause() pause ImageLoader} during touch scrolling
        	 * @param pauseOnFling  Whether {@linkplain ImageLoader#pause() pause ImageLoader} during fling
        	 */
        	public PauseOnScrollListener(ImageLoader imageLoader, boolean pauseOnScroll, boolean pauseOnFling) {
        		this(imageLoader, pauseOnScroll, pauseOnFling, null);
        	}
        
        	/**
        	 * Constructor
        	 *
        	 * @param imageLoader    {@linkplain ImageLoader} instance for controlling
        	 * @param pauseOnScroll  Whether {@linkplain ImageLoader#pause() pause ImageLoader} during touch scrolling
        	 * @param pauseOnFling   Whether {@linkplain ImageLoader#pause() pause ImageLoader} during fling
        	 * @param customListener Your custom {@link OnScrollListener} for {@linkplain AbsListView list view} which also
        	 *                       will be get scroll events
        	 */
        	public PauseOnScrollListener(ImageLoader imageLoader, boolean pauseOnScroll, boolean pauseOnFling,
        			OnScrollListener customListener) {
        		this.imageLoader = imageLoader;
        		this.pauseOnScroll = pauseOnScroll;
        		this.pauseOnFling = pauseOnFling;
        		externalListener = customListener;
        	}
        
        	@Override
        	public void onScrollStateChanged(AbsListView view, int scrollState) {
        		switch (scrollState) {
        			case OnScrollListener.SCROLL_STATE_IDLE:
        				imageLoader.resume();
        				break;
        			case OnScrollListener.SCROLL_STATE_TOUCH_SCROLL:
        				if (pauseOnScroll) {
        					imageLoader.pause();
        				}
        				break;
        			case OnScrollListener.SCROLL_STATE_FLING:
        				if (pauseOnFling) {
        					imageLoader.pause();
        				}
        				break;
        		}
        		if (externalListener != null) {
        			externalListener.onScrollStateChanged(view, scrollState);
        		}
        	}
        
        	@Override
        	public void onScroll(AbsListView view, int firstVisibleItem, int visibleItemCount, int totalItemCount) {
        		if (externalListener != null) {
        			externalListener.onScroll(view, firstVisibleItem, visibleItemCount, totalItemCount);
        		}
        	}
        }
