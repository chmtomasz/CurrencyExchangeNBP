package org.tchm.graph;

import javafx.scene.chart.CategoryAxis;
import javafx.scene.chart.LineChart;
import javafx.scene.chart.NumberAxis;
import javafx.scene.chart.XYChart;
import org.tchm.currency.Currency;

import java.util.List;

public class GraphMaker {

    public LineChart createChart(double width, double height) {

        double min = 0;
        double max = 10;

        //Tworzę wykres
        CategoryAxis xAxis = new CategoryAxis();
        NumberAxis yAxis = new NumberAxis(min, max, 0.1);
        LineChart<String, Number> currencyInTime = new LineChart<>(xAxis, yAxis);
        currencyInTime.setAnimated(false);
        currencyInTime.setCreateSymbols(false);
        currencyInTime.setLegendVisible(true);
        currencyInTime.setTitle("/PLN");
        currencyInTime.setMinWidth(0.95 * width);
        currencyInTime.setMinHeight(0.95 * height);

        XYChart.Series data = new XYChart.Series();
       // currencies.forEach(e -> data.getData().add(new XYChart.Data(e.getDate(), e.getValue())));

        data.setName("Historia Kursu");


        currencyInTime.getData().addAll(data);

        return currencyInTime;

    }


}
