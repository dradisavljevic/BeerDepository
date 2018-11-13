package com.beer.depository.activity

import android.support.v7.app.AppCompatActivity
import android.os.Bundle
import android.view.View
import android.widget.ListView
import com.beer.depository.R
import com.beer.depository.adapter.CatalogueAdapter
import com.beer.depository.model.Can
import com.beer.depository.net.CatalogueClient
import com.loopj.android.http.JsonHttpResponseHandler
import cz.msebera.android.httpclient.Header
import org.json.JSONArray
import org.json.JSONException
import org.json.JSONObject
import android.support.v7.widget.SearchView
import android.view.Menu
import android.widget.ProgressBar
import android.content.Intent
import android.widget.AdapterView
import java.text.Normalizer
import java.util.*
import android.widget.Button
import android.widget.TextView
import com.beer.depository.util.Helper

//Main activity of the program. Represents the list of cans - a catalogue
class CatalogueActivity : AppCompatActivity() {
    //fields to hold values of graphical components
    private var lvCans: ListView? = null
    private var canAdapter: CatalogueAdapter? = null
    private var client: CatalogueClient? = null
    private var progress : ProgressBar? = null
    private var pageNum : TextView? = null
    private var btnPrev: Button? = null
    private var btnNext: Button? = null

    //variable clientId is access key to Imgur API. Album hash represents album specific part of URL that leads to desired album. This part is top secret :)
    private var clientId : String? = null
    private var albumHash : String? = null

    //set of variables used for pagination
    private var pageCount: Int = 0
    private var increment: Int = 0
    private var totalItems : Int = 0

    //once retrieved, all the photos of cans will be stored in this list
    private var cans: ArrayList<Can> = arrayListOf()

    //these are constants, thus the val instead of var
    val NUM_ITEMS_PAGE = 10
    val CAN_DETAIL_KEY = "can"

    //When activity is created this part of code will execute
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        //assigning layout view to our activity
        setContentView(R.layout.activity_catalogue)

        //gathering all the graphical components into variables
        lvCans = findViewById<View>(R.id.lvCans) as ListView
        progress = findViewById<View>(R.id.progress) as ProgressBar
        pageNum = findViewById<View>(R.id.pageNum) as TextView
        //buttons for pagination
        btnPrev = findViewById(R.id.prev)
        btnNext = findViewById(R.id.next)
        //once created, there is no previous page
        btnPrev!!.isEnabled = false
        btnNext!!.isEnabled = false
        //intialize empty array list to be used with createon of the adapter
        val aCans = ArrayList<Can>()
        canAdapter = CatalogueAdapter(this, aCans)
        //adding adapter to the list view that will hold all the beer cans
        lvCans!!.adapter = canAdapter
        //gathering the top secret information in order to access the beer cans
        albumHash = getString(R.string.album_hash)
        clientId = getString(R.string.client_id)
        //initially populating the list with all the cans
        getAllCans()
        //adding listener to the list of beer cans. Once clicked, a detailed view of the can is presented
        setupCanSelectedListener()

        //adding listeners to both buttons to be used for pagination purpose
        btnNext!!.setOnClickListener {
            increment++
            loadAdapter(increment)
            lvCans!!.setSelectionAfterHeaderView()
            checkEnable()
        }

