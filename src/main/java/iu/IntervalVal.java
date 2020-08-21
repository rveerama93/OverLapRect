package iu;


public interface IntervalVal extends Comparable<IntervalVal> {
    double intervalStart();
    double intervalEnd();

    default boolean overlaps(IntervalVal o)	{
        return intervalEnd() > o.intervalStart() && o.intervalEnd() > intervalStart();
    }

    default int compareTo(IntervalVal o) {
        if (intervalStart() > o.intervalStart()) {
            return 1;
        } else if (intervalStart() < o.intervalStart()) {
            return -1;
        } else return Double.compare(intervalEnd(), o.intervalEnd());
    }
}

