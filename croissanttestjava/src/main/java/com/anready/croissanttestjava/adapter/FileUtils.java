package com.anready.croissanttestjava.adapter;

import android.app.Activity;
import android.app.AlertDialog;
import android.content.ContentResolver;
import android.database.Cursor;
import android.net.Uri;
import android.widget.ListView;

import com.anready.croissanttestjava.MainActivity;
import com.anready.croissanttestjava.R;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

public class FileUtils {

    public static void getObjectsByFolderId(Activity activity) {
        if (!accessToCroissant(activity)) {
            alertDialog(activity, "This app can't connect with Croissant, please provide permission in App Croissant");
        }

        if (!checkPermission(activity)) {
            alertDialog(activity, "No permission granted for app Croissant");
            return;
        }

        List<DirectoryContents> fullList = getListOfObjects(activity);
        MainActivity act = (MainActivity) activity;

        Collections.sort(fullList, (o1, o2) -> {
            return o1.name.compareTo(o2.name);
        });

        if (!"/".equals(act.getPath())) {
            fullList.add(0, new DirectoryContents("..", true, false));
        }

        ListView listView = activity.findViewById(R.id.listObjects);
        ListObjectsAdapter adapter = new ListObjectsAdapter(activity, fullList);
        activity.runOnUiThread(() -> listView.setAdapter(adapter));
    }

    private static List<DirectoryContents> getListOfObjects(Activity ac) {
        MainActivity act = (MainActivity) ac;
        ContentResolver contentResolver = ac.getContentResolver();
        Uri uri = Uri.parse("content://com.anready.croissant.files")
                .buildUpon()
                .appendQueryParameter("path", act.getPath())
                .appendQueryParameter("command", "list")
                .build();

        Cursor cursor = null;
        try {
            cursor = contentResolver.query(uri, null, null, null, null);
            if (cursor != null && cursor.moveToFirst()) {
                int dataIndex = cursor.getColumnIndex("response");
                if (dataIndex == -1) {
                    alertDialog(ac, "Data column not found");
                    return new ArrayList<>();
                }

                JSONArray jsonArray = new JSONArray(cursor.getString(dataIndex));
                if (error(jsonArray)) {
                    alertDialog(ac, "Error: " + jsonArray.getJSONObject(0).getString("error"));
                    return new ArrayList<>();
                }

                List<DirectoryContents> fullList = new ArrayList<>();
                for (int i = 0; i < jsonArray.length(); i++) {
                    JSONObject fileInfo = jsonArray.getJSONObject(i);
                    fullList.add(new DirectoryContents(
                            fileInfo.getString("name"),
                            fileInfo.getBoolean("type"),
                            fileInfo.getBoolean("visibility")
                    ));
                }

                return fullList;
            } else {
                alertDialog(ac, "Error while getting data!");
            }
        } catch (Exception e) {
            alertDialog(ac, "Error while getting data!\n" + e.getMessage());
        } finally {
            if (cursor != null) {
                cursor.close();
            }
        }

        return new ArrayList<>();
    }

    private static boolean checkPermission(Activity ac) {
        ContentResolver contentResolver = ac.getContentResolver();
        Uri uri = Uri.parse("content://com.anready.croissant.files")
                .buildUpon()
                .appendQueryParameter("command", "isPermissionsGranted")
                .build();

        Cursor cursor = null;
        try {
            cursor = contentResolver.query(uri, null, null, null, null);
            if (cursor != null && cursor.moveToFirst()) {
                int dataIndex = cursor.getColumnIndex("response");
                if (dataIndex == -1) {
                    alertDialog(ac, "Data column not found");
                    return false;
                }

                JSONArray jsonArray = new JSONArray(cursor.getString(dataIndex));
                if (error(jsonArray)) {
                    alertDialog(ac, "Error: " + jsonArray.getJSONObject(0).getString("error"));
                    return false;
                }

                return jsonArray.getJSONObject(0).getBoolean("result");
            } else {
                alertDialog(ac, "Error while getting data!");
            }
        } catch (Exception e) {
            alertDialog(ac, "Error while getting data!\n" + e.getMessage());
        } finally {
            if (cursor != null) {
                cursor.close();
            }
        }
        return false;
    }

    private static boolean accessToCroissant(Activity ac) {
        ContentResolver contentResolver = ac.getContentResolver();
        Uri uri = Uri.parse("content://com.anready.croissant.files")
                .buildUpon()
                .appendQueryParameter("command", "accessToCroissant")
                .build();

        Cursor cursor = null;
        try {
            cursor = contentResolver.query(uri, null, null, null, null);
            if (cursor != null && cursor.moveToFirst()) {
                int dataIndex = cursor.getColumnIndex("response");
                if (dataIndex == -1) {
                    alertDialog(ac, "Data column not found");
                    return false;
                }

                JSONArray jsonArray = new JSONArray(cursor.getString(dataIndex));
                if (error(jsonArray)) {
                    alertDialog(ac, "Error: " + jsonArray.getJSONObject(0).getString("error"));
                    return false;
                }

                return jsonArray.getJSONObject(0).getBoolean("result");
            } else {
                alertDialog(ac, "Error while getting data!");
            }
        } catch (Exception e) {
            alertDialog(ac, "Error while getting data!\n" + e.getMessage());
        } finally {
            if (cursor != null) {
                cursor.close();
            }
        }
        return false;
    }

    public static boolean isPathExist(Activity ac, String path) {
        ContentResolver contentResolver = ac.getContentResolver();
        Uri uri = Uri.parse("content://com.anready.croissant.files")
                .buildUpon()
                .appendQueryParameter("path", path)
                .appendQueryParameter("command", "pathExist")
                .build();

        Cursor cursor = null;
        try {
            cursor = contentResolver.query(uri, null, null, null, null);
            if (cursor != null && cursor.moveToFirst()) {
                int dataIndex = cursor.getColumnIndex("response");
                if (dataIndex == -1) {
                    alertDialog(ac, "Data column not found");
                    return false;
                }

                JSONArray jsonArray = new JSONArray(cursor.getString(dataIndex));
                if (error(jsonArray)) {
                    alertDialog(ac, "Error: " + jsonArray.getJSONObject(0).getString("error"));
                    return false;
                }

                return jsonArray.getJSONObject(0).getBoolean("result");
            } else {
                alertDialog(ac, "Error while getting data!");
            }
        } catch (Exception e) {
            alertDialog(ac, "Error while getting data!\n" + e.getMessage());
        } finally {
            if (cursor != null) {
                cursor.close();
            }
        }
        return false;
    }

    private static void alertDialog(Activity ac, String s) {
        ac.runOnUiThread(() -> {
            AlertDialog.Builder builder = new AlertDialog.Builder(ac);
            builder.setTitle("Error");
            builder.setMessage(s);
            builder.setPositiveButton("OK", (dialog, which) -> dialog.dismiss());
            builder.show();
        });
    }

    private static boolean error(JSONArray jsonArray) {
        try {
            jsonArray.getJSONObject(0).getString("error");
            return true;
        } catch (JSONException e) {
            return false;
        }
    }
}
