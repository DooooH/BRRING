package com.cookandroid.lowest_price_alert.board

import android.content.Context
import android.content.Intent
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.BaseAdapter
import android.widget.ImageView
import android.widget.TextView
import android.widget.Toast
import androidx.core.content.ContextCompat.startActivity
import com.cookandroid.lowest_price_alert.MainActivity
import com.cookandroid.lowest_price_alert.R

class PostListAdapter (val context: Context, val postList: ArrayList<Post>) : BaseAdapter() {
    override fun getCount(): Int {
        return postList.size
    }

    override fun getItem(p0: Int): Any {
        return postList[p0]
    }

    override fun getItemId(p0: Int): Long {
        return 0
    }

    override fun getView(p0: Int, p1: View?, p2: ViewGroup?): View {/* LayoutInflater는 item을 Adapter에서 사용할 View로 부풀려주는(inflate) 역할을 한다. */
        val view: View = LayoutInflater.from(context).inflate(R.layout.board_lv_post_item, null)

        /* 위에서 생성된 view를 res-layout-main_lv_item.xml 파일의 각 View와 연결하는 과정이다. */
        val itemPhoto = view.findViewById<ImageView>(R.id.itemPhoto)
        val title = view.findViewById<TextView>(R.id.title)
        val itemName = view.findViewById<TextView>(R.id.itemName)
        val option = view.findViewById<TextView>(R.id.option)
        val price = view.findViewById<TextView>(R.id.price)
        val comment = view.findViewById<TextView>(R.id.comment)
        val postId = view.findViewById<TextView>(R.id.postId)
        val boardId = view.findViewById<TextView>(R.id.boardId)


        /* ArrayList<Dog>의 변수 dog의 이미지와 데이터를 ImageView와 TextView에 담는다. */
        val post = postList[p0]
        val resourceId = context.resources.getIdentifier(post.photo, "drawable", context.packageName)
        itemPhoto.setImageResource(resourceId)
        title.text = post.title
        itemName.text = post.itemName
        option.text = post.option
        price.text = post.price
        comment.text = post.comment
        postId.text = post.postId
        boardId.text = post.boardId

        view.setOnClickListener {
            //Toast.makeText(this.context, title.text, Toast.LENGTH_SHORT).show()
            val intent = Intent(this.context, PostContentActivity::class.java)
            intent.putExtra("boardId", boardId.text)
            intent.putExtra("postId", postId.text)
            startActivity(this.context, intent, null)
        }

        return view
    }


}