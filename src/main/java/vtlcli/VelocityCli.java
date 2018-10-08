/*
 * This program and the accompanying materials are made available under the terms of the
 * Eclipse Public License v2.0 which accompanies this distribution, and is available at
 * https://www.eclipse.org/legal/epl-v20.html
 *
 * SPDX-License-Identifier: EPL-2.0
 *
 * Copyright Contributors to the Zowe Project.
 */

package vtlcli;

import org.apache.velocity.Template;
import org.apache.velocity.VelocityContext;
import org.apache.velocity.app.VelocityEngine;
import org.apache.velocity.exception.ParseErrorException;
import org.apache.velocity.exception.ResourceNotFoundException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.yaml.snakeyaml.Yaml;

import java.io.BufferedWriter;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileWriter;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStreamWriter;
import java.io.Writer;
import java.util.Map;
import java.util.Map.Entry;

import picocli.CommandLine;
import picocli.CommandLine.Command;
import picocli.CommandLine.Option;
import picocli.CommandLine.Parameters;

@Command(name = "vtl", 
         header = "%n@|green Apache Velocity Template Language CLI|@",
         sortOptions = false,
         headerHeading = "Usage:%n%n",
         synopsisHeading = "%n",         
         parameterListHeading = "%nParameters:%n",
         optionListHeading = "%nOptions:%n")
public class VelocityCli implements Runnable {
    private static Logger logger = LoggerFactory.getLogger(VelocityCli.class);

    @Parameters(paramLabel = "FILE", description = "File with a Velocity template to process")
    File inputTemplate;

    @Option(names = { "-c", "--context" }, description = "Context variable for Velocity (can be repeated)", paramLabel="variable=value")
    Map<String, String> context;

    @Option(names = { "-y", "--yaml-context" }, description = "YAML file with context variables")
    File yamlContextFile;

    @Option(names = { "-e", "--env-context"}, description = "Set the context variables from environment")
    boolean envContext;    

    @Option(names = { "-o", "--out" }, description = "Output file (default: print to console)")
    File outputFile;

    public static void main(String[] args) {
        System.setProperty("picocli.usage.width", "120");
        try {
            CommandLine.run(new VelocityCli(), args);
        } catch (VelocityCliError e) {
            logger.error("{}: {}", e.getMessage(), e.getCause().getMessage());
            System.exit(1);
        }
    }

    @Override
    public void run() {
        VelocityEngine engine = new VelocityEngine();
        if (inputTemplate.getParent() != null) {
            engine.setProperty("file.resource.loader.path", inputTemplate.getParent());
        }
        engine.init();

        VelocityContext velocityContext = new VelocityContext();
        loadEnvContext(velocityContext);
        loadYamlContext(velocityContext);
        loadOptionContext(velocityContext);

        Writer writer = getWriter();
        try {
            Template template = engine.getTemplate(inputTemplate.getName());
            template.merge(velocityContext, writer);
            writer.flush();
        } catch (ResourceNotFoundException e) {
            throw new VelocityCliError("Error loading template", e);
        } catch (ParseErrorException e) {
            throw new VelocityCliError("Error parsing template", e);
        } catch (IOException e) {
            throw new VelocityCliError("I/O error", e);
        }
    }

    private void loadEnvContext(VelocityContext velocityContext) {
        if (envContext) {
            for (Entry<String, String> entry : System.getenv().entrySet()) {
                velocityContext.put(entry.getKey(), entry.getValue());
            }
        }
    }

    private void loadOptionContext(VelocityContext velocityContext) {
        if (context != null) {
            for (Entry<String, String> entry : context.entrySet()) {
                velocityContext.put(entry.getKey(), entry.getValue());
            }
        }
    }

    @SuppressWarnings("unchecked")
    private void loadYamlContext(VelocityContext velocityContext) {
        if (yamlContextFile != null) {
            try {
                InputStream input = new FileInputStream(yamlContextFile);
                Yaml yaml = new Yaml();
                Map<String, Object> context = (Map<String, Object>) yaml.load(input);
                for (Entry<String, Object> entry : context.entrySet()) {
                    velocityContext.put(entry.getKey(), entry.getValue());
                }
            } catch (FileNotFoundException e) {
                throw new VelocityCliError("Error reading YAML context file", e);
            }
        }
    }

    private Writer getWriter() {
        if (outputFile != null) {
            try {
                return new BufferedWriter(new FileWriter(outputFile));
            } catch (IOException e) {
                throw new VelocityCliError("Error opening output file", e);
            }
        } else {
            return new BufferedWriter(new OutputStreamWriter(System.out));
        }
    }
}
