package com.yline.view.refresh;

import android.animation.ValueAnimator;
import android.content.Context;
import android.content.res.TypedArray;
import android.support.v4.view.ViewCompat;
import android.util.AttributeSet;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewConfiguration;
import android.view.ViewGroup;
import android.widget.FrameLayout;

import com.yline.view.R;
import com.yline.view.refresh.callback.OnRefreshListener;
import com.yline.view.refresh.callback.OnFooterCallback;
import com.yline.view.refresh.callback.OnHeaderCallback;
import com.yline.view.refresh.view.HeaderView;
import com.yline.view.refresh.view.LoadMoreView;

/**
 * 下拉刷新 + 上拉加载，控件
 *
 * @author yline 2018/9/5 -- 16:28
 */
public class PullToRefreshLayout extends FrameLayout {
	
	private OnHeaderCallback mHeaderCallback;
	private OnFooterCallback mOnFooterCallback;
	private View mChildView;
	
	private static final long ANIM_TIME = 300;
	private static int HEAD_HEIGHT = 60;
	private static int FOOT_HEIGHT = 60;
	
	private static int head_height;
	private static int head_height_2;
	private static int foot_height;
	private static int foot_height_2;
	
	private float mTouchY;
	private float mCurrentY;
	
	private boolean canLoadMore = true;
	private boolean canRefresh = true;
	private boolean isRefresh;
	private boolean isLoadMore;
	
	//滑动的最小距离
	private int mTouchSlope;
	
	private OnRefreshListener refreshListener;
	
	
	private View loadingView, errorView, emptyView;
	private int loading = R.layout.layout_loading, empty = R.layout.layout_empty, error = R.layout.layout_error;
	
	public void setRefreshListener(OnRefreshListener refreshListener) {
		this.refreshListener = refreshListener;
	}
	
	public PullToRefreshLayout(Context context) {
		this(context, null);
	}
	
	public PullToRefreshLayout(Context context, AttributeSet attrs) {
		this(context, attrs, 0);
	}
	
	public PullToRefreshLayout(Context context, AttributeSet attrs, int defStyleAttr) {
		super(context, attrs, defStyleAttr);
		
		TypedArray a = context.obtainStyledAttributes(attrs, R.styleable.PullToRefreshLayout, defStyleAttr, 0);
		error = a.getResourceId(R.styleable.PullToRefreshLayout_view_error, error);
		loading = a.getResourceId(R.styleable.PullToRefreshLayout_view_loading, loading);
		empty = a.getResourceId(R.styleable.PullToRefreshLayout_view_empty, empty);
		a.recycle();
		
		init();
	}
	
	private void cal() {
		head_height = dp2Px(getContext(), HEAD_HEIGHT);
		foot_height = dp2Px(getContext(), FOOT_HEIGHT);
		head_height_2 = dp2Px(getContext(), HEAD_HEIGHT * 2);
		foot_height_2 = dp2Px(getContext(), FOOT_HEIGHT * 2);
		
		mTouchSlope = ViewConfiguration.get(getContext()).getScaledTouchSlop();
	}
	
	private void init() {
		cal();
		int count = getChildCount();
		if (count != 1) {
			new IllegalArgumentException("child only can be one");
		}
		
	}
	
	@Override
	protected void onFinishInflate() {
		super.onFinishInflate();
		mChildView = getChildAt(0);
		addHeadView();
		addFooterView();
	}
	
	@Override
	protected void onAttachedToWindow() {
		super.onAttachedToWindow();
		
	}
	
