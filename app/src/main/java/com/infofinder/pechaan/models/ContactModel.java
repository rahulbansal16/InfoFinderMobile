package com.infofinder.pechaan.models;

import com.google.gson.Gson;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.List;

//  This is intended for storing the list of contacts in the device
public class ContactModel {
    private List<SignupModel> contacts;
    private SignupModel user;

    public ContactModel(List<SignupModel> contacts, SignupModel user) {
        this.contacts = contacts;
        this.user = user;
    }

    public ContactModel() {
    }

    public List<SignupModel> getContacts() {
        return contacts;
    }

    public void setContacts(List<SignupModel> contacts) {
        this.contacts = contacts;
    }

    public SignupModel getUser() {
        return user;
    }

    public void setUser(SignupModel user) {
        this.user = user;
    }


    public JSONObject toJSONObject() throws JSONException {
        String jsonInString = new Gson().toJson(this);
        return new JSONObject(jsonInString);
    }
}
