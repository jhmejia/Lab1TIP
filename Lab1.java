import javax.swing.*;
import java.awt.event.*;
import java.util.*;

public class Lab1 extends JFrame implements ActionListener {
	private JTextField assemblerInstruction;
	private JTextField binaryInstruction;
	private JTextField hexInstruction;
	private JLabel errorLabel;
	
	public Lab1() {
		setTitle("PDP-11");
		setBounds(100, 100, 400, 300);
		setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
		getContentPane().setLayout(null);

		assemblerInstruction = new JTextField();
		assemblerInstruction.setBounds(25, 24, 134, 28);
		getContentPane().add(assemblerInstruction);
		assemblerInstruction.setColumns(10);

		JLabel lblAssemblyLanguage = new JLabel("Assembly Language");
		lblAssemblyLanguage.setBounds(25, 64, 160, 16);
		getContentPane().add(lblAssemblyLanguage);

		binaryInstruction = new JTextField();
		binaryInstruction.setBounds(25, 115, 180, 28);
		getContentPane().add(binaryInstruction);
		binaryInstruction.setColumns(10);

		hexInstruction = new JTextField();
		hexInstruction.setBounds(236, 115, 134, 28);
		getContentPane().add(hexInstruction);
		hexInstruction.setColumns(10);

		JLabel lblBinary = new JLabel("Binary Instruction");
		lblBinary.setBounds(25, 155, 190, 16);
		getContentPane().add(lblBinary);

		JLabel lblHexEquivalent = new JLabel("Hex Instruction");
		lblHexEquivalent.setBounds(236, 155, 131, 16);
		getContentPane().add(lblHexEquivalent);

		errorLabel = new JLabel("");
		errorLabel.setBounds(25, 235, 280, 16);
		getContentPane().add(errorLabel);

		JButton btnEncode = new JButton("Encode");
		btnEncode.setBounds(230, 25, 117, 29);
		getContentPane().add(btnEncode);
		btnEncode.addActionListener(this);

		JButton btnDecode = new JButton("Decode Binary");
		btnDecode.setBounds(30, 183, 170, 29);
		getContentPane().add(btnDecode);
		btnDecode.addActionListener(this);

		JButton btnDecodeHex = new JButton("Decode Hex");
		btnDecodeHex.setBounds(230, 183, 150, 29);
		getContentPane().add(btnDecodeHex);
		btnDecodeHex.addActionListener(this);
	}

	public void actionPerformed(ActionEvent evt) {
		errorLabel.setText("");
		if (evt.getActionCommand().equals("Encode")) {
			encode();
		} else if (evt.getActionCommand().equals("Decode Binary")) {
			decodeBin();
		} else if (evt.getActionCommand().equals("Decode Hex")) {
			decodeHex();
		}
	}

	public static void main(String[] args) {
		Lab1 window = new Lab1();
		window.setVisible(true);
	}

	String shortToHex(short x) {
		String ans="";
		for (int i=0; i<4; i++) {
			int hex = x & 15;
			char hexChar = "0123456789ABCDEF".charAt(hex);
			ans = hexChar + ans;
			x = (short)(x >> 4);
		}
		return ans;
	}

