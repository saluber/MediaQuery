
public class Histogram2 {
	
	private Integer[] _histogram;
	private int _numPixels;
	public static int H_BINS = 30;
	private int GRAY_BINS = 4;
	private int TOTAL_BINS = H_BINS + GRAY_BINS;
	public static int histogram_height = 1000; //TOTAL_BINS; // 256
	private double threshold = 0.7; // threshold for comparing histograms
	private double valueRatioThreshold = 0.5;
	private static int RANGE = 1;
	
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
				
				sum += _histogram[i]/(double)histogram_height;
				sum_sqr += _histogram[i]/(double)histogram_height*_histogram[i]/(double)histogram_height;
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
		
		double h = new Double(h_value.doubleValue());
		double s = new Double(s_value.doubleValue());
		double v = new Double(v_value.doubleValue());
		
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
		for (int i=0; i< H_BINS; i++){
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
		for (int i=0; i< H_BINS; i++){
			if (_histogram[i] > max){
				max = _histogram[i];
			}
		}
		
		return max;
	}
	
	
	public int getMaxBin(){
		int maxBin = 0;
		int max = 0;
		for (int i=0; i< H_BINS; i++){
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
		
		int maxBin = this.getMaxBin();
		boolean containsRange = false;
		Integer[] binRange = h.getBinRange(h.getMaxBin(), RANGE);
		for (int i=0; i<binRange.length; i++){
			if (binRange[i] == maxBin) {
				containsRange = true;
				break;
			}
		}

		if (!containsRange) {
			distance = -1.0;
		}
		
	/*	
		// Check if max bin in logo image is in search image cluster
		int maxBin = this.getMaxBin();
		int searchBinSum = 0;
		Integer[] binRange = h.getBinRange(maxBin);
		for (int i=0; i<binRange.length; i++){
			searchBinSum += h.getBinValue(binRange[i]);
		}
		double queryImageMaxBinRatio = (double)this.getMaxBinValue()/(double)this.sumBinValues();
		double searchImageMaxBinRatio = (double)searchBinSum/(double)h.sumBinValues();
		
		System.out.println("Query max bin ratio: " + queryImageMaxBinRatio);
		System.out.println("Search max bin ratio: " + searchImageMaxBinRatio);
		//double queryImageSecondMaxBinRatio = (double)this.get2ndMaxBinValue()/(double)this.getNumPixels();
		//double searchImageSecondMaxBinRatio = (double)h.get2ndMaxBinValue()/(double)h.getNumPixels();
		
		//double queryRatio = queryImageSecondMaxBinRatio/queryImageMaxBinRatio;
		//double searchRatio = searchImageSecondMaxBinRatio/searchImageMaxBinRatio;
		System.out.println("Ratio difference: " + Math.abs(queryImageMaxBinRatio - searchImageMaxBinRatio));
		if (!(Math.abs(queryImageMaxBinRatio - searchImageMaxBinRatio) < valueRatioThreshold))
		{
			System.out.println("Not match due to ratio difference. Query image: " + queryImageMaxBinRatio
					+ ". Search image: " + searchImageMaxBinRatio);
			System.out.println("Difference: " + Math.abs(queryImageMaxBinRatio - searchImageMaxBinRatio));
			distance = -1.0;
		} 
		
		*/
		return distance;
	}
	
	
	public Integer[] getBinRange(int bin, int range){
		Integer[] binRange;
		if (bin == H_BINS){
			binRange = new Integer[1];
			binRange[0] = bin;
		} else if (bin > H_BINS) {
			binRange = new Integer[3];
			binRange = new Integer[3];
			binRange[0] = bin;
			binRange[1] = bin + 1;
			binRange[2] = bin + 2;
		} else if (bin == 0 ){
			binRange = new Integer[(range*2) + 1];
			for (int i=0; i<range; i++) {
				binRange[i] = H_BINS - range + i;
			}
			binRange[range] = bin;
			for (int i=0; i<range; i++){
				binRange[range + i + 1] = i + 1;
			}
		} else if (bin == H_BINS - 1) {
			binRange = new Integer[(range*2) + 1];
			for (int i=0; i<range; i++) {
				binRange[i] = H_BINS -1- range + i;
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
	
	/*
	public Integer[] getBinRange(int bin){
		Integer[] range;
		if (bin == H_BINS) {
			range = new Integer[1];
			range[0] = bin;
		} else if (bin > H_BINS ) {
				range = new Integer[3];
				range[0] = bin;
				range[1] = bin + 1;
				range [2] = bin + 2;
		} else if (bin == 0){
			range = new Integer[3];
			range[0] = H_BINS - 1;
			range[1] = bin;
			range[2] = bin+1;
		} else if (bin == H_BINS - 1){
			range = new Integer[3];
			range[0] = bin-1;
			range[1] = bin;
			range[2] = 0;
		} else {
			range = new Integer[3];
			range[0] = bin-1;
			range[1] = bin;
			range[2] = bin+1;
		}
	return range;
	} */
	
	
}
