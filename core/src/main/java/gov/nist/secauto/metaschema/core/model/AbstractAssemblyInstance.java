/*
 * SPDX-FileCopyrightText: none
 * SPDX-License-Identifier: CC0-1.0
 */

package gov.nist.secauto.metaschema.core.model;

import gov.nist.secauto.metaschema.core.util.ObjectUtils;

import edu.umd.cs.findbugs.annotations.NonNull;

/**
 * A base class for an assembly that is a member of a containing model.
 *
 * @param <PARENT>
 *          the Java type of the parent model container for this instance
 * @param <DEFINITION>
 *          the Java type of the related assembly definition
 * @param <INSTANCE>
 *          the Java type of the implementing instance type
 * @param <PARENT_DEFINITION>
 *          the Java type of the containing assembly definition
 */
public abstract class AbstractAssemblyInstance<
    PARENT extends IContainerModel,
    DEFINITION extends IAssemblyDefinition,
    INSTANCE extends IAssemblyInstance,
    PARENT_DEFINITION extends IAssemblyDefinition>
    extends AbstractNamedModelInstance<PARENT, PARENT_DEFINITION>
    implements IAssemblyInstance, IFeatureDefinitionReferenceInstance<DEFINITION, INSTANCE> {

  /**
   * Construct a new assembly instance that is contained with the provided parent
   * container.
   *
   * @param parent
   *          the parent container for this instance
   */
  protected AbstractAssemblyInstance(@NonNull PARENT parent) {
    super(parent);
  }

  @Override
  public DEFINITION getDefinition() {
    // this should always be not null
    return ObjectUtils.asType(ObjectUtils.requireNonNull(
        getContainingModule()
            .getScopedAssemblyDefinitionByName(getReferencedDefinitionQName())));
  }

  /**
   * Generates a "coordinate" string for the assembly instance.
   *
   * @return the coordinate
   */
  @SuppressWarnings("null")
  @Override
  public String toCoordinates() {
    IDefinition definition = getDefinition();
    return String.format("assembly instance %s -> %s in module %s (@%d(%d)",
        getXmlQName(),
        definition.getDefinitionQName(),
        getContainingDefinition().getContainingModule().getShortName(),
        hashCode(),
        definition.hashCode());
  }
}
