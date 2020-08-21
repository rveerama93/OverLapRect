package iu;

import com.google.common.collect.Multimap;
import com.google.common.collect.Ordering;
import com.google.common.collect.TreeMultimap;


import java.io.FileInputStream;
import java.io.FileWriter;
import java.util.*;

public class IntervalTree  {


    public static void main(String[] args) throws Exception {

        if(args.length < 1) {
            System.out.println("Error, usage: java ClassName inputfile");
            System.exit(1);
        }

        Scanner sc = new Scanner(new FileInputStream(args[0]));
        FileWriter writer = new FileWriter("output.txt");


        String rect_id;
        String xlow;
        String ylow;
        String xhigh;
        String yhigh;
        IntervalTreeImplementation<Impl> tree = new IntervalTreeImplementation<>();
        IntervalTreeImplementation<Impl> tree_overlap = new IntervalTreeImplementation<>();
        ArrayList<Rectangle> a = new ArrayList<>();

        try{
        while (sc.hasNextLine()) {
            rect_id = sc.next();
            xlow = sc.next();
            ylow = sc.next();
            xhigh = sc.next();
            yhigh = sc.next();

            a.add(new Rectangle(Integer.parseInt(rect_id), Double.parseDouble(xlow), Double.parseDouble(ylow), Double.parseDouble(xhigh), Double.parseDouble(yhigh) ));
        }
        }
        catch (Exception e){
            System.out.println("Blank Line encountered at last and ignored this in program   ");

        }


        sc.close();
        Multimap<Double, Integer> xvalues= TreeMultimap.create(Ordering.natural(),Ordering.natural());
        for (Rectangle rectangle : a) {
            xvalues.put(rectangle.xlow, rectangle.rect_id);
            xvalues.put(rectangle.xhigh, rectangle.rect_id);
        }


        List<Integer> counter1= new ArrayList<>();
        boolean isFound=false;
        for(Map.Entry<Double, Integer> mapping : xvalues.entries()){
            int val=mapping.getValue();
            Rectangle r= new Rectangle();
            r=a.get(val-1);
            Impl i= new Impl(r.getYlow(), r.getYhigh());
            boolean flag = counter1.contains(val);
            Rectangle r1= new Rectangle();
            if (!flag){
                counter1.add(val);

                tree.overlappers(i).forEachRemaining( x ->{
                    tree_overlap.insert(new Impl(x.intervalStart(),x.intervalEnd()));
                    r1.setYlow(x.intervalStart());
                    r1.setYhigh(x.intervalEnd());
                });

                if(tree_overlap.size()==1){
                    tree_overlap.insert(i);
                    for (Rectangle st : a) {
                        if ((r1.getYlow() == st.getYlow()) && (r1.getYhigh() == st.getYhigh())) {
                            r1.setRect_id(st.getRect_id());
                        }

                    }
                    System.out.println(r1.rect_id +" and "+ val + " Overlap");
                    writer.write(r1.rect_id +" and "+ val + " Overlap");
                    writer.close();
                    isFound=true;
                    break;
                }
                tree.insert(i);
            }

            else
            {
                tree.deleteNode(i);
            }


        }
        if(!isFound){
            System.out.println("No Overlap");
            writer.write("No Overlap");
            writer.close();
        }


    }


}

