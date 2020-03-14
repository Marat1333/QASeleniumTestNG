package com.leroy.core.testrail.models;

public class StepResultModel extends BaseTestRailModel {
    private String content;
    private String expected;
    private String actual;
    private int status_id;

    public StepResultModel() {
    }

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
        this.content = safelyTextForJsonFormat(content);
    }

    public String getExpected() {
        return expected;
    }

    public void addExpectedResult(String expected) {
        expected = expected.replaceAll("\\n", "");
        if (this.expected == null || expected.isEmpty())
            this.expected = safelyTextForJsonFormat(expected);
        else
            this.expected += "\\n" + safelyTextForJsonFormat(expected);
    }

    public String getActual() {
        return actual;
    }

    public void addActualResult(String actual) {
        actual = actual.replaceAll("\\n", "");
        if (this.actual == null || actual.isEmpty())
            this.actual = safelyTextForJsonFormat(actual);
        else
            this.actual += "\\n" + safelyTextForJsonFormat(actual);
    }

    public int getStatus_id() {
        return status_id;
    }

    public void setStatus_id(int status_id) {
        this.status_id = status_id;
    }

    private String safelyTextForJsonFormat(String text) {
        return text.replaceAll("\"", "\\\\\"");
    }

}
