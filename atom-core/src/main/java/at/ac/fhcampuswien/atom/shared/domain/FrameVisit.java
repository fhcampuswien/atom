/* ATOM - Advanced Transparent Object Manager
 * Copyright © Vienna, Austria 2014 by FH Campus Wien 
 * Some rights reserved. See COPYING, AUTHORS.
 */
package at.ac.fhcampuswien.atom.shared.domain;

import java.io.Serializable;
import java.util.Arrays;
import java.util.Date;
import java.util.logging.Level;

import javax.persistence.CascadeType;
import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.Enumerated;
import javax.persistence.FetchType;
import javax.persistence.ManyToOne;

import org.hibernate.annotations.Cascade;
import org.hibernate.annotations.OnDelete;
import org.hibernate.annotations.OnDeleteAction;

import at.ac.fhcampuswien.atom.shared.AtomConfig;
import at.ac.fhcampuswien.atom.shared.AtomConfig.FrameType;
import at.ac.fhcampuswien.atom.shared.AtomTools;
import at.ac.fhcampuswien.atom.shared.ClientSession;
import at.ac.fhcampuswien.atom.shared.DataFilter;
import at.ac.fhcampuswien.atom.shared.DomainClass;
import at.ac.fhcampuswien.atom.shared.annotations.AccessListRoles;
import at.ac.fhcampuswien.atom.shared.annotations.AccessLists;
import at.ac.fhcampuswien.atom.shared.annotations.AnalyzerIgnore;
import at.ac.fhcampuswien.atom.shared.annotations.AttributeLoadingPolicy;
import at.ac.fhcampuswien.atom.shared.annotations.ClassNamePlural;
import at.ac.fhcampuswien.atom.shared.annotations.HideFromGui;
import at.ac.fhcampuswien.atom.shared.annotations.RelationDefinition;
import at.ac.fhcampuswien.atom.shared.annotations.Searchable;
import at.ac.fhcampuswien.atom.shared.annotations.SortColumn;
import at.ac.fhcampuswien.atom.shared.exceptions.ValidationError;

@Entity
@ClassNamePlural("FrameVisits")
@HideFromGui
@AccessLists(rolesLists= {
		@AccessListRoles(value = { "*" }, accessTypes = { AtomConfig.accessCreateNew }),
		@AccessListRoles(value = { "*" }, accessTypes = { AtomConfig.accessReadWrite }, requiredRelations = RelationDefinition.defaultRelation)
})
@RelationDefinition(where = "p.objectID = {$objectID} OR obj.creationUser.objectID = {$objectID} OR obj.updateUser.objectID = {$objectID}", distinct = true, joins = "LEFT OUTER JOIN obj.visitor p")
@SortColumn("lastVisit DESC")
@Searchable(false)
//@Table(uniqueConstraints = @UniqueConstraint(columnNames = {"visitor_objectID", "representedInstance_objectID", "nameOfRepresentedClass", "representedSearchString", "frameType"}))
/*

ALTER TABLE FrameVisit
ADD CONSTRAINT visitor_unique UNIQUE (visitor_objectID, representedInstance_objectID, nameOfRepresentedClass, representedSearchString, frameType);

SELECT f.* FROM FrameVisit f
WHERE EXISTS(
  SELECT objectID FROM FrameVisit a
  WHERE (a.frameType = f.frameType OR (a.frameType = NULL AND f.frameType = NULL))
    AND (a.nameOfRepresentedClass = f.nameOfRepresentedClass OR (a.nameOfRepresentedClass IS NULL AND f.nameOfRepresentedClass IS NULL))
    AND (a.representedSearchString = f.representedSearchString OR (a.representedSearchString IS NULL AND f.representedSearchString IS NULL))
    AND (a.representedInstance_objectID = f.representedInstance_objectID OR (a.representedInstance_objectID IS NULL AND f.representedInstance_objectID IS NULL))
    AND (a.visitor_objectID = f.visitor_objectID OR (a.visitor_objectID IS NULL AND f.visitor_objectID IS NULL))
    AND a.objectID <> f.objectID
)

ALTER TRIGGER [dbo].[prevent_duplicate_FrameVisits]
   ON  [Atom].[dbo].[FrameVisit]
   AFTER INSERT, UPDATE
AS 
BEGIN
	-- SET NOCOUNT ON added to prevent extra result sets from
	-- interfering with SELECT statements.
	SET NOCOUNT ON;

    -- Insert statements for trigger here
	IF EXISTS(
		SELECT 1
		FROM INSERTED i
		JOIN Atom.dbo.FrameVisit v
		ON i.[visitor_objectID] = v.[visitor_objectID]
		AND i.[representedInstance_objectID] = v.[representedInstance_objectID]
		AND i.[nameOfRepresentedClass] = v.[nameOfRepresentedClass]
		AND i.[representedSearchString] = v.[representedSearchString]
		AND i.[frameType] = v.[frameType]
		AND i.[filters] = v.[filters]
	)
	BEGIN
		RAISERROR ('Unique constraint violation - Another FrameVisit with those properties already exists!', 16, 1)
		ROLLBACK TRAN;
	END
END

 
DELETE FROM FrameVisit
WHERE objectID IN (38717, 38828, 38836, 38883, 38884, 38954, 38960, 38961, 40493, 40497, 40498, 40502, 40504);
DELETE FROM FeaturedObject
WHERE objectID IN (38717, 38828, 38836, 38883, 38884, 38954, 38960, 38961, 40493, 40497, 40498, 40502, 40504);
DELETE FROM DomainObject
WHERE objectID IN (38717, 38828, 38836, 38883, 38884, 38954, 38960, 38961, 40493, 40497, 40498, 40502, 40504);

exec master..sp_WhoIsActive

*/
public class FrameVisit extends FeaturedObject implements Serializable {

