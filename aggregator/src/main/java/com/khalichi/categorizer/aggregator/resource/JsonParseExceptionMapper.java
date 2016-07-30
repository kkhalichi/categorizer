package com.khalichi.categorizer.aggregator.resource;

import javax.ws.rs.core.Response;
import javax.ws.rs.ext.ExceptionMapper;
import javax.ws.rs.ext.Provider;
import com.khalichi.framework.rs.RESTUtils;
import org.codehaus.jackson.JsonParseException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

/**
 * Returns proper responses for JSON parsing exceptions.
 * @author Keivan Khalichi
 */
@Component
@Provider
class JsonParseExceptionMapper implements ExceptionMapper<JsonParseException> {

    @Autowired
    private RESTUtils restUtils;

    @Override
    public Response toResponse(final JsonParseException theException) {
        return this.restUtils.errorNotAcceptable("Exception while parsing JSON object.  Exception message: %s", theException.getMessage());
    }
}