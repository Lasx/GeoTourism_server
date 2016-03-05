package com.mmontes.model.entity.metric;

import com.mmontes.model.dao.StatsDao;
import org.springframework.beans.factory.annotation.Autowired;

import java.util.List;

public abstract class Metric {

    private String id;
    private String name;

    @Autowired
    protected StatsDao statsDao;

    public Metric() {
    }

    public Metric(String id, String name) {
        this.id = id;
        this.name = name;
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public abstract List<List<Double>> getStats();
}
