/* ATOM - Advanced Transparent Object Manager
 * Copyright © Vienna, Austria 2014 by FH Campus Wien 
 * Some rights reserved. See COPYING, AUTHORS.
 */
package at.ac.fhcampuswien.atom.client.gui.gxt;

import java.util.ArrayList;
import java.util.Date;
import java.util.HashSet;
import java.util.LinkedHashMap;
import java.util.List;

import com.allen_sauer.gwt.dnd.client.VetoDragException;
import java.util.logging.Level;
import com.google.gwt.cell.client.DateCell;
import com.google.gwt.event.logical.shared.ResizeEvent;
import com.google.gwt.i18n.client.DateTimeFormat;
import com.google.gwt.user.client.Timer;
import com.google.gwt.user.client.Window;
import com.google.gwt.user.client.ui.FocusPanel;
import com.google.gwt.user.client.ui.Label;
import com.google.gwt.user.client.ui.Widget;
import com.sencha.gxt.core.client.IdentityValueProvider;
import com.sencha.gxt.core.client.Style.SelectionMode;
import com.sencha.gxt.data.shared.ListStore;
import com.sencha.gxt.data.shared.ModelKeyProvider;
import com.sencha.gxt.data.shared.SortDir;
import com.sencha.gxt.data.shared.SortInfo;
import com.sencha.gxt.data.shared.SortInfoBean;
import com.sencha.gxt.data.shared.loader.FilterPagingLoadConfig;
import com.sencha.gxt.data.shared.loader.FilterPagingLoadConfigBean;
import com.sencha.gxt.data.shared.loader.LoadResultListStoreBinding;
import com.sencha.gxt.data.shared.loader.PagingLoadResult;
import com.sencha.gxt.data.shared.loader.PagingLoader;
import com.sencha.gxt.widget.core.client.container.VerticalLayoutContainer;
import com.sencha.gxt.widget.core.client.container.VerticalLayoutContainer.VerticalLayoutData;
import com.sencha.gxt.widget.core.client.event.CellDoubleClickEvent;
import com.sencha.gxt.widget.core.client.event.CellDoubleClickEvent.CellDoubleClickHandler;
import com.sencha.gxt.widget.core.client.event.SortChangeEvent;
import com.sencha.gxt.widget.core.client.form.NumberPropertyEditor.IntegerPropertyEditor;
import com.sencha.gxt.widget.core.client.form.NumberPropertyEditor.LongPropertyEditor;
import com.sencha.gxt.widget.core.client.grid.CheckBoxSelectionModel;
import com.sencha.gxt.widget.core.client.grid.ColumnConfig;
import com.sencha.gxt.widget.core.client.grid.ColumnModel;
import com.sencha.gxt.widget.core.client.grid.Grid;
import com.sencha.gxt.widget.core.client.grid.filters.BooleanFilter;
import com.sencha.gxt.widget.core.client.grid.filters.DateFilter;
import com.sencha.gxt.widget.core.client.grid.filters.GridFilters;
import com.sencha.gxt.widget.core.client.grid.filters.ListFilter;
import com.sencha.gxt.widget.core.client.grid.filters.NumericFilter;
import com.sencha.gxt.widget.core.client.grid.filters.StringFilter;

import at.ac.fhcampuswien.atom.client.App;
import at.ac.fhcampuswien.atom.client.gui.dnd.AtomDNDWidget;
import at.ac.fhcampuswien.atom.client.gui.frames.Frame;
import at.ac.fhcampuswien.atom.client.gui.gxt.MyRpcProxy.SettingsProvider;
import at.ac.fhcampuswien.atom.client.rpc.RPCCaller;
import at.ac.fhcampuswien.atom.shared.AtomTools;
import at.ac.fhcampuswien.atom.shared.DataFilter;
import at.ac.fhcampuswien.atom.shared.DomainClass;
import at.ac.fhcampuswien.atom.shared.DomainClassAttribute;
import at.ac.fhcampuswien.atom.shared.DomainObjectList;
import at.ac.fhcampuswien.atom.shared.domain.DomainObject;

