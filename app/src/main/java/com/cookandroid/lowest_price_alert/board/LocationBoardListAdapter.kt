package com.cookandroid.lowest_price_alert.board

import android.content.Context
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.BaseAdapter
import android.widget.TextView
import com.cookandroid.lowest_price_alert.R

class LocationBoardListAdapter (val context: Context, val locationBoardList: ArrayList<LocationBoard>) : BaseAdapter() {
    override fun getCount(): Int {
        return locationBoardList.size
    }

    override fun getItem(p0: Int): Any {
        return locationBoardList[p0]
    }

    override fun getItemId(p0: Int): Long {
        return 0
    }

    fun getItemBoardId(p0: Int): String {
        return locationBoardList[p0].boardId
    }

    override fun getView(p0: Int, p1: View?, p2: ViewGroup?): View {/* LayoutInflater는 item을 Adapter에서 사용할 View로 부풀려주는(inflate) 역할을 한다. */
        val view: View = LayoutInflater.from(context).inflate(R.layout.board_lv_item, null)

        /* 위에서 생성된 view를 res-layout-main_lv_item.xml 파일의 각 View와 연결하는 과정이다. */
        val boardTitle = view.findViewById<TextView>(R.id.boardTitle)
        val boardId = view.findViewById<TextView>(R.id.boardId)

        /* ArrayList<Dog>의 변수 dog의 이미지와 데이터를 ImageView와 TextView에 담는다. */
        val locationBoard = locationBoardList[p0]
        boardTitle.text = locationBoard.title
        boardId.text = locationBoard.boardId

        return view
    }


}