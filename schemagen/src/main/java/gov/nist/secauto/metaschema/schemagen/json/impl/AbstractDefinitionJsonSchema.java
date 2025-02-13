/*
 * SPDX-FileCopyrightText: none
 * SPDX-License-Identifier: CC0-1.0
 */

package gov.nist.secauto.metaschema.schemagen.json.impl;

import com.fasterxml.jackson.databind.node.ObjectNode;

import gov.nist.secauto.metaschema.core.model.IDefinition;
import gov.nist.secauto.metaschema.schemagen.SchemaGenerationException;
import gov.nist.secauto.metaschema.schemagen.json.IDefinitionJsonSchema;
import gov.nist.secauto.metaschema.schemagen.json.IJsonGenerationState;

import java.io.IOException;
import java.util.Map;
import java.util.Objects;

import edu.umd.cs.findbugs.annotations.NonNull;

public abstract class AbstractDefinitionJsonSchema<D extends IDefinition>
    extends AbstractDefineableJsonSchema
    implements IDefinitionJsonSchema<D> {
  @NonNull
  private final D definition;

  @Override
  public D getDefinition() {
    return definition;
  }

  protected AbstractDefinitionJsonSchema(
      @NonNull D definition) {
    this.definition = definition;
  }

  @Override
  public boolean isInline(IJsonGenerationState state) {
    return state.isInline(getDefinition());
  }

  protected abstract void generateBody(
      @NonNull IJsonGenerationState state,
      @NonNull ObjectNode obj) throws IOException;

  @Override
  public void generateInlineSchema(ObjectNode obj, IJsonGenerationState state) {
    D definition = getDefinition();

    try {
      generateTitle(definition, obj);
      generateDescription(definition, obj);

      generateBody(state, obj);
    } catch (IOException ex) {
      throw new SchemaGenerationException(ex);
    }
  }

  public static void generateTitle(@NonNull IDefinition definition, @NonNull ObjectNode obj) {
    MetadataUtils.generateTitle(definition, obj);
  }

  public static void generateDescription(@NonNull IDefinition definition, @NonNull ObjectNode obj) {
    MetadataUtils.generateDescription(definition, obj);
  }

  @Override
  public void gatherDefinitions(
      @NonNull Map<IKey, IDefinitionJsonSchema<?>> gatheredDefinitions,
      @NonNull IJsonGenerationState state) {
    gatheredDefinitions.put(getKey(), this);
  }

  public static class SimpleKey implements IKey {
    @NonNull
    private final IDefinition definition;

    public SimpleKey(@NonNull IDefinition definition) {
      this.definition = definition;
    }

    @Override
    public IDefinition getDefinition() {
      return definition;
    }

    @Override
    public String getJsonKeyFlagName() {
      return null;
    }

    @Override
    public String getDiscriminatorProperty() {
      return null;
    }

    @Override
    public String getDiscriminatorValue() {
      return null;
    }

    @Override
    public int hashCode() {
      return Objects.hash(definition, null, null, null);
    }

    @SuppressWarnings("PMD.OnlyOneReturn")
    @Override
    public boolean equals(Object obj) {
      if (this == obj) {
        return true;
      }
      if (!(obj instanceof IKey)) {
        return false;
      }
      IKey other = (IKey) obj;
      return Objects.equals(definition, other.getDefinition())
          && Objects.equals(null, other.getJsonKeyFlagName())
          && Objects.equals(null, other.getDiscriminatorProperty())
          && Objects.equals(null, other.getDiscriminatorValue());
    }
  }

}
