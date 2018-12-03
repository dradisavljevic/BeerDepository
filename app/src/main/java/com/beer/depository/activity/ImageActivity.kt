package com.beer.depository.activity

import android.view.View
import android.os.Bundle
import android.support.v4.view.ViewPager
import android.support.v7.app.AppCompatActivity
import android.widget.ImageView
import android.widget.ProgressBar
import com.beer.depository.Handler.CircularViewPagerHandler
import com.beer.depository.R
import com.beer.depository.adapter.CanViewPagerAdapter
import com.beer.depository.net.CatalogueClient
import com.loopj.android.http.JsonHttpResponseHandler
import cz.msebera.android.httpclient.Header
import org.json.JSONException
import org.json.JSONObject


class ImageActivity : AppCompatActivity() {

    private var ivCanImage: ImageView? = null
    private lateinit var viewPager: ViewPager
    private var vpAdapter:  CanViewPagerAdapter? = null
    private lateinit var images: ArrayList<String>
    private var client: CatalogueClient? = null
    private lateinit var imageActivity : ImageActivity
    private var progress : ProgressBar? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_pager)
        ivCanImage = findViewById(R.id.canFullSizeImage)
        progress = findViewById<View>(R.id.progressImages) as ProgressBar

        images = ArrayList()
        progress!!.visibility = ProgressBar.VISIBLE
        val imageCan = intent.getSerializableExtra("image") as String
        this.title = intent.getSerializableExtra("title") as String
        val album = intent.getSerializableExtra("album") as String
        imageActivity = this
        if (album == "Not Existing") {
            images.add(imageCan)
            viewPager = findViewById<View>(R.id.viewPager) as ViewPager
            vpAdapter = CanViewPagerAdapter(this, images)

            viewPager!!.adapter = vpAdapter
            progress!!.visibility = ProgressBar.GONE
        } else {

            client = CatalogueClient()
            client!!.getImages(object : JsonHttpResponseHandler() {
                override fun onSuccess(statusCode: Int, headers: Array<Header>?, response: JSONObject?) {
                    try {
                        if (response != null) {
                            var data = response.getJSONArray("data")
                            System.out.println(data.length())
                            for (i in 0 until data.length()) {
                                images.add(data.getJSONObject(i).getString("link"))
                            }
                            viewPager = findViewById<View>(R.id.viewPager) as ViewPager
                            viewPager.addOnPageChangeListener(CircularViewPagerHandler(viewPager))
                            vpAdapter = CanViewPagerAdapter(imageActivity, images)
                            vpAdapter!!.notifyDataSetChanged()
                            viewPager!!.adapter = vpAdapter
                            progress!!.visibility = ProgressBar.GONE
                        }
                    } catch (e: JSONException) {
                        // Invalid JSON format, show appropriate error.
                        e.printStackTrace()
                    }

                }

                override fun onFailure(statusCode: Int, headers: Array<Header>, responseString: String, throwable: Throwable) {
                }
            },getString(R.string.client_id), album.trim())

        }





    }
}