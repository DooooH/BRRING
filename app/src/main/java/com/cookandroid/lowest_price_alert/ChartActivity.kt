package com.cookandroid.lowest_price_alert

import android.os.Bundle
import android.os.SystemClock
import androidx.appcompat.app.AppCompatActivity
import com.github.mikephil.charting.charts.LineChart
import com.github.mikephil.charting.data.Entry
import com.github.mikephil.charting.data.LineData
import com.github.mikephil.charting.data.LineDataSet

class ChartActivity : AppCompatActivity() {
    lateinit var lineChart : LineChart

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.product_info)

        val thread = ThreadClass()
        thread.start()
    }

    inner class ThreadClass : Thread() {
        override fun run() {
            lineChart = findViewById<LineChart>(R.id.linechart)
            val input = Array<Double>(200,{Math.random()})
            // Entry 배열 생성
            var entries: ArrayList<Entry> = ArrayList()
            // Entry 배열 초기값 입력
            entries.add(Entry(0F,0F))
            // 그래프 구현을 위한 LineDataSet 생성
            var dataset: LineDataSet = LineDataSet(entries, "input")
            // 그래프 data 생성 -> 최종 입력 데이터
            var data: LineData = LineData(dataset)
            // chart.xml에 배치된 lineChart에 데이터 연결
            lineChart.data = data

            runOnUiThread {
                // 그래프 생성
                lineChart.animateXY(1, 1)
            }

            for (i in 0 until input.size){

                SystemClock.sleep(500)
                data.addEntry(Entry(i.toFloat(), input[i].toFloat()), 0)
                data.notifyDataChanged()
                lineChart.notifyDataSetChanged()
                lineChart.invalidate()
            }

        }
    }
}