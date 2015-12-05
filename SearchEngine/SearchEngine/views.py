#!/usr/bin/env python
# -*- coding: utf-8 -*-
from django.shortcuts import render
from SPARQLWrapper import SPARQLWrapper, JSON                 
import sys, re

#freebase MQL queries
import json
import settings 
import urllib


def index(request):
    return render(request, 'SearchEngine/index.html')

def search(request):
    search_term = request.GET['search_term']
    generics = []  #e.g. [{"name": "Ecotrin", "manufacturer": "", "is_product": False}, {"name": "Zoloft", "manufacturer": "GKS", "is_product": False}]

    # DBPedia (1) List of Brands
    sparql_dbpedia = SPARQLWrapper("http://dbpedia.org/sparql")
    sparql_dbpedia.setReturnFormat(JSON)

    # search_term is main item
    sparql_dbpedia.setQuery("""
        PREFIX dbo: <http://dbpedia.org/ontology/>
        PREFIX rdfs: <http://www.w3.org/2000/01/rdf-schema#>

        SELECT ?reference 
        WHERE { 
                ?reference dbo:wikiPageRedirects ?resource .
                ?resource rdfs:label "%s"@EN . 
            }
        LIMIT 500
        """ % (search_term))  

    results = sparql_dbpedia.query().convert()

    # IF search_term is not main item -> len(results["results"]["bindings"]}=0
    # Check if it´s a reference to main item
    if len(results["results"]["bindings"]) == 0:

        sparql_dbpedia.setQuery("""
        PREFIX dbo: <http://dbpedia.org/ontology/>
        PREFIX rdfs: <http://www.w3.org/2000/01/rdf-schema#>

        SELECT ?reference 
        WHERE { 
                ?reference dbo:wikiPageRedirects ?reference2 .
                {
                    SELECT ?reference2 
                    WHERE { 
                        ?resource dbo:wikiPageRedirects ?reference2. 
                        ?resource rdfs:label "%s"@EN . 
                    }
                }
            }
        LIMIT 500
        """ % (search_term))  

        results = sparql_dbpedia.query().convert()


    # FreeBase Query settings
    api_key = settings.api_key
    service_url = 'https://www.googleapis.com/freebase/v1/mqlread'

    for result in results["results"]["bindings"]:
        name = result["reference"]["value"].split("/")[-1]

        if bool(re.search("_code_|_acid|_", name)): # filter chemical formuals "_acid" and APH codes "_code_"
            continue

        # Check if it´s a drug brand
        query = [{
            "/type/object/type": "/medicine/drug_brand",
            "/type/object/name": name,
            "/business/brand/products": [],
            "/medicine/drug_brand/manufacturer": None,
            "/common/topic/official_website": None
        }]  

        
        params = {
                'query': json.dumps(query),
                'key': api_key
        }

        url = service_url + '?' + urllib.urlencode(params)
        response = json.loads(urllib.urlopen(url).read())

        if response["result"]:
            generics.append({
                                "name": name, 
                                "manufacturer": response["result"][0]["/medicine/drug_brand/manufacturer"],
                                "is_product": False, 
                                "website": response["result"][0]["/common/topic/official_website"],
                             })

        # Check for products of drug brand
            for product in response["result"][0]["/business/brand/products"]:
                query = [{
                    "/type/object/type": "/medicine/drug_manufacturer",
                    "/medicine/drug_manufacturer/drugs_manufactured": product,
                    "/type/object/name": None,
                    "/common/topic/official_website": None
                }]

                params = {
                        'query': json.dumps(query),
                        'key': api_key
                }

                url = service_url + '?' + urllib.urlencode(params)
                response2 = json.loads(urllib.urlopen(url).read())
                #print "++++++++++++++++++",name, ": Response2", response

                generics.append({
                    "name": product, 
                    "manufacturer": response2["result"][0]["/type/object/name"],
                    "is_product": True, 
                    "manu_website": response2["result"][0]["/common/topic/official_website"],
                 })
        else:
                # If it´s not a drug brand, check if it´s a drug alias
                query = [{
                    "/type/object/type": "/medicine/drug",
                    "/common/topic/alias": "Ecotrin"
                }]

                params = {
                        'query': json.dumps(query),
                        'key': api_key
                }

                url = service_url + '?' + urllib.urlencode(params)
                response2 = json.loads(urllib.urlopen(url).read())

                # alias exist -> add as generics, no additional information on FreeBase
                if response2["result"]:
                    generics.append({
                                "name": name, 
                                "is_product": False, 
                             })


    context = { "search_term" : search_term,
                "results": generics,
                "numbers": len(results["results"]["bindings"])}
    
    return render(request, 'SearchEngine/search.html', context)