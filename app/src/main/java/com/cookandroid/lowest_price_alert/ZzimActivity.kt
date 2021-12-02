package com.cookandroid.lowest_price_alert

import android.content.Context
import android.content.Intent
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.BaseAdapter
import android.widget.ImageView
import android.widget.ListView
import android.widget.TextView
import androidx.appcompat.app.AppCompatActivity
import com.bumptech.glide.Glide
import com.google.firebase.firestore.FirebaseFirestore

class ZzimActivity : AppCompatActivity() {



    public override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.zzim_activity)

        val now_user = "nhUKsEBop5beTg2c4jT4vZtYj842"  // 현재 유저 번호 (현재는 하드코딩)
        val firestoredb = FirebaseFirestore.getInstance() // firestore db

        var zzim_title_text = findViewById<TextView>(R.id.zzim_title)
        var productList = arrayListOf<Product>()
        var texttest = findViewById<TextView>(R.id.test)
        var str = ""

        firestoredb.collection("user").document(now_user).get().addOnSuccessListener { result ->
            val wish_list = result["wish_list"] as ArrayList<String>
            firestoredb.collection("product_list").get()
                .addOnSuccessListener { product ->

                    zzim_title_text.text = "찜한 상품 (" + wish_list.size.toString() + ")"

                    for (document in product) {
                        val item = Product(
                            document["name"] as String,
                            document["image_url"] as String,
                            "sub1",
                            "sub2",
                            document["no"] as String,
                            document.id as String
                        )
                        val p_no = document["no"].toString()

                        for (i: Int in 0..wish_list.size - 1) {
                            if (wish_list[i].equals(p_no)) {
                                productList.add(item)
                                str += item.photo.toString() + "\n"
                                break
                            }
                        }

                        var zzim_list_view = findViewById<ListView>(R.id.zzim_list_view)
                        val zzimAdapter = MainListAdapter(this, productList)
                        zzim_list_view.adapter = zzimAdapter




                        zzim_list_view.setOnItemClickListener { parent, view, position, id ->

                            val number = productList.get(position).no
                            val code = productList.get(position).product_code
                            val intent = Intent(this, ChartActivity::class.java)
                            intent.putExtra("product_code", code.toString())
                            intent.putExtra("product_no", number.toString())
                            startActivity(intent)
                        }
                    }

                }


        }

    }

    class Product(
        val name: String,
        val photo: String,
        val sub1: String,
        val sub2: String,
        val no: String,
        val product_code: String
    )

    class MainListAdapter(val context: Context, val ProductList: ArrayList<Product>) :
        BaseAdapter() {

        override fun getView(position: Int, convertView: View?, parent: ViewGroup?): View {
            val view: View = LayoutInflater.from(context).inflate(R.layout.zzim_list_item, null)

            val Photo = view.findViewById<ImageView>(R.id.zzim_item_Img)
            val name = view.findViewById<TextView>(R.id.zzim_item_name)
            val sub1 = view.findViewById<TextView>(R.id.zzim_sub1_text)
            val sub2 = view.findViewById<TextView>(R.id.zzim_sub2_text)

            val product = ProductList[position]
            var url = "http:" + product.photo

            Glide.with(this.context).load(url)
                .into(Photo) //이미지 url로 사진 불러오기

            name.text = product.name
            sub1.text = "제품 확인하기 >"
            sub2.text = ""

            return view
        }

        override fun getCount(): Int {
            return ProductList.size
        }

        override fun getItem(position: Int): Any {
            return ProductList[position]
        }

        override fun getItemId(p0: Int): Long {
            return 0
        }


    }
}
