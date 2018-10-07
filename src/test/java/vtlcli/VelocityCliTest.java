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
import static org.junit.Assert.assertTrue;

import org.junit.After;
import org.junit.Before;
import org.junit.Test;

import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.PrintStream;
import java.util.Collections;

public class VelocityCliTest {
    private PrintStream out;
    private ByteArrayOutputStream capturedOut;

    private PrintStream err;
    private ByteArrayOutputStream capturedErr;

    @Before
    public void setUp() {
        out = System.out;
        err = System.err;
        capturedOut = new ByteArrayOutputStream();
        capturedErr = new ByteArrayOutputStream();
        System.setOut(new PrintStream(capturedOut));
        System.setErr(new PrintStream(capturedErr));
    }

    private String getCapturedOut() {
        return capturedOut.toString();
    }

    private String getCapturedErr() {
        return capturedErr.toString();
    }

    @Test
    public void testNoArgs() {
        VelocityCli.main(new String[] {});
    }

    @Test
    public void testMissingTemplate() {
        VelocityCli cli = new VelocityCli();
        cli.inputTemplate = new File("missing.vtl");
        cli.run();
        assertEquals("", getCapturedOut());
        assertTrue(getCapturedErr().contains("Error loading template"));
    }

    @Test
    public void testNoContext() {
        VelocityCli cli = new VelocityCli();
        cli.inputTemplate = new File(System.getProperty("user.dir"), "templates/hello.vtl");
        cli.run();
        assertEquals("Hello, ${name}!\n", getCapturedOut());
    }

    @Test
    public void testParsingError() {
        VelocityCli cli = new VelocityCli();
        cli.inputTemplate = new File(System.getProperty("user.dir"), "templates/parsing_error.vtl");
        cli.run();
        assertEquals("", getCapturedOut());
        assertTrue(getCapturedErr().contains("Error parsing template"));
    }

    @Test
    public void testContext() {
        VelocityCli cli = new VelocityCli();
        cli.inputTemplate = new File(System.getProperty("user.dir"), "templates/hello.vtl");
        cli.context = Collections.singletonMap("name", "world");
        cli.run();
        assertEquals("Hello, world!\n", getCapturedOut());
    }
    
    @After
    public void tearDown() {        
        System.setOut(out);
        System.setErr(err);
        System.out.print(getCapturedErr());
        System.err.print(getCapturedErr());
    }
}
