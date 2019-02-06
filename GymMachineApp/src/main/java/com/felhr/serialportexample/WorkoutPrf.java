package com.felhr.serialportexample;

public class WorkoutPrf {

    public enum Direction {
        PULL,
        REL
    }
    
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

    WorkoutPrf( String Name,
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
