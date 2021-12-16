package com.example.musicplayer

import android.Manifest
import android.annotation.SuppressLint
import android.app.AlertDialog
import android.app.SearchManager
import android.content.DialogInterface
import android.content.pm.PackageManager
import android.media.MediaPlayer
import android.media.MediaPlayer.OnCompletionListener
import android.os.Bundle
import android.os.Handler
import android.view.Gravity
import android.view.Menu
import android.view.MenuItem
import android.view.View
import android.widget.*
import android.widget.SeekBar.OnSeekBarChangeListener
import androidx.appcompat.app.AppCompatActivity
import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat
import androidx.drawerlayout.widget.DrawerLayout
import androidx.viewpager.widget.ViewPager
import androidx.viewpager.widget.ViewPager.OnPageChangeListener
import com.google.android.material.floatingactionbutton.FloatingActionButton
import com.google.android.material.navigation.NavigationView
import com.google.android.material.snackbar.Snackbar
import com.google.android.material.tabs.TabLayout
import com.google.android.material.tabs.TabLayout.OnTabSelectedListener
import java.lang.Exception
import java.util.ArrayList


class MainActivity : AppCompatActivity(), View.OnClickListener, AllSongFragment.createDataParse,
    FavSongFragment.createDataParsed, CurrentSongFragment.createDataParsed {
    private var menu: Menu? = null
    private var imgBtnPlayPause: ImageButton? = null
    private var imgbtnReplay: ImageButton? = null
    private var imgBtnPrev: ImageButton? = null
    private var imgBtnNext: ImageButton? = null
    private var imgBtnSetting: ImageButton? = null
    private var tabLayout: TabLayout? = null
    private var viewPager: ViewPager? = null
    private var seekbarController: SeekBar? = null
    private var mDrawerLayout: DrawerLayout? = null
    private var tvCurrentTime: TextView? = null
    private var tvTotalTime: TextView? = null
    private var songList: ArrayList<SongsList>? = null
    var position = 0
        private set
    private var searchText = ""
    private var currSong: SongsList? = null
    private var checkFlag = false
    private var repeatFlag = false
    private var playContinueFlag = false
    private var favFlag = true
    override var playlistFlag = false
        private set
    private val MY_PERMISSION_REQUEST = 100
    private var allSongLength = 0
    var mediaPlayer: MediaPlayer? = null
    var handler: Handler? = null
    var runnable: Runnable? = null
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        NavigationView(R.layout.activity_main)
        init()
        grantedPermission()
    }

    /**
     * Initialising the views
     */
    private fun init() {
        imgBtnPrev = findViewById(R.id.img_btn_previous)
        imgBtnNext = findViewById(R.id.img_btn_next)
        imgbtnReplay = findViewById(R.id.img_btn_replay)
        imgBtnSetting = findViewById(R.id.img_btn_setting)
        tvCurrentTime = findViewById(R.id.tv_current_time)
        tvTotalTime = findViewById(R.id.tv_total_time)
        val refreshSongs = findViewById<FloatingActionButton>(R.id.btn_refresh)
        seekbarController = findViewById(R.id.seekbar_controller)
        viewPager = findViewById(R.id.songs_viewpager)
        val navigationView = findViewById<NavigationView>(R.id.nav_view)
        mDrawerLayout = findViewById(R.id.drawer_layout)
        imgBtnPlayPause = findViewById(R.id.img_btn_play)
        val toolbar = findViewById<Toolbar>(R.id.toolbar)
        handler = Handler()
        mediaPlayer = MediaPlayer()
        toolbar.setTitleTextColor(resources.getColor(R.color.text_color))
        setSupportActionBar(toolbar)
        val actionBar = supportActionBar!!
        actionBar.setDisplayHomeAsUpEnabled(true)
        actionBar.setHomeAsUpIndicator(R.drawable.menu_icon)
        imgBtnNext.setOnClickListener(this)
        imgBtnPrev.setOnClickListener(this)
        imgbtnReplay.setOnClickListener(this)
        refreshSongs.setOnClickListener(this)
        imgBtnPlayPause.setOnClickListener(this)
        imgBtnSetting.setOnClickListener(this)
        navigationView.setNavigationItemSelectedListener { item ->
            item.isChecked = true
            mDrawerLayout.closeDrawers()
            when (item.itemId) {
                R.id.nav_about -> about()
            }
            true
        }
    }

    private fun setSupportActionBar(toolbar: Toolbar) {}

    /**
     * Function to ask user to grant the permission.
     */
    private fun grantedPermission() {
        if (ContextCompat.checkSelfPermission(
                this@MainActivity,
                Manifest.permission.READ_EXTERNAL_STORAGE
            ) != PackageManager.PERMISSION_GRANTED
        ) {
            ActivityCompat.requestPermissions(
                this@MainActivity,
                arrayOf(Manifest.permission.READ_EXTERNAL_STORAGE),
                MY_PERMISSION_REQUEST
            )
            if (ActivityCompat.shouldShowRequestPermissionRationale(
                    this@MainActivity,
                    Manifest.permission.READ_EXTERNAL_STORAGE
                )
            ) {
                ActivityCompat.requestPermissions(
                    this@MainActivity,
                    arrayOf(Manifest.permission.READ_EXTERNAL_STORAGE),
                    MY_PERMISSION_REQUEST
                )
            } else {
                if (ContextCompat.checkSelfPermission(
                        this@MainActivity,
                        Manifest.permission.READ_EXTERNAL_STORAGE
                    ) != PackageManager.PERMISSION_GRANTED
                ) {
                    val snackbar = Snackbar.make(
                        mDrawerLayout!!,
                        "Provide the Storage Permission",
                        Snackbar.LENGTH_LONG
                    )
                    snackbar.show()
                }
            }
        } else {
            setPagerLayout()
        }
    }

    /**
     * Checking if the permission is granted or not
     *
     * @param requestCode
     * @param permissions
     * @param grantResults
     */
    override fun onRequestPermissionsResult(
        requestCode: Int,
        permissions: Array<String>,
        grantResults: IntArray
    ) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults)
        when (requestCode) {
            MY_PERMISSION_REQUEST -> if (grantResults.size > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                if (ContextCompat.checkSelfPermission(
                        this@MainActivity,
                        Manifest.permission.READ_EXTERNAL_STORAGE
                    ) == PackageManager.PERMISSION_GRANTED
                ) {
                    Toast.makeText(this, "Permission Granted!", Toast.LENGTH_SHORT).show()
                    setPagerLayout()
                } else {
                    val snackbar = Snackbar.make(
                        mDrawerLayout!!,
                        "Provide the Storage Permission",
                        Snackbar.LENGTH_LONG
                    )
                    snackbar.show()
                    finish()
                }
            }
        }
    }

    /**
     * Setting up the tab layout with the viewpager in it.
     */
    private fun setPagerLayout() {
        val adapter = ViewPagerAdapter(supportFragmentManager, contentResolver)
        viewPager!!.adapter = adapter
        viewPager!!.addOnPageChangeListener(object : OnPageChangeListener {
            override fun onPageScrolled(
                position: Int,
                positionOffset: Float,
                positionOffsetPixels: Int
            ) {
            }

            override fun onPageSelected(position: Int) {}
            override fun onPageScrollStateChanged(state: Int) {}
        })
        tabLayout = findViewById(R.id.tabs)
        tabLayout.setTabGravity(TabLayout.GRAVITY_FILL)
        tabLayout.setupWithViewPager(viewPager)
        tabLayout.addOnTabSelectedListener(object : OnTabSelectedListener {
            override fun onTabSelected(tab: TabLayout.Tab) {
                viewPager!!.currentItem = tab.position
            }

            override fun onTabUnselected(tab: TabLayout.Tab) {}
            override fun onTabReselected(tab: TabLayout.Tab) {}
        })
    }

    /**
     * Function to show the dialog for about us.
     */
    private fun about() {
        val builder = AlertDialog.Builder(this)
        builder.setTitle(getString(R.string.about))
            .setMessage(getString(R.string.about_text))
            .setPositiveButton(
                R.string.ok
            ) { dialog, which -> }
        val alertDialog = builder.create()
        alertDialog.show()
    }

    override fun onCreateOptionsMenu(menu: Menu): Boolean {
        this.menu = menu
        menuInflater.inflate(R.menu.action_bar_menu, menu)
        val manager = getSystemService(SEARCH_SERVICE) as SearchManager
        val searchView = menu.findItem(R.id.menu_search).actionView as SearchView
        searchView.setSearchableInfo(manager.getSearchableInfo(componentName))
        searchView.setOnQueryTextListener(object : SearchView.OnQueryTextListener {
            override fun onQueryTextSubmit(query: String): Boolean {
                return false
            }

            override fun onQueryTextChange(newText: String): Boolean {
                searchText = newText
                queryText()
                setPagerLayout()
                return true
            }
        })
        return super.onCreateOptionsMenu(menu)
    }

    @SuppressLint("WrongConstant")
    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        when (item.itemId) {
            android.R.id.home -> {
                mDrawerLayout!!.openDrawer(Gravity.START)
                return true
            }
            R.id.menu_search -> {
                Toast.makeText(this, "Search", Toast.LENGTH_SHORT).show()
                return true
            }
            R.id.menu_favorites -> {
                if (checkFlag) if (mediaPlayer != null) {
                    favFlag = if (favFlag) {
                        Toast.makeText(this, "Added to Favorites", Toast.LENGTH_SHORT).show()
                        item.setIcon(R.drawable.ic_favorite_filled)
                        val favList = SongsList(
                            songList!![position].getTitle(),
                            songList!![position].getSubTitle(), songList!![position].getPath()
                        )
                        val favoritesOperations = FavoritesOperations(this)
                        favoritesOperations.addSongFav(favList)
                        setPagerLayout()
                        false
                    } else {
                        item.setIcon(R.drawable.favorite_icon)
                        true
                    }
                }
                return true
            }
        }
        return super.onOptionsItemSelected(item)
    }

    /**
     * Function to handle the click events.
     *
     * @param v
     */
    override fun onClick(v: View) {
        when (v.id) {
            R.id.img_btn_play -> if (checkFlag) {
                if (mediaPlayer!!.isPlaying) {
                    mediaPlayer!!.pause()
                    imgBtnPlayPause!!.setImageResource(R.drawable.play_icon)
                } else if (!mediaPlayer!!.isPlaying) {
                    mediaPlayer!!.start()
                    imgBtnPlayPause!!.setImageResource(R.drawable.pause_icon)
                    playCycle()
                }
            } else {
                Toast.makeText(this, "Select the Song ..", Toast.LENGTH_SHORT).show()
            }
            R.id.btn_refresh -> {
                Toast.makeText(this, "Refreshing", Toast.LENGTH_SHORT).show()
                setPagerLayout()
            }
            R.id.img_btn_replay -> if (repeatFlag) {
                Toast.makeText(this, "Replaying Removed..", Toast.LENGTH_SHORT).show()
                mediaPlayer!!.isLooping = false
                repeatFlag = false
            } else {
                Toast.makeText(this, "Replaying Added..", Toast.LENGTH_SHORT).show()
                mediaPlayer!!.isLooping = true
                repeatFlag = true
            }
            R.id.img_btn_previous -> if (checkFlag) {
                if (mediaPlayer!!.currentPosition > 10) {
                    if (position - 1 > -1) {
                        attachMusic(
                            songList!![position - 1].getTitle(),
                            songList!![position - 1].getPath()
                        )
                        position = position - 1
                    } else {
                        attachMusic(songList!![position].getTitle(), songList!![position].getPath())
                    }
                } else {
                    attachMusic(songList!![position].getTitle(), songList!![position].getPath())
                }
            } else {
                Toast.makeText(this, "Select a Song . .", Toast.LENGTH_SHORT).show()
            }
            R.id.img_btn_next -> if (checkFlag) {
                if (position + 1 < songList!!.size) {
                    attachMusic(
                        songList!![position + 1].getTitle(),
                        songList!![position + 1].getPath()
                    )
                    position += 1
                } else {
                    Toast.makeText(this, "Playlist Ended", Toast.LENGTH_SHORT).show()
                }
            } else {
                Toast.makeText(this, "Select the Song ..", Toast.LENGTH_SHORT).show()
            }
            R.id.img_btn_setting -> if (!playContinueFlag) {
                playContinueFlag = true
                Toast.makeText(this, "Loop Added", Toast.LENGTH_SHORT).show()
            } else {
                playContinueFlag = false
                Toast.makeText(this, "Loop Removed", Toast.LENGTH_SHORT).show()
            }
        }
    }

    /**
     * Function to attach the song to the music player
     *
     * @param name
     * @param path
     */
    private fun attachMusic(name: String?, path: String?) {
        imgBtnPlayPause!!.setImageResource(R.drawable.play_icon)
        title = name
        menu!!.getItem(1).setIcon(R.drawable.favorite_icon)
        favFlag = true
        try {
            mediaPlayer!!.reset()
            mediaPlayer!!.setDataSource(path)
            mediaPlayer!!.prepare()
            setControls()
        } catch (e: Exception) {
            e.printStackTrace()
        }
        mediaPlayer!!.setOnCompletionListener(object : OnCompletionListener {
            override fun onCompletion(mp: MediaPlayer) {
                imgBtnPlayPause!!.setImageResource(R.drawable.play_icon)
                if (playContinueFlag) {
                    if (position + 1 < songList!!.size) {
                        attachMusic(
                            songList!![position + 1].getTitle(),
                            songList!![position + 1].getPath()
                        )
                        position += 1
                    } else {
                        Toast.makeText(this@MainActivity, "PlayList Ended", Toast.LENGTH_SHORT)
                            .show()
                    }
                }
            }
        })
    }

    /**
     * Function to set the controls according to the song
     */
    private fun setControls() {
        seekbarController!!.max = mediaPlayer!!.duration
        mediaPlayer!!.start()
        playCycle()
        checkFlag = true
        if (mediaPlayer!!.isPlaying) {
            imgBtnPlayPause!!.setImageResource(R.drawable.pause_icon)
            tvTotalTime!!.text = getTimeFormatted(mediaPlayer!!.duration.toLong())
        }
        seekbarController!!.setOnSeekBarChangeListener(object : OnSeekBarChangeListener {
            override fun onProgressChanged(seekBar: SeekBar, progress: Int, fromUser: Boolean) {
                if (fromUser) {
                    mediaPlayer!!.seekTo(progress)
                    tvCurrentTime!!.text = getTimeFormatted(progress.toLong())
                }
            }

            override fun onStartTrackingTouch(seekBar: SeekBar) {}
            override fun onStopTrackingTouch(seekBar: SeekBar) {}
        })
    }

    /**
     * Function to play the song using a thread
     */
    private fun playCycle() {
        try {
            seekbarController!!.progress = mediaPlayer!!.currentPosition
            tvCurrentTime!!.text = getTimeFormatted(mediaPlayer!!.currentPosition.toLong())
            if (mediaPlayer!!.isPlaying) {
                runnable = Runnable { playCycle() }
                handler!!.postDelayed(runnable, 100)
            }
        } catch (e: Exception) {
            e.printStackTrace()
        }
    }

    private fun getTimeFormatted(milliSeconds: Long): String {
        var finalTimerString = ""
        val secondsString: String

        //Converting total duration into time
        val hours = (milliSeconds / 3600000).toInt()
        val minutes = (milliSeconds % 3600000).toInt() / 60000
        val seconds = (milliSeconds % 3600000 % 60000 / 1000).toInt()

        // Adding hours if any
        if (hours > 0) finalTimerString = "$hours:"

        // Prepending 0 to seconds if it is one digit
        secondsString = if (seconds < 10) "0$seconds" else "" + seconds
        finalTimerString = "$finalTimerString$minutes:$secondsString"

        // Return timer String;
        return finalTimerString
    }

    /**
     * Function Overrided to receive the data from the fragment
     *
     * @param name
     * @param path
     */
    override fun onDataPass(name: String?, path: String?) {
        Toast.makeText(this, name, Toast.LENGTH_LONG).show()
        attachMusic(name, path)
    }

    override fun getLength(length: Int) {
        allSongLength = length
    }

    fun fullSongList(songList: ArrayList<SongsList>, position: Int) {
        this.songList = songList
        this.position = position
        playlistFlag = songList.size == allSongLength
        playContinueFlag = !playlistFlag
    }

    override fun queryText(): String {
        return searchText.toLowerCase()
    }

    override val song: SongsList?
        get() {
            position = -1
            return currSong
        }

    override fun currentSong(songsList: SongsList?) {
        currSong = songsList
    }

    override fun onResume() {
        super.onResume()
    }

    override fun onPause() {
        super.onPause()
    }

    override fun onDestroy() {
        super.onDestroy()
        mediaPlayer!!.release()
        handler!!.removeCallbacks(runnable!!)
    }
}
