/*
 * SPDX-FileCopyrightText: none
 * SPDX-License-Identifier: CC0-1.0
 */

package gov.nist.secauto.metaschema.schemagen;

import gov.nist.secauto.metaschema.core.model.IAssemblyDefinition;
import gov.nist.secauto.metaschema.core.model.IAssemblyInstance;
import gov.nist.secauto.metaschema.core.model.IChoiceInstance;
import gov.nist.secauto.metaschema.core.model.IDefinition;
import gov.nist.secauto.metaschema.core.model.IFieldDefinition;
import gov.nist.secauto.metaschema.core.model.IFieldInstance;
import gov.nist.secauto.metaschema.core.model.IFlagDefinition;
import gov.nist.secauto.metaschema.core.model.IFlagInstance;
import gov.nist.secauto.metaschema.core.model.IModule;
import gov.nist.secauto.metaschema.core.model.INamedInstance;
import gov.nist.secauto.metaschema.core.model.INamedModelInstance;
import gov.nist.secauto.metaschema.core.model.INamedModelInstanceGrouped;
import gov.nist.secauto.metaschema.core.model.ModelWalker;
import gov.nist.secauto.metaschema.core.util.ObjectUtils;

import java.util.Collection;
import java.util.HashSet;
import java.util.LinkedHashMap;
import java.util.Map;
import java.util.Set;
import java.util.concurrent.atomic.AtomicBoolean;

import edu.umd.cs.findbugs.annotations.NonNull;

public class ModuleIndex {
  private final Map<IDefinition, DefinitionEntry> index = new LinkedHashMap<>();// new ConcurrentHashMap<>();

  @NonNull
  public static ModuleIndex indexDefinitions(@NonNull IModule module, @NonNull IInlineStrategy inlineStrategy) {
    Collection<? extends IAssemblyDefinition> definitions = module.getExportedRootAssemblyDefinitions();
    ModuleIndex index = new ModuleIndex();
    if (!definitions.isEmpty()) {
      IndexVisitor visitor = new IndexVisitor(index, inlineStrategy);
      for (IAssemblyDefinition definition : definitions) {
        assert definition != null;

        // // add the root definition to the index
        // index.getEntry(definition).incrementReferenceCount();

        // walk the definition
        visitor.walk(ObjectUtils.requireNonNull(definition));
      }
    }
    return index;
  }

  public boolean hasEntry(@NonNull IDefinition definition) {
    return index.containsKey(definition);
  }

  @NonNull
  public DefinitionEntry getEntry(@NonNull IDefinition definition) {
    return ObjectUtils.notNull(index.computeIfAbsent(
        definition,
        k -> new ModuleIndex.DefinitionEntry(ObjectUtils.notNull(k))));
  }

  @NonNull
  public Collection<DefinitionEntry> getDefinitions() {
    return ObjectUtils.notNull(index.values());
  }

