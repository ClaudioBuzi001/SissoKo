/*
 * (c) Copyright Leonardo Company S.p.A.. All rights reserved.
 *
 * Any right of industrial and intellectual property on this document,
 * and of technical Know-how herein contained, belongs to
 * Leonardo Company S.p.A. and/or third parties.
 * According to the law, it is forbidden to disclose, reproduce or however
 * use this document and any data herein contained for any use without
 * previous written authorization by Leonardo Company S.p.A.
 *
 */
package application4F;

import org.omg.CORBA.Any;
import org.omg.CORBA.AnyHolder;
import org.omg.CORBA.Object;
import org.omg.CORBA.ORBPackage.InvalidName;

import eu.cardamom.CdmwPlatformMngt.AttributeNotFound;
import eu.cardamom.CdmwPlatformMngt.Event;
import eu.cardamom.CdmwPlatformMngt.EventKind;
import eu.cardamom.CdmwPlatformMngt.Owner;
import eu.cardamom.CdmwPlatformMngt.PropertyChange;
import eu.cardamom.CdmwPlatformMngt.PropertyInListNotFound;
import eu.cardamom.CdmwPlatformMngt.PropertyInfo;
import eu.cardamom.CdmwPlatformMngt.SupervisionObserver;
import eu.cardamom.CdmwPlatformMngt.SupervisionObserverPOA;
import eu.cardamom.CdmwPlatformMngt.External.Application;
import eu.cardamom.CdmwPlatformMngt.ManagedElementContexts.ProcessContext;
import eu.cardamom.CdmwPlatformMngt.ManagedElementContexts.ProcessContextHelper;

/**
 * The Class SupervisionObserverWPImpl_4F.
 */
public abstract class SupervisionObserverWPImpl_4F extends SupervisionObserverPOA implements SupervisionObsEnv_4F {

	/** The prop. */
	private String prop = "";

	/**
	 * Gets the prop.
	 *
	 * @return the prop
	 */
	public String getProp() {
		return prop;
	}

	/**
	 * Sets the prop.
	 *
	 * @param prop the new prop
	 */
	public void setProp(String prop) {
		this.prop = prop;
	}

	/**
	 * Register.
	 *
	 * @throws AttributeNotFound the attribute not found
	 */
	public void register() throws AttributeNotFound {
		Object obj = null;
		try {

			obj = GiForkMain_4F.getOrb().resolve_initial_references("ManagedProcessContext");
			SupervisionObserver supObs = this._this(GiForkMain_4F.getOrb());
			ProcessContext m_ctx = ProcessContextHelper.narrow(obj);
			Application myAppl = m_ctx.the_application();
			Any myAny = org.omg.CORBA.ORB.init().create_any();
			AnyHolder myAnyHolder = new org.omg.CORBA.AnyHolder(myAny);
			myAnyHolder.value = myAppl.get_attribute("applicationSession", Owner.USER_DEFINED);
			prop = myAnyHolder.value.extract_string();
			String[] property_names = new String[1];
			property_names[0] = "applicationSession";
			myAppl.register_property_observer("Obs_" + myAppl.name() + "_" + m_ctx.process_name(), supObs,
					property_names);

		} catch (InvalidName | PropertyInListNotFound e) {

			e.printStackTrace();
		}

	}

	/**
	 * Notify.
	 *
	 * @param an_event the an event
	 */
	@Override
	public synchronized void _notify(Event an_event) {
		try {
			if (an_event.event_kind == EventKind.PROPERTY_CHANGE) {

				final PropertyChange propertyValue = (PropertyChange) an_event;

				PropertyInfo[] propertyInfos = propertyValue.property_infos;

				for (PropertyInfo propInfo : propertyInfos) {

					if (propInfo.property_name.equals("applicationSession")) {

						prop = propInfo.property_new_value.extract_string();
						performAction(prop);

					}
				}
			}
		} catch (Exception e) {

			System.out.println("ERROR NOTIFY: " + e.getMessage());
			e.printStackTrace();
		}

	}

	/**
	 * Perform action.
	 *
	 * @param prop2 the prop 2
	 */
	@Override
	public abstract void performAction(String prop2);

}
