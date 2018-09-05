package com.yline.refresh;

import android.os.Bundle;
import android.view.View;

import com.yline.refresh.custom.CustomActivity;
import com.yline.refresh.simple.ListActivity;
import com.yline.refresh.simple.RecyclerViewActivity;
import com.yline.refresh.simple.ScrollViewActivity;
import com.yline.test.BaseTestActivity;

public class MainActivity extends BaseTestActivity {
	@Override
	public void testStart(View view, Bundle savedInstanceState) {
		addButton("ListView", new View.OnClickListener() {
			@Override
			public void onClick(View v) {
				ListActivity.launch(MainActivity.this);
			}
		});
		
		addButton("RecyclerView", new View.OnClickListener() {
			@Override
			public void onClick(View v) {
				RecyclerViewActivity.launch(MainActivity.this);
			}
		});
		
		addButton("ScrollView", new View.OnClickListener() {
			@Override
			public void onClick(View v) {
				ScrollViewActivity.launch(MainActivity.this);
			}
		});
		
		addButton("CustomRefreshLayout", new View.OnClickListener() {
			@Override
			public void onClick(View v) {
				CustomActivity.launch(MainActivity.this);
			}
		});
	}
}
