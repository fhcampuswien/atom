/* ATOM - Advanced Transparent Object Manager
 * Copyright © Vienna, Austria 2014 by FH Campus Wien 
 * Some rights reserved. See COPYING, AUTHORS.
 */
package at.ac.fhcampuswien.atom.client.gui.frames;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.LinkedHashMap;
import java.util.LinkedHashSet;
import java.util.Map;
import java.util.Map.Entry;
import java.util.Set;

import at.ac.fhcampuswien.atom.client.App;
import at.ac.fhcampuswien.atom.client.ClientTools;
import at.ac.fhcampuswien.atom.client.gui.CenterHeader;
import at.ac.fhcampuswien.atom.client.gui.CenterHeader.State;
import at.ac.fhcampuswien.atom.client.gui.attributes.AttributeView;
import at.ac.fhcampuswien.atom.client.rpc.RPCCaller;
import at.ac.fhcampuswien.atom.client.rpc.WaitingFor;
import at.ac.fhcampuswien.atom.shared.AtomConfig;
import at.ac.fhcampuswien.atom.shared.AtomTools;
import at.ac.fhcampuswien.atom.shared.DomainClass;
import at.ac.fhcampuswien.atom.shared.DomainClassAttribute;
import at.ac.fhcampuswien.atom.shared.Notifiable;
import at.ac.fhcampuswien.atom.shared.domain.DomainObject;
import at.ac.fhcampuswien.atom.shared.exceptions.AuthenticationException;
import at.ac.fhcampuswien.atom.shared.exceptions.ValidationError;

import com.allen_sauer.gwt.log.client.Log;
import com.google.gwt.core.shared.GWT;
import com.google.gwt.dom.client.Style.Unit;
import com.google.gwt.event.logical.shared.ResizeEvent;
import com.google.gwt.resources.client.CssResource;
import com.google.gwt.uibinder.client.UiBinder;
import com.google.gwt.uibinder.client.UiField;
import com.google.gwt.user.client.Window;
import com.google.gwt.user.client.ui.FlexTable;
import com.google.gwt.user.client.ui.Label;
import com.google.gwt.user.client.ui.ScrollPanel;
import com.google.gwt.user.client.ui.SimplePanel;
import com.google.gwt.user.client.ui.Widget;

public class DomainObjectDetailFrame extends Frame {

	private static DomainObjectDetailFrameUiBinder uiBinder = GWT.create(DomainObjectDetailFrameUiBinder.class);

	interface DomainObjectDetailFrameUiBinder extends UiBinder<Widget, DomainObjectDetailFrame> {
	}

	private static String getName(DomainObject object, DomainClass domainClass) {
		if (object != null && object.getObjectID() != null) {
			return object.getStringRepresentation();
		} else {
			return domainClass.getSimpleName() + " (neu)";
		}
	}

	private static CenterHeader.State getState(boolean editable, DomainClass domainClass, DomainObject object) {
		
		Set<String> at = object == null ? null : object.getUserPermissions();
		if(at == null)
			at = domainClass.getAccessHandler().getNoRelationRequiredAccess(RPCCaller.getSinglton().getClientSession());

		if ((object == null || object.getObjectID() == null)
				&& AtomTools.isAccessAllowed(AtomConfig.accessCreateNew, at))
			return CenterHeader.State.OBJECT_EDIT_NEW;

		else {
			
			if (AtomTools.isAccessAllowed(AtomConfig.accessReadWrite, at) || ((object == null || object.getObjectID() == null) && at.contains(AtomConfig.accessCreateNew))) {
				if (editable) {
					return CenterHeader.State.OBJECT_EDIT_NORMAL;
				} else {
					return CenterHeader.State.OBJECT_DETAIL_VIEW_NORMAL;
				}
			} else if (AtomTools.isAccessAllowed(AtomConfig.accessLinkage, at)) {
				return CenterHeader.State.EMPTY;
			} else {
				throw new AuthenticationException("User is not allowed to read DomainClass '"
						+ domainClass.getSingularName() + "' instance '" + object.getStringRepresentation() + "'");
			}
		}
	}

	interface PanelStyle extends CssResource {
		String whiteSpace();

