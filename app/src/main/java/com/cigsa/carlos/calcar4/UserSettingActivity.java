package com.cigsa.carlos.calcar4;

import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.preference.Preference;
import android.preference.PreferenceActivity;
import android.preference.PreferenceFragment;
import android.preference.PreferenceManager;
import android.widget.Toast;



public class UserSettingActivity extends PreferenceActivity
{
    @Override
    protected void onCreate(Bundle savedInstanceState)
    {
        super.onCreate(savedInstanceState);

        if (savedInstanceState == null) {
            getFragmentManager().beginTransaction().replace(android.R.id.content, new MyPreferenceFragment()).commit();
        }

       // checkValues();
    }


     public static class MyPreferenceFragment extends PreferenceFragment
             implements SharedPreferences.OnSharedPreferenceChangeListener
    {
        @Override
        public void onCreate(final Bundle savedInstanceState)
        {
            super.onCreate(savedInstanceState);
            addPreferencesFromResource(R.xml.settings);

            // set texts correctly
            onSharedPreferenceChanged(getPreferenceScreen().getSharedPreferences(), "Usuario");
            onSharedPreferenceChanged(getPreferenceScreen().getSharedPreferences(), "WebServicePrivado");
            onSharedPreferenceChanged(getPreferenceScreen().getSharedPreferences(), "WebServicePublico");
            onSharedPreferenceChanged(getPreferenceScreen().getSharedPreferences(), "FrecuenciaRefresco");
            onSharedPreferenceChanged(getPreferenceScreen().getSharedPreferences(), "RedLocal");

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
            if (key.equals("Usuario") ||
                key.equals("WebServicePrivado") ||
                key.equals("WebServicePublico") ||
                key.equals("RedLocal") ||
                key.equals("FrecuenciaRefresco") ) {
                Preference pref = findPreference(key);
                pref.setSummary(sharedPreferences.getString(key, ""));
            }
        }
    }

    private void checkValues()
    {
        SharedPreferences sharedPrefs = PreferenceManager.getDefaultSharedPreferences(getBaseContext());
        String strUsuario= sharedPrefs.getString("Usuario", "NA");
        //boolean bAppUpdates = sharedPrefs.getBoolean("applicationUpdates",false);
        String strWebServicePrivado = sharedPrefs.getString("WebServicePrivado", "NA");
        //Integer iFrecuenciaRefresco = sharedPrefs.getInt("FrecuenciaRefresco", 60);
        String iFrecuenciaRefresco = sharedPrefs.getString("FrecuenciaRefresco", "60");

        String msg = "Cur Values: ";
        msg += "\n Usuario = " + strUsuario;
        msg += "\n Web service privado = " + strWebServicePrivado;
        msg += "\n Frecuencia de refresco = " + iFrecuenciaRefresco;

        Toast.makeText(getApplicationContext(), msg, Toast.LENGTH_LONG).show();

    }
}
