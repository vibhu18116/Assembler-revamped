/************************************************************
*															*
*	Assembler made by Tanya Sanjay Kumar and Vibhu Agrawal	*
*															*
************************************************************/

import java.io.*;
import java.util.ArrayList;
import java.util.HashMap;

class FileConversion{

	/********************************************************
	*		Class to handle Pass 0, Pass 1, Pass 2.			*
	*********************************************************/

	
	private boolean isComment(String line) {
		if(line.startsWith("//")) {
			return true;
		}
		
		return false;
	}
	
	private boolean isValidOpcode(String op) {
		if(Assembler.getOpcodesTable().containsKey(op)) {
			return true;
		}
		
		return false;
	}

	private String convert(String a) {
		int b = a.length();
		String o = "0";
		while(b<8) {
			a = o + a;
			b++;
		}
		return a;
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

	private int getIndexOFopcode(String[] all){
		for (int i = 0; i<all.length; i++){

			if (isValidOpcode(all[i])){
				return i;
			}
		}

		return -1;
	}

	public boolean zeroPass(String inputfile) throws FileNotFoundException, IOException{

		/****************************************
		*		MACRO EXPANSION OCCUS			*
		*		AND AN INTERMEDIATE FILE IS 	*
		*		GENERATED						*
		*****************************************/

		BufferedReader br = new BufferedReader(new FileReader(inputfile));
		BufferedWriter wr = new BufferedWriter(new FileWriter("intermediate_" + inputfile + ".txt"));

		MacroDefinitionTable mdt = new MacroDefinitionTable();
		boolean pass1eligible = true;

		boolean readNextLine = true;

		String line = "";
		String words[] = {};

		while (true){

			if (readNextLine){
				line = br.readLine();

				if (line == null){
					break;
				}

				line = line.trim();

				if (line.isBlank()){
					System.out.println("Warning: Blank Line Encountered.");
					continue;
				}

				if (isComment(line))
					continue;

				words = line.split(" ");

				if (words[0].equals("START") || line.equals("END")){
					wr.append(line);
					wr.newLine();

					if (line.equals("END")){
						break;
					}
					continue;
				}
			}
			
			if (words.length>1 && words[1].equals("MACRO")){

				String macro_name = words[0];
				ArrayList<String> params = new ArrayList<String>();
				ArrayList<String> label = new ArrayList<String>();

				if (words.length > 2){
					for (int i = 2; i < words.length; i++){
						if (!isComment(words[i])){
							params.add(words[i]);
						}
					}
				}

				String complete_macro = "";

				while (true){
					readNextLine = true;
					line = br.readLine();

					if (line == null){
						mdt.addMacro(macro_name, complete_macro, label, params);
						System.out.println("Warning: EOF reached. ENDM or MEND not found.");
						if (pass1eligible)
							wr.close();
						return pass1eligible;
					}

					line = line.trim();

					String l[] = line.split(":");
					if (l.length > 1){
						label.add(l[0]);
					}
					words = line.split(" ");


					if (words[0].equals("ENDM") || words[0].equals("MEND")){
						mdt.addMacro(macro_name, complete_macro, label, params);
						break;
					}else if (words.length>2){
						if (words[1].equals("ENDM") || words[1].equals("MEND")){
							mdt.addMacro(macro_name, complete_macro, label, params);
							break;
						}

						if (words[1].equals("MACRO")){
							mdt.addMacro(macro_name, complete_macro, label, params);
							System.out.println("Warning: MEND or ENDM not found. Next macro started.");
							readNextLine = false;
							break;
						}
					}
					
					complete_macro += line;
					complete_macro += "\n";

				}
			}else if(!isValidOpcode(words[0]) && !words[0].substring(words[0].length() - 1).equals(":")){
				if (mdt.isMacro(words[0])){
					ArrayList<String> params = new ArrayList<String>();
					if (words.length > 2){
						for (int i = 1; i < words.length; i++){
							if (!isComment(words[i]))
								params.add(words[i]);
						}
					}
					boolean checkExpansion = mdt.expandMacro(words[0], wr, params);

					if (!checkExpansion)
						pass1eligible = false;
				}else{
					System.out.println("ERROR: " + words[0] + ": Invalid OPCODE or Macro declaration found before definition. Terminating...");
					return false;
				}
			}else{
				wr.append(line);
				wr.newLine();
			}
		}

		if (pass1eligible)
			wr.close();
		return pass1eligible;

	}

	public boolean firstPass(String fileaddress) throws IOException,FileNotFoundException {


		/****************************************
		*	FIRST PASS TO CREATE SYMBOL AND 	*
		*	LABEL TABLES TO HANDLE FORWARD 		*
		*	REFERENCING.						*
		*****************************************/

		boolean errorFlag = false;
		BufferedReader br = new BufferedReader(new FileReader(fileaddress));
		int locationCounter = 0;
		SymbolTable sym = new SymbolTable();
		LabelTable lbl = new LabelTable();
		boolean firstLine = true;
		String lastLine = "";

		String line = "";

		while(true) {

			line = br.readLine();

			if(line == null) {
				break;
			}else{
				lastLine = line;
			}

			line = line.trim();

			if (firstLine){
				firstLine = false;
				String words[] = line.split(" ");
				if (words[0].equals("START")){
					if (words.length > 1){
						locationCounter = Integer.parseInt(words[1]);
					}
					continue;
				}else{
					System.out.println("Warning: START directive not found. Starting conversion from first line.");
				}
			}

			if (line.equals("END")){
				break;
			}

			if (line.isBlank()){
				System.out.println("Warning: Blank Line Encountered.");
				continue;
			}

			if (isComment(line)){
				continue;
			}

			String[] s = line.split(":");	

			if(s.length > 1) {
				if (s[0] == "R1" || s[0] == "R2"){
					System.out.println("ERROR: Reserved tokens R1 or R2 used as labels.");
					errorFlag = true;
				}else{
					if (!lbl.containsDefined(s[0]))
						lbl.add(s[0], convert(Integer.toBinaryString(locationCounter)));
					else{
						System.out.println("ERROR: Multiple definitions found for " + s[0] + " label.");
						errorFlag = true;
					}
				}				
			}

			String all[] = line.split(" ");

			for (String target: all){

				target = target.replace(":", "");

				if (!isValidOpcode(target) && !lbl.contains(target) && !isComment(target)){

					if (target.equals("R1")){
						sym.add(target, "000010001000");
					}else if (target.equals("R2")){
						sym.add(target, "000010001001");
					}else{

						try{
							int targInt = Integer.parseInt(target);
						}catch(Exception e){

							if (!sym.contains(target)){
								int index = getIndexOFopcode(all);

								if (index!=-1){
									if (all[index].equals("BRN") || all[index].equals("BRP") || all[index].equals("BRZ")){
										lbl.add(target, "----");
									}else{
										sym.add(target, "----");
									}
								}
							}	
						}					
					}
				}

			}

			int index = getIndexOFopcode(all);

			if (index != -1){

				if (all[index].equals("CLA") || all[index].equals("STP")){

					try{
						if (!isComment(all[index+1])){
							System.out.println("Warning: OPERAND supplied to " + all[index] + ", ignoring the operand.");
						}

					}catch(IndexOutOfBoundsException e){
						//No error;;
					}

				}else{
					
					try{

						String oper = all[index+1];

						if (isComment(oper))
							throw new IndexOutOfBoundsException();

						try{

							String oper2 = all[index+2];

							if (!isComment(oper2))
								System.out.println("Warning: Excess operands supplied to " + all[index] + ", ignoring excess operands.");
						}catch(IndexOutOfBoundsException e){
							//Not an error!!
						}

					}catch(IndexOutOfBoundsException e){
						System.out.println("ERROR: No operand supplied to " + all[index]);
						errorFlag = true;
					}
				}

			}else{
				int temp = locationCounter+1;
				System.out.println("ERROR: No valid opcode found in line " + temp);
				errorFlag = true;
			}
			
			locationCounter++;
		}

		if (!lastLine.equals("END")){
			System.out.println("Warning: END directive missing. Treating EOF as END.");
		}
		br.close();

		boolean comp_sym_table = sym.completeTable(locationCounter);

		if (!comp_sym_table){
			errorFlag = true;
			return errorFlag;
		}

		BufferedWriter lbl_sym_table = new BufferedWriter(new FileWriter("LabelNSymbolTable.txt"));

		boolean lblTabCheck = lbl.write(lbl_sym_table);

		if (!lblTabCheck){
			errorFlag = true;
			lbl_sym_table.close();
			return !errorFlag;			
		}

		boolean symTabCheck = sym.write(lbl_sym_table);

		if (!symTabCheck){
			errorFlag = true;
		}

		lbl_sym_table.close();

		return !errorFlag;

	}

	private Object[] readLblNSymTable(String tableName){

		try{
			SymbolTable st = new SymbolTable();
			LabelTable lt = new LabelTable();
			BufferedReader reSym = new BufferedReader(new FileReader(tableName));

			while (true){
				String line = reSym.readLine();

				if (line == null)
					break;

				if (line.equals("Variables"))
					break;

				String[] s = line.split(":");
				lt.add(s[0], s[1]);
			}

			while (true){
				String line = reSym.readLine();

				if (line == null)
					break;

				String[] s = line.split(":");
				st.add(s[0], s[1]);
			}

			Object tables[] = new Object[2];
			tables[0] = lt;
			tables[1] = st;

			return tables;

		}catch(IOException e){
			System.out.println("Error: Unable to read label and symbol table. Terminating...");
			return null;
		}
		
	}

	public void secondPass(String inpaddress,String outaddress) throws IOException,FileNotFoundException{

		/****************************************
		*	CONVERSION TO ACTUAL BINARY			*
		*****************************************/
		
		BufferedReader re = new BufferedReader(new FileReader(inpaddress));
		BufferedWriter wr = new BufferedWriter(new FileWriter(outaddress));
		int loc = 0;
		String line = null;
		String[] l;
		String opcode;
		String operand;
		String write;
		LabelTable lbl;
		SymbolTable sym;

		Object tables[] = readLblNSymTable("LabelNSymbolTable.txt");

		if (tables != null){
			if (tables[0] == null){
				return;
			}else{
				lbl = (LabelTable) tables[0];
			}

			if (tables[1] == null){
				return;
			}else{
				sym = (SymbolTable) tables[1];
			}
		}else{
			return;
		}
		

		while(true) {

			line = re.readLine();
			if (line == null) {
				break;
			}

			line = line.trim();

			if (line.isBlank()){
				continue;
			}

			l = line.split(" ");

			if (l[0].equals("START")){
				loc = Integer.parseInt(l[1]);
				continue;
			}

			if (isComment(line)) {
				continue;
			}

			if (line.equals("END")){
				wr.close();
				return;
			}

			l[0] = (l[0]).replace(":", "");

			if(lbl.contains(l[0])) {
				opcode = l[1];
				if(!opcode.equals("CLA") && !opcode.equals("STP"))
					operand = l[2];
				else
					operand = Integer.toString(4095);
			}

			else {
				opcode = l[0];
				if(!opcode.equals("CLA") && !opcode.equals("STP"))
					operand = l[1];
				else
					operand = Integer.toString(4095);
			}

			if(!opcode.equals("BRP") && !opcode.equals("BRN") && !opcode.equals("BRZ")){
				Integer operandInt = null;

				try{
					operandInt = Integer.parseInt(operand);

				}catch(Exception e){
				}
				
				if (operandInt != null){
					if (operandInt>4095){
						System.out.println("ERROR: Illegal memory access. Maximum allowable address is 4094. Used address: "  + operandInt);
						System.out.println("Terminating execution.");
						return;
					}else if(operandInt<0){
						System.out.println("ERROR: Negative memory addresses are not permissible.");
						System.out.println("Terminating execution.");
						return;
					}
					write = convert(Integer.toBinaryString(loc)) + Assembler.toBinary(opcode) + convertTwelveBits(Integer.toBinaryString(operandInt));
				}else{
					String varOperand = sym.getBinary(operand);
					write= convert(Integer.toBinaryString(loc)) + Assembler.toBinary(opcode) + varOperand;
				}
			}
			else {
				write = convert(Integer.toBinaryString(loc)) + Assembler.toBinary(opcode) + convertTwelveBits(lbl.getBinary(operand));
			}
			wr.append(write);
			wr.newLine();
			loc++;
		}

		wr.close();
	}
	
}