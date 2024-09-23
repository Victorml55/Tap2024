package com.example.tap2024.vistas;
import javafx.application.Platform;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.Scene;
import javafx.scene.control.Alert;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.layout.GridPane;
import javafx.scene.layout.HBox;
import javafx.scene.layout.StackPane;
import javafx.scene.layout.VBox;
import javafx.stage.Stage;
import org.kordamp.bootstrapfx.BootstrapFX;
import org.kordamp.bootstrapfx.scene.layout.Panel;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Random;
import java.util.Timer;
import java.util.TimerTask;

public class Loteria extends Stage {

    private HBox hBoxMain, hBoxButtons;
    private VBox vbxTablilla, vbxMazo;
    private Button btnAnterior, btnSiguiente, btnIniciar, btnFinalizar;
    private Label lblTimer;
    private GridPane gdpTablilla;
    private ImageView imvMazo;
    private Scene escena;
    private String[] arImages = {"1.jpg","2.jpg","3.jpg","4.jpg","5.jpg","6.jpg","7.jpg","8.jpg","9.jpg","10.jpg","11.jpg","12.jpg","13.jpg","14.jpg","15.jpg","16.jpg","17.jpg","18.jpg","19.jpg","20.jpg","21.jpg","22.jpg","23.jpg","24.jpg","25.jpg","26.jpg","27.jpg","28.jpg","29.jpg","30.jpg","31.jpg","32.jpg","33.jpg","34.jpg","35.jpg","36.jpg","37.jpg","38.jpg","39.jpg","40.jpg","41.jpg","42.jpg","43.jpg","44.jpg","45.jpg","46.jpg","47.jpg","48.jpg","49.jpg","50.jpg","51.jpg","52.jpg","53.jpg","54.jpg"};
    private Button[][] arBtnTab;
    private Timer timerGeneral, timerMazo;
    private int plantillaActual = 0;
    private List<Button[][]> plantillas;
    private TimerTask timerTaskGeneral, timerTaskMazo;
    private List<String> cartasDisponibles;
    private List<String> cartasMazo;
    private boolean juegoIniciado = false;
    private int segundosTotales = 0;

    private Panel pnlPrincipal;

    public Loteria(){
        CrearUI();

        this.setTitle("Lotería de México");
        this.setScene(escena);
        this.show();
    }

    private void CrearUI(){
        ImageView imvAnt, imvSig;
        imvAnt = new ImageView(new Image(getClass().getResource("/images/izquierda.png").toString()));
        imvAnt.setFitHeight(40);
        imvAnt.setFitWidth(40);
        imvSig = new ImageView(new Image(getClass().getResource("/images/derecha.png").toString()));
        imvSig.setFitWidth(40);
        imvSig.setFitHeight(40);

        gdpTablilla = new GridPane();
        CrearPlantillas();

        btnAnterior = new Button();
        btnAnterior.setGraphic(imvAnt);
        btnAnterior.setPrefSize(80, 40); // Cambiado el tamaño del botón

        btnSiguiente = new Button();
        btnSiguiente.setGraphic(imvSig);
        btnSiguiente.setPrefSize(80, 40);

        btnIniciar = new Button("Iniciar");
        btnIniciar.getStyleClass().setAll("btn-sm", "btn-info");

        btnFinalizar = new Button("Finalizar");
        btnFinalizar.getStyleClass().setAll("btn-sm", "btn-dark");
        btnFinalizar.setDisable(true);

        hBoxButtons = new HBox(10, btnAnterior, btnSiguiente, btnIniciar, btnFinalizar); // Ajustado el espacio entre botones
        hBoxButtons.setAlignment(Pos.CENTER); // Centrado los botones

        vbxTablilla = new VBox(20, gdpTablilla, hBoxButtons); // Aumentado el espacio entre la tablilla y los botones
        vbxTablilla.setPadding(new Insets(15));

        CrearMazo();

        hBoxMain = new HBox(30, vbxTablilla, vbxMazo); // Aumentado el espacio entre los elementos principales
        hBoxMain.setPadding(new Insets(25)); // Ajustado el padding para mayor separación visual

        pnlPrincipal = new Panel("Lotería de México");
        pnlPrincipal.getStyleClass().add("panel-primary");
        pnlPrincipal.setBody(hBoxMain);

        escena = new Scene(pnlPrincipal, 850, 650);
        escena.getStylesheets().add(BootstrapFX.bootstrapFXStylesheet());
        escena.getStylesheets().add(getClass().getResource("/styles/loteria.css").toExternalForm());

        // Acciones de botones
        btnAnterior.setOnAction(e -> {
            if (!juegoIniciado) {
                plantillaActual = (plantillaActual == 0) ? plantillas.size() - 1 : plantillaActual - 1;
                mostrarPlantilla(plantillaActual);
            }
        });

        btnSiguiente.setOnAction(e -> {
            if (!juegoIniciado) {
                plantillaActual = (plantillaActual == plantillas.size() - 1) ? 0 : plantillaActual + 1;
                mostrarPlantilla(plantillaActual);
            }
        });

        btnIniciar.setOnAction(e -> {
            iniciarJuego();
            btnIniciar.setDisable(true);
            btnFinalizar.setDisable(false);
            btnAnterior.setDisable(true);
            btnSiguiente.setDisable(true);
            juegoIniciado = true;
        });

        btnFinalizar.setOnAction(e -> finalizarJuego());
    }