	@AnalyzerIgnore
	private static final long serialVersionUID = -11009381736405542L;

	//cascade = { CascadeType.ALL }, 
	@ManyToOne(fetch = FetchType.EAGER, cascade = CascadeType.DETACH)
	@AttributeLoadingPolicy(requiredForStringRepresentation=true, whenNotPrimary=true, withLists=true)
	@OnDelete(action = OnDeleteAction.NO_ACTION)
	private StoreableUser visitor;

	@Enumerated
	@Column(nullable = false)
	private FrameType frameType;

	//cascade = { CascadeType.ALL }, 
	@ManyToOne(fetch = FetchType.EAGER, cascade = CascadeType.DETACH, optional = true)
//	@OnDelete(action = OnDeleteAction.CASCADE)
	@OnDelete(action = OnDeleteAction.NO_ACTION)
	@Cascade(value = {org.hibernate.annotations.CascadeType.DETACH})
	private DomainObject representedInstance;

	private transient DomainClass representedClass;

	private String nameOfRepresentedClass;

	// @Transient @GwtTransient
	// private transient Frame frame;

	@Column(length=1000)
	private String frameShortTitle;

	@Column(columnDefinition = "varbinary(3000)")
	private DataFilter[] filters;

	private String representedSearchString;
	private Boolean simpleSearch;

	private Date lastVisit;

	// @AnalyzerIgnore @Transient @GwtTransient
	// private transient boolean createdLabel = false;

	/**
	 * this should not be used, it only exists for GWT serialization purposes!
	 */
	@SuppressWarnings("unused")
	@Deprecated()
	private FrameVisit() {
	}
	
	public FrameVisit(StoreableUser visitor, FrameType frameType, DomainObject representedInstance, DomainClass representedClass,
			String representedSearchString, Boolean isSimpleSearch, DataFilter[] filters, String frameShortTitle) {
		this(visitor, frameType, representedInstance, representedClass, representedSearchString, isSimpleSearch, frameShortTitle);
		this.filters = filters;
	}

	public FrameVisit(StoreableUser visitor, FrameType frameType, DomainObject representedInstance, DomainClass representedClass,
			String representedSearchString, Boolean isSimpleSearch, String frameShortTitle) {
		this(visitor, frameType, representedInstance, (representedClass == null ? null : representedClass.getName()),
				representedSearchString, isSimpleSearch, frameShortTitle);
		this.representedClass = representedClass;
	}

