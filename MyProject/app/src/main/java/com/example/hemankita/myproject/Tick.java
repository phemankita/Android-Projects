package com.example.hemankita.myproject;

import android.content.Intent;
import android.support.v4.content.LocalBroadcastManager;
import android.util.Log;

/**
 * Created by Hemankita on 6/30/2016.
 */
public class Tick extends Thread {
    LocalBroadcastManager broadcastManager;
    boolean myShouldRun = true;

    public static String DEBUG_TAG = "TickThread";
    public static String TICK_ACTION = "TICK";

    public Tick(LocalBroadcastManager broadcastManager){
        this.broadcastManager = broadcastManager;
    }

    public void run() {
        while (shouldRun()) {
            mySleep(500);
            if (shouldRun()) {
                Log.d(DEBUG_TAG, "Tick");
                broadcastManager.sendBroadcast(new Intent(TICK_ACTION));
            }
        }
    }
    private void mySleep(long millis){
            try {
                sleep(millis);
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
        }

    synchronized
        private boolean shouldRun(){
            return myShouldRun;
        }

    synchronized
        public void cancel(){
            myShouldRun = false;
        }
    }

