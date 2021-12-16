package com.example.musicplayer

import android.app.AlertDialog
import android.content.ContentResolver
import android.content.Context
import android.content.DialogInterface
import android.os.Bundle
import android.provider.MediaStore
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.AdapterView
import android.widget.AdapterView.OnItemClickListener
import android.widget.AdapterView.OnItemLongClickListener
import android.widget.ListView
import androidx.fragment.app.Fragment
import androidx.fragment.app.ListFragment
import java.util.ArrayList


class AllSongFragment : ListFragment() {
    var songsList: ArrayList<SongsList>? = null
    var newList: ArrayList<SongsList>? = null
    private var listView: ListView? = null
    private var createDataParse: createDataParse? = null
    private var contentResolver: ContentResolver? = null
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
    }

    override fun onAttach(context: Context) {
        super.onAttach(context)
        createDataParse = context as createDataParse
    }

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        return inflater.inflate(R.layout.fragment_tab, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        listView = view.findViewById(R.id.list_playlist)
        contentResolver = contentResolver1
        setContent()
    }

    /**
     * Setting the content in the listView and sending the data to the Activity
     */
    fun setContent() {
        var searchedList = false
        songsList = ArrayList<SongsList>()
        newList = ArrayList<SongsList>()
        music
        var adapter = SongAdapter(context, songsList)
        if (createDataParse!!.queryText() != "") {
            adapter = onQueryTextChange()
            adapter.notifyDataSetChanged()
            searchedList = true
        } else {
            searchedList = false
        }
        createDataParse!!.getLength(songsList!!.size)
        listView!!.adapter = adapter
        val finalSearchedList = searchedList
        listView!!.onItemClickListener =
            OnItemClickListener { parent, view, position, id -> // Toast.makeText(getContext(), "You clicked :\n" + songsList.get(position), Toast.LENGTH_SHORT).show();
                if (!finalSearchedList) {
                    createDataParse!!.onDataPass(
                        songsList!![position].getTitle(),
                        songsList!![position].getPath()
                    )
                    createDataParse!!.fullSongList(songsList, position)
                } else {
                    createDataParse!!.onDataPass(
                        newList!![position].getTitle(),
                        newList!![position].getPath()
                    )
                    createDataParse!!.fullSongList(songsList, position)
                }
            }
        listView!!.onItemLongClickListener =
            OnItemLongClickListener { parent, view, position, id ->
                showDialog(position)
                true
            }
    }

    val music: Unit
        get() {
            val songUri = MediaStore.Audio.Media.EXTERNAL_CONTENT_URI
            val songCursor = contentResolver!!.query(songUri, null, null, null, null)
            if (songCursor != null && songCursor.moveToFirst()) {
                val songTitle = songCursor.getColumnIndex(MediaStore.Audio.Media.TITLE)
                val songArtist = songCursor.getColumnIndex(MediaStore.Audio.Media.ARTIST)
                val songPath = songCursor.getColumnIndex(MediaStore.Audio.Media.DATA)
                do {
                    songsList!!.add(
                        SongsList(
                            songCursor.getString(songTitle),
                            songCursor.getString(songArtist),
                            songCursor.getString(songPath)
                        )
                    )
                } while (songCursor.moveToNext())
                songCursor.close()
            }
        }

    fun onQueryTextChange(): SongAdapter {
        val text = createDataParse!!.queryText()
        for (songs in songsList) {
            val title: String = songs.getTitle().toLowerCase()
            if (title.contains(text)) {
                newList!!.add(songs)
            }
        }
        return SongAdapter(context, newList)
    }

    private fun showDialog(position: Int) {
        val builder = AlertDialog.Builder(context)
        builder.setMessage(getString(R.string.play_next))
            .setCancelable(true)
            .setNegativeButton(
                R.string.no
            ) { dialog, which -> }
            .setPositiveButton(
                R.string.yes
            ) { dialog, which ->
                createDataParse!!.currentSong(songsList!![position])
                setContent()
            }
        val alertDialog = builder.create()
        alertDialog.show()
    }

    interface createDataParse {
        fun onDataPass(name: String?, path: String?)
        fun fullSongList(songList: ArrayList<SongsList>?, position: Int)
        fun queryText(): String
        fun currentSong(songsList: SongsList?)
        fun getLength(length: Int)
    }

    companion object {
        private val contentResolver1: ContentResolver? = null
        fun getInstance(position: Int, mcontentResolver: ContentResolver?): Fragment {
            val bundle = Bundle()
            bundle.putInt("pos", position)
            val tabFragment = AllSongFragment()
            tabFragment.arguments = bundle
            contentResolver1 = mcontentResolver
            return tabFragment
        }
    }
}