		String space();

		String invisibleBorder();

		String avLabel();
	}

	@UiField
	protected PanelStyle panelStyle;

	@UiField
	protected SimplePanel center;
	@UiField
	protected SimplePanel northPanel;

	private AtomTabPanel tabPanel;

	private boolean isEditable;
	private boolean unsavedNew = false;
	
	private HashMap<String, FlexTable> groupPages = new HashMap<String, FlexTable>();
	private LinkedHashMap<DomainClassAttribute, AttributeView<?, ?, ?>> attributeFields = new LinkedHashMap<DomainClassAttribute, AttributeView<?, ?, ?>>();
	private HashMap<DomainClassAttribute, Label> attributeLables = new HashMap<DomainClassAttribute, Label>();
	private LinkedHashMap<String, ArrayList<DomainClassAttribute>> classGroups = null;
	private HashMap<String, HashMap<DomainClassAttribute, Integer>> visibleAttributesPositions = new HashMap<String, HashMap<DomainClassAttribute,Integer>>();
	
	private WaitingFor<DomainObject> objectReciever = null;

	public DomainObjectDetailFrame(DomainObject object, DomainClass classOfObject, boolean editable,
			WaitingFor<DomainObject> reciever) {
		this(object, classOfObject, editable);
		this.objectReciever = reciever;
	}

	public DomainObjectDetailFrame(DomainObject object, DomainClass domainClass, boolean isEditable) {
		super(AtomTools.getMessages().Detailansicht_von(getName(object, domainClass)), getName(object, domainClass),
				getState(isEditable, domainClass, object), me, object, domainClass, AtomConfig.FrameType.DETAIL_VIEW);

		AtomTools.log(Log.LOG_LEVEL_TRACE, "DomainObjectDetailFrame.Constructor started", this);

		initWidget(uiBinder.createAndBindUi(this));

		if(object != null) {
			this.representedObject = object;
		}
		else {
			unsavedNew = true;
			representedObject = ClientTools.createInstance(representedClass);
		}
		representedObject.registerWatcher(shownFieldsNotifiable);
		
		this.representedClass = domainClass;
		this.isEditable = isEditable;
		// subscribeMeToChangesOfMyObject();

		tabPanel = new AtomTabPanel();
		// tabPanel = new TabLayoutPanel(23,
		// com.google.gwt.dom.client.Style.Unit.PX);
		// tabPanel.getElement().getStyle().setHeight(100, Unit.PCT);
		// tabPanel.getElement().getStyle().setWidth(100, Unit.PCT);
		// tabPanel = new DecoratedTabPanel();
		// tabPanel.setStyleName("nothing");
		// tabPanel.getElement().getStyle().setBorderStyle(BorderStyle.HIDDEN);

		// tabPanel.

		generateFields();
		representedObject.onDetailViewOpened();

		northPanel.add(tabPanel.getTabBar());
		center.add(tabPanel.getDeckPanel());

		if (!unsavedNew) {
			loadValues();

			if (representedObject.getCompletelyLoaded() != true) {
				App.setLoadingState(true, DomainObjectDetailFrame.this);
				RPCCaller.getSinglton().loadDomainObject(representedObject.getObjectID(),
						representedObject.getConcreteClass(), receiver);
			}
		}

		AtomTools.log(Log.LOG_LEVEL_TRACE, "DomainObjectDetailFrame.Constructor finished", this);
	}

	// private void subscribeMeToChangesOfMyObject() {
	// if (representedObject != null) {
	// RPCCaller.getSinglton().registerChangeListener(representedObject.getObjectID(),
	// new ChangeNoticer());
	// if (new Boolean(false).equals(representedObject.getCompletelyLoaded())) {
	// AtomGUI.getSinglton().setLoadingState(true, null);
	// RPCCaller.getSinglton().loadDomainObject(representedObject.getObjectID(),
	// representedObject.getConcreteClass(), null);
	// }
	// }
	// }

	private void generateFields() {
		classGroups = representedClass.getGroupedAttributes();
		for (Map.Entry<String, ArrayList<DomainClassAttribute>> group : classGroups.entrySet()) {
			handleGroup(group.getValue(), group.getKey());
		}
	}
	
