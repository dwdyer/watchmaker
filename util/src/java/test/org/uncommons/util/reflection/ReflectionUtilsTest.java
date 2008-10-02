// ============================================================================
//   Copyright 2006, 2007, 2008 Daniel W. Dyer
//
//   Licensed under the Apache License, Version 2.0 (the "License");
//   you may not use this file except in compliance with the License.
//   You may obtain a copy of the License at
//
//       http://www.apache.org/licenses/LICENSE-2.0
//
//   Unless required by applicable law or agreed to in writing, software
//   distributed under the License is distributed on an "AS IS" BASIS,
//   WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
//   See the License for the specific language governing permissions and
//   limitations under the License.
// ============================================================================
package org.uncommons.util.reflection;

import java.lang.reflect.Method;
import java.math.BigDecimal;
import org.testng.annotations.Test;

/**
 * Unit test for {@link ReflectionUtils}.
 * @author Daniel Dyer
 */
public class ReflectionUtilsTest
{
    @Test
    public void testFindMethod()
    {
        Method toString = ReflectionUtils.findKnownMethod(Object.class, "toString");
        assert toString.getName().equals("toString") : "Wrong method returned: " + toString.getName();
        // Make sure that the method returned is from the correct class and not some other class.
        Class<?> declaringClass = toString.getDeclaringClass();
        assert declaringClass.equals(Object.class) : "Method from wrong class returned: " + declaringClass.getName();
    }

    
    @Test(expectedExceptions = IllegalArgumentException.class)
    public void testNoSuchMethod()
    {
        ReflectionUtils.findKnownMethod(Object.class, "noSuchMethod");
    }


    /**
     * Invoking a method that throws a RuntimeException should result in that
     * exception being thrown back to the caller.  The RuntimeException should not
     * be wrapped in an InvocationTargetException as would be the case for a
     * normal reflective invocation. 
     */
    @Test(dependsOnMethods = "testFindMethod",
          expectedExceptions = ArithmeticException.class)
    public void testRuntimeExceptions()
    {
        Method divide = ReflectionUtils.findKnownMethod(BigDecimal.class, "divide", BigDecimal.class);
        ReflectionUtils.invokeUnchecked(divide, BigDecimal.ONE, BigDecimal.ZERO); // Should throw ArithmeticException.
    }


    /**
     * Invoking a method that throws an Error should result in that
     * error being thrown back to the caller.  The Error should not
     * be wrapped in an InvocationTargetException as would be the case for a
     * normal reflective invocation.
     */
    @Test(dependsOnMethods = "testFindMethod",
          expectedExceptions = InternalError.class)
    public void testErrors()
    {
        class ErrorTest
        {
            public void doError()
            {
                throw new InternalError();
            }
        }
        Method error = ReflectionUtils.findKnownMethod(ErrorTest.class, "doError");
        ReflectionUtils.invokeUnchecked(error, new ErrorTest()); // Should throw InternalError.
    }
}
