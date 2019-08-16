package com.drcarlosamaral.controlecronicosserver;

import java.io.InputStream;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.io.OutputStream;
import java.net.ServerSocket;
import java.net.Socket;

import javafx.application.Platform;

public class Coneccao implements Runnable{
	int porta;
	private ApoiosMongoADO ado = new ApoiosMongoADO();
	private ServerSocket serverSocket;
	
	public Coneccao(int porta) {
		this.porta = porta;
	}

	/**
	 * Após receber a porta, o cliente deve enviar primeiro:
	 * 1) Login;
	 * 2) Password.
	 * Receberá "OK", ou "ERRO".
	 * Sempre em caso de ERRO, será seguido por uma string do número de linhas de erro, e das respectivas linhas com informações dos erros.
	 * Caso OK, deve enviar na ordem:
	 * 1)Tipo de solicitação (GET ou SET);
	 * 2)Número de linhas de solicitações;
	 * 3)As solicitações.
	 * As solicitações possíveis estão descritas em ApoiosMongoADO.get()/.set();
	 */
	@Override
	public void run() {
		try (ServerSocket serverSocket = new ServerSocket(porta)) {
			this.serverSocket = serverSocket;
			serverSocket.setSoTimeout(30000);
			Socket clientSocket = serverSocket.accept();
			//System.out.println("Coneccao: Porta " + porta + " bloqueada");
			//Servidor_main.bloqueiaPorta(porta); // Já bloqueei
			OutputStream socketOut = clientSocket.getOutputStream();
			InputStream socketIn = clientSocket.getInputStream();
			
			ObjectOutputStream oos = new ObjectOutputStream(socketOut);
			ObjectInputStream ois = new ObjectInputStream(socketIn);
			String msg = "";
			
			// Loging
			String log = (String) ois.readObject(); 
			Long pwd = Long.parseLong((String) ois.readObject());
			if (!Login.log(log, pwd)) {
				oos.writeObject("ERRO");
				oos.writeObject(1);
				oos.writeObject("Login e Senha não combinam.");
				msg = null;
			} else {
				oos.writeObject("OK");
			}
			
			// trabalhando com as requisições: 
			Object obj = ois.readObject();
			if (obj instanceof String) {
				msg = (String) obj;
			} else {
				msg = null;
				System.out.println("ERRO AO PEGAR COMANDO GET, SET, ADD ou DEL");
			}
			System.out.print("Requisição na porta " + porta + ": " + msg);
			if (msg != null && msg.equals("SET")) {
				msg = (String) ois.readObject();
				System.out.println(" " + msg);
				Object retorno = ado.set(msg, oos, ois);
				oos.writeObject(retorno); 
				
			} else if (msg != null && msg.equals("GET")) {
					Object objeto = ois.readObject();
					System.out.println(" " + objeto);
					msg = (String) objeto;
					Object retorno = ado.get(msg, oos, ois);
					oos.writeObject(retorno); 

			} else if (msg != null && msg.equals("DEL")) {
					msg = (String) ois.readObject();
					System.out.println(" " + msg);
					Object retorno = ado.del(msg, oos, ois);
					oos.writeObject(retorno); 

			} else if (msg != null && msg.equals("ADD")) {
					msg = (String) ois.readObject();
					System.out.println(" " + msg);
					Object retorno = ado.add(msg, oos, ois);
					oos.writeObject(retorno); 
			}
			
			Servidor_main.liberadaPorta(porta);
			
		} catch (Exception e) {
				e.printStackTrace();
		}
	}
}
