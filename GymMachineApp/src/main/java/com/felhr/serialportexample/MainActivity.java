package com.felhr.serialportexample;

import android.content.BroadcastReceiver;
import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.ServiceConnection;
import android.graphics.drawable.ColorDrawable;
import android.net.Uri;
import android.os.Bundle;
import android.os.Handler;
import android.os.IBinder;
import android.os.Message;
import android.provider.ContactsContract;
import android.support.v4.app.FragmentManager;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.Gravity;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;
import com.jjoe64.graphview.GraphView;
import com.jjoe64.graphview.series.LineGraphSeries;
import com.jjoe64.graphview.series.PointsGraphSeries;
import com.jjoe64.graphview.series.DataPoint;

import android.graphics.Color;
import android.widget.VideoView;

import java.lang.ref.WeakReference;
import java.util.ArrayList;
import java.util.ListIterator;
import java.util.Set;

public class MainActivity extends AppCompatActivity implements AdapterView.OnItemSelectedListener, WorkoutAdapter.ItemClickListener {

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
    private RecyclerView workoutRecyclerView;
    private RecyclerView.LayoutManager workoutLayoutManager;
    private Button cycle;
    private Button reps;
    private Button buttonVideoStart;
    private int trainerID = -1;
    private String trainerDisplayControl = new String( "PHOTO" );
    private WorkoutAdapter workoutAdapter;

    private final ServiceConnection usbConnection = new ServiceConnection() {
        @Override
        public void onServiceConnected(ComponentName arg0, IBinder arg1) {
            usbService = ((UsbService.UsbBinder) arg1).getService();
            usbService.setHandler(mHandler);
            // Zero out arduino profile settings.
            byte[] buf = {(byte) 's', (byte) '0', (byte) 'i', (byte) '0',
                          (byte) 'm', (byte) '0', (byte) 'w', (byte) '0' };
            usbService.write(buf);
            setPrf( prf );
        }

        @Override
        public void onServiceDisconnected(ComponentName arg0) {
            usbService = null;
        }
    };

    private void myToast( String text, int length ) {
        Toast toast = Toast.makeText(this, text, length);
        ViewGroup group = (ViewGroup) toast.getView();
        TextView messageTextView = (TextView) group.getChildAt(0);
        messageTextView.setTextSize(45);
        TextView v = (TextView) toast.getView().findViewById(android.R.id.message);
        if( v != null) v.setGravity(Gravity.CENTER);
        toast.setGravity(Gravity.CENTER|Gravity.CENTER, 0, 0);
        toast.show();
    }


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

    public void sendTextMessage (View view) {
        FragmentManager fm = getSupportFragmentManager();
        SendMessageFragment sendMessage = new SendMessageFragment( "Brandon");
        sendMessage.show(fm, "fragment_send_message");
    }

    public void buttonCycle(View view) {
        cyclesMax++;
        if( cyclesMax > 8 ) {
            cyclesMax = 1;
        }
        cycle.setText( Integer.toString(cyclesMax) +  " Sets" );
        cycleCnt = 1;
    }
    
    public void buttonReps(View view) {
        repsMax++;
        if( repsMax > 10 ) {
            repsMax = 1;
        }
        reps.setText( Integer.toString(repsMax) +  " Reps" );
        repCnt = 0;
    }

    private void prfRightDirChange( WorkoutPrf.Direction dir ) {
        if( prf.dirRight == WorkoutPrf.Direction.REL && dir == WorkoutPrf.Direction.PULL ) {

            prf.reps++;

            if( prf.isRepsMax() ) {
                //if( workoutItr.hasNext() ) {
                if( workoutListPos+1 < workoutList.size() ) {
                    myToast( "Segment Completed\n Select next segment below", Toast.LENGTH_LONG);
                    //setPrf( workoutItr.next(), false );
                }
                else {
                    myToast( "Excellent job!\nYou completed the workout", Toast.LENGTH_LONG);
                }
                //rotemc
            }
            reps.setText( "Reps " + Integer.toString(prf.reps) + ":" +
                    Integer.toString(prf.repsMax) );

            if( cyclesMax > 1 ) {
                repCnt++;
                if( repCnt > repsMax ) {
                    repCnt = 1;
                    cycleCnt++;
                    if( cycleCnt > cyclesMax ) {
                        workoutPullMinus( cycleCnt-2 );
                        workoutRelMinus( cycleCnt-2 );
                        cycleCnt = 1;
                    }
                    else {
                        workoutPullPlus( 1 );
                        workoutRelPlus( 1 );
                    }
                }
            }
        }
        prf.dirRight = dir;
    }

