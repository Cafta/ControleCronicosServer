/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.drcarlosamaral.controlecronicosserver;

import static com.mongodb.client.model.Filters.exists;

import java.io.BufferedWriter;
import java.io.FileWriter;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.io.PrintWriter;
import java.io.StringWriter;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

import org.bson.Document; 

import com.mongodb.MongoClient;
import com.mongodb.MongoClientURI;
import com.mongodb.client.MongoCollection;
import com.mongodb.client.MongoCursor;
import com.mongodb.client.MongoDatabase;
import static com.mongodb.client.model.Filters.*;

import javafx.application.Platform;
import javafx.scene.control.Alert;
import javafx.scene.control.ButtonType;

/**
 *
 * @author Carlos Amaral
 */
public class ApoiosMongoADO {
    
    public ApoiosMongoADO(){ 
        // Se o arquivo de configuração não existir vou mostrar uma primeira mensagem antes de mandar configurar
        // pois o usuário pode não entender de como configurar e preferir encerrar o programa.
//        if (!((File) new File("config.mdbcf")).exists()) {
//            Alert alert = new Alert(Alert.AlertType.ERROR,
//                "Click OK para configurar, ou CANCEL para sair.",
//                    ButtonType.OK, ButtonType.CANCEL);
//            alert.setHeaderText("Acesso ao Banco de Dados não configurado.");
//            alert.setTitle("Ajuste Inicial:");
//            alert.showAndWait();
//            if (alert.getResult().equals(ButtonType.CANCEL)) System.exit(0);
////            configuraConeccao();
//        }
        // testa a coneccao, e configura se for inválida
//        testaConeccao();
    }
        
//    public static void testaConeccao(){
//        while (!coneccaoValida()){ 
//            Alert alert = new Alert(Alert.AlertType.ERROR,
//                "Verifique a rede, se o servidor do banco de dados está ligado e "
//                        + "se o usuário e a senha estão corretas no URL.",
//                    ButtonType.OK, ButtonType.CANCEL);
//            alert.setTitle("ERRO");
//            alert.setHeaderText("Erro ao tentar conectar o MongoDB!");
//            alert.showAndWait();
//            if (alert.getResult().equals(ButtonType.CANCEL)) System.exit(0);
//            configuraConeccao();
//        }
//    }
    
//    public static Boolean coneccaoValida(){
//        File file = new File("config.mdbcf");
//        if (!file.exists()) return false;
//        Properties prop = new Properties();
//        try (InputStream config = new FileInputStream(file)){
//                prop.load(config);
//        } catch (Exception putz){
//            return false;
//        }
//        URL = prop.getProperty("url");
//        //Agora que eu j� verifiquei o arquivo de configuração vamos testar se conecta.
//        try {
//	        MongoClientURI connectionString = new MongoClientURI(Login.getURL());
//	        try (MongoClient mongoClient = new MongoClient(connectionString)){
//	            MongoDatabase database = mongoClient.getDatabase("ControleNovaAmerica");
//	            MongoCollection<Document> collection = database.getCollection("ListaDeBairros");
//	            Document documento = collection.find().first();
//	            //se n�o deu erro � porque conectou
//	            return true;
//	        } catch (Exception putz){
//	            //return false;
//	        }
//        } catch (Exception putz1) {
//        	
//        }
//        return false;
//    }
    
//    public static void configuraConeccao() {
//        String erroDepuravel = null;
//        File file = new File("config.mdbcf");
//        Properties prop = new Properties();
//        // Se nao tem arquivo de configuracao cria com URL default
//        if (!file.exists()){
//            prop.setProperty("url", "mongodb://root:root@localhost:27017");
//            try (OutputStream out = new FileOutputStream("config.mdbcf")){
//                prop.store(out, "Este arquivo foi gravado automaticamente por ApoiosMongoADO.java");
//            } catch (Exception e){
//                arquivaErro("Erro em ApoiosMongoADO.configuraConeccao()", e);
//            }
//        }
//        // Agora já tem arquivo de configuração prévio ou recem criado.
//        // Pego o URL do arquivo de configuração para que o usuário possa fazer a alteração nele, se desejar
//        try (InputStream config = new FileInputStream(file)){
//            prop.load(config);
//            URL = prop.getProperty("url");
//        } catch (Exception putz){
//            erroDepuravel = "Erro ao ler propriedades do arquivo de configuração";
//        }
//        TextInputDialog inputDialog;
//        if (erroDepuravel == null){ 
//            inputDialog = new TextInputDialog(URL);
//        } else {
//            inputDialog = new TextInputDialog("mongodb://root:root@localhost:27017");
//        }
//        inputDialog.setTitle("Configuração");
//        inputDialog.setHeaderText("Escreva a URL completa de acesso ao MongoDB");
//        inputDialog.setContentText("URL:");
//        Optional<String> urlTyped = inputDialog.showAndWait();
//        if (urlTyped.isPresent()) {
//            prop.setProperty("url", urlTyped.get());
//            try (OutputStream out = new FileOutputStream("config.mdbcf")){
//                prop.store(out, "Este arquivo foi gravado automaticamente por ApoiosMongoADO.java");
//            } catch (Exception e){
//                arquivaErro("Erro em ApoiosMongoADO.configuraConeccao() no prop.store2", e);
//            }
//        } 
//    }
    
//    public static String getString(String msg) {
//    	// abaixo é só um teste
//    	if (msg.equals("versao")) {
//    		return getVersion();
//    	}
//    	return "Objeto Indefinido"; 
//    }
    
