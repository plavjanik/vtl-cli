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
import org.junit.Test;

import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.PrintStream;
import java.util.Collections;

public class VelocityCliTest {
    private PrintStream out;
    private ByteArrayOutputStream capturedOut;

    @Before
    public void setUp() {
        out = System.out;
        capturedOut = new ByteArrayOutputStream();
        System.setOut(new PrintStream(capturedOut));
    }

    private String getCapturedOut() {
        return capturedOut.toString();
    }

    @Test
    public void testNoContext() {
        VelocityCli cli = new VelocityCli();
        cli.inputTemplate = new File(System.getProperty("user.dir"), "templates/hello.vtl");
        cli.run();
        assertEquals("Hello, ${name}!\n", getCapturedOut());
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
    }
}