	private void handleGroup(ArrayList<DomainClassAttribute> attributes, String groupName) {

		// only show the System tab if the user is admin
//		if ("System".equals(groupName) && !RPCCaller.getSinglton().isUserAdmin())
//			return;

		HashMap<DomainClassAttribute, Integer> attributesPositions = new HashMap<DomainClassAttribute, Integer>(); 
		
		// only make a tab if the group is non empty
		if (attributes != null && attributes.size() > 0) {
			groupPages.put(groupName, new FlexTable());
			groupPages.get(groupName).setStyleName(panelStyle.invisibleBorder());
			ScrollPanel scrollPanel = new ScrollPanel(groupPages.get(groupName));
			scrollPanel.setStylePrimaryName(panelStyle.whiteSpace());

			HashSet<String> labels = new HashSet<String>();

			int j = 0;
			for(final DomainClassAttribute attribute : attributes) {
//			for (int i = 0; i < attributes.size(); i++) {
//				DomainClassAttribute attribute = attributes.get(i);
				Boolean hide = attribute.getHideFromDetailGui();
				Integer nr = null;
				if(representedObject != null)
					nr = representedObject.getNullReasons().get(attribute.getName());
				if ((hide == null || hide == false) && nr != AtomConfig.nullReasonNotRelationEssential) {
					AttributeView<?, ?, ?> attributeView = AttributeView.getAttributeViewForType(attribute, this);
					// attributeView.setSize("100%", "100%");

					if (representedObject != null) {
						attributeView.setReadOnly(!isEditable);
						attributeView.setValue(ClientTools.getAttributeValue(representedClass, attribute,
								representedObject));

						if (representedObject.getNullReasons() != null
								&& representedObject.getNullReasons().containsKey(attribute.getName())) {
							Integer nullReason = representedObject.getNullReasons().get(attribute.getName());
							AtomTools.log(Log.LOG_LEVEL_TRACE, "NullReason for Attribute " + attribute.getName()
									+ " instance " + representedObject + " = " + nullReason, this);

							if (representedObject.getCompletelyLoaded() == true
									&& nullReason == AtomConfig.nullReasonLazyLoading)
								nullReason = null;

							attributeView.setNullReason(nullReason);
						} else {
							AtomTools.log(Log.LOG_LEVEL_TRACE, "NO NullReason for Attribute " + attribute.getName()
									+ " instance " + representedObject, this);
							attributeView.setNullReason(null);
						}
					}
					
					if(attribute.isWriteAble()) {
						attributeView.addChangeHandler(new Notifiable<Object>() {
	
							@Override
							public void doNotify(Object reason) {
								if(representedObject != null) {
									ClientTools.setAttributeValue(representedClass, attribute, representedObject, reason);
								}
							}
						});
					}
					

					// else if (!attribute.isWriteAble())
					// attributeView.setValue("nach dem ersten Speichern sichtbar");
					attributeView.setReadOnly(!(isEditable && attribute.isWriteAble()));
					attributeFields.put(attribute, attributeView);

					Label avLabel = new Label(attribute.getDisplayName());
					avLabel.setStylePrimaryName(panelStyle.avLabel());
					avLabel.setWordWrap(false);
					attributeLables.put(attribute, avLabel);
					labels.add(attribute.getDisplayName());

					// flexTable.setText(i, 0, attribute.getDisplayName());
					groupPages.get(groupName).setWidget(j, 0, avLabel);
					groupPages.get(groupName).setWidget(j, 1, attributeView);
					attributesPositions.put(attribute, j);

					j++;
				}
			}
			
			if(groupName == null)
				groupName = representedClass.getDefaultAttributeGroup();
			
			visibleAttributesPositions.put(groupName, attributesPositions);

			AttributeGroupTabHeader attributeGroupTabHeader = new AttributeGroupTabHeader(groupName);
			attributeGroupTabHeader.setStyleName(panelStyle.space());
			// Label label = new Label(groupName);
			// FlexTable tabHeader = new FlexTable();
			// tabHeader.setWidget(3, 0, label);
			// tabHeader.getFlexCellFormatter().setRowSpan(0, 2, 2);

			// flexTable.getElement().getStyle().setPadding(10, Unit.PX);
			// scrollPanel.getElement().getStyle().setMargin(10, Unit.PX);

			tabPanel.add(scrollPanel, attributeGroupTabHeader);

			int maxLabelWidth = 0;
			// Label testLabel = new Label("init");
			// RootLayoutPanel.get().add(testLabel);
			for (String label : labels) {
				// testLabel.setText(label);
				// //testLabel.getElement().getAbsoluteRight()
				maxLabelWidth = Math.max(maxLabelWidth, App.getTextWidth(label));
			}
			// RootLayoutPanel.get().remove(testLabel);
			groupPages.get(groupName).getColumnFormatter().getElement(0).getStyle().setWidth(maxLabelWidth, Unit.PX);
		}
	}

