package com.khalichi.framework.rs;

import javax.ws.rs.core.Response;
import com.khalichi.framework.stereotype.Logger;
import org.apache.commons.logging.Log;
import org.springframework.stereotype.Component;

/**
 * REST utilities, a collection of convenient {@link Response}es with messages.
 * This component is meant to be injected as a bean; refrain from using as a Singleton class
 * anti-pattern.
 * @author Keivan Khalichi
 */
@Component
public class RESTUtils {

    @Logger
    private Log log;

    /**
     * Logs warning and returns {@link Response.Status#NOT_MODIFIED} code along with a message.
     * @param theMessage the message to return as part fo response
     * @return HTTP response object
     */
    public Response warnNotModified(final String theMessage) {
        this.log.warn(theMessage);
        return Response.notModified().entity(theMessage).build();
    }

    /**
     * Convenience method that calls {@link #warnNotModified(String)} applying string  formatting.
     * @param theMessage the formatted message
     * @param theArguments the replacement values to be applied to the formatted message
     * @return
     * @see String#format(String, Object...)
     */
    public Response warnNotModified(final String theMessage, final Object... theArguments) {
        final String aMessage = String.format(theMessage, theArguments);
        return this.warnNotModified(aMessage);
    }

    public Response errorNotAcceptable(final String theMessage, final Object... theArguments) {
        final String aMessage = String.format(theMessage, theArguments);
        return Response.notAcceptable(null).entity(aMessage).build();
    }
}
