package org.gosky.rebatis.processor;

import io.vertx.codegen.DataObjectModel;
import io.vertx.codegen.Generator;
import io.vertx.codegen.format.CamelCase;
import io.vertx.codegen.format.Case;
import io.vertx.codegen.format.LowerCamelCase;
import io.vertx.codegen.format.SnakeCase;
import io.vertx.codegen.type.AnnotationValueInfo;
import io.vertx.codegen.type.ClassTypeInfo;
import io.vertx.codegen.type.TypeInfo;

import javax.persistence.Table;
import java.io.PrintWriter;
import java.io.StringWriter;
import java.lang.annotation.Annotation;
import java.util.*;

public class MapperGen extends Generator<DataObjectModel> {


    public MapperGen() {
        kinds = Collections.singleton("dataObject");
        name = "data_object_mappers";
    }


    @Override
    public Collection<Class<? extends Annotation>> annotations() {
//        env.getMessager().printMessage(Diagnostic.Kind.WARNING,"test1223123123");
        return Collections.singletonList(Table.class);
    }

    @Override
    public String filename(DataObjectModel model) {
        if (model.isClass()) {
            return getAnnotation(model)
                    .map(ann -> model.getType().getPackageName() + "." + genSimpleName(model) + ".java")
                    .orElse(null);
        }
        return null;
    }

    private Optional<AnnotationValueInfo> getAnnotation(DataObjectModel model) {
        return model
                .getAnnotations()
                .stream().filter(ann -> ann.getName().equals(Table.class.getName()))
                .findFirst();
    }

    private String genSimpleName(DataObjectModel model) {
        return model.getType().getSimpleName() + "BaseMapper";
    }


    private Case formatter;

    @Override
    public String render(DataObjectModel model, int index, int size, Map<String, Object> session) {
        StringWriter buffer = new StringWriter();
        PrintWriter writer = new PrintWriter(buffer);
        String visibility = model.isPublicConverter() ? "public" : "";
        String tableName = model.getType().getSimpleName(SnakeCase.INSTANCE);

//        formatter = getCase(model, "formatter");

        writer.print("package " + model.getType().getPackageName() + ";\n");
        writer.print("\n");
        writer.println("import org.slf4j.Logger;\n" +
                "import org.slf4j.LoggerFactory;");
        writer.println("import io.vertx.core.Handler;\n" +
                "import io.vertx.mysqlclient.MySQLClient;\n" +
                "import io.vertx.sqlclient.SqlResult;");
        writer.print("import org.gosky.adapter.DefaultCall;\n");
        writer.print("/**\n");
        writer.print(" * Mapper for {@link " + model.getType().getSimpleName() + "}.\n");
        writer.print(" * NOTE: This class has been automatically generated from the {@link " + model.getType().getSimpleName() + "} original class using Vert.x codegen.\n");
        writer.print(" */\n");

        writer.print("public class " + genSimpleName(model) + " extends org.gosky.common.BaseMapper {\n");
        writer.println("    private Logger logger = LoggerFactory.getLogger(org.gosky.common.BaseMapper.class);");
        writer.println("    public " + genSimpleName(model) + " (org.gosky.Rebatis rebatis) {");
        writer.println("        super(rebatis);");
        writer.println("    }\n");
        writer.println("    org.gosky.adapter.Call<" + model.getType().getSimpleName() + "> selectByPrimaryKey(Long id) {");
        writer.println("        java.util.Map<String, Object> parameters = new java.util.HashMap<>();");
        writer.println("        parameters.put(\"id\", id);");
        writer.println("        String template = \"select * from " + tableName + " where id = #{id}\";");
        writer.println("        long start = System.currentTimeMillis();");
        writer.println("        io.vertx.core.Future<" + model.getType().getSimpleName() + "> execute = io.vertx.sqlclient.templates.SqlTemplate");
        writer.println("                .forQuery(client, template)");
        writer.println("                .mapTo(" + model.getType().getSimpleName() + ".class)");
        writer.println("                .execute(parameters)");
        writer.println("                .map(users -> {");
        writer.println("                    io.vertx.sqlclient.RowIterator<" + model.getType().getSimpleName() + "> iterator = users.iterator();");
        writer.println("                    if (iterator.hasNext()) {\n" +
                "                        return iterator.next();\n" +
                "                    } else {\n" +
                "                        return null;\n" +
                "                    }\n" +
                "                })");
        writer.println("                .onComplete(event -> logger.info(\"run sql={}, params={}, duration={}, result={}\", template, parameters, System.currentTimeMillis() - start, event.result()));");
        writer.println("        return new org.gosky.adapter.DefaultCall(execute);");
        writer.println("}\n");

        writer.println("    public DefaultCall<Long> insert(" + model.getType().getSimpleName() + " val) {\n");
        writer.print("        StringBuilder sql = new StringBuilder();\n");
        writer.print("        sql.append(\"insert into " + tableName + " (\");\n");
        model.getPropertyMap().forEach((s, propertyInfo) -> {
            writer.print("        if (val."+ propertyInfo.getGetterMethod() + "() != null) {\n");
            writer.print("            sql.append(\"" + propertyInfo.getName() + ", \");\n");
            writer.print("        }\n");
        });
//        writer.println("    @org.apache.ibatis.annotations.Insert(\"insert into user (id, name, age, sex) values (#{id}, #{name}, #{age}, #{sex} )\")");
        writer.print("        sql.append(\") values (\");\n");
        model.getPropertyMap().forEach((s, propertyInfo) -> {
            writer.print("        if (val."+ propertyInfo.getGetterMethod() + "() != null) {\n");
            writer.print("            sql.append(\"#{" + propertyInfo.getName() + "}, \");\n");
            writer.print("        }\n");
        });
        writer.print("        sql.append(\");\");\n");
        writer.println("        long start = System.currentTimeMillis();");
        writer.print("        io.vertx.core.Future<Long> execute = io.vertx.sqlclient.templates.SqlTemplate\n" +
                "                .forQuery(client, sql.toString())");
        writer.print("                .mapFrom(" + model.getType().getSimpleName() + ".class)\n");
        writer.print("                .execute(val)\n" +
                "                .map(rowSet -> rowSet.property(MySQLClient.LAST_INSERTED_ID))\n" +
                "                .onComplete(event -> logger.info(\"run sql={}, params={}, duration={}, result={}\", sql, val, System.currentTimeMillis() - start, event.result()));\n");
        writer.print("        return new org.gosky.adapter.DefaultCall(execute);\n");
        writer.print("    }\n");


        writer.print("}\n");
        return buffer.toString();
    }


    private String Camel2Snake(String camelCae) {
        List<String> atoms = CamelCase.INSTANCE.parse(camelCae);
        return SnakeCase.INSTANCE.format(atoms);
    }

    private Case getCase(DataObjectModel model, String name) {
        AnnotationValueInfo abc = getAnnotation(model).get();
        TypeInfo cti = (ClassTypeInfo) abc.getMember(name);
        switch (cti.getName()) {
            case "io.vertx.codegen.format.CamelCase":
                return CamelCase.INSTANCE;
            case "io.vertx.codegen.format.SnakeCase":
                return SnakeCase.INSTANCE;
            case "io.vertx.codegen.format.LowerCamelCase":
                return LowerCamelCase.INSTANCE;
            default:
                throw new UnsupportedOperationException();
        }
    }
}
