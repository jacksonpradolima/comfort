/*
 * Copyright (C) 2017 University of Goettingen, Germany
 * 
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *         http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package de.ugoe.cs.comfort.collection.metriccollector;

import static org.junit.Assert.assertEquals;

import de.ugoe.cs.comfort.configuration.GeneralConfiguration;
import de.ugoe.cs.comfort.data.CoverageData;
import de.ugoe.cs.comfort.data.graphs.CallEdge;
import de.ugoe.cs.comfort.data.graphs.CallGraph;
import de.ugoe.cs.comfort.data.graphs.CallType;
import de.ugoe.cs.comfort.data.graphs.DependencyGraph;
import de.ugoe.cs.comfort.data.models.IUnit;
import de.ugoe.cs.comfort.filer.models.Result;
import java.io.IOException;
import java.nio.file.Paths;
import java.util.HashSet;
import java.util.Set;
import org.junit.Before;
import org.junit.Test;

/**
 * @author Fabian Trautsch
 */
public class TestCoverageCollectorTest extends BaseMetricCollectorTest {
    private final String basePath = getPathToResource("metricCollectorTestData/ieeeAndistqb");

    private TestCoverageCollector testCoverageCollector;

    private GeneralConfiguration javaConfig = new GeneralConfiguration();
    private GeneralConfiguration pythonConfig = new GeneralConfiguration();

    private Set<Result> expectedResult = new HashSet<>();

    private CoverageData covDataJava = new CoverageData();
    private CoverageData covDataPython = new CoverageData();
    private CallGraph javaCallGraph = new CallGraph();
    private DependencyGraph javaDependencyGraph = new DependencyGraph();

    @Before
    public void clearResults() {
        expectedResult.clear();
    }

    @Before
    public void createJavaConfig() {
        javaConfig.setProjectDir(basePath+"/java");
    }

    @Before
    public void createPythonConfig() {
        pythonConfig.setProjectDir(basePath+"/python");
        pythonConfig.setLanguage("python");
    }

    @Before
    public void setUpCoverageData() {
        // Java Data
        Set<IUnit> testedMethodsOfTest1 = new HashSet<>();
        testedMethodsOfTest1.add(covP1C1M1);
        testedMethodsOfTest1.add(covP1C2M1);

        Set<IUnit> testedMethodsOfTest2 = new HashSet<>();
        testedMethodsOfTest2.add(covP1C1M1);

        Set<IUnit> testedMethodsOfTest3 = new HashSet<>();
        testedMethodsOfTest3.add(covP2C1M1);

        covDataJava.add(T1Test1, testedMethodsOfTest1);
        covDataJava.add(T2Test1, testedMethodsOfTest2);
        covDataJava.add(T2Test2, testedMethodsOfTest3);


        // Python Data
        Set<IUnit> testedMethodsOfTest1py = new HashSet<>();
        testedMethodsOfTest1py.add(pyModule1Init);
        testedMethodsOfTest1py.add(pyModule2Init);

        Set<IUnit> testedMethodsOfTest2py = new HashSet<>();
        testedMethodsOfTest2py.add(pyModule1Init);

        Set<IUnit> testedMethodsOfTest3py = new HashSet<>();
        testedMethodsOfTest3py.add(pyModule3Init);

        covDataPython.add(pyTest1Test, testedMethodsOfTest1py);
        covDataPython.add(pyTest2Test, testedMethodsOfTest2py);
        covDataPython.add(pyTest2Test2, testedMethodsOfTest3py);

    }

    @Before
    public void setUpCallGraphData() {
        javaCallGraph.addEdge(new CallEdge(CallType.INVOKE_SPECIAL, 0, T1Test1, C1M1_p1));
        javaCallGraph.addEdge(new CallEdge(CallType.INVOKE_SPECIAL, 0, C1M1_p1, C2M1_p1));
        javaCallGraph.addEdge(new CallEdge(CallType.INVOKE_SPECIAL, 0, T2Test1, C1M1_p1));
        javaCallGraph.addEdge(new CallEdge(CallType.INVOKE_SPECIAL, 1, T2Test1, C3M1));
        javaCallGraph.addEdge(new CallEdge(CallType.INVOKE_SPECIAL, 0, T2Test2, C3M1));
    }

    @Before
    public void setUpDependencyGraphData() {
        javaDependencyGraph.putEdge(T1, C1);
        javaDependencyGraph.putEdge(C1, C2);
        javaDependencyGraph.putEdge(T2, C1);
        javaDependencyGraph.putEdge(T2, C3);
    }

    @Test
    public void createCodeCoverageMetricJavaMethodLevelTest() throws IOException {
        testCoverageCollector = new TestCoverageCollector(javaConfig, filerMock);
        testCoverageCollector.createResultsJavaPythonMethodLevel(covDataJava);
        expectedResult.add(new Result("org.foo.t1.Test1.test1", Paths.get("src/main/java/org/foo/t1/Test1.java"), "cov_tcov_met", "66"));
        expectedResult.add(new Result("org.foo.t2.Test2.test1", Paths.get("src/main/java/org/foo/t2/Test2.java"), "cov_tcov_met","33"));
        expectedResult.add(new Result("org.foo.t2.Test2.test2", Paths.get("src/main/java/org/foo/t2/Test2.java"), "cov_tcov_met","33"));
        assertEquals("Result set is not correct!", expectedResult, filerMock.getResults().getResults());

    }

