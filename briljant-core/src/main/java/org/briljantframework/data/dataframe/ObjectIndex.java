/*
 * The MIT License (MIT)
 *
 * Copyright (c) 2015 Isak Karlsson
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

package org.briljantframework.data.dataframe;

import org.briljantframework.Check;
import org.briljantframework.data.BoundType;
import org.briljantframework.data.index.Index;
import org.briljantframework.data.index.ObjectComparator;
import org.briljantframework.data.vector.Vector;

import java.util.AbstractSet;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.Collections;
import java.util.Comparator;
import java.util.HashMap;
import java.util.Iterator;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.ListIterator;
import java.util.Map;
import java.util.NavigableMap;
import java.util.NoSuchElementException;
import java.util.Set;
import java.util.SortedMap;
import java.util.TreeMap;

/**
 * The {@code ObjectIndex} contains any key and associates it with a location.
 *
 * <p> Keys of this index is iterated in insertion order
 *
 * @author Isak Karlsson
 */
public final class ObjectIndex implements Index {

  private Map<Object, Integer> keys = new LinkedHashMap<>();
  private List<Object> locations = new ArrayList<>();
  private List<Integer> order = new ArrayList<>();

  public ObjectIndex(Collection<?> coll) {
    Iterator<?> it = coll.iterator();
    for (int i = 0; it.hasNext(); i++) {
      Object next = it.next();
      if (getKeys().put(next, i) != null) {
        throw duplicateKey(next);
      }
      locations.add(next);
      order.add(i);
    }
  }

  private ObjectIndex(
      Map<Object, Integer> keys, List<Object> locations, List<Integer> order) {
    this.keys = keys;
    this.locations = locations;
    this.order = order;
  }

  public static ObjectIndex of(Vector vector) {
    return create(vector.asList(Object.class));
  }


  public static ObjectIndex of(Object... args) {
    return create(Arrays.asList(args));
  }

  public static <T> ObjectIndex create(Collection<? extends T> coll) {
    return new ObjectIndex(coll);
  }

  public static <T extends Comparable<T>> ObjectIndex createSorted(
      Collection<? extends T> collection) {
    TreeMap<Object, Integer> keys = new TreeMap<>();
    Iterator<? extends T> it = collection.iterator();
    List<Object> locations = new ArrayList<>();
//    List<>
    for (int i = 0; it.hasNext(); i++) {
      T next = it.next();
      if (keys.put(next, i) != null) {
        throw duplicateKey(next);
      }
      locations.add(next);
    }
    return new ObjectIndex(keys, locations, null);
  }

  private static UnsupportedOperationException duplicateKey(Object next) {
    return new UnsupportedOperationException(String.format("Duplicate key: %s", next));
  }

  private static NoSuchElementException noSuchElement(Object key) {
    return new NoSuchElementException(String.format("name '%s' not in index", key));
  }

  @Override
  public int getLocation(Object key) {
    Integer idx = getKeys().get(key);
    if (idx == null) {
      throw noSuchElement(key);
    }
    return idx;
  }

  @Override
  public Set<Object> selectRange(Object from, BoundType fromBound, Object to, BoundType toBound) {
    Check.argument(shareComparableSupertype(from, to), "keys are not comparable");
    Check.argument(!fromBound.greaterThan(compare(from, to)) &&
                   !toBound.lessThan(compare(to, from)),
                   "Illegal key ordering (to-key is larger than from-key)");

    Map<Object, Integer> includedKeys;
    if (getKeys() instanceof NavigableMap) {
      SortedMap<Object, Integer> tmp = ((NavigableMap<Object, Integer>) getKeys())
          .subMap(from, fromBound == BoundType.INCLUSIVE, to, toBound == BoundType.INCLUSIVE);

      // note: is this a good idea for saving memory?
      if (tmp.size() / getKeys().size() > 0.5) {
        includedKeys = new TreeMap<>(tmp.comparator());
        includedKeys.putAll(tmp);
      } else {
        includedKeys = tmp;
      }
    } else {
      includedKeys = new LinkedHashMap<>();
      for (Map.Entry<Object, Integer> entry : getKeys().entrySet()) {
        Object key = entry.getKey();
        if (fromBound.greaterThan(compare(key, from)) && toBound.lessThan(compare(key, to))) {
          includedKeys.put(key, entry.getValue());
        }
      }
    }
    return includedKeys.keySet();
  }