    public static Object get(String msg, ObjectOutputStream oos, ObjectInputStream ois) {
    	if (msg.equals("versao")) {
    		return getVersion();
    	} else if (msg.equals("listaPessoas")) {
    		return getListaPessoas();
    	} else if (msg.equals("usuario")) {
    		return getDadosUsuario(oos, ois);
    	} else {
    		return new Document();
    	}
    }
    
    public static String getVersion() {
    	MongoClientURI connectionString = new MongoClientURI(Login.getURL());
    	try (MongoClient mongoClient = new MongoClient(connectionString)){
    		MongoDatabase mongoDB = mongoClient.getDatabase("ControleNovaAmerica");
    		MongoCollection<Document> collection = mongoDB.getCollection("login");
    		Document doc = collection.find(exists("versao")).first();
    		if (doc != null && !doc.isEmpty()) {
    			return doc.getString("versao");
    		} else {
    			return "sem versao definida";
    		}
    	} catch (Exception e){
    		arquivaErro("Erro em ApoiosMongoADO.getVersion()", e);
    	}
    	return "sem versao definida";
    }
    
   
    
    private static Object getDadosUsuario(ObjectOutputStream oos, ObjectInputStream ois) {
    	try {
			String login = (String) ois.readObject();
			int pwd = Integer.parseInt((String) ois.readObject());
			//agora vou ter que achar o usuario com esse login e password no BD
			MongoClientURI connectionString = new MongoClientURI(Login.getURL());
	    	try (MongoClient mongoClient = new MongoClient(connectionString)){
	    		MongoDatabase mongoDB = mongoClient.getDatabase("ControleNovaAmerica");
	    		MongoCollection<Document> collection = mongoDB.getCollection("login");
	    		Document doc = collection.find(and(eq("login",login),eq("pwd", pwd))).first();
	    		if (doc != null && !doc.isEmpty()) {
	    			return doc;
	    		} else {
	    			return new Document();
	    		}
	    	} catch (Exception e){
	    		arquivaErro("Erro em ApoiosMongoADO.getDadosUsuario()", e);
	    	}
		} catch (ClassNotFoundException e) {
			arquivaErro("Erro em ApoiosMongoADO.getDadosUsuario()", e);
		} catch (IOException e) {
			arquivaErro("Erro em ApoiosMongoADO.getDadosUsuario()", e);
		}
    	return new Document();
    }
    
