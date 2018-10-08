package com.drcarlosamaral.controlecronicosserver;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.io.PrintWriter;
import java.net.ServerSocket;
import java.net.Socket;
import java.net.UnknownHostException;
import java.util.HashSet;
import java.util.Set;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

public class Servidor_main {

	static Set<Integer> portasLivres;
	
	public static void main(String[] args) {
		
		int porta = 0;
		ExecutorService executorService = Executors.newWorkStealingPool();
		portasLivres = new HashSet<Integer>();
		for (int i = 1051; i < 1100; i++) {
			portasLivres.add(i);
		}
		while (true) {
			try (ServerSocket serverSocket = new ServerSocket(1050)) {
				System.out.println("Servidor: Aguardando requisições");
				Socket socket = serverSocket.accept();
				InputStream inputStream = socket.getInputStream();
				OutputStream outputStream = socket.getOutputStream();
	
				BufferedReader bufferedReader = new BufferedReader(new InputStreamReader(inputStream));
				PrintWriter printWriter = new PrintWriter(outputStream, true);
	
	//			String log = bufferedReader.readLine();
	//			String pwd = bufferedReader.readLine();
				
				String lido = bufferedReader.readLine();
				if (lido.equals("PORTA")) {
					for (int i = 0; i < portasLivres.size(); i++) {
						porta = portasLivres.iterator().next();
						if (estaLivre(porta))
							break;
					}
				}
				System.out.println("Servidor: Pegou a porta " + porta);
				Coneccao conn = new Coneccao(porta);
				printWriter.println(porta);
				
				executorService.execute(conn);
				
			} catch (IOException e) {
				System.out.println("Impossível abrir a porta 1050");
			}
		}

	}

	private static boolean estaLivre(int porta) {
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

	public static void liberadaPorta(int porta) {
		portasLivres.add(porta);
	}

	public static void bloqueiaPorta(int porta) {
		portasLivres.remove(porta);
	}

}
