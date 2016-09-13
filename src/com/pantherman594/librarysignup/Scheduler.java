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

import javafx.event.ActionEvent;
import javafx.event.EventHandler;
import javafx.scene.control.Button;
import javafx.scene.control.ComboBox;
import javafx.scene.control.PasswordField;
import javafx.scene.control.TextField;
import javafx.scene.text.Text;

import java.awt.*;
import java.awt.event.KeyEvent;
import java.io.IOException;
import java.net.URI;
import java.net.URISyntaxException;
import java.time.LocalDate;
import java.util.Calendar;
import java.util.HashMap;
import java.util.Map;
import java.util.Set;

/**
 * Created by david on 9/11.
 */
class Scheduler implements EventHandler<ActionEvent> {
    private TextField email;
    private TextField password;
    private Text title;
    private Button submit;
    private Set<ComboBox> comboBoxes;
    private Map<Long, String> holidays;
    private Map<Integer, Integer> studies;

    Scheduler(TextField email, PasswordField password, Text title, Button submit, Map<Integer, Integer> studies, Set<ComboBox> comboBoxes) {
        this.email = email;
        this.password = password;
        this.title = title;
        this.submit = submit;
        this.studies = studies;
        this.comboBoxes = comboBoxes;

        holidays = new HashMap<>();
        addHolidays();
    }

    @Override
    public void handle(ActionEvent event) {
        if (email.getText() == null || email.getText().isEmpty() || password.getText() == null || password.getText().isEmpty()) {
            title.setText("\tError: Invalid email/password.");
            return;
        }
        title.setText("\tLibrary Sign Up is running... (minimize this)");
        LibrarySignUp.getInstance().hide();
        for (ComboBox comboBox : comboBoxes) {
            comboBox.setDisable(true);
        }
        email.setDisable(true);
        password.setDisable(true);

        submit.setText("Quit");
        submit.setOnAction(ev -> LibrarySignUp.getInstance().exit(0));

        new Thread(() -> {
            while (LibrarySignUp.getInstance().shouldRunScheduler()) {
                Calendar date = Calendar.getInstance();
                //date.add(Calendar.DAY_OF_MONTH, 1);
                date.set(Calendar.HOUR_OF_DAY, 21);
                date.set(Calendar.MINUTE, 2);
                date.set(Calendar.SECOND, 45);
                date.set(Calendar.MILLISECOND, 0);

                final long endTime = date.getTimeInMillis();
                while (System.currentTimeMillis() < endTime) {
                    try {
                        Thread.sleep(1000);
                    } catch (InterruptedException ignored) {
                    }
                }

                long tomorrow = LocalDate.now().toEpochDay() + 1;
                int sixDay = getSixDay(getSchoolDays(tomorrow));
                if (isSchoolDay(tomorrow) && studies.keySet().contains(sixDay)) {
                    if (Desktop.isDesktopSupported() && Desktop.getDesktop().isSupported(Desktop.Action.BROWSE)) {
                        int control = KeyEvent.VK_CONTROL;
                        if (System.getProperty("os.name").toLowerCase().contains("mac")) {
                            control = KeyEvent.VK_META;
                        }

                        Keyboard k = null;
                        try {
                            k = new Keyboard();
                        } catch (AWTException ignored) {
                            title.setText("Error: Unable to initialize keyboard robot.");
                            return;
                        }

                        try {
                            Desktop.getDesktop().browse(new URI("https://pickatime.com/client?logout=on&ven=11607876"));
                            Thread.sleep(1000);
                            k.doType(control, KeyEvent.VK_W);
                            Desktop.getDesktop().browse(new URI("https://pickatime.com/client?ven=11607876&email=" + email.getText()));
                            Thread.sleep(5000);

                            k.doType(control, KeyEvent.VK_F);
                            Thread.sleep(1000);
                            k.type("onal pass");
                            Thread.sleep(1000);
                            k.doType(KeyEvent.VK_ESCAPE);
                            k.type("\t");
                            k.type(password.getText());
                            k.type("\n");

                            Thread.sleep(5000);

                            k.doType(control, KeyEvent.VK_F);
                            Thread.sleep(1000);
                            k.type("st");
                            k.doType(KeyEvent.VK_ESCAPE);
                            k.type("\n");

                            Thread.sleep(5000);

                            k.doType(control, KeyEvent.VK_F);
                            Thread.sleep(1000);

                            String idNum = "55 ";
                            switch(studies.get(sixDay)) {
                                case 2: idNum = "59 "; break;
                                case 3: idNum = "48 "; break;
                                case 4: idNum = "37 "; break;
                                case 6: idNum = "42 "; break;
                                case 7: idNum = "30 "; break;
                            }
                            k.type(idNum);
                            Thread.sleep(1000);
                            k.type("\n");
                            Thread.sleep(1000);
                            k.doType(KeyEvent.VK_ESCAPE);
                            k.type("\n");

                            Thread.sleep(5000);

                            k.doType(control, KeyEvent.VK_F);
                            Thread.sleep(1000);
                            k.type("cr");
                            k.doType(KeyEvent.VK_ESCAPE);
                            k.type("\n");

                            k.doType(control, KeyEvent.VK_W);
                            Desktop.getDesktop().browse(new URI("https://pickatime.com/client?logout=on&ven=11607876"));
                            Thread.sleep(1000);
                            k.doType(control, KeyEvent.VK_W);
                        } catch (IOException | URISyntaxException | InterruptedException ignored) {}
                        System.exit(0);
                    } else {
                        title.setText("Error: Unable to open browser.");
                    }
                }
            }
        }).start();
    }

