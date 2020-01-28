package com.leroy.core.testrail.models;

import java.util.*;

public class PlanEntryModel extends BaseTestRailModel {
    private Long suite_id;
    private String name;
    private List<Long> case_ids;
    private boolean include_all = true;
    private List<RunModel> runs = new LinkedList<>();
    private Set<Long> config_ids = new LinkedHashSet<>();
    private Long milestone_id;

    public PlanEntryModel(Long id) {
        setId(id);
    }

    public Long getSuite_id() {
        return suite_id;
    }

    public void setSuite_id(Long suite_id) {
        this.suite_id = suite_id;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public List<Long> getCase_ids() {
        return case_ids;
    }

    public void setCase_ids(List<Long> case_ids) {
        this.case_ids = case_ids;
    }

    public boolean isInclude_all() {
        return include_all;
    }

    public void setInclude_all(boolean include_all) {
        this.include_all = include_all;
    }

    public List<RunModel> getRuns() {
        return runs;
    }

    public void setRuns(List<RunModel> runs) {
        this.runs = runs;
    }

    public Set<Long> getConfig_ids() {
        return config_ids;
    }

    public void setConfig_ids(Set<Long> config_ids) {
        this.config_ids = config_ids;
    }

    public Long getMilestone_id() {
        return milestone_id;
    }

    public void setMilestone_id(Long milestone_id) {
        this.milestone_id = milestone_id;
    }

    @Override
    public Map<String, Object> getData() {
        Map<String, Object> data = new HashMap<>();
        data.put("suite_id", getSuite_id());
        data.put("milestone_id", getMilestone_id());
        data.put("name", getName());
        data.put("include_all", isInclude_all());
        data.put("case_ids", getCase_ids());
        data.put("config_ids", getConfig_ids());
        data.put("runs", getRuns());
        return data;
    }
}
