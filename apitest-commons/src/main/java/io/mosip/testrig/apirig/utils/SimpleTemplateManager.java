package io.mosip.testrig.apirig.utils;

import org.apache.velocity.VelocityContext;
import org.apache.velocity.app.VelocityEngine;

import io.mosip.kernel.core.templatemanager.spi.TemplateManager;

import java.io.*;
import java.nio.charset.StandardCharsets;
import java.util.Map;
import java.util.Properties;

public class SimpleTemplateManager implements TemplateManager {

    private final VelocityEngine velocityEngine;

    public SimpleTemplateManager() {
        Properties props = new Properties();
        props.setProperty("resource.loader", "file");
        props.setProperty("input.encoding", "UTF-8");
        velocityEngine = new VelocityEngine(props);
        velocityEngine.init();
    }

    @Override
    public InputStream merge(InputStream template, Map<String, Object> values) throws IOException {
        StringWriter writer = new StringWriter();
        try (BufferedReader reader = new BufferedReader(new InputStreamReader(template, StandardCharsets.UTF_8))) {
            StringBuilder templateContent = new StringBuilder();
            String line;
            while ((line = reader.readLine()) != null) {
                templateContent.append(line).append(System.lineSeparator());
            }

            VelocityContext context = new VelocityContext(values);
            velocityEngine.evaluate(context, writer, "TemplateLogTag", templateContent.toString());
        }

        return new ByteArrayInputStream(writer.toString().getBytes(StandardCharsets.UTF_8));
    }

    @Override
    public boolean merge(String templateName, Writer writer, Map<String, Object> values) throws IOException {
        return merge(templateName, writer, values, "UTF-8");
    }

    @Override
    public boolean merge(String templateName, Writer writer, Map<String, Object> values, String encodingType)
            throws IOException {
        try (InputStream templateStream = new FileInputStream(templateName);
             InputStreamReader reader = new InputStreamReader(templateStream, encodingType)) {

            StringWriter tempWriter = new StringWriter();
            StringBuilder templateContent = new StringBuilder();
            BufferedReader bufferedReader = new BufferedReader(reader);
            String line;
            while ((line = bufferedReader.readLine()) != null) {
                templateContent.append(line).append(System.lineSeparator());
            }

            VelocityContext context = new VelocityContext(values);
            velocityEngine.evaluate(context, tempWriter, "TemplateLogTag", templateContent.toString());

            writer.write(tempWriter.toString());
            return true;
        } catch (Exception e) {
            return false;
        }
    }
}
