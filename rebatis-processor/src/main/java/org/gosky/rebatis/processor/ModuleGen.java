//package org.gosky.rebatis.processor;
//
//import com.google.auto.service.AutoService;
//import io.vertx.codegen.type.TypeMirrorFactory;
//import org.apache.ibatis.annotations.Mapper;
//
//import javax.annotation.processing.*;
//import javax.lang.model.SourceVersion;
//import javax.lang.model.element.TypeElement;
//import javax.lang.model.util.Elements;
//import javax.lang.model.util.Types;
//import java.util.Set;
//
//@AutoService(Processor.class)
//@SupportedSourceVersion(SourceVersion.RELEASE_8)
//public class ModuleGen extends AbstractProcessor {
//
//    private Elements elementUtils;
//    private Types typeUtils;
//    private TypeMirrorFactory tmf;
//
//    @Override
//    public synchronized void init(ProcessingEnvironment processingEnv) {
//        super.init(processingEnv);
//        elementUtils = processingEnv.getElementUtils();
//        typeUtils = processingEnv.getTypeUtils();
//        tmf = new TypeMirrorFactory(elementUtils, typeUtils);
//    }
//
//    @Override
//    public boolean process(Set<? extends TypeElement> annotations, RoundEnvironment roundEnv) {
//        if (roundEnv.processingOver()) {
//            return true;
//        }
//        roundEnv.getElementsAnnotatedWith(Mapper.class)
//                .stream()
//                .filter(element -> element instanceof TypeElement)
//                .map(element -> (TypeElement)element)
//                .forEach(typeElement -> {
//                    processingEnv.getFiler().createSourceFile(model.type.packageName + "." + genSimpleName(model) + "\n")
//                });
//
//
//        return false;
//    }
//}
