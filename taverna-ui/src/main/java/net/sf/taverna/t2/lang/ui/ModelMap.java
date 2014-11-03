/*******************************************************************************
 * Copyright (C) 2007 The University of Manchester   
 * 
 *  Modifications to the initial code base are copyright of their
 *  respective authors, or their employers as appropriate.
 * 
 *  This program is free software; you can redistribute it and/or
 *  modify it under the terms of the GNU Lesser General Public License
 *  as published by the Free Software Foundation; either version 2.1 of
 *  the License, or (at your option) any later version.
 *    
 *  This program is distributed in the hope that it will be useful, but
 *  WITHOUT ANY WARRANTY; without even the implied warranty of
 *  MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the GNU
 *  Lesser General Public License for more details.
 *    
 *  You should have received a copy of the GNU Lesser General Public
 *  License along with this program; if not, write to the Free Software
 *  Foundation, Inc., 59 Temple Place, Suite 330, Boston, MA 02111-1307
 ******************************************************************************/
package net.sf.taverna.t2.lang.ui;

import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import net.sf.taverna.t2.lang.observer.MultiCaster;
import net.sf.taverna.t2.lang.observer.Observable;
import net.sf.taverna.t2.lang.observer.Observer;
import net.sf.taverna.t2.lang.ui.ModelMap.ModelMapEvent;

import org.apache.log4j.Logger;

/**
 * Map of the models present in the workbench associated with their names,
 * together with the ability to manipulate this. Contains, from version 1.5
 * onwards, methods to set and notify components of changes to the underlying
 * set of named models.
 * 
 * A 'model' can be any Object that has an effect on the UI.
 * 
 * @author Stuart Owen
 * @author Tom Oinn
 * @author Stian Soiland-Reyes
 * 
 */
public class ModelMap implements Observable<ModelMapEvent> {

	private static ModelMap instance = new ModelMap();

	private static Logger logger = Logger.getLogger(ModelMap.class);

	public static ModelMap getInstance() {
		return instance;
	}

	/**
	 * At any given time there are zero or more named model objects over which
	 * the workbench UI is acting.
	 */
	private Map<String, Object> modelMap = Collections
			.synchronizedMap(new HashMap<String, Object>());

	protected MultiCaster<ModelMapEvent> multiCaster = new MultiCaster<ModelMapEvent>(
			this);

	private ModelMap() {
	}


	public void addObserver(Observer<ModelMapEvent> observer) {
		multiCaster.addObserver(observer);
	}


	public Object getModel(String modelName) {
		return modelMap.get(modelName);
	}

	public List<Observer<ModelMapEvent>> getObservers() {
		return multiCaster.getObservers();
	}

	public void removeObserver(Observer<ModelMapEvent> observer) {
		multiCaster.removeObserver(observer);
	}

	/**
	 * Manipulate the current model map
	 * 
	 * @param modelName
	 *            name of the model to act on
	 * @param model
	 *            null to destroy the model or a reference to the new model to
	 *            set. If it didn't already exist a modelCreated event will be
	 *            fired otherwise modelChanged is called.
	 */
	public synchronized void setModel(String modelName, Object model) {
		// FIXME: What happens if a listener changes a model midthrough?
		logger.debug("setModel " + modelName + "=" + model);
		if (!modelMap.containsKey(modelName)) {
			if (model != null) {
				// Create new model object
				modelMap.put(modelName, model);
				modelCreated(modelName, model);
			}
		} else {
			if (model == null) {
				// Destroy model object
				Object oldModel = modelMap.get(modelName);
				modelMap.remove(modelMap.get(modelName));
				modelDestroyed(modelName, oldModel);
			} else {
				Object oldModel = modelMap.get(modelName);
				if (oldModel != model) {
					// Update model object
					modelMap.put(modelName, model);
					modelChanged(modelName, oldModel, model);
				}
			}
		}
	}

	private void modelChanged(String modelName, Object oldModel, Object newModel) {
		multiCaster
				.notify(new ModelChangedEvent(modelName, oldModel, newModel));
	}

	private void modelCreated(String modelName, Object model) {
		multiCaster.notify(new ModelCreatedEvent(modelName, model));
	}

	private void modelDestroyed(String modelName, Object oldModel) {
		multiCaster.notify(new ModelDestroyedEvent(modelName, oldModel));
	}

	public static class ModelChangedEvent extends ModelMapEvent {
		ModelChangedEvent(String modelName, Object oldModel, Object newModel) {
			super(modelName, oldModel, newModel);
		}
	}

	public static class ModelCreatedEvent extends ModelMapEvent {
		ModelCreatedEvent(String modelName, Object newModel) {
			super(modelName, null, newModel);
		}
	}

	public static class ModelDestroyedEvent extends ModelMapEvent {
		ModelDestroyedEvent(String modelName, Object oldModel) {
			super(modelName, oldModel, null);
		}
	}

	public static abstract class ModelMapEvent {
		private final String modelName;
		private final Object newModel;
		private final Object oldModel;

		ModelMapEvent(String modelName, Object oldModel, Object newModel) {
			this.modelName = modelName;
			this.oldModel = oldModel;
			this.newModel = newModel;
		}

		public String getModelName() {
			return modelName;
		}

		public Object getNewModel() {
			return newModel;
		}

		public Object getOldModel() {
			return oldModel;
		}
	}

}
