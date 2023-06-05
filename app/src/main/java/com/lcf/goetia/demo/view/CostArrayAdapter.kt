package com.lcf.goetia.demo.view

import android.content.Context
import android.view.View
import android.view.ViewGroup
import android.widget.ArrayAdapter
import androidx.annotation.LayoutRes

class CostArrayAdapter<T>(context: Context, @LayoutRes resource: Int, list: List<T>) :
    ArrayAdapter<T>(context, resource, list) {
    override fun getView(position: Int, convertView: View?, parent: ViewGroup): View {
        runCatching { Thread.sleep((Math.random() * 10).toLong()) }
        return super.getView(position, convertView, parent)
    }
}