package com.list;

import java.util.ArrayList;
import java.util.HashMap;

import android.os.Bundle;
import android.os.Message;
import android.support.annotation.Nullable;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ListView;

import com.R;
import com.base.BaseActivity;
import com.base.BaseFragment;
import com.data.Constants;
import com.data.Preferences;
import com.net.HttpRequest;
import com.net.NetworkDelegateImpl;
import com.pulllayout.ShunshunBaseAdapter;
import com.utils.Util;
import com.view.PullToRefreshLayout;
import com.view.PullToRefreshLayout.OnRefreshListener;
import com.widget.ShunHandler;

public abstract class PullRefreshTempleteFragment<T> extends BaseFragment
		implements OnRefreshListener, ShunHandler.OnSuccess {
	protected ListView mListView = null;
	protected ShunshunBaseAdapter<?> mAdapter = null;
	protected PullToRefreshLayout mPullToRefreshLayout = null;
	protected int page = 1;
	protected View mNodataView = null;
	protected HashMap<String, Object> mHashMap = null;

	protected View mRootView = null;

	@Override
	public View onCreateView(LayoutInflater inflater,
			@Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
		mRootView = inflater.inflate(getLayoutResource(), null);
		return mRootView;
	}

	@Override
	public void onViewCreated(View view, @Nullable Bundle savedInstanceState) {
		super.onViewCreated(view, savedInstanceState);

		mHandler = new ShunHandler((BaseActivity) getActivity(), this);

		findView();
		setListener();
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
		mListView = (ListView) mRootView.findViewById(R.id.content_view);
		mPullToRefreshLayout = (PullToRefreshLayout) mRootView
				.findViewById(R.id.refresh_view);
		mNodataView = mRootView.findViewById(R.id.no_data);
	}

	protected void afterSetListener() {

	}

	// 处理资讯接口数据
	protected void dealData(Object obj) {
		ArrayList<HashMap<String, Object>> hashMaps = getHashMaps();
		ArrayList<T> arrayList = (ArrayList<T>) createList(hashMaps);

		// arrayList = Mood.testData();
		if (page == 1) {
			if (arrayList != null && arrayList.size() > 0) {

				mAdapter = newAdapter(arrayList);
				mListView.setAdapter(mAdapter);
				mPullToRefreshLayout.setVisibility(View.VISIBLE);
				mNodataView.setVisibility(View.GONE);
			} else {
				mPullToRefreshLayout.setVisibility(View.GONE);
				mNodataView.setVisibility(View.VISIBLE);
			}
		} else {
			Util.updateMorePageList((BaseActivity) getActivity(), arrayList,
					mAdapter, page, getPageSize());
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
	public void onResult(Message message) {
		if (message.what == getEndMsg()) {
			dealData(message.obj);
			dismissProgressDialog();
		}
	}

	protected int getLayoutResource() {
		return R.layout.layout_pull_refresh;
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

	protected abstract String getAction();

	protected abstract String getListKey();

	protected abstract ShunshunBaseAdapter<?> newAdapter(ArrayList<T> data);

	protected abstract ArrayList<T> createList(
			ArrayList<HashMap<String, Object>> data);

}
