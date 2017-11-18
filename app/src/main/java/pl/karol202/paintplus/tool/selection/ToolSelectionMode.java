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

package pl.karol202.paintplus.tool.selection;

import android.graphics.Region.Op;
import pl.karol202.paintplus.R;

public enum ToolSelectionMode
{
	NEW(R.string.selection_mode_new, R.drawable.ic_selection_mode_new_black_24dp, Op.REPLACE),
	ADD(R.string.selection_mode_add, R.drawable.ic_selection_mode_add_black_24dp, Op.UNION),
	SUBTRACT(R.string.selection_mode_subtract, R.drawable.ic_selection_mode_subtract_black_24dp, Op.DIFFERENCE),
	MULTIPLY(R.string.selection_mode_multiply, R.drawable.ic_selection_mode_multiply_black_24dp, Op.INTERSECT);
	
	private int name;
	private int icon;
	private Op op;
	
	ToolSelectionMode(int name, int icon, Op op)
	{
		this.name = name;
		this.icon = icon;
		this.op = op;
	}
	
	public int getName()
	{
		return name;
	}
	
	public int getIcon()
	{
		return icon;
	}
	
	public Op getOp()
	{
		return op;
	}
}