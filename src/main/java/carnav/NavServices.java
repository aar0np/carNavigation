package carnav;

import java.util.ArrayList;
import java.util.List;

import org.javatuples.Pair;

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
	
	public List<float[]> vectorSearch(String map, float[] vector) {
		
		// run similarity search on DB, get top 10 closest results
		FindIterable<Document> docsI = collection.find(eq("map", mapName),vector,10);
		List<Document> docs = new ArrayList<Document>();
		
		Pair<Float,Float> start = new Pair<Float,Float>(vector[0],vector[1]);
		Pair<Float,Float> end = new Pair<Float,Float>(vector[2],vector[3]);

		//List<Document> returnVal = new ArrayList<Document>();
		List<float[]> returnVal = new ArrayList<float[]>();
		List<Pair<Float,Float>> vectorPairs = new ArrayList<Pair<Float,Float>>();
		
		// process results into vector pairs, and add to list
		for (Document doc : docsI) {
			float[] vectors = doc.getVector().get();
			docs.add(doc);
			
			Pair<Float,Float> pair1 = new Pair<Float,Float>(vectors[0],vectors[1]);
			Pair<Float,Float> pair2 = new Pair<Float,Float>(vectors[2],vectors[3]);

			vectorPairs.add(pair1);
			vectorPairs.add(pair2);
		}
		
		// reprocess the results to guage how useful they are
		for (Document doc : docs) {
			float[] vectors = doc.getVector().get();
			Pair<Float,Float> vector1 = new Pair<Float,Float>(vectors[0],vectors[1]);
			Pair<Float,Float> vector2 = new Pair<Float,Float>(vectors[2],vectors[3]);
			
			// is within 6? of start or end point
			boolean nearStartOrEnd = false;
			
			double distanceStart1 = euclideanDistance(vector1,start);
			double distanceEnd1 = euclideanDistance(vector1,end);
			double distanceStart2 = euclideanDistance(vector2,start);
			double distanceEnd2 = euclideanDistance(vector2,end);
			
			if (distanceStart1 < 6 || distanceStart2 < 6
					|| distanceEnd1 < 6 || distanceEnd2 < 6) {
				
				nearStartOrEnd = true;
			}
			
			boolean isOrphan = false;
			boolean isDuplicate = false;
			
			if (!nearStartOrEnd) {
				// is orphan?
				int closeMatchesV1 = 0;
				int closeMatchesV2 = 0;
				
				for (Pair<Float,Float> pair : vectorPairs) {
					double distanceV1 = euclideanDistance(vector1,pair);
					double distanceV2 = euclideanDistance(vector2,pair);
					
					if (distanceV1 <= 1) {
						closeMatchesV1++;
					}
					
					if (distanceV2 <= 1) {
						closeMatchesV2++;
					}
					if (closeMatchesV1 > 1 && closeMatchesV2 > 1) {
						// we know for sure these are not unique, so break
						break;
					}
				}
				
				if (closeMatchesV1 == 1 || closeMatchesV2 == 1) {
					isOrphan = true;
				}
			}
		
			// Does the vector already exist, but moving in the opposite direction?
			if (vectors[1] == vectors[3]) {
				float oppositeVectorHorizontal1[] = {vectors[2],vectors[3] + 1,vectors[0],vectors[1] + 1};
				float oppositeVectorHorizontal2[] = {vectors[2],vectors[3] - 1,vectors[0],vectors[1] - 1};
				
				for (float[] returnVector : returnVal) {
					if (returnVector[0] == oppositeVectorHorizontal1[0] && returnVector[2] == oppositeVectorHorizontal1[2] &&
							(returnVector[1] == oppositeVectorHorizontal1[1] ||
							returnVector[1] == oppositeVectorHorizontal2[1])) {
						isDuplicate = true;
						break;
					}
				}
			} else {
				float oppositeVectorVertical1[] = {vectors[2] + 1,vectors[3],vectors[0] + 1,vectors[1]};
				float oppositeVectorVertical2[] = {vectors[2] - 1,vectors[3],vectors[0] - 1,vectors[1]};
				
				for (float[] returnVector : returnVal) {
					if (returnVector[1] == oppositeVectorVertical1[1] && returnVector[3] == oppositeVectorVertical1[3] &&
							(returnVector[0] == oppositeVectorVertical1[0] ||
							returnVector[0] == oppositeVectorVertical2[0])) {
						isDuplicate = true;
						break;
					}
				}
			}
			
			if (!isDuplicate) {
				// if not a duplicate
				if (!isOrphan) {
					// if not an orphan, add!
					returnVal.add(vectors);
				} else if (nearStartOrEnd) {
					// if orphan, but near start or end, add!
					returnVal.add(vectors);
				}
			}
		}
		
		return returnVal;
	}
	
	private double euclideanDistance(Pair<Float,Float> vector1, Pair<Float,Float> vector2) {
		Float x1 = vector1.getValue0();
		Float x2 = vector2.getValue0();
		Float y1 = vector1.getValue1();
		Float y2 = vector2.getValue1();
		
		double distance = Math.sqrt(Math.pow(x2-x1, 2) + Math.pow(y2-y1, 2));
		
		return distance;
	}
}
