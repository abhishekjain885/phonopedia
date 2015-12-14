package com.example.abhishek.phonopedia;

import android.accounts.Account;
import android.accounts.AccountManager;
import android.annotation.SuppressLint;
import android.app.AlertDialog;
import android.bluetooth.BluetoothAdapter;
import android.content.ClipData;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.IntentFilter;
import android.database.Cursor;
import android.graphics.Point;
import android.hardware.Sensor;
import android.hardware.SensorManager;
import android.net.Uri;
import android.net.wifi.WifiConfiguration;
import android.net.wifi.WifiInfo;
import android.net.wifi.WifiManager;
import android.os.Build;
import android.os.Bundle;
import android.provider.Settings;
import android.support.v7.app.ActionBarActivity;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
import android.telephony.TelephonyManager;
import android.text.ClipboardManager;
import android.text.SpannableString;
import android.text.format.Formatter;
import android.text.util.Linkify;
import android.util.DisplayMetrics;
import android.util.Log;
import android.view.Display;
import android.view.Menu;
import android.view.MenuItem;
import android.view.MotionEvent;
import android.view.View;
import android.widget.Toast;

import com.melnykov.fab.FloatingActionButton;

import java.io.BufferedReader;
import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.util.List;

public class MainActivity extends ActionBarActivity

{
    private String android_id;
    private String device_name;
    private String email;
    private String gsf;
    private String IMEI_device_id;
    private String Sim_id;
    private String serial;
    private String local_ip;
    private String macAddress;
    private String blue_mac;
    private String Hardware_serial;
    private String Build_Finger_print;
    private String sensors;
    private String os_version;
    private String Android_version;
    private int height;
    private int width;
    //   private double screenInches;
    private String cpu_info;
    private String mem_info;
    private String[] mem_inf;
    private String cpu_abi;
    private String hardware;
    private String total_data;
    private boolean isroot;
    private String root;
    private ClipboardManager clipboard;
    private ClipData myClip;
    private Toolbar toolbar;
    private RecyclerView recyclerView;
    private RecyclerView.Adapter adapter;
    private RecyclerView.LayoutManager layoutManager;
    private ItemsModelList data = new ItemsModelList();

