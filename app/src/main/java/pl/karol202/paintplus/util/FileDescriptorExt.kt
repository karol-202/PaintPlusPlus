package pl.karol202.paintplus.util

import android.os.ParcelFileDescriptor
import java.io.FileDescriptor
import java.io.FileOutputStream
import java.lang.Exception

fun FileDescriptor.toFileOutputStream() = FileOutputStream(this)

fun ParcelFileDescriptor.closeSafely() =
		try
		{
			close()
		}
		catch(e: Exception)
		{
			e.printStackTrace()
		}
