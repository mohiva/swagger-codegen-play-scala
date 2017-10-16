package io.swagger.codegen.languages;

import com.google.common.base.CaseFormat;
import com.samskivert.mustache.Mustache;
import com.samskivert.mustache.Template;
import io.swagger.codegen.*;
import io.swagger.models.Response;
import io.swagger.models.auth.SecuritySchemeDefinition;
import io.swagger.models.properties.*;
import org.apache.commons.lang3.StringUtils;

import java.io.File;
import java.io.IOException;
import java.io.StringWriter;
import java.io.Writer;
import java.util.*;

/**
 * Client generator which is based on the PlayWS library.
 *
 * This client is based on the `AkkaScalaClientCodegen`. For more information please visit:
 * https://github.com/mohiva/swagger-codegen-play-scala
 */
public class PlayScalaClientCodegen extends DefaultCodegen implements CodegenConfig {
    private String mainPackage = "io.swagger.client";
    private String invokerPackage = mainPackage + ".core";
    private String sourceFolder = "src/main/scala";
    private String resourcesFolder = "src/main/resources";

    /**
     * Project based values for the build.sbt file.
     */
    private String projectOrganization = "io.swagger";
    private String projectName = "swagger-client";
    private String projectVersion = "1.0.0";
    private String scalaVersion = "2.12.3";
    private String playVersion = "2.6.6";

    /**
     * Typesafe config based values.
     */
    private String configPath = mainPackage;

    /**
     * Config values which affect the generation of the client.
     */
    private boolean renderJavadoc = true;
    private boolean removeOAuthSecurities = true;

    /**
     * If set to true, only the default response (the one with le lowest 2XX code) will be considered as a success, and all
     * others as ApiErrors.
     * If set to false, all responses defined in the model will be considered as a success upon reception. Only http errors,
     * unmarshalling problems and any other RuntimeException will be considered as ApiErrors.
     */
    private boolean onlyOneSuccess = true;

    /**
     * Some custom codegen constants.
     */
    private class CustomCodegenConstants {
        static final String CONFIG_PATH = "configPath";
        static final String CONFIG_PATH_DESC = "path under which the config must be defined";

        static final String PROJECT_ORGANIZATION = "projectOrganization";
        static final String PROJECT_ORGANIZATION_DESC = "project organization in generated build.sbt";

        static final String PROJECT_NAME = "projectName";
        static final String PROJECT_NAME_DESC = "project name in generated build.sbt";

        static final String PROJECT_VERSION = "projectVersion";
        static final String PROJECT_VERSION_DESC = "project version in generated build.sbt";

        static final String SCALA_VERSION = "scalaVersion";
        static final String SCALA_VERSION_DESC = "the Scala version to use in generated build.sbt";

        static final String PLAY_VERSION = "playVersion";
        static final String PLAY_VERSION_DESC = "the Play version to use in generated build.sbt";
    }