/**
 * Represents a List of DomainObjects
 * 
 * @author thomas.kaefer
 * 
 */
public class DomainObjectListWidget extends FocusPanel implements AtomDNDWidget {

	private static int subtractedWidth = 30;
	private static int subtractedHeight = 227;

	private static int subtractedWidthForTabs = 54;
	private static int subtractedHeightForTabs = 263;

	private DomainClass representedClass;
	private Grid<DomainObject> grid;

	private HashSet<String> columnNames;
	private MyPagingToolBar toolBar;

	private List<ColumnConfig<DomainObject, ?>> columnConfigs = null;
	private GridFilters<DomainObject> gridFilters = null;

	private MyRpcProxy proxy = null;
	private VerticalLayoutContainer contentPanel = null;

	private int pageSize = 5;
	private int newPageSize = 5;
	private int totalSize = Integer.MAX_VALUE;
	private boolean firstLoad = true;
	// private int lastHeight, lastWidth;
	private String representedSearchString = null;
	
	private Date lastLoadAction = new Date();

	// private HashSet<DomainObject> observedObjects = null;

	private PagingLoader<FilterPagingLoadConfig, PagingLoadResult<DomainObject>> loader = null;
	private CheckBoxSelectionModel<DomainObject> cbSelectionModel;

	private SortInfo defaultSort = null;

	public enum ActionMode {
		DEFAULT_OPEN, SINGLE_SELECT, MULTI_SELECT
	}

	private ActionMode selectedMode;
	
	private boolean insideTab;
	private Frame owner;
	
	private DataFilter[] defaultFilters;

