package github.nonoas.jfx.flat.ui.theme;

import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.LinkedHashMap;
import java.util.LinkedHashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import java.util.stream.Collectors;
import java.util.stream.Stream;

/**
 * Validates theme CSS files against the shared theme template.
 */
public final class ThemeCssValidator {

    private static final String PLACEHOLDER = "__REQUIRED__";
    private static final Pattern COMMENT_PATTERN = Pattern.compile("/\\*.*?\\*/", Pattern.DOTALL);
    private static final Pattern IMPORT_PATTERN = Pattern.compile("@import\\s+[\"']([^\"']+)[\"']\\s*;");
    private static final Pattern BLOCK_PATTERN = Pattern.compile("(?s)([^{}]+)\\{([^{}]*)\\}");

    private ThemeCssValidator() {
        // Utility class
    }

    /**
     * Validates one theme stylesheet against the template stylesheet.
     */
    public static ValidationReport validate(Path templatePath, Path themePath) throws IOException {
        CssDocument template = CssDocument.load(templatePath);
        CssDocument target = CssDocument.load(themePath);

        List<String> errors = new ArrayList<>();
        List<String> warnings = new ArrayList<>();

        if (!target.imports.stream().anyMatch(path -> "style-template.css".equals(path.getFileName().toString()))) {
            warnings.add("missing @import \"style-template.css\"");
        }

        for (Map.Entry<String, Map<String, String>> selectorEntry : template.rules.entrySet()) {
            String selector = selectorEntry.getKey();
            Map<String, String> targetProps = target.rules.get(selector);
            if (targetProps == null) {
                errors.add("missing selector: " + selector);
                continue;
            }

            for (Map.Entry<String, String> propEntry : selectorEntry.getValue().entrySet()) {
                String property = propEntry.getKey();
                String expectedValue = propEntry.getValue();
                String actualValue = targetProps.get(property);

                if (actualValue == null) {
                    errors.add("missing property: " + selector + " -> " + property);
                    continue;
                }

                if (isPlaceholder(expectedValue)) {
                    if (isPlaceholder(actualValue)) {
                        errors.add("placeholder not replaced: " + selector + " -> " + property);
                    }
                    continue;
                }

                if (!normalizeValue(expectedValue).equals(normalizeValue(actualValue))) {
                    errors.add("value mismatch: " + selector + " -> " + property
                            + " expected [" + expectedValue + "] but was [" + actualValue + "]");
                }
            }
        }

        for (Map.Entry<String, Map<String, String>> selectorEntry : target.rules.entrySet()) {
            String selector = selectorEntry.getKey();
            Map<String, String> templateProps = template.rules.get(selector);
            if (templateProps == null) {
                warnings.add("extra selector: " + selector);
                continue;
            }

            for (String property : selectorEntry.getValue().keySet()) {
                if (!templateProps.containsKey(property)) {
                    warnings.add("extra property: " + selector + " -> " + property);
                }
            }
        }

        return new ValidationReport(themePath.toAbsolutePath().normalize(), errors, warnings);
    }

    /**
     * Command-line entry point.
     * Defaults to validating every style-*.css theme in {@code src/main/resources/css}.
     */
    public static void main(String[] args) throws IOException {
        Path projectRoot = Paths.get("").toAbsolutePath().normalize();
        Path cssDir = projectRoot.resolve(Paths.get("src", "main", "resources", "css"));
        Path templatePath = cssDir.resolve("style-theme-template.css");

        List<Path> targets;
        if (args != null && args.length > 0) {
            targets = new ArrayList<>();
            for (String arg : args) {
                Path path = Paths.get(arg);
                if (!path.isAbsolute()) {
                    path = projectRoot.resolve(path);
                }
                targets.add(path.normalize());
            }
        } else {
            try (Stream<Path> stream = Files.list(cssDir)) {
                targets = stream
                        .filter(path -> path.getFileName().toString().startsWith("style-"))
                        .filter(path -> path.getFileName().toString().endsWith(".css"))
                        .filter(path -> !path.getFileName().toString().equals("style-template.css"))
                        .filter(path -> !path.getFileName().toString().equals("style-theme-template.css"))
                        .sorted(Comparator.comparing(path -> path.getFileName().toString()))
                        .collect(Collectors.toList());
            }
        }

        boolean hasErrors = false;
        for (Path target : targets) {
            ValidationReport report = validate(templatePath, target);
            if (!report.isValid()) {
                hasErrors = true;
            }
            System.out.print(report.toDisplayString(projectRoot));
        }

        if (hasErrors) {
            System.exit(1);
        }
    }

    private static boolean isPlaceholder(String value) {
        return PLACEHOLDER.equals(normalizeValue(value));
    }

