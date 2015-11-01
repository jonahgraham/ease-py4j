package org.eclipse.ease.lang.python.py4j.internal;

import org.eclipse.ease.IScriptEngine;
import org.eclipse.ease.IScriptEngineLaunchExtension;

public class Py4JBootstrap implements IScriptEngineLaunchExtension {

	private static final String BOOTSTRAP_CODE = "java = __ease_gateway.jvm.java\n" +
		"org = __ease_gateway.jvm.org\n" +
		"com = __ease_gateway.jvm.com\n" +
		"__ease_bootstrapEM = org.eclipse.ease.modules.EnvironmentModule()\n" +
		"__ease_bootstrapEM.loadModule(\"/System/Environment\")\n";

	@Override
	public void createEngine(IScriptEngine engine) {
		engine.executeAsync(BOOTSTRAP_CODE);
	}

}
