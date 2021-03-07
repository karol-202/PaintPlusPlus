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
package pl.karol202.paintplus.recent

import android.Manifest
import android.content.Intent
import android.content.pm.PackageManager
import android.net.Uri
import android.os.Bundle
import android.os.Environment
import android.view.Menu
import android.view.MenuItem
import android.view.View
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.lifecycleScope
import androidx.recyclerview.widget.ItemTouchHelper
import androidx.recyclerview.widget.RecyclerView
import com.google.firebase.analytics.FirebaseAnalytics
import org.koin.androidx.viewmodel.ext.android.viewModel
import pl.karol202.paintplus.BuildConfig
import pl.karol202.paintplus.R
import pl.karol202.paintplus.activity.ActivityPaint
import pl.karol202.paintplus.activity.PermissionRequest
import pl.karol202.paintplus.activity.PermissionRequest.PermissionGrantListener
import pl.karol202.paintplus.activity.PermissionRequest.PermissionGrantingActivity
import pl.karol202.paintplus.databinding.ActivityRecentBinding
import pl.karol202.paintplus.file.ImageLoader
import pl.karol202.paintplus.util.BlockableLinearLayoutManager
import pl.karol202.paintplus.util.collectIn
import pl.karol202.paintplus.util.viewBinding

class ActivityRecent : AppCompatActivity(), PermissionGrantingActivity
{
	private inner class SwipeCallback : ItemTouchHelper.SimpleCallback(0, ItemTouchHelper.LEFT or ItemTouchHelper.RIGHT)
	{
		override fun onMove(recyclerView: RecyclerView,
		                    viewHolder: RecyclerView.ViewHolder,
		                    target: RecyclerView.ViewHolder) = false

		override fun onSwiped(viewHolder: RecyclerView.ViewHolder, direction: Int)
		{
			val holder = viewHolder as RecentAdapter.ViewHolder
			holder.image?.uri?.let(recentViewModel::removeRecentImage)
		}
	}

	private val recentViewModel by viewModel<RecentViewModel>()
	private val views by viewBinding(ActivityRecentBinding::inflate)

	private val adapter = RecentAdapter(this) { startEditingWithUri(it.uri) }
	private val permissionListeners = mutableMapOf<Int, PermissionGrantListener>()

	override fun onCreate(savedInstanceState: Bundle?)
	{
		super.onCreate(savedInstanceState)
		initFirebaseIfNotDebug()
		setContentView(views.root)

		// TODO Get rid of it
		ImageLoader.setTemporaryFileLocation(getExternalFilesDir(Environment.DIRECTORY_PICTURES))

		setSupportActionBar(views.toolbar.root)

		views.recyclerRecent.layoutManager = BlockableLinearLayoutManager(this)
		views.recyclerRecent.adapter = adapter
		ItemTouchHelper(SwipeCallback()).attachToRecyclerView(views.recyclerRecent)

		views.buttonNewImage.setOnClickListener { startEditingWithUri(null) }

		recentViewModel.recentImages.collectIn(lifecycleScope) {
			adapter.images = it
			views.viewNoImages.visibility = if(it.isEmpty()) View.VISIBLE else View.GONE
		}
	}

	private fun initFirebaseIfNotDebug()
	{
		if(!BuildConfig.DEBUG) FirebaseAnalytics.getInstance(this)
	}

	override fun onCreateOptionsMenu(menu: Menu): Boolean
	{
		menuInflater.inflate(R.menu.menu_recent, menu)
		return super.onCreateOptionsMenu(menu)
	}

	override fun onOptionsItemSelected(item: MenuItem): Boolean
	{
		when(item.itemId)
		{
			R.id.action_open_image -> startEditingWithPicker()
			else -> super.onOptionsItemSelected(item)
		}
		return true
	}

	private fun startEditingWithUri(uri: Uri?) =
			startActivity(Intent(this, ActivityPaint::class.java).apply {
				if(uri != null) putExtra(ActivityPaint.URI_KEY, uri)
			})

	private fun startEditingWithPicker() =
			PermissionRequest(this, Manifest.permission.WRITE_EXTERNAL_STORAGE, {
				startActivity(Intent(this@ActivityRecent, ActivityPaint::class.java).apply {
					putExtra(ActivityPaint.OPEN_KEY, true)
				})
			}).execute()

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
}
