package com.anready.croissant.adapter

import android.content.Context
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.Switch
import android.widget.TextView
import androidx.appcompat.app.AlertDialog
import androidx.recyclerview.widget.RecyclerView
import com.anready.croissant.R

class AppAdapter(private val context: Context, private val appList: List<AppModel>) :
    RecyclerView.Adapter<AppAdapter.ViewHolder>() {

    inner class ViewHolder(view: View) : RecyclerView.ViewHolder(view) {
        val appIcon: ImageView = view.findViewById(R.id.imageView)
        val appName: TextView = view.findViewById(R.id.textView)
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val view = LayoutInflater.from(parent.context).inflate(R.layout.select_file_item, parent, false)
        return ViewHolder(view)
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        val app = appList[position]
        holder.appName.text = app.name
        holder.appIcon.setImageDrawable(app.icon)

        holder.itemView.setOnClickListener {
            showAppDialog(context)
        }
    }

    private fun showAppDialog(context: Context) {
        val builder = AlertDialog.Builder(context)
        val dialogView = LayoutInflater.from(context).inflate(R.layout.dialog_layout, null)

        val switchButton = dialogView.findViewById<Switch>(R.id.switchButton)
        builder.setView(dialogView)
            .setPositiveButton("OK") { dialog, id ->
                // Обработка нажатия кнопки OK
            }

        val dialog = builder.create()
        dialog.show()
    }

    override fun getItemCount(): Int {
        return appList.size
    }
}