  protected boolean shareComparableSupertype(Object a, Object b) {
    return a instanceof Comparable && b instanceof Comparable && shareSupertype(a, b);
  }

  private boolean shareSupertype(Object a, Object b) {
    Class<?> clsA = a.getClass();
    Class<?> clsB = b.getClass();
    return clsA.equals(clsB) || clsA.isAssignableFrom(clsB) || clsB.isAssignableFrom(clsA);
  }

  @SuppressWarnings("unchecked")
  private int compare(Object a, Object b) {
    return ((Comparable) a).compareTo(b);
  }

  @Override
  public Object getKey(int location) {
    return locations.get(location);
  }

  @Override
  public boolean contains(Object key) {
    return getKeys().containsKey(key);
  }

  @Override
  public Collection<Integer> locations() {
    return getKeys().values();
  }

  @Override
  public Set<Entry> entrySet() {
    if (order == null) {
      return new EntrySet();
    }
    return new OrderedEntrySet();
  }

  @Override
  public boolean equals(Object object) {
    if (this == object) {
      return true;
    }
    if (object == null || getClass() != object.getClass()) {
      return false;
    }

    ObjectIndex entries = (ObjectIndex) object;
    return getKeys().equals(entries.getKeys());
  }

  @Override
  public int hashCode() {
    return getKeys().hashCode();
  }

  @Override
  public Set<Object> keySet() {
    // Already maintains an order
    if (keys instanceof NavigableMap) {
      return keys.keySet();
    }

    return new OrderedKeySet();
  }

  @Override
  public Builder newBuilder() {
    return new Builder();
  }

  @Override
  public Index.Builder newCopyBuilder() {
    return new Builder(getKeys(), locations, order);
  }

  @Override
  public int size() {
    return getKeys().size();
  }

  @Override
  public int[] locations(Object[] keys) {
    int[] indicies = new int[keys.length];
    for (int i = 0; i < keys.length; i++) {
      indicies[i] = this.getKeys().get(keys[i]);
    }
    return indicies;
  }

  @Override
  public String toString() {
    return getKeys().toString();
  }

  protected Map<Object, Integer> getKeys() {
    return keys;
  }

  public static final class Builder implements Index.Builder {

    private final static Comparator<Object> objectComparator =
        ObjectComparator.getInstance();
    private Map<Object, Integer> keys;
    private List<Object> locations;
    private List<Integer> order;
    private int currentSize = 0;


    public Builder() {
      this.keys = new HashMap<>();
      this.locations = new ArrayList<>();
      this.order = new ArrayList<>();
    }

    private Builder(
        Map<Object, Integer> keys, List<Object> locations, List<Integer> order) {
      if (keys instanceof SortedMap) {
        this.keys = new TreeMap<>(((SortedMap<Object, Integer>) keys).comparator());
        this.keys.putAll(keys);
      } else {
        this.keys = new HashMap<>(keys);
      }
      this.locations = new ArrayList<>(locations);
      this.order = new ArrayList<>(order);
      this.currentSize = this.locations.size();
    }

    @Override
    public boolean contains(Object key) {
      return getKeys().containsKey(key);
    }

    @Override
    public int getLocation(Object key) {
      Integer v = getKeys().get(key);
      if (v == null) {
        throw noSuchElement(key);
      }
      return v;
    }

    @Override
    public Object getKey(int index) {
      if (order != null) {
        return locations.get(order.get(index));
      } else {
        return locations.get(index);
      }
    }

    @Override
    public void add(Object key) {
      set(key, currentSize);
    }

    @Override
    public void add(int key) {
      set(key, currentSize);
    }

    @Override
    public void sort(Comparator<Object> cmp) {
      Map<Object, Integer> treeMap = new TreeMap<>(cmp);
      treeMap.putAll(getKeys());
      this.order = null; // ignore order now
      this.keys = treeMap;
    }

    @Override
    public void sort() {
      sort(objectComparator);
    }

