package pl.karol202.paintplus.activity;

import android.content.Intent;

public interface ActivityResultListener
{
	void onActivityResult(int resultCode, Intent data);
}