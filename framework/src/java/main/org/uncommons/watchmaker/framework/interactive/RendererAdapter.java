// ============================================================================
//   Copyright 2007 Daniel W. Dyer
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
package org.uncommons.watchmaker.framework.interactive;

import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;

/**
 * Adapter class for chaining together two renderers in series to provide
 * flexibility.  For example, if we have a Long -> Date renderer that turns
 * an number of milliseconds since epoch into a Java date, and a Date -> String
 * renderer that converts a Java date into its String representation in a
 * particular locale, we can combine the two to create a Long -> String renderer
 * without having to write a separate implementation of the {@link Renderer}
 * interface. 
 * @author Daniel Dyer
 */
public class RendererAdapter<T, S> implements Renderer<T, S>
{
    private final Renderer<T, ?> renderer1;
    private final Renderer<?, S> renderer2;


    public <R> RendererAdapter(Renderer<T, ? extends R> renderer1,
                               Renderer<R, S> renderer2)
    {
        this.renderer1 = renderer1;
        this.renderer2 = renderer2;
    }

    
    @SuppressWarnings({"unchecked"})
    public S render(T entity)
    {
        // This reflection charade is necessary because we can't convince the
        // compiler that the output of renderer1 is compatible with the input
        // of renderer2 without exposing a redundant "intermediate" type parameter
        // in the class definition.  I don't what to do that, I'd rather have
        // the ugliness encapsulated here than complicate code that uses this class.
        try
        {
            Method renderMethod = renderer2.getClass().getMethod("render", Object.class);
            return (S) renderMethod.invoke(renderer2, renderer1.render(entity));
        }
        catch (IllegalAccessException ex)
        {
            // This cannot happen - the render method is public.
            throw new IllegalStateException(ex);
        }
        catch (NoSuchMethodException ex)
        {
            // This cannot happen - the render method is explicitly identified.
            throw new IllegalStateException(ex);
        }
        catch (InvocationTargetException ex)
        {
            // The render method is not declared to throw any exceptions so the
            // worst that can happen is a RuntimeException - we can re-throw that.
            throw (RuntimeException) ex.getCause();
        }
    }
}
