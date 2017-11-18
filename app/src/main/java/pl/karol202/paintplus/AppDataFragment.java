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

package pl.karol202.paintplus;

import android.os.Bundle;
import android.support.v4.app.Fragment;
import pl.karol202.paintplus.image.Image;
import pl.karol202.paintplus.tool.Tool;
import pl.karol202.paintplus.tool.Tools;

public class AppDataFragment extends Fragment
{
	public static final String TAG = "DATA_FRAGMENT";
	
	private AsyncManager asyncManager;
	private Image image;
	private Tools tools;
	
	@Override
	public void onCreate(Bundle savedInstanceState)
	{
		super.onCreate(savedInstanceState);
		setRetainInstance(true);
		
		image = new Image(getActivity());
		image.newImage(600, 600);
		
		tools = new Tools(image, asyncManager);
	}
	
	public void setAsyncManager(AsyncManager asyncManager)
	{
		this.asyncManager = asyncManager;
	}
	
	public Image getImage()
	{
		return image;
	}
	
	public Tools getTools()
	{
		return tools;
	}
	
	public Tool getCurrentTool()
	{
		return tools.getCurrentTool();
	}

	public void setCurrentTool(Tool tool)
	{
		tools.setCurrentTool(tool);
	}
}