  private static class IndexVisitor
      extends ModelWalker<ModuleIndex> {
    @NonNull
    private final IInlineStrategy inlineStrategy;
    @NonNull
    private final ModuleIndex index;

    public IndexVisitor(@NonNull ModuleIndex index, @NonNull IInlineStrategy inlineStrategy) {
      this.index = index;
      this.inlineStrategy = inlineStrategy;
    }

    @Override
    protected ModuleIndex getDefaultData() {
      return index;
    }

    @Override
    protected boolean visit(IFlagInstance instance, ModuleIndex index) {
      handleInstance(instance);
      return true;
    }

    @Override
    protected boolean visit(IFieldInstance instance, ModuleIndex index) {
      handleInstance(instance);
      return true;
    }

    @Override
    protected boolean visit(IAssemblyInstance instance, ModuleIndex index) {
      handleInstance(instance);
      return true;
    }

    @Override
    protected void visit(IFlagDefinition def, ModuleIndex data) {
      handleDefinition(def);
    }

    // @Override
    // protected boolean visit(IAssemblyDefinition def, ModuleIndex data) {
    // // only walk if the definition hasn't already been visited
    // return !index.hasEntry(def);
    // }

    @Override
    protected boolean visit(IFieldDefinition def, ModuleIndex data) {
      return handleDefinition(def);
    }

    @Override
    protected boolean visit(IAssemblyDefinition def, ModuleIndex data) {
      return handleDefinition(def);
    }

    private boolean handleDefinition(@NonNull IDefinition definition) {
      DefinitionEntry entry = getDefaultData().getEntry(definition);
      boolean visited = entry.isVisited();
      if (!visited) {
        entry.markVisited();

        if (inlineStrategy.isInline(definition, index)) {
          entry.markInline();
        }
      }
      return !visited;
    }

    /**
     * Updates the index entry for the definition associated with the reference.
     *
     * @param instance
     *          the instance to process
     */
    @NonNull
    private DefinitionEntry handleInstance(INamedInstance instance) {
      IDefinition definition = instance.getDefinition();
      // check if this will be a new entry, which needs to be called before getEntry,
      // which will create it
      DefinitionEntry entry = getDefaultData().getEntry(definition);
      entry.addReference(instance);

      if (isChoice(instance)) {
        entry.markUsedAsChoice();
      }

      if (isChoiceSibling(instance)) {
        entry.markAsChoiceSibling();
      }
      return entry;
    }

    private static boolean isChoice(@NonNull INamedInstance instance) {
      return instance.getParentContainer() instanceof IChoiceInstance;
    }

    private static boolean isChoiceSibling(@NonNull INamedInstance instance) {
      IDefinition containingDefinition = instance.getContainingDefinition();
      return containingDefinition instanceof IAssemblyDefinition
          && !((IAssemblyDefinition) containingDefinition).getChoiceInstances().isEmpty();
    }
  }

  public static class DefinitionEntry {
    @NonNull
    private final IDefinition definition;
    private final Set<INamedInstance> references = new HashSet<>();
    private final AtomicBoolean inline = new AtomicBoolean(); // false
    private final AtomicBoolean visited = new AtomicBoolean(); // false
    private final AtomicBoolean usedAsChoice = new AtomicBoolean(); // false
    private final AtomicBoolean choiceSibling = new AtomicBoolean(); // false

    public DefinitionEntry(@NonNull IDefinition definition) {
      this.definition = definition;
    }

    @NonNull
    public IDefinition getDefinition() {
      return definition;
    }

    public boolean isRoot() {
      return definition instanceof IAssemblyDefinition
          && ((IAssemblyDefinition) definition).isRoot();
    }

    public boolean isReferenced() {
      return !references.isEmpty()
          || isRoot();
    }

    public Set<INamedInstance> getReferences() {
      return references;
    }

    public boolean addReference(@NonNull INamedInstance reference) {
      return references.add(reference);
    }

    public void markVisited() {
      visited.compareAndSet(false, true);
    }

    public boolean isVisited() {
      return visited.get();
    }

    public void markInline() {
      inline.compareAndSet(false, true);
    }

    public boolean isInline() {
      return inline.get();
    }

    public void markUsedAsChoice() {
      usedAsChoice.compareAndSet(false, true);
    }

    public boolean isUsedAsChoice() {
      return usedAsChoice.get();
    }

    public void markAsChoiceSibling() {
      choiceSibling.compareAndSet(false, true);
    }

    public boolean isChoiceSibling() {
      return choiceSibling.get();
    }

    public boolean isUsedAsJsonKey() {
      return references.stream()
          .anyMatch(ref -> ref instanceof INamedModelInstance
              && ((INamedModelInstance) ref).hasJsonKey());
    }

    public boolean isUsedWithoutJsonKey() {
      return definition instanceof IFlagDefinition
          || references.isEmpty()
          || references.stream()
              .anyMatch(ref -> ref instanceof INamedModelInstance
                  && !((INamedModelInstance) ref).hasJsonKey());
    }

    public boolean isChoiceGroupMember() {
      return references.stream()
          .anyMatch(INamedModelInstanceGrouped.class::isInstance);
    }
  }
}
