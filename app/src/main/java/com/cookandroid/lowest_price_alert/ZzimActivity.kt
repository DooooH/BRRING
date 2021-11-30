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

class ZzimActivity: AppCompatActivity()  {
    public override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.zzim_activity)

        val now_user = "nhUKsEBop5beTg2c4jT4vZtYj842"  // 현재 유저 번호 (현재는 하드코딩)
        val firestoredb = FirebaseFirestore.getInstance() // firestore db

        firestoredb.collection("user").document(now_user).get().addOnSuccessListener { result ->
            val wish_list = result["wish_list"] as ArrayList<String>
            firestoredb.collection("product_list").get()
                .addOnSuccessListener { product ->
                    var productList = arrayListOf<Product>()


                    for(document in product){
                        val item = Product(document["name"] as String, document["image_url"] as String, "sub1", "sub2", document["no"] as String, document.id as String)
                        val p_no = document["no"].toString()
                        val test = findViewById<TextView>(R.id.test)
                        test.text = document.id.toString()

                            for (i: Int in 0..wish_list.size ) {
                            if(wish_list[i].equals(p_no)){
                                productList.add(item)
                                break
                            }
                        }
                    }



                    var zzim_list_view = findViewById<ListView>(R.id.zzim_list_view)
                    val zzimAdapter = MainListAdapter(this, productList)
                    zzim_list_view.adapter = zzimAdapter

                    zzim_list_view.setOnItemClickListener{ parent, view, position, id ->

                        val number = productList.get(position).no
                        val code = productList.get(position).product_code
                        val intent = Intent(this, ChartActivity::class.java)
                        intent.putExtra("product_code", code.toString())
                        intent.putExtra("product_no",number.toString())
                        startActivity(intent)
                    }
                }

        }

    }
    class Product (
        val name: String,
        val photo: String,
        val sub1 : String,
        val sub2 : String,
        val no : String,
        val product_code : String
        )

    class MainListAdapter (val context: Context, val ProductList: ArrayList<Product>) : BaseAdapter() {

        override fun getView(position: Int, convertView: View?, parent: ViewGroup?): View {
            val view: View = LayoutInflater.from(context).inflate(R.layout.zzim_list_item, null)

            val Photo = view.findViewById<ImageView>(R.id.zzim_item_Img)
            val name = view.findViewById<TextView>(R.id.zzim_item_name)
            val sub1 = view.findViewById<TextView>(R.id.zzim_sub1_text)
            val sub2 = view.findViewById<TextView>(R.id.zzim_sub2_text)

            val product = ProductList[position]
            val resourceId = context.resources.getIdentifier(product.photo, "drawable", context.packageName)


            var url = "http:" + product.photo
            Glide.with(this.context).load(url)
                .into(Photo) //이미지 url로 사진 불러오기
            Photo.setImageResource(resourceId)
            name.text = product.name
            sub1.text = product.sub1
            sub2.text = product.sub2

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