    private int getSchoolDays(long date) {
        int schoolDays = 1;
        for (long i = LocalDate.of(2016, 9, 8).toEpochDay(); i < date; i++) {
            if (isSchoolDay(i)) {
                schoolDays++;
            }
        }
        return schoolDays;
    }

    private boolean isSchoolDay(long date) {
        return date % 7 != 2 && date % 7 != 3 && !holidays.keySet().contains(date);
    }

    private int getSixDay(int schoolDays) {
        return schoolDays % 6 == 0 ? 6 : schoolDays % 6;
    }

    private void addHolidays() {
        addHoliday(2016, 10, 10, "Columbus Day");
        addHoliday(2016, 11, 11, "Veterans' Day");
        addHoliday(2016, 11, 24, "Thanksgiving Recess");
        addHoliday(2016, 11, 25, "Thanksgiving Recess");
        addHoliday(2016, 12, 23, "Winter Recess");
        addHoliday(2016, 12, 26, "Winter Recess");
        addHoliday(2016, 12, 27, "Winter Recess");
        addHoliday(2016, 12, 28, "Winter Recess");
        addHoliday(2016, 12, 29, "Winter Recess");
        addHoliday(2016, 12, 30, "Winter Recess");
        addHoliday(2017, 1, 2, "Winter Recess");
        addHoliday(2017, 1, 3, "Winter Recess");
        addHoliday(2017, 1, 16, "M. L. King Jr. Day");
        addHoliday(2017, 2, 20, "Presidents' Day");
        addHoliday(2017, 2, 21, "February Recess");
        addHoliday(2017, 2, 22, "February Recess");
        addHoliday(2017, 2, 23, "February Recess");
        addHoliday(2017, 2, 24, "February Recess");
        addHoliday(2017, 4, 14, "Good Friday");
        addHoliday(2017, 4, 17, "Patriots' Day");
        addHoliday(2017, 4, 18, "Spring Recess");
        addHoliday(2017, 4, 19, "Spring Recess");
        addHoliday(2017, 4, 20, "Spring Recess");
        addHoliday(2017, 4, 21, "Spring Recess");
        addHoliday(2017, 5, 29, "Memorial Day");
    }

    private void addHoliday(int year, int month, int day, String name) {
        holidays.put(LocalDate.of(year, month, day).toEpochDay(), name);
    }
}
