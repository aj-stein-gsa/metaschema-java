/*
 * SPDX-FileCopyrightText: none
 * SPDX-License-Identifier: CC0-1.0
 */

package gov.nist.secauto.metaschema.schemagen.xml.impl.schematype;

import gov.nist.secauto.metaschema.core.datatype.markup.MarkupDataTypeProvider;
import gov.nist.secauto.metaschema.core.model.IAssemblyDefinition;
import gov.nist.secauto.metaschema.core.model.IChoiceGroupInstance;
import gov.nist.secauto.metaschema.core.model.IChoiceInstance;
import gov.nist.secauto.metaschema.core.model.IFieldInstanceAbsolute;
import gov.nist.secauto.metaschema.core.model.IFlagInstance;
import gov.nist.secauto.metaschema.core.model.IModelInstanceAbsolute;
import gov.nist.secauto.metaschema.core.model.INamedModelInstanceAbsolute;
import gov.nist.secauto.metaschema.core.model.INamedModelInstanceGrouped;
import gov.nist.secauto.metaschema.core.model.XmlGroupAsBehavior;
import gov.nist.secauto.metaschema.core.qname.IEnhancedQName;
import gov.nist.secauto.metaschema.core.util.ObjectUtils;
import gov.nist.secauto.metaschema.schemagen.SchemaGenerationException;
import gov.nist.secauto.metaschema.schemagen.xml.XmlSchemaGenerator;
import gov.nist.secauto.metaschema.schemagen.xml.impl.DocumentationGenerator;
import gov.nist.secauto.metaschema.schemagen.xml.impl.XmlGenerationState;

import java.util.Collection;

import javax.xml.namespace.QName;
import javax.xml.stream.XMLStreamException;

import edu.umd.cs.findbugs.annotations.NonNull;

