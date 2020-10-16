/*
 * Copyright 2017 karol-202
 *
 *    Licensed under the Apache License, Version 2.0 (the "License");
 *    you may not use this file except in compliance with the License.
 *    You may obtain a copy of the License at
 *
 *        http://www.apache.org/licenses/LICENSE-2.0
 *
 *    Unless required by applicable law or agreed to in writing, software
 *    distributed under the License is distributed on an "AS IS" BASIS,
 *    WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 *    See the License for the specific language governing permissions and
 *    limitations under the License.
 */

package pl.karol202.paintplus.tool;

import android.app.Activity;
import android.content.Context;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import pl.karol202.paintplus.activity.ActivityPaint;

public abstract class ToolProperties extends Fragment
{
	private ActivityPaint activityPaint;
	protected Tool tool;
	
	@SuppressWarnings("deprecation")
	@Override
	public void onAttach(Activity activity)
	{
		super.onAttach(activity);
		init(activity);
	}
	
	@Override
	public void onAttach(Context context)
	{
		super.onAttach(context);
		init(context);
	}
	
	private void init(Context context)
	{
		if(!(context instanceof ActivityPaint))
			throw new RuntimeException("This fragment can only be attached to ActivityPaint.");
		activityPaint = (ActivityPaint) context;
	}
	
	@Override
	public View onCreateView(@NonNull LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState)
	{
		Tools tools = activityPaint.getTools();
		Bundle bundle = getArguments();
		if(bundle == null) throw new RuntimeException("No arguments found.");
		
		int toolId = bundle.getInt("tool");
		if(toolId == -1) throw new RuntimeException("-1 is not valid tool id.");
		
		this.tool = tools.getTool(toolId);
		return null;
	}
}