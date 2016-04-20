package com.cigsa.carlos.calcar4;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.drawable.AnimationDrawable;
import android.net.NetworkInfo;
import android.net.wifi.WifiInfo;
import android.net.wifi.WifiManager;
import android.preference.PreferenceManager;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.os.Handler;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.text.DecimalFormat;
import java.text.DecimalFormatSymbols;
import java.text.NumberFormat;
import java.text.SimpleDateFormat;
import java.util.Locale;
import java.util.Timer;
import java.util.TimerTask;
import java.util.concurrent.TimeUnit;

import org.apache.http.HttpResponse;
import org.apache.http.client.ClientProtocolException;
import org.apache.http.client.HttpClient;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.impl.client.DefaultHttpClient;
import org.apache.http.params.BasicHttpParams;
import org.apache.http.params.HttpConnectionParams;
import org.apache.http.params.HttpParams;
import org.json.JSONException;
import org.json.JSONObject;

import android.os.AsyncTask;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.animation.AlphaAnimation;
import android.view.animation.Animation;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;



public class Carlcar4MainActivity extends AppCompatActivity  implements OnClickListener  {

    private Button btn;
    private ProgressBar pb;
    private ImageView ivNodo0Up;
    private ImageView ivNodo0Down;
    private ImageView ivNodo1Up;
    private ImageView ivNodo1Down;
    private ImageView ivLock;
    private ImageView ivUnLock;
    private ImageView ivPower;
    private ImageView ivFlash;
    private ImageView ivWarning;
    private TextView tvTemperatura0;
    private TextView tvTemperatura1;
    private TextView tvHumedad0;
    private TextView tvHumedad1;
    private TextView tvNombre0;
    private TextView tvNombre1;
    private TextView tvFecha0;
    private TextView tvFecha1;
    private TextView tvPendienteCliente;
    private TextView tvPendienteHora;
    private TextView tvPendienteComando;
    private TextView tvSistema;
    private TextView tvDeseada;
    private TextView tvNombreDeseada;
    private TextView tvWorkMode;
    private TextView tvHysteresis;
    private final Animation anim =  new AlphaAnimation(0.0f, 1.0f);
    private boolean bBlinking;
    private boolean bCambioMode;
    //private TextView tv
    private String strDateAct0;
    //private double dAct0;
    private double dTemp0Ant;
    private double dTemp0;
    private double dTemp1Ant;
    private double dTemp1;
    private String strHum0;
    private String strHum1;
    private String strName0;
    private String strName1;
    private String strDate0;
    private String strDate1;
    private String strSystemOn;
    private String strHeating;
    private String strPendienteComando;
    private String strPendienteCliente;
    private String strPendienteHora;
    private String strLastCompletedCommand;
    private double dDeseada;
    private String strNombreDeseada;
    private String strWorkMode; //="manual"; //getString(R.string.str_manual);
    private String strHysteresis;

    private double dHysteresis;
    private double dMaxTemp;
    private double dMinTemp;
    private double dCalibration;
    private int iTimer = 60000;
    //private char decSeparator;

    private  AnimationDrawable frameAnimation;

    Timer timer;
    TimerTask timerTask;

