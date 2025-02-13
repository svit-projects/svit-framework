package org.jmouse.context.bind;

import org.jmouse.util.Priority;

import java.util.function.Supplier;

/**
 * Binder for Java records, enabling their instantiation and property binding from a data source.
 * <p>
 * This binder extracts record components, binds them using the provided {@link DataSource},
 * and creates a new immutable record instance via a {@link ValueObject}.
 * </p>
 *
 * <p>Example usage:</p>
 * <pre>{@code
 * BindContext context = new BindContext();
 * ValueObjectBinder binder = new ValueObjectBinder(context);
 *
 * NamePath namePath = NamePath.of("person");
 * Bindable<Person> bindable = Bindable.of(Person.class);
 * DataSource source = DataSource.of(Map.of("name", "John"));
 *
 * BindingResult<Person> result = binder.bind(namePath, bindable, source);
 * Person person = result.orElse(null); // Person [name=John]
 * }</pre>
 */
@Priority(Integer.MIN_VALUE / 2)
public class ValueObjectBinder extends AbstractBinder {

    /**
     * Constructs a binder for handling Java records.
     *
     * @param context the binding context
     */
    public ValueObjectBinder(BindContext context) {
        super(context);
    }

    /**
     * Binds a record class by extracting its properties, resolving values from the {@link DataSource},
     * and instantiating a new immutable record instance.
     *
     * @param name     the hierarchical name of the object being bound
     * @param bindable the bindable target describing the type to bind
     * @param source   the source of values for binding
     * @param callback the binding callback for customization
     * @param <T>      the type of the bound object
     * @return a {@link BindResult} containing the bound record instance if successful
     */
    @Override
    @SuppressWarnings({"unchecked"})
    public <T> BindResult<T> bind(NamePath name, Bindable<T> bindable, DataSource source, BindCallback callback) {
        Class<?> rawType = bindable.getType().getRawType();

        if (bindable.getTypeDescriptor().isRecord()) {
            // Create a ValueObject representation of the record
            ValueObject<?>     vo      = ValueObject.of((Class<? extends Record>) rawType);
            ValueObject.Values values  = vo.getRecordValues();
            Supplier<?>        factory = vo.getInstance(values);

            // Bind each record property from the data source
            for (Bean.Property<?> property : vo.getProperties()) {
                String      propertyName = property.getName();
                var         setter       = Bean.Setter.ofMap(propertyName);
                Bindable<?> component    = Bindable.of(property.getType());

                bindValue(name.append(propertyName), component, source, callback)
                        .ifPresent(bound -> setter.set(values, bound));
            }

            // Create and return the record instance
            return BindResult.of((T) factory.get());
        }

        return BindResult.empty();
    }

    /**
     * Determines whether this binder supports the given type.
     * This binder only supports Java records.
     *
     * @param bindable the bindable type descriptor
     * @param <T>      the type of the object
     * @return {@code true} if the type is a record, {@code false} otherwise
     */
    @Override
    public <T> boolean supports(Bindable<T> bindable) {
        return bindable.getTypeDescriptor().isRecord();
    }

}
