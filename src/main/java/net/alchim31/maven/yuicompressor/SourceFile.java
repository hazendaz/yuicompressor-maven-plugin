/*
 * SPDX-License-Identifier: LGPL-2.1-or-later
 * See LICENSE file for details.
 *
 * Copyright 2012-2026 Hazendaz.
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
            File defaultDest = destRoot.toPath().resolve(frpath).toFile();
            if (defaultDest.exists() && defaultDest.canRead()) {
                return defaultDest;
            }
        }
        return srcRoot.toPath().resolve(frpath).toFile();
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
        return destRoot.toPath().resolve(rpath + suffix + extension).toFile();
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
