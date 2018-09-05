package com.yline.view.refresh.view;

import android.content.Context;
import android.support.v4.content.ContextCompat;
import android.util.AttributeSet;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.TextView;

import com.yline.view.R;
import com.yline.view.refresh.callback.OnFooterCallback;

/**
 * 默认，上拉加载，视图
 *
 * @author yline 2018/9/5 -- 15:04
 */
public class DefaultFooterView extends FrameLayout implements OnFooterCallback {
	private TextView textView;
	private ImageView arrowImageView;
	private ProgressBar progressBar;
	
	public DefaultFooterView(Context context) {
		this(context, null);
	}
	
	public DefaultFooterView(Context context, AttributeSet attrs) {
		this(context, attrs, 0);
	}
	
	public DefaultFooterView(Context context, AttributeSet attrs, int defStyleAttr) {
		super(context, attrs, defStyleAttr);
		LayoutInflater.from(context).inflate(R.layout.layout_default_footer, this, true);
		setBackgroundColor(ContextCompat.getColor(context, android.R.color.transparent));
		
		initView();
	}
	
	private void initView() {
		textView = findViewById(R.id.footer_text);
		arrowImageView = findViewById(R.id.footer_arrow);
		progressBar = findViewById(R.id.footer_progress);
	}
	
	@Override
	public void begin() {
	
	}
	
	@Override
	public void progress(float progress, float all) {
		float s = progress / all;
		if (s >= 0.9f) {
			arrowImageView.setRotation(0);
		} else {
			arrowImageView.setRotation(180);
		}
		if (progress >= all - 10) {
			textView.setText("松开加载更多");
		} else {
			textView.setText("上拉加载");
		}
	}
	
	@Override
	public void finishing(float progress, float all) {
	
	}
	
	@Override
	public void loading() {
		arrowImageView.setVisibility(GONE);
		progressBar.setVisibility(VISIBLE);
		textView.setText("加载中...");
	}
	
	@Override
	public void normal() {
		arrowImageView.setVisibility(VISIBLE);
		progressBar.setVisibility(GONE);
		textView.setText("上拉加载");
	}
	
	@Override
	public View getView() {
		return this;
	}
}