	private void loadNullReasons() {

		for (DomainClassAttribute attribute : this.attributeFields.keySet()) {
			AttributeView<?, ?, ?> attributeView = attributeFields.get(attribute);

			if (representedObject.getNullReasons() != null
					&& representedObject.getNullReasons().containsKey(attribute.getName())) {
				Integer nullReason = representedObject.getNullReasons().get(attribute.getName());
				AtomTools.log(Log.LOG_LEVEL_TRACE, "NullReason for Attribute " + attribute.getName() + " instance "
						+ representedObject + " = " + nullReason, this);

				if (representedObject.getCompletelyLoaded() == true && nullReason == AtomConfig.nullReasonLazyLoading)
					nullReason = null;

				attributeView.setNullReason(nullReason);
			} else {
				AtomTools.log(Log.LOG_LEVEL_TRACE, "NO NullReason for Attribute " + attribute.getName() + " instance "
						+ representedObject, this);
				attributeView.setNullReason(null);
			}

		}

		// FIXME: implement
	}

	private void loadValues() {
		if (representedObject != null && representedClass != null) {
			setEditableAndRevertValues(null, true);
		}
	}

//	private void loadValuesOfClass(DomainClass domainClass) {
//		if (domainClass.getSuperClass() != null)
//			loadValuesOfClass(domainClass.getSuperClass());
//		for (DomainClassAttribute oneAttribute : domainClass.getAttributes()) {
//			if (oneAttribute.getAnnotation("HideFromDetailGui") == null) {
//				AttributeView<?, ?> attributeView = attributeFields.get(oneAttribute);
//				if (attributeView != null)
//					attributeView.setValue(ClientTools.getAttributeValue(domainClass, oneAttribute, representedObject));
//			}
//		}
//	}

	private void setEditableAndRevertValues(Boolean editable, boolean revert) {
		
		if(editable != null)
			this.isEditable = editable;
		
		changeCenterHeaderButtonPanelState(getState(isEditable, this.getRepresentedClass(), representedObject));

		for (Entry<DomainClassAttribute, AttributeView<?, ?, ?>> entry : attributeFields.entrySet()) {
			
			AttributeView<?, ?, ?> attributeView = entry.getValue();
			
			if(editable != null) {
				boolean readOnly = !isEditable || !entry.getKey().isWriteAble();
				
				//workaround GWT-BUG https://code.google.com/p/google-web-toolkit/issues/detail?id=1488
//				if(readOnly && !attributeView.isReadOnly() && attributeView instanceof StringFormattedLobView) {
//					String group = entry.getKey().getAttributeGroup();
//					if(group == null)
//						group = representedClass.getDefaultAttributeGroup();
//					attributeView = AttributeView.getAttributeViewForType(entry.getKey(), this);
//					attributeView.setReadOnly(readOnly);
////					attributeView.setValue("locked?"); //entry.getValue().getValue());
//					Integer pos = visibleAttributesPositions.get(group).get(entry.getKey());
//					groupPages.get(group).clearCell(pos, 1);
//					groupPages.get(group).setWidget(pos, 1, attributeView);
//					attributeFields.put(entry.getKey(), attributeView);
//					revert = false;
//				}
//				else {
					attributeView.setReadOnly(readOnly);
//				}
			}

			if (revert) {
				attributeView.setValue(ClientTools.getAttributeValue(representedClass, entry.getKey(), representedObject));
			}
		}
	}
	
