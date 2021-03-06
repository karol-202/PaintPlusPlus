package pl.karol202.paintplus.color.curves;

public class ChannelInOutSet
{
	private ColorChannel in;
	private ColorChannel out;
	
	ChannelInOutSet(ColorChannel in, ColorChannel out)
	{
		this.in = in;
		this.out = out;
	}
	
	@Override
	public boolean equals(Object o)
	{
		if(this == o) return true;
		if(o == null || getClass() != o.getClass()) return false;
		
		ChannelInOutSet that = (ChannelInOutSet) o;
		
		return in == that.in && out == that.out;
	}
	
	@Override
	public int hashCode()
	{
		int result = in != null ? in.hashCode() : 0;
		result = 31 * result + (out != null ? out.hashCode() : 0);
		return result;
	}
	
	public ColorChannel getIn()
	{
		return in;
	}
	
	public ColorChannel getOut()
	{
		return out;
	}
}