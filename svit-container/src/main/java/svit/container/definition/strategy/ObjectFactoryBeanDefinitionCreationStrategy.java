package svit.container.definition.strategy;

import svit.container.BeanContext;
import svit.container.BeanContextException;
import svit.container.ObjectFactory;
import svit.container.BeanScope;
import svit.container.definition.BeanDefinition;
import svit.container.definition.ObjectFactoryBeanDefinition;

/**
 * A strategy for creating {@link BeanDefinition} instances from {@link ObjectFactory} objects.
 * <p>
 * This strategy supports creating bean definitions where the bean instance is provided by a factory.
 * The default bean scope for such definitions is {@link BeanScope#PROTOTYPE}.
 */
public class ObjectFactoryBeanDefinitionCreationStrategy implements BeanDefinitionCreationStrategy<ObjectFactory<Object>> {

    /**
     * Determines if this strategy supports the provided object.
     * <p>
     * Currently, this strategy does not support implicit bean definition creation from arbitrary objects.
     *
     * @param object the object to check.
     * @return {@code false}, as only named factories are supported.
     */
    @Override
    public boolean supports(Object object) {
        return object instanceof ObjectFactory<?>;
    }

    /**
     * Unsupported operation for creating a bean definition without an explicit name.
     *
     * @param object  the factory object.
     * @param context the bean context.
     * @return nothing, as this operation always throws an exception.
     * @throws BeanContextException indicating that this operation is unsupported.
     */
    @Override
    public BeanDefinition create(ObjectFactory<Object> object, BeanContext context) {
        throw new BeanContextException("Unsupported bean definition creation without explicit bean name");
    }

    /**
     * Creates a {@link BeanDefinition} from the given {@link ObjectFactory} and an explicit bean name.
     * <p>
     * The bean scope for definitions created by this strategy is set to {@link BeanScope#PROTOTYPE}.
     *
     * @param name    the name of the bean.
     * @param object  the factory object providing the bean instance.
     * @param context the bean context.
     * @return the created {@link BeanDefinition}.
     */
    @Override
    public BeanDefinition create(String name, ObjectFactory<Object> object, BeanContext context) {
        ObjectFactoryBeanDefinition definition = new ObjectFactoryBeanDefinition(name, object.getClass(), object);

        definition.setBeanScope(BeanScope.PROTOTYPE);

        return definition;
    }
}