package com.demo.company.controller;

public interface PersonControllerPath {
    String BASE_PATH = "/api/person";
    String FIND_BY_CODE = "/{personCode}";
    String UPDATE_BY_CODE = "/{personCode}";
    String UPDATE_NAME_BY_CODE = "/{personCode}/name";
    String DELETE_BY_CODE = "/{personCode}";
}