    /**
     * The class constructor.
     */
    public PlayScalaClientCodegen() {
        super();
        outputFolder = "generated-code/scala";
        modelTemplateFiles.put("model.mustache", ".scala");
        apiTemplateFiles.put("api.mustache", ".scala");
        embeddedTemplateDir = templateDir = "play-scala";

        apiPackage = mainPackage + ".api";
        modelPackage = mainPackage + ".model";

        reservedWords = new HashSet<>(
            Arrays.asList(
                "abstract", "case", "catch", "class", "def", "do", "else", "extends",
                "false", "final", "finally", "for", "forSome", "if", "implicit",
                "import", "lazy", "match", "new", "null", "object", "override", "package",
                "private", "protected", "return", "sealed", "super", "this", "throw",
                "trait", "try", "true", "type", "val", "var", "while", "with", "yield")
        );

        if (renderJavadoc) {
            additionalProperties.put("javadocRenderer", new JavadocLambda());
        }
        additionalProperties.put("fnCapitalize", new CapitalizeLambda());
        additionalProperties.put("fnCamelize", new CamelizeLambda(false));
        additionalProperties.put("fnEnumEntry", new EnumEntryLambda());
        additionalProperties.put("onlyOneSuccess", onlyOneSuccess);

        supportingFiles.add(new SupportingFile("sbt.mustache", "", "build.sbt"));
        supportingFiles.add(new SupportingFile("reference.mustache", resourcesFolder, "reference.conf"));

        importMapping.remove("Seq");
        importMapping.remove("List");
        importMapping.remove("Set");
        importMapping.remove("Map");

        importMapping.put("OffsetDateTime", "java.time.OffsetDateTime");
        importMapping.put("LocalDate", "java.time.LocalDate");

        typeMapping = new HashMap<>();
        typeMapping.put("array", "Seq");
        typeMapping.put("set", "Set");
        typeMapping.put("boolean", "Boolean");
        typeMapping.put("string", "String");
        typeMapping.put("int", "Int");
        typeMapping.put("integer", "Int");
        typeMapping.put("long", "Long");
        typeMapping.put("float", "Float");
        typeMapping.put("byte", "Byte");
        typeMapping.put("short", "Short");
        typeMapping.put("char", "Char");
        typeMapping.put("long", "Long");
        typeMapping.put("double", "Double");
        typeMapping.put("object", "Any");
        typeMapping.put("file", "ApiFile");
        typeMapping.put("number", "Double");
        typeMapping.put("DateTime", "OffsetDateTime");
        typeMapping.put("date", "LocalDate");

        languageSpecificPrimitives = new HashSet<>(
            Arrays.asList(
                "String",
                "boolean",
                "Boolean",
                "Double",
                "Int",
                "Long",
                "Float",
                "Object",
                "List",
                "Seq",
                "Map")
        );
        instantiationTypes.put("array", "ListBuffer");
        instantiationTypes.put("map", "Map");

        cliOptions.add(new CliOption(CodegenConstants.MODEL_PACKAGE, CodegenConstants.MODEL_PACKAGE_DESC));
        cliOptions.add(new CliOption(CodegenConstants.API_PACKAGE, CodegenConstants.API_PACKAGE_DESC));
        cliOptions.add(new CliOption(CodegenConstants.INVOKER_PACKAGE, CodegenConstants.INVOKER_PACKAGE_DESC));
        cliOptions.add(new CliOption(CustomCodegenConstants.CONFIG_PATH, CustomCodegenConstants.CONFIG_PATH_DESC));
        cliOptions.add(new CliOption(CustomCodegenConstants.PROJECT_ORGANIZATION, CustomCodegenConstants.PROJECT_ORGANIZATION_DESC));
        cliOptions.add(new CliOption(CustomCodegenConstants.PROJECT_NAME, CustomCodegenConstants.PROJECT_NAME_DESC));
        cliOptions.add(new CliOption(CustomCodegenConstants.PROJECT_VERSION, CustomCodegenConstants.PROJECT_VERSION_DESC));
        cliOptions.add(new CliOption(CustomCodegenConstants.SCALA_VERSION, CustomCodegenConstants.SCALA_VERSION_DESC));
        cliOptions.add(new CliOption(CustomCodegenConstants.PLAY_VERSION, CustomCodegenConstants.PLAY_VERSION_DESC));
    }

