#pragma version(1)
#pragma rs java_package_name(pl.karol202.paintplus.color.manipulators)

uchar* curve_rtr;
uchar* curve_rtg;
uchar* curve_rtb;
uchar* curve_gtr;
uchar* curve_gtg;
uchar* curve_gtb;
uchar* curve_btr;
uchar* curve_btg;
uchar* curve_btb;

uchar4 RS_KERNEL transform(uchar4 in)
{
	float4 out = { 0, 0, 0, 0 };
	if(curve_rtr != NULL) out.r += curve_rtr[in.r];
	if(curve_rtg != NULL) out.g += curve_rtg[in.r];
	if(curve_rtb != NULL) out.b += curve_rtb[in.r];
	if(curve_gtr != NULL) out.r += curve_gtr[in.g];
	if(curve_gtg != NULL) out.g += curve_gtg[in.g];
	if(curve_gtb != NULL) out.b += curve_gtb[in.g];
	if(curve_btr != NULL) out.r += curve_btr[in.b];
	if(curve_btg != NULL) out.g += curve_btg[in.b];
	if(curve_btb != NULL) out.b += curve_btb[in.b];
	out.r = min((float) out.r, (float) 255);
	out.g = min((float) out.g, (float) 255);
	out.b = min((float) out.b, (float) 255);
	out.a = in.a;
	return convert_uchar4(out);
}