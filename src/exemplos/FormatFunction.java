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

import Funcoes.DbMain;
import Funcoes.FuncoesGlobais;
import net.sourceforge.jeval.Evaluator;
import net.sourceforge.jeval.function.*;

import java.text.DecimalFormat;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.GregorianCalendar;

public class FormatFunction implements Function {
	/**
	 * Returns the name of the function - "Format".
	 *
	 * @return The name of this function class.
	 */
	public String getName() {
		return "Format";
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
	 *            oneParameter
	 *            String a ser formatada
	 *
	 *            twoParameter
	 *            String da mascara de formatação
	 *
	 *            treeParameter
	 *            integer do tipo de formatação
	 *            0 - Formata String (Ex.: '12345678901','###.###.###-##'  Result => 123.456.789-01)
	 *            1 - Formata String tipo data (Ex.: '2016-10-03','dd/MM/yyyy' Result => 03/10/2016)
	 *            2 - Formata String tipo currency (Ex.: '1234.56','#0.00' Result => 1.234,56)
	 *
	 * @return The source string in format user especified.
	 *
	 * @exception FunctionException
	 *                Thrown if the argument(s) are not valid for this function.
	 */
	public FunctionResult execute(Evaluator evaluator, String arguments)
			throws FunctionException {
		String result = "";
		String exceptionMessage = "Two string arguments and one integer are required.";
		ArrayList values = FunctionHelper.getTwoStringsAndOneInteger(arguments, ',');
		if(values.size() != 3) {
			throw new FunctionException(exceptionMessage);
		} else {
			try {
				String e = FunctionHelper.trimAndRemoveQuoteChars((String)values.get(0), evaluator.getQuoteCharacter());
				String argumentTwo = FunctionHelper.trimAndRemoveQuoteChars((String)values.get(1), evaluator.getQuoteCharacter());
				int index = ((Integer)values.get(2)).intValue();
				if (index == 0) {
					result = FORMATAR(e, argumentTwo);
				} else if (index == 1) {
					SimpleDateFormat formatter = new SimpleDateFormat(argumentTwo);
					if (e.equalsIgnoreCase("NOW")) {
						result = formatter.format(DbMain.getDateTimeServer());
					} else {
						result = formatter.format(StringtoDate(e,"yyyy/MM/dd"));
					}
				} else {
					if (argumentTwo.equalsIgnoreCase("CURRENCY")) {
						float _arg1 = StringToFloat(e);
						result = floatToCurrency(_arg1,2);
					} else {
						float _arg1 = StringToFloat(e);
						result = FormatPattern(e,argumentTwo);
					}
				}
			} catch (FunctionException var9) {
				throw new FunctionException(var9.getMessage(), var9);
			} catch (Exception var10) {
				throw new FunctionException(exceptionMessage, var10);
			}
		}

		return new FunctionResult(result,
				FunctionConstants.FUNCTION_RESULT_TYPE_STRING);
	}

	private String FORMATAR(String variavel, String mascara) {
		String variavelClean = "";
		variavelClean = variavel.replaceAll("!","");
		variavelClean = variavelClean.replace("#","");
		variavelClean = variavelClean.replace(".","");
		variavelClean = variavelClean.replace(",","");
		variavelClean = variavelClean.replace("-","");
		variavelClean = variavelClean.replace("/","");
		variavelClean = variavelClean.replace(" ","");
		variavelClean = variavelClean.replace("=","");

		String variavelOut = ""; int pos = 0;
		for (int i=0;i<mascara.length();i++) {
			if (mascara.substring(i,i+1).equalsIgnoreCase("!")) {variavelOut += variavelClean.substring(pos,pos+1).toUpperCase(); pos++;}
			if (mascara.substring(i,i+1).equalsIgnoreCase("#")) {variavelOut += variavelClean.substring(pos,pos+1); pos++;}
			if (mascara.substring(i,i+1).equalsIgnoreCase(".")) variavelOut += ".";
			if (mascara.substring(i,i+1).equalsIgnoreCase("-")) variavelOut += "-";
			if (mascara.substring(i,i+1).equalsIgnoreCase("/")) variavelOut += "/";
		}
		return variavelOut;
	}

	private Date StringtoDate(String Data, String patern) {
		GregorianCalendar ret = null;
		Date ter = null;
		if (!Data.trim().equalsIgnoreCase("")) {
			int posDia = patern.indexOf("dd");
			int posMes = patern.indexOf("MM");
			int posAno = patern.indexOf("yyyy");

			int vDia = Integer.valueOf(Data.substring(posDia, posDia + 2));
			int vMes = Integer.valueOf(Data.substring(posMes, posMes + 2)) - 1;
			int vAno = Integer.valueOf(Data.substring(posAno, posAno + 4));

			GregorianCalendar dt = new GregorianCalendar();
			dt.set(vAno, vMes, vDia);
			ret = dt;
		}

		if (ret != null) { ter = ret.getTime(); }
		return ter;
	}

	private float StringToFloat(String Valor) {
		String retorno = "0.00";
		if (Valor != null) {
			if (!Valor.trim().equals("")) {
				retorno = Valor;
			}
		}

		return Float.valueOf(retorno);
	}

	private String floatToCurrency(float cValor, int decimal) {
		DecimalFormat v = new DecimalFormat();
		if (decimal > 0) {
			v.applyPattern("#,##0." + FuncoesGlobais.StrZero("0", decimal));
		} else {
			v.applyPattern("##");
		}
		return v.format(cValor);
	}

	static public String FormatPattern(String cValor, String pattern) {
		Float Valor;

		cValor = cValor.replace("-", "0");

		DecimalFormat v = new DecimalFormat();
		Valor = Float.valueOf(cValor);
		v.applyPattern(pattern);
		return v.format(Valor);
	}

}