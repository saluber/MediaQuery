import sun.misc.FloatingDecimal;

public class Histogram2 {
	
	private Integer[] _histogram;
	private int _numPixels;
	private int H_BINS = 50;
	private int GRAY_BINS = 4;
	private int TOTAL_BINS = H_BINS + GRAY_BINS;
	private int histogram_height = 256; //TOTAL_BINS; // 256
	/*
	private int BLACKBIN = H_BINS;
	private int WHITEBIN = H_BINS + 1;
	private int GRAYBIN = H_BINS + 2;
	private int REDBIN = H_BINS + 3;
	*/
	
	public Histogram2() {
		_histogram = new Integer[TOTAL_BINS];
		for(int i=0; i<_histogram.length; i++){
			_histogram[i] = 0;
		}
		_numPixels = 0;
	}
	
	public void AddValue(float[] hsv) {
		_histogram[getHBin(hsv)]++;		
		_numPixels++;
	}
	
	
	public void Normalize() {
		for (int i = 0; i<_histogram.length; i++) {
				_histogram[i] = (int)Math.round(((double)_histogram[i]*(double)histogram_height)/(double)_numPixels);
				//int normalizedValue = (_histogram[i]*histogram_height)/_numPixels;
				//_histogram[i] = normalizedValue;
		}	
	}
	
	public void print() {	
		for (int i=0; i<_histogram.length; i++) {
			System.out.println("Bin: " + i + ", Value: " + _histogram[i]);
		}
	}
	
	
	// Accessors	
	public Integer[] getHistogram() {
		return _histogram;
	}
		
	public int getNumPixels() {
		return _numPixels;
	}
	
	public int sumNonZeroCountBins()
	{
		int sum = 0;
		for (int i=0; i<_histogram.length; i++) {
			if (_histogram[i] != 0)
			{
				sum++;
			}
		}
		return sum;
	}
	
	public int sumBinValues() {
		int sum = 0;
		for (int i=0; i<_histogram.length; i++) {
			sum += _histogram[i];
		}
		return sum;
	}
	
	public int getHBin(float[] hsv) {		
		Float h_value = new Float(hsv[0]);
		Float s_value = new Float (hsv[1]);
		Float v_value = new Float (hsv[2]);
		
		double h = new FloatingDecimal(h_value.floatValue()).doubleValue();
		double s = new FloatingDecimal(s_value.floatValue()).doubleValue();
		double v = new FloatingDecimal(v_value.floatValue()).doubleValue();
		
		if (v < 0.1)
		{
			// BLACK
			return H_BINS;
		}
		else if (s < 0.1)
		{
			if (v > 0.75)
			{
				// WHITE
				return H_BINS + 1;
			}
			else
			{
				// GRAY
				return H_BINS + 2 + (int)Math.floor(v*(double)(GRAY_BINS - 2.0) * 0.999999);
			}
			//return H_BINS + (int)Math.floor(Math.abs(v)*GRAY_BINS * 0.999999);
		}
		// For almost-black and almost-white and gray pixels, just use gray bins
		/*
		if ((v < 0.1) || (s < 0.9 && v > 0.9) || 
				(s == 0 && v == 0) || (h == 0 && s == 0)) {
			return H_BINS + (int)Math.floor(Math.abs(v)*GRAY_BINS * 0.999999);
		}
		*/ else {
			// Else use hue bins
			return (int)Math.floor(h*(double)H_BINS*0.999999);
			//return (int)Math.floor(Math.abs(h)*H_BINS * 0.999999);
		}
	}
	
	public int getBinValue(int binNumber) {
		return _histogram[binNumber];
	}
	
	// Gets the smallest bin value that is != max value
	public int getMinBinValue(){
		int max = gexMaxBinValue();
		int min = max;
		for (int i=0; i< _histogram.length; i++){
			if ((_histogram[i] < min) ){
				min = _histogram[i];
			}
		}
		
		if (min == max)
		{
			min = 0;
		}
		
		return min;
	}
	
	public int gexMaxBinValue(){
		int max = 0;
		for (int i=0; i< _histogram.length; i++){
			if (_histogram[i] > max){
				max = _histogram[i];
			}
		}
		
		return max;
	}
}
