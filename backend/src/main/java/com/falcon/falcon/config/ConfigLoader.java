package com.falcon.falcon.config;

import java.nio.file.Path;

import tools.jackson.core.JacksonException;
import tools.jackson.core.exc.JacksonIOException;
import tools.jackson.databind.ObjectMapper;

public class ConfigLoader {

    private final ObjectMapper mapper = new ObjectMapper();

    public FalconConfig loadFalconConfig(Path path) {
        return read(path, FalconConfig.class, "configuration");
    }

    public EmpirePlan loadEmpirePlan(Path path) {
        return read(path, EmpirePlan.class, "empire plan");
    }

    private <T> T read(Path path, Class<T> type, String what) {
        try {
            return mapper.readValue(path, type);

        } catch (JacksonIOException failure) {
            throw new ConfigurationException("Cannot read " + path, failure);

        } catch (JacksonException failure) {
            Throwable root = failure.getCause();
            String detail = root instanceof IllegalArgumentException ? ": " + root.getMessage() : "";
            throw new ConfigurationException(path + " is not a valid " + what + " file" + detail, failure);
        }
    }
}