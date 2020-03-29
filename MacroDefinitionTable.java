/************************************************************
*															*
*	Assembler made by Tanya Sanjay Kumar and Vibhu Agrawal	*
*															*
************************************************************/

import java.util.HashMap;
import java.util.ArrayList;
import java.io.*;
import java.util.Iterator;
import java.util.Arrays;

class MacroDefinitionTable{

	/****************************************
	*	Class to handle macro expansion.	*
	*****************************************/

	private HashMap<String, String> macroNameNDef = new HashMap<String, String>();
	private HashMap<String, ArrayList<String>> macro_params = new HashMap<String, ArrayList<String>>();
	private HashMap<String, HashMap<String, Integer>> macroName_labels_count = new HashMap<String, HashMap<String, Integer>>();

	public void addMacro(String name, String def, ArrayList<String> labels, ArrayList<String> params){
		macroNameNDef.put(name, def);

		HashMap<String, Integer> label_count = new HashMap<String, Integer>();

		for (String s: labels){
			label_count.put(s, 0);
		}

		macroName_labels_count.put(name, label_count);
		macro_params.put(name, params);
	}

	public boolean isMacro(String name){

		if (macroNameNDef.containsKey(name)){
			return true;
		}

		return false;
	}


	public boolean expandMacro(String m_name, BufferedWriter fileUsed, ArrayList<String> subs_params) throws IOException{

		String writingContent = macroNameNDef.get(m_name);
		String[] content = writingContent.split("\n");
		ArrayList<String> parameters = macro_params.get(m_name);
		HashMap<String, Integer> labels = macroName_labels_count.get(m_name);

		if (subs_params.size()!=parameters.size()){
			System.out.println("Error: Invalid number of arguments to macro " + m_name + ". Cannot Expand.");
			return false;
		}

		Iterator<HashMap.Entry<String, Integer>> iter = labels.entrySet().iterator();

		while (iter.hasNext()){
			HashMap.Entry<String, Integer> entry = iter.next();
			labels.put(entry.getKey(), entry.getValue()+1);
		}


		for (String line: content){

			String[] words = line.split(" ");

			for (String w: words){

				w = w.replace(":", "");

				if (parameters.contains(w)){

					fileUsed.append(subs_params.get(parameters.indexOf(w)));

				}else if (labels.containsKey(w)){
					fileUsed.append(w);
					fileUsed.append(Integer.toString(labels.get(w)));

					w = w+":";

					if (Arrays.asList(words).contains(w))
						fileUsed.append(":");


				}else{
					fileUsed.append(w);
				}

				fileUsed.append(" ");
			}

			fileUsed.newLine();
		}
		return true;
	}
}