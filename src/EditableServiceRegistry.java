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

/**
 * A service registry that can be modified.
 */
public abstract class EditableServiceRegistry extends ServiceRegistry {

  /**
   * Registers the given service in this service registry, with a default priority.
   *
   * @param aService a service.
   */
  public abstract void register(Object aService);

  /**
   * Registers the given object in this service registry, with the given priority.
   *
   * @param aService  a service
   * @param aPriority a priority
   */
  public abstract void register(Object aService, int aPriority);

  /**
   * Unregisters the given service in this service registry.
   *
   * @param aService a service.
   */
  public abstract void unregister(Object aService);
}
