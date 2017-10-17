package io.swagger.codegen.languages;

import com.samskivert.mustache.Escapers;
import com.samskivert.mustache.Mustache;
import com.samskivert.mustache.Template;
import io.swagger.codegen.*;
import io.swagger.models.Response;
import io.swagger.models.auth.SecuritySchemeDefinition;
import io.swagger.models.properties.FileProperty;
import io.swagger.models.properties.Property;
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
public class PlayScalaClientCodegen extends AbstractScalaCodegen implements CodegenConfig {
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

        setReservedWordsLowerCase(
                Arrays.asList(
                        // local variable names used in API methods (endpoints)
                        "path", "contentTypes", "contentType", "queryParams", "headerParams",
                        "formParams", "postBody", "mp", "basePath", "apiInvoker",

                        // scala reserved words
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

        instantiationTypes.put("array", "ListBuffer");
        instantiationTypes.put("map", "HashMap");

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
        return "Generates a Scala client library based on PlayWS.";
    }

    /**
     * Overrides the default Mustache escaper with a custom one that allows to escape variables with grave accents.
     */
    @Override
    public Mustache.Compiler processCompiler(Mustache.Compiler compiler) {
        Mustache.Escaper SCALA = text -> {
            // The given text is a reserved word which is escaped by enclosing it with grave accents. If we would
            // escape that with the default Mustache `HTML` escaper, then the escaper would also escape our grave
            // accents. So we remove the grave accents before the escaping and add it back after the escaping.
            if (text.startsWith("`") && text.endsWith("`")) {
                String unescaped =  text.substring(1, text.length() - 1);
                return "`" + Escapers.HTML.escape(unescaped) + "`";
            }

            // All none reserved words will be escaped with the default Mustache `HTML` escaper
            return Escapers.HTML.escape(text);
        };

        return compiler.withEscaper(SCALA);
    }

    /**
     * We escape all scala reserved words by enclosing the word with grave accents.
     */
    @Override
    public String escapeReservedWord(String name) {
        return "`" + name + "`";
    }

    @Override
    public String escapeQuotationMark(String input) {
        // remove " to avoid code injection
        return input.replace("\"", "");
    }

    @Override
    public String toOperationId(String operationId) {
        // throw exception if method name is empty
        if (StringUtils.isEmpty(operationId)) {
            throw new RuntimeException("Empty method name (operationId) not allowed");
        }

        // method name cannot use reserved keyword, e.g. return
        if (isReservedWord(operationId)) {
            throw new RuntimeException(operationId + " (reserved word) cannot be used as method name");
        }

        return formatIdentifier(operationId, false);
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
    public String toDefaultValue(Property p) {
        if (!p.getRequired()) {
            return "None";
        }

        return super.toDefaultValue(p);
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

    private String formatIdentifier(String name, boolean capitalized) {
        String identifier = camelize(sanitizeName(name), true);
        if (capitalized) {
            identifier = StringUtils.capitalize(identifier);
        }
        if (identifier.matches("[a-zA-Z_$][\\w_$]+") && !isReservedWord(identifier)) {
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
