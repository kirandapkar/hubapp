package com.example.asus.outlab9;

import android.content.Intent;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ListView;
import android.widget.ProgressBar;
import android.widget.Toast;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.ArrayList;


public class SearchActivity extends AppCompatActivity {
    ArrayList<String> mylist ;

    ListView lv ;
    EditText searchKey;

    ArrayAdapter<String> arrayAdapter;

    ProgressBar progressBar;

    Button queryButton ;

    static final String API_URL = "https://api.github.com/search/users?q=";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_search);
        mylist = new ArrayList<String>();

        lv = (ListView) findViewById(R.id.list1);

        searchKey = (EditText) findViewById(R.id.searchkey);
        progressBar = (ProgressBar) findViewById(R.id.progressBar);

        Button queryButton = (Button) findViewById(R.id.queryButton);
        queryButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                new RetrieveFeedTask().execute();
            }
        });

    }

    class RetrieveFeedTask extends AsyncTask<Void, Void, String> {

        private Exception exception;

        protected void onPreExecute() {
            progressBar.setVisibility(View.VISIBLE);

        }

        protected String doInBackground(Void... urls) {
            String key = searchKey.getText().toString();

            try {
                URL url = new URL(API_URL + key + "&sort=repositories&order=desc");
                HttpURLConnection urlConnection = (HttpURLConnection) url.openConnection();
                try {
                    BufferedReader bufferedReader = new BufferedReader(new InputStreamReader(urlConnection.getInputStream()));
                    StringBuilder stringBuilder = new StringBuilder();
                    String line;
                    while ((line = bufferedReader.readLine()) != null) {
                        stringBuilder.append(line).append("\n");
                    }
                    bufferedReader.close();
                    return stringBuilder.toString();
                }
                finally{
                    urlConnection.disconnect();
                }
            }
            catch(Exception e) {
                Log.e("ERROR", e.getMessage(), e);
                return null;
            }
        }

        protected void onPostExecute(String response) {
            if(response == null) {
                Toast.makeText(getApplicationContext(), "There was an error in extracting JSON Data",
                        Toast.LENGTH_LONG).show();
            }
            progressBar.setVisibility(View.GONE);
            Log.i("INFO", response);

            try {

               JSONObject root = new JSONObject(response);

            JSONArray namesArray = root.getJSONArray("items");

                mylist = new ArrayList<>();
                for(int i=0; i<namesArray.length(); i++){

            JSONObject firstname = namesArray.getJSONObject(i);

            String name = firstname.getString("login");


                mylist.add(name); }}
            catch (JSONException e) {
                e.printStackTrace();
            }

            Toast.makeText(getApplicationContext(), "Completed Search",
                    Toast.LENGTH_SHORT).show();

           arrayAdapter = new ArrayAdapter<String>(
                    getApplicationContext(),
                    android.R.layout.simple_list_item_1, mylist
                     );
            lv.setAdapter(arrayAdapter);
            lv.setOnItemClickListener(new AdapterView.OnItemClickListener() {
                @Override
                public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                    Intent intent = new Intent(getApplicationContext(), UserActivity.class);
                    String text= (String) lv.getItemAtPosition(position);
                    intent.putExtra("userid",text);
                    getApplicationContext().startActivity(intent);

                }
            });
        }
    }
}