package com.timbuchalka.top10downloader;

import android.os.AsyncTask;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.ListView;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;

public class MainActivity extends AppCompatActivity {

    private static final String TAG = "MainActivity";
    private ListView listApps;
    private String feedURL = "http://ax.itunes.apple.com/WebObjects/MZStoreServices.woa/ws/RSS/topfreeapplications/limit=10/xml";
    private int feedLimit = 10;

    private String feedCachedUrl = "INVALIDATED";
    private static final String FEED_URL = "FeedURL";
    private static final String FEED_LIMIT = "FeedLimit";
    
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        listApps = (ListView)findViewById(R.id.xmlListView);

        if(savedInstanceState!= null)
        {
            feedURL = savedInstanceState.getString(FEED_URL);
            feedLimit = savedInstanceState.getInt(FEED_LIMIT);
        }
        downloadUrl(String.format(feedURL,feedLimit));
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.feeds_menu,menu);
        if(feedLimit == 10)
        {
            menu.findItem(R.id.mnu10).setChecked(true);
        }
        else
        {
            menu.findItem(R.id.mnu25).setChecked(true);
        }
        return super.onCreateOptionsMenu(menu);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        int id = item.getItemId();
        //String feedURL;

        switch (id) {
            case R.id.mnuFree:
                feedURL = "http://ax.itunes.apple.com/WebObjects/MZStoreServices.woa/ws/RSS/topfreeapplications/limit=%d/xml";
                break;
            case R.id.mnuPaid:
                feedURL = "http://ax.itunes.apple.com/WebObjects/MZStoreServices.woa/ws/RSS/toppaidapplications/limit=%d/xml";
                break;
            case R.id.mnuSongs:
                feedURL = "http://ax.itunes.apple.com/WebObjects/MZStoreServices.woa/ws/RSS/topsongs/limit=%d/xml";
                break;
            case R.id.mnu10:
            case R.id.mnu25:
                if(!item.isChecked())
                {
                    item.setChecked(true);
                    feedLimit = 35 - feedLimit;
                    Log.d(TAG, "onOptionsItemSelected: " + item.getTitle() + " setting feedLimit to " + feedLimit);
                }
                else
                {
                    Log.d(TAG, "onOptionsItemSelected: " + item.getTitle() + " feedLimit unchanged");
                }
                break;
            case R.id.mnuRefresh:
                feedCachedUrl = "INVALIDATED";
                break;
            default:
                return super.onOptionsItemSelected(item);
        }

        downloadUrl(String.format(feedURL,feedLimit));
        return true;
    }

    private void downloadUrl(String feedURL) {
        if(!feedURL.equals(feedCachedUrl)) {
            Log.d(TAG, "onCreate: starting asynctask.");
            DownloadData downloadData = new DownloadData();
            //downloadData.execute("http://ax.itunes.apple.com/WebObjects/MZStoreServices.woa/ws/RSS/topfreeapplications/limit=10/xml");
            downloadData.execute(feedURL);
            feedCachedUrl = feedURL;
            Log.d(TAG, "onCreate: done");
        }
        else
        {
            Log.d(TAG, "downloadUrl: URL not changed");
        }
    }


    private class DownloadData extends AsyncTask<String,Void,String>
    {
        private static final String TAG = "DownloadData";
        
        @Override
        protected String doInBackground(String... strings) {
            Log.d(TAG, "doInBackground: starts with " + strings[0]);
            String rssFeed = downloadXML(strings[0]);
            if(rssFeed == null){
                Log.e(TAG, "doInBackground: Error downloading");
            }
            return rssFeed;
            //return "doInBackground completed.";
        }


        @Override
        protected void onPostExecute(String s) {
            super.onPostExecute(s);
            Log.d(TAG, "onPostExecute: parameter is " + s);
            ParseApplications parseApplications = new ParseApplications();
            parseApplications.parse(s);

            FeedAdapter<FeedEntry> feedAdapter = new FeedAdapter<>(MainActivity.this,R.layout.list_record,parseApplications.getApplications());
            listApps.setAdapter(feedAdapter);

            //ArrayAdapter<FeedEntry> arrayAdapter = new ArrayAdapter<FeedEntry>(MainActivity.this,R.layout.list_item,parseApplications.getApplications());
            //listApps.setAdapter(arrayAdapter);
        }
    }

    private String downloadXML(String urlPath)
    {
        StringBuilder xmlResult = new StringBuilder();

        try{
            URL url = new URL(urlPath);
            HttpURLConnection connection = (HttpURLConnection)url.openConnection();
            int response = connection.getResponseCode();
            Log.d(TAG, "downloadXML: The response code was "+ response);

            //InputStream inputStream = connection.getInputStream();
            //InputStreamReader inputStreamReader = new InputStreamReader(inputStream);
            //BufferedReader reader = new BufferedReader(inputStreamReader);

            BufferedReader reader = new BufferedReader(new InputStreamReader(connection.getInputStream()));

            int charsRead;
            char[] inputBuffer = new char[500];
            while(true)
            {
                charsRead = reader.read(inputBuffer);
                if(charsRead < 0) break;
                if(charsRead > 0) xmlResult.append(String.copyValueOf(inputBuffer,0,charsRead));
            }
            reader.close();
            return xmlResult.toString();
        }
        catch (MalformedURLException e)
        {
            Log.e(TAG, "downloadXML: Invalid URL: " + e.getMessage());
        }
        catch (IOException e)
        {
            Log.e(TAG, "downloadXML: IO exception reading data: " + e.getMessage());
        }
        catch (SecurityException e)
        {
            Log.e(TAG, "downloadXML: Security Exception. Needs permission?");
            e.printStackTrace();
        }

        return null;
    }

    @Override
    protected void onSaveInstanceState(Bundle outState) {
        outState.putString(FEED_URL, feedURL);
        outState.putInt(FEED_LIMIT, feedLimit);
        super.onSaveInstanceState(outState);
    }

//    @Override
//    protected void onRestoreInstanceState(Bundle savedInstanceState) {
//        super.onRestoreInstanceState(savedInstanceState);
//        feedURL = savedInstanceState.getString(FEED_URL);
//        feedLimit = savedInstanceState.getInt(FEED_LIMIT);
//    }
}


