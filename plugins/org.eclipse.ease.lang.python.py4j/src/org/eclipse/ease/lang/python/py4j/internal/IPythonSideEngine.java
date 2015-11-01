package org.eclipse.ease.lang.python.py4j.internal;

import java.util.Map;

public interface IPythonSideEngine {
	Object execute(String codeText) throws Throwable;
	Object internalGetVariable(String name);
	Map<String, Object> internalGetVariables();
	boolean internalHasVariable(String name);
	void internalSetVariable(String name, Object content);
}