    @Test
    public void createCodeCoverageMetricPythonMethodTest() throws IOException {
        testCoverageCollector = new TestCoverageCollector(pythonConfig, filerMock);

        testCoverageCollector.createResultsJavaPythonMethodLevel(covDataPython);
        expectedResult.add(new Result("tests.test_module1:Module1Test.test", Paths.get("tests/test_module1.py"), "cov_tcov_met", "66"));
        expectedResult.add(new Result("tests.test_module2:Module2Test.test",  Paths.get("tests/test_module2.py"), "cov_tcov_met", "33"));
        expectedResult.add(new Result("tests.test_module2:Module2Test.test2",  Paths.get("tests/test_module2.py"), "cov_tcov_met", "33"));
        assertEquals("Result set is not correct!", expectedResult, filerMock.getResults().getResults());
    }

    @Test
    public void createCodeCoverageMetricJavaClassLevelTest() throws IOException {
        testCoverageCollector = new TestCoverageCollector(javaConfig, filerMock);
        testCoverageCollector.createResultsJavaPythonClassLevel(covDataJava);
        expectedResult.add(new Result("org.foo.t1.Test1", Paths.get("src/main/java/org/foo/t1/Test1.java"), "cov_tcov", "66"));
        expectedResult.add(new Result("org.foo.t2.Test2", Paths.get("src/main/java/org/foo/t2/Test2.java"), "cov_tcov", "66"));
        assertEquals("Result set is not correct!", expectedResult, filerMock.getResults().getResults());
    }

    @Test
    public void createCodeCoverageMetricPythonClassTest() throws IOException {
        testCoverageCollector = new TestCoverageCollector(pythonConfig, filerMock);

        testCoverageCollector.createResultsJavaPythonClassLevel(covDataPython);
        expectedResult.add(new Result("tests.test_module1", Paths.get("tests/test_module1.py"), "cov_tcov", "66"));
        expectedResult.add(new Result("tests.test_module2", Paths.get("tests/test_module2.py"), "cov_tcov", "66"));
        assertEquals("Result set is not correct!", expectedResult, filerMock.getResults().getResults());
    }

    @Test
    public void createResultsForCallGraphJavaMethodLevelTest() throws IOException {
        testCoverageCollector = new TestCoverageCollector(javaConfig, filerMock);

        testCoverageCollector.createResultsForCallGraphMethodLevel(javaCallGraph);
        expectedResult.add(new Result("org.foo.t1.Test1.test1", Paths.get("src/main/java/org/foo/t1/Test1.java"), "call_tcov_met", "66"));
        expectedResult.add(new Result("org.foo.t2.Test2.test1", Paths.get("src/main/java/org/foo/t2/Test2.java"), "call_tcov_met", "100"));
        expectedResult.add(new Result("org.foo.t2.Test2.test2", Paths.get("src/main/java/org/foo/t2/Test2.java"), "call_tcov_met", "33"));
        assertEquals("Result set is not correct!", expectedResult, filerMock.getResults().getResults());
    }

    @Test
    public void createResultsForCallGraphJavaClassLevelTest() throws IOException {
        testCoverageCollector = new TestCoverageCollector(javaConfig, filerMock);

        testCoverageCollector.createResultsForCallGraphClassLevel(javaCallGraph);
        expectedResult.add(new Result("org.foo.t1.Test1", Paths.get("src/main/java/org/foo/t1/Test1.java"), "call_tcov", "66"));
        expectedResult.add(new Result("org.foo.t2.Test2", Paths.get("src/main/java/org/foo/t2/Test2.java"), "call_tcov", "100"));
        assertEquals("Result set is not correct!", expectedResult, filerMock.getResults().getResults());
    }


    @Test
    public void createResultsForDependencyGraphJavaTest() throws IOException {
        testCoverageCollector = new TestCoverageCollector(javaConfig, filerMock);

        testCoverageCollector.createResultsForDependencyGraph(javaDependencyGraph);
        expectedResult.add(new Result("org.foo.Test1", null, "dep_tcov", "66"));
        expectedResult.add(new Result("org.foo.Test2", null, "dep_tcov", "100"));
        assertEquals("Result set is not correct!", expectedResult, filerMock.getResults().getResults());
    }

    @Test
    public void createResultsForDependencyGraphPythonTest() throws IOException {
        DependencyGraph dependencyGraph = new DependencyGraph();
        dependencyGraph.putEdge(pyTest1, module1);
        dependencyGraph.putEdge(module1, module2);
        dependencyGraph.putEdge(pyTest2, module1);
        dependencyGraph.putEdge(pyTest2, module3);

        testCoverageCollector = new TestCoverageCollector(pythonConfig, filerMock);

        testCoverageCollector.createResultsForDependencyGraph(dependencyGraph);
        expectedResult.add(new Result("tests.test1", Paths.get("tests/test1.py"), "dep_tcov", "66"));
        expectedResult.add(new Result("tests.test2", Paths.get("tests/test2.py"), "dep_tcov", "100"));
        assertEquals("Result set is not correct!", expectedResult, filerMock.getResults().getResults());
    }

}
