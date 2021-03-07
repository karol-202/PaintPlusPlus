package pl.karol202.paintplus

import android.app.Application
import androidx.preference.PreferenceManager
import org.koin.android.ext.koin.androidApplication
import org.koin.android.ext.koin.androidContext
import org.koin.androidx.viewmodel.dsl.viewModel
import org.koin.core.context.startKoin
import org.koin.dsl.module
import pl.karol202.paintplus.settings.SettingsRepository
import pl.karol202.paintplus.settings.SharedPrefsSettingsRepository
import pl.karol202.paintplus.viewmodel.PaintViewModel

class PaintPlusPlusApplication : Application()
{
	override fun onCreate()
	{
		super.onCreate()
		startKoin {
			androidContext(this@PaintPlusPlusApplication)
			modules(appModule())
		}
	}

	private fun appModule() = module {
		single { PreferenceManager.getDefaultSharedPreferences(androidContext()) }

		single<SettingsRepository> { SharedPrefsSettingsRepository(get()) }

		viewModel { PaintViewModel(androidApplication(), get()) }
	}
}
