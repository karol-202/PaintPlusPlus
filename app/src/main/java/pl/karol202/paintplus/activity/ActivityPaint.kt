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

import android.annotation.TargetApi
import android.content.Intent
import android.net.Uri
import android.os.Build
import android.os.Bundle
import android.view.*
import androidx.activity.result.contract.ActivityResultContract
import androidx.activity.result.contract.ActivityResultContracts
import androidx.appcompat.app.AlertDialog
import androidx.appcompat.app.AppCompatActivity
import androidx.coordinatorlayout.widget.CoordinatorLayout
import androidx.lifecycle.lifecycleScope
import com.google.android.material.snackbar.Snackbar
import org.koin.androidx.viewmodel.ext.android.viewModel
import pl.karol202.paintplus.databinding.ActivityPaintBinding
import pl.karol202.paintplus.legacy.AppContextLegacy
import pl.karol202.paintplus.options.OptionFileCapturePhoto
import pl.karol202.paintplus.options.OptionFileOpen
import pl.karol202.paintplus.recent.RecentViewModel
import pl.karol202.paintplus.settings.ActivitySettings
import pl.karol202.paintplus.util.*
import pl.karol202.paintplus.viewmodel.PaintViewModel
import pl.karol202.paintplus.viewmodel.PaintViewModel.ImageEvent
import java.util.*

class ActivityPaint : AppCompatActivity(), AppContextLegacy
{
	companion object
	{
		const val ARG_OPEN_URI = "open_uri"
		const val ARG_OPEN_PICKER = "open_picker"
		const val ARG_OPEN_CAMERA = "open_camera"
	}

	private val recentViewModel by viewModel<RecentViewModel>()
	private val paintViewModel by viewModel<PaintViewModel>()
	private val views by viewBinding(ActivityPaintBinding::inflate)

	private val actions by lazy { ActivityPaintActions(this, recentViewModel, paintViewModel) }
	private val drawers by lazy { ActivityPaintDrawers(this, views, paintViewModel) }
	private val layers by lazy { ActivityPaintLayers(this, views, paintViewModel) }

	private val resultListeners = mutableMapOf<Int, ActivityResultListener>()

	private val openUri by argument<Uri>(ARG_OPEN_URI)
	private val openPicker by argumentOr(ARG_OPEN_PICKER, false)
	private val openCamera by argumentOr(ARG_OPEN_CAMERA, false)

	private var currentDialog: AlertDialog? = null

	val image get() = paintViewModel.image
	val tools get() = paintViewModel.tools
	val isAnyDrawerOpen get() = drawers.isAnyDrawerOpen
	val mainContainer get() = views.mainContainer

	override fun onCreate(savedInstanceState: Bundle?)
	{
		super.onCreate(savedInstanceState)

		setContentView(views.root)
		window.decorView.setOnSystemUiVisibilityChangeListener { initSystemUIVisibility() }
		initSystemUIVisibility()

		setSupportActionBar(views.toolbar.root)
		supportActionBar?.setDisplayHomeAsUpEnabled(true)
		supportActionBar?.setHomeButtonEnabled(true)

		views.paintView.setImage(paintViewModel.image)

		drawers.initDrawers()
		layers.initLayers()
	}

	private fun initSystemUIVisibility()
	{
		if(Build.VERSION.SDK_INT >= Build.VERSION_CODES.R) initSystemUIVisibility30() else initSystemUIVisibilityPre30()
	}

	@TargetApi(Build.VERSION_CODES.R)
	private fun initSystemUIVisibility30()
	{
		window.setDecorFitsSystemWindows(false)
		window.insetsController?.systemBarsBehavior = WindowInsetsController.BEHAVIOR_SHOW_TRANSIENT_BARS_BY_SWIPE
		window.insetsController?.hide(WindowInsets.Type.systemBars())
	}

	// Can be replaced with usage of WindowInsetsControllerCompat as soon as the bug is resolved:
	// https://issuetracker.google.com/issues/173203649
	@Suppress("DEPRECATION")
	private fun initSystemUIVisibilityPre30()
	{
		window.decorView.systemUiVisibility = View.SYSTEM_UI_FLAG_LAYOUT_STABLE or
				View.SYSTEM_UI_FLAG_LAYOUT_HIDE_NAVIGATION or
				View.SYSTEM_UI_FLAG_LAYOUT_FULLSCREEN or
				View.SYSTEM_UI_FLAG_HIDE_NAVIGATION or
				View.SYSTEM_UI_FLAG_FULLSCREEN or
				View.SYSTEM_UI_FLAG_IMMERSIVE_STICKY
	}

