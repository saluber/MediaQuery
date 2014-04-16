import java.util.HashMap;
import java.util.Map;

public class Histogram 
{
	private Map<Integer, Double> _histogram;
	private double _numPixels;
	private double _minHue = 360.0;
	private double _maxHue = -1.0;
	
	private static int hueThreshold = 1;
	private static double valueThreshold = 100;
	private static double levels = 360.0;
	
	public Histogram()
	{
		_histogram = new HashMap<Integer, Double>();
		_numPixels = 0.0;
	}
	
	public Map<Integer, Double> getHistogram()
	{
		return _histogram;
	}
	
	public void AddValue(Integer hueValue)
	{
		int hueBucket = hueValue/hueThreshold;
		double prevValue = 0.0;
		if (_histogram.containsKey(hueBucket))
		{
			prevValue = _histogram.get(hueBucket);
		}
		
		if (hueValue > _maxHue)
		{
			_maxHue = hueValue;
		}
		
		if (hueValue < _minHue)
		{
			_minHue = hueValue;
		}

		_histogram.put(hueBucket, prevValue + 1.0);
		_numPixels++;
	}
	
	public void Normalize()
	{
		for (Integer hue : _histogram.keySet())
		{
			double count = _histogram.get(hue);
			count = Math.round((count - _minHue)/(_numPixels - _minHue)*(levels - 1.0));
			_histogram.put(hue, count);
		}
	}
	
	// Assumes both histograms are normalized
	public boolean contains(Histogram h)
	{
		Map<Integer, Double> m = h.getHistogram();
		boolean match = true;
		for (Integer hue : m.keySet())
		{
			if (!this._histogram.containsKey(hue))
			{
				match = false;
				break;
			}
			
			double c1 = this._histogram.get(hue);
			double c2 = m.get(hue);
			
			if (Math.abs(c1 - c2) > valueThreshold)
			{
				match = false;
				break;
			}
		}
		
		return match;
	}
	
	public void print()
	{
		for (Integer hue : _histogram.keySet())
		{
			Double n = _histogram.get(hue);
			System.out.println(hue + ": " + n);
		}
		
		System.out.println();
	}
}
