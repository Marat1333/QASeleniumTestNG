package com.leroy.core.testrail.models;

public class StepResultModel extends BaseModel {
    private String content;
    private String expected;
    private String actual;
    private int status_id;

    public StepResultModel() {}

    public StepResultModel(String content, String expected, String actual, int status_id) {
        this.content = content;
        this.expected = expected;
        this.actual = actual;
        this.status_id = status_id;
    }

    public String getContent() {
        return content;
    }

    public void setContent(String content) {
        this.content = content;
    }

    public String getExpected() {
        return expected;
    }

    public void setExpected(String expected) {
        this.expected = expected;
    }

    public String getActual() {
        return actual;
    }

    public void setActual(String actual) {
        this.actual = actual;
    }

    public int getStatus_id() {
        return status_id;
    }

    public void setStatus_id(int status_id) {
        this.status_id = status_id;
    }

}