    @SuppressLint("NewApi")
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);


        android_id = Settings.Secure.getString(this.getContentResolver(),
                Settings.Secure.ANDROID_ID);


        device_name = Build.DEVICE;
        Hardware_serial = Build.SERIAL;
        Build_Finger_print = Build.FINGERPRINT;
        cpu_abi = Build.CPU_ABI;
        Android_version= Build.VERSION.RELEASE;


        hardware = Build.HARDWARE;
        os_version = System.getProperty("os.version");


        Display display = getWindowManager().getDefaultDisplay();
        Point size = new Point();
        display.getSize(size);
        width = size.x;
        height = size.y;




        AccountManager manager = (AccountManager) getSystemService(ACCOUNT_SERVICE);

        Account[] list = manager.getAccounts();
        email = null;

        for (Account account : list) {
            if (account.type.equalsIgnoreCase("com.google")) {
                email = account.name;
                break;
            }
        }

        gsf = getGsfAndroidId(this);

        TelephonyManager tManager = (TelephonyManager) getSystemService(Context.TELEPHONY_SERVICE);
        IMEI_device_id = tManager.getDeviceId();


        Sim_id = tManager.getSubscriberId();

        serial = tManager.getSimSerialNumber();


        WifiManager wim = (WifiManager) getSystemService(WIFI_SERVICE);
        List<WifiConfiguration> l = wim.getConfiguredNetworks();
        if (l == null) {
            local_ip = "null";
        } else {
            WifiConfiguration wc = l.get(0);
            local_ip = Formatter.formatIpAddress(wim.getConnectionInfo().getIpAddress());
        }

        WifiInfo wInfo = wim.getConnectionInfo();
        macAddress = wInfo.getMacAddress();

        BluetoothAdapter bluetoothDefaultAdapter = BluetoothAdapter.getDefaultAdapter();
        if ((bluetoothDefaultAdapter != null) && (bluetoothDefaultAdapter.isEnabled())) {
            blue_mac = BluetoothAdapter.getDefaultAdapter().getAddress();
        } else {
            blue_mac = "null";
        }

        SensorManager sm = (SensorManager) getSystemService(SENSOR_SERVICE);
        List<Sensor> listi = sm.getSensorList(Sensor.TYPE_ALL);
        int i = 1;

        for (Sensor s : listi) {
            sensors = (sensors + "--" + s.getName());
            Log.d("SENSORS", s.getName());
            i++;
        }

        // cpu information

        try {
            Process proc = Runtime.getRuntime().exec("cat /proc/cpuinfo");
            InputStream is = proc.getInputStream();
            // TextView tv = (TextView)findViewById(R.id.tvcmd);
            cpu_info = (getStringFromInputStream(is));
        } catch (IOException e) {
            // Log.e(TAG, "------ getCpuInfo " + e.getMessage());
        }

        // memory information
        try {
            Process proc = Runtime.getRuntime().exec("cat /proc/meminfo");
            InputStream is = proc.getInputStream();

            mem_info = (getStringFromInputStream(is));
            mem_inf=mem_info.split("Buffers");
        } catch (IOException e) {
            Log.e("", "------ getMemoryInfo " + e.getMessage());
        }

        isroot= isrooted("su");
        if(isroot)
        {
            root="yes";

        }
        else
        {
            root="N0";
        }


        Intent batteryIntent = this.getApplicationContext().registerReceiver(null,
                new IntentFilter(Intent.ACTION_BATTERY_CHANGED));
        double rawlevel = batteryIntent.getIntExtra("level", -1);
        double scale = batteryIntent.getIntExtra("scale", -1);
        double level = -1;
        int blevel=0;
        if (rawlevel >= 0 && scale > 0) {
            level = rawlevel / scale;
            level=level*100;
            blevel=(int)level;
        }
        else
        {
            blevel=0;
        }


        //  accountName = data.getStringExtra(AccountManager.KEY_ACCOUNT_NAME);
        data.addItem(new ItemsModel(device_name, "Device Name"));
        data.addItem(new ItemsModel(email, "Email"));
        data.addItem(new ItemsModel(android_id, "Android device id"));
        data.addItem(new ItemsModel(IMEI_device_id, "IMEI"));
        data.addItem(new ItemsModel(hardware, "hardware"));
        data.addItem(new ItemsModel(os_version, "Kernal Version"));
        data.addItem(new ItemsModel(Android_version, "Android Version"));
        data.addItem(new ItemsModel(root, "Root Access"));
        data.addItem(new ItemsModel(blevel+"%", "Battery"));
        data.addItem(new ItemsModel(height+" X "+width, "Screen Resolution"));
        //  data.addItem(new ItemsModel(screenInches+"", "Screen Resolution"));
        data.addItem(new ItemsModel(Sim_id, "SIM Subscriber ID"));
        data.addItem(new ItemsModel(serial, "SIM Card Serial"));
        data.addItem(new ItemsModel(local_ip, "Local IP Address"));
        data.addItem(new ItemsModel(macAddress, "Wifi Mac Address"));
        data.addItem(new ItemsModel(blue_mac, "Bluetooth MAC Address"));

        data.addItem(new ItemsModel(mem_inf[0], "Memory"));
        data.addItem(new ItemsModel(cpu_abi, "CPU_abi"));
        data.addItem(new ItemsModel(sensors, "Sensors"));
        data.addItem(new ItemsModel(gsf, "Google Service Framework (GSF)"));
        data.addItem(new ItemsModel(cpu_info, "cpu_info"));
        data.addItem(new ItemsModel(Hardware_serial, "Hardware Serial"));
        data.addItem(new ItemsModel(Build_Finger_print, "Device Build Finger Prints"));

        total_data = "Device Name-->" + device_name + "\nEmail id-->" + email + "\n Android ID-->" + android_id + "\nIMEI NUMBER-->" + IMEI_device_id + "\nSIM ID" + Sim_id + "\nLocal IP" + local_ip + "\nsensors-->" + sensors;


        setContentView(R.layout.items_layout);
        recyclerView = (RecyclerView) findViewById(R.id.my_recycler_view);
        //recyclerView.setHasFixedSize(true);

        layoutManager = new LinearLayoutManager(this);
        recyclerView.setLayoutManager(layoutManager);

        adapter = new recycleViewAdapter(data);
        recyclerView.setAdapter(adapter);
        FloatingActionButton fab = (FloatingActionButton) findViewById(R.id.fab);
        fab.attachToRecyclerView(recyclerView);
        fab.setOnTouchListener(new View.OnTouchListener() {
            @Override
            public boolean onTouch(View view, MotionEvent motionEvent) {

                if (motionEvent.getAction() == MotionEvent.ACTION_UP) {

                    final SpannableString s = new SpannableString("https://github.com/abhishekjain885/Phone-id");
                    Linkify.addLinks(s, Linkify.WEB_URLS);
                    String header = "Watch" + "\nSource code at github@Abhishek_jain";
                    new AlertDialog.Builder(MainActivity.this)
                            .setTitle(header)
                            .setMessage(s)
//                            .setPositiveButton(android.R.string.yes, new DialogInterface.OnClickListener() {
//                                public void onClick(DialogInterface dialog, int which) {
//                                    // continue with delete
//                                }
//                            })
                            .setNegativeButton(android.R.string.ok, new DialogInterface.OnClickListener() {
                                public void onClick(DialogInterface dialog, int which) {
                                    // do nothing
                                }
                            })
                            .setIcon(R.drawable.but)
                            .show();


                    //  Toast.makeText(MainActivity.this, "Refresh Clicked!", Toast.LENGTH_LONG).show();

                }
                return false;
            }
        });
        toolbar = (Toolbar) findViewById(R.id.ttoolbar);
        toolbar.setTitle("Phonopedia");
        //  toolbar.setBackgroundColor(new ColorDrawable(Color.parseColor("#0000ff")));
        setSupportActionBar(toolbar);
    }

    public static boolean isrooted(String su) {
        boolean found = false;
        if (!found) {
            String[] places = { "/sbin/", "/system/bin/", "/system/xbin/",
                    "/data/local/xbin/", "/data/local/bin/",
                    "/system/sd/xbin/", "/system/bin/failsafe/", "/data/local/" };
            for (String where : places) {
                if (new File(where + su).exists()) {
                    found = true;

                    break;
                }
            }
        }
        return found;
    }


    private static String getStringFromInputStream(InputStream is) {

        StringBuilder sb = new StringBuilder();
        BufferedReader br = new BufferedReader(new InputStreamReader(is));
        String line = null;

        try {
            while((line = br.readLine()) != null) {
                sb.append(line);
                sb.append("\n");
            }
        }
        catch (IOException e) {
            Log.e("", "------ getStringFromInputStream " + e.getMessage());
        }
        finally {
            if(br != null) {
                try {
                    br.close();
                }
                catch (IOException e) {
                    Log.e("", "------ getStringFromInputStream " + e.getMessage());
                }
            }
        }

        return sb.toString();


    }

    private String getGsfAndroidId(MainActivity mainActivity) {
        Uri URI = Uri.parse("content://com.google.android.gsf.gservices");
        String ID_KEY = "android_id";
        String params[] = {ID_KEY};
        Cursor c = mainActivity.getContentResolver().query(URI, null, null, params, null);
        if (!c.moveToFirst() || c.getColumnCount() < 2)
            return null;
        try
        {
            return Long.toHexString(Long.parseLong(c.getString(1)));
        }
        catch (NumberFormatException e)
        {
            return null;
        }

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
        if (id == R.id.Copy) {

            clipboard = (ClipboardManager)this.getSystemService(Context.CLIPBOARD_SERVICE);
            clipboard.setText(total_data);
            Toast.makeText(this,"Data copied to Clipboard",Toast.LENGTH_LONG).show();



            return true;
        }
        else if(id==R.id.share)
        {
            Intent whatsappIntent = new Intent(Intent.ACTION_SEND);
            whatsappIntent.setType("text/plain");
            whatsappIntent.setPackage("com.whatsapp");
            whatsappIntent.putExtra(Intent.EXTRA_TEXT, total_data);
            try {
                startActivity(whatsappIntent);
            } catch (android.content.ActivityNotFoundException ex) {

            }

        }

        return super.onOptionsItemSelected(item);
    }
}