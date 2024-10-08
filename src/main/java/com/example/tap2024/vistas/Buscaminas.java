package com.example.tap2024.vistas;

import javafx.geometry.Pos;
import javafx.scene.Scene;
import javafx.scene.control.Alert;
import javafx.scene.control.Button;
import javafx.scene.control.TextField;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.input.MouseButton;
import javafx.scene.layout.GridPane;
import javafx.scene.layout.HBox;
import javafx.scene.layout.VBox;
import javafx.stage.Stage;

import java.util.Random;

public class Buscaminas extends Stage {

    private int gridSize = 10;
    private int bombCount = 10;
    private Button[][] buttons;
    private boolean[][] bombGrid;
    private boolean[][] revelar;
    private boolean[][] marcada;
    private Random random = new Random();
    private TextField bombInput;
    private Button generarButton;
    private GridPane grid;
    private VBox vBox;
    private HBox inputArea; // Nueva área para bombInput y generarButton
    private Scene escena;
    private Image minaImage;

    public Buscaminas() {
        CrearUI();
        this.setTitle("Buscaminas");
        this.setScene(escena);
        this.show();
    }

    private void CrearUI() {

        minaImage = new Image(getClass().getResourceAsStream("/images/mina.jpg"));

        // Campo de texto para ingresar cantidad de bombas
        bombInput = new TextField();
        bombInput.setPromptText("Cantidad de Bombas");
        bombInput.setPrefWidth(150); // Cambiar el tamaño del cuadro de texto

        // Botón para generar el campo de juego
        generarButton = new Button("Jugar");
        generarButton.setStyle("-fx-background-color: #3A86FF; -fx-text-fill: white;"); // Estilo personalizado
        generarButton.setOnAction(e -> {
            try {
                bombCount = Integer.parseInt(bombInput.getText());
                if (bombCount < 1 || bombCount >= gridSize * gridSize) {
                    throw new NumberFormatException("Cantidad inválida de bombas.");
                }
                inicializarCampo();
            } catch (NumberFormatException ex) {
                mostrarAlerta("Ingrese una cantidad válida de bombas.");
            }
        });

        // Añadimos bombInput y generarButton en un HBox
        inputArea = new HBox(10, bombInput, generarButton);
        inputArea.setAlignment(Pos.CENTER);
        inputArea.setStyle("-fx-padding: 10;");

        // GridPane para el campo de juego
        grid = new GridPane();
        grid.setAlignment(Pos.CENTER);
        grid.setStyle("-fx-padding: 20; -fx-background-color: #F0F0F0;");

        // VBox principal que contiene todo
        vBox = new VBox(20, inputArea, grid);
        vBox.setAlignment(Pos.TOP_CENTER);
        vBox.setStyle("-fx-padding: 20; -fx-background-color: #FFFFFF;");

        escena = new Scene(vBox, 500, 600); // Cambiamos el tamaño de la ventana
        escena.getStylesheets().add(getClass().getResource("/styles/buscaminas.css").toExternalForm());
    }

    private void inicializarCampo() {
        grid.getChildren().clear();
        buttons = new Button[gridSize][gridSize];
        bombGrid = new boolean[gridSize][gridSize];
        revelar = new boolean[gridSize][gridSize];
        marcada = new boolean[gridSize][gridSize];

        for (int i = 0; i < bombCount; i++) {
            int x, y;
            do {
                x = random.nextInt(gridSize);
                y = random.nextInt(gridSize);
            } while (bombGrid[x][y]);
            bombGrid[x][y] = true;
        }

        for (int x = 0; x < gridSize; x++) {
            for (int y = 0; y < gridSize; y++) {
                Button button = new Button();
                button.setPrefSize(40, 40); // Tamaño de las celdas más pequeño
                final int finalX = x;
                final int finalY = y;

                button.getStyleClass().add("mina-button");
                button.setOnMouseClicked(e -> {
                    if (e.getButton() == MouseButton.PRIMARY) {
                        descubrirCasilla(finalX, finalY);
                    }
                    if (e.getButton() == MouseButton.SECONDARY) {
                        marcarCasilla(finalX, finalY);
                    }
                });

                buttons[x][y] = button;
                grid.add(button, x, y);
            }
        }
    }

    private void descubrirCasilla(int x, int y) {
        if (revelar[x][y] || marcada[x][y]) {
            return;
        }

        if (bombGrid[x][y]) {
            ImageView bombView = new ImageView(minaImage);
            bombView.setFitWidth(25);
            bombView.setFitHeight(25);
            buttons[x][y].setGraphic(bombView);
            buttons[x][y].setStyle("-fx-background-color: white;");
            mostrarAlerta("Perdiste");
            revelarTodo();
        } else {
            int bombasAdyacentes = contarBombasAdyacentes(x, y);
            revelar[x][y] = true;

            if (bombasAdyacentes > 0) {
                buttons[x][y].setText(String.valueOf(bombasAdyacentes));
            } else {
                buttons[x][y].setText("");
                for (int dx = -1; dx <= 1; dx++) {
                    for (int dy = -1; dy <= 1; dy++) {
                        int nx = x + dx;
                        int ny = y + dy;
                        if (esPosicionValida(nx, ny)) {
                            descubrirCasilla(nx, ny);
                        }
                    }
                }
            }
            buttons[x][y].setDisable(true);
            verificarVictoria();
        }
    }

    private void marcarCasilla(int x, int y) {
        if (revelar[x][y]) {
            return;
        }
        if (!marcada[x][y]) {
            buttons[x][y].setText("F");
            marcada[x][y] = true;
            buttons[x][y].getStyleClass().add("flagged-button");
        } else {
            buttons[x][y].setText("");
            marcada[x][y] = false;
            buttons[x][y].getStyleClass().remove("flagged-button");
        }
    }

    private int contarBombasAdyacentes(int x, int y) {
        int count = 0;
        for (int dx = -1; dx <= 1; dx++) {
            for (int dy = -1; dy <= 1; dy++) {
                int nx = x + dx;
                int ny = y + dy;
                if (esPosicionValida(nx, ny) && bombGrid[nx][ny]) {
                    count++;
                }
            }
        }
        return count;
    }

    private boolean esPosicionValida(int x, int y) {
        return x >= 0 && x < gridSize && y >= 0 && y < gridSize;
    }

    private void revelarTodo() {
        for (int x = 0; x < gridSize; x++) {
            for (int y = 0; y < gridSize; y++) {
                if (bombGrid[x][y]) {
                    ImageView bombView = new ImageView(minaImage);
                    bombView.setFitWidth(25);
                    bombView.setFitHeight(25);
                    buttons[x][y].setGraphic(bombView);
                    buttons[x][y].setStyle("-fx-background-color: grey;");
                }
                buttons[x][y].setDisable(true);
            }
        }
    }

    private void verificarVictoria() {
        int casillasDescubiertas = 0;
        for (int x = 0; x < gridSize; x++) {
            for (int y = 0; y < gridSize; y++) {
                if (revelar[x][y]) {
                    casillasDescubiertas++;
                }
            }
        }
        if (casillasDescubiertas == (gridSize * gridSize - bombCount)) {
            mostrarAlerta("Ganaste");
            revelarTodo();
        }
    }

    private void mostrarAlerta(String mensaje) {
        Alert alert = new Alert(Alert.AlertType.INFORMATION);
        alert.setTitle("Resultado del Juego");
        alert.setHeaderText(null);
        alert.setContentText(mensaje);
        alert.showAndWait();
    }
}
