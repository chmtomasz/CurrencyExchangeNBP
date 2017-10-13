package org.tchm.main;

import javafx.application.Application;
import javafx.application.Platform;
import javafx.scene.Scene;
import javafx.stage.Stage;
import org.tchm.currency.Currency;
import org.tchm.database.DBConnector;
import org.tchm.database.DBTasks;
import org.tchm.readFiles.DirectoryListener;
import org.tchm.utils.Initiator;
import org.tchm.view.MainView;

import java.io.File;
import java.io.IOException;
import java.sql.Connection;
import java.util.List;

public class MainClass extends Application{
    static List<Currency> cur;
    static List<String> allCur;
    static MainView mainView;
    public static void main(String[] args){

        launch(args);

    }

    @Override
    public void start(Stage primaryStage) throws Exception {

        Initiator.initData();
        DBConnector dbConnector = new DBConnector();
        DBTasks dbTasks = new DBTasks();
        DirectoryListener directoryListener = new DirectoryListener();

        new Thread(new Runnable() {
            @Override
            public void run() {
                try {
                    directoryListener.watchDirectory(new File("res/files/importFilesHere").toPath());
                } catch (IOException e) {
                    e.printStackTrace();
                }

            }
        }, "Thread: Directory Watcher...").start();

        Connection c = dbConnector.connectToDatabase();

        allCur = dbTasks.selectSingleCurrency(c);
        primaryStage.setTitle("NBP CURRENCY");
        this.mainView = new MainView();

        Scene scene = mainView.setView(c,allCur);
        primaryStage.setScene(scene);
        primaryStage.show();
        primaryStage.setOnCloseRequest(e -> {
            Platform.exit();
            System.exit(0);
        });
    }
}