	public FrameVisit(StoreableUser visitor, FrameType frameType, DomainObject representedInstance, String nameOfRepresentedClass,
			String representedSearchString, Boolean isSimpleSearch, String frameShortTitle) {
		this.visitor = visitor;
		this.frameType = frameType;
		this.representedInstance = representedInstance;
		this.nameOfRepresentedClass = nameOfRepresentedClass;
		this.representedSearchString = representedSearchString;
		this.simpleSearch = isSimpleSearch;
		this.frameShortTitle = frameShortTitle;
		this.lastVisit = new Date();
	}

	public StoreableUser getVisitor() {
		return visitor;
	}

	public DomainObject getRepresentedInstance() {
		return representedInstance;
	}

	public DomainClass getRepresentedClass() {
		return representedClass;
	}

	public String getNameOfRepresentedClass() {
		return nameOfRepresentedClass;
	}

	public String getRepresentedSearchString() {
		return representedSearchString;
	}

	public Boolean getSimpleSearch() {
		return simpleSearch;
	}

	public Date getLastVisit() {
		return lastVisit;
	}

	public void setLastVisit(Date lastVisit) {
		this.lastVisit = lastVisit;
	}

	public String getFrameShortTitle() {
		return frameShortTitle;
	}

	public void setFrameShortTitle(String frameShortTitle) {
		this.frameShortTitle = frameShortTitle;
	}

	// public Frame getFrame() {
	// return frame;
	// }
	//
	// public void setFrame(Frame frame) {
	// this.frame = frame;
	// }

	public DataFilter[] getFilters() {
		return filters;
	}

	public FrameType getFrameType() {
		if(frameType == null)
			AtomTools.log(Level.SEVERE, "FrameType must never be null!", this);
		return frameType;
	}

	/**
	 * Need to be able to set fields of type DomainObject on the server
	 * 
	 * @param representedInstance
	 */
	public void setRepresentedInstance(DomainObject representedInstance) {
		this.representedInstance = representedInstance;
	}

	/**
	 * Need to be able to set fields of type DomainObject on the server
	 * 
	 * @param visitor
	 */
	public void setVisitor(StoreableUser visitor) {
		this.visitor = visitor;
	}

	@Override
	public boolean isUserRelated(ClientSession session) {
		// if (super.isUserRelated(session))
		// return true;
		// else
		return (visitor != null && visitor.equals(session.getUser())) || (session.getUser() == null && visitor == null);
	}

	@Override
	public boolean equals(Object obj) {
		if (obj == null || !(obj instanceof FrameVisit))
			return false;
		else {
			return this.hashCode() == obj.hashCode();
		}
	}

	public int getFrameHashCode() {
		if(representedClass == null && nameOfRepresentedClass != null && nameOfRepresentedClass.length() > 0)
			AtomTools.log(Level.WARNING, "cannot calculate frameHashCode since we are missing the representedClass!", this);
		return AtomTools.getFrameHashCode(representedClass, representedInstance, representedSearchString, frameType, filters);
	}
	
	@Override
	public int hashCode() {
		int value = 7 * (nameOfRepresentedClass == null ? 0 : nameOfRepresentedClass.hashCode())
				 + 11 * ((representedInstance == null || representedInstance.getObjectID() == null) ? 0 : representedInstance.getObjectID())
				 + 17 * (representedSearchString == null ? 0 : representedSearchString.hashCode())
				 + 31 * (frameType == null ? 0 : frameType.toString().hashCode())
				 + 59 * (visitor == null || visitor.getObjectID() == null  ? 0 : visitor.getObjectID())
				 ;
		

		 //+ 71 * (filters == null ? 0 : filters.hashCode())
		
		if(filters != null && filters.length > 0)
			for(DataFilter f : filters) {
				value += 71 * f.hashCode();
			}
		
		// AtomTools.log(Level.INFO, "return hashCode = " + value +
		// " for frame " + this.shortTitle, this);
		return value;
	}

