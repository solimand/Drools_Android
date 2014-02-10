/**
 * 
 */
package com.gradle.application.medicalec;

import java.io.PrintStream;
import java.util.Collection;
import java.util.Map;

import org.kie.api.KieBase;
import org.kie.api.definition.type.FactType;
import org.kie.api.runtime.ClassObjectFilter;
import org.kie.api.runtime.KieContainer;
import org.kie.api.runtime.KieSession;
import org.kie.api.runtime.KieSessionConfiguration;
import org.kie.api.runtime.rule.FactHandle;

import android.util.Log;

public class Session {
	
	private static final String TAG = "SessionDrools";
	private static final String PACKAGE = "com.gradle.application.medicalec";


	private static final String[] TYPES = { /*"MVI", "Sample", "Event", "Fluent",*/ "MyEvent"};

	private KieBase base;

	private KieSession session;

	protected Session(KieBase base, KieSession ksession/*, KieSessionConfiguration config*/) {
		if (base == null)
			throw new IllegalArgumentException(
					"Illegal 'base' in Session(KnowledgeBase, KnowledgeSessionConfiguration): " + base);
		this.base = base;
		this.session = ksession;
	}

	public void clear() {
		if (null != session) {
			session.dispose();
		}
//		assert invariant() : "Illegal state in Session.isRunning()";
	}
	
	/**TESTING**/
	protected int count(String type) {
//		Class<?> c = session.getKieBase().getFactType(PACKAGE, type).getFactClass();
//		Class<?> c = base.getFactType(PACKAGE, type).getFactClass();
		int result = (int) session.getFactCount();
//		int result = session.getObjects(new ClassObjectFilter(c)).size();
		return result;
	}

	protected void dump() {
		Log.i(TAG, "--[ WM content ]--------------------------------------------------------------");
		for (String type : TYPES)
			Log.i(TAG, String.format("- %s(%d)", type, count(type)));
		Collection<? extends Object> objects = session.getObjects();
		if (objects.size() > 0) {
			Log.i(TAG, "------------------------------------------------------------------------------");
			for (Object object : session.getObjects())
				Log.i(TAG, "> " + object);
		}
		Log.i(TAG, "Packages : " + session.getKieBase().getKiePackages());
		Log.i(TAG, "==============================================================================");
//		assert invariant() : "Illegal state in Session.dump()";
	}

	protected KieSession getMachinery() {
//		assert invariant() : "Illegal state in Session.getMachinery()";
		return session;
	}
//
//	private boolean invariant() {
//		return (base != null && config != null);
//	}

	public boolean isRunning() {
		boolean result = (null != session);
//		assert invariant() : "Illegal state in Session.isRunning()";
		return result;
	}

	public FactHandle notify(String name, Object value, Map<String, Object> params) {
		if (null == name || (name = name.trim()).isEmpty())
			throw new IllegalArgumentException(
					"Illegal 'name' argument in Session.notify(String, Object, Map<String, Object>): " + name);
		if (null == value)
			throw new IllegalArgumentException(
					"Illegal 'value' argument in Session.notify(String, Object, Map<String, Object>): " + value);
		if (null == params)
			throw new IllegalArgumentException(
					"Illegal 'values' argument in Session.notify(String, Object, Map<String, Object>): " + params);
		FactHandle handle = null;
		if (null != session)
			try {
				FactType type = base.getFactType(PACKAGE, name);
				if (null != type) {
					Object eventObj = type.newInstance();
					type.set(eventObj, "value", value);
					type.set(eventObj, "params", params);
					handle = session.insert(eventObj);
					session.fireAllRules();
				} else
					System.err.println("* Unexpected event '" + name + "' (ignored)");
			} catch (InstantiationException e) {
				e.printStackTrace();
			} catch (IllegalAccessException e) {
				e.printStackTrace();
			}
//		assert invariant() : "Illegal state in Session.notify(String, Object, Map<String, Object>)";
		return handle;
	}

	public void start() {
		if (null == session) {
			this.session.fireAllRules();
		}
//		assert invariant() : "Illegal state in Session.start()";
	}

	public void stop() {
		if (null != session) {
			KieSession zombie = session;
			session = null;
			zombie.dispose();
		}
//		assert invariant() : "Illegal state in Session.stop()";
	}

}
