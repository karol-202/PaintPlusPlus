#pragma version(1)
#pragma rs java_package_name(pl.karol202.paintplus.image.layer.mode)

//rs_allocation dstAlloc;

uchar4 RS_KERNEL sum(uchar4 in, uint32_t x, uint32_t y)
{
	//uchar4 dst = rsGetElementAt_uchar4(dstAlloc, x, y);
	uchar4 out = in;
	out.r = in.r;// + dst.r;
	out.g = in.g;// + dst.g;
	out.b = in.b;// + dst.b;
	out.a = in.a;// + dst.a;
	return out;
}