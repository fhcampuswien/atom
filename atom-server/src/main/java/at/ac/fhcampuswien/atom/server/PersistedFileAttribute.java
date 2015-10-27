/* ATOM - Advanced Transparent Object Manager
 * Copyright © Vienna, Austria 2014 by FH Campus Wien 
 * Some rights reserved. See COPYING, AUTHORS.
 */
package at.ac.fhcampuswien.atom.server;

import java.sql.Blob;
import java.util.Date;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.FetchType;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.Lob;
import javax.persistence.ManyToOne;

import org.hibernate.annotations.OnDelete;
import org.hibernate.annotations.OnDeleteAction;

import at.ac.fhcampuswien.atom.shared.domain.DomainObject;
import at.ac.fhcampuswien.atom.shared.domain.StoreableUser;

@Entity
public class PersistedFileAttribute {

	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	private Integer id;
	
	@Lob
	@Column(columnDefinition = "varbinary(max)")
	private Blob content;
	
	private String fileName;
	
	private String contentType;

	private String forClassName;
	private String forAttributeName;
	
	@ManyToOne(fetch = FetchType.EAGER)
	@OnDelete(action = OnDeleteAction.CASCADE)
	private DomainObject forInstance;

	@ManyToOne(fetch = FetchType.EAGER)
	@OnDelete(action = OnDeleteAction.NO_ACTION)
	private StoreableUser uploader;

	private Date uploadDate;
	
	private boolean forInstanceSaved = false;
	
	public PersistedFileAttribute(Blob content, String fileName, String contentType, String forClassName, String forAttributeName, DomainObject forInstance, StoreableUser uploader) {
		this.content = content;
		this.fileName = fileName;
		this.contentType = contentType;
		this.forClassName = forClassName;
		this.forAttributeName = forAttributeName;
		this.forInstance = forInstance;
		this.uploader = uploader;
		uploadDate = new Date();
		forInstanceSaved = false;
	}
	
	public Integer getId() {
		return id;
	}

	public Blob getContent() {
		return content;
	}

	public String getFileName() {
		return fileName;
	}

	public String getContentType() {
		return contentType;
	}

	public String getForClassName() {
		return forClassName;
	}

	public String getForAttributeName() {
		return forAttributeName;
	}

	public DomainObject getForInstance() {
		return forInstance;
	}

	public void setForInstance(DomainObject forInstance) {
		this.forInstance = forInstance;
	}

	public StoreableUser getUploader() {
		return uploader;
	}
	
	public Date getUploadDate() {
		return uploadDate;
	}

	/**
	 * useless. only for hibernate
	 */
	PersistedFileAttribute() {
		uploadDate = new Date();
	}

	public boolean isForInstanceSaved() {
		return forInstanceSaved;
	}
	
	public void setForInstanceSaved(boolean forInstanceSaved) {
		this.forInstanceSaved = forInstanceSaved;
	}
}


/*
 *
 * Manually modified constraint - added ON DELETE CASCADE
 * so that an associated file does not block the deletion of an enitity 
 * 

ALTER TABLE [dbo].[PersistedFileAttribute] DROP CONSTRAINT [FK_a47ifik54mr449um3rsh3mflb]
GO

ALTER TABLE [dbo].[PersistedFileAttribute]  WITH CHECK
 ADD CONSTRAINT [FK_a47ifik54mr449um3rsh3mflb] FOREIGN KEY([forInstance_objectID])
 REFERENCES [dbo].[DomainObject] ([objectID])
 ON DELETE CASCADE
GO
*/