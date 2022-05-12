import org.springframework.beans.BeansException;
import org.springframework.context.ApplicationContext;
import org.springframework.context.ApplicationContextAware;

public class SpringUtil  implements ApplicationContextAware {
    private static ApplicationContext applicationContext;

    public SpringUtil() {
    }

    @SuppressWarnings("static-access")
    @Override
    public void setApplicationContext(ApplicationContext context) throws BeansException {
        this.applicationContext = context;
    }

    public static Object getBean(String beanName) {
        if (beanName == null) return null;
        return applicationContext.getBean(beanName);
    }

    public static <T> T getBeanByType(Class<T> clazz) {
        return applicationContext.getBean(clazz);
    }

    public static ApplicationContext getContext() {
        return applicationContext;
    }
}