package com.example.hsexercise.feature

import android.content.Context
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.example.hsexercise.R
import com.example.hsexercise.feature.database.FeatureModel
import kotlinx.android.synthetic.main.image_layout.view.*

class PicturesAdapter(val picturesList:List<FeatureModel>): RecyclerView.Adapter<PicturesAdapter.PicturesViewHolder>() {

    private lateinit var context: Context
    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): PicturesViewHolder {
        context = parent.context.applicationContext
        val view = LayoutInflater.from(parent.context).inflate(R.layout.image_layout, parent, false)
        return PicturesViewHolder(view)
    }

    override fun getItemCount(): Int = picturesList.size

    override fun onBindViewHolder(holder: PicturesViewHolder, position: Int) {
        Glide.with(context)
            .load(picturesList[position].url)
            .into(holder.picture)

        holder.author.text  = picturesList.get(position).author
        val dimen =  "${picturesList[position].width} x ${picturesList[position].height}"
        holder.demensions.text = dimen
    }


    class PicturesViewHolder(itemView: View): RecyclerView.ViewHolder(itemView){
        var picture = itemView.pictureImageView
        var author = itemView.authorTextView
        var demensions = itemView.dimensionsTextView
    }
}