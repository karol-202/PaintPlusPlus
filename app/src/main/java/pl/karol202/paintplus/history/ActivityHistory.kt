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
package pl.karol202.paintplus.history

import android.os.Bundle
import android.view.Menu
import android.view.MenuItem
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.lifecycleScope
import androidx.recyclerview.widget.DividerItemDecoration
import androidx.recyclerview.widget.LinearLayoutManager
import org.koin.androidx.viewmodel.ext.android.viewModel
import pl.karol202.paintplus.R
import pl.karol202.paintplus.databinding.ActivityHistoryBinding
import pl.karol202.paintplus.image.HistoryState
import pl.karol202.paintplus.util.collectIn
import pl.karol202.paintplus.util.viewBinding
import pl.karol202.paintplus.viewmodel.HistoryViewModel

class ActivityHistory : AppCompatActivity()
{
	private val historyViewModel by viewModel<HistoryViewModel>()
	private val views by viewBinding(ActivityHistoryBinding::inflate)

	private val adapter = HistoryAdapter()

	override fun onCreate(savedInstanceState: Bundle?)
	{
		super.onCreate(savedInstanceState)
		setContentView(views.root)

		setSupportActionBar(views.toolbar.root)
		supportActionBar?.setHomeButtonEnabled(true)
		supportActionBar?.setDisplayHomeAsUpEnabled(true)

		views.recyclerHistory.layoutManager = LinearLayoutManager(this)
		views.recyclerHistory.adapter = adapter
		views.recyclerHistory.addItemDecoration(DividerItemDecoration(this, DividerItemDecoration.HORIZONTAL))

		historyViewModel.historyStateFlow.collectIn(lifecycleScope, this::onHistoryUpdate)
	}

	private fun onHistoryUpdate(state: HistoryState)
	{
		adapter.historyState = state
		invalidateOptionsMenu()
	}

	override fun onCreateOptionsMenu(menu: Menu): Boolean
	{
		menuInflater.inflate(R.menu.menu_history, menu)
		return super.onCreateOptionsMenu(menu)
	}

	override fun onPrepareOptionsMenu(menu: Menu): Boolean
	{
		menu.findItem(R.id.action_undo).isEnabled = historyViewModel.canUndo
		menu.findItem(R.id.action_redo).isEnabled = historyViewModel.canRedo
		return super.onPrepareOptionsMenu(menu)
	}

	override fun onOptionsItemSelected(item: MenuItem): Boolean
	{
		when(item.itemId)
		{
			android.R.id.home -> onBackPressed()
			R.id.action_undo -> historyViewModel.undo()
			R.id.action_redo -> historyViewModel.redo()
		}
		return super.onOptionsItemSelected(item)
	}
}
