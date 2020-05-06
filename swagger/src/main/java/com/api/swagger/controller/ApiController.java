package com.api.swagger.controller;

import io.swagger.annotations.Api;
import io.swagger.annotations.ApiImplicitParam;
import io.swagger.annotations.ApiOperation;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("api")
@Api(value = "swagger api")
public class ApiController {

    @GetMapping("info/{id}")
    @ApiOperation(value = "get swagger api info", httpMethod = "GET", notes = "none")
    @ApiImplicitParam(name = "id", value = "ID", paramType = "query", required = true, dataType = "String")
    public String getInfo(@PathVariable(value = "id") String id) {
        return "swagger api info : [" + id + "]";
    }

}
