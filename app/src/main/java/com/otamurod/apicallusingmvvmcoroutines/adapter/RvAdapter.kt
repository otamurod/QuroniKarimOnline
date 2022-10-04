package com.otamurod.apicallusingmvvmcoroutines.adapter

import android.annotation.SuppressLint
import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.otamurod.apicallusingmvvmcoroutines.databinding.ItemRvBinding
import com.otamurod.apicallusingmvvmcoroutines.models.RecyclerData
import com.squareup.picasso.Picasso

class RvAdapter() : RecyclerView.Adapter<RvAdapter.VH>() {
    var items = ArrayList<RecyclerData>()

    @SuppressLint("NotifyDataSetChanged")
    fun setUpdatedData(items: ArrayList<RecyclerData>) {
        this.items = items
        notifyDataSetChanged()
    }

    inner class VH(val itamRvBinding: ItemRvBinding) : RecyclerView.ViewHolder(itamRvBinding.root) {

        fun onBInd(data: RecyclerData) {
            
            itamRvBinding.number.text = data.number.toString()
            itamRvBinding.name.text = data.name
            itamRvBinding.description.text = data.englishName
        }
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): VH {
        return VH(ItemRvBinding.inflate(LayoutInflater.from(parent.context), parent, false))
    }

    override fun onBindViewHolder(holder: VH, position: Int) {
        holder.onBInd(items[position])
    }

    override fun getItemCount() = items.size
}