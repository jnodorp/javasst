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
import java.net.URL;
import java.net.URLClassLoader;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.AbstractMap;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.function.Function;
import java.util.stream.Collectors;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.fail;

/**
 * This test class contains a test case to verify the functional equivalence of class files produced by 'javac' and
 * those created by "our" compiler.
 */
public class BytecodeTest {

    /**
     * The minimum parameter value.
     */
    private static final int MIN = 0;

    /**
     * The maximum parameter value.
     */
    private static final int MAX = 100;

    /**
     * The parameter step width.
     */
    private static final int STEP = 1;

    /**
     * The files to verify.
     */
    private static final List<File> files = new ArrayList<>();

    /**
     * The logger.
     */
    private static final Logger LOGGER = LoggerFactory.getLogger(BytecodeTest.class);

    // Initialize the files to test.
    static {
        final List<String> resourceNames = new ArrayList<>();
        resourceNames.add("javasst/bytecode/AssignTest.sst");
        resourceNames.add("javasst/bytecode/BooleanTest.sst");
        resourceNames.add("javasst/bytecode/Fibonacci.sst");
        resourceNames.add("javasst/parser/expression_test.sst");
        resourceNames.add("javasst/parser/test.sst");

        files.addAll(resourceNames.stream().map(resourceName -> {
            URL resource = BytecodeTest.class.getClassLoader().getResource(resourceName);
            if (resource != null) {
                return new File(resource.getFile());
            } else {
                return null;
            }
        }).filter(file -> file != null).collect(Collectors.toList()));
    }

    /**
     * Compiler for Java code.
     */
    private final Function<File, File> javaCompiler = file -> {
        final String extension = file.getName().substring(file.getName().lastIndexOf(".") + 1);

        Path dir = null;
        try {
            dir = Files.createTempDirectory("out");

            run("cp", file.toString(), file.getParent() + File.separator + file.getName().replace(extension, "java"));
            if (run("javac", "-d", dir.toString(), file.getParent() + File.separator + file.getName().replace(extension, "java")) != 0) {
                throw new RuntimeException("javac failed.");
            }
            run("rm", file.getParent() + File.separator + file.getName().replace(extension, "java"));

            return dir.resolve(getBytecode(file).className() + ".class").toFile();
        } catch (IOException e) {
            LOGGER.error("Unable to create output directory.", e);
        } catch (InterruptedException e) {
            LOGGER.error("Interrupt during compilation.", e);
        }

        return null;
    };

    /**
     * Compiler for JavaSST code.
     */
    private final Function<File, File> javaSstCompiler = file -> {
        final BytecodeGenerator bytecode = getBytecode(file);
        Path dir = null;
        try {
            dir = Files.createTempDirectory("out");
        } catch (IOException e) {
            LOGGER.error("Unable to create output directory.");
        }

        if (dir != null) {
            final File out = dir.resolve(bytecode.className() + ".class").toFile();
            bytecode.generate().writeToFile(out.toString());
            return out;
        } else {
            return null;
        }
    };

    /**
     * Create all permutations of a certain length using elements from a pool.
     *
     * @param size The permutations length.
     * @param pool The pool.
     * @return A list of all permutations.
     */
    private static List<Integer[]> permute(int size, Integer... pool) {
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

    @Test
    public void testCompiler() throws IOException, InterruptedException, ClassNotFoundException, NoSuchMethodException,
            IllegalAccessException, InvocationTargetException, InstantiationException {
        final List<Map.Entry<Object, Object>> objects = new ArrayList<>();
        for (File file : files) {
            String className = getBytecode(file).className();
            File java = javaCompiler.apply(file);
            File javaSst = javaSstCompiler.apply(file);

            Class orig = new URLClassLoader(new URL[]{java.getParentFile().toURI().toURL()}).loadClass(className);
            Class comp = new URLClassLoader(new URL[]{javaSst.getParentFile().toURI().toURL()}).loadClass(className);

            Object origObj = null;
            for (Constructor constructor : orig.getDeclaredConstructors()) {
                constructor.setAccessible(true);
                origObj = constructor.newInstance();
            }

            Object compObj = null;
            for (Constructor constructor : comp.getDeclaredConstructors()) {
                constructor.setAccessible(true);
                compObj = constructor.newInstance();
            }

            objects.add(new AbstractMap.SimpleEntry<>(origObj, compObj));
        }

        objects.forEach(entry -> {
            final Object java = entry.getKey();
            final Object javaSst = entry.getValue();

            for (Method javaMethod : java.getClass().getDeclaredMethods()) {
                javaMethod.setAccessible(true);
                Method javaSstMethod = null;
                try {
                    javaSstMethod = javaSst.getClass().getMethod(javaMethod.getName(), javaMethod.getParameterTypes());
                    javaSstMethod.setAccessible(true);
                } catch (NoSuchMethodException e) {
                    fail("Method " + javaMethod.getName() + " is not contained in both class files for file.");
                }

                Integer[] pool = new Integer[1 + (MAX - MIN) / STEP];
                for (int i = MIN; i <= MAX; i = i + STEP) {
                    pool[i] = i;
                }

                List<Integer[]> parameters = permute(javaMethod.getParameterCount(), pool);
                final Method fJavaSstMethod = javaSstMethod;
                parameters.forEach(parameterSet -> {
                    Object javaResult = null;
                    Object javaSstResult = null;

                    try {
                        javaResult = javaMethod.invoke(java, parameterSet);
                    } catch (Throwable t) {
                        javaResult = t.getMessage();
                    }

                    try {
                        javaSstResult = fJavaSstMethod.invoke(javaSst, parameterSet);
                    } catch (Throwable t) {
                        javaSstResult = t.getMessage();
                    }

                    assertEquals(javaResult, javaSstResult);
                });
            }
        });
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

        // ast.foreach(SemanticAnalyzers.allDeclared());

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
