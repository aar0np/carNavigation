package carnav;

import com.datastax.astra.client.Collection;
import com.datastax.astra.client.DataAPIClient;
import com.datastax.astra.client.model.Document;
import com.datastax.astra.client.Database;

// import static com.datastax.astra.client.model.SimilarityMetric.EUCLIDEAN;

public class AstraDBMethods {
	
	private Collection<Document> collection;
	
	public AstraDBMethods() {
		String token = System.getenv("ASTRA_DB_TOKEN");
		String endpoint = System.getenv("ASTRA_DB_ENDPOINT");
	
		// Initializing client with a token
		DataAPIClient client = new DataAPIClient(token);

		// Accessing the Database through the HTTP endpoint
		Database db = client.getDatabase(endpoint);

		// Create collection with vector support
		// collection = db.createCollection("car_navigation", 4, EUCLIDEAN);
		collection = db.getCollection("car_navigation");
	}
	
	public Collection<Document> getCollection() {
		return this.collection;
	}
}
