package com.anready.croissant.adapter

import android.content.Context
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.BaseAdapter
import android.widget.ImageView
import com.google.android.material.switchmaterial.SwitchMaterial
import android.widget.TextView
import androidx.appcompat.app.AlertDialog
import com.anready.croissant.Constants.APPS_READ_ACCESS
import com.anready.croissant.R

class AppAdapter(private val context: Context, private val appList: List<AppModel>) : BaseAdapter() {

    override fun getCount(): Int = appList.size

    override fun getItem(position: Int): Any = appList[position]

    override fun getItemId(position: Int): Long = position.toLong()

    override fun getView(position: Int, convertView: View?, parent: ViewGroup?): View {
        val view: View = convertView ?: LayoutInflater.from(context).inflate(R.layout.select_file_item, parent, false)

        val appIcon = view.findViewById<ImageView>(R.id.imageView)
        val appName = view.findViewById<TextView>(R.id.textView)

        val app = appList[position]
        appIcon.setImageDrawable(app.icon)
        "${app.name} (${app.packageName})".also { appName.text = it }

        view.setOnClickListener {
            showAppDialog(context, app.packageName)
        }

        return view
    }

    private fun showAppDialog(context: Context, packageName: String) {
        val builder = AlertDialog.Builder(context)
        val dialogView = LayoutInflater.from(context).inflate(R.layout.dialog_layout, null)

        val sharedPreferences = context.getSharedPreferences(APPS_READ_ACCESS, Context.MODE_PRIVATE)

        val switchButton = dialogView.findViewById<SwitchMaterial>(R.id.switchButton)
        switchButton.isChecked = sharedPreferences.getBoolean(packageName, true)
        switchButton.setOnCheckedChangeListener { _, isChecked ->
            sharedPreferences.edit().putBoolean(packageName, isChecked).apply()
        }

        builder.setView(dialogView)
            .setPositiveButton("OK") { dialog, _ ->
                dialog.dismiss()
            }

        val dialog = builder.create()
        dialog.show()
    }
}
