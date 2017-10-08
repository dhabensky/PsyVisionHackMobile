package com.idp.emocore;

import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Matrix;
import android.graphics.Paint;
import android.graphics.Rect;
import android.support.annotation.Nullable;
import android.util.AttributeSet;
import android.view.View;

/**
 * Created by dhabensky on 08.10.2017.
 */

public class ViewOverlay extends View {



	public ViewOverlay(Context context) {
		super(context);
	}

	public ViewOverlay(Context context, @Nullable AttributeSet attrs) {
		super(context, attrs);
	}

	public ViewOverlay(Context context, @Nullable AttributeSet attrs, int defStyleAttr) {
		super(context, attrs, defStyleAttr);
	}

	private Rect mRect;
	private Paint mPaint;

	@Override
	protected void onDraw(Canvas canvas) {
		super.onDraw(canvas);

		if (mRect != null) {
			if (mPaint == null) {
				mPaint = new Paint(Paint.ANTI_ALIAS_FLAG);
				mPaint.setStrokeWidth(4 * 3);
				mPaint.setColor(Color.WHITE);
				mPaint.setStyle(Paint.Style.STROKE);
			}
			canvas.drawRect(mRect, mPaint);
		}
	}

	public void setFaceRect(Rect rect) {
		mRect = rect;
		if (mRect != null) {
			Matrix matrix = new Matrix();
			matrix.postScale(getWidth() / 2000f, getHeight() / 2000f);
			matrix.postTranslate(getWidth() / 2f, getHeight() / 2f);

			float[] mapped = new float[4];
			matrix.mapPoints(mapped, new float[] { rect.left, rect.top, rect.right, rect.bottom });
			mRect.left = Math.round(mapped[0]);
			mRect.top  = Math.round(mapped[1]);
			mRect.right  = Math.round(mapped[2]);
			mRect.bottom = Math.round(mapped[3]);
			int left = mRect.left;
			mRect.left = getWidth() - mRect.right;
			mRect.right = getWidth() - left;
		}
		invalidate();
	}

}
