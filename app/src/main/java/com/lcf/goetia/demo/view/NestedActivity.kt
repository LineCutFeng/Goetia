package com.lcf.goetia.demo.view

import android.graphics.Color
import android.os.Bundle
import android.util.Log
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.appcompat.app.AppCompatActivity
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.lcf.goetia.demo.R

class NestedActivity : AppCompatActivity() {

    private val TAG = "NestedActivity"
    lateinit var nested: CustomView
    lateinit var recyclerview: RecyclerView

    val list = ArrayList<String>().apply {
        for (i in 1..100) {
            this.add("$i")
        }
    }


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_nested)

        nested = findViewById(R.id.cv)
        nested.setTopView(TextView(this).apply {
            this.text = "我是一行文本，啦啦啦"
        })
        recyclerview = nested.recyclerView

        recyclerview.layoutManager = LinearLayoutManager(this)
        recyclerview.adapter = ArrayAdapter()

//
//        recyclerview.adapter =
    }

    inner class ArrayAdapter : RecyclerView.Adapter<ArrayAdapter.MyViewHolder>() {

        override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): MyViewHolder {
            val inflate = layoutInflater.inflate(R.layout.array_adapter_item, parent, false)
            return MyViewHolder(inflate)
        }

        override fun getItemCount(): Int {
            return list.size
        }

        override fun onBindViewHolder(holder: MyViewHolder, position: Int) {
            (holder.itemView as? TextView)?.text = list[position]
        }

        inner class MyViewHolder(val iView : View) : RecyclerView.ViewHolder(iView) {

        }
    }
}