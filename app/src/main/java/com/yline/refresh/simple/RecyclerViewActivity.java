package com.yline.refresh.simple;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.os.Handler;
import android.os.Bundle;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;

import com.yline.base.BaseAppCompatActivity;
import com.yline.refresh.R;
import com.yline.test.StrConstant;
import com.yline.view.recycler.test.SimpleRecyclerAdapter;
import com.yline.view.refresh.callback.OnRefreshListener;
import com.yline.view.refresh.PullToRefreshLayout;
import com.yline.view.refresh.ViewStatus;

public class RecyclerViewActivity extends BaseAppCompatActivity {
	public static void launch(Context context) {
		if (null != context) {
			Intent intent = new Intent();
			intent.setClass(context, RecyclerViewActivity.class);
			if (!(context instanceof Activity)) {
				intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
			}
			context.startActivity(intent);
		}
	}
	
	private PullToRefreshLayout mRefreshLayout;
	private SimpleRecyclerAdapter mRecyclerAdapter;
	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_recycler);
		
		initView();
		initData();
	}
	
	private void initView() {
		mRefreshLayout = findViewById(R.id.recycler_refresh);
		
		mRecyclerAdapter = new SimpleRecyclerAdapter();
		RecyclerView recyclerView = findViewById(R.id.recycler_view);
		recyclerView.setLayoutManager(new LinearLayoutManager(this));
		recyclerView.setAdapter(mRecyclerAdapter);
		
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
	
	private void initData() {
		// mRecyclerAdapter.setDataList(StrConstant.getListFive(20), true);
		
		new Handler().postDelayed(new Runnable() {
			@Override
			public void run() {
				mRefreshLayout.showView(ViewStatus.LOADING_STATUS);
				new Handler().postDelayed(new Runnable() {
					@Override
					public void run() {
						mRefreshLayout.showView(ViewStatus.EMPTY_STATUS);
						new Handler().postDelayed(new Runnable() {
							@Override
							public void run() {
								mRefreshLayout.showView(ViewStatus.ERROR_STATUS);
							}
						}, 2000);
					}
				}, 2000);
			}
		}, 2000);
	}
}
