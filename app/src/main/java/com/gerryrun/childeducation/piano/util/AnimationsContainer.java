package com.gerryrun.childeducation.piano.util;

import android.content.Context;
import android.content.res.TypedArray;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.drawable.BitmapDrawable;
import android.os.Build;
import android.os.Handler;
import android.widget.ImageView;
import android.widget.ImageView.ScaleType;

import com.gerryrun.childeducation.piano.MyApplication;

import java.lang.ref.SoftReference;

/**
 * TITLE
 * Created by shixiaoming on 16/12/27.
 */

public class AnimationsContainer {
    // 单例
//    private static AnimationsContainer mInstance;
    private int FPS = 58;  // 每秒播放帧数，fps = 1/t，t-动画两帧时间间隔
    private int resId; //图片资源
    private Context mContext = MyApplication.getAppContext();
    /**
     * 循环读取帧---循环播放帧
     */
//    public class FramesSequenceAnimation {
    private boolean loop;
    private int[] mFrames; // 帧数组
    //获取单例
  /*  public static AnimationsContainer getInstance(int resId, int fps) {
        if (mInstance == null)
            mInstance = new AnimationsContainer();
        mInstance.setResId(resId, fps);
        return mInstance;
    }*/
    private int mIndex; // 当前帧
//    // 从xml中读取资源ID数组
//    private int[] mProgressAnimFrames = getData(resId);
    private boolean mShouldRun; // 开始/停止播放用
    private boolean mIsRunning; // 动画是否正在播放，防止重复播放
    private SoftReference<ImageView> mSoftReferenceImageView; // 软引用ImageView，以便及时释放掉
    private Handler mHandler;
    private int mDelayMillis;
    private OnAnimationStoppedListener mOnAnimationStoppedListener; //播放停止监听
    private Bitmap mBitmap = null;
    private BitmapFactory.Options mBitmapOptions;//Bitmap管理类，可有效减少Bitmap的OOM问题
    public AnimationsContainer() {
    }
    public AnimationsContainer(int resId, int fps) {
        setResId(resId, fps);
    }

    public void setResId(int resId, int fps) {
        this.resId = resId;
        this.FPS = fps;
    }

    public int getResId() {
        return resId;
    }

    public void createProgressDialogAnim(ImageView imageView) {
        createProgressDialogAnim(imageView, true);
    }

    /**
     * @param imageView
     * @param
     * @return progress dialog animation
     */
    public AnimationsContainer createProgressDialogAnim(ImageView imageView, boolean loop) {
//        return new FramesSequenceAnimation(imageView, getData(resId), FPS, loop);
        mHandler = new Handler();
        mFrames = getData(resId);
        mIndex = -1;
        mSoftReferenceImageView = new SoftReference<ImageView>(imageView);
        mShouldRun = false;
        mIsRunning = false;
        mDelayMillis = 1000 / FPS;//帧动画时间间隔，毫秒
        this.loop = loop;
        imageView.setScaleType(ScaleType.CENTER_CROP);
        imageView.setImageResource(mFrames[0]);

        // 当图片大小类型相同时进行复用，避免频繁GC
        if (Build.VERSION.SDK_INT >= 11) {
            Bitmap bmp = ((BitmapDrawable) imageView.getDrawable()).getBitmap();
            int width = bmp.getWidth();
            int height = bmp.getHeight();
            Bitmap.Config config = bmp.getConfig();
            mBitmap = Bitmap.createBitmap(width, height, config);
            mBitmapOptions = new BitmapFactory.Options();
            //设置Bitmap内存复用
            mBitmapOptions.inBitmap = mBitmap;//Bitmap复用内存块，类似对象池，避免不必要的内存分配和回收
            mBitmapOptions.inMutable = true;//解码时返回可变Bitmap
            mBitmapOptions.inSampleSize = 1;//缩放比例
        }
        return this;
    }

