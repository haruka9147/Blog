package com.example.kitadaharuka.blog;

import android.content.res.Configuration;
import android.os.Bundle;
import android.os.Handler;
import android.os.Looper;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.ActionBarDrawerToggle;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.ListView;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;
import java.util.ArrayList;

import okhttp3.Call;
import okhttp3.Callback;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.Response;

public class MainActivity extends AppCompatActivity {
    private static final String TAG = "MainActivity";
    private final String URL = "http://sorena-create.com/blog/wp-json/wp/v2/posts?_embed";
    private String json_str;
    private ListView listview;
    private MyAdapter adapter;
    private final OkHttpClient client = new OkHttpClient();
    private ArrayList<String> title_arr = new ArrayList<>();
    private ArrayList<String> category_arr = new ArrayList<>();
    private ArrayList<String> image_arr = new ArrayList<>();
    private ArrayList<String> imageLarge_arr = new ArrayList<>();

    //drawer menu
    private String[] mPlanetTitles = null;
    private DrawerLayout mDrawerLayout = null;
    private ListView mDrawerList = null;
    private ActionBarDrawerToggle mDrawerToggle = null;

    private CharSequence mDrawerTitle;
    private CharSequence mTitle;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        // call api
        try {
            run();
        } catch (Exception e) {
            // nothing
            e.printStackTrace();
        }

        mTitle = getTitle();
        mDrawerTitle = mTitle;

        // set drawer menu list
        mPlanetTitles = new String[]{"HOME","News", "Event", "Food", "Beauty"};

        // get id drawerlayout, listview
        mDrawerLayout = (DrawerLayout) findViewById(R.id.drawer_layout);
        mDrawerList = (ListView) findViewById(R.id.left_drawer);

        // set listview adapter R.layout.drawer_list_item
        mDrawerList.setAdapter(new ArrayAdapter<String>(this, R.layout.drawer_list_item, mPlanetTitles));

        // click event
        mDrawerList.setOnItemClickListener(
                new ListView.OnItemClickListener() {
                    @Override
                    public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                        ListView list = (ListView) parent;
                        String txt = (String) list.getItemAtPosition(position);
                        //Log.d(TAG, txt);
                        if(txt.equals("HOME")) {
                            showFragment(json_str);
                            // selected
                            mDrawerList.setItemChecked(position, true);

                            // change title
                            setTitle(mPlanetTitles[position]);

                            // close menu
                            mDrawerLayout.closeDrawer(mDrawerList);
                        } else {
                            selectItem(position, txt);
                        }
                    }
                }
        );

        // set home btn in action bar
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setHomeButtonEnabled(true);

        // action when click home btn
        mDrawerToggle = new ActionBarDrawerToggle(this, mDrawerLayout, R.string.drawer_open, R.string.drawer_close) {
            public void onDrawerClosed(View view) {
                getSupportActionBar().setTitle(mTitle);
                invalidateOptionsMenu(); // call onPrepareOptionsMenu
            }

            public void onDrawerOpened(View drawerView) {
                getSupportActionBar().setTitle(mDrawerTitle);
                invalidateOptionsMenu(); // call onPrepareOptionsMenu
            }
        };

        mDrawerLayout.setDrawerListener(mDrawerToggle);

    }

    /**
     * use OkHttp for getting API
     * @throws Exception
     */
    public void run() throws Exception {

        Request request = new Request.Builder()
                .url(URL)
                .build();

        client.newCall(request).enqueue(new Callback() {
            final Handler mainHandler = new Handler(Looper.getMainLooper());
            @Override public void onFailure(Call call, IOException e) {
                e.printStackTrace();
            }

            @Override public void onResponse(Call call, Response response) throws IOException {
                final String content = response.body().string();
                mainHandler.post(new Runnable() {
                    @Override
                    public void run() {
                        //change UI
                        getApi(content);
                    }
                });
            }
        });
    }

    /**
     * get api and show it to ListView
     * @param data
     */
    public void getApi(String data) {

        try {
            JSONArray json = new JSONArray(data);
            for(int i = 0; i < json.length(); i++) {
                JSONObject item = json.getJSONObject(i);
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

                // get large image
                JSONObject item5 = item.getJSONObject("_embedded");
                JSONArray arr2 = item5.getJSONArray("wp:featuredmedia");
                JSONObject item6 = arr2.getJSONObject(0)
                        .getJSONObject("media_details")
                        .getJSONObject("sizes")
                        .getJSONObject("medium_large");
                imageLarge_arr.add(item6.getString("source_url"));

            }

            // store json data
            json_str = data;

            // show fragment
            showFragment(data);

        } catch (JSONException e) {
            throw new RuntimeException(e);
        }

    }

    /**
     * show fragment
     * @param data
     */
    public void showFragment(String data) {
        Bundle args = new Bundle();
        args.putString("json", data);
        FragmentManager fm = getSupportFragmentManager();

        MainFragment mainFragment;

        Fragment f = fm.findFragmentByTag("main");

        if(f != null) {
            mainFragment = (MainFragment) f;
        }else {
            mainFragment = new MainFragment();
            mainFragment.setArguments(args);
            fm.beginTransaction().replace(R.id.fragment_main, mainFragment, "main").commit();
        }
    }

    /**
     * drawer menu list click event
     * @param position
     * @param txt
     */
    private void selectItem(int position, String txt) {
        // create fragment
        Fragment fragment = new CategoryFragment();
        Bundle args = new Bundle();
        args.putString("json", json_str);
        args.putString("category", txt);
        fragment.setArguments(args);

        // change view
        FragmentManager fragmentManager = getSupportFragmentManager();
        fragmentManager.beginTransaction().replace(R.id.fragment_main, fragment).commit();

        // selected
        mDrawerList.setItemChecked(position, true);

        // change title
        setTitle(mPlanetTitles[position]);

        // close menu
        mDrawerLayout.closeDrawer(mDrawerList);
    }

    @Override
    public boolean onPrepareOptionsMenu(Menu menu) {
        // If the nav drawer is open, hide action items related to the content view
        return super.onPrepareOptionsMenu(menu);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {

        if (mDrawerToggle.onOptionsItemSelected(item)) {
            return true;
        }

        int id = item.getItemId();

        return super.onOptionsItemSelected(item);

    }

    @Override
    public void setTitle(CharSequence title) {
        // change title
        mTitle = title;
        getSupportActionBar().setTitle(mTitle);
    }

    @Override
    protected void onPostCreate(Bundle savedInstanceState) {
        super.onPostCreate(savedInstanceState);
        mDrawerToggle.syncState();
    }

    @Override
    public void onConfigurationChanged(Configuration newConfig) {
        super.onConfigurationChanged(newConfig);
        mDrawerToggle.onConfigurationChanged(newConfig);
    }


}

