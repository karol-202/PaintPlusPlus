package pl.karol202.paintplus.helpers

import pl.karol202.paintplus.image.EffectsService
import kotlin.properties.Delegates

abstract class AbstractHelper(private val effectsService: EffectsService) : Helper
{
	protected fun <V> notifying(initial: V) = Delegates.observable(initial) { _, _, _ -> effectsService.notifyViewUpdate() }
}
