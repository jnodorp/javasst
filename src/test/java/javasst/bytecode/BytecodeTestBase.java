package javasst.bytecode;

import compiler.ast.Ast;
import javasst.ast.JavaSstNode;
import javasst.parser.JavaSstParser;
import javasst.scanner.JavaSstScanner;
import org.junit.Test;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.File;
import java.io.IOException;
import java.lang.reflect.Constructor;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.net.MalformedURLException;
import java.net.URL;
import java.net.URLClassLoader;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.fail;

/**
 * This test class contains a test case to verify the functional equivalence of class files produced by 'javac' and
 * those created by "our" compiler.
 */
public abstract class BytecodeTestBase {

    /**
     * The logger.
     */
    protected static final Logger LOGGER = LoggerFactory.getLogger(BytecodeTestBase.class);

/*    private final String[] files = new String[]{
            "javasst/bytecode/A.sst",
            "javasst/bytecode/AssignTest.sst",
            "javasst/bytecode/BooleanTest.sst",
            "javasst/bytecode/Fibonacci.sst",
            "javasst/bytecode/Test.sst",
            "javasst/parser/expression_test.sst",
            "javasst/parser/test.sst"
    };*/

    /**
     * The parameter values to choose from.
     */
    private final int[] pool = new int[]{0, 1, 2, 3, Integer.MAX_VALUE};

    /**
     * Create all permutations of a certain length using elements from a pool.
     *
     * @param size The permutations length.
     * @param pool The pool.
     * @return A list of all permutations.
     */
    private static List<Integer[]> permute(int size, final int[] pool) {
        if (size < 1) {
            return new ArrayList<>();
        }

        final int itemsPoolCount = pool.length;
        final List<Integer[]> permutations = new ArrayList<>();
        for (int i = 0; i < Math.pow(itemsPoolCount, size); i++) {
            Integer[] permutation = new Integer[size];
            for (int j = 0; j < size; j++) {
                int itemPoolIndex = (int) Math.floor((double) (i % (int) Math.pow(itemsPoolCount, j + 1)) / (int) Math.pow(itemsPoolCount, j));
                permutation[j] = pool[itemPoolIndex];
            }
            permutations.add(permutation);
        }

        return permutations;
    }

    /**
     * Get the Java file.
     *
     * @return The Java file.
     */
    protected abstract String getJavaFile();

    /**
     * Get the JavaSST file.
     *
     * @return The JavaSST file.
     */
    protected abstract String getJavaSstFile();

    /**
     * Get the class name.
     *
     * @return The class name.
     */
    protected abstract String getClassName();

    @Test
    public void test() throws IllegalAccessException {
        try {
            compileAndCompare(getFile(getJavaFile()), getFile(getJavaSstFile()));
        } catch (MalformedURLException | InstantiationException | InvocationTargetException | ClassNotFoundException e) {
            LOGGER.error("Exception thrown during compilation/comparison.", e);
            fail("Exception thrown during compilation/comparison.");
        }
    }

    /**
     * Compiler for Java code.
     */
    private File compileJava(final File file) {
        Path dir;
        try {
            dir = Files.createTempDirectory("compiledJava");

            if (run("javac", "-d", dir.toString(), file.getAbsolutePath()) != 0) {
                fail("javac failed.");
            }

            return dir.resolve(getClassName() + ".class").toFile();
        } catch (IOException e) {
            LOGGER.error("Unable to create output directory.", e);
        } catch (InterruptedException e) {
            LOGGER.error("Interrupt during compilation.", e);
        }

        return null;
    }

    /**
     * Compiler for JavaSST code.
     */
    private File compileJavaSst(final File file) {
        final BytecodeGenerator bytecode = getBytecode(file);
        Path dir;
        try {
            dir = Files.createTempDirectory("compiledJavaSst");

            final File out = dir.resolve(bytecode.className() + ".class").toFile();
            bytecode.generate().writeToFile(out.getAbsolutePath());
            return out;

        } catch (IOException e) {
            LOGGER.error("Unable to create output directory.");
        }

        return null;
    }

