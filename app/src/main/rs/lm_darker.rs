#pragma version(1)
#pragma rs java_package_name(pl.karol202.paintplus.image.layer.mode)

rs_allocation dstAlloc;
float opacity;

static float4 demultiplicate(float4 vector);

uchar4 RS_KERNEL evaluate(uchar4 src_char, uint32_t x, uint32_t y)
{
	uchar4 dst_char = rsGetElementAt_uchar4(dstAlloc, x, y);
	
	float4 src_float = rsUnpackColor8888(src_char);
	src_float = demultiplicate(src_float);
	src_float.a *= opacity;
	float4 dst_float = rsUnpackColor8888(dst_char);
	dst_float = demultiplicate(dst_float);
	
	float srcFactor = src_float.a * (1 - dst_float.a);
	float dstFactor = dst_float.a * (1 - src_float.a);
	float bothFactor = src_float.a * dst_float.a;
	
	float4 srcPart = (float4) { src_float.r, src_float.g, src_float.b, 1 } * srcFactor;
	float4 dstPart = (float4) { dst_float.r, dst_float.g, dst_float.b, 1 } * dstFactor;
	
	float srcV = max(max(src_float.r, src_float.g), src_float.b);
	float dstV = max(max(dst_float.r, dst_float.g), dst_float.b);
	float4 darker = srcV <= dstV ? src_float : dst_float;
	float4 bothPart = (float4) { darker.r, darker.g, darker.b, 1 } * bothFactor;

	float4 out_float = min(srcPart + dstPart + bothPart, (float4) { 1, 1, 1, 1 });
	return rsPackColorTo8888(out_float);
}

static float4 demultiplicate(float4 old)
{
	if(old.a == 1) return old;
	float4 new = (float4) { 0, 0, 0, old.a };
	if(old.a == 0) return new;
	new.r = min(old.r / old.a, 1.0);
    new.g = min(old.g / old.a, 1.0);
    new.b = min(old.b / old.a, 1.0);
	return new;
}