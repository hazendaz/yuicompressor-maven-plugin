/*
 * SPDX-License-Identifier: LGPL-2.1-or-later
 * See LICENSE file for details.
 *
 * Copyright 2012-2026 Hazendaz.
 */
package net.alchim31.maven.yuicompressor;

import java.io.File;
import java.io.IOException;
import java.nio.file.Path;

import org.junit.Assert;
import org.junit.Test;

import org.apache.commons.io.FileUtils;

/**
 * The Class ResourcesTest.
 */
public class ResourcesTest {

    /** The output dir. */
    private File outputDir = Path.of("target/classes").toFile();

    /**
     * Test simple resources.
     *
     * @throws IOException the io exception
     */
    @Test
    public void testSimpleResources() throws IOException {
        File r01 = outputDir.toPath().resolve("file_r01.js").toFile();
        assertExists(r01);
        assertNoComments(r01);

        File r02 = outputDir.toPath().resolve("file_r02.js").toFile();
        assertExists(r02);
        assertNoComments(r02);
    }

    /**
     * Test resources with filter.
     *
     * @throws IOException the io exception
     */
    @Test
    public void testResourcesWithFilter() throws IOException {
        File r01 = outputDir.toPath().resolve("file_rf01.js").toFile();
        assertExists(r01);
        assertNoComments(r01);
        assertNoFilteringValue(r01);

        File r02 = outputDir.toPath().resolve("file_rf02.js").toFile();
        assertExists(r02);
        assertNoComments(r02);
        assertNoFilteringValue(r02);
    }

    /**
     * Test resources with target path.
     *
     * @throws IOException the io exception
     */
    @Test
    public void testResourcesWithTargetPath() throws IOException {
        File r01 = outputDir.toPath().resolve("redirect/file_rr01.js").toFile();
        assertExists(r01);
        assertNotExists(outputDir.toPath().resolve("file_rr01.js").toFile());
        assertNoComments(r01);

        File r02 = outputDir.toPath().resolve("redirect/file_rr02.js").toFile();
        assertExists(r02);
        assertNotExists(outputDir.toPath().resolve("file_rr02.js").toFile());
        assertNoComments(r02);
    }

    /**
     * Assert exists.
     *
     * @param file the file
     */
    private void assertExists(File file) {
      Assert.assertTrue(file.getName() + " not found", file.exists());
    }

    /**
     * Assert not exists.
     *
     * @param file the file
     */
    private void assertNotExists(File file) {
      Assert.assertFalse(file.getName() + " found", file.exists());
    }

    /**
     * Assert no comments.
     *
     * @param file the file
     * @throws IOException the io exception
     */
    private void assertNoComments(File file) throws IOException {
        String content = FileUtils.readFileToString(file);
        //System.out.println(file + ": "+ content);
        Assert.assertTrue("comments found (=> not compressed)", content.indexOf("//") < 0);
        Assert.assertTrue("comments found (=> not compressed)", content.indexOf("/*") < 0);
    }

    /**
     * Assert no filtering value.
     *
     * @param file the file
     * @throws IOException the io exception
     */
    private void assertNoFilteringValue(File file) throws IOException {
        String content = FileUtils.readFileToString(file);
        Assert.assertTrue("property to filter found", content.indexOf("${") < 0);
    }
}
