package com.anready.croissanttestjava;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.view.inputmethod.InputMethodManager;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.Toast;

import androidx.annotation.NonNull;

import com.anready.croissanttestjava.adapter.FileUtils;

public class MainActivity extends Activity {

    private String path = "/";
    private EditText pathEditText;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        if (savedInstanceState != null) {
            path = savedInstanceState.getString("N", "/");
        }

        pathEditText = findViewById(R.id.editTextText);
        pathEditText.setText(path);

        ImageView sendPath = findViewById(R.id.sendPath);
        sendPath.setOnClickListener(v -> {
            if (!FileUtils.isPathExist(MainActivity.this, pathEditText.getText().toString())) {
                Toast.makeText(MainActivity.this, "Path doesn't exist", Toast.LENGTH_SHORT).show();
                return;
            }

            path = pathEditText.getText().toString();

            View view = getCurrentFocus();
            if (view != null) {
                InputMethodManager imm = (InputMethodManager) getSystemService(Activity.INPUT_METHOD_SERVICE);
                imm.hideSoftInputFromWindow(view.getWindowToken(), 0);
            }

            FileUtils.getObjectsByFolderId(MainActivity.this);
        });

        FileUtils.getObjectsByFolderId(this);
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        if (requestCode == 123) {
            if (resultCode == 6) {
                String errorMessage = data.getStringExtra("ERR");
                Toast.makeText(this, errorMessage, Toast.LENGTH_SHORT).show();
            }
        }
    }


    @Override
    protected void onSaveInstanceState(@NonNull Bundle outState) {
        super.onSaveInstanceState(outState);
        outState.putString("N", path);
    }

    @Override
    protected void onRestoreInstanceState(@NonNull Bundle savedInstanceState) {
        super.onRestoreInstanceState(savedInstanceState);
        path = savedInstanceState.getString("N", "/");
    }

    @Override
    public void onBackPressed() {
        if (path.lastIndexOf("/") > 0) {
            path = path.substring(0, path.lastIndexOf("/"));
            pathEditText.setText(path);
            FileUtils.getObjectsByFolderId(this);
        } else {
            super.onBackPressed();
        }
    }

    public String getPath() {
        return path;
    }

    public void setPath(String newPath) {
        this.path = newPath;
    }
}