    private void prfChange( WorkoutPrf prfPrm ) {
        prf = prfPrm;
        graph.setTitle( prf.name );
        pullDisplay.setText("" + prf.multPull);
        relDisplay.setText("" + prf.multRel);
        pointsPull.resetData(prfDataPointsPull());
        pointsRel.resetData(prfDataPointsRel());
        cyclesMax = 1;
        cycleCnt = 1;
        repsMax = 1;
        repCnt = 0;
        cycle.setText( Integer.toString(cyclesMax) +  " Sets" );
        reps.setText( Integer.toString(cyclesMax) +  " Reps" );
    }

    public void videoStart(View view) {
//rotemc
        VideoView trainerVideoView = (VideoView) findViewById(R.id.videoView);
        trainerVideoView.start();
        buttonVideoStart.setVisibility( View.INVISIBLE );
    }

    public void weightPrf(View view) {
        setPrf( weightPref );
      /*
        byte[] buf = {(byte) 'w'};
        usbService.write(buf);
        prfChange( weightPref );*/
    }

    public void springPrf(View view) {
        setPrf( springPref );
        /*
        byte[] buf = {(byte) 's'};
        usbService.write(buf);
        prfChange( springPref );*/
    }

    public void inversePrf(View view) {
        setPrf( invSpringPref );
        /*
        byte[] buf = {(byte) 'i'};
        usbService.write(buf);
        prfChange( invSpringPref );*/
    }

    public void mtnPrf(View view) {
        setPrf( mtnPref );
        /*
        byte[] buf = {(byte) 'm'};
        usbService.write(buf);
        prfChange( mtnPref );*/
    }

    private static byte dig3(int x) { return (byte)('0' + (x/100)%10); }
    private static byte dig2(int x) { return (byte)('0' + (x/10)%10); }
    private static byte dig1(int x) { return (byte)('0' + x%10); }

    public void setPrf(WorkoutPrf p ) {setPrf (p, true); }
    public void setPrf(WorkoutPrf p, boolean isForcePrfChange ) {
        if (usbService != null) {
            if( p != prf || isForcePrfChange ) {
                byte[] buff = {(byte) Character.toLowerCase(p.name.charAt(0)) };
                usbService.write(buff);
            }
            byte[] buf = {
                    (byte) 'p', (byte) '*', dig3(p.multPull), dig2(p.multPull), dig1(p.multPull),
                    (byte) 'r', (byte) '*', dig3(p.multRel), dig2(p.multRel), dig1(p.multRel),
                    (byte) 'p', (byte) '+', dig3(p.addPull), dig2(p.addPull), dig1(p.addPull),
                    (byte) 'r', (byte) '+', dig3(p.addRel), dig2(p.addRel), dig1(p.addRel) };
            usbService.write(buf);
            //myToast( new String(buf), Toast.LENGTH_LONG );
            prfChange( p );
            setTrainerDisplay( true );
        }
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
        byte[] buf = new byte[val+1];
        buf[0] = 'p';
        for( int i=0; i<val; i++ ) {
            buf[i+1] = '*';
        }
        usbService.write(buf);
        prf.multPull += val;
        pullDisplay.setText("" + prf.multPull);
        pointsPull.resetData(prfDataPointsPull());
    }
    public void workoutPullPlus(View view) {
        workoutPullPlus( 1 );
    }

    private void workoutPullMinus( int val ) {
        byte[] buf = new byte[val+1];
        buf[0] = 'p';
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
        /*prf.multRel += val;
        relDisplay.setText("" + prf.multRel);
        pointsRel.resetData(prfDataPointsRel());
        byte[] buf = {(byte) 'r', (byte) '*'};
        usbService.write(buf); */
        byte[] buf = new byte[val+1];
        buf[0] = 'r';
        for( int i=0; i<val; i++ ) {
            buf[i+1] = '*';
        }
        usbService.write(buf);
        prf.multRel += val;
        relDisplay.setText("" + prf.multRel);
        pointsRel.resetData(prfDataPointsRel());
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
        prf.multRel -= val;
        if (prf.multRel < 1) prf.multRel = 1;
        relDisplay.setText("" + prf.multRel);
        pointsRel.resetData(prfDataPointsRel());
    }
    public void workoutRelMinus(View view) {
        workoutRelMinus( 1 );
    }

