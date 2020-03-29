/************************************************************
*															*
*	Assembler made by Tanya Sanjay Kumar and Vibhu Agrawal	*
*															*
************************************************************/

import java.io.*;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;

class LabelTable{

	/****************************************
	*		Class to handle Labels.			*
	*****************************************/

	private HashMap<String, String> lbl;
	
	LabelTable(){
		lbl = new HashMap<String, String>();
	}
	
	public HashMap<String, String> getLabel() {
		return lbl;
	}

	public boolean contains(String s){
		if (lbl.containsKey(s))
			return true;

		return false;
	}
	
	public String getBinary(String l) {
		return lbl.get(l);
	}
	
	public void add(String a,String b) {
		lbl.put(a,b);
	}

	public boolean containsDefined(String a){
		if (lbl.containsKey(a) && !lbl.get(a).equals("----"))
			return true;

		return false;
	}

	private boolean checkIfComplete(){
		Iterator<HashMap.Entry<String, String>> iter = lbl.entrySet().iterator();
		boolean e_flag = false;

		while (iter.hasNext()){
			HashMap.Entry<String, String> value = iter.next();

			if (value.getValue().equals("----")){
				System.out.println("ERROR: " + value.getKey() + " label not resolved.");
				e_flag = true;
			}
		}

		if (e_flag)
			return false;

		return true;
	}

	public boolean write(BufferedWriter lbltable){
		try{

			boolean complete = this.checkIfComplete();

			if (!complete)
				return false;

			Iterator<HashMap.Entry<String, String>> iter = lbl.entrySet().iterator();

			while (iter.hasNext()){
				HashMap.Entry<String, String> entry = iter.next();
				lbltable.append(entry.getKey() + ":" + entry.getValue());
				lbltable.newLine();
			}
			return true;

		}catch(IOException e){
			System.out.println("Error in writing Label Table to hard disk. Terminating.");
			return false;
		}
	}
}
