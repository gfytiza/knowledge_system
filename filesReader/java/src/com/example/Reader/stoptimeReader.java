package com.example.Reader;

import com.opencsv.CSVReader;
import org.apache.jena.rdf.model.*;
import org.apache.jena.riot.Lang;
import org.apache.jena.riot.RDFDataMgr;
import org.apache.jena.vocabulary.RDFS;
import org.apache.log4j.*;

import java.io.*;
import java.nio.file.Files;
import java.nio.file.Paths;

public class stoptimeReader {
    private Model model;
    private static final String SAMPLE_CSV_FILE_PATH = "./stop_time.txt";
    OutputStream out = new FileOutputStream("stoptime-data.ttl");

    public stoptimeReader() throws FileNotFoundException {
    }

    public void writeModel() {
        model.write(System.out, "TTL");
        RDFDataMgr.write(out, model, Lang.TURTLE);
    }

    public void addStatement(String s, String p, String o){
        Resource subject = model.createResource(s);
        Property predicate = model.createProperty(p);
        RDFNode object = model.createResource(o);
        Statement stmt = model.createStatement(subject, predicate, object);
        model.add(stmt);
    }

    public void createModel(String [] nextRecord){
        model = ModelFactory.createDefaultModel();
        String ns = "http://www.georgiaftz.org/myontology/";
        String nsRDFS = "http://www.w3.org/2000/01/rdf-schema#";

        addStatement(ns+nextRecord[0], ns+"hasStop", ns+nextRecord[3]);
/*        Resource route = model.getResource(ns+nextRecord[0]);
        route.addLiteral(model.createProperty(ns+"route_short_name"), model.createTypedLiteral(new String(   nextRecord[1] )));
        route.addLiteral(model.createProperty(ns+"route_type"), model.createTypedLiteral(new Integer(  nextRecord[4] )));
        route.addLiteral(model.createProperty(ns+"route_long_name"), model.createTypedLiteral(new String( nextRecord[2]))); */
    }

    public static void main(String[] args) throws IOException {
        BasicConfigurator.configure();
        stoptimeReader dave = new stoptimeReader();
        Reader reader = Files.newBufferedReader(Paths.get(SAMPLE_CSV_FILE_PATH));
        CSVReader csvReader = new CSVReader(reader);
        // Reading Records One by One in a String array
        String[] nextRecord;
        nextRecord = csvReader.readNext();
        while ((nextRecord = csvReader.readNext()) != null) {
            dave.createModel(nextRecord);
            dave.writeModel();
        }
    }
}
