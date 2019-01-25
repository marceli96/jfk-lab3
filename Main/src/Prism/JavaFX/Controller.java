package Prism.JavaFX;

import Prism.Description;
import Prism.ICallable;
import javafx.beans.value.ChangeListener;
import javafx.beans.value.ObservableValue;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.event.ActionEvent;
import javafx.event.EventHandler;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.*;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.scene.input.MouseEvent;
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
    private TextArea taMetadata;

    @FXML
    private ListView<String> lvMethods;

    private ArrayList<Class> classes;
    private ArrayList<String> metadata;

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
        taMetadata.clear();
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

                            if (!ICallable.class.isAssignableFrom(c))
                                throw new Exception("Class " + className + " does not implement contract.");

                            Method[] methods = c.getDeclaredMethods();
                            for (int i = 0; i < methods.length; i++) {
                                classes.add(c);
                                lvMethods.getItems().add(c.getSimpleName() + " :: " + methods[i].getName());
                            }
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

        if (!tfArg1.getText().isEmpty() && !tfArg2.getText().isEmpty() && lvMethods.getSelectionModel().getSelectedIndex() != -1) {
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

        lvMethods.setOnMouseClicked(new EventHandler<MouseEvent>() {
            @Override
            public void handle(MouseEvent event) {
                if (lvMethods.getSelectionModel().getSelectedIndex() != -1) {
                    taMetadata.clear();

                    Class<?> c = classes.get(lvMethods.getSelectionModel().getSelectedIndex());
                    Description description = c.getAnnotation(Description.class);
                    taMetadata.appendText("Class name: " + c.getSimpleName() + "\n");
                    taMetadata.appendText("Description: " + description.description() + "\n");

                    Class<?>[] interfaces =  c.getInterfaces();
                    taMetadata.appendText("Interfaces: \n");
                    for(int i = 0; i < interfaces.length; i++)
                        taMetadata.appendText("\t" + interfaces[i].getSimpleName() + "\n");

                    Method[] methods = c.getDeclaredMethods();
                    taMetadata.appendText("Methods: \n");
                    for(int i = 0; i < methods.length; i++){
                        taMetadata.appendText("\t" + methods[i].getName() + "\n");

                        Class<?>[] parametersType =  methods[i].getParameterTypes();
                        taMetadata.appendText("\t\tParameters: \n");
                        if(parametersType.length == 0)
                            taMetadata.appendText("\t\t\tNone\n");
                        else {
                            for(int j = 0; j < parametersType.length; j++){
                                taMetadata.appendText("\t\t\t Arg " + (j+1) + ": " + parametersType[j].getName() + "\n");
                            }
                        }

                        Class<?> returnType = methods[i].getReturnType();
                        taMetadata.appendText("\t\tReturn: \n");
                        taMetadata.appendText("\t\t\t" + returnType.getName());
                    }
                }
            }
        });

        buttonRun.setDisable(true);
    }
}