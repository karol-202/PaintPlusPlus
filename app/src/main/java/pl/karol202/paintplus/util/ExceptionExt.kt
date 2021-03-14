package pl.karol202.paintplus.util

import java.io.Closeable
import java.io.IOException

fun <T : Closeable, R> T.useSuppressingIOException(block: (T) -> R) =
		try
		{
			use(block)
		}
		catch(e: IOException)
		{
			e.printStackTrace()
			null
		}
