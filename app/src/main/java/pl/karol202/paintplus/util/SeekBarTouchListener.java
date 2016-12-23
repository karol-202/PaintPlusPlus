package pl.karol202.paintplus.util;

import android.view.MotionEvent;
import android.view.View;

public class SeekBarTouchListener implements View.OnTouchListener
{
	@Override
	public boolean onTouch(View v, MotionEvent event)
	{
		int action = event.getAction();
		switch (action)
		{
		case MotionEvent.ACTION_DOWN:
			v.getParent().requestDisallowInterceptTouchEvent(true);
			break;
		case MotionEvent.ACTION_UP:
			v.getParent().requestDisallowInterceptTouchEvent(false);
			break;
		}
		v.onTouchEvent(event);
		return true;
	}
}