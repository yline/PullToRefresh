package com.yline.view.refresh;

import android.animation.ValueAnimator;
import android.content.Context;
import android.support.annotation.IntDef;
import android.support.v4.view.ViewCompat;
import android.util.AttributeSet;
import android.view.Gravity;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewConfiguration;
import android.view.ViewGroup;
import android.widget.FrameLayout;

import com.yline.view.refresh.callback.OnLoadMoreListener;
import com.yline.view.refresh.callback.OnRefreshListener;
import com.yline.view.refresh.callback.OnFooterCallback;
import com.yline.view.refresh.callback.OnHeaderCallback;
import com.yline.view.refresh.view.DefaultHeaderView;
import com.yline.view.refresh.view.DefaultFooterView;

import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;

/**
 * 下拉刷新 + 上拉加载，控件
 *
 * @author yline 2018/9/5 -- 16:28
 */
public class PullToRefreshLayout extends FrameLayout {
	private static final long ANIM_TIME = 300; // 计算时间 + 偏移增加的布局
	private static final int HEAD_HEIGHT = 60; // 头部，默认，高度，dp
	private static final int FOOT_HEIGHT = 60; // 底部，默认，高度，dp
	
	private OnHeaderCallback mHeaderCallback;
	private OnFooterCallback mOnFooterCallback;
	private View mChildView;
	
	private static int headHeight;
	private static int maxHeadHeight;
	private static int footHeight;
	private static int maxFootHeight;
	
	private boolean canRefresh = true; // 下拉加载，开关
	private boolean isRefreshMove = true; // 下拉加载，RecyclerView是否移动
	
	private boolean canLoadMore = true; // 上拉刷新，开关
	private boolean isLoadMoreMove = true; // 上拉刷新，RecyclerView是否移动
	
	// 临时变量
	private float mTouchY;
	private float mCurrentY;
	
	private boolean isRefresh;
	private boolean isLoadMore;
	
	//滑动的最小距离
	private final int mTouchSlope;
	
	private OnRefreshListener refreshListener;
	private OnLoadMoreListener loadMoreListener;
	
	public void setOnRefreshListener(OnRefreshListener refreshListener) {
		this.refreshListener = refreshListener;
	}
	
	public void setOnLoadMoreListener(OnLoadMoreListener listener){
		this.loadMoreListener = listener;
	}
	
	public PullToRefreshLayout(Context context) {
		this(context, null);
	}
	
	public PullToRefreshLayout(Context context, AttributeSet attrs) {
		this(context, attrs, 0);
	}
	
	public PullToRefreshLayout(Context context, AttributeSet attrs, int defStyleAttr) {
		super(context, attrs, defStyleAttr);
		
		mTouchSlope = ViewConfiguration.get(context).getScaledTouchSlop();
		initValues();
	}
	
	private void initValues() {
		headHeight = dp2Px(getContext(), HEAD_HEIGHT);
		footHeight = dp2Px(getContext(), FOOT_HEIGHT);
		maxHeadHeight = dp2Px(getContext(), HEAD_HEIGHT * 2);
		maxFootHeight = dp2Px(getContext(), FOOT_HEIGHT * 2);
	}
	
	@Override
	protected void onFinishInflate() {
		super.onFinishInflate();
		mChildView = getChildAt(0);
		addHeadView();
		addFooterView();
	}
	