    //we are going to use a handler to be able to run in our TimerTask
    final Handler handler = new Handler();


    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_carlcar7);
        btn=(Button)findViewById(R.id.button1);
        pb=(ProgressBar)findViewById(R.id.progressBar1);
        pb.setVisibility(View.GONE);

        //NumberFormat nf = NumberFormat.getInstance();
        //if(nf instanceof DecimalFormat) {
            //DecimalFormatSymbols sym = ((DecimalFormat) nf).getDecimalFormatSymbols();
            // decSeparator = sym.getDecimalSeparator();
        //}

        strWorkMode = getString(R.string.str_manual);

        ivNodo0Up=(ImageView)findViewById(R.id.nodo0Up);
        ivNodo0Up.setVisibility(View.GONE);

        ivNodo0Down=(ImageView)findViewById(R.id.nodo0Down);
        ivNodo0Down.setVisibility(View.GONE);

        ivNodo1Up=(ImageView)findViewById(R.id.nodo1Up);
        ivNodo1Up.setVisibility(View.GONE);

        ivNodo1Down=(ImageView)findViewById(R.id.nodo1Down);
        ivNodo1Down.setVisibility(View.GONE);


        ivLock=(ImageView)findViewById(R.id.imageLock);
        //ivLock.setVisibility(View.GONE);

        ivUnLock=(ImageView)findViewById(R.id.imageUnLock);
        ivUnLock.setVisibility(View.GONE);

        ivLock.setOnClickListener(new OnClickListener() {
            public void onClick(View v) {
                ivLock.setVisibility(View.GONE);
                ivUnLock.setVisibility(View.VISIBLE);
            }
        });

        ivUnLock.setOnClickListener(new OnClickListener() {
            public void onClick(View v) {
                ivUnLock.setVisibility(View.GONE);
                ivLock.setVisibility(View.VISIBLE);

                /*
                enviar temperatura a pi
                lanzar async task
                */
                SharedPreferences sharedPrefs = PreferenceManager.getDefaultSharedPreferences(getBaseContext());
                final AsyncTask<String, Integer, Double> execute = new MyAsyncTask().execute("desire", tvDeseada.getText().toString().replace(',', '.').replace("º", ""), sharedPrefs.getString("Usuario", "NA"));
            }
        });



        ivPower=(ImageView)findViewById(R.id.imagePower);
        // set its background to our AnimationDrawable XML resource.
        //ivPower.setBackgroundResource(R.drawable.blinking_power);

		/*
		 * Get the background, which has been compiled to an AnimationDrawable
		 * object.
		 */
        //frameAnimation = (AnimationDrawable) ivPower.getBackground();
        frameAnimation = (AnimationDrawable) getResources().getDrawable(R.drawable.blinking_power);
        //ivPower.setBackgroundColor(Color.parseColor("?android:colorBackground"));
        ivPower.setImageDrawable(frameAnimation);
        frameAnimation.stop();
        frameAnimation.selectDrawable(0);


        ivFlash=(ImageView)findViewById(R.id.imageFlash);
        ivFlash.setVisibility(View.GONE);

        ivWarning=(ImageView)findViewById(R.id.imageWarning);
        ivWarning.setVisibility(View.GONE);


        ivPower.setOnClickListener(new OnClickListener() {
            public void onClick(View v) {
                // parpadeo de button power
                frameAnimation.start();
                if (strSystemOn.equals("true"))
                    tvPendienteComando.setText(R.string.str_enviando_comando_off);
                else
                    tvPendienteComando.setText(R.string.str_enviando_comando_on);
                // lanzar async task
                SharedPreferences sharedPrefs = PreferenceManager.getDefaultSharedPreferences(getBaseContext());
                new MyAsyncTask().execute("power", strSystemOn, sharedPrefs.getString("Usuario", "NA"));
            }
        });

        btn.setOnClickListener(this);
        tvTemperatura0=(TextView)findViewById(R.id.txt_temp_node0);
        tvTemperatura1=(TextView)findViewById(R.id.txt_temp_node1);
        tvHumedad0=(TextView)findViewById(R.id.txt_hum_node0);
        tvHumedad1=(TextView)findViewById(R.id.txt_hum_node1);
        tvNombre0 = (TextView)findViewById(R.id.txt_name_node0);
        tvNombre1 = (TextView)findViewById(R.id.txt_name_node1);
        tvFecha0 = (TextView)findViewById(R.id.txt_date_node0);
        tvFecha1 = (TextView)findViewById(R.id.txt_date_node1);

        tvPendienteCliente  = (TextView)findViewById(R.id.txt_sistema_name);
        tvPendienteHora     = (TextView)findViewById(R.id.txt_sistema_date);
        tvPendienteComando  = (TextView)findViewById(R.id.txt_sistema_info);
        tvSistema           = (TextView)findViewById(R.id.txt_sistema);

        tvDeseada           = (TextView)findViewById(R.id.txt_temperatura_deseada_info);
        tvNombreDeseada     = (TextView)findViewById(R.id.txt_temperatura_deseada_date);

        tvWorkMode          = (TextView)findViewById(R.id.txt_manual_automatico);

        tvHysteresis        = (TextView)findViewById(R.id.txt_hysteresis);

        //anim = new AlphaAnimation(0.0f, 1.0f);
        anim.setDuration(1000); //You can manage the time of the blink with this parameter
        anim.setStartOffset(20);
        anim.setRepeatMode(Animation.REVERSE);
        anim.setRepeatCount(Animation.INFINITE);


        tvWorkMode.setOnClickListener(new OnClickListener() {
            public void onClick(View v) {
                String strMode;

                if (strWorkMode.equals(getString(R.string.str_auto)))
                    strMode=getString(R.string.str_manual);
                else
                    strMode=getString(R.string.str_auto);

                // lanzar animacion
                tvWorkMode.startAnimation(anim);
                bBlinking = true;

                // lanzar async task
                SharedPreferences sharedPrefs = PreferenceManager.getDefaultSharedPreferences(getBaseContext());
                new MyAsyncTask().execute("workmode", strMode, sharedPrefs.getString("Usuario", "NA"));
            }
        });

        tvDeseada.setOnTouchListener(new OnSwipeTouchListener(this) {
            public void onSwipeTop() {
                if (ivUnLock.getVisibility() == View.VISIBLE) {

                    String strTemp;
                    strTemp = tvDeseada.getText().toString();
                    //if (decSeparator == ',')
                    //    strTemp.replace('.',decSeparator);
                    //else
                    //    strTemp.replace(',',decSeparator);

                    double dTemp = Double.parseDouble(strTemp.replace(',','.').replace("º",""));
                    dTemp += dCalibration;
                    tvDeseada.setText(String.format("%.2fº", dTemp));
                }
            }
            public void onSwipeRight() {
                Toast.makeText(getApplicationContext(), "right", Toast.LENGTH_SHORT).show();
            }
            public void onSwipeLeft() {
                Toast.makeText(getApplicationContext(), "left", Toast.LENGTH_SHORT).show();
            }
            public void onSwipeBottom() {
                if (ivUnLock.getVisibility() == View.VISIBLE) {
                    String strTemp;
                    strTemp = tvDeseada.getText().toString();
                    //if (decSeparator == ',')
                    //    strTemp.replace('.',decSeparator);
                    //else
                    //    strTemp.replace(',',decSeparator);


                    double dTemp = Double.parseDouble(strTemp.replace(',','.').replace("º",""));
                    dTemp -= dCalibration;
                    tvDeseada.setText(String.format("%.2fº", dTemp));
                }
            }

        });


        Toolbar myToolbar = (Toolbar) findViewById(R.id.my_toolbar);
        setSupportActionBar(myToolbar);
        //myToolbar.inflateMenu(R.menu.main_menu);

        // para evitar inicializacion cuando se cambia de orientacion la pantalla
        // onPause()=>onStop()=>onDestroy()
        //onCreate()=>onStart()=>onResume()
        if(savedInstanceState == null){
            SharedPreferences mPrefs = getPreferences(MODE_PRIVATE);;
            tvTemperatura0.setText(mPrefs.getString("tvTemperatura0", "0.0º"));
            tvTemperatura1.setText(mPrefs.getString("tvTemperatura1", "0.0º"));
            tvHumedad0.setText(mPrefs.getString("tvHumedad0", "0.0%"));
            tvHumedad1.setText(mPrefs.getString("tvHumedad1", "0.0%"));
            tvNombre0.setText(mPrefs.getString("tvNombre0", "-"));
            tvNombre1.setText(mPrefs.getString("tvNombre1", "-"));
            tvFecha0.setText(mPrefs.getString("tvFecha0", "-"));
            tvFecha1.setText(mPrefs.getString("tvFecha1", "-"));

            tvPendienteCliente.setText(mPrefs.getString("tvPendienteCliente", "-"));
            tvPendienteHora.setText(mPrefs.getString("tvPendienteHora", "-"));
            tvPendienteComando.setText(mPrefs.getString("tvPendienteComando", "-"));
            tvDeseada.setText(mPrefs.getString("tvDeseada", "0.0º"));
            tvNombreDeseada.setText(mPrefs.getString("tvNombreDeseada", "-"));

            tvHysteresis.setText(mPrefs.getString("tvHysteresis", "0.00"));
        }

    }

    @Override
    public void onSaveInstanceState(Bundle savedInstanceState) {
        super.onSaveInstanceState(savedInstanceState);
        // Save UI state changes to the savedInstanceState.
        // This bundle will be passed to onCreate if the process is
        // killed and restarted.
        savedInstanceState.putString("tvTemperatura0", tvTemperatura0.getText().toString());
        savedInstanceState.putString("tvTemperatura1", tvTemperatura1.getText().toString());
        savedInstanceState.putString("tvHumedad0", tvHumedad0.getText().toString());
        savedInstanceState.putString("tvHumedad1", tvHumedad1.getText().toString());
        savedInstanceState.putString("tvNombre0", tvNombre0.getText().toString());
        savedInstanceState.putString("tvNombre1", tvNombre0.getText().toString());
        savedInstanceState.putString("tvFecha0", tvFecha0.getText().toString());
        savedInstanceState.putString("tvFecha1", tvFecha1.getText().toString());

        savedInstanceState.putString("tvPendienteCliente", tvPendienteCliente.getText().toString());
        savedInstanceState.putString("tvPendienteHora", tvPendienteHora.getText().toString());
        savedInstanceState.putString("tvPendienteComando", tvPendienteComando.getText().toString());
        savedInstanceState.putString("tvDeseada", tvDeseada.getText().toString());
        savedInstanceState.putString("tvNombreDeseada", tvNombreDeseada.getText().toString());

        savedInstanceState.putString("tvHysteresis", tvHysteresis.getText().toString());


    }

    @Override
    public void onRestoreInstanceState(Bundle savedInstanceState) {
        super.onRestoreInstanceState(savedInstanceState);
        // Restore UI state from the savedInstanceState.
        // This bundle has also been passed to onCreate.
        tvTemperatura0.setText(savedInstanceState.getString("tvTemperatura0"));
        tvTemperatura1.setText(savedInstanceState.getString("tvTemperatura1"));
        tvHumedad0.setText(savedInstanceState.getString("tvHumedad0"));
        tvHumedad1.setText(savedInstanceState.getString("tvHumedad1"));
        tvNombre0.setText(savedInstanceState.getString("tvNombre0"));
        tvNombre1.setText(savedInstanceState.getString("tvNombre1"));
        tvFecha0.setText(savedInstanceState.getString("tvFecha0"));
        tvFecha1.setText(savedInstanceState.getString("tvFecha1"));

        tvPendienteCliente.setText(savedInstanceState.getString("tvPendienteCliente"));
        tvPendienteHora.setText(savedInstanceState.getString("tvPendienteHora"));
        tvPendienteComando.setText(savedInstanceState.getString("tvPendienteComando"));
        tvDeseada.setText(savedInstanceState.getString("tvDeseada"));
        tvNombreDeseada.setText(savedInstanceState.getString("tvNombreDeseada"));

        tvHysteresis.setText(savedInstanceState.getString("tvHysteresis"));

    }

    @Override
    protected void onResume() {
        super.onResume();

        //onResume we start our timer so it can start when the app comes from the background
        startTimer(3000,iTimer);
    }

    @Override
    public void onPause() {
        super.onPause();
        //timerHandler.removeCallbacks(timerRunnable);
        stoptimertask();

        // Store values between instances here
        SharedPreferences preferences = getPreferences(MODE_PRIVATE);
        SharedPreferences.Editor editor = preferences.edit();  // Put the values from the UI


        editor.putString("tvTemperatura0", tvTemperatura0.getText().toString());
        editor.putString("tvTemperatura1", tvTemperatura1.getText().toString());
        editor.putString("tvHumedad0", tvHumedad0.getText().toString());
        editor.putString("tvHumedad1", tvHumedad1.getText().toString());
        editor.putString("tvNombre0", tvNombre0.getText().toString());
        editor.putString("tvNombre1", tvNombre0.getText().toString());
        editor.putString("tvFecha0", tvFecha0.getText().toString());
        editor.putString("tvFecha1", tvFecha1.getText().toString());

        editor.putString("tvPendienteCliente", tvPendienteCliente.getText().toString());
        editor.putString("tvPendienteHora", tvPendienteHora.getText().toString());
        editor.putString("tvPendienteComando", tvPendienteComando.getText().toString());
        editor.putString("tvDeseada", tvDeseada.getText().toString());
        editor.putString("tvNombreDeseada", tvNombreDeseada.getText().toString());

        editor.putString("tvHysteresis", tvHysteresis.getText().toString());

        // Commit to storage
        editor.commit();
    }

    public void startTimer(int iMilisecondsInicio, int iMilisecons) {
        //set a new Timer
        timer = new Timer();

        //initialize the TimerTask's job
        initializeTimerTask();

        //schedule the timer, after the first 30000ms the TimerTask will run every 60000ms
        timer.schedule(timerTask, iMilisecondsInicio, iMilisecons); //
    }

    public void stoptimertask() {
        //stop the timer, if it's not already null
        if (timer != null) {
            timer.cancel();
            timer = null;
        }
    }

    public void initializeTimerTask() {

        timerTask = new TimerTask() {
            public void run() {

                //use a handler to run a toast that shows the current timestamp
                handler.post(new Runnable() {
                    public void run() {
                        //get the current timeStamp
                        //Calendar calendar = Calendar.getInstance();
                        //SimpleDateFormat simpleDateFormat = new SimpleDateFormat("dd:MMMM:yyyy HH:mm:ss a");
                        //final String strDate = simpleDateFormat.format(calendar.getTime());

                        //show the toast
                        //int duration = Toast.LENGTH_SHORT;
                        //Toast toast = Toast.makeText(getApplicationContext(),"uno", duration); //strDate, duration);
                        //toast.show();
                        pb.setVisibility(View.VISIBLE);
                        new MyAsyncTask().execute("settings");
                    }
                });
            }
        };
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.main_menu, menu);

        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        Intent intent;
        switch (item.getItemId()) {
            case R.id.action_favorite:
                Log.i("ActionBar", "Nuevo!");
                intent = new Intent(this, DisplayEstadisticasActivity.class);
                intent.putExtra("cadena", DameCadenaConexion());
                intent.putExtra("name0" , strName0);
                intent.putExtra("name1" , strName1);
                startActivity(intent);
                return true;
            case R.id.action_settings:
                Log.i("ActionBar", "Settings!");;
                //intent = new Intent();
                //intent.setClassName(this, "com.cigsa.carlos.calcar4.UserSettingActivity");
                intent = new Intent(this, UserSettingActivity.class);
                startActivity(intent);
                return true;
            case R.id.action_settings_pi:
                Log.i("ActionBar", "Settings pi!");;
                intent = new Intent(this, UserSettingPiActivity.class);
                intent.putExtra("hysteresis", dHysteresis);
                intent.putExtra("max_temp", dMaxTemp);
                intent.putExtra("min_temp" , dMinTemp);
                intent.putExtra("calibration" , dCalibration);
                intent.putExtra("cadena", DameCadenaConexion());
                startActivity(intent);
                return true;
            default:
                return super.onOptionsItemSelected(item);
        }
    }

    public void onClick(View v) {
            pb.setVisibility(View.VISIBLE);
        new MyAsyncTask().execute("settings");
    }

    public String DameCadenaConexion() {
        String strCadenaConecion = "NA" ;
        String strWifiName = IsWifiConnected();
        SharedPreferences sharedPrefs = PreferenceManager.getDefaultSharedPreferences(getBaseContext());

        if (strWifiName != null) {
            String strNombreWifi = sharedPrefs.getString("RedLocal", "NA");
            strNombreWifi = "\"" + strNombreWifi +  "\"" ;
            if (strWifiName.equals(strNombreWifi)) {
                strCadenaConecion = sharedPrefs.getString("WebServicePrivado", "NA");
            }
            else
            {
                strCadenaConecion = sharedPrefs.getString("WebServicePublico", "NA");
            }
        }
        else
        {
          strCadenaConecion = sharedPrefs.getString("WebServicePublico", "NA");
        }
       return strCadenaConecion;
    }

    public String IsWifiConnected() {

        String strCad = "NA";

        WifiManager manager = (WifiManager) this.getSystemService(Context.WIFI_SERVICE);
        if (manager.isWifiEnabled()) {
            WifiInfo wifiInfo = manager.getConnectionInfo();
            if (wifiInfo != null) {
                NetworkInfo.DetailedState state = WifiInfo.getDetailedStateOf(wifiInfo.getSupplicantState());
                if (state == NetworkInfo.DetailedState.CONNECTED || state == NetworkInfo.DetailedState.OBTAINING_IPADDR) {
                    strCad =  wifiInfo.getSSID();
                }
            }
        }

        return strCad;
    }



    private class MyAsyncTask extends AsyncTask<String, Integer, Double>{

        private Boolean bLanzoGet;
        private Boolean bHayDatos;
        private String strException;


        @Override
        protected Double doInBackground(String... params) {
            // TODO Auto-generated method stub
            if (params[0].equals("settings")) {
                bLanzoGet = true;
                postDataGet(params[0]);
            }
            else {
                bLanzoGet = false;
                postDataSet(params[0],params[1], params[2]);
            }
            return null;
        }

        protected void onPostExecute(Double result) {

            if (bLanzoGet) {

                pb.setVisibility(View.GONE);

                if (strException != null)
                  Toast.makeText(getApplicationContext(),  strException, Toast.LENGTH_LONG).show();

                if (bHayDatos) {
                    Toast.makeText(getApplicationContext(), "command sent", Toast.LENGTH_LONG).show();
                    long diffInSec = 120;

                    SimpleDateFormat output = new SimpleDateFormat("dd/MM/yyyy HH:mm:ss", Locale.getDefault());
                    long msTime = System.currentTimeMillis();
                    try {
                        //Date datee = output.parse(strDateAct0);
                        //long longee = datee.getTime();
                         diffInSec = TimeUnit.MILLISECONDS.toSeconds(msTime - output.parse(strDateAct0).getTime());
                        // diffInSec = TimeUnit.MILLISECONDS.toSeconds(msTime - longee);
                    } catch(java.text.ParseException e) {
                    e.printStackTrace();
                    }

                    if (diffInSec>=120)
                       ivWarning.setVisibility(View.VISIBLE);
                    else
                       ivWarning.setVisibility(View.GONE);

                    tvTemperatura0.setText(String.format("%.2fº", dTemp0));
                    tvTemperatura1.setText(String.format("%.2fº", dTemp1));
                    tvNombre0.setText(strName0);
                    tvNombre1.setText(strName1);
                    tvFecha0.setText(strDate0);
                    tvFecha1.setText(strDate1);
                    tvHumedad0.setText(String.format("%.2f%%", Double.parseDouble(strHum0)) );
                    tvHumedad1.setText(String.format("%.2f%%", Double.parseDouble(strHum1)) );
                    tvPendienteCliente.setText(strPendienteCliente);
                    tvPendienteHora.setText(strPendienteHora);
                    tvHysteresis.setText(strHysteresis);

                    if (strWorkMode.equals(getString(R.string.str_auto)))
                        tvWorkMode.setText(getString(R.string.str_a));
                    else
                        tvWorkMode.setText(getString(R.string.str_m));

                    if ((bBlinking)&&(bCambioMode)) {
                        tvWorkMode.clearAnimation(); // cancel blink animation
                        tvWorkMode.setAlpha(1.0f); // restore original alpha
                        bBlinking = false;

                        if (iTimer == 3000) {
                            stoptimertask();
                            iTimer = 60000;
                            startTimer(60000,iTimer);
                        }
                    }

                    if (ivLock.getVisibility() != View.GONE) {
                        tvDeseada.setText(String.format("%.2fº", dDeseada));
                        tvNombreDeseada.setText(strNombreDeseada);
                    }

                    if ((!strPendienteComando.equals("1"))&& //strPendienteComando 1, 0 o vacio
                            (!strPendienteComando.equals("0")))
                    {
                        frameAnimation.stop();
                        frameAnimation.selectDrawable(0);

                        if (strLastCompletedCommand.equals("on"))  //strLastCompletedCommand  // on off
                           tvPendienteComando.setText(R.string.str_on_ejecutado);
                        else
                           tvPendienteComando.setText(R.string.str_off_ejecutado);

                        if (iTimer == 3000) {
                            stoptimertask();
                            iTimer = 60000;
                            startTimer(60000,iTimer);
                        }
                    }

                    if (strHeating.equals("true"))
                        ivFlash.setVisibility(View.VISIBLE);
                    else
                        ivFlash.setVisibility(View.GONE);

                    if (strSystemOn.equals("true"))
                      tvSistema.setText(R.string.str_sistema_on);
                    else
                        tvSistema.setText(R.string.str_sistema_off);

                        if (dTemp0 < dTemp0Ant) {
                        ivNodo0Up.setVisibility(View.GONE);
                        ivNodo0Down.setVisibility(View.VISIBLE);
                    } else if (dTemp0 > dTemp0Ant) {
                        ivNodo0Up.setVisibility(View.VISIBLE);
                        ivNodo0Down.setVisibility(View.GONE);
                    }

                    if (dTemp1 < dTemp1Ant) {
                        ivNodo1Up.setVisibility(View.GONE);
                        ivNodo1Down.setVisibility(View.VISIBLE);
                    } else if (dTemp1 > dTemp1Ant) {
                        ivNodo1Up.setVisibility(View.VISIBLE);
                        ivNodo1Down.setVisibility(View.GONE);
                    }
                } else {
                    //Toast.makeText(getApplicationContext(), "command NOT sent", Toast.LENGTH_LONG).show();
                    //tvTemperatura0.setText("-");
                    //tvTemperatura1.setText("-");
                    //tvNombre0.setText("-");
                    //tvNombre1.setText("-");
                    //tvFecha0.setText("-");
                    //tvFecha1.setText("-");
                    //tvHumedad0.setText("-");
                    //tvHumedad1.setText("-");
                }
            }
        }

        protected void onProgressUpdate(Integer... progress){
            if (bLanzoGet) {
                pb.setProgress(progress[0]);
            }
        }

        public void postDataSet(String strComando, String strOne, String strCliente) {

            try {

                String strComando1;
            String strComando2 = null;
            // set the connection timeout value to 30 seconds (30000 milliseconds)
            final HttpParams httpParams = new BasicHttpParams();
            HttpConnectionParams.setConnectionTimeout(httpParams, 10000);
            HttpConnectionParams.setSoTimeout(httpParams, 10000);

                    // Create a new HttpClient and Post Header
            bHayDatos = false;
            strException = null;
            HttpClient httpclient = new DefaultHttpClient(httpParams);
            String strCadConexion = DameCadenaConexion();

            if (strComando.equals("power")) {

                strComando1 = strCadConexion + "/data/put/pending_command/";
                if (strOne.equals("true"))
                    strComando1 = strComando1 + "0";
                else
                    strComando1 = strComando1 + "1";

                strComando2 = strCadConexion + "/data/put/pending_command_client/" + strCliente;
            }
            else if  (strComando.equals("workmode")){
                strComando1 = strCadConexion + "/data/put/work_mode/" + strOne;
            }
            else { // "desire"
                strComando1 = strCadConexion + "/data/put/desired_temp/" + strOne ;
                strComando2 = strCadConexion + "/data/put/client_desired_temp/" + strCliente;

            }


            HttpGet httpget1 = new HttpGet(strComando1);
             HttpGet httpget2 = null;
                if (strComando2 != null)
               httpget2 = new HttpGet(strComando2);

                // Execute HTTP Post Request
                HttpResponse response;

                response = httpclient.execute(httpget1);

                if (httpget2 != null)
                  response = httpclient.execute(httpget2);

                if (strException != null)
                    Toast.makeText(getApplicationContext(),  strException, Toast.LENGTH_LONG).show();

                if (strException == null) {

                    ByteArrayOutputStream out = new ByteArrayOutputStream();
                    response.getEntity().writeTo(out);
                    out.close();

                    String respuesta = out.toString();

                    if (strComando.equals("power")) {
                        stoptimertask();
                        iTimer = 3000;
                        startTimer(3000,iTimer);
                    }
                    else if (strComando.equals("workmode")){
                        stoptimertask();
                        iTimer = 3000;
                        startTimer(3000,iTimer);
                    }

                    bHayDatos = true;
                }

            } catch (ClientProtocolException e) {
                // TODO Auto-generated catch block
                e.printStackTrace();
                strException = e.getMessage();
            } catch (IOException e) {
                // TODO Auto-generated catch block
                e.printStackTrace();
                strException = e.getMessage();
            } catch(Exception e) {
                e.printStackTrace();
                strException = e.getMessage();
            }

        }

        public void postDataGet(String strUnused) {

            // set the connection timeout value to 30 seconds (30000 milliseconds)
            final HttpParams httpParams = new BasicHttpParams();
            HttpConnectionParams.setConnectionTimeout(httpParams, 10000);
            HttpConnectionParams.setSoTimeout(httpParams, 10000);
            strException = null;

            // Create a new HttpClient and Post Header
            bHayDatos = false;
            HttpClient httpclient = new DefaultHttpClient(httpParams);
            String strCadConexion = DameCadenaConexion();
            strCadConexion = strCadConexion + "/data/get";
            //HttpPost httppost = new HttpPost("http://192.168.0.158:43001/do_pi.php");
            HttpGet httpget = new HttpGet(strCadConexion);
            //HttpGet httpget = new HttpGet("http://192.168.0.158:43001/do_pi.php/data/get");

            try {
                // Add your data
                //  List<NameValuePair> nameValuePairs = new ArrayList<NameValuePair>();
                // nameValuePairs.add(new BasicNameValuePair("myHttpData", valueIWantToSend));
                // httppost.setEntity(new UrlEncodedFormEntity(nameValuePairs));

                // Execute HTTP Post Request
                //HttpResponse response = httpclient.execute(httppost);
                HttpResponse response = httpclient.execute(httpget);

                 ByteArrayOutputStream out = new ByteArrayOutputStream();
                 response.getEntity().writeTo(out);
                 out.close();

                    String respuesta = out.toString();

                    JSONObject reader = new JSONObject(respuesta);

                    JSONObject main = reader.getJSONObject("value");

                    dTemp0Ant = dTemp0;
                    dTemp1Ant = dTemp1;

                    //dAct0 = Double.parseDouble(main.getString("act0_temp"));
                    strDateAct0 = main.getString("act0_date");

                    dTemp0 = Double.parseDouble(main.getString("node0_temp"));
                    dTemp1 = Double.parseDouble(main.getString("node1_temp"));
                    strName0 = main.getString("node0_name");
                    strName1 = main.getString("node1_name");
                    strDate0 = main.getString("node0_date");
                    strDate1 = main.getString("node1_date");
                    strHum0 = main.getString("node0_hum");
                    strHum1 = main.getString("node1_hum");
                    dHysteresis = Double.parseDouble(main.getString("hysteresis"));
                    strHysteresis = main.getString("hysteresis");
                    dMaxTemp = Double.parseDouble(main.getString("max_temp"));
                    dMinTemp = Double.parseDouble(main.getString("min_temp"));
                    dCalibration = Double.parseDouble(main.getString("calibration"));
                    strSystemOn = main.getString("system_on");
                    strHeating = main.getString("heating");
                    strPendienteComando = main.getString("pending_command"); // 1 o vacio
                    strPendienteCliente = main.getString("pending_command_client");
                    strPendienteHora = main.getString("date_last_completed_command");
                    strLastCompletedCommand = main.getString("last_completed_command"); // on off

                    String strOldWokMode = strWorkMode;
                    strWorkMode = main.getString("work_mode"); // manual - auto
                    if (!strWorkMode.equals(strOldWokMode))
                        bCambioMode = true;
                    else
                        bCambioMode = false;
                    //strTemperaturaDeseada = main.getString("desired_temp");
                    //strTemperaturaDeseadaCliente = main.getString("client_desired_temp");
                dDeseada = Double.parseDouble(main.getString("desired_temp").replace(',','.'));
                    strNombreDeseada = main.getString("client_desired_temp");

                    bHayDatos = true;

            } catch (ClientProtocolException e) {
                // TODO Auto-generated catch block
                e.printStackTrace();
                strException = e.getMessage();
                //Toast.makeText(getApplicationContext(),  e.getMessage(), Toast.LENGTH_LONG).show();
            } catch (IOException e) {
                // TODO Auto-generated catch block
                e.printStackTrace();
                strException = e.getMessage();
                //Toast.makeText(getApplicationContext(),  e.getMessage(), Toast.LENGTH_LONG).show();
            } catch (JSONException e) {
                e.printStackTrace();
                strException = e.getMessage();
                //Toast.makeText(getApplicationContext(),  e.getMessage(), Toast.LENGTH_LONG).show();
            } catch(Exception e) {
                e.printStackTrace();
                strException = e.getMessage();
                //Toast.makeText(getApplicationContext(),  e.getMessage(), Toast.LENGTH_LONG).show();
            }

        }

    }
}