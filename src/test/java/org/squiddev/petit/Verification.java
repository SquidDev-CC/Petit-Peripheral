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
import static org.junit.Assert.assertEquals;

public class Verification {
	public static final Pattern ANNOTATION = Pattern.compile("@([a-z]+)", Pattern.CASE_INSENSITIVE);

	@Rule
	public ErrorCollector collector = new ErrorCollector();

	@Test
	public void noPeripheral() throws Exception {
		Set<String> matched = new HashSet<String>(Arrays.asList("Alias", "Handler", "LuaFunction", "Optional", "Provided"));

		for (String node : filter(new CustomJavaFileObject("NoPeripheral"), "Cannot find @Peripheral")) {
			Matcher matcher = ANNOTATION.matcher(node);
			if (matcher.find()) {
				collector.checkThat("Expected " + matcher.group(1), matched.remove(matcher.group(1)), equalTo(true));
			}
		}

		collector.checkThat(matched.toString(), matched.size(), equalTo(0));
	}

	@Test
	public void noConverter() throws Exception {
		Set<String> matched = new HashSet<String>(Arrays.asList(
			"Environment noConverter",
			"IComputerAccess notProvided"
			/*
			 We don't include @Provider String as that should be stripped
			 */
		));

		for (String node : filter(new CustomJavaFileObject("NoConverter"), "[IPeripheral] No converter")) {
			collector.checkThat("Expected " + node, matched.remove(node), equalTo(true));
		}

		collector.checkThat(matched.toString(), matched.size(), equalTo(0));
	}

	@Test
	public void argumentOrder() throws Exception {
		assertEquals(1, filter(new CustomJavaFileObject("ArgumentOrder"), "Unexpected required argument after optional one").size());
	}

	@Test
	public void duplicateMethods() throws Exception {
		assertEquals(1, filter(new CustomJavaFileObject("DuplicateMethods"), "Duplicate name 'foo'").size());
	}

	@Test
	public void primitiveOptional() throws Exception {
		assertEquals(1, filter(new CustomJavaFileObject("PrimitiveOptional"), "Primitive cannot be optional").size());
	}

	@Test
	public void variableReturn() throws Exception {
		assertEquals(1, filter(new CustomJavaFileObject("VariableReturn"), "Expected array for variable return").size());
	}

	@Test
	public void illegalName() throws Exception {
		assertEquals(2, filter(new CustomJavaFileObject("IllegalName"), "Invalid name").size());
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

		collector.checkThat("File should not pass", task.call(), equalTo(false));

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

	public List<String> filter(JavaFileObject object, String message) throws Exception {
		return filter(run(object), object, message);
	}

	public List<String> filter(List<? extends Diagnostic> diagnostics, JavaFileObject object, String message) throws IOException {
		List<String> result = new ArrayList<String>();

		for (Diagnostic diagnostic : diagnostics) {
			if (diagnostic.getCode().endsWith(".proc.messager") && (message == null || diagnostic.getMessage(null).contains(message))) {
				result.add(object.getCharContent(true).subSequence(
					(int) diagnostic.getStartPosition(),
					(int) diagnostic.getEndPosition()
				).toString());
			}
		}

		return result;
	}
	//endregion
}
