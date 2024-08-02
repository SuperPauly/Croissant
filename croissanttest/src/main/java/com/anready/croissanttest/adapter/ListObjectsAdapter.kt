package com.anready.croissanttest.adapter

import android.app.Activity
import android.app.AlertDialog
import android.content.ContentResolver
import android.content.Intent
import android.database.Cursor
import android.net.Uri
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.BaseAdapter
import android.widget.ImageView
import android.widget.ListView
import android.widget.TextView
import com.anready.croissanttest.MainActivity
import com.anready.croissanttest.adapter.FileUtils.getObjectsByFolderId
import org.json.JSONArray
import org.json.JSONException


class ListObjectsAdapter(
    private val ac: Activity,
    private val entries: List<DirectoryContents>,
) : BaseAdapter() {

    private val inflater: LayoutInflater = LayoutInflater.from(ac)

    override fun getCount(): Int {
        return entries.size
    }

    override fun getItem(position: Int): Any? {
        return null
    }

    override fun getItemId(position: Int): Long {
        return 0
    }

    override fun getView(position: Int, convertView: View?, parent: ViewGroup): View {
        var view = convertView
        if (view == null) {
            view = inflater.inflate(com.anready.croissanttest.R.layout.select_file_item, parent, false)
        }

        val textView = view!!.findViewById<TextView>(com.anready.croissanttest.R.id.textView)
        val imageView = view.findViewById<ImageView>(com.anready.croissanttest.R.id.imageView)

        textView.text = entries[position].name
        imageView.setImageResource(if (entries[position].isDirectory) com.anready.croissanttest.R.drawable.ic_folder else com.anready.croissanttest.R.drawable.ic_file)

        view.setOnClickListener {
            val act = ac as MainActivity
            if (entries[position].name == "..") {
                act.path = act.path.substring(0, act.path.lastIndexOf("/"))
                getObjectsByFolderId(ac)
            } else if (entries[position].isDirectory) {
                act.path = act.path + "/" + entries[position].name
                getObjectsByFolderId(ac)
            } else {
                val intent = Intent()
                intent.setClassName(
                    "com.anready.croissant",
                    "com.anready.croissant.providers.OpenFile"
                )
                intent.putExtra("path", act.path + "/" + entries[position].name)
                ac.startActivity(intent)
            }
        }

        return view
    }
}

object FileUtils {
    fun getObjectsByFolderId(activity: Activity) {
        if (!checkPermission(activity)) {
            alertDialog(activity, "No permission granted for app Croissant")
        }

        val fullList = getListOfObjects(activity)
        val act = activity as MainActivity

        fullList.sortBy { it.name }
        if (act.path != "/") fullList.add(0, DirectoryContents("..", true, isHidden = false))

        val listView = activity.findViewById<ListView>(com.anready.croissanttest.R.id.listObjects)
        val adapter = ListObjectsAdapter(activity, fullList)
        activity.runOnUiThread { listView.adapter = adapter }
    }

    private fun getListOfObjects(ac: Activity): MutableList<DirectoryContents> {
        val act = ac as MainActivity
        val contentResolver: ContentResolver = ac.contentResolver
        val uri = Uri.parse("content://com.anready.croissant.files")
            .buildUpon()
            .appendQueryParameter("path", act.path)
            .appendQueryParameter("command", "list")
            .build()

        var cursor: Cursor? = null
        try {
            cursor = contentResolver.query(uri, null, null, null, null)
            if (cursor != null && cursor.moveToFirst()) {
                val dataIndex = cursor.getColumnIndex("response")
                if (dataIndex == -1) {
                    alertDialog(ac, "Data column not found")
                    return mutableListOf()
                }

                val jsonArray = JSONArray(cursor.getString(dataIndex))
                if (error(jsonArray)) {
                    alertDialog(ac, "Error: " + jsonArray.getJSONObject(0).getString("error"))
                    return mutableListOf()
                }

                val fullList = mutableListOf<DirectoryContents>()

                for (i in 0 until jsonArray.length()) {
                    val fileInfo = jsonArray.getJSONObject(i)
                    fullList.add(
                        DirectoryContents(
                            name = fileInfo.getString("name"),
                            isDirectory = fileInfo.getBoolean("type"),
                            isHidden = fileInfo.getBoolean("visibility")
                        )
                    )
                }

                return fullList
            } else {
                alertDialog(ac, "Error while getting data!")
            }
        } catch (e: Exception) {
            alertDialog(ac, "Error while getting data!\n" + e.message)
        } finally {
            cursor?.close()
        }

        return mutableListOf()
    }

    private fun checkPermission(ac: Activity): Boolean {
        val contentResolver: ContentResolver = ac.contentResolver
        val uri = Uri.parse("content://com.anready.croissant.files")
            .buildUpon()
            .appendQueryParameter("command", "isPermissionsGranted")
            .build()

        var cursor: Cursor? = null
        try {
            cursor = contentResolver.query(uri, null, null, null, null)
            if (cursor != null && cursor.moveToFirst()) {
                val dataIndex = cursor.getColumnIndex("response")
                if (dataIndex == -1) {
                    alertDialog(ac, "Data column not found")
                    return false
                }

                val jsonArray = JSONArray(cursor.getString(dataIndex))
                if (error(jsonArray)) {
                    alertDialog(ac, "Error: " + jsonArray.getJSONObject(0).getString("error"))
                    return false
                }

                val fileInfo = jsonArray.getJSONObject(0)
                return fileInfo.getBoolean("result")
            } else {
                alertDialog(ac, "Error while getting data!")
            }
        } catch (e: Exception) {
            alertDialog(ac, "Error while getting data!\n" + e.message)
        } finally {
            cursor?.close()
        }
        return false
    }

    private fun alertDialog(ac: Activity, s: String) {
        ac.runOnUiThread {
            val builder = AlertDialog.Builder(ac)
            builder.setTitle("Error")
            builder.setMessage(s)
            builder.setPositiveButton("OK") { dialog, _ -> dialog.dismiss() }
            builder.show()
        }
    }

    private fun error(jsonArray: JSONArray): Boolean {
        try {
            val error = jsonArray.getJSONObject(0)
            error.getString("error")
            return true
        } catch (e: JSONException) {
            return false
        }
    }
}