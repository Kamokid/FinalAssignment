package com.example.finalassignment;

import androidx.appcompat.app.AppCompatActivity;

import android.app.ProgressDialog;
import android.content.Intent;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.BaseAdapter;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import org.xmlpull.v1.XmlPullParser;
import org.xmlpull.v1.XmlPullParserException;
import org.xmlpull.v1.XmlPullParserFactory;

import java.io.IOException;
import java.io.InputStream;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.ArrayList;
import java.util.HashMap;

public class MainActivity extends AppCompatActivity {

    ListView lvRss;
    MylistAdapter myAdapter;
    ArrayList<String> titles;
    ArrayList<String> links;
    ArrayList<String> description;
    ArrayList<String> pubDate;
    ArrayList<HashMap<String, String>> elements = new ArrayList<>();


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        titles = new ArrayList<String>();
        links = new ArrayList<String>();
        description = new ArrayList<String>();
        pubDate = new ArrayList<String>();

        lvRss = (ListView) findViewById(R.id.lvRss);
        lvRss.setAdapter(myAdapter = new MylistAdapter());
        myAdapter.notifyDataSetChanged();

        lvRss.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override

            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {

                Uri uri = Uri.parse(links.get(position));
                Intent intent = new Intent(Intent.ACTION_VIEW, uri);
                startActivity(intent);

            }
        });

        new RssExtract().execute();
    }

    public class RssExtract extends AsyncTask<Integer, Void, Exception> {

        ProgressDialog progressDialog = new ProgressDialog(MainActivity.this);

        Exception exception = null;


        @Override
        protected void onPreExecute() {
            super.onPreExecute();
            progressDialog.setMessage("Busy loading rss feed...please wait");
            progressDialog.show();
        }

        @Override
        protected Exception doInBackground(Integer... param) {
            try {
                HandlerHTTP sh = new HandlerHTTP();

                InputStream com = sh.makeServiceCall("https://feeds.bbci.co.uk/news/world/us_and_canada/rss.xml");

                XmlPullParserFactory factory = XmlPullParserFactory.newInstance();

                factory.setNamespaceAware(false);

                XmlPullParser xpull = factory.newPullParser();

                xpull.setInput(com, "UTF-8");

                boolean insideItem = false;

                int eventType = xpull.getEventType();

                int position = 0;

                while (eventType != XmlPullParser.END_DOCUMENT) {
                    if (eventType == XmlPullParser.START_TAG) {
                        if (xpull.getName().equalsIgnoreCase("item")) {
                            insideItem = true;
                        } else if (xpull.getName().equalsIgnoreCase("title")) {
                            if (insideItem) {
                                titles.add(xpull.nextText());
                            }
                        } else if (xpull.getName().equalsIgnoreCase("link")) {
                            if (insideItem) {
                                links.add(xpull.nextText());
                            }
                        } else if (xpull.getName().equalsIgnoreCase("description")) {
                            if (insideItem) {
                                description.add(xpull.nextText());
                            }
                        } else if (xpull.getName().equalsIgnoreCase("pubDate")) {
                            if (insideItem) {
                                pubDate.add(xpull.nextText());
                            }
                        }

                    } else if (eventType == XmlPullParser.END_TAG && xpull.getName().equalsIgnoreCase("item")) {
                        HashMap<String, String> items = new HashMap<>();
                        items.put("title", titles.get(position));
                        items.put("description", description.get(position));
                        items.put("pubDate", pubDate.get(position));
                        elements.add(items);
                        position = position + 1;
                        insideItem = false;
                    }

                    eventType = xpull.next();
                }
            } catch (MalformedURLException e) {
                exception = e;
            } catch (XmlPullParserException e) {
                exception = e;
            } catch (IOException e) {
                exception = e;
            }
            return exception;
        }

        @Override
        protected void onPostExecute(Exception s) {
            super.onPostExecute(s);

            //  ArrayAdapter<String> adapter = new ArrayAdapter<>(MainActivity.this,
            //        android.R.layout.simple_expandable_list_item_1, titles);

            //ArrayAdapter<HashMap<String,String>> element = new ArrayAdapter<>(MainActivity.this
            //, android.R.layout.simple_list_item_1, elements);
            //  lvRss.setAdapter(element);
            progressDialog.dismiss();
        }

    }

    private class MylistAdapter extends BaseAdapter {

        @Override
        public int getCount() {
            return elements.size();
        }

        @Override
        public Object getItem(int position) {
            return elements.get(position).get("title");
        }

        public Object getDescription(int position){
            return elements.get(position).get("description");
        }

        public Object getPubDate(int position){
            return elements.get(position).get("pubDate");
        }

        @Override
        public long getItemId(int position) {
            return (long) position;
        }

        @Override
        public View getView(int i, View old, ViewGroup parent) {
            ProgressDialog progressDialog = new ProgressDialog(MainActivity.this);
            progressDialog.setMessage("Busy loading rss feed...please wait");
            progressDialog.show();


            View newView = old;
            LayoutInflater inflater = getLayoutInflater();

            //make a new row:
            if (newView == null) {
                newView = inflater.inflate(R.layout.row_layout, parent, false);
            }

            //set what the text should be for this row:
            TextView tView = newView.findViewById(R.id.title);
            tView.setText(getItem(i).toString());
            TextView tView1 = newView.findViewById(R.id.description);
            tView1.setText(getDescription(i).toString());
            TextView tView2 = newView.findViewById(R.id.date);
            tView2.setText(getPubDate(i).toString());

             Toast.makeText(getApplicationContext(),"Loading", Toast.LENGTH_SHORT).show();

            progressDialog.dismiss();
            View finalNewView = newView;
            return finalNewView;


        }
    }
}