public class Range
{
	private int _lowerBound;
	private int _upperBound;
	private String _bCast;
	
	public Range(int l, int u, String bCast)
	{
		_lowerBound = l;
		_upperBound = u;
		_bCast = bCast;
	}
	
	public boolean contains(int x)
	{
		return(x >= _lowerBound && x <= _upperBound);
	}
	
	public int upperBound()
	{
		return _upperBound;
	}
	
	public int lowerBound()
	{
		return _lowerBound;
	}
	
	public String toString()
	{
		return("[" + _bCast.charAt(_lowerBound) + ", " + _bCast.charAt(_upperBound) + "]");
	}
}
