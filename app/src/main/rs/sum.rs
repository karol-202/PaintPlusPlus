#pragma version(1)
#pragma rs java_package_name(pl.karol202.paintplus.image.layer.mode)

rs_allocation dstAlloc;
float opacity;

uchar4 RS_KERNEL sum(uchar4 in, uint32_t x, uint32_t y)
{
	uchar4 dst = rsGetElementAt_uchar4(dstAlloc, x, y);
	uchar4 out = in;
	in.r *= opacity;
	in.g *= opacity;
	in.b *= opacity;
	in.a *= opacity;
	
	out.r = min(in.r + dst.r, 255);
	out.g = min(in.g + dst.g, 255);
	out.b = min(in.b + dst.b, 255);
	out.a = min(in.a + dst.a, 255);
	return out;
}