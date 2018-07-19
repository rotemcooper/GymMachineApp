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
    private TextView pullDisplay;
    private TextView relDisplay;
    private EditText editText;
    private MyHandler mHandler;
    private GraphView graph;

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

    public void weightPrf(View view) {
        byte[] buf = {(byte) 'w'};
        usbService.write(buf);
        prf = weightPref;
        graph.setTitle( prf.name );
        pullDisplay.setText("" + prf.multPull);
        relDisplay.setText("" + prf.multRel);
        pointsPull.resetData(prfDataPointsPull());
        pointsRel.resetData(prfDataPointsRel());
    }

    public void springPrf(View view) {
        byte[] buf = {(byte) 's'};
        usbService.write(buf);
        prf = springPref;
        graph.setTitle( prf.name );
        pullDisplay.setText("" + prf.multPull);
        relDisplay.setText("" + prf.multRel);
        pointsPull.resetData(prfDataPointsPull());
        pointsRel.resetData(prfDataPointsRel());
    }

    public void inversePrf(View view) {
        byte[] buf = {(byte) 'i'};
        usbService.write(buf);
        prf = invSpringPref;
        graph.setTitle( prf.name );
        pullDisplay.setText("" + prf.multPull);
        relDisplay.setText("" + prf.multRel);
        pointsPull.resetData(prfDataPointsPull());
        pointsRel.resetData(prfDataPointsRel());
        //pointRight.resetData(currentPoint(40, 0));
    }

    public void mtnPrf(View view) {
        byte[] buf = {(byte) 'm'};
        usbService.write(buf);
        prf = mtnPref;
        graph.setTitle( prf.name );
        pullDisplay.setText("" + prf.multPull);
        relDisplay.setText("" + prf.multRel);
        pointsPull.resetData(prfDataPointsPull());
        pointsRel.resetData(prfDataPointsRel());
        //pointRight.resetData(currentPoint(80, 0));
    }

    public void workoutPlus(View view) {
        byte[] buf = {(byte) '*'};
        usbService.write(buf);
    }

    public void workoutMinus(View view) {
        byte[] buf = {(byte) '/'};
        usbService.write(buf);
    }

    public void workoutPullPlus(View view) {
        prf.multPull += 1;
        pullDisplay.setText("" + prf.multPull);
        pointsPull.resetData(prfDataPointsPull());
        byte[] buf = {(byte) 'p', (byte) '*'};
        usbService.write(buf);
    }

    public void workoutPullMinus(View view) {
        if (prf.multPull > 1) prf.multPull -= 1;
        pullDisplay.setText("" + prf.multPull);
        pointsPull.resetData(prfDataPointsPull());
        byte[] buf = {(byte) 'p', (byte) '/'};
        usbService.write(buf);
    }

    public void workoutRelPlus(View view) {
        prf.multRel += 1;
        relDisplay.setText("" + prf.multRel);
        pointsRel.resetData(prfDataPointsRel());
        byte[] buf = {(byte) 'r', (byte) '*'};
        usbService.write(buf);
    }

    public void workoutRelMinus(View view) {
        if (prf.multRel > 1) prf.multRel -= 1;
        relDisplay.setText("" + prf.multRel);
        pointsRel.resetData(prfDataPointsRel());
        byte[] buf = {(byte) 'r', (byte) '/'};
        usbService.write(buf);
    }

    public enum Direction {
        PULL,
        REL
    }

    private class workoutPrf {
        String name;
        int addPull;
        int addRel;
        int multPull;
        int multRel;
        int[] tbl;
        int distRight;
        int distLeft;
        Direction dirRight;
        Direction dirLeft;


        workoutPrf(String Name, int[] Tbl) {
            name = Name;
            addPull = 0;
            addRel = 0;
            multPull = 4;
            multRel = 4;
            tbl = Tbl;
            distRight = 0;
            distLeft = 0;
            dirRight = Direction.PULL;
            dirLeft = Direction.PULL;
        }
    }

    private workoutPrf weightPref;
    private workoutPrf springPref;
    private workoutPrf invSpringPref;
    private workoutPrf mtnPref;
    private workoutPrf prf;

    private static int W1 = 50;
    private int[] weight_tbl =
                   {/*0, 0, 0, 0, 0, 0, 0, 0,*/ 0, 0,
                    W1, W1, W1, W1, W1, W1, W1, W1, W1, W1,
                    W1, W1, W1, W1, W1, W1, W1, W1, W1, W1,
                    W1, W1, W1, W1, W1, W1, W1, W1, W1, W1,
                    W1, W1, W1, W1, W1, W1, W1, W1, W1, W1,
                    W1, W1, W1, W1, W1, W1, W1, W1, W1, W1,
                    W1, W1, W1, W1, W1, W1, W1, W1, W1, W1,
                    W1, W1, W1, W1, W1, W1, W1, W1, W1, W1,
                    W1, W1, W1, W1, W1, W1, W1, W1, W1, W1};

    private int[] spring_tbl =
                   {/*0, 0, 0, 0, 0, 0, 0, 0, 0, 0,*/
                    0, 1, 2, 3, 4, 5, 6, 7, 8, 9,
                    10, 11, 12, 13, 14, 15, 16, 17, 18, 19,
                    20, 21, 22, 23, 24, 25, 26, 27, 28, 29,
                    30, 31, 32, 33, 34, 35, 36, 37, 38, 39,
                    40, 41, 42, 43, 44, 45, 46, 47, 48, 49,
                    50, 51, 52, 53, 54, 55, 56, 57, 58, 59,
                    60, 61, 62, 63, 64, 65, 66, 67, 68, 69,
                    70, 71, 72, 73, 74, 75, 76, 77, 78, 79,
                    80, 81, 82, 83, 84, 85, 86, 87, 88, 89,
                    90, 91, 92, 93, 94, 95, 96, 97, 98, 99,
                    100, 101, 102, 103, 104, 105, 106, 107, 108, 109,
                    110, 111, 112, 113, 114, 115, 116, 117, 118, 119,
                    120, 121, 122, 123, 124, 125, 126, 127, 128, 129,
                    130, 131, 132, 133, 134, 135, 136, 137, 138, 139,
                    140, 141, 142, 143, 144, 145, 146, 147, 148, 149};

    private int[] inv_spring_tbl =
                   {/*  0,  0,  0,  0,  0,  0,  0,  0,*/  0,  0,
                    149,148,147,146,145,144,143,142,141,140,
                    139,138,137,136,135,134,133,132,131,130,
                    129,128,127,126,125,124,123,122,121,120,
                    119,118,117,116,115,114,113,112,111,110,
                    109,108,107,106,105,104,103,102,101,100,
                    99,98,97,96,95,94,93,92,91,90,
                    89,88,87,86,85,84,83,82,81,80,
                    79,78,77,76,75,74,73,72,71,70,
                    69,68,67,66,65,64,63,62,61,60,
                    59,58,57,56,55,54,53,52,51,50,
                    49,48,47,46,45,44,43,42,41,40,
                    39,38,37,36,35,34,33,32,31,30,
                    29,28,27,26,25,24,23,22,21,20,
                    19,18,17,16,15,14,13,12,11,10,
                    9,8,7,6,5,4,3,2,1,0 };

    private int[] mtn_tbl =
                   {/* 0,   0,   0,   0,   0,   0,   0,   0,*/   0,   0,
                    50,  52,  54,  56,  58,  60,  62,  64,  66,  68,
                    70,  72,  74,  76,  78,  80,  82,  84,  86,  88,
                    90,  92,  94,  96,  98, 100, 102, 104, 106, 108,
                   110, 112, 114, 116, 118, 120, 122, 124, 126, 128,
                   128, 126, 124, 122, 120, 118, 116, 114, 112, 110,
                   108, 106, 104, 102, 100,  98,  96,  94,  92,  90,
                    88,  86,  84,  82,  80,  78,  76,  74,  72,  70,
                    68,  66,  64,  62,  60,  58,  56,  54,  52,  50 };

    private DataPoint[] prfDataPointsPull() {
        DataPoint[] dp = new DataPoint[200];
        for (int i = 0; i < 200; i++) {
            int len = i;
            if (len >= prf.tbl.length) {
                len = prf.tbl.length - 1;
            }
            dp[i] = new DataPoint(i, prf.tbl[len] * prf.multPull);
        }
        return dp;
    }

    private DataPoint[] prfDataPointsRel() {
        DataPoint[] dp = new DataPoint[200];
        for (int i = 0; i < 200; i++) {
            int len = i;
            if (len >= prf.tbl.length) {
                len = prf.tbl.length - 1;
            }
            dp[i] = new DataPoint(i, prf.tbl[len] * prf.multRel);
        }
        return dp;
    }

    private DataPoint[] currentPoint(int point, Direction dir) {
        if (point < 0) point = 0;
        if (point >= prf.tbl.length) point = prf.tbl.length - 1;
        int mult = prf.multPull;
        if ( dir == Direction.REL ) mult = prf.multRel;
        DataPoint[] dp = {new DataPoint(point, prf.tbl[point] * mult)};
        return dp;
    }

    /*
    private int[] prfTbl = weight_tbl;
    int     addPull=0;
    int     addRel=0;
    int     multPull=4;
    int     multRel=4;
    */

    private LineGraphSeries<DataPoint> pointsPull;
    private LineGraphSeries<DataPoint> pointsRel;
    private PointsGraphSeries<DataPoint> pointRight;
    private PointsGraphSeries<DataPoint> pointLeft;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        mHandler = new MyHandler(this);

        display = (TextView) findViewById(R.id.textView1);
        editText = (EditText) findViewById(R.id.editText1);
        pullDisplay = (TextView) findViewById(R.id.textViewPull);
        relDisplay = (TextView) findViewById(R.id.textViewRel);
        Button sendButton = (Button) findViewById(R.id.buttonSend);

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

        weightPref = new workoutPrf("Weight", weight_tbl);
        springPref = new workoutPrf("Spring", spring_tbl);
        invSpringPref = new workoutPrf("Inverse Spring", inv_spring_tbl);
        mtnPref = new workoutPrf("Mountain", mtn_tbl);

        prf = weightPref;

        pullDisplay = (TextView) findViewById(R.id.textViewPull);
        pullDisplay.setText("" + prf.multPull);
        relDisplay = (TextView) findViewById(R.id.textViewRel);
        relDisplay.setText("" + prf.multRel);

         // Create and title the graph
        graph = (GraphView) findViewById(R.id.graph);
        graph.setTitle( prf.name );
        graph.setTitleTextSize( 60 );

        // Draw workout profile line for pull
        pointsPull = new LineGraphSeries<DataPoint>(prfDataPointsPull());
        graph.addSeries(pointsPull);
        pointsPull.setDrawBackground(true);

        // Draw workout profile line for release
        pointsRel = new LineGraphSeries<DataPoint>(prfDataPointsRel());
        graph.addSeries(pointsRel);
        pointsRel.setDrawBackground(true);

        // Draw workout points for right and left cables
        pointRight = new PointsGraphSeries<>(currentPoint(0, Direction.PULL));
        graph.addSeries(pointRight);
        pointRight.setColor(Color.RED);

        pointLeft = new PointsGraphSeries<>(currentPoint(0, Direction.PULL));
        graph.addSeries(pointLeft);
        pointLeft.setColor(Color.BLUE);

        graph.getViewport().setMinX(0);
        graph.getViewport().setMaxX(200);
        graph.getViewport().setMinY(0);
        graph.getViewport().setMaxY(600);

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

    public void usbRxProc(String data) {
        String[] params = data.split(", ", 0);
        for (String element : params) {
            if (element.startsWith("dist")) {
                String[] values = element.split("=", 0);
                if (values.length > 1) {
                    String[] val = values[1].split("/", 0);
                    if (val.length > 1) {
                        //editText.setText( val[1] );
                        //try {
                        int distRight = Integer.parseInt(val[0]);
                        if( distRight > prf.distRight ) {
                            prf.dirRight = Direction.PULL;
                        }
                        else if( distRight < prf.distRight ) {
                            prf.dirRight = Direction.REL;
                        }

                        int distLeft = Integer.parseInt(val[1]);
                        if( distLeft > prf.distLeft ) {
                            prf.dirLeft = Direction.PULL;
                        }
                        else if( distLeft < prf.distLeft ) {
                            prf.dirLeft = Direction.REL;
                        }

                        pointRight.resetData(currentPoint(distRight, prf.dirRight));
                        pointLeft.resetData(currentPoint(distLeft, prf.dirLeft));
                        prf.distRight = distRight;
                        prf.distLeft = distLeft;
                        //}
                        //catch( NumberFormatException e) {
                        // Do nothing
                        //}
                    }
                }
            }
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
                case UsbService.SYNC_READ:
                    String data = (String) msg.obj;
                    mActivity.get().usbRxProc(data);
                    //mActivity.get().display.append(data);
                    break;
                case UsbService.CTS_CHANGE:
                    Toast.makeText(mActivity.get(), "CTS_CHANGE", Toast.LENGTH_LONG).show();
                    break;
                case UsbService.DSR_CHANGE:
                    Toast.makeText(mActivity.get(), "DSR_CHANGE", Toast.LENGTH_LONG).show();
                    break;
            }
        }
    }
}