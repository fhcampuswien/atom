/* ATOM - Advanced Transparent Object Manager
 * Copyright © Vienna, Austria 2014 by FH Campus Wien 
 * Some rights reserved. See COPYING, AUTHORS.
 */
package at.ac.fhcampuswien.atom.client.gui.gxt;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashSet;
import java.util.Iterator;
import java.util.List;
import java.util.logging.Level;

import com.google.gwt.user.client.rpc.AsyncCallback;
import com.sencha.gxt.data.client.loader.RpcProxy;
import com.sencha.gxt.data.shared.SortDir;
import com.sencha.gxt.data.shared.SortInfo;
import com.sencha.gxt.data.shared.loader.FilterConfig;
import com.sencha.gxt.data.shared.loader.FilterPagingLoadConfig;
import com.sencha.gxt.data.shared.loader.PagingLoadResult;

import at.ac.fhcampuswien.atom.client.App;
import at.ac.fhcampuswien.atom.client.gui.frames.ExternalLoaderFrame;
import at.ac.fhcampuswien.atom.client.rpc.RPCCaller;
import at.ac.fhcampuswien.atom.shared.AtomTools;
import at.ac.fhcampuswien.atom.shared.DataFilter;
import at.ac.fhcampuswien.atom.shared.DataSorter;
import at.ac.fhcampuswien.atom.shared.DomainClass;
import at.ac.fhcampuswien.atom.shared.DomainObjectList;
import at.ac.fhcampuswien.atom.shared.domain.DomainObject;

public class MyRpcProxy extends RpcProxy<FilterPagingLoadConfig, PagingLoadResult<DomainObject>> {

	private ArrayList<DataFilter> previousFilters = new ArrayList<DataFilter>();
	private ArrayList<DataFilter> dataFilters = new ArrayList<DataFilter>();
	private ArrayList<DataSorter> dataSorters = new ArrayList<DataSorter>();
	
	private DomainObjectList listToUse = null;
	private DomainObjectList lastUsedList = null;
	private int offsetPreloadedList = -1;
	private String searchString = null;
	private boolean onlyScanStringRepresentation;
	
	private ExternalLoaderFrame owner;
	
	private DomainClass representedClass;
	private boolean onlyRelated;
	private boolean onlyWriteables;

	public interface SettingsProvider {
		public int getPageSize();
		public int getTotalSize();
		public void setTotalSize(int totalSize);
		public int getNewPageSize();
		public boolean getForceRefresh();
		public DataFilter[] getDefaultFilters();
	}
	
	private SettingsProvider settingsProvider;
	
	public MyRpcProxy(DomainObjectList preloadedList, int offset, DomainClass representedClass, boolean onlyRelated, boolean onlyWriteables, String searchString, boolean onlyScanStringRepresentation, HashSet<String> columnNames, SettingsProvider sizeProvider, ExternalLoaderFrame owner) {

		this.listToUse = preloadedList;
		this.offsetPreloadedList = offset;

		this.representedClass = representedClass;
		this.onlyRelated = onlyRelated;
		this.onlyWriteables = onlyWriteables;
		this.searchString = searchString;
		this.onlyScanStringRepresentation = onlyScanStringRepresentation;
		this.settingsProvider = sizeProvider;
		
		this.owner = owner;
		
		DataFilter[] filtersGot = settingsProvider.getDefaultFilters();
		if(filtersGot != null && filtersGot.length > 0)
			previousFilters.addAll(Arrays.asList(filtersGot));	
	}

