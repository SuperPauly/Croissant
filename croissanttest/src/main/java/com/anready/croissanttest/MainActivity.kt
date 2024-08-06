package com.anready.croissanttest

import android.os.Bundle
import android.widget.EditText
import android.widget.ImageView
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import com.anready.croissanttest.adapter.FileUtils

class MainActivity : AppCompatActivity() {
    var path = "/"
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        if (savedInstanceState != null) {
            path = savedInstanceState.getString("N", "/")
        }

        val pathEditText = findViewById<EditText>(R.id.editTextText)
        pathEditText.setText(path)

        findViewById<ImageView>(R.id.sendPath).setOnClickListener {
            if (!FileUtils.isPathExist(this, pathEditText.text.toString())) {
                Toast.makeText(this, "Path doesn't exist", Toast.LENGTH_SHORT).show()
                return@setOnClickListener
            }

            path = pathEditText.text.toString()
            FileUtils.getObjectsByFolderId(this)
        }

        FileUtils.getObjectsByFolderId(this)
    }

    override fun onSaveInstanceState(outState: Bundle) {
        super.onSaveInstanceState(outState)
        outState.putString("N", path)
    }


    override fun onRestoreInstanceState(savedInstanceState: Bundle) {
        super.onRestoreInstanceState(savedInstanceState)
        path = savedInstanceState.getString("N", "/")
    }

    override fun onBackPressed() {
        if (path.lastIndexOf("/") > 0) {
            path = path.substring(0, path.lastIndexOf("/"))
            FileUtils.getObjectsByFolderId(this)
        } else {
            super.onBackPressed()
        }
    }
}