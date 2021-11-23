package com.cookandroid.lowest_price_alert.board

import android.content.Context
import android.content.Intent
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.BaseAdapter
import android.widget.TextView
import android.widget.Toast
import androidx.core.content.ContextCompat.startActivity
import com.cookandroid.lowest_price_alert.MainActivity
import com.cookandroid.lowest_price_alert.R

class CommentListAdapter (val context: Context, val commentList: ArrayList<Comment>) : BaseAdapter() {
    override fun getCount(): Int {
        return commentList.size
    }

    override fun getItem(p0: Int): Any {
        return commentList[p0]
    }

    override fun getItemId(p0: Int): Long {
        return 0
    }

    override fun getView(p0: Int, p1: View?, p2: ViewGroup?): View {/* LayoutInflater는 item을 Adapter에서 사용할 View로 부풀려주는(inflate) 역할을 한다. */
        val view: View = LayoutInflater.from(context).inflate(R.layout.board_lv_comment_item, null)

        /* 위에서 생성된 view를 res-layout-main_lv_item.xml 파일의 각 View와 연결하는 과정이다. */
        val commentId = view.findViewById<TextView>(R.id.commentId)
        val uid = view.findViewById<TextView>(R.id.uid)
        val username= view.findViewById<TextView>(R.id.username)
        val commentContent = view.findViewById<TextView>(R.id.commentContent)

        /* ArrayList<Dog>의 변수 dog의 이미지와 데이터를 ImageView와 TextView에 담는다. */
        val comment = commentList[p0]
        commentId.text = comment.commentId
        uid.text = comment.uid
        username.text = comment.username
        commentContent.text = comment.commentContent

        view.setOnClickListener {
            /*
            Toast.makeText(this.context, boardId.text, Toast.LENGTH_SHORT).show()
            val intent = Intent(this.context, PostActivity::class.java)
            intent.putExtra("boardId", boardId.text)
            startActivity(this.context, intent, null)
             */
        }

        return view
    }


}