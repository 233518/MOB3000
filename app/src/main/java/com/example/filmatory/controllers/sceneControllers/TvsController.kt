package com.example.filmatory.controllers.sceneControllers

import androidx.recyclerview.widget.GridLayoutManager
import com.example.filmatory.R
import com.example.filmatory.api.data.tv.Tvs
import com.example.filmatory.controllers.MainController
import com.example.filmatory.guis.TvsGui
import com.example.filmatory.scenes.activities.TvsScene
import com.example.filmatory.systems.ApiSystem.RequestBaseOptions
import com.example.filmatory.utils.adapters.DataAdapter
import com.example.filmatory.utils.items.MediaModel
import com.google.android.material.dialog.MaterialAlertDialogBuilder

/**
 * TvsController manipulates the TvsScene gui
 *
 * @param tvsScene The TvsScene to use
 */
class TvsController(private val tvsScene: TvsScene) : MainController(tvsScene) {
    private val tvsGui = TvsGui(tvsScene, this)

    private val tvsPopularDesc : ArrayList<MediaModel> = ArrayList()
    private val tvsFilteredAZ : ArrayList<MediaModel> = ArrayList()
    private val tvsFilteredDateAsc : ArrayList<MediaModel> = ArrayList()

    private lateinit var tvsPopularAsc: ArrayList<MediaModel>
    private lateinit var tvsFilteredZA: ArrayList<MediaModel>
    private lateinit var tvsFilteredDateDesc: ArrayList<MediaModel>

    private var tvsFilteredGenre : ArrayList<MediaModel> = ArrayList()

    private var genreId : Int? = null

    init {
        apiSystem.requestTvs(RequestBaseOptions(null, null, ::tvsData, ::onFailure))
        apiSystem.requestTvsFilterTitleAZ(RequestBaseOptions(null, null, ::tvsDataFilterTitle, ::onFailure))
        apiSystem.requestTvsFilterDateDesc(RequestBaseOptions(null, null, ::tvsDataFilterDate, ::onFailure))
    }

    /**
     * Sets data from API to Arraylists and displays popular tvshows descending on activity start
     *
     * @param tvs The response from API
     */
    private fun tvsData(tvs: Tvs){
        tvs.forEach { item ->
            tvsPopularDesc.add(MediaModel(DataAdapter.TYPE_TV, item.title, item.releaseDate, "https://www.themoviedb.org/t/p/w600_and_h900_bestv2" + item.pictureUrl, item.id))
        }
        tvsPopularAsc = ArrayList(tvsPopularDesc)
        tvsPopularAsc.reverse()

        tvsScene.runOnUiThread {
            val tvsAdapter = DataAdapter(tvsScene, this, tvsScene, tvsPopularDesc)
            tvsGui.tvsRecyclerView.layoutManager = GridLayoutManager(tvsScene, 2)
            tvsGui.tvsRecyclerView.adapter = tvsAdapter
            tvsAdapter.notifyDataSetChanged()
        }
    }

    private fun tvsFilteredGenreData(tvs: Tvs){
        tvsFilteredGenre.clear()
        tvs.forEach { item ->
            if(item.genre.contains(genreId)){
                tvsFilteredGenre.add(MediaModel(DataAdapter.TYPE_TV, item.title, item.releaseDate, "https://www.themoviedb.org/t/p/w600_and_h900_bestv2" + item.pictureUrl, item.id))
            }
        }
        tvsScene.runOnUiThread {
            val tvsAdapter = DataAdapter(tvsScene, this, tvsScene, tvsFilteredGenre)
            tvsGui.tvsRecyclerView.layoutManager = GridLayoutManager(tvsScene, 2)
            tvsGui.tvsRecyclerView.adapter = tvsAdapter
            tvsAdapter.notifyDataSetChanged()
        }
    }

    /**
     * Sets data from API to Arraylists
     *
     * @param tvs The response from API
     */
    private fun tvsDataFilterTitle(tvs: Tvs){
        tvs.forEach{
                item -> tvsFilteredAZ.add(MediaModel(DataAdapter.TYPE_TV, item.title, item.releaseDate,"https://www.themoviedb.org/t/p/w600_and_h900_bestv2" + item.pictureUrl, item.id))
        }
        tvsFilteredZA = ArrayList(tvsFilteredAZ)
        tvsFilteredZA.reverse()
    }

