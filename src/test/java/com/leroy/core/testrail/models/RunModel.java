package com.leroy.core.testrail.models;

import com.leroy.core.configuration.Log;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class RunModel extends PlanModel {

    private Long suite_id;
    private Long plan_id;
    private List<Long> config_ids;
    private boolean include_all = true;


    public Long getSuite_id() {
        return suite_id;
    }

    public void setSuite_id(Long suite_id) {
        this.suite_id = suite_id;
    }

    public Long getPlan_id() {
        return plan_id;
    }

    public void setPlan_id(Long plan_id) {
        this.plan_id = plan_id;
    }

    public List<Long> getConfig_ids() {
        return config_ids;
    }

    public void setConfig_ids(List<Long> config_ids) {
        this.config_ids = config_ids;
    }

    public boolean isInclude_all() {
        return include_all;
    }

    public void setInclude_all(boolean include_all) {
        this.include_all = include_all;
    }

    @Override
    public Map<String, Object> getData() {
        Map<String, Object> data = new HashMap<>();
        data.put("suite_id", getSuite_id());
        data.put("name", getName());
        data.put("plan_id", getPlan_id());
        data.put("milestone_id", getMilestone_id());
        data.put("include_all", isInclude_all());
        return data;
    }

    @Override
    public String toString() {
        try {
            return toJsonString();
        }catch (IllegalAccessException err) {
            Log.error("toString() method. Error: " + err.getMessage());
            return null;
        }
    }
}
