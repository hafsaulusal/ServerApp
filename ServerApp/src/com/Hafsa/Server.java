package com.Hafsa;


	import java.io.BufferedReader;
	import java.io.IOException;
	import java.io.InputStreamReader;
	import java.io.PrintWriter;
	import java.net.ServerSocket;
	import java.net.Socket;
	import java.util.HashSet;

	
	public class Server {

		//443 numaral� g�venli PORT 
	    private static final int PORT = 443;
   
	    //ayn� kullan�c� ad� bulunmamas� i�in Hashset tan�mland�
	    private static HashSet<String> names = new HashSet<String>();
	    
	    private static HashSet<PrintWriter> writers = new HashSet<PrintWriter>();
	    
	    public static void main(String[] args) throws Exception {
	        System.out.println("Sunucu �al��t�r�l�yor...");
	        ServerSocket listener = new ServerSocket(PORT);  //port dinleniyor
	        try {
	            while (true) {
	                new Handler(listener.accept()).start();//beklemekte olan �a�r� kabul edildi ve thread s�n�f�na ait start() metodu �al��t�r�ld�
	            }
	        } finally { 
	            listener.close();
	        }
	        
	       
	    }

	    
	    private static class Handler extends Thread {
	        private String name;
	        private Socket socket;
	        private BufferedReader in;
	        private PrintWriter out;

	        
	        public Handler(Socket socket) {
	            this.socket = socket;
	        }

	       
	        public void run() {
	            try {

	                in = new BufferedReader(new InputStreamReader(
	                    socket.getInputStream()));
	                out = new PrintWriter(socket.getOutputStream(), true);

	               
	                while (true) {
	                    out.println("SUBMITNAME");
	                    name = in.readLine();
	                    if (name == null) {
	                        return;
	                    }
	                    synchronized (names) { //threadlerin kilitlenmesini �nledim
	                        if (!names.contains(name)) {
	                            names.add(name);
	                            break;
	                        }
	                    }
	                }

	                
	                out.println("NAMEACCEPTED");
	                writers.add(out);

	              
	                while (true) {
	                    String input = in.readLine(); //sat�r sat�r yazd�rma
	                    if (input == null) {
	                        return;
	                    }
	                    for (PrintWriter writer : writers) {
	                        writer.println("MESSAGE " + name + ": " + input);//mesaj� g�nderen ve mesaj yazd�r�l�yor
	                    }
	                }
	            } catch (IOException e) {
	                System.out.println(e);
	            } finally {
	                
	                if (name != null) {
	                    names.remove(name);
	                }
	                if (out != null) {
	                    writers.remove(out);
	                }
	                try {
	                    socket.close();
	                } catch (IOException e) {
	                }
	            }
	        }
	    }
	}

