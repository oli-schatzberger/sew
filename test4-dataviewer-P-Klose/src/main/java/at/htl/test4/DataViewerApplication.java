package at.htl.test4;

import javafx.application.Application;
import javafx.fxml.FXMLLoader;
import javafx.scene.Scene;
import javafx.stage.Stage;

import java.io.IOException;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;

public class DataViewerApplication extends Application {
    static Connection conn;
    @Override
    public void start(Stage stage) throws IOException {
        FXMLLoader fxmlLoader = new FXMLLoader(DataViewerApplication.class.getResource("dataviewer-view.fxml"));
        Scene scene = new Scene(fxmlLoader.load(), 800, 600);
        scene.getStylesheets().add(DataViewerApplication.class.getResource("chart.css").toExternalForm());
        DataViewerController controller = fxmlLoader.getController();
        controller.stage = stage;
        stage.setTitle("DataViewer");
        stage.setScene(scene);
        stage.show();
    }

    public static void main(String[] args) throws SQLException {
        try{
            Connection c = DriverManager.getConnection("jdbc:derby://localhost:1527/db;create=true", "app", "app");
            DataViewerApplication.conn = c;
            launch();
        } catch (SQLException e) {
            e.printStackTrace();
        }

    }
}