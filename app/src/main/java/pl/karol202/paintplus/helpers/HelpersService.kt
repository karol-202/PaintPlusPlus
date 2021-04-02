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
package pl.karol202.paintplus.helpers

import android.graphics.PointF
import kotlinx.coroutines.flow.merge

class HelpersService(val helpers: List<Helper>)
{
	val updateEventFlow = helpers.mapNotNull { it.updateEventFlow }.merge()

	fun snapX(x: Float) = snap(x, SnappingHelper::snapX)

	fun snapY(y: Float) = snap(y, SnappingHelper::snapY)

	fun snapPoint(point: PointF) = snap(point, SnappingHelper::snapPoint)

	private fun <T> snap(value: T, snapper: SnappingHelper.(T) -> T) =
			helpers.filterIsInstance<SnappingHelper>().fold(value) { acc, helper -> helper.snapper(acc) }
}
