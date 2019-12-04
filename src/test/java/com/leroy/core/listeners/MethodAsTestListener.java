package com.leroy.core.listeners;

import com.leroy.core.listeners.helpers.XMLSuiteResultMethodAsTestWriter;
import com.leroy.core.listeners.helpers.XMLSuiteResultWriter;
import org.testng.ITestResult;

import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class MethodAsTestListener extends Listener {

    private static final String TEST_CASE_ID_PATTERN = "C\\d+";

    protected XMLSuiteResultWriter getSuiteResultWriter() {
        return new XMLSuiteResultMethodAsTestWriter(config);
    }

    @Override
    protected String getTestCaseId(ITestResult arg0) {
        String result = "UNDEFINED_TEST_CASE_ID";
        Matcher matcher = Pattern.compile(TEST_CASE_ID_PATTERN).matcher(arg0.getMethod().getDescription());
        if (matcher.find()) {
            result = matcher.group(0);
        }
        return result;
    }
}