	public DomainObjectListWidget(DomainClass theRepresentedClass, DomainObjectList preloadedList, String searchString, boolean onlyScanStringRepresentation,
			DataFilter[] defaultFilters, boolean onlyRelated, boolean insideTab, ActionMode mode, Frame owner) {

		AtomTools.log(Level.FINER, "DomainObjectListWidget.Constructor started", this);

		representedSearchString = searchString;
		this.defaultFilters = defaultFilters;
		representedClass = theRepresentedClass;
		selectedMode = mode;
		this.insideTab = insideTab;
		this.owner = owner;

		ListStore<DomainObject> store = new ListStore<DomainObject>(new ModelKeyProvider<DomainObject>() {
			@Override
			public String getKey(DomainObject item) {
				return item.getObjectID().toString();
			}
		});

		if (preloadedList != null) {
			totalSize = preloadedList.getTotalSize();
			pageSize = preloadedList.getDomainObjects().size();
		}

		proxy = new MyRpcProxy(preloadedList, 0, representedClass, onlyRelated, representedSearchString, onlyScanStringRepresentation, getColumnNames(),
				new SettingsProvider() {

					@Override
					public int getTotalSize() {
						return totalSize;
					}

					@Override
					public int getPageSize() {
						return pageSize;
					}

					@Override
					public int getNewPageSize() {
						return newPageSize;
					}

					@Override
					public void setTotalSize(int totalSize) {
						DomainObjectListWidget.this.totalSize = totalSize;
					}

					@Override
					public boolean getForceRefresh() {
						if (DomainObjectListWidget.this.toolBar == null)
							return false;
						else
							return DomainObjectListWidget.this.toolBar.refreshing;
					}

					@Override
					public DataFilter[] getDefaultFilters() {
						return DomainObjectListWidget.this.defaultFilters;
					}
				}, this.owner);

		// final PagingLoader<FilterPagingLoadConfig,
		// PagingLoadResult<DomainObject>>
		loader = new PagingLoader<FilterPagingLoadConfig, PagingLoadResult<DomainObject>>(proxy) {
			@Override
			protected FilterPagingLoadConfig newLoadConfig() {
				FilterPagingLoadConfig fplc = new FilterPagingLoadConfigBean();
				this.setLimit(pageSize);
				fplc.setLimit(pageSize);
				return fplc;
			}
		};
		loader.addLoadHandler(new LoadResultListStoreBinding<FilterPagingLoadConfig, DomainObject, PagingLoadResult<DomainObject>>(store));
		//loader.setReuseLoadConfig(true);

		cbSelectionModel = new CheckBoxSelectionModel<DomainObject>(new IdentityValueProvider<DomainObject>());
		if (ActionMode.SINGLE_SELECT.equals(selectedMode)) {
			cbSelectionModel.setSelectionMode(SelectionMode.SINGLE);
		} else if (ActionMode.MULTI_SELECT.equals(selectedMode) || ActionMode.DEFAULT_OPEN.equals(selectedMode)) {
			cbSelectionModel.setSelectionMode(SelectionMode.MULTI);
		} else {
			AtomTools.log(Level.SEVERE, "unknown DomainObjectListWidget.ActionMode!", this);
		}

		ColumnModel<DomainObject> cm = new ColumnModel<DomainObject>(getColumnConfigs());

		contentPanel = new VerticalLayoutContainer();

		grid = new Grid<DomainObject>(store, cm);
		// {
		// @Override
		// protected void onAfterFirstAttach() {
		// super.onAfterFirstAttach();
		// Scheduler.get().scheduleDeferred(new ScheduledCommand() {
		// @Override
		// public void execute() {
		// loadTimer.run();
		// // loadTimer.schedule(1);
		// // loader.load();
		// }
		// });
		// }
		// };

		grid.setLoader(loader);
		grid.setSelectionModel(cbSelectionModel);
		gridFilters.initPlugin(grid);

		toolBar = new MyPagingToolBar(25);
		toolBar.bind(loader);

		contentPanel.add(grid, new VerticalLayoutData(1, 1));
		contentPanel.add(toolBar, new VerticalLayoutData(1, -1));

		// contentPanel = new ContentPanel(ContentPanelBaseAppearance.
		// contentPanel.setBodyBorder(true);
		// contentPanel.setHeaderVisible(false);
		// contentPanel.setHeading("");
		// contentPanel.setButtonAlign(BoxLayoutPack.CENTER);
		resize(Window.getClientHeight(), Window.getClientWidth(), 1, true);
		pageSize = newPageSize;

		// loader = new BasePagingLoader<PagingLoadResult<BaseModel>>(proxy) {
		// @Override
		// protected Object newLoadConfig() {
		// BasePagingLoadConfig config = new BaseFilterPagingLoadConfig();
		// AtomTools.log(Level.FINER, "creating LoadConfig; pageSize=" +
		// pageSize + "; newPageSize=" + newPageSize, this);
		// pageSize = newPageSize;
		// config.setLimit(pageSize);
		// return config;
		// }
		// };

		// grid.addListener(Events.Attach, new Listener<BaseEvent>() {
		// public void handleEvent(BaseEvent be) {
		// loader.load();
		//
		// // TODO: DISCUSS: Automatic reload list when it comes back to the top
		// of the screen?
		// // to disable automatic reload, uncomment the line below this one.
		// grid.removeListener(Events.Attach, this);
		// }
		// });

		grid.addCellDoubleClickHandler(new CellDoubleClickHandler() {

			@Override
			public void onCellClick(CellDoubleClickEvent event) {

				if (selectedMode == ActionMode.DEFAULT_OPEN) {
					proxy.openObject(event.getRowIndex());
					// } else if (selectedMode == ActionMode.SINGLE_SELECT) {
					// // TO DO: select the row that was clicked and deselect
					// all other rows
					// } else if (selectedMode == ActionMode.MULTI_SELECT) {
					// // TO DO: toggle the selection of the row that was
					// clicked
				} else {
					App.actionSave();
				}
			}
		});

		grid.getView().setForceFit(true);
		// grid.setStyleAttribute("borderTop", "none");
		// grid.setAutoExpandColumn("name");
		grid.setBorders(false);

		grid.getView().setStripeRows(true);
		grid.getView().setColumnLines(true);
		// grid.addPlugin(getGridFilters());
		// contentPanel.add(grid);

		// new MyGxtGridDragSource(grid);

		loader.setRemoteSort(true);

		// toolBar = new MyPagingToolBar(pageSize);

		toolBar.bind(loader);
		// contentPanel.add() .setBottomComponent(toolBar);

		this.add(contentPanel);

		String sortColumn = representedClass.getSortColumn();
		if (sortColumn != null && sortColumn.length() > 0) {
			DomainClassAttribute attr = representedClass.getAttributeNamed(sortColumn);
			if (attr != null) {
				defaultSort = new SortInfoBean(sortColumn, SortDir.ASC);
				grid.fireEvent(new SortChangeEvent(defaultSort));
			}
		}

		AtomTools.log(Level.FINER, "DomainObjectListWidget.Constructor finished", this);
	}

