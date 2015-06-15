package org.briljantframework.dataframe;


import com.google.common.collect.Iterators;

import org.jetbrains.annotations.NotNull;

import java.util.AbstractCollection;
import java.util.AbstractList;
import java.util.AbstractSet;
import java.util.Collection;
import java.util.Iterator;
import java.util.Map;
import java.util.NoSuchElementException;
import java.util.Set;

/**
 * @author Isak Karlsson
 */
public class IntIndex extends AbstractList<Object> implements Index {

  private final int size;

  public IntIndex(int size) {
    this.size = size;
  }

  private NoSuchElementException noSuchElement(Object key) {
    return new NoSuchElementException(String.format("name '%s' not in index", key));
  }

  @Override
  public int index(Object key) {
    if (key instanceof Integer) {
      int k = (int) key;
      if (k < size && k > 0) {
        return k;
      }
    }
    throw noSuchElement(key);
  }

  @Override
  public Object get(int index) {
    if (index >= 0 && index < size) {
      return index;
    }
    throw noSuchElement(index);
  }

  @Override
  public boolean contains(Object key) {
    if (key instanceof Integer) {
      int k = (int) key;
      if (k >= 0 && k < size) {
        return true;
      }
    }
    return false;
  }

  @Override
  public Collection<Integer> indices() {
    return new AbstractCollection<Integer>() {
      @Override
      public Iterator<Integer> iterator() {
        return new Iterator<Integer>() {
          public int current = 0;

          @Override
          public boolean hasNext() {
            return current < size;
          }

          @Override
          public Integer next() {
            if (current >= size) {
              throw new NoSuchElementException();
            }
            return current++;
          }
        };
      }

      @Override
      public int size() {
        return size;
      }
    };
  }

  @Override
  public Set<Entry> entrySet() {
    return new AbstractSet<Entry>() {
      @NotNull
      @Override
      public Iterator<Entry> iterator() {
        return new Iterator<Entry>() {
          private int current = 0;

          @Override
          public boolean hasNext() {
            return current < size();
          }

          @Override
          public Entry next() {
            int i = current++;
            return new Entry(i, i);
          }
        };
      }

      @Override
      public int size() {
        return IntIndex.this.size();
      }
    };
  }

  @Override
  public Set<Object> keySet() {
    return new AbstractSet<Object>() {
      @Override
      public Iterator<Object> iterator() {
        return new Iterator<Object>() {
          public int current = 0;

          @Override
          public boolean hasNext() {
            return current < size;
          }

          @Override
          public Object next() {
            if (current >= size) {
              throw new NoSuchElementException();
            }
            return current++;
          }
        };
      }

      @Override
      public int size() {
        return size;
      }
    };
  }

  @Override
  public Collection<Integer> indices(Object[] keys) {
    return new AbstractCollection<Integer>() {
      @Override
      public Iterator<Integer> iterator() {
        return new Iterator<Integer>() {
          private int current = 0;

          @Override
          public boolean hasNext() {
            return current < keys.length;
          }

          @Override
          public Integer next() {
            return index(keys[current++]);
          }
        };
      }

      @Override
      public int size() {
        return size;
      }
    };
  }

  @Override
  public Map<Integer, Object> indexMap() {
    return null; // TODO: fix
  }

  @Override
  public Builder newBuilder() {
    return new Builder() {

      private HashIndex.Builder builder;
      private int currentSize = 0;

      @Override
      public boolean contains(Object key) {
        if (isMonotonicallyIncreasing(key)) {
          int k = (int) key;
          return k >= 0 && k < currentSize;
        } else {
          initializeHashBuilder();
          return builder.contains(key);
        }
      }

      private void initializeHashBuilder() {
        if (builder == null) {
          builder = new HashIndex.Builder();
          for (int i = 0; i < currentSize; i++) {
            builder.set(i, i);
          }
        }
      }

      @Override
      public int index(Object key) {
        if (isMonotonicallyIncreasing(key)) {
          return (int) key;
        } else {
          initializeHashBuilder();
          return builder.index(key);
        }
      }

      @Override
      public Object get(int index) {
        if (index > currentSize) {
          throw noSuchElement(index);
        }
        return builder == null ? index : builder.get(index);
      }

      @Override
      public void add(Object key) {
        set(key, currentSize);
      }

      private boolean isMonotonicallyIncreasing(Object key) {
        return key instanceof Integer && builder == null;
      }

      private RuntimeException nonMonotonicallyIncreasingIndex(int index) {
        return new UnsupportedOperationException(
            String.format("Creating gap in index. current != index (%d != %d)",
                          currentSize, index)
        );
      }

      @Override
      public void set(Object key, int index) {
        if (index > currentSize) {
          throw nonMonotonicallyIncreasingIndex(index);
        }
        if (!isMonotonicallyIncreasing(key) || !key.equals(index)) {
          initializeHashBuilder();
          builder.set(key, index);
        }
        currentSize++;
      }

      @Override
      public Index build() {
        if (builder == null) {
          return new IntIndex(currentSize);
        } else {
          return builder.build();
        }
      }

      @Override
      public void set(Entry entry) {
        set(entry.key(), entry.index());
      }

      @Override
      public void putAll(Set<Entry> entries) {
        entries.forEach(this::set);
      }

      @Override
      public void swap(int a, int b) {

      }

      @Override
      public int size() {
        return builder == null ? currentSize : builder.size();
      }
    };
  }

  @Override
  public int size() {
    return size;
  }

  @Override
  public String toString() {
    return Iterators.toString(iterator());
  }
}