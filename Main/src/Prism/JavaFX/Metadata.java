package Prism.JavaFX;

import javafx.beans.property.SimpleStringProperty;

public class Metadata {
    private SimpleStringProperty className;
    private SimpleStringProperty methodName;
    private SimpleStringProperty arg1;
    private SimpleStringProperty arg2;
    private SimpleStringProperty returnType;
    private SimpleStringProperty description;

    public Metadata(String className, String methodName, String arg1, String arg2, String returnType, String description) {
        this.className = new SimpleStringProperty(className);
        this.methodName = new SimpleStringProperty(methodName);
        this.arg1 = new SimpleStringProperty(arg1);
        this.arg2 = new SimpleStringProperty(arg2);
        this.returnType = new SimpleStringProperty(returnType);
        this.description = new SimpleStringProperty(description);
    }

    public String getClassName() {
        return className.get();
    }

    public String getMethodName() {
        return methodName.get();
    }

    public String getArg1() {
        return arg1.get();
    }

    public String getArg2() {
        return arg2.get();
    }

    public String getReturnType() {
        return returnType.get();
    }

    public String getDescription() {
        return description.get();
    }

    public void setClassName(String className) {
        this.className.set(className);
    }

    public void setMethodName(String methodName) {
        this.methodName.set(methodName);
    }

    public void setArg1(String arg1) {
        this.arg1.set(arg1);
    }

    public void setArg2(String arg2) {
        this.arg2.set(arg2);
    }

    public void setReturnType(String returnType) {
        this.returnType.set(returnType);
    }

    public void setDescription(String description) {
        this.description.set(description);
    }
}
