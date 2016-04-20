package com.cigsa.carlos.calcar4;

import android.graphics.Color;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.v7.app.ActionBarActivity;
import android.support.v7.app.AppCompatActivity;
import android.text.format.Time;
import android.util.Log;
import android.view.View;
import android.widget.Toast;

import com.jjoe64.graphview.DefaultLabelFormatter;
import com.jjoe64.graphview.GraphView;
import com.jjoe64.graphview.LegendRenderer;
import com.jjoe64.graphview.helper.DateAsXAxisLabelFormatter;
import com.jjoe64.graphview.helper.StaticLabelsFormatter;
import com.jjoe64.graphview.series.DataPoint;
import com.jjoe64.graphview.series.LineGraphSeries;

import org.apache.http.HttpResponse;
import org.apache.http.NameValuePair;
import org.apache.http.client.ClientProtocolException;
import org.apache.http.client.HttpClient;
import org.apache.http.client.entity.UrlEncodedFormEntity;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.impl.client.DefaultHttpClient;
import org.apache.http.message.BasicNameValuePair;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.text.DateFormat;
import java.text.NumberFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

public class DisplayEstadisticasActivity extends AppCompatActivity {

    static final long ONE_MINUTE_IN_MILLIS=60000;//millisecs
    private LineGraphSeries<DataPoint> serie_deseada;
    private LineGraphSeries<DataPoint> serie_0;
    private LineGraphSeries<DataPoint> serie_1;
    private GraphView graph;
    private double min_y;
    private double max_y;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_display_estadisticas);
        //getSupportActionBar().setDisplayHomeAsUpEnabled(true);

        graph = (GraphView) findViewById(R.id.graph);
        graph.getGridLabelRenderer().setLabelFormatter(new DateAsXAxisLabelFormatter(this, DateFormat.getTimeInstance(DateFormat.SHORT)));

        //NumberFormat nf = NumberFormat.getInstance();
        //nf.setMinimumFractionDigits(1);
        //nf.setMinimumIntegerDigits(2);

        //graph.getGridLabelRenderer().setLabelFormatter(new DefaultLabelFormatter(nf, nf));


        new MyAsyncEstadisticalTask().execute("prueba");

        /*long msTime = System.currentTimeMillis();

        LineGraphSeries<DataPoint> series = new LineGraphSeries<DataPoint>(new DataPoint[] {
                new DataPoint(new Date(msTime), 1),
                new DataPoint(new Date(msTime + (10 * ONE_MINUTE_IN_MILLIS)), 5),
                new DataPoint(new Date(msTime + (20 * ONE_MINUTE_IN_MILLIS)), 3),
                new DataPoint(new Date(msTime + (30 * ONE_MINUTE_IN_MILLIS)), 2),
                new DataPoint(new Date(msTime + (40 * ONE_MINUTE_IN_MILLIS)), 6)
        });
        graph.addSeries(series);*/
    }


    private class MyAsyncEstadisticalTask extends AsyncTask<String, Integer, Double> {

        @Override
        protected Double doInBackground(String... params) {
            // TODO Auto-generated method stub
            postData(params[0]);
            return null;
        }

        protected void onPostExecute(Double result){
            //pb.setVisibility(View.GONE);
            Toast.makeText(getApplicationContext(), "command sent", Toast.LENGTH_LONG).show();

            // legend
            serie_deseada.setTitle("deseada");
            serie_0.setTitle(getIntent().getStringExtra("name0"));
            serie_1.setTitle(getIntent().getStringExtra("name1"));
            serie_deseada.setColor(Color.BLACK);
            serie_0.setColor(Color.BLUE);
            serie_1.setColor(Color.RED);
            //serie_0.setDrawDataPoints(true);
            //serie_1.setDrawDataPoints(true);
            graph.getLegendRenderer().setVisible(true);
            graph.getLegendRenderer().setAlign(LegendRenderer.LegendAlign.BOTTOM);

            //temperatura.setText(temp);
            graph.addSeries(serie_0);
            graph.addSeries(serie_1);
            graph.addSeries(serie_deseada);

           // set manual Y bounds
           graph.getViewport().setXAxisBoundsManual(true);
           long msTime = System.currentTimeMillis();
           //new Date(msTime - (24 * 60 * ONE_MINUTE_IN_MILLIS))))
           graph.getViewport().setMaxX(msTime);
           graph.getViewport().setMinX(msTime - (24 * 60 * ONE_MINUTE_IN_MILLIS));

            graph.getViewport().setYAxisBoundsManual(true);

            int miny, maxy;


            miny = (int) (Math.round(min_y * 100.0) / 100.0);
            maxy = (int) (Math.round(max_y + 0.5)); // round up


            graph.getViewport().setMinY(miny);
            graph.getViewport().setMaxY(maxy);

            // use static labels for horizontal and vertical labels

            List<String> labelsList = new ArrayList<String>();
            int i=0;
            for(i=miny; i<=maxy; i++){
                labelsList.add(String.format("%.2f", (i * 100.0) / 100.0));
               // labelsList.add(String.format("%.2f", ((i+0.5)*100.0)/100.0));
            }
            String[] labelsArr = new String[labelsList.size()];
            labelsArr = labelsList.toArray(labelsArr);

            //String[] labelsXArr = {"00:00", "01:00", "02:00", "03:00", "04:00", "05:00", "06:00"};

            DateAsXAxisLabelFormatter dateX = new DateAsXAxisLabelFormatter(getApplicationContext(), DateFormat.getTimeInstance(DateFormat.SHORT));
            StaticLabelsFormatter staticLabelsFormatter = new StaticLabelsFormatter(graph,null,labelsArr, dateX);
            //StaticLabelsFormatter staticLabelsFormatter = new StaticLabelsFormatter(graph,labelsXArr,labelsArr, dateX);

            graph.getGridLabelRenderer().setLabelFormatter(staticLabelsFormatter);


           graph.getViewport().setScrollable(true);
           graph.getViewport().setScalable(true);

        }
        protected void onProgressUpdate(Integer... progress){
           // pb.setProgress(progress[0]);
        }

        public void postData(String valueIWantToSend) {
            // Create a new HttpClient and Post Header
            HttpClient httpclient = new DefaultHttpClient();
            String strCadConexion     = getIntent().getStringExtra("cadena");
            HttpPost httppost = new HttpPost(strCadConexion);

            try {
                // Add your data
                long msTime = System.currentTimeMillis();
                SimpleDateFormat output = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");

                List<NameValuePair> nameValuePairs = new ArrayList<NameValuePair>(3);
                nameValuePairs.add(new BasicNameValuePair("t", "temps_between"));
                nameValuePairs.add(new BasicNameValuePair("end", output.format(new Date(msTime))));
                // datos de 2 dias
                nameValuePairs.add(new BasicNameValuePair("start", output.format(new Date(msTime - (2 * 24 * 60 * ONE_MINUTE_IN_MILLIS)))));
                httppost.setEntity(new UrlEncodedFormEntity(nameValuePairs));

                // Execute HTTP Post Request
                HttpResponse response = httpclient.execute(httppost);

                ByteArrayOutputStream out = new ByteArrayOutputStream();
                response.getEntity().writeTo(out);
                out.close();

                String respuesta = out.toString();

                JSONArray reader = new JSONArray(respuesta);

                int lengthJsonArr = reader.length();

                ArrayList<DataPoint> datapoints_0 = new ArrayList<DataPoint>();
                ArrayList<DataPoint> datapoints_1 = new ArrayList<DataPoint>();
                ArrayList<DataPoint> datapoints_deseada = new ArrayList<DataPoint>();
                SimpleDateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
                String dateItem ;
                String node0 = "0.0";
                String node1 = "0.0";
                String deseada = "0.0";
                String system_on = "0";
                double dTemp = 0;

                min_y = 100;
                max_y = 0;

                for(int i=0; i < lengthJsonArr; i++)
                {
                    /****** Get Object for each JSON node.***********/
                    JSONObject jsonChildNode = reader.getJSONObject(i);

                    /******* Fetch node values **********/
                    dateItem = jsonChildNode.optString("temperature_date");
                    node0   = jsonChildNode.optString("node0");
                    node1   = jsonChildNode.optString("node1");
                    deseada = jsonChildNode.optString("desired_temperature");
                    system_on = jsonChildNode.optString("system_on");

                    if (!deseada.equals("0.0")) {
                        Date convertedDate = new Date();
                        convertedDate = dateFormat.parse(dateItem);
                        dTemp = Double.parseDouble(deseada);

                        if (system_on.equals("0"))
                            datapoints_deseada.add(new DataPoint(convertedDate, 0));
                        else
                            datapoints_deseada.add(new DataPoint(convertedDate, dTemp));

                        if (dTemp < min_y)
                            min_y = dTemp;
                        if (dTemp > max_y)
                            max_y = dTemp;
                    }
                     if (!node0.equals("0.0")) {
                         Date convertedDate = new Date();
                         convertedDate = dateFormat.parse(dateItem);
                         dTemp = Double.parseDouble(node0);
                         datapoints_0.add(new DataPoint(convertedDate, dTemp));

                         if (dTemp < min_y)
                             min_y = dTemp;
                         if (dTemp > max_y)
                             max_y = dTemp;
                     }
                    if (!node1.equals("0.0")) {
                        Date convertedDate = new Date();
                        convertedDate = dateFormat.parse(dateItem);
                        dTemp = Double.parseDouble(node1);
                        datapoints_1.add(new DataPoint(convertedDate, dTemp));

                        if (dTemp < min_y)
                            min_y = dTemp;
                        if (dTemp > max_y)
                            max_y = dTemp;
                    }

                }

                DataPoint[] dataArr_0 = datapoints_0.toArray(new DataPoint[datapoints_0.size()]);
                serie_0 = new LineGraphSeries<DataPoint>(dataArr_0);
                DataPoint[] dataArr_1 = datapoints_1.toArray(new DataPoint[datapoints_1.size()]);
                serie_1 = new LineGraphSeries<DataPoint>(dataArr_1);
                DataPoint[] dataArr_deseada = datapoints_deseada.toArray(new DataPoint[datapoints_deseada.size()]);
                serie_deseada = new LineGraphSeries<DataPoint>(dataArr_deseada);


            } catch (ClientProtocolException e) {
                // TODO Auto-generated catch block
            } catch (IOException e) {
                // TODO Auto-generated catch block
            } catch (JSONException e) {
                   e.printStackTrace();
            }
            catch (ParseException e) {
                // TODO Auto-generated catch block
                e.printStackTrace();
            }
        }

    }
}
