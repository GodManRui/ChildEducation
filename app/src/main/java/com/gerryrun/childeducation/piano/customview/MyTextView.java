package com.gerryrun.childeducation.piano.customview;


import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Paint;
import android.graphics.Rect;
import android.util.AttributeSet;
import android.view.ViewGroup;
import android.widget.LinearLayout;
import android.widget.TextView;

public class MyTextView extends LinearLayout {
    private MyTextViewInner textView;

    public MyTextView(Context context) {
        super(context);
        init(null, 0);
    }

    public MyTextView(Context context, AttributeSet attrs) {
        super(context, attrs);
        init(attrs, 0);
    }

    public MyTextView(Context context, AttributeSet attrs, int defStyle) {
        super(context, attrs, defStyle);
        init(attrs, defStyle);
    }

    private void init(AttributeSet attrs, int defStyleAttr) {
        if (attrs != null && defStyleAttr > 0) {
            textView = new MyTextViewInner(getContext(), attrs, defStyleAttr);
        } else if (attrs != null) {
            textView = new MyTextViewInner(getContext(), attrs);
        } else {
            textView = new MyTextViewInner(getContext());
        }
        this.setPadding(0, 0, 0, 0);
        addView(textView);
    }

    @Override
    protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {
        super.onMeasure(widthMeasureSpec, heightMeasureSpec);
        int width = getMeasuredWidth();
        width = width + textView.getPaddingLeft() + textView.getPaddingRight();
        setMeasuredDimension(width, getMeasuredHeight());
    }

    public TextView getTextView() {
        return textView;
    }

    public class MyTextViewInner extends android.support.v7.widget.AppCompatTextView {
        //设置是否remove间距，true为remove
        private boolean noDefaultPadding = true;
        private Paint.FontMetricsInt fontMetricsInt;
        private Rect minRect;

        public MyTextViewInner(Context context) {
            super(context);
        }

        public MyTextViewInner(Context context, AttributeSet attrs) {
            super(context, attrs);
        }

        public MyTextViewInner(Context context, AttributeSet attrs, int defStyle) {
            super(context, attrs, defStyle);
        }

        @Override
        protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {
            if (fontMetricsInt == null) {
                //fontMetricsInt包含的是text文字四条线的 距离，
                //此四条线距离也是以text文字baseline为基准的
                fontMetricsInt = new Paint.FontMetricsInt();
            }
            getPaint().getFontMetricsInt(fontMetricsInt);
            if (minRect == null) {
                //minRect用来获取文字实际显示的时候的左上角和右下角  坐标
                //该坐标是以text文字baseline为基准的
                minRect = new Rect();
            }
            getPaint().getTextBounds(getText().toString(), 0, getText().length(), minRect);
            ViewGroup.MarginLayoutParams lp = (ViewGroup.MarginLayoutParams) this.getLayoutParams();
            lp.topMargin = -(fontMetricsInt.bottom - minRect.bottom) + (fontMetricsInt.top - minRect.top);
            lp.rightMargin = -(minRect.left + (getMeasuredWidth() - minRect.right));
            super.onMeasure(widthMeasureSpec, heightMeasureSpec);
        }

        @Override
        protected void onDraw(Canvas canvas) {
            if (noDefaultPadding) {
                if (fontMetricsInt == null) {
                    //fontMetricsInt包含的是text文字四条线的 距离，
                    //此四条线距离也是以text文字baseline为基准的
                    fontMetricsInt = new Paint.FontMetricsInt();
                }
                getPaint().getFontMetricsInt(fontMetricsInt);
                if (minRect == null) {
                    //minRect用来获取文字实际显示的时候的左上角和右下角  坐标
                    //该坐标是以text文字baseline为基准的
                    minRect = new Rect();
                }
                getPaint().getTextBounds(getText().toString(), 0, getText().length(), minRect);
                canvas.translate(-minRect.left, fontMetricsInt.bottom - minRect.bottom);
            }
            super.onDraw(canvas);
        }

        @Override
        public void setText(CharSequence text, BufferType type) {
            super.setText(text, type);
            this.requestLayout();
        }
    }

}