	private fun observeViewModel()
	{
		paintViewModel.settingsFlow.collectIn(lifecycleScope) {
			views.paintView.setSettings(it)
		}
		paintViewModel.currentToolFlow.collectIn(lifecycleScope) {
			views.paintView.setCurrentTool(it)
		}
		paintViewModel.titleFlow.collectIn(lifecycleScope) {
			views.toolbar.root.title = it
		}
		paintViewModel.dialogFlow.collectIn(lifecycleScope) { definition ->
			currentDialog?.dismiss()
			currentDialog =
					if(definition != null) AlertDialog.Builder(this)
							.apply(definition::init)
							.setOnDismissListener { paintViewModel.hideDialog() }
							.show()
					else null
		}
		paintViewModel.messageEventFlow.collectIn(lifecycleScope) {
			showSnackbar(it.text)
		}
		paintViewModel.imageEventFlow.collectIn(lifecycleScope) {
			when(it)
			{
				ImageEvent.IMAGE_CHANGED -> views.paintView.notifyImageChanged()
				ImageEvent.LAYERS_CHANGED -> views.paintView.notifyLayersChanged()
				ImageEvent.IMAGE_MATRIX_CHANGED -> views.paintView.notifyImageMatrixChanged()
				ImageEvent.SELECTION_CHANGED -> views.paintView.notifySelectionChanged()
				else -> {}
			}
		}
		paintViewModel.actionRequestEventFlow.collectIn(lifecycleScope) { request ->
			/*
			I'm aware that this way of using Activity Result API is not correct,
			because it does not work when the application process is killed.
			However, other parts of application also do not support being killed (e.g. bitmaps),
			so proper implementing it would require much more effort.

			Therefore, not using registerForActivityResult here (and hence ignoring lifecycle) is not a problem.
			*/

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
			}
		}
	}

	private fun showSnackbar(message: Int) = Snackbar.make(views.mainContainer, message, Snackbar.LENGTH_LONG).show()

	override fun onPostCreate(savedInstanceState: Bundle?)
	{
		super.onPostCreate(savedInstanceState)
		drawers.postInitDrawers()
		observeViewModel()

		if(savedInstanceState == null) openImageIfRequested()
	}

	private fun openImageIfRequested() = when
	{
		openUri != null -> OptionFileOpen(recentViewModel, paintViewModel).executeWithUri(openUri!!)
		openPicker -> OptionFileOpen(recentViewModel, paintViewModel).executeWithoutSaving()
		openCamera -> OptionFileCapturePhoto(paintViewModel).execute()
		else -> {}
	}

	override fun onWindowFocusChanged(hasFocus: Boolean)
	{
		super.onWindowFocusChanged(hasFocus)
		if(hasFocus) initSystemUIVisibility()
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

	fun showSettingsActivity() = startActivity(Intent(this, ActivitySettings::class.java))

	fun registerActivityResultListener(requestCode: Int, listener: ActivityResultListener)
	{
		if(requestCode in resultListeners) throw RuntimeException("requestCode is already used: $requestCode")
		resultListeners[requestCode] = listener
	}

	fun unregisterActivityResultListener(requestCode: Int)
	{
		if(requestCode !in resultListeners) throw RuntimeException("requestCode isn't registered yet: $requestCode")
		resultListeners.remove(requestCode)
	}

	override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?)
	{
		super.onActivityResult(requestCode, resultCode, data)
		resultListeners[requestCode]?.onActivityResult(resultCode, data)
	}

	fun togglePropertiesDrawer() = drawers.togglePropertiesDrawer()

	fun toggleLayersSheet() = layers.toggleLayersSheet()

	fun closeLayersSheet() = layers.closeLayersSheet()

	fun setScrollingBlocked(blocked: Boolean) = layers.setScrollingBlocked(blocked)

	// LEGACY

	override fun getContext() = this

	override fun createSnackbar(message: Int, duration: Int): Snackbar
	{
		val snackbar = Snackbar.make(views.mainContainer, message, duration)
		val params = snackbar.view.layoutParams as CoordinatorLayout.LayoutParams
		params.setMargins(0, 0, 0, -NavigationBarUtils.getNavigationBarHeight(this))
		return snackbar
	}
}
