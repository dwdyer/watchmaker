//=============================================================================
// Copyright 2006-2010 Daniel W. Dyer
//
// Licensed under the Apache License, Version 2.0 (the "License");
// you may not use this file except in compliance with the License.
// You may obtain a copy of the License at
//
//     http://www.apache.org/licenses/LICENSE-2.0
//
// Unless required by applicable law or agreed to in writing, software
// distributed under the License is distributed on an "AS IS" BASIS,
// WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
// See the License for the specific language governing permissions and
// limitations under the License.
//=============================================================================
package org.uncommons.util.reflection;

import java.lang.reflect.Constructor;
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


    @Test(dependsOnMethods = "testFindMethod")
    public void testSuccessfulInvocation()
    {
        Method toString = ReflectionUtils.findKnownMethod(Object.class, "toString");
        String result = ReflectionUtils.invokeUnchecked(toString, "Hello");
        assert result.equals("Hello") : "Wrong value returned by method.";
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
    public void testMethodRuntimeExceptions()
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


    @Test
    public void testFindConstructor()
    {
        Constructor<Object> defaultConstructor = ReflectionUtils.findKnownConstructor(Object.class);
        // Make sure that the method returned is from the correct class and not some other class.
        Class<?> declaringClass = defaultConstructor.getDeclaringClass();
        assert declaringClass.equals(Object.class) : "Constructor from wrong class returned: " + declaringClass.getName();
    }


    @Test(expectedExceptions = IllegalArgumentException.class)
    public void testNoSuchConstructor()
    {
        ReflectionUtils.findKnownConstructor(Object.class, String.class);
    }


    @Test(dependsOnMethods = "testFindConstructor",
          expectedExceptions = IllegalStateException.class)
    public void testExceptionInConstructor()
    {
        Constructor<ExceptionTest> constructor = ReflectionUtils.findKnownConstructor(ExceptionTest.class);
        ReflectionUtils.invokeUnchecked(constructor); // Should throw exception.
    }


    @Test(dependsOnMethods = "testFindConstructor",
          expectedExceptions = InternalError.class)
    public void testErrorInConstructor()
    {
        Constructor<ErrorTest> constructor = ReflectionUtils.findKnownConstructor(ErrorTest.class);
        ReflectionUtils.invokeUnchecked(constructor); // Should throw error.
    }


    @Test(dependsOnMethods = "testFindConstructor",
          expectedExceptions = IllegalArgumentException.class)
    public void testConstructingAbstractClass()
    {
        Constructor<Abstract> constructor = ReflectionUtils.findKnownConstructor(Abstract.class);
        ReflectionUtils.invokeUnchecked(constructor); // Should throw an exception.
    }


    @Test(dependsOnMethods = "testFindConstructor",
          expectedExceptions = IllegalArgumentException.class)
    public void testUnconstructable() throws NoSuchMethodException
    {
        Constructor<Unconstructable> constructor = Unconstructable.class.getDeclaredConstructor();
        ReflectionUtils.invokeUnchecked(constructor); // Should throw an exception.
    }


    @Test(dependsOnMethods = "testFindMethod",
          expectedExceptions = IllegalArgumentException.class)
    public void testInaccessibleMethod() throws NoSuchMethodException
    {
        Method method = Inaccessible.class.getDeclaredMethod("inaccessibleMethod");
        ReflectionUtils.invokeUnchecked(method, new Inaccessible()); // Should throw an exception.
    }


    private static class ExceptionTest
    {
        public ExceptionTest()
        {
            throw new IllegalStateException();
        }
    }

    
    private static class ErrorTest
    {
        public ErrorTest()
        {
            throw new InternalError();
        }
    }


    private abstract static class Abstract
    {
        public Abstract()
        {
            // Do nothing.
        }
    }


    private static class Unconstructable
    {
        private Unconstructable()
        {
            // Do nothing.
        }
    }


    private static class Inaccessible
    {
        private void inaccessibleMethod()
        {
            // Do nothing.
        }
    }
}
