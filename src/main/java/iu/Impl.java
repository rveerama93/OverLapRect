package iu;

public class Impl implements IntervalVal {
    double a;
    double b;

    public Impl(){}
    Impl(double  a,double b){
        this.a=a;
        this.b=b;
    }
    public double intervalStart(){
        return a;
    }
    public double intervalEnd(){
        return b;
    }
}

