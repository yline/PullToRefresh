package com.yline.refresh.custom;

import android.content.Context;
import android.util.AttributeSet;

import com.yline.view.refresh.PullToRefreshLayout;

public class NormalPullToRefreshLayout extends PullToRefreshLayout {
	public NormalPullToRefreshLayout(Context context) {
		super(context);
		initView(context);
	}
	
	public NormalPullToRefreshLayout(Context context, AttributeSet attrs) {
		super(context, attrs);
		initView(context);
	}
	
	public NormalPullToRefreshLayout(Context context, AttributeSet attrs, int defStyleAttr) {
		super(context, attrs, defStyleAttr);
		initView(context);
	}
	
	private void initView(Context context) {
		setFooterView(new NormalFooterView(context));
		setHeaderView(new NormalHeaderView(context));
	}
}
