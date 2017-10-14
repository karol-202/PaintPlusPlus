package pl.karol202.paintplus.history;

import android.content.Context;
import android.content.Intent;

public class ActivityHistoryHelper
{
	private static History history;//Is there any way to remove this ugly static veriable?
	
	private Context context;
	
	public ActivityHistoryHelper(History history, Context context)
	{
		ActivityHistoryHelper.history = history;
		this.context = context;
	}
	
	public void startActivity()
	{
		Intent intent = new Intent(context, ActivityHistory.class);
		context.startActivity(intent);
	}
	
	static History getHistory()
	{
		if(history == null) throw new NullPointerException("History object has been already used.");
		History history = ActivityHistoryHelper.history;
		ActivityHistoryHelper.history = null;
		return history;
	}
}