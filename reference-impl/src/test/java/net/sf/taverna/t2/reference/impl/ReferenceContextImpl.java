package net.sf.taverna.t2.reference.impl;

import java.util.ArrayList;
import java.util.List;

import net.sf.taverna.t2.reference.ReferenceContext;

class ReferenceContextImpl implements ReferenceContext{
	private List<Object> entities;

	public ReferenceContextImpl(){
		entities = new ArrayList<Object>();
	}
	
	public <T> List<T> getEntities(Class<T> entityType) {
		List<T> entitiesOfType = new ArrayList<T>();
		for (Object entity : entities){
			if (entityType.isInstance(entity)){
				entitiesOfType.add(entityType.cast(entity));
			}
		}
		return entitiesOfType;
	}

	public void addEntity(Object entity){
		entities.add(entity);
	}
};
