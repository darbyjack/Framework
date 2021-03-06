/*
 * MIT License
 *
 * Copyright (c) 2019-2020 PiggyPiglet
 *
 * Permission is hereby granted, free of charge, to any person obtaining a copy
 * of this software and associated documentation files (the "Software"), to deal
 * in the Software without restriction, including without limitation the rights
 * to use, copy, modify, merge, publish, distribute, sublicense, and/or sell
 * copies of the Software, and to permit persons to whom the Software is
 * furnished to do so, subject to the following conditions:
 *
 * The above copyright notice and this permission notice shall be included in all
 * copies or substantial portions of the Software.
 *
 * THE SOFTWARE IS PROVIDED "AS IS", WITHOUT WARRANTY OF ANY KIND, EXPRESS OR
 * IMPLIED, INCLUDING BUT NOT LIMITED TO THE WARRANTIES OF MERCHANTABILITY,
 * FITNESS FOR A PARTICULAR PURPOSE AND NONINFRINGEMENT. IN NO EVENT SHALL THE
 * AUTHORS OR COPYRIGHT HOLDERS BE LIABLE FOR ANY CLAIM, DAMAGES OR OTHER
 * LIABILITY, WHETHER IN AN ACTION OF CONTRACT, TORT OR OTHERWISE, ARISING FROM,
 * OUT OF OR IN CONNECTION WITH THE SOFTWARE OR THE USE OR OTHER DEALINGS IN THE
 * SOFTWARE.
 */

package me.piggypiglet.framework.managers.implementations;

import me.piggypiglet.framework.managers.Manager;
import me.piggypiglet.framework.utils.SearchUtils;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

public abstract class SearchableManager<S extends SearchUtils.Searchable> extends Manager<S> {
    protected final transient List<S> items = new ArrayList<>();

    @Override
    public void add(S item) {
        super.add(item);
        items.add(item);
    }

    @Override
    public void remove(S item) {
        super.remove(item);
        items.remove(item);
    }

    @Override
    protected Collection<S> retrieveAll() {
        return items;
    }

    /**
     * Search the manager with a query, uses levenshtein weighted ratio.
     * @param query Query string
     * @return List of searchables, in order of most similar to least.
     */
    @SuppressWarnings("unchecked")
    public List<S> search(String query) {
        return SearchUtils.search((List<SearchUtils.Searchable>) items, query);
    }
}
