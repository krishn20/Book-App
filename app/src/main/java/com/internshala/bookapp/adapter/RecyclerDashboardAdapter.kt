package com.internshala.bookapp.adapter

import android.content.Context
import android.content.Intent
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.*
import androidx.recyclerview.widget.RecyclerView
import com.internshala.bookapp.R
import com.internshala.bookapp.activity.DescriptionActivity
import com.internshala.bookapp.model.Book
import com.squareup.picasso.Picasso


class RecyclerDashboardAdapter(val context: Context, private val itemList: ArrayList<Book>): RecyclerView.Adapter<RecyclerDashboardAdapter.DashboardViewHolder>() {

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): DashboardViewHolder {
        val view = LayoutInflater.from(parent.context).inflate(R.layout.recycler_dashboard_single_row, parent, false)
        return DashboardViewHolder(view)
    }

    override fun getItemCount(): Int {
        return itemList.size
    }

    override fun onBindViewHolder(holder: DashboardViewHolder, position: Int) {
        val book = itemList[position]

        holder.name.text = book.bookName
        holder.author.text = book.bookAuthor
        holder.rating.text = book.bookRating
        holder.cost.text = book.bookPrice
        Picasso.get().load(book.bookImage).error(R.drawable.default_book_cover).into(holder.image)

        holder.rowParent.setOnClickListener {
            val intent = Intent(context, DescriptionActivity::class.java)
            intent.putExtra("bookId", book.bookId)
            context.startActivity(intent)
        }

    }

    class DashboardViewHolder(view: View): RecyclerView.ViewHolder(view){
        val name: TextView = view.findViewById(R.id.txtBookName)
        val author: TextView = view.findViewById(R.id.txtBookAuthor)
        val rating: TextView = view.findViewById(R.id.txtBookRating)
        val cost: TextView = view.findViewById(R.id.txtBookPrice)
        val image: ImageView = view.findViewById(R.id.imgBookImage)
        val rowParent: LinearLayout = view.findViewById(R.id.rowLLParent)

    }

}