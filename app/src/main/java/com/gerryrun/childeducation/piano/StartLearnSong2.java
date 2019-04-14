package com.gerryrun.childeducation.piano;

import android.animation.Animator;
import android.animation.AnimatorListenerAdapter;
import android.animation.ObjectAnimator;
import android.app.ProgressDialog;
import android.content.Context;
import android.media.MediaPlayer;
import android.media.MediaPlayer.OnPreparedListener;
import android.net.Uri;
import android.os.Bundle;
import android.os.Environment;
import android.support.annotation.Nullable;
import android.support.annotation.UiThread;
import android.util.DisplayMetrics;
import android.util.Log;
import android.view.View;
import android.view.WindowManager;
import android.view.animation.LinearInterpolator;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.Toast;

import com.gerryrun.childeducation.piano.bean.SelectSong.DataBean;
import com.gerryrun.childeducation.piano.customview.FullVideoView;
import com.gerryrun.childeducation.piano.parse.ReadMIDI;
import com.gerryrun.childeducation.piano.parse.entity.ResultSequence;
import com.gerryrun.childeducation.piano.util.AnimationsContainer;
import com.gerryrun.childeducation.piano.util.NetUtil;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.Serializable;
import java.util.ArrayList;

import okhttp3.Call;
import okhttp3.Callback;
import okhttp3.Response;

import static com.gerryrun.childeducation.piano.StartLearnSong.getBearArrays;
import static com.gerryrun.childeducation.piano.StartLearnSong.getFrameArrays;
import static com.gerryrun.childeducation.piano.StartLearnSong.getImagePitch;

/**
 * 系统动画
 */
public class StartLearnSong2 extends BaseActivity {


    private MediaPlayer mediaPlayer;
    private FrameLayout flAddPitch;
    private Thread playerThread;
    private boolean isPlaying;
    private float pitchSpace = 6.3f;               //音符间距 占rightSpacePx宽度的比例 也就是说，基线右边屏准备最多放几个音符
    private float baselineScaling = 0.245f;         //绿色线位置在屏幕宽度的比例

    private float pitchMarginPx;      //标准一个音符的间距
    private float leftSpacePx;      //基线左边的像素个数
    private float rightSpacePx;     //基线右边的像素个数
    private float dol;
    private float re;
    private float mi;
    private float fa;
    private float sol;
    private float la;
    private float si;
    private int pitchWH;
    private ImageView imageBoomAnimationView;
    private int mBlastWH;
    private ImageView imPlayPause;
    private ArrayList<ResultSequence> resultSequences;
    private ArrayList<ImageView> imagePitchViews;
    private boolean resetPlay = true;

