package com.internship.apigateway.dto;

public class RequestWrapper {

    private String data;

    public RequestWrapper() {
    }

    public RequestWrapper(String data) {
        this.data = data;
    }

    public String getData() {
        return data;
    }

    public void setData(String data) {
        this.data = data;
    }
}