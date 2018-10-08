package com.example.picasso;

import android.content.Context;
import android.graphics.Bitmap;
import android.support.annotation.NonNull;
import android.util.Log;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;

import com.squareup.picasso3.Callback;
import com.squareup.picasso3.provider.PicassoProvider;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import static android.widget.ImageView.ScaleType.CENTER_CROP;

final class SampleGridViewAdapter extends BaseAdapter {
  private final Context context;
  private final List<String> urls = new ArrayList<>();

  SampleGridViewAdapter(Context context) {
    this.context = context;

    // Ensure we get a different ordering of images on each run.
    Collections.addAll(urls, Data.URLS);
    Collections.shuffle(urls);

    // Triple up the list.
    ArrayList<String> copy = new ArrayList<>(urls);
    urls.addAll(copy);
    urls.addAll(copy);
  }

  @Override public View getView(int position, View convertView, ViewGroup parent) {
    SquaredImageView view = (SquaredImageView) convertView;
    if (view == null) {
      view = new SquaredImageView(context);
      view.setScaleType(CENTER_CROP);
    }

    // Get the image URL for the current position.
    String url = getItem(position);
    final String file_name = getFileName(url);
      String imagePath = getFileDir(context) + "/images/" + file_name;
      File folder = new File(getFileDir(context) + "/images/");
      if (!folder.exists()) {
          folder.mkdirs();
      }
      File img = new File(imagePath);
      if(img.exists()){
            Log.v("file", img.getPath());
          PicassoProvider.get() //
                  .load(img) //
                  .placeholder(R.drawable.placeholder) //
                  .error(R.drawable.error) //
                  .fit() //
                  .tag(context) //
                  .into(view);
      }else {
          // Trigger the download of the URL asynchronously into the image view.
          PicassoProvider.get() //
                  .load(url) //
                  .placeholder(R.drawable.placeholder) //
                  .error(R.drawable.error) //
                  .fit() //
                  .tag(context) //
                  .into(view, new Callback() {
                      @Override
                      public void onSuccess(Bitmap bitmap) {
                          try {
                              String imagePath = getFileDir(context) + "/images/" + file_name;
                              File img = new File(imagePath);
                              FileOutputStream out = new FileOutputStream(img);
                              bitmap.compress(Bitmap.CompressFormat.PNG, 100, out);
                          } catch (IOException e) {
                              e.printStackTrace();
                          }
                      }

                      @Override
                      public void onError(@NonNull Throwable t) {

                      }
                  });
      }
    return view;
  }
  //get file dir
  public static String getFileDir(Context context) {
    File filea;
    if(context == null) return  null;
    if (android.os.Environment.getExternalStorageState().equals(
            android.os.Environment.MEDIA_MOUNTED)) {
      filea = context.getExternalFilesDir(null);
    } else {
      filea = context.getFilesDir();
    }
    return filea.toString();
  }
  public   String getFileName(String url)  {
    String fileName = url.substring(url.lastIndexOf('/')+1, url.length());
    if(fileName.lastIndexOf("#") > 0) {
      fileName = fileName.substring(0, fileName.lastIndexOf("#"));
    }
    return fileName;
  }
  @Override public int getCount() {
    return urls.size();
  }

  @Override public String getItem(int position) {
    return urls.get(position);
  }

  @Override public long getItemId(int position) {
    return position;
  }
}
