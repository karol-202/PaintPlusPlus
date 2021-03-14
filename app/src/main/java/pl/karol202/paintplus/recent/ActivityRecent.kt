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

import android.content.Intent
import android.net.Uri
import android.os.Bundle
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
import pl.karol202.paintplus.databinding.ActivityRecentBinding
import pl.karol202.paintplus.util.BlockableLinearLayoutManager
import pl.karol202.paintplus.util.collectIn
import pl.karol202.paintplus.util.viewBinding

class ActivityRecent : AppCompatActivity()
{
	private class SwipeCallback(private val onSwipe: (RecentImage) -> Unit) :
			ItemTouchHelper.SimpleCallback(0, ItemTouchHelper.LEFT or ItemTouchHelper.RIGHT)
	{
		override fun onMove(recyclerView: RecyclerView,
		                    viewHolder: RecyclerView.ViewHolder,
		                    target: RecyclerView.ViewHolder) = false

		override fun onSwiped(viewHolder: RecyclerView.ViewHolder, direction: Int)
		{
			(viewHolder as RecentAdapter.ViewHolder).image?.let(onSwipe)
		}
	}

	private val recentViewModel by viewModel<RecentViewModel>()
	private val views by viewBinding(ActivityRecentBinding::inflate)

	private val adapter = RecentAdapter(this) { startEditingWithUri(it.uri) }

	override fun onCreate(savedInstanceState: Bundle?)
	{
		super.onCreate(savedInstanceState)
		setContentView(views.root)

		setSupportActionBar(views.toolbar.root)

		views.recyclerRecent.layoutManager = BlockableLinearLayoutManager(this)
		views.recyclerRecent.adapter = adapter
		ItemTouchHelper(SwipeCallback(this::removeRecentImage)).attachToRecyclerView(views.recyclerRecent)

		views.buttonNewImage.setOnClickListener { startEditing() }

		recentViewModel.recentImages.collectIn(lifecycleScope) {
			adapter.images = it
			views.viewNoImages.visibility = if(it.isEmpty()) View.VISIBLE else View.GONE
		}
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

	private fun startEditingWithUri(uri: Uri) = startEditing { putExtra(ActivityPaint.ARG_OPEN_URI, uri) }

	private fun startEditingWithPicker() = startEditing { putExtra(ActivityPaint.ARG_OPEN_PICKER, true) }

	private fun startEditing(intentBuilder: Intent.() -> Unit = {}) =
			startActivity(Intent(this, ActivityPaint::class.java).apply(intentBuilder))

	private fun removeRecentImage(image: RecentImage) = recentViewModel.removeRecentImage(image.uri)
}
