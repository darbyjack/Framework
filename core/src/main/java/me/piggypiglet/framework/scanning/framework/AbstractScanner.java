package me.piggypiglet.framework.scanning.framework;

import com.google.common.collect.ImmutableSet;
import me.piggypiglet.framework.scanning.builders.ScannerBuilder;
import me.piggypiglet.framework.utils.annotations.reflection.Disabled;

import javax.annotation.Nonnull;
import java.lang.annotation.Annotation;
import java.lang.reflect.*;
import java.util.Arrays;
import java.util.Collection;
import java.util.HashSet;
import java.util.Set;
import java.util.function.Function;
import java.util.stream.Collectors;
import java.util.stream.Stream;

/**
 * Utility abstraction around Scanner interface to handle class list population
 */
public abstract class AbstractScanner implements Scanner {
    protected final ScannerBuilder.ScannerData data;

    private final Set<Class<?>> classes;
    private final Set<Constructor<?>> constructors;
    private final Set<Method> methods;
    private final Set<Field> fields;

    /**
     * Util super constructor to provide pre defined classes and their members for a scanner. Do
     * not implement your scanner logic in the constructor, use the abstract populate method. This
     * constructor is purely for utility purposes if your classes are loaded elsewhere, but you need them
     * in a scanner object.
     *
     * @param classes Set of loaded classes
     */
    protected AbstractScanner(@Nonnull final ScannerBuilder.ScannerData data, @Nonnull final Set<Class<?>> classes,
                              @Nonnull final Set<Constructor<?>> constructors, @Nonnull final Set<Method> methods,
                              @Nonnull final Set<Field> fields) {
        this.data = data;
        this.classes = classes;
        this.constructors = constructors;
        this.methods = methods;
        this.fields = fields;
    }

    /**
     * One arg (ScannerData) constructor that will assign #classes, #constructors,
     * #methods, and #fields to a new HashSet.
     */
    protected AbstractScanner(@Nonnull final ScannerBuilder.ScannerData data) {
        this.data = data;
        classes = new HashSet<>();
        constructors = new HashSet<>();
        methods = new HashSet<>();
        fields = new HashSet<>();
    }

    /**
     * Provisioning logic of the classes retrievable from the scanner configuration.
     *
     * @return Set of loaded classes
     */
    protected abstract Set<Class<?>> provideClasses(@Nonnull Class<?> main, @Nonnull String pckg, @Nonnull String[] exclusions) ;

    /**
     * Store the values from the class loading method provided by the implementation,
     * and stream through the classes and extract their respective constructors,
     * methods, and fields.
     */
    public void populate() {
        Stream.of(classes, constructors, methods, fields).forEach(Collection::clear);

        classes.addAll(provideClasses(data.getMain(), data.getPckg(), data.getExclusions()).stream()
                .filter(this::notDisabled)
                .collect(Collectors.toSet()));
        constructors.addAll(get(Class::getDeclaredConstructors));
        methods.addAll(get(Class::getDeclaredMethods));
        fields.addAll(get(Class::getDeclaredFields));
    }

    /**
     * Get all loaded classes in this scanner.
     *
     * @return Set of classes
     */
    public Set<Class<?>> getClasses() {
        return ImmutableSet.copyOf(classes);
    }

    /**
     * Get all known sub types of a specific type. Comparison is performed using
     * Class#isAssignableFrom.
     *
     * @param type Master type
     * @param <T>  Type generic
     * @return Set of classes
     */
    @SuppressWarnings("unchecked")
    @Override
    public <T> Set<Class<? extends T>> getSubTypesOf(@Nonnull final Class<T> type) {
        return stream(classes)
                .filter(type::isAssignableFrom)
                .map(c -> (Class<? extends T>) c)
                .collect(Collectors.toSet());
    }

    /**
     * Get all known classes that are annotated with a specific annotation type.
     * Check is performed with Class#isAnnotationPresent.
     *
     * @param annotation Annotation class
     * @return Set of classes
     */
    @Override
    public Set<Class<?>> getClassesAnnotatedWith(@Nonnull final Class<? extends Annotation> annotation) {
        return stream(classes)
                .filter(c -> c.isAnnotationPresent(annotation))
                .collect(Collectors.toSet());
    }

    /**
     * Get all known classes that have methods annotated with a specific annotation
     * type. Check is performed with Method#isAnnotationPresent.
     *
     * @param annotation Annotation class
     * @return Set of classes
     */
    @Override
    public Set<Class<?>> getClassesWithAnnotatedMethods(@Nonnull final Class<? extends Annotation> annotation) {
        return stream(methods)
                .filter(m -> m.isAnnotationPresent(annotation))
                .map(Method::getDeclaringClass)
                .collect(Collectors.toSet());
    }

    /**
     * Get all known constructor parameters that are annotated with a specific
     * annotation type. Check is performed by streaming through all known
     * constructors, then streaming their respective parameters, and finally
     * performing a reference equality check on the annotation types.
     *
     * @param annotation Annotation class
     * @return Set of parameters
     */
    @Override
    public Set<Parameter> getParametersInConstructorsAnnotatedWith(@Nonnull final Class<? extends Annotation> annotation) {
        return stream(constructors)
                .flatMap(c -> Arrays.stream(c.getParameters())
                        .filter(p -> Arrays.stream(p.getAnnotations())
                                .map(Annotation::annotationType)
                                .anyMatch(pt -> pt == annotation)))
                .collect(Collectors.toSet());
    }

    /**
     * Get all known fields that are annotated with a specific annotation type.
     * Check is performed with Field#isAnnotationPresent.
     *
     * @param annotation Annotation class
     * @return Set of fields
     */
    @Override
    public Set<Field> getFieldsAnnotatedWith(@Nonnull final Class<? extends Annotation> annotation) {
        return stream(fields)
                .filter(f -> f.isAnnotationPresent(annotation))
                .collect(Collectors.toSet());
    }

    /**
     * Execute a method that returns an array of a type extending AccessibleObject on each class
     * cached in this scanner, and then flatten the results into an ImmutableSet of the requested type.
     *
     * @param getter Array getter function to perform on the class
     * @param <T>    Wildcard type parameter extending AccessibleObject
     * @return Set of T
     */
    private <T extends AccessibleObject> Set<T> get(@Nonnull final Function<Class<?>, T[]> getter) {
        return classes.parallelStream()
                .map(getter)
                .flatMap(Arrays::stream)
                .collect(Collectors.collectingAndThen(Collectors.toList(), ImmutableSet::copyOf));
    }

    /**
     * Check if a class is annotated with the "Disabled" utility annotation.
     *
     * @param clazz Class to check
     * @return boolean value of result
     */
    private boolean notDisabled(@Nonnull final Class<?> clazz) {
        return !clazz.isAnnotationPresent(Disabled.class);
    }

    /**
     * Initialize a stream from the provided collection. Stream will be parallel
     * if set so in the ScannerData object.
     *
     * @param collection Collection to stream
     * @param <R>        Collection element type
     * @return Stream of R
     */
    private <R> Stream<R> stream(@Nonnull final Collection<R> collection) {
        return data.isParallelFiltering() ? collection.parallelStream() : collection.stream();
    }
}
