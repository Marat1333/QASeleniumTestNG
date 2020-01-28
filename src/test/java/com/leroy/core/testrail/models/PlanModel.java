package com.leroy.core.testrail.models;

import java.util.HashMap;
import java.util.Map;

public class PlanModel extends BaseTestRailModel {
    private String name;
    private Long milestone_id;
    private Long project_id;

    public Long getProject_id() {
        return project_id;
    }

    public void setProject_id(Long project_id) {
        this.project_id = project_id;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
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
        data.put("name", getName());
        data.put("milestone_id", getMilestone_id());
        return data;
    }
}
