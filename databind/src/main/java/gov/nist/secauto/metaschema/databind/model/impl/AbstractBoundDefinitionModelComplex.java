/*
 * SPDX-FileCopyrightText: none
 * SPDX-License-Identifier: CC0-1.0
 */

package gov.nist.secauto.metaschema.databind.model.impl;

import gov.nist.secauto.metaschema.core.model.IBoundObject;
import gov.nist.secauto.metaschema.core.util.ObjectUtils;
import gov.nist.secauto.metaschema.databind.IBindingContext;
import gov.nist.secauto.metaschema.databind.io.BindingException;
import gov.nist.secauto.metaschema.databind.model.IBoundDefinitionModelComplex;
import gov.nist.secauto.metaschema.databind.model.IBoundInstanceFlag;
import gov.nist.secauto.metaschema.databind.model.IBoundModule;

import java.lang.annotation.Annotation;
import java.lang.reflect.Method;

import javax.xml.namespace.QName;

import edu.umd.cs.findbugs.annotations.NonNull;
import edu.umd.cs.findbugs.annotations.Nullable;
import nl.talsmasoftware.lazy4j.Lazy;

public abstract class AbstractBoundDefinitionModelComplex<A extends Annotation>
    implements IBoundDefinitionModelComplex {
  @NonNull
  private final Class<? extends IBoundObject> clazz;
  @NonNull
  private final A annotation;
  @NonNull
  private final IBindingContext bindingContext;
  @NonNull
  private final Lazy<IBoundModule> module;
  @NonNull
  private final Lazy<QName> qname;
  @NonNull
  private final Lazy<QName> definitionQName;
  @Nullable
  private final Method beforeDeserializeMethod;
  @Nullable
  private final Method afterDeserializeMethod;

  protected AbstractBoundDefinitionModelComplex(
      @NonNull Class<? extends IBoundObject> clazz,
      @NonNull A annotation,
      @NonNull Class<? extends IBoundModule> moduleClass,
      @NonNull IBindingContext bindingContext) {
    this.clazz = clazz;
    this.annotation = annotation;
    this.bindingContext = bindingContext;
    this.module = ObjectUtils.notNull(Lazy.lazy(() -> bindingContext.registerModule(moduleClass)));
    this.qname = ObjectUtils.notNull(Lazy.lazy(() -> getContainingModule().toModelQName(getEffectiveName())));
    this.definitionQName = ObjectUtils.notNull(Lazy.lazy(() -> getContainingModule().toModelQName(getName())));
    this.beforeDeserializeMethod = ClassIntrospector.getMatchingMethod(
        clazz,
        "beforeDeserialize",
        Object.class);
    this.afterDeserializeMethod = ClassIntrospector.getMatchingMethod(
        clazz,
        "afterDeserialize",
        Object.class);
  }

  @Override
  public Class<? extends IBoundObject> getBoundClass() {
    return clazz;
  }

  public A getAnnotation() {
    return annotation;
  }

  @Override
  @NonNull
  public IBoundModule getContainingModule() {
    return ObjectUtils.notNull(module.get());
  }

  @Override
  @NonNull
  public IBindingContext getBindingContext() {
    return bindingContext;
  }

  @SuppressWarnings("null")
  @Override
  public final QName getXmlQName() {
    return qname.get();
  }

  @SuppressWarnings("null")
  @Override
  public final QName getDefinitionQName() {
    return definitionQName.get();
  }

  @Override
  public boolean isInline() {
    return getBoundClass().getEnclosingClass() != null;
  }

  @Override
  public Method getBeforeDeserializeMethod() {
    return beforeDeserializeMethod;
  }

  @Override
  public Method getAfterDeserializeMethod() {
    return afterDeserializeMethod;
  }

  // @Override
  // public String getJsonKeyFlagName() {
  // // definition items never have a JSON key
  // return null;
  // }

  @Override
  public IBoundObject deepCopyItem(IBoundObject item, IBoundObject parentInstance) throws BindingException {
    IBoundObject instance = newInstance(item::getMetaschemaData);

    callBeforeDeserialize(instance, parentInstance);

    deepCopyItemInternal(item, instance);

    callAfterDeserialize(instance, parentInstance);

    return instance;
  }

  protected void deepCopyItemInternal(@NonNull IBoundObject fromObject, @NonNull IBoundObject toObject)
      throws BindingException {
    for (IBoundInstanceFlag instance : getFlagInstances()) {
      instance.deepCopy(fromObject, toObject);
    }
  }
}
