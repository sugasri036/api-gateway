package com.internship.apigateway.dto;

public class ResponseWrapper {

    private String data;

    public ResponseWrapper() {
    }

    public ResponseWrapper(String data) {
        this.data = data;
    }

    public String getData() {
        return data;
    }

    public void setData(String data) {
        this.data = data;
    }
}