package com.yzb.lock.util;

import org.springframework.beans.BeansException;
import org.springframework.context.ApplicationContext;
import org.springframework.context.ApplicationContextAware;
import org.springframework.stereotype.Component;
import org.springframework.util.StringUtils;

/**
 * Created by brander on 2019/1/25
 */
@Component
public class ContextUtils implements ApplicationContextAware {


    private static ApplicationContext context;

    public static ApplicationContext getContext() {
        return context;
    }

    public static void setContext(ApplicationContext context) {
        ContextUtils.context = context;
    }

    /**
     * 获取spring已经加载的bean对象
     * 通过类class获取
     */
    public static <T> T getBeanByClass(Class<T> bean) throws Exception {
        return context.getBean(bean);
    }

    /**
     * 通过bean id 获取bean
     */
    public static Object getBeanById(String beanId) throws Exception {
        if (StringUtils.isEmpty(beanId)) {
            return context.getBean(beanId);
        }
        return null;
    }

    /* (non-Javadoc)
     * @see org.springframework.context.ApplicationContextAware#setApplicationContext(org.springframework.context.ApplicationContext)
     */
    @Override
    public void setApplicationContext(ApplicationContext applicationContext) throws BeansException {
        System.out.println("---------------------------");
        System.out.println("---------------------------");

        System.out.println("---------------------------");
        System.out.println("---------------------------");
        System.out.println("---------------------------");
        ContextUtils.context = applicationContext;
        System.out.println("========ApplicationContext配置成功,在普通类可以通过调用SpringUtils.getAppContext()获取applicationContext对象,applicationContext=" + ContextUtils.context + "========");
    }

}
