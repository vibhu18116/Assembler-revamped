# Assembler-revamped
A 2-pass assembler implemented in Java

Assembler is a piece of code that converts assembly level language instructions to
corresponding machine code for the computer hardware to understand and execute the
actual instruction. To perform the conversion, the assembler scans through the file line
by line. Every scan through the file is termed as a pass. Assemblers can be
implemented in 1-pass, 2-pass and multi-pass formats. However in one pass, the object
code created is directly bound to physical memory addresses and it does not provide
the flexibility of relocation. In 2-pass assembler, relocation can occur as only virtual
memory mapping is done in the object code. The binding to physical addresses occurs
during the loading phase which is performed by the loader. In multi-pass, the further
passes are meant for code optimisation to reduce usage of hardware resources.

**Running The Project:**

Once all the Java files have been compiled to produce corresponding class files, one of
the following methods can be used to invoke the assembler:
1. `java Assembler <input_filename.txt> <output_filename.txt>`
2. `java Assembler <input_filename.txt>`
The user is prompted to enter output file name.
3. `java Assembler`
The user is prompted to enter both input and output file name.
