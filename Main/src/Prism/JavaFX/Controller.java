package Prism.JavaFX;

import Prism.Description;
import Prism.ICallable;
import javafx.beans.value.ChangeListener;
import javafx.beans.value.ObservableValue;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.*;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.scene.text.Text;
import javafx.stage.DirectoryChooser;

import java.io.File;
import java.io.IOException;
import java.lang.reflect.Method;
import java.lang.reflect.TypeVariable;
import java.net.URL;
import java.net.URLClassLoader;
import java.text.ParseException;
import java.util.ArrayList;
import java.util.Enumeration;
import java.util.ResourceBundle;
import java.util.jar.JarEntry;
import java.util.jar.JarFile;

public class Controller implements Initializable {

    @FXML
    private Button buttonRun;

    @FXML
    private Text textWarning, textPath, textResult, textArg1Error, textArg2Error, textNoMethod;

    @FXML
    private TextField tfArg1, tfArg2;

    @FXML
    private TableView metadataTable;

    @FXML
    private TableColumn<Metadata, String> className;

    @FXML
    private TableColumn<Metadata, String> methodName;

    @FXML
    private TableColumn<Metadata, String> arg1;

    @FXML
    private TableColumn<Metadata, String> arg2;

    @FXML
    private TableColumn<Metadata, String> returnType;

    @FXML
    private TableColumn<Metadata, String> description;

    @FXML
    private ListView<String> lvMethods;

    private ArrayList<Class> classes;
    private ObservableList<Metadata> tableData;

    public void buttonOpenAction(ActionEvent event) {
        textWarning.setVisible(false);
        DirectoryChooser directoryChooser = new DirectoryChooser();
        File directory = directoryChooser.showDialog(null);

        if (directory != null) {
            textPath.setText(directory.getAbsolutePath());
            try {
                reflection(directory);
            } catch (Exception e) {
                e.printStackTrace();
            }
        } else {
            textWarning.setText("No directory selected");
            textWarning.setVisible(true);
        }
    }

    private void reflection(File directory) throws Exception {
        tableData.clear();
        classes.clear();
        lvMethods.getItems().clear();
        File[] files = directory.listFiles();
        if (files != null) {
            for (File file : files) {
                if (file.getName().endsWith(".jar")) {
                    System.out.println("Plik = " + file.getName());
                    JarFile jarFile = null;
                    try {
                        jarFile = new JarFile(file);
                        Enumeration<JarEntry> entries = jarFile.entries();

                        URL[] urls = {new URL("jar:file:" + file.getAbsolutePath() + "!/")};
                        URLClassLoader cl = URLClassLoader.newInstance(urls);

                        while (entries.hasMoreElements()) {
                            JarEntry je = entries.nextElement();
                            if (je.isDirectory() || !je.getName().endsWith(".class"))
                                continue;

                            String className = je.getName().substring(0, je.getName().length() - 6);
                            className = className.replace('/', '.');

                            Class<?> c = cl.loadClass(className);
                            if (!c.isAnnotationPresent(Description.class))
                                continue;

                            Description description = c.getAnnotation(Description.class);

                            if (!ICallable.class.isAssignableFrom(c))
                                throw new Exception("Class " + className + " does not implement contract.");

                            Method[] methods = c.getDeclaredMethods();
                            Class<?>[] parametersType = methods[0].getParameterTypes();
                            lvMethods.getItems().add(c.getSimpleName() + " :: " + methods[0].getName());
                            classes.add(c);
                            tableData.add(new Metadata(className, methods[0].getName(), parametersType[0].getName(),
                                    parametersType[1].getName(), methods[0].getReturnType().getName(), description.description()));
                        }
                    } catch (IOException e) {
                        e.printStackTrace();
                    } catch (ClassNotFoundException e) {
                        e.printStackTrace();
                    }
                }
            }
        }
        buttonRun.setDisable(false);
    }

    public void buttonRunAction(ActionEvent event) {
        textArg1Error.setVisible(false);
        textArg2Error.setVisible(false);
        textNoMethod.setVisible(false);

        if(!tfArg1.getText().isEmpty() && !tfArg2.getText().isEmpty() && lvMethods.getSelectionModel().getSelectedIndex() != -1){
            int arg1 = Integer.parseInt(tfArg1.getText());
            int arg2 = Integer.parseInt(tfArg2.getText());
            try {
                executeMethod(lvMethods.getSelectionModel().getSelectedIndex(), arg1, arg2);
            } catch (IllegalAccessException e) {
                e.printStackTrace();
            } catch (InstantiationException e) {
                e.printStackTrace();
            }
        } else {
            if (tfArg1.getText().isEmpty()) {
                textArg1Error.setText("Podaj argument 1");
                textArg1Error.setVisible(true);
            }
            if (tfArg2.getText().isEmpty()) {
                textArg2Error.setText("Podaj argument 2");
                textArg2Error.setVisible(true);
            }
            if (lvMethods.getSelectionModel().getSelectedIndex() == -1)
                textNoMethod.setVisible(true);
        }
    }

    private void executeMethod(int i, int arg1, int arg2) throws IllegalAccessException, InstantiationException {
        ICallable callable;
        callable = (ICallable) classes.get(i).newInstance();
        textResult.setText(String.valueOf(callable.generate(arg1, arg2)));
    }

    @Override
    public void initialize(URL location, ResourceBundle resources) {
        classes = new ArrayList<Class>();

        tableData = FXCollections.observableArrayList();
        className.setCellValueFactory(new PropertyValueFactory<Metadata, String>("className"));
        description.setCellValueFactory(new PropertyValueFactory<Metadata, String>("description"));
        methodName.setCellValueFactory(new PropertyValueFactory<Metadata, String>("methodName"));
        arg1.setCellValueFactory(new PropertyValueFactory<Metadata, String>("arg1"));
        arg2.setCellValueFactory(new PropertyValueFactory<Metadata, String>("arg2"));
        returnType.setCellValueFactory(new PropertyValueFactory<Metadata, String>("returnType"));
        metadataTable.setItems(tableData);

        tfArg1.textProperty().addListener(new ChangeListener<String>() {
            @Override
            public void changed(ObservableValue<? extends String> observable, String oldValue, String newValue) {
                if (!newValue.matches("\\d*")) {
                    tfArg1.setText(newValue.replaceAll("[^\\d]", ""));
                }
            }
        });

        tfArg2.textProperty().addListener(new ChangeListener<String>() {
            @Override
            public void changed(ObservableValue<? extends String> observable, String oldValue, String newValue) {
                if (!newValue.matches("\\d*")) {
                    tfArg2.setText(newValue.replaceAll("[^\\d]", ""));
                }
            }
        });

        buttonRun.setDisable(true);
    }
}