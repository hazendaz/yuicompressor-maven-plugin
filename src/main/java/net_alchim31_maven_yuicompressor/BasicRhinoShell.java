/* ***** BEGIN LICENSE BLOCK *****
 * Version: MPL 1.1/GPL 2.0
 *
 * The contents of this file are subject to the Mozilla Public License Version
 * 1.1 (the "License"); you may not use this file except in compliance with
 * the License. You may obtain a copy of the License at
 * http://www.mozilla.org/MPL/
 *
 * Software distributed under the License is distributed on an "AS IS" basis,
 * WITHOUT WARRANTY OF ANY KIND, either express or implied. See the License
 * for the specific language governing rights and limitations under the
 * License.
 *
 * The Original Code is Rhino code, released
 * May 6, 1998.
 *
 * The Initial Developer of the Original Code is
 * Netscape Communications Corporation.
 * Portions created by the Initial Developer are Copyright (C) 1997-1999
 * the Initial Developer. All Rights Reserved.
 *
 * Contributor(s):
 *
 * Alternatively, the contents of this file may be used under the terms of
 * the GNU General Public License Version 2 or later (the "GPL"), in which
 * case the provisions of the GPL are applicable instead of those above. If
 * you wish to allow use of your version of this file only under the terms of
 * the GPL and not to allow others to use your version of this file under the
 * MPL, indicate your decision by deleting the provisions above and replacing
 * them with the notice and other provisions required by the GPL. If you do
 * not delete the provisions above, a recipient may use your version of this
 * file under either the MPL or the GPL.
 *
 * ***** END LICENSE BLOCK ***** */

package net_alchim31_maven_yuicompressor;

import org.codehaus.plexus.util.IOUtil;
import org.mozilla.javascript.*;

import java.io.*;

/**
 * The BasicRhinoShell program.
 * <p>
 * Can execute scripts interactively or in batch mode at the command line. An
 * example of controlling the JavaScript engine.
 * <p>
 * Based on Rhino (2007-08-30).
 *
 * @author Norris Boyd
 * @see <a href="http://lxr.mozilla.org/mozilla/source/js/rhino/examples/BasicRhinoShell.java">Basic Rhino Shell</a>
 */
@SuppressWarnings("serial")
public class BasicRhinoShell extends ScriptableObject {

    @Override
    public String getClassName() {
        return "global";
    }

    /**
     * Main entry point.
     * <p>
     * Process arguments as would a normal Java program. Also create a new
     * Context and associate it with the current thread. Then set up the
     * execution environment and begin to execute scripts.
     *
     * @param args the args
     * @param reporter the reporter
     */
    public static void exec(String[] args, ErrorReporter reporter) {
        // Associate a new Context with this thread
        Context cx = Context.enter();
        cx.setErrorReporter(reporter);
        try {
            // Initialize the standard objects (Object, Function, etc.)
            // This must be done before scripts can be executed.
            BasicRhinoShell basicRhinoShell = new BasicRhinoShell();
            cx.initStandardObjects(basicRhinoShell);

            // Define some global functions particular to the BasicRhinoShell.
            // Note
            // that these functions are not part of ECMA.
            String[] names = {"print", "quit", "version", "load", "help", "readFile", "warn"};
            basicRhinoShell.defineFunctionProperties(names, BasicRhinoShell.class, ScriptableObject.DONTENUM);

            args = processOptions(cx, args);

            // Set up "arguments" in the global scope to contain the command
            // line arguments after the name of the script to execute
            Object[] array;
            if (args.length == 0) {
                array = new Object[0];
            } else {
                int length = args.length - 1;
                array = new Object[length];
                System.arraycopy(args, 1, array, 0, length);
            }
            Scriptable argsObj = cx.newArray(basicRhinoShell, array);
            basicRhinoShell.defineProperty("arguments", argsObj, ScriptableObject.DONTENUM);

            basicRhinoShell.processSource(cx, args.length == 0 ? null : args[0]);
        } finally {
            Context.exit();
        }
    }

