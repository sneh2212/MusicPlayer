package com.example.musicplayer

import android.content.ContentResolver
import androidx.fragment.app.Fragment
import androidx.fragment.app.FragmentManager
import androidx.fragment.app.FragmentPagerAdapter

class ViewPagerAdapter(fm: FragmentManager?, private val contentResolver: ContentResolver) :
    FragmentPagerAdapter(fm!!) {
    private val title = arrayOf("All SONGS", "CURRENT PLAYLIST", "FAVORITES")
    override fun getItem(position: Int): Fragment? {
        return when (position) {
            0 -> AllSongFragment.getInstance(
                position,
                contentResolver
            )
            1 -> CurrentSongFragment.getInstance(position)
            else -> null
        }
    }

    override fun getCount(): Int {
        return title.size
    }

    override fun getPageTitle(position: Int): CharSequence? {
        return title[position]
    }
}