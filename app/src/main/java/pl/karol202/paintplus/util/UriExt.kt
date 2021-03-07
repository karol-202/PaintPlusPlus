package pl.karol202.paintplus.util

import android.content.Context
import android.net.Uri
import android.provider.OpenableColumns

fun Uri.getDisplayName(context: Context) =
		context.contentResolver.query(this, null, null, null, null)?.use { cursor ->
			val index = cursor.getColumnIndex(OpenableColumns.DISPLAY_NAME)
			if(cursor.moveToFirst() && index != -1) cursor.getString(index) else null
		}
