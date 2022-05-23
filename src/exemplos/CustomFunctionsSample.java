package exemplos;

import net.sourceforge.jeval.EvaluationException;
import net.sourceforge.jeval.Evaluator;

/**
 * Contains a couple of samples for evaluating expression containing custom
 * functions. There are many more examples in the JUnit tests.
 */
public class CustomFunctionsSample {

	/**
	 * Run the sample code. No arguments are necessary.
	 * 
	 * @param args
	 */
	public static void main(String args[]) {

		/*
		 * This sample shows the basic usage of the JEval Evaluator class.
		 * Calling the default contructor will set he quoteCharater to single
		 * quote. This constructor will also load all math variables, math
		 * functions and string variables.
		 */
		Evaluator evaluator = new Evaluator();

		try {
			System.out.println(evaluator
					.evaluate("trim('   jjj  jjj    ')"));

            evaluator.putFunction(new CapituleFunction());

			System.out.println(evaluator
					.evaluate("Capitule(trim('   joao luiz DIAS   '))"));

			System.out.println(evaluator
					.evaluate("Capitule('WELLINGTON de souza pInTo')"));

			evaluator.clearFunctions();

			evaluator.putFunction(new ExtensoFunction());
			System.out.println(evaluator
					.evaluate("toLowerCase(Extenso('8655548.42'))"));

			evaluator.clearFunctions();

			evaluator.putFunction(new FormatFunction());
/*
			System.out.println(evaluator
					.evaluate("Format('125.50','###.##',0)"));
*/
			System.out.println(evaluator
					.evaluate("Format('2016/10/03','dd-MM-yyyy',1)"));
			System.out.println(evaluator
					.evaluate("Format('Now','dd-MM-yyyy hh:mmm',1)"));
			System.out.println(evaluator
					.evaluate("Format('2016-08-11','dd 'de' MMMM 'de' yyyy',1)"));
/*
			System.out.println(evaluator
					.evaluate("Format('1123.45','Currency',2)"));
			System.out.println(evaluator
					.evaluate("Format('678.90','#0.000',2)"));

			evaluator.clearFunctions();

*/
/*
			evaluator.putFunction(new CondicaoFunction());
			System.out.println(evaluator
					.evaluate("Condicao('F' == 'M', '3', '4')"));
			evaluator.clearFunctions();
*/

			/**
			 * This sample shows an invalid expression. The variables were just
			 * cleared, therefor the function "stringReverse" no longer exists.
			 */
			System.out.println("An exception is expected in the "
					+ "next evaluation.");
			System.out.println(evaluator
					.evaluate("stringReverse('Hello World!')"));
		} catch (EvaluationException ee) {
			System.out.println(ee);
		}
	}
}
