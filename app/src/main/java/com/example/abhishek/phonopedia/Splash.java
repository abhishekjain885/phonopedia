package com.example.abhishek.phonopedia;

import android.app.Activity;
import android.content.Intent;
import android.support.v7.app.ActionBarActivity;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;


public class Splash extends Activity {

    public int WAIT_TIME_SPLASH_SCREEN = 1000;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_splash);

        final Intent changeActivity = new Intent(this,MainActivity.class);

/*       NotificationManager manager = (NotificationManager)getSystemService(NOTIFICATION_SERVICE);
       SyncAdapter sync = new  SyncAdapter(this, false, manager);
       sync.sync();
*/
        //creating thread for waiting WAIT_TIME seconds
        Thread wait = new Thread(){
            @Override
            public void run() {
                //use try catch block to avoid unhandled thread interrrupted exception and preventing app from crashing
                try {

                    sleep(WAIT_TIME_SPLASH_SCREEN);
                } catch (InterruptedException e) {
                    //Log.e("Splash Screen to Home Thread wait error",e.getMessage());
                } catch (Exception e) {
                    //Log.e("Other thread exception",e.getMessage());
                } finally{
                    //starting the activity using the intent
                    startActivity(changeActivity);
                }

            }
        };
        wait.start();
    }





}
