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

package pl.karol202.paintplus.file.explorer;

import android.content.Intent;
import android.net.Uri;
import pl.karol202.paintplus.R;

import java.io.File;

public class ActivityFileOpen extends ActivityFileChoose
{
	@Override
	public int getLayout()
	{
		return R.layout.activity_file_open;
	}
	
	@Override
	public boolean onFileSelected(File file)
	{
		if(!super.onFileSelected(file)) return false;
		Intent intent = new Intent();
		intent.setData(Uri.fromFile(file));
		setResult(RESULT_OK, intent);
		finish();
		return true;
	}
}