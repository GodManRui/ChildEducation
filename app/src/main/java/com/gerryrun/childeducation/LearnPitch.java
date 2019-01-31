package com.gerryrun.childeducation;

import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.MotionEvent;
import android.view.View;
import android.widget.Button;

import com.gerryrun.childeducation.util.PanioMusic;
import com.gerryrun.childeducation.util.StatusBarColor;

public class LearnPitch extends AppCompatActivity {

    private Button button[];// 按钮数组
    private PanioMusic utils;// 工具类
    private View parent;// 父视图
    private int buttonId[];// 按钮id
    private boolean havePlayed[];// 是否已经播放了声音，当手指在同一个按钮内滑动，且已经发声，就为true
    private View keys;// 按钮们所在的视图
    private int pressedkey[];


    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        StatusBarColor.setWindowsTranslucent(this);
        setContentView(R.layout.learn_pitch);
//        CreateView();
//        setTitle("学音高");
        initView();
        parent.setClickable(true);

        parent.setOnTouchListener(this::onTouchListener);

    }

    private void initView() {
        /*findViewById(R.id.duo).setOnClickListener(v -> clickDuo());
        findViewById(R.id.re).setOnClickListener(v -> clickRe());
        findViewById(R.id.mi).setOnClickListener(v -> clickMe());
        findViewById(R.id.fa).setOnClickListener(v -> clickFa());
        findViewById(R.id.sol).setOnClickListener(v -> clickSol());
        findViewById(R.id.la).setOnClickListener(v -> clickLa());
        findViewById(R.id.si).setOnClickListener(v -> clickSi());
        findViewById(R.id.do1).setOnClickListener(v -> clickDol());*/
        keys = findViewById(R.id.llKeys);
//        parent = findViewById(R.id.ll_parent);
        parent = keys;
        // 新建工具类
        utils = new PanioMusic(getApplicationContext());

        // 按钮资源Id
        buttonId = new int[8];
        buttonId[0] = R.id.duo;
        buttonId[1] = R.id.re;
        buttonId[2] = R.id.mi;
        buttonId[3] = R.id.fa;
        buttonId[4] = R.id.sol;
        buttonId[5] = R.id.la;
        buttonId[6] = R.id.si;
        buttonId[7] = R.id.do1;

        button = new Button[8];
        havePlayed = new boolean[8];

        // 获取按钮对象
        for (int i = 0; i < button.length; i++) {
            button[i] = findViewById(buttonId[i]);
            button[i].setClickable(false);
            havePlayed[i] = false;
        }

        pressedkey = new int[5];
        for (int j = 0; j < pressedkey.length; j++) {
            pressedkey[j] = -1;
        }


    }

    private boolean onTouchListener(View v, MotionEvent event) {
        int temp;
        int tempIndex;
        int pointercount;
        pointercount = event.getPointerCount();
        for (int count = 0; count < pointercount; count++) {
            boolean moveflag = false;// 标记是否是在按键上移动
            temp = isInAnyScale(event.getX(count), event.getY(count),
                    button);
            if (temp != -1) {// 事件对应的是当前点
                switch (event.getActionMasked()) {
                    case MotionEvent.ACTION_DOWN:
                        // // 单独一根手指或最先按下的那个
                        // pressedkey = temp;
                    case MotionEvent.ACTION_POINTER_DOWN:
                        pressedkey[count] = temp;
                        if (!havePlayed[temp]) {// 在某个按键范围内
                            button[temp]
                                    .setBackgroundResource(getBackgroundPressed(temp, true));
                            // 播放音阶
                            utils.soundPlay(temp);
                            havePlayed[temp] = true;
                        }
                        break;
                    case MotionEvent.ACTION_MOVE:
                        temp = pressedkey[count];
                        for (int i = temp + 1; i >= temp - 1; i--) {
                            // 当在两端的按钮时，会有一边越界
                            if (i < 0 || i >= button.length) {
                                continue;
                            }
                            if (isInScale(event.getX(count),
                                    event.getY(count), button[i])) {// 在某个按键内
                                moveflag = true;
                                if (i != temp) {// 在相邻按键内
                                    boolean laststill = false;
                                    boolean nextstill = false;
                                    // 假设手指已经从上一个位置抬起，但是没有真的抬起，所以不移位
                                    pressedkey[count] = -1;
                                    for (int j = 0; j < pointercount; j++) {
                                        if (pressedkey[j] == temp) {
                                            laststill = true;
                                        }
                                        if (pressedkey[j] == i) {
                                            nextstill = true;
                                        }
                                    }

                                    if (!nextstill) {// 移入的按键没有按下
                                        // 设置当前按键
                                        button[i]
                                                .setBackgroundResource(getBackgroundPressed(i, true));
                                        // 发音
                                        utils.soundPlay(i);
                                        havePlayed[i] = true;
                                    }

                                    pressedkey[count] = i;

                                    if (!laststill) {// 没有手指按在上面
                                        // 设置上一个按键
                                        button[temp]
                                                .setBackgroundResource(getBackgroundPressed(temp, false));
                                        havePlayed[temp] = false;
                                    }

                                    break;
                                }
                            }
                        }
                        break;
                    case MotionEvent.ACTION_UP:
                    case MotionEvent.ACTION_POINTER_UP:
                        // 事件与点对应
                        tempIndex = event.getActionIndex();
                        if (tempIndex == count) {
                            Log.i("--", "index" + tempIndex);
                            boolean still = false;
                            // 当前点已抬起
                            for (int t = count; t < 5; t++) {
                                if (t != 4) {
                                    if (pressedkey[t + 1] >= 0) {
                                        pressedkey[t] = pressedkey[t + 1];
                                    } else {
                                        pressedkey[t] = -1;
                                    }
                                } else {
                                    pressedkey[t] = -1;
                                }

                            }
                            for (int i = 0; i < pressedkey.length; i++) {// 是否还有其他点
                                if (pressedkey[i] == temp) {
                                    still = true;
                                    break;
                                }
                            }
                            if (!still) {// 已经没有手指按在该键上
                                button[temp]
                                        .setBackgroundResource(getBackgroundPressed(temp, false));
                                havePlayed[temp] = false;
                                Log.i("--", "button" + temp + "up");
                            }
                            break;
                        }
                }
            }
            //
            if (event.getActionMasked() == MotionEvent.ACTION_MOVE
                    && !moveflag) {
                if (pressedkey[count] != -1) {
                    button[pressedkey[count]]
                            .setBackgroundResource(getBackgroundPressed(pressedkey[count], false));
                    havePlayed[pressedkey[count]] = false;
                }
            }
        }
        return false;
    }

    /**
     * 判断某个点是否在一个按钮集合中的某个按钮内
     *
     * @param x      横坐标
     * @param y      纵坐标
     * @param button 按钮数组
     * @return
     */
    private int isInAnyScale(float x, float y, Button[] button) {
        // keys.getTop()是获取按钮所在父视图相对其父视图的右上角纵坐标
        for (int i = 0; i < button.length; i++) {
            int left = button[i].getLeft();
            int right = button[i].getRight();

           /* boolean b = x > left;
            boolean b1 = x < right;
            boolean b2 = y > button[i].getTop();
            boolean b3 = y < button[i].getBottom();
*/
            if (x > left && x < right && y > button[i].getTop() && y < button[i].getBottom()) {
                return i;
            }
        }

        return -1;
    }

    private int getBackgroundPressed(int temp, boolean isPress) {
        if (isPress)
            switch (temp) {
                case 0:
                    return R.drawable.yyqijian30;
                case 1:
                    return R.drawable.yyqijian31;
                case 2:
                    return R.drawable.yyqijian32;
                case 3:
                    return R.drawable.yyqijian33;
                case 4:
                    return R.drawable.yyqijian34;
                case 5:
                    return R.drawable.yyqijian35;
                case 6:
                    return R.drawable.yyqijian36;
                case 7:
                    return R.drawable.yyqijian37;
            }
        else
            switch (temp) {
                case 0:
                    return R.drawable.yyqijian1;
                case 1:
                    return R.drawable.yyqijian2;
                case 2:
                    return R.drawable.yyqijian3;
                case 3:
                    return R.drawable.yyqijian4;
                case 4:
                    return R.drawable.yyqijian5;
                case 5:
                    return R.drawable.yyqijian6;
                case 6:
                    return R.drawable.yyqijian7;
                case 7:
                    return R.drawable.yyqijian8;
            }
        return 0;
    }

    /**
     * 判断某个点是否在某个按钮的范围内
     *
     * @param x      横坐标
     * @param y      纵坐标
     * @param button 按钮对象
     * @return 在：true；不在：false
     */
    private boolean isInScale(float x, float y, Button button) {
        // keys.getTop()是获取按钮所在父视图相对其父视图的右上角纵坐标

        return x > button.getLeft() && x < button.getRight()
                && y > button.getTop()
                && y < button.getBottom();
    }


}
