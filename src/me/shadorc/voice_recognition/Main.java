package me.shadorc.voice_recognition;

import edu.cmu.sphinx.frontend.util.Microphone;
import edu.cmu.sphinx.recognizer.Recognizer;
import edu.cmu.sphinx.result.ConfidenceResult;
import edu.cmu.sphinx.result.ConfidenceScorer;
import edu.cmu.sphinx.result.Path;
import edu.cmu.sphinx.result.Result;
import edu.cmu.sphinx.util.props.ConfigurationManager;

public class Main {

	public static void main(String[] args) {
		System.out.println("Loading config ...");

		/* Load Sphinx Configuration */
		ConfigurationManager cm = new ConfigurationManager(Main.class.getResource("sphinx.config.xml"));

		System.out.println("Initializing Sphinx ...");

		Recognizer recognizer = (Recognizer) cm.lookup("recognizer");
		recognizer.allocate();

		System.out.println("Starting Microphone ...");

		/* Start the microphone or exit if this is not possible */
		Microphone microphone = (Microphone) cm.lookup("microphone");
		if(!microphone.startRecording()) {
			System.out.println("Cannot start microphone.");
			recognizer.deallocate();
			System.exit(1);
		}

		System.out.println("Say a command to the flat.");
		System.out.println("Start speaking. Press Ctrl-C to quit.\n");

		/* Loop the recognition until the program exits. */
		while (true) {

			Result result = recognizer.recognize();

			if (result != null) {

				ConfidenceScorer cs = (ConfidenceScorer) cm.lookup("confidenceScorer");
				ConfidenceResult cr = cs.score(result);
				Path best = cr.getBestHypothesis();

				// Print linear confidence of the best path
				System.out.println(best.getTranscription());

				String resultText = result.getBestFinalResultNoFiller().trim();
				if(!resultText.isEmpty()) {
					System.out.println("Sending Command: " + resultText + '\n');
				} else {
					System.out.println("I can't hear what you said.\n");
				}
			}
		}
	}
}