package org.tchm.web;

import org.tchm.database.DBTasks;

import java.io.*;
import java.net.MalformedURLException;
import java.net.URL;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.StandardCopyOption;
import java.sql.Connection;
import java.util.ArrayList;
import java.util.List;

public class DocDownloader {

    public void saveSingleFile(String link, Path destination) {

        URL url = null;
        try {
            url = new URL(link);
        } catch (MalformedURLException e) {
            e.printStackTrace();
        }
        try (InputStream in = url.openStream()) {
            Files.copy(in, destination, StandardCopyOption.REPLACE_EXISTING);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public void downloadCSV() {
        String fileVersion = "archiwum_tab_a_20";

        for(int i = 0;i<5;i++) {
            String link = ("http://www.nbp.pl/kursy/Archiwum/"+fileVersion+(13+i)+".csv");
            saveSingleFile(link,  new File("res/files/csv/"+fileVersion+(13+i)+".csv").toPath());
        }
    }

    public List<String> downloadNewXml(Connection c) {
        String fileName;
        String lastDay;
        URL url = null;
        try {
            url = new URL("http://www.nbp.pl/kursy/xml/dir.txt");
        } catch (MalformedURLException e) {
            e.printStackTrace();
        }
        Boolean filesAreMissing = false;
        List<String> fileList = new ArrayList<>();
        DBTasks dbTasks = new DBTasks();
        lastDay = dbTasks.getLastDay(c);
        System.out.println(lastDay);
        if(lastDay != null) {
            lastDay = lastDay.replace("-", "").substring(2);
            System.out.println(lastDay);
        }
        try (BufferedReader br = new BufferedReader(new InputStreamReader(url.openStream()))) {
            while ((fileName = br.readLine()) != null) {
                if(fileName.startsWith("a")&& fileName.substring(6).equals(lastDay)) {
                    filesAreMissing = true;
                    continue;
                } else if(lastDay==null&&filesAreMissing==false){
                    filesAreMissing=true;
                }
                if(fileName.startsWith("a")&&filesAreMissing){
                    fileList.add(fileName);
                    System.out.println(fileName);
                }
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
        return fileList;
    }

}
