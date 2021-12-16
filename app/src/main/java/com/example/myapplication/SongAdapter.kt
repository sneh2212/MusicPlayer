package com.example.musicplayer

import android.content.Context
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ArrayAdapter
import android.widget.Filterable
import android.widget.TextView
import java.util.ArrayList

class SongAdapter(private val mContext: Context, songList: ArrayList<SongsList>) :
    ArrayAdapter<SongsList?>(mContext, 0, songList), Filterable {
    private val songList: ArrayList<SongsList> = ArrayList<SongsList>()
    override fun getView(position: Int, convertView: View?, parent: ViewGroup): View {
        var listItem = convertView
        if (listItem == null) {
            listItem = LayoutInflater.from(mContext).inflate(R.layout.playlist_items, parent, false)
        }
        val currentSong: SongsList = songList[position]
        val tvTitle = listItem!!.findViewById<TextView>(R.id.tv_music_name)
        val tvSubtitle = listItem.findViewById<TextView>(R.id.tv_music_subtitle)
        tvTitle.setText(currentSong.getTitle())
        tvSubtitle.setText(currentSong.getSubTitle())
        return listItem
    }

    init {
        this.songList = songList
    }
}