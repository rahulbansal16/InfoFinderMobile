package com.infofinder.pechaan.models;

import java.io.Serializable;

/**
 * source, next --> node
 */
public class EdgesModel implements Serializable, Comparable<EdgesModel> {
    private String nodeNumber;
    private String contactName;
    private int order;

    public EdgesModel(String nodeNumber, String contactName, int order) {
        this.nodeNumber = nodeNumber;
        this.contactName = contactName;
        this.order = order;
    }

    public String getNodeNumber() {
        return nodeNumber;
    }

    public void setNodeNumber(String nodeNumber) {
        this.nodeNumber = nodeNumber;
    }

    public int getOrder() {
        return order;
    }

    public void setOrder(int order) {
        this.order = order;
    }

    public String getContactName() {
        return contactName;
    }

    public void setContactName(String contactName) {
        this.contactName = contactName;
    }

    @Override
    public int compareTo(EdgesModel o) {
        return this.getOrder()-o.getOrder();
    }
}
