package org.tchm.view;

import javafx.collections.ObservableList;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.Scene;
import javafx.scene.chart.CategoryAxis;
import javafx.scene.chart.LineChart;

import javafx.scene.chart.NumberAxis;
import javafx.scene.chart.XYChart;
import javafx.scene.control.*;
import javafx.scene.layout.GridPane;
import javafx.scene.text.Font;
import javafx.scene.text.FontWeight;
import javafx.scene.text.Text;
import org.tchm.currency.Currency;
import org.tchm.database.DBTasks;
import org.tchm.readFiles.XMLReader;
import org.tchm.utils.Initiator;
import org.tchm.web.DocDownloader;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.nio.file.StandardCopyOption;
import java.sql.Connection;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.time.LocalDate;
import java.util.*;

public class MainView {

    public LocalDate dateStart;
    public LocalDate dateEnd;
    public String valueCode = "EUR";
    public List<Currency> cur;
    public XYChart.Series series;
    public XYChart.Series predictedLine;
    public XYChart.Series trendLine;
    public DatePicker startDate = new DatePicker();
    public DatePicker endDate = new DatePicker();
    public ListView currencyList;
    public double slope;
    public double offset;
    public CheckBox cbl;
    public LineChart<String,Number> lineChart;
    public ObservableList<String> observableList;
    public int predictedDaysNumber;
    DateFormat dateFormat= new SimpleDateFormat("yyyy-MM-dd");
    private DBTasks dbTasks= new DBTasks();

    public Scene setView(Connection c, List<String> allCurr){
        GridPane grid = new GridPane();
        grid.setAlignment(Pos.CENTER);
        grid.setHgap(10);
        grid.setVgap(10);
        grid.setPadding(new Insets(10, 20, 10, 20));
        startDate = new DatePicker();
        endDate = new DatePicker();
        final Button last30days = new Button("Last 30 days");
        final Button updateToDate = new Button("Update data to today");
        final Button refreshData = new Button("Refresh all data");
        cbl = new CheckBox("Add Trendline");
        currencyList = currencyList(allCurr);
        currencyList.getSelectionModel().setSelectionMode(SelectionMode.MULTIPLE);
        startDate.setValue(LocalDate.parse("2017-01-01"));
        endDate.setValue(LocalDate.now());
        this.dateStart = startDate.getValue();
        this.dateEnd = endDate.getValue();
        startDate.setOnAction(event -> {
            this.dateStart = startDate.getValue();
            if(this.dateStart.isAfter(this.dateEnd)){
                startDate.setValue(this.dateEnd);
            } else {
                updateChart(c);
            }
        });
        endDate.setOnAction(event -> {
            this.dateEnd = endDate.getValue();
            if(this.dateEnd.isBefore(this.dateStart)){
                endDate.setValue(this.dateStart);
            } else {
                updateChart(c);
            }
        });
        currencyList.setOnMouseClicked(event -> {
            this.observableList = currencyList.getSelectionModel().getSelectedItems();
            updateChart(c);
        });
        last30days.setOnAction(event -> {
            updateChart30Days();
        });
        updateToDate.setOnAction(event -> {
            updateToCurrentDate(c);
            updateChart(c);
        });
        refreshData.setOnAction(event -> {
            dbTasks.clearTable(c);
            Initiator.initData();
            updateChart(c);
        });
        Text title = new Text("NBP CURRENCY EXCHANGE");
        title.setFont(Font.font("Verdana", FontWeight.BOLD, 24));
        dbTasks = new DBTasks();
        this.cur = dbTasks.selectCurrency(c, this.dateStart.toString(),this.dateEnd.toString(),this.valueCode);
        TextField predictedDays = new TextField();
        predictedDays.textProperty().addListener((observable, oldValue, newValue) -> {
                System.out.println(oldValue+" "+newValue);
                if (!newValue.matches("\\d*")) {
                    predictedDays.setText(newValue.replaceAll("[^\\d]", ""));
                } else if (!newValue.equals("")) {
                    if (Integer.parseInt(newValue) > 30) {
                        predictedDays.setText(newValue = (("" + 30)));
                    }
                    this.predictedDaysNumber = Integer.parseInt(newValue);

                }


        });
        Label dateStartLabel = new Label("Date Start");
        Label dateEndLabel = new Label("Date End");
        Label currencyListLabel = new Label("Hold CTRL to select multiple currencies");
        Label predictedDaysLabel = new Label("Number of days to predict value (max 30)");
        grid.add(title, 2, 0,4,1);
        grid.add(dateStartLabel,2,1);
        grid.add(dateEndLabel,3,1);
        grid.add(startDate,2,2);
        grid.add(endDate,3,2);
        grid.add(predictedDaysLabel,1,6);
        grid.add(predictedDays, 1, 5);
        grid.add(addChart(),2,3,3,1);
        grid.add(currencyListLabel,1,1);
        grid.add(currencyList,1,2,1,3);
        grid.add(last30days,4,2);
        grid.add(updateToDate,3,5);
        grid.add(refreshData,4,5);
        grid.add(cbl,2,5);
        Scene scene = new Scene(grid, 800, 600);

        return scene;
    }

