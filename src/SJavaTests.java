//import java.io.ByteArrayOutputStream;
//import java.util.Map;
//
//import oop.ex6.main.*;
//import org.junit.jupiter.api.*;
//
//import java.io.*;
//import java.nio.file.*;
//import java.util.*;
//import java.util.stream.*;
//
////import static org.junit.jupiter.api.Assertions.*;
//
//    /**
//     * Run each test file as a single standalone test
//     * Works with JUnit 5
//     *
//     * Written by Ariel Jannai, 13/06/2018
//     * Version 1.0.0
//     *
//     * How to use:
//     *    1. Verify that all requirements are satisfied
//     *    2. Run the test file
//     *    3. Check the results
//     *    4. And again
//     *
//     * Sources:
//     *    Streams replacement: https://stackoverflow.com/a/1119559/2350423
//     */
//public class SJavaTests {
//    // We start looking for the folders/files in the root of the project, so "tests" is under "ex6".
//    private static final String testsDir = "tests/ex6_files/tests";
//    private static final String infoFile = "tests/ex6_files/sjavac_tests.txt";
//    private static final String SUCCESS_RESULT = "0";
//    private static final String SJAVA_SUFFIX = ".sjava";
//    private static Map<String, String> testsInfo;
//    private final ByteArrayOutputStream outContent = new ByteArrayOutputStream();
//    private final ByteArrayOutputStream errContent = new ByteArrayOutputStream();
//
//    /**
//     * Runs before all tests.
//     * Creates a map between filename to description according to the infoFile.
//     * infoFile syntax: filename description
//     * The content is being split on the first occurrence of the space char
//     */
//    @BeforeAll
//    private static void initTestsInfo() {
//        SJavaTests.testsInfo = new HashMap<>();
//        try (Stream<String> stream = Files.lines(Paths.get(SJavaTests.infoFile))) {
//            stream
//                    .filter(line -> !line.isEmpty())
//                    .forEach(line -> {
//                        int idx = line.indexOf(" ");
//                        SJavaTests.testsInfo
//                                .put(line.substring(0, idx), line.substring(idx + 1, line.length()));
//                    });
//        } catch (IOException e) {
//            e.printStackTrace();
//        }
//    }
//
//    /**
//     * The factory that runs all tests
//     * @return A list of tests
//     */
//    @TestFactory
//    List<DynamicTest> createSJavaTests() {
//        List<DynamicTest> list = new ArrayList<DynamicTest>();
//        try (Stream<Path> paths = Files.walk(Paths.get(SJavaTests.testsDir))) {
//            List<Path> files = paths
//                    // If you want to filter and run only part of the tests,
//                    // you can replace (or add) the filter condition to satisfy your needs.
//                    .filter(path -> path.getFileName().toString().endsWith(SJAVA_SUFFIX))
//                    .collect(Collectors.toList());
//            files.forEach(file -> list.add(
//                    DynamicTest.dynamicTest(SJavaTests.getTestString(file), () -> runTest(file))
//            ));
//        } catch (IOException e) {
//            e.printStackTrace();
//        }
//
//        return list;
//    }
//
//    /**
//     * The function that is used to run each test.
//     * Calling Sjava.oop.ex6.main and checking the result
//     * @param testFile The path of the test
//     */
//    private void runTest(Path testFile) throws IOException, IllegalCodeException {
//        this.setupStreams();
//
//        String filePath = testFile.toString();
//        Sjavac.oop.ex6.main(new String[] {filePath});
//        assertEquals(SUCCESS_RESULT, this.outContent.toString().trim());
//
//        this.restoreStreams();
//    }
//
//    /**
//     * Gets the test description string
//     * @param testName The path of the test
//     * @return The display name for the test
//     */
//    private static String getTestString(Path testName) {
//        return SJavaTests.getTestString(testName.getFileName().toString());
//    }
//
//    /**
//     * Gets the test description string
//     * @param testName The name of the test file
//     * @return The display name for the test
//     */
//    private static String getTestString(String testName) {
//        if (SJavaTests.testsInfo.containsKey(testName)) {
//            return String.format("%s %s", testName, SJavaTests.testsInfo.get(testName));
//        }
//
//        return testName;
//    }
//
//    /**
//     * configure the alternative streams (out/err)
//     */
//    private void setupStreams() {
//        outContent.reset();
//        errContent.reset();
//        System.setOut(new PrintStream(outContent));
//        System.setErr(new PrintStream(errContent));
//    }
//
//    /**
//     * Restore the default streams (out/err)
//     */
//    private void restoreStreams() {
//        System.setOut(System.out);
//        System.setErr(System.err);
//    }
//}
//
//
