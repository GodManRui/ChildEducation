package com.gerryrun.childeducation;

import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.util.Log;
import android.view.MotionEvent;
import android.view.View;
import android.view.animation.AccelerateInterpolator;
import android.view.animation.AlphaAnimation;
import android.view.animation.Animation;
import android.view.animation.AnimationSet;
import android.view.animation.ScaleAnimation;
import android.widget.Button;
import android.widget.ImageView;

import com.gerryrun.childeducation.util.AnimationsContainer;
import com.gerryrun.childeducation.util.PianoMusic;

public class LearnPitch extends BaseActivity {

    private Button button[];// 按钮数组
    private PianoMusic player;// 工具类
    private boolean havePlayed[];// 是否已经播放了声音，当手指在同一个按钮内滑动，且已经发声，就为true
    private View keys;// 按钮们所在的视图
    private int pressedkey[];
    private View imDo;
    private View imRe;
    private View imMi;
    private View imFa;
    private View imSol;
    private View imLa;
    private View imSi;
    private View imDol;
    private ImageView imTvAnimation;


    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.learn_pitch);
//        CreateView();
//        setTitle("学音高");
        initView();
    }

    private void initView() {
        keys = findViewById(R.id.llKeys);
        imDo = findViewById(R.id.im_do);
        imRe = findViewById(R.id.im_re);
        imMi = findViewById(R.id.im_mi);
        imFa = findViewById(R.id.im_fa);
        imSol = findViewById(R.id.im_sol);
        imLa = findViewById(R.id.im_la);
        imSi = findViewById(R.id.im_si);
        imDol = findViewById(R.id.im_dol);

        imTvAnimation = findViewById(R.id.im_animation);

        findViewById(R.id.im_go_home).setOnClickListener((v) -> finish());
        keys.setClickable(true);
        keys.setOnTouchListener(this::onTouchListener);
//        parent = findViewById(R.id.ll_parent);
        // 新建工具类
        player = new PianoMusic(getApplicationContext());

        // 按钮资源Id
        // 按钮id
        int[] buttonId = new int[8];
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
                            button[temp].setBackgroundResource(getBackgroundPressed(temp, true));
                            // 播放音阶
                            player.soundPlay(temp);
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
                                        player.soundPlay(i);
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
                            for (int aPressedkey : pressedkey) {// 是否还有其他点
                                if (aPressedkey == temp) {
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
     * @return 按钮索引
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

    /**
     * @param isPress 是否是被按下
     */
    private int getBackgroundPressed(int temp, boolean isPress) {
        if (isPress) {
            switch (temp) {
                case 0:
                    showTvAnimation(R.array.music_tv_do);
                    setImageView(imDo);
                    return R.drawable.music_keysdianjihou_1;
                case 1:
                    showTvAnimation(R.array.music_tv_re);
                    setImageView(imRe);
                    return R.drawable.music_keysdianjihou_2;
                case 2:
                    showTvAnimation(R.array.music_tv_mi);
                    setImageView(imMi);
                    return R.drawable.music_keysdianjihou_3;
                case 3:
                    showTvAnimation(R.array.music_tv_fa);
                    setImageView(imFa);
                    return R.drawable.music_keysdianjihou_4;
                case 4:
                    showTvAnimation(R.array.music_tv_sol);
                    setImageView(imSol);
                    return R.drawable.music_keysdianjihou_5;
                case 5:
                    showTvAnimation(R.array.music_tv_la);
                    setImageView(imLa);
                    return R.drawable.music_keysdianjihou_6;
                case 6:
                    showTvAnimation(R.array.music_tv_si);
                    setImageView(imSi);
                    return R.drawable.music_keysdianjihou_7;
                case 7:
                    showTvAnimation(R.array.music_tv_dol);
                    setImageView(imDol);
                    return R.drawable.music_keysdianjihou_8;
            }
        } else
            switch (temp) {
                case 0:
                    return R.drawable.music_keysdianjiqian_1;
                case 1:
                    return R.drawable.music_keysdianjiqian_2;
                case 2:
                    return R.drawable.music_keysdianjiqian_3;
                case 3:
                    return R.drawable.music_keysdianjiqian_4;
                case 4:
                    return R.drawable.music_keysdianjiqian_5;
                case 5:
                    return R.drawable.music_keysdianjiqian_6;
                case 6:
                    return R.drawable.music_keysdianjiqian_7;
                case 7:
                    return R.drawable.music_keysdianjiqian_8;
            }
        return 0;
    }

    private void showTvAnimation(int arrays) {
        Object tag = imTvAnimation.getTag();
        if (tag != null) {
            AnimationsContainer old = (AnimationsContainer) tag;
            old.stop();
            old = null;
            tag = null;
        }
        AnimationsContainer mBgAnimation
                = new AnimationsContainer(arrays, 34).createProgressDialogAnim(imTvAnimation, false);
        imTvAnimation.setTag(mBgAnimation);
        mBgAnimation.start();
    }

    private void setImageView(View imageView) {
        if (imageView != null && imageView.getVisibility() != View.VISIBLE)
            imageView.setVisibility(View.VISIBLE);
        showMenu(imageView);
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        if (player != null) {
            player.shutdown();
            player = null;
        }
    }

    public void showMenu(View view) {
        Animation animation = view.getAnimation();
        if (animation != null) {
            animation.reset();
            view.startAnimation(animation);
            return;
        }
        AnimationSet swellAnimationSet = new AnimationSet(true);
        swellAnimationSet.addAnimation(new ScaleAnimation(0.5f, 1.0f, 0.5f, 1.0f, Animation.RELATIVE_TO_SELF, 0.5f,
                Animation.RELATIVE_TO_SELF, 0.5f));
        swellAnimationSet.addAnimation(new AlphaAnimation(0.8f, 1.0f));

        swellAnimationSet.setDuration(250);
        swellAnimationSet.setInterpolator(new AccelerateInterpolator());
        swellAnimationSet.setFillAfter(true);
        view.startAnimation(swellAnimationSet);
     /*   //三个平移动画 平移出来  
        ObjectAnimator firstAnimator = ObjectAnimator.ofFloat(view, "rotation", 0, 135, 0);

        //组合动画  
        AnimatorSet animatorSet = new AnimatorSet();
        animatorSet.setDuration(800);//动画时长  
        animatorSet.setInterpolator(new OvershootInterpolator());
        //设置动画一起播放  
        animatorSet.play(firstAnimator);

        animatorSet.start();*/
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


    public void learnChildSong(View view) {
        startActivity(new Intent(this, LearnChildSong.class));
        finish();
    }
}
