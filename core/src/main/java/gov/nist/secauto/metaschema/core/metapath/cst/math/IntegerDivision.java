/*
 * SPDX-FileCopyrightText: none
 * SPDX-License-Identifier: CC0-1.0
 */

package gov.nist.secauto.metaschema.core.metapath.cst.math;

import gov.nist.secauto.metaschema.core.metapath.DynamicContext;
import gov.nist.secauto.metaschema.core.metapath.ISequence;
import gov.nist.secauto.metaschema.core.metapath.cst.IExpression;
import gov.nist.secauto.metaschema.core.metapath.cst.IExpressionVisitor;
import gov.nist.secauto.metaschema.core.metapath.function.FunctionUtils;
import gov.nist.secauto.metaschema.core.metapath.function.OperationFunctions;
import gov.nist.secauto.metaschema.core.metapath.item.atomic.IIntegerItem;
import gov.nist.secauto.metaschema.core.metapath.item.atomic.INumericItem;

import edu.umd.cs.findbugs.annotations.NonNull;
import edu.umd.cs.findbugs.annotations.Nullable;

public class IntegerDivision
    extends AbstractArithmeticExpression<IIntegerItem> {

  /**
   * Create an expression that gets the whole number quotient result by dividing
   * the dividend by the divisor.
   *
   * @param dividend
   *          the expression whose item result will be divided
   * @param divisor
   *          the expression whose item result will be divided by
   */
  public IntegerDivision(@NonNull IExpression dividend, @NonNull IExpression divisor) {
    super(dividend, divisor, IIntegerItem.class);
  }

  @Override
  public Class<IIntegerItem> getBaseResultType() {
    return IIntegerItem.class;
  }

  @Override
  public <RESULT, CONTEXT> RESULT accept(IExpressionVisitor<RESULT, CONTEXT> visitor, CONTEXT context) {
    return visitor.visitIntegerDivision(this, context);
  }

  @Override
  public ISequence<? extends IIntegerItem> accept(DynamicContext dynamicContext, ISequence<?> focus) {
    INumericItem dividend = FunctionUtils.toNumericOrNull(
        getFirstDataItem(getLeft().accept(dynamicContext, focus), true));
    INumericItem divisor = FunctionUtils.toNumericOrNull(
        getFirstDataItem(getRight().accept(dynamicContext, focus), true));

    return resultOrEmpty(dividend, divisor);
  }

  /**
   * Get the whole number quotient result by dividing the dividend by the divisor.
   *
   * @param dividend
   *          the item to be divided
   * @param divisor
   *          the item to divide by
   * @return the quotient result or an empty {@link ISequence} if either item is
   *         {@code null}
   */
  @NonNull
  protected static ISequence<? extends IIntegerItem> resultOrEmpty(@Nullable INumericItem dividend,
      @Nullable INumericItem divisor) {
    ISequence<? extends IIntegerItem> retval;
    if (dividend == null || divisor == null) {
      retval = ISequence.empty();
    } else {
      IIntegerItem result = divide(dividend, divisor);
      retval = ISequence.of(result);
    }
    return retval;
  }

  /**
   * Get the whole number quotient result by dividing the dividend by the divisor.
   *
   * @param dividend
   *          the item to be divided
   * @param divisor
   *          the item to divide by
   * @return the quotient result
   */
  public static IIntegerItem divide(@NonNull INumericItem dividend, @NonNull INumericItem divisor) {
    return OperationFunctions.opNumericIntegerDivide(dividend, divisor);
  }
}
