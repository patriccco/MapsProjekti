package com.example.kona.myapplication;

import android.content.Intent;
import android.graphics.drawable.Drawable;
import android.os.SystemClock;
import android.util.AttributeSet;
import android.util.Log;
import android.view.Gravity;
import android.view.View;
import android.graphics.Paint;
import android.content.Context;
import android.graphics.Canvas;
import android.view.MotionEvent;
import android.graphics.Color;
import android.widget.Toast;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Random;

import static com.firebase.ui.auth.AuthUI.getApplicationContext;

/**
 * Class for the grid minigame
 */
public class PixelGridView extends View {
    FirebaseAuth auth = FirebaseAuth.getInstance();
    FirebaseDatabase database = FirebaseDatabase.getInstance();
    private String enemyname;

    private int numColumns, numRows,readycount, redcount;

    private int cellWidth, cellHeight;
    private Paint orange = new Paint();
    private Paint cyan = new Paint();
    private Paint red = new Paint();
    private boolean[][] cellChecked;
    private boolean[][] redChecked ;
    private boolean iscreated = false;
    private boolean timestarted = false;
    long tStop;
    double start,finaltime;
    Transaction transaction = new Transaction();
    Quest Questobject = new Quest();

    final DatabaseReference myRef = database.getReference("Player");

    Context context;

    public PixelGridView(Context context) {
        this(context, null);
    }

    /**
     * Constuctor for the gridview for the tapping minigame.
     * @param context
     * @param attrs
     */
    public PixelGridView(Context context, AttributeSet attrs) {

        super(context, attrs);
        this.context =context;

        orange.setStyle(Paint.Style.FILL_AND_STROKE);
        orange.setColor(Color.rgb(255, 102, 0));
        cyan.setColor(Color.CYAN);
        cyan.setStyle(Paint.Style.FILL);
        red.setColor(Color.RED);
        cyan.setStyle(Paint.Style.FILL);
    }

    /**
     * Set the amount of vertical pixels on the screen.
     * @param numColumns
     */
    public void setNumColumns(int numColumns) {
        this.numColumns = numColumns;
        calculateDimensions();
    }

    public int getNumColumns() {
        return numColumns;
    }

    /**
     * Set the amount of horizontal pixels on the screen.
     * @return
     */
    public void setNumRows(int numRows) {
        this.numRows = numRows;
        calculateDimensions();
    }

    public int getNumRows() {
        return numRows;
    }

    @Override
    protected void onSizeChanged(int w, int h, int oldw, int oldh) {
        super.onSizeChanged(w, h, oldw, oldh);
        calculateDimensions();
    }

    /**
     * Fit grid to the screen.
     */
    private void calculateDimensions() {
        if (numColumns < 1 || numRows < 1) {
            return;
        }

        cellWidth = getWidth() / numColumns;
        cellHeight = getHeight() / numRows;

        cellChecked = new boolean[numColumns][numRows];
        redChecked = new boolean[numColumns][numRows];

        invalidate();
    }


