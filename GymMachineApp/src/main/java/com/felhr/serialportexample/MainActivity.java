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
    private TextView dbgDisplay;
    private boolean dbgDisplayOn;
    private TextView pullDisplay;
    private TextView relDisplay;
    private EditText editText;
    private MyHandler mHandler;
    private GraphView graph;
    private Button cycle;

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


    public void strengthTest(View view) {
        byte[] buf = {(byte) 't'};
        usbService.write(buf);
        prf = strengthTestPrf;
        prf.multPull = 15;
        prf.multRel = 15;
        graph.setTitle( prf.name );
        pullDisplay.setText("" + prf.multPull);
        relDisplay.setText("" + prf.multRel);
        pointsPull.resetData(prfDataPointsPull());
        pointsRel.resetData(prfDataPointsRel());
    }

    public void buttonCycle(View view) {
        cycleCnt++;
        if( cycleCnt > 5 ) {
            cycleCnt = 0;
        }
        cycle.setText( Integer.toString(cycleCnt) +  " Cycles" );
        cycleCurr = 0;
    }

    private void prfRightDirChange( Direction dir ) {
        if( prf.dirRight == Direction.REL && dir == Direction.PULL ) {
            if( cycleCnt != 0 ) {
                cycleCurr++;
                if( cycleCurr >= cycleCnt ) {
                    workoutPullMinus( cycleCurr-1 );
                    workoutRelMinus( cycleCurr-1 );
                    cycleCurr = 0;
                }
                else {
                    workoutPullPlus( 1 );
                    workoutRelPlus( 1 );
                }
            }
        }
        prf.dirRight = dir;
    }

    private void prfChange( workoutPrf prfPrm ) {
        prf = prfPrm;
        graph.setTitle( prf.name );
        pullDisplay.setText("" + prf.multPull);
        relDisplay.setText("" + prf.multRel);
        pointsPull.resetData(prfDataPointsPull());
        pointsRel.resetData(prfDataPointsRel());
        cycleCnt = 0;
        cycleCurr = 0;
        cycle.setText( Integer.toString(cycleCnt) +  " Cycles" );
    }

    public void weightPrf(View view) {
        byte[] buf = {(byte) 'w'};
        usbService.write(buf);
        prfChange( weightPref );
    }

    public void springPrf(View view) {
        byte[] buf = {(byte) 's'};
        usbService.write(buf);
        prfChange( springPref );
    }

    public void inversePrf(View view) {
        byte[] buf = {(byte) 'i'};
        usbService.write(buf);
        prfChange( invSpringPref );
    }

    public void mtnPrf(View view) {
        byte[] buf = {(byte) 'm'};
        usbService.write(buf);
        prfChange( mtnPref );
    }

    public void workoutPlus(View view) {
        byte[] buf = {(byte) '*'};
        usbService.write(buf);
    }

    public void workoutMinus(View view) {
        byte[] buf = {(byte) '/'};
        usbService.write(buf);
    }

    private void workoutPullPlus( int val ) {
        prf.multPull += val;
        pullDisplay.setText("" + prf.multPull);
        pointsPull.resetData(prfDataPointsPull());
        byte[] buf = {(byte) 'p', (byte) '*'};
        usbService.write(buf);
    }
    public void workoutPullPlus(View view) {
        workoutPullPlus( 1 );
    }

    private void workoutPullMinus( int val ) {
        byte[] buf = new byte[val+1];
        buf[0] = 'p';
        //{(byte) 'p', (byte) '/'};
        //usbService.write(buf);
        for( int i=0; i<val; i++ ) {
            buf[i+1] = '/';
        }
        usbService.write(buf);
        prf.multPull -= val;
        if (prf.multPull < 1) prf.multPull = 1;
        pullDisplay.setText("" + prf.multPull);
        pointsPull.resetData(prfDataPointsPull());
    }
    public void workoutPullMinus(View view) {
        workoutPullMinus( 1 );
    }

    private void workoutRelPlus( int val ) {
        prf.multRel += val;
        relDisplay.setText("" + prf.multRel);
        pointsRel.resetData(prfDataPointsRel());
        byte[] buf = {(byte) 'r', (byte) '*'};
        usbService.write(buf);
    }
    public void workoutRelPlus(View view) {
        workoutRelPlus( 1 );
    }

    private void workoutRelMinus( int val ) {
        byte[] buf = new byte[val+1];
        buf[0] = 'r';
        for( int i=0; i<val; i++ ) {
            buf[i+1] = '/';
        }
        usbService.write(buf);
        //byte[] buf = {(byte) 'r', (byte) '/'};
        //for( int i=0; i<val; i++ ) {
        //    usbService.write(buf);
        //}
        prf.multRel -= val;
        if (prf.multRel < 1) prf.multRel = 1;
        relDisplay.setText("" + prf.multRel);
        pointsRel.resetData(prfDataPointsRel());
    }
    public void workoutRelMinus(View view) {
        workoutRelMinus( 1 );
    }

    public enum Direction {
        PULL,
        REL
    }

    private class workoutPrf {
        private final int addPullInit;
        private final int addRelInit;
        private final int multPullInit;
        private final int multRelInit;

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


        workoutPrf( String Name,
                    int addPullInitPrm,
                    int addRelInitPrm,
                    int multPullInitPrm,
                    int multRelInitPrm,
                    int[] Tbl) {
            name = Name;
            addPullInit = addPullInitPrm;
            addRelInit = addRelInitPrm;
            multPullInit = multPullInitPrm;
            multRelInit = multRelInitPrm;
            tbl = Tbl;
            distRight = 0;
            distLeft = 0;
            dirRight = Direction.PULL;
            dirLeft = Direction.PULL;
            reset();
        }

        void reset() {
            addPull = addPullInit;
            addRel = addRelInit;
            multPull = multPullInit;
            multRel = multRelInit;
        }
    }

    private workoutPrf weightPref;
    private workoutPrf springPref;
    private workoutPrf invSpringPref;
    private workoutPrf mtnPref;
    private workoutPrf strengthTestPrf;
    private workoutPrf prf;
    private int        cycleCnt;
    private int        cycleCurr;

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
                    29,28,27,26,25,24,23,22,21,20 /*,
                    19,18,17,16,15,14,13,12,11,10,
                    9,8,7,6,5,4,3,2,1,0*/ };

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

    private int[] strength_test_tbl =
                   { 0,   0,   0,   0,   0,   0,   0,   0,   0,   0,
                     0,   0,   0,   0,   0,   0,   0,   0,   0,   0,
                     0,   0,   0,   0,   0,   0,   0,   0,   0,   0,
                     0,   0,   0,   0,   0,   0,   0,   0,   0,   0,
                     0,   0,   0,   0,   0,   0,   0,   0,   0,   0,
                     0,   0,   0,   0,   0,   0,   0,   0,   0,   0,
                    10,  10,  10,  10,  10,  10,  10,  10,  10,  10,
                    10,  10,  10,  10,  10,  10,  10,  10,  10,  10,
                    10,  10,  10,  10,  10,  10,  10,  10,  10,  10,
                    10,  10,  10,  10,  10,  10,  10,  10,  10,  10,
                    10,  10,  10,  10,  10,  10,  10,  10,  10,  10,
                    10,  10,  10,  10,  10,  10,  10,  10,  10,  10,
                    10,  10,  10,  10,  10,  10,  10,  10,  10,  10,
                    10,  10,  10,  10,  10,  10,  10,  10,  10,  10,
                     0,   0,   0,   0,   0,   0,   0,   0,   0,   0,
                     0,   0,   0,   0,   0,   0,   0,   0,   0,   0,
                    };

    private double torqueToPound( int x ) {
        //double y = 0.0707x + 15.886
        double y = 0.09*x + 5.0;
        //double y = -(8E-05)*x*x + 0.1494*x - 1.1517;
        return y;
    }

    private DataPoint[] prfDataPointsPull() {
        DataPoint[] dp = new DataPoint[200];
        for (int i = 0; i < 200; i++) {
            int len = i;
            if (len >= prf.tbl.length) {
                len = prf.tbl.length - 1;
            }
            dp[i] = new DataPoint(i, torqueToPound(prf.tbl[len] * prf.multPull));
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
            dp[i] = new DataPoint(i, torqueToPound(prf.tbl[len] * prf.multRel));
        }
        return dp;
    }

    private DataPoint[] currentPoint(int point, Direction dir) {
        if (point < 0) point = 0;
        int pointChart = point;
        if (pointChart >= prf.tbl.length) pointChart = prf.tbl.length - 1;
        int mult = prf.multPull;
        if ( dir == Direction.REL ) mult = prf.multRel;
        DataPoint[] dp = {new DataPoint(point, torqueToPound(prf.tbl[pointChart] * mult))};
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

        dbgDisplay = (TextView) findViewById(R.id.textView1);
        dbgDisplayOn = false;
        dbgDisplay.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                dbgDisplayOn = !dbgDisplayOn;
            }
        });

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

        weightPref = new workoutPrf("Weight", 0,0,4,4, weight_tbl);
        springPref = new workoutPrf("Spring", 0,0,4,4, spring_tbl);
        invSpringPref = new workoutPrf("Inverse Spring", 0,0,2,2, inv_spring_tbl);
        mtnPref = new workoutPrf("Mountain", 0,0,4,4, mtn_tbl);
        strengthTestPrf = new workoutPrf("Strength Test", 0,0,4,4, strength_test_tbl);

        prf = weightPref;
        cycleCnt = 0;
        cycleCurr = 0;
        cycle = (Button) findViewById(R.id.buttonCycle);

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
        graph.getViewport().setMaxY(80);

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

    boolean testFinished = false;
    public void usbRxProc(String data) {
        if( dbgDisplayOn ) {
            if( dbgDisplay.getText().length() > 1000 ) {
                dbgDisplay.setText(dbgDisplay.getText().subSequence(0,500));
            }
            dbgDisplay.append(data);
        }

        if( prf == strengthTestPrf ) {
            if( data.contains("test torque=") ) {
                testFinished = false;
                String[] params = data.split("test torque=", 0);
                if( params.length == 2 ) {
                    try {
                      int torque = Integer.parseInt(params[1].trim());
                      prf.multPull = torque/10;
                      pullDisplay.setText("" + prf.multPull);
                      pointsPull.resetData(prfDataPointsPull());
                      graph.setTitle( "Strength Test, " + Integer.toString(prf.multPull) + "lb" );
                    }
                    catch( NumberFormatException e) {
                      //Do nothing
                    }
                }
            }
            else if( data.contains("Strength test done") ) {
                if( !testFinished ) {
                    String str = new String( "Test Done");
                    Toast.makeText(this, str, Toast.LENGTH_LONG).show();
                    testFinished = true;
                    graph.setTitle( str );
                    graph.setTitle( str );
                }
            }
        }
        else {
            String[] params = data.split(", ", 0);
            for (String element : params) {
                if (element.startsWith("dist")) {
                    String[] values = element.split("=", 0);
                    if (values.length > 1) {
                        String[] val = values[1].split("/", 0);
                        if (val.length > 1) {
                            try {
                                int distRight = Integer.parseInt(val[0]);
                                if( distRight > prf.distRight ) {
                                    //prf.dirRight = Direction.PULL;
                                    prfRightDirChange( Direction.PULL );
                                }
                                else if( distRight < prf.distRight ) {
                                    //prf.dirRight = Direction.REL;
                                    prfRightDirChange( Direction.REL );
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
                            }
                            catch( NumberFormatException e) {
                                // Do nothing
                            }
                        }
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
                    //mActivity.get().dbgDisplay.append(data);
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