    private void CrearMazo() {
        lblTimer = new Label("00:00");
        lblTimer.setStyle("-fx-font-size: 24px; -fx-font-weight: bold;"); // Mayor tamaño de texto y negritas

        imvMazo = new ImageView(getClass().getResource("/images/dorso.jpeg").toString());
        imvMazo.setFitHeight(420);
        imvMazo.setFitWidth(280);

        vbxMazo = new VBox(15, lblTimer, imvMazo); // Ajustado el espacio entre el timer y el mazo
        vbxMazo.setAlignment(Pos.CENTER); // Centrado el contenido verticalmente
    }

    private void CrearPlantillas() {
        plantillas = new ArrayList<>();
        cartasDisponibles = new ArrayList<>(List.of(arImages));

        for (int p = 0; p < 5; p++) {
            arBtnTab = new Button[4][4];
            List<String> cartasRevueltas = new ArrayList<>(cartasDisponibles);
            Collections.shuffle(cartasRevueltas);

            GridPane gridPane = new GridPane();
            gridPane.setHgap(10); // Espacio horizontal entre cartas
            gridPane.setVgap(10); // Espacio vertical entre cartas

            for (int i = 0; i < 4; i++) {
                for (int j = 0; j < 4; j++) {
                    String carta = cartasRevueltas.get(i * 4 + j);
                    Image img = new Image(getClass().getResource("/images/" + carta).toString());
                    ImageView imv = new ImageView(img);
                    imv.setFitWidth(80);
                    imv.setFitHeight(130);

                    StackPane stackPane = new StackPane();
                    stackPane.getChildren().add(imv);

                    Label lblMarcador = new Label("X");
                    lblMarcador.getStyleClass().add("carta-marcador");
                    lblMarcador.setVisible(false);
                    stackPane.getChildren().add(lblMarcador);

                    arBtnTab[j][i] = new Button();
                    arBtnTab[j][i].setGraphic(stackPane);
                    gridPane.add(arBtnTab[j][i], j, i);

                    int finalI = i, finalJ = j;
                    String finalCarta = carta;
                    arBtnTab[j][i].setOnAction(e -> {
                        if (juegoIniciado) {
                            marcarCarta(stackPane, lblMarcador, finalCarta);
                        }
                    });
                }
            }
            plantillas.add(arBtnTab);
        }

        mostrarPlantilla(0);
    }

    private void mostrarPlantilla(int index) {
        gdpTablilla.getChildren().clear();
        Button[][] plantilla = plantillas.get(index);
        for (int i = 0; i < 4; i++) {
            for (int j = 0; j < 4; j++) {
                gdpTablilla.add(plantilla[j][i], j, i);
            }
        }
    }

    private void marcarCarta(StackPane stackPane, Label lblMarcador, String cartaDeLaPlantilla) {
        String imagenActualMazoNombre = imvMazo.getImage().getUrl().substring(imvMazo.getImage().getUrl().lastIndexOf("/") + 1);

        if (cartaDeLaPlantilla.equals(imagenActualMazoNombre)) {
            lblMarcador.setVisible(true);
            ((Button) stackPane.getParent().getParent()).setDisable(true);
            verificarVictoria();
        }
    }

    private void iniciarJuego() {
        if (timerGeneral != null) {
            timerGeneral.cancel();
        }
        if (timerMazo != null) {
            timerMazo.cancel();
        }

        cartasMazo = new ArrayList<>(List.of(arImages));
        Collections.shuffle(cartasMazo);

        segundosTotales = 0;
        lblTimer.setText("00:00");

        timerTaskGeneral = new TimerTask() {
            @Override
            public void run() {
                Platform.runLater(() -> {
                    segundosTotales++;
                    int minutos = segundosTotales / 60;
                    int segundos = segundosTotales % 60;
                    lblTimer.setText(String.format("%02d:%02d", minutos, segundos));
                });
            }
        };

        timerTaskMazo = new TimerTask() {
            @Override
            public void run() {
                Platform.runLater(() -> {
                    if (!cartasMazo.isEmpty()) {
                        String cartaSiguiente = cartasMazo.remove(0);
                        imvMazo.setImage(new Image(getClass().getResource("/images/" + cartaSiguiente).toString()));
                    } else {
                        finalizarJuego();
                    }
                });
            }
        };

        timerGeneral = new Timer();
        timerGeneral.scheduleAtFixedRate(timerTaskGeneral, 1000, 1000);

        timerMazo = new Timer();
        timerMazo.scheduleAtFixedRate(timerTaskMazo, 5000, 5000);
    }

    private void finalizarJuego() {
        if (timerGeneral != null) {
            timerGeneral.cancel();
        }
        if (timerMazo != null) {
            timerMazo.cancel();
        }

        Alert alert = new Alert(Alert.AlertType.INFORMATION);
        alert.setTitle("Juego terminado");
        alert.setHeaderText("Fin del juego");
        alert.setContentText("Gracias por jugar");
        alert.showAndWait();

        juegoIniciado = false;
        btnIniciar.setDisable(false);
        btnFinalizar.setDisable(true);
        btnAnterior.setDisable(false);
        btnSiguiente.setDisable(false);
    }

    private void verificarVictoria() {
        boolean victoria = true;
        for (Button[] fila : plantillas.get(plantillaActual)) {
            for (Button btn : fila) {
                StackPane sp = (StackPane) btn.getGraphic();
                Label lblMarcador = (Label) sp.getChildren().get(1);
                if (!lblMarcador.isVisible()) {
                    victoria = false;
                    break;
                }
            }
        }

        if (victoria) {
            finalizarJuego();
        }
    }
}
