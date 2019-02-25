/* ATOM - Advanced Transparent Object Manager
 * Copyright © Vienna, Austria 2014 by FH Campus Wien 
 * Some rights reserved. See COPYING, AUTHORS.
 */
package at.ac.fhcampuswien.atom.client.gui.attributes;

import java.util.Collection;
import java.util.List;

import at.ac.fhcampuswien.atom.client.App;
import at.ac.fhcampuswien.atom.client.ClientConfig;
import at.ac.fhcampuswien.atom.client.ClientTools;
import at.ac.fhcampuswien.atom.client.gui.dnd.DndTextBox;
import at.ac.fhcampuswien.atom.client.gui.dnd.DomainObjectDropController;
import at.ac.fhcampuswien.atom.client.gui.frames.DomainObjectDetailFrame;
import at.ac.fhcampuswien.atom.client.gui.frames.DomainObjectSelectorFrame;
import at.ac.fhcampuswien.atom.client.gui.frames.DomainObjectSelectorFrame.DomainObjectSelectionHandler;
import at.ac.fhcampuswien.atom.client.rpc.RPCCaller;
import at.ac.fhcampuswien.atom.client.rpc.WaitingFor;
import at.ac.fhcampuswien.atom.shared.AtomConfig;
import at.ac.fhcampuswien.atom.shared.AtomTools;
import at.ac.fhcampuswien.atom.shared.DataFilter;
import at.ac.fhcampuswien.atom.shared.DomainClass;
import at.ac.fhcampuswien.atom.shared.DomainObjectList;
import at.ac.fhcampuswien.atom.shared.domain.DomainObject;

import java.util.logging.Level;
import com.google.gwt.core.shared.GWT;
import com.google.gwt.dom.client.Style.Unit;
import com.google.gwt.event.dom.client.ClickEvent;
import com.google.gwt.event.dom.client.MouseOutEvent;
import com.google.gwt.event.dom.client.MouseOutHandler;
import com.google.gwt.event.dom.client.MouseOverEvent;
import com.google.gwt.event.dom.client.MouseOverHandler;
import com.google.gwt.resources.client.CssResource;
import com.google.gwt.uibinder.client.UiBinder;
import com.google.gwt.uibinder.client.UiField;
import com.google.gwt.uibinder.client.UiHandler;
import com.google.gwt.user.client.rpc.AsyncCallback;
import com.google.gwt.user.client.ui.PushButton;
import com.google.gwt.user.client.ui.Widget;

public class DomainObjectView extends AttributeView<DomainObject, DndTextBox, DomainObject> {

	private static DomainObjectViewUiBinder uiBinder = GWT.create(DomainObjectViewUiBinder.class);

	interface DomainObjectViewUiBinder extends UiBinder<Widget, DomainObjectView> {
	}

	interface PanelStyle extends CssResource {
		String textBox();
	}

	private DomainObjectDropController domainObjectDropController;
	private String representedType;
	private String name;
	private boolean onlyWriteables;
	private boolean madeDraggable = false;
	private boolean madeDropable = false;
	private DomainObjectDetailFrame owningFrame;
	private boolean theMouseIsOverMe = false;

	@UiField
	protected DndTextBox textBox;
	@UiField
	protected PushButton deleteButton;
	@UiField
	protected PushButton searchButton;

