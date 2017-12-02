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

package pl.karol202.paintplus.tool.shape;

import android.graphics.Paint;
import pl.karol202.paintplus.R;

public enum Join
{
	MITTER(R.string.join_mitter, R.drawable.ic_join_mitter_black_24dp, Paint.Join.MITER),
	BEVEL(R.string.join_bevel, R.drawable.ic_join_bevel_black_24dp, Paint.Join.BEVEL),
	ROUND(R.string.join_round, R.drawable.ic_join_round_black_24dp, Paint.Join.ROUND);
	
	private int name;
	private int icon;
	private Paint.Join paintJoin;
	
	Join(int name, int icon, Paint.Join paintJoin)
	{
		this.name = name;
		this.icon = icon;
		this.paintJoin = paintJoin;
	}
	
	public int getName()
	{
		return name;
	}
	
	public int getIcon()
	{
		return icon;
	}
	
	public Paint.Join getPaintJoin()
	{
		return paintJoin;
	}
}