	private Notifiable<HashMap<String,LinkedHashSet<String>>> shownFieldsNotifiable = new Notifiable<HashMap<String,LinkedHashSet<String>>>() {

		@Override
		public void doNotify(HashMap<String,LinkedHashSet<String>> reason) {
			if(reason == null || reason.size() == 0)
				return;
			
			LinkedHashSet<String> autoGroup = reason.get(DomainObject.autoDetectAttributeGroup);
			if(autoGroup != null && autoGroup.size() > 0) {
				boolean first = true, showAllButListed = false;
				for(String s : autoGroup) {
					if(first) {
						first = false;
						if(DomainObject.showAllButListed.equals(s)) {
							showAllButListed = true;
							continue;
						}
					}
					DomainClassAttribute attr = representedClass.getAttributeNamed(s);
					if(attr == null) {
						AtomTools.log(Log.LOG_LEVEL_ERROR, "could not find Attribute named \"" + s + "\" in DomainClass \"" + representedClass.getName() + "\"", this);
					}
					else {
						String attrGroup = attr.getAttributeGroup();
						if(attrGroup == null || attrGroup.length() == 0) {
							attrGroup = representedClass.getDefaultAttributeGroup();
						}
						LinkedHashSet<String> currentGroup = reason.get(attrGroup);
						if(currentGroup == null) {
							currentGroup = new LinkedHashSet<String>();
						}
						if(currentGroup.size() == 0 && showAllButListed) {
							currentGroup.add(DomainObject.showAllButListed);
						}
						
						if(showAllButListed == (currentGroup.size() > 0 && 
								DomainObject.showAllButListed.equals(currentGroup.iterator().next())
								)
						  ) {
							currentGroup.add(s);
						}
						else {
							currentGroup.remove(s);
						}
						reason.put(attrGroup, currentGroup);
					}
				}
				reason.remove(autoGroup);
			}
			
			for(Entry<String,LinkedHashSet<String>> entry : reason.entrySet()) {
				
				String group = entry.getKey();
				
				if(DomainObject.autoDetectAttributeGroup.equals(group))
					continue;
				
				if(group == null) {
					AtomTools.log(Log.LOG_LEVEL_ERROR, "hidden fields contains group null - this should not happen, any field that is shown or hidden without a specified group should land in the autoGroup group!", this);
					continue;
				}
				
				FlexTable groupPage = groupPages.get(group);
				groupPage.removeAllRows();
				HashMap<DomainClassAttribute, Integer> attributesPositions = new HashMap<DomainClassAttribute, Integer>();
				
				ArrayList<String> attributes = new ArrayList<String>();
				
				boolean first = true, showAllButListed = false;
				for(String s : entry.getValue()) {
					if(first) {
						first = false;
						if(DomainObject.showAllButListed.equals(s)) {
							showAllButListed = true;
							for(DomainClassAttribute atr : classGroups.get(group)) {
								attributes.add(atr.getName());
							}
							continue;
						}
					}
					
					if(showAllButListed)
						attributes.remove(s);
					else
						attributes.add(s);
				}
				
				int i = 0;
				for(String an : attributes) {
					DomainClassAttribute atr = representedClass.getAttributeNamed(an);
					groupPage.setWidget(i, 0, attributeLables.get(atr));
					groupPage.setWidget(i, 1, attributeFields.get(atr));
					attributesPositions.put(atr, i);
					i++;
				}
				visibleAttributesPositions.put(group, attributesPositions);
			}
			
			//need to go over all group pages and restore the fields that might have been hidden previously, and no entry is given now.
			for(Entry<String, FlexTable> gp : groupPages.entrySet()) {
				if(!reason.keySet().contains(gp.getKey())) {
					gp.getValue().removeAllRows();
					HashMap<DomainClassAttribute, Integer> attributesPositions = new HashMap<DomainClassAttribute, Integer>();
					int i = 0;
					for(DomainClassAttribute atr : classGroups.get(gp.getKey())) {
						gp.getValue().setWidget(i, 0, attributeLables.get(atr));
						gp.getValue().setWidget(i, 1, attributeFields.get(atr));
						attributesPositions.put(atr, i);
						i++;
					}
					visibleAttributesPositions.put(gp.getKey(), attributesPositions);
				}
			}
		}
	};

