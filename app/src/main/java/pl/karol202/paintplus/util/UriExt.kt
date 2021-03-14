package pl.karol202.paintplus.util

import android.content.Context
import android.content.Intent
import android.net.Uri
import android.os.ParcelFileDescriptor
import android.provider.OpenableColumns
import androidx.documentfile.provider.DocumentFile
import java.lang.Exception

private const val PERSISTABLE_URI_FLAGS = Intent.FLAG_GRANT_READ_URI_PERMISSION or Intent.FLAG_GRANT_WRITE_URI_PERMISSION

enum class FileDescriptorMode(val string: String)
{
	READ("r"),
	WRITE("w");
}

fun Uri.getDisplayName(context: Context) =
		context.contentResolver.query(this, null, null, null, null)?.use { cursor ->
			val index = cursor.getColumnIndex(OpenableColumns.DISPLAY_NAME)
			if(cursor.moveToFirst() && index != -1) cursor.getString(index) else null
		}

fun Uri.openFileDescriptor(context: Context, mode: FileDescriptorMode) =
		try
		{
			context.contentResolver.openFileDescriptor(this, mode.string)
		}
		catch(e: Exception)
		{
			e.printStackTrace()
			null
		}

fun Uri.takePersistablePermission(context: Context) =
		try
		{
			context.contentResolver.takePersistableUriPermission(this, PERSISTABLE_URI_FLAGS)
		}
		catch(e: Exception)
		{
			e.printStackTrace()
		}

fun Uri.delete(context: Context) = DocumentFile.fromSingleUri(context, this)!!.delete()
