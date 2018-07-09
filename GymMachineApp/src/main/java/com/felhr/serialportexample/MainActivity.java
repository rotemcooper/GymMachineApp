package com.felhr.serialportexample;

import android.content.BroadcastReceiver;
import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.ServiceConnection;
import android.os.Bundle;
import android.os.Handler;
import android.os.IBinder;
import android.os.Message;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import com.jjoe64.graphview.GraphView;
import com.jjoe64.graphview.series.LineGraphSeries;
import com.jjoe64.graphview.series.PointsGraphSeries;
import com.jjoe64.graphview.series.DataPoint;
import android.graphics.Color;

import java.lang.ref.WeakReference;
import java.util.Set;

public class MainActivity extends AppCompatActivity {

    /*
     * Notifications from UsbService will be received here.
     */
    private final BroadcastReceiver mUsbReceiver = new BroadcastReceiver() {
        @Override
        public void onReceive(Context context, Intent intent) {
            switch (intent.getAction()) {
                case UsbService.ACTION_USB_PERMISSION_GRANTED: // USB PERMISSION GRANTED
                    Toast.makeText(context, "USB Ready", Toast.LENGTH_SHORT).show();
                    break;
                case UsbService.ACTION_USB_PERMISSION_NOT_GRANTED: // USB PERMISSION NOT GRANTED
                    Toast.makeText(context, "USB Permission not granted", Toast.LENGTH_SHORT).show();
                    break;
                case UsbService.ACTION_NO_USB: // NO USB CONNECTED
                    Toast.makeText(context, "No USB connected", Toast.LENGTH_SHORT).show();
                    break;
                case UsbService.ACTION_USB_DISCONNECTED: // USB DISCONNECTED
                    Toast.makeText(context, "USB disconnected", Toast.LENGTH_SHORT).show();
                    break;
                case UsbService.ACTION_USB_NOT_SUPPORTED: // USB NOT SUPPORTED
                    Toast.makeText(context, "USB device not supported", Toast.LENGTH_SHORT).show();
                    break;
            }
        }
    };
    private UsbService usbService;
    private TextView display;
    private EditText editText;
    private MyHandler mHandler;
    private final ServiceConnection usbConnection = new ServiceConnection() {
        @Override
        public void onServiceConnected(ComponentName arg0, IBinder arg1) {
            usbService = ((UsbService.UsbBinder) arg1).getService();
            usbService.setHandler(mHandler);
        }

        @Override
        public void onServiceDisconnected(ComponentName arg0) {
            usbService = null;
        }
    };

    public void weightPrf( View view ) {
        byte[] b = { (byte) 'w' };
        prfTbl = weight_tbl;
        usbService.write( b );

        DataPoint[] dp = new DataPoint[200];
        for(int i=0; i<200; i++){
            if(i<prfTbl.length) {
                dp[i] = new DataPoint(i, prfTbl[i]);
            }
            else {
                dp[i] = new DataPoint(i, prfTbl[prfTbl.length-1]);
            }
        }
        series.resetData( dp );
    }

    public void springPrf( View view ) {
        byte[] b = { (byte) 's' };
        prfTbl = spring_tbl;
        usbService.write( b );

        DataPoint[] dp = new DataPoint[200];
        for(int i=0; i<200; i++){
            if(i<prfTbl.length) {
                dp[i] = new DataPoint(i, prfTbl[i]);
            }
            else {
                dp[i] = new DataPoint(i, prfTbl[prfTbl.length-1]);
            }
        }
        series.resetData( dp );
    }

    public void inversePrf( View view ) {
        byte[] b = { (byte) 'i' };
        usbService.write( b );

        series2.resetData( new DataPoint[]{
                new DataPoint(40, prfTbl[40])
        });
    }

    public void mtnPrf( View view ) {
        byte[] b = { (byte) 'm' };
        usbService.write( b );

        series2.resetData( new DataPoint[]{
                new DataPoint(80, prfTbl[80])
        });
    }

    public void workoutPlus( View view ) {
        byte[] b = { (byte) '*' };
        usbService.write( b );
    }

    public void workoutMinus( View view ) {
        byte[] b = { (byte) '/' };
        usbService.write( b );
    }

