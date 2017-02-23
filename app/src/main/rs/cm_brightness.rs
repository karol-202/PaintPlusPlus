#pragma version(1)
#pragma rs java_package_name(pl.karol202.paintplus.color.manipulators)

bool* selectionData;
uint16_t selectionWidth;
uint16_t selectionLeft;
uint16_t selectionTop;
uint16_t selectionRight;
uint16_t selectionBottom;

float brightness;
float contrast;

static bool isSelected(uint32_t x, uint32_t y)
{
	if(selectionData == NULL) return true;
	if(x < selectionLeft || y < selectionTop || x >= selectionRight || y >= selectionBottom) return false;
	uint16_t selectionX = x - selectionLeft;
	uint16_t selectionY = y - selectionTop;
	return selectionData[selectionY * selectionWidth + selectionX];
}
	
static float map(float src, float srcMin, float srcMax, float dstMin, float dstMax)
{
	float result =  dstMin + ((src - srcMin) / (srcMax - srcMin) * (dstMax - dstMin));
	return max(min(result, (float) 255), (float) 0);
}

uchar4 RS_KERNEL invert(uchar4 in, uint32_t x, uint32_t y)
{
	if(!isSelected(x, y)) return in;

	uchar4 out = in;
	
    float srcMin;
    float srcMax;
    if(contrast > 0)
    {
        srcMin = contrast / 2 * 255;
        srcMax = (1 - contrast / 2) * 255;
    }
    else if(contrast == -1)
    {
        out.r = 128;
        out.g = 128;
        out.b = 128;
        return out;
    }
    else
    {
		srcMax = 1 / (contrast + 1) * 255;
		srcMin = 255 - srcMax;
    }
    
    float dstMin = brightness > 0 ? 255 * brightness : 0;
    float dstMax = brightness < 0 ? 255 * (brightness + 1) : 255;
	
	out.r = map(in.r, srcMin, srcMax, dstMin, dstMax);
	out.g = map(in.g, srcMin, srcMax, dstMin, dstMax);
	out.b = map(in.b, srcMin, srcMax, dstMin, dstMax);
	
	return out;
}