        btnPrev!!.setOnClickListener {
            increment--
            loadAdapter(increment)
            lvCans!!.setSelectionAfterHeaderView()
            checkEnable()
        }

    }

    //function used to shift between pages of the list. Filling adapter with images from different pages
    private fun loadAdapter(number: Int) {
        progress!!.visibility = ProgressBar.VISIBLE
        //setting text on the label indicating how many pages there is
        if(pageCount!==0){
            pageNum!!.text = "Page "+(number+1)+" of "+pageCount
        } else {
            pageNum!!.text = "Page $increment of $pageCount"
        }
        //calculating where data for the page starts
        val start = number * NUM_ITEMS_PAGE
        //cleaning up the content of the adapter before populating it again
        canAdapter!!.clear()
        // Sort cans by title. In order for list not to look so chaotic
        val sortedList = cans.sortedWith(compareBy { it.title })

        //Making sure we don't exceed the maximum number of elements for each page to be displayed
        for (i in start until start+NUM_ITEMS_PAGE) {
            if (i < sortedList.size) {
                canAdapter!!.add(sortedList[i])
            } else {
                break
            }
        }
        progress!!.visibility = ProgressBar.GONE
    }


    //function initially called to fetch all the data from our album
    private fun getAllCans() {
        //initializing our API client
        client = CatalogueClient()
        //while we populate the list, a progress bar is displayed
        progress!!.visibility = ProgressBar.VISIBLE
        //calling the get method of Imgur API
        client!!.getCans(object : JsonHttpResponseHandler() {
            override fun onSuccess(statusCode: Int, headers: Array<Header>?, response: JSONObject?) {
                try {
                    //Array to hold all the images from album
                    var images: JSONArray? = null
                    if (response != null) {
                        // Get the response data and all imgur images from it
                        images = response.getJSONObject("data").getJSONArray("images")
                        // Parse json array into array of model objects
                        cans = Can().fromJson(images)
                        // Remove all cans from the adapter
                        canAdapter!!.clear()
                        // Sort images by title
                        val sortedList = cans.sortedWith(compareBy { it.title })
                        // Calculating the total number of beer can images
                        totalItems = sortedList.count()
                        // Step for our pagination. We use this to decide if the number of pages will be even or odd
                        var step = totalItems % NUM_ITEMS_PAGE
                        step = if (step == 0) 0 else 1
                        // Calculating the total number of pages
                        pageCount = totalItems / NUM_ITEMS_PAGE + step
                        // Setting the indicator on which page user is currently located
                        if(pageCount!==0){
                            pageNum!!.text = "Page "+(increment+1)+" of "+pageCount
                        } else {
                            pageNum!!.text = "Page $increment of $pageCount"
                        }
                        // Adding cans from the beginning of the list to adapter
                        for (i in 0 until NUM_ITEMS_PAGE) {
                            if (i < sortedList.size) {
                                canAdapter!!.add(sortedList[i])
                            } else {
                                break
                            }
                        }
                        checkEnable()
                        //Once we load out all the cans, we remove the progress bar
                        progress!!.visibility = ProgressBar.GONE
                        //Notify adapter that change has happened
                        canAdapter!!.notifyDataSetChanged()
                    }
                } catch (e: JSONException) {
                    // Invalid JSON format, show appropriate error.
                    e.printStackTrace()
                }

            }

            override fun onFailure(statusCode: Int, headers: Array<Header>, responseString: String, throwable: Throwable) {
                //Even if it comes to error, we must remove progress bar to avoid confusion
                progress!!.visibility = ProgressBar.GONE
            }
        }, this.clientId!!, this.albumHash!!)
    }

    private fun searchCans(query : String, capitalize : Boolean) {
        //initializing our API client
        client = CatalogueClient()
        //while we populate the list, a progress bar is displayed
        progress!!.visibility = ProgressBar.VISIBLE
        //calling the get method of Imgur API
        client!!.getCans(object : JsonHttpResponseHandler() {
            override fun onSuccess(statusCode: Int, headers: Array<Header>?, response: JSONObject?) {
                try {
                    //Array to hold all the images from album
                    var images: JSONArray? = null
                    if (response != null) {
                        // Get the response data and all imgur images from it
                        images = response.getJSONObject("data").getJSONArray("images")
                        // Parse json array into array of model objects
                        cans = Can().fromJson(images)
                        // Remove all cans from the adapter
                        canAdapter!!.clear()
                        // Sort images by title
                        val sortedList = cans.sortedWith(compareBy { it.title })

                        // Remove all cans from the adapter
                        cans.removeAll(sortedList)

                        for (can in sortedList) {
                            //We remove accents and diacritics from both the title and search query in order to make the search easier
                            //unaccent implements java's Normalizer, so it will only run on JVM
                            val title = Helper.normalize(can.title!!)
                            val normQuery = Helper.normalize(query)

                            //If can satisfies the search criteria we add it to our list of cans
                            if (title.contains(normQuery, capitalize))
                                cans.add(can)
                        }

                        //Once we have gathered all the cans that satisfy the search criteria, we establish a new number of elements to be displayed
                        totalItems = cans.count()
                        //and once again calculate step and page number for the pagination
                        var step = totalItems % NUM_ITEMS_PAGE
                        step = if (step == 0) 0 else 1
                        pageCount = totalItems / NUM_ITEMS_PAGE + step

                        //Initially we start from the first page
                        for (i in 0 until NUM_ITEMS_PAGE) {
                            if (i < cans.size) {
                                canAdapter!!.add(cans[i])
                            } else {
                                break
                            }
                        }
                        increment = 0
                        if (pageCount!=0) {
                            pageNum!!.text = "Page " + (increment + 1) + " of " + pageCount
                        } else {
                            pageNum!!.text = "Page $increment of $pageCount"
                        }
                        //Here we check if next and previous button should be disabled
                        checkEnable()


                        //Once the search is done, we remove the progress bar
                        progress!!.visibility = ProgressBar.GONE
                        canAdapter!!.notifyDataSetChanged()
                    }
                } catch (e: JSONException) {
                    // Invalid JSON format, show appropriate error.
                    e.printStackTrace()
                }

            }

            override fun onFailure(statusCode: Int, headers: Array<Header>, responseString: String, throwable: Throwable) {
                //Remove the progress bar even if error occurred, to avoid confusion
                progress!!.visibility = ProgressBar.GONE
            }
        }, this.clientId!!, this.albumHash!!)
    }


    //This is the menu component where we conduct the search
    override fun onCreateOptionsMenu(menu: Menu): Boolean {
        // Inflate the menu; this adds items to the action bar if it is present.
        menuInflater.inflate(R.menu.menu_can_list, menu)
        val searchItem = menu.findItem(R.id.menu_search)
        val searchView = searchItem.actionView as SearchView
        searchView.setOnQueryTextListener(object : SearchView.OnQueryTextListener {
            override fun onQueryTextSubmit(query: String): Boolean {
                //If the query is capitalized, make search case sensitive, otherwise it is case insensitive
                if ( query == query.capitalize()) {
                    searchCans(query, false)
                } else {
                    searchCans(query, true)
                }
                // Reset SearchView
                searchView.clearFocus()
                searchView.setQuery("", false)
                searchView.isIconified = true
                searchItem.collapseActionView()
                // Set activity title to search query
                this@CatalogueActivity.title = query
                return true
            }

            override fun onQueryTextChange(s: String): Boolean {
                return false
            }
        })
        return true
    }


    //Function that adds listener to our ListView
    private fun setupCanSelectedListener() {
        lvCans!!.onItemClickListener = AdapterView.OnItemClickListener { parent, view, position, id ->
            // Launch the detail view passing can as an extra
            val intent = Intent(this@CatalogueActivity, CanDetailActivity::class.java)
            intent.putExtra(CAN_DETAIL_KEY, canAdapter!!.getItem(position))
            startActivity(intent)
        }
    }

    //Regular expression used by the normalizer
    private val REGEX_UNACCENT = "\\p{InCombiningDiacriticalMarks}+".toRegex()

    //function that utilizes Java's normalizer class. Will only run on JVM. Not used since it doesnt properly translate đ to d or dj
    fun CharSequence.unaccent(): String {
        val temp = Normalizer.normalize(this, Normalizer.Form.NFD)
        return REGEX_UNACCENT.replace(temp, "")
    }

    //Checks if buttons should be enabled or disabled
    private fun checkEnable() {
        btnNext!!.isEnabled = increment + 1 !== pageCount
        btnPrev!!.isEnabled = increment !== 0
    }

}