    private WorkoutPrf weightPref;
    private WorkoutPrf springPref;
    private WorkoutPrf invSpringPref;
    private WorkoutPrf mtnPref;
    private WorkoutPrf strengthTestPrf;
    private WorkoutPrf prf;
    ArrayList<WorkoutPrf> workoutList;
    int workoutListPos;
    ListIterator<WorkoutPrf> workoutItr;

    private int        cyclesMax;
    private int        cycleCnt;
    private int        repsMax;
    private int        repCnt;

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

    private DataPoint[] currentPoint(int point, WorkoutPrf.Direction dir) {
        if (point < 0) point = 0;
        int pointChart = point;
        if (pointChart >= prf.tbl.length) pointChart = prf.tbl.length - 1;
        int mult = prf.multPull;
        if ( dir == WorkoutPrf.Direction.REL ) mult = prf.multRel;
        DataPoint[] dp = {new DataPoint(point, torqueToPound(prf.tbl[pointChart] * mult))};
        return dp;
    }

    private LineGraphSeries<DataPoint> pointsPull;
    private LineGraphSeries<DataPoint> pointsRel;
    private PointsGraphSeries<DataPoint> pointRight;
    private PointsGraphSeries<DataPoint> pointLeft;
    private Spinner trainerSpinner;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        android.support.v7.app.ActionBar bar = getSupportActionBar();
        if( bar != null ) {
            bar.setBackgroundDrawable(new ColorDrawable(getResources().getColor(R.color.colorPrimary)));
        }

        //-----------------------------------------------------------------------

        mHandler = new MyHandler(this);

