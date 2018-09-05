package com.yline.refresh.simple;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.os.Handler;
import android.os.Bundle;

import com.yline.base.BaseAppCompatActivity;
import com.yline.refresh.R;
import com.yline.view.refresh.callback.OnRefreshListener;
import com.yline.view.refresh.PullToRefreshLayout;

public class ScrollViewActivity extends BaseAppCompatActivity {
	public static void launch(Context context) {
		if (null != context) {
			Intent intent = new Intent();
			intent.setClass(context, ScrollViewActivity.class);
			if (!(context instanceof Activity)) {
				intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
			}
			context.startActivity(intent);
		}
	}
	
	private PullToRefreshLayout mRefreshLayout;
	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_scroll);
		
		initView();
	}
	
	private void initView() {
		mRefreshLayout = findViewById(R.id.scroll_refresh);
		initViewClick();
	}
	
	private void initViewClick() {
		mRefreshLayout.setRefreshListener(new OnRefreshListener() {
			@Override
			public void refresh() {
				new Handler().postDelayed(new Runnable() {
					@Override
					public void run() {
						mRefreshLayout.finishRefresh();
					}
				}, 2000);
			}
			
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
}
