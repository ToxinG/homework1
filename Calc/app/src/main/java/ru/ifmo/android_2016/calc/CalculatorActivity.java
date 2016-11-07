package ru.ifmo.android_2016.calc;

import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.HorizontalScrollView;
import android.widget.TextView;

public class CalculatorActivity extends AppCompatActivity {

    private static final int[] numBtnIds = new int[]{
            R.id.d0, R.id.d1,
            R.id.d2, R.id.d3,
            R.id.d4, R.id.d5,
            R.id.d6, R.id.d7,
            R.id.d8, R.id.d9,
            R.id.point
    };
    private static final int[] oprBtnIds = new int[]{
            R.id.add, R.id.sub,
            R.id.mul, R.id.div,
            R.id.percent
    };
    private Button eqvBtn, clearBtn, signBtn;

    private Button [] numBtn = new Button[numBtnIds.length];
    private Button [] oprBtn = new Button[oprBtnIds.length];

    private TextView result, expression;
    private HorizontalScrollView exprScrollview, resScrollview;

    private String resStr = "", exprStr = "";
    private int exprOpr = 0;
    private double exprNum = 0;
    private boolean isPositive = true;

    private View.OnClickListener receiveNum = new View.OnClickListener() {
        @Override
        public void onClick (View v) {
            char symbol = ((Button) v).getText().charAt(0);
            if (v.getId() == R.id.point && (resStr.indexOf('.') > -1 || resStr.isEmpty())) //point is unsuitable
                return;
            if (resStr.length() >= 16) //number is too long
                return;
            if (v.getId() == R.id.d0 && (resStr.equals("0") || (resStr.indexOf('.') > -1 && resStr.charAt(resStr.length() - 1) == '0'))) {
                return;
            }
            resStr += symbol;
            refresh();
        }
    };

    private View.OnClickListener receiveOpr = new View.OnClickListener() {
        @Override
        public void onClick (View v) {
            if (resStr.isEmpty())
                return;
            char symbol = ((Button) v).getText().charAt(0);
            if (exprOpr != 0 && !resStr.isEmpty())
                evaluate();
            exprStr = resStr + " " + symbol;
            exprNum = isPositive ? Double.parseDouble(resStr) : (-1) * Double.parseDouble(resStr.substring(1, resStr.length()));
            exprOpr = v.getId();
            resStr = "";
            refresh();
        }
    };

    private void refresh() {
        expression.setText(exprStr);
        result.setText(resStr);
        exprScrollview.post(new Runnable() {
            @Override
            public void run() {
                exprScrollview.fullScroll(HorizontalScrollView.FOCUS_RIGHT);
            }
        });
        resScrollview.post(new Runnable() {
            @Override
            public void run() {
                resScrollview.fullScroll(HorizontalScrollView.FOCUS_RIGHT);
            }
        });
    }

    private void evaluate() {
        double left = exprNum;
        double right = Double.parseDouble(resStr);
        switch (exprOpr) {
            case R.id.add:
                left += right;
                break;
            case R.id.sub:
                left -= right;
                break;
            case R.id.mul:
                left *= right;
                break;
            case R.id.div:
                left /= right;
                break;
            case R.id.percent:
                left = left / 100 * right;
                break;
        }
        resStr = Double.toString(left);
        if (resStr.substring(resStr.length() - 2, resStr.length()).equals(".0"))
            resStr = resStr.substring(0, resStr.length() - 2);
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_calculator);

        eqvBtn = (Button) findViewById(R.id.eqv);
        eqvBtn.setOnClickListener(new View.OnClickListener(){
            @Override
            public void onClick(View v) {
                if (exprStr.equals(""))
                    return;
                evaluate();
                exprStr = "";
                exprOpr = 0;
                exprNum = 0;
                refresh();
            }
        });

        clearBtn = (Button) findViewById(R.id.clear);
        clearBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (resStr.isEmpty())
                    return;
                if (resStr.length() == 1)
                    resStr = "";
                else {
                    resStr = resStr.substring(0, resStr.length() - 1);
                    if (!Character.isDigit(resStr.charAt(resStr.length() - 1)))
                        resStr = resStr.substring(0, resStr.length() - 1);
                }
                refresh();
            }
        });

        clearBtn.setOnLongClickListener(new View.OnLongClickListener() {
            @Override
            public boolean onLongClick(View v) {
                resStr = "";
                exprStr = "";
                exprNum = 0;
                exprOpr = 0;
                refresh();
                return true;
            }
        });

        signBtn = (Button) findViewById(R.id.sign);
        signBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (resStr.isEmpty() || resStr.equals("0"))
                    return;
                if (resStr.charAt(0) == '-') {
                    resStr = resStr.substring(1, resStr.length());
                    isPositive = true;
                }
                else {
                    isPositive = false;
                    resStr = '-' + resStr;
                }
                refresh();
            }
        });

        for (int i = 0; i < numBtn.length; i++) {
            numBtn[i] = (Button) findViewById(numBtnIds[i]);
            numBtn[i].setOnClickListener(receiveNum);
        }

        for (int i = 0; i < oprBtn.length; i++) {
            oprBtn[i] = (Button) findViewById(oprBtnIds[i]);
            oprBtn[i].setOnClickListener(receiveOpr);
        }

        result = (TextView) findViewById(R.id.result);
        expression = (TextView) findViewById(R.id.expression);

        exprScrollview = (HorizontalScrollView) findViewById(R.id.expression_scrollview);
        resScrollview = (HorizontalScrollView) findViewById(R.id.result_scrollview);
        refresh();
    }

    @Override
    protected void onSaveInstanceState(Bundle outState) {
        super.onSaveInstanceState (outState);

        outState.putDouble("exprNum", exprNum);
        outState.putString("exprStr", exprStr);
        outState.putString("resStr", resStr);
        outState.putInt("exprOpr", exprOpr);
        outState.putBoolean("isPositive", isPositive);
    }

    @Override
    protected void onRestoreInstanceState(Bundle savedInstanceState) {
        super.onRestoreInstanceState (savedInstanceState);

        exprNum = savedInstanceState.getDouble ("exprNum");
        exprStr = savedInstanceState.getString("exprStr");
        resStr = savedInstanceState.getString("resStr");
        exprOpr = savedInstanceState.getInt("exprOpr");
        isPositive = savedInstanceState.getBoolean("isPositive");
        refresh();
    }
}
