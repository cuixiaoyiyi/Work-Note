package com.list;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

import android.os.Bundle;
import android.os.Message;
import android.text.TextUtils;
import android.view.View;
import android.widget.ListView;
import android.widget.TextView;

import com.R;
import com.base.BaseActivity;
import com.data.Constants;
import com.data.Preferences;
import com.net.HttpRequest;
import com.net.NetworkDelegateImpl;
import com.pulllayout.ShunshunBaseAdapter;
import com.utils.Util;
import com.view.PullToRefreshLayout;
import com.view.PullToRefreshLayout.OnRefreshListener;
import com.widget.ShunHandler;
/**
* 一个很好用的上拉下拉刷新模板类
*/

public abstract class PullRefreshTempleteActivity<T> extends BaseActivity
		implements OnRefreshListener, ShunHandler.OnSuccess {
	protected TextView mTitleTextView = null;
	protected ListView mListView = null;
	protected ShunshunBaseAdapter<?> mAdapter = null;
	protected PullToRefreshLayout mPullToRefreshLayout = null;
	protected int page = 1;
	protected View mNodataView = null;
	protected HashMap<String, Object> mHashMap = null;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(getLayoutResource());

		mHandler = new ShunHandler(this, this);

		findView();
		setListener();
		setTitleBar();
		afterSetListener();
		requestList();
	}

	protected void setListener() {
		mPullToRefreshLayout.setOnRefreshListener(this);
		mListView.setOnItemClickListener(this);

		mNodataView.setOnClickListener(new View.OnClickListener() {

			@Override
			public void onClick(View v) {
				page = 1;
				requestList();
			}
		});
	}

	protected void findView() {
		mListView = (ListView) findViewById(R.id.content_view);
		mPullToRefreshLayout = (PullToRefreshLayout) findViewById(R.id.refresh_view);
		mNodataView = findViewById(R.id.no_data);

		mTitleTextView = (TextView) findViewById(R.id.text_view_title);
	}

	private void setTitleBar() {
		if (mTitleTextView != null) {
			if (0 != getTextTitle()) {
				try {
					mTitleTextView.setText(getTextTitle());
				} catch (Exception e) {
					e.printStackTrace();
				}
			}
		}
	}

	protected void afterSetListener() {
	}

	// 处理资讯接口数据
	@SuppressWarnings("unchecked")
	protected void dealData(Object obj) {
		ArrayList<HashMap<String, Object>> hashMaps = getHashMaps();
		ArrayList<T> arrayList = (ArrayList<T>) createList(hashMaps);

		// arrayList = Mood.testData();
		if (page == 1) {
			if (arrayList != null && arrayList.size() > 0) {
				if (mAdapter == null || mAdapter.getData() == null) {
					mAdapter = newAdapter(arrayList);
					mListView.setAdapter(mAdapter);
				} else {
					@SuppressWarnings("rawtypes")
					List datas = mAdapter.getData();
					if (datas.size() == arrayList.size()) {
						showToast(R.string.no_more);
					}
					datas.clear();
					datas.addAll(arrayList);
					mAdapter.notifyDataSetChanged();
				}
				mPullToRefreshLayout.setVisibility(View.VISIBLE);
				mNodataView.setVisibility(View.GONE);
			} else {
				if (mListView.getHeaderViewsCount() == 0) {
					mPullToRefreshLayout.setVisibility(View.GONE);
					mNodataView.setVisibility(View.VISIBLE);
				}else{
					mListView.setAdapter(new ShunshunBaseAdapter<>(new ArrayList<>()));
				}
			}
		} else {
			Util.updateMorePageList(this, arrayList, mAdapter, page,
					getPageSize());
		}
		mPullToRefreshLayout.refreshFinish(PullToRefreshLayout.SUCCEED);
	}

	protected void requestList() {
		requestList(false);
	}

	protected void requestList(boolean getCache) {
		showProgressDialog();
		if (mHashMap == null) {
			mHashMap = new HashMap<>();
		}
		final NetworkDelegateImpl delegate = new NetworkDelegateImpl(mHandler,
				mHashMap);
		if (!TextUtils.isEmpty(getCacheKey()) && page == 1) {
			delegate.setSaveKey(getCacheKey());
		}
		delegate.setEndMessageWhat(getEndMsg());
		if (getCache && !TextUtils.isEmpty(getCacheKey())
				&& !TextUtils.isEmpty(Preferences.getData(getCacheKey()))) {
			delegate.didFinishedLoad(Preferences
					.getDataInputStream(getCacheKey()));
		}
		HttpRequest.requestServerWithSign(Constants.SERVER_ADDRESS
				+ getAction(), dataToServer(), delegate);
	}

	@Override
	public void onRefresh(PullToRefreshLayout pullToRefreshLayout) {
		page = 1;
		requestList();
	}

	@Override
	public void onLoadMore(PullToRefreshLayout pullToRefreshLayout) {
		if (mAdapter.getData() != null) {
			page = mAdapter.getData().size() / getPageSize() + 1;
		}
		requestList();
	}

	@Override
	public void onResult(Message msg) {
		if (msg.what == getEndMsg()) {
			dealData(msg.obj);
			dismissProgressDialog();
		}
	}

	protected int getLayoutResource() {
		return R.layout.activity_pull_layout;
	}

	protected int getEndMsg() {
		return Constants.msg.MSG_LIST;
	}

	protected int getPageSize() {
		return 10;// default
	}

	protected HashMap<String, String> dataToServer() {
		HashMap<String, String> dataToServer = new HashMap<String, String>();
		dataToServer.put("page", String.valueOf(page));
		dataToServer.put("limit", String.valueOf(getPageSize()));
		return dataToServer;
	}

	protected ArrayList<HashMap<String, Object>> getHashMaps() {
		return Util.getHashMapList(mHashMap, getListKey());
	}

	protected String getCacheKey() {
		return null;
	}

	protected abstract int getTextTitle();

	protected abstract String getAction();

	protected abstract String getListKey();

	protected abstract ShunshunBaseAdapter<?> newAdapter(ArrayList<T> data);

	protected abstract ArrayList<T> createList(
			ArrayList<HashMap<String, Object>> data);

}