	String shortToBinary(short x) {
		String ans="";
		for(int i=0; i<16; i++) {
			ans = (x & 1) + ans;
			x = (short)(x >> 1);
		}
		return ans;
	}
	
/************************************************************************/
/* Put your implementation of the encode, decodeBin, and decodeHex      */
/* methods here. You may add any other methods that you think are       */
/* appropriate. However, you should not change anything in the code     */
/* that I have written.                                                 */
/************************************************************************/
	/**
	 * Lab 1 Code
	 * Author: John Henry Mejia
	 * Sources: Previous lecture materials
	 * 
	 */
	
	
	/**
	 * Encode
	 * 
	 */
	void encode() {
	    this.binaryInstruction.setText("");
	    this.hexInstruction.setText("");
	    this.errorLabel.setText("");
	    String assem = this.assemblerInstruction.getText().trim().toUpperCase(); //In case it is upper case
	    StringTokenizer tokenize = new StringTokenizer(assem);
	    if (tokenize.countTokens() != 2) {
	      this.errorLabel.setText("Illegal format for assembly instruction");
	      return;
	    } 
	    String op = tokenize.nextToken();
	    int binary = 0;
	    if (op.equals("ADD")) {
	      binary = 24576;
	    } else if (op.equals("SUB")) {
	      binary = 57344;
	    } else if (op.equals("MOV")) {
	      binary = 16384;
	    } else if (op.equals("MOVB")) {
	      binary = 49152;
	    } else if (op.equals("CMP")) {
	      binary = 0;
	    } else if (op.equals("CMPB")) {
	      binary = 32768;
	    } else {
	      this.errorLabel.setText("Illegal operation for assembly instruction");
	      return;
	    } 
	    String operands = tokenize.nextToken();
	    tokenize = new StringTokenizer(operands, ",", true);
	    if (tokenize.countTokens() != 3 || operands.endsWith(",")) {
	      this.errorLabel.setText("Illegal format for assembly instruction");
	      return;
	    } 
		
	    String source = tokenize.nextToken();
		//Calling the method for 2nd part 
	    binary |= encodeRegister(source) << 6; //Shifts the results 6 bits
	    tokenize.nextToken();
	    String destination = tokenize.nextToken();
		//Calling method for 3rd part
	    binary |= encodeRegister(destination); 
	    if (this.errorLabel.getText().equals("")) {
	      this.binaryInstruction.setText(shortToBinary((short)binary));
	      this.hexInstruction.setText(shortToHex((short)binary));
	    } 
	  }
	 /**
	  * 
	  * @param register
	  * Options: (Rn), -(Rn), (Rn)+
	  * @return
	  */ 
	  int encodeRegister(String register) {
	    int registerCode = 0;
	    char registerNum = '0';
	    if (register.charAt(0) == '-') {
	      if (register.length() != 5 || register.charAt(1) != '(' || register.charAt(4) != ')' || 
	        register.charAt(2) != 'R') {
	        this.errorLabel.setText("Illegal register specs");
	        return 0;
	      } 
	      registerCode = 32;
	      registerNum = register.charAt(3);
	    } else if (register.endsWith("+")) {
	      if (register.length() != 5 || register.charAt(0) != '(' || register.charAt(3) != ')' || 
	        register.charAt(1) != 'R') {
	        this.errorLabel.setText("Illegal register specs");
	        return 0;
	      } 
	      registerCode = 16;
	      registerNum = register.charAt(2);
	    } else if (register.charAt(0) == '(') {
	      if (register.length() != 4 || register.charAt(3) != ')' || register.charAt(1) != 'R') {
	        this.errorLabel.setText("Illegal register specs");
	        return 0;
	      } 
	      registerCode = 8;
	      registerNum = register.charAt(2);
	    } else if (register.charAt(0) == 'R') {
	      if (register.length() != 2) {
	        this.errorLabel.setText("Illegal register specs");
	        return 0;
	      } 
	      registerNum = register.charAt(1);
	    } else {
	      this.errorLabel.setText("Illegal register specs");
	      return 0;
	    } 
	    if (registerNum < '0' || registerNum > '7') {
	      this.errorLabel.setText("Illegal register number");
	      return 0;
	    } 
	    return registerCode + Integer.parseInt(registerNum);
	  }
	 /**
	  * DecodeBin
	  */
	  void decodeBin() {
	    int binaryNum;
	    this.assemblerInstruction.setText("");
	    this.hexInstruction.setText("");
	    this.errorLabel.setText("");
	    String s = this.binaryInstruction.getText().trim();
	    if (s.length() != 16) {
	      this.errorLabel.setText("Binary number must be 16 digits!");
	      return;
	    } 
	    try {
	      binaryNum = Integer.parseInt(s, 2);
	    } catch (Exception e) {
	      this.errorLabel.setText("Illegal binary number");
	      return;
	    } 
	    this.hexInstruction.setText(shortToHex((short)binaryNum));
		//This will decode the binary number
	    decode(binaryNum);
	  }
	  /**
	   * Decode Hex
	   */
	  void decodeHex() {
	    int binary;
	    this.assemblerInstruction.setText("");
	    this.binaryInstruction.setText("");
	    String s = this.hexInstruction.getText().trim();
	    if (s.length() != 4) {
	      this.errorLabel.setText("Hex number must be 4 digits");
	      return;
	    } 
	    try {
	      binary = Integer.parseInt(s, 16);
	    } catch (Exception e) {
	      this.errorLabel.setText("Illegal hex number");
	      return;
	    } 
	    this.binaryInstruction.setText(shortToBinary((short)binary));
	    decode(binary);
	  }
	  
	  void decode(int binary) {
	    if ((binary & 0xFFFF0000) != 0) {
	      this.errorLabel.setText("Illegal instruction");
	      return;
	    } 
	    String assem = "";
	    if ((binary & 0xF000) == 24576) {
	      assem = "ADD ";
	    } else if ((binary & 0xF000) == 57344) {
	      assem = "SUB ";
	    } else if ((binary & 0xF000) == 16384) {
	      assem = "MOV ";
	    } else if ((binary & 0xF000) == 49152) {
	      assem = "MOVB ";
	    } else if ((binary & 0xF000) == 0) {
	      assem = "CMP ";
	    } else if ((binary & 0xF000) == 32768) {
	      assem = "CMPB ";
	    } else {
	      this.errorLabel.setText("Illegal instruction");
	      return;
	    } 
	    int source = binary >> 6 & 0x3F;
	    assem = String.valueOf(assem) + registerEncode(source);
	    int destination = binary & 0x3F;
	    assem = String.valueOf(assem) + "," + registerEncode(destination);
	    if (this.errorLabel.getText().equals(""))
	      this.assemblerInstruction.setText(assem); 
	  }
	  
	  String registerEncode(int register) {
	    String code = "";
	    code = Integer.toString(register & 0x7);
	    int mode = register >> 3 & 0x7;
	    if (mode == 0) {
	      code = "R" + code;
	    } else if (mode == 1) {
	      code = "(R" + code + ")";
	    } else if (mode == 2) {
	      code = "(R" + code + ")+";
	    } else if (mode == 4) {
	      code = "-(R" + code + ")";
	    } else {
	      this.errorLabel.setText("Illegal instruction");
	    } 
	    return code;
	  }
}