	private List<ColumnConfig<DomainObject, ?>> getColumnConfigs() {
		if (columnConfigs == null) {
			generateColumnConfigsNFilters();
		}
		return columnConfigs;
	}

	private HashSet<String> getColumnNames() {
		if (columnNames == null) {
			columnNames = new HashSet<String>();
			for (DomainClassAttribute oneAttribute : representedClass.getSortedAttributesListView()) {
				columnNames.add(oneAttribute.getDisplayName());
			}
		}
		return columnNames;
	}

	private void generateColumnConfigsNFilters() {
		columnConfigs = new ArrayList<ColumnConfig<DomainObject, ?>>();

		gridFilters = new GridFilters<DomainObject>(loader);

		if (selectedMode == ActionMode.SINGLE_SELECT || selectedMode == ActionMode.MULTI_SELECT) {
			// make a selection column here
			cbSelectionModel.getColumn().setHeader("");
			columnConfigs.add(cbSelectionModel.getColumn());
		}

		for (DomainClassAttribute oneAttribute : representedClass.getSortedAttributesListView()) {
			// if (oneAttribute.getAnnotation("HideFromListGui") == null) {

			// Workaround: since the GXT Grid seems to be mixed up if html tags
			// are inside the content, dont generate columns for formatted
			// attributes
//			if (oneAttribute.getAnnotation("StringFormattedLob") == null) {
				// String attributeName = oneAttribute.getName();
				// String attributeDisplayName = oneAttribute.getDisplayName();
				String attributeType = oneAttribute.getType();

				ColumnConfig<DomainObject, ?> genericColumn;

				// DomainObjectValueProvider<?> valueProvider;

				if ("java.lang.Long".equals(attributeType)) {
					DomainObjectValueProvider<Long> valueProvider = new DomainObjectValueProvider<Long>(representedClass, oneAttribute);
					ColumnConfig<DomainObject, Long> column = new ColumnConfig<DomainObject, Long>(valueProvider);
					genericColumn = column;
					if(!oneAttribute.isTransient()) {
						NumericFilter<DomainObject, Long> filter = new NumericFilter<DomainObject, Long>(valueProvider,
								new LongPropertyEditor());
						gridFilters.addFilter(filter);
					}
				} else if ("java.lang.Integer".equals(attributeType)) {
					DomainObjectValueProvider<Integer> valueProvider = new DomainObjectValueProvider<Integer>(representedClass,
							oneAttribute);
					ColumnConfig<DomainObject, Integer> column = new ColumnConfig<DomainObject, Integer>(valueProvider);
					genericColumn = column;
					if(!oneAttribute.isTransient()) {
						NumericFilter<DomainObject, Integer> filter = new NumericFilter<DomainObject, Integer>(valueProvider,
								new IntegerPropertyEditor());
						gridFilters.addFilter(filter);
					}
				} else if ("java.util.Date".equals(attributeType)) {

					DomainObjectValueProvider<Date> valueProvider = new DomainObjectValueProvider<Date>(representedClass, oneAttribute);
					ColumnConfig<DomainObject, Date> column = new ColumnConfig<DomainObject, Date>(valueProvider);
					column.setCell(new DateCell(DateTimeFormat.getFormat("dd.MM.yyyy")));
					genericColumn = column;

					if(!oneAttribute.isTransient()) {
						DateFilter<DomainObject> filter = new DateFilter<DomainObject>(valueProvider);
						gridFilters.addFilter(filter);
					}

					// DomainObjectValueProvider<String> valueProviderString =
					// new DomainObjectValueProvider<String>(representedClass,
					// oneAttribute, true);
					// DomainObjectValueProvider<Date> valueProviderDate = new
					// DomainObjectValueProvider<Date>(representedClass,
					// oneAttribute);
					// ColumnConfig<DomainObject, String> column = new
					// ColumnConfig<DomainObject, String>(valueProviderString);
					// genericColumn = column;
					// DateFilter<DomainObject> filter = new
					// DateFilter<DomainObject>(valueProviderDate);
					// gridFilters.addFilter(filter);

					// column.setDateTimeFormat(DateTimeFormat.getFormat("dd.MM.yyyy"));

					// DateFilter dateFilter = new DateFilter(attributeName);
					// gridFilters.addFilter(dateFilter);
					// } else if (attributeType.contains("boolean")) {
					// BooleanFilter booleanFilter = new
					// BooleanFilter(attributeName);
					// gridFilters.addFilter(booleanFilter);
				} else if ("java.lang.Boolean".equals(attributeType)) {
					DomainObjectValueProvider<Boolean> valueProvider = new DomainObjectValueProvider<Boolean>(representedClass,
							oneAttribute);
					ColumnConfig<DomainObject, Boolean> column = new ColumnConfig<DomainObject, Boolean>(valueProvider);
					genericColumn = column;
					if(!oneAttribute.isTransient()) {
						BooleanFilter<DomainObject> filter = new BooleanFilter<DomainObject>(valueProvider);
						gridFilters.addFilter(filter);
					}
				} else {
					if (!"java.lang.String".equals(attributeType))
						AtomTools.log(Level.WARNING, "attributeType nicht erkannt: '" + attributeType
								+ "' --> verwende toString()", this);

					DomainObjectValueProvider<String> valueProvider = new DomainObjectValueProvider<String>(representedClass,
							oneAttribute);
					ColumnConfig<DomainObject, String> column = new ColumnConfig<DomainObject, String>(valueProvider);
					genericColumn = column;
					
					//look for @ListBoxDefinition annotation and create specific filter for it.
					String[] listValues = oneAttribute.getListBoxDisplay();
//					final String[] listKeys = oneAttribute.getListBoxKeys();
					final LinkedHashMap<String, String> listbox = oneAttribute.getListBoxMappedReverse();
//					final String msSeperator = oneAttribute.getListBoxMSSeperator();
					if(listbox != null && listbox.size() > 0) {
						ListStore<String> store = new ListStore<String>(new ModelKeyProvider<String>() {

							@Override
							public String getKey(String item) {
								if(item == null) return null;
								
//								if(msSeperator != null && !"".equals(msSeperator) && item.contains(msSeperator)) {
//									AtomTools.log(Level.WARNING, "this has not yet been implemented", this);
//									String[] parts = item.split(msSeperator);
//									String ret = null;
//									for(String part : parts) {
//										String key = listbox.get(part);
//										if(key != null && key.length()>0) {
//											if(ret == null)
//												ret = key;
//											else
//												ret = ret + msSeperator + key;
//										}
//									}
//									return ret;
//								}
//								else
									return listbox.get(item);
							}
						});
//						if(msSeperator != null && !"".equals(msSeperator)) {
//							HashSet<String> combinations = AtomTools.getCartesianProduct(listValues, msSeperator);
//							listValues = combinations.toArray(new String[combinations.size()]);
//							Arrays.sort(listValues);
//						}
						for(String s : listValues) {
							store.add(s);
						}

						if(!oneAttribute.isTransient()) {
							ListFilter<DomainObject, String> filter = new ListFilter<DomainObject, String>(valueProvider, store);
							gridFilters.addFilter(filter);
						}
					}
					else {
						if(!oneAttribute.isTransient()) {
							StringFilter<DomainObject> filter = new StringFilter<DomainObject>(valueProvider);
							gridFilters.addFilter(filter);
						}
					}
					
					// StringFilter stringFilter = new
					// StringFilter(attributeName);
					// gridFilters.addFilter(stringFilter);
				}

				genericColumn.setHeader(oneAttribute.getDisplayName());
				genericColumn.setFixed(false);
				columnConfigs.add(genericColumn);

				if (new Boolean(true).equals(oneAttribute.getHideFromListGui()))
					genericColumn.setHidden(true);

//			}
		}
	}

