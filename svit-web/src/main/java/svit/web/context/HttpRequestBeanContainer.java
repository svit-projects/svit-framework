package svit.web.context;

import jakarta.servlet.http.HttpServletRequest;
import svit.container.BeanInstanceContainer;
import svit.container.ObjectFactory;

/**
 * A {@link BeanInstanceContainer} implementation that uses {@link HttpServletRequest} to manage bean instances.
 * Beans are stored as request attributes.
 */
public class HttpRequestBeanContainer implements BeanInstanceContainer {

    private final ObjectFactory<HttpServletRequest> objectFactory;

    /**
     * Constructs an instance of {@link HttpRequestBeanContainer} with the given request.
     *
     * @param objectFactory the {@link ObjectFactory<HttpServletRequest>} that provides {@link HttpServletRequest}
     */
    public HttpRequestBeanContainer(ObjectFactory<HttpServletRequest> objectFactory) {
        this.objectFactory = objectFactory;
    }

    /**
     * Retrieves a bean from the request by its name.
     *
     * @param name the name of the bean to retrieve.
     * @param <T>  the type of the bean.
     * @return the bean instance, or {@code null} if no bean is found with the given name.
     */
    @Override
    @SuppressWarnings("unchecked")
    public <T> T getBean(String name) {
        return (T) objectFactory.createObject().getAttribute(name);
    }

    /**
     * Registers a bean in the request with the specified name.
     *
     * @param name the name of the bean.
     * @param bean the bean instance to store in the request.
     */
    @Override
    public void registerBean(String name, Object bean) {
        objectFactory.createObject().setAttribute(name, bean);
    }
}
