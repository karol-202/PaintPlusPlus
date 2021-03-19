package pl.karol202.paintplus.util

import kotlin.properties.ReadOnlyProperty
import kotlin.reflect.KProperty

class MemoizeDelegate1<T1, R>(private val func: (T1) -> R) : ReadOnlyProperty<Any?, (T1) -> R>
{
	private val cache = mutableMapOf<T1, R>()

	override fun getValue(thisRef: Any?, property: KProperty<*>): (T1) -> R = { arg1 ->
		cache.getOrPut(arg1) { func(arg1) }
	}
}

class MemoizeDelegate2<T1, T2, R>(private val func: (T1, T2) -> R) : ReadOnlyProperty<Any?, (T1, T2) -> R>
{
	private val cache = mutableMapOf<Pair<T1, T2>, R>()

	override fun getValue(thisRef: Any?, property: KProperty<*>): (T1, T2) -> R = { arg1, arg2 ->
		cache.getOrPut(arg1 to arg2) { func(arg1, arg2) }
	}
}

class MemoizeDelegate3<T1, T2, T3, R>(private val func: (T1, T2, T3) -> R) : ReadOnlyProperty<Any?, (T1, T2, T3) -> R>
{
	private val cache = mutableMapOf<Triple<T1, T2, T3>, R>()

	override fun getValue(thisRef: Any?, property: KProperty<*>): (T1, T2, T3) -> R = { arg1, arg2, arg3 ->
		cache.getOrPut(Triple(arg1, arg2, arg3)) { func(arg1, arg2, arg3) }
	}
}

class MemoizeDelegate4<T1, T2, T3, T4, R>(private val func: (T1, T2, T3, T4) -> R) :
		ReadOnlyProperty<Any?, (T1, T2, T3, T4) -> R>
{
	private val cache = mutableMapOf<List<Any?>, R>()

	override fun getValue(thisRef: Any?, property: KProperty<*>): (T1, T2, T3, T4) -> R = { arg1, arg2, arg3, arg4 ->
		cache.getOrPut(listOf(arg1, arg2, arg3, arg4)) { func(arg1, arg2, arg3, arg4) }
	}
}

class MemoizeDelegate5<T1, T2, T3, T4, T5, R>(private val func: (T1, T2, T3, T4, T5) -> R) :
		ReadOnlyProperty<Any?, (T1, T2, T3, T4, T5) -> R>
{
	private val cache = mutableMapOf<List<Any?>, R>()

	override fun getValue(thisRef: Any?, property: KProperty<*>): (T1, T2, T3, T4, T5) -> R = { arg1, arg2, arg3, arg4, arg5 ->
		cache.getOrPut(listOf(arg1, arg2, arg3, arg4, arg5)) { func(arg1, arg2, arg3, arg4, arg5) }
	}
}

fun <T1, R> memoize(func: (T1) -> R) = MemoizeDelegate1(func)
fun <T1, T2, R> memoize(func: (T1, T2) -> R) = MemoizeDelegate2(func)
fun <T1, T2, T3, R> memoize(func: (T1, T2, T3) -> R) = MemoizeDelegate3(func)
fun <T1, T2, T3, T4, R> memoize(func: (T1, T2, T3, T4) -> R) = MemoizeDelegate4(func)
fun <T1, T2, T3, T4, T5, R> memoize(func: (T1, T2, T3, T4, T5) -> R) = MemoizeDelegate5(func)

fun <T1, R> cache(arg1: () -> T1,
                  func: (T1) -> R) =
		memoize(func).map { it(arg1()) }
fun <T1, T2, R> cache(arg1: () -> T1, arg2: () -> T2,
                      func: (T1, T2) -> R) =
		memoize(func).map { it(arg1(), arg2()) }
fun <T1, T2, T3, R> cache(arg1: () -> T1, arg2: () -> T2, arg3: () -> T3,
                          func: (T1, T2, T3) -> R) =
		memoize(func).map { it(arg1(), arg2(), arg3()) }
fun <T1, T2, T3, T4, R> cache(arg1: () -> T1, arg2: () -> T2, arg3: () -> T3, arg4: () -> T4,
                              func: (T1, T2, T3, T4) -> R) =
		memoize(func).map { it(arg1(), arg2(), arg3(), arg4()) }
fun <T1, T2, T3, T4, T5, R> cache(arg1: () -> T1, arg2: () -> T2, arg3: () -> T3, arg4: () -> T4, arg5: () -> T5,
                                  func: (T1, T2, T3, T4, T5) -> R) =
		memoize(func).map { it(arg1(), arg2(), arg3(), arg4(), arg5()) }

