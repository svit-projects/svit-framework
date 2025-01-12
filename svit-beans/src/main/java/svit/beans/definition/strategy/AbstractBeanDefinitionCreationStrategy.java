package svit.beans.definition.strategy;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import svit.beans.BeanContext;
import svit.beans.annotation.Provide;
import svit.beans.annotation.Qualifier;
import svit.beans.definition.BeanDefinition;
import svit.beans.definition.BeanDependency;
import svit.beans.definition.SimpleBeanDependency;

import java.lang.reflect.AnnotatedElement;
import java.lang.reflect.Parameter;
import java.util.List;

/**
 * An abstract base class for creating {@link BeanDefinition} objects from annotated elements.
 * <p>
 * This class provides utility methods for resolving dependencies, updating bean lifecycle,
 * and generating bean definitions based on annotations and parameters.
 *
 * @param <T> the type of the annotated element (e.g., {@link java.lang.Class}, {@link java.lang.reflect.Method})
 */
public abstract class AbstractBeanDefinitionCreationStrategy<T extends AnnotatedElement>
        implements BeanDefinitionCreationStrategy<T> {

    /**
     * Logger instance for logging details about the creation process.
     */
    protected final Logger logger = LoggerFactory.getLogger(getClass());

    /**
     * Creates a {@link BeanDefinition} for the given annotated element using the default naming strategy.
     *
     * @param object  the annotated element
     * @param context the {@link BeanContext} used during definition creation
     * @return the created {@link BeanDefinition}
     */
    @Override
    public BeanDefinition create(T object, BeanContext context) {
        return create(context.getNameResolver().resolveName(object), object, context);
    }

    /**
     * Creates bean dependencies for the given array of parameters.
     * Each parameter is converted into a {@link BeanDependency} using {@link #createDependency(Parameter)}.
     *
     * @param dependencies the list in which to store the created dependencies
     * @param parameters   the array of parameters that require dependency objects
     */
    protected void createDependencies(List<BeanDependency> dependencies, Parameter[] parameters) {
        for (Parameter parameter : parameters) {
            dependencies.add(createDependency(parameter));
        }
    }

    /**
     * Creates a single {@link BeanDependency} from the given parameter.
     * <p>
     * If the parameter is annotated with {@link Qualifier}, its value is used
     * as the dependency name.
     *
     * @param parameter the parameter to convert into a dependency
     * @return a {@link BeanDependency} representing the parameter
     */
    protected BeanDependency createDependency(Parameter parameter) {
        String name = null;

        if (parameter.isAnnotationPresent(Qualifier.class)) {
            name = parameter.getAnnotation(Qualifier.class).value();
        }

        return new SimpleBeanDependency(parameter.getType(), name);
    }

    /**
     * Updates the bean lifecycle by inspecting the {@link Provide} annotation on the provided element.
     * If present, the annotation's scope value is assigned to the {@code definition}.
     *
     * @param definition the bean definition to update
     * @param element    the annotated element (class or method) that may hold the {@link Provide} annotation
     */
    protected void updateBeanLifecycle(BeanDefinition definition, AnnotatedElement element) {
        if (element.isAnnotationPresent(Provide.class)) {
            definition.setBeanScope(element.getAnnotation(Provide.class).scope());
        }
    }

    /**
     * Determines if this strategy supports the provided object.
     *
     * @param object the object to check.
     * @return {@code true} if the strategy supports the object, {@code false} otherwise.
     */
    @Override
    public boolean supports(Object object) {
        return object instanceof AnnotatedElement;
    }
}