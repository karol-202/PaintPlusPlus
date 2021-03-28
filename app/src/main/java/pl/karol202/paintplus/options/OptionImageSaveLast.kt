package pl.karol202.paintplus.options

import pl.karol202.paintplus.image.FileService

class OptionImageSaveLast(private val fileService: FileService,
                          private val optionImageSave: OptionImageSave)
{
	fun execute()
	{
		val uri = fileService.lastUri ?: return
		val saveFormat = fileService.lastSaveFormat
		if(saveFormat != null) optionImageSave.executeWithUriAndFormat(uri, saveFormat)
		else optionImageSave.executeWithUri(uri)
	}
}
