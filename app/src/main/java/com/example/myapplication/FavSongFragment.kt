package com.example.musicplayer

import android.app.AlertDialog
import android.app.ListFragment
import android.content.Context
import android.content.DialogInterface
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.AdapterView
import android.widget.AdapterView.OnItemClickListener
import android.widget.AdapterView.OnItemLongClickListener
import android.widget.ListView
import android.widget.Toast
import java.util.ArrayList


class FavSongFragment : ListFragment() {
    private var favoritesOperations: FavoritesOperations? = null
    var songsList: ArrayList<SongsList>? = null
    var newList: ArrayList<SongsList>? = null
    private var listView: ListView? = null
    private var createDataParsed: createDataParsed? = null
    override fun setArguments(bundle: Bundle) {}
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
    }

    override fun onAttach(context: Context) {
        super.onAttach(context)
        createDataParsed = context as createDataParsed
        favoritesOperations = FavoritesOperations(context)
    }

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle
    ): View? {
        return inflater.inflate(R.layout.fragment_tab, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        listView = view.findViewById(R.id.list_playlist)
        setContent()
    }

    /**
     * Setting the content in the listView and sending the data to the Activity
     */
    fun setContent() {
        var searchedList = false
        songsList = ArrayList<SongsList>()
        newList = ArrayList<SongsList>()
        songsList = favoritesOperations!!.allFavorites
        var adapter = SongAdapter(context, songsList)
        if (createDataParsed!!.queryText() != "") {
            adapter = onQueryTextChange()
            adapter.notifyDataSetChanged()
            searchedList = true
        } else {
            searchedList = false
        }
        listView!!.adapter = adapter
        val finalSearchedList = searchedList
        listView!!.onItemClickListener =
            OnItemClickListener { parent, view, position, id -> // Toast.makeText(getContext(), "You clicked :\n" + songsList.get(position), Toast.LENGTH_SHORT).show();
                if (!finalSearchedList) {
                    createDataParsed!!.onDataPass(
                        songsList!![position].getTitle(),
                        songsList!![position].getPath()
                    )
                    createDataParsed!!.fullSongList(songsList, position)
                } else {
                    createDataParsed!!.onDataPass(
                        newList!![position].getTitle(),
                        newList!![position].getPath()
                    )
                    createDataParsed!!.fullSongList(songsList, position)
                }
            }
        listView!!.onItemLongClickListener =
            OnItemLongClickListener { parent, view, position, id ->
                deleteOption(position)
                true
            }
    }

    private fun deleteOption(position: Int) {
        if (position != createDataParsed!!.position) showDialog(
            songsList!![position].getPath(),
            position
        ) else Toast.makeText(
            context, "You Can't delete the Current Song", Toast.LENGTH_SHORT
        ).show()
    }

    interface createDataParsed {
        fun onDataPass(name: String?, path: String?)
        fun fullSongList(songList: ArrayList<SongsList>?, position: Int)
        val position: Int

        fun queryText(): String
    }

    fun onQueryTextChange(): SongAdapter {
        val text = createDataParsed!!.queryText()
        for (songs in songsList) {
            val title: String = songs.getTitle().toLowerCase()
            if (title.contains(text)) {
                newList!!.add(songs)
            }
        }
        return SongAdapter(context, newList)
    }

    private fun showDialog(index: String, position: Int) {
        val builder = AlertDialog.Builder(context)
        builder.setTitle(getString(R.string.delete))
            .setMessage(getString(R.string.delete_text))
            .setCancelable(true)
            .setNegativeButton(
                R.string.no
            ) { dialog, which -> }
            .setPositiveButton(
                R.string.yes
            ) { dialog, which ->
                favoritesOperations!!.removeSong(index)
                createDataParsed!!.fullSongList(songsList, position)
                setContent()
            }
        val alertDialog = builder.create()
        alertDialog.show()
    }

    companion object {
        fun getInstance(position: Int): ListFragment {
            val bundle = Bundle()
            bundle.putInt("pos", position)
            val tabFragment = FavSongFragment()
            tabFragment.setArguments(bundle)
            return tabFragment
        }
    }
}