    public void workoutPullPlus( View view ) {
        byte[] b = { (byte) 'p', (byte) '*' };
        usbService.write( b );
    }

    public void workoutPullMinus( View view ) {
        byte[] b = { (byte) 'p', (byte) '/' };
        usbService.write( b );
    }

    public void workoutRelPlus( View view ) {
        byte[] b = { (byte) 'r', (byte) '*' };
        usbService.write( b );
    }

    public void workoutRelMinus( View view ) {
        byte[] b = { (byte) 'r', (byte) '/' };
        usbService.write( b );
    }


    private int[] spring_tbl =
        {   0,   0,   0,   0,   0,   0,   0,   0,   0,   0,
            0,   1,   2,   3,   4,   5,   6,   7,   8,   9,
            10,  11,  12,  13,  14,  15,  16,  17,  18,  19,
            20,  21,  22,  23,  24,  25,  26,  27,  28,  29,
            30,  31,  32,  33,  34,  35,  36,  37,  38,  38,
            40,  41,  42,  43,  44,  45,  46,  47,  48,  49,
            50,  51,  52,  53,  54,  55,  56,  57,  58,  59,
            60,  61,  62,  63,  64,  65,  66,  67,  68,  69,
            70,  71,  72,  73,  74,  75,  76,  77,  78,  79,
            80,  81,  82,  83,  84,  85,  86,  87,  88,  89,
            90,  91,  92,  93,  94,  95,  96,  97,  98,  99,
            100, 101, 102, 103, 104, 105, 106, 107, 108, 109,
            110, 111, 112, 113, 114, 115, 116, 117, 118, 119,
            120, 121, 122, 123, 124, 125, 126, 127, 128, 129,
            130, 131, 132, 133, 134, 135, 136, 137, 138, 139,
            140, 141, 142, 143, 144, 145, 146, 147, 148, 149 };

    private static int W1 = 50;
    private static int W2 = 50;
    private static int W3 = 50;
    private static int W4 = 50;
    private int[] weight_tbl =
         {   0,   0,   0,   0,   0,   0,   0,   0,   0,   0,
            W1,  W1,  W1,  W1,  W1,  W1,  W1,  W1,  W1,  W1,
            W1,  W1,  W1,  W1,  W1,  W1,  W1,  W1,  W1,  W1,
            W2,  W2,  W2,  W2,  W2,  W2,  W2,  W2,  W2,  W2,
            W2,  W2,  W2,  W2,  W2,  W2,  W2,  W2,  W2,  W2,
            W3,  W3,  W3,  W3,  W3,  W3,  W3,  W3,  W3,  W3,
            W3,  W3,  W3,  W3,  W3,  W3,  W3,  W3,  W3,  W3,
            W4,  W4,  W4,  W4,  W4,  W4,  W4,  W4,  W4,  W4,
            W4,  W4,  W4,  W4,  W4,  W4,  W4,  W4,  W4,  W4 };

    private int[] prfTbl;
    private LineGraphSeries<DataPoint> series;
    private PointsGraphSeries<DataPoint> series2;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        mHandler = new MyHandler(this);

        display = (TextView) findViewById(R.id.textView1);
        editText = (EditText) findViewById(R.id.editText1);
        Button sendButton = (Button) findViewById(R.id.buttonSend);

        //Button weightPrf = (Button) findViewById(R.id.buttonWeightPrf);

        sendButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (!editText.getText().toString().equals("")) {
                    String data = editText.getText().toString();
                    if (usbService != null) { // if UsbService was correctly binded, Send data
                        usbService.write(data.getBytes());
                    }
                }
            }
        });

        prfTbl = weight_tbl;
        DataPoint[] dp = new DataPoint[200];
        for(int i=0; i<200; i++){
            if(i<prfTbl.length) {
                dp[i] = new DataPoint(i, prfTbl[i]);
            }
            else {
                dp[i] = new DataPoint(i, prfTbl[prfTbl.length-1]);
            }
        }

        GraphView graph = (GraphView) findViewById(R.id.graph);
        /*LineGraphSeries<DataPoint>*/
        series = new LineGraphSeries<DataPoint>(dp);
        graph.addSeries(series);
        series.setDrawBackground(true);
