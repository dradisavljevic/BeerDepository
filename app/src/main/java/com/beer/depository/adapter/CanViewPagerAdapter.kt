package com.beer.depository.adapter

import android.app.Activity
import android.content.Context
import android.net.Uri
import android.support.v4.view.PagerAdapter
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import com.beer.depository.R
import com.squareup.picasso.Picasso
import android.util.DisplayMetrics
import android.widget.ImageView
import android.support.v4.view.ViewPager




class CanViewPagerAdapter(activty: Activity, aImages: ArrayList<String>) : PagerAdapter() {
    private var imageActivity : Activity = activty
    private var images : ArrayList<String> = aImages
    private lateinit var inflater : LayoutInflater


    override fun getCount(): Int {
        return images.count()

    }

    override fun isViewFromObject(p0: View, p1: Any): Boolean {
        return p0 == p1
    }

    override fun instantiateItem(container: ViewGroup, position: Int): Any {
        this.inflater = imageActivity.applicationContext.getSystemService(Context.LAYOUT_INFLATER_SERVICE) as LayoutInflater
        val itemView = this.inflater.inflate(R.layout.activity_can_image,container,false)
        val image = itemView.findViewById(R.id.canFullSizeImage) as ImageView
        val dis=DisplayMetrics()
        imageActivity.windowManager.defaultDisplay.getMetrics(dis)
        val height=dis.heightPixels
        val width=dis.widthPixels
        image.minimumHeight = height
        image.minimumWidth = width
        Picasso.with(imageActivity.applicationContext).load(Uri.parse(images[position]))
                .error(R.drawable.empty3)
                .into(image)
        container.addView(itemView)
        return itemView
    }

    override fun destroyItem(container: ViewGroup, position: Int, `object`: Any) {
        (container as ViewPager).removeView(`object` as View)
    }
}