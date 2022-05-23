/*
 * Copyright 2002-2007 Robert Breidecker.
 * 
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 * 
 *      http://www.apache.org/licenses/LICENSE-2.0
 * 
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package exemplos;

import Funcoes.Extenco;
import net.sourceforge.jeval.EvaluationException;
import net.sourceforge.jeval.Evaluator;
import net.sourceforge.jeval.function.*;

import java.math.BigDecimal;

public class ExtensoFunction implements Function {
	/**
	 * Returns the name of the function - "Extenso".
	 *
	 * @return The name of this function class.
	 */
	public String getName() {
		return "Extenso";
	}

	/**
	 * Executes the function for the specified argument. This method is called
	 * internally by Evaluator.
	 *
	 * @param evaluator
	 *            An instance of Evaluator.
	 * @param arguments
	 *            A string argument that will be converted into a string that is
	 *            in reverse order. The string argument(s) HAS to be enclosed in
	 *            quotes. White space that is not enclosed within quotes will be
	 *            trimmed. Quote characters in the first and last positions of
	 *            any string argument (after being trimmed) will be removed
	 *            also. The quote characters used must be the same as the quote
	 *            characters used by the current instance of Evaluator. If there
	 *            are multiple arguments, they must be separated by a comma
	 *            (",").
	 *
	 * @return The source string in reverse order.
	 *
	 * @exception FunctionException
	 *                Thrown if the argument(s) are not valid for this function.
	 */
	public FunctionResult execute(Evaluator evaluator, String arguments)
			throws FunctionException {
		String result = "";

		try {
			String stringValue = new Evaluator().evaluate(arguments, true,
					false);

			String argumentOne = FunctionHelper.trimAndRemoveQuoteChars(
					stringValue, evaluator.getQuoteCharacter());

			result = CAPITULAR(EXTENCO(argumentOne));
		} catch (FunctionException fe) {
			throw new FunctionException(fe.getMessage(), fe);
		} catch (EvaluationException ee) {
			throw new FunctionException("Invalid expression in arguments.", ee);
		} catch (Exception e) {
			throw new FunctionException("One string argument is required.", e);
		}

		return new FunctionResult(result,
				FunctionConstants.FUNCTION_RESULT_TYPE_STRING);
	}

	private String EXTENCO(String variavel) {
		String valor = variavel; //.replace(".","");
		//valor = valor.replace(",",".");
		return new Extenco(new BigDecimal(valor)).toString();
	}

	private String CAPITULAR(String variavel) {
		String[] palavras = variavel.toLowerCase().split(" ");
		for (int i=0; i<palavras.length; i++) {
			palavras[i] = palavras[i].substring(0,1).toUpperCase() + palavras[i].substring(1);
		}
		String texto = ""; for (String var : palavras) {texto += var + " ";}
		// exeçoes
		texto = texto.replaceAll(" Do ", " do ");
		texto = texto.replaceAll(" Dos ", " dos ");
		texto = texto.replaceAll(" Da ", " da ");
		texto = texto.replaceAll(" Das ", " das ");
		texto = texto.replaceAll(" De ", " de ");
		texto = texto.replaceAll(" E ", " e ");
		texto = texto.replaceAll(" Es ", " es ");
		texto = texto.replaceAll(" O ", " o ");
		texto = texto.replaceAll(" Os ", " os ");
		texto = texto.replaceAll(" A ", " a ");
		texto = texto.replaceAll(" As ", " as ");
		return texto.trim();
	}
}