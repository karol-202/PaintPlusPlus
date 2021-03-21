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

import android.view.LayoutInflater
import androidx.appcompat.app.AlertDialog
import androidx.viewbinding.ViewBinding
import pl.karol202.paintplus.util.layoutInflater

interface Option
{
	abstract class BasicDialog(builder: AlertDialog.Builder)
	{
		protected val context = builder.context
	}

	abstract class LayoutDialog<B : ViewBinding>(builder: AlertDialog.Builder,
	                                             binding: (LayoutInflater) -> B) : BasicDialog(builder)
	{
		protected val views = binding(context.layoutInflater)

		init
		{
			builder.setView(views.root)
		}
	}
}