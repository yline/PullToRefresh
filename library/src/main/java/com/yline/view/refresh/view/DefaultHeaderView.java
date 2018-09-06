package com.yline.view.refresh.view;

import android.content.Context;
import android.util.AttributeSet;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.TextView;

import com.yline.view.refresh.R;
import com.yline.view.refresh.callback.OnHeaderCallback;

/**
 * 默认，下拉刷新，视图
 *
 * @author yline 2018/9/5 -- 15:04
 */
public class DefaultHeaderView extends FrameLayout implements OnHeaderCallback {
	private TextView textView;
	private ImageView arrowImageView;
	private ProgressBar progressBar;
	
	public DefaultHeaderView(Context context) {
		this(context, null);
	}
	
	public DefaultHeaderView(Context context, AttributeSet attrs) {
		this(context, attrs, 0);
	}
	
	public DefaultHeaderView(Context context, AttributeSet attrs, int defStyleAttr) {
		super(context, attrs, defStyleAttr);
		
		LayoutInflater.from(context).inflate(R.layout.layout_default_header, this, true);
		initView();
	}
	
	private void initView() {
		textView = findViewById(R.id.header_text);
		arrowImageView = findViewById(R.id.header_arrow);
		progressBar = findViewById(R.id.header_progress);
	}
	
	@Override
	public void begin() {
	
	}
	
	@Override
	public void progress(float progress, float all) {
		float s = progress / all;
		if (s >= 0.9f) {
			arrowImageView.setRotation(180);
		} else {
			arrowImageView.setRotation(0);
		}
		if (progress >= all - 10) {
			textView.setText("松开刷新");
		} else {
			textView.setText("下拉加载");
		}
	}
	
	@Override
	public void finishing(float progress, float all) {
	
	}
	
	@Override
	public void loading() {
		arrowImageView.setVisibility(GONE);
		progressBar.setVisibility(VISIBLE);
		textView.setText("刷新中...");
	}
	
	@Override
	public void normal() {
		arrowImageView.setVisibility(VISIBLE);
		progressBar.setVisibility(GONE);
		textView.setText("下拉刷新");
	}
	
	@Override
	public View getView() {
		return this;
	}
}
