package knowledgeBases;

import org.apache.jena.query.Query;
import org.apache.jena.query.QueryExecution;
import org.apache.jena.query.QueryExecutionFactory;
import org.apache.jena.query.QueryFactory;
import org.apache.jena.query.ResultSet;
import org.apache.jena.query.ResultSetFormatter;

//another query
//+"	SELECT DISTINCT ?drugManufactureNname ?BrandName 		"
//+"	WHERE {				"	
//+"	      ?drugMa fb:medicine.drug_manufacturer.drugs_manufactured  ?drugBrandId .  "
//+"	      ?drugMa fb:type.object.name ?drugManufactureNname .  "
//+"	       ?drugMa  a dbo:Company .  "
//+"	     ?drugBrandId fb:type.object.name ?BrandName .  "
//+"	     ?drugBrandId fb:medicine.manufactured_drug_form.manufactured_form_of ?drugFormulationId .  "
//+"	      ?drugFormulationId fb:type.object.name ?FormulationName .  "
//+"	     ?drugFormulationId fb:medicine.drug_formulation.formulation_of dbpedia:Aspirin .  "
//+"	  		 FILTER(lang(?drugManufactureNname)=\"en\")  "    
//+"	} ORDER BY ?BrandName " ;


public class freebaseSearchManufacturer{

//	public static void printStatements(Model m, Resource s, Property p, Resource o) {
//		for (StmtIterator i = m.listStatements(s, p, o); i.hasNext();) {
//			Statement stmt = i.nextStatement();
//			System.out.println(" - " + PrintUtil.print(stmt));
//		}
//	}

	public static void main(String[] args) {
		//String dbPedia = "http://www.dbpedia.org/";		
		
		String ontology_service = "http://factforge.net/sparql";
		String generics = "Ecotrin";

		String endpointSparql = 
		" PREFIX fb: <http://rdf.freebase.com/ns/> \n "
		+"	PREFIX dbpedia: <http://dbpedia.org/resource/> \n "
		+"	PREFIX dbo: <http://dbpedia.org/ontology/> \n "
		+"	SELECT DISTINCT   ?genericManufactureName \n "
		+"	WHERE {\n "
		+"	   ?genericManufactureId fb:medicine.drug_manufacturer.drugs_manufactured  \n "
		+"	                                                        ?alternativeGenericsId .\n "
		+"	  ?genericManufactureId fb:type.object.name ?genericManufactureName .\n "
		+"	  {?alternativeGenericsId a fb:medicine.drug_brand  .}\n "
		+"	  UNION{\n "
		+"	  ?alternativeGenericsId fb:type.object.name ?alternativeGenericsBrandName.}\n "
		+"	   UNION{\n "
		+"	      ?alternativeGenericsId   a fb:medicine.drug .}\n "
		+"	   FILTER (regex(?alternativeGenericsBrandName, \".*"+generics+".*\") && lang(?genericManufactureName)=\"en\")\n "
		+"	  } LIMIT 20 "
		
		;

		System.out.println("the manufacturer of "+generics+":\n");
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

