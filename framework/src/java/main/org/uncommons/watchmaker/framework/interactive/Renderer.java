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

/**
 * Maps objects of one type to objects of a different type.  For example,
 * this class could be used to render dates as Strings or to render arrays
 * as GUI list components.
 * @param <T> The input type for the renderer.
 * @param <S> The output type for the renderer.
 * @author Daniel Dyer
 */
public interface Renderer<T, S>
{
    /**
     * Renders an object of one type as an instance of another.  For example,
     * if the generic types of this renderer are Date and String, this method
     * would return a String representation of a Date.
     * @param entity An object to render as a different type.
     * @return A rendering of the parameter.
     */
    S render(T entity);
}
