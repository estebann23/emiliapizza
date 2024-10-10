package com.example;
public class BatchInfo {
    public int batchId;
    public String driverName;
    public String postcode;
    public BatchInfo(int batchId, String driverName,  String postcode) {
        this.batchId = batchId;
        this.driverName = driverName;
        this.postcode= postcode;
    }
}