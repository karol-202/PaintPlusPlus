package pl.karol202.paintplus.settings

import kotlinx.coroutines.flow.Flow

interface SettingsRepository
{
	val settings: Flow<Settings>
}
