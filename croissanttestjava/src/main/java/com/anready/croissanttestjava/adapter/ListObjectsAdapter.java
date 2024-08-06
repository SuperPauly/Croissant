package com.anready.croissanttestjava.adapter;

import android.app.Activity;
import android.content.Intent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.TextView;

import com.anready.croissanttestjava.MainActivity;
import com.anready.croissanttestjava.R;

import java.util.List;

public class ListObjectsAdapter extends BaseAdapter {

    private final Activity ac;
    private final List<DirectoryContents> entries;
    private final LayoutInflater inflater;

    public ListObjectsAdapter(Activity ac, List<DirectoryContents> entries) {
        this.ac = ac;
        this.entries = entries;
        this.inflater = LayoutInflater.from(ac);
    }

    @Override
    public int getCount() {
        return entries.size();
    }

    @Override
    public Object getItem(int position) {
        return null;
    }

    @Override
    public long getItemId(int position) {
        return 0;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        View view = convertView;
        if (view == null) {
            view = inflater.inflate(R.layout.select_file_item, parent, false);
        }

        TextView textView = view.findViewById(R.id.textView);
        ImageView imageView = view.findViewById(R.id.imageView);

        DirectoryContents entry = entries.get(position);
        textView.setText(entry.name);
        imageView.setImageResource(entry.isDirectory ? R.drawable.ic_folder : R.drawable.ic_file);

        view.setOnClickListener(v -> {
            MainActivity act = (MainActivity) ac;
            if ("..".equals(entry.name)) {
                act.setPath(act.getPath().substring(0, act.getPath().lastIndexOf("/")));
                FileUtils.getObjectsByFolderId(ac);
            } else if (entry.isDirectory) {
                act.setPath(act.getPath() + "/" + entry.name);
                FileUtils.getObjectsByFolderId(ac);
            } else {
                Intent intent = new Intent();
                intent.setClassName("com.anready.croissant", "com.anready.croissant.providers.OpenFile");
                intent.putExtra("path", act.getPath() + "/" + entry.name);
                ac.startActivity(intent);
            }
        });

        return view;
    }
}

