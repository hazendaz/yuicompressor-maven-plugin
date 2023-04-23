/*
 * YuiCompressor Maven plugin
 *
 * Copyright 2012-2023 Hazendaz.
 *
 * Licensed under the GNU Lesser General Public License (LGPL),
 * version 2.1 or later (the "License").
 * You may not use this file except in compliance with the License.
 * You may read the licence in the 'lgpl.txt' file in the root folder of
 * project or obtain a copy at
 *
 *     https://www.gnu.org/licenses/lgpl-2.1.html
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" basis,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package net_alchim31_maven_yuicompressor;

import com.google.common.collect.Lists;
import org.junit.After;
import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;
import org.codehaus.plexus.util.FileUtils;
import org.codehaus.plexus.build.DefaultBuildContext;

import java.io.File;
import java.util.Collection;
import java.util.HashSet;

/**
 * The Class AggregationTestCase.
 */
public class AggregationTestCase {

    /** The dir. */
    private File dir_;

    /** The default build context. */
    private DefaultBuildContext defaultBuildContext = new DefaultBuildContext();

    @Before
    public void setUp() throws Exception {
        dir_ = File.createTempFile(this.getClass().getName(), "-test");
        dir_.delete();
        dir_.mkdirs();
    }

    @After
    public void tearDown() throws Exception {
        FileUtils.deleteDirectory(dir_);
    }

    /**
     * Test 0 to 1.
     *
     * @throws Exception the exception
     */
    @Test
    public void test0to1() throws Exception {
        Aggregation target = new Aggregation();
        target.output = new File(dir_, "output.js");

        Assert.assertFalse(target.output.exists());
        target.run(null, defaultBuildContext);
        Assert.assertFalse(target.output.exists());

        target.includes = new String[]{};
        Assert.assertFalse(target.output.exists());
        target.run(null, defaultBuildContext);
        Assert.assertFalse(target.output.exists());

        target.includes = new String[]{"**/*.js"};
        Assert.assertFalse(target.output.exists());
        target.run(null, defaultBuildContext);
        Assert.assertFalse(target.output.exists());
    }


    /**
     * Test 1 to 1.
     *
     * @throws Exception the exception
     */
    @Test
    public void test1to1() throws Exception {
        File f1 = new File(dir_, "01.js");
        FileUtils.fileWrite(f1.getAbsolutePath(), "1");
        Aggregation target = new Aggregation();
        target.output = new File(dir_, "output.js");
        target.includes = new String[]{f1.getName()};

        Assert.assertFalse(target.output.exists());
        target.run(null, defaultBuildContext);
        Assert.assertTrue(target.output.exists());
        Assert.assertEquals(FileUtils.fileRead(f1), FileUtils.fileRead(target.output));
    }

    /**
     * Test 2 to 1.
     *
     * @throws Exception the exception
     */
    @Test
    public void test2to1() throws Exception {
        File f1 = new File(dir_, "01.js");
        FileUtils.fileWrite(f1.getAbsolutePath(), "1");

        File f2 = new File(dir_, "02.js");
        FileUtils.fileWrite(f2.getAbsolutePath(), "22\n22");

        Aggregation target = new Aggregation();
        target.output = new File(dir_, "output.js");

        target.includes = new String[]{f1.getName(), f2.getName()};
        Assert.assertFalse(target.output.exists());
        target.run(null, defaultBuildContext);
        Assert.assertTrue(target.output.exists());
        Assert.assertEquals(FileUtils.fileRead(f1) + FileUtils.fileRead(f2), FileUtils.fileRead(target.output));

        target.output.delete();
        target.includes = new String[]{"*.js"};
        Assert.assertFalse(target.output.exists());
        target.run(null, defaultBuildContext);
        Assert.assertTrue(target.output.exists());
        Assert.assertEquals(FileUtils.fileRead(f1) + FileUtils.fileRead(f2), FileUtils.fileRead(target.output));
    }

    /**
     * Test no duplicate aggregation.
     *
     * @throws Exception the exception
     */
    @Test
    public void testNoDuplicateAggregation() throws Exception {
        File f1 = new File(dir_, "01.js");
        FileUtils.fileWrite(f1.getAbsolutePath(), "1");

        File f2 = new File(dir_, "02.js");
        FileUtils.fileWrite(f2.getAbsolutePath(), "22\n22");

        Aggregation target = new Aggregation();
        target.output = new File(dir_, "output.js");

        target.includes = new String[]{f1.getName(), f1.getName(), f2.getName()};
        Assert.assertFalse(target.output.exists());
        target.run(null, defaultBuildContext);
        Assert.assertTrue(target.output.exists());
        Assert.assertEquals(FileUtils.fileRead(f1) + FileUtils.fileRead(f2), FileUtils.fileRead(target.output));

        target.output.delete();
        target.includes = new String[]{f1.getName(), "*.js"};
        Assert.assertFalse(target.output.exists());
        target.run(null, defaultBuildContext);
        Assert.assertTrue(target.output.exists());
        Assert.assertEquals(FileUtils.fileRead(f1) + FileUtils.fileRead(f2), FileUtils.fileRead(target.output));
    }

