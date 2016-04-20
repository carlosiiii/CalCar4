package com.cigsa.carlos.calcar4;

import android.content.Intent;
import android.content.SharedPreferences;
import android.os.AsyncTask;
import android.os.Bundle;
import android.preference.Preference;
import android.preference.PreferenceActivity;
import android.preference.PreferenceFragment;
import android.preference.PreferenceManager;
import android.view.View;
import android.widget.Toast;

import org.apache.http.HttpResponse;
import org.apache.http.NameValuePair;
import org.apache.http.client.ClientProtocolException;
import org.apache.http.client.HttpClient;
import org.apache.http.client.entity.UrlEncodedFormEntity;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.impl.client.DefaultHttpClient;
import org.apache.http.message.BasicNameValuePair;
import org.apache.http.params.BasicHttpParams;
import org.apache.http.params.HttpConnectionParams;
import org.apache.http.params.HttpParams;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;


public class UserSettingPiActivity extends PreferenceActivity
{
    static private String hysteresis;
    static private String max_temp;
    static private String min_temp;
    static private String calibration;

    @Override
    protected void onCreate(Bundle savedInstanceState)
    {
        super.onCreate(savedInstanceState);

        if (savedInstanceState == null) {
            getFragmentManager().beginTransaction().replace(android.R.id.content, new MyPreferenceFragment()).commit();
        }

         hysteresis   = String.format("%.2f", getIntent().getDoubleExtra("hysteresis", 0.0)).replace(',', '.');
         max_temp     = String.format("%.2f", getIntent().getDoubleExtra("max_temp", 0.0)).replace(',','.');
         min_temp     = String.format("%.2f", getIntent().getDoubleExtra("min_temp", 0.0)).replace(',', '.');
        calibration   = String.format("%.2f", getIntent().getDoubleExtra("calibration", 0.0)).replace(',','.');

        // set texts correctly
        SharedPreferences prefs = PreferenceManager.getDefaultSharedPreferences(getBaseContext());
        SharedPreferences.Editor editor = prefs.edit();
        editor.putString("hysteresis", hysteresis);
        editor.putString("max_temp", max_temp);
        editor.putString("min_temp", min_temp);
        editor.putString("calibration", calibration);
        editor.commit();

        // checkValues();
    }


    public class MyPreferenceFragment extends PreferenceFragment
            implements SharedPreferences.OnSharedPreferenceChangeListener
    {
        @Override
        public void onCreate(final Bundle savedInstanceState)
        {
            super.onCreate(savedInstanceState);
            addPreferencesFromResource(R.xml.settings_pi);

            // set texts correctly

            findPreference("hysteresis").setSummary(hysteresis);
            findPreference("max_temp").setSummary(max_temp);
            findPreference("min_temp").setSummary(min_temp);
            findPreference("calibration").setSummary(calibration);
        }

        @Override
        public void onResume() {
            super.onResume();
            // Set up a listener whenever a key changes
            getPreferenceScreen().getSharedPreferences().registerOnSharedPreferenceChangeListener(this);
        }

        @Override
        public void onPause() {
            super.onPause();
            // Set up a listener whenever a key changes
            getPreferenceScreen().getSharedPreferences().unregisterOnSharedPreferenceChangeListener(this);
        }


        @Override
        public void onSharedPreferenceChanged(SharedPreferences sharedPreferences, String key) {
            if (key.equals("hysteresis") ||
                    key.equals("max_temp") ||
                    key.equals("min_temp") ||
                    key.equals("calibration")) {
                Preference pref = findPreference(key);
                pref.setSummary(sharedPreferences.getString(key, "").replace(',', '.'));

                new MyAsyncSettingPiTask().execute(key,sharedPreferences.getString(key, "").replace(',','.') );
            }
        }
    }

    private void checkValues()
    {
        SharedPreferences sharedPrefs = PreferenceManager.getDefaultSharedPreferences(getBaseContext());
        String hysteresis= sharedPrefs.getString("hysteresis", "NA");
        String max_temp = sharedPrefs.getString("max_temp", "NA");
        String min_temp = sharedPrefs.getString("min_temp", "NA");
        String calibration = sharedPrefs.getString("calibration", "NA");

        String msg = "Cur Values: ";
        msg += "\n hysteresis = " + hysteresis;
        msg += "\n max_temp = " + max_temp;
        msg += "\n min_temp = " + min_temp;
        msg += "\n calibration = " + calibration;

        Toast.makeText(getApplicationContext(), msg, Toast.LENGTH_LONG).show();

    }


    private class MyAsyncSettingPiTask extends AsyncTask<String , Integer, Double> {

        @Override
        protected Double doInBackground(String... params) {
            // TODO Auto-generated method stub
            postData(params[0], params[1]);
            return null;
        }

        protected void onPostExecute(Double result){
            //pb.setVisibility(View.GONE);
             Toast.makeText(getApplicationContext(), "command sent", Toast.LENGTH_LONG).show();
        }
        protected void onProgressUpdate(Integer... progress){
           // pb.setProgress(progress[0]);
        }

        public void postData(String key, String value) {

            // Create a new HttpClient and Post Header
            HttpClient httpclient = new DefaultHttpClient();
            String strCadConexion     = getIntent().getStringExtra("cadena");
            HttpPost httppost = new HttpPost(strCadConexion);

            try{

            List<NameValuePair> nameValuePairs = new ArrayList<NameValuePair>(3);
            nameValuePairs.add(new BasicNameValuePair("t", "update_setting"));
            nameValuePairs.add(new BasicNameValuePair("key", key));
            nameValuePairs.add(new BasicNameValuePair("value", value));
            httppost.setEntity(new UrlEncodedFormEntity(nameValuePairs));

            // Execute HTTP Post Request
            HttpResponse response = httpclient.execute(httppost);

            ByteArrayOutputStream out = new ByteArrayOutputStream();
            response.getEntity().writeTo(out);
            out.close();

            String respuesta = out.toString();

            } catch (ClientProtocolException e) {
                // TODO Auto-generated catch block
                e.printStackTrace();
                Toast.makeText(getApplicationContext(),  e.getMessage(), Toast.LENGTH_LONG).show();
            } catch (IOException e) {
                // TODO Auto-generated catch block
                e.printStackTrace();
                Toast.makeText(getApplicationContext(),  e.getMessage(), Toast.LENGTH_LONG).show();
            } catch(Exception e) {
                e.printStackTrace();
                Toast.makeText(getApplicationContext(),  e.getMessage(), Toast.LENGTH_LONG).show();
            }

        }

    }
}
