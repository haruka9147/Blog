package com.example.kitadaharuka.blog;

import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentPagerAdapter;
import android.util.Log;
import android.util.SparseArray;
import android.view.ViewGroup;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

/**
 * Created by kitadaharuka on 2018/01/23.
 */

public class MyFragmentPagerAdapter extends FragmentPagerAdapter {
    private String json = "";
    private JSONArray data;
    private Fragment[] fragments = new BannerFragment[3];

    /**
     * constructor
     * @param fragmentManager
     * @param json
     */
    public MyFragmentPagerAdapter(FragmentManager fragmentManager, String json) {
        super(fragmentManager);
        this.json = json;

        // parse json
        try {
            data = new JSONArray(json);

        } catch (JSONException e) {
            throw new RuntimeException(e);
        }
    }

    @Override
    public int getCount() {
        return 3;
    }

    @Override
    public Fragment getItem(int position) {
        String url = "";
        String title = "";

        try {
            JSONObject item = data.getJSONObject(position);

            // title
            JSONObject item2 = item.getJSONObject("title");
            title = item2.getString("rendered");

            // image
            JSONObject item4 = item.getJSONObject("_embedded");
            JSONArray arr = item4.getJSONArray("wp:featuredmedia");
            JSONObject item5 = arr.getJSONObject(0)
                    .getJSONObject("media_details")
                    .getJSONObject("sizes")
                    .getJSONObject("medium_large");
            url = item5.getString("source_url");

        } catch (JSONException e) {
            throw new RuntimeException(e);
        }

        Bundle bundle = new Bundle();

        Fragment frag = fragments[position];
        if (frag == null) {
            bundle.putString("url", url);
            bundle.putString("title", title);
            bundle.putString("json", json);
            bundle.putInt("position", position);
            frag = new BannerFragment();
            frag.setArguments(bundle);
            fragments[position] = frag;
        }

        return frag;
    }

}
