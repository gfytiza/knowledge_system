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


public class stopReader {
    private Model model;
    private static final String SAMPLE_CSV_FILE_PATH = "./stops.txt";
    OutputStream out1 = new FileOutputStream("stops-data.ttl");

    public stopReader() throws FileNotFoundException {
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
        String nsGEO = "<http://www.openlinksw.com/schemas/virtrdf#Geometry>";


        addStatement(ns+nextRecord[0], nsRDFS+"type", ns+"stops");
        Resource route = model.getResource(ns+nextRecord[0]);
        route.addLiteral(model.createProperty(ns+"stop_name"), model.createTypedLiteral(new String(   nextRecord[2] )));
        route.addLiteral(model.createProperty(ns+"stop_desc"), model.createTypedLiteral(new String(  nextRecord[3] )));
        // Replace the sybol @ with ^^ after in the editor
        route.addLiteral(model.createProperty(ns+"stop_loc"), model.createLiteral("point("+nextRecord[4]+" "+nextRecord[5]+")",nsGEO));
    }

    public static void main(String[] args) throws IOException {
        BasicConfigurator.configure();
        stopReader dave = new stopReader();
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

