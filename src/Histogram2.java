public class Histogram2 {
	
	private Integer[] _histogram;
	private int _numPixels;
	private int H_BINS = 50;
	private int GRAY_BINS = 4;
	private int TOTAL_BINS = H_BINS + GRAY_BINS;
	private int histogram_height = 256;


	
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
		for (int i=0; i<_histogram.length; i++) {
			int normalizedValue = (_histogram[i]*histogram_height)/_numPixels;
			_histogram[i] = normalizedValue;
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
	
	
	public int sumBinValues() {
		int sum = 0;
		for (int i=0; i<_histogram.length; i++) {
			sum += _histogram[i];
		}
		return sum;
	}
	
	public int getHBin(float[] hsv) {		
		float h = hsv[0];
		float s = hsv[1];
		float v = hsv[2];
		
	
		// For almost-black and almost-white and gray pixels, just use gray bins
		if ((s > 0.1 && v < 0.1) || (s < 0.1 && v > 0.9) || 
				(s == 0 && v == 0) || (h == 0 && s == 0)) {
			return H_BINS + (int)Math.floor(Math.abs(v)*GRAY_BINS * 0.999999);
		} else {
			// Else use hue bins
			return (int)Math.floor(Math.abs(h)*H_BINS * 0.999999);
		}
		
	}
	
	public int getBinValue(int binNumber) {
		return _histogram[binNumber];
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
