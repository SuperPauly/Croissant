package com.anready.croissanttest

import android.app.Activity
import android.content.Intent
import android.os.Bundle
import android.view.inputmethod.InputMethodManager
import android.widget.EditText
import android.widget.ImageView
import android.widget.Toast
import androidx.activity.result.ActivityResultLauncher
import androidx.activity.result.contract.ActivityResultContracts
import androidx.appcompat.app.AppCompatActivity
import com.anready.croissanttest.adapter.FileUtils

class MainActivity : AppCompatActivity() {
    lateinit var activityResultLauncher: ActivityResultLauncher<Intent>

    var path = "/"
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        if (savedInstanceState != null) {
            path = savedInstanceState.getString("N", "/")
        }

        if (FileUtils.accessToOpenFiles(this)){
            FileUtils.alertDialog(this, "This app can't open files by Croissant. Please give us this permission")
        }

        val pathEditText = findViewById<EditText>(R.id.editTextText)
        pathEditText.setText(path)

        activityResultLauncher = registerForActivityResult(
            ActivityResultContracts.StartActivityForResult()
        ) { result ->
            if (result.resultCode == 6) {
                val errorMessage: String = result.data?.getStringExtra("ERR").toString()
                Toast.makeText(this, errorMessage, Toast.LENGTH_SHORT).show()
            }
        }


        findViewById<ImageView>(R.id.sendPath).setOnClickListener {
            if (!FileUtils.isPathExist(this, pathEditText.text.toString())) {
                Toast.makeText(this, "Path doesn't exist", Toast.LENGTH_SHORT).show()
                return@setOnClickListener
            }

            path = pathEditText.text.toString()

            val view = currentFocus
            if (view != null) {
                val imm = getSystemService(Activity.INPUT_METHOD_SERVICE) as InputMethodManager
                imm.hideSoftInputFromWindow(view.windowToken, 0)
            }

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