package pl.karol202.paintplus.viewmodel

import androidx.appcompat.app.AlertDialog

fun interface DialogDefinition
{
	fun init(builder: AlertDialog.Builder)
}
