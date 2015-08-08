package org.squiddev.petit;


import org.junit.After;
import org.junit.Before;
import org.junit.Rule;
import org.junit.Test;
import org.junit.rules.ErrorCollector;

import javax.tools.*;
import java.io.IOException;
import java.net.URISyntaxException;
import java.net.URL;
import java.util.*;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import static org.hamcrest.CoreMatchers.equalTo;
import static org.junit.Assert.assertFalse;

public class Verification {
	@Rule
	public ErrorCollector collector = new ErrorCollector();

	@Test
	public void noPeripheral() throws Exception {
		int errors = 0;

		JavaFileObject object = new CustomJavaFileObject("NoPeripheral");
		Set<String> matched = new HashSet<String>();
		Pattern findAnnotation = Pattern.compile("@([A-Za-z]+)", Pattern.CASE_INSENSITIVE);

		for (Diagnostic diagnostic : run(object)) {
			if (diagnostic.getCode().equals("compiler.warn.proc.messager")) {
				if (diagnostic.getMessage(null).equals("Cannot find @Peripheral")) {
					errors++;

					// Extract annotation name for validation helping
					String node = object.getCharContent(true).subSequence(
						(int) diagnostic.getStartPosition(),
						(int) diagnostic.getEndPosition()
					).toString();

					Matcher matcher = findAnnotation.matcher(node);
					if (matcher.find()) matched.add(matcher.group(1));
				}
			}
		}

		for (String node : new String[]{"Alias", "Handler", "LuaFunction", "Optional", "Provided"}) {
			if (!matched.remove(node)) {
				collector.checkThat("Expected " + node, matched.remove(node), equalTo(true));
			}
		}

		collector.checkThat(errors, equalTo(5));
	}

	//region File loading
	public JavaCompiler compiler;
	public DiagnosticCollector<JavaFileObject> diagnostics;
	public StandardJavaFileManager fileManager;

	@Before
	public void setup() {
		compiler = ToolProvider.getSystemJavaCompiler();
		diagnostics = new DiagnosticCollector<JavaFileObject>();
		fileManager = compiler.getStandardFileManager(diagnostics, null, null);
	}

	@After
	public void tearDown() throws Exception {
		fileManager.close();
	}

	public List<Diagnostic<? extends JavaFileObject>> run(JavaFileObject object) throws Exception {
		JavaCompiler.CompilationTask task = compiler.getTask(null, fileManager, diagnostics, null, null, Collections.singleton(object));
		assertFalse("File should not pass", task.call());

		return diagnostics.getDiagnostics();
	}

	public static class CustomJavaFileObject extends SimpleJavaFileObject {
		public final String contents;

		protected CustomJavaFileObject(String name) throws IOException, URISyntaxException {
			this(CustomJavaFileObject.class.getResource("verification/" + name + ".java"));
		}

		protected CustomJavaFileObject(URL resource) throws URISyntaxException, IOException {
			super(resource.toURI(), Kind.SOURCE);
			Scanner s = new Scanner(resource.openStream()).useDelimiter("\\A");
			contents = s.hasNext() ? s.next() : "";
		}

		@Override
		public CharSequence getCharContent(boolean ignoreEncodingErrors) throws IOException {
			return contents;
		}
	}
	//endregion
}
