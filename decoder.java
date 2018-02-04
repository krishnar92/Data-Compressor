/*******************************************************************************
* Author: Krishna Ramesh                                                       *
* Date: 4/5/2017                                                               *
* Input: Code tabke file and encoded file                                      *
* Function: Reconstructs the huffman tree from the code table and traverse the *
* table repeatedly to decode the file                                          *
* Output: original input file to the encoder								   *
********************************************************************************/

import java.io.BufferedReader;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.io.PrintWriter;
import java.util.HashMap;
import java.util.Map;

public class decoder {
	String code_table_filename = null;
	String encoded_filename = null;
	public static void main(String args[]){
		String ct_filename = null;
		String enc_filename = null;
		if(args.length > 0){
			ct_filename = args[1];
			enc_filename = args[0];
		}
		decoder huffman_decoder = new decoder(ct_filename,enc_filename);
		huffman_decoder.begin_decode();
	}
	
	public decoder(String code_table_filename_a, String encoded_filename_a){
		this.code_table_filename = code_table_filename_a;
		this.encoded_filename = encoded_filename_a;
	}
	public void begin_decode(){
		String input = this.readFromFile();
		HuffmanNode huffmanTree = this.constructHuffmanTree();
		this.decode(huffmanTree, input);
	}
	public void decode(HuffmanNode tree, String input){
		// as each bit is encountered traverse the tree(left if 0 and right if 1)
		// stop when a leaf is encountered
		int i = 0;
		HuffmanNode iter = tree;
		try{
			PrintWriter p = new PrintWriter(new FileWriter("decoded.txt",true));
			while(i < input.length()){
			iter = tree;
				for(;i<input.length();++i){
					if(input.charAt(i)=='0'){
						iter = iter.left;
					}
					else if(input.charAt(i) == '1'){
						iter = iter.right;
					}
					if(iter.data != null){
						p.printf("%d\n", iter.data);
						break;
					}
				}
			++i;
			}
			p.close();
		}
		catch(Exception e){
			e.printStackTrace();
		}
	}
	public HuffmanNode constructHuffmanTree(){
		// traverse huffman code of all the elements, creating each node(if not present) on encountering each bit  
		HuffmanNode root = new HuffmanNode(-1);
		root.huffman_code = "";
		HashMap<Integer,String> ct = new HashMap<Integer,String>();
		BufferedReader br;
		String line;
		try {
			br = new BufferedReader(new FileReader(this.code_table_filename));
			while((line = br.readLine())!=null && !(line.isEmpty())){
				String[] temp = line.replace("\n", "").split(":");
				ct.put(Integer.parseInt(temp[0]), temp[1]);
			}
		} catch (FileNotFoundException e1) {
			e1.printStackTrace();
		}
		 catch (IOException e) {
			e.printStackTrace();
		}

		for (Map.Entry<Integer, String> entry : ct.entrySet()){
			int key = entry.getKey();
			String value = entry.getValue();
			HuffmanNode temp = root;
			for(int i = 0; i < value.length(); ++i){
				if(value.charAt(i) == '0'){
					if(temp.left == null){
						temp.left = new HuffmanNode(-1);
						temp.left.huffman_code = temp.huffman_code + "0";
					}
					temp = temp.left;
				}
				else if(value.charAt(i) == '1'){
					if(temp.right == null){
						temp.right = new HuffmanNode(-1);
						temp.right.huffman_code = temp.huffman_code + "1";
					}
					temp = temp.right;
				}
			}
			temp.data = key;
		}
		return root;
	}
	public String readFromFile(){
		final String BYTE_FORMAT = "%8s";
		final Character CHAR_BLANK = ' ';
		final Character CHAR_ZERO = '0';
		StringBuilder sb = new StringBuilder();
		String decodeString;
		File file = new File(this.encoded_filename);
		byte byteRead;
		try {
			FileInputStream fis = new FileInputStream(file);
			while (fis.available()>0) {
				byteRead = (byte)fis.read();
		        decodeString = String.format(BYTE_FORMAT, Integer.toBinaryString(byteRead & 0xFF)).replace(CHAR_BLANK,CHAR_ZERO);
		        sb.append(decodeString);
		        }
		fis.close();
		} catch (IOException e) {
			e.printStackTrace();
		}
      return sb.toString();
	}
}