	private WaitingFor<DomainObject> receiver = new WaitingFor<DomainObject>() {

		@Override
		public void requestFailed(String reason) {
			AtomTools.log(Log.LOG_LEVEL_ERROR, "DomainObject request failed -> " + reason, this);
		}

		@Override
		public void recieve(DomainObject instance) {
			App.setLoadingState(true, DomainObjectDetailFrame.this);

			// change titles to represent the new instance state.
			String name = getName(instance, representedClass);
			DomainObjectDetailFrame.this.setTitles("Detailansicht von " + name, name);
			
			if(DomainObjectDetailFrame.this.representedObject.getObjectID() == null) {
				//if we save an instance for the first time, then we didn't have an objectid before.
				//the identity changed --> remove the old thing from the userinterface, and add the new one.
				App.removeFromUI(DomainObjectDetailFrame.this.representedObject);
				DomainObjectDetailFrame.this.representedObject = instance;
				App.addFrame(DomainObjectDetailFrame.this);
			}
			else {
				instance.setShownFields(representedObject.getShownFields());
				representedObject.unregisterWatcher(shownFieldsNotifiable);
				representedObject = instance;
				instance.registerWatcher(shownFieldsNotifiable);
				unsavedNew = false;
			}
 
			loadNullReasons();
			loadValues();
			
//			AtomGUI.getSinglton().frameIdentityChanged(DomainObjectDetailFrame.this);

			if (objectReciever != null)
				objectReciever.recieve(instance);

			App.setLoadingState(false, DomainObjectDetailFrame.this);
		}

	};

	@Override
	public void actionSave() {
		App.setLoadingState(true, this);
		if (representedObject == null) {
			// create
			representedObject = ClientTools.createInstance(representedClass);
			// have to recieve manually, because normal update procedure can't
			// work because we don't know the id the new instance will get from
			// the database
		}
		try {
			// update
			for (DomainClassAttribute anAttribute : attributeFields.keySet()) {
				AttributeView<?, ?, ?> aView = attributeFields.get(anAttribute);
				if (anAttribute.isWriteAble())
					ClientTools.setAttributeValue(representedClass, anAttribute, representedObject, aView.getValue());
				else
					aView.setValue(ClientTools.getAttributeValue(representedClass, anAttribute, representedObject));
			}

			// validation
			ClientTools.validateDomainObject(representedObject, representedClass);
		} catch (ValidationError ve) {
			this.deliverError(ve);
			App.setLoadingState(false, this);
			return;
		}

		RPCCaller.getSinglton().saveDomainObject(representedObject, receiver);
		setEditableAndRevertValues(false, true);
	}

	@Override
	public void actionEdit() {
		setEditable(true);
	}

	@Override
	public void actionCancel() {
		if (Window.confirm(AtomTools.getMessages().confirm_cancel())) {
			if (representedObject != null && representedObject.getObjectID() != null) {
				setEditableAndRevertValues(false, true);
				App.setLoadingState(true, this);
				RPCCaller.getSinglton().loadDomainObject(representedObject.getObjectID(),
						representedObject.getConcreteClass(), receiver);
			} else
				App.closeFrame(this);
		}
	}

	@Override
	public void actionDelete() {
		if (Window.confirm(AtomTools.getMessages().confirm_delete())) {
			centerHeaderButtonPanelState = State.EMPTY;
			this.content = null;
			this.setTitles(AtomTools.getMessages().deleting(representedObject.getStringRepresentation()), AtomTools
					.getMessages().deleting(representedObject.getStringRepresentation()));
			RPCCaller.getSinglton().deleteDomainObject(representedObject, new Notifiable<String>() {

				public void doNotify(String notifyReason) {
					if ("deletion successful".equals(notifyReason)) {
						String title = AtomTools.getMessages().delete_success(representedObject.getStringRepresentation());
						DomainObjectDetailFrame.this.setTitles(title, title);
						App.removeFromUI(DomainObjectDetailFrame.this.representedObject);
						DomainObjectDetailFrame.this.representedObject = null;
					}
					if ("deletion failed".equals(notifyReason)) {
						DomainObjectDetailFrame.this.setTitles(
								AtomTools.getMessages().delete_error(representedObject.getStringRepresentation()),
								AtomTools.getMessages().delete_error(representedObject.getStringRepresentation()));
					}
				}
			});
			App.frameChanged(this);
		}
	}

