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
import org.apache.velocity.exception.MethodInvocationException;
import org.apache.velocity.exception.ParseErrorException;
import org.apache.velocity.exception.ResourceNotFoundException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.BufferedWriter;
import java.io.File;
import java.io.IOException;
import java.io.OutputStreamWriter;
import java.io.Writer;
import java.util.Map;
import java.util.Map.Entry;

import picocli.CommandLine;
import picocli.CommandLine.Command;
import picocli.CommandLine.Option;
import picocli.CommandLine.Parameters;

@Command(name = "vtl", header = "%n@|green Velocity CLI|@")
public class VelocityCli implements Runnable {
    @Parameters(paramLabel = "FILE", description = "Velocity template to process.")
    File inputTemplate;

    @Option(names = { "-c", "--context" })
    Map<String, String> context;

    Logger logger = LoggerFactory.getLogger(VelocityCli.class);

    public static void main(String[] args) {
        CommandLine.run(new VelocityCli(), args);
    }

    @Override
    public void run() {
        VelocityEngine engine = new VelocityEngine();
        if (inputTemplate.getParent() != null) {
            engine.setProperty("file.resource.loader.path", inputTemplate.getParent());
        }
        engine.init();
        VelocityContext velocityContext = new VelocityContext();

        if (context != null) {
            for (Entry<String, String> entry : context.entrySet()) {
                velocityContext.put(entry.getKey(), entry.getValue());
            }
        }

        Writer writer = new BufferedWriter(new OutputStreamWriter(System.out));

        try {
            Template template = engine.getTemplate(inputTemplate.getName());
            template.merge(velocityContext, writer);
        } catch (ResourceNotFoundException e) {
            logger.error("Error loading template '{}': {}", inputTemplate.getPath(), e.getMessage());
        } catch (ParseErrorException e) {
            logger.error("Error parsing template: {}", e.getMessage());
        }

        try {
            writer.flush();
        } catch (IOException e) {
            logger.error("Error writing to the output", e);
        }
    }
}
