package pl.karol202.paintplus.image

enum class RotationAmount(val angle: Float)
{
	ANGLE_90(90f),
	ANGLE_180(180f),
	ANGLE_270(270f);

	fun getOpposite() = when(this)
	{
		ANGLE_90 -> ANGLE_270
		ANGLE_180 -> ANGLE_180
		ANGLE_270 -> ANGLE_90
	}
}