    private void set(Object key, int index) {
      if (getKeys().put(key, index) != null) {
        throw duplicateKey(key);
      }
      locations.add(key);
      if (order != null) {
        order.add(index);
      }
      currentSize++;
    }

    private void set(int key, int index) {
      set((Object) key, index);
    }

    @Override
    public void extend(int size) {
      if (size > currentSize) {
        for (int i = currentSize; i < size; i++) {
          set(i, i);
        }
        currentSize = size;
      }
    }

    @Override
    public void resize(int size) {
      if (size < currentSize) {
        for (int i = currentSize - 1; i >= size; i--) {
          remove(i);
        }
      } else {
        extend(size);
      }
    }

    @Override
    public Index build() {
      Map<Object, Integer> immutableKeys;
      if (getKeys() instanceof NavigableMap) {
        immutableKeys = Collections.unmodifiableNavigableMap(
            (NavigableMap<Object, Integer>) getKeys());
      } else {
        immutableKeys = Collections.unmodifiableMap(getKeys());
      }
      Index index = new ObjectIndex(immutableKeys,
                                    Collections.unmodifiableList(locations),
                                    order == null ? null : Collections.unmodifiableList(order));
      this.keys = null;
      this.locations = null;
      this.order = null;
      return index;
    }

    @Override
    public void swap(int a, int b) {
      if (order == null) {
        return;
      }
      Collections.swap(order, a, b);
//      Collections.swap(locations, a, b);
//      Object keyA = locations.get(a);
//      Object keyB = locations.get(b);
//      locations.set(a, keyB);
//      locations.set(b, keyA);

      // swap
//      getKeys().put(keyA, getKeys().put(keyB, getKeys().get(keyA)));
//
//      Integer tmp = getKeys().get(keyA);
//      getKeys().put(keyA, getKeys().get(keyB));
//      getKeys().put(keyB, tmp);

    }

    @Override
    public int size() {
      return getKeys().size();
    }

    @Override
    public void remove(int index) {
      getKeys().remove(locations.remove(index));
      // TODO: remove order?
      // swap locations backwards
      for (int i = index; i < size(); i++) {
        Object key = locations.get(i);
        getKeys().compute(key, (k, v) -> v - 1);
      }

      if (order != null) {
        for (ListIterator<Integer> iterator = order.listIterator(); iterator.hasNext(); ) {
          int v = iterator.next();
          if (v > index) {
            iterator.set(v - 1);
          } else if (v == index) {
            iterator.remove();
          }
        }
      }
//      for (int i = 0; i < order.size(); i++) {
//        int v = order.get(i);
//
//      }
    }

    protected Map<Object, Integer> getKeys() {
      return keys;
    }
  }

  private class EntrySet extends AbstractSet<Entry> {

    @Override
    public Iterator<Entry> iterator() {

      return new Iterator<Entry>() {
        final Iterator<Map.Entry<Object, Integer>> it = getKeys().entrySet().iterator();

        @Override
        public boolean hasNext() {
          return it.hasNext();
        }

        @Override
        public Entry next() {
          Map.Entry<Object, Integer> entry = it.next();
          return new Entry(entry.getKey(), entry.getValue());
        }
      };
    }

    @Override
    public int size() {
      return ObjectIndex.this.size();
    }
  }

  private class OrderedEntrySet extends AbstractSet<Entry> {

    @Override
    public Iterator<Entry> iterator() {
      return new Iterator<Entry>() {
        int current = 0;

        @Override
        public boolean hasNext() {
          return current < size();
        }

        @Override
        public Entry next() {
          int value = order.get(current++);
          Object key = getKey(value);
          return new Entry(key, value);
        }
      };
    }

    @Override
    public int size() {
      return ObjectIndex.this.size();
    }
  }

  private class OrderedKeySet extends AbstractSet<Object> {

    @Override
    public Iterator<Object> iterator() {
      return new Iterator<Object>() {
        private int current = 0;

        @Override
        public boolean hasNext() {
          return current < size();
        }

        @Override
        public Object next() {
          int location = order.get(current++);
          return getKey(location);
        }
      };
    }

    @Override
    public int size() {
      return ObjectIndex.this.size();
    }
  }
}
