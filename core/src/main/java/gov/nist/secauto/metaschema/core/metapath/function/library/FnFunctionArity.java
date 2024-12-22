/*
 * SPDX-FileCopyrightText: none
 * SPDX-License-Identifier: CC0-1.0
 */

package gov.nist.secauto.metaschema.core.metapath.function.library;

import gov.nist.secauto.metaschema.core.metapath.DynamicContext;
import gov.nist.secauto.metaschema.core.metapath.MetapathConstants;
import gov.nist.secauto.metaschema.core.metapath.function.FunctionUtils;
import gov.nist.secauto.metaschema.core.metapath.function.IArgument;
import gov.nist.secauto.metaschema.core.metapath.function.IFunction;
import gov.nist.secauto.metaschema.core.metapath.item.IItem;
import gov.nist.secauto.metaschema.core.metapath.item.ISequence;
import gov.nist.secauto.metaschema.core.metapath.item.atomic.IIntegerItem;
import gov.nist.secauto.metaschema.core.util.ObjectUtils;

import java.util.List;

import edu.umd.cs.findbugs.annotations.NonNull;

/**
 * /** Implements <a href=
 * "https://www.w3.org/TR/xpath-functions-31/#func-function-arity">fn:function-arity</a>
 * functions.
 */
public final class FnFunctionArity {
  @NonNull
  private static final String NAME = "function-arity";
  @NonNull
  static final IFunction SIGNATURE = IFunction.builder()
      .name(NAME)
      .namespace(MetapathConstants.NS_METAPATH_FUNCTIONS)
      .deterministic()
      .contextDependent()
      .focusIndependent()
      .argument(IArgument.builder()
          .name("func")
          .type(IItem.type())
          .one()
          .build())
      .returnType(IIntegerItem.type())
      .returnOne()
      .functionHandler(FnFunctionArity::execute)
      .build();

  @SuppressWarnings("unused")
  @NonNull
  private static ISequence<IIntegerItem> execute(@NonNull IFunction function,
      @NonNull List<ISequence<?>> arguments,
      @NonNull DynamicContext dynamicContext,
      IItem focus) {
    try {
      IFunction fn = FunctionUtils.asType(ObjectUtils.requireNonNull(arguments.get(0).getFirstItem(true)));
    	return ISequence.of(IIntegerItem.valueOf(fn.arity()));
    } catch (Exception ex) {
    	// TODO: Spec is unclear for no function provided
		  return ISequence.empty();
	  }
  }

  private FnFunctionArity() {
    // disable construction
  }
}
