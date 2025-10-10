package service;

import java.io.FileNotFoundException;
import java.io.PrintWriter;

import org.junit.platform.engine.discovery.DiscoverySelectors;
import org.junit.platform.launcher.Launcher;
import org.junit.platform.launcher.LauncherDiscoveryRequest;
import org.junit.platform.launcher.core.LauncherDiscoveryRequestBuilder;
import org.junit.platform.launcher.core.LauncherFactory;
import org.junit.platform.launcher.listeners.SummaryGeneratingListener;

/**
 * Class used to manually run the {@link AggregatorServiceTestSuite} from a standard Java main
 * method.
 * <p>
 * This can be useful in standalone environments or when integration with build tools is not
 * available.
 * </p>
 */
public class RunAggregatorTestSuite {

	/** The constant TEST_RESULT_FILE_NAME */
	private static final String TEST_RESULT_FILE_NAME = "Aggregator_Service_Test_Results.txt";

	/**
	 * Main method that launches the test suite using the JUnit Platform launcher API.
	 *
	 * @param args command-line arguments (not used)
	 */
	public static void main(String[] args) {
		// Build a request for the suite class
		LauncherDiscoveryRequest request = LauncherDiscoveryRequestBuilder.request()
				.selectors(DiscoverySelectors.selectClass(AggregatorServiceTestSuite.class)).build();

		// Create the test launcher and listener
		Launcher launcher = LauncherFactory.create();
		SummaryGeneratingListener listener = new SummaryGeneratingListener();

		// Register the listener and execute the test suite
		launcher.registerTestExecutionListeners(listener);
		launcher.execute(request);

		/* Optionally, you can print the test results to a file */
//		printTestResultsIntoCustomFile(listener);

		// Print test summary to the console
		listener.getSummary().printTo(new java.io.PrintWriter(System.out, true));

		// Check for failures and print details
		System.out.println("\n/************ FAILED TEST ************/ \n");
		listener.getSummary().getFailures().forEach(failure -> {
			System.out.println("\nTEST FAILED: " + failure.getTestIdentifier().getDisplayName() + "\n");
			failure.getException().printStackTrace(System.out);
		});
	}

	/**
	 * Writes the test execution summary to a custom result file.
	 *
	 * @param listener the listener containing the test execution summary
	 */
	private static void printTestResultsIntoCustomFile(SummaryGeneratingListener listener) {
		try {
			PrintWriter writer = new PrintWriter(TEST_RESULT_FILE_NAME);
			listener.getSummary().printTo(writer);
			writer.close();
		} catch (FileNotFoundException e) {
			e.printStackTrace();
		}

	}
}