	@Override
	public String toString() {
		return "FrameVisit " + frameShortTitle + "(" + String.valueOf(getObjectID()) + ")";
	}

	@Override
	public void prepareSave(ClientSession session) {
		if (!isUserRelated(session)) {
			throw new ValidationError("You can only save your own ClipBoardEntry instances! (owner==you)");
		}
		super.prepareSave(session);
	}

	// public void labelHasBeenCreated() {
	// createdLabel = true;
	// }
	//
	// public boolean hasLabelBeenCreated() {
	// return createdLabel;
	// }

	@Override
	public void prepareForClient() {
		super.prepareForClient();
		
		if(FrameType.DETAIL_VIEW.equals(getFrameType())) {
			if(getRepresentedInstance() != null)
				setFrameShortTitle(getRepresentedInstance().getStringRepresentation());
		}
//		else if(FrameType.SEARCH.equals(getFrameType())) {
//			
//		}
//		
//		if(getRepresentedInstance() != null || (getRepresentedSearchString() != null && !"".equals(getRepresentedSearchString())) || (getNameOfRepresentedClass() == null || "".equals(getNameOfRepresentedClass())))
//			setFrameShortTitle(AtomTools.getShortFrameTitle(getFrameType(), getRepresentedInstance(), getRepresentedSearchString()));
	}
	
	public String getHistoryString() {
		if (AtomConfig.FrameType.LIST_ALL.equals(getFrameType())) {
			return getFrameType().toString() + "_" + getNameOfRepresentedClass();
		} else if (AtomConfig.FrameType.LIST_RELATED.equals(getFrameType())) {
			return getFrameType().toString() + "_" + getNameOfRepresentedClass();
		} else if (AtomConfig.FrameType.DETAIL_VIEW.equals(getFrameType())) {
			DomainObject object = getRepresentedInstance();
			if (object == null || object.getObjectID() == null) {
				return getFrameType().toString() + "_new_" + getNameOfRepresentedClass();
			} else {
				return getFrameType().toString() + "_" + object.getObjectID() + "_" + getNameOfRepresentedClass();
				// FIXME + "_edit_" + ((DomainObjectDetailFrame) frame).getIsEditable();
			}
		} else if (AtomConfig.FrameType.SEARCH.equals(getFrameType()) || AtomConfig.FrameType.SEARCH_SIMPLE.equals(getFrameType())) {
			return getFrameType().toString() + "_" + getRepresentedSearchString();
		} else if (AtomConfig.FrameType.SEARCHCLASS.equals(getFrameType()) || AtomConfig.FrameType.SEARCHCLASS_SIMPLE.equals(getFrameType())) {
			return getFrameType().toString() + "_" + getNameOfRepresentedClass() + "_" + getRepresentedSearchString();
		} else if (AtomConfig.FrameType.FILTERCLASS.equals(getFrameType())) {
			String val = getFrameType().toString() + "_" + getNameOfRepresentedClass();
			val += "_" + AtomTools.getFilterString(filters == null ? null : Arrays.asList(filters));
			return val;
		} else if (AtomConfig.FrameType.IMPORT.equals(getFrameType())) {
			return getFrameType().toString() + "_" + getNameOfRepresentedClass();
		}
		else if (getFrameType() == null)
			return "FrameType is null?!";
		else
			return getFrameType().toString();
	}
}


/*
 * 

## manually modified constraint to allow deletion of instances with FrameVisits

ALTER TABLE [dbo].[FrameVisit] DROP CONSTRAINT [FK_opdj3fq3yhyobmcc6208x2lbu]
GO

ALTER TABLE [dbo].[FrameVisit]  WITH CHECK ADD  CONSTRAINT [FK_opdj3fq3yhyobmcc6208x2lbu] FOREIGN KEY([representedInstance_objectID])
REFERENCES [dbo].[DomainObject] ([objectID])
ON DELETE CASCADE
GO

ALTER TABLE [dbo].[FrameVisit] CHECK CONSTRAINT [FK_opdj3fq3yhyobmcc6208x2lbu]
GO

 * 
 */