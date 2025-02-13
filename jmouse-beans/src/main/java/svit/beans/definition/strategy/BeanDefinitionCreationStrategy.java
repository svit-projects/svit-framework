package svit.beans.definition.strategy;

import svit.beans.BeanContext;
import svit.beans.definition.BeanDefinition;

/**
 * Strategy interface for creating {@link BeanDefinition} objects from various elements.
 * <p>
 * A {@code BeanDefinitionCreationStrategy} provides methods to determine if it supports a given element,
 * and to create a {@link BeanDefinition} based on that element.
 *
 * @param <T> the type of element that this strategy supports (e.g., {@link java.lang.reflect.AnnotatedElement}, {@link Class}, {@link Object}, etc.)
 */
public interface BeanDefinitionCreationStrategy<T> {

    /**
     * Determines if this strategy supports the provided object.
     *
     * @param object the object to check.
     * @return {@code true} if the strategy supports the object, {@code false} otherwise.
     */
    boolean supports(Object object);

    /**
     * Creates a {@link BeanDefinition} from the provided object.
     *
     * @param object the object from which the bean definition is created.
     * @param context the {@link BeanContext} used during creation.
     * @return the created {@link BeanDefinition}.
     */
    BeanDefinition create(T object, BeanContext context);

    /**
     * Creates a {@link BeanDefinition} with a specified name from the provided object.
     *
     * @param name    the name of the bean.
     * @param object the object from which the bean definition is created.
     * @param context the {@link BeanContext} used during creation.
     * @return the created {@link BeanDefinition}.
     */
    BeanDefinition create(String name, T object, BeanContext context);
}