    private static List<Document> getListaPessoas(){
    	List<Document> lista = new ArrayList<>();
    	MongoClientURI connectionString = new MongoClientURI(Login.getURL());
    	try (MongoClient mongoClient = new MongoClient(connectionString)){
    		MongoDatabase mongoDB = mongoClient.getDatabase("ControleNovaAmerica");
    		MongoCollection<Document> collection = mongoDB.getCollection("Pessoas");
    		MongoCursor<Document> cursor = collection.find(or(eq("posto","NovaAmerica"),not(exists("posto"))))
    										.projection(eq("name",1))
    										.iterator();
    		while(cursor.hasNext()) {
    			lista.add(cursor.next());
    		}
    	} catch (Exception e){
    		arquivaErro("Erro em ApoiosMongoADO.getVersion()", e);
    	}
		return lista;
    }
    
//    public List<String> getBairros(){
//        List<String> bairros = new ArrayList<>();
//        
//        bairros.add("Jardim Nova America");
//        bairros.add("Irmaos Sigrist");
//        bairros.add("Residencial da Paz");
//        
//        return bairros;
//    }
    
//    public List<String> getRuas(){
//        List<String> ruas = new ArrayList<>();
//        try {
//            
//        } catch (Exception putz){
//            arquivaErro("Erro em ApoiosMongoADO.getBairros()", putz);
//        }
//        return ruas;
//    }
    
//    /**
//     * Adiciona nomes para ruas.  CUIDADO com a duplica��o de nomes. Fa�a o tratamento no c�digo
//     * @param rua = nome da rua.
//     */
//    public void addRuas(String... rua){
//        try {
//            if (rua == null || rua.length == 0) return;
//            MongoClientURI connectionString = new MongoClientURI(URL);
//            try (MongoClient mongoClient = new MongoClient(connectionString)){
//                MongoDatabase database = mongoClient.getDatabase("ControleNovaAmerica");
//                MongoCollection<Document> collection = database.getCollection("ListaDeRuas");
//                Document doc = new Document();
//                for (String r : rua){
//                	doc.put("nome", r);
//                	collection.insertOne(doc);
//                }
//            }
//        } catch (Exception putz) {
//            arquivaErro("Erro em ApoiosMongoADO.addRuas()", putz);
//        }
//    }
//    
//    public void delRuas(String... ruas){
//        try {
//            if (ruas == null || ruas.length == 0) return;
//            MongoClientURI connectionString = new MongoClientURI(URL);
//            try (MongoClient mongoClient = new MongoClient(connectionString)){
//            	MongoDatabase database = mongoClient.getDatabase("ControleNovaAmerica");
//            	MongoCollection<Document> collection = database.getCollection("ListaDeRuas");
//            	for (String r : ruas) {
//            		Document doc = new Document();
//            		doc.append("nome", r);
//            		collection.findOneAndDelete(doc);
//            	}
//            }
//        } catch (Exception putz) {
//            arquivaErro("Erro em ApoiosMongoADO.delRuas()", putz);
//        }
//    }
    
    public Boolean arquiva(Document document){
        try {
            if (document == null || document.isEmpty()) return false;
            MongoClientURI connectionString = new MongoClientURI(Login.getURL());
            try(MongoClient mongoClient = new MongoClient(connectionString)){
            	MongoDatabase database = mongoClient.getDatabase("ControleNovaAmerica");
            	MongoCollection<Document> collection = database.getCollection("ArquivoMorto");
            	collection.insertOne(document);
            }
        } catch (Exception putz) {
            arquivaErro("Erro em ApoiosMongoADO.arquiva(Document)", putz);
        }
        return false;
    }
    
    public static void arquivaErro(String erro, Exception ex){
        StringWriter errors = new StringWriter();
        ex.printStackTrace(new PrintWriter(errors));

        PrintWriter pw = null;
        erro = String.valueOf(LocalDateTime.now()) + "\n" + erro + "\n" + errors.toString() + "\n";
            try{
                FileWriter fw = new FileWriter("errolog.txt", true);
                BufferedWriter bw = new BufferedWriter(fw);
                pw = new PrintWriter(bw);
                pw.write(erro);
                Platform.runLater(new Runnable(){
                    @Override public void run() {
                        Alert alert = new Alert(Alert.AlertType.ERROR,
                            "Erro gravado em errolog.txt",
                            ButtonType.OK);
                        alert.setTitle("ERRO");
                        alert.setHeaderText("Desculpa, mas ocorreu um erro.");
                        alert.showAndWait();
                    }
                });
            } catch (Exception putaMerda){
                putaMerda.printStackTrace(System.out);
            } finally {
                if (pw != null){
                    pw.close();
                }
            }
    }
    
    public void bdUpdate() {
    	try {
    		MongoClientURI connectionString = new MongoClientURI(Login.getURL());
    		try(MongoClient mongoClient = new MongoClient(connectionString)){
    			MongoDatabase mongoDB = mongoClient.getDatabase("ControleNovaAmerica");
    			MongoCollection<Document> collection = mongoDB.getCollection("login");
    			Document doc = new Document();
    			doc.append("versao", Login.VERSAO);
    			collection.insertOne(doc);
    		}
    	} catch (Exception putz) {
    		arquivaErro("Erro em ApoiosMongoADO.bdUpdate()", putz);
    	}
    }
    
}
