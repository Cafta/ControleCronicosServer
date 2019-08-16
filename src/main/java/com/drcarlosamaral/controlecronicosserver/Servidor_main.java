package com.drcarlosamaral.controlecronicosserver;

import java.io.IOException;
import java.io.InputStream;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.io.OutputStream;
import java.net.ServerSocket;
import java.net.Socket;
import java.net.UnknownHostException;
import java.util.HashSet;
import java.util.Set;

public class Servidor_main {

	private static Set<Integer> portasLivres;
	private static Object sync1 = new Object();
	private static Object sync2 = new Object();
	private static Object sync3 = new Object();
	
	public static void main(String[] args) {
		
		// Testando a conecção com o banco de dados
		ApoiosMongoADO ado = new ApoiosMongoADO();
		if (!ado.coneccaoValida()) {
			System.out.println("Erro na coneccao com o MongoDB");
			return;
		}
		
		int porta = 0;
		portasLivres = new HashSet<Integer>();
		for (int i = 1051; i < 1100; i++) {
			portasLivres.add(i);
		}
		
		synchronized(Servidor_main.class) {
			while (true) {
				try (ServerSocket serverSocket = new ServerSocket(1050)) {
					System.out.println("Servidor: Aguardando requisições na porta 1050");
					Socket socket = serverSocket.accept();
		
					OutputStream socketOut = socket.getOutputStream();
					InputStream socketIn = socket.getInputStream();
					
					ObjectOutputStream oos = new ObjectOutputStream(socketOut);
					ObjectInputStream ois = new ObjectInputStream(socketIn);

					Object obj = ois.readObject();
					if (obj instanceof String && ((String) obj).equals("PORTA")) {
						for (int i = 0; i < portasLivres.size(); i++) { 
							porta = portasLivres.iterator().next(); 
							System.out.print("porta " + porta); 
							if (estaLivre(porta)) { 
								System.out.println(" livre."); 
								break; 
							} else {
								System.out.println(" ocupada."); 
								portasLivres.remove(porta); 
							} 
						} 
					}
					System.out.println("Pegou a porta " + porta); 
					bloqueiaPorta(porta); 
					Coneccao conn = new Coneccao(porta);
					
					new Thread(conn).start();
					
					oos.writeObject(porta); // mandando a porta lida
					System.out.println("Enviou Porta2 = " + porta);
					ois.readObject(); // para esperar pegar a porta antes de fechar a connecção
				
					
					/*
					 * InputStream inputStream = socket.getInputStream(); OutputStream outputStream
					 * = socket.getOutputStream(); BufferedReader bufferedReader = new
					 * BufferedReader(new InputStreamReader(inputStream)); PrintWriter printWriter =
					 * new PrintWriter(outputStream, true);
					 * 
					 * String lido = bufferedReader.readLine(); if (lido.equals("PORTA")) { for (int
					 * i = 0; i < portasLivres.size(); i++) { porta =
					 * portasLivres.iterator().next(); System.out.print("porta " + porta); if
					 * (estaLivre(porta)) { System.out.println(" livre."); break; } else {
					 * System.out.println(" ocupada."); portasLivres.remove(porta); } } // Se
					 * terminou o for e não achou nenhuma porta livre ferrou! Tem alguma coisa muito
					 * errado ApoiosMongoADO.arquivaErro("Acabaram-se as portas livres", new
					 * Exception("nenhuma porta livre")); break; }
					 * System.out.println("Pegou a porta " + porta); bloqueiaPorta(porta); Coneccao
					 * conn = new Coneccao(porta);
					 * 
					 * executorService.execute(conn); // para aguardar antes de mandar a porta
					 * printWriter.println(porta); // mandando a porta lido =
					 * bufferedReader.readLine(); // é para travar até confirmar que recebeu
					 */
				
				} catch (Exception e) {
					System.out.println("Impossível abrir a porta 1050");
					break;
				}
				System.out.println("Fechou um ciclo completo...");
			}
		}
	}

	private static boolean estaLivre(int porta) {
		synchronized(sync1) {
			try {
				ServerSocket serverSocket = new ServerSocket(porta);
				// Socket s = serverSocket.accept();
				serverSocket.close();
			} catch (UnknownHostException e) {
				e.printStackTrace();
			} catch (IOException e) {
				return false;
			}
			return true;
		}
	}

	public static void liberadaPorta(int porta) {
		synchronized (sync2) {
			portasLivres.add(porta);
			System.out.println("Porta " + porta + " liberada.");
		}
	}

	public static void bloqueiaPorta(int porta) {
		synchronized (sync3) {
			portasLivres.remove(porta);
			System.out.println("Porta " + porta + " bloqueada.");
		}
	}

}