    public LineChart addChart(){

        final CategoryAxis xAxis = new CategoryAxis();
        final NumberAxis yAxis = new NumberAxis();
        yAxis.setLabel("PLN");
        yAxis.setForceZeroInRange(false);
        lineChart = new LineChart<>(xAxis,yAxis);
        this.series = new XYChart.Series();
        this.trendLine = new XYChart.Series();


        lineChart.setCreateSymbols(false);
        lineChart.setAnimated(false);
        lineChart.getData().add(series);
        lineChart.getData().add(trendLine);
        return lineChart;
    }

    public ListView currencyList(List<String> cur){
        ListView<String> list = new ListView<>();
        list.getItems().addAll(cur);
        return  list;
    }


    public void updateChart(Connection c){
        lineChart.getData().clear();
        List<String> codeValues = new ArrayList<>();
        if(observableList==null){
            codeValues.add("EUR");
        } else {
            for (String ob : observableList) {
                codeValues.add(ob);
            }
        }

        for(String cv : codeValues) {
            this.cur = dbTasks.selectCurrency(c, this.dateStart.toString(), this.dateEnd.toString(), cv);
            series = new XYChart.Series();
            trendLine = new XYChart.Series();
            if (predictedDaysNumber > 0) {
                predictedLine = new XYChart.Series();
                predictedLine.setName(cv+" prediction");
            }
            series.setName(cv);
            trendLine.setName(cv + " trendLine");
            int i = 0;
            int iteration = 0;
            if (this.cbl.isSelected()) {
                calculateTrendLine(cur);
            }
            Date lastDate = new Date();
            for (Currency cu : cur) {
                series.getData().add(new XYChart.Data(dateFormat.format(cu.c_date), cu.c_value));
                lastDate = cu.c_date;
                if (this.cbl.isSelected()) {
                    trendLine.getData().add(new XYChart.Data(dateFormat.format(cu.c_date), slope * (i++) + offset));
                }
            }

            if(predictedDaysNumber>0) {
                int startPoint = cur.size();
                Calendar cal = Calendar.getInstance();
                cal.setTime(lastDate);
                cal.add(Calendar.DATE, -predictedDaysNumber);
                dateFormat.format(cal.getTime());
                calculateTrendLine(cur);
                this.cur = dbTasks.selectCurrency(c, dateFormat.format(cal.getTime()), dateFormat.format(lastDate), cv);

                cal.setTime(lastDate);
                int j = 1;
                Collections.reverse(cur);
                for (Currency cu : cur) {
                    iteration++;
                    cal.add(Calendar.DATE, j);
                    if (iteration == 1) {
                        predictedLine.getData().add(new XYChart.Data(dateFormat.format(lastDate), cu.c_value));
                    } else {
                        predictedLine.getData().add(new XYChart.Data(dateFormat.format(cal.getTime()), ((slope * (startPoint++) + offset) + cu.c_value) / 2));
                    }
                }
            }


            lineChart.getData().add(series);
            if (this.cbl.isSelected()) {
                lineChart.getData().add(trendLine);
            }
            if (predictedDaysNumber > 0) {

                lineChart.getData().add(predictedLine);
            }
        }
    }

    public void updateChart30Days(){
        LocalDate today = LocalDate.now();
        LocalDate daysAgo30 = today.minusDays( 30 );
        startDate.setValue(daysAgo30);
        endDate.setValue(today);
    }

    public void updateToCurrentDate(Connection c){
        DocDownloader docDownloader = new DocDownloader();
        XMLReader xmlReader = new XMLReader();

            List<String> filesList = docDownloader.downloadNewXml(c);
            for(String fileName : filesList){
                fileName = fileName+".xml";
                docDownloader.saveSingleFile("http://www.nbp.pl/kursy/xml/"+fileName,new File("res/files/xml/" + fileName).toPath());
            }

        try {
            Files.list(new File("res/files/xml").toPath()).forEach(path -> {
                File file = path.toFile();
                List<String[]> data = xmlReader.readXML(file);
                for(String[] singleLine : data){
                      dbTasks.insertSingleCurrency(c,singleLine[0],singleLine[1],singleLine[2],singleLine[3],singleLine[4]);
                }
                Path targetPath = Paths.get("res/files/archive/xml/");
                try {
                    Files.move(path,targetPath.resolve(file.getName()), StandardCopyOption.REPLACE_EXISTING);
                } catch (IOException e) {
                    e.printStackTrace();
                }
            });
        } catch (IOException e) {
            e.printStackTrace();
        }

        currencyList.getItems().setAll(dbTasks.selectSingleCurrency(c));
    }

    public void calculateTrendLine(List<Currency> cur){
        int i = 0;
        int totalRecords = cur.size();
        double sumXY = 0;
        double sumXSquare= 0;
        double sumX= 0;
        double sumY= 0;
        for(Currency cu : cur){
            i++;
            sumXY +=i*cu.c_value;
            sumXSquare += i*i;
            sumX += i;
            sumY += cu.c_value;
        }
        this.slope = (totalRecords*sumXY-sumX*sumY)/(totalRecords*sumXSquare-(sumX*sumX));
        this.offset = (sumY - slope*sumX)/totalRecords;
    }

}
