package org.eclipse.ease.lang.python.py4j.internal;

import org.eclipse.ease.modules.AbstractEnvironment;

public class Py4JEnvironment extends AbstractEnvironment {

	@Override
	public void wrap(Object toBeWrapped) {
		toBeWrapped.toString();
	}

}
