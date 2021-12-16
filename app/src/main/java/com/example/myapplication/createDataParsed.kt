package com.example.musicplayer

import android.content.Context
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.AdapterView
import android.widget.AdapterView.OnItemClickListener
import android.widget.AdapterView.OnItemLongClickListener
import android.widget.ListView
import androidx.fragment.app.ListFragment
import java.util.ArrayList


class CurrentSongFragment : ListFragment() {
    var songsList: ArrayList<SongsList?> = ArrayList<SongsList?>()
    private var listView: ListView? = null
    private var createDataParsed: createDataParsed? = null
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
    }

    override fun onAttach(context: Context) {
        super.onAttach(context)
        createDataParsed = context as createDataParsed
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
        //songsList = new ArrayList<>();
        setContent()
    }

    /**
     * Setting the content in the listView and sending the data to the Activity
     */
    fun setContent() {
        if (createDataParsed!!.song != null) songsList.add(createDataParsed!!.song)
        val adapter = SongAdapter(context, songsList)
        if (songsList.size > 1) if (createDataParsed!!.playlistFlag) {
            songsList.clear()
        }
        listView!!.adapter = adapter
        adapter.notifyDataSetChanged()
        listView!!.onItemClickListener =
            OnItemClickListener { parent, view, position, id -> // Toast.makeText(getContext(), "You clicked :\n" + songsList.get(position), Toast.LENGTH_SHORT).show();
                createDataParsed!!.onDataPass(
                    songsList[position].getTitle(),
                    songsList[position].getPath()
                )
                createDataParsed!!.fullSongList(songsList, position)
            }
        listView!!.onItemLongClickListener =
            OnItemLongClickListener { parent, view, position, id -> true }
    }

    interface createDataParsed {
        fun onDataPass(name: String?, path: String?)
        fun fullSongList(songList: ArrayList<SongsList?>?, position: Int)
        val song: SongsList?
        val playlistFlag: Boolean
    }

    companion object {
        fun getInstance(position: Int): CurrentSongFragment {
            val bundle = Bundle()
            bundle.putInt("pos", position)
            val tabFragment = CurrentSongFragment()
            tabFragment.arguments = bundle
            return tabFragment
        }
    }
}
