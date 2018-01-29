package com.example.kitadaharuka.blog;

import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ListView;
import android.widget.TextView;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;

/**
 * Created by kitadaharuka on 2018/01/25.
 */

public class CategoryFragment extends Fragment{
    private String json;
    private String category = null;
    private ListView listview;
    private MyAdapter adapter;
    private TextView txt;
    private ArrayList<String> title_arr = new ArrayList<>();
    private ArrayList<String> category_arr = new ArrayList<>();
    private ArrayList<String> image_arr = new ArrayList<>();

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        final View v = inflater.inflate(R.layout.fragment_category, container, false);

        category = getArguments().getString("category");
        json = getArguments().getString("json");


        try {
            JSONArray json2 = new JSONArray(json);
            JSONArray json3 = new JSONArray();
            for(int i = 0; i < json2.length(); i++) {
                JSONObject item = json2.getJSONObject(i);
                if(category.equals(item.getString("category_name"))) {
                    json3.put(json2.getJSONObject(i));
                    // get title
                    JSONObject item2 = item.getJSONObject("title");
                    title_arr.add(item2.getString("rendered"));

                    // get category
                    category_arr.add(item.getString("category_name"));

                    // get image
                    JSONObject item3 = item.getJSONObject("_embedded");
                    JSONArray arr = item3.getJSONArray("wp:featuredmedia");
                    JSONObject item4 = arr.getJSONObject(0)
                            .getJSONObject("media_details")
                            .getJSONObject("sizes")
                            .getJSONObject("thumbnail");
                    image_arr.add(item4.getString("source_url"));
                    //Log.d(TAG, "getApi: " + data);
                }
            }

            // store json data
            final String JSON = json3.toString();

            //Log.d("CategoryFragment", "json -> " + JSON);

            adapter = new MyAdapter(getActivity(), R.layout.list_item, title_arr, category_arr, image_arr);
            listview = (ListView) v.findViewById(R.id.list2);
            listview.setAdapter(adapter);

            Log.d("CategoryFragment", "json -> " + JSON);

            listview.setOnItemClickListener(new AdapterView.OnItemClickListener() {
                public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                    Intent intent = new Intent(view.getContext(), HtmlActivity.class);
                    intent.putExtra("position", position);
                    intent.putExtra("json", JSON);
                    startActivity(intent);
                }
            });

            txt = (TextView) v.findViewById(R.id.category_name);
            txt.setText(category);

        } catch (JSONException e) {
            throw new RuntimeException(e);
        }
        return v;
    }
}
