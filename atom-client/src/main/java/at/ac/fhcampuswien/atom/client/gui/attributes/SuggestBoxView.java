/* ATOM - Advanced Transparent Object Manager
 * Copyright © Vienna, Austria 2014 by FH Campus Wien 
 * Some rights reserved. See COPYING, AUTHORS.
 */
package at.ac.fhcampuswien.atom.client.gui.attributes;

import java.util.LinkedHashMap;
import java.util.Map;
import java.util.logging.Level;

import com.google.gwt.core.shared.GWT;
import com.google.gwt.dom.client.Style.Unit;
import com.google.gwt.event.dom.client.ChangeEvent;
import com.google.gwt.event.dom.client.ChangeHandler;
import com.google.gwt.resources.client.CssResource;
import com.google.gwt.uibinder.client.UiBinder;
import com.google.gwt.uibinder.client.UiField;
import com.google.gwt.user.client.ui.SuggestBox;
import com.google.gwt.user.client.ui.Widget;

import at.ac.fhcampuswien.atom.client.rpc.RPCCaller;
import at.ac.fhcampuswien.atom.client.rpc.WaitingFor;
import at.ac.fhcampuswien.atom.shared.AtomTools;
import at.ac.fhcampuswien.atom.shared.DomainClass;
import at.ac.fhcampuswien.atom.shared.Notifiable;

public class SuggestBoxView extends AttributeView<String, SuggestBoxView, String> {

	private static SuggestBoxViewUiBinder uiBinder = GWT
	.create(SuggestBoxViewUiBinder.class);

	interface SuggestBoxViewUiBinder extends
		UiBinder<Widget, SuggestBoxView> {
	}
	
	interface PanelStyle extends CssResource {
		String suggestBox();
	}

	@UiField
	protected PanelStyle panelStyle;
	
//	protected SimplePanel simplePanel;

	@UiField
	protected SuggestBox suggestBox;
	
	private LinkedHashMap<String, String> keyDisplayMap = null;
	private String multiSelectSeperator = null;
	private boolean allowOtherValues = false;
	
	public SuggestBoxView(LinkedHashMap<String, String> keyDisplayMap, String multiSelectSeperator, boolean allowOtherValues) {
		super();
		this.keyDisplayMap = keyDisplayMap;
		this.multiSelectSeperator = multiSelectSeperator;
		this.allowOtherValues = allowOtherValues;
		
		prepareSuggestBox();
	}
	
	public SuggestBoxView(DomainClass representedClass, String attributeName, String multiSelectSeperator, boolean allowOtherValues) {
		this.multiSelectSeperator = multiSelectSeperator;
		this.allowOtherValues = allowOtherValues;
		
		RPCCaller.getSinglton().loadListBoxChoices(representedClass, attributeName, new WaitingFor<LinkedHashMap<String,String>>() {
			
			@Override
			public void requestFailed(String reason) {
				AtomTools.log(Level.SEVERE, "getListBoxChoices failed -> " + reason, this);
			}
			
			@Override
			public void recieve(LinkedHashMap<String,String> result) {
				SuggestBoxView.this.keyDisplayMap = result;
				prepareSuggestBox();
			}
		});
		
		
		if(keyDisplayMap == null) {
			keyDisplayMap = new LinkedHashMap<String, String>(1);
			keyDisplayMap.put(null, "loading, please wait..");
			prepareSuggestBox();
		}
	}
	
	private void prepareSuggestBox() {
		// TODO FIXME implement!
	}
	
	@Override
	protected boolean createFieldWidget() {
		field = this;		
		
		initWidget(uiBinder.createAndBindUi(this));
		return false;
	}
	
	/**
	 * @see at.ac.fhcampuswien.atom.client.gui.attributes.AttributeView#showValue()
	 */
	@Override
	protected void showValue() {
		suggestBox.setValue(this.value);
	}

	@Override
	public void setReadOnly(boolean readOnly) {
		super.setReadOnly(readOnly);
		this.readOnly = readOnly;
		suggestBox.setEnabled(!readOnly);
	}

	@Override
	protected void readValue() {
		this.value = suggestBox.getValue();
	}

	@Override
	public void setValue(Object value) {
		if(value != null) {
			this.value = value.toString();
		}
		else {
			value = null;
		}
		prepareSuggestBox();
	}

}
