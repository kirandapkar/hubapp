package com.example.asus.outlab9;

import android.content.Intent;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.widget.ListAdapter;
import android.widget.ListView;
import android.widget.SimpleAdapter;
import android.widget.TextView;
import android.widget.Toast;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.HashMap;

public class UserActivity extends AppCompatActivity {
    final String API_URL = "https://api.github.com/users/";
    Intent intent1;
    String searchname;
    ListView lv;

    ArrayList<HashMap<String, String>> contactList;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_user);
        intent1= getIntent();
        searchname = intent1.getStringExtra("userid");
        new RetrieveRepos().execute();
        contactList = new ArrayList<>();
        lv= (ListView) findViewById(R.id.list1);;
        new RetrieveDetails().execute();
    }



        class RetrieveRepos extends AsyncTask<Void, Void, String> {

            private Exception exception;

            protected void onPreExecute() {
            }

            protected String doInBackground(Void... urls) {

                try {
                    URL url = new URL(API_URL + searchname);
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
                    } finally {
                        urlConnection.disconnect();
                    }
                } catch (Exception e) {
                    Log.e("ERROR", e.getMessage(), e);
                    return null;
                }
            }

            protected void onPostExecute(String response) {
                if (response == null) {
                    Toast.makeText(getApplicationContext(), "There was an error in extracting JSON Data",
                            Toast.LENGTH_LONG).show();
                }
                Log.i("INFO", response);

                try {

                    JSONObject root = new JSONObject(response);

                    String user_name = root.getString("name");

                    String company_name = root.getString("company"); // basketball

                    String location = root.getString("location");

                    TextView nameV = (TextView) findViewById(R.id.nameText);
                    TextView compV = (TextView) findViewById(R.id.companyText);
                    TextView locV = (TextView) findViewById(R.id.locationText);

                    nameV.setText("Name: " + user_name);
                    compV.setText("Company: " + company_name);
                    locV.setText("Location: " + location);

                } catch (JSONException e) {
                    e.printStackTrace();
                }
            }
        }

            class RetrieveDetails extends AsyncTask<Void, Void, String> {

                private Exception exception;

                protected void onPreExecute() {
                }

                protected String doInBackground(Void... urls) {

                    try {
                        URL url = new URL(API_URL + searchname+"/repos");
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
                        response = "THERE WAS AN ERROR";
                    }
                    Log.i("INFO", response);

                    try {
                        JSONArray jarry = new JSONArray(response);

                        for(int i=0; i<jarry.length(); i++){

                            JSONObject abcd = jarry.getJSONObject(i);
                            String desc = abcd.getString("description");
                            String time = abcd.getString("created_at");
                            String repname = abcd.getString("name");


                            int year = Integer.parseInt(time.substring(0,4));
                            int month = Integer.parseInt(time.substring(5,7));
                            int day = Integer.parseInt(time.substring(8,10));

                            Date c = Calendar.getInstance().getTime();
                            SimpleDateFormat df = new SimpleDateFormat("ddMMyyyy");
                            String formattedDate = df.format(c);

                            int cy = Integer.parseInt(formattedDate.substring(4,8));
                            int cm = Integer.parseInt(formattedDate.substring(2,4));
                            int cd = Integer.parseInt(formattedDate.substring(0,2));

                            double passeddays=365.25*(cy-year)+365.25/12.0 * (cm- month) + cd-day;

                            int years1=(int) (passeddays/365.25);
                            int months1=(int) ((passeddays-years1*365.25)*12.0/365.25);
                            int days1= (int) (passeddays-years1*365.25 - months1*365.25/12.0);

                            String years2 = String.format("%02d", years1);
                            String months2 = String.format("%02d", months1);
                            String days2 = String.format("%02d", days1);

                            String timedash = years2+" years, \n"+months2+" months, \n"+days2+" days ";

                            HashMap <String, String> contact = new HashMap();
                            contact.put("Description", desc);
                            contact.put("Time", timedash);
                            contact.put("RepositoryName", repname);
                            contactList.add(contact);
                        }



                    }
                    catch (JSONException e) {
                        e.printStackTrace();
                    }


               ListAdapter arrayAdapter = new SimpleAdapter(
                        getApplicationContext(),
                        contactList, R.layout.list_item, new String[]{"RepositoryName",
               "Time","Description" }, new int[]{R.id.reponame, R.id.repoage, R.id.descript});


                lv.setAdapter(arrayAdapter);

                }
        }
    }

