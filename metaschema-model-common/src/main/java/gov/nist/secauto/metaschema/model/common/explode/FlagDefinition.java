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

package gov.nist.secauto.metaschema.model.common.explode;

import gov.nist.secauto.metaschema.model.common.IMetaschema;
import gov.nist.secauto.metaschema.model.common.ModuleScopeEnum;
import gov.nist.secauto.metaschema.model.common.constraint.IAllowedValuesConstraint;
import gov.nist.secauto.metaschema.model.common.constraint.IConstraint;
import gov.nist.secauto.metaschema.model.common.constraint.IExpectConstraint;
import gov.nist.secauto.metaschema.model.common.constraint.IIndexHasKeyConstraint;
import gov.nist.secauto.metaschema.model.common.constraint.IMatchesConstraint;
import gov.nist.secauto.metaschema.model.common.datatype.IJavaTypeAdapter;
import gov.nist.secauto.metaschema.model.common.datatype.markup.MarkupLine;
import gov.nist.secauto.metaschema.model.common.definition.IFlagDefinition;

import org.jetbrains.annotations.NotNull;

import java.util.List;

public class FlagDefinition
    extends AbstractDefinition<IFlagDefinition>
    implements IFlagDefinition {

  public FlagDefinition(@NotNull IFlagDefinition delegate) {
    super(delegate);
  }

  @Override
  public IJavaTypeAdapter<?> getDatatype() {
    return getDelegate().getDatatype();
  }

  @Override
  public List<? extends IConstraint> getConstraints() {
    return getDelegate().getConstraints();
  }

  @Override
  public List<? extends IAllowedValuesConstraint> getAllowedValuesContraints() {
    return getDelegate().getAllowedValuesContraints();
  }

  @Override
  public List<? extends IMatchesConstraint> getMatchesConstraints() {
    return getDelegate().getMatchesConstraints();
  }

  @Override
  public List<? extends IIndexHasKeyConstraint> getIndexHasKeyConstraints() {
    return getDelegate().getIndexHasKeyConstraints();
  }

  @Override
  public List<? extends IExpectConstraint> getExpectConstraints() {
    return getDelegate().getExpectConstraints();
  }

  @Override
  public IMetaschema getContainingMetaschema() {
    return getDelegate().getContainingMetaschema();
  }

  @Override
  public String getFormalName() {
    return getDelegate().getFormalName();
  }

  @Override
  public MarkupLine getDescription() {
    return getDelegate().getDescription();
  }

  @Override
  public ModuleScopeEnum getModuleScope() {
    return ModuleScopeEnum.LOCAL;
  }

  @Override
  public boolean isGlobal() {
    return false;
  }
}
