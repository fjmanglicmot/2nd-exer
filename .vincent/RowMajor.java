import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.net.ServerSocket;
import java.net.Socket;
import java.net.UnknownHostException;
import java.util.Random;
import java.util.Scanner;
import java.io.FileNotFoundException;
import java.io.File;

/* Author: Carpio, Vincent Paul L. 2012-46505 
 *  CMSC 180 Exercise 2: One to Many Personalized Broadcast and Many to One Reduction
 */

public class RowMajor {
	public static final int size = 1000;
	public static int[][]A1,A2, B1,B2, thisC4, thisC2, receivedC4, receivedC2, thisC;
	public static int id;
	public static String IP_P0 = "localhost", IP_P1 = "localhost", IP_P2 = "localhost", IP_P3 = "localhost";
	public static boolean computationFinished = false, receivedResultC4 = false, 
			receivedResultC2 = false, receivedA = false, receivedB = false;
	public static void main(String[] args) {
		File file = new File("config.txt");
		try{
			Scanner sc = new Scanner(file);
			IP_P0 = sc.nextLine();
			IP_P1 = sc.nextLine();
			IP_P2 = sc.nextLine();
			IP_P3 = sc.nextLine();
		}catch(FileNotFoundException e){
			e.printStackTrace();
		}
		
		try{
			id = Integer.parseInt(args[0]);
			//TODO: hostIP = args[1];
		}catch(ArrayIndexOutOfBoundsException e){
			System.out.println("Program usage: java SerialRowMajor <process_number>");
			e.printStackTrace();
		}
		
		switch(id){
			case 0:	initializeMatrix();					
					System.out.println("Initialized A1, A2, B1, B2");
					long startProgram = System.currentTimeMillis();
					try {
						ServerSocket server = new ServerSocket(2400);
						int peerCount = 0;
						while(peerCount!=2){
							Socket client = server.accept();
							
							peerCount++;
							System.out.println("Client Connected");


							new ClientThread(client,id).start();
							System.out.println("Thread started");
			
						}
						System.out.println("Matrix multiplication A1 x B1 initiated");
						thisC4 = matrixMultiplication(A1,B1);
						
						
						long startReduceC4,endReduceC4;
						while(true){ //while result is not received		
							
							try {
								Thread.sleep(100);
								//System.out.println(receivedResultC4);
							} catch (InterruptedException e) {}							
							if(receivedResultC4){  	//reduced thisC4 and receivedC4
								startReduceC4 = System.currentTimeMillis();
								System.out.println("Reduction of P0 and P1 initiated");
								thisC2 = new int[size/2][size];
								for(int i=0; i<size/2; i++){
									for(int j=0; j<size; j++){								
										if(j<size/2){
											thisC2[i][j] = thisC4[i][j];
										}else{
											thisC2[i][j] = receivedC4[i][j-(size/2)];
										}
									}
								}	
								endReduceC4 = System.currentTimeMillis();
								break;
							}
						}
						System.out.println("Successfully reduced result of P0 and P1 Time Elapsed: "+(endReduceC4-startReduceC4)+" ms");
						
						long startReduceC2, endReduceC2;
						while(true){
							
							try {
								Thread.sleep(100);
								//System.out.println(receivedResultC2);
							} catch (InterruptedException e) {}
							
							if(receivedResultC2){  	//reduced thisC2 and receivedC2
								startReduceC2 = System.currentTimeMillis();
								System.out.println("Reduction of (P0 P1) and (P2 P3) initiated");
								thisC = new int[size][size];
								for(int i=0; i<size; i++){
									for(int j=0; j<size; j++){	
										if(i<size/2){											
											thisC[i][j] = thisC2[i][j];											
										}else{
											thisC[i][j] = receivedC2[i-size/2][j];
										}
										
									}
								}	
								endReduceC2 = System.currentTimeMillis();
								break;
							}
						}
						System.out.println("Successfuly reduced results to C Time Elapsed: "+(endReduceC2-startReduceC2)+" ms");
						
					} catch (IOException e) {
						e.printStackTrace();
					}
					long endProgram = System.currentTimeMillis();
					System.out.println("Total time elapsed = "+(endProgram-startProgram)+" ms");
					return;
					
			case 1: try {
						Socket socketToP0;
						while(true){
							try{
								socketToP0 = new Socket(IP_P0, 2400); //connect to process 1
								break;
							}catch(IOException e){
								//System.out.println("Trying to connect to P0");
								continue;
							}
						}
						
						ObjectOutputStream outToP0 = new ObjectOutputStream(socketToP0.getOutputStream());
						
						outToP0.writeObject(id); //introduce to P0
						System.out.println("Introduced to P0");
						ObjectInputStream inToP0 = new ObjectInputStream(socketToP0.getInputStream());						
						
						while(true){ //Blocking
							try{
								A1 = (int[][])inToP0.readObject();
								receivedA = true;
								System.out.println("Received A1 from P0");
								break;
							}catch(IOException e){continue;}
						}
						
						while(true){ //Blocking
							try{
								B2 = (int[][])inToP0.readObject();			
								receivedB = true;						
								System.out.println("Received B2 from P0");
								break;
							}catch(IOException e){continue;}
						}
						outToP0.writeObject(true);
						
						
						
						Socket socketToP3;
						while(true){
							try{
								socketToP3 = new Socket(IP_P3,2403);
								break;
							}catch(IOException e){
								//System.out.println("Trying to connect to P3");
								continue;
							}
							
						}						
						ObjectOutputStream outToP3 = new ObjectOutputStream(socketToP3.getOutputStream());
						outToP3.writeObject(id); //introduce to P3
						ObjectInputStream inToP3 = new ObjectInputStream(socketToP3.getInputStream());
						System.out.println("Introduced to P3");
						long startSendP1_P3 = System.currentTimeMillis();
						outToP3.writeObject(B2); //send B2 to P3					
						inToP3.readObject();
						long endSendP1_P3 = System.currentTimeMillis();
						System.out.println("Sent B2 to P3 Time Elapsed: "+(endSendP1_P3-startSendP1_P3)+" ms");
						
						
						
						//close streams and socket for P3
						//TODO: receive a confirmation before closing socket
						//outToP3.close();
						//socketToP3.close();
						//System.out.println("Closed connections to P3");
						
						//matrix multiplication
						System.out.println("Matrix mutliplcation A1 x B2 initiated");
						thisC = matrixMultiplication(A1,B2);
						
						long startsendP1_P0 = System.currentTimeMillis();
						outToP0.writeObject(thisC); //send result to P0
						inToP0.readObject();
						long endsendP1_P0 = System.currentTimeMillis();
						System.out.println("Sent Result of A1 x B2 to P0 Time Elapsed: "+(endsendP1_P0-startsendP1_P0)+" ms");
						
						//close streams and socket for P0
						/*
						inToP0.close();
						outToP0.close();
						socketToP0.close();
						*/
						System.out.println("Closed connections to P0");
						
					} catch (UnknownHostException e) {
						e.printStackTrace();
					} catch (IOException e) {
						e.printStackTrace();
					} catch (ClassNotFoundException e) {
						e.printStackTrace();
					}
					return;
			case 2: try {
						Socket socketToP0;
						
						while(true){
							try{
								socketToP0 = new Socket(IP_P0,2400);
								break;
							}catch(IOException e){
								//System.out.println("Trying to connect to P0");
								continue;
							}
						}
						
						
						
						ObjectInputStream inToP0 = new ObjectInputStream(socketToP0.getInputStream());
						ObjectOutputStream outToP0 = new ObjectOutputStream(socketToP0.getOutputStream());
						
						outToP0.writeObject(id); //introduce to P0
						System.out.println("Introduce to P0");
						
						while(true){ //Blocking
							try{
								A2 = (int[][])inToP0.readObject();
								receivedA = true;
								System.out.println("Received A2 from P0");
								break;
							}catch(IOException e){continue;}
						}
						
						while(true){ //Blocking
							try{
								B1 = (int[][])inToP0.readObject();
								receivedB = true;
								System.out.println("Received B1 from P0");
								break;
							}catch(IOException e){continue;}
						}
						outToP0.writeObject(true);
						
						Socket socketToP3;
						while(true){
							try{
								socketToP3 = new Socket(IP_P3,2403);
								break;
							}catch(IOException e){
								//System.out.println("Trying to connect to P3");
								continue;
							}
						}
						
						new ClientThread(socketToP3,id).start();
						System.out.println("Matrix multiplication A2 x B1 initiated");
						thisC4 = matrixMultiplication(A2,B1);					
						
						System.out.println("Entering blocking loop");
						long startReduceC4, endReduceC4,startSendC2,endSendC2;
						while(true){ //while result is not received		
							
							try {
								Thread.sleep(100);
								//System.out.println(receivedResultC4);
							} catch (InterruptedException e) {
								
								e.printStackTrace();
							}
							if(receivedResultC4){  	//reduced thisC and receivedC
								startReduceC4 = System.currentTimeMillis();
								System.out.println("Reduction of P2 and P3 initiated");
								thisC2 = new int[size/2][size];
								for(int i=0; i<size/2; i++){
									for(int j=0; j<size; j++){								
										if(j<size/2){
											thisC2[i][j] = thisC4[i][j];
										}else{
											thisC2[i][j] = receivedC4[i][j-(size/2)];
										}
									}
								}	
								endReduceC4 = System.currentTimeMillis();
								
								startSendC2 = System.currentTimeMillis();
								outToP0.writeObject(thisC2);
								endSendC2 = System.currentTimeMillis();
								break;
							}
						}	
						System.out.println("Exited blocking loop");
						System.out.println("Successfully reduced P2 and P3 Time Elapsed: "+(endReduceC4-startReduceC4)+" ms");
						
						//close streams and socket for P0
						/*
						inToP0.close();
						outToP0.close();
						socketToP0.close();*/
						System.out.println("Sent reduced P2 and P3 to P0 Time Elapsed: "+(endSendC2-startSendC2)+" ms");
					} catch (UnknownHostException e) {
						e.printStackTrace();
					} catch (IOException e) {
						e.printStackTrace();
					} catch (ClassNotFoundException e) {
						e.printStackTrace();
					}
					break;
			case 3: try {
						ServerSocket server = new ServerSocket(2403);
						int peerCount = 0;
						while(peerCount!=2){
							Socket client = server.accept();
							
							peerCount++;
							System.out.println("Client Connected");
		
							new ClientThread(client,id).start();
						}
						/*
						while(true){
							if(receivedA&&receivedB){
								System.out.println("Matrix multiplcation A2 x B2 initiated");
								thisC4 = matrixMultiplication(A2,B2);
								break;
							}
						}
						*/
						/*
						while(true){
							if(terminateP3ServerAck){								
								server.close();								
								break;
							}
						}
						System.out.println("P3 Server closed");*/
						
					} catch (IOException e) {
						e.printStackTrace();
					}
					break;
		
		}

	}
	
	
	public static void initializeMatrix(){
		Random random = new Random();
		A1 = new int[size/2][size];
		A2 = new int[size/2][size];
		B1 = new int[size][size/2];
		B2 = new int[size][size/2];
		for(int i=0; i<size; i++){
			for(int j=0; j<size; j++){
				if(i<size/2){
					A1[i][j] = random.nextInt(20)+1;
					A2[i][j] = random.nextInt(20)+1;
				}
				if(j<size/2){
					B1[i][j] = random.nextInt(20)+1;
					B2[i][j] = random.nextInt(20)+1;
				}
			}
		}		
	}
	
	
	public static int[][] matrixMultiplication(int [][]a, int [][]b){
		int[][] result = new int[size/2][size/2];
		
		long startMatrixMat = System.currentTimeMillis();
		
		for(int i=0; i<size/2; i++){
			//verbose
			if(i==size/8) System.out.println("Matrix multiplcation at 25%");
			else if(i==size/4) System.out.println("Matrix multiplcation at 50%");
			else if(i==3*size/8) System.out.println("Matrix multiplcation at 75%");
			
			for(int j=0; j<size/2; j++){
				result[i][j] = 0;
				for(int k=0; k<size; k++){
					result[i][j]  += a[i][k] * b[k][j];
				}			
			}		
		}
		long endMatrixMat = System.currentTimeMillis();
		long computeTime = endMatrixMat - startMatrixMat;
		computationFinished = true;
		System.out.println("Computation Finished Time Elapsed: "+computeTime+" ms");
		return result;
	}
	
