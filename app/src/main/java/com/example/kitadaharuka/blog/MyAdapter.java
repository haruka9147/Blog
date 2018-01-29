package com.example.kitadaharuka.blog;

import android.app.Activity;
import android.content.res.Resources;
import android.graphics.Color;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.text.Html;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.ImageView;
import android.widget.TextView;

import com.squareup.picasso.Picasso;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

/**
 * Created by kitadaharuka on 2018/01/23.
 */

public class MyAdapter extends ArrayAdapter<String>{
    private Activity context;
    private int layoutId;
    private ArrayList<String> title;
    private ArrayList<String> category;
    private ArrayList<String> image;
    private Map<String, Integer> colors = new HashMap<String, Integer>(){
        {
            put("Event", Color.rgb(48, 101, 186));
            put("News", Color.rgb(224, 117, 24));
            put("Food", Color.rgb(68, 149, 143));
            put("Beauty", Color.rgb(145, 55, 140));
        }
    };

    /**
     * constructor
     * @param context
     * @param layoutId
     * @param title
     * @param category
     * @param image
     */
    public MyAdapter(Activity context, int layoutId, ArrayList<String> title, ArrayList<String> category, ArrayList<String> image) {
        super(context, layoutId, title);
        this.context = context;
        this.layoutId = layoutId;
        this.title = title;
        this.category = category;
        this.image = image;
    }

    @NonNull
    @Override
    public View getView(int position, @Nullable View convertView, @NonNull ViewGroup parent) {
        LayoutInflater inflater = context.getLayoutInflater();
        convertView = inflater.inflate(layoutId, parent, false);

        ImageView img = (ImageView) convertView.findViewById(R.id.list_img);
        TextView txt1 = (TextView) convertView.findViewById(R.id.list_category);
        TextView txt2 = (TextView) convertView.findViewById(R.id.list_title);

        RoundedTransformation transform = new RoundedTransformation(10, 0);
        Picasso.with(context).load(image.get(position)).resize(80, 80).centerCrop().transform(transform).into(img);
        txt1.setText(category.get(position));
        //Log.d("MyAdapter", "cate:" + category.get(position));
        txt1.setBackgroundColor(colors.get(category.get(position)));
        txt2.setText(Html.fromHtml(title.get(position)).toString());

        return convertView;
    }
}
