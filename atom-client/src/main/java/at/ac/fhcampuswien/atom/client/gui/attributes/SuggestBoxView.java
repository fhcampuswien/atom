/* ATOM - Advanced Transparent Object Manager
 * Copyright © Vienna, Austria 2014 by FH Campus Wien 
 * Some rights reserved. See COPYING, AUTHORS.
 */
package at.ac.fhcampuswien.atom.client.gui.attributes;

import java.util.LinkedHashMap;
import java.util.logging.Level;

import com.google.gwt.core.shared.GWT;
import com.google.gwt.resources.client.CssResource;
import com.google.gwt.uibinder.client.UiBinder;
import com.google.gwt.uibinder.client.UiField;
import com.google.gwt.user.client.ui.MultiWordSuggestOracle;
import com.google.gwt.user.client.ui.SuggestBox;
import com.google.gwt.user.client.ui.Widget;

import at.ac.fhcampuswien.atom.client.rpc.RPCCaller;
import at.ac.fhcampuswien.atom.client.rpc.WaitingFor;
import at.ac.fhcampuswien.atom.shared.AtomTools;
import at.ac.fhcampuswien.atom.shared.DomainClass;
import at.ac.fhcampuswien.atom.shared.exceptions.ValidationError;

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

	@UiField(provided=true)
	protected SuggestBox suggestBox;
	
	private MultiWordSuggestOracle oracle;
	
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
		
		AtomTools.log(Level.INFO, "prepareSuggestBox() value = "+suggestBox.getValue(), this);
		
		oracle.clear();
		if(keyDisplayMap != null && keyDisplayMap.size()>0)
		for(String s : keyDisplayMap.values()) {
			if(s != null && s.length()>0)
				oracle.add(s);
		}
//		oracle.add("something static");
//		oracle.add("something also static");
//		suggestBox.refreshSuggestionList();
//		suggestBox.showSuggestionList();
//		SuggestBox oldBox = suggestBox;
//		suggestBox = new SuggestBox(oracle, oldBox.getValueBox());
		
		
//		SuggestOracle oracle = suggestBox.getSuggestOracle();
//		oracle.
//		suggestBox.
		// TODO FIXME implement!
	}
	
	@Override
	protected boolean createFieldWidget() {
		field = this;
		oracle = new MultiWordSuggestOracle();
		oracle.add("test");
		suggestBox = new SuggestBox(oracle);
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
		if(keyDisplayMap.values().contains(suggestBox.getValue()) || allowOtherValues)
			this.value = suggestBox.getValue();
		else
			throw new ValidationError("SuggestBoxView Value entered that is not contained within list!");
	}
}
