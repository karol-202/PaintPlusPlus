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
import android.content.pm.PackageManager
import android.net.Uri
import android.os.Build
import android.os.Bundle
import android.view.*
import androidx.appcompat.app.AppCompatActivity
import androidx.coordinatorlayout.widget.CoordinatorLayout
import androidx.lifecycle.lifecycleScope
import com.google.android.material.snackbar.Snackbar
import org.koin.androidx.viewmodel.ext.android.viewModel
import pl.karol202.paintplus.AsyncManager
import pl.karol202.paintplus.R
import pl.karol202.paintplus.activity.PermissionRequest.PermissionGrantListener
import pl.karol202.paintplus.activity.PermissionRequest.PermissionGrantingActivity
import pl.karol202.paintplus.databinding.ActivityPaintBinding
import pl.karol202.paintplus.options.OptionFileOpen
import pl.karol202.paintplus.recent.RecentImageCreator
import pl.karol202.paintplus.settings.ActivitySettings
import pl.karol202.paintplus.util.GraphicsHelper
import pl.karol202.paintplus.util.NavigationBarUtils
import pl.karol202.paintplus.util.collectIn
import pl.karol202.paintplus.util.viewBinding
import pl.karol202.paintplus.viewmodel.PaintViewModel
import pl.karol202.paintplus.viewmodel.PaintViewModel.ImageEvent
import pl.karol202.paintplus.viewmodel.PaintViewModel.TitleOverride

class ActivityPaint : AppCompatActivity(), PermissionGrantingActivity, AppContext
{
	companion object
	{
		const val URI_KEY = "path"
		const val OPEN_KEY = "open"
	}

	private val paintViewModel by viewModel<PaintViewModel>()

	private lateinit var actions: ActivityPaintActions
	private lateinit var drawers: ActivityPaintDrawers
	private lateinit var layers: ActivityPaintLayers

	private var resultListeners = mutableMapOf<Int, ActivityResultListener>()
	private var permissionListeners = mutableMapOf<Int, PermissionGrantListener>()
	var asyncManager = AsyncManager(this)
		private set
	private var recentImageCreator = RecentImageCreator(this)

	private var initUri: Uri? = null
	private var openFile = false

	val image get() = paintViewModel.image
	val tools get() = paintViewModel.tools
	val isAnyDrawerOpen get() = drawers.isAnyDrawerOpen
	val fileEditListener get() = recentImageCreator
	val mainContainer get() = views.mainContainer

	private val views by viewBinding(ActivityPaintBinding::inflate)

	override fun onCreate(savedInstanceState: Bundle?)
	{
		super.onCreate(savedInstanceState)
		readArguments(intent.extras)
		GraphicsHelper.init(this)

		setContentView(views.root)
		window.decorView.setOnSystemUiVisibilityChangeListener { initSystemUIVisibility() }
		initSystemUIVisibility()

		setSupportActionBar(views.toolbar.root)
		supportActionBar?.setDisplayHomeAsUpEnabled(true)
		supportActionBar?.setHomeButtonEnabled(true)

		views.paintView.setImage(paintViewModel.image)

		actions = ActivityPaintActions(this, paintViewModel)
		drawers = ActivityPaintDrawers(this, views, paintViewModel)
		layers = ActivityPaintLayers(this, views, paintViewModel)

		drawers.initDrawers()
		layers.initLayers()
	}

	private fun readArguments(bundle: Bundle?)
	{
		if(bundle == null) return
		initUri = bundle.getParcelable(URI_KEY)
		openFile = initUri == null && bundle.getBoolean(OPEN_KEY, false)
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
		paintViewModel.titleOverrideFlow.collectIn(lifecycleScope) {
			val toolName = getString(paintViewModel.currentToolFlow.value.name)
			views.toolbar.root.title = when(it)
			{
				TitleOverride.NONE -> toolName
				TitleOverride.TOOL_SELECTION -> getString(R.string.choice_of_tool)
				TitleOverride.TOOL_PROPERTIES -> getString(R.string.properties, toolName)
			}
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
	}

	override fun onPostCreate(savedInstanceState: Bundle?)
	{
		super.onPostCreate(savedInstanceState)
		drawers.postInitDrawers()
		loadImageIfPathIsPresent()
		selectImageToOpenIfNeeded()
		observeViewModel()
	}

	private fun loadImageIfPathIsPresent()
	{
		if(initUri != null) OptionFileOpen(this, image, asyncManager, recentImageCreator).openFile(initUri)
	}

	private fun selectImageToOpenIfNeeded()
	{
		if(openFile) OptionFileOpen(this, image, asyncManager, recentImageCreator).executeWithoutAsking()
	}

	override fun onSaveInstanceState(outState: Bundle)
	{
		intent.putExtra("path", null as String?)
		super.onSaveInstanceState(outState)
	}

	override fun onDestroy()
	{
		super.onDestroy()
		GraphicsHelper.destroy()
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
		return true
	}

	override fun onPrepareOptionsMenu(menu: Menu): Boolean
	{
		actions.prepareMenu(menu)
		return super.onPrepareOptionsMenu(menu)
	}

	override fun onOptionsItemSelected(item: MenuItem): Boolean
	{
		return actions.handleAction(item) || super.onOptionsItemSelected(item)
	}

	fun showSettingsActivity() = startActivity(Intent(this, ActivitySettings::class.java))

	override fun getContext() = this

	override fun createSnackbar(message: Int, duration: Int): Snackbar
	{
		val snackbar = Snackbar.make(views.mainContainer, message, duration)
		val params = snackbar.view.layoutParams as CoordinatorLayout.LayoutParams
		params.setMargins(0, 0, 0, -NavigationBarUtils.getNavigationBarHeight(this))
		return snackbar
	}

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

	override fun registerPermissionGrantListener(requestCode: Int, listener: PermissionGrantListener)
	{
		if(requestCode in permissionListeners) throw RuntimeException("requestCode is already used: $requestCode")
		permissionListeners[requestCode] = listener
	}

	override fun onRequestPermissionsResult(requestCode: Int, permissions: Array<String>, grantResults: IntArray)
	{
		super.onRequestPermissionsResult(requestCode, permissions, grantResults)
		if(grantResults[0] == PackageManager.PERMISSION_GRANTED) permissionListeners[requestCode]?.onPermissionGrant()
		permissionListeners.remove(requestCode)
	}

	fun togglePropertiesDrawer() = drawers.togglePropertiesDrawer()

	fun toggleLayersSheet() = layers.toggleLayersSheet()

	fun closeLayersSheet() = layers.closeLayersSheet()

	fun setScrollingBlocked(blocked: Boolean) = layers.setScrollingBlocked(blocked)
}