	private static class ClientThread extends Thread {
		private Socket socket;
        private ObjectInputStream in;
        private ObjectOutputStream out;
        private int type;
 
        
		public ClientThread(Socket socket, int type) {
			this.socket = socket;  
			this.type = type;
		}
		
		public void run(){
			//System.out.println("Streams creation");
            try {
            	out = new ObjectOutputStream(socket.getOutputStream());
            	out.flush();
				in = new ObjectInputStream(socket.getInputStream());
				
				//System.out.println("Streams created");
				if(type==0){
					int x;
					while(true){
						try{
							x = (int)in.readObject();
							break;
						}catch(IOException e){continue;}
					}
					
					switch(x){
						case 1: long startSendP0_P1 = System.currentTimeMillis();
								out.writeObject(A1);
								out.writeObject(B2);
								boolean ackP0_P1 = (boolean) in.readObject();
								long endSendP0_P1 = System.currentTimeMillis();
								long sendTimeP0_P1 = endSendP0_P1-startSendP0_P1;
								System.out.println("Sent A1 and B2 to P1 Time Spent: "+sendTimeP0_P1+" ms");								
								
								
								while(true){
									try{
										receivedC4 = (int[][])in.readObject();
										out.writeObject(true);
										receivedResultC4 = true;
										System.out.println("Received result of P1");
										break;
									}catch(IOException e){continue;}
								}
								
								//TODO: receive result and reduce with computed for P0
								break;
						case 2: long startSendP0_P2 = System.currentTimeMillis();
								out.writeObject(A2);
								out.writeObject(B1);
								boolean ackP0_P2 = (boolean) in.readObject();
								long endSendP0_P2 = System.currentTimeMillis();
								long sendTimeP0_P2 = endSendP0_P2-startSendP0_P2;
								
								System.out.println("Sent A2 and B1 to P2 Time Spent: "+sendTimeP0_P2+" ms");
								//TODO: receive result and reduce with reduced P0 and P1
								while(true){ //Blocking
									try{
										receivedC2 = (int[][])in.readObject();
										receivedResultC2 = true;
										out.writeObject(true);										
										System.out.println("Received result of reduced P2 and P3");
										break;
									}catch(IOException e){continue;}
								}
								
								break;
						case 3: break;
					}
					
				}else if(type==2){		
					long startSendP2_P3 = System.currentTimeMillis();
					out.writeObject(id); //introduce to P3
					System.out.println("Introduce to P3");
					out.writeObject(A2); //send A2 to P3			
					
					in.readObject();
					long endSendP2_P3 = System.currentTimeMillis();
					System.out.println("Sent A2 to P3 Time Elapsed: "+(endSendP2_P3-startSendP2_P3)+" ms");
					
					System.out.println("Entering blocking loop for receiving result of P3");
					while(true){ //Blocking
						
						try {
							Thread.sleep(100);
						} catch (InterruptedException e) {}
						
						try{
							receivedC4 = (int[][])in.readObject();
							System.out.println("Received result of P3");							
							receivedResultC4 = true;
							out.writeObject(true);
							break;
						}catch(IOException e){continue;}
						
					}
					System.out.println("Exited loop for P3");
				}else if(type==3){
					int y;
					while(true){						
						try{
							y = (int)in.readObject();
							break;
						}catch(IOException e){continue;}
					}
					if(y==1){
						while(true){
							try{
								B2 = (int[][])in.readObject();
								
								receivedB = true;
								out.writeObject(true);
								System.out.println("Received B2 from P1");
								break;
							}catch(IOException e){continue;}
						}
						
					
					}else {
						while(true){
							try{
								A2 = (int[][])in.readObject();	
								
								receivedA = true;
								out.writeObject(true);
								System.out.println("Received A2 from P2");
								break;
							}catch(IOException e){continue;}
						}
						
						System.out.println("Entering blocking loop");
						long startSendP3_P2, endSendP3_P2;
						while(true){
							
							try {
								Thread.sleep(100);
								//System.out.println("receivedA : "+receivedA+", receivedB : "+receivedB);
							} catch (InterruptedException e) {}
							if(receivedA&&receivedB){
								System.out.println("Matrix multiplcation A2 x B2 initiated");	
								thisC4 = matrixMultiplication(A2,B2);		
								startSendP3_P2 = System.currentTimeMillis();						
								out.writeObject(thisC4);	
								
								break;
							}
						}
						System.out.println("Exited blocking loop");

						boolean dummy = (boolean)in.readObject();
						endSendP3_P2 = System.currentTimeMillis();
						System.out.println("Sent result to P2 Time Elapsed: "+(endSendP3_P2-startSendP3_P2));
					}
				}
				
			} catch (IOException | ClassNotFoundException e) {
				e.printStackTrace();
			} finally{
				try {
					in.close();
					out.close();
					socket.close();
				} catch (IOException e) {
					e.printStackTrace();
				}
				
				
			}
            
          
		}
	}
	
}
