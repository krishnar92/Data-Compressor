/*******************************************************************************
* Author: Krishna Ramesh                                                       *
* Date: 4/5/2017                                                               *
* Input: Text file(with numbers)                                               *
* Function: Parses the text file and creates a Huffman Tree using either of the*
* data structures: Binary heap, Pairing Heap                                   *
* Output: Corresponding huffman codes for each numbers, code table is persisted*
* and is used later by the decoder to re-construct back the huffman tree       *
********************************************************************************/

import java.io.*;
import java.util.*;

public class encoder {

        BinaryHeap bheap = new BinaryHeap();
        FouraryHeap fheap = new FouraryHeap();
        PairingHeap pheap = new PairingHeap();
        String data_structure;

        // root of the node that has the actual information required for huffman encoding
        HuffmanNode huffmanTree;

        // elements of the huffman tree with corresponding huffman codes
        HashMap<Integer,String> code_table = new HashMap<Integer,String>();
         
        // input file
        String filename = null;

        public static void main(String[] args) {
                String filename = null;
                if(args.length > 0){
                        filename = args[0];
                }

                encoder huffman_encoder = new encoder(filename);
                
                // for pairing heap use "pairing_heap", for binary heap use "binary_heap"
                huffman_encoder.data_structure = "pairing_heap";

                huffman_encoder.begin_encode();
        }

        public encoder(String filename_a){
                this.filename = filename_a;
        }

        public void begin_encode(){
                // creates an unheapified structure containing huffman nodes(containing element and its frequency) 
                this.generate_frequency();

                // heapify the structure generate a huffman tree
                if(this.data_structure == "binary_heap"){
                        this.bheap.build_heap();
                        this.huffmanTree = this.bheap.generate_huffman_tree();
                }
                else if(this.data_structure == "pairing_heap"){
                        this.huffmanTree = this.pheap.generate_huffman_tree();
                }

                this.generateCodeTable(this.huffmanTree, "");
                this.encode();
                this.write_code_table();
        }
        public void write_code_table(){

                PrintWriter p;
                try {
                        p = new PrintWriter(new FileWriter("code_table.txt",true));
                        for (Map.Entry<Integer, String> entry : this.code_table.entrySet()){
                                // {element}:{huffman code}
                                p.printf("%d:%s\n", entry.getKey(), entry.getValue());
                        }
                        p.close();
                } catch (IOException e) {
                        e.printStackTrace();
                }
        }

        public void encode(){
                StringBuilder s = new StringBuilder();

                try{
                        BufferedReader br = new BufferedReader(new FileReader(this.filename));
                        String line,value;
                        while(((line=br.readLine())!=null) && !(line.isEmpty())){
                                int key = Integer.parseInt(line);
                                value = this.code_table.get(key);
                                s.append(value);
                        }
                        br.close();
                        s.toString();

                        // write bytewise
                        FileOutputStream fos = new FileOutputStream(new File("encoded.bin"));
                        for (int i = 0; i < s.length(); i += 8) {
                          String str= s.substring(i, i + 8); // grab a byte
                          int parsedByte = 0xFF & Integer.parseInt(str, 2);
                          fos.write(parsedByte); // write a byte
                    }
                        fos.flush();
                        fos.close();
                }
                catch(Exception e){
                        System.out.println(e.toString());
                }
        }

        public void generateCodeTable(HuffmanNode root, String prefix){
                if(root!=null){
                        if(root.data != null){
                                this.code_table.put(root.data, prefix);
                        }
                        this.generateCodeTable(root.left, prefix+"0");
                        this.generateCodeTable(root.right, prefix+"1");
                }
        }
        public void generate_frequency(){
                try{

                        BufferedReader br = new BufferedReader(new FileReader(new File(this.filename)));
                        
                        // the elements in the input file are no greater than 1000000
                        // the elements correspond to the index in the keys array and their frequency 
                        // is stored as the value at that array index
                        int keys[] = new int[1000000];

                        String s;
                        while(((s=br.readLine())!=null) && !(s.isEmpty())){
                                int key = Integer.parseInt(s);
                                keys[key] += 1;
                        }

                        // create a huffman node for each element and add it to the heap(without heapification)
                        for(int i = 0; i < keys.length; ++i){
                                if(keys[i] > 0){
                                        HuffmanNode node = new HuffmanNode(i,keys[i]);
                                        if(this.data_structure == "binary_heap"){
                                                this.bheap.heap.add(node);
                                        }
                                        else if(this.data_structure == "fourary_heap"){
                                                this.fheap.heap.add(node);
                                        }
                                        else if(this.data_structure == "pairing_heap"){
                                                this.pheap.insert(node);
                                        }
                                }
                        }
                        br.close();
                }
                catch(Exception e){
                        System.out.println(e.toString());
                }
        }
}

class HuffmanNode{
        Integer data;
        int frequency;
        String huffman_code;
        HuffmanNode left;
        HuffmanNode right;
        public HuffmanNode(int frequency_a){
                this.frequency = frequency_a;
                this.left = this.right = null;
                this.data = null;
        }
        public HuffmanNode(int data_a, int frequency_a){
                this(frequency_a);
                this.data = data_a;
        }
}

class PairingHeapNode {
//      int data;
//      int frequency;
        HuffmanNode hn;
        PairingHeapNode child;
        PairingHeapNode left;
        PairingHeapNode right;

