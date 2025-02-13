package org.jmouse.context.bind;

import org.jmouse.core.convert.Conversion;
import svit.beans.CyclicReferenceDetector;
import svit.beans.DefaultCyclicReferenceDetector;

import java.util.function.Supplier;

/**
 * The {@code Binder} class is responsible for managing the binding process, allowing for flexible object binding strategies.
 * It handles recursive reference detection and provides various binding methods for different data types.
 * <p>
 * This class supports deep and shallow binding strategies and utilizes a factory to register and retrieve binders for different types.
 * It also ensures that cyclic references are detected during the binding process to prevent infinite loops.
 * </p>
 */
public class Binder implements ObjectBinder, BindContext {

    private static final Supplier<? extends RuntimeException> exceptionSupplier = ()
            -> new BindException("Recursive binding detected");

    private final DataSource                      source;
    private final BinderFactory                   factory;
    private final Conversion                      conversion;
    private final CyclicReferenceDetector<String> detector;
    private       BindCallback                    defaultCallback;
    private       BindingStrategy                 strategy;

    /**
     * Constructs a {@code Binder} with the specified data source, binder factory, and binding strategy.
     * Registers the default binders (MapBinder, ArrayBinder, SetBinder, ListBinder, and JavaBeanBinder).
     *
     * @param source the data source for binding
     * @param factory the factory for creating binders
     * @param strategy the binding strategy (deep or shallow)
     */
    public Binder(DataSource source, BinderFactory factory, BindingStrategy strategy) {
        this.defaultCallback = BindCallback.NOOP;
        this.strategy = strategy;
        this.source = source;
        this.factory = factory;
        this.detector = new DefaultCyclicReferenceDetector<>();
        this.conversion = new BinderConversion();

        // register default binders
        factory.registerBinder(new ScalarValueBinder(this));
        factory.registerBinder(new MapBinder(this));
        factory.registerBinder(new ArrayBinder(this));
        factory.registerBinder(new SetBinder(this));
        factory.registerBinder(new ListBinder(this));
        factory.registerBinder(new ValueObjectBinder(this));
        factory.registerBinder(new JavaBeanBinder(this));
    }

    /**
     * Creates a new {@code Binder} instance using the given source object.
     *
     * @param source the source object for binding
     * @param callback the binding callback for customization
     *
     * @return a new {@code Binder} instance
     */
    public static Binder with(Object source, BindCallback callback) {
        Binder binder = new Binder(DataSource.of(source));

        binder.setDefaultCallback(callback);

        return binder;
    }

    /**
     * Creates a new {@code Binder} instance using the given source object.
     *
     * @param source the source object for binding
     * @return a new {@code Binder} instance
     */
    public static Binder withDataSource(Object source) {
        return new Binder(DataSource.of(source));
    }

    /**
     * Constructs a {@code Binder} using the specified data source and default binder factory.
     *
     * @param source the data source for binding
     */
    public Binder(DataSource source) {
        this(source, new DefaultBinderFactory(), BindingStrategy.DEEP);
    }

    /**
     * Binds the given {@link Bindable} object with the source data using the default path (null).
     *
     * @param <T> the type of the object to bind
     * @param bindable the object to bind
     * @return the result of the binding
     */
    public <T> BindResult<T> bind(Bindable<T> bindable) {
        return bind(null, bindable);
    }

    /**
     * Binds the given {@link Bindable} object with the source data at the specified path.
     *
     * @param <T> the type of the object to bind
     * @param path the path at which to bind the object
     * @param bindable the object to bind
     * @return the result of the binding
     */
    public <T> BindResult<T> bind(String path, Bindable<T> bindable) {
        return bind(NamePath.of(path), bindable, source, null);
    }

    /**
     * Binds the given {@link Bindable} object with the source data at the specified path and source.
     *
     * @param <T> the type of the object to bind
     * @param path the path at which to bind the object
     * @param bindable the object to bind
     * @param source the data source
     * @param callback the binding callback for customization
     * @return the result of the binding
     */
    @Override
    public <T> BindResult<T> bind(NamePath path, Bindable<T> bindable, DataSource source, BindCallback callback) {
        try {
            ObjectBinder binder     = factory.getBinderFor(bindable);
            BindCallback customizer = callback == null ? this.defaultCallback : callback;

            // Detect recursive binding before proceeding
            detector.detect(path::path, exceptionSupplier);

            return binder.bind(path, bindable, source, customizer);
        } catch (Exception exception) {
            return BindResult.of(performException(path, bindable, exception));
        } finally {
            // Remove the path after binding to prevent future recursion detection for the same path
            detector.remove(path::path);
        }
    }

    @SuppressWarnings({"unchecked"})
    private <T> T performException(NamePath path, Bindable<T> bindable, Exception exception) {
        try {
            return (T) defaultCallback.onFailure(path, bindable, this, exception);
        } catch (Throwable cause) {
            throw new BindException(path, bindable, cause);
        }
    }

    /**
     * This method is not supported for the root binder and is meant to be used in type-specific binders.
     *
     * @throws UnsupportedOperationException if called on the root binder
     */
    @Override
    public <T> BindResult<T> bindValue(NamePath name, Bindable<T> bindable, DataSource source, BindCallback callback) {
        throw new UnsupportedOperationException("Root binder is not supported this method. This methods can be called only in type-binder specific.");
    }

    /**
     * Returns the data source associated with this binder.
     *
     * @return the data source
     */
    @Override
    public DataSource getDataSource() {
        return source;
    }

    /**
     * Returns the root binder for this binding process.
     *
     * @return the root binder
     */
    @Override
    public ObjectBinder getRootBinder() {
        return this;
    }

    /**
     * Checks if the binding strategy is deep binding.
     *
     * @return {@code true} if deep binding is enabled, otherwise {@code false}
     */
    @Override
    public boolean isDeepBinding() {
        return BindingStrategy.DEEP.equals(strategy);
    }

    /**
     * Checks if the binding strategy is shallow binding.
     *
     * @return {@code true} if shallow binding is enabled, otherwise {@code false}
     */
    @Override
    public boolean isShallowBinding() {
        return BindingStrategy.SHALLOW.equals(strategy);
    }

    @Override
    public void useDeepBinding() {
        strategy = BindingStrategy.DEEP;
    }

    @Override
    public void useShallowBinding() {
        strategy = BindingStrategy.SHALLOW;
    }

    /**
     * Returns the conversion logic associated with this binder.
     *
     * @return the conversion
     */
    @Override
    public Conversion getConversion() {
        return conversion;
    }

    public BindCallback getDefaultCallback() {
        return defaultCallback;
    }

    public void setDefaultCallback(BindCallback defaultCallback) {
        this.defaultCallback = defaultCallback;
    }

}