	public void resizeSingle(ResizeEvent event) {
		if (event != null) {
			resize(event.getHeight(), event.getWidth(), 1, true);
		}
		else {
			resize(Window.getClientHeight(), Window.getClientWidth(), 1, true);
		}	
	}

	public void resize(ResizeEvent event) {
		if (event != null) {
			resize(event.getHeight(), event.getWidth(), 1000, true);
		}
		else {
			resize(Window.getClientHeight(), Window.getClientWidth(), 1000, true);
		}	
	}

	public void resize(int height, int width) {
		resize(height, width, 1000, false);
	}

	private Timer loadTimer = new Timer() {

		@Override
		public void run() {
			// AtomTools.log(Level.FINER,
			// "DomainObjectListWidget.LoadTimer - started - loading data; pageSize="
			// + pageSize
			// + "; newPageSize=" + newPageSize + "; totalSize=" + totalSize,
			// this);
			if (firstLoad) {
				firstLoad = false;

				FilterPagingLoadConfig config = new FilterPagingLoadConfigBean();
				config.setOffset(0);
				config.setLimit(pageSize);

				if (defaultSort != null) {
					ArrayList<SortInfo> sortList = new ArrayList<SortInfo>();
					sortList.add(defaultSort);
					config.setSortInfo(sortList);
					grid.fireEvent(new SortChangeEvent(defaultSort));
				}

				// if (state.containsKey("sortField")) {
				// config.setSortField((String)state.get("sortField"));
				// config.setSortDir(SortDir.valueOf((String)state.get("sortDir")));
				// }
				loader.load(config);
				toolBar.setPageSize(pageSize);
				//loader.load(0, pageSize);
				lastLoadAction = new Date();
			}
			
			if (contentPanel != null) {
				AtomTools.log(Level.FINER, "gxt table bugfix: set real size here in the delayed load timer", this);
				contentPanel.setPixelSize(currentWidth, currentHeight);
			}
			
			if (newPageSize != pageSize && !(pageSize >= totalSize && newPageSize >= totalSize)) {
				// calculate offset for new page size (start with same offset,
				// unless -->
				int oldPage = toolBar.getActivePage();
				if (oldPage == -1)
					oldPage = 1;

				int currentOffset = (oldPage - 1) * pageSize;

				// if we are on the last page, stay on last page, even if we
				// will have more smaller pages? TODO: Discuss
				if (currentOffset >= totalSize - pageSize)
					currentOffset = Math.max(0, totalSize - newPageSize);

				pageSize = newPageSize;
				toolBar.setPageSize(pageSize);
				loader.setLimit(pageSize);
				loader.load(currentOffset, pageSize);
				lastLoadAction = new Date();
			}
			// if (contentPanel != null)
			// contentPanel.setSize(lastWidth, lastHeight);
			// this.schedule(1000);
			// AtomTools.log(Level.FINER,
			// "DomainObjectListWidget.LoadTimer - finished method - loading data; pageSize="
			// + pageSize + "; newPageSize=" + newPageSize + "; totalSize=" +
			// totalSize, this);
		}
	};