    /**
     * Sets data from API to Arraylists
     *
     * @param tvs The response from API
     */
    private fun tvsDataFilterDate(tvs: Tvs){
        tvs.forEach{
                item -> tvsFilteredDateAsc.add(MediaModel(DataAdapter.TYPE_TV, item.title, item.releaseDate,"https://www.themoviedb.org/t/p/w600_and_h900_bestv2" + item.pictureUrl, item.id))
        }
        tvsFilteredDateDesc = ArrayList(tvsFilteredDateAsc)
        tvsFilteredDateDesc.reverse()
    }

    /**
     * Filtrerer tv-seriene i valgt rekkefølge fra brukeren og oppdaterer adapteren
     *
     * @param arrayList : Dataen som skal vises
     */
    private fun tvsFilter(arrayList : ArrayList<MediaModel>){
        val tvsAdapter = DataAdapter(tvsScene, this, tvsScene, arrayList)
        tvsScene.runOnUiThread {
            tvsGui.tvsRecyclerView.layoutManager = GridLayoutManager(tvsScene, 2)
            tvsGui.tvsRecyclerView.adapter = tvsAdapter
            tvsAdapter.notifyDataSetChanged()
        }
    }

    private fun filteredGenre(id : Int){
        genreId = id
        apiSystem.requestTvs(RequestBaseOptions(null, null, ::tvsFilteredGenreData, ::onFailure))
    }

    fun showFilterList(){
        tvsScene.runOnUiThread {
            var chosenItem: Int = -1
            MaterialAlertDialogBuilder(tvsScene)
                .setTitle(tvsScene.resources.getString(R.string.filter))
                .setNeutralButton(tvsScene.resources.getString(R.string.close_btn)) { dialog, which -> }
                .setPositiveButton(tvsScene.resources.getString(R.string.confirm_btn)) { dialog, which ->
                    when(chosenItem){
                        0 -> tvsFilter(tvsPopularDesc)
                        1 -> tvsFilter(tvsPopularAsc)
                        2 -> tvsFilter(tvsFilteredDateAsc)
                        3 -> tvsFilter(tvsFilteredDateDesc)
                        4 -> tvsFilter(tvsFilteredAZ)
                        5 -> tvsFilter(tvsFilteredZA)
                        else -> {
                            tvsFilter(tvsPopularDesc)
                        }
                    }
                }
                .setSingleChoiceItems(R.array.filter_array, chosenItem) { dialog, which ->
                    chosenItem = which
                }
            .show()
        }
    }

    fun showGenreFilterList(){
        tvsScene.runOnUiThread {
            var chosenItem: Int = -1
            MaterialAlertDialogBuilder(tvsScene)
                .setTitle(tvsScene.resources.getString(R.string.filter_genre))
                .setNeutralButton(tvsScene.resources.getString(R.string.close_btn)) { dialog, which -> }
                .setPositiveButton(tvsScene.resources.getString(R.string.confirm_btn)) { dialog, which ->
                    when(chosenItem){
                        0 -> filteredGenre(10759)
                        1 -> filteredGenre(16)
                        2 -> filteredGenre(35)
                        3 -> filteredGenre(80)
                        4 -> filteredGenre(99)
                        5 -> filteredGenre(18)
                        6 -> filteredGenre(10751)
                        7 -> filteredGenre(10762)
                        8 -> filteredGenre(10763)
                        9 -> filteredGenre(10764)
                        10 -> filteredGenre(10765)
                        11 -> filteredGenre(10766)
                        12 -> filteredGenre(10767)
                        13 -> filteredGenre(10768)
                        14 -> filteredGenre(37)
                    }
                }
                .setSingleChoiceItems(R.array.filter_genre_tv_array, chosenItem) { dialog, which ->
                    chosenItem = which
                }
            .show()
        }
    }
}