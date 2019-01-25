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

import static org.junit.Assert.assertEquals;

import org.junit.After;
import org.junit.Before;
import org.junit.Rule;
import org.junit.Test;
import org.junit.rules.TemporaryFolder;

import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.IOException;
import java.io.PrintStream;
import java.nio.file.Files;
import java.util.Collections;
import java.util.HashMap;
import java.util.Map;

public class VelocityCliTest {
    private PrintStream out;
    private ByteArrayOutputStream capturedOut;

    @Before
    public void setUp() {
        out = System.out;
        capturedOut = new ByteArrayOutputStream();
        System.setOut(new PrintStream(capturedOut));
    }

    @Rule
    public TemporaryFolder folder = new TemporaryFolder();

    private String getCapturedOut() {
        return capturedOut.toString();
    }

    private File file(String filename) {
        return new File(System.getProperty("user.dir"), filename);
    }

    @Test
    public void testNoArgs() {
        VelocityCli.main(new String[] {});
    }

    @Test(expected = VelocityCliError.class)
    public void testMissingTemplate() {
        VelocityCli cli = new VelocityCli();
        cli.inputTemplate = new File("missing.vtl");
        cli.run();
        assertEquals("", getCapturedOut());
    }

    @Test
    public void testNoContext() {
        VelocityCli cli = new VelocityCli();
        cli.inputTemplate = file("templates/hello.vtl");
        cli.run();
        assertEquals(String.format("Hello, ${name}!%n"), getCapturedOut());
    }

    @Test(expected = VelocityCliError.class)
    public void testParsingError() {
        VelocityCli cli = new VelocityCli();
        cli.inputTemplate = file("templates/parsing_error.vtl");
        cli.run();
        assertEquals("", getCapturedOut());
    }

    @Test
    public void testContext() {
        VelocityCli cli = new VelocityCli();
        cli.inputTemplate = file("templates/hello.vtl");
        cli.context = Collections.singletonMap("name", "world");
        cli.run();
        assertEquals(String.format("Hello, world!%n"), getCapturedOut());
    }

    @Test
    public void testOutputFile() throws IOException {
        VelocityCli cli = new VelocityCli();
        cli.inputTemplate = file("templates/hello.vtl");
        cli.context = Collections.singletonMap("name", "world");
        cli.outputFile = new File(folder.getRoot().getAbsolutePath(), "test.out");
        cli.run();
        assertEquals(String.format("Hello, world!%n"), new String(Files.readAllBytes(cli.outputFile.toPath())));
    }

    @Test(expected = VelocityCliError.class)
    public void testInvalidOutputFile() throws IOException {
        VelocityCli cli = new VelocityCli();
        cli.inputTemplate = file("templates/hello.vtl");
        cli.outputFile = new File("/1:\\invalid");
        cli.run();
    }

    @Test
    public void testYamlContext() {
        VelocityCli cli = new VelocityCli();
        cli.inputTemplate = file("templates/hello.vtl");
        cli.yamlContextFile = file("templates/hello.yml");
        cli.run();
        assertEquals(String.format("Hello, world!%n"), getCapturedOut());
    }

    @Test(expected = VelocityCliError.class)
    public void testInvalidYamlContext() {
        VelocityCli cli = new VelocityCli();
        cli.inputTemplate = file("templates/hello.vtl");
        cli.yamlContextFile = file("templates/missing.yml");

        cli.run();
    }

    @Test(expected = VelocityCliError.class)
    public void testInvalidInputEncoding() {
        VelocityCli cli = new VelocityCli();
        cli.inputTemplate = file("templates/hello.vtl");
        cli.inputEncoding = "invalid";
        cli.run();
    }

    @Test(expected = VelocityCliError.class)
    public void testInvalidYamlEncoding() {
        VelocityCli cli = new VelocityCli();
        cli.inputTemplate = file("templates/hello.vtl");
        cli.yamlContextFile = file("templates/hello.yml");
        cli.yamlEncoding = "invalid";
        cli.run();
    }    

    @Test(expected = VelocityCliError.class)
    public void testInvalidOutputEncoding() {
        VelocityCli cli = new VelocityCli();
        cli.inputTemplate = file("templates/hello.vtl");
        cli.outputEncoding = "invalid";
        cli.outputFile = new File(folder.getRoot().getAbsolutePath(), "test.out");
        cli.run();
    }

    @Test
    public void testEncoding_all_in_ebcdic() throws IOException {
        VelocityCli cli = new VelocityCli();
        cli.inputTemplate = file("templates/cp1047.vtl");
        cli.yamlContextFile = file("templates/hello_cp1047.yml");
        cli.inputEncoding = "Cp1047";
        cli.yamlEncoding = "Cp1047";
        cli.outputEncoding = "ISO-8859-1";
        cli.outputFile = new File(folder.getRoot().getAbsolutePath(), "test.out");
        cli.run();
        assertEquals(String.format("Hello Petr!"), new String(Files.readAllBytes(cli.outputFile.toPath())));
    }

    @Test
    public void testEncoding_all_in_ebcdic_yml_encoding_not_specified() throws IOException {
        VelocityCli cli = new VelocityCli();
        cli.inputTemplate = file("templates/cp1047.vtl");
        cli.yamlContextFile = file("templates/hello_cp1047.yml");
        cli.inputEncoding = "Cp1047";
        //cli.yamlEncoding = "Cp1047";  intentionally not specified it should be same like cli.inputEncoding
        cli.outputEncoding = "ISO-8859-1";
        cli.outputFile = new File(folder.getRoot().getAbsolutePath(), "test.out");
        cli.run();
        assertEquals(String.format("Hello Petr!"), new String(Files.readAllBytes(cli.outputFile.toPath())));
    }

    @Test
    public void testEncoding_template_in_ebcdic_yaml_in_ascii() throws IOException {
        VelocityCli cli = new VelocityCli();
        cli.inputTemplate = file("templates/cp1047.vtl");
        cli.yamlContextFile = file("templates/hello.yml");
        cli.inputEncoding = "Cp1047";
        cli.yamlEncoding = "ISO-8859-1";
        cli.outputEncoding = "ISO-8859-1";
        cli.outputFile = new File(folder.getRoot().getAbsolutePath(), "test.out");
        cli.run();
        assertEquals(String.format("Hello Petr!"), new String(Files.readAllBytes(cli.outputFile.toPath())));
    }

    @Test
    public void testEnvironmentContext() throws Exception {
    	VelocityCli cli = new VelocityCli();
        Map<String, String> env = System.getenv();
        Map<String, String> newEnv = new HashMap<>(env);
        newEnv.put("VTL_NAME", "environment");
        EnvUtils.setenv(newEnv);
        cli.inputTemplate = file("templates/env.vtl");
        cli.envContext = true;
        cli.run();
        assertEquals(String.format("Hello, environment!%n"), getCapturedOut());        
    }
    
    @Test
    public void testZosmfVariablesWithDash() throws Exception {
        VelocityCli cli = new VelocityCli();
        cli.inputTemplate = file("templates/zOSMFvariables.vtl");
        Map<String, String> m = new HashMap<String, String>();

        m.put("DBname", "noscope");
        m.put("instance-DBname", "instance-scope");
        m.put("global-DBname", "global-scope");
        cli.context = m;

        cli.run();
        assertEquals(String.format("Variable without scope 'noscope'%n"
            + "Variable in instance scope 'instance-scope'%n" 
            + "Variable in global scope 'global-scope'"),
            getCapturedOut());
    }      


    @After
    public void tearDown() {
        System.setOut(out);
        System.out.print(getCapturedOut());
    }
}
