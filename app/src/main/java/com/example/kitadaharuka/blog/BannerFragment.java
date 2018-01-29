package com.example.kitadaharuka.blog;

import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.text.Html;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.squareup.picasso.Picasso;

import org.json.JSONArray;
import org.json.JSONException;


public class BannerFragment extends Fragment {

    private ImageView banner_img;
    private TextView banner_title;
    private RelativeLayout relativeLayout;
    private JSONArray data;
    private int position;

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        final View v = inflater.inflate(R.layout.fragment_banner, container, false);

        if (getArguments() != null && getArguments().containsKey("url") && getArguments().containsKey("title")) {

            // image
            banner_img = (ImageView) v.findViewById(R.id.banner_img);
            Picasso.with(getContext()).load(getArguments().getString("url")).fit().centerCrop().into(banner_img);

            // title
            banner_title = (TextView) v.findViewById(R.id.banner_title);
            banner_title.setText(Html.fromHtml(getArguments().getString("title")).toString());

            // position
            position = getArguments().getInt("position");

            try {
                data = new JSONArray(getArguments().getString("json").toString());
            } catch(JSONException e) {
                throw new RuntimeException(e);
            }

            // set click event
            relativeLayout = (RelativeLayout) v.findViewById(R.id.banner);
            relativeLayout.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    Intent intent = new Intent(view.getContext(), HtmlActivity.class);
                    intent.putExtra("position", position);
                    intent.putExtra("json", getArguments().getString("json").toString());
                    startActivity(intent);
                }
            });

        }

        return v;
    }
}
