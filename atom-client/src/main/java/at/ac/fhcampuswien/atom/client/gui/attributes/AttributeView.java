/* ATOM - Advanced Transparent Object Manager
 * Copyright © Vienna, Austria 2014 by FH Campus Wien 
 * Some rights reserved. See COPYING, AUTHORS.
 */
package at.ac.fhcampuswien.atom.client.gui.attributes;

import java.util.HashSet;
import java.util.logging.Level;

import com.google.gwt.event.dom.client.KeyUpEvent;
import com.google.gwt.event.dom.client.KeyUpHandler;
import com.google.gwt.event.logical.shared.ResizeEvent;
import com.google.gwt.event.logical.shared.ValueChangeEvent;
import com.google.gwt.event.logical.shared.ValueChangeHandler;
import com.google.gwt.user.client.ui.Composite;
import com.google.gwt.user.client.ui.Widget;

import at.ac.fhcampuswien.atom.client.gui.AtomClientBundle;
import at.ac.fhcampuswien.atom.client.gui.frames.DomainObjectDetailFrame;
import at.ac.fhcampuswien.atom.shared.AtomTools;
import at.ac.fhcampuswien.atom.shared.DomainClass;
import at.ac.fhcampuswien.atom.shared.DomainClassAttribute;
import at.ac.fhcampuswien.atom.shared.Notifiable;
import at.ac.fhcampuswien.atom.shared.annotations.ListBoxDefinition;
import at.ac.fhcampuswien.atom.shared.exceptions.ValidationError;

/**
 * 
 * @author kaefert
 *
 * @param <D> value object (with original type as within the DomainObject definition)  
 * @param <E> view widget
 * @param <F> value as typed within view
 */
public abstract class AttributeView<D extends Object, E extends Widget, F extends Object> extends Composite {

