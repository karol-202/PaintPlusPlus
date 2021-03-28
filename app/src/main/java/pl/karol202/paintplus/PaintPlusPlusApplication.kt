package pl.karol202.paintplus

import android.app.Application
import android.renderscript.RenderScript
import androidx.preference.PreferenceManager
import com.google.firebase.analytics.FirebaseAnalytics
import org.koin.android.ext.koin.androidContext
import org.koin.androidx.viewmodel.dsl.viewModel
import org.koin.core.context.startKoin
import org.koin.dsl.module
import pl.karol202.paintplus.color.manipulators.InvertColorManipulator
import pl.karol202.paintplus.helpers.HelpersService
import pl.karol202.paintplus.image.*
import pl.karol202.paintplus.image.layer.mode.LayerModesService
import pl.karol202.paintplus.options.*
import pl.karol202.paintplus.recent.LocalDatabase
import pl.karol202.paintplus.recent.RecentImageRepository
import pl.karol202.paintplus.recent.RecentViewModel
import pl.karol202.paintplus.recent.RoomRecentImageRepository
import pl.karol202.paintplus.settings.SettingsRepository
import pl.karol202.paintplus.settings.SharedPrefsSettingsRepository
import pl.karol202.paintplus.tool.ToolsService
import pl.karol202.paintplus.viewmodel.PaintViewModel

class PaintPlusPlusApplication : Application()
{
	override fun onCreate()
	{
		super.onCreate()
		initFirebaseIfNotDebug()

		startKoin {
			androidContext(this@PaintPlusPlusApplication)
			modules(appModule(), databaseModule(), repositoryModule(), serviceModule(), optionModule(),
			        colorManipulatorModule(), viewModelModule())
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
		single { ImageService(get(), get()) }
		single { ClipboardService() }
		single { ColorsService() }
		single { HistoryService(get()) }
		single { HelpersService(get(), get()) }
		single { ViewService(get()) }
		single { ToolsService(get()) }
		single { LayerModesService(get()) }
		single { FileService(get(), get()) }
	}

	private fun optionModule() = module {
		single { OptionLayerColorsInvert(get(), get(), get()) }
		single { OptionCopy(get(), get()) }
		single { OptionImageCropBySelection(get(), get(), get(), get()) }
		single { OptionLayerCropBySelection(get(), get()) }
		single { OptionCut(get(), get(), get()) }
		single { OptionImageCapturePhoto(get(), get(), get(), get(), get()) }
		single { OptionImageNew(get(), get(), get(), get()) }
		single { OptionImageOpen(get(), get(), get(), get(), get()) }
		single { OptionImageSave(get(), get(), get(), get()) }
		single { OptionFlip(get()) }
		single { OptionImageFlatten(get(), get(), get()) }
		single { OptionImageFlip(get(), get(), get()) }
		single { OptionImageRotate(get(), get(), get()) }
		single { OptionSelectInversion(get(), get(), get()) }
		single { OptionLayerChangeOrder(get(), get()) }
		single { OptionLayerDelete(get(), get(), get()) }
		single { OptionLayerDuplicate(get(), get(), get()) }
		single { OptionLayerFlip(get(), get(), get()) }
		single { OptionLayerInfoShow(get()) }
		single { OptionLayerMergeDown(get(), get()) }
		single { OptionLayerNameChange(get(), get(), get()) }
		single { OptionLayerOpen(get(), get(), get(), get(), get()) }
		single { OptionLayerPropertiesEdit(get(), get(), get(), get()) }
		single { OptionLayerSave(get(), get(), get(), get()) }
		single { OptionLayerSelect(get()) }
		single { OptionLayerFitToImage(get(), get()) }
		single { OptionLayerVisibilityToggle(get(), get()) }
		single { OptionOpen(get(), get()) }
		single { OptionPaste(get(), get(), get(), get()) }
		single { OptionSave(get(), get(), get()) }
		single { OptionSelectAll(get(), get(), get()) }
		single { OptionSelectNothing(get(), get(), get()) }
		single { OptionSetZoom(get(), get()) }
	}

	private fun colorManipulatorModule() = module {
		single { InvertColorManipulator(get()) }
	}

	private fun viewModelModule() = module {
		viewModel { RecentViewModel(get(), get()) }
		viewModel { PaintViewModel(get(), get(), get(), get(), get(), get(), get(), get(), get(), get(), get(), get(), get(),
		                           get(), get(), get(), get(), get(), get(), get(), get(), get(), get(), get(), get(), get(),
		                           get(), get(), get(), get(), get(), get(), get(), get(), get(), get(), get(), get(), get(),
		                           get(), get(), get(), get(), get(), get(), get(), get(), get()) }
	}
}
