package com.infofinder.pechaan.models;

import com.infofinder.pechaan.Constants;

import org.json.JSONObject;

import java.io.Serializable;
import java.util.LinkedHashMap;
import java.util.List;

public class ContactResultModel implements Serializable {
    private String sourceNumber;
    private String sourceName;
    private String destinationNumber;
    private String destinationName;
    private List<EdgesModel> edges;

    public ContactResultModel(String sourceNumber, String sourceName, String destinationNumber, String destinationName, List<EdgesModel> edges) {
        this.sourceNumber = sourceNumber;
        this.sourceName = sourceName;
        this.destinationNumber = destinationNumber;
        this.destinationName = destinationName;
        this.edges = edges;
    }

    public JSONObject toJSONObject(){
            LinkedHashMap<String,String> linkedHashMap = new LinkedHashMap<String,String>();
            linkedHashMap.put(Constants.SOURCE_NUMBER, sourceNumber);
            linkedHashMap.put(Constants.DESTINATION_NUMBER, destinationNumber);
//            linkedHashMap.put(CGson)
            return new JSONObject(linkedHashMap);
    }

    public String getSourceNumber() {
        return sourceNumber;
    }

    public void setSourceNumber(String sourceNumber) {
        this.sourceNumber = sourceNumber;
    }

    public String getDestinationNumber() {
        return destinationNumber;
    }

    public void setDestinationNumber(String destinationNumber) {
        this.destinationNumber = destinationNumber;
    }

    public List<EdgesModel> getEdges() {
        return edges;
    }

    public void setEdges(List<EdgesModel> edges) {
        this.edges = edges;
    }

    public String toString(){
        return sourceNumber +" : " + destinationNumber;
    }

    public String getSourceName() {
        if(this.sourceName == "" || this.sourceName == null){
            return destinationNumber;
        }
        return sourceName;
    }

    public void setSourceName(String sourceName) {
        this.sourceName = sourceName;
    }

    public String getDestinationName() {
        if(this.destinationName == "" || this.destinationName == null){
            return destinationNumber;
        }
        return destinationName;
    }

    public void setDestinationName(String destinationName) {
        this.destinationName = destinationName;
    }
}