	public void setEditable(boolean editable) {
		// TODO: ask if the user wants to revert changes (if the view was
		// editable before and should be changed to
		setEditableAndRevertValues(editable, false);
	}

	public boolean getIsEditable() {
		return isEditable;
	}

	@Override
	public boolean goingInvisible() {
		AtomTools
				.log(Log.LOG_LEVEL_TRACE, "DomainObjectDetailView '" + this.getLongTitle() + "' going invisible", this);
		for (AttributeView<?, ?, ?> attributeView : attributeFields.values()) {
			attributeView.goingInvisible();
		}
		return true;
	}

	@Override
	public void goingVisible() {
		AtomTools.log(Log.LOG_LEVEL_TRACE, "DomainObjectDetailView '" + this.getLongTitle() + "' going visible", this);
		for (AttributeView<?, ?, ?> attributeView : attributeFields.values()) {
			attributeView.goingVisible();
		}
	}

	@Override
	public void resize(ResizeEvent event) {
		for (AttributeView<?, ?, ?> attributeView : attributeFields.values()) {
			attributeView.resize(event);
		}
		ScrollPanel activeTab = (ScrollPanel) tabPanel.getWidget(tabPanel.getSelectedIndex());
		AtomTools.log(Log.LOG_LEVEL_TRACE, "active ScrollPanel = " + activeTab.toString(), this);
	}
	
	@Override
	public void suggestDND(DomainClass dc) {
		if(isEditable)
			for (AttributeView<?, ?, ?> attributeView : attributeFields.values())
				attributeView.suggestDND(dc);
	}

	public void setObjectReciever(WaitingFor<DomainObject> reciever) {
		this.objectReciever = reciever;
	}

	public void applyValuesOf(DomainObject sourceInstance) {
		
		if(this.representedObject == null)
			representedObject = ClientTools.createInstance(representedClass);

		HashMap<String, DomainClassAttribute> allAttrs = representedClass.getAllAttributes();
		for(DomainClassAttribute attr : allAttrs.values()) {
			if(attr.isWriteAble()) {
				Object value = ClientTools.getAttributeValue(representedClass, attr, sourceInstance);
				ClientTools.setAttributeValue(representedClass, attr, representedObject, value);	
			}
		}
		loadValues();
	}

	// private class ChangeNoticer implements Notifiable {
	//
	// @Override
	// public void doNotify(String notifyReason) {
	// if ("Object changed".equals(notifyReason)) {
	// DomainObject updatedObject =
	// RPCCaller.getSinglton().getLoadedObject(representedObject.getObjectID());
	// if (updatedObject != null) {
	// representedObject = updatedObject;
	// loadValues();
	// DomainObjectDetailFrame.this.setTitles("Detailansicht von " +
	// getName(representedObject, representedClass), getName(representedObject,
	// representedClass));
	// AtomGUI.getSinglton().setLoadingState(false,
	// DomainObjectDetailFrame.this);
	// } else {
	// centerHeaderButtonPanelState = State.EMPTY;
	// DomainObjectDetailFrame.this.content = null;
	// DomainObjectDetailFrame.this.setTitles(representedObject.getStringRepresentation()
	// + " wurd gelöscht", representedObject.getStringRepresentation() +
	// " wurd gelöscht");
	// }
	// AtomGUI.getSinglton().frameChanged(DomainObjectDetailFrame.this);
	// } else
	// AtomTools.log(Log.LOG_LEVEL_WARN,
	// "DomainObjectDetailFrame does not understand notifyReason=" +
	// notifyReason, this);
	// }
	//
	// }
}
