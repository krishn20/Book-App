package com.internshala.bookapp.activity

import android.app.AlertDialog
import android.content.Context
import android.content.Intent
import android.os.AsyncTask
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.provider.Settings
import android.view.View
import android.widget.*
import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat
import androidx.room.Room
import com.android.volley.Response
import com.android.volley.toolbox.JsonObjectRequest
import com.android.volley.toolbox.Volley
import com.internshala.bookapp.R
import com.internshala.bookapp.database.BookDatabase
import com.internshala.bookapp.database.BookEntity
import com.internshala.bookapp.util.ConnectionManager
import com.squareup.picasso.Picasso
import org.json.JSONException
import org.json.JSONObject

private lateinit var textBookName: TextView
private lateinit var textBookAuthor: TextView
private lateinit var textBookPrice: TextView
private lateinit var textBookRating: TextView
private lateinit var textBookImage: ImageView
private lateinit var textBookDesc: TextView
private lateinit var addToFavoritesButton: Button
private lateinit var progressLayoutInDesc: RelativeLayout
private lateinit var progressBarInDesc: ProgressBar

var bookId: String? = "100"


class DescriptionActivity : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_description)

        textBookName = findViewById(R.id.txtBookName)
        textBookAuthor = findViewById(R.id.txtBookAuthor)
        textBookPrice = findViewById(R.id.txtBookPrice)
        textBookRating = findViewById(R.id.txtBookRating)
        textBookImage = findViewById(R.id.imgBookImage)
        textBookDesc = findViewById(R.id.txtBookDesc)
        addToFavoritesButton = findViewById(R.id.addToFavButton)
        progressLayoutInDesc = findViewById(R.id.progressLayout)
        progressBarInDesc = findViewById(R.id.progressBar)

        progressLayoutInDesc.visibility = View.VISIBLE
        progressBarInDesc.visibility = View.VISIBLE

        if (intent != null) {
            bookId = intent.getStringExtra("bookId")
        } else {
            finish()
            Toast.makeText(
                this@DescriptionActivity,
                "Some unexpected error occurred",
                Toast.LENGTH_SHORT
            ).show()
        }

        if (bookId == "100") {
            finish()
            Toast.makeText(
                this@DescriptionActivity,
                "Some unexpected error occurred",
                Toast.LENGTH_SHORT
            ).show()
        }

        val queue = Volley.newRequestQueue(this@DescriptionActivity)
        val url = "http://13.235.250.119/v1/book/get_book/"

        val jsonParams = JSONObject()
        jsonParams.put("book_id", bookId)


        if (ConnectionManager().checkConnectivity(this@DescriptionActivity)) {

            val jsonRequest =
                object : JsonObjectRequest(Method.POST, url, jsonParams, Response.Listener {
                    try {

                        val success = it.getBoolean("success")
                        if (success) {
                            progressLayoutInDesc.visibility = View.GONE
                            val bookJsonObject = it.getJSONObject("book_data")

                            val bookImageUrl = bookJsonObject.getString("image")
                            Picasso.get().load(bookJsonObject.getString("image"))
                                .error(R.drawable.default_book_cover).into(textBookImage)
                            textBookName.text = bookJsonObject.getString("name")
                            textBookAuthor.text = bookJsonObject.getString("author")
                            textBookPrice.text = bookJsonObject.getString("price")
                            textBookRating.text = bookJsonObject.getString("rating")
                            textBookDesc.text = bookJsonObject.getString("description")

                            //Making a book entity and adding it to the table. This will then be used by the database to perform operations(using DAO).

                            val bookEntity = BookEntity(
                                bookId?.toInt() as Int,
                                textBookName.text.toString(),
                                textBookAuthor.text.toString(),
                                textBookPrice.text.toString(),
                                textBookRating.text.toString(),
                                textBookDesc.text.toString(),
                                bookImageUrl
                            )

                            //Firstly we'll check if the clicked book is already in the favorites or not. Therefore we'll use Mode 1.
                            //If it is there, change the button color and text.
                            //If it is not there, change the button color to default and default text.

                            val checkFav = DBAsyncTask(applicationContext, bookEntity, 1).execute()
                            val isFav = checkFav.get()

                            if (isFav) {
                                addToFavoritesButton.text = "Remove from Favorites"
                                val favColor =
                                    ContextCompat.getColor(applicationContext, R.color.colorFav)
                                addToFavoritesButton.setBackgroundColor(favColor)
                            } else {
                                addToFavoritesButton.text = "Add to Favorites"
                                val noFavColor =
                                    ContextCompat.getColor(applicationContext, R.color.colorPrimary)
                                addToFavoritesButton.setBackgroundColor(noFavColor)
                            }

                            //When the button is clicked, we'll firstly see if it was previously added to the favorites or not.
                            // If the result for this comes out to be false, then we'll add the book and also change the button text and color to "Remove".
                            // If the result is true, then we'll remove the book and also change the button text and color to "Add".

                            addToFavoritesButton.setOnClickListener {

                                if (!DBAsyncTask(applicationContext, bookEntity, 1).execute()
                                        .get()
                                ) {

                                    val async =
                                        DBAsyncTask(applicationContext, bookEntity, 2).execute()
                                    val result = async.get()
                                    if (result) {
                                        Toast.makeText(
                                            applicationContext,
                                            "Book added to Favorites",
                                            Toast.LENGTH_SHORT
                                        ).show()

                                        addToFavoritesButton.text = "Remove from Favorites"
                                        val favColor = ContextCompat.getColor(
                                            applicationContext,
                                            R.color.colorFav
                                        )
                                        addToFavoritesButton.setBackgroundColor(favColor)
                                    } else {
                                        Toast.makeText(
                                            applicationContext,
                                            "Some unexpected error occurred!",
                                            Toast.LENGTH_SHORT
                                        ).show()
                                    }
                                } else {

                                    val async =
                                        DBAsyncTask(applicationContext, bookEntity, 3).execute()
                                    val result = async.get()
                                    if (result) {
                                        Toast.makeText(
                                            applicationContext,
                                            "Book removed from Favorites",
                                            Toast.LENGTH_SHORT
                                        ).show()

                                        addToFavoritesButton.text = "Add to Favorites"
                                        val noFavColor = ContextCompat.getColor(
                                            applicationContext,
                                            R.color.colorPrimary
                                        )
                                        addToFavoritesButton.setBackgroundColor(noFavColor)
                                    } else {
                                        Toast.makeText(
                                            applicationContext,
                                            "Some unexpected error occurred!",
                                            Toast.LENGTH_SHORT
                                        ).show()
                                    }
                                }


                            }


                        } else {
                            Toast.makeText(this, "Some Error Occurred!", Toast.LENGTH_SHORT).show()
                        }

                    } catch (e: JSONException) {
                        Toast.makeText(this, "Some unexpected error occurred", Toast.LENGTH_SHORT)
                            .show()
                    }

                },
                    Response.ErrorListener {
                        Toast.makeText(this, "Volley error occurred", Toast.LENGTH_SHORT).show()
                    }) {
                    override fun getHeaders(): MutableMap<String, String> {

                        val headers = HashMap<String, String>()
                        headers["Content-type"] = "application/json"
                        headers["token"] = "d1f3468e9bfbe5"

                        return headers
                    }
                }

            queue.add(jsonRequest)

        } else {
            val dialog = AlertDialog.Builder(this@DescriptionActivity)
            dialog.setTitle("Failure!")
            dialog.setMessage("Couldn't connect to the Internet")
            dialog.setPositiveButton("Open Settings") { text, listener ->
                val settingsIntent = Intent(Settings.ACTION_WIRELESS_SETTINGS)
                startActivity(settingsIntent)
                finish()
            }

            dialog.setNegativeButton("Exit") { text, listener ->
                ActivityCompat.finishAffinity(this@DescriptionActivity)
            }
            dialog.create()
            dialog.show()
        }

    }

    class DBAsyncTask(
        val context: Context,
        private val bookEntity: BookEntity,
        private val mode: Int
    ) : AsyncTask<Void, Void, Boolean>() {

        private val db = Room.databaseBuilder(context, BookDatabase::class.java, "books-db").build()

        override fun doInBackground(vararg params: Void?): Boolean {

            //Mode 1 -> Check to see if book in favorites db or not
            //Mode 2 -> Insert book into favorites db
            //Mode 3 -> Delete book from favorites db

            when (mode) {
                1 -> {
                    val book: BookEntity? = db.bookDao().getBookById(bookEntity.book_id.toString())
                    db.close()
                    return book != null
                }

                2 -> {
                    db.bookDao().insertBook(bookEntity)
                    db.close()
                    return true
                }

                3 -> {
                    db.bookDao().deleteBook(bookEntity)
                    db.close()
                    return true
                }
            }

            return false

        }

    }


}