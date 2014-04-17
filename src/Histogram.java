import java.util.HashMap;
import java.util.Map;

public class Histogram 
{
	private Map<Integer, Double> _histogram;
	private int _numPixels;
	
	private static int hueThreshold = 2;
	private static double huePixelCountThreshold = 100;
	private static double levels = 360.0;
	
	public Histogram()
	{
		_histogram = new HashMap<Integer, Double>();
		_numPixels = 0;
	}
	
	public void AddValue(Integer hueValue)
	{
		int hueBucket = hueValue/hueThreshold;
		double huePixelCount = 0.0;
		if (_histogram.containsKey(hueBucket))
		{
			huePixelCount = _histogram.get(hueBucket);
		}
		
		_histogram.put(hueBucket, huePixelCount + 1.0);
		_numPixels++;
	}
	
	public void Normalize()
	{
		for (Integer hueBucket : _histogram.keySet())
		{
			double count = _histogram.get(hueBucket);
			count = (double)count/((double)_numPixels);
			
			// Round off to nearest hundreth
			count = Math.round(count*100.0)/100.0;
			
			_histogram.put(hueBucket, count);
		}
	}
	
	// Assumes both histograms are normalized
	public boolean contains(Histogram h)
	{
		Map<Integer, Double> m = h.getHistogram();
		boolean match = true;
		for (Integer hueBucket : m.keySet())
		{
			double hueBucketVal = m.get(hueBucket);
			if (hueBucketVal > 0.01)
			{
				if (!_histogram.containsKey(hueBucket))
				{
					match = false;
					break;
				}
				
				/*
				double myHueBucketVal = _histogram.get(hueBucket);
				if (Math.abs(hueBucketVal - myHueBucketVal) > valueThreshold)
				{
					match = false;
					break;
				}
				*/
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
	
	// Accessors
	public Map<Integer, Double> getHistogram()
	{
		return _histogram;
	}
	
	public int getNumPixels()
	{
		return _numPixels;
	}
}
