
public class Histogram {
	
	private Integer[] _histogram;
	private int _numPixels;
	public static int _hueBins = 30;
	public static int _histogramHeight = 1000; //TOTAL_BINS; // 256
	private static int _grayBins = 4;
	private static int _totalBins = _hueBins + _grayBins;
	private static double _BDistanceThreshold = 0.7; // threshold for comparing histograms
	private static int _range = 1;
	private double _mean;
	private double _var;

	
	public Histogram() {
		//Initialize every bin in the histogram to 0;
		_histogram = new Integer[_totalBins];
		for(int i=0; i<_histogram.length; i++){
			_histogram[i] = 0;
		}
		_numPixels = 0;
		_mean = 0;
		_var = 0;
	}
	
	
	// Adds the given hsv value to it's corresponding histogram bin
	public void AddValue(float[] hsv) {
		_histogram[getHBin(hsv)]++;		
		_numPixels++;
	}
	
	
	// Normalizes the histogram
	public void Normalize() {
		double sum = 0.0;
		double sum_sqr = 0.0;
		for (int i = 0; i<_histogram.length; i++) {
				_histogram[i] = (int)Math.round(((double)_histogram[i]*(double)_histogramHeight)/(double)_numPixels);
				
				sum += _histogram[i]/(double)_histogramHeight;
				sum_sqr += _histogram[i]/(double)_histogramHeight*_histogram[i]/(double)_histogramHeight;
		}
		
		// Computes mean and variance
		_mean = sum/(double)_numPixels;
		_var = (sum_sqr - (sum*sum)/(double)_numPixels)/((double)_numPixels - 1); 
	}
	
	
	
	// Compares two histograms; returns -1 if no match
	public double Compare(Histogram h) {

		// Computes the Bhattacharyya Distance between the two histograms
		Double distance = 0.25*(this.getVariance()/h.getVariance() + h.getVariance()/this.getVariance() + 2);
		distance = 0.25*Math.log1p(distance);
		distance += 0.25*(Math.pow(this.getMean() - h.getMean(), 2.0)/(this.getVariance() + h.getVariance()));


		// Checks if distance difference is within threshold
		if ((distance > _BDistanceThreshold) || distance.isNaN()) {
			distance = -1.0;
		}

		// Checks if the given histogram contains the max bin or it's surrounding bins
		// i.e. if query image's max bin = 0 or RED, then search image must contain bin 0
		//   or it's surrounding bins
		int maxBin = this.getMaxBin();
		boolean containsRange = false;
		Integer[] binRange = h.getBinRange(h.getMaxBin(), _range);
		for (int i=0; i<binRange.length; i++){
			if (binRange[i] == maxBin) {
				containsRange = true;
				break;
			}
		}

		if (!containsRange) {
			distance = -1.0;
		}
		return distance;
	}
	
	
	/*   Accessors   */
	
	// Returns the histogram
	public Integer[] getHistogram() {
		return _histogram;
	}
	
	// Returns the mean of all the bin values in the histogram
	public double getMean() {
		return _mean;
	}
	
	// Returns the variance between the bin values in the histogram
	public double getVariance() {
		return _var;
	}
		
	// Returns the total number of pixels
	public int getNumPixels() {
		return _numPixels;
	}
	
	
	// Returns the sum of all the bins with a value > 0
	public int sumNonZeroCountBins() {
		int sum = 0;
		for (int i=0; i<_histogram.length; i++) {
			if (_histogram[i] != 0) {
				sum++;
			}
		}
		return sum;
	}
	
	
	// Returns the sum of all the bin values
	public int sumBinValues() {
		int sum = 0;
		for (int i=0; i<_histogram.length; i++) {
			sum += _histogram[i];
		}
		return sum;
	}
	
	
	// Returns the given hsv value's corresponding histogram bin
	public int getHBin(float[] hsv) {		
		Float h_value = new Float(hsv[0]);
		Float s_value = new Float (hsv[1]);
		Float v_value = new Float (hsv[2]);
		
		double h = new Double(h_value.doubleValue());
		double s = new Double(s_value.doubleValue());
		double v = new Double(v_value.doubleValue());
		
		if (v < 0.1) {
			// BLACK
			return _hueBins;
		} else if (s < 0.1) {
			if (v > 0.75) {
				// WHITE
				return _hueBins + 1;
			} else {
				// GRAY
				return _hueBins + 2 + (int)Math.floor(v*(double)(_grayBins - 2.0) * 0.999999);
			}
		} else {
			// Else use hue bins
			return (int)Math.floor(h*(double)_hueBins*0.999999);
		}
	}
	
	
	// Returns the value in the given bin number
	public int getBinValue(int binNumber) {
		return _histogram[binNumber];
	}
	
	
	// Gets the smallest bin value that is != max value
	public int getMinBinValue(){
		int max = getMaxBinValue();
		int min = max;
		for (int i=0; i< _hueBins; i++){
			if ((_histogram[i] < min) ){
				min = _histogram[i];
			}
		}
		
		if (min == max) {
			min = 0;
		}
		
		return min;
	}
	
	
	// Returns the highest value for all bins in the histogram
	public int getMaxBinValue() {
		int max = 0;
		for (int i=0; i< _hueBins; i++) {
			if (_histogram[i] > max) {
				max = _histogram[i];
			}
		}
		
		return max;
	}
	
	// Returns the bin number containing the highest value
	public int getMaxBin(){
		int maxBin = 0;
		int max = 0;
		for (int i=0; i< _hueBins; i++){
			if (_histogram[i] > max){
				maxBin = i;
				max = _histogram[i];
			}
		}
		
		return maxBin;
	}

	
	
	// Returns a range of bin numbers given a bin number and range value
	// i.e.  bin = 10, range = 2 returns {8, 9, 10, 11, 12}
	public Integer[] getBinRange(int bin, int range){
		Integer[] binRange;
		if (bin == _hueBins){
			binRange = new Integer[1];
			binRange[0] = bin;
		} else if (bin > _hueBins) {
			binRange = new Integer[3];
			binRange = new Integer[3];
			binRange[0] = bin;
			binRange[1] = bin + 1;
			binRange[2] = bin + 2;
		} else if (bin == 0 ){
			binRange = new Integer[(range*2) + 1];
			for (int i=0; i<range; i++) {
				binRange[i] = _hueBins - range + i;
			}
			binRange[range] = bin;
			for (int i=0; i<range; i++){
				binRange[range + i + 1] = i + 1;
			}
		} else if (bin == _hueBins - 1) {
			binRange = new Integer[(range*2) + 1];
			for (int i=0; i<range; i++) {
				binRange[i] = _hueBins -1- range + i;
			}
			binRange[range] = bin;
			for (int i=0; i<range; i++){
				binRange[range + i + 1] = i + 1;
			}
			
		} else {
			binRange = new Integer[(range*2) + 1];
			for (int i=0; i<range; i++){
				binRange[i] = bin - range + i;
			}
			binRange[range] = bin;
			for (int i=0; i<range; i++){
				binRange[range + i + 1] = bin + i + 1;
			}
			
		}
		return binRange;
	}
	
	
	// Prints the histogram
	public void print() {	
		for (int i=0; i<_histogram.length; i++) {
			System.out.println("Bin: " + i + ", Value: " + _histogram[i]);
		}
	}
	
	
}
