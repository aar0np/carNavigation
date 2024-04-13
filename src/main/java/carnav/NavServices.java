package carnav;

import java.util.ArrayList;
import java.util.List;

import com.datastax.astra.client.Collection;
import com.datastax.astra.client.model.Document;
import com.datastax.astra.client.model.FindIterable;
import static com.datastax.astra.client.model.Filters.eq;

public class NavServices {
	
	private Collection<Document> collection;
	private String mapName;
	
	public NavServices(AstraDBMethods astraDB, String mapName) {
		this.collection = astraDB.getCollection();
		this.mapName = mapName;
	}
	
	public List<Document> vectorSearch(float[] vector) {
		
		List<Document> returnVal = new ArrayList<Document>();
		FindIterable<Document> docs = collection.find(eq("map", mapName),vector,10);
		
		for (Document doc : docs) {
			returnVal.add(doc);
		}
		
		return returnVal;
	}
}