    @Override
    public void processOpts() {
        super.processOpts();

        if (additionalProperties.containsKey(CodegenConstants.INVOKER_PACKAGE)) {
            invokerPackage = (String) additionalProperties.get(CodegenConstants.INVOKER_PACKAGE);
        }

        if (additionalProperties.containsKey(CustomCodegenConstants.CONFIG_PATH)) {
            configPath = (String) additionalProperties.get(CustomCodegenConstants.CONFIG_PATH);
        }

        if (additionalProperties.containsKey(CustomCodegenConstants.PROJECT_ORGANIZATION)) {
            projectOrganization = (String) additionalProperties.get(CustomCodegenConstants.PROJECT_ORGANIZATION);
        }

        if (additionalProperties.containsKey(CustomCodegenConstants.PROJECT_NAME)) {
            projectName = (String) additionalProperties.get(CustomCodegenConstants.PROJECT_NAME);
        }

        if (additionalProperties.containsKey(CustomCodegenConstants.PROJECT_VERSION)) {
            projectVersion = (String) additionalProperties.get(CustomCodegenConstants.PROJECT_VERSION);
        }

        if (additionalProperties.containsKey(CustomCodegenConstants.SCALA_VERSION)) {
            scalaVersion = (String) additionalProperties.get(CustomCodegenConstants.SCALA_VERSION);
        }

        if (additionalProperties.containsKey(CustomCodegenConstants.PLAY_VERSION)) {
            playVersion = (String) additionalProperties.get(CustomCodegenConstants.PLAY_VERSION);
        }

        additionalProperties.put(CodegenConstants.INVOKER_PACKAGE, invokerPackage);
        additionalProperties.put(CustomCodegenConstants.CONFIG_PATH, configPath);
        additionalProperties.put(CustomCodegenConstants.PROJECT_ORGANIZATION, projectOrganization);
        additionalProperties.put(CustomCodegenConstants.PROJECT_NAME, projectName);
        additionalProperties.put(CustomCodegenConstants.PROJECT_VERSION, projectVersion);
        additionalProperties.put(CustomCodegenConstants.SCALA_VERSION, scalaVersion);
        additionalProperties.put(CustomCodegenConstants.PLAY_VERSION, playVersion);

        final String invokerFolder = (sourceFolder + File.separator + invokerPackage).replace(".", File.separator);
        supportingFiles.add(new SupportingFile("apiFile.mustache", invokerFolder, "ApiFile.scala"));
        supportingFiles.add(new SupportingFile("apiConfig.mustache", invokerFolder, "ApiConfig.scala"));
        supportingFiles.add(new SupportingFile("apiRequest.mustache", invokerFolder, "ApiRequest.scala"));
        supportingFiles.add(new SupportingFile("apiResponse.mustache", invokerFolder, "ApiResponse.scala"));
        supportingFiles.add(new SupportingFile("apiInvoker.mustache", invokerFolder, "ApiInvoker.scala"));
        supportingFiles.add(new SupportingFile("apiImplicits.mustache", invokerFolder, "ApiImplicits.scala"));

        importMapping.put("ApiFile", invokerPackage + ".ApiFile");
    }

    /**
     * A codegen property that is a container type has an inner type that can either be a container itself or a
     * non-container type. This method returns the non-container type of a codegen property by traversing recursively
     * the `items` of a `CodegenProperty`.
     *
     * @param property The codegen property to traverse recursively.
     * @return The non-container item.
     */
    private CodegenProperty getNonContainerItem(CodegenProperty property) {
        if (property.isContainer) {
            return getNonContainerItem(property.items);
        }

        return property;
    }

    /**
     * In Scala we must not import Models which are located in the same package. If we do that, we get an warning
     * like this:
     *
     * ```
     * imported `Tag' is permanently hidden by definition of object Tag in package models
     * ```
     *
     * So we remove all model imports from model files to avoid this warning.
     */
    @Override
    public Map<String, Object> postProcessModels(Map<String, Object> objectMap) {
        Map<String, Object> processed = super.postProcessModels(objectMap);

        // Get the model imports
        List<String> modelImports = new ArrayList<>();
        List<Map<String, Object>> models = (List<Map<String, Object>>) processed.get("models");
        for (Map<String, Object> model : models) {
            Object value = model.get("model");
            if (value instanceof CodegenModel) {
                CodegenModel codegenModel = (CodegenModel) value;
                for (CodegenProperty property : codegenModel.allVars) {
                    CodegenProperty item = getNonContainerItem(property);
                    if (item != null && !item.isPrimitiveType && !importMapping.containsKey(item.datatype)) {
                        if (!modelImports.contains(item.datatype)) {
                            modelImports.add(toModelImport(item.datatype));
                        }
                    }
                }
            }
        }

        // Remove the model imports
        Iterator<Map<String, String>> imports = ((List<Map<String, String>>) processed.get("imports")).iterator();
        while (imports.hasNext()) {
            String value = imports.next().get("import");
            if (modelImports.contains(value)) {
                imports.remove();
            }
        }

        return processed;
    }

