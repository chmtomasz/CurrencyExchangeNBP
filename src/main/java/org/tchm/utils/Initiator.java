package org.tchm.utils;

import org.tchm.database.DBConnector;
import org.tchm.database.DBTasks;
import org.tchm.readFiles.CSVReader;
import org.tchm.web.DocDownloader;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.sql.Connection;

public class Initiator {

    public static void initData(){
        try {
            Files.createDirectories(Paths.get("res/files/csv"));
            Files.createDirectories(Paths.get("res/files/xml"));
            Files.createDirectories(Paths.get("res/files/archive/csv"));
            Files.createDirectories(Paths.get("res/files/archive/xml"));
            Files.createDirectories(Paths.get("res/files/importFilesHere"));
        } catch (IOException e) {
            e.printStackTrace();
        }
        DBConnector dbConnector = new DBConnector();
        Connection c = dbConnector.connectToDatabase();
        DBTasks dbTasks = new DBTasks();
        dbTasks.createTable(c);
        if(dbTasks.checkIfTableEmpty(c)){
            DocDownloader docDownloader = new DocDownloader();
            docDownloader.downloadCSV();
            CSVReader csvReader = new CSVReader();
            csvReader.readCSV();
        }

    }

}
