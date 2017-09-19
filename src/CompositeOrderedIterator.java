/*
 *
 * Copyright (c) 1999-2017 Luciad All Rights Reserved.
 *
 * Luciad grants you ("Licensee") a non-exclusive, royalty free, license to use,
 * modify and redistribute this software in source and binary code form,
 * provided that i) this copyright notice and license appear on all copies of
 * the software; and ii) Licensee does not utilize the software in a manner
 * which is disparaging to Luciad.
 *
 * This software is provided "AS IS," without a warranty of any kind. ALL
 * EXPRESS OR IMPLIED CONDITIONS, REPRESENTATIONS AND WARRANTIES, INCLUDING ANY
 * IMPLIED WARRANTY OF MERCHANTABILITY, FITNESS FOR A PARTICULAR PURPOSE OR
 * NON-INFRINGEMENT, ARE HEREBY EXCLUDED. LUCIAD AND ITS LICENSORS SHALL NOT BE
 * LIABLE FOR ANY DAMAGES SUFFERED BY LICENSEE AS A RESULT OF USING, MODIFYING
 * OR DISTRIBUTING THE SOFTWARE OR ITS DERIVATIVES. IN NO EVENT WILL LUCIAD OR ITS
 * LICENSORS BE LIABLE FOR ANY LOST REVENUE, PROFIT OR DATA, OR FOR DIRECT,
 * INDIRECT, SPECIAL, CONSEQUENTIAL, INCIDENTAL OR PUNITIVE DAMAGES, HOWEVER
 * CAUSED AND REGARDLESS OF THE THEORY OF LIABILITY, ARISING OUT OF THE USE OF
 * OR INABILITY TO USE SOFTWARE, EVEN IF LUCIAD HAS BEEN ADVISED OF THE
 * POSSIBILITY OF SUCH DAMAGES.
 */
//package samples.common.util;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import java.util.NoSuchElementException;

/**
 * Composite iterator that takes into account the ordering of elements from the delegate Iterator instances.
 * Note: The delegate iterators must be sorted already. This allows this class to efficiently (and lazily) merge
 * the delegate iterators without having the entire list of elements in memory.
 */
public class CompositeOrderedIterator<T> implements Iterator<T> {

  private final IteratorInfoQueue<T> fQueue = new IteratorInfoQueue<>();
  private final OrderProvider<T> fOrderProvider;

  private T fNext = null;

  public CompositeOrderedIterator(List<Iterator<T>> aIterators, OrderProvider<T> aOrderProvider) {
    fOrderProvider = aOrderProvider;
    for (Iterator<T> iterator : aIterators) {
      if (iterator == null) {
        continue;
      }

      if (!iterator.hasNext()) {
        continue;
      }
      T next = iterator.next();

      IteratorInfo<T> iterator_info = new IteratorInfo<T>();
      iterator_info.fIterator = iterator;
      iterator_info.fNext = next;
      iterator_info.fOrderIndex = fOrderProvider.getOrderIndex(next, iterator);

      fQueue.addIteratorInfo(iterator_info);
    }
  }

  @Override
  public boolean hasNext() {
    fNext = getNext();
    return fNext != null;
  }

  @Override
  public T next() {
    if (!hasNext()) {
      throw new NoSuchElementException("hasNext() returns false. next() should not be called.");
    }
    T next = fNext;
    fNext = null;
    return next;
  }

  @Override
  public void remove() {
    throw new UnsupportedOperationException("Removing elements using this iterator is not supported, use ServiceRegistry methods instead.");
  }

  private T getNext() {
    if (fNext != null) {
      return fNext;
    }

    IteratorInfo<T> iteratorInfo = fQueue.smallest();
    if (iteratorInfo == null) {
      return null;
    }

    T instance = iteratorInfo.fNext;

    if (!iteratorInfo.fIterator.hasNext()) {
      iteratorInfo.fNext = null;
      iteratorInfo.fOrderIndex = -1;
    } else {
      T next = iteratorInfo.fIterator.next();
      iteratorInfo.fNext = next;
      iteratorInfo.fOrderIndex = fOrderProvider.getOrderIndex(next, iteratorInfo.fIterator);
    }

    return instance;
  }

  private static class IteratorInfo<T> {
    private Iterator<T> fIterator;
    private int fOrderIndex;
    private T fNext;
  }

  private static class IteratorInfoQueue<T> {
    private List<IteratorInfo<T>> fQueue = new ArrayList<>();

    public void addIteratorInfo(IteratorInfo<T> aInfo) {
      fQueue.add(aInfo);
    }

    public IteratorInfo<T> smallest() {
      if (fQueue.size() == 0) {
        return null;
      }
      IteratorInfo<T> firstInfo = null;

      for (IteratorInfo<T> info : fQueue) {
        if (info.fOrderIndex == -1) {
          // Iterator has been depleted => skip
          continue;
        }
        if (firstInfo == null || info.fOrderIndex < firstInfo.fOrderIndex) {
          firstInfo = info;
        }
      }
      return firstInfo;
    }
  }

  public interface OrderProvider<T> {
    /**
     * Returns the order of the given object.
     * <ul>
     *   <li>Smaller value => earlier in the order.</li>
     *   <li>Larger value => later in the order</li>
     *   <li>Must be equal to or larger than 0</li>
     * </ul>
     *
     * @param aObject the object for which to get the order index.
     * @param aIterator the iterator from which the given object was returned. Provided as context info.
     *
     * @return the order index.
     */
    int getOrderIndex(T aObject, Iterator<T> aIterator);

  }
}
