package com.example.kitadaharuka.blog;

import android.app.ActionBar;
import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.MenuItem;
import android.view.View;
import android.webkit.WebView;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;


public class HtmlActivity extends AppCompatActivity {
    private static final String TAG = "HtmlActivity";
    private int position;
    private String json = "";
    private WebView webView;
    private String title;
    private String category;
    private String content;
    private String image;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_html);

        Intent intent = getIntent();
        position = intent.getIntExtra("position", 0);
        json = intent.getStringExtra("json");

        webView = (WebView) findViewById(R.id.web_view);

        // parse json
        try {
            JSONArray data = new JSONArray(json);
            JSONObject item = data.getJSONObject(position);

            // title
            JSONObject item2 = item.getJSONObject("title");
            title = item2.getString("rendered");

            // category
            category = item.getString("category_name");

            // content
            JSONObject item3 = item.getJSONObject("content");
            content = item3.getString("rendered");
            Log.d(TAG, "content" + content);

            // image
            JSONObject item4 = item.getJSONObject("_embedded");
            JSONArray arr = item4.getJSONArray("wp:featuredmedia");
            JSONObject item5 = arr.getJSONObject(0)
                    .getJSONObject("media_details")
                    .getJSONObject("sizes")
                    .getJSONObject("medium_large");
            image = item5.getString("source_url");

        } catch (JSONException e) {
            throw new RuntimeException(e);
        }

        loadHTML();
    }

    /**
     * Load html
     */
    private void loadHTML() {
        String html = createHtml();

        webView.loadDataWithBaseURL("file:///android_asset/", html, "text/html", "utf−8", null);
    }

    /**
     * create html code
     * @return html code
     */
    protected String createHtml() {
        StringBuilder htmlBuilder = new StringBuilder();

        htmlBuilder.append("<html>");
        htmlBuilder.append("<head>");

        // リセットするアレ
        htmlBuilder.append("<link rel=\"stylesheet\" type=\"text/css\" href=\"file:///android_asset/css/reset.css\">");
        htmlBuilder.append("<link rel=\"stylesheet\" type=\"text/css\" href=\"file:///android_asset/css/style.css\">");

        // HTML 読み込んだら全文表示のスクリプト
        htmlBuilder.append("<script>window.onload=function(){var html=document.getElementsByTagName('html');console.log(html[0].outerHTML);}</script>");

        htmlBuilder.append("</head>");
        htmlBuilder.append("<body>");

        htmlBuilder.append("<p class=\"main_image\">");
        htmlBuilder.append("<img src=\""+ image +"\" alt=\""+ title +"\">");
        htmlBuilder.append("</p>");

        htmlBuilder.append("<div class=\"main_content\">");
        htmlBuilder.append("<p class=\"category "+ category +"\">");
        htmlBuilder.append("<span>"+ category +"</span>");
        htmlBuilder.append("</p>");
        htmlBuilder.append("<h1>"+ title +"</h1>");
        htmlBuilder.append("<div class=\"content\">"+ content +"</div>");
        htmlBuilder.append("</div>");

        htmlBuilder.append("</body>");
        htmlBuilder.append("</html>");

        return htmlBuilder.toString();
    }
}
