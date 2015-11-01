package org.eclipse.ease.lang.python.py4j.internal;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.io.OutputStreamWriter;
import java.net.URL;
import java.util.Map;

import org.apache.commons.lang.StringEscapeUtils;
import org.eclipse.core.runtime.FileLocator;
import org.eclipse.core.runtime.Platform;
import org.eclipse.ease.AbstractScriptEngine;
import org.eclipse.ease.Script;
import org.eclipse.ease.lang.python.PythonHelper;

import py4j.ClientServer;

public class Py4jPythonScriptEngine extends AbstractScriptEngine {

	private ClientServer gatewayServer;
	private IPythonSideEngine pythonSideEngine;
	private Process pythonProcess;
	private Thread inputGobbler, errorGobbler;

	/**
	 * This runnable will consume an input stream's content into an output
	 * stream as soon as it gets available.
	 * <p>
	 * Typically used to empty processes' standard output and error, preventing
	 * them to choke.
	 * </p>
	 * <p>
	 * <b>Note</b> that a {@link StreamGobbler} will never close either of its
	 * streams.
	 * </p>
	 */
	private static class StreamGobbler implements Runnable {
		private final BufferedReader reader;

		private final BufferedWriter writer;

		public StreamGobbler(InputStream stream, OutputStream output) {
			this.reader = new BufferedReader(new InputStreamReader(stream));
			if (output == null)
				this.writer = null;
			else
				this.writer = new BufferedWriter(new OutputStreamWriter(output));
		}

		public void run() {
			boolean writeFailure = false;

			String line = null;
			try {
				while ((line = reader.readLine()) != null) {
					// Do not try to write again after a failure, but keep
					// reading
					// as long as possible to prevent the input stream from
					// choking.
					if (!writeFailure && writer != null) {
						try {
							writer.write(line);
							writer.newLine();
							writer.flush();
						} catch (IOException e) {
							writeFailure = true;
						}
					}
				}
			} catch (IOException e) {
				// TODO don't swallow?
			}
		}

	}

	public Py4jPythonScriptEngine() {
		super("Py4J");
	}

	@Override
	protected boolean setupEngine() {
		System.out.println(System.currentTimeMillis() + " - enter setupEngine");
		ProcessBuilder pb = new ProcessBuilder();

		// TODO: this is a hack for getting the paths, needs to be done properly
		File py4jPythonSrc;
		File py4jEaseMain;
		try {
			File py4jEaseBundleFile = FileLocator.getBundleFile(Platform.getBundle(Activator.PLUGIN_ID));
			py4jEaseMain = new File(py4jEaseBundleFile, "/pysrc/ease_py4j_main.py");
			if (!py4jEaseMain.exists()) {
				throw new IOException("Can't find py4jEaseMain, expected it here: " + py4jEaseMain);
			}

			File py4jPythonBundleFile = FileLocator.getBundleFile(Platform.getBundle("py4j-python"));
			py4jPythonSrc = new File(py4jPythonBundleFile, "/src");
			File py4j = new File(py4jPythonSrc, "py4j");
			if (!py4j.exists() || !py4j.isDirectory()) {
				throw new IOException("Can't find py4j python directory, expected it here: " + py4j);
			}
		} catch (IOException e1) {
			e1.printStackTrace();
			return false;
		}


		pb.environment().put("PYTHONPATH", py4jPythonSrc.toString());
		pb.command().add("python");
		pb.command().add("-u");
		pb.command().add(py4jEaseMain.toString());


		try {
			pythonProcess = pb.start();
		} catch (IOException e) {
			// TODO don't swallow exception, note that the caller on receiving
			// false response
			// throws a new RuntimeException with no detail
			return false;
		}
		inputGobbler = new Thread(new StreamGobbler(pythonProcess.getInputStream(), getOutputStream()));
		inputGobbler.start();
		errorGobbler = new Thread(new StreamGobbler(pythonProcess.getErrorStream(), getErrorStream()));
		errorGobbler.start();

		// Python is started, start java and have it connect to Python
		gatewayServer = new ClientServer(this);

		try {
			Thread.sleep(1000);
		} catch (InterruptedException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}

		pythonSideEngine = (IPythonSideEngine) gatewayServer
				.getPythonServerEntryPoint(new Class[] { IPythonSideEngine.class });
		System.out.println(System.currentTimeMillis() + " - exit setupEngine");
		return true;
	}

	@Override
	protected Object execute(Script script, Object reference, String fileName, boolean uiThread) throws Throwable {
		System.out.println(
				System.currentTimeMillis() + " - enter execute - " + StringEscapeUtils.escapeJava(script.getCode()));
		if (uiThread) {
			script.toString();
		}
		Object result = pythonSideEngine.execute(script.getCode());
		System.out.println(System.currentTimeMillis() + " - exit execute");
		return result;
	}

	@Override
	public void terminateCurrent() {
	}

	@Override
	protected boolean teardownEngine() {
		gatewayServer.shutdown();
		pythonProcess.destroy();
		return true;
	}

	@Override
	public String getSaveVariableName(final String name) {
		return PythonHelper.getSaveName(name);
	}

	@Override
	public void registerJar(URL url) {
		throw new UnsupportedOperationException();
	}

	@Override
	protected Object internalGetVariable(String name) {
		return pythonSideEngine.internalGetVariable(name);
	}

	@Override
	protected Map<String, Object> internalGetVariables() {
		return pythonSideEngine.internalGetVariables();
	}

	@Override
	protected boolean internalHasVariable(String name) {
		return pythonSideEngine.internalHasVariable(name);
	}

	@Override
	protected void internalSetVariable(String name, Object content) {
		pythonSideEngine.internalSetVariable(name, content);
	}

	@Override
	protected Object internalRemoveVariable(String name) {
		throw new UnsupportedOperationException();
	}

}
