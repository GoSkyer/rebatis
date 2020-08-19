package org.gosky.rebatis.processor

import com.google.auto.service.AutoService
import io.vertx.codegen.DataObjectModel
import io.vertx.codegen.ModelProvider
import io.vertx.codegen.PropertyInfo
import io.vertx.codegen.TableModel
import io.vertx.codegen.format.CamelCase
import io.vertx.codegen.format.Case
import io.vertx.codegen.format.LowerCamelCase
import io.vertx.codegen.format.SnakeCase
import io.vertx.codegen.type.AnnotationValueInfo
import io.vertx.codegen.type.ClassTypeInfo
import io.vertx.codegen.type.TypeInfo
import io.vertx.codegen.type.TypeMirrorFactory
import org.apache.commons.lang3.StringUtils
import java.io.IOException
import java.io.PrintWriter
import java.io.StringWriter
import java.util.*
import javax.annotation.processing.*
import javax.lang.model.SourceVersion
import javax.lang.model.element.Element
import javax.lang.model.element.TypeElement
import javax.lang.model.util.Elements
import javax.lang.model.util.Types
import javax.persistence.Table
import javax.tools.Diagnostic

@AutoService(Processor::class)
@SupportedSourceVersion(SourceVersion.RELEASE_8)
class MapperGen : AbstractProcessor() {
    private val MODEL_PROVIDER = ModelProvider { env: ProcessingEnvironment?, typeFactory: TypeMirrorFactory?, elt: TypeElement ->
        if (elt.getAnnotation(Table::class.java) != null) {
            return@ModelProvider TableModel(env, typeFactory, elt)
        } else {
            return@ModelProvider null
        }
    }
    private var elementUtils: Elements? = null
    private var typeUtils: Types? = null
    private var tmf: TypeMirrorFactory? = null

    @Synchronized
    override fun init(processingEnv: ProcessingEnvironment) {
        super.init(processingEnv)
        elementUtils = processingEnv.elementUtils
        typeUtils = processingEnv.typeUtils
        tmf = TypeMirrorFactory(elementUtils, typeUtils)
    }

    override fun getSupportedAnnotationTypes(): Set<String> {
        return setOf(Table::class.java.canonicalName)
    }

    override fun process(annotations: Set<TypeElement>, round: RoundEnvironment): Boolean {
        if (round.processingOver()) {
            return true
        }

        round.getElementsAnnotatedWith(Table::class.java)
                .stream()
                .filter { elt: Element? -> elt is TypeElement }
                .map { elt: Element? -> elt as TypeElement? }
                .forEach { te: TypeElement? ->
                    val model = MODEL_PROVIDER.getModel(processingEnv, tmf, te) as TableModel
                    if (model != null) {
                        model.process()
                        processingEnv.messager.printMessage(Diagnostic.Kind.NOTE, "anno=" + model.annotations[0].getMember("name"))
                        val content = render(model)
                        if (content.length > 0) {
                            try {
                                val target = processingEnv.filer.createSourceFile(model.type.packageName + "." + genSimpleName(model))
                                try {
                                    target.openWriter().use { writer ->
                                        writer.write(content)
                                        processingEnv.messager.printMessage(Diagnostic.Kind.NOTE, "Generated model " + model.fqn)
                                    }
                                } catch (e: IOException) {
                                    e.printStackTrace()
                                    processingEnv.messager.printMessage(Diagnostic.Kind.ERROR, "Generated model error:" + e.message)
                                }
                            } catch (e: Exception) {
                                e.printStackTrace()
                            }
                        }
                    }
                }

        return true
    }

    //    @Override
    //    public String filename(DataObjectModel model) {
    //        if (model.isClass()) {
    //            return getAnnotation(model)
    //                    .map(ann -> model.getType().getPackageName() + "." + genSimpleName(model) + ".java")
    //                    .orElse(null);
    //        }
    //        return null;
    //    }
    private fun getAnnotation(model: DataObjectModel): Optional<AnnotationValueInfo> {
        return model
                .annotations
                .stream().filter { ann: AnnotationValueInfo -> ann.name == Table::class.java.name }
                .findFirst()
    }

    private fun genSimpleName(model: TableModel): String {
        var tableName = model.annotations[0].getMember("name").toString()
        if (StringUtils.isEmpty(tableName)) {
            tableName = model.type.getSimpleName(CamelCase.INSTANCE)
        } else {
            tableName = snake2Camel(tableName)
        }
        return tableName + "BaseMapper"
    }

