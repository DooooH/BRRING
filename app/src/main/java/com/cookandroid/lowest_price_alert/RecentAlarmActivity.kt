package com.cookandroid.lowest_price_alert

import android.content.Context
import android.content.Intent
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.*
import androidx.appcompat.app.AppCompatActivity
import com.bumptech.glide.Glide
import com.google.firebase.firestore.FirebaseFirestore
import org.w3c.dom.Text

class RecentAlarmActivity : AppCompatActivity() {


    public override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.recent_alarm_activity)

        val now_user = "nhUKsEBop5beTg2c4jT4vZtYj842"  // 현재 유저 번호 (현재는 하드코딩)
        val firestoredb = FirebaseFirestore.getInstance() // firestore db

        var alarmList = arrayListOf<Alarm>()
        var str = ""
        var back_btn = findViewById<ImageButton>(R.id.back_button)
        var alarm_text = findViewById<TextView>(R.id.alarm_title)


        back_btn.setOnClickListener {
            onBackPressed()
        }

        firestoredb.collection("user").document(now_user).get().addOnSuccessListener { result ->
            val alarm_list = result["alarm_list"] as ArrayList<String>
            alarm_text.text = "최근 알람 (" + alarm_list.size.toString() + ")"
            firestoredb.collection("product_list").get()
                .addOnSuccessListener { product ->

                    for (i: Int in 0..alarm_list.size - 1) {
                        alarm_list[i] = alarm_list[i].replace(" ", "")

                    }
                    for (i: Int in 0..alarm_list.size - 1) {
                        for (document in product) {

                            val item = Alarm(
                                document["name"] as String,
                                document.id,
                                document["no"] as String,
                                document["image_url"] as String
                            )
                            val p_no = document.id.toString()


                            if (alarm_list[i].equals(p_no) == true) {

                                alarmList.add(item)
                            }
                        }


                        var alarm_list_view = findViewById<ListView>(R.id.alarm_list_view)
                        val alarmAdapter = MainListAdapter(this, alarmList)
                        alarm_list_view.adapter = alarmAdapter




                        alarm_list_view.setOnItemClickListener { parent, view, position, id ->

                            val code = alarmList.get(position).product_code
                            var number = alarmList.get(position).product_no


                            val intent = Intent(this, ChartActivity::class.java)
                            intent.putExtra("product_code", code.toString())
                            intent.putExtra("product_no", number.toString())
                            startActivity(intent)
                        }
                    }

                }
        }
    }


    class Alarm(
        val name: String,
        val product_code: String,
        val product_no: String,
        val photo: String
    )

    class MainListAdapter(val context: Context, val AlarmList: ArrayList<Alarm>) :
        BaseAdapter() {

        override fun getView(position: Int, convertView: View?, parent: ViewGroup?): View {
            val view: View = LayoutInflater.from(context).inflate(R.layout.recent_alarm_item, null)

            val Photo = view.findViewById<ImageView>(R.id.alarm_item_Img)
            val name = view.findViewById<TextView>(R.id.alarm_item_name)
            val sub1 = view.findViewById<TextView>(R.id.alarm_sub1_text)

            val alarm = AlarmList[position]
            var url = "http:" + alarm.photo

            Glide.with(this.context).load(url)
                .into(Photo) //이미지 url로 사진 불러오기

            name.text = alarm.name
            sub1.text = "최저가 알림 정보가 있습니다"
            return view
        }

        override fun getCount(): Int {
            return AlarmList.size
        }

        override fun getItem(position: Int): Any {
            return AlarmList[position]
        }

        override fun getItemId(p0: Int): Long {
            return 0
        }


    }

}