    /**
     * Parse arguments.
     *
     * @param cx the cx
     * @param args the args
     * @return the string[]
     */
    public static String[] processOptions(Context cx, String[] args) {
        for (int i = 0; i < args.length; i++) {
            String arg = args[i];
            if (!arg.startsWith("-")) {
                String[] result = new String[args.length - i];
                for (int j = i; j < args.length; j++) {
                    result[j - i] = args[j];
                }
                return result;
            }
            if (arg.equals("-version")) {
                if (++i == args.length) {
                    usage(arg);
                }
                double d = Context.toNumber(args[i]);
                if (d != d) {
                    usage(arg);
                }
                cx.setLanguageVersion((int) d);
                continue;
            }
            usage(arg);
        }
        return new String[0];
    }

    /**
     * Print a usage message.
     *
     * @param s the s
     */
    private static void usage(String s) {
        p("Didn't understand \"" + s + "\".");
        p("Valid arguments are:");
        p("-version 100|110|120|130|140|150|160|170");
        System.exit(1);
    }

    /**
     * Print a help message.
     * <p>
     * This method is defined as a JavaScript function.
     */
    public void help() {
        p("");
        p("Command                Description");
        p("=======                ===========");
        p("help()                 Display usage and help messages. ");
        p("defineClass(className) Define an extension using the Java class");
        p("                       named with the string argument. ");
        p("                       Uses ScriptableObject.defineClass(). ");
        p("load(['foo.js', ...])  Load JavaScript source files named by ");
        p("                       string arguments. ");
        p("loadClass(className)   Load a class named by a string argument.");
        p("                       The class must be a script compiled to a");
        p("                       class file. ");
        p("print([expr ...])      Evaluate and print expressions. ");
        p("quit()                 Quit the BasicRhinoShell. ");
        p("version([number])      Get or set the JavaScript version number.");
        p("");
    }

    /**
     * Print the string values of its arguments.
     * <p>
     * This method is defined as a JavaScript function. Note that its arguments
     * are of the "varargs" form, which allows it to handle an arbitrary number
     * of arguments supplied to the JavaScript function.
     *
     * @param cx the cx
     * @param thisObj the this obj
     * @param args the args
     * @param funObj the fun obj
     */
    public static void print(Context cx, Scriptable thisObj, Object[] args, Function funObj) {
        for (int i = 0; i < args.length; i++) {
            if (i > 0) {
                System.out.print(" ");
            }

            // Convert the arbitrary JavaScript value into a string form.
            String s = Context.toString(args[i]);

            System.out.print(s);
        }
        System.out.println();
    }

    /**
     * Quit the BasicRhinoShell.
     * <p>
     * This only affects the interactive mode.
     * <p>
     * This method is defined as a JavaScript function.
     */
    public void quit() {
        quitting = true;
    }

    /**
     * Warn.
     *
     * @param cx the cx
     * @param thisObj the this obj
     * @param args the args
     * @param funObj the fun obj
     */
    public static void warn(Context cx, Scriptable thisObj, Object[] args, Function funObj) {
        String message = Context.toString(args[0]);
        int line = (int) Context.toNumber(args[1]);
        String source = Context.toString(args[2]);
        int column = (int) Context.toNumber(args[3]);
        cx.getErrorReporter().warning(message, null, line, source, column);
    }

    /**
     * This method is defined as a JavaScript function.
     *
     * @param path the path
     * @return the string
     */
    public String readFile(String path) {
        try {
            return IOUtil.toString(new FileInputStream(path));
        } catch (RuntimeException exc) {
            throw exc;
        } catch (Exception exc) {
            throw new RuntimeException("wrap: " + exc.getMessage(), exc);
        }
    }

