/*
 * SPDX-FileCopyrightText: none
 * SPDX-License-Identifier: CC0-1.0
 */

package gov.nist.secauto.metaschema.core.metapath.function.library;

import gov.nist.secauto.metaschema.core.metapath.DynamicContext;
import gov.nist.secauto.metaschema.core.metapath.ISequence;
import gov.nist.secauto.metaschema.core.metapath.MetapathConstants;
import gov.nist.secauto.metaschema.core.metapath.function.FunctionUtils;
import gov.nist.secauto.metaschema.core.metapath.function.IArgument;
import gov.nist.secauto.metaschema.core.metapath.function.IFunction;
import gov.nist.secauto.metaschema.core.metapath.item.IItem;
import gov.nist.secauto.metaschema.core.metapath.item.atomic.IAnyUriItem;
import gov.nist.secauto.metaschema.core.metapath.item.node.INodeItem;
import gov.nist.secauto.metaschema.core.util.ObjectUtils;

import java.net.URI;
import java.util.List;

import edu.umd.cs.findbugs.annotations.NonNull;
import edu.umd.cs.findbugs.annotations.Nullable;

/**
 * Since a node doesn't have a base URI in Metaschema, this is an alias for the
 * document-uri function.
 */
public final class FnBaseUri {

  @NonNull
  static final IFunction SIGNATURE_NO_ARG = IFunction.builder()
      .name("base-uri")
      .namespace(MetapathConstants.NS_METAPATH_FUNCTIONS)
      .deterministic()
      .contextDependent()
      .focusDependent()
      .returnType(IAnyUriItem.class)
      .returnOne()
      .functionHandler(FnBaseUri::executeNoArg)
      .build();

  @NonNull
  static final IFunction SIGNATURE_ONE_ARG = IFunction.builder()
      .name("base-uri")
      .namespace(MetapathConstants.NS_METAPATH_FUNCTIONS)
      .deterministic()
      .contextIndependent()
      .focusIndependent()
      .argument(IArgument.builder()
          .name("arg1")
          .type(INodeItem.class)
          .zeroOrOne()
          .build())
      .returnType(IAnyUriItem.class)
      .returnOne()
      .functionHandler(FnBaseUri::executeOneArg)
      .build();

  private FnBaseUri() {
    // disable construction
  }

  @SuppressWarnings("unused")
  @NonNull
  private static ISequence<IAnyUriItem> executeNoArg(@NonNull IFunction function,
      @NonNull List<ISequence<?>> arguments,
      @NonNull DynamicContext dynamicContext,
      IItem focus) {
    return ISequence.of(fnBaseUri(
        FunctionUtils.requireTypeOrNull(INodeItem.class, focus)));
  }

  @SuppressWarnings("unused")
  @NonNull
  private static ISequence<IAnyUriItem> executeOneArg(@NonNull IFunction function,
      @NonNull List<ISequence<?>> arguments,
      @NonNull DynamicContext dynamicContext,
      IItem focus) {

    ISequence<? extends INodeItem> arg = FunctionUtils.asType(ObjectUtils.requireNonNull(arguments.get(0)));

    INodeItem item = arg.getFirstItem(true);

    return ISequence.of(fnBaseUri(item));
  }

  /**
   * Get the base URI for the provided {@code nodeItem}.
   * <p>
   * Based on the XPath 3.1 <a href=
   * "https://www.w3.org/TR/xpath-functions-31/#func-base-uri">fn:base-uri</a>
   * function.
   *
   * @param nodeItem
   *          the node to get the base URI from
   * @return the base URI, or {@code null} if the node is either null or doesn't
   *         have a base URI
   */
  @SuppressWarnings("PMD.NullAssignment") // for readability
  @Nullable
  public static IAnyUriItem fnBaseUri(INodeItem nodeItem) {
    IAnyUriItem retval;
    if (nodeItem == null) {
      retval = null; // NOPMD - intentional
    } else {
      URI baseUri = nodeItem.getBaseUri();
      retval = baseUri == null ? null : IAnyUriItem.valueOf(baseUri);
    }
    return retval;
  }
}
