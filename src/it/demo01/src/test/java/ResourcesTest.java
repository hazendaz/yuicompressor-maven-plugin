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
package net.alchim31.maven.yuicompressor;

import java.io.File;

import org.junit.Assert;
import org.junit.Test;

import org.apache.commons.io.FileUtils;

public class ResourcesTest {

    private File outputDir_ = new File("target/classes");

    @Test
    public void testSimpleResources() throws Exception {
        File r01 = new File(outputDir_, "file_r01.js");
        assertExists(r01);
        assertNoComments(r01);

        File r02 = new File(outputDir_, "file_r02.js");
        assertExists(r02);
        assertNoComments(r02);
    }

    @Test
    public void testResourcesWithFilter() throws Exception {
        File r01 = new File(outputDir_, "file_rf01.js");
        assertExists(r01);
        assertNoComments(r01);
        assertNoFilteringValue(r01);

        File r02 = new File(outputDir_, "file_rf02.js");
        assertExists(r02);
        assertNoComments(r02);
        assertNoFilteringValue(r02);
    }

    @Test
    public void testResourcesWithTargetPath() throws Exception {
        File r01 = new File(outputDir_, "redirect/file_rr01.js");
        assertExists(r01);
        assertNotExists(new File(outputDir_, "file_rr01.js"));
        assertNoComments(r01);

        File r02 = new File(outputDir_, "redirect/file_rr02.js");
        assertExists(r02);
        assertNotExists(new File(outputDir_, "file_rr02.js"));
        assertNoComments(r02);
    }

    private void assertExists(File file) throws Exception {
      Assert.assertTrue(file.getName() + " not found", file.exists());
    }

    private void assertNotExists(File file) throws Exception {
      Assert.assertFalse(file.getName() + " found", file.exists());
    }

    private void assertNoComments(File file) throws Exception {
        String content = FileUtils.readFileToString(file);
        //System.out.println(file + ": "+ content);
        Assert.assertTrue("comments found (=> not compressed)", content.indexOf("//") < 0);
        Assert.assertTrue("comments found (=> not compressed)", content.indexOf("/*") < 0);
    }

    private void assertNoFilteringValue(File file) throws Exception {
        String content = FileUtils.readFileToString(file);
        Assert.assertTrue("property to filter found", content.indexOf("${") < 0);
    }
}
