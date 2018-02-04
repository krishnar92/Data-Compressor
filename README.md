# Huffman Encoding

Huffman encoding is one of the most efficient algorithm used for compressing data.

Since construction of Huffman tree is all about repeatedly finding out the minimum pair of elements and combining them,
heaps can be used as they support efficient removal of minimum elements.

This project implements Huffman algorithm using two different data structures:
1) Binary heap
2) Pairing heap

## encoder.java

Input: file containing the stream of elements(in this case, numbers separated by whitespace)

Function: 
1) Calculate the frequency of occurence of each element in the input file
2) Create huffman tree through both the data structures and generate huffman code for each element
3) Encode the input file

Output:
1) An encoded file
2) Code table(elements and their corresponding codes)

## decoder.java

Input: Code table, encoded file

Function: 
1) Create huffman tree from the code table
2) Decode the encoded file to reconstruct the original input file

Output:
Original input file


