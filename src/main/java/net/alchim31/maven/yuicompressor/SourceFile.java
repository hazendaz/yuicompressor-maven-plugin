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
    private File srcRoot_;

    /** The dest root. */
    private File destRoot_;

    /** The dest as source. */
    private boolean destAsSource_;

    /** The rpath. */
    private String rpath_;

    /** The extension. */
    private String extension_;

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
     *
     * @throws Exception
     *             the exception
     */
    public SourceFile(File srcRoot, File destRoot, String name, boolean destAsSource) throws Exception {
        srcRoot_ = srcRoot;
        destRoot_ = destRoot;
        destAsSource_ = destAsSource;
        rpath_ = name;
        int sep = rpath_.lastIndexOf('.');
        if (sep > 0) {
            extension_ = rpath_.substring(sep);
            rpath_ = rpath_.substring(0, sep);
        } else {
            extension_ = "";
        }
    }

    /**
     * To file.
     *
     * @return the file
     */
    public File toFile() {
        String frpath = rpath_ + extension_;
        if (destAsSource_) {
            File defaultDest = new File(destRoot_, frpath);
            if (defaultDest.exists() && defaultDest.canRead()) {
                return defaultDest;
            }
        }
        return new File(srcRoot_, frpath);
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
        return new File(destRoot_, rpath_ + suffix + extension_);
    }

    /**
     * Gets the extension.
     *
     * @return the extension
     */
    public String getExtension() {
        return extension_;
    }
}
