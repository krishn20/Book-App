package com.internshala.bookapp.fragment

import android.app.Activity
import android.app.AlertDialog
import android.content.Context
import android.content.Intent
import android.os.Bundle
import android.provider.Settings
import android.view.*
import androidx.fragment.app.Fragment
import android.widget.Button
import android.widget.ProgressBar
import android.widget.RelativeLayout
import android.widget.Toast
import androidx.core.app.ActivityCompat
import androidx.recyclerview.widget.DividerItemDecoration
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.android.volley.Request
import com.android.volley.Response
import com.android.volley.toolbox.JsonObjectRequest
import com.android.volley.toolbox.Volley
import com.internshala.bookapp.R
import com.internshala.bookapp.adapter.RecyclerDashboardAdapter
import com.internshala.bookapp.model.Book
import com.internshala.bookapp.util.ConnectionManager
import org.json.JSONException
import java.util.*
import kotlin.Comparator
import kotlin.collections.HashMap

class DashboardFragment : Fragment() {

    private lateinit var recyclerDashboard: RecyclerView
    private lateinit var layoutManager: RecyclerView.LayoutManager
    private lateinit var recyclerAdapter: RecyclerDashboardAdapter
    private lateinit var checkConnectionButton: Button
    private lateinit var progressLayout: RelativeLayout
    private lateinit var progressBar: ProgressBar

    private val bookInfoList = arrayListOf<Book>()

    private var ratingComparator = Comparator<Book> { book1, book2 ->
        if (book1.bookRating.compareTo(book2.bookRating, true) == 0) {
            book1.bookName.compareTo(book2.bookName, true)
        } else {
            book1.bookRating.compareTo(book2.bookRating, true)
        }
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {

        val view = inflater.inflate(R.layout.fragment_dashboard, container, false)

        setHasOptionsMenu(true)

        recyclerDashboard = view.findViewById(R.id.recyclerDashboard)
        layoutManager = LinearLayoutManager(activity)
        checkConnectionButton = view.findViewById(R.id.checkInternetButton)
        progressLayout = view.findViewById(R.id.progressLayout)
        progressBar = view.findViewById(R.id.progressBar)

        progressLayout.visibility = View.VISIBLE

        checkConnectionButton.setOnClickListener {
            if (ConnectionManager().checkConnectivity(activity as Context)) {
                val dialog = AlertDialog.Builder(activity as Context)
                dialog.setTitle("Success!")
                dialog.setMessage("Connected to the Internet")
                dialog.setPositiveButton("OK") { text, listener ->

                }
                dialog.setNegativeButton("Cancel") { text, listener ->
                }
                dialog.create()
                dialog.show()
            } else {
                val dialog = AlertDialog.Builder(activity as Context)
                dialog.setTitle("Failure!")
                dialog.setMessage("Couldn't connect to the Internet")
                dialog.setPositiveButton("OK") { text, listener ->

                }
                dialog.setNegativeButton("Cancel") { text, listener ->
                }
                dialog.create()
                dialog.show()
            }
        }

        val queue = Volley.newRequestQueue(activity as Context)
        val url = "http://13.235.250.119/v1/book/fetch_books/"

        if (ConnectionManager().checkConnectivity(activity as Context)) {
            val jsonObjectRequest = object : JsonObjectRequest(
                Request.Method.GET,
                url,
                null,
                Response.Listener {

                    try {
                        progressLayout.visibility = View.GONE
                        val success = it.getBoolean("success")
                        if (success) {
                            val data = it.getJSONArray("data")
                            for (i in 0 until data.length()) {
                                val bookJSONObject = data.getJSONObject(i)
                                val bookObject = Book(
                                    bookJSONObject.getString("book_id"),
                                    bookJSONObject.getString("name"),
                                    bookJSONObject.getString("author"),
                                    bookJSONObject.getString("rating"),
                                    bookJSONObject.getString("price"),
                                    bookJSONObject.getString("image")
                                )

                                bookInfoList.add(bookObject)
                            }

                            recyclerAdapter =
                                RecyclerDashboardAdapter(activity as Context, bookInfoList)
                            recyclerDashboard.adapter = recyclerAdapter
                            recyclerDashboard.layoutManager = layoutManager
                            recyclerDashboard.addItemDecoration(
                                DividerItemDecoration(
                                    recyclerDashboard.context,
                                    (layoutManager as LinearLayoutManager).orientation
                                )
                            )
                        } else {
                            Toast.makeText(
                                activity as Context,
                                "Some Error Occurred!",
                                Toast.LENGTH_SHORT
                            ).show()
                        }
                    } catch (e: JSONException) {
                        Toast.makeText(
                            activity as Context,
                            "Some unexpected error occurred",
                            Toast.LENGTH_SHORT
                        ).show()
                    }

                },

                Response.ErrorListener {
                    if (activity != null) {
                        Toast.makeText(
                            activity as Context,
                            "Volley error occurred",
                            Toast.LENGTH_SHORT
                        ).show()
                    }
                }) {

                override fun getHeaders(): MutableMap<String, String> {

                    val headers = HashMap<String, String>()
                    headers["Content-type"] = "application/json"
                    headers["token"] = "d1f3468e9bfbe5"

                    return headers
                }
            }

            queue.add(jsonObjectRequest)

        } else {
            val dialog = AlertDialog.Builder(activity as Context)
            dialog.setTitle("Failure!")
            dialog.setMessage("Couldn't connect to the Internet")
            dialog.setPositiveButton("Open Settings") { text, listener ->
                val settingsIntent = Intent(Settings.ACTION_WIRELESS_SETTINGS)
                startActivity(settingsIntent)
                activity?.finish()
            }

            dialog.setNegativeButton("Exit") { text, listener ->
                ActivityCompat.finishAffinity(activity as Activity)
            }
            dialog.create()
            dialog.show()
        }

        return view

    }

    override fun onCreateOptionsMenu(menu: Menu, inflater: MenuInflater) {
        inflater.inflate(R.menu.menu_dashboard, menu)
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        val id = item.itemId
        if (id == R.id.action_sort) {
            Collections.sort(bookInfoList, ratingComparator)
            bookInfoList.reverse()
        }

        recyclerAdapter.notifyDataSetChanged()

        return super.onOptionsItemSelected(item)
    }

}