/* ATOM - Advanced Transparent Object Manager
 * Copyright © Vienna, Austria 2014 by FH Campus Wien 
 * Some rights reserved. See COPYING, AUTHORS.
 */
package at.ac.fhcampuswien.atom.client.gui.dnd;

import java.util.List;

import at.ac.fhcampuswien.atom.shared.domain.DomainObject;

import com.allen_sauer.gwt.dnd.client.VetoDragException;
import com.google.gwt.user.client.ui.Widget;

public interface AtomDNDWidget {
    public Widget getDndProxy(int x, int y);
    public List<DomainObject> getRepresentedDomainObjects();
	public DomainObject getRepresentedDomainObject();
    public void previewDragStart(int x, int y) throws VetoDragException;
}
