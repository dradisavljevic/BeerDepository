package com.beer.depository.Handler

import android.support.v4.view.ViewPager


class CircularViewPagerHandler(private val mViewPager: ViewPager) : ViewPager.OnPageChangeListener {
    private var mCurrentPosition: Int=0
    private var mScrollState: Int=0

    private val isScrollStateSettling: Boolean
        get()=mScrollState == ViewPager.SCROLL_STATE_SETTLING

    override fun onPageSelected(position: Int) {
        mCurrentPosition=position
    }

    override fun onPageScrollStateChanged(state: Int) {
        handleScrollState(state)
        mScrollState=state
    }

    private fun handleScrollState(state: Int) {
        if (state == ViewPager.SCROLL_STATE_IDLE) {
            setNextItemIfNeeded()
        }
    }

    private fun setNextItemIfNeeded() {
        if (!isScrollStateSettling) {
            handleSetNextItem()
        }
    }

    private fun handleSetNextItem() {
        val lastPosition=mViewPager.adapter!!.count - 1
        if (mCurrentPosition == 0) {
            mViewPager.setCurrentItem(lastPosition, false)
        } else if (mCurrentPosition == lastPosition) {
            mViewPager.setCurrentItem(0, false)
        }
    }

    override fun onPageScrolled(position: Int, positionOffset: Float, positionOffsetPixels: Int) {}
}