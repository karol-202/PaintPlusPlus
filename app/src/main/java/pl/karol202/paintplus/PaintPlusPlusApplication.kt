package pl.karol202.paintplus

import android.app.Application
import android.renderscript.RenderScript
import androidx.preference.PreferenceManager
import com.google.firebase.analytics.FirebaseAnalytics
import org.koin.android.ext.koin.androidApplication
import org.koin.android.ext.koin.androidContext
import org.koin.androidx.viewmodel.dsl.viewModel
import org.koin.core.context.startKoin
import org.koin.dsl.module
import pl.karol202.paintplus.image.*
import pl.karol202.paintplus.image.layer.mode.LayerModesService
import pl.karol202.paintplus.options.*
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
		initFirebaseIfNotDebug()

		startKoin {
			androidContext(this@PaintPlusPlusApplication)
			modules(appModule(), databaseModule(), repositoryModule(), serviceModule(), optionModule(), viewModelModule())
		}
	}

	private fun initFirebaseIfNotDebug()
	{
		if(!BuildConfig.DEBUG) FirebaseAnalytics.getInstance(this)
	}

	private fun appModule() = module {
		single { RenderScript.create(get()) }
		single { PreferenceManager.getDefaultSharedPreferences(get()) }
	}

	private fun databaseModule() = module {
		single { LocalDatabase.create(get()) }
		single { get<LocalDatabase>().recentImageDao() }
	}

	private fun repositoryModule() = module {
		single<RecentImageRepository> { RoomRecentImageRepository(get()) }
		single<SettingsRepository> { SharedPrefsSettingsRepository(get()) }
	}

	private fun serviceModule() = module {
		single { ImageService(get()) }
		single { ClipboardService() }
		single { ColorsService() }
		single { HistoryService() }
		single { SelectionService() }
		single { ViewService(get()) }
		single { LayerModesService(get()) }
	}

	private fun optionModule() = module {
		single { OptionColorsInvert(get()) }
		single { OptionCropImageBySelection(get()) }
		single { OptionCropLayerBySelection(get()) }
		single { OptionFileCapturePhoto(get()) }
		single { OptionFileNew(get()) }
		single { OptionFileOpen(get(), get(), get()) }
		single { OptionFileSave(get(), get(), get()) }
		single { OptionFlip(get()) }
		single { OptionImageFlatten(get()) }
		single { OptionImageFlip(get()) }
		single { OptionImageRotate(get()) }
		single { OptionLayerFlip(get()) }
		single { OptionLayerOpen(get(), get()) }
		single { OptionLayerSave(get(), get(), get()) }
		single { OptionLayerToImageSize(get()) }
		single { OptionOpen(get()) }
		single { OptionSave(get()) }
		single { OptionSetZoom(get()) }
	}

	private fun viewModelModule() = module {
		viewModel { RecentViewModel(androidApplication(), get()) }
		viewModel { PaintViewModel(androidApplication(), get(), get(), get(), get(), get()) }
	}
}