    private static String normalizeValue(String value) {
        return value == null ? "" : value.trim().replaceAll("\\s+", " ");
    }

    private static String normalizeSelector(String selector) {
        return selector.trim().replaceAll("\\s+", " ");
    }

    private static final class CssDocument {

        private final Path path;
        private final List<Path> imports;
        private final Map<String, Map<String, String>> rules;

        private CssDocument(Path path, List<Path> imports, Map<String, Map<String, String>> rules) {
            this.path = path;
            this.imports = imports;
            this.rules = rules;
        }

        private static CssDocument load(Path path) throws IOException {
            return load(path.toAbsolutePath().normalize(), new LinkedHashSet<>());
        }

        private static CssDocument load(Path path, Set<Path> stack) throws IOException {
            if (!Files.exists(path)) {
                throw new IOException("CSS file does not exist: " + path);
            }
            if (!stack.add(path)) {
                throw new IOException("Circular CSS import detected: " + path);
            }

            String raw = Files.readString(path, StandardCharsets.UTF_8);
            String withoutComments = COMMENT_PATTERN.matcher(raw).replaceAll("");

            List<Path> imports = new ArrayList<>();
            Matcher importMatcher = IMPORT_PATTERN.matcher(withoutComments);
            while (importMatcher.find()) {
                imports.add(path.getParent().resolve(importMatcher.group(1)).normalize());
            }

            Map<String, Map<String, String>> rules = new LinkedHashMap<>();
            for (Path imported : imports) {
                CssDocument importedDoc = load(imported, stack);
                mergeRules(rules, importedDoc.rules);
            }

            String css = IMPORT_PATTERN.matcher(withoutComments).replaceAll("");
            Matcher blockMatcher = BLOCK_PATTERN.matcher(css);
            while (blockMatcher.find()) {
                String selectorGroup = blockMatcher.group(1).trim();
                if (selectorGroup.isEmpty() || selectorGroup.startsWith("@")) {
                    continue;
                }

                Map<String, String> declarations = parseDeclarations(blockMatcher.group(2));
                if (declarations.isEmpty()) {
                    continue;
                }

                String[] selectors = selectorGroup.split(",");
                for (String selector : selectors) {
                    String normalizedSelector = normalizeSelector(selector);
                    if (normalizedSelector.isEmpty()) {
                        continue;
                    }
                    rules.computeIfAbsent(normalizedSelector, ignored -> new LinkedHashMap<>()).putAll(declarations);
                }
            }

            stack.remove(path);
            return new CssDocument(path, imports, rules);
        }

        private static Map<String, String> parseDeclarations(String body) {
            Map<String, String> declarations = new LinkedHashMap<>();
            String[] lines = body.split(";");
            for (String line : lines) {
                String trimmed = line.trim();
                if (trimmed.isEmpty()) {
                    continue;
                }

                int splitIndex = trimmed.indexOf(':');
                if (splitIndex < 0) {
                    continue;
                }

                String property = trimmed.substring(0, splitIndex).trim();
                String value = trimmed.substring(splitIndex + 1).trim();
                if (!property.isEmpty() && !value.isEmpty()) {
                    declarations.put(property, value);
                }
            }
            return declarations;
        }

        private static void mergeRules(Map<String, Map<String, String>> target,
                                       Map<String, Map<String, String>> source) {
            for (Map.Entry<String, Map<String, String>> entry : source.entrySet()) {
                target.computeIfAbsent(entry.getKey(), ignored -> new LinkedHashMap<>()).putAll(entry.getValue());
            }
        }
    }

    /**
     * Result of a single stylesheet validation.
     */
    public static final class ValidationReport {

        private final Path themePath;
        private final List<String> errors;
        private final List<String> warnings;

        private ValidationReport(Path themePath, List<String> errors, List<String> warnings) {
            this.themePath = themePath;
            this.errors = List.copyOf(errors);
            this.warnings = List.copyOf(warnings);
        }

        public Path getThemePath() {
            return themePath;
        }

        public List<String> getErrors() {
            return errors;
        }

        public List<String> getWarnings() {
            return warnings;
        }

        public boolean isValid() {
            return errors.isEmpty();
        }

        public String toDisplayString(Path projectRoot) {
            String relativePath = projectRoot.relativize(themePath).toString().replace('\\', '/');
            StringBuilder builder = new StringBuilder();
            builder.append(isValid() ? "PASS " : "FAIL ").append(relativePath).append(System.lineSeparator());

            for (String error : errors) {
                builder.append("  ERROR ").append(error).append(System.lineSeparator());
            }
            for (String warning : warnings) {
                builder.append("  WARN ").append(warning).append(System.lineSeparator());
            }
            return builder.toString();
        }
    }
}