	public static int getRowsFitting(int windowHeight, boolean withinTab) {
		int heightForRows;
		if (withinTab) {
			heightForRows = windowHeight - subtractedHeightForTabs;
		} else {
			heightForRows = windowHeight - subtractedHeight;
		}
		return (int) Math.floor((heightForRows - (33 + 28)) / 22);
	}

	private boolean firstResize = true;
	private int currentWidth, currentHeight;

	private void resize(int windowHeight, int windowWidth, int timeOut, boolean subtraction) {

		int newWidth = App.getFrameWidth();
		int newHeight = 0;
		if (subtraction) {
			if (insideTab) {
				newWidth -= subtractedWidthForTabs;
				newHeight = windowHeight - subtractedHeightForTabs;
				// if (firstResize) {
				// firstResize = false;
				// newHeight -= 27;
				// }
			} else {
				newWidth -= subtractedWidth;
				newHeight = windowHeight - subtractedHeight;
			}
		} else {
			newWidth = windowWidth;
			newHeight = windowHeight;
		}

		AtomTools.log(Level.FINER, "resizing to Width=" + newWidth + "; newHeight=" + newHeight + "(w" + subtractedWidth
				+ ", h" + subtractedHeight + "; wt" + subtractedWidthForTabs + ", ht" + subtractedHeightForTabs + "; " + firstResize
				+ ")", this);

		if (contentPanel != null) {
			//gxt table bugfix: first set size one pixel smaller, then in the delayed timer run set real size
			AtomTools.log(Level.FINER, "gxt table bugfix: first set size one pixel smaller", this);
			contentPanel.setPixelSize(newWidth-(timeOut<2?1:0), newHeight);
		}

		// columnHeaders = 22, toolbar bottom = 32, height of one row = 22
		int heightForLines = newHeight - (32 + 22);
		newPageSize = (int) Math.floor(heightForLines / 22);

		// AtomTools.log(Level.FINER, "setting newPageSize=" +
		// newPageSize, this);

		currentHeight = newHeight;
		currentWidth = newWidth;

		if (toolBar != null) {
			// AtomTools.log(Level.FINER,
			// "canceling and rescheduling loadTimer", this);
			loadTimer.cancel();
			loadTimer.schedule(timeOut);
		}
	}

