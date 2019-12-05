package com.leroy.core.testrail.models;

import java.util.HashMap;
import java.util.Map;

public class ResultModel extends BaseModel {

    /** Statuses:
    1	Passed
    2	Blocked
    3	Untested (not allowed when adding a result)
    4	Retest
    5	Failed
     */

    private Integer status_id;
    private String comment;
    private String elapsed;
    private Long case_id;
    private Long run_id;
    private String executionLog;

    public ResultModel(Long run_id, Long case_id) {
        this.run_id = run_id;
        this.case_id = case_id;
    }

    public Integer getStatus_id() {
        return status_id;
    }

    public void setStatus_id(Integer status_id) {
        this.status_id = status_id;
    }

    public String getComment() {
        return comment;
    }

    public void setComment(String comment) {
        this.comment = comment;
    }

    public String getElapsed() {
        return elapsed;
    }

    public void setElapsed(String elapsed) {
        this.elapsed = elapsed;
    }

    public Long getCase_id() {
        return case_id;
    }

    public void setCase_id(Long case_id) {
        this.case_id = case_id;
    }

    public Long getRun_id() {
        return run_id;
    }

    public void setRun_id(Long run_id) {
        this.run_id = run_id;
    }

    public String getExecutionLog() {
        return executionLog;
    }

    public void setExecutionLog(String executionLog) {
        this.executionLog = executionLog;
    }

    @Override
    public Map<String, Object> getData() {
        Map<String, Object> data = new HashMap<>();
        data.put("status_id", getStatus_id());
        data.put("comment", getComment());
        data.put("elapsed", getElapsed());
        data.put("custom_execlog", getExecutionLog());
        return data;
    }
}
