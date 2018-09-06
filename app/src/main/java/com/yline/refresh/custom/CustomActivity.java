package com.yline.refresh.custom;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;

import com.yline.base.BaseAppCompatActivity;
import com.yline.refresh.R;
import com.yline.test.StrConstant;
import com.yline.view.recycler.test.SimpleRecyclerAdapter;
import com.yline.view.refresh.PullToRefreshLayout;
import com.yline.view.refresh.callback.OnLoadMoreListener;
import com.yline.view.refresh.callback.OnRefreshListener;

public class CustomActivity extends BaseAppCompatActivity {
	public static void launch(Context context){
		if (null != context){
			Intent intent = new Intent();
			intent.setClass(context, CustomActivity.class);
			if (!(context instanceof Activity)){
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
		setContentView(R.layout.activity_custom);
		
		initView();
		initData();
	}
	
	
	private void initView() {
		mRefreshLayout = findViewById(R.id.custom_refresh);
		
		mRecyclerAdapter = new SimpleRecyclerAdapter();
		RecyclerView recyclerView = findViewById(R.id.custom_view);
		recyclerView.setLayoutManager(new LinearLayoutManager(this));
		recyclerView.setAdapter(mRecyclerAdapter);
		
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
						mRecyclerAdapter.setDataList(StrConstant.getListEnglish(size), true);
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
		mRecyclerAdapter.setDataList(StrConstant.getListFive(20), true);
	}
}
