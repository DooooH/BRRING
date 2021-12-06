package com.cookandroid.lowest_price_alert

import android.app.NotificationChannel
import android.app.NotificationManager
import android.app.PendingIntent
import android.app.Service
import android.content.Context
import android.content.Intent
import android.graphics.BitmapFactory
import android.graphics.Color
import android.os.Build
import android.os.IBinder
import android.widget.Toast
import androidx.core.app.NotificationCompat
import com.google.firebase.database.*
import com.google.firebase.firestore.FirebaseFirestore

class MyService : Service() {


    val channel_name: String = "CHANNEL_1"
    val CHANNEL_ID: String = "MY_CH"

    override fun onStartCommand(intent: Intent, flags: Int, startId: Int): Int {
        val firestoredb = FirebaseFirestore.getInstance() // firestore db
        val firebaseDatabase = FirebaseDatabase.getInstance() // 실시간 데이터 db
        var now_user = "nhUKsEBop5beTg2c4jT4vZtYj842"
        if (intent.hasExtra("user_id")) {
            now_user = intent.getStringExtra("user_id").toString()
        }
        firestoredb.collection("user").document(now_user).get()
            .addOnSuccessListener { result ->
                val wish_list = result["wish_list"] as ArrayList<String>

                firestoredb.collection("product_list").get()
                    .addOnSuccessListener { product ->

                        for (i: Int in wish_list.size - 1 downTo 0) {
                            for (document in product) {
                                val p_no = document["no"].toString()
                                val name = document["name"].toString()
                                if (wish_list[i].equals(p_no)) {



                                    val notificationId: Int = 1002
                                    val intent_noti = Intent(this, IntroActivity::class.java).apply { // 알림 클릭시 실행할 Activity 지정

                                    }

                                    var name_substring = ""
                                    if(name.length > 25){
                                        name_substring = name.substring(0,25)
                                    }
                                    else{
                                        name_substring = name.toString()
                                    }
                                    val pendingIntent: PendingIntent = PendingIntent.getActivity(applicationContext, 0, intent_noti, 0)
                                    var mLargeIconForNoti = BitmapFactory.decodeResource(getResources(),R.drawable.brring_square)
                                    var builder = NotificationCompat.Builder(this, CHANNEL_ID) // 푸쉬 알람 기능
                                        .setSmallIcon(R.drawable.brring_icon)
                                        .setContentTitle("BRRING 최저가 알림") // 푸쉬 알람에 띄울 큰 문장
                                        .setContentText(name_substring) // 푸쉬 알람에 띄울 작은 문장
                                        .setPriority(NotificationCompat.PRIORITY_DEFAULT)
                                        .setLargeIcon(mLargeIconForNoti)
                                        .setContentIntent(pendingIntent) // 알림 클릭 시 Activity 실행

                                    var min_cost = 1000000000
                                    var change_flag = 0

                                    val path = "product_list/" + p_no.toString() // 실시간 db에 접근하기 위한 경로. 현재는 하드코딩.
                                    val myRef: DatabaseReference = firebaseDatabase.getReference(path) // 실시간 db에 접근

                                    myRef.addValueEventListener(object : ValueEventListener {
                                        override fun onDataChange(snapshot: DataSnapshot) {
                                            val snapshot_info = snapshot.child("price")




                                            for (item in snapshot_info.children) {

                                                var price_info = item.value.toString()  //  실시간 db로 부터 가져오는 가격 정보
                                                if (min_cost > price_info.toInt()) {
                                                    if (change_flag == 1) {
                                                        Toast.makeText(
                                                            applicationContext, "This is a Service running in Background",
                                                            Toast.LENGTH_SHORT
                                                        ).show()

                                                        // 기존에 들어있던 가격이면 알림 없음
                                                        createNotificationChannel(
                                                            builder,
                                                            notificationId
                                                        ) // 새로 들어온 정보가 최저가이면 알림

                                                        firestoredb.collection("user").document(now_user).get() // db에 알람 리스트 갱신
                                                            .addOnSuccessListener { result ->

                                                                val alarm_list = result["alarm_list"] as ArrayList<String>
                                                                alarm_list.add(document.id)// 찜 목록에 추가

                                                                firestoredb.collection("user").document(now_user)
                                                                    .update("alarm_list", alarm_list)
                                                            }

                                                    }
                                                    min_cost =
                                                        Math.min(min_cost, price_info.toInt()) // 최저가 갱신
                                                }
                                            }

                                            change_flag = 1



                                        }

                                        override fun onCancelled(error: DatabaseError) { // 실시간 db 접근을 실패하면
                                            println("Failed to read value.")
                                        }
                                    })





                                }
                            }
                        }
                    }
            }

        return START_STICKY
    }

    override fun onBind(intent: Intent): IBinder? {
        throw UnsupportedOperationException("Not yet implemented")
    }

    private fun createNotificationChannel( // 푸쉬 알림 생성
        builder: NotificationCompat.Builder,
        notificationId: Int
    ) {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            val descriptionText = "notification for lowest price"
            val importance = NotificationManager.IMPORTANCE_DEFAULT
            val channel = NotificationChannel(CHANNEL_ID, channel_name, importance).apply {
                description = descriptionText
            }

            channel.lightColor = Color.BLUE
            //channel.enableVibration(true)
            val notificationManager: NotificationManager =
                getSystemService(Context.NOTIFICATION_SERVICE) as NotificationManager
            notificationManager.createNotificationChannel(channel)

            notificationManager.notify(notificationId, builder.build())
        } else {
            val notificationManager: NotificationManager =
                getSystemService(Context.NOTIFICATION_SERVICE) as NotificationManager
            notificationManager.notify(notificationId, builder.build())
        }
    }

}
