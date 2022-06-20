package at.htl.test4;

import javafx.collections.ObservableList;
import javafx.concurrent.Service;
import javafx.concurrent.Task;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.scene.chart.AreaChart;
import javafx.scene.chart.NumberAxis;
import javafx.scene.chart.XYChart;
import javafx.scene.control.*;
import javafx.scene.paint.Color;
import javafx.stage.FileChooser;
import javafx.stage.Stage;
import javafx.util.converter.FormatStringConverter;

import java.io.File;
import java.text.DecimalFormat;
import java.util.List;
import java.util.stream.Stream;

public class DataViewerController {
    Stage stage;

    @FXML
    private Label lName;

    @FXML
    private TextField tfFilename;

    @FXML
    private Button btSelectFile;

    @FXML
    private ChoiceBox<String> cbGemeinde;

    @FXML
    private ProgressBar pbProgress;


    @FXML
    private AreaChart<?, ?> chart;

    @FXML
    private Label lStatus;

    Service<List<String>> svImportService;
    Service svLoadDataService;
    Service svLoadGesamtService;
    Service svImportIntoDb;

    FileChooser fileChooser = new FileChooser();

    File file;
    String gemeinde;
    String srcFile;


    @FXML
    public void initialize() {
        lName.setText("Peter Klose");

        chart.getXAxis().setLabel("Jahr");
        ((NumberAxis)chart.getXAxis()).setTickLabelFormatter(new FormatStringConverter<>(new DecimalFormat("0000")));
        chart.getYAxis().setLabel("Bewohner");

        svImportService = new Service() {
            @Override
            protected Task createTask() {
                Task task = new ImportTask(file);
                return task;
            }
        };

        svLoadDataService = new Service() {
            @Override
            protected Task createTask() {
                Task task = new LoadDataTask(gemeinde,srcFile);
                return task;
            }
        };
        svImportIntoDb = new Service() {
            @Override
            protected Task createTask() {
                Task task = new ImportIntoDbTask(srcFile);
                return task;
            }
        };
        svLoadGesamtService = new Service() {
            @Override
            protected Task createTask() {
                Task task = new LoadGesamtTask(srcFile);
                return task;
            }
        };
    }


    @FXML
    void importAction(ActionEvent event) {
        cbGemeinde.getItems().clear();
        file = fileChooser.showOpenDialog(null);

        pbProgress.progressProperty().bind(svImportService.progressProperty());
        statusAction("Importiere...");


        svImportService.setOnSucceeded(workerStateEvent -> {
            List<String> retunVal = svImportService.getValue();
            if(retunVal != null){
                cbGemeinde.getItems().add("Gesamt");
                cbGemeinde.getItems().addAll(retunVal);
                statusOk("Gemeindeliste wurde aktualisiert!");
            }else {
                statusError("Gemeindeliste wurde NICHT aktualisiert!");
            }
        });

        svImportService.restart();
    }


    @FXML
    void redrawAction(ActionEvent event) {

        statusAction("Aufbereiten der Daten...");
        gemeinde = cbGemeinde.getValue();

        chart.getData().clear();
        XYChart.Series dataSeries = new XYChart.Series();
        dataSeries.setName(gemeinde);

        if(!gemeinde.equals("Gesamt")){

        pbProgress.progressProperty().bind(svLoadDataService.progressProperty());
        svLoadDataService.restart();

        svLoadDataService.setOnSucceeded(workerStateEvent -> {
            // Task<XVChart.Series<Integer,Integer>>
            List<Integer> vals = (List<Integer>) svLoadDataService.getValue();
            if(vals != null){
                for (int i = 0; i < vals.size(); i+=2) {
                    dataSeries.getData().add(new XYChart.Data( vals.get(i), vals.get(i+1)));
                }
                statusOk("Chart aktualisiert!");
                chart.getData().add(dataSeries);

                addChartToolTips();
            }else {
                statusError("Chart NICHT aktualisiert!");
            }
        });
        }else {
            System.out.println("GESAMT");
            pbProgress.progressProperty().bind(svLoadGesamtService.progressProperty());

            svLoadGesamtService.restart();


        }



        // Beispiel-Sourcecode zum Demonstrieren der Funktionsweise



        /*dataSeries.getData().add(new XYChart.Data( 1980, 2000));
        dataSeries.getData().add(new XYChart.Data( 1990, 2020));
        dataSeries.getData().add(new XYChart.Data( 2000, 2100));*/

        // TODO: Aufgabe 5 - Gesamteinwohner für OÖ anzeigen

        // statusAction("Aufbereitung der Daten...");
        // statusOk("Chart aktualisiert!");



    }

    @FXML
    void dbImportAction(ActionEvent event) {

        statusAction("Datenbank-Import läuft...");

        pbProgress.progressProperty().bind(svImportIntoDb.progressProperty());
        svImportIntoDb.restart();

        svImportIntoDb.setOnSucceeded(workerStateEvent -> {
            List<Boolean> vals = (List<Boolean>) svImportIntoDb.getValue();
            if(vals != null){
                statusOk("DB-Import abgeschlossen!");
            }else {
                statusError("DB-Import NICHT aktualisiert!");
            }
        });


        // statusAction("Datenbank-Import läuft....");
        // statusOk("DB-Import abgeschlossen");
    }

    void addChartToolTips() {

        chart.getData().stream().forEach(series -> {
            series.getData().stream().forEach(data -> {
                Tooltip.install(data.getNode(), new Tooltip("Jahr " + data.getXValue() + ": " + data.getYValue() + " Einwohner"));
                data.getNode().setOnMouseEntered(mouseEvent -> data.getNode().getStyleClass().add("onHover"));
                data.getNode().setOnMouseExited(mouseEvent -> data.getNode().getStyleClass().remove("onHover"));
            });
        });




        /*for (int s=0; s<chart.getData().size(); s++) {
            XYChart.Series series = chart.getData().get(s);

            for (int d=0; d<series.getData().size(); d++) {

                XYChart.Data data = (XYChart.Data)series.getData().get(d);

                Tooltip.install(data.getNode(), new Tooltip("Jahr " + data.getXValue() + ": " + data.getYValue() + " Einwohner"));
                data.getNode().setOnMouseEntered(mouseEvent -> data.getNode().getStyleClass().add("onHover"));
                data.getNode().setOnMouseExited(mouseEvent -> data.getNode().getStyleClass().remove("onHover"));
            }
        }*/

    }


    //<editor-fold desc="//Ready methods.... no need to change...">
    @FXML
    void selectFileAction(ActionEvent event) {
        FileChooser fc = new FileChooser();
        fc.setTitle("CSV-Datei auswählen...");
        fc.getExtensionFilters().add(new FileChooser.ExtensionFilter("CSV-Datei", "*.csv"));
        fc.setInitialDirectory(new File("."));
        File file = fc.showOpenDialog(stage);
        if (file != null)
            srcFile = file.getAbsolutePath();
            tfFilename.setText(srcFile);
    }
    //</editor-fold>


    //<editor-fold desc="// Helper Methods for Status-Messages">
    void statusOk(String text) {
        lStatus.setTextFill(Color.GREEN);
        lStatus.setText(text);
    }

    void statusError(String text) {
        lStatus.setTextFill(Color.RED);
        lStatus.setText(text);
    }

    void statusAction(String text) {
        lStatus.setTextFill(Color.BLACK);
        lStatus.setText(text);
    }
    //</editor-fold>
}