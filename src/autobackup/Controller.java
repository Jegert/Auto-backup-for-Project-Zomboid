package autobackup;

import javafx.fxml.FXML;
import javafx.scene.control.Button;
import javafx.scene.control.CheckBox;
import javafx.scene.control.Label;
import javafx.scene.control.TextField;
import javafx.scene.text.Text;
import javafx.stage.DirectoryChooser;
import org.apache.commons.io.FileUtils;

import java.io.*;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.*;


public class Controller {

    @FXML
    public Button save_btn;
    @FXML
    public Button backup_btn;
    @FXML
    public Button stop_btn;
    @FXML
    public Button start_btn;
    @FXML
    public TextField min_interval;
    @FXML
    public Label save_text;
    @FXML
    public Label backup_txt;
    @FXML
    public Text system_info;
    @FXML
    public CheckBox automatic_box;

    public String save_path = "No folder selected";
    public String backup_path = "No folder selected";
    public Integer interval = 0;
    public Integer save_timer = 0;
    public Boolean start_auto = false;
    public String program_dir;

    public Controller() {
        try {
            //location of the program (needed to create the config file)
            program_dir = new File(Controller.class.getProtectionDomain().getCodeSource().getLocation().toURI().getPath()).getParent();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    @FXML
    public void initialize() {
        //creates a config file if created yet
        if (!new File(program_dir + "\\backup.properties").exists()) {
            try {
                new File(program_dir + "\\backup.properties").createNewFile();

                //gives parameters to config file
                Properties prop = new Properties();

                InputStream is = new FileInputStream(program_dir + "\\backup.properties");
                prop.load(is);
                is.close();
                OutputStream os = new FileOutputStream(program_dir + "\\backup.properties");
                prop.setProperty("save_path", "No folder selected");
                prop.setProperty("backup_path", "No folder selected");
                prop.setProperty("interval", "0");
                prop.setProperty("auto", "false");
                prop.store(os, null);
                os.close();
            } catch (Exception e) {
                e.printStackTrace();
            }
        } else {
            getProperties();
        }

        stop_btn.setDisable(true);
        save_text.setText(save_path);
        backup_txt.setText(backup_path);
        automatic_box.setSelected(start_auto);
        min_interval.setText(String.valueOf(interval));
        if (start_auto) {
            startProgram();
        }
    }

    private void getProperties() {
        //get from config file (save, backup path)
        Properties prop = new Properties();
        try {
            InputStream is = new FileInputStream(program_dir + "\\backup.properties");
            prop.load(is);
            save_path = prop.getProperty("save_path");
            backup_path = prop.getProperty("backup_path");
            interval = Integer.parseInt(prop.getProperty("interval"));
            start_auto = Boolean.parseBoolean(prop.getProperty("auto"));
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    private void setConfig(String key, String value) {
        //update config file
        Properties prop = new Properties();
        try {
            InputStream is = new FileInputStream(program_dir + "\\backup.properties");
            prop.load(is);
            is.close();
            OutputStream os = new FileOutputStream(program_dir + "\\backup.properties");
            prop.setProperty(key, value);
            prop.store(os, null);
            os.close();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }


    @FXML
    private void chooseSave() {
        //choose save folder
        DirectoryChooser dc = new DirectoryChooser();
        dc.setInitialDirectory(new File(System.getProperty("user.home") + "\\Zomboid\\Saves"));
        File selectedDirectory = dc.showDialog(null);
        if (selectedDirectory != null) {
            save_text.setText(selectedDirectory.toString());
            try {
                setConfig("save_path", selectedDirectory.toString());
                save_path = selectedDirectory.toString();
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
    }

    @FXML
    private void chooseBackup() {
        //choose backup folder
        DirectoryChooser dc = new DirectoryChooser();
        dc.setInitialDirectory(new File(System.getProperty("user.home") + "\\Zomboid\\Saves"));
        File selectedDirectory = dc.showDialog(null);
        if (selectedDirectory != null) {
            backup_txt.setText(selectedDirectory.toString());
            try {
                setConfig("backup_path", selectedDirectory.toString());
                backup_path = selectedDirectory.toString();
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
    }

    @FXML
    private void updateInterval() {
        //accept only numbers in textfield
        if (!min_interval.getCharacters().toString().replaceAll("\\D+", "").isEmpty()) {
            Integer temp_intrvl = Integer.parseInt(min_interval.getCharacters().toString().replaceAll("\\D+", ""));
            setConfig("interval", String.valueOf(temp_intrvl));
            interval = temp_intrvl;
            if (temp_intrvl == 0) {
                system_info.setText("Will make a backup once.");
            } else {
                system_info.setText("Will make a backup every " + temp_intrvl + " minutes.");
            }
        } else {
            system_info.setText("Please enter a valid number!");
        }
    }

    @FXML
    private void startProgram() {
        //won't start if no folder selected
        if (save_path.equals("No folder selected")) {
            system_info.setText("Please select a save folder.");
        } else {
            //start backup program
            save_btn.setDisable(true);
            backup_btn.setDisable(true);
            min_interval.setDisable(true);
            start_btn.setDisable(true);
            stop_btn.setDisable(false);
            automatic_box.setDisable(true);
            system_info.setText("Program started.");

            //set backup folder as save folder, when left to default
            if (backup_path.equals("No folder selected")) {
                setConfig("backup_path", save_path);
                backup_txt.setText(backup_path);
                backup_path = save_path;
            }

            if (interval > 0) {
                Timer timer = new Timer();
                TimerTask task = new TimerTask() {
                    @Override
                    public void run() {
                        if (save_btn.isDisabled()) {
                            backup_folder();
                        } else {
                            timer.cancel();
                        }
                    }
                };
                timer.schedule(task, 0, 1000);
            } else {
                backup_folder();
            }
        }
    }

    @FXML
    private void stopProgram() {
        //stops the program
        save_btn.setDisable(false);
        backup_btn.setDisable(false);
        min_interval.setDisable(false);
        start_btn.setDisable(false);
        stop_btn.setDisable(true);
        automatic_box.setDisable(false);
        system_info.setText("Program stopped.");
    }

    @FXML
    private void toggle_automatic() {
        //gets the value from the tickbox
        if (automatic_box.isSelected()) {
            setConfig("auto", "true");
            start_auto = true;
            system_info.setText("The program will start automatically next time.");
        } else {
            setConfig("auto", "false");
            start_auto = false;
            system_info.setText("Program stopped.");
        }
    }

    private void backup_folder() {
        //if timer reaches the interval will perform a backup
        save_timer += 1;
        if (save_timer / 60 >= interval || interval == 0) {
            system_info.setText("Performing a backup.");
            try {
                LocalDateTime dateTime = LocalDateTime.now();
                String date = dateTime.format(DateTimeFormatter.ofPattern("dd-MM-yyyy HH_mm_ss"));
                FileUtils.copyDirectoryToDirectory(new File(save_path), new File(backup_path + String.format("\\backup %s", date)));
            } catch (Exception e) {
                system_info.setText("PROBLEM BACKUPING THE SAVE");
            }
            system_info.setText("Backup completed!");
            save_timer = 0;
        } else if (save_timer % 60 == 0 && interval != 0) {
            system_info.setText("Next backup in: " + String.valueOf(interval - save_timer / 60) + " minutes.");
        }
    }
}
