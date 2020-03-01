package me.piggypiglet.framework.scanning.internal;

import com.google.inject.TypeLiteral;
import me.piggypiglet.framework.language.framework.LanguageEnum;
import me.piggypiglet.framework.logging.annotations.LoggerName;
import me.piggypiglet.framework.logging.framework.Logger;
import me.piggypiglet.framework.scanning.framework.Scanner;
import me.piggypiglet.framework.scanning.internal.objects.ScanningRequest;
import me.piggypiglet.framework.task.Task;
import me.piggypiglet.framework.utils.annotations.internal.InternalAnnotations;
import me.piggypiglet.framework.utils.annotations.wrapper.AnnotationWrapper;
import org.jetbrains.annotations.NotNull;

import java.lang.reflect.Field;
import java.lang.reflect.Parameter;
import java.lang.reflect.Type;
import java.util.Set;
import java.util.function.Function;

public enum ScanningKeys {
    LANGUAGES("languages", new TypeLiteral<Set<Class<? extends Enum<? extends LanguageEnum>>>>(){}.getType(),
            scanner -> scanner.getSubTypesOf(LanguageEnum.class)),
    TASK_TYPES("task_types", new TypeLiteral<Set<Class<? extends Task>>>(){}.getType(),
            scanner -> scanner.getSubTypesOf(Task.class)),
    LOGGING_TYPES("logger_types", new TypeLiteral<Set<Class<? extends Logger<?>>>>(){}.getType(),
            scanner -> scanner.getSubTypesOf(Logger.class)),
    LOGGING_FIELDS("logger_fields", new TypeLiteral<Set<Field>>(){}.getType(),
            scanner -> scanner.getClassesAnnotatedWith(new AnnotationWrapper(LoggerName.class))),
    LOGGING_PARAMETERS("logger_params", new TypeLiteral<Set<Parameter>>(){}.getType(),
            scanner -> scanner.getParametersAnnotatedWithInConstructors(new AnnotationWrapper(LoggerName.class)));

    private final ScanningRequest compiled;

    ScanningKeys(@NotNull final String annotationValue, @NotNull final Type bindType,
                 @NotNull final Function<Scanner, Set<?>> getter) {
        final AnnotationWrapper annotation = new AnnotationWrapper(InternalAnnotations.internal(annotationValue));
        compiled = new ScanningRequest(annotation, bindType, getter);
    }

    @NotNull
    public ScanningRequest getRequest() {
        return compiled;
    }
}
