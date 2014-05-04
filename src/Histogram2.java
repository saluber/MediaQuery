import sun.misc.FloatingDecimal;

public class Histogram2 {
	
	private Integer[] _histogram;
	private int _numPixels;
	private int H_BINS = 50;
	private int GRAY_BINS = 4;
	private int TOTAL_BINS = H_BINS + GRAY_BINS;
	private int histogram_height = 256; //TOTAL_BINS; // 256
	private double threshold = 0.5; // threshold for comparing histograms
	private double valueRatioThreshold = 0.5;
	
	private double _mean;
	private double _var;
	
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
		_mean = 0;
		_var = 0;
	}
	
	public void AddValue(float[] hsv) {
		_histogram[getHBin(hsv)]++;		
		_numPixels++;
	}
	
	public void Normalize() {
		double sum = 0.0;
		double sum_sqr = 0.0;
		for (int i = 0; i<_histogram.length; i++) {
				_histogram[i] = (int)Math.round(((double)_histogram[i]*(double)histogram_height)/(double)_numPixels);
				//int normalizedValue = (_histogram[i]*histogram_height)/_numPixels;
				//_histogram[i] = normalizedValue;
				
				sum += _histogram[i];
				sum_sqr += _histogram[i]*_histogram[i];
		}
		
		_mean = sum/(double)_numPixels;
		_var = (sum_sqr - (sum*sum)/(double)_numPixels)/((double)_numPixels - 1); 
	}
	
	public void Normalize(Histogram2 logoHistogram) {
		double sum = 0.0;
		double sum_sqr = 0.0;
		for (int i = 0; i<_histogram.length; i++) {
				_histogram[i] = (int)Math.round(((double)_histogram[i]*(double)histogram_height)/(double)_numPixels);
				//int normalizedValue = (_histogram[i]*histogram_height)/_numPixels;
				//_histogram[i] = normalizedValue;
				
				sum += _histogram[i];
				sum_sqr += _histogram[i]*_histogram[i];
		}
		
		_mean = sum/(double)_numPixels;
		_var = (sum_sqr - (sum*sum)/(double)_numPixels)/((double)_numPixels - 1); 
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
	
	public double getMean()
	{
		return _mean;
	}
	
	public double getVariance()
	{
		return _var;
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
		int max = getMaxBinValue();
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
	
	public int getMaxBinValue(){
		int max = 0;
		for (int i=0; i< _histogram.length; i++){
			if (_histogram[i] > max){
				max = _histogram[i];
			}
		}
		
		return max;
	}
	
	public int get2ndMaxBinValue(){
		int maxBin = getMaxBin();
		int max = 0;
		for (int i=0; i< _histogram.length; i++){
			if ((_histogram[i] > max) && (i != maxBin)){
				max = _histogram[i];
			}
		}
		
		return max;
	}
	
	public int getMaxBin(){
		int maxBin = 0;
		int max = 0;
		for (int i=0; i< _histogram.length; i++){
			if (_histogram[i] > max){
				maxBin = i;
				max = _histogram[i];
			}
		}
		
		return maxBin;
	}
	
	public double Compare(Histogram2 h)
	{
		Double distance = 0.25*(this.getVariance()/h.getVariance() + h.getVariance()/this.getVariance() + 2);
		distance = 0.25*Math.log1p(distance);
		distance += 0.25*(Math.pow(this.getMean() - h.getMean(), 2.0)/(this.getVariance() + h.getVariance()));
		
		/*
		System.out.println("Query mean: " + this.getMean());
		System.out.println("Query var: " + this.getVariance());
		System.out.println("Search image mean: " + h.getMean());
		System.out.println("Search image var: " + h.getVariance());
		*/
		System.out.println("B distance: " + distance);
		
		// Check if distance difference is within threshold
		if ((distance > threshold) || distance.isNaN())
		{
			distance = -1.0;
		}
		
		
		
		// Check if max bin in logo image is in search image cluster
		double queryImageMaxBinRatio = (double)this.getMaxBinValue()/(double)this.getNumPixels();
		double searchImageMaxBinRatio = (double)h.getMaxBinValue()/(double)h.getNumPixels();
		double queryImageSecondMaxBinRatio = (double)this.get2ndMaxBinValue()/(double)this.getNumPixels();
		double searchImageSecondMaxBinRatio = (double)h.get2ndMaxBinValue()/(double)h.getNumPixels();
		
		double queryRatio = queryImageSecondMaxBinRatio/queryImageMaxBinRatio;
		double searchRatio = searchImageSecondMaxBinRatio/searchImageMaxBinRatio;
		System.out.println("Ratio difference: " + Math.abs(queryRatio - searchRatio));
		if (!(Math.abs(queryRatio - searchRatio) < valueRatioThreshold))
		{
			System.out.println("Not match due to ratio difference. Query image: " + queryRatio
					+ ". Search image: " + searchRatio);
			System.out.println("Difference: " + Math.abs(queryRatio - searchRatio));
			distance = -1.0;
		}
		
		return distance;
	}
}