    /**
     * Get a resource as file.
     *
     * @param file The resource path.
     * @return The file.
     */
    private File getFile(final String file) {
        final URL resource = BytecodeTestBase.class.getClassLoader().getResource(file);
        if (resource != null) {
            return new File(resource.getFile());
        } else {
            return null;
        }
    }

    /**
     * Compile both files and compare the output.
     *
     * @param file1 The baseline file.
     * @param file2 The new file.
     * @throws MalformedURLException     Thrown on malformed URLs.
     * @throws ClassNotFoundException    Thrown if compiled class cannot be found.
     * @throws IllegalAccessException    Thrown if method is not accessible.
     * @throws InvocationTargetException Thrown if method cant be invoked on created instance.
     * @throws InstantiationException    Thrown if unable to instantiate the compiled class.
     */
    private void compileAndCompare(final File file1, final File file2) throws MalformedURLException,
            ClassNotFoundException, IllegalAccessException, InvocationTargetException,
            InstantiationException {
        final String className = getClassName();
        final File java = compileJava(file1);
        final File javaSst = compileJavaSst(file2);

        if (java == null) fail("Could not compile Java.");
        LOGGER.info("Compiled Java code to " + java.getAbsolutePath());
        if (javaSst == null) fail("Could not compile JavaSST.");
        LOGGER.info("Compiled JavaSST code to " + javaSst.getAbsolutePath());

        final Class orig = new URLClassLoader(new URL[]{java.getParentFile().toURI().toURL()}).loadClass(className);
        final Class comp = new URLClassLoader(new URL[]{javaSst.getParentFile().toURI().toURL()}).loadClass(className);

        Object javaObj = null;
        for (Constructor constructor : orig.getDeclaredConstructors()) {
            constructor.setAccessible(true);
            javaObj = constructor.newInstance();
        }
        assert javaObj != null;

        Object javaSstObj = null;
        for (Constructor constructor : comp.getDeclaredConstructors()) {
            constructor.setAccessible(true);
            javaSstObj = constructor.newInstance();
        }
        assert javaSstObj != null;

        for (final Method javaMethod : javaObj.getClass().getDeclaredMethods()) {
            javaMethod.setAccessible(true);

            Method javaSstMethod = null;
            try {
                javaSstMethod = javaSstObj.getClass().getMethod(javaMethod.getName(), javaMethod.getParameterTypes());
                javaSstMethod.setAccessible(true);
            } catch (NoSuchMethodException e) {
                fail("Method " + javaMethod.getName() + " is not contained in both class files for file " + java + ".");
            }

            final List<Integer[]> parameters = permute(javaMethod.getParameterCount(), pool);
            for (Integer[] parameterSet : parameters) {
                Object javaResult;
                try {
                    javaResult = javaMethod.invoke(javaObj, parameterSet);
                } catch (Throwable t) {
                    javaResult = t.getMessage();
                }

                Object javaSstResult;
                try {
                    javaSstResult = javaSstMethod.invoke(javaSstObj, parameterSet);
                } catch (Throwable t) {
                    javaSstResult = t.getMessage();
                }

                final String input = Arrays.toString(parameterSet).replace("[", "(").replace("]", ")");
                final String call = javaMethod.getName() + input;
                assertEquals("Wrong result for '" + call + "'.", javaResult, javaSstResult);
            }
        }
    }

    /**
     * Generate the bytecode.
     *
     * @param file The source file.
     * @return The bytecode.
     */
    private BytecodeGenerator getBytecode(final File file) {
        final JavaSstScanner scanner = new JavaSstScanner(file.toString());
        final JavaSstParser parser = new JavaSstParser(scanner);
        final Ast<JavaSstNode> ast = parser.parse();
        final String name = file.getName();
        return new BytecodeGenerator(name, ast);
    }

    /**
     * Run a command.
     *
     * @param cmd The command and its parameters.
     * @return The exit code.
     * @throws IOException          Thrown if an I/O error occurs.
     * @throws InterruptedException Thrown if the thread is interrupted while waiting for the process to return.
     */
    private int run(final String... cmd) throws IOException, InterruptedException {
        final ProcessBuilder processBuilder = new ProcessBuilder(cmd).inheritIO();
        LOGGER.debug(String.join(" ", processBuilder.command()));
        return processBuilder.start().waitFor();
    }
}