    /**
     * Test 2 to 1 order.
     *
     * @throws Exception the exception
     */
    @Test
    public void test2to1Order() throws Exception {
        File f1 = new File(dir_, "01.js");
        FileUtils.fileWrite(f1.getAbsolutePath(), "1");

        File f2 = new File(dir_, "02.js");
        FileUtils.fileWrite(f2.getAbsolutePath(), "2");

        Aggregation target = new Aggregation();
        target.output = new File(dir_, "output.js");

        target.includes = new String[]{f2.getName(), f1.getName()};
        Assert.assertFalse(target.output.exists());
        target.run(null, defaultBuildContext);
        Assert.assertTrue(target.output.exists());
        Assert.assertEquals(FileUtils.fileRead(f2) + FileUtils.fileRead(f1), FileUtils.fileRead(target.output));
    }

    /**
     * Test 2 to 1 with new line.
     *
     * @throws Exception the exception
     */
    @Test
    public void test2to1WithNewLine() throws Exception {
        File f1 = new File(dir_, "01.js");
        FileUtils.fileWrite(f1.getAbsolutePath(), "1");

        File f2 = new File(dir_, "02.js");
        FileUtils.fileWrite(f2.getAbsolutePath(), "22\n22");

        Aggregation target = new Aggregation();
        target.output = new File(dir_, "output.js");
        target.insertNewLine = true;
        target.includes = new String[]{f1.getName(), f2.getName()};

        Assert.assertFalse(target.output.exists());
        target.run(null, defaultBuildContext);
        Assert.assertTrue(target.output.exists());
        Assert.assertEquals(FileUtils.fileRead(f1) + "\n" + FileUtils.fileRead(f2) + "\n", FileUtils.fileRead(target.output));
    }

    /**
     * Test absolute path from inside.
     *
     * @throws Exception the exception
     */
    @Test
    public void testAbsolutePathFromInside() throws Exception {
        File f1 = new File(dir_, "01.js");
        FileUtils.fileWrite(f1.getAbsolutePath(), "1");

        File f2 = new File(dir_, "02.js");
        FileUtils.fileWrite(f2.getAbsolutePath(), "22\n22");

        Aggregation target = new Aggregation();
        target.output = new File(dir_, "output.js");

        target.includes = new String[]{f1.getAbsolutePath(), f2.getName()};
        Assert.assertFalse(target.output.exists());
        target.run(null, defaultBuildContext);
        Assert.assertTrue(target.output.exists());
        Assert.assertEquals(FileUtils.fileRead(f1) + FileUtils.fileRead(f2), FileUtils.fileRead(target.output));
    }

    /**
     * Test absolute path from outside.
     *
     * @throws Exception the exception
     */
    @Test
    public void testAbsolutePathFromOutside() throws Exception {
        File f1 = File.createTempFile("test-01", ".js");
        try {
            FileUtils.fileWrite(f1.getAbsolutePath(), "1");

            File f2 = new File(dir_, "02.js");
            FileUtils.fileWrite(f2.getAbsolutePath(), "22\n22");

            Aggregation target = new Aggregation();
            target.output = new File(dir_, "output.js");

            target.includes = new String[]{f1.getAbsolutePath(), f2.getName()};
            Assert.assertFalse(target.output.exists());
            target.run(null, defaultBuildContext);
            Assert.assertTrue(target.output.exists());
            Assert.assertEquals(FileUtils.fileRead(f1) + FileUtils.fileRead(f2), FileUtils.fileRead(target.output));
        } finally {
            f1.delete();
        }
    }

    /**
     * Test auto exclude wildcards.
     *
     * @throws Exception the exception
     */
    @Test
    public void testAutoExcludeWildcards() throws Exception {
        File f1 = new File(dir_, "01.js");
        FileUtils.fileWrite(f1.getAbsolutePath(), "1");

        File f2 = new File(dir_, "02.js");
        FileUtils.fileWrite(f2.getAbsolutePath(), "22\n22");

        Aggregation target = new Aggregation();
        target.autoExcludeWildcards = true;
        target.output = new File(dir_, "output.js");

        Collection<File> previouslyIncluded = new HashSet<File>();
        previouslyIncluded.add(f1);

        target.includes = new String[]{f1.getName(), f2.getName()};
        Assert.assertFalse(target.output.exists());
        target.run(previouslyIncluded, defaultBuildContext);
        Assert.assertTrue(target.output.exists());
        Assert.assertEquals(FileUtils.fileRead(f1) + FileUtils.fileRead(f2), FileUtils.fileRead(target.output));

        target.output.delete();
        target.includes = new String[]{"*.js"};
        Assert.assertFalse(target.output.exists());
        //f1 was in previouslyIncluded so it is not included
        Assert.assertEquals(target.run(previouslyIncluded, defaultBuildContext), Lists.newArrayList(f2));
        Assert.assertTrue(target.output.exists());
        Assert.assertEquals(FileUtils.fileRead(f2), FileUtils.fileRead(target.output));
    }
}
