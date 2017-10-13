package org.tchm.readFiles;

import org.tchm.database.DBConnector;
import org.tchm.database.DBTasks;

import java.io.IOException;
import java.nio.file.*;
import java.sql.Connection;
import java.util.List;

import static java.nio.file.StandardWatchEventKinds.ENTRY_CREATE;

public class DirectoryListener {

    private WatchService watcher;

    public void watchDirectory(Path directory) throws IOException {
        watcher = FileSystems.getDefault().newWatchService();
        directory.register(watcher, ENTRY_CREATE);
        CSVReader csvReader = new CSVReader();
        DBConnector dbConnector = new DBConnector();
        Connection c = dbConnector.connectToDatabase();
        XMLReader xmlReader = new XMLReader();
        DBTasks dbTasks = new DBTasks();
        while (true) {
            WatchKey key;
            try {
                key = watcher.take();
            } catch (InterruptedException x) {
                return;
            }

            for (WatchEvent<?> event: key.pollEvents()) {

                WatchEvent<Path> ev = (WatchEvent<Path>)event;
                Path filename = ev.context();

                if(filename.endsWith(".csv")){
                    csvReader.readSingleCSV(filename.toFile(),c);
                } else if (filename.endsWith(".xml")){
                    List<String[]> files = xmlReader.readXML(filename.toFile());
                    for(String[] singleLine : files){
                        dbTasks.insertSingleCurrency(c,singleLine[0],singleLine[1],singleLine[2],singleLine[3],singleLine[4]);
                    }
                }
                System.out.println(filename);
            }
            boolean valid = key.reset();
            if (!valid) {
                break;
            }
        }
    }
}
