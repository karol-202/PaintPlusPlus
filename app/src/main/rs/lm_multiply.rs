#pragma version(1)
#pragma rs java_package_name(pl.karol202.paintplus.image.layer.mode)

rs_allocation dstAlloc;
float opacity;

uchar4 RS_KERNEL multiply(uchar4 in_char, uint32_t x, uint32_t y)
{
	uchar4 dst_char = rsGetElementAt_uchar4(dstAlloc, x, y);
	float4 dst_float = rsUnpackColor8888(dst_char);
	
	if(opacity != -1)
	{
		in_char.r = 255 - ((255 - in_char.r) * opacity);
		in_char.g = 255 - ((255 - in_char.g) * opacity);
		in_char.b = 255 - ((255 - in_char.b) * opacity);
		in_char.a = 255 - ((255 - in_char.a) * opacity);
	}
	float4 in_float = rsUnpackColor8888(in_char);
	
	float4 out_float = (float4) {0, 0, 0, 0};
	out_float.r = in_float.r * dst_float.r;
	out_float.g = in_float.g * dst_float.g;
	out_float.b = in_float.b * dst_float.b;
	out_float.a = in_float.a * dst_float.a;
	return rsPackColorTo8888(out_float);
}