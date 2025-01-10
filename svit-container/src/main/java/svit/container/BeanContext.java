package svit.container;

import svit.container.naming.BeanNameResolver;
import svit.container.definition.BeanDefinitionFactory;
import svit.container.processor.BeanPostProcessorAware;

/**
 * Interface representing a bean context in the container framework.
 * Provides methods to manage bean factories, definitions, and initializers.
 */
public interface BeanContext
        extends BeanContainer, BeanInstanceContainer, BeanDefinitionContainer, BeanPostProcessorAware {

    /**
     * Retrieves the current {@link BeanFactory}.
     *
     * @return the associated bean factory.
     */
    BeanFactory getBeanFactory();

    /**
     * Retrieves the current {@link BeanDefinitionFactory}.
     *
     * @return the associated bean definition factory.
     */
    BeanDefinitionFactory getBeanDefinitionFactory();

    /**
     * Retrieves the current {@link BeanNameResolver}.
     *
     * @return the associated bean name resolver.
     */
    BeanNameResolver getNameResolver();

    /**
     * Sets the {@link BeanFactory} for this context.
     *
     * @param beanFactory the bean factory to set.
     */
    void setBeanFactory(BeanFactory beanFactory);

    /**
     * Sets the {@link BeanDefinitionFactory} for this context.
     *
     * @param definitionFactory the bean definition factory to set.
     */
    void setBeanDefinitionFactory(BeanDefinitionFactory definitionFactory);

    /**
     * Sets the {@link BeanNameResolver} for this context.
     *
     * @param nameResolver the bean name resolver to set.
     */
    void setNameResolver(BeanNameResolver nameResolver);

    /**
     * Sets the parent context for this bean context.
     *
     * @param parent the parent {@link BeanContext}.
     */
    void setParentContext(BeanContext parent);

    /**
     * Retrieves the {@link BeanInstanceContainer} for the specified lifecycle scope.
     *
     * @param lifecycle the lifecycle scope for which to retrieve the container
     * @return the {@link BeanInstanceContainer} associated with the given lifecycle
     */
    BeanInstanceContainer getBeanInstanceContainer(Lifecycle lifecycle);

    /**
     * Retrieves the parent context of this bean context.
     *
     * @return the parent {@link BeanContext}.
     */
    BeanContext getParentContext();

    /**
     * Adds a {@link BeanContextInitializer} to this context.
     *
     * @param initializer the initializer to add.
     */
    void addInitializer(BeanContextInitializer initializer);

    /**
     * Refreshes the bean context, reinitializing all components and beans.
     */
    void refresh();
}
