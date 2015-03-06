package com.xhs.view.parallaxpager;

import android.content.Context;
import android.graphics.drawable.AnimationDrawable;
import android.support.v4.view.ViewPager;
import android.util.AttributeSet;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.FrameLayout;
import android.widget.ImageView;

import com.xhs.xhsparallaxwelcome.R;

import java.util.ArrayList;
import java.util.List;

/**
 * 視差滾動
 * 感谢https://github.com/prolificinteractive/ParallaxPager
 * 
 */ 
public class ParallaxContainer extends FrameLayout {

	private String TAG = "ParallaxContainer";

	private List<View> parallaxViews = new ArrayList<View>();
	private ViewPager viewPager;
	private int pageCount = 0;
	private int containerWidth;
	private boolean isLooping = false;
	private final ParallaxPagerAdapter adapter;

	Context context;
	public ViewPager.OnPageChangeListener mCommonPageChangeListener;
	private List<View> viewlist = new ArrayList<View>();
	public int currentPosition = 0;

	public ParallaxContainer(Context context) {
		super(context);
		this.context = context;
		adapter = new ParallaxPagerAdapter(context);
	}

	public ParallaxContainer(Context context, AttributeSet attrs) {
		super(context, attrs);
		adapter = new ParallaxPagerAdapter(context);
	}

	public ParallaxContainer(Context context, AttributeSet attrs, int defStyle) {
		super(context, attrs, defStyle);
		adapter = new ParallaxPagerAdapter(context);
	}

	@Override
	public void onWindowFocusChanged(boolean hasFocus) {
		containerWidth = getMeasuredWidth();
		if (viewPager != null) {
			mCommonPageChangeListener.onPageScrolled(viewPager.getCurrentItem(), 0, 0);
		}
		super.onWindowFocusChanged(hasFocus);
	}

	public void setLooping(boolean looping) {
		isLooping = looping;
		updateAdapterCount();
	}

	ImageView iv;

	public void setImage(ImageView iv) {
		this.iv = iv;
	}

	private void updateAdapterCount() {
		adapter.setCount(isLooping ? Integer.MAX_VALUE : pageCount);
	}

	public void setupChildren(LayoutInflater inflater, int... childIds) {
		if (getChildCount() > 0) {
			throw new RuntimeException("setupChildren should only be called once when ParallaxContainer is empty");
		}

		ParallaxLayoutInflater parallaxLayoutInflater = new ParallaxLayoutInflater(
				inflater, getContext());

		for (int childId : childIds) {
			View view = parallaxLayoutInflater.inflate(childId, this);
			viewlist.add(view);
		}

		pageCount = getChildCount();
		for (int i = 0; i < pageCount; i++) {
			View view = getChildAt(i);
			addParallaxView(view, i);
		}

		updateAdapterCount();

		viewPager = new ViewPager(getContext());
		viewPager.setLayoutParams(new LayoutParams(LayoutParams.MATCH_PARENT,LayoutParams.MATCH_PARENT));
		viewPager.setId(R.id.parallax_pager);
		attachOnPageChangeListener();
		viewPager.setAdapter(adapter);
		addView(viewPager, 0);
	}
 
	/** 至少持续时间 */
	private static final long DELAY_TIME = 600; 
	protected void attachOnPageChangeListener() {
		mCommonPageChangeListener = new ViewPager.OnPageChangeListener() {
			@Override
			public void onPageScrollStateChanged(int state) {
				Log.v(TAG, "onPageScrollStateChanged" + state);
				iv.setBackgroundResource(R.anim.man_run);
				final AnimationDrawable animationDrawable = (AnimationDrawable) iv.getBackground();
				switch (state) {
				case 0: 
					finishAnim(animationDrawable);
					break;
				case 1: 
					isEnd = false;
					animationDrawable.start();
					break;
				case 2:  
					finishAnim(animationDrawable);
					break;
				}
			}

			boolean isleft = false;

			@Override
			public void onPageScrolled(int pageIndex, float offset, int offsetPixels) {
//				Log.v(TAG, "onPageScrolled" + pageIndex + "  offset" + offset + "   offsetPixels" + offsetPixels);

				if (offsetPixels < 10) {
					isleft = false;
				}

				if (pageCount > 0) {
					pageIndex = pageIndex % pageCount;
				}

				if (pageIndex == 3) {
					if (isleft) {

					} else {
						iv.setX(iv.getLeft() - offsetPixels);
					}
				}

				ParallaxViewTag tag;
				for (View view : parallaxViews) {
					tag = (ParallaxViewTag) view.getTag(R.id.parallax_view_tag);
					if (tag == null) {
						continue;
					}

					if ((pageIndex == tag.index - 1 || (isLooping && (pageIndex == tag.index
							- 1 + pageCount)))
							&& containerWidth != 0) {

						// make visible
						view.setVisibility(VISIBLE);

						// slide in from right
						view.setTranslationX((containerWidth - offsetPixels) * tag.xIn);

						// slide in from top
						view.setTranslationY(0 - (containerWidth - offsetPixels) * tag.yIn);

						// fade in
						view.setAlpha(1.0f - (containerWidth - offsetPixels) * tag.alphaIn / containerWidth);

					} else if (pageIndex == tag.index) {

						// make visible
						view.setVisibility(VISIBLE);

						// slide out to left
						view.setTranslationX(0 - offsetPixels * tag.xOut);

						// slide out to top
						view.setTranslationY(0 - offsetPixels * tag.yOut);

						// fade out
						view.setAlpha(1.0f - offsetPixels * tag.alphaOut / containerWidth);

					} else {
						view.setVisibility(GONE);
					}
				}
			}

			@Override
			public void onPageSelected(int position) {
				Log.v(TAG, "onPageSelected" + position);
				currentPosition = position;
			}
		};
		viewPager.setOnPageChangeListener(mCommonPageChangeListener);
	}
	 
	boolean isEnd = false;
	private synchronized void finishAnim(final AnimationDrawable animationDrawable)
	{ 
		if(isEnd)
		{
			return;
		}
		isEnd = true;
		final long delay = DELAY_TIME ;
		new Thread(new Runnable() {  
            @Override  
            public void run() {  
            	Log.v(TAG, "onPageScrollStateChanged   delay" + delay);
            	if(delay > 0)
            	{
					try {
						Thread.sleep(delay); 
					} catch (InterruptedException e) { 
						e.printStackTrace();
					}
            	} 
            	if (animationDrawable.isRunning() && isEnd) {
					animationDrawable.stop();
				} 
            }  
        }).start();    
	}

	private void addParallaxView(View view, int pageIndex) {
		if (view instanceof ViewGroup) {
			ViewGroup viewGroup = (ViewGroup) view;
			for (int i = 0, childCount = viewGroup.getChildCount(); i < childCount; i++) {
				addParallaxView(viewGroup.getChildAt(i), pageIndex);
			}
		}

		ParallaxViewTag tag = (ParallaxViewTag) view.getTag(R.id.parallax_view_tag);
		if (tag != null) {
			tag.index = pageIndex;
			parallaxViews.add(view);
		}
	}
}
