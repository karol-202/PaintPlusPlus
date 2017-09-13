#pragma version(1)
#pragma rs java_package_name(pl.karol202.paintplus.image.layer.mode)

rs_allocation dstAlloc;
float opacity;

uchar4 RS_KERNEL sum(uchar4 in, uint32_t x, uint32_t y)
{
	uchar4 dst = rsGetElementAt_uchar4(dstAlloc, x, y);
	uchar4 out = in;
	if(opacity != 1)
	{
		in.r *= opacity;
		in.g *= opacity;
		in.b *= opacity;
		in.a *= opacity;
	}
	
	out.r = abs(dst.r - in.r);
	out.g = abs(dst.g - in.g);
	out.b = abs(dst.b - in.b);
	out.a = dst.a;
	return out;
}