	@Override
	public void load(FilterPagingLoadConfig loadConfig, final AsyncCallback<PagingLoadResult<DomainObject>> callback) {
		AtomTools.log(Level.FINER, "DomainObjectListWidget.MyRpcProxy.load starting", this);

		final FilterPagingLoadConfig config = loadConfig;

		if (listToUse != null) {
			lastUsedList = listToUse;
			callback.onSuccess(new DomainObjectPagingLoadResult(listToUse.getDomainObjects(), offsetPreloadedList, settingsProvider.getTotalSize()));
//			callback.onSuccess(transformResult(listToUse, offsetPreloadedList));
			settingsProvider.setTotalSize(listToUse.getTotalSize());
			listToUse = null;
		} else {

			/*********************** Handle Sorting ***********************/
			List<? extends SortInfo> sortInfos = config.getSortInfo();
			for(SortInfo sortInfo : sortInfos) {
				SortDir sortDir = sortInfo.getSortDir();
				String sortField = sortInfo.getSortField();
				
				boolean sortOrder = true;
				boolean validSorter = true;
				
				if (sortDir == SortDir.ASC)
					sortOrder = true;
				else if (sortDir == SortDir.DESC)
					sortOrder = false;
				else {
					validSorter = false;
				}
				
				if(validSorter) {
					DataSorter sorter = null;
					for (DataSorter aSorter : dataSorters) {
						if (aSorter.getColumn().equals(sortField))
							sorter = aSorter;
					}
					if (sorter != null) {
						dataSorters.remove(sorter);
					}
					sorter = new DataSorter(sortField, sortOrder);
					dataSorters.add(sorter);
				}
			}
			

			/*********************** Handle Filters ***********************/
			List<FilterConfig> filterConfigs = config.getFilters();
			dataFilters.clear();
			HashSet<String> newFilterFields = new HashSet<String>();
			if (filterConfigs != null) {
				for (FilterConfig filterConfig : filterConfigs) {
					String searchedValue = filterConfig.getValue();
					if(searchedValue.contains("::")) {
						//syntax with which Sencha Grid joins multiple selected values
						for(String val : searchedValue.split("::")) {
							dataFilters.add(new DataFilter(filterConfig.getField(), val, filterConfig.getComparison(), filterConfig.getType()));
						}
					}
					else
						dataFilters.add(new DataFilter(filterConfig.getField(), searchedValue, filterConfig.getComparison(), filterConfig.getType()));
					newFilterFields.add(filterConfig.getField());
				}
			}
			if(previousFilters != null) {
				
				Iterator<DataFilter> iter = previousFilters.iterator();
				while (iter.hasNext()) {
				    if (newFilterFields.contains(iter.next().getColumn())) {
				        iter.remove();
				    }
				}
				
				dataFilters.addAll(previousFilters);
//				for(Map.Entry<String, Object> e : defaultFilters.entrySet()) {
//					dataFilters.add(new DataFilter(e.getKey(), e.getValue().toString(), null, null));
//				}
			}
			owner.setDataFilters(dataFilters.toArray(new DataFilter[dataFilters.size()]));

			/*********************** load DomainObjects ***********************/

			AtomTools.log(Level.FINER, "DomainObjectListWidget.MyRpcProxy.load calling server for data", this);
			owner.loading(true);
			App.setLoadingState(true, owner);
			RPCCaller.getSinglton().loadListOfDomainObjects(representedClass, dataFilters, dataSorters, config.getOffset(), config.getLimit(), false,
					searchString, onlyScanStringRepresentation, onlyRelated, onlyWriteables, settingsProvider.getForceRefresh(), new AsyncCallback<DomainObjectList>() {

						public void onFailure(Throwable caught) {
							AtomTools.log(Level.SEVERE, "DomainObjectListWidget.MyRpcProxy.load could not load data from server", this);
//							grid.mask(caught.getMessage());
							// grid.set
						}

						public void onSuccess(DomainObjectList result) {
							AtomTools.log(Level.FINER, "DomainObjectListWidget.MyRpcProxy.load server returned data", this);
							settingsProvider.setTotalSize(result.getTotalSize());
							lastUsedList = result;
							callback.onSuccess(new DomainObjectPagingLoadResult(result.getDomainObjects(), result.getFromRow(), settingsProvider.getTotalSize()));
							owner.loading(false);
							App.setLoadingState(false, owner);
						}
					});
//			toolBar.enableEvents(true);
		}

		AtomTools.log(Level.FINER, "DomainObjectListWidget.MyRpcProxy.load finished", this);
	}

	protected void openObject(int number) {
		App.openDetailView(lastUsedList.getDomainObjects().get(number), null, false);
	}

}
