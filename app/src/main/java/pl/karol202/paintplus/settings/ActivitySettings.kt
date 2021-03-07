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
package pl.karol202.paintplus.settings

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.view.MenuItem
import androidx.appcompat.app.ActionBar
import androidx.appcompat.widget.Toolbar
import androidx.fragment.app.FragmentManager
import androidx.fragment.app.commit
import pl.karol202.paintplus.R
import pl.karol202.paintplus.databinding.ActivitySettingsBinding
import pl.karol202.paintplus.settings.SettingsFragment
import pl.karol202.paintplus.util.viewBinding
import java.lang.RuntimeException

class ActivitySettings : AppCompatActivity()
{
	private val views by viewBinding(ActivitySettingsBinding::inflate)

	override fun onCreate(savedInstanceState: Bundle?)
	{
		super.onCreate(savedInstanceState)
		setContentView(views.root)

		supportFragmentManager.commit { replace(R.id.settings, SettingsFragment()) }

		setSupportActionBar(views.toolbar.root)
		supportActionBar?.setDisplayHomeAsUpEnabled(true)
		supportActionBar?.setHomeButtonEnabled(true)
	}

	override fun onOptionsItemSelected(item: MenuItem): Boolean
	{
		if(item.itemId == android.R.id.home) finish()
		return super.onOptionsItemSelected(item)
	}
}
