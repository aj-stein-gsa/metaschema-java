/*
 * Portions of this software was developed by employees of the National Institute
 * of Standards and Technology (NIST), an agency of the Federal Government and is
 * being made available as a public service. Pursuant to title 17 United States
 * Code Section 105, works of NIST employees are not subject to copyright
 * protection in the United States. This software may be subject to foreign
 * copyright. Permission in the United States and in foreign countries, to the
 * extent that NIST may hold copyright, to use, copy, modify, create derivative
 * works, and distribute this software and its documentation without fee is hereby
 * granted on a non-exclusive basis, provided that this notice and disclaimer
 * of warranty appears in all copies.
 *
 * THE SOFTWARE IS PROVIDED 'AS IS' WITHOUT ANY WARRANTY OF ANY KIND, EITHER
 * EXPRESSED, IMPLIED, OR STATUTORY, INCLUDING, BUT NOT LIMITED TO, ANY WARRANTY
 * THAT THE SOFTWARE WILL CONFORM TO SPECIFICATIONS, ANY IMPLIED WARRANTIES OF
 * MERCHANTABILITY, FITNESS FOR A PARTICULAR PURPOSE, AND FREEDOM FROM
 * INFRINGEMENT, AND ANY WARRANTY THAT THE DOCUMENTATION WILL CONFORM TO THE
 * SOFTWARE, OR ANY WARRANTY THAT THE SOFTWARE WILL BE ERROR FREE.  IN NO EVENT
 * SHALL NIST BE LIABLE FOR ANY DAMAGES, INCLUDING, BUT NOT LIMITED TO, DIRECT,
 * INDIRECT, SPECIAL OR CONSEQUENTIAL DAMAGES, ARISING OUT OF, RESULTING FROM,
 * OR IN ANY WAY CONNECTED WITH THIS SOFTWARE, WHETHER OR NOT BASED UPON WARRANTY,
 * CONTRACT, TORT, OR OTHERWISE, WHETHER OR NOT INJURY WAS SUSTAINED BY PERSONS OR
 * PROPERTY OR OTHERWISE, AND WHETHER OR NOT LOSS WAS SUSTAINED FROM, OR AROSE OUT
 * OF THE RESULTS OF, OR USE OF, THE SOFTWARE OR SERVICES PROVIDED HEREUNDER.
 */

package gov.nist.secauto.metaschema.core.configuration;

import edu.umd.cs.findbugs.annotations.NonNull;

/**
 * Provides a complete, abstract implementation of a generalized feature.
 * Feature implementations can extend this class the implement configuration
 * sets for a given purpose.
 *
 * @param <V>
 *          the feature value Java type
 */
public abstract class AbstractConfigurationFeature<V> implements IConfigurationFeature<V> {
  @NonNull
  private final String name;
  @NonNull
  private final Class<V> valueClass;
  @NonNull
  private final V defaultValue;

  /**
   * Construct a new feature with a default value.
   *
   * @param name
   *          the name of the feature
   * @param valueClass
   *          the class of the feature's value
   * @param defaultValue
   *          the value's default
   */
  protected AbstractConfigurationFeature(
      @NonNull String name,
      @NonNull Class<V> valueClass,
      @NonNull V defaultValue) {
    this.name = name;
    this.valueClass = valueClass;
    this.defaultValue = defaultValue;
  }

  @Override
  public String getName() {
    return name;
  }

  @Override
  public V getDefault() {
    return defaultValue;
  }

  @Override
  public Class<V> getValueClass() {
    return valueClass;
  }

  @Override
  public String toString() {
    return new StringBuilder()
        .append(getName())
        .append('(')
        .append(getDefault().toString())
        .append(')')
        .toString();
  }
}
