package com.cookandroid.lowest_price_alert

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.widget.*
import com.bumptech.glide.Glide
import com.github.mikephil.charting.charts.LineChart
import com.github.mikephil.charting.components.YAxis
import com.github.mikephil.charting.data.Entry
import com.github.mikephil.charting.data.LineData
import com.github.mikephil.charting.data.LineDataSet
import com.github.mikephil.charting.interfaces.datasets.ILineDataSet
import com.google.firebase.database.*
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.database.DatabaseReference
import com.google.firebase.database.FirebaseDatabase

class ChartActivity : AppCompatActivity() {
    lateinit var lineChart: LineChart
    val chartData = ArrayList<ChartData>() // Line Chart에 그리기 위한 데이터를 담을 ArrayList
    val product_list = ArrayList<ProductData>() // firestore에 존재하는 모든 제품 정보를 담을 ArrayList

    public override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.product_info)
        title = "최저가 알리미"

        val firebaseDatabase = FirebaseDatabase.getInstance() // 실시간 데이터 db
        val firestoredb = FirebaseFirestore.getInstance() // firestore db


        var imageview = findViewById<ImageView>(R.id.imageview) // 제품 사진 ImageView
        var product_name_textView = findViewById<TextView>(R.id.nametext) // 제품명 TextView
        var most_cheap_textview = findViewById<TextView>(R.id.most_cheap) // 최저가 TextView
        var avg_textview = findViewById<TextView>(R.id.average) // 평균가 TextView
        var most_expensive_textview = findViewById<TextView>(R.id.most_expensive) // 최고가 TextView
        //var product_url_textview = findViewById<TextView>(R.id.url_text) // 이미지 불러오기 test 용

        firestoredb.collection("product_list") //firestore db로부터 등록된 모든 제품 읽어오기
            .get()
            .addOnSuccessListener { result ->
                product_list.clear()
                for(document in result){ // firestore db에 담긴 모든 제품에 대하여
                    val product = ProductData()
                    product.product_no = document["no"].toString() // 제품 번호
                    product.StartDate = document["start_date"].toString() // 제품 크롤링 시작 날짜
                    product.LastDate = document["last_date"].toString() // 제품 크롤링 마지막 날짜
                    product.imageURL = document["image_url"].toString() // 제품 사진 url
                    product.name = document["name"].toString() // 제품명
                    product_list.add(product) // product_list에 해당 제품 추가
                }

                var product_no_now = "15253217" // 일단은 하드코딩. 원래는 받아와야됨. (사용자가 선택한 물품 번호)
                var product_index_now = -1 // product_list에 담긴 제품 중 필요한 제품에 바로 접근하기 위해 index를 저장하기 위한 변수
                for(i: Int in 0..product_list.size-1){ // 사용자가 선택한 물품 번호 검색
                    if(product_no_now.equals(product_list.get(i).product_no)){ // 사용자가 선택한 물품 번호와 같으면
                        product_index_now = i // index 저장
                        break
                    }
                }

                var start_date_info = product_list.get(product_index_now).StartDate.split("-") // 시작 날짜 정보 가공 (나중에 업그레이드 해야함)
                var start_date = start_date_info[2].toInt() // 현재 코드는 같은 달 내에서만 작동
                var last_date_info = product_list.get(product_index_now).LastDate.split("-") // 마지막 날짜 정보 가공 (나중에 업그레이드 해야함)
                var last_date = last_date_info[2].toInt()

                var price_info = "0" //  실시간 db로 부터 가져오는 가격 정보
                var max_cost = 0 // 최고가 검색을 위한 변수
                var min_cost = 1000000000 // 최저가 검색을 위한 변수
                var avg_cost = 0 // 평균가 검색을 위한 변수
                var total_cost = 0 // 평균가 검색을 위해 모든 가격에 대한 합산을 위한 변수
                var count_record = 0 // 평균가 검색을 위해 검색한 가격의 수에 대한 변수

                while(start_date <= last_date){ // 불완전한 코드이기는 한데 일단 현재 수집한 모든 정보에 대하여, 라는 뜻
                    count_record ++ // 검색한 가격 갯수 ++

                    val path = "product_list/15253217/price/" // 실시간 db에 접근하기 위한 경로. 현재는 하드코딩.
                    var date = "" // chart에 날짜를 표기하기 위해 날짜 정보 저장
                    if(start_date < 10){ // 한자리 수이면 0이 하나 더 붙어야 해
                        date = "2021-11-0" + start_date.toString()
                    }else{
                        date = "2021-11-" + start_date.toString()
                    }

                    val now_path = path + date
                    //var image = "http://img.danawa.com/prod_img/500000/217/253/img/15253217_1.jpg?shrink=330:330&_v=20211026140920"
                    start_date++
                    Glide.with(this).load(R.drawable.ipad).into(imageview) // 지금은 그냥 하드코딩. 이미지 url로 불러오는거 시도해봐야 함


                    val myRef : DatabaseReference = firebaseDatabase.getReference(now_path) // 실시간 db에 접근
                    product_name_textView.text = "제품명 : " + product_list.get(product_index_now).name // 제품명 textView에 띄우기
                    //product_url_textview.text = "이미지 : "+ product_list.get(product_index_now).imageURL
                    
                    //Read from the database
                    myRef.addValueEventListener(object : ValueEventListener {
                        override fun onDataChange(snapshot: DataSnapshot) {
                            price_info = snapshot?.value.toString() // 실시간 db로 부터 가격 정보 가져옴
                            max_cost = Math.max(max_cost, price_info.toInt()) // 최고가 갱신
                            min_cost = Math.min(min_cost, price_info.toInt()) // 최저가 갱신
                            total_cost += price_info.toInt() // 가격 합산 갱신
                            avg_cost = total_cost / count_record // 평균가 갱신

                            most_cheap_textview.text = "최저가 : " + min_cost.toString() + "원" // textview에 최저가 띄우기
                            most_expensive_textview.text = "최고가 : " + max_cost.toString()+ "원"// textview에 최고가 띄우기
                            avg_textview.text = "평균가 : " + avg_cost.toString()+ "원"// textview에 평균가 띄우기


                            addChartItem(date,price_info.toDouble()) // Chart를 그리기 위해 가격 및 날짜 정보 추가
                            LineChartGraph(chartData, "price") // 그래프 그리기
                        }

                        override fun onCancelled(error: DatabaseError) { // 실시간 db 접근을 실패하면
                            println("Failed to read value.")
                        }

                    })

                }
            }.addOnFailureListener { exception -> // fire store 접근을 실패하면
            }
    }

    data class ProductData(
        var product_no : String = "",
        var StartDate: String = "",
        var LastDate: String = "",
        var imageURL : String = "",
        var name : String=""
    )

    data class ChartData(
        var dateData: String = "",
        var priceData: Double = 0.0
    )
    
    private fun addChartItem(date: String, price: Double) { // 차트에 담을 정보 추가
        val item = ChartData() 
        item.priceData = price // 가격
        item.dateData = date // 날짜
        chartData.add(item)
    }
    
    private fun LineChartGraph(chartItem: ArrayList<ChartData>, displayname: String) {
        lineChart = findViewById(R.id.lineChart)

        val entries = ArrayList<Entry>()

        for (i in chartItem.indices) {
            entries.add(Entry(chartItem[i].priceData.toFloat(), i)) //  그래프 그리기 위해서 가격 정보 추가
        }

        val depenses = LineDataSet(entries, displayname)
        depenses.axisDependency = YAxis.AxisDependency.LEFT
        depenses.valueTextSize = 12f // 값 폰트 지정하여 크게 보이게 하기
        //depenses.setColors(ColorTemplate.COLORFUL_COLORS) //그래프 색깔
        //depenses.setDrawCubic(true); //선 둥글게 만들기
        depenses.setDrawFilled(false) //그래프 밑부분 색칠

        val labels = ArrayList<String>()
        for (i in chartItem.indices) {
            labels.add(chartItem[i].dateData) // 그래프 그리기 위해서 날짜 정보 추가
        }

        val dataSets = ArrayList<ILineDataSet>()
        dataSets.add(depenses as ILineDataSet)
        val data = LineData(labels, dataSets) // 라이브러리 v3.x 사용하면 에러 발생함

        lineChart.data = data
        lineChart.animateXY(1000,1000);
        lineChart.invalidate()
    }
}
