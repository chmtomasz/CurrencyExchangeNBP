package org.tchm.readFiles;

import org.tchm.database.DBConnector;
import org.tchm.database.DBTasks;

import java.io.*;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.nio.file.StandardCopyOption;
import java.sql.Connection;
import java.sql.Date;
import java.sql.PreparedStatement;
import java.sql.SQLException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.List;

public class CSVReader {

    public void readCSV()  {
        DBConnector dbConnector = new DBConnector();
        Connection c = dbConnector.connectToDatabase();
        Path targetPath = Paths.get("res/files/archive/csv/");
        try {
            Files.list(new File("res/files/csv").toPath()).forEach(path -> {
                File file = path.toFile();
                readSingleCSV(file,c);
                try {
                    Files.move(path,targetPath.resolve(file.getName()), StandardCopyOption.REPLACE_EXISTING);
                } catch (IOException e) {
                    e.printStackTrace();
                }

            });
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public void readSingleCSV(File file, Connection c){
        String line;
        String[] temp;
        String[] codes = null;
        String[] descriptions = null;
        List<String[]> allCurrenciesValue = new ArrayList<>();
        try (BufferedReader br =   new BufferedReader(new InputStreamReader(new FileInputStream(file),"ISO-8859-2"))){
            line = br.readLine();
            temp = line.split(";");
            codes = temp;
            line = br.readLine();
            temp = line.split(";");
            descriptions = temp;
            while((line = br.readLine()) !=null) {
                temp = line.split(";");
                if(temp.length<2) {
                    break;
                }
                allCurrenciesValue.add(temp);
            }
        } catch (FileNotFoundException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        }
        DBTasks dbTasks = new DBTasks();
        dbTasks.batchInsert(c,codes,descriptions,allCurrenciesValue);
    }

}
