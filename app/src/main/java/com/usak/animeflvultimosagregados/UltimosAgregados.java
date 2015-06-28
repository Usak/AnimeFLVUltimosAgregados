package com.usak.animeflvultimosagregados;

import android.os.AsyncTask;
import android.os.Bundle;
import android.os.Handler;

import android.support.v4.widget.SwipeRefreshLayout;
import android.support.v7.app.ActionBarActivity;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.InputStreamReader;

import java.net.HttpURLConnection;
import java.net.URL;

import java.util.ArrayList;
import java.util.List;
import android.util.Log;

public class UltimosAgregados extends ActionBarActivity implements SwipeRefreshLayout.OnRefreshListener {

    private final String url = "https://www.kimonolabs.com/api/cogu9rj8?apikey=a4497d853ceb044be7b52a41e72dd838";
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
        mRecyclerView.setLayoutManager(new LinearLayoutManager(this));
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
                if (adapter == null) {
                    adapter = new MyRecyclerAdapter(UltimosAgregados.this, itemList);
                    mRecyclerView.setAdapter(adapter);
                } else {
                    adapter.notifyDataSetChanged();
                }
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

                JSONArray agregados = response.optJSONObject("results").optJSONArray("Últimos agregados");

                if (null == itemList) {
                    itemList = new ArrayList<>();
                }

                for (int i = 0; i < agregados.length(); i++) {
                    JSONObject agregado = agregados.getJSONObject(i);
                    Item item = new Item();
                    item.setTitle(agregado.getJSONObject("title").optString("text"));
                    item.setThumbnail(agregado.getJSONObject("thumbnail").optString("src"));
                    itemList.add(item);
                }
            } catch (JSONException e) {
                e.printStackTrace();
            }
        }
    }

}
