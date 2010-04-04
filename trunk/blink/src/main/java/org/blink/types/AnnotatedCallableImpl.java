package org.blink.types;

import java.lang.annotation.Annotation;
import java.lang.reflect.Member;
import java.lang.reflect.Type;
import java.util.LinkedList;
import java.util.List;

import javax.enterprise.inject.spi.AnnotatedCallable;
import javax.enterprise.inject.spi.AnnotatedParameter;
import javax.enterprise.inject.spi.AnnotatedType;

public class AnnotatedCallableImpl<T> extends AnnotatedMemberImpl<T> implements AnnotatedCallable<T> {

    private List<AnnotatedParameter<T>> annotatedParameters = new LinkedList<AnnotatedParameter<T>>();

    public AnnotatedCallableImpl(AnnotatedType<T> declaringType,
            Member javaMemeber, Type baseType) {
        super(declaringType, javaMemeber, baseType);

    }

    protected void setAnnotatedParameters(Type[] genericParameterTypes,
            Annotation[][] parameterAnnotations) {
        int i = 0;

        for (Type genericParameter : genericParameterTypes) {
            AnnotatedParameterImpl<T> parameterImpl = new AnnotatedParameterImpl<T>(
                    this, i, genericParameter);
            parameterImpl.setAnnotations(parameterAnnotations[i]);

            addAnnotatedParameter(parameterImpl);
            i++;
        }
    }

    void addAnnotatedParameter(AnnotatedParameter<T> parameter) {
        this.annotatedParameters.add(parameter);
    }

    @Override
    public List<AnnotatedParameter<T>> getParameters() {
        return annotatedParameters;
    }
}
