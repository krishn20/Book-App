package com.internshala.bookapp.fragment

import android.content.Context
import android.os.AsyncTask
import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.FrameLayout
import android.widget.ProgressBar
import android.widget.RelativeLayout
import androidx.recyclerview.widget.GridLayoutManager
import androidx.recyclerview.widget.RecyclerView
import androidx.room.Room
import com.internshala.bookapp.R
import com.internshala.bookapp.adapter.RecyclerFavoritesAdapter
import com.internshala.bookapp.database.BookDatabase
import com.internshala.bookapp.database.BookEntity

private lateinit var recyclerFavorite: RecyclerView
private lateinit var progressLayout: RelativeLayout
private lateinit var progressBar: ProgressBar
private lateinit var layoutManager: GridLayoutManager
private lateinit var recyclerAdapter: RecyclerFavoritesAdapter
private var dbBookList = listOf<BookEntity>()

class FavoritesFragment : Fragment() {

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        val view =  inflater.inflate(R.layout.fragment_favorites, container, false)

        recyclerFavorite = view.findViewById(R.id.recyclerFavorite)
        progressLayout = view.findViewById(R.id.progressLayout)
        progressBar = view.findViewById(R.id.progressBar)

        layoutManager = GridLayoutManager(activity as Context, 2)
        dbBookList = RetrieveFavorites(activity as Context).execute().get()

        progressLayout.visibility = View.VISIBLE

        if(activity!=null){
            progressLayout.visibility = View.GONE
            recyclerAdapter = RecyclerFavoritesAdapter(activity as Context, dbBookList)
            recyclerFavorite.adapter = recyclerAdapter
            recyclerFavorite.layoutManager = layoutManager
        }

        return view
    }

    class RetrieveFavorites(val context: Context): AsyncTask<Void, Void, List<BookEntity>>(){
        override fun doInBackground(vararg params: Void?): List<BookEntity> {

            val db = Room.databaseBuilder(context, BookDatabase::class.java, "books-db").build()
            return db.bookDao().getAllBooks()
        }

    }

}