package com.cookandroid.lowest_price_alert

import android.content.Context
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.BaseAdapter
import android.widget.ImageView
import android.widget.TextView
import com.bumptech.glide.Glide

internal class SearchAdapter(
    private val context: Context,
    private val imageArray: Array<String>,
    private val nameInWords: Array<String>,
    private val priceInWords: Array<String>,
    private val specInWords: Array<String>
):
BaseAdapter() {
    private var layoutInflater: LayoutInflater? = null
    private lateinit var imageView: ImageView
    private lateinit var nameView: TextView
    private lateinit var priceView: TextView
    private lateinit var specView: TextView

    override fun getCount(): Int {
        return nameInWords.size
    }
    override fun getItem(position: Int): Any? {
        return null
    }
    override fun getItemId(position: Int): Long {
        return 0
    }
    override fun getView(
        position: Int,
        convertView: View?,
        parent: ViewGroup
    ): View? {
        var convertView = convertView
        if (layoutInflater == null) {
            layoutInflater =
                context.getSystemService(Context.LAYOUT_INFLATER_SERVICE) as LayoutInflater
        }
        if (convertView == null) {
            convertView = layoutInflater!!.inflate(R.layout.grid_item, null)
        }
        imageView = convertView!!.findViewById(R.id.gridImg)
        nameView = convertView.findViewById(R.id.gridName)
        priceView = convertView.findViewById(R.id.gridPrice)
        specView = convertView.findViewById(R.id.gridSpec)

//        imageView.setImageResource(imageArray[position])
//        Picasso.get().load(imageArray[position]).into(imageView)
        Glide.with(context)
            .load("http:"+imageArray[position])
            .into(imageView)
        nameView.text = nameInWords[position]
        priceView.text = priceInWords[position]
        specView.text = specInWords[position]

        return convertView
    }
}