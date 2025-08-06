package com.jjthedev.pilly;

import java.util.List;
import java.util.Map;

public class UserDisplay {
    public String id;
    public String name;

    public Map<String, List<Integer>> pills; //Map<name, Map<
    public UserDisplay(String id, String name) {
        this.id = id;
        this.name = name;
    }

    public void addPills(Map<String, List<Integer>> pills)
    {
        this.pills = pills;
    }
}
