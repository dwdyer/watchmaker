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

import java.util.List;

/**
 * A console provides users with a mechanism for interacting with an
 * evolutionary algorithm.
 * @param <T> The type of entity that can be presented by this console.
 * Evolutionary algorithms that evolve a different type can work with
 * a console via a {@link Renderer} that performs the necessary conversions.
 * @author Daniel Dyer
 */
public interface Console<T>
{
    /**
     * @param renderedEntities A list of the suitably transformed entities
     * that will be presented to the user for selection.
     * @return The index of the selected entity.
     */
    int select(List<? extends T> renderedEntities);
}
