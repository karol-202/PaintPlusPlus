package pl.karol202.paintplus.image

import android.graphics.Region

data class Selection(val region: Region)
{
	companion object
	{
		val empty = Selection(Region())
	}

	val path = region.boundaryPath
}