    /**
     * Convert Swagger Response object to Codegen Response object
     *
     * @param responseCode HTTP response code
     * @param response Swagger Response object
     * @return Codegen Response object
     */
    @Override
    public CodegenResponse fromResponse(String responseCode, Response response) {
        CodegenResponse r = super.fromResponse(responseCode, response);

        if (response.getSchema() instanceof FileProperty) {
            r.primitiveType = true;
        }

        return r;
    }

    @Override
    public CodegenType getTag() {
        return CodegenType.CLIENT;
    }

    @Override
    public String getName() {
        return "play-scala";
    }

    @Override
    public String getHelp() {
        return "Generates a Scala client library base on PlayWS.";
    }

    @Override
    public String escapeReservedWord(String name) {
        return "`" + name + "`";
    }

    @Override
    public String apiFileFolder() {
        return outputFolder + "/" + sourceFolder + "/" + apiPackage().replace('.', File.separatorChar);
    }

    @Override
    public String modelFileFolder() {
        return outputFolder + "/" + sourceFolder + "/" + modelPackage().replace('.', File.separatorChar);
    }

    @Override
    public String getTypeDeclaration(Property p) {
        if (p instanceof ArrayProperty) {
            ArrayProperty ap = (ArrayProperty) p;
            Property inner = ap.getItems();
            return getSwaggerType(p) + "[" + getTypeDeclaration(inner) + "]";
        } else if (p instanceof MapProperty) {
            MapProperty mp = (MapProperty) p;
            Property inner = mp.getAdditionalProperties();

            return getSwaggerType(p) + "[String, " + getTypeDeclaration(inner) + "]";
        }

        return super.getTypeDeclaration(p);
    }

    @Override
    public List<CodegenSecurity> fromSecurity(Map<String, SecuritySchemeDefinition> schemes) {
        final List<CodegenSecurity> codegenSecurities = super.fromSecurity(schemes);
        if (!removeOAuthSecurities) {
            return codegenSecurities;
        }

        // Remove OAuth securities
        Iterator<CodegenSecurity> it = codegenSecurities.iterator();
        while (it.hasNext()) {
            final CodegenSecurity security = it.next();
            if (security.isOAuth) {
                it.remove();
            }
        }
        // Adapt 'hasMore'
        it = codegenSecurities.iterator();
        while (it.hasNext()) {
            final CodegenSecurity security = it.next();
            security.hasMore = it.hasNext();
        }

        if (codegenSecurities.isEmpty()) {
            return null;
        }
        return codegenSecurities;
    }

    @Override
    public String toOperationId(String operationId) {
        // throw exception if method name is empty
        if (StringUtils.isEmpty(operationId)) {
            throw new RuntimeException("Empty method name (operationId) not allowed");
        }

        return super.toOperationId(CaseFormat.UPPER_CAMEL.to(CaseFormat.LOWER_CAMEL, operationId));
    }

    @Override
    public String toParamName(String name) {
        return formatIdentifier(name, false);
    }

    @Override
    public String toVarName(String name) {
        return formatIdentifier(name, false);
    }

    @Override
    public String toEnumName(CodegenProperty property) {
        return formatIdentifier(property.baseName, true);
    }

