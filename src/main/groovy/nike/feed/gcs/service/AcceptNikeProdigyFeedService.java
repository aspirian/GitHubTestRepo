package nike.feed.gcs.service;

public interface AcceptNikeProdigyFeedService {

	/**
	 * @param filename
	 * @return
	 * @throws Exception
	 */
	Long acceptFeed(String filename) throws Exception;
}
