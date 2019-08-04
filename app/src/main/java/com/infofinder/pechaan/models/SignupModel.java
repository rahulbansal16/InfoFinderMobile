package com.infofinder.pechaan.models;

import androidx.annotation.NonNull;

import com.infofinder.pechaan.Constants;

import org.json.JSONObject;

import java.io.Serializable;
import java.util.LinkedHashMap;

public class SignupModel implements Serializable {

    @NonNull
    private String name;
    @NonNull
    private String number;
    private boolean selfSigned;

    public SignupModel(String name, String number, boolean selfSigned) {
        this.name = name;
        this.number = number;
        this.selfSigned = selfSigned;
    }

    public JSONObject toJSONObject(){
        LinkedHashMap<String,String> linkedHashMap = new LinkedHashMap<String,String>();
        linkedHashMap.put(Constants.NAME, name);
        linkedHashMap.put(Constants.PHONE_NUMBER, number);
        return new JSONObject(linkedHashMap);
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getNumber() {
        return number;
    }

    public void setNumber(String number) {
        this.number = number;
    }

    public boolean isSelfSigned() {
        return selfSigned;
    }

    public void setSelfSigned(boolean selfSigned) {
        this.selfSigned = selfSigned;
    }
}
