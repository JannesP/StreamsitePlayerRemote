package com.nourl.streamsiteplayerremote.activities;

import android.content.Intent;
import android.content.SharedPreferences;
import android.media.MediaPlayer;
import android.os.Bundle;
import android.preference.Preference;
import android.preference.PreferenceManager;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.Menu;
import android.view.MenuItem;
import android.view.ViewTreeObserver;
import android.widget.LinearLayout;
import android.widget.MediaController;
import android.widget.RelativeLayout;

import com.nourl.streamsiteplayerremote.R;
import com.nourl.streamsiteplayerremote.networking.NetworkInterface;
import com.nourl.streamsiteplayerremote.networking.NetworkMediaPlayerControl;
import com.nourl.streamsiteplayerremote.networking.tcp.TcpNetworkInterface;

public class MainActivity extends AppCompatActivity implements SharedPreferences.OnSharedPreferenceChangeListener {

    private NetworkMediaPlayerControl mediaControl;
    private NetworkInterface networkInterface;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        PreferenceManager.getDefaultSharedPreferences(this).registerOnSharedPreferenceChangeListener(this);
        createNetworkInterface();
        createMediaControl();
    }

    @Override
    protected void onStop() {
        super.onStop();
        mediaControl.cancelRefreshLoop();
    }

    @Override
    protected void onRestart() {
        super.onRestart();
        mediaControl.startRefreshLoop();
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_main, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();

        //noinspection SimplifiableIfStatement
        if (id == R.id.action_settings) {
            Intent intent = new Intent();
            intent.setClass(this, SettingsActivity.class);
            startActivity(intent);
            return true;
        }

        return super.onOptionsItemSelected(item);
    }

    private void createNetworkInterface() {
        if (networkInterface != null) {
            networkInterface.stop();
        }
        SharedPreferences prefs = PreferenceManager.getDefaultSharedPreferences(this);
        String ip = prefs.getString("pref_tcp_ip", "127.0.0.1");
        int port = Integer.parseInt(prefs.getString("pref_tcp_port", "8003"));
        int timeout = Integer.parseInt(prefs.getString("pref_tcp_timeout", "1000"));
        networkInterface = new TcpNetworkInterface(ip, port, timeout);
        networkInterface.start();
    }

    private void createMediaControl() {
        MediaController mediaController = new MediaController(this, false) {
            @Override
            public void hide() {
                show();
            }  //override to prevent automatic hiding
        };
        mediaController.setAnchorView(findViewById(R.id.mediaFrame));
        mediaControl = new NetworkMediaPlayerControl(mediaController, networkInterface, this);

        RelativeLayout mainLayout = (RelativeLayout) findViewById(R.id.mainLayout);
        mainLayout.getViewTreeObserver().addOnGlobalLayoutListener(
            new ViewTreeObserver.OnGlobalLayoutListener() {
                public void onGlobalLayout() {
                    mediaControl.show();
                }
            }
        );
    }

    @Override
    public void onSharedPreferenceChanged(SharedPreferences sharedPreferences, String key) {
        switch (key) {
            case "pref_tcp_ip":
                createNetworkInterface();
                break;
            case "pref_tcp_port":
                createNetworkInterface();
                break;
            case "pref_tcp_timeout":
                createNetworkInterface();
                break;
        }
    }
}
