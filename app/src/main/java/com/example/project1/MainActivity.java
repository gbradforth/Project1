package com.example.project1;

import androidx.appcompat.app.AppCompatActivity;
import androidx.gridlayout.widget.GridLayout;

import android.content.res.Resources;
import android.graphics.Color;
import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.TextView;

import java.util.ArrayList;

public class MainActivity extends AppCompatActivity {

    private static final int COLUMN_COUNT = 10;
    private ArrayList<TextView> cell_tvs;
    private int clock = 0;
    private boolean running = false;
    private boolean flagMode = false;
    private final int[][] grid_array = new int[12][10];
    private boolean overFlag = false;

    private void initArray(){
        ArrayList<Integer> xarray = new ArrayList<>();
        ArrayList<Integer> yarray = new ArrayList<>();
        for (int i = 0; i < 7; i++){
            int math1 = (int)(Math.random()*12);
            int math2 = (int)(Math.random()*10);
            while (xarray.contains(math1)){
                math1 = (int)(Math.random()*12);
            }
            while (yarray.contains(math2)){
                math2 = (int)(Math.random()*10);
            }
            xarray.add(math1);
            yarray.add(math2);
        }

        for (int i = 0; i < 12; i++){
            for (int j = 0; j < 10; j++){
                for (int k = 0; k < 4; k++) {
                    //System.out.print("(" + xarray.get(k) + ", " + yarray.get(k) + ");");
                    if (xarray.get(k) == i && yarray.get(k) == j) {
                        grid_array[i][j] = -1;
                        //System.out.println("BOMB");
                    }
                    else if ((xarray.get(k) == i-1 && yarray.get(k) == j) ||
                            (xarray.get(k) == i+1 && yarray.get(k) == j) ||
                            (xarray.get(k) == i && yarray.get(k) == j-1) ||
                            (xarray.get(k) == i && yarray.get(k) == j+1) ||
                            (xarray.get(k) == i-1 && yarray.get(k) == j-1) ||
                            (xarray.get(k) == i+1 && yarray.get(k) == j-1) ||
                            (xarray.get(k) == i-1 && yarray.get(k) == j+1) ||
                            (xarray.get(k) == i+1 && yarray.get(k) == j+1)
                    ){
                        if (grid_array[i][j] != -1)
                            grid_array[i][j]++;
                        //System.out.println("close!");
                    }
                    else {
                        grid_array[i][j] = 0;
                    }
                }
                //System.out.println(":" + i + ":" + j);
            }
        }
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        initArray();

        /* Grid code */
        cell_tvs = new ArrayList<TextView>();

        GridLayout grid = (GridLayout) findViewById(R.id.gridLayout01);
        // Method (3): add four dynamically created cells with LayoutInflater
        LayoutInflater li = LayoutInflater.from(this);
        for (int i = 0; i < 12; i++) {
            for (int j = 0; j < 10; j++) {
                TextView tv = (TextView) li.inflate(R.layout.custom_cell_layout, grid, false);
                //tv.setText(String.valueOf(i)+String.valueOf(j));
                tv.setTextColor(Color.GREEN);
                tv.setBackgroundColor(Color.GREEN);
                tv.setOnClickListener(this::onClickTV);

                GridLayout.LayoutParams lp = (GridLayout.LayoutParams) tv.getLayoutParams();
                lp.rowSpec = GridLayout.spec(i);
                lp.columnSpec = GridLayout.spec(j);

                grid.addView(tv, lp);

                cell_tvs.add(tv);
            }
        }

        /* Stopwatch code */
        if (savedInstanceState != null) {
            clock = savedInstanceState.getInt("clock");
            running = savedInstanceState.getBoolean("running");
        }
        runTimer();
        running = true;

        /* Pick code */
        final TextView pickView = (TextView) findViewById(R.id.textView_p3);
        if (flagMode){
            pickView.setText(getString(R.string.flag));
        }
        else{
            pickView.setText(getString(R.string.pick));
        }
    }

    private int findIndexOfCellTextView(TextView tv) {
        for (int n=0; n<cell_tvs.size(); n++) {
            if (cell_tvs.get(n) == tv)
                return n;
        }
        return -1;
    }

    public void onClickTV(View view){
        TextView tv = (TextView) view;
        int n = findIndexOfCellTextView(tv);
        int i = n/COLUMN_COUNT;
        int j = n%COLUMN_COUNT;
        String display = "";

        if (flagMode){
            display = getString(R.string.flag);
            tv.setText(display);
        }
        else{
            if (grid_array[i][j] == 0)
                display = " ";
            else if (grid_array[i][j] == -1) {
                display = getString(R.string.mine);
                overFlag = true;
            }
            else
                display = Integer.toString(grid_array[i][j]);
            tv.setText(display);
            if (tv.getCurrentTextColor() == Color.GREEN) {
                tv.setTextColor(Color.GRAY);
                tv.setBackgroundColor(Color.LTGRAY);
            }
            if (overFlag){
                endGame("lost");
            }
        }
    }

    public void endGame(String state){
        String message = Integer.toString(clock) + " " + state;
        Intent intent = new Intent(this, ReceiveMessageActivity.class);
        intent.putExtra("com.example.sendmessage.MESSAGE", message);
        startActivity(intent);
    }

    public void onClickPick(View view){
        TextView flagPick = (TextView) view;
        if (flagMode){
            flagPick.setText(getString(R.string.pick));
            flagMode = false;
        }
        else{
            flagPick.setText(getString(R.string.flag));
            flagMode = true;
        }
    }
    public void onSaveInstanceState(Bundle savedInstanceState) {
        super.onSaveInstanceState(savedInstanceState);
        savedInstanceState.putInt("clock", clock);
        savedInstanceState.putBoolean("running", running);
    }

    private void runTimer() {
        final TextView timeView = (TextView) findViewById(R.id.textView_clock);
        final Handler handler = new Handler();

        handler.post(new Runnable() {
            @Override
            public void run() {
                int seconds = clock;
                String time = String.format("%03d", seconds);
                timeView.setText(time);

                if (running) {
                    clock++;
                }
                handler.postDelayed(this, 1000);
            }
        });
    }
}
