package knowledgeBases;

import org.apache.jena.query.Query;
import org.apache.jena.query.QueryExecution;
import org.apache.jena.query.QueryExecutionFactory;
import org.apache.jena.query.QueryFactory;
import org.apache.jena.query.ResultSet;
import org.apache.jena.query.ResultSetFormatter;
import org.apache.jena.rdf.model.Model;
import org.apache.jena.rdf.model.ModelFactory;

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
		String genericDrug = "Aspirin";

		String endpointSparql = 
		" PREFIX fb: <http://rdf.freebase.com/ns/> "
		+"	PREFIX dbpedia: <http://dbpedia.org/resource/> "
		+"	PREFIX dbo: <http://dbpedia.org/ontology/> "
//		+"	SELECT DISTINCT ?drugManufactureNname ?BrandName 		"
//		+"	WHERE {				"	
//		+"	      ?drugMa fb:medicine.drug_manufacturer.drugs_manufactured  ?drugBrandId .  "
//		+"	      ?drugMa fb:type.object.name ?drugManufactureNname .  "
//		+"	       ?drugMa  a dbo:Company .  "
//		+"	     ?drugBrandId fb:type.object.name ?BrandName .  "
//		+"	     ?drugBrandId fb:medicine.manufactured_drug_form.manufactured_form_of ?drugFormulationId .  "
//		+"	      ?drugFormulationId fb:type.object.name ?FormulationName .  "
//		+"	     ?drugFormulationId fb:medicine.drug_formulation.formulation_of dbpedia:Aspirin .  "
//		+"	  		 FILTER(lang(?drugManufactureNname)=\"en\")  "    
//		+"	} ORDER BY ?BrandName " ;
			+"		PREFIX dbo: <http://dbpedia.org/ontology/>"
			+"		SELECT DISTINCT  ?drugManufactuerName ?drugBrandName "
			+"		WHERE { "
			+"		       ?drugBrandId fb:medicine.manufactured_drug_form.manufacturer ?drugManufactuerId  ;"
			+"		                     fb:type.object.name ?drugBrandName ;"
			+"		                      fb:common.topic.notable_types fb:medicine.manufactured_drug_form ;"
			+"		                    fb:medicine.manufactured_drug_form.manufactured_form_of ?drugId ."
			+"		    ?drugManufactuerId fb:type.object.name ?drugManufactuerName ."
			+"		              ?drugId    fb:medicine.drug_formulation.drug_category  dbpedia:Drug ;"
			+"		                          fb:type.object.name ?drugName .  "
			+"		  FILTER (regex(?drugName , \".*"+genericDrug+"\") && lang(?drugManufactuerName)=\"en\" ) "
			+"		} ORDER BY ?drugBrandName";

		System.out.println(endpointSparql);
		Query query = QueryFactory.create(endpointSparql);
		
		Model dbModel = ModelFactory.createDefaultModel();
		
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