	private void addHeadView() {
		if (mHeaderCallback == null) {
			mHeaderCallback = new HeaderView(getContext());
		} else {
			removeView(mHeaderCallback.getView());
		}
		LayoutParams layoutParams = new LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, 0);
		mHeaderCallback.getView().setLayoutParams(layoutParams);
		if (mHeaderCallback.getView().getParent() != null)
			((ViewGroup) mHeaderCallback.getView().getParent()).removeAllViews();
		addView(mHeaderCallback.getView(), 0);
	}
	
	private void addFooterView() {
		if (mOnFooterCallback == null) {
			mOnFooterCallback = new LoadMoreView(getContext());
		} else {
			removeView(mOnFooterCallback.getView());
		}
		LayoutParams layoutParams = new LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, 0);
		layoutParams.gravity = Gravity.BOTTOM;
		mOnFooterCallback.getView().setLayoutParams(layoutParams);
		if (mOnFooterCallback.getView().getParent() != null)
			((ViewGroup) mOnFooterCallback.getView().getParent()).removeAllViews();
		addView(mOnFooterCallback.getView());
	}
	
	@Override
	public boolean onInterceptTouchEvent(MotionEvent ev) {
		if (!canLoadMore && !canRefresh)
			return super.onInterceptTouchEvent(ev);
		//if (isRefresh || isLoadMore) return true;
		switch (ev.getAction()) {
			case MotionEvent.ACTION_DOWN:
				mTouchY = ev.getY();
				mCurrentY = mTouchY;
				break;
			case MotionEvent.ACTION_MOVE:
				float currentY = ev.getY();
				float dy = currentY - mCurrentY;
				if (canRefresh) {
					boolean canChildScrollUp = canChildScrollUp();
					if (dy > mTouchSlope && !canChildScrollUp) {
						mHeaderCallback.begin();
						return true;
					}
				}
				if (canLoadMore) {
					boolean canChildScrollDown = canChildScrollDown();
					if (dy < -mTouchSlope && !canChildScrollDown) {
						mOnFooterCallback.begin();
						return true;
					}
				}
		}
		return super.onInterceptTouchEvent(ev);
	}
	
	@Override
	public boolean onTouchEvent(MotionEvent event) {
		if (isRefresh || isLoadMore)
			return true;
		switch (event.getAction()) {
			case MotionEvent.ACTION_MOVE:
				mCurrentY = event.getY();
				float dura = (mCurrentY - mTouchY) / 3.0f;
				if (dura > 0 && canRefresh) {
					dura = Math.min(head_height_2, dura);
					dura = Math.max(0, dura);
					mHeaderCallback.getView().getLayoutParams().height = (int) dura;
					ViewCompat.setTranslationY(mChildView, dura);
					requestLayout();
					mHeaderCallback.progress(dura, head_height);
				} else {
					if (canLoadMore) {
						dura = Math.min(foot_height_2, Math.abs(dura));
						dura = Math.max(0, Math.abs(dura));
						mOnFooterCallback.getView().getLayoutParams().height = (int) dura;
						ViewCompat.setTranslationY(mChildView, -dura);
						requestLayout();
						mOnFooterCallback.progress(dura, foot_height);
					}
				}
				return true;
			case MotionEvent.ACTION_UP:
			case MotionEvent.ACTION_CANCEL:
				float currentY = event.getY();
				final int dy1 = (int) (currentY - mTouchY) / 3;
				if (dy1 > 0 && canRefresh) {
					if (dy1 >= head_height) {
						createAnimatorTranslationY(State.REFRESH,
								dy1 > head_height_2 ? head_height_2 : dy1, head_height,
								new CallBack() {
									@Override
									public void onSuccess() {
										isRefresh = true;
										if (refreshListener != null) {
											refreshListener.refresh();
										}
										mHeaderCallback.loading();
									}
								});
					} else if (dy1 > 0 && dy1 < head_height) {
						setFinish(dy1, State.REFRESH);
						mHeaderCallback.normal();
					}
				} else {
					if (canLoadMore) {
						if (Math.abs(dy1) >= foot_height) {
							createAnimatorTranslationY(State.LOADMORE, Math.abs(dy1) > foot_height_2 ? foot_height_2 : Math.abs(dy1), foot_height, new CallBack() {
								@Override
								public void onSuccess() {
									isLoadMore = true;
									if (refreshListener != null) {
										refreshListener.loadMore();
									}
									mOnFooterCallback.loading();
								}
							});
						} else {
							setFinish(Math.abs(dy1), State.LOADMORE);
							mOnFooterCallback.normal();
						}
					}
				}
				break;
		}
		return super.onTouchEvent(event);
	}
	
	private boolean canChildScrollDown() {
		if (mChildView == null) {
			return false;
		}
		return ViewCompat.canScrollVertically(mChildView, 1);
	}
	
	private boolean canChildScrollUp() {
		if (mChildView == null) {
			return false;
		}
		return ViewCompat.canScrollVertically(mChildView, -1);
	}
	
	/**
	 * 创建动画
	 */
	public void createAnimatorTranslationY(@State.REFRESH_STATE final int state, final int start,
	                                       final int purpose, final CallBack callBack) {
		final ValueAnimator anim;
		anim = ValueAnimator.ofInt(start, purpose);
		anim.setDuration(ANIM_TIME);
		anim.addUpdateListener(new ValueAnimator.AnimatorUpdateListener() {
			@Override
			public void onAnimationUpdate(ValueAnimator valueAnimator) {
				int value = (int) valueAnimator.getAnimatedValue();
				if (state == State.REFRESH) {
					mHeaderCallback.getView().getLayoutParams().height = value;
					ViewCompat.setTranslationY(mChildView, value);
					if (purpose == 0) { //代表结束加载
						mHeaderCallback.finishing(value, head_height_2);
					} else {
						mHeaderCallback.progress(value, head_height);
					}
				} else {
					mOnFooterCallback.getView().getLayoutParams().height = value;
					ViewCompat.setTranslationY(mChildView, -value);
					if (purpose == 0) { //代表结束加载
						mOnFooterCallback.finishing(value, head_height_2);
					} else {
						mOnFooterCallback.progress(value, foot_height);
					}
				}
				if (value == purpose) {
					if (callBack != null)
						callBack.onSuccess();
				}
				requestLayout();
				
				
			}
			
		});
		anim.start();
	}
	
	private void setFinish(int height, @State.REFRESH_STATE final int state) {
		createAnimatorTranslationY(state, height, 0, new CallBack() {
			@Override
			public void onSuccess() {
				if (state == State.REFRESH) {
					isRefresh = false;
					mHeaderCallback.normal();
					
				} else {
					isLoadMore = false;
					mOnFooterCallback.normal();
				}
			}
		});
	}
	
	private void setFinish(@State.REFRESH_STATE int state) {
		if (state == State.REFRESH) {
			if (mHeaderCallback != null && mHeaderCallback.getView().getLayoutParams().height > 0 && isRefresh) {
				setFinish(head_height, state);
			}
		} else {
			if (mOnFooterCallback != null && mOnFooterCallback.getView().getLayoutParams().height > 0 && isLoadMore) {
				setFinish(foot_height, state);
			}
		}
	}
	
	public interface CallBack {
		void onSuccess();
	}
	
	private void showLoadingView() {
		if (loadingView == null) {
			loadingView = LayoutInflater.from(getContext()).inflate(loading, null);
			LayoutParams layoutParams = new LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT,
					ViewGroup.LayoutParams.MATCH_PARENT);
			addView(loadingView, layoutParams);
		} else {
			loadingView.setVisibility(VISIBLE);
		}
	}
	
	private void showEmptyView() {
		if (emptyView == null) {
			emptyView = LayoutInflater.from(getContext()).inflate(empty, null);
			LayoutParams layoutParams = new LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT,
					ViewGroup.LayoutParams.MATCH_PARENT);
			addView(emptyView, layoutParams);
		} else {
			emptyView.setVisibility(VISIBLE);
		}
	}
	
	private void showErrorView() {
		if (errorView == null) {
			errorView = LayoutInflater.from(getContext()).inflate(error, null);
			LayoutParams layoutParams = new LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT,
					ViewGroup.LayoutParams.MATCH_PARENT);
			addView(errorView, layoutParams);
		} else {
			errorView.setVisibility(VISIBLE);
		}
	}
	
	private void hideView(View view) {
		if (view != null)
			view.setVisibility(GONE);
	}
	
	private void switchView(int status) {
		switch (status) {
			case ViewStatus.CONTENT_STATUS:
				hideView(loadingView);
				hideView(emptyView);
				hideView(errorView);
				
				mChildView.setVisibility(VISIBLE);
				break;
			case ViewStatus.LOADING_STATUS:
				hideView(mChildView);
				hideView(emptyView);
				hideView(errorView);
				
				showLoadingView();
				break;
			case ViewStatus.EMPTY_STATUS:
				hideView(mChildView);
				hideView(loadingView);
				hideView(errorView);
				
				showEmptyView();
				break;
			case ViewStatus.ERROR_STATUS:
				hideView(mChildView);
				hideView(loadingView);
				hideView(emptyView);
				
				showErrorView();
				break;
			default:
				hideView(loadingView);
				hideView(emptyView);
				hideView(errorView);
				
				mChildView.setVisibility(VISIBLE);
				break;
		}
	}
	
	/**
	 * 设置展示view (error,empty,loading)
	 */
	public void showView(@ViewStatus.VIEW_STATUS int status) {
		switchView(status);
	}
	
	/**
	 * 获取view (error,empty,loading)
	 */
	public View getView(@ViewStatus.VIEW_STATUS int status) {
		switch (status) {
			case ViewStatus.EMPTY_STATUS:
				return emptyView;
			case ViewStatus.LOADING_STATUS:
				return loadingView;
			case ViewStatus.ERROR_STATUS:
				return errorView;
			case ViewStatus.CONTENT_STATUS:
				return mChildView;
		}
		return null;
	}
	
	public void autoRefresh() {
		createAnimatorTranslationY(State.REFRESH,
				0, head_height,
				new CallBack() {
					@Override
					public void onSuccess() {
						isRefresh = true;
						if (refreshListener != null) {
							refreshListener.refresh();
						}
						mHeaderCallback.loading();
					}
				});
	}
	
	/**
	 * 结束刷新
	 */
	public void finishRefresh() {
		setFinish(State.REFRESH);
	}
	
	/**
	 * 结束加载更多
	 */
	public void finishLoadMore() {
		setFinish(State.LOADMORE);
	}
	
	/**
	 * 设置是否启用加载更多
	 */
	public void setCanLoadMore(boolean canLoadMore) {
		this.canLoadMore = canLoadMore;
	}
	
	/**
	 * 设置是否启用下拉刷新
	 */
	public void setCanRefresh(boolean canRefresh) {
		this.canRefresh = canRefresh;
	}
	
	/**
	 * 设置是下拉刷新头部
	 *
	 * @param mHeaderView 需实现 HeadView 接口
	 */
	public void setHeaderView(OnHeaderCallback mHeaderView) {
		this.mHeaderCallback = mHeaderView;
		addHeadView();
	}
	
	/**
	 * 设置是下拉刷新尾部
	 *
	 * @param mOnFooterCallback 需实现 FooterView 接口
	 */
	public void setFooterView(OnFooterCallback mOnFooterCallback) {
		this.mOnFooterCallback = mOnFooterCallback;
		addFooterView();
	}
	
	/**
	 * 设置刷新控件的高度
	 *
	 * @param dp 单位为dp
	 */
	public void setHeadHeight(int dp) {
		head_height = dp2Px(getContext(), dp);
	}
	
	/**
	 * 设置加载更多控件的高度
	 *
	 * @param dp 单位为dp
	 */
	public void setFootHeight(int dp) {
		foot_height = dp2Px(getContext(), dp);
	}
	
	/**
	 * 设置刷新控件的下拉的最大高度 且必须大于本身控件的高度  最佳为2倍
	 *
	 * @param dp 单位为dp
	 */
	public void setMaxHeadHeight(int dp) {
		if (head_height >= dp2Px(getContext(), dp)) {
			return;
		}
		head_height_2 = dp2Px(getContext(), dp);
	}
	
	/**
	 * 设置加载更多控件的上拉的最大高度 且必须大于本身控件的高度  最佳为2倍
	 *
	 * @param dp 单位为dp
	 */
	public void setMaxFootHeight(int dp) {
		if (foot_height >= dp2Px(getContext(), dp)) {
			return;
		}
		foot_height_2 = dp2Px(getContext(), dp);
	}
	
	private static int dp2Px(Context context, float dp) {
		final float scale = context.getResources().getDisplayMetrics().density;
		return (int) (dp * scale + 0.5f);
	}
}
