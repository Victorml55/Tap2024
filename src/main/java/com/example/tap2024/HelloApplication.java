package com.example.tap2024;
import com.example.tap2024.vistas.Calculadora;
import com.example.tap2024.vistas.Loteria;
import javafx.application.Application;
import javafx.scene.Scene;
import javafx.scene.control.Menu;
import javafx.scene.control.MenuBar;
import javafx.scene.control.MenuItem;
import javafx.scene.layout.BorderPane;
import javafx.stage.Stage;
import java.io.IOException;
import java.util.Objects;

public class HelloApplication extends Application {
    private BorderPane bdpPrincipal;
    private MenuBar menBar;
    private Menu menCompetencia1, menuCompetencia2, menSalir;
    private MenuItem mitCalculadora;
    private MenuItem mitLoteria;

    public void CrearUI(){
        mitCalculadora = new MenuItem("Calculadora");
        mitCalculadora.setOnAction(actionEvent -> new Calculadora());
        mitLoteria = new MenuItem("Loteria");
        mitLoteria.setOnAction(actionEvent -> new Loteria());
        menCompetencia1 = new Menu("Competencia 1");
        menCompetencia1.getItems().addAll(mitCalculadora,mitLoteria);
        menBar = new MenuBar(menCompetencia1);
        bdpPrincipal = new BorderPane();
        bdpPrincipal.setTop(menBar);
    }

    @Override
    public void start(Stage stage) throws IOException {
        CrearUI();
        Scene scene = new Scene(bdpPrincipal, 350, 250);
        scene.getStylesheets().add(Objects.requireNonNull(getClass().getResource("/Styles/main.css")).toExternalForm());
        stage.setTitle("Topicos Avanzados de Programacion");
        stage.setScene(scene);
        stage.setMaximized(true);
        stage.show();
    }
    
    public static void main(String... args) {
        launch();
    }
}