/*
         LineGraphSeries<DataPoint> series = new LineGraphSeries<DataPoint>(new DataPoint[] {

                new DataPoint(0, 1),
                new DataPoint(1, 5),
                new DataPoint(2, 3),
                new DataPoint(3, 2),
                new DataPoint(4, 6)
        });
*/
        /*PointsGraphSeries<DataPoint>*/
        series2 = new PointsGraphSeries<>(new DataPoint[]{
                new DataPoint(40, prfTbl[40])
        });
        graph.addSeries(series2);
        series2.setColor(Color.RED);

        graph.getViewport().setMinX(0);
        graph.getViewport().setMaxX(200);
        graph.getViewport().setMinY(0);
        graph.getViewport().setMaxY(300);

        graph.getViewport().setYAxisBoundsManual(true);
        graph.getViewport().setXAxisBoundsManual(true);
    }

    @Override
    public void onResume() {
        super.onResume();
        setFilters();  // Start listening notifications from UsbService
        startService(UsbService.class, usbConnection, null); // Start UsbService(if it was not started before) and Bind it
    }

    @Override
    public void onPause() {
        super.onPause();
        unregisterReceiver(mUsbReceiver);
        unbindService(usbConnection);
    }

    private void startService(Class<?> service, ServiceConnection serviceConnection, Bundle extras) {
        if (!UsbService.SERVICE_CONNECTED) {
            Intent startService = new Intent(this, service);
            if (extras != null && !extras.isEmpty()) {
                Set<String> keys = extras.keySet();
                for (String key : keys) {
                    String extra = extras.getString(key);
                    startService.putExtra(key, extra);
                }
            }
            startService(startService);
        }
        Intent bindingIntent = new Intent(this, service);
        bindService(bindingIntent, serviceConnection, Context.BIND_AUTO_CREATE);
    }

    private void setFilters() {
        IntentFilter filter = new IntentFilter();
        filter.addAction(UsbService.ACTION_USB_PERMISSION_GRANTED);
        filter.addAction(UsbService.ACTION_NO_USB);
        filter.addAction(UsbService.ACTION_USB_DISCONNECTED);
        filter.addAction(UsbService.ACTION_USB_NOT_SUPPORTED);
        filter.addAction(UsbService.ACTION_USB_PERMISSION_NOT_GRANTED);
        registerReceiver(mUsbReceiver, filter);
    }
    public void usbRxProc( String data )
    {
        String [] params = data.split(",",0);
        for (String element : params)
        {
            if( element.startsWith("dist") ) {
                String[] values = element.split( "=", 2);
                String[] val = values[1].split( "/", 2 );
                int distRight = Integer.parseInt(val[0]);
                int distLeft = Integer.parseInt(val[1]);
                if( distRight > prfTbl.length ) {
                    distRight = prfTbl.length-1;
                }
                if( distLeft > prfTbl.length ) {
                    distLeft = prfTbl.length-1;
                }
                series2.resetData( new DataPoint[]{
                        new DataPoint(distRight, prfTbl[distRight]) });
            }
            /*
            if( element.startsWith("prf") ) {

            }
            else if( element.startsWith("add") ) {

            }
            else if( element.startsWith("mult") ) {

            }
            */

        }
    }
    /*
     * This handler will be passed to UsbService.
     * Data received from serial port is displayed through this handler
     */
    private static class MyHandler extends Handler {
        private final WeakReference<MainActivity> mActivity;

        public MyHandler(MainActivity activity) {
            mActivity = new WeakReference<>(activity);
        }

        @Override
        public void handleMessage(Message msg) {
            switch (msg.what) {
                case UsbService.MESSAGE_FROM_SERIAL_PORT:
                    String data = (String) msg.obj;
                    mActivity.get().usbRxProc( data );
                    mActivity.get().display.append(data);
                    break;
                case UsbService.CTS_CHANGE:
                    Toast.makeText(mActivity.get(), "CTS_CHANGE",Toast.LENGTH_LONG).show();
                    break;
                case UsbService.DSR_CHANGE:
                    Toast.makeText(mActivity.get(), "DSR_CHANGE",Toast.LENGTH_LONG).show();
                    break;
            }
        }
    }
}