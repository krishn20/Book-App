package com.internshala.bookapp.adapter

import android.content.Context
import android.content.Intent
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.LinearLayout
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.internshala.bookapp.R
import com.internshala.bookapp.activity.DescriptionActivity
import com.internshala.bookapp.database.BookEntity
import com.squareup.picasso.Picasso

class RecyclerFavoritesAdapter(val context: Context, private val bookList: List<BookEntity>): RecyclerView.Adapter<RecyclerFavoritesAdapter.FavoriteViewHolder>() {

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): FavoriteViewHolder {
        val view = LayoutInflater.from(context).inflate(R.layout.recycler_favorite_single_row, parent, false)
        return FavoriteViewHolder(view)
    }

    override fun getItemCount(): Int {
        return bookList.size
    }

    override fun onBindViewHolder(holder: FavoriteViewHolder, position: Int) {
        val book = bookList[position]
        holder.textBookName.text = book.bookName
        holder.textBookAuthor.text = book.bookAuthor
        holder.textBookPrice.text = book.bookPrice
        holder.textBookRating.text = book.bookRating
        Picasso.get().load(book.bookImage).error(R.drawable.default_book_cover).into(holder.imgBookImage)

        holder.llContent.setOnClickListener {
            val intent = Intent(context, DescriptionActivity::class.java)
            intent.putExtra("bookId", book.book_id.toString())
            context.startActivity(intent)
        }
    }

    class FavoriteViewHolder(view: View): RecyclerView.ViewHolder(view){
        val textBookName: TextView = view.findViewById(R.id.txtFavBookTitle)
        val textBookAuthor: TextView = view.findViewById(R.id.txtFavBookAuthor)
        val textBookPrice: TextView = view.findViewById(R.id.txtFavBookPrice)
        val textBookRating: TextView = view.findViewById(R.id.txtFavBookRating)
        val imgBookImage: ImageView = view.findViewById(R.id.imgFavBookImage)
        val llContent: LinearLayout = view.findViewById(R.id.llFavContent)
    }

}