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

package pl.karol202.paintplus.recent;

import android.content.Context;
import android.graphics.Bitmap;
import android.net.Uri;
import pl.karol202.paintplus.file.UriMetadata;

public class RecentImageCreator implements OnFileEditListener
{
	private static final int MAX_BOUND = 256;
	
	private Context context;
	
	public RecentImageCreator(Context context)
	{
		this.context = context;
	}
	
	@Override
	public void onFileEdited(Uri uri, Bitmap bitmap)
	{
		RecentLoader loader = new RecentLoader(context);
		loader.load();
		loader.addOrUpdateRecentImage(createRecentImage(uri, bitmap));
		loader.save();
	}
	
	private RecentImage createRecentImage(Uri uri, Bitmap originalBitmap)
	{
		UriMetadata metadata = new UriMetadata(context, uri);
		String[] parts = metadata.getDisplayName().split("/");
		String name = parts[parts.length - 1];
		Bitmap thumbnail = createThumbnail(originalBitmap);
		
		return new RecentImage(uri, null, thumbnail, name, System.currentTimeMillis());
	}
	
	private Bitmap createThumbnail(Bitmap originalBitmap)
	{
		int width = Math.min(originalBitmap.getWidth(), MAX_BOUND);
		int height = Math.min(originalBitmap.getHeight(), MAX_BOUND);
		float ratio = (float) originalBitmap.getWidth() / (float) originalBitmap.getHeight();
		if(ratio < 1) width *= ratio;
		else if(ratio > 1) height /= ratio;
		return Bitmap.createScaledBitmap(originalBitmap, width, height, false);
	}
}