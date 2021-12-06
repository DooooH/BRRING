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
import com.bumptech.glide.Glide
import com.cookandroid.lowest_price_alert.MainActivity
import com.cookandroid.lowest_price_alert.R
import com.google.firebase.ktx.Firebase
import com.google.firebase.storage.StorageReference
import com.google.firebase.storage.ktx.storage

val storage = Firebase.storage // firebase cloud storage for profile image

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
        val profile_image= view.findViewById<ImageView>(R.id.profile_image)
        val commentContent = view.findViewById<TextView>(R.id.commentContent)

        /* ArrayList<Dog>의 변수 dog의 이미지와 데이터를 ImageView와 TextView에 담는다. */
        val comment = commentList[p0]

        // set profile image from firebase cloud storage
        var storageRef = storage.reference
        var imagesRef : StorageReference? = storageRef.child(comment.profile_image)
        imagesRef?.downloadUrl?.addOnSuccessListener { uri ->
            Glide.with(context)
                .load("http:"+uri)
                .placeholder(R.drawable.no_profile)
                .error(R.drawable.no_profile)
                .into(profile_image) //이미지 url로 사진 불러오기
            commentId.text = comment.commentId
        }

        uid.text = comment.uid
        username.text = comment.username
        commentContent.text = comment.commentContent


        return view
    }


}