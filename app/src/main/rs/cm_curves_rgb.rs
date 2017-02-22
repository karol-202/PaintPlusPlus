#pragma version(1)
#pragma rs java_package_name(pl.karol202.paintplus.color.manipulators)

bool* selectionData;
uint16_t selectionWidth;
uint16_t selectionLeft;
uint16_t selectionTop;
uint16_t selectionRight;
uint16_t selectionBottom;

uchar* curve_rtr;
uchar* curve_rtg;
uchar* curve_rtb;
uchar* curve_gtr;
uchar* curve_gtg;
uchar* curve_gtb;
uchar* curve_btr;
uchar* curve_btg;
uchar* curve_btb;

static bool isSelected(uint32_t x, uint32_t y)
{
	if(selectionData == NULL) return true;
	if(x < selectionLeft || y < selectionTop || x >= selectionRight || y >= selectionBottom) return false;
	uint16_t selectionX = x - selectionLeft;
	uint16_t selectionY = y - selectionTop;
	return selectionData[selectionY * selectionWidth + selectionX];
}

uchar4 RS_KERNEL transform(uchar4 in, uint32_t x, uint32_t y)
{
	if(!isSelected(x, y)) return in;

	float4 out = { 0, 0, 0, 0 };
	if(curve_rtr != NULL) out.r += curve_rtr[in.r]; else out.r += in.r;
	if(curve_rtg != NULL) out.g += curve_rtg[in.r];
	if(curve_rtb != NULL) out.b += curve_rtb[in.r];
	if(curve_gtr != NULL) out.r += curve_gtr[in.g];
	if(curve_gtg != NULL) out.g += curve_gtg[in.g]; else out.g += in.g;
	if(curve_gtb != NULL) out.b += curve_gtb[in.g];
	if(curve_btr != NULL) out.r += curve_btr[in.b];
	if(curve_btg != NULL) out.g += curve_btg[in.b];
	if(curve_btb != NULL) out.b += curve_btb[in.b]; else out.b += in.b;
	out.r = min((float) out.r, (float) 255);
	out.g = min((float) out.g, (float) 255);
	out.b = min((float) out.b, (float) 255);
	out.a = in.a;
	return convert_uchar4(out);
}