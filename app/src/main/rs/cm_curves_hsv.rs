#pragma version(1)
#pragma rs java_package_name(pl.karol202.paintplus.color.manipulators)

typedef struct HSV
{
	float h;
	float s;
	float v;
} float3_hsv;

bool* selectionData;
uint16_t selectionWidth;
uint16_t selectionLeft;
uint16_t selectionTop;
uint16_t selectionRight;
uint16_t selectionBottom;

ushort* curve_hth;
ushort* curve_hts;
ushort* curve_htv;
ushort* curve_sth;
ushort* curve_sts;
ushort* curve_stv;
ushort* curve_vth;
ushort* curve_vts;
ushort* curve_vtv;

static ushort3 hsv_to_ushort3(float3_hsv hsv)
{
	ushort3 out = { (ushort) round(hsv.h), (ushort) round(hsv.s), (ushort) round(hsv.v) };
	return out;
}

static ushort3 float3_to_ushort3(float3 in)
{
	ushort3 out = { (ushort) round(in.r), (ushort) round(in.g), (ushort) round(in.b) };
	return out;
}

static ushort3 rgb_to_hsv(ushort3 in)
{
    float3_hsv out;
    float mn, mx, delta;

    mn = in.r < in.g ? in.r : in.g;
    mn = mn < in.b ? mn : in.b;

    mx = in.r > in.g ? in.r : in.g;
    mx = mx > in.b ? mx : in.b;

    out.v = mx * 100 / 255;
    delta = mx - mn;
    if(delta == 0)
    {
        out.h = 0;
        out.s = 0;
        return hsv_to_ushort3(out);
    }
    if(mx > 0) out.s = delta * 100 / mx;
    else
    {
        out.h = 0;
        out.s = 0;
        return hsv_to_ushort3(out);
    }
    
    if(in.r == mx) out.h = (in.g - in.b) / delta;
    else if(in.g == mx) out.h = 2 + (in.b - in.r) / delta;
    else out.h = 4 + (in.r - in.g) / delta;
    out.h *= 60;
	if(out.h < 0) out.h += 360;
	
    return hsv_to_ushort3(out);
}

//Problemy z dokładnością
static ushort3 hsv_to_rgb(ushort3 in)
{
	float3_hsv in_hsv = { in.r, in.g / (float) 100, in.b / (float) 100 };
	float3 out;
    float hh, p, q, t, ff;
    long i;

    if(in_hsv.s == 0)
    {
        out.r = in_hsv.v * 255;
        out.g = out.r;
        out.b = out.r;
        return convert_ushort3(out);
    }
    
    hh = in_hsv.h;
    if(hh >= 360) hh = 0;
    hh /= 60;
    i = (long) hh;
    ff = hh - i;
    p = in_hsv.v * (1 - in_hsv.s);
    q = in_hsv.v * (1 - (in_hsv.s * ff));
    t = in_hsv.v * (1 - (in_hsv.s * (1 - ff)));

    switch(i)
    {
    case 0:
        out.r = in_hsv.v * 255;
        out.g = t * 255;
        out.b = p * 255;
        break;
    case 1:
        out.r = q * 255;
        out.g = in_hsv.v * 255;
        out.b = p * 255;
        break;
    case 2:
        out.r = p * 255;
        out.g = in_hsv.v * 255;
        out.b = t * 255;
        break;
    case 3:
        out.r = p * 255;
        out.g = q * 255;
        out.b = in_hsv.v * 255;
        break;
    case 4:
        out.r = t * 255;
        out.g = p * 255;
        out.b = in_hsv.v * 255;
        break;
    case 5:
    default:
        out.r = in_hsv.v * 255;
        out.g = p * 255;
        out.b = q * 255;
        break;
    }
    return convert_ushort3(out);
}

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

	ushort3 in_rgb = { (ushort) in.r, (ushort) in.g, (ushort) in.b };
	ushort3 in_hsv = rgb_to_hsv(in_rgb);
	ushort in_h = in_hsv.r;
	ushort in_s = in_hsv.g;
	ushort in_v = in_hsv.b;
	ushort out_h, out_s, out_v;
	if(curve_hth != NULL) out_h += curve_hth[in_h]; else out_h += in_h;
	//if(curve_hts != NULL) out_s += curve_hts[in_h];
	//if(curve_htv != NULL) out_v += curve_htv[in_h];
	if(curve_sth != NULL) out_h += curve_sth[in_s];
	if(curve_sts != NULL) out_s += curve_sts[in_s]; else out_s += in_s;
	if(curve_stv != NULL) out_v += curve_stv[in_s];
	if(curve_vth != NULL) out_h += curve_vth[in_v];
	if(curve_vts != NULL) out_s += curve_vts[in_v];
	if(curve_vtv != NULL) out_v += curve_vtv[in_v]; else out_v += in_v;
	
	out_h = min((float) out_h, (float) 360);
	out_s = min((float) out_s, (float) 100);
	out_v = min((float) out_v, (float) 100);
	
	ushort3 out_hsv = { out_h, out_s, out_v };
	ushort3 out_rgb = hsv_to_rgb(out_hsv);
	uchar4 out = { out_rgb.r, out_rgb.g, out_rgb.b, in.a };
	return out;
}