	public DomainObjectView(String representedType, String name, boolean onlyWriteables, DomainObjectDetailFrame forFrame) {
		this.representedType = representedType;
		this.name = name;
		this.onlyWriteables = onlyWriteables;
		this.owningFrame = forFrame;

		searchButton.getElement().getStyle().setPaddingLeft(3, Unit.PX);
		deleteButton.getElement().getStyle().setPaddingLeft(3, Unit.PX);

		// textBox.getElement().getStyle().setBorderColor("red");
		domainObjectDropController = new DomainObjectDropController(this, new DomainObjectDropController.HandlesDrops() {
			private String previousColor = ClientConfig.colorDragReady;

			public boolean interestedIn(DomainObject domainObject) {
				previousColor = textBox.getElement().getStyle().getBackgroundColor();
				if (!readOnly
						&& domainObject != null
						&& RPCCaller.getSinglton().getLoadedDomainTree() != null
						&& RPCCaller.getSinglton().getLoadedDomainTree().getDomainClassNamed(DomainObjectView.this.representedType)
								.isClassOfInstance(domainObject)) {
					textBox.getElement().getStyle().setBackgroundColor(ClientConfig.colorDragOverMatch);
					return true;
				} else {
					if (!readOnly)
						textBox.getElement().getStyle().setBackgroundColor(ClientConfig.colorDragOverMismatch);
					return false;
				}
			}

			public void dropperLeft() {
				if (!readOnly)
					textBox.getElement().getStyle().setBackgroundColor(previousColor);
			}

			public boolean drop(DomainObject domainObject) {
				if (!readOnly) {
					textBox.getElement().getStyle().setBackgroundColor(ClientConfig.colorDragReady);
					if (!readOnly
							&& domainObject != null
							&& RPCCaller.getSinglton().getLoadedDomainTree() != null
							&& RPCCaller.getSinglton().getLoadedDomainTree().getDomainClassNamed(DomainObjectView.this.representedType)
									.isClassOfInstance(domainObject)) {
						// if(domainObject != null &&
						// DomainObjectView.this.representedType.equals(domainObject.getConcreteClass()))
						// {
						value = domainObject;
						showValue();
						return true;
					} else {
						return false;
					}
				} else {
					return false;
				}
			}
		});

		showValue();

		this.addDomHandler(new MouseOutHandler() {

			@Override
			public void onMouseOut(MouseOutEvent event) {
				App.suggestDND(null);
				theMouseIsOverMe = false;
			}
		}, MouseOutEvent.getType());

		this.addDomHandler(new MouseOverHandler() {

			@Override
			public void onMouseOver(MouseOverEvent event) {
				theMouseIsOverMe = true;

				DomainClass tree = RPCCaller.getSinglton().getLoadedDomainTree();
				if (tree != null) {
					App.suggestDND(tree.getDomainClassNamed(DomainObjectView.this.representedType));
				} else {
					App.suggestDND(null);
				}
			}
		}, MouseOverEvent.getType());
	}

	@Override
	public void suggestDND(DomainClass dc) {
		if (dc == null || theMouseIsOverMe) {
			this.textBox.getElement().getStyle().setBackgroundColor(ClientConfig.colorDragReady);
		} else if (dc.classOrSuperClassNamed(representedType)) {
			this.textBox.getElement().getStyle().setBackgroundColor(ClientConfig.colorDomainClassMatch);
		} else {
			this.textBox.getElement().getStyle().setBackgroundColor(ClientConfig.colorDragReady); // colorDragOverMismatch);
		}
	}

	public String getRepresentedType() {
		return representedType;
	}

	@UiHandler("deleteButton")
	protected void onClick_deleteButton(ClickEvent event) {
		setValue(null);
	}