	private boolean registered = false;

	public void goingVisible() {
		if (!registered) {
			// grid.getView().getBody().
			App.registerDndWidget(this);
			registered = true;
		}
//		loadTimer.schedule(10);
//		grid.getView().refresh(true);
		
		Date lastClassChange = RPCCaller.getSinglton().getClassChangeDate(DomainObjectListWidget.this.representedClass.getName());
		if(lastClassChange != null && lastLoadAction.before(lastClassChange)) {
			toolBar.refresh();
			lastLoadAction = new Date();
		}	
	}

	public void goingInvisible() {
		if (registered) {
			App.unRegisterDndWidget(this);
			registered = false;
		}
	}

	public Widget getDndProxy(int x, int y) {
		AtomTools.log(Level.FINER, "DomainObjectListWidget getDndProxy", this);
		return new Label(AtomTools.domainObjectsString(getRepresentedDomainObjects(), "nichts ausgewählt"));
	}

	public List<DomainObject> getRepresentedDomainObjects() {
		return grid.getSelectionModel().getSelectedItems();
	}

	public DomainObject getRepresentedDomainObject() {
		if (grid != null && grid.getSelectionModel() != null && 
				grid.getSelectionModel().getSelectedItems() != null && 
				grid.getSelectionModel().getSelectedItems().size() > 0)
			return grid.getSelectionModel().getSelectedItems().get(0);
		else
			return null;
	}

	public void previewDragStart(int x, int y) throws VetoDragException {
		if (y < 23 || grid == null || grid.getSelectionModel() == null || grid.getSelectionModel().getSelectedItems() == null
				|| grid.getSelectionModel().getSelectedItems().size() == 0 || y > this.getOffsetHeight() - 28) {
			throw new VetoDragException();
		}
	}
}