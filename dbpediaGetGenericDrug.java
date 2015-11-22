package knowledgeBases;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.util.List;
import java.util.Map;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import org.apache.jena.query.Query;
import org.apache.jena.query.QueryExecution;
import org.apache.jena.query.QueryExecutionFactory;
import org.apache.jena.query.QueryFactory;
import org.apache.jena.query.QuerySolution;
import org.apache.jena.query.ResultSet;
import org.apache.jena.query.ResultSetFormatter;


public class dbpediaGetGenericDrug{

//	public static void printStatements(Model m, Resource s, Property p, Resource o) {
//		for (StmtIterator i = m.listStatements(s, p, o); i.hasNext();) {
//			Statement stmt = i.nextStatement();
//			System.out.println(" - " + PrintUtil.print(stmt));
//		}
//	}

	@SuppressWarnings("null")
	public static void main(String[] args) throws IOException {
		//String dbPedia = "http://www.dbpedia.org/";		
		
	      BufferedReader br = new BufferedReader(new
	                              InputStreamReader(System.in));
	      String userInput;
	      System.out.println("Enter drug name:");
	          userInput = (String) br.readLine();
	          System.out.println("You entered : " + userInput);
	     	
		String ontology_service = "http://dbpedia.org/sparql";
		System.out.println(userInput);
		String endpointSparql = 
		"		PREFIX dbpedia: <http://dbpedia.org/resource/> \n"
		+"		PREFIX dbo: <http://dbpedia.org/ontology/>	\n"
		+"		PREFIX dbp: <http://dbpedia.org/property/> \n"
		+"		PREFIX dbr: <http://dbpedia.org/resource/> \n"
		+"		PREFIX rdf: <http://www.w3.org/1999/02/22-rdf-syntax-ns#>\n"
		+"		PREFIX rdfs: <http://www.w3.org/2000/01/rdf-schema#>\n"

		+"			SELECT DISTINCT ?drugName \n"
		+"			WHERE {\n"
		+"			   ?drugName dbo:wikiPageRedirects ?genericDrug .\n"
		+"			   ?genericDrug rdfs:label ?label ;\n"
		+"			        rdf:type dbo:Drug . \n"
		+"			    FILTER(lang(?label)=\"en\"&& regex(?genericDrug,\".*"+userInput+"$\"))\n"
		
					    
		+"			} limit 1000\n";
	//	System.out.println(endpointSparql);
		System.out.println("the alternatives drugs are:");
		Query query = QueryFactory.create(endpointSparql);
		
		QueryExecution queryExe =
				QueryExecutionFactory.sparqlService(ontology_service, query);    //endpoint search query
		
		ResultSet results = queryExe.execSelect();
		List<QuerySolution> listResult = ResultSetFormatter.toList(results);    //format result as list
		int listResultSize = listResult.size();
		String[] PreResults = new String[listResultSize] ;
		String[] extractedResults = new String[listResultSize];
		for(int i=0;i<listResultSize;i++)
		{
			// extractedResults[i] =listResult.get(i).toString();
			 
			 Pattern pattern = Pattern.compile(".*/");
			 Matcher matcher = pattern.matcher(listResult.get(i).toString());
			
			 if (matcher.find())
			 {
				 PreResults[i]= matcher.group(0);
			     
			 }
			 int startIndex = PreResults[i].length();
			 int endIndex = listResult.get(i).toString().length();
			 extractedResults[i]=(String) listResult.get(i).toString().subSequence(startIndex, endIndex-3);	 
			 System.out.println(extractedResults[i]);
		}
	//  System.out.println(extractedResults); 
	//	System.out.println(listResult);	
		
	}
}

