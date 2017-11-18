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

package pl.karol202.paintplus.tool.shape.line;

import android.graphics.Paint;
import pl.karol202.paintplus.R;

public enum Cap
{
	ROUND(R.string.line_cap_round, R.drawable.ic_cap_round_black_24dp, Paint.Cap.ROUND),
	SQUARE(R.string.line_cap_square, R.drawable.ic_cap_square_black_24dp, Paint.Cap.SQUARE),
	BUTT(R.string.line_cap_butt, R.drawable.ic_cap_butt_black_24dp, Paint.Cap.BUTT);
	
	private int name;
	private int icon;
	private Paint.Cap paintCap;
	
	Cap(int name, int icon, Paint.Cap paintCap)
	{
		this.name = name;
		this.icon = icon;
		this.paintCap = paintCap;
	}
	
	public int getName()
	{
		return name;
	}
	
	public int getIcon()
	{
		return icon;
	}
	
	public Paint.Cap getPaintCap()
	{
		return paintCap;
	}
}