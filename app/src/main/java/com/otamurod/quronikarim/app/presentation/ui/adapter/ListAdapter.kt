package com.otamurod.quronikarim.app.presentation.ui.adapter

import android.content.Context
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ArrayAdapter
import com.otamurod.quronikarim.R
import com.otamurod.quronikarim.databinding.ListItemBinding

class ListAdapter(
    private var myContext: Context,
    listItem: Int,
    private val list: Array<String>,
    private val img: Int,
    private val selected: String?
) : ArrayAdapter<String>(myContext, listItem, list) {

    override fun getView(position: Int, convertView: View?, parent: ViewGroup): View {
        val binding: ListItemBinding = if (convertView == null) {
            ListItemBinding.inflate(LayoutInflater.from(myContext), parent, false)
        } else {
            ListItemBinding.bind(convertView)
        }
        if (selected!!.length == 2) {
            binding.title.isAllCaps = true
        }
        binding.title.text = list[position]
        binding.icon.setImageResource(img)
        if (list[position] == selected) {
            binding.selected.setImageResource(R.drawable.ic_selected)
        } else {
            binding.selected.setImageResource(0)
        }
        return binding.root
    }
}