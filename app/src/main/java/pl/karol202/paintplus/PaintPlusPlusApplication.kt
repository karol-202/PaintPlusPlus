package pl.karol202.paintplus

import android.app.Application
import android.renderscript.RenderScript
import androidx.preference.PreferenceManager
import com.google.firebase.analytics.FirebaseAnalytics
import org.koin.android.ext.koin.androidContext
import org.koin.androidx.viewmodel.dsl.viewModel
import org.koin.core.context.startKoin
import org.koin.dsl.bind
import org.koin.dsl.module
import pl.karol202.paintplus.color.manipulators.BrightnessColorManipulator
import pl.karol202.paintplus.color.manipulators.CurvesColorsManipulator
import pl.karol202.paintplus.color.manipulators.InvertColorManipulator
import pl.karol202.paintplus.helpers.Grid
import pl.karol202.paintplus.helpers.Helper
import pl.karol202.paintplus.helpers.HelpersService
import pl.karol202.paintplus.image.*
import pl.karol202.paintplus.image.layer.mode.*
import pl.karol202.paintplus.options.*
import pl.karol202.paintplus.recent.LocalDatabase
import pl.karol202.paintplus.recent.RecentImageRepository
import pl.karol202.paintplus.recent.RecentViewModel
import pl.karol202.paintplus.recent.RoomRecentImageRepository
import pl.karol202.paintplus.settings.SettingsRepository
import pl.karol202.paintplus.settings.SharedPrefsSettingsRepository
import pl.karol202.paintplus.tool.Tool
import pl.karol202.paintplus.tool.ToolsService
import pl.karol202.paintplus.tool.brush.ToolBrush
import pl.karol202.paintplus.tool.drag.ToolDrag
import pl.karol202.paintplus.tool.fill.ToolFill
import pl.karol202.paintplus.tool.gradient.ToolGradient
import pl.karol202.paintplus.tool.gradient.shape.*
import pl.karol202.paintplus.tool.marker.ToolMarker
import pl.karol202.paintplus.tool.pan.ToolPan
import pl.karol202.paintplus.tool.pickcolor.ToolColorPick
import pl.karol202.paintplus.tool.rubber.ToolRubber
import pl.karol202.paintplus.tool.selection.ToolSelection
import pl.karol202.paintplus.tool.shape.Shape
import pl.karol202.paintplus.tool.shape.ToolShape
import pl.karol202.paintplus.tool.shape.circle.ShapeCircle
import pl.karol202.paintplus.tool.shape.line.ShapeLine
import pl.karol202.paintplus.tool.shape.polygon.ShapePolygon
import pl.karol202.paintplus.tool.shape.star.ShapeStar
import pl.karol202.paintplus.viewmodel.HistoryViewModel
import pl.karol202.paintplus.viewmodel.PaintViewModel

