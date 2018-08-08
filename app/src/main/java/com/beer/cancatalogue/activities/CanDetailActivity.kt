package com.beer.cancatalogue.activities


import com.squareup.picasso.Picasso
import android.widget.ImageView
import com.beer.cancatalogue.R
import com.beer.cancatalogue.models.Can
import android.widget.TextView
import android.os.Bundle
import android.support.v7.app.AppCompatActivity
import android.view.MenuItem
import android.net.Uri

//Otheractivity of the program, used to display more detailed information about each can
class CanDetailActivity : AppCompatActivity() {
    //fields to hold values of graphical components
    private var ivCanPhoto: ImageView? = null
    private var tvBrand: TextView? = null
    private var tvChar: TextView? = null
    private var tvOrigin: TextView? = null
    private var tvBought: TextView? = null
    private var tvQuantity: TextView? = null
    private var tvColor: TextView? = null
    private var tvOwner: TextView? = null

    //When activity is created this part of code will be executed
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        //assigning layout view to our activity
        setContentView(R.layout.activity_detail_can)
        //gathering all the graphical components into variables
        ivCanPhoto = findViewById(R.id.ivCanPhoto) as ImageView
        tvBrand = findViewById(R.id.tvBrand) as TextView
        tvChar = findViewById(R.id.tvChar) as TextView
        tvOrigin = findViewById(R.id.tvOrigin) as TextView
        tvBought = findViewById(R.id.tvBought) as TextView
        tvQuantity = findViewById(R.id.tvQuantity) as TextView
        tvColor = findViewById(R.id.tvColor) as TextView
        tvOwner = findViewById(R.id.tvOwner) as TextView

        val can = intent.getSerializableExtra("can") as Can
        //Call a function that fills graphical component with information
        loadCan(can)
    }


    //With call to this function, the newly opened activity is populated with can specific information
    private fun loadCan(can: Can) {
        //change activity title
        this.title = can.title

        //Picasso library to conveniently read image from the imgur URL that is located in can's canPicture field
        Picasso.with(this).load(Uri.parse(can.canPicture)).error(R.drawable.empty3).into(ivCanPhoto)
        //populate graphical components
        tvBrand!!.text = can.brand
        tvChar!!.text = can.description
        tvOrigin!!.text = can.origin
        tvBought!!.text = can.bought
        tvQuantity!!.text = can.quantity
        tvColor!!.text = can.canColor
        tvOwner!!.text = can.ownership

    }

    //Part of code for the future implementation of the settings button
    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        val id = item.itemId

        return if (id == R.string.action_settings) {
            true
        } else
            super.onOptionsItemSelected(item)

    }
}