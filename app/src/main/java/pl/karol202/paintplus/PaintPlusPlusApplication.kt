package pl.karol202.paintplus

import android.app.Application
import androidx.preference.PreferenceManager
import org.koin.android.ext.koin.androidApplication
import org.koin.android.ext.koin.androidContext
import org.koin.androidx.viewmodel.dsl.viewModel
import org.koin.core.context.startKoin
import org.koin.dsl.module
import pl.karol202.paintplus.recent.LocalDatabase
import pl.karol202.paintplus.recent.RecentImageRepository
import pl.karol202.paintplus.recent.RecentViewModel
import pl.karol202.paintplus.recent.RoomRecentImageRepository
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
		single { PreferenceManager.getDefaultSharedPreferences(get()) }

		single { LocalDatabase.create(get()) }
		single { get<LocalDatabase>().recentImageDao() }

		single<RecentImageRepository> { RoomRecentImageRepository(get()) }
		single<SettingsRepository> { SharedPrefsSettingsRepository(get()) }

		viewModel { RecentViewModel(androidApplication(), get()) }
		viewModel { PaintViewModel(androidApplication(), get()) }
	}
}
