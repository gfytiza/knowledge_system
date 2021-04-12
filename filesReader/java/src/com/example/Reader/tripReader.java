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

public class tripReader {
    private Model model;
    private static final String SAMPLE_CSV_FILE_PATH = "./trips.txt";
    OutputStream out1 = new FileOutputStream("trips-data.ttl");

    public tripReader() throws FileNotFoundException {
    }

    public void writeModel() {
        model.write(System.out, "TTL");
        RDFDataMgr.write(out1, model, Lang.TURTLE);
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

        addStatement(ns+nextRecord[2], nsRDFS+"type", ns+"trips");
        addStatement(ns+nextRecord[2], ns+"fromService", ns+nextRecord[1]);
        addStatement(ns+nextRecord[2], ns+"fromRoute", ns+nextRecord[0]);
        Resource route = model.getResource(ns+nextRecord[2]);
        route.addLiteral(model.createProperty(ns+"trip_headsign"), model.createTypedLiteral(new String(   nextRecord[3] )));
        route.addLiteral(model.createProperty(ns+"route_direction"), model.createTypedLiteral(new Integer(  nextRecord[4] )));
    }

    public static void main(String[] args) throws IOException {
        BasicConfigurator.configure();
        tripReader dave = new tripReader();
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
