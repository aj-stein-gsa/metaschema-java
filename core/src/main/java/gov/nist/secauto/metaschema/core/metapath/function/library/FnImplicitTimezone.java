/*
 * SPDX-FileCopyrightText: none
 * SPDX-License-Identifier: CC0-1.0
 */

package gov.nist.secauto.metaschema.core.metapath.function.library;

import gov.nist.secauto.metaschema.core.metapath.DynamicContext;
import gov.nist.secauto.metaschema.core.metapath.ISequence;
import gov.nist.secauto.metaschema.core.metapath.MetapathConstants;
import gov.nist.secauto.metaschema.core.metapath.function.IFunction;
import gov.nist.secauto.metaschema.core.metapath.item.IItem;
import gov.nist.secauto.metaschema.core.metapath.item.atomic.IDateTimeItem;
import gov.nist.secauto.metaschema.core.metapath.item.atomic.IDayTimeDurationItem;

import java.util.List;

import edu.umd.cs.findbugs.annotations.NonNull;

/**
 * Implements <a href=
 * "https://www.w3.org/TR/xpath-functions-31/#func-implicit-timezone">fn:implicit-timezone</a>.
 */
public final class FnImplicitTimezone {
  @NonNull
  static final IFunction SIGNATURE = IFunction.builder()
      .name("implicit-timezone")
      .namespace(MetapathConstants.NS_METAPATH_FUNCTIONS)
      .deterministic()
      .contextDependent()
      .focusIndependent()
      .returnType(IDayTimeDurationItem.class)
      .returnOne()
      .functionHandler(FnImplicitTimezone::execute)
      .build();

  private FnImplicitTimezone() {
    // disable construction
  }

  @SuppressWarnings("unused")
  @NonNull
  private static ISequence<IDayTimeDurationItem> execute(@NonNull IFunction function,
      @NonNull List<ISequence<?>> arguments,
      @NonNull DynamicContext dynamicContext,
      IItem focus) {
    return ISequence.of(fnImplicitTimezone(dynamicContext));
  }

  /**
   * Implements <a href=
   * "https://www.w3.org/TR/xpath-functions-31/#func-implicit-timezone">fn:implicit-timezone</a>.
   *
   * @param dynamicContext
   *          the dynamic evaluation context
   * @return the current date
   */
  @NonNull
  public static IDayTimeDurationItem fnImplicitTimezone(@NonNull DynamicContext dynamicContext) {
    return IDayTimeDurationItem.valueOf(dynamicContext.getImplicitTimeZone().toString());
  }
}
