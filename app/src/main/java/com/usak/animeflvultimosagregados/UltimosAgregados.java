package com.usak.animeflvultimosagregados;

import android.content.Intent;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.v4.widget.SwipeRefreshLayout;
import android.support.v7.app.ActionBarActivity;
import android.support.v7.widget.GridLayoutManager;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.View;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.ArrayList;
import java.util.List;

public class UltimosAgregados extends ActionBarActivity implements SwipeRefreshLayout.OnRefreshListener, MyRecyclerAdapter.ClickListener {

    //private final String url = "https://www.kimonolabs.com/api/cogu9rj8?apikey=a4497d853ceb044be7b52a41e72dd838";
    private final String url = "https://api.import.io/store/data/7780d46a-523c-4962-8140-863cfa35bc75/_query?input/webpage/url=http%3A%2F%2Fanimeflv.net%2F&_user=be69f215-fd2b-4cc9-961d-d03e7257a59f&_apikey=be69f215fd2b4cc9961dd03e7257a59f2a40440f7b649484c91706d42980e16e3f2b9677b37e56523d180e0b6345ab453c197a2c224ef72686f52dd697f6d03f80a224035e1739d3c104b996db9a3d68";
    private static final String TAG = "UltimosAgregados";
    private List<Item> itemList = new ArrayList<>();

    private RecyclerView mRecyclerView;
    private SwipeRefreshLayout mSwipeRefreshLayout;
    private MyRecyclerAdapter adapter;
    private Toolbar toolbar;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_ultimos_agregados);

        toolbar = (Toolbar)findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        getSupportActionBar().setTitle("AnimeFLV");
        toolbar.setTitleTextColor(getResources().getColor(android.R.color.white));

        mRecyclerView = (RecyclerView) findViewById(R.id.recycler_view);

      //  mRecyclerView.setLayoutManager(new LinearLayoutManager(this));

        mRecyclerView.setLayoutManager(new GridLayoutManager(this, 2, GridLayoutManager.VERTICAL, false));
        mRecyclerView.setHasFixedSize(true);
        mSwipeRefreshLayout = (SwipeRefreshLayout)findViewById(R.id.swipeRefreshLayout);
        mSwipeRefreshLayout.setOnRefreshListener(this);
        mSwipeRefreshLayout.setColorSchemeResources(android.R.color.holo_blue_dark,
                android.R.color.holo_orange_light,
                android.R.color.holo_green_dark,
                android.R.color.holo_red_light);

        new AsyncHttpTask().execute(url);

    }

    @Override
    public void onRefresh() {
        itemList.clear();
        new AsyncHttpTask().execute(url);
        mSwipeRefreshLayout.setRefreshing(false);
    }

    @Override
    public void itemClicked(View view, int i) {
        startActivity(new Intent(Intent.ACTION_VIEW).setData(Uri.parse(itemList.get(i).getUrl())));
    }

    public class AsyncHttpTask extends AsyncTask<String, Void, Integer> {
        @Override
        protected void onPreExecute() {
            setProgressBarIndeterminateVisibility(true);
        }

        @Override
        protected void onPostExecute(Integer result) {
            setProgressBarIndeterminateVisibility(false);
                /* Download complete. Lets update UI */
            if (result == 1) {
                adapter = new MyRecyclerAdapter(UltimosAgregados.this, itemList);
                mRecyclerView.setAdapter(adapter);
                adapter.setClickListener(UltimosAgregados.this);
            } else {
                Log.e(TAG, "Failed to fetch data!");
            }
        }

        @Override
        protected Integer doInBackground(String... params) {
            Integer result = 0;
            HttpURLConnection urlConnection;

            try {
                URL url = new URL(params[0]);
                urlConnection = (HttpURLConnection) url.openConnection();

                urlConnection.setRequestMethod("GET");
                int statusCode = urlConnection.getResponseCode();

                if (statusCode == 200) {
                    BufferedReader r = new BufferedReader(new InputStreamReader(urlConnection.getInputStream()));
                    StringBuilder response = new StringBuilder();
                    String line;
                    while ((line = r.readLine()) != null) {
                        response.append(line);
                    }

                    parseResult(response.toString());
                    result = 1;
                }else{
                    result = 0;
                }

            } catch (Exception e) {
                Log.d(TAG, e.getLocalizedMessage());
            }
            return result;
        }

        private void parseResult(String result) {
            try {
                JSONObject response = new JSONObject(result);

                JSONArray agregados = response.optJSONArray("results");

                if (null == itemList) {
                itemList = new ArrayList<>();
            }

            for (int i = 0; i < agregados.length(); i++) {
                JSONObject agregado = agregados.getJSONObject(i);
                Item item = new Item();
                item.setTitle(agregado.optString("episode/_title"));
                item.setThumbnail(agregado.optString("thumbnail"));
                item.setUrl(agregado.optString("episode"));
                itemList.add(item);
            }
        } catch (JSONException e) {
                e.printStackTrace();
            }
        }
    }

}
