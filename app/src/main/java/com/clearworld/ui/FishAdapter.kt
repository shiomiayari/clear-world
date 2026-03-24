package com.clearworld.ui

import android.graphics.Color
import android.graphics.drawable.GradientDrawable
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.RecyclerView
import com.clearworld.R
import com.clearworld.db.entity.Fish

class FishAdapter : ListAdapter<Fish, FishAdapter.FishViewHolder>(DiffCallback) {

    inner class FishViewHolder(view: View) : RecyclerView.ViewHolder(view) {
        private val colorView: View = view.findViewById(R.id.view_fish_color)
        private val sizeLabel: TextView = view.findViewById(R.id.tv_fish_size)

        fun bind(fish: Fish) {
            // HSVから色を生成して丸く表示
            val color = Color.HSVToColor(
                floatArrayOf(fish.colorHue.toFloat(), fish.colorSaturation, 0.9f)
            )
            val circle = GradientDrawable().apply {
                shape = GradientDrawable.OVAL
                setColor(color)
            }
            colorView.background = circle

            sizeLabel.text = when (fish.size) {
                "large"  -> "🐟 大"
                "medium" -> "🐠 中"
                else     -> "🐡 小"
            }
        }
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): FishViewHolder {
        val view = LayoutInflater.from(parent.context)
            .inflate(R.layout.item_fish, parent, false)
        return FishViewHolder(view)
    }

    override fun onBindViewHolder(holder: FishViewHolder, position: Int) {
        holder.bind(getItem(position))
    }

    private object DiffCallback : DiffUtil.ItemCallback<Fish>() {
        override fun areItemsTheSame(oldItem: Fish, newItem: Fish) = oldItem.id == newItem.id
        override fun areContentsTheSame(oldItem: Fish, newItem: Fish) = oldItem == newItem
    }
}
