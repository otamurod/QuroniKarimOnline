package com.otamurod.quronikarim.app.presentation.ui.adapter

import android.annotation.SuppressLint
import android.content.Context
import android.view.LayoutInflater
import android.view.ViewGroup
import android.widget.Toast
import androidx.recyclerview.widget.RecyclerView
import com.otamurod.quronikarim.app.domain.model.surah.Surah
import com.otamurod.quronikarim.databinding.ItemRvBinding

class MainAdapter(
    var context: Context,
    var onClick: OnClick
) : RecyclerView.Adapter<MainAdapter.VH>() {
    var items = ArrayList<Surah>()

    @SuppressLint("NotifyDataSetChanged")
    fun setUpdatedData(items: List<Surah>) {
            try {
                this.items = items as ArrayList<Surah>
            }catch (e:Exception){
                Toast.makeText(context, "Loading...", Toast.LENGTH_SHORT).show()
            }
        notifyDataSetChanged()
    }

    inner class VH(val itemRvBinding: ItemRvBinding) : RecyclerView.ViewHolder(itemRvBinding.root) {

        fun onBind(surah: Surah) {

            itemRvBinding.number.text = surah.number.toString()
            itemRvBinding.name.text = surah.name
            itemRvBinding.description.text = surah.englishName

            itemRvBinding.root.setOnClickListener {
                onClick.onItemClick(surah.number)
            }
        }
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): VH {
        return VH(ItemRvBinding.inflate(LayoutInflater.from(parent.context), parent, false))
    }

    override fun onBindViewHolder(holder: VH, position: Int) {
        holder.onBind(items[position])
    }

    override fun getItemCount() = items.size

    interface OnClick {
        fun onItemClick(surahNumber: Int)
    }
}