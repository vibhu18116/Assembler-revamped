/************************************************************
*															*
*	Assembler made by Tanya Sanjay Kumar and Vibhu Agrawal	*
*															*
************************************************************/

import java.io.*;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;

class SymbolTable{

	/************************************************
	*	Class to handle Symbols (or variables).		*
	*************************************************/

	private HashMap<String, String> sym;
	
	SymbolTable(){
		sym = new HashMap<String, String>();
	}
	
	public HashMap<String, String> getSymbol() {
		return sym;
	}

	public boolean contains(String s){
		if (sym.containsKey(s))
			return true;

		return false;
	}
	
	public String getBinary(String l) {
		return sym.get(l);
	}
	
	public void add(String a,String b) {
		sym.put(a,b);
	}

	private String convertTwelveBits(String a) {
		int b = a.length();
		String o = "0";
		while(b<12) {
			a = o + a;
			b++;
		}
		return a;
	}

	public boolean completeTable(int loc){

		Iterator<HashMap.Entry<String, String>> iter = sym.entrySet().iterator();

		while (iter.hasNext()){
			HashMap.Entry<String, String> entry = iter.next();
			this.add(entry.getKey(), convertTwelveBits((Integer.toBinaryString(loc))));
			if (loc>4094){
				System.out.println("ERROR: Out of Memory. Trying to access address beyond 4094");
				return false;
			}
			loc++;
		}

		return true;
	}

	public boolean write(BufferedWriter bw){
		try{

			bw.write("Variables");
			bw.newLine();
			Iterator<HashMap.Entry<String, String>> iter = sym.entrySet().iterator();

			while (iter.hasNext()){
				HashMap.Entry<String, String> entry = iter.next();
				bw.append(entry.getKey() + ":" + entry.getValue());
				bw.newLine();
			}

			bw.close();

			return true;

		}catch(IOException e){
			System.out.println("Error in writing Symbol Table to hard disk. Terminating.");
			return false;
		}
	}
}
