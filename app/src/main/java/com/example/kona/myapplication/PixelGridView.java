package com.example.kona.myapplication;

import android.util.AttributeSet;
import android.view.View;
import android.graphics.Paint;
import android.content.Context;
import android.graphics.Canvas;
import android.view.MotionEvent;
import android.graphics.Color;

import java.util.ArrayList;
import java.util.List;
import java.util.Random;

public class PixelGridView extends View {

    private int numColumns, numRows;
    private int cellWidth, cellHeight;
    private Paint orange = new Paint();
    private Paint cyan = new Paint();
    private Paint red = new Paint();
    private boolean[][] cellChecked;
    private boolean[][] redChecked ;
    private boolean iscreated = false;
    long tStart, tStop, drawDuration;
    private boolean beginDraw;

    public PixelGridView(Context context) {
        this(context, null);
    }

    public PixelGridView(Context context, AttributeSet attrs) {
        super(context, attrs);
        orange.setStyle(Paint.Style.FILL_AND_STROKE);
        orange.setColor(Color.rgb(255, 102, 0));
        cyan.setColor(Color.CYAN);
        cyan.setStyle(Paint.Style.FILL);
        red.setColor(Color.RED);
        cyan.setStyle(Paint.Style.FILL);
    }

    public void setNumColumns(int numColumns) {
        this.numColumns = numColumns;
        calculateDimensions();
    }

    public int getNumColumns() {
        return numColumns;
    }

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




    @Override
    protected void onDraw(Canvas canvas) {

        canvas.drawColor(Color.rgb(21, 21, 21));

        if (numColumns == 0 || numRows == 0) {
            return;
        }

        int width = getWidth();
        int height = getHeight();

        //cyan fill
        for (int i = 0; i < numColumns; i++) {
            for (int j = 0; j < numRows; j++) {
                if (cellChecked[i][j]) {

                    canvas.drawRect(i * cellWidth, j * cellHeight,
                            (i + 1) * cellWidth, (j + 1) * cellHeight,
                            cyan);
                }
                if (redChecked[i][j] && !cellChecked[i][j]) {

                    canvas.drawRect(i * cellWidth, j * cellHeight,
                            (i + 1) * cellWidth, (j + 1) * cellHeight,
                            red);


            }


            }
        }


        //draws grid
        for (int i = 1; i < numColumns; i++) {
            canvas.drawLine(i * cellWidth, 0, i * cellWidth, height, orange);

        }

        for (int i = 1; i < numRows; i++) {
            canvas.drawLine(0, i * cellHeight, width, i * cellHeight, orange);
        }

        if (!iscreated){
            for (int i = 0; i < numColumns; i++) {
                for (int j = 0; j < numRows; j++) {
                    Random r2 = new Random();

                    redChecked[i][j] = false;
                    int r2answer = r2.nextInt(59) + 1;
                    if (r2answer == 1) {
                        redChecked[i][j] = true;
                    }
                }


        }
        iscreated = true;}

    }

    @Override
    public boolean onTouchEvent(MotionEvent event) {

        float touchX = event.getX();
        float touchY = event.getY();
        int column = (int)(event.getX() / cellWidth);
        int row = (int)(event.getY() / cellHeight);

        switch (event.getAction()) {
            case MotionEvent.ACTION_DOWN:
                //drawPath.moveTo(touchX, touchY);
                tStart = System.currentTimeMillis();

                cellChecked[column][row] = true;
                //cellChecked[column][row] = !cellChecked[column][row];
                break;
            case MotionEvent.ACTION_MOVE:
                //drawPath.lineTo(touchX, touchY);
                cellChecked[column][row] = true;
                //cellChecked[column][row] = !cellChecked[column][row];
                break;
            case MotionEvent.ACTION_UP:
                //drawCanvas.drawPath(drawPath, drawPaint);
                ///drawPath.reset();
                tStop = System.currentTimeMillis();
                break;
            default:
                return false;
        }
        drawDuration = tStop - tStart;
        invalidate();
        return true;

    }


}
