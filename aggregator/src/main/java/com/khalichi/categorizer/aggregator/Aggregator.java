package com.khalichi.categorizer.aggregator;

import com.khalichi.framework.log.LogInjectionUtil;
import org.codehaus.jackson.jaxrs.JacksonJaxbJsonProvider;
import org.codehaus.jackson.jaxrs.JacksonJsonProvider;
import org.codehaus.jackson.map.ObjectMapper;
import org.springframework.beans.BeansException;
import org.springframework.beans.factory.config.BeanPostProcessor;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.autoconfigure.condition.ConditionalOnMissingBean;
import org.springframework.boot.builder.SpringApplicationBuilder;
import org.springframework.boot.context.web.SpringBootServletInitializer;
import org.springframework.boot.orm.jpa.EntityScan;
import org.springframework.context.annotation.Bean;
import org.springframework.core.Ordered;
import org.springframework.data.jpa.repository.config.EnableJpaRepositories;
import org.springframework.web.WebApplicationInitializer;
import org.springframework.web.servlet.mvc.method.annotation.ExceptionHandlerExceptionResolver;

/**
 * The main application.
 * @author Keivan Khalichi
 */
@SpringBootApplication(scanBasePackages = "com.khalichi")
@EntityScan("com.khalichi.categorizer.persistence.entity")
@EnableJpaRepositories("com.khalichi.categorizer.persistence.dao")
public class Aggregator extends SpringBootServletInitializer implements WebApplicationInitializer {

    public static void main(final String... theArguments) {
        SpringApplication.run(Aggregator.class, theArguments);
    }

    @Override
    protected SpringApplicationBuilder configure(SpringApplicationBuilder application) {
        return application.sources(Aggregator.class);
    }

    /**
     * Injects logger for {@link com.khalichi.framework.stereotype.Logger} annotated member attributes.
     */
    @Bean
    static BeanPostProcessor logInjectionBeanPostProcessor() {
        return new BeanPostProcessor() {
            @Override
            public Object postProcessBeforeInitialization(final Object theBean, final String theBeanName) throws BeansException {
                return theBean;
            }

            @Override
            public Object postProcessAfterInitialization(final Object theBean, final String theBeanName) throws BeansException {
                return LogInjectionUtil.injectLogger(theBean);
            }
        };
    }

    @Bean
    public ExceptionHandlerExceptionResolver exceptionHandlerExceptionResolver() {
        final ExceptionHandlerExceptionResolver aReturnValue = new ExceptionHandlerExceptionResolver();
        aReturnValue.setOrder(Ordered.HIGHEST_PRECEDENCE);
        return aReturnValue;
    }

    /**
     * Enables returning JSON from CXF REST services.
     * @return an instance of the {@link JacksonJsonProvider}
     */
    @Bean
   	@ConditionalOnMissingBean
    public JacksonJsonProvider jsonProvider() {
   		JacksonJaxbJsonProvider aProvider = new JacksonJaxbJsonProvider();
   		aProvider.setMapper(new ObjectMapper());
   		return aProvider;
   	}
}