    /**
     * Draw objects to the canvas.
     * @param canvas
     */
    @Override
    protected void onDraw(Canvas canvas) {

        canvas.drawColor(getResources().getColor(R.color.gridcolor));

        if (numColumns == 0 || numRows == 0) {
            return;
        }

        int width = getWidth();
        int height = getHeight();

        //cyan fill
        for (int i = 0; i < numColumns; i++) {
            for (int j = 0; j < numRows; j++) {

                if (cellChecked[i][j]) {
                    Drawable d = getResources().getDrawable(R.drawable.greenbtn);
                    d.setBounds(i * cellWidth, j * cellHeight,
                            (i + 1) * cellWidth, (j + 1) * cellHeight);
                    d.draw(canvas);
                }
                if (redChecked[i][j] && !cellChecked[i][j]) {

                    Drawable d = getResources().getDrawable(R.drawable.redbtn);
                    d.setBounds(i * cellWidth, j * cellHeight,
                            (i + 1) * cellWidth, (j + 1) * cellHeight);
                    d.draw(canvas);

            }

            }
        }

        if (!iscreated) {
            while(redcount < 6) {
            readycount = 0;
            redcount = 0;

            for (int i = 0; i < numColumns; i++) {
                for (int j = 0; j < numRows; j++) {
                    Random r2 = new Random();

                    redChecked[i][j] = false;
                    int r2answer = r2.nextInt(15) + 1;
                    if (r2answer == 1 && redcount < 7) {
                        redcount++;
                        redChecked[i][j] = true;
                    }
                }
            }


            }
            iscreated = true;


        }

        if(redcount==readycount && redcount != 0 && readycount !=0 ){

            myRef.child(auth.getUid()).addListenerForSingleValueEvent(new ValueEventListener() {

                @Override
                public void onDataChange(DataSnapshot dataSnapshot) {
                    //Haetaan vihun tiedot, ensin nimi
                    enemyname = dataSnapshot.child("enemy").getValue().toString();
                    if (finaltime < 3.000) {
                        if(enemyname.equals("no")) {
                            String settext = getContext().getString(R.string.questreward);
                            Toast toast = Toast.makeText(getContext(),
                                    settext, Toast.LENGTH_LONG);
                            toast.setGravity(Gravity.CENTER | Gravity.CENTER_HORIZONTAL, 0, 0);
                            toast.show();
                        }
                        else {
                            String settext = getContext().getString(R.string.enemyreward);
                            String settext2 = getContext().getString(R.string.enemyreward2);
                            Toast toast = Toast.makeText(getContext(),
                                    settext + "\n" + settext2, Toast.LENGTH_LONG);
                            toast.setGravity(Gravity.CENTER | Gravity.CENTER_HORIZONTAL, 0, 0);
                            toast.show();
                        }
                    }
                    else {
                        String settext = getContext().getString(R.string.tooslow);
                        if(enemyname.equals("no")) {
                            String settext2 = getContext().getString(R.string.missionfialed);
                            Toast toast = Toast.makeText(getContext(),
                                    settext + "\n" + settext2, Toast.LENGTH_LONG);
                            toast.setGravity(Gravity.CENTER | Gravity.CENTER_HORIZONTAL, 0, 0);
                            toast.show();
                        }else{
                            Toast toast = Toast.makeText(getContext(),
                                    settext + "\n" + enemyname + getContext().getString(R.string.enemybeatyou), Toast.LENGTH_LONG);
                            toast.setGravity(Gravity.CENTER | Gravity.CENTER_HORIZONTAL, 0, 0);
                            toast.show();
                        }
                    }


                    handlevictory();
                }
                @Override
                public void onCancelled(DatabaseError databaseError) {
                }
            });

        }


    }

    @Override
    public boolean onTouchEvent(MotionEvent event) {

        float touchX = event.getX();
        float touchY = event.getY();
        int column = (int)(event.getX() / cellWidth);
        int row = (int)(event.getY() / cellHeight);

        switch (event.getAction()) {
            case MotionEvent.ACTION_DOWN:

                if(iscreated = true && !timestarted) {
                    Log.d("Start" , " " + start);
                    start = (double) SystemClock.uptimeMillis();
                }


                //drawPath.moveTo(touchX, touchY);
                if (redChecked[column][row] && cellChecked[column][row] || !redChecked[column][row]){
                    iscreated = false;
                    timestarted = true;

                }
                else {

                    cellChecked[column][row] = true;
                    readycount++;
                    if(readycount == redcount){
                        tStop = SystemClock.uptimeMillis();

                        Log.d("stop" , " " + tStop);

                        finaltime = (((double)tStop - start) / 1000);
                        if(finaltime < 3.000) {
                            Log.d("voitto", "ON " + finaltime);

                        }
                    }
                }


                //cellChecked[column][row] = !cellChecked[column][row];
                break;
            case MotionEvent.ACTION_MOVE:
                //drawPath.lineTo(touchX, touchY);
                //cellChecked[column][row] = true;
                //cellChecked[column][row] = !cellChecked[column][row];
                break;
            case MotionEvent.ACTION_UP:
                //drawCanvas.drawPath(drawPath, drawPaint);
                ///drawPath.reset();
                break;
            default:
                return false;
        }
        invalidate();
        return true;

    }

    /**
     * Determine the rewards for the armGame
     */
    public void handlevictory(){

        if (finaltime < 3.00){
            transaction.addMoney(20);
        }



        redcount = 0;
        readycount =0;
        myRef.child("User").child(auth.getUid()).child("enemy").setValue("noenemy");

        Questobject.setQuest(0.01, 0.01, "noquest", "noquest", 0, false);
        Intent intent = new Intent(context,MapsActivity.class);
                        context.startActivity(intent);

    }


}