    /**
     * Get and set the language version.
     * <p>
     * This method is defined as a JavaScript function.
     *
     * @param cx the cx
     * @param thisObj the this obj
     * @param args the args
     * @param funObj the fun obj
     * @return the double
     */
    public static double version(Context cx, Scriptable thisObj, Object[] args, Function funObj) {
        double result = cx.getLanguageVersion();
        if (args.length > 0) {
            double d = Context.toNumber(args[0]);
            cx.setLanguageVersion((int) d);
        }
        return result;
    }

    /**
     * Load and execute a set of JavaScript source files.
     * <p>
     * This method is defined as a JavaScript function.
     *
     * @param cx the cx
     * @param thisObj the this obj
     * @param args the args
     * @param funObj the fun obj
     */
    public static void load(Context cx, Scriptable thisObj, Object[] args, Function funObj) {
        BasicRhinoShell basicRhinoShell = (BasicRhinoShell) getTopLevelScope(thisObj);
        for (Object element : args) {
            basicRhinoShell.processSource(cx, Context.toString(element));
        }
    }

    /**
     * Evaluate JavaScript source.
     *
     * @param cx       the current context
     * @param filename the name of the file to compile, or null for interactive
     *                 mode.
     */
    private void processSource(Context cx, String filename) {
        if (filename == null) {
            BufferedReader in = new BufferedReader(new InputStreamReader(System.in));
            String sourceName = "<stdin>";
            int lineno = 1;
            boolean hitEOF = false;
            do {
                int startline = lineno;
                System.err.print("js> ");
                System.err.flush();
                try {
                    String source = "";
                    // Collect lines of source to compile.
                    while (true) {
                        String newline;
                        newline = in.readLine();
                        if (newline == null) {
                            hitEOF = true;
                            break;
                        }
                        source = source + newline + "\n";
                        lineno++;
                        // Continue collecting as long as more lines
                        // are needed to complete the current
                        // statement. stringIsCompilableUnit is also
                        // true if the source statement will result in
                        // any error other than one that might be
                        // resolved by appending more source.
                        if (cx.stringIsCompilableUnit(source)) {
                            break;
                        }
                    }
                    Object result = cx.evaluateString(this, source, sourceName, startline, null);
                    if (result != Context.getUndefinedValue()) {
                        System.err.println(Context.toString(result));
                    }
                } catch (WrappedException we) {
                    // Some form of exception was caught by JavaScript and
                    // propagated up.
                    System.err.println(we.getWrappedException().toString());
                    we.printStackTrace();
                } catch (EvaluatorException ee) {
                    // Some form of JavaScript error.
                    System.err.println("js: " + ee.getMessage());
                } catch (JavaScriptException jse) {
                    // Some form of JavaScript error.
                    System.err.println("js: " + jse.getMessage());
                } catch (IOException ioe) {
                    System.err.println(ioe.toString());
                }
                if (quitting) {
                    // The user executed the quit() function.
                    break;
                }
            } while (!hitEOF);
            System.err.println();
        } else {
            FileReader in = null;
            try {
                in = new FileReader(filename);
            } catch (FileNotFoundException ex) {
                Context.reportError("Couldn't open file \"" + filename + "\".");
                return;
            }

            try {
                // Here we evalute the entire contents of the file as
                // a script. Text is printed only if the print() function
                // is called.
                cx.evaluateReader(this, in, filename, 1, null);
            } catch (WrappedException we) {
                System.err.println(we.getWrappedException().toString());
                we.printStackTrace();
            } catch (EvaluatorException ee) {
                System.err.println("js: " + ee.getMessage());
            } catch (JavaScriptException jse) {
                System.err.println("js: " + jse.getMessage());
            } catch (IOException ioe) {
                System.err.println(ioe.toString());
            } finally {
                try {
                    in.close();
                } catch (IOException ioe) {
                    System.err.println(ioe.toString());
                }
            }
        }
    }

    /**
     * P.
     *
     * @param s the s
     */
    private static void p(String s) {
        System.out.println(s);
    }

    private boolean quitting;
}
