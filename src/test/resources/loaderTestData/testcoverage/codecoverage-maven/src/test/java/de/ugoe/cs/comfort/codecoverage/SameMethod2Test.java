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

package de.ugoe.cs.comfort.codecoverage;

import org.junit.Test;

import static org.junit.Assert.assertEquals;

/**
 * @author Fabian Trautsch
 */
public class SameMethod2Test {

    @Test
    public void testSameMethod() {
        Module1 mod1 = new Module1();
        assertEquals(4, mod1.sum(2,2));
    }
}