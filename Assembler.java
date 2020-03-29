/************************************************************
*															*
*	Assembler made by Tanya Sanjay Kumar and Vibhu Agrawal	*
*															*
************************************************************/


import java.io.*;
import java.util.ArrayList;
import java.util.HashMap;

public class Assembler {

	/****************************************************
	*	THE ASSEMBLER HANDLES OPCODES AS DEFINED		*
	*	IN CONSTRUCTOR OF THIS CLASS.					*
	*													*
	*	MAPPING TO VIRTUAL MEMORY ADDRESSES IS ALSO		*
	*	PERFORMED. 										*
	*													*
	*	THE FORMAT OF THE OUTPUT FILE IS AS FOLLOWS:	*
	*	FIRST 8 BITS: VIRTUAL ADDRESS ASSIGNED.			*
	*	NEXT 4 BITS: OPCODE 							*
	*	NEXT 12 BITS: OPERAND ADDRESS					*
	*****************************************************/
	
	private static HashMap<String,String> opcodesTable;
	
	public Assembler() throws IOException {

		opcodesTable = new HashMap<String,String>();

		opcodesTable.put("CLA", "0000");
		opcodesTable.put("LAC", "0001");
		opcodesTable.put("SAC", "0010");
		opcodesTable.put("ADD", "0011");
		opcodesTable.put("SUB", "0100");
		opcodesTable.put("BRZ", "0101");
		opcodesTable.put("BRN", "0110");
		opcodesTable.put("BRP", "0111");
		opcodesTable.put("INP", "1000");
		opcodesTable.put("DSP", "1001");
		opcodesTable.put("MUL", "1010");
		opcodesTable.put("DIV", "1011");
		opcodesTable.put("STP", "1100");
	}
	
	public static String toBinary(String instr) {
		return opcodesTable.get(instr);
	}
	
	public static HashMap<String,String> getOpcodesTable() {
		return opcodesTable;
	}
	
	
	public static void main(String[] args) throws IOException {


		InputStreamReader i = new InputStreamReader(System.in);
		BufferedReader br = new BufferedReader(i);

		String inp;
		String out;

		if (args.length == 0){
			System.out.println("Enter your input file:");
			inp = br.readLine();
			System.out.println("Enter your output file:");
			out = br.readLine();

		}else if (args.length == 1){
			inp = args[0];
			System.out.println("Enter your output file:");
			out = br.readLine();

		}else if (args.length == 2){
			inp = args[0];
			out = args[1];

		}else{
			System.out.println("Warning: Too many arguments... Taking first two as input and output file.");
			inp = args[0];
			out = args[1];
		}


		File inpFile = new File(inp);

		if (!inpFile.exists()){
			System.out.println("No such input file exists. Terminating execution.");
			return;
		}

		if (!inpFile.canRead()){
			System.out.println("Input file does not provide read permissions. Terminating execution.");
			return;
		}

		File outFile = new File(out);

		if (!outFile.exists()){
			System.out.println("No such output file exists. Defaulting to output.txt.");
			out = "output.txt";

		}else if (!outFile.canWrite()){
			System.out.println("Given output file does not provide write permissions. Defaulting to output.txt");
			out = "output.txt";
		}



		Assembler asmb = new Assembler();
		FileConversion fileTOconvert = new FileConversion();

		try{
			boolean moveTOpass1 = fileTOconvert.zeroPass(inp);

			if (moveTOpass1){
				boolean success = fileTOconvert.firstPass("intermediate_" + inp + ".txt");
				if (success)
					fileTOconvert.secondPass("intermediate_" + inp + ".txt", out);

				else{
					System.out.println("Rectify errors and then retry!!");
				}
			
			}else{
				System.out.println("Rectify errors and then retry!!");
			}
		}catch(Exception e){
			System.out.println("An error occured. " + e.getMessage());
		}
		
		
	}

}