    @Override
    public String getSwaggerType(Property p) {
        String swaggerType = super.getSwaggerType(p);
        String type;
        if (typeMapping.containsKey(swaggerType)) {
            type = typeMapping.get(swaggerType);
            if (languageSpecificPrimitives.contains(type)) {
                return toModelName(type);
            }
        } else {
            type = swaggerType;
        }
        return toModelName(type);
    }

    @Override
    public String toInstantiationType(Property p) {
        if (p instanceof MapProperty) {
            MapProperty ap = (MapProperty) p;
            String inner = getSwaggerType(ap.getAdditionalProperties());
            return instantiationTypes.get("map") + "[String, " + inner + "]";
        } else if (p instanceof ArrayProperty) {
            ArrayProperty ap = (ArrayProperty) p;
            String inner = getSwaggerType(ap.getItems());
            return instantiationTypes.get("array") + "[" + inner + "]";
        } else {
            return null;
        }
    }

    @Override
    public String toDefaultValue(Property p) {
        if (!p.getRequired()) {
            return "None";
        }
        if (p instanceof StringProperty) {
            return "null";
        } else if (p instanceof BooleanProperty) {
            return "null";
        } else if (p instanceof DateProperty) {
            return "null";
        } else if (p instanceof DateTimeProperty) {
            return "null";
        } else if (p instanceof DoubleProperty) {
            return "null";
        } else if (p instanceof FloatProperty) {
            return "null";
        } else if (p instanceof IntegerProperty) {
            return "null";
        } else if (p instanceof LongProperty) {
            return "null";
        } else if (p instanceof MapProperty) {
            MapProperty ap = (MapProperty) p;
            String inner = getSwaggerType(ap.getAdditionalProperties());
            return "Map[String, " + inner + "].empty ";
        } else if (p instanceof ArrayProperty) {
            ArrayProperty ap = (ArrayProperty) p;
            String inner = getSwaggerType(ap.getItems());
            return "Seq[" + inner + "].empty ";
        } else {
            return "null";
        }
    }

    @Override
    public String escapeUnsafeCharacters(String input) {
        return input.replace("*/", "*_/").replace("/*", "/_*");
    }

    @Override
    public String escapeQuotationMark(String input) {
        // remove " to avoid code injection
        return input.replace("\"", "");
    }

    private String formatIdentifier(String name, boolean capitalized) {
        String identifier = camelize(name, true);
        if (capitalized) {
            identifier = StringUtils.capitalize(identifier);
        }
        if (identifier.matches("[a-zA-Z_$][\\w_$]+") && !reservedWords.contains(identifier)) {
            return identifier;
        }
        return escapeReservedWord(identifier);
    }

    private static abstract class CustomLambda implements Mustache.Lambda {
        @Override
        public void execute(Template.Fragment frag, Writer out) throws IOException {
            final StringWriter tempWriter = new StringWriter();
            frag.execute(tempWriter);
            out.write(formatFragment(tempWriter.toString()));
        }

        public abstract String formatFragment(String fragment);
    }


    private static class JavadocLambda extends CustomLambda {
        @Override
        public String formatFragment(String fragment) {
            final String[] lines = fragment.split("\\r?\\n");
            final StringBuilder sb = new StringBuilder();
            sb.append("  /**\n");
            for (String line : lines) {
                sb.append("   * ").append(line).append("\n");
            }
            sb.append("   */\n");
            return sb.toString();
        }
    }

    private static class CapitalizeLambda extends CustomLambda {
        @Override
        public String formatFragment(String fragment) {
            return StringUtils.capitalize(fragment);
        }
    }

    private static class CamelizeLambda extends CustomLambda {
        private final boolean capitalizeFirst;

        CamelizeLambda(boolean capitalizeFirst) {
            this.capitalizeFirst = capitalizeFirst;
        }

        @Override
        public String formatFragment(String fragment) {
            return camelize(fragment, !capitalizeFirst);
        }
    }

    private class EnumEntryLambda extends CustomLambda {
        @Override
        public String formatFragment(String fragment) {
            return formatIdentifier(fragment, true);
        }
    }
}
