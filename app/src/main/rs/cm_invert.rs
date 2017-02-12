#pragma version(1)
#pragma rs java_package_name(pl.karol202.paintplus.color.manipulators)

uchar4 RS_KERNEL invert(uchar4 in)
{
	uchar4 out = in;
	out.r = 255 - out.r;
	out.g = 255 - out.g;
	out.b = 255 - out.b;
	return out;
}