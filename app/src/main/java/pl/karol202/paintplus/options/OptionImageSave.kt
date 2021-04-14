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
package pl.karol202.paintplus.options

import android.net.Uri
import androidx.annotation.StringRes
import pl.karol202.paintplus.R
import pl.karol202.paintplus.file.SaveFormat
import pl.karol202.paintplus.image.EffectsService
import pl.karol202.paintplus.image.FileService
import pl.karol202.paintplus.image.ImageService
import pl.karol202.paintplus.image.ViewService
import pl.karol202.paintplus.viewmodel.PaintViewModel

class OptionImageSave(private val imageService: ImageService,
                      private val fileService: FileService,
                      private val effectsService: EffectsService,
                      private val optionSave: OptionSave) : Option
{
	private val bitmapToSave get() = imageService.image.getFlattenedBitmap()

	fun execute() =
			optionSave.execute(bitmapToSave, this::onResult)

	fun executeWithUri(uri: Uri) =
			optionSave.executeWithUri(bitmapToSave, this::onResult, uri)

	fun executeWithUriAndFormat(uri: Uri, format: SaveFormat) =
			optionSave.executeWithUriAndFormat(bitmapToSave, this::onResult, uri, format)

	private fun onResult(result: OptionSave.SaveResult) = when(result)
	{
		is OptionSave.SaveResult.Success -> onSaved(result.uri, result.format)
		is OptionSave.SaveResult.Failed.CannotSave -> onError(R.string.message_cannot_save_file)
		is OptionSave.SaveResult.Failed.UnsupportedFormat -> onError(R.string.message_unsupported_format)
	}

	private fun onSaved(uri: Uri, format: SaveFormat)
	{
		fileService.onFileSave(uri, format)
		effectsService.showMessage(R.string.message_saved)
	}

	private fun onError(@StringRes message: Int) = effectsService.showMessage(message)
}
