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
package pl.karol202.paintplus.tool

import android.graphics.Canvas
import android.view.MotionEvent
import androidx.fragment.app.Fragment
import kotlinx.coroutines.flow.Flow
import pl.karol202.paintplus.image.layer.Layer

interface Tool
{
	val name: Int
	val icon: Int
	val propertiesFragmentClass: Class<out Fragment>

	val updateEventFlow: Flow<Unit>? get() = null

	fun onTouch(event: MotionEvent): Boolean

	fun drawOnLayer(canvas: Canvas, layer: Layer) {}

	fun drawOnTop(canvas: Canvas) {}
}
