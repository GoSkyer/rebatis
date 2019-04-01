package org.goskyer.rebatis.processor;

import com.github.jasync.sql.db.QueryResult;
import com.google.auto.service.AutoService;
import com.squareup.javapoet.ClassName;
import com.squareup.javapoet.JavaFile;
import com.squareup.javapoet.MethodSpec;
import com.squareup.javapoet.TypeSpec;

import org.gosky.mapping.ResultSetMapper;

import java.io.IOException;
import java.util.Set;
import java.util.TreeSet;

import javax.annotation.processing.AbstractProcessor;
import javax.annotation.processing.Processor;
import javax.annotation.processing.RoundEnvironment;
import javax.lang.model.element.Element;
import javax.lang.model.element.Modifier;
import javax.lang.model.element.TypeElement;

@AutoService(Processor.class)
public class TestProcessor extends AbstractProcessor {

    @Override
    public Set<String> getSupportedAnnotationTypes() {
        Set<String> set = new TreeSet<>();
        set.add(Test.class.getCanonicalName());
        set.add(Entity.class.getCanonicalName());
        return set;
    }

    @Override
    public boolean process(Set<? extends TypeElement> annotations, RoundEnvironment roundEnv) {
        Set<? extends Element> elementsAnnotatedWith = roundEnv.getElementsAnnotatedWith(Entity.class);
        for (Element element : elementsAnnotatedWith) {
            MethodSpec main = MethodSpec.methodBuilder("parser")
                    .addModifiers(Modifier.PUBLIC)
                    .addAnnotation(Override.class)
                    .returns(ClassName.get(element.asType()))
                    .addParameter(QueryResult.class, "qr")
//                    .addParameter(ClassName.get(element.asType()), "pojo")
                    .addParameter(Object.class, "o")
                    .addStatement("$T.out.println($S)", System.class, "Hello, JavaPoet!")
                    .addStatement("$T pojo = ($T)o", ClassName.get(element.asType()), ClassName.get(element.asType()))
                    .addStatement("return pojo")
                    .build();
            TypeSpec helloWorld = TypeSpec.classBuilder("UserResultMapper")
                    .addModifiers(Modifier.PUBLIC, Modifier.FINAL)
                    .addSuperinterface(ResultSetMapper.class)
                    .addMethod(main)
                    .build();
            JavaFile javaFile = JavaFile.builder("com.example.helloworld", helloWorld)
                    .build();
            try {
                javaFile.writeTo(processingEnv.getFiler());
            } catch (IOException e) {
                e.printStackTrace();
            }
        }


        return false;
    }
}