/**
 * A class to represent a search result 
 * 
 * @author Ryan Dielhenn
 */
public class SearchResult {
	
	//File the query was found in
	private String location;
	
	//Number of times the query appears in location
	private int queryCount;
	
	//Total number of words in location
	private int wordCount;
	
	//Score of search result obtained by queryCount/wordCount
	private double score;
	
	
	/**
	 * @param location location 
	 * @param queryCount
	 * @param wordCount
	 */
	public SearchResult(String location, int queryCount, int wordCount) {
		this.location = location;
		this.queryCount = queryCount;
		this.wordCount = wordCount;
		this.score = (double) queryCount/wordCount;
	}

	/**
	 * @return the location
	 */
	public String getLocation() {
		return location;
	}

	/**
	 * @return the queryCount
	 */
	public int getQueryCount() {
		return queryCount;
	}

	/**
	 * @return the wordCount
	 */
	public int getWordCount() {
		return wordCount;
	}

	/**
	 * @return the score
	 */
	public double getScore() {
		return score;
	}
	
	/**
	 * Updates the query count and score
	 * @param queriesFound 
	 */
	public void updateCount(int queriesFound) {
		this.queryCount += queriesFound;
		this.score = (double) queryCount / wordCount;
	}
	 
	/**
	 * Compares search result objects by score, then word count, then location
	 * @param result 
	 * @return -1 if this is less than result, 0 if equal, 1 if this is greater than result
	 */
	public int compareTo(SearchResult result) {
		if (this.score == result.getScore()) {
			if (this.queryCount == result.getQueryCount()) {
				if (this.location.compareTo(result.getLocation()) == 0) {
					return 0;
				}
				return this.location.compareTo(result.getLocation()) < 0 ? -1 : 1;
			}
			return this.queryCount < result.getQueryCount() ? -1 : 1;
			
		}
		return this.score < result.getScore() ? -1 : 1;
	}
}