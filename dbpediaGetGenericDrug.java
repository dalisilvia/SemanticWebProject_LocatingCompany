package knowledgeBases;

import org.apache.jena.query.Query;
import org.apache.jena.query.QueryExecution;
import org.apache.jena.query.QueryExecutionFactory;
import org.apache.jena.query.QueryFactory;
import org.apache.jena.query.ResultSet;
import org.apache.jena.query.ResultSetFormatter;


public class dbpediaGetGenericDrug{

//	public static void printStatements(Model m, Resource s, Property p, Resource o) {
//		for (StmtIterator i = m.listStatements(s, p, o); i.hasNext();) {
//			Statement stmt = i.nextStatement();
//			System.out.println(" - " + PrintUtil.print(stmt));
//		}
//	}

	public static void main(String[] args) {
		//String dbPedia = "http://www.dbpedia.org/";		
		
		String ontology_service = "http://factforge.net/sparql";

		String endpointSparql = 
				"PREFIX fb: <http://rdf.freebase.com/ns/> \n"
				+" PREFIX rdfs: <http://www.w3.org/2000/01/rdf-schema#> \n"
				+" PREFIX rdf: <http://www.w3.org/1999/02/22-rdf-syntax-ns#> "
				+"	PREFIX dbpedia: <http://dbpedia.org/resource/> \n"
				+"		PREFIX dbo: <http://dbpedia.org/ontology/> \n"
				+"		PREFIX dbp: <http://dbpedia.org/property/> \n"
				+"		PREFIX dbr: <http://dbpedia.org/resource/>  \n"

				+"		SELECT DISTINCT * \n"
				+"		WHERE { \n"
				+"		   ?drugBrandName dbo:wikiPageRedirects ?genericDrug . \n"
				+"		   ?genericDrug rdfs:label ?label ; \n"
				+"		                rdf:type dbo:Drug . \n"
				+"		   FILTER(lang(?label)=\"en\" ) \n"
					    
				+"		} limit 1000 \n" ;
			

		System.out.println(endpointSparql);
		Query query = QueryFactory.create(endpointSparql);
		
		QueryExecution queryExe =
				QueryExecutionFactory.sparqlService(ontology_service, query);    //endpoint search query
		
		ResultSet results = queryExe.execSelect();
		
	/*	while(results.hasNext()) {
			QuerySolution sol = results.next();
			String s = sol.get("x").toString();
			System.out.println(s);
		}
		*/
		ResultSetFormatter.out(System.out, results);
		
		
	}
}
