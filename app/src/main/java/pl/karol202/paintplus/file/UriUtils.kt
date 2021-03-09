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
package pl.karol202.paintplus.file

import android.content.Context
import android.net.Uri
import android.os.ParcelFileDescriptor
import androidx.core.util.rangeTo
import pl.karol202.paintplus.file.UriUtils
import androidx.documentfile.provider.DocumentFile
import java.io.IOException
import java.lang.Exception

enum class FileDescriptorMode(val string: String)
{
	READ("r"),
	WRITE("w");
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

fun ParcelFileDescriptor.closeSafely() =
		try
		{
			close()
		}
		catch(e: Exception)
		{
			e.printStackTrace()
		}

// LEGACY
object UriUtils
{
	@JvmStatic
	fun createFileOpenDescriptor(context: Context, uri: Uri) = uri.openFileDescriptor(context, FileDescriptorMode.READ)

	@JvmStatic
	fun createFileSaveDescriptor(context: Context, uri: Uri) = uri.openFileDescriptor(context, FileDescriptorMode.WRITE)

	@JvmStatic
	fun closeFileDescriptor(fileDescriptor: ParcelFileDescriptor?) = fileDescriptor?.closeSafely()

	@JvmStatic
	fun deleteDocument(context: Context?, uri: Uri?)
	{
		val file = DocumentFile.fromSingleUri(context!!, uri!!)
		file?.delete()
	}
}
