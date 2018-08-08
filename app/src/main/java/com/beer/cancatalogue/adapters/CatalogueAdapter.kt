package com.beer.cancatalogue.adapters

import android.view.ViewGroup
import android.widget.ArrayAdapter
import android.view.View
import android.content.Context
import com.beer.cancatalogue.models.Can
import com.squareup.picasso.Picasso
import android.widget.TextView
import android.view.LayoutInflater
import android.widget.ImageView
import com.beer.cancatalogue.R
import android.net.Uri

//ArrayAdapter of Cans. Has constructor explicitly stated
class CatalogueAdapter(context: Context, aCans: ArrayList<Can>) : ArrayAdapter<Can>(context, 0, aCans) {
    //Inner class to hold graphical components
    private class ViewHolder {
        var ivCanPhoto: ImageView? = null
        var tvTitle: TextView? = null
        var tvDesc: TextView? = null
    }

    //Translates can that is displayed into a row in our adapter
    override fun getView(position: Int, convertView: View?, parent: ViewGroup): View {
        var convertView = convertView

        //scoop the data from this position
        val can = getItem(position)

        //This part of the code checks if the view is being reused. Otherwise, it inflates the view to be displayed
        val viewHolder: ViewHolder
        if (convertView == null) {
            viewHolder = ViewHolder()
            val inflater = context.getSystemService(Context.LAYOUT_INFLATER_SERVICE) as LayoutInflater
            convertView = inflater.inflate(R.layout.can_item, parent, false)
            viewHolder.ivCanPhoto = convertView!!.findViewById<View>(R.id.ivCanPhoto) as ImageView
            viewHolder.tvTitle = convertView.findViewById<View>(R.id.tvTitle) as TextView
            viewHolder.tvDesc = convertView.findViewById<View>(R.id.tvDesc) as TextView
            convertView.tag = viewHolder
        } else {
            viewHolder = convertView.tag as ViewHolder
        }

        //Add can title and full description to be displayed. Also displaying picture from image's URL
        viewHolder.tvTitle!!.text = can!!.title
        viewHolder.tvDesc!!.text = can.info
        Picasso.with(context).load(Uri.parse(can.canPicture)).error(R.drawable.empty3).into(viewHolder.ivCanPhoto)
        // Return the completed view to render on screen
        return convertView
    }
}