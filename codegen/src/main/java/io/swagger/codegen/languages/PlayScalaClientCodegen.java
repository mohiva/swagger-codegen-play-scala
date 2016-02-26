package io.swagger.codegen.languages;

import com.google.common.base.CaseFormat;
import com.samskivert.mustache.Mustache;
import com.samskivert.mustache.Template;
import io.swagger.codegen.*;
import io.swagger.models.auth.SecuritySchemeDefinition;
import io.swagger.models.properties.*;
import org.apache.commons.lang.StringUtils;

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
    protected String mainPackage = "io.swagger.client";
    protected String invokerPackage = mainPackage + ".core";
    protected String sourceFolder = "src/main/scala";
    protected String resourcesFolder = "src/main/resources";

    /**
     * Project based values for the build.sbt file.
     */
    protected String projectOrganization = "io.swagger";
    protected String projectName = "swagger-client";
    protected String projectVersion = "1.0.0";
    protected String scalaVersion = "2.11.7";
    protected String playVersion = "2.4.6";

    /**
     * Typesafe config based values.
     */
    protected String configPath = mainPackage;

    /**
     * Config values which affect the generation of the client.
     */
    protected boolean renderJavadoc = true;
    protected boolean removeOAuthSecurities = true;

    /**
     * If set to true, only the default response (the one with le lowest 2XX code) will be considered as a success, and all
     * others as ApiErrors.
     * If set to false, all responses defined in the model will be considered as a success upon reception. Only http errors,
     * unmarshalling problems and any other RuntimeException will be considered as ApiErrors.
     */
    protected boolean onlyOneSuccess = true;

    /**
     * Some custom codegen constants.
     */
    class CustomCodegenConstants {
        public static final String CONFIG_PATH = "configPath";
        public static final String CONFIG_PATH_DESC = "path under which the config must be defined";

        public static final String PROJECT_ORGANIZATION = "projectOrganization";
        public static final String PROJECT_ORGANIZATION_DESC = "project organization in generated build.sbt";

        public static final String PROJECT_NAME = "projectName";
        public static final String PROJECT_NAME_DESC = "project name in generated build.sbt";

        public static final String PROJECT_VERSION = "projectVersion";
        public static final String PROJECT_VERSION_DESC = "project version in generated build.sbt";

        public static final String SCALA_VERSION = "scalaVersion";
        public static final String SCALA_VERSION_DESC = "the Scala version to use in generated build.sbt";

        public static final String PLAY_VERSION = "playVersion";
        public static final String PLAY_VERSION_DESC = "the Play version to use in generated build.sbt";
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

        importMapping.put("DateTime", "org.joda.time.DateTime");

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
        typeMapping.put("file", "File");
        typeMapping.put("number", "Double");

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
                "Map",
                "File")
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
        } else {
            additionalProperties.put(CodegenConstants.INVOKER_PACKAGE, invokerPackage);
        }

        if (additionalProperties.containsKey(CustomCodegenConstants.CONFIG_PATH)) {
            configPath = (String) additionalProperties.get(CustomCodegenConstants.CONFIG_PATH);
        } else {
            additionalProperties.put(CustomCodegenConstants.CONFIG_PATH, configPath);
        }

        if (additionalProperties.containsKey(CustomCodegenConstants.PROJECT_ORGANIZATION)) {
            projectOrganization = (String) additionalProperties.get(CustomCodegenConstants.PROJECT_ORGANIZATION);
        } else {
            additionalProperties.put(CustomCodegenConstants.PROJECT_ORGANIZATION, projectOrganization);
        }

        if (additionalProperties.containsKey(CustomCodegenConstants.PROJECT_NAME)) {
            projectName = (String) additionalProperties.get(CustomCodegenConstants.PROJECT_NAME);
        } else {
            additionalProperties.put(CustomCodegenConstants.PROJECT_NAME, projectName);
        }

        if (additionalProperties.containsKey(CustomCodegenConstants.PROJECT_VERSION)) {
            projectVersion = (String) additionalProperties.get(CustomCodegenConstants.PROJECT_VERSION);
        } else {
            additionalProperties.put(CustomCodegenConstants.PROJECT_VERSION, projectVersion);
        }

        if (additionalProperties.containsKey(CustomCodegenConstants.SCALA_VERSION)) {
            scalaVersion = (String) additionalProperties.get(CustomCodegenConstants.SCALA_VERSION);
        } else {
            additionalProperties.put(CustomCodegenConstants.SCALA_VERSION, scalaVersion);
        }

        if (additionalProperties.containsKey(CustomCodegenConstants.PLAY_VERSION)) {
            playVersion = (String) additionalProperties.get(CustomCodegenConstants.PLAY_VERSION);
        } else {
            additionalProperties.put(CustomCodegenConstants.PLAY_VERSION, playVersion);
        }

        additionalProperties.put(CodegenConstants.INVOKER_PACKAGE, invokerPackage);
        additionalProperties.put(CustomCodegenConstants.CONFIG_PATH, configPath);
        additionalProperties.put(CustomCodegenConstants.PROJECT_ORGANIZATION, projectOrganization);
        additionalProperties.put(CustomCodegenConstants.PROJECT_NAME, projectName);
        additionalProperties.put(CustomCodegenConstants.PROJECT_VERSION, projectVersion);
        additionalProperties.put(CustomCodegenConstants.SCALA_VERSION, scalaVersion);
        additionalProperties.put(CustomCodegenConstants.PLAY_VERSION, playVersion);

        final String invokerFolder = (sourceFolder + File.separator + invokerPackage).replace(".", File.separator);
        supportingFiles.add(new SupportingFile("apiConfig.mustache", invokerFolder, "ApiConfig.scala"));
        supportingFiles.add(new SupportingFile("apiRequest.mustache", invokerFolder, "ApiRequest.scala"));
        supportingFiles.add(new SupportingFile("apiResponse.mustache", invokerFolder, "ApiResponse.scala"));
        supportingFiles.add(new SupportingFile("apiInvoker.mustache", invokerFolder, "ApiInvoker.scala"));
        supportingFiles.add(new SupportingFile("apiImplicits.mustache", invokerFolder, "ApiImplicits.scala"));
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

        public CamelizeLambda(boolean capitalizeFirst) {
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
