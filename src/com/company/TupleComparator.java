package com.company;

import java.util.Comparator;

/**
 * Created by erfan on 11/11/14.
 */
class TupleComparator implements Comparator<RankTuple> {
    @Override
    public int compare(RankTuple t1, RankTuple t2){
        if(t1.y > t2.y) return 1;
        else {
            if (t1.y < t2.y) return -1;
            else return 0;
        }
    }
}