	@SuppressWarnings("rawtypes")
	public static AttributeView<?, ?, ?> getAttributeView(DomainClassAttribute attribute, DomainObjectDetailFrame forFrame) {
		String type = attribute.getType();
		String attributeName = attribute.getName();
		
		AtomTools.log(Level.FINER, "generating attributeView for type:" + type, null);

		AttributeView<?, ?, ?> returnValue = null;

		String[] listBoxKeys = attribute.getListBoxKeys();
		String listBoxSql = attribute.getListBoxSql();
		String listBoxMSSeperator = attribute.getListBoxMSSeperator();
		boolean listBoxAllowOtherValues = attribute.getListBoxAllowOtherValues();
		boolean listBoxAnyExistingValue = attribute.getListBoxAnyExistingValue();
		ListBoxDefinition.ViewType listBoxViewType = attribute.getListBoxViewType();
		
		if(listBoxAnyExistingValue || (listBoxSql != null && !listBoxSql.equals(""))) {
			if ("java.util.List<at.ac.fhcampuswien.atom.shared.domain.PersistentString>".equals(type)
					|| "java.util.Set<at.ac.fhcampuswien.atom.shared.domain.PersistentString>".equals(type))
				returnValue = new ListOfPersistentStringsView(type, forFrame.getRepresentedClass(), attributeName);
			// this is a listbox with options to load from sql
			else if(listBoxViewType == ListBoxDefinition.ViewType.DropDown)
				returnValue = new ListBoxView(forFrame.getRepresentedClass(), attributeName, listBoxMSSeperator);
			else if(listBoxViewType == ListBoxDefinition.ViewType.FilterAbleDropDown)
				returnValue = new SuggestBoxView(forFrame.getRepresentedClass(), attributeName, listBoxAllowOtherValues);
			else if(listBoxViewType == ListBoxDefinition.ViewType.RadioButtons || listBoxViewType == ListBoxDefinition.ViewType.RadioTable)
				returnValue = new RadioButtonsView(forFrame.getRepresentedClass(), attributeName, listBoxViewType, listBoxMSSeperator);
			else
				AtomTools.log(Level.SEVERE, "ListBox Definition with unexpected ViewType encountered! this is a fatal error and requires code adaption (either Domain definition or AttributeView generation)", attribute);
		}
		else if(listBoxKeys != null) {
			if ("java.util.List<at.ac.fhcampuswien.atom.shared.domain.PersistentString>".equals(type)
					|| "java.util.Set<at.ac.fhcampuswien.atom.shared.domain.PersistentString>".equals(type))
				returnValue = new ListOfPersistentStringsView(type, attribute.getListBoxMapped());
			// this is a listbox with static options
			if(listBoxViewType == ListBoxDefinition.ViewType.DropDown)
				returnValue = new ListBoxView(attribute.getListBoxMapped(), listBoxMSSeperator);
			else if(listBoxViewType == ListBoxDefinition.ViewType.FilterAbleDropDown)
				returnValue = new SuggestBoxView(attribute.getListBoxMapped(), listBoxAllowOtherValues);
			else
				returnValue = new RadioButtonsView(forFrame.getRepresentedClass().getName() + "." + attributeName, attribute.getListBoxMapped(), listBoxViewType, listBoxMSSeperator);
//		}
//
//		if (listBoxNamed != null) {
//			String viewType = attribute.getListBoxViewType();
//			if (AtomConfig.listBoxSql.equals(listBoxNamed)) {
//				if(AtomConfig.listBoxViewTypeDropDown.equals(viewType))
//					returnValue = new ListBoxView(AtomConfig.listBoxSql, forFrame.getRepresentedClass(), attributeName);
//				else if(AtomConfig.listBoxViewTypeRadioButtons.equals(viewType) || AtomConfig.listBoxViewTypeRadioTable.equals(viewType)) 
//					returnValue = new RadioButtonsView(AtomConfig.listBoxSql, forFrame.getRepresentedClass(), attributeName, viewType);
//				else
//					AtomTools.log(Level.SEVERE, "Unknown ListBoxViewType: " + viewType, null); 
//					
//			} else
//				if(AtomConfig.listBoxViewTypeDropDown.equals(viewType))
//					returnValue = new ListBoxView(listBoxNamed, attribute.getListBoxKeys(), attribute.getListBoxDisplay());
//				else if(AtomConfig.listBoxViewTypeRadioButtons.equals(viewType) || AtomConfig.listBoxViewTypeRadioTable.equals(viewType)) 
//					returnValue = new RadioButtonsView(listBoxNamed, attribute.getListBoxKeys(), attribute.getListBoxDisplay(), viewType);
//				else
//					AtomTools.log(Level.SEVERE, "Unknown ListBoxViewType: " + viewType, null); 
				
		} else if (type.startsWith("at.ac.fhcampuswien.atom.shared.domain")) {
			returnValue = new DomainObjectView(type, attributeName, forFrame);
		} else if ("java.lang.String".equals(type)) {
			if (attribute.getAnnotation("FileAttribute") != null) {
				returnValue = new FileView(forFrame.getRepresentedClass().getName(), attributeName, forFrame.getRepresentedObject().getObjectID());
			}
			else if (attribute.getAnnotation("StringFormattedLob") != null)
				returnValue = new StringFormattedLobView();
			else if (attribute.getAnnotation("javax.persistence.Lob") != null || 
					 attribute.getAnnotation("StringMultiLine") != null)
				returnValue = new StringLobView();
			else if (attribute.getAnnotation("LinkAttribute") != null)
				returnValue = new LinkView(attribute.getLinkPrefix(), attribute.getLinkSuffix());
			else
				returnValue = new StringView();
		} else if ("java.util.Date".equals(type)) {
			returnValue = new DateView(attribute.getDisplayName());
		} else if ("java.lang.Long".equals(type)) {
			returnValue = new LongView();
		} else if ("java.lang.Integer".equals(type)) {
			returnValue = new IntegerView();
		} else if ("java.lang.Double".equals(type)) {
			if(attribute.useSlider()) {
				returnValue = new SliderView(attribute.getSliderMinValue(), attribute.getSliderMaxValue(), attribute.getSliderDefaultValue(), attribute.getSliderStepSize(), attribute.getSliderRoundTo());
			}
			else
				returnValue = new DoubleView();
		} else if ("java.lang.Boolean".equals(type)) {
			returnValue = new BooleanView(attributeName, attribute.getBooleanMeanings());
		} else if ("java.util.List<at.ac.fhcampuswien.atom.shared.domain.PersistentString>".equals(type)
				|| "java.util.Set<at.ac.fhcampuswien.atom.shared.domain.PersistentString>".equals(type)) {
			returnValue = new ListOfPersistentStringsView(type);
//		} else if ("java.util.Set<at.ac.fhcampuswien.atom.shared.domain.PermittedOE>".equals(type)) {
//			returnValue = new SetOfPermittedOEsView(attributeName, forFrame);
//		} else if ("java.util.Set<at.ac.fhcampuswien.atom.shared.domain.PermittedRole>".equals(type)) {
//			returnValue = new SetOfPermittedRolesView(attributeName, forFrame);
		} else if (type.startsWith("java.util.List<at.ac.fhcampuswien.atom.shared.domain.")
				|| type.startsWith("java.util.Set<at.ac.fhcampuswien.atom.shared.domain.")) {
			returnValue = new ListOfDomainObjectsView(type, attributeName, forFrame);
			// } else if ("java.util.ArrayList<java.lang.String>".equals(type))
			// {
			// return new ArrayListOfStringsView();
		} else if (type.startsWith("java.util.Set") || type.startsWith("java.util.List") || type.startsWith("java.util.ArrayList")) {
			returnValue = new CollectionView();
		} else {
			AtomTools.log(Level.WARNING, "no special attributeView implementation for type '" + type + "', will use StringView for attribute '" + attributeName
					+ "'", null);
			returnValue = new StringView();
		}

		// read money email sozialversicherungsnummer oder ähnliches annotation und übergib info an erzeugte AttributeView
		returnValue.validators = attribute.getValidators();

		return returnValue;
	}

