package com.anready.croissant

import android.content.Context
import android.content.pm.PackageManager
import android.os.Bundle
import android.view.LayoutInflater
import android.widget.Switch
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AlertDialog
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.anready.croissant.adapter.AppAdapter
import com.anready.croissant.adapter.AppModel

class AppControl : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContentView(R.layout.activity_app_control)

        try {
            val recyclerView = findViewById<RecyclerView>(R.id.recyclerView)
            recyclerView.layoutManager = LinearLayoutManager(this)

            val appList = getInstalledApps()

            val adapter = appList?.let { AppAdapter(this, it) }
            recyclerView.adapter = adapter
        } catch (e: Exception) {
            val alertDialog = AlertDialog.Builder(this)
            alertDialog.setTitle("fdgf")
            alertDialog.setMessage(e.toString())
            alertDialog.setPositiveButton("ОК") { dialog, _ ->
                dialog.dismiss()
            }
            alertDialog.show()
        }
    }

    private fun getInstalledApps(): MutableList<AppModel>? {
        try {
            val apps = mutableListOf<AppModel>()
            val pm = packageManager
            val packages = pm.getInstalledApplications(PackageManager.GET_META_DATA)

            for (appInfo in packages) {
                val appName = pm.getApplicationLabel(appInfo).toString()
                val appIcon = pm.getApplicationIcon(appInfo)
                apps.add(AppModel(appName, appIcon))
            }
            return apps
        } catch (e: Exception) {
            val alertDialog = AlertDialog.Builder(this)
            alertDialog.setTitle("fdgf")
            alertDialog.setMessage(e.toString())
            alertDialog.setPositiveButton("ОК") { dialog, _ ->
                dialog.dismiss()
            }
            alertDialog.show()
        }

        return null
    }
}