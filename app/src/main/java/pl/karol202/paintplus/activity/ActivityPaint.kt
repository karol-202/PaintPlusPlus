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
package pl.karol202.paintplus.activity

import android.content.Intent
import android.net.Uri
import android.os.Bundle
import android.view.*
import androidx.activity.result.contract.ActivityResultContract
import androidx.activity.result.contract.ActivityResultContracts
import androidx.appcompat.app.AlertDialog
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.lifecycleScope
import com.google.android.material.snackbar.Snackbar
import org.koin.androidx.viewmodel.ext.android.viewModel
import pl.karol202.paintplus.color.picker.ColorPickerContract
import pl.karol202.paintplus.databinding.ActivityPaintBinding
import pl.karol202.paintplus.history.ActivityHistory
import pl.karol202.paintplus.settings.ActivitySettings
import pl.karol202.paintplus.util.*
import pl.karol202.paintplus.viewmodel.PaintViewModel
import java.util.*

class ActivityPaint : AppCompatActivity()
{
	companion object
	{
		const val ARG_OPEN_URI = "open_uri"
		const val ARG_OPEN_PICKER = "open_picker"
		const val ARG_OPEN_CAMERA = "open_camera"
	}

	private val paintViewModel by viewModel<PaintViewModel>()
	private val views by viewBinding(ActivityPaintBinding::inflate)

	private val actions by lazy { ActivityPaintActions(this, paintViewModel) }
	private val drawers by lazy { ActivityPaintDrawers(this, views, paintViewModel) }
	private val layers by lazy { ActivityPaintLayers(this, views, paintViewModel) }

	private val openUri by argument<Uri>(ARG_OPEN_URI)
	private val openPicker by argumentOr(ARG_OPEN_PICKER, false)
	private val openCamera by argumentOr(ARG_OPEN_CAMERA, false)

	private var currentDialog: AlertDialog? = null

	val isAnyDrawerOpen get() = drawers.isAnyDrawerOpen

	override fun onCreate(savedInstanceState: Bundle?)
	{
		super.onCreate(savedInstanceState)

		setContentView(views.root)
		window.decorView.setOnSystemUiVisibilityChangeListener { enterFullscreen() }
		enterFullscreen()

		setSupportActionBar(views.toolbar.root)
		supportActionBar?.setDisplayHomeAsUpEnabled(true)
		supportActionBar?.setHomeButtonEnabled(true)

		drawers.initDrawers()
		layers.initLayers()

		views.paintView.helpers = paintViewModel.helpers
		views.paintView.onViewportSizeChangeListener = paintViewModel::setViewportSize
	}

	private fun observeViewModel()
	{
		paintViewModel.imageFlow.collectIn(lifecycleScope) { views.paintView.image = it }
		paintViewModel.selectionFlow.collectIn(lifecycleScope) { views.paintView.selection = it }
		paintViewModel.viewPositionFlow.collectIn(lifecycleScope) { views.paintView.viewPosition = it }
		paintViewModel.currentToolFlow.collectIn(lifecycleScope) { views.paintView.currentTool = it }
		paintViewModel.settingsFlow.collectIn(lifecycleScope) { views.paintView.filtering = it.smoothView }
		paintViewModel.titleFlow.collectIn(lifecycleScope) { views.toolbar.root.title = it }
		paintViewModel.dialogFlow.collectIn(lifecycleScope) { updateDialog(it) }
		paintViewModel.messageEventFlow.collectIn(lifecycleScope) { showSnackbar(it.text) }
		paintViewModel.actionRequestEventFlow.collectIn(lifecycleScope) { onActionRequest(it) }
		paintViewModel.viewUpdateEventFlow.collectIn(lifecycleScope) { views.paintView.invalidate() }
	}

	private fun updateDialog(definition: PaintViewModel.DialogDefinition?)
	{
		currentDialog?.dismiss()
		currentDialog =
				if(definition != null) AlertDialog.Builder(this)
						.also { definition.init(it) { currentDialog } }
						.setOnDismissListener { paintViewModel.hideDialog() }
						.show()
				else null
	}

	private fun showSnackbar(message: Int) = Snackbar.make(views.mainContainer, message, Snackbar.LENGTH_LONG).show()

	/*
	I'm aware that this way of using Activity Result API is not correct,
	because it does not work when the application process is killed.
	However, other parts of application also do not support being killed (e.g. bitmaps),
	so proper implementing it would require much more effort.

	Therefore, not using registerForActivityResult here (and hence ignoring lifecycle) is not a problem.
	*/
	private fun onActionRequest(request: PaintViewModel.ActionRequest<*>)
	{
		fun <I, O> registerRequest(contract: ActivityResultContract<I, O>,
		                           callback: (O) -> Unit) =
				activityResultRegistry.register(UUID.randomUUID().toString(), contract, callback)

		when(request)
		{
			is PaintViewModel.ActionRequest.OpenFile ->
				registerRequest(ActivityResultContracts.OpenDocument(), request.callback)
						.launch(request.mimeFilters.toTypedArray())
			is PaintViewModel.ActionRequest.SaveFile ->
				registerRequest(ActivityResultContracts.CreateDocument(), request.callback)
						.launch(request.suggestedName)
			is PaintViewModel.ActionRequest.CapturePhoto ->
				registerRequest(ActivityResultContracts.TakePicture(), request.callback)
						.launch(request.uri)
			is PaintViewModel.ActionRequest.PickColor ->
				registerRequest(ColorPickerContract(), request.callback)
						.launch(request.pickerConfig)
		}
	}

	override fun onPostCreate(savedInstanceState: Bundle?)
	{
		super.onPostCreate(savedInstanceState)
		drawers.postInitDrawers()
		observeViewModel()

		if(savedInstanceState == null) openImageIfRequested()
	}

	private fun openImageIfRequested() = when
	{
		openUri != null -> paintViewModel.openImage(openUri!!)
		openPicker -> paintViewModel.openImageWithoutSaving()
		openCamera -> paintViewModel.openImageFromCamera()
		else -> {}
	}

	override fun onWindowFocusChanged(hasFocus: Boolean)
	{
		super.onWindowFocusChanged(hasFocus)
		if(hasFocus) enterFullscreen()
		layers.updateView()
	}

	override fun onCreateOptionsMenu(menu: Menu): Boolean
	{
		actions.inflateMenu(menu)
		return super.onCreateOptionsMenu(menu)
	}

	override fun onPrepareOptionsMenu(menu: Menu): Boolean
	{
		actions.prepareMenu(menu)
		return super.onPrepareOptionsMenu(menu)
	}

	override fun onOptionsItemSelected(item: MenuItem) = actions.handleAction(item) || super.onOptionsItemSelected(item)

	fun showHistoryActivity() = startActivity(Intent(this, ActivityHistory::class.java))

	fun showSettingsActivity() = startActivity(Intent(this, ActivitySettings::class.java))

	fun togglePropertiesDrawer() = drawers.togglePropertiesDrawer()

	fun toggleLayersSheet() = layers.toggleLayersSheet()

	fun closeLayersSheet() = layers.closeLayersSheet()
}