	@UiHandler("searchButton")
	protected void onClick_searchButton(ClickEvent event) {

		ClientTools.getFilterConfigFromUser(DomainObjectView.this.representedType, false, new WaitingFor<Collection<DataFilter>>() {

			@Override
			public void recieve(final Collection<DataFilter> filters) {
				textBox.setText("... suche, bitte warten ...");

				DataFilter quickSearch = null;
				DataFilter deepSearch = null;
				for(DataFilter f : filters) {
					if(AtomConfig.specialFilterDeepSearch.equals(f.getColumn()))
						deepSearch = f;
					else if(AtomConfig.specialFilterQuickSearch.equals(f.getColumn()))
						quickSearch = f;
				}
				
				DomainClass representedClass = RPCCaller.getSinglton().getLoadedDomainTree().getDomainClassNamed(DomainObjectView.this.representedType);
				final String searchString;
				final boolean onlyScanStringRepresentation;
				if(deepSearch != null) {
					searchString = deepSearch.getValue();
					onlyScanStringRepresentation = true;
				}
				else {
					onlyScanStringRepresentation = false;
					if(quickSearch != null)
						searchString = quickSearch.getValue();
					else
						searchString = null;
				}
				
				RPCCaller.getSinglton().loadListOfDomainObjects(representedClass, filters, null, 0, 25, false, searchString, onlyScanStringRepresentation, false, onlyWriteables, true, new AsyncCallback<DomainObjectList>() {
					
					@Override
					public void onSuccess(DomainObjectList result) {
						if(result == null || result.getTotalSize() <= 0 || result.getDomainObjects() == null || result.getDomainObjects().size() <= 0) {
							//result is empty, no DomainObjects recieved!
							AtomTools.log(Level.INFO, "nichts gefunden", this);
							DomainObjectView.this.showValue();
						}
						else if(result.getDomainObjects().size() == 1) {
							value = result.getDomainObjects().get(0);
							DomainObjectView.this.showValue();
						}
						else {
							//multiple results
							getObjectFromSelectorFrame(result, searchString, onlyScanStringRepresentation);
						}
					}
					
					@Override
					public void onFailure(Throwable caught) {
						AtomTools.log(Level.SEVERE, "Fehler beim laden des Suchergebnisses vom Server: " + caught, this);
					}
				});
			}

			@Override
			public void requestFailed(String reason) {
				// don't care
			}
		});

		// popup.center();
		// DialogBox dialogBox = new DialogBox(false, true);
		// dialogBox.setText("hello!");
		// dialogBox.center();
		// dialogBox.show();

		// AtomGUI.getSinglton().openList(RPCCaller.getSinglton().getDomainTree(null, false).getDomainClassNamed(DomainObjectView.this.representedType));
	}

	private void getObjectFromSelectorFrame(DomainObjectList preloadedResult, String searchString, boolean simpleSearch) {
		DomainObjectSelectorFrame frame = new DomainObjectSelectorFrame(getRepresentedType(), false, name, preloadedResult, searchString, simpleSearch, onlyWriteables, new DomainObjectSelectionHandler() {

			@Override
			public void handleDomainObjectList(List<DomainObject> list) {
				if (list.size() == 1) {
					DomainObjectView.this.value = list.get(0);
					App.addFrame(owningFrame);
					DomainObjectView.this.showValue();
				} else {
					AtomTools.log(Level.SEVERE, "recieved a list with size != 1 for singleSelction DomainObjectPopup", this);
				}
			}

			@Override
			public void cancelSelection() {
				App.addFrame(owningFrame);
			}
		});
		App.showFrame(frame);
	}

	@Override
	protected boolean createFieldWidget() {
		initWidget(uiBinder.createAndBindUi(this));
		field = textBox;
		return false;
	}

	@Override
	public void setReadOnly(boolean readOnly) {
		super.setReadOnly(readOnly);

		if (deleteButton != null)
			deleteButton.setEnabled(!readOnly);
		if (searchButton != null)
			searchButton.setEnabled(!readOnly);

		if (!readOnly)
			field.getElement().getStyle().setBackgroundColor(ClientConfig.colorDragReady);
	}

	@Override
	protected void showValue() {
		if (value == null) {

			if (AtomConfig.nullReasonNotRelationEssential.equals(this.getNullReason())) {
				textBox.setText("Permission denied!");
			} else if (AtomConfig.nullReasonLazyLoading.equals(this.getNullReason())) {
				textBox.setText("bitte warten.. noch nicht fertig geladen..");
			} else {
				textBox.setText("empty");
			}

			if (madeDraggable) {
				App.unRegisterDndWidget(textBox);
				madeDraggable = false;
			}

		} else {
			textBox.setValue(value);
			if (!madeDraggable) {
				App.registerDndWidget(textBox);
				madeDraggable = true;
			}
		}
	}

	@Override
	protected void readValue() {
		// not required, because the value can only be changed by dropping
		// something on the field, which is registered by the dropHandler
	}

	@Override
	public void goingInvisible() {
		if (madeDropable) {
			AtomTools.log(Level.FINER, "unregistering dropController", this);
			App.unregisterDndDropController(domainObjectDropController);
			madeDropable = false;
		}
	}

	@Override
	public void goingVisible() {
		if (!madeDropable) {
			AtomTools.log(Level.FINER, "registering dropController", this);
			App.registerDndDropController(domainObjectDropController);
			madeDropable = true;
		}
	}
}
