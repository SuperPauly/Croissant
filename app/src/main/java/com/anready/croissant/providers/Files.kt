package com.anready.croissant.providers

import android.Manifest
import android.content.ContentProvider
import android.content.ContentValues
import android.content.pm.PackageManager
import android.database.Cursor
import android.net.Uri
import android.database.MatrixCursor
import android.os.Build
import android.os.Environment
import androidx.core.content.ContextCompat
import org.json.JSONArray
import org.json.JSONObject
import java.io.File

class Files : ContentProvider() {
    override fun query(
        uri: Uri,
        projection: Array<String>?,
        selection: String?,
        selectionArgs: Array<String>?,
        sortOrder: String?,
    ): Cursor {
        val cursor = MatrixCursor(arrayOf("response"))
        val path = uri.getQueryParameter("path")
        val command = uri.getQueryParameter("command")

        when (command) {
            "list" -> {
                cursor.addRow(arrayOf(listObjects(path.toString())))
            }
            "isPermissionsGranted" -> {
                cursor.addRow(arrayOf(message("result", checkPermission())))
            }
            "pathExist" -> {
                cursor.addRow(arrayOf(isPathExist(path.toString())))
            }
            else -> {
                cursor.addRow(arrayOf(message("error", "ERR_03: Unknown command")))
            }
        }

        return cursor
    }

    private fun message(type: String, message: Any): String {
        val error = JSONObject()
        error.put(type, message)
        return JSONArray().put(error).toString()
    }

    private fun checkPermission(): Boolean {
        val context = context ?: return false
        return if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.R) {
            Environment.isExternalStorageManager()
        } else {
            ContextCompat.checkSelfPermission(
                context,
                Manifest.permission.READ_EXTERNAL_STORAGE
            ) == PackageManager.PERMISSION_GRANTED
        }
    }

    private fun listObjects(path: String): String {
        if (!checkPermission()) {
            return message("error", "ERR_01: No permission to access external storage")
        }

        val files = File(Environment.getExternalStorageDirectory().absolutePath + path).listFiles()
        val filesArray = JSONArray()

        if (files == null) {
            return message("error", "ERR_02: Incorrect path provided")
        }

        for (file in files) {
            val fileObject = JSONObject()
            fileObject.put("name", file.name)
            fileObject.put("type", file.isDirectory)
            fileObject.put("visibility", file.isHidden)
            filesArray.put(fileObject)
        }

        return filesArray.toString()
    }

    private fun isPathExist(path: String): String {
        if (!checkPermission()) {
            return message("error", "ERR_01: No permission to access external storage")
        }

        val file = File(Environment.getExternalStorageDirectory().absolutePath + path).exists()
        return message("result", file)
    }

    override fun onCreate(): Boolean {
        return true
    }

    override fun getType(uri: Uri): String? {
        return null
    }

    override fun insert(uri: Uri, values: ContentValues?): Uri? {
        return null
    }

    override fun delete(uri: Uri, selection: String?, selectionArgs: Array<String>?): Int {
        return 0
    }

    override fun update(uri: Uri, values: ContentValues?, selection: String?, selectionArgs: Array<String>?): Int {
        return 0
    }
}
