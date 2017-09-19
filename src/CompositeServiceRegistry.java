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
//package samples.common.serviceregistry;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.Iterator;
import java.util.List;
import java.util.function.Consumer;
import java.util.stream.Collectors;

//import samples.common.util.CompositeOrderedIterator;

/**
 * <p>Service registry that is composed of other service registries.</p>
 *
 * <p>Note that priorities are not fully respected when using this class. The order in which the
 * iterator returns the services is determined by
 * <ul>
 *   <li>The order in which the delegate service registries were added.</li>
 *   <li>Per delegate service registry: the order in which the services are returned.</li>
 * </ul>
 * </p>
 */
public class CompositeServiceRegistry extends ServiceRegistry {

  private final Collection<ServiceRegistry> fServiceRegistries = new ArrayList<ServiceRegistry>();

  /**
   * Creates a new composite service registry from the given collection of service registries.
   * @param aServiceRegistries the delegate service registries.
   */
  public CompositeServiceRegistry(Collection<? extends ServiceRegistry> aServiceRegistries) {
    fServiceRegistries.addAll(aServiceRegistries);
  }

  /**
   * Creates a new composite service registry from the given collection of service registries.
   * @param aServiceRegistries the delegate service registries.
   */
  public CompositeServiceRegistry(ServiceRegistry... aServiceRegistries) {
    fServiceRegistries.addAll(Arrays.asList(aServiceRegistries));
  }

  @Override
  public <T> Iterable<T> query(Class<T> aClass) {
    // Return a composite iterable of all iterables returned by the delegate service registries
    List<Iterable<T>> delegates = getDelegateIterables(aClass);
    return new CompositeIterable<T>(delegates);
  }

  @Override
  public int getPriority(Object aService) {
    for (ServiceRegistry serviceRegistry : fServiceRegistries) {
      try {
        return serviceRegistry.getPriority(aService);
      } catch (IllegalArgumentException e) {
        // Do nothing, try the next service registry
      }
    }
    throw new IllegalArgumentException("No priority for service " + aService);
  }

  private <T> List<Iterable<T>> getDelegateIterables(Class<T> aClass) {
    List<Iterable<T>> iterables = new ArrayList<Iterable<T>>();

    for (ServiceRegistry serviceRegistry : fServiceRegistries) {
      Iterable<T> iterable = serviceRegistry.query(aClass);
      if (iterable != null) {
        Iterable<T> wrappedIterable = new ServiceRegistryIterableWrapper<>(iterable, serviceRegistry);
        iterables.add(wrappedIterable);
      }
    }
    return iterables;
  }

  private static class CompositeIterable<T> implements Iterable<T> {

    private final List<Iterable<T>> fDelegates;

    public CompositeIterable(List<Iterable<T>> aDelegates) {
      fDelegates = aDelegates;
    }

    @Override
    public Iterator<T> iterator() {
      List<Iterator<T>> iterators = fDelegates.stream().map(Iterable::iterator).collect(Collectors.toList());
      return new CompositeOrderedIterator<T>(iterators, (instance, iterator) -> {
        ServiceRegistryIteratorWrapper<T> iteratorWrapper = (ServiceRegistryIteratorWrapper<T>) iterator;
        ServiceRegistry serviceRegistry = iteratorWrapper.getServiceRegistry();
        return serviceRegistry.getPriority(instance);
      });
    }
  }

  private class ServiceRegistryIterableWrapper<T> implements Iterable<T> {

    private final Iterable<T> fDelegate;
    private final ServiceRegistry fServiceRegistry;

    public ServiceRegistryIterableWrapper(Iterable<T> aDelegate, ServiceRegistry aServiceRegistry) {
      fDelegate = aDelegate;
      fServiceRegistry = aServiceRegistry;
    }

    @Override
    public Iterator<T> iterator() {
      Iterator<T> delegate = fDelegate.iterator();
      return new ServiceRegistryIteratorWrapper<>(delegate, fServiceRegistry);
    }
  }

  private class ServiceRegistryIteratorWrapper<T> implements Iterator<T> {

    private final Iterator<T> fDelegate;
    private final ServiceRegistry fServiceRegistry;

    public ServiceRegistryIteratorWrapper(Iterator<T> aDelegate, ServiceRegistry aServiceRegistry) {
      fDelegate = aDelegate;
      fServiceRegistry = aServiceRegistry;
    }

    @Override
    public boolean hasNext() {
      return fDelegate.hasNext();
    }

    @Override
    public T next() {
      return fDelegate.next();
    }

    @Override
    public void remove() {
      fDelegate.remove();
    }

    @Override
    public void forEachRemaining(Consumer<? super T> action) {
      fDelegate.forEachRemaining(action);
    }

    public ServiceRegistry getServiceRegistry() {
      return fServiceRegistry;
    }
  }


}
