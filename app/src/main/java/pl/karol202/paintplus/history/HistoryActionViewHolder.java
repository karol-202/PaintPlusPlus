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

package pl.karol202.paintplus.history;

import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;
import pl.karol202.paintplus.R;
import pl.karol202.paintplus.history.action.Action;

public class HistoryActionViewHolder extends HistoryViewHolder
{
	public static final int PREVIEW_SIZE_DP = 60;
	
	private ImageView imagePreview;
	private TextView textName;
	
	HistoryActionViewHolder(View view)
	{
		super(view);
		imagePreview = view.findViewById(R.id.image_history_action_preview);
		textName = view.findViewById(R.id.text_history_action);
	}
	
	@Override
	void bind(final History history, Action action)
	{
		imagePreview.setImageBitmap(action.getActionPreview());
		textName.setText(action.getActionName());
	}
}