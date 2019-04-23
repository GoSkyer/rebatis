package org.goskyer.rebatis.processor;

import com.github.jasync.sql.db.RowData;
import com.google.auto.service.AutoService;
import com.squareup.javapoet.ClassName;
import com.squareup.javapoet.FieldSpec;
import com.squareup.javapoet.JavaFile;
import com.squareup.javapoet.MethodSpec;
import com.squareup.javapoet.ParameterizedTypeName;
import com.squareup.javapoet.TypeName;
import com.squareup.javapoet.TypeSpec;

import org.gosky.converter.Converter;
import org.gosky.converter.ConverterFactory;

import java.io.IOException;
import java.util.HashMap;
import java.util.List;
import java.util.Set;
import java.util.TreeSet;
import java.util.stream.Collectors;

import javax.annotation.processing.AbstractProcessor;
import javax.annotation.processing.ProcessingEnvironment;
import javax.annotation.processing.Processor;
import javax.annotation.processing.RoundEnvironment;
import javax.lang.model.element.Element;
import javax.lang.model.element.ElementKind;
import javax.lang.model.element.Modifier;
import javax.lang.model.element.Name;
import javax.lang.model.element.TypeElement;
import javax.lang.model.element.VariableElement;
import javax.lang.model.util.Elements;
import javax.persistence.Entity;

@AutoService(Processor.class)
public class CoreProcessor extends AbstractProcessor {


    private Elements elementUtils;

    @Override
    public synchronized void init(ProcessingEnvironment processingEnv) {
        super.init(processingEnv);
        elementUtils = processingEnv.getElementUtils();
    }


    @Override
    public Set<String> getSupportedAnnotationTypes() {
        Set<String> set = new TreeSet<>();
        set.add(Entity.class.getCanonicalName());
        return set;
    }

    @Override
    public boolean process(Set<? extends TypeElement> annotations, RoundEnvironment roundEnv) {
        Set<? extends Element> elementsAnnotatedWith = roundEnv.getElementsAnnotatedWith(Entity.class);
        // 创建ConvertFactory class
        // 创建map对象
        FieldSpec mapField = FieldSpec.builder(ParameterizedTypeName.get(
                ClassName.get(HashMap.class),
                ClassName.get(Class.class),
                ClassName.get(Converter.class)
        ), "map")
                .addModifiers(Modifier.PRIVATE, Modifier.FINAL)
                .initializer("new HashMap<>()").build();

        // 创建init方法
        MethodSpec.Builder initMethodBuilder =
                MethodSpec.methodBuilder("init")
                        .addModifiers(Modifier.PUBLIC, Modifier.FINAL)
                        .addAnnotation(Override.class);

        for (Element element : elementsAnnotatedWith) {
            if (element instanceof TypeElement) {
                TypeSpec pojoConvertClass = pojoProcessor(element);
                ClassName name = ClassName.get("org.gosky.rebatis.apt.convert", pojoConvertClass.name);
                initMethodBuilder.addStatement("$T $L = new $T()", name, pojoConvertClass.name, name);
                initMethodBuilder.addStatement("map.put($T.class,$L)", element, pojoConvertClass.name);
            }


        }

        // 创建convert方法
        MethodSpec convertMethod = MethodSpec.methodBuilder("convert")
                .addModifiers(Modifier.PUBLIC, Modifier.FINAL)
                .addAnnotation(Override.class)
                .addParameter(RowData.class, "rowData")
                .addParameter(Class.class, "pojoClass")
                .returns(Object.class)
                .addCode("return map.get(pojoClass).convert(rowData);")
                .build();

        //生成class属性
        TypeSpec typeSpec = TypeSpec.classBuilder("RebatisConverterFactory")
                .addModifiers(Modifier.PUBLIC, Modifier.FINAL)
                .addSuperinterface(ConverterFactory.class)
                .addMethod(initMethodBuilder.build())
                .addMethod(convertMethod)
                .addField(mapField)
                .build();
        JavaFile javaFile = JavaFile.builder("org.gosky.rebatis.apt", typeSpec)
                .build();
        try {
            javaFile.writeTo(processingEnv.getFiler());
        } catch (IOException e) {
            e.printStackTrace();
        }

        return false;
    }

    private TypeSpec pojoProcessor(Element element) {
        //创建convert方法
        MethodSpec.Builder convertBuilder = MethodSpec.methodBuilder("convert");
        TypeName typeName = ClassName.get(element.asType());
        String pojoName = element.getSimpleName().toString().toLowerCase();

        convertBuilder
                .addModifiers(Modifier.PUBLIC)
                .addAnnotation(Override.class)
                .returns(typeName)
                .addParameter(RowData.class, "rowData")
                .addStatement("$T _$L = new $T()", typeName, pojoName, typeName);
        // 获取entity内部的field
        List<? extends Element> allElementMembers = elementUtils.getAllMembers((TypeElement) element).stream()
                .filter(o -> ElementKind.FIELD.equals(((Element) o).getKind()))
                .filter(o -> o instanceof VariableElement).collect(Collectors.toList());
        //生成set方法
        for (Element allElementMember : allElementMembers) {
            Name simpleName = allElementMember.getSimpleName();
            CharSequence charSequence = simpleName.subSequence(0, 1);
            String s = charSequence.toString().toUpperCase() + simpleName.subSequence(1, simpleName.length());
            convertBuilder.addStatement("_$L.set$L(rowData.getAs($S))", pojoName, s, simpleName);
        }
        //添加return
        MethodSpec convertMethod = convertBuilder.addStatement("return _$L", pojoName).build();
        //class 名称
        String className = element.getSimpleName() + "ResultConvert";

        //创建getName方法
        MethodSpec getNameMethod = MethodSpec.methodBuilder("getName")
                .addModifiers(Modifier.PUBLIC)
                .addAnnotation(Override.class)
                .returns(String.class)
                .addStatement("return $S", className)
                .build();

        //创建getEntityClass方法
        MethodSpec getEntityClassMethod = MethodSpec.methodBuilder("getEntityClass")
                .addModifiers(Modifier.PUBLIC)
                .addAnnotation(Override.class)
                .returns(Class.class)
                .addStatement("return $T.class", typeName)
                .build();

        //生成class属性
        TypeSpec typeSpec = TypeSpec.classBuilder(className)
                .addModifiers(Modifier.PUBLIC, Modifier.FINAL)
                .addSuperinterface(Converter.class)
                .addMethod(convertMethod)
                .addMethod(getNameMethod)
                .addMethod(getEntityClassMethod)
                .build();
        JavaFile javaFile = JavaFile.builder("org.gosky.rebatis.apt.convert", typeSpec)
                .build();

        try {
            javaFile.writeTo(processingEnv.getFiler());
        } catch (IOException e) {
            e.printStackTrace();
        }
        return typeSpec;
    }

    public class ConvertTuple {
        private String convertName;
        private String pojoName;
    }

}