    /**
     * 从xml中读取帧数组
     *
     * @param resId
     * @return
     */
    private int[] getData(int resId) {
        TypedArray array = mContext.getResources().obtainTypedArray(resId);

        int len = array.length();
        int[] intArray = new int[array.length()];

        for (int i = 0; i < len; i++) {
            intArray[i] = array.getResourceId(i, 0);
        }
        array.recycle();
        return intArray;
    }

    /**
     * 播放动画，同步锁防止多线程读帧时，数据安全问题
     */
    public synchronized void start() {
        mShouldRun = true;
        if (mIsRunning)
            return;

        Runnable runnable = new Runnable() {
            @Override
            public void run() {
                ImageView imageView = mSoftReferenceImageView.get();
                if (!mShouldRun || imageView == null) {
                    mIsRunning = false;
                    if (mOnAnimationStoppedListener != null) {
                        mOnAnimationStoppedListener.AnimationStopped();
                    }
                    return;
                }

                mIsRunning = true;
                //新开线程去读下一帧
                mHandler.postDelayed(this, mDelayMillis);

                if (imageView.isShown()) {
                    int imageRes = getNext();
                    if (imageRes != -1) {
                        if (mBitmap != null) { // so Build.VERSION.SDK_INT >= 11
                            Bitmap bitmap = null;
                            try {
                                bitmap = BitmapFactory.decodeResource(imageView.getResources(), imageRes, mBitmapOptions);
                            } catch (Exception e) {
                                e.printStackTrace();
                            }
                            if (bitmap != null) {
                                imageView.setImageBitmap(bitmap);
                            } else {
                                imageView.setImageResource(imageRes);
                                mBitmap.recycle();
                                mBitmap = null;
                            }
                        } else {
                            imageView.setImageResource(imageRes);
                        }
                    } else stop();
                }
            }
        };
        mHandler.post(runnable);
    }

       /* public FramesSequenceAnimation(ImageView imageView, int[] frames, int fps, boolean loop) {
            mHandler = new Handler();
            mFrames = frames;
            mIndex = -1;
            mSoftReferenceImageView = new SoftReference<ImageView>(imageView);
            mShouldRun = false;
            mIsRunning = false;
            mDelayMillis = 1000 / fps;//帧动画时间间隔，毫秒
            this.loop = loop;
            imageView.setScaleType(ScaleType.CENTER_CROP);
            imageView.setImageResource(mFrames[0]);

            // 当图片大小类型相同时进行复用，避免频繁GC
            if (Build.VERSION.SDK_INT >= 11) {
                Bitmap bmp = ((BitmapDrawable) imageView.getDrawable()).getBitmap();
                int width = bmp.getWidth();
                int height = bmp.getHeight();
                Bitmap.Config config = bmp.getConfig();
                mBitmap = Bitmap.createBitmap(width, height, config);
                mBitmapOptions = new BitmapFactory.Options();
                //设置Bitmap内存复用
                mBitmapOptions.inBitmap = mBitmap;//Bitmap复用内存块，类似对象池，避免不必要的内存分配和回收
                mBitmapOptions.inMutable = true;//解码时返回可变Bitmap
                mBitmapOptions.inSampleSize = 1;//缩放比例
            }
        }*/

    //循环读取下一帧
    private int getNext() {
        mIndex++;
        if (mIndex >= mFrames.length - 1) {
            if (!loop) {
                return -1;
            }
            mIndex = 0;
        }
        return mFrames[mIndex];
    }

    /**
     * 停止播放
     */
    public synchronized void stop() {
        mShouldRun = false;
    }

    /**
     * 设置停止播放监听
     *
     * @param listener
     */
    public void setOnAnimStopListener(OnAnimationStoppedListener listener) {
        this.mOnAnimationStoppedListener = listener;
    }

    /**
     * 停止播放监听
     */
    public interface OnAnimationStoppedListener {
        void AnimationStopped();
    }
//    }
}