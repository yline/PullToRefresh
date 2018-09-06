package com.yline.refresh.simple;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.os.Handler;
import android.os.Bundle;
import android.widget.ListView;

import com.yline.base.BaseAppCompatActivity;
import com.yline.refresh.R;
import com.yline.test.StrConstant;
import com.yline.view.recycler.test.SimpleListAdapter;
import com.yline.view.refresh.callback.OnLoadMoreListener;
import com.yline.view.refresh.callback.OnRefreshListener;
import com.yline.view.refresh.PullToRefreshLayout;

public class ListActivity extends BaseAppCompatActivity {
	public static void launch(Context context) {
		if (null != context) {
			Intent intent = new Intent();
			intent.setClass(context, ListActivity.class);
			if (!(context instanceof Activity)) {
				intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
			}
			context.startActivity(intent);
		}
	}
	
	private PullToRefreshLayout mRefreshLayout;
	private SimpleListAdapter mListAdapter;
	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_list);
		
		initView();
		initData();
	}
	
	private void initView() {
		mRefreshLayout = findViewById(R.id.list_refresh);
		
		ListView listView = findViewById(R.id.list_view);
		mListAdapter = new SimpleListAdapter(this);
		listView.setAdapter(mListAdapter);
		
		mRefreshLayout.autoRefresh();
		initViewClick();
	}
	
	private void initViewClick() {
		mRefreshLayout.setOnRefreshListener(new OnRefreshListener() {
			@Override
			public void refresh() {
				new Handler().postDelayed(new Runnable() {
					@Override
					public void run() {
						
						int size = StrConstant.getIntRandom(20);
						mListAdapter.setDataList(StrConstant.getListEnglish(size), true);
						mRefreshLayout.finishRefresh();
					}
				}, 2000);
			}
		});
		mRefreshLayout.setOnLoadMoreListener(new OnLoadMoreListener() {
			@Override
			public void loadMore() {
				new Handler().postDelayed(new Runnable() {
					@Override
					public void run() {
						mRefreshLayout.finishLoadMore();
					}
				}, 2000);
			}
		});
	}
	
	private void initData() {
		mListAdapter.setDataList(StrConstant.getListEnglish(20), true);
	}
}
