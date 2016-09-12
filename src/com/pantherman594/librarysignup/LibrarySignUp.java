/*
 * Copyright (c) 2016 David Shen
 *
 * Permission is hereby granted, free of charge, to any person obtaining a copy of this software and associated documentation files (the "Software"), to deal in the Software without restriction, including without limitation the rights to use, copy, modify, merge, publish, distribute, sublicense, and/or sell copies of the Software, and to permit persons to whom the Software is furnished to do so, subject to the following conditions:
 *
 * The above copyright notice and this permission notice shall be included in all copies or substantial portions of the Software.
 *
 * THE SOFTWARE IS PROVIDED "AS IS", WITHOUT WARRANTY OF ANY KIND, EXPRESS OR IMPLIED, INCLUDING BUT NOT LIMITED TO THE WARRANTIES OF MERCHANTABILITY, FITNESS FOR A PARTICULAR PURPOSE AND NONINFRINGEMENT. IN NO EVENT SHALL THE AUTHORS OR COPYRIGHT HOLDERS BE LIABLE FOR ANY CLAIM, DAMAGES OR OTHER LIABILITY, WHETHER IN AN ACTION OF CONTRACT, TORT OR OTHERWISE, ARISING FROM, OUT OF OR IN CONNECTION WITH THE SOFTWARE OR THE USE OR OTHER DEALINGS IN THE SOFTWARE.
 */

package com.pantherman594.librarysignup;

import javafx.application.Application;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.control.ComboBox;
import javafx.scene.control.Label;
import javafx.scene.control.TextField;
import javafx.scene.layout.GridPane;
import javafx.scene.layout.HBox;
import javafx.scene.text.Font;
import javafx.scene.text.FontWeight;
import javafx.scene.text.Text;
import javafx.stage.Stage;

import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;

/**
 * Created by david on 9/11.
 */

public class LibrarySignUp extends Application {
    private static LibrarySignUp instance;

    private boolean runScheduler;
    private Stage primaryStage;

    public static void main(String[] args) {
        launch(args);
    }

    static LibrarySignUp getInstance() {
        return instance;
    }

    @SuppressWarnings("unchecked")
    @Override
    public void start(Stage primaryStage) throws Exception {
        instance = this;
        this.primaryStage = primaryStage;

        Map<Integer, Integer> studies = new HashMap<>();
        Set<ComboBox> comboBoxes = new HashSet<>();

        GridPane grid = new GridPane();
        grid.setAlignment(Pos.TOP_LEFT);
        grid.setHgap(10);
        grid.setVgap(10);
        grid.setPadding(new Insets(25, 25, 25, 25));

        Scene scene = new Scene(grid, 450, 300);

        Text title = new Text("\tPlease select your studies:");
        title.setFont(Font.font("Lato, Tahoma", FontWeight.NORMAL, 18));

        grid.add(title, 0, 0, 3, 1);

        for (int i = 1; i <= 6; i++) {
            Label day = new Label("Day " + i + ": ");
            grid.add(day, 0, i);

            ComboBox dayBox = new ComboBox();
            dayBox.getItems().addAll(
                    "None",
                    "R1",
                    "R2",
                    "R3",
                    "R4",
                    "R6",
                    "R7"
            );
            dayBox.setValue("None");

            final int x = i;
            dayBox.valueProperty().addListener((obs, wasSelected, isNowSelected) -> {
                if (isNowSelected != "None") {
                    studies.put(x, Integer.valueOf(((String) isNowSelected).substring(1)));
                } else {
                    studies.remove(x);
                }
            });
            grid.add(dayBox, 1, i);
            comboBoxes.add(dayBox);
        }

        TextField email = new TextField();
        email.setPromptText("Enter your email.");
        email.setPrefColumnCount(20);
        grid.add(email, 2, 3);

        TextField password = new TextField();
        password.setPromptText("Enter your password.");
        password.setPrefColumnCount(20);
        grid.add(password, 2, 4);

        HBox submitBox = new HBox(10);
        submitBox.setAlignment(Pos.BOTTOM_RIGHT);

        Button submit = new Button("Submit");
        runScheduler = true;
        submit.setOnAction(new Scheduler(email, password, title, submit, studies, comboBoxes));

        submitBox.getChildren().add(submit);
        grid.add(submitBox, 2, 5);

        primaryStage.setTitle("Library Sign Up");
        primaryStage.setScene(scene);
        primaryStage.show();

        scene.getWindow().setOnCloseRequest(event -> exit(0));
    }

    boolean shouldRunScheduler() {
        return runScheduler;
    }

    void exit(int status) {
        runScheduler = false;
        primaryStage.hide();
        System.exit(status);
    }
}
