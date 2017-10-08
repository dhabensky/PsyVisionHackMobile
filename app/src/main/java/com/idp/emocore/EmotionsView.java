package com.idp.emocore;


import android.content.Context;
import android.support.annotation.Nullable;
import android.util.AttributeSet;
import android.view.View;
import android.widget.FrameLayout;
import android.widget.TextView;

/**
 * Created by dhabensky on 08.10.2017.
 */

public class EmotionsView extends FrameLayout {

	private View mView;
	private TextView mAudioEmotion;
	private TextView mImageEmotion;

	public EmotionsView(Context context) {
		super(context);
		init();
	}

	public EmotionsView(Context context, @Nullable AttributeSet attrs) {
		super(context, attrs);
		init();
	}

	public EmotionsView(Context context, @Nullable AttributeSet attrs, int defStyleAttr) {
		super(context, attrs, defStyleAttr);
		init();
	}

	private void init() {
		mView = inflate(getContext(), R.layout.emotions, this);
		mAudioEmotion = (TextView) mView.findViewById(R.id.audio_emotion);
		mImageEmotion = (TextView) mView.findViewById(R.id.image_emotion);
	}

	public void setAudioEmotion(String emotion, float value) {
		mAudioEmotion.setText(format(emotion, value));
	}

	public void setImageEmotion(String emotion, float value) {
		mImageEmotion.setText(emotion);//format(emotion, value));
	}

	private String format(String emotion, float val) {
		return emotion + ": " + val;
	}

}
