package com.khalichi.framework.log;

import com.khalichi.framework.stereotype.Logger;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.springframework.util.ReflectionUtils;

/**
 */
public class LogInjectionUtil {

    public static Object injectLogger(final Object theBean) {
        ReflectionUtils.doWithFields(theBean.getClass(), theField -> {
            if ((theField.getAnnotation(Logger.class) != null) && theField.getType().equals(Log.class)) {
                theField.setAccessible(true);
                if (theField.get(theBean) == null) {
                    final Log aLog = LogFactory.getLog(theField.getDeclaringClass());
                    theField.set(theBean, aLog);
                }
            }
        });
        return theBean;
    }
}
