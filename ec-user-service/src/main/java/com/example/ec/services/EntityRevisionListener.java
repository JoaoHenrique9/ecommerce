package com.example.ec.services;

import org.hibernate.envers.RevisionListener;

public class EntityRevisionListener implements RevisionListener {

	@Override
	public void newRevision(Object revisionEntity) {
		// Revision rev = (Revision)revisionEntity;
		// rev.setUser();

	}
}
