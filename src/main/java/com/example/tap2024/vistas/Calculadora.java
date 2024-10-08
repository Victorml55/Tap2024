package com.example.tap2024.vistas;

import javafx.geometry.Pos;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.control.TextField;
import javafx.scene.layout.GridPane;
import javafx.scene.layout.VBox;
import javafx.stage.Stage;

import static java.lang.Double.parseDouble;

public class Calculadora extends Stage {

    private Button[][] arBtns;
    private Button btnClear;
    private TextField txtPantalla;
    private GridPane gdpTeclado;
    private VBox vBox;
    private Scene escena;
    private String[] strTeclas = {"7","8","9","*","4","5","6","/","1","2","3","+","0",".","=","-"};

    private String operador = "";
    private double operando1 = 0;
    private boolean nuevoOperando = true;
    private boolean calculoEnCadena = false;

    private void CrearUI(){
        arBtns = new Button[4][4];
        txtPantalla = new TextField("0");
        txtPantalla.setAlignment(Pos.CENTER_RIGHT);
        txtPantalla.setEditable(false);
        gdpTeclado = new GridPane();
        CrearTeclado();
        btnClear = new Button("Clear");
        btnClear.setId("font-button");
        btnClear.setOnAction(event -> limpiar());
        vBox = new VBox(txtPantalla, gdpTeclado, btnClear);
        escena = new Scene(vBox, 200, 200);
        escena.getStylesheets().add(getClass().getResource("/styles/cal.css").toString());
    }

    private void CrearTeclado(){
        for (int i = 0; i < arBtns.length; i++) {
            for (int j = 0; j < arBtns.length; j++) {
                arBtns[j][i] = new Button(strTeclas[4*i+j]);
                arBtns[j][i].setPrefSize(50,50);
                int finalI = i;
                int finalJ = j;
                arBtns[j][i].setOnAction(event -> detectarTecla(strTeclas[4* finalI + finalJ]));
                gdpTeclado.add(arBtns[j][i],j,i);
            }
        }
    }

    public Calculadora(){
        CrearUI();
        this.setTitle("Calculadora");
        this.setScene(escena);
        this.show();
    }

    private void detectarTecla(String tecla){
        if (tecla.matches("[0-9\\.]")) {
            if (nuevoOperando) {
                txtPantalla.setText(tecla);
                nuevoOperando = false;
            } else {
                txtPantalla.appendText(tecla);
            }
            calculoEnCadena = false;
        } else if (tecla.matches("[\\+\\-\\*/]")) {
            if (!calculoEnCadena) {
                operando1 = parseDouble(txtPantalla.getText());
            }
            operador = tecla;
            nuevoOperando = true;
        } else if (tecla.equals("=")) {
            realizarOperacion();
        }
    }

    private void realizarOperacion() {
        double operando2;
        try {
            operando2 = parseDouble(txtPantalla.getText());
        } catch (NumberFormatException e) {
            txtPantalla.setText("Error: Entrada inválida");
            return;
        }

        double resultado = 0;
        boolean error = false;

        switch (operador) {
            case "+":
                resultado = operando1 + operando2;
                break;
            case "-":
                resultado = operando1 - operando2;
                break;
            case "*":
                resultado = operando1 * operando2;
                break;
            case "/":
                if (operando2 != 0) {
                    resultado = operando1 / operando2;
                } else {
                    txtPantalla.setText("Error: División por 0");
                    error = true;
                }
                break;
            default:
                txtPantalla.setText("Error: Operación inválida");
                error = true;
                break;
        }

        if (!error) {
            txtPantalla.setText(String.valueOf(resultado));
            operando1 = resultado;
            nuevoOperando = true;
            calculoEnCadena = true;
        }
    }

    private void limpiar() {
        txtPantalla.setText("0");
        operador = "";
        operando1 = 0;
        nuevoOperando = true;
        calculoEnCadena = false;
    }
}