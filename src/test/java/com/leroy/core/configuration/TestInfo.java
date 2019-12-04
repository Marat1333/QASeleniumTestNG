package com.leroy.core.configuration;

@Deprecated
public class TestInfo {
    private String className;
    private String methodName;
    private String methodDescription;
    private String status;
    private boolean isConfiguration;
    private long duration;

    public TestInfo() {
    }

    public TestInfo(String className, String methodName, String methodDescription, String status, boolean isConfiguration, long duration) {
        this.className = className;
        this.methodName = methodName;
        this.methodDescription = methodDescription;
        this.status = status;
        this.isConfiguration = isConfiguration;
        this.duration = duration;
    }

    public String getClassName() {
        return className;
    }

    public void setClassName(String className) {
        this.className = className;
    }

    public String getMethodName() {
        return methodName;
    }

    public void setMethodName(String methodName) {
        this.methodName = methodName;
    }

    public String getMethodDescription() {
        return methodDescription;
    }

    public void setMethodDescription(String methodDescription) {
        this.methodDescription = methodDescription;
    }

    public String getStatus() {
        return status;
    }

    public void setStatus(String status) {
        this.status = status;
    }

    public boolean isConfiguration() {
        return isConfiguration;
    }

    public void setConfiguration(boolean configuration) {
        isConfiguration = configuration;
    }

    public long getDuration() {
        return duration;
    }

    public void setDuration(long duration) {
        this.duration = duration;
    }
}
