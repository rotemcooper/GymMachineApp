package com.felhr.serialportexample;

public class PersonPrf {
    String name;
    private int sqBicep;
    private int sqTricep;
    private int sqBack;

    PersonPrf( String name ) {
        this.name = name;
        sqBicep = 10;
        sqTricep = 10;
        sqBack = 10;
    }

    void setSqBicep( int sq ) { sqBicep=sq; }
    void setSqTricep( int sq ) { sqTricep=sq; }
    void setSqBack( int sq ) { sqBack=sq; }

    int getSqBicep() { return sqBicep; }
    int getSqTricep() { return sqTricep; }
    int getSqBack() { return sqBack; }
}
