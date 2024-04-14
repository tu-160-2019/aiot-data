package com.ai.model;

import com.ai.util.Output;
import lombok.Data;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * 目标检测，分类输出对象
 */
@Data
public class DetectionOutput implements Output {

    Float score;

    String name;
    Integer batchId;
    private Integer clsId;

    private List<Map<String, Integer>> location;

    public DetectionOutput(Integer batchId, Integer x0, Integer y0, Integer x1, Integer y1, Integer clsId, Float score, String name) {

        this.batchId = batchId;

        this.score = score;

        this.name = name;

        this.clsId = clsId;

        this.location = new ArrayList<>();

        Map<String, Integer> xy1 = new HashMap<>();
        Map<String, Integer> xy2 = new HashMap<>();
        Map<String, Integer> xy3 = new HashMap<>();
        Map<String, Integer> xy4 = new HashMap<>();

        xy1.put("x", x0);
        xy1.put("y", y0);

        xy2.put("x", x1);
        xy2.put("y", y0);

        xy3.put("x", x1);
        xy3.put("y", y1);

        xy4.put("x", x0);
        xy4.put("y", y1);

        location.add(xy1);
        location.add(xy2);
        location.add(xy3);
        location.add(xy4);

    }

    public Float getScore() {
        return score;
    }

    public void setScore(Float score) {
        this.score = score;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public Integer getBatchId() {
        return batchId;
    }

    public void setBatchId(Integer batchId) {
        this.batchId = batchId;
    }

    public Integer getClsId() {
        return clsId;
    }

    public void setClsId(Integer clsId) {
        this.clsId = clsId;
    }

    public List<Map<String, Integer>> getLocation() {
        return location;
    }

    public void setLocation(List<Map<String, Integer>> location) {
        this.location = location;
    }

    @Override
    public String toString() {
        return "DetectionOutput {" +
                "  name: " + getName() +
                ", location : [ { x:" + location.get(0).get("x") + " , y:" + location.get(0).get("y") + "}" +
                ", { x:" + location.get(1).get("x") + " , y:" + location.get(1).get("y") + "}" +
                ", { x:" + location.get(2).get("x") + " , y:" + location.get(2).get("y") + "}" +
                ", { x:" + location.get(3).get("x") + " , y:" + location.get(3).get("y") + "}" +
                "] }";
    }
}