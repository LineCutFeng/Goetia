package com.lcf.goetia.demo

import android.os.Bundle
import android.widget.ListView
import androidx.appcompat.app.AppCompatActivity
import androidx.databinding.DataBindingUtil
import com.lcf.goetia.demo.view.CostArrayAdapter
import com.lcf.goetia.choreographerhook.frame.choreographerhook.FrameTracker
import com.lcf.goetia.choreographerhook.frame.choreographerhook.OnFpsChangeListener
import com.lcf.goetia.demo.databinding.ActivityMainBinding

class FrameDemoActivity : AppCompatActivity() {

    lateinit var activityMainBinder: ActivityMainBinding

    val listener = Listener()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        activityMainBinder = DataBindingUtil.setContentView<ActivityMainBinding>(this, R.layout.activity_main)

        val list = ArrayList<String>()
        for (i in 1..100) {
            list.add("$i")
        }
        findViewById<ListView>(R.id.list_view).adapter =
            CostArrayAdapter(this, R.layout.array_adapter_item, list)
        FrameTracker.getFrameTracker(this).let {
            activityMainBinder.frameLineCharView.maxFps = (1000000000F / it.frameIntervalNanos + 0.5F).toInt()
            it.register(listener)
        }
    }

    inner class Listener : OnFpsChangeListener(), FrameTracker.FrameListener {
        override fun onFpsChange(fps: Int) {
            activityMainBinder.frameLineCharView.addFps(fps)
        }

        override fun onFrame(startNs: Long, endNs: Long, dropFrame: Int) {
            onFrameChange(dropFrame)
        }
    }

    override fun onDestroy() {
        FrameTracker.getFrameTracker(this).unregister(listener)
        super.onDestroy()
    }
}