        public PairingHeapNode(HuffmanNode hn_a){
                this.hn = hn_a;
                this.child = this.left = this.right = null;
        }
}

class BinaryHeap {
        ArrayList<HuffmanNode> heap = new ArrayList<HuffmanNode>();

        public void build_heap(){
                for (int i = (this.heap.size()/2) - 1; i >= 0; --i){
                        heapify(i);
                }
        }

        public void heapify(int i){
                int left = (2*i) + 1;
                int right = (2*i) + 2;
                int smallest;

                if((left < this.heap.size()) && (this.heap.get(left).frequency < this.heap.get(i).frequency)){
                        smallest = left;
                }
                else{
                        smallest = i;
                }

                if((right < this.heap.size()) && (this.heap.get(right).frequency < this.heap.get(smallest).frequency)){
                        smallest = right;
                }

                if(smallest != i){
                        HuffmanNode temp;
                        temp = this.heap.get(i);
                        this.heap.set(i,this.heap.get(smallest));
                        this.heap.set(smallest,temp);
                        this.heapify(smallest);
                }
        }

        public HuffmanNode remove_min(){

                HuffmanNode temp = this.heap.get(0);
                HuffmanNode last_ele = this.heap.get(this.heap.size()-1);
                this.heap.set(0, last_ele);
                this.heap.set(this.heap.size() - 1,temp);
                this.heap.remove(this.heap.size()-1);
                this.heapify(0);
                return temp;
        }

        public HuffmanNode generate_huffman_tree(){
                // huffman tree is generated through repeated removal of min frequency elements and then combining them
                // in each iteration two minimum elements are removed, combined and added back, reducing the heap size
                // by one in each iteration, ultimately ending up with a heap size of 1
                while((this.heap.size()) > 1){
                        HuffmanNode min1 = this.remove_min();
                        HuffmanNode min2 = this.heap.get(0); // avoids remove min to prevent heapification again

                        HuffmanNode combined = this.combine_nodes(min1, min2);

                        this.heap.set(0, combined);

                        this.heapify(0);
                }                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                            
                return this.heap.get(0);
        }

        public HuffmanNode combine_nodes(HuffmanNode a, HuffmanNode b){
                HuffmanNode combined = new HuffmanNode(a.frequency + b.frequency);
                combined.left = a;
                combined.right = b;
                return combined;
        }
}

class PairingHeap {
        PairingHeapNode root = null;

        public void insert(HuffmanNode node_a){
                PairingHeapNode node = new PairingHeapNode(node_a);
                if(this.root == null){
                        this.root = node;
                        return;
                }
                this.root = PairingHeap.meld(this.root, node);
        }

        public PairingHeapNode remove_min(){
                int no_of_children = 0;
                PairingHeapNode iter = this.root.child;
                PairingHeapNode retval = this.root;

                if(this.root.child == null){
                        this.root = null;
                        return retval;
                }
                while(iter != null){
                        no_of_children = no_of_children + 1;
                        iter = iter.right;
                }

                PairingHeapNode[] old_array = new PairingHeapNode[no_of_children];

                iter = this.root.child;

                for(int i = 0; i < old_array.length; ++i){
                        old_array[i] = iter;
                        iter = iter.right;
                }

                while(old_array.length > 1){
                        int new_array_length = (int)Math.ceil((((double)old_array.length))/2);

                        PairingHeapNode[] new_array = new PairingHeapNode[new_array_length];

                        int new_array_index, old_array_index;
                        new_array_index = old_array_index = 0;

                        while(old_array_index < old_array.length){
                                PairingHeapNode melded_node, node1, node2;

                                if((old_array_index+1) < old_array.length){
                                        node1 = old_array[old_array_index];
                                        node2 = old_array[old_array_index+1];
                                        node1.left = node2.left = node1.right = node2.right = null;
                                        melded_node = PairingHeap.meld(old_array[old_array_index], old_array[old_array_index+1]);
                                }
                                else{
                                        node1 = old_array[old_array_index];
                                        node1.left = node1.right = null;
                                        melded_node = node1;
                                }

                                new_array[new_array_index] = melded_node;
                                new_array_index = new_array_index + 1;
                                old_array_index = old_array_index + 2;
                        }

                        old_array = new_array;
                }

                this.root = old_array[0];
                this.root.left = null;
                retval.child = null;
                retval.right = null;
                return retval;
        }

        public static PairingHeapNode meld(PairingHeapNode node1, PairingHeapNode node2){
                PairingHeapNode retval;

                if(node1.hn.frequency < node2.hn.frequency){
                        node2.left = node1;
                        node2.right = node1.child;
                        node1.child = node2;
                        if(node2.right != null){
                                node2.right.left = node2;
                        }
                        retval = node1;
                }

                else{
                        node1.left = node2;
                        node1.right = node2.child;
                        node2.child = node1;
                        if(node1.right != null){
                                node1.right.left = node1;
                        }
                        retval = node2;
                }
                return retval;
        }

        public HuffmanNode generate_huffman_tree(){
                while(this.root.child != null){
                        PairingHeapNode min1 = this.remove_min();
                        PairingHeapNode min2 = this.remove_min();
                        this.insert(combine_nodes(min1.hn, min2.hn));
                }
                return this.root.hn;
        }

        public HuffmanNode combine_nodes(HuffmanNode a, HuffmanNode b){
                HuffmanNode combined = new HuffmanNode(a.frequency + b.frequency);
                combined.left = a;
                combined.right = b;
                return combined;
        }
}
