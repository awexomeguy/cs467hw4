public class Range
{
	private int _lowerBound;
	private int _upperBound;
	
	public Range(int l, int u)
	{
		_lowerBound = l;
		_upperBound = u;
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
	
	public void print()
	{
		System.out.println("[" + _lowerBound + ", " + _upperBound + "]");
	}
}