/*class CacheDelegate1<T1, R>(private val arg1: () -> T1,
                            private val func: (T1) -> R) : ReadOnlyProperty<Any?, R>
{
	private val cache = mutableMapOf<T1, R>()

	override fun getValue(thisRef: Any?, property: KProperty<*>): R
	{
		val arg1 = arg1()
		return cache.getOrPut(arg1) { func(arg1) }
	}
}

class CacheDelegate2<T1, T2, R>(private val arg1: () -> T1,
                                private val arg2: () -> T2,
                                private val func: (T1, T2) -> R) : ReadOnlyProperty<Any?, R>
{
	private val cache = mutableMapOf<Pair<T1, T2>, R>()

	override fun getValue(thisRef: Any?, property: KProperty<*>): R
	{
		val arg1 = arg1()
		val arg2 = arg2()
		return cache.getOrPut(arg1 to arg2) { func(arg1, arg2) }
	}
}

class CacheDelegate3<T1, T2, T3, R>(private val arg1: () -> T1,
                                    private val arg2: () -> T2,
                                    private val arg3: () -> T3,
                                    private val func: (T1, T2, T3) -> R) : ReadOnlyProperty<Any?, R>
{
	private val cache = mutableMapOf<Triple<T1, T2, T3>, R>()

	override fun getValue(thisRef: Any?, property: KProperty<*>): R
	{
		val arg1 = arg1()
		val arg2 = arg2()
		val arg3 = arg3()
		return cache.getOrPut(Triple(arg1, arg2, arg3)) { func(arg1, arg2, arg3) }
	}
}

class CacheDelegate4<T1, T2, T3, T4, R>(private val arg1: () -> T1,
                                        private val arg2: () -> T2,
                                        private val arg3: () -> T3,
                                        private val arg4: () -> T4,
                                        private val func: (T1, T2, T3, T4) -> R) : ReadOnlyProperty<Any?, R>
{
	private val cache = mutableMapOf<List<Any?>, R>()

	override fun getValue(thisRef: Any?, property: KProperty<*>): R
	{
		val arg1 = arg1()
		val arg2 = arg2()
		val arg3 = arg3()
		val arg4 = arg4()
		return cache.getOrPut(listOf(arg1, arg2, arg3, arg4)) { func(arg1, arg2, arg3, arg4) }
	}
}

class CacheDelegate5<T1, T2, T3, T4, T5, R>(private val arg1: () -> T1,
                                            private val arg2: () -> T2,
                                            private val arg3: () -> T3,
                                            private val arg4: () -> T4,
                                            private val arg5: () -> T5,
                                            private val func: (T1, T2, T3, T4, T5) -> R) : ReadOnlyProperty<Any?, R>
{
	private val cache = mutableMapOf<List<Any?>, R>()

	override fun getValue(thisRef: Any?, property: KProperty<*>): R
	{
		val arg1 = arg1()
		val arg2 = arg2()
		val arg3 = arg3()
		val arg4 = arg4()
		val arg5 = arg5()
		return cache.getOrPut(listOf(arg1, arg2, arg3, arg4, arg5)) { func(arg1, arg2, arg3, arg4, arg5) }
	}
}

fun <T1, R> cache(arg1: () -> T1,
                  func: (T1) -> R) = CacheDelegate1(arg1, func)
fun <T1, T2, R> cache(arg1: () -> T1, arg2: () -> T2,
                      func: (T1, T2) -> R) = CacheDelegate2(arg1, arg2, func)
fun <T1, T2, T3, R> cache(arg1: () -> T1, arg2: () -> T2, arg3: () -> T3,
                          func: (T1, T2, T3) -> R) = CacheDelegate3(arg1, arg2, arg3, func)
fun <T1, T2, T3, T4, R> cache(arg1: () -> T1, arg2: () -> T2, arg3: () -> T3, arg4: () -> T4,
                              func: (T1, T2, T3, T4) -> R) = CacheDelegate4(arg1, arg2, arg3, arg4, func)
fun <T1, T2, T3, T4, T5, R> cache(arg1: () -> T1, arg2: () -> T2, arg3: () -> T3, arg4: () -> T4, arg5: () -> T5,
                                  func: (T1, T2, T3, T4, T5) -> R) = CacheDelegate5(arg1, arg2, arg3, arg4, arg5, func)
*/
