package com.felhr.serialportexample;

public class PersonPrf {
    String name;
    private int sqTotal;
    private int sqBicep;
    private int sqTricep;
    private int sqBack;
    private int sqCnt;
    private int pullBias;
    private int relBias;

    PersonPrf( String name ) {
        this.name = name;
        sqBicep = 0;
        sqTricep = 0;
        sqBack = 0;
        sqTotal = 0;
        sqCnt = 0;
        pullBias = 0;
        relBias = 0;
    }

    int setSq( WorkoutPrf.Type type, int val ) {
        switch( type ) {
            case SQ_BICEP:
                setSqBicep( val );
                sqCnt++;
                sqTotal += val;
                break;
            case SQ_TRICEP:
                setSqTricep( val );
                sqCnt++;
                sqTotal += val;
                break;
            case SQ_BACK:
                setSqBack( val );
                sqCnt++;
                sqTotal += val;
                break;
        }
        return getSqOverall();
    }

    //void setSqOverall( int sq ) { sqOverall=sq; }
    void setSqBicep( int sq ) { sqBicep=sq; }
    void setSqTricep( int sq ) { sqTricep=sq; }
    void setSqBack( int sq ) { sqBack=sq; }

    int getSqBicep() { return sqBicep; }
    int getSqTricep() { return sqTricep; }
    int getSqBack() { return sqBack; }

    void pullBiasInc( int val ) { pullBias += val; }
    void relBiasInc( int val ) { relBias += val; }
    int pullBias() { return pullBias; }
    int relBias(){ return relBias; }

    int getSqOverall() {
        if( sqCnt > 0 ) {
            return sqTotal/sqCnt;
        }
        else {
            return sqTotal;
        }
    }
}