        dbgDisplay = (TextView) findViewById(R.id.textView1);
        dbgDisplayOn = false;
        dbgDisplay.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                dbgDisplayOn = !dbgDisplayOn;
            }
        });
        dbgDisplay.setVisibility(View.INVISIBLE);

        // View for sending characters to machine
        editText = (EditText) findViewById(R.id.editText1);
        editText.setVisibility(View.INVISIBLE);

        pullDisplay = (TextView) findViewById(R.id.textViewPull);
        relDisplay = (TextView) findViewById(R.id.textViewRel);
        Button sendButton = (Button) findViewById(R.id.buttonSend);

        //-----------------------------------------------------------------------

        // Load activity type data with ArrayAdapter using the string array and a spinner layout
        ArrayAdapter<CharSequence> trainerAdapter = ArrayAdapter.createFromResource(this,
                R.array.trainerMenu, R.layout.main_trainer_spinner);

        // Specify the layout to use when the list of choices appears
        trainerAdapter.setDropDownViewResource(R.layout.workout_selector_spinner_dropdown);

        // Apply the adapter to the spinner
        trainerSpinner = (Spinner) findViewById(R.id.spinnerTrainer);
        trainerSpinner.setAdapter(trainerAdapter);

        // Set listener
        //trainerSpinner.setSelection(0,false);
        trainerSpinner.setOnItemSelectedListener(this);

        //-----------------------------------------------------------------------

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

        weightPref = new WorkoutPrf("Weight", 0,0,4,4, WorkoutPrf.WEIGHT_TBL );
        springPref = new WorkoutPrf("Spring", 0,0,4,4, WorkoutPrf.SPRING_TBL);
        invSpringPref = new WorkoutPrf("Inverse Spring", 0,0,2,2, WorkoutPrf.INV_SPRING_TBL);
        mtnPref = new WorkoutPrf("Mountain", 0,0,4,4, WorkoutPrf.MTN_TBL);
        strengthTestPrf = new WorkoutPrf("Strength Test", 0,0,4,4, WorkoutPrf.STRENGTH_TEST_TBL);

        //------------------------------------------------------------------------------

        // Set the trainer ID if provided in intent (default is -1).
        //Toast.makeText(this, String.valueOf(trainerID), Toast.LENGTH_LONG).show();
        workoutRecyclerView = (RecyclerView) findViewById(R.id.workoutRecycler);
        // Improve performance (if changes in content do not change the layout
        // size of the RecyclerView)
        workoutRecyclerView.setHasFixedSize(true);

        Intent intent = getIntent();
        trainerID = intent.getIntExtra("TrainerID", -1);
        if( trainerID == -1 ) {
            workoutRecyclerView.setVisibility( View.INVISIBLE );
        }

        // Set Workout profile based on intent if provided. Else set to default.
        if (intent.hasExtra("Workout")) {
            workoutList = (ArrayList<WorkoutPrf>) getIntent().getSerializableExtra("Workout");

            workoutLayoutManager = new LinearLayoutManager(this, LinearLayoutManager.HORIZONTAL, false);
            workoutRecyclerView.setLayoutManager(workoutLayoutManager);

            // Specify workout adapter
            workoutAdapter = new WorkoutAdapter(this, workoutList);
            workoutRecyclerView.setAdapter(workoutAdapter);

            // Set onClickListener
            workoutAdapter.setClickListener(this);
        }
        else {
            workoutList = new ArrayList<WorkoutPrf>();
            workoutList.add(weightPref);
        }
        workoutItr = workoutList.listIterator();
        prf = workoutItr.next();

        //------------------------------------------------------------------------------

        cyclesMax = 1;
        cycleCnt = 1;
        cycle = (Button) findViewById(R.id.buttonCycle);

        repsMax = 1;
        repCnt = 0;
        reps = (Button) findViewById(R.id.buttonReps);
        buttonVideoStart = (Button) findViewById(R.id.buttonVideoStart);
        buttonVideoStart.setVisibility( View.INVISIBLE );

        pullDisplay = (TextView) findViewById(R.id.textViewPull);
        pullDisplay.setText("" + prf.multPull);
        relDisplay = (TextView) findViewById(R.id.textViewRel);
        relDisplay.setText("" + prf.multRel);

         // Create and title the graph
        graph = (GraphView) findViewById(R.id.graph);

        graph.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                VideoView trainerVideoView = (VideoView) findViewById(R.id.videoView);
                trainerVideoView.pause();
                buttonVideoStart.setVisibility( View.VISIBLE );
                //rotemc
            }
        });


        graph.setTitle( prf.name );
        graph.setTitleTextSize( 60 );
        graph.setTitleColor( Color.WHITE );

        graph.getGridLabelRenderer().setGridColor(0xFFFF7900);
        graph.getGridLabelRenderer().setHorizontalLabelsColor(0xFFFF7900);
        graph.getGridLabelRenderer().setVerticalLabelsColor(0xFFFF7900);
        graph.getGridLabelRenderer().setTextSize(50);
        //graph.getGridLabelRenderer().setHorizontalAxisTitleColor(Color.WHITE);

        // Draw workout profile line for pull
        pointsPull = new LineGraphSeries<DataPoint>(prfDataPointsPull());
        graph.addSeries(pointsPull);
        pointsPull.setDrawBackground(true);

        // Draw workout profile line for release
        pointsRel = new LineGraphSeries<DataPoint>(prfDataPointsRel());
        graph.addSeries(pointsRel);
        pointsRel.setDrawBackground(true);

        // Draw workout points for right and left cables
        pointRight = new PointsGraphSeries<>(currentPoint(0, WorkoutPrf.Direction.PULL));
        graph.addSeries(pointRight);
        pointRight.setColor(Color.GREEN);
        pointRight.setSize( 25 );

        pointLeft = new PointsGraphSeries<>(currentPoint(0, WorkoutPrf.Direction.PULL));
        graph.addSeries(pointLeft);
        pointLeft.setColor(Color.BLUE);
        pointLeft.setSize( 25 );

        graph.getViewport().setMinX(0);
        graph.getViewport().setMaxX(200);
        graph.getViewport().setMinY(0);
        graph.getViewport().setMaxY(80);

        graph.getViewport().setYAxisBoundsManual(true);
        graph.getViewport().setXAxisBoundsManual(true);

        //------------------------------------------------------------------

        // Display trainer image/video as needed
        setPrf( prf );
        //setTrainerDisplay( true );

        reps.setText( "Reps " + Integer.toString(prf.reps) + ":" +
                Integer.toString(prf.repsMax) );
    }

    //-----------------------------------------------------------------------------------------

    // Set up the trainer video, audio and photo based on user selection
    private void setTrainerDisplay( boolean isNewSegment ) {
        ImageView trainerImageView = (ImageView) findViewById(R.id.imageTrainer);
        VideoView trainerVideoView = (VideoView) findViewById(R.id.videoView);

        if( trainerID < 0 )  {
            // No trainer provided,remove trainer photo and video views
            trainerImageView.setVisibility( View.INVISIBLE );
            trainerVideoView.setVisibility( View.INVISIBLE );
            buttonVideoStart.setVisibility( View.INVISIBLE );
            return;
        }
//rotemc
        // Display image
        trainerImageView.setImageResource( trainerID );

        // Play video

        if( prf.videoUri != null /*&& (isNewSegment || !trainerVideoView.isPlaying())*/ ) {
            trainerVideoView.setVideoURI(Uri.parse(prf.videoUri));
            trainerVideoView.pause();
            trainerVideoView.seekTo( 1);
        }

        //Toast.makeText(this, trainerDisplayControl, Toast.LENGTH_SHORT).show();
        switch( trainerDisplayControl ) {
            case "VIDEO+PHOTO":
                trainerImageView.setVisibility( View.VISIBLE );
                trainerVideoView.setVisibility( View.VISIBLE );
                buttonVideoStart.setVisibility( View.VISIBLE );
                break;
            case "AUDIO+PHOTO":
                trainerImageView.setVisibility( View.VISIBLE );
                trainerVideoView.setVisibility( View.VISIBLE );
                buttonVideoStart.setVisibility( View.VISIBLE );
                break;
            case "VIDEO":
                trainerImageView.setVisibility( View.INVISIBLE );
                trainerVideoView.setVisibility( View.VISIBLE );
                buttonVideoStart.setVisibility( View.VISIBLE );
                break;
            case "PHOTO":
                trainerImageView.setVisibility( View.VISIBLE );
                trainerVideoView.setVisibility( View.INVISIBLE );
                buttonVideoStart.setVisibility( View.INVISIBLE );
                break;
            case "AUDIO":
                trainerImageView.setVisibility( View.INVISIBLE );
                trainerVideoView.setVisibility( View.VISIBLE );
                buttonVideoStart.setVisibility( View.VISIBLE );
                break;
            case "CLEAR":
                trainerImageView.setVisibility( View.INVISIBLE );
                trainerVideoView.setVisibility( View.INVISIBLE );
                buttonVideoStart.setVisibility( View.INVISIBLE );
                break;
        }
    }

    @Override
    public void onItemClick(View view, int position) {
        //rotemc
        //myToast( "Workout list position " + position, Toast.LENGTH_SHORT);
        workoutListPos = position;
        setPrf( workoutList.get(position), true );
        reps.setText( "Reps " + Integer.toString(prf.reps) + ":" +
                Integer.toString(prf.repsMax) );
    }

    public void onItemSelected(AdapterView<?> parent, View view, int pos, long id) {
        // Trainer video control spinner selected.
        trainerDisplayControl = (String) parent.getItemAtPosition(pos);
        setTrainerDisplay( false );
    }

    public void onNothingSelected(AdapterView<?> parent) {
        // Another interface callback
    }

    //-----------------------------------------------------------------------------------------

    @Override
    public void onResume() {
        super.onResume();
        setFilters();  // Start listening notifications from UsbService
        startService(UsbService.class, usbConnection, null); // Start UsbService(if it was not started before) and Bind it
    }

    @Override
    public void onPause() {
        super.onPause();
        if( usbService != null ) {
            usbService.setHandler( null );
        }
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
                                    prfRightDirChange( WorkoutPrf.Direction.PULL );
                                }
                                else if( distRight < prf.distRight ) {
                                    prfRightDirChange( WorkoutPrf.Direction.REL );
                                }

                                int distLeft = Integer.parseInt(val[1]);
                                if( distLeft > prf.distLeft ) {
                                    prf.dirLeft = WorkoutPrf.Direction.PULL;
                                }
                                else if( distLeft < prf.distLeft ) {
                                    prf.dirLeft = WorkoutPrf.Direction.REL;
                                }
                                pointRight.resetData(currentPoint(distRight, prf.dirRight));
                                pointLeft.resetData(currentPoint(distLeft, prf.dirLeft));
                                prf.distRight = distRight;
                                prf.distLeft = distLeft;

                                if( prf.distRight > prf.distLeft ) {
                                    graph.setTitle( prf.name + ", " + Integer.toString((int)currentPoint(distRight, prf.dirRight)[0].getY()) + "lb" );
                                }
                                else {
                                    graph.setTitle( prf.name + ", " + Integer.toString((int)currentPoint(distLeft, prf.dirLeft)[0].getY()) + "lb" );
                                }
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