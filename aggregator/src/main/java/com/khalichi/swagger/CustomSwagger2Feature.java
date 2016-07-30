package com.khalichi.swagger;

import org.apache.cxf.endpoint.Server;
import org.apache.cxf.jaxrs.swagger.Swagger2Feature;

/**
 * Customized feature needed in order to set {@link Swagger2Feature#basePath}; at the moment there is no externalization of this information via
 * auto-configuration.  This externalization must be done manually.
 * @author Keivan Khalichi
 */
public class CustomSwagger2Feature extends Swagger2Feature {

    public static final String BASE_PATH = "/services";

    /** {@inheritDoc} */
    @Override
    protected void addSwaggerResource(final Server theServer) {
        this.setBasePath(BASE_PATH);
        this.setTitle("Categorizer");
        this.setDescription("APIs for categories and subcategories management");
        this.setContact("Keivan Khalichi");
        this.setLicense("Copyright (c) 2016 Khalichi.com");
        this.setLicenseUrl("mailto:keivan@khalichi.com");
        this.setVersion("0.0.1-SNAPSHOT");
        this.setPrettyPrint(true);
        super.addSwaggerResource(theServer);
    }
}