    private val formatter: Case? = null
    fun render(model: TableModel): String {
        val buffer = StringWriter()
        val writer = PrintWriter(buffer)
        var tableName = model.annotations[0].getMember("name").toString()
        if (StringUtils.isEmpty(tableName)) {
            tableName = model.type.getSimpleName(SnakeCase.INSTANCE)
        }
        //        formatter = getCase(model, "formatter");
        writer.print("""
    package ${model.type.packageName};
    
    """.trimIndent())
        writer.print("\n")
        writer.println("""
    import org.slf4j.Logger;
    import org.slf4j.LoggerFactory;
    """.trimIndent())
        writer.println("""
    import io.vertx.core.Handler;
    import io.vertx.mysqlclient.MySQLClient;
    import io.vertx.sqlclient.SqlResult;
    """.trimIndent())
        writer.print("import org.gosky.adapter.DefaultCall;\n")
        writer.print("/**\n")
        writer.print(""" * Mapper for {@link ${model.type.simpleName}}.
""")
        writer.print(""" * NOTE: This class has been automatically generated from the {@link ${model.type.simpleName}} original class using Vert.x codegen.
""")
        writer.print(" */\n")
        writer.print("""public class ${genSimpleName(model)} extends org.gosky.common.BaseMapper {
""")
        writer.println("    private Logger logger = LoggerFactory.getLogger(org.gosky.common.BaseMapper.class);")
        writer.println("    public " + genSimpleName(model) + " (org.gosky.Rebatis rebatis) {")
        writer.println("        super(rebatis);")
        writer.println("    }\n")

        //selectById
        writer.println("   public org.gosky.adapter.Call<" + model.type.simpleName + "> selectByPrimaryKey(Long id) {")
        writer.println("        java.util.Map<String, Object> parameters = new java.util.HashMap<>();")
        writer.println("        parameters.put(\"id\", id);")
        writer.println("        String template = \"select * from $tableName where id = #{id}\";")
        writer.println("        long start = System.currentTimeMillis();")
        writer.println("        io.vertx.core.Future<" + model.type.simpleName + "> execute = io.vertx.sqlclient.templates.SqlTemplate")
        writer.println("                .forQuery(client, template)")
        writer.println("                .mapTo(" + model.type.simpleName + ".class)")
        writer.println("                .execute(parameters)")
        writer.println("                .map(users -> {")
        writer.println("                    io.vertx.sqlclient.RowIterator<" + model.type.simpleName + "> iterator = users.iterator();")
        writer.println("""                    if (iterator.hasNext()) {
                        return iterator.next();
                    } else {
                        return null;
                    }
                })""")
        writer.println("                .onComplete(event -> logger.info(\"run sql={}, params={}, duration={}, result={}\", template, parameters, System.currentTimeMillis() - start, event.result()));")
        writer.println("        return new org.gosky.adapter.DefaultCall(execute);")
        writer.println("}\n")

        //insert
        writer.println("""    public DefaultCall<Long> insert(${model.type.simpleName} val) {
""")
        writer.print("        StringBuilder sql = new StringBuilder();\n")
        writer.print("        sql.append(\"insert into $tableName (\");\n")
        model.propertyMap.values.forEachIndexed { index, propertyInfo ->
            writer.print("""        if (val.${propertyInfo.getterMethod}() != null) {
""")
            if (index < (model.propertyMap.values.size - 1)) {
                writer.print("""            sql.append("${propertyInfo.name}, ");
""")
            } else {
                writer.print("""            sql.append("${propertyInfo.name} ");
""")
            }
            writer.print("        }\n")
        }
        writer.print("        sql.append(\") values (\");\n")
        model.propertyMap.values.forEachIndexed { index, propertyInfo ->
            writer.print("""        if (val.${propertyInfo.getterMethod}() != null) {
""")
            if (index < (model.propertyMap.values.size - 1)) {
                writer.print("""            sql.append("#{${propertyInfo.name}}, ");
""")
            } else {
                writer.print("""            sql.append("#{${propertyInfo.name}} ");
""")
            }
            writer.print("        }\n")
        }
        writer.print("        sql.append(\");\");\n")
        writer.println("        long start = System.currentTimeMillis();")
        writer.print("""        io.vertx.core.Future<Long> execute = io.vertx.sqlclient.templates.SqlTemplate
                .forQuery(client, sql.toString())""")
        writer.print("""                .mapFrom(${model.type.simpleName}.class)
""")
        writer.print("""                .execute(val)
                .map(rowSet -> rowSet.property(MySQLClient.LAST_INSERTED_ID))
                .onComplete(event -> logger.info("run sql={}, params={}, duration={}, result={}", sql, val, System.currentTimeMillis() - start, event.result()));
""")
        writer.print("        return new org.gosky.adapter.DefaultCall(execute);\n")
        writer.print("    }\n")

        //update
        writer.println("""    public DefaultCall<Long> update(${model.type.simpleName} val) {
""")
        writer.print("        StringBuilder sql = new StringBuilder();\n")
        writer.print("""        sql.append("update $tableName set ");
""")
        model.propertyMap.values.forEachIndexed { index, propertyInfo ->
            writer.print("""        if (val.${propertyInfo.getterMethod}() != null) {
""")
            writer.print("""            sql.append("${propertyInfo.name} = #{${propertyInfo.name}} ,"); 
""")
            writer.print("        }\n")
        }
        writer.print("        sql.deleteCharAt(sql.length() - 1);\n")
        writer.print("""        sql.append(" where 1=1 ");
""")
        model.propertyMap.values.forEachIndexed { index, propertyInfo ->
            writer.print("""        if (val.${propertyInfo.getterMethod}() != null) {
""")
            writer.print("""            sql.append("and ${propertyInfo.name} = #{${propertyInfo.name}} ");
""")
            writer.print("        }\n")
        }
        writer.print("        sql.append(\";\");\n")
        writer.println("        long start = System.currentTimeMillis();")
        writer.print("""        io.vertx.core.Future<Long> execute = io.vertx.sqlclient.templates.SqlTemplate
                .forQuery(client, sql.toString())""")
        writer.print("""                .mapFrom(${model.type.simpleName}.class)
""")
        writer.print("""                .execute(val)
                .map(rowSet -> rowSet.property(MySQLClient.LAST_INSERTED_ID))
                .onSuccess(event -> logger.info("run sql success={}, params={}, duration={}, result={}", sql, val, System.currentTimeMillis() - start, event))
                .onFailure(throwable -> logger.error("run sql failure={}, params={}, duration={}", sql, val, System.currentTimeMillis() - start, throwable));
""")
        writer.print("        return new org.gosky.adapter.DefaultCall(execute);\n")
        writer.print("    }\n")

        //templateOne
        writer.println("    org.gosky.adapter.Call<" + model.type.simpleName + """> templateOne(${model.type.simpleName} _m) {""")
        writer.print("        StringBuilder sql = new StringBuilder();\n")
        writer.println("""        sql.append("select * from $tableName where 1=1 ");""")
        model.propertyMap.values.forEachIndexed { index, propertyInfo ->
            writer.print("""        if (_m.${propertyInfo.getterMethod}() != null) {
""")
            writer.print("""            sql.append("and ${propertyInfo.name} = #{${propertyInfo.name}} ");
""")
            writer.print("        }\n")
        }
        writer.println("""        sql.append(" limit 1;");""")
        writer.println("        long start = System.currentTimeMillis();")
        writer.println("        io.vertx.core.Future<" + model.type.simpleName + "> execute = io.vertx.sqlclient.templates.SqlTemplate")
        writer.println("                .forQuery(client, sql.toString())")
        writer.println("                .mapTo(" + model.type.simpleName + ".class)")
        writer.print("""                .mapFrom(${model.type.simpleName}.class)
""")
        writer.println("                .execute(_m)")
        writer.println("                .map(users -> {")
        writer.println("                    io.vertx.sqlclient.RowIterator<" + model.type.simpleName + "> iterator = users.iterator();")
        writer.println("""                    if (iterator.hasNext()) {
                        return iterator.next();
                    } else {
                        return null;
                    }
                })""")
        writer.println("""                                .onSuccess(event -> logger.info("run sql success={}, params={}, duration={}, result={}", sql, _m, System.currentTimeMillis() - start, event))
                .onFailure(throwable -> logger.error("run sql failure={}, params={}, duration={}", sql, _m, System.currentTimeMillis() - start, throwable));""")
        writer.println("        return new org.gosky.adapter.DefaultCall(execute);")
        writer.println("}\n")

        //delete
        writer.println("""    public DefaultCall<Long> delete(Long id) {
""")
        writer.print("        StringBuilder sql = new StringBuilder();\n")
        writer.print("""        sql.append("delete from $tableName where id = #{id}");
""")
        writer.println("        java.util.Map<String, Object> parameters = new java.util.HashMap<>();")
        writer.println("        parameters.put(\"id\", id);")
        writer.println("        long start = System.currentTimeMillis();")
        writer.print("""        io.vertx.core.Future<Long> execute = io.vertx.sqlclient.templates.SqlTemplate
                .forQuery(client, sql.toString())""")
        writer.print("""                .execute(parameters)
                .map(rowSet -> rowSet.property(MySQLClient.LAST_INSERTED_ID))
                .onSuccess(event -> logger.info("run sql success={}, params={}, duration={}, result={}", sql, id, System.currentTimeMillis() - start, event))
                .onFailure(throwable -> logger.error("run sql failure={}, params={}, duration={}", sql, id, System.currentTimeMillis() - start, throwable));
""")
        writer.print("        return new org.gosky.adapter.DefaultCall(execute);\n")
        writer.print("    }\n")

        writer.print("}\n")
        return buffer.toString()
    }

    private fun camel2Snake(camelCae: String): String {
        val atoms = CamelCase.INSTANCE.parse(camelCae)
        return SnakeCase.INSTANCE.format(atoms)
    }

    private fun snake2Camel(camelCae: String): String {
        val atoms = SnakeCase.INSTANCE.parse(camelCae)
        return CamelCase.INSTANCE.format(atoms)
    }

    private fun getCase(model: DataObjectModel, name: String): Case {
        val abc = getAnnotation(model).get()
        val cti: TypeInfo = abc.getMember(name) as ClassTypeInfo
        return when (cti.name) {
            "io.vertx.codegen.format.CamelCase" -> CamelCase.INSTANCE
            "io.vertx.codegen.format.SnakeCase" -> SnakeCase.INSTANCE
            "io.vertx.codegen.format.LowerCamelCase" -> LowerCamelCase.INSTANCE
            else -> throw UnsupportedOperationException()
        }
    }
}