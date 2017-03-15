package com.Vinctor;

import java.lang.annotation.Annotation;
import java.util.LinkedHashSet;
import java.util.Set;

import javax.annotation.processing.AbstractProcessor;
import javax.annotation.processing.ProcessingEnvironment;
import javax.lang.model.SourceVersion;

public abstract class Vprocessor extends AbstractProcessor {


    protected EntityHandler entityHandler;

    @Override
    public synchronized void init(ProcessingEnvironment processingEnv) {
        super.init(processingEnv);
        entityHandler = new EntityHandler(processingEnv);
    }

    @Override
    public Set<String> getSupportedAnnotationTypes() {
        Set<String> types = new LinkedHashSet<>();
        Class[] typeStrings = getSupportedAnnotations();
        for (Class type : typeStrings) {
            types.add(type.getCanonicalName());
        }
        return types;
    }

    protected abstract Class<? extends Annotation>[] getSupportedAnnotations();

    @Override
    public SourceVersion getSupportedSourceVersion() {
        return SourceVersion.latestSupported();
    }
}