class PaintPlusPlusApplication : Application()
{
	override fun onCreate()
	{
		super.onCreate()
		initFirebaseIfNotDebug()

		startKoin {
			androidContext(this@PaintPlusPlusApplication)
			modules(appModule(), databaseModule(), repositoryModule(), serviceModule(), optionModule(), toolsModule(),
			        shapesModule(), gradientShapesModule(), colorManipulatorModule(), layerModesModule(), helpersModule(),
			        viewModelModule())
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
		single { HelpersService(getAll()) }
		single { ViewService(get()) }
		single { ToolsService(getAll()) }
		single { FileService(get(), get()) }
		single { EffectsService() }
	}

	private fun optionModule() = module {
		single { OptionCopy(get(), get()) }
		single { OptionCut(get(), get(), get()) }
		single { OptionFlip(get()) }
		single { OptionImageCapturePhoto(get(), get(), get(), get(), get()) }
		single { OptionImageCropBySelection(get(), get(), get(), get()) }
		single { OptionImageFlatten(get(), get(), get()) }
		single { OptionImageFlip(get(), get(), get()) }
		single { OptionImageNew(get(), get(), get(), get()) }
		single { OptionImageOpen(get(), get(), get(), get(), get()) }
		single { OptionImageResize(get(), get(), get()) }
		single { OptionImageRotate(get(), get(), get()) }
		single { OptionImageSave(get(), get(), get(), get()) }
		single { OptionImageSaveAs(get()) }
		single { OptionImageSaveLast(get(), get()) }
		single { OptionImageScale(get(), get(), get()) }
		single { OptionLayerChangeOrder(get(), get()) }
		single { OptionLayerColorBrightness(get(), get(), get(), get()) }
		single { OptionLayerColorCurves(get(), get(), get(), get()) }
		single { OptionLayerColorInvert(get(), get(), get()) }
		single { OptionLayerCropBySelection(get(), get()) }
		single { OptionLayerDelete(get(), get(), get()) }
		single { OptionLayerDrag(get(), get(), get()) }
		single { OptionLayerDuplicate(get(), get(), get()) }
		single { OptionLayerFitToImage(get(), get()) }
		single { OptionLayerFlip(get(), get(), get()) }
		single { OptionLayerInfoShow(get()) }
		single { OptionLayerMergeDown(get(), get()) }
		single { OptionLayerNameChange(get(), get(), get()) }
		single { OptionLayerNew(get(), get(), get()) }
		single { OptionLayerOpen(get(), get(), get(), get(), get()) }
		single { OptionLayerPropertiesEdit(get(), get(), get(), getAll()) }
		single { OptionLayerResize(get(), get(), get()) }
		single { OptionLayerRotate(get(), get(), get()) }
		single { OptionLayerSave(get(), get(), get(), get()) }
		single { OptionLayerScale(get(), get(), get()) }
		single { OptionLayerSelect(get()) }
		single { OptionLayerVisibilityToggle(get(), get()) }
		single { OptionOpen(get(), get()) }
		single { OptionPaste(get(), get(), get(), get()) }
		single { OptionResize(get()) }
		single { OptionSave(get(), get(), get()) }
		single { OptionScale(get()) }
		single { OptionSelect(get()) }
		single { OptionSelectAll(get(), get(), get()) }
		single { OptionSelectInversion(get(), get(), get()) }
		single { OptionSelectNothing(get(), get(), get()) }
		single { OptionSetZoom(get(), get()) }
	}

	private fun toolsModule() = module {
		single { ToolPan(get(), get()) } bind Tool::class
		single { ToolMarker(get(), get(), get(), get(), get(), get()) } bind Tool::class
		single { ToolBrush(get(), get(), get(), get(), get(), get()) } bind Tool::class
		single { ToolFill(get(), get(), get(), get(), get(), get()) } bind Tool::class
		single { ToolShape(get(), get(), get(), get(), get(), getAll()) } bind Tool::class
		single { ToolSelection(get(), get(), get(), get(), get(), get(), get()) } bind Tool::class
		single { ToolColorPick(get(), get(), get(), get(), get()) } bind Tool::class
		single { ToolDrag(get(), get(), get(), get(), get()) } bind Tool::class
		single { ToolRubber(get(), get(), get(), get(), get()) } bind Tool::class
		single { ToolGradient(get(), get(), get(), get(), get(), get(), getAll()) } bind Tool::class
	}

	private fun shapesModule() = module {
		single { ShapeLine(get(), get(), get(), get(), get()) } bind Shape::class
		single { ShapeCircle(get(), get(), get(), get(), get()) } bind Shape::class
		single { ShapePolygon(get(), get(), get(), get(), get()) } bind Shape::class
		single { ShapeStar(get(), get(), get(), get(), get()) } bind Shape::class
	}

	private fun gradientShapesModule() = module {
		single { GradientShapeLinear() } bind GradientShape::class
		single { GradientShapeBilinear() } bind GradientShape::class
		single { GradientShapeRadial() } bind GradientShape::class
		single { GradientShapeSweep() } bind GradientShape::class
		single { GradientShapeSweepSymmetric() } bind GradientShape::class
	}

	private fun colorManipulatorModule() = module {
		single { InvertColorManipulator(get()) }
		single { BrightnessColorManipulator(get()) }
		single { CurvesColorsManipulator(get()) }
	}

	private fun layerModesModule() = module {
		single { DefaultLayerMode } bind LayerMode::class
		single { ScreenLayerMode } bind LayerMode::class
		single { OverlayLayerMode } bind LayerMode::class
		single { AddLayerMode(get()) } bind LayerMode::class
		single { SubtractionLayerMode(get()) } bind LayerMode::class
		single { DifferenceLayerMode(get()) } bind LayerMode::class
		single { MultiplyLayerMode(get()) } bind LayerMode::class
		single { LighterLayerMode(get()) } bind LayerMode::class
		single { DarkerLayerMode(get()) } bind LayerMode::class
	}

	private fun helpersModule() = module {
		single { Grid(get(), get(), get()) } bind Helper::class
	}

	private fun viewModelModule() = module {
		viewModel { RecentViewModel(get(), get()) }
		viewModel { PaintViewModel(get(), get(), get(), get(), get(), get(), get(), get(), get(), get(), get(), get(), get(),
		                           get(), get(), get(), get(), get(), get(), get(), get(), get(), get(), get(), get(), get(),
		                           get(), get(), get(), get(), get(), get(), get(), get(), get(), get(), get(), get(), get(),
		                           get(), get(), get(), get(), get(), get(), get(), get(), get(), get(), get()) }
		viewModel { HistoryViewModel(get(), get()) }
	}
}
