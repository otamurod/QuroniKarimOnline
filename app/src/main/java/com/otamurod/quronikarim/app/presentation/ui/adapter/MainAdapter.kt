package com.otamurod.quronikarim.app.presentation.ui.adapter

import android.annotation.SuppressLint
import android.content.Context
import android.view.LayoutInflater
import android.view.ViewGroup
import android.widget.Toast
import androidx.recyclerview.widget.RecyclerView
import com.otamurod.quronikarim.app.domain.model.surah.Surah
import com.otamurod.quronikarim.databinding.ItemSurahBinding


class MainAdapter(
    var context: Context, var onClick: OnClick
) : RecyclerView.Adapter<MainAdapter.VH>() {
    private var items = ArrayList<Surah>()

    @SuppressLint("NotifyDataSetChanged")
    fun setUpdatedData(items: List<Surah>) {
        try {
            this.items = items as ArrayList<Surah>
        } catch (e: Exception) {
            Toast.makeText(context, "Loading...", Toast.LENGTH_SHORT).show()
        }
        notifyDataSetChanged()
    }

    inner class VH(private val itemSurahBinding: ItemSurahBinding) :
        RecyclerView.ViewHolder(itemSurahBinding.root) {
        fun onBind(surah: Surah) {
            itemSurahBinding.number.text = surah.number.toString()
            itemSurahBinding.name.text = surah.name
            itemSurahBinding.description.text = surah.englishName

            itemSurahBinding.root.setOnClickListener {
                onClick.onItemClick(surah)
            }
        }
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): MainAdapter.VH {
        return VH(ItemSurahBinding.inflate(LayoutInflater.from(parent.context), parent, false))
    }

    override fun onBindViewHolder(holder: VH, position: Int) {
        holder.onBind(items[position])
    }

    override fun getItemCount() = items.size

    interface OnClick {
        fun onItemClick(surah: Surah)
    }
}