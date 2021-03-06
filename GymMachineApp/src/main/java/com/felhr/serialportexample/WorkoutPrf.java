package com.felhr.serialportexample;

import android.media.midi.MidiOutputPort;
import android.net.Uri;

import java.io.Serializable;

public class WorkoutPrf implements Serializable {

    public enum Direction {
        PULL,
        REL
    }

    public enum State {
        INIT,
        SELECTED,
        COMPLETED
    }

    public enum Type {
        WARMUP,
        WORKOUT,
        SQ_BICEP,
        SQ_TRICEP,
        SQ_BACK,
    }

    public State state;
    public Type type;

    private final int addPullInit;
    private final int addRelInit;
    private final int multPullInit;
    private final int multRelInit;
    public int repsMax;

    public final String name;
    public final String videoUri;

    int addPull;
    int addRel;
    int multPull;
    int multRel;
    int reps;
    int[] tbl;
    final byte usbChar;
    int distRight;
    int distLeft;
    Direction dirRight;
    Direction dirLeft;


    WorkoutPrf( String Name,
                int addPullInitPrm,
                int addRelInitPrm,
                int multPullInitPrm,
                int multRelInitPrm,
                int[] Tbl )
    {
        this( Name,
                null,
                addPullInitPrm,
                addRelInitPrm,
                multPullInitPrm,
                multRelInitPrm,
                1,
                Tbl );
    }

    WorkoutPrf( String Name,
                String videoUriPrm,
                int addPullInitPrm,
                int addRelInitPrm,
                int multPullInitPrm,
                int multRelInitPrm,
                int repsMaxPrm,
                int[] Tbl ) {
        this( Name,
                Type.WORKOUT,
                videoUriPrm,
                addPullInitPrm,
                addRelInitPrm,
                multPullInitPrm,
                multRelInitPrm,
                repsMaxPrm,
                Tbl );
    }

    WorkoutPrf( String name,
                Type type,
                String videoUri,
                int addPullInit,
                int addRelInit,
                int multPullInit,
                int multRelInit,
                int repsMax,
                int[] tbl ) {
        this.name = name;
        this.type = type;
        this.videoUri = videoUri;
        this.addPullInit = addPullInit;
        this.addRelInit = addRelInit;
        this.multPullInit = multPullInit;
        this.multRelInit = multRelInit;
        this.repsMax = repsMax;
        this.tbl = tbl;
        if( tbl == WEIGHT_TBL ) {
            usbChar = 'w';
        }
        else if( tbl == SPRING_TBL ) {
            usbChar = 's';
        }
        else if( tbl == INV_SPRING_TBL ) {
            usbChar = 'i';
        }
        else if( tbl == MTN_TBL ) {
            usbChar = 'm';
        }
        else if( tbl == STRENGTH_TEST_TBL ) {
            usbChar = 't';
        }
        else {
            usbChar = 'w'; // default
        }

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
        reps = 1;
        state = State.INIT;
    }

    boolean isRepsMax() {
        return ( repsMax > 0 && reps > repsMax );
    }


    //----------------------------------------------------------------------------

    // Pre programmed workout profiles

    private static final int W1 = 50;
    public static final int[] WEIGHT_TBL =
                  {/*0,  0,  0,  0,  0,  0,  0,  0,*/ 0, 0,
                    W1, W1, W1, W1, W1, W1, W1, W1, W1, W1,
                    W1, W1, W1, W1, W1, W1, W1, W1, W1, W1,
                    W1, W1, W1, W1, W1, W1, W1, W1, W1, W1,
                    W1, W1, W1, W1, W1, W1, W1, W1, W1, W1,
                    W1, W1, W1, W1, W1, W1, W1, W1, W1, W1,
                    W1, W1, W1, W1, W1, W1, W1, W1, W1, W1,
                    W1, W1, W1, W1, W1, W1, W1, W1, W1, W1,
                    W1, W1, W1, W1, W1, W1, W1, W1, W1, W1};

    public static final int[] SPRING_TBL =
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

    public static final int[] INV_SPRING_TBL =
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

    public static final int[] MTN_TBL =
                 {/* 0,   0,   0,   0,   0,   0,   0,   0,*/   0,   0,
                    50,  52,  54,  56,  58,  60,  62,  64,  66,  68,
                    70,  72,  74,  76,  78,  80,  82,  84,  86,  88,
                    90,  92,  94,  96,  98, 100, 102, 104, 106, 108,
                    110, 112, 114, 116, 118, 120, 122, 124, 126, 128,
                    128, 126, 124, 122, 120, 118, 116, 114, 112, 110,
                    108, 106, 104, 102, 100,  98,  96,  94,  92,  90,
                    88,  86,  84,  82,  80,  78,  76,  74,  72,  70,
                    68,  66,  64,  62,  60,  58,  56,  54,  52,  50 };

    public static final int[] STRENGTH_TEST_TBL =
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
}