public class XmlComplexTypeAssemblyDefinition
    extends AbstractXmlComplexType<IAssemblyDefinition> {

  public XmlComplexTypeAssemblyDefinition(
      @NonNull QName qname,
      @NonNull IAssemblyDefinition definition) {
    super(qname, definition);
  }

  @Override
  protected void generateTypeBody(XmlGenerationState state) throws XMLStreamException {
    IAssemblyDefinition definition = getDefinition();

    Collection<? extends IModelInstanceAbsolute> modelInstances = definition.getModelInstances();
    if (!modelInstances.isEmpty()) {
      state.writeStartElement(XmlSchemaGenerator.PREFIX_XML_SCHEMA, "sequence", XmlSchemaGenerator.NS_XML_SCHEMA);
      for (IModelInstanceAbsolute modelInstance : modelInstances) {
        assert modelInstance != null;
        generateModelInstance(modelInstance, state);
      }
      state.writeEndElement();
    }

    Collection<? extends IFlagInstance> flagInstances = definition.getFlagInstances();
    if (!flagInstances.isEmpty()) {
      for (IFlagInstance flagInstance : flagInstances) {
        assert flagInstance != null;
        generateFlagInstance(flagInstance, state);
      }
    }
  }

  protected void generateModelInstance( // NOPMD acceptable complexity
      @NonNull IModelInstanceAbsolute modelInstance,
      @NonNull XmlGenerationState state)
      throws XMLStreamException {

    boolean grouped = false;
    if (XmlGroupAsBehavior.GROUPED.equals(modelInstance.getXmlGroupAsBehavior())) {
      // handle grouping
      state.writeStartElement(XmlSchemaGenerator.PREFIX_XML_SCHEMA, "element", XmlSchemaGenerator.NS_XML_SCHEMA);

      IEnhancedQName groupAsQName = ObjectUtils.requireNonNull(modelInstance.getEffectiveXmlGroupAsQName());

      if (!state.getDefaultNS().equals(groupAsQName.getNamespace())) {
        throw new SchemaGenerationException(
            String.format("Attempt to create element '%s' on definition '%s' with different namespace", groupAsQName,
                getDefinition().toCoordinates()));
      }
      state.writeAttribute("name", ObjectUtils.requireNonNull(groupAsQName.getLocalName()));

      if (modelInstance.getMinOccurs() == 0) {
        // this is an optional instance group
        state.writeAttribute("minOccurs", "0");
      }

      // now generate the child elements of the group
      state.writeStartElement(XmlSchemaGenerator.PREFIX_XML_SCHEMA, "complexType", XmlSchemaGenerator.NS_XML_SCHEMA);
      state.writeStartElement(XmlSchemaGenerator.PREFIX_XML_SCHEMA, "sequence", XmlSchemaGenerator.NS_XML_SCHEMA);

      // mark that we need to close these elements
      grouped = true;
    }

    switch (modelInstance.getModelType()) {
    case ASSEMBLY:
      generateNamedModelInstance((INamedModelInstanceAbsolute) modelInstance, grouped, state);
      break;
    case FIELD: {
      IFieldInstanceAbsolute fieldInstance = (IFieldInstanceAbsolute) modelInstance;
      if (fieldInstance.isEffectiveValueWrappedInXml()) {
        generateNamedModelInstance(fieldInstance, grouped, state);
      } else {
        generateUnwrappedFieldInstance(fieldInstance, grouped, state);
      }
      break;
    }
    case CHOICE:
      generateChoiceModelInstance((IChoiceInstance) modelInstance, state);
      break;
    case CHOICE_GROUP:
      generateChoiceGroupInstance((IChoiceGroupInstance) modelInstance, state);
      break;
    default:
      throw new UnsupportedOperationException(modelInstance.getModelType().toString());
    }

    if (grouped) {
      state.writeEndElement(); // xs:sequence
      state.writeEndElement(); // xs:complexType
      state.writeEndElement(); // xs:element
    }
  }

  protected void generateNamedModelInstance(
      @NonNull INamedModelInstanceAbsolute modelInstance,
      boolean grouped,
      @NonNull XmlGenerationState state) throws XMLStreamException {
    state.writeStartElement(XmlSchemaGenerator.PREFIX_XML_SCHEMA, "element", XmlSchemaGenerator.NS_XML_SCHEMA);

    state.writeAttribute("name", modelInstance.getEffectiveName());

    // state.generateElementNameOrRef(modelInstance);

    if (!grouped && modelInstance.getMinOccurs() != 1) {
      state.writeAttribute("minOccurs", ObjectUtils.notNull(Integer.toString(modelInstance.getMinOccurs())));
    }

    if (modelInstance.getMaxOccurs() != 1) {
      state.writeAttribute("maxOccurs",
          modelInstance.getMaxOccurs() == -1 ? "unbounded"
              : ObjectUtils.notNull(Integer.toString(modelInstance.getMaxOccurs())));
    }

    IXmlType type = state.getXmlForDefinition(modelInstance.getDefinition());
    if (type.isGeneratedType(state) && type.isInline(state)) {
      DocumentationGenerator.generateDocumentation(modelInstance, state);
      type.generate(state);
    } else {
      state.writeAttribute("type", type.getTypeReference());
      DocumentationGenerator.generateDocumentation(modelInstance, state);
    }
    state.writeEndElement(); // xs:element
  }

  protected static void generateUnwrappedFieldInstance(
      @NonNull IFieldInstanceAbsolute fieldInstance,
      boolean grouped,
      @NonNull XmlGenerationState state) throws XMLStreamException {

    if (!MarkupDataTypeProvider.MARKUP_MULTILINE.equals(fieldInstance.getDefinition().getJavaTypeAdapter())) {
      throw new IllegalStateException();
    }

    state.writeStartElement(XmlSchemaGenerator.PREFIX_XML_SCHEMA, "group", XmlSchemaGenerator.NS_XML_SCHEMA);

    state.writeAttribute("ref", "blockElementGroup");

    // minOccurs=1 is the schema default
    if (!grouped && fieldInstance.getMinOccurs() != 1) {
      state.writeAttribute("minOccurs", ObjectUtils.notNull(Integer.toString(fieldInstance.getMinOccurs())));
    }

    // if (fieldInstance.getMaxOccurs() != 1) {
    // state.writeAttribute("maxOccurs",
    // fieldInstance.getMaxOccurs() == -1 ? "unbounded"
    // : ObjectUtils.notNull(Integer.toString(fieldInstance.getMaxOccurs())));
    // }

    // unwrapped fields always have a max-occurance of 1. Since the markup multiline
    // is unbounded, this
    // value is unbounded.
    state.writeAttribute("maxOccurs", "unbounded");

    DocumentationGenerator.generateDocumentation(fieldInstance, state);

    state.writeEndElement(); // xs:group
  }

  protected void generateChoiceModelInstance(
      @NonNull IChoiceInstance choice,
      @NonNull XmlGenerationState state) throws XMLStreamException {
    state.writeStartElement(XmlSchemaGenerator.PREFIX_XML_SCHEMA, "choice", XmlSchemaGenerator.NS_XML_SCHEMA);

    for (IModelInstanceAbsolute instance : choice.getModelInstances()) {
      assert instance != null;

      if (instance instanceof IChoiceInstance) {
        generateChoiceModelInstance((IChoiceInstance) instance, state);
      } else {
        generateModelInstance(instance, state);
      }
    }

    state.writeEndElement(); // xs:choice
  }

  private void generateChoiceGroupInstance(IChoiceGroupInstance choiceGroup, XmlGenerationState state)
      throws XMLStreamException {
    state.writeStartElement(XmlSchemaGenerator.PREFIX_XML_SCHEMA, "choice", XmlSchemaGenerator.NS_XML_SCHEMA);

    int min = choiceGroup.getMinOccurs();
    if (min != 1) {
      state.writeAttribute("minOccurs", ObjectUtils.notNull(Integer.toString(min)));
    }

    int max = choiceGroup.getMaxOccurs();
    if (max < 0) {
      state.writeAttribute("maxOccurs", "unbounded");
    } else if (max > 1) {
      state.writeAttribute("maxOccurs", ObjectUtils.notNull(Integer.toString(max)));
    }

    for (INamedModelInstanceGrouped instance : choiceGroup.getNamedModelInstances()) {
      assert instance != null;

      generateGroupedNamedModelInstance(instance, state);
    }

    state.writeEndElement(); // xs:choice
  }

  protected void generateGroupedNamedModelInstance(
      @NonNull INamedModelInstanceGrouped instance,
      @NonNull XmlGenerationState state) throws XMLStreamException {
    state.writeStartElement(XmlSchemaGenerator.PREFIX_XML_SCHEMA, "element", XmlSchemaGenerator.NS_XML_SCHEMA);

    state.writeAttribute("name", instance.getEffectiveName());

    // state.generateElementNameOrRef(modelInstance);

    IXmlType type = state.getXmlForDefinition(instance.getDefinition());
    if (type.isGeneratedType(state) && type.isInline(state)) {
      DocumentationGenerator.generateDocumentation(instance, state);
      type.generate(state);
    } else {
      state.writeAttribute("type", type.getTypeReference());
      DocumentationGenerator.generateDocumentation(instance, state);
    }
    state.writeEndElement(); // xs:element
  }
}
