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

/**
 * The Class SourceFile.
 */
public class SourceFile {

    /** The src root. */
    private File srcRoot;

    /** The dest root. */
    private File destRoot;

    /** The dest as source. */
    private boolean destAsSource;

    /** The rpath. */
    private String rpath;

    /** The extension. */
    private String extension;

    /**
     * Instantiates a new source file.
     *
     * @param srcRoot
     *            the src root
     * @param destRoot
     *            the dest root
     * @param name
     *            the name
     * @param destAsSource
     *            the dest as source
     */
    public SourceFile(File srcRoot, File destRoot, String name, boolean destAsSource) {
        this.srcRoot = srcRoot;
        this.destRoot = destRoot;
        this.destAsSource = destAsSource;
        this.rpath = name;
        int sep = this.rpath.lastIndexOf('.');
        if (sep > 0) {
            this.extension = this.rpath.substring(sep);
            this.rpath = rpath.substring(0, sep);
        } else {
            this.extension = "";
        }
    }

    /**
     * To file.
     *
     * @return the file
     */
    public File toFile() {
        String frpath = rpath + extension;
        if (destAsSource) {
            File defaultDest = new File(destRoot, frpath);
            if (defaultDest.exists() && defaultDest.canRead()) {
                return defaultDest;
            }
        }
        return new File(srcRoot, frpath);
    }

    /**
     * To dest file.
     *
     * @param suffix
     *            the suffix
     *
     * @return the file
     */
    public File toDestFile(String suffix) {
        return new File(destRoot, rpath + suffix + extension);
    }

    /**
     * Gets the extension.
     *
     * @return the extension
     */
    public String getExtension() {
        return extension;
    }
}
