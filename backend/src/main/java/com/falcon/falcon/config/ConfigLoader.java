package com.falcon.falcon.config;

import java.nio.file.Path;

import tools.jackson.databind.ObjectMapper;

public class ConfigLoader {

    private final ObjectMapper mapper = new ObjectMapper();

    public FalconConfig loadFalconConfig(Path path){
        return mapper.readValue(path, FalconConfig.class);
    }

    public EmpirePlan loadEmpirePlan(Path path){
        return mapper.readValue(path, EmpirePlan.class);
    }

}