	protected D value;
	protected E field;
	protected boolean readOnly;
	protected String[] validators;

	private void markGood() {
		field.removeStyleName(AtomClientBundle.INSTANCE.css().insensitiveAttributeView());
		field.removeStyleName(AtomClientBundle.INSTANCE.css().attributeViewValidationError());
		field.addStyleName(AtomClientBundle.INSTANCE.css().sensitiveAttributeView());
	}
	
	private void markError() {
		field.removeStyleName(AtomClientBundle.INSTANCE.css().insensitiveAttributeView());
		field.removeStyleName(AtomClientBundle.INSTANCE.css().sensitiveAttributeView());
		field.addStyleName(AtomClientBundle.INSTANCE.css().attributeViewValidationError());
	}
	
	public ValidationError validateAndMark(String attributeName) {
		try {
			readValue();
			AtomTools.validateAttribute(attributeName, value, validators);
			markGood();
			return null;
		} catch (ValidationError e) {
			markError();
			return e;
		}
	}

	public boolean isReadOnly() {
		return readOnly;
	}

	public D getValue() {
		this.readValue();
		return this.value;
	}

	@SuppressWarnings("unchecked")
	public void setValue(Object value) {
		
		if((this.value != null && !this.value.equals(value)) || this.value != value) {
			this.value = (D) value;
			this.showValue();
			
			if(changeHandlers != null) {
				for(Notifiable<Object> c : changeHandlers) {
					c.doNotify(value);
				}
			}
		}
	}

	protected AttributeView() {
		initAttributeView(null);
	}

	public AttributeView(D initialValue) {
		initAttributeView(initialValue);
	}

	protected void initAttributeView(D initialValue) {
		value = initialValue;
		if (createFieldWidget()) {
			this.initWidget(field);
			// this.setStylePrimaryName("AttributeView");
			showValue();
		}
		// this.add(field);
	}

	public void setReadOnly(boolean readOnly) {
		this.readOnly = readOnly;
//		field.getElement().getStyle().clearBackgroundColor();
		
		if (readOnly) {
			field.removeStyleName(AtomClientBundle.INSTANCE.css().attributeViewValidationError());
			field.removeStyleName(AtomClientBundle.INSTANCE.css().sensitiveAttributeView());
			field.addStyleName(AtomClientBundle.INSTANCE.css().insensitiveAttributeView());
		} else {
			field.removeStyleName(AtomClientBundle.INSTANCE.css().insensitiveAttributeView());
			field.addStyleName(AtomClientBundle.INSTANCE.css().sensitiveAttributeView());
		}
	}

	protected abstract boolean createFieldWidget();

	protected abstract void showValue();

	protected abstract void readValue();

	public void goingInvisible() {
	}

	public void goingVisible() {
	}

	protected class MyKeyUpHandler implements KeyUpHandler {

		public void onKeyUp(KeyUpEvent event) {
			if(!isReadOnly()) {
				validateAndMark("irrelevant");
			}
		}
	}

	private Integer nullReason;

	public void setNullReason(Integer integer) {
		nullReason = integer;
	}

	protected Integer getNullReason() {
		return nullReason;
	}

	public void resize(ResizeEvent event) {
	}

	public void suggestDND(DomainClass dc) {
	}
	
	
	protected HashSet<Notifiable<Object>> changeHandlers = null;
	
	public void addChangeHandler(Notifiable<Object> changeHandler) {
		if(changeHandlers == null)
			changeHandlers = new HashSet<Notifiable<Object>>();
		
		changeHandlers.add(changeHandler);
	}
	
	public void removeChangeHandler(Notifiable<Object> changeHandler) {
		if(changeHandlers != null) {
			changeHandlers.remove(changeHandler);
		}
	}
	
	private ValueChangeHandler<F> vcHandler = null;
	protected ValueChangeHandler<F> getVCHandler() {
		if(vcHandler == null) {
			vcHandler = new ValueChangeHandler<F>() {
				
				@Override
				public void onValueChange(ValueChangeEvent<F> event) {
					AtomTools.log(Level.FINER, "AttributeView value changed", this);
					if(changeHandlers != null) {
						readValue();
						for(Notifiable<Object> ch : changeHandlers) {
							ch.doNotify(value);
						}
					}
				}
			};
		}
		return vcHandler;
	}
	
}
