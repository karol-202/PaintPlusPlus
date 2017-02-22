#pragma version(1)
#pragma rs java_package_name(pl.karol202.paintplus.color.manipulators)

bool* selectionData;
uint16_t selectionWidth;
uint16_t selectionLeft;
uint16_t selectionTop;
uint16_t selectionRight;
uint16_t selectionBottom;

static bool isSelected(uint32_t x, uint32_t y)
{
	if(selectionData == NULL) return true;
	if(x < selectionLeft || y < selectionTop || x >= selectionRight || y >= selectionBottom) return false;
	uint16_t selectionX = x - selectionLeft;
	uint16_t selectionY = y - selectionTop;
	return selectionData[selectionY * selectionWidth + selectionX];
}

uchar4 RS_KERNEL invert(uchar4 in, uint32_t x, uint32_t y)
{
	if(!isSelected(x, y)) return in;

	uchar4 out = in;
	out.r = 255 - out.r;
	out.g = 255 - out.g;
	out.b = 255 - out.b;
	return out;
}