	private void addHeadView() {
		if (mHeaderCallback == null) {
			mHeaderCallback = new DefaultHeaderView(getContext());
		} else {
			removeView(mHeaderCallback.getView());
		}
		LayoutParams layoutParams = new LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, 0);
		mHeaderCallback.getView().setLayoutParams(layoutParams);
		if (mHeaderCallback.getView().getParent() != null) {
			((ViewGroup) mHeaderCallback.getView().getParent()).removeAllViews();
		}
		addView(mHeaderCallback.getView(), 0);
	}
	
	private void addFooterView() {
		if (mOnFooterCallback == null) {
			mOnFooterCallback = new DefaultFooterView(getContext());
		} else {
			removeView(mOnFooterCallback.getView());
		}
		LayoutParams layoutParams = new LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, 0);
		layoutParams.gravity = Gravity.BOTTOM;
		mOnFooterCallback.getView().setLayoutParams(layoutParams);
		if (mOnFooterCallback.getView().getParent() != null) {
			((ViewGroup) mOnFooterCallback.getView().getParent()).removeAllViews();
		}
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
					dura = Math.min(maxHeadHeight, dura);
					dura = Math.max(0, dura);
					mHeaderCallback.getView().getLayoutParams().height = (int) dura;
					if (isRefreshMove) {
						ViewCompat.setTranslationY(mChildView, dura);
					}
					requestLayout();
					mHeaderCallback.progress(dura, headHeight);
				} else {
					if (canLoadMore) {
						dura = Math.min(maxFootHeight, Math.abs(dura));
						dura = Math.max(0, Math.abs(dura));
						mOnFooterCallback.getView().getLayoutParams().height = (int) dura;
						if (isLoadMoreMove) {
							ViewCompat.setTranslationY(mChildView, -dura);
						}
						requestLayout();
						mOnFooterCallback.progress(dura, footHeight);
					}
				}
				return true;
			case MotionEvent.ACTION_UP:
			case MotionEvent.ACTION_CANCEL:
				float currentY = event.getY();
				final int dy1 = (int) (currentY - mTouchY) / 3;
				if (dy1 > 0 && canRefresh) {
					if (dy1 >= headHeight) {
						createAnimatorTranslationY(State.REFRESH,
								dy1 > maxHeadHeight ? maxHeadHeight : dy1, headHeight, new OnAnimatorFinishCallback() {
									@Override
									public void onSuccess() {
										isRefresh = true;
										if (refreshListener != null) {
											refreshListener.refresh();
										}
										mHeaderCallback.loading();
									}
								});
					} else if (dy1 > 0 && dy1 < headHeight) {
						setFinish(dy1, State.REFRESH);
						mHeaderCallback.normal();
					}
				} else {
					if (canLoadMore) {
						if (Math.abs(dy1) >= footHeight) {
							createAnimatorTranslationY(State.LOADMORE, Math.abs(dy1) > maxFootHeight ? maxFootHeight : Math.abs(dy1), footHeight, new OnAnimatorFinishCallback() {
								@Override
								public void onSuccess() {
									isLoadMore = true;
									if (loadMoreListener != null) {
										loadMoreListener.loadMore();
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
		if (null == mChildView) {
			return false;
		}
		return ViewCompat.canScrollVertically(mChildView, -1);
	}
	
	private void createAnimatorTranslationY(@State.REFRESH_STATE final int state, final int start, final int purpose,
	                                        final OnAnimatorFinishCallback callBack) {
		final ValueAnimator anim = ValueAnimator.ofInt(start, purpose);
		anim.setDuration(ANIM_TIME);
		anim.addUpdateListener(new ValueAnimator.AnimatorUpdateListener() {
			@Override
			public void onAnimationUpdate(ValueAnimator valueAnimator) {
				int value = (int) valueAnimator.getAnimatedValue();
				if (state == State.REFRESH) {
					mHeaderCallback.getView().getLayoutParams().height = value;
					if (isRefreshMove) {
						ViewCompat.setTranslationY(mChildView, value);
					}
					if (purpose == 0) { //代表结束加载
						mHeaderCallback.finishing(value, maxHeadHeight);
					} else {
						mHeaderCallback.progress(value, headHeight);
					}
				} else {
					mOnFooterCallback.getView().getLayoutParams().height = value;
					if (isLoadMoreMove) {
						ViewCompat.setTranslationY(mChildView, -value);
					}
					if (purpose == 0) { //代表结束加载
						mOnFooterCallback.finishing(value, maxHeadHeight);
					} else {
						mOnFooterCallback.progress(value, footHeight);
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
		createAnimatorTranslationY(state, height, 0, new OnAnimatorFinishCallback() {
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
				setFinish(headHeight, state);
			}
		} else {
			if (mOnFooterCallback != null && mOnFooterCallback.getView().getLayoutParams().height > 0 && isLoadMore) {
				setFinish(footHeight, state);
			}
		}
	}
	
	private interface OnAnimatorFinishCallback {
		void onSuccess();
	}
	
	/* ---------------------------------外置，api--------------------------------------- */
	
	/**
	 * 手动开始刷新
	 */
	public void autoRefresh() {
		createAnimatorTranslationY(State.REFRESH, 0, headHeight, new OnAnimatorFinishCallback() {
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
	 * 下拉加载，RecyclerView是否移动
	 */
	public void setRefreshMove(boolean refreshMove) {
		isRefreshMove = refreshMove;
	}
	
	/**
	 * 上拉刷新，RecyclerView是否移动
	 */
	public void setLoadMoreMove(boolean loadMoreMove) {
		isLoadMoreMove = loadMoreMove;
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
		headHeight = dp2Px(getContext(), dp);
	}
	
	/**
	 * 设置加载更多控件的高度
	 *
	 * @param dp 单位为dp
	 */
	public void setFootHeight(int dp) {
		footHeight = dp2Px(getContext(), dp);
	}
	
	/**
	 * 设置刷新控件的下拉的最大高度 且必须大于本身控件的高度  最佳为2倍
	 *
	 * @param dp 单位为dp
	 */
	public void setMaxHeadHeight(int dp) {
		if (headHeight >= dp2Px(getContext(), dp)) {
			return;
		}
		maxHeadHeight = dp2Px(getContext(), dp);
	}
	
	/**
	 * 设置加载更多控件的上拉的最大高度 且必须大于本身控件的高度  最佳为2倍
	 *
	 * @param dp 单位为dp
	 */
	public void setMaxFootHeight(int dp) {
		if (footHeight >= dp2Px(getContext(), dp)) {
			return;
		}
		maxFootHeight = dp2Px(getContext(), dp);
	}
	
	private static int dp2Px(Context context, float dp) {
		final float scale = context.getResources().getDisplayMetrics().density;
		return (int) (dp * scale + 0.5f);
	}
	
	/**
	 * 下拉刷新，还是上拉加载，常量
	 */
	public static class State {
		@IntDef({REFRESH, LOADMORE})
		@Retention(RetentionPolicy.SOURCE)
		public @interface REFRESH_STATE {
		
		}
		
		public static final int REFRESH = 10;
		public static final int LOADMORE = 11;
	}
}