    private FullVideoView videoView;
    private ImageView imMusicBear;
    private volatile boolean measureOk;
    private DataBean mData;
    private boolean videoViewPrepared;
    private boolean mediaPlayerPrepared;
    private ProgressDialog progressDialog;
    private InputStream inputStream;
    private File musicFile;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.start_learn_song);
        initData();

    }

    private void initData() {
        progressDialog = new ProgressDialog(this);
        progressDialog.setMessage("初始化资源...");
        progressDialog.setCancelable(false);
        progressDialog.show();
        try {
            mData = (DataBean) getIntent().getSerializableExtra("data");
        } catch (Exception e) {
        }
        String musicUrl = mData.getUrl();
        String[] split = musicUrl.split("/");
        musicFile = new File(getCacheDir().getAbsolutePath() + "/" + split[split.length - 1]);
        Log.w("JerryZhu", "写入路径:: " + musicFile.getAbsolutePath());
       /* if (!file.canWrite()) {
            Toast.makeText(this, "未授予读写权限！", Toast.LENGTH_SHORT).show();
            finish();
        }*/
        NetUtil.getQuestion(musicUrl, new Callback() {
            @Override
            public void onFailure(Call call, IOException e) {

            }

            @Override
            public void onResponse(Call call, Response response) throws IOException {

                InputStream is = null;
                byte[] buf = new byte[2048];
                int len = 0;
                FileOutputStream fos = null;
                try {
                    long total = response.body().contentLength();
                    Log.e("jerry", "total------>" + total);
                    long current = 0;
                    is = response.body().byteStream();
                    fos = new FileOutputStream(musicFile);
                    while ((len = is.read(buf)) != -1) {
                        current += len;
                        fos.write(buf, 0, len);
                    }
                    fos.flush();
                    Log.w("JerryZhu", "onResponse: SUCCESS");
                    runOnUiThread(() -> {
                        initBackgroundAnim();
                        initPlayer();
                    });
//                    successCallBack((T) file, callBack);
                } catch (Exception e) {
//                    failedCallBack("下载失败", callBack);
                } finally {
                    try {
                        if (is != null) {
                            is.close();
                        }
                        if (fos != null) {
                            fos.close();
                        }
                    } catch (IOException e) {
                    }
                }
            }
        });
    }

    private void initBackgroundAnim() {
        WindowManager wm = (WindowManager) this.getSystemService(Context.WINDOW_SERVICE);
        DisplayMetrics dm = new DisplayMetrics();
        if (wm != null)
            wm.getDefaultDisplay().getMetrics(dm);
        int widthPx = dm.widthPixels;         // 屏幕宽度（像素）
        int heightPx = dm.heightPixels;
        if (heightPx != 0) {
            if ((widthPx / heightPx) - 16 / 9 > 0.2f) {
                baselineScaling = 0.27f;
            }
        }
        flAddPitch = findViewById(R.id.fl_add_pitch);
        imMusicBear = findViewById(R.id.im_music_bear);
        imPlayPause = findViewById(R.id.im_play_pause);
        imPlayPause.setOnClickListener((v) -> clickPlayPause());
        findViewById(R.id.im_home).setOnClickListener(v -> finish());
        flAddPitch.post(() -> {
            leftSpacePx = flAddPitch.getWidth() * baselineScaling;
            rightSpacePx = flAddPitch.getWidth() * (1 - baselineScaling);
            pitchMarginPx = rightSpacePx / pitchSpace;


            int height = flAddPitch.getHeight();
            pitchWH = (int) (height * 0.19f);
            dol = height * (1f - 0.19f);//*
            re = height * 0.73f;    //*
            mi = height * 0.62f;    //*
            fa = height * 0.54f;    //*
            sol = height * 0.43f;//*
            la = height * 0.35f; //*
            si = height * 0.25f;

            mBlastWH = pitchWH * 3;
            FrameLayout.LayoutParams params = new FrameLayout.LayoutParams(mBlastWH, mBlastWH);
            params.leftMargin = (int) leftSpacePx - mBlastWH / 2;
            imageBoomAnimationView = new ImageView(this);
            imageBoomAnimationView.setLayoutParams(params);
            imageBoomAnimationView.setVisibility(View.INVISIBLE);
            flAddPitch.addView(imageBoomAnimationView);
            measureOk = true;
        });

        videoView = findViewById(R.id.bg_video);
        videoView.setVideoURI(Uri.parse(mData.getGift()));
        videoView.setOnPreparedListener(mp -> {
            if (mediaPlayerPrepared && progressDialog != null)
                progressDialog.dismiss();
            videoViewPrepared = true;
        });
//        videoView.setVideoURI(Uri.parse("android.resource://" + getPackageName() + "/" + R.raw.music_woniu));
        videoView.start();

        AnimationsContainer mBgAnimation
                = new AnimationsContainer(R.array.music_bear_nomal, 20).createProgressDialogAnim(imMusicBear, true);
        imMusicBear.setTag(mBgAnimation);
        mBgAnimation.start();
    }

    private void initPlayer() {
        //将声音资源文件设置给MediaPlayer对象
//        mediaPlayer = MediaPlayer.create(this, R.raw.small_start);
//        mediaPlayer = MediaPlayer.create(this, Uri.parse(mData.getUrl()));
        mediaPlayer = MediaPlayer.create(this, Uri.fromFile(musicFile));
        mediaPlayer.setOnPreparedListener(mp -> {
            if (videoViewPrepared && progressDialog != null) {
                progressDialog.dismiss();
            }
            mediaPlayerPrepared = true;
        });
        playerThread = new Thread(new MusicThread());
        playerThread.start();
    }

    private void clickPlayPause() {
        if (imagePitchViews == null || imagePitchViews.size() <= 0) {
            Toast.makeText(this, "正在初始化,请稍后", Toast.LENGTH_SHORT).show();
            return;
        }
        if (mediaPlayer == null) {
            Toast.makeText(this, "退出时间过长,请重新进入...", Toast.LENGTH_SHORT).show();
            finish();
            return;
        }
        if (isPlaying) {  //播放状态 ，暂停播放
            imPlayPause.setBackgroundResource(R.drawable.play);
            for (ImageView imagePitchView : imagePitchViews) {
                ObjectAnimator translationX = (ObjectAnimator) imagePitchView.getTag();
                translationX.pause();
            }
            isPlaying = false;
            mediaPlayer.pause();
        } else { //暂停状态， 开始播放
            imPlayPause.setBackgroundResource(R.drawable.pause);
            for (ImageView imagePitchView : imagePitchViews) {
                ObjectAnimator translationX = (ObjectAnimator) imagePitchView.getTag();
                if (resetPlay) {             //如果是从头播放的
                    if (imagePitchView.getParent() == null)
                        flAddPitch.addView(imagePitchView);
                    if (imagePitchView.getVisibility() != View.VISIBLE)
                        imagePitchView.setVisibility(View.VISIBLE);
                    translationX.start();
                    continue;
                }
                translationX.resume();
            }
            if (resetPlay) resetPlay = false;
            isPlaying = true;
//            if (resetPlay)
            mediaPlayer.start();
        }
    }

    @Override
    public void onBackPressed() {
        super.onBackPressed();
        onMyDestroy();
    }

    private void onMyDestroy() {
        if (mediaPlayer != null) {
            mediaPlayer.stop();
            mediaPlayer.release();
            mediaPlayer = null;
        }
        isPlaying = false;
        if (playerThread != null) {
            playerThread.interrupt();
        }
        if (videoView != null) {
            videoView.stopPlayback();
            videoView = null;
        }
    }

    @Override
    protected void onPause() {
        super.onPause();
        try {
            onMyDestroy();
        } catch (Exception e) {
        }
    }

    @Override
    protected void onResume() {
        super.onResume();
        if (videoView != null && !videoView.isPlaying()) {
            videoView.start();
        }
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        try {
            onMyDestroy();
        } catch (Exception e) {
        }

    }

    @UiThread
    private void preparePlay() {
        if (mediaPlayer == null) return;
        mediaPlayer.setOnCompletionListener(mp -> {
            isPlaying = false;
            resetPlay = true;
            imPlayPause.setBackgroundResource(R.drawable.play);
            AnimationsContainer old = (AnimationsContainer) imMusicBear.getTag();
            if (old != null) {
                old.stop();
                old = null;
            }
            AnimationsContainer newAnimationsContainer = new AnimationsContainer(getBearArrays(0), 30).createProgressDialogAnim(imMusicBear, false);
            imMusicBear.setTag(newAnimationsContainer);
            newAnimationsContainer.start();
        });
        imagePitchViews = new ArrayList<>();
        for (int i = 0; i < resultSequences.size(); i += 2) {
            createImagePitch(resultSequences.get(i));
        }
    }

    private synchronized void createImagePitch(ResultSequence resultSequence) {

        String pitch = resultSequence.getPitchNote();
//        float marginTop = getMarginTop(pitch) / flAddPitch.getHeight();

        ImageView imageView = new ImageView(StartLearnSong2.this);
        FrameLayout.LayoutParams params = new FrameLayout.LayoutParams(pitchWH, pitchWH);
        params.topMargin = (int) getMarginTop(pitch);
//        imageView.setTag(params.topMargin);
        imageView.setLayoutParams(params);
        int imagePitchSource = getImagePitch(pitch);
        imageView.setImageResource(imagePitchSource);
        imageView.setTag(R.id.tag_resource_id, imagePitchSource);

            /*Animation animation = new TranslateAnimation(Animation.RELATIVE_TO_PARENT, 1f, Animation.RELATIVE_TO_PARENT, 0.2f,
                    Animation.RELATIVE_TO_PARENT, 0.5f, Animation.RELATIVE_TO_PARENT, 0.5f);*/
        float fromXValue = (float) (baselineScaling + resultSequence.getCurrentTime() * 0.1f);
    /*    Animation animation = new TranslateAnimation(Animation.RELATIVE_TO_PARENT, fromXValue, Animation.RELATIVE_TO_PARENT, 0.2f,
                Animation.RELATIVE_TO_SELF, 0, Animation.RELATIVE_TO_SELF, 0);*/
        ObjectAnimator translationX = ObjectAnimator.ofFloat(imageView, "translationX", flAddPitch.getWidth() * fromXValue, flAddPitch.getWidth() * baselineScaling);
        long round = Math.round(resultSequence.getCurrentTime() * 1000);
        translationX.setInterpolator(new LinearInterpolator());

        translationX.setDuration(round);
        translationX.addListener(new AnimatorListenerAdapter() {
            @Override
            public void onAnimationEnd(Animator animation) {
                super.onAnimationEnd(animation);
                removeAnimation(imageView);
            }
        });
        imageView.setTag(translationX);
        imagePitchViews.add(imageView);
//        flAddPitch.addView(imageView);
//        imageView.setVisibility(View.VISIBLE);
    }

    private float getMarginTop(String pitchNote) {
        switch (pitchNote) {
            case "C":
                return dol;
            case "D":
                return re;
            case "E":
                return mi;
            case "F":
                return fa;
            case "G":
                return sol;
            case "A":
                return la;
            case "B":
                return si;
        }
        return 0;
    }

    private void removeAnimation(ImageView image) {
        int resourceId = (int) image.getTag(R.id.tag_resource_id);
        AnimationsContainer old = (AnimationsContainer) imMusicBear.getTag();
        if (old != null) {
            old.stop();
            old = null;
        }
        AnimationsContainer newAnimationsContainer = new AnimationsContainer(getBearArrays(resourceId), 30).createProgressDialogAnim(imMusicBear, false);
        imMusicBear.setTag(newAnimationsContainer);
        newAnimationsContainer.start();
        imageBoomAnimationView.clearAnimation();
        if (imageBoomAnimationView.getVisibility() != View.VISIBLE)
            imageBoomAnimationView.setVisibility(View.VISIBLE);
        if (imageBoomAnimationView.getTag() != null) {
            AnimationsContainer progressDialogAnim = (AnimationsContainer) imageBoomAnimationView.getTag();
            progressDialogAnim.stop();
            progressDialogAnim = null;
        }
        FrameLayout.LayoutParams animationParams = (FrameLayout.LayoutParams) imageBoomAnimationView.getLayoutParams();
        animationParams.topMargin = ((FrameLayout.LayoutParams) image.getLayoutParams()).topMargin - (mBlastWH / 2 - pitchWH / 2);
        imageBoomAnimationView.setLayoutParams(animationParams);
        AnimationsContainer progressDialogAnim = new AnimationsContainer(getFrameArrays(resourceId), 60).createProgressDialogAnim(imageBoomAnimationView, false);
        flAddPitch.removeView(image);
        progressDialogAnim.start();
    }


    class MusicThread implements Runnable {
        @Override
        public void run() {
            //todo 解析文件
            ReadMIDI readMIDI = new ReadMIDI();
//            resultSequences = readMIDI.myRead(null, getResources().openRawResource(R.raw.small_start));
            try {
                FileInputStream fileInputStream = new FileInputStream(musicFile);

                resultSequences = readMIDI.myRead(null, fileInputStream);
                if (resultSequences == null) {
                    return;
                }
                for (ResultSequence resultSequence : resultSequences) {
                    Log.w("JerryZhu", "run: " + resultSequence);
                }
                while (!measureOk) {
                }
            } catch (FileNotFoundException e) {
                e.printStackTrace();
            }
            runOnUiThread(StartLearnSong2.this::preparePlay);
        }
    }
}
