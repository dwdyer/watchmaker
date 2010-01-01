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
package org.uncommons.watchmaker.framework.interactive;

import java.lang.reflect.Method;
import org.uncommons.util.reflection.ReflectionUtils;

/**
 * Adapter class for chaining together two renderers in series to provide
 * flexibility.  For example, if we have a Long -> Date renderer that turns
 * a number of milliseconds since epoch into a Java date, and a Date -> String
 * renderer that converts a Java date into its String representation in a
 * particular locale, we can combine the two to create a Long -> String renderer
 * without having to write a separate implementation of the {@link Renderer}
 * interface.
 * @param <T> The input type for the renderer.
 * @param <S> The output type for the renderer.
 * @author Daniel Dyer
 */
public class RendererAdapter<T, S> implements Renderer<T, S>
{
    private final Renderer<T, ?> renderer1;
    private final Renderer<?, S> renderer2;


    /**
     * Creates an adapter that feeds the output of renderer1 into renderer2.
     * @param <R> The intermediate type when transforming objects of type T to
     * objects of type S.
     * @param renderer1 A renderer that will translate an object of the input type
     * (T) into an object of the intermediate type (R).
     * @param renderer2 A renderer that will translate an object of the intermediate type
     * (R) into an object of the output type (S).     
     */
    public <R> RendererAdapter(Renderer<T, ? extends R> renderer1,
                               Renderer<R, S> renderer2)
    {
        this.renderer1 = renderer1;
        this.renderer2 = renderer2;
    }


    /**
     * {@inheritDoc}
     */
    public S render(T entity)
    {
        // This reflection charade is necessary because we can't convince the
        // compiler that the output of renderer1 is compatible with the input
        // of renderer2 without exposing a redundant "intermediate" type parameter
        // in the class definition.  I don't what to do that, I'd rather have
        // the ugliness encapsulated here than complicate code that uses this class.
        Method renderMethod = ReflectionUtils.findKnownMethod(Renderer.class,
                                                              "render",
                                                              Object.class);
        return ReflectionUtils.<S>invokeUnchecked(renderMethod,
                                                  renderer2,
                                                  renderer1.render(entity));
    }
}
