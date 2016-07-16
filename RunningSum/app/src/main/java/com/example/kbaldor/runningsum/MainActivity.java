package com.example.kbaldor.runningsum;

import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.view.menu.ExpandedMenuView;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;

import java.util.ArrayList;
import java.util.Random;

import nz.sodium.Cell;
import nz.sodium.CellLoop;
import nz.sodium.CellSink;
import nz.sodium.Handler;
import nz.sodium.Lambda1;
import nz.sodium.Lambda2;
import nz.sodium.Lambda3;
import nz.sodium.Stream;
import nz.sodium.StreamSink;
import nz.sodium.Transaction;
import nz.sodium.Unit;

public class MainActivity extends AppCompatActivity {

    Random myRandom = new Random();
    int n;

    StreamSink<Integer> nextRandom     = new StreamSink<>();

    StreamSink<Unit>    incrementEvent = new StreamSink<>();
    StreamSink<Unit>    decrementEvent = new StreamSink<>();

    CellSink<Integer> max = new CellSink<>(10);
    CellSink<Integer> min = new CellSink<>(3);

    // I normally wouldn't put these here, but I wanted to provide a hint
    CellLoop<Integer>            N;
    CellLoop<ArrayList<Integer>> lastNValues;
    Cell<Integer>                sum;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        Button MinusButton = (Button)findViewById(R.id.minus);
        Button PlusButton  = (Button)findViewById(R.id.plus);

        final TextView valueView = (TextView)findViewById(R.id.N);
        final TextView values = (TextView)findViewById(R.id.LastNumbers);
        final TextView sumValue = (TextView)findViewById(R.id.Sum);

        MinusButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                decrementEvent.send(Unit.UNIT);
            }
        });
        PlusButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                incrementEvent.send(Unit.UNIT);
            }
        });


        // You need a transaction for closing loops
        Transaction.runVoid(new Runnable() {
            @Override
            public void run() {

                // define your reactive network here
                N = new CellLoop<>();
                lastNValues = new CellLoop<>();

                Stream<Integer> incrementValues = incrementEvent.snapshot(N, new Lambda2<Unit, Integer, Integer>() {
                    @Override
                    public Integer apply(Unit unit, Integer old_value) {
                        return old_value+1;
                    }
                });

                Stream<Integer> decrementValues = decrementEvent.snapshot(N, new Lambda2<Unit, Integer, Integer>() {
                    @Override
                    public Integer apply(Unit unit, Integer old_value) {
                        return old_value-1;
                    }
                });


                Stream<Integer> changeValues = incrementValues.merge(decrementValues,
                        new Lambda2<Integer, Integer, Integer>() {
                            @Override
                            public Integer apply(Integer inc, Integer dec) {
                                return (inc+dec)/2;
                            }
                        });

                Cell<Integer> candidateValues = changeValues.hold(5);

                final Cell<Integer> legalValues =
                        candidateValues.lift(min, max, new Lambda3<Integer, Integer, Integer, Integer>() {
                            @Override
                            public Integer apply(Integer change, Integer min, Integer max) {
                                if(change > max) return max;
                                if(change < min) return min;
                                return change;
                            }
                        });


                N.loop(legalValues);

                Stream<ArrayList<Integer>> randValues = nextRandom.snapshot(lastNValues, new Lambda2<Integer, ArrayList<Integer>, ArrayList<Integer>>() {
                    @Override
                    public ArrayList<Integer> apply(Integer integer, ArrayList<Integer> integers) {
                        integers.add(integer);
                        return integers;
                    }
                });

                ArrayList<Integer> list = new ArrayList<Integer>();
                //list.add(myRandom.nextInt(9)+1);
                Cell<ArrayList<Integer>> randomValue = randValues.hold(list);

                Cell<ArrayList<Integer>> legalList = randomValue.lift(N, new Lambda2<ArrayList<Integer>, Integer, ArrayList<Integer>>() {
                    @Override
                    public ArrayList<Integer> apply(ArrayList<Integer> integers, Integer integer) {
                        Log.i("My value",Integer.valueOf(integer).toString());
                        Log.i("My value",Integer.valueOf(integers.size()).toString());
                        if(integers.size()<=integer){
                            Log.i("small","added");
                        return integers;
                        }
                        else{
                            Log.i("Big","entered");
                            integers.remove(0);
                        }
                        return integers;
                    }
                });

                lastNValues.loop(legalList);


                sum= legalList.map(new Lambda1<ArrayList<Integer>, Integer>() {
                    @Override
                    public Integer apply(ArrayList<Integer> integers) {
                        int size = integers.size();
                        int sumVal=0;
                        for(int i=0;i<size;i++)
                        {
                            sumVal=sumVal+integers.get(i);
                        }
                        return sumVal;
                    }
                });


            }
        });

        N.listen(new Handler<Integer>() {
            @Override
            public void run(Integer value) {
                valueView.setText(value.toString());
            }
        });

        lastNValues.listen(new Handler<ArrayList<Integer>>() {
            @Override
            public void run(ArrayList<Integer> integers) {
                values.setText(integers.toString());
            }
        });
        sum.listen(new Handler<Integer>() {
            @Override
            public void run(Integer integer) {
                sumValue.setText(integer.toString());
            }
        });



    }

    public void sendNumber(View view){
        n = myRandom.nextInt(9)+1;
        Log.i("random",Integer.valueOf(n).toString());
        nextRandom.send(n);
    }

    public void decN(View view){
        decrementEvent.send(Unit.UNIT);
    }

    public void incN(View view){
        incrementEvent.send(Unit.UNIT);
    }

}
