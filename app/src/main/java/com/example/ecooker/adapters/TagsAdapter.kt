package com.example.ecooker.adapters

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.LinearLayout
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.example.ecooker.R
import com.example.ecooker.models.Tags

class TagsAdapter(
    private val data: List<Tags>,
    private val onTagClicked: (String) -> Unit
) : RecyclerView.Adapter<TagsAdapter.ViewHolder>() {

    private val expandedPositions = mutableSetOf<Int>()

    inner class ViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        val categoryName: TextView = itemView.findViewById(R.id.categoryName)
        val tagsLayout: LinearLayout = itemView.findViewById(R.id.tagsLayout)
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val view = LayoutInflater.from(parent.context).inflate(R.layout.item_tag, parent, false)
        return ViewHolder(view)
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        val category = data[position]
        holder.categoryName.text = category.categoryName

        holder.tagsLayout.removeAllViews()
        for (tag in category.tags) {
            val tagView = LayoutInflater.from(holder.itemView.context)
                .inflate(R.layout.item_single_tag, holder.tagsLayout, false) as TextView
            tagView.text = tag
            tagView.setOnClickListener {
                onTagClicked(tag)
            }
            holder.tagsLayout.addView(tagView)
        }


        // Zaktualizowany onClickListener dla głównego elementu
        holder.itemView.setOnClickListener {
            if (expandedPositions.contains(position)) {
                expandedPositions.remove(position)
                holder.tagsLayout.visibility = View.GONE
            } else {
                expandedPositions.add(position)
                holder.tagsLayout.visibility = View.VISIBLE
            }
        }
    }

    override fun getItemCount() = data.size
}
