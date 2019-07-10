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
import org.bson.types.ObjectId;

import com.mongodb.MongoClient;
import com.mongodb.MongoClientURI;
import com.mongodb.client.DistinctIterable;
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
    
    public static Boolean coneccaoValida(){
        /*File file = new File("config.mdbcf");
        if (!file.exists()) return false;
        Properties prop = new Properties();
        try (InputStream config = new FileInputStream(file)){
                prop.load(config);
        } catch (Exception putz){
            return false;
        }
        URL = prop.getProperty("url");*/
        //Agora que eu j� verifiquei o arquivo de configuração vamos testar se conecta.
        try {
	        MongoClientURI connectionString = new MongoClientURI(Login.getURL());
	        try (MongoClient mongoClient = new MongoClient(connectionString)){
	            MongoDatabase database = mongoClient.getDatabase(Login.bd);
	            MongoCollection<Document> collection = database.getCollection("Funcionarios");
	            Document documento = collection.find(eq("login", "usuario")).first();
	            if (documento == null) { //parece que não tem nem o banco de dados
	            	Document d1 = new Document();
	            	Document d2 = new Document();
	            	Document d3 = new Document();
	            	d1.append("nome", "Usuário");
	            	d1.append("login", "usuario");
	            	d1.append("pwd", 1766);
	            	d1.append("grupo", "usuario");
	            	d2.append("grupo", "usuario");
	            	d2.append("versao", Login.VERSAO);
	            	d3.append("nome", "Dr.Carlos");
	            	d3.append("login", "Carlos");
	            	d3.append("pwd", 1869);
	            	d3.append("unidade", "C.S.Nova América");
	            	d3.append("funcao", "Médico(a)");
	            	d3.append("especializacao", "Generalista");
	            	d3.append("grupo", "adm");
	            	List<Document> documents = new ArrayList<>();
	            	collection.insertOne(d1);
	            	collection.insertOne(d2);
	            	collection.insertOne(d3);
	            }
	            //se n�o deu erro � porque conectou
	            return true;
	        } catch (Exception putz){
	            //return false;
	        	System.out.println("Erro em ApoiosMongoDB.conecccaoValida()");
	        }
        } catch (Exception putz1) {
        	System.out.println("Erro em ApoiosMongoDB.conecccaoValida()_2");
        }
        return false;
    }
    
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
    	try {
	    	if (msg.equals("versao")) {
	    		return getVersion();
	    	} else if (msg.equals("lista")) {
	    		return getLista(oos,ois);
	    	} else if (msg.equals("listaPessoas")) {
	    		return getListaPessoas();
	    	} else if (msg.equals("listaMedicos")) {
	    		return getListaMedicos();
	    	} else if (msg.equals("listaFFs")) {
	    		return getListaFFs();
	    	} else if (msg.equals("listaRuas")) {
	    		return getListaRuas();
	    	} else if (msg.equals("listaReceitas")) {
	    		ObjectId oId = (ObjectId) ois.readObject();
	    		return getListaReceitas(oId);
	    	} else if (msg.equals("usuario")) {
	    		return getDadosUsuario(oos, ois);
	    	} else if (msg.equals("pessoa")) {
	    		return getPessoa(oos,ois);
	    	} else if (msg.equals("paciente")) {
	    		return getPaciente(oos,ois);
	    	} else if (msg.equals("funcionario")) {
	    		return getFuncionario(oos,ois);
	    	} else if (msg.equals("enderecoByFF")) {
	    		return getEnderecoByFF(oos,ois);
	    	} else if (msg.equals("controleHas")) {
	    		return getControleHas(oos,ois);
	    	} else if (msg.equals("controleDm")) {
	    		return getControleDm(oos,ois);
	    	} else {
	    		return new Document();
	    	}
    	} catch (Exception e) {
    		e.printStackTrace();
    		return new Document();
    	}
    }
    
    public static List<Document> getLista(ObjectOutputStream oos, ObjectInputStream ois) {
    	
    	try {
    		// lista do quê:
    		String oque = (String) ois.readObject();
    		if (oque.equals("pessoas")){
    			return getListaPessoas();
    		} else if (oque.equals("funcionarios")) {
    			return getListaFuncionarios();
    		} else if (oque.equals("medicos")) {
    			return getListaMedicos();
    		} else if (oque.equals("alias")) {
    			return getListaAlias();
    		} else if (oque.equals("funcoes")) {
    			return getListaFuncoes();
    		}
    	} catch (Exception e) {
    		e.printStackTrace();
    	}
    	return new ArrayList<Document>();
    }
    
    public static Object del(String msg, ObjectOutputStream oos, ObjectInputStream ois) {
    	try {
	    	if (msg.equals("receita")) {
	    		ObjectId oId = (ObjectId) ois.readObject();
	    		return delReceita(oId);
	    	} else {
	    		return new Document();
	    	}
    	} catch (Exception e) {
    		e.printStackTrace();
    		return new Document();
    	}
    }
    
    public static Object add(String msg, ObjectOutputStream oos, ObjectInputStream ois) {
    	try {
	    	if (msg.equals("receita")) {
	    		Document receita = (Document) ois.readObject();
	    		return addReceita(receita);
	    	}
	    	if (msg.equals("pessoa")) {
	    		Document pessoa = (Document) ois.readObject();
	    		return addPessoa(pessoa);
	    	}
	    	if (msg.equals("funcionario")) {
	    		Document funcionario = (Document) ois.readObject();
	    		return addFuncionario(funcionario);
	    	}
	    	if (msg.equals("paciente")) {
	    		Document paciente = (Document) ois.readObject();
	    		return addPaciente(paciente);
	    	}
	    	if (msg.equals("controleHas")) {
	    		Document controleHas = (Document) ois.readObject();
	    		return addControleHas(controleHas);
	    	}
	    	if (msg.equals("controleDm")) {
	    		Document controleDm = (Document) ois.readObject();
	    		return addControleDm(controleDm);
	    	}
	    	if (msg.equals("endereco")) {
	    		Document endereco = (Document) ois.readObject();
	    		return addEndereco(endereco);
	    	}
	    	return false;
    	} catch (Exception e) {
    		e.printStackTrace();
    		return new Document();
    	}
    }
    
    public static Object set(String msg, ObjectOutputStream oos, ObjectInputStream ois) {
    	try {
	    	if (msg.equals("receita")) {
	    		Document receita = (Document) ois.readObject();
	    		return setReceita(receita);
	    	}
	    	if (msg.equals("pessoa")) {
	    		Document pessoa = (Document) ois.readObject();
	    		return setPessoa(pessoa);
	    	}
	    	if (msg.equals("paciente")) {
	    		Document paciente = (Document) ois.readObject();
	    		return setPaciente(paciente);
	    	}
	    	if (msg.equals("funcionario")) {
	    		Document funcionario = (Document) ois.readObject();
	    		return setFuncionario(funcionario);
	    	}
	    	if (msg.equals("endereco")) {
	    		Document endereco = (Document) ois.readObject();
	    		return setEndereco(endereco);
	    	}
	    	return false;
    	} catch (Exception e) {
    		e.printStackTrace();
    		return new Document();
    	}
    }
    
    private static Document getPessoa(ObjectOutputStream oos, ObjectInputStream ois) {
    	Document doc = new Document();
    	// vai passar o que?
    	try {
			String resposta = (String) ois.readObject();
			if (resposta.equals("ObjectId")) {
				ObjectId oId = (ObjectId) ois.readObject();
				return getPessoa(oId);
			} else if (resposta.equals("nome")) {
				List<Document> docs = getPessoa((String) ois.readObject());
				oos.writeObject(docs);
				return new Document();
			}
		} catch (Exception e) {
			e.printStackTrace();
		}
    	return doc;
    }
    
    private static Document getFuncionario(ObjectOutputStream oos, ObjectInputStream ois) {
    	Document doc = new Document();
    	// vai passar o que?
    	try {
			String resposta = (String) ois.readObject();
			if (resposta.equals("byLogin")) {
				String log = (String) ois.readObject();
				Integer pwd = (Integer) ois.readObject();
				return getFuncionario(log, pwd);
			} else if (resposta.equals("byAlias")) {
				String alias = (String) ois.readObject();
				return getFuncionarioByAlias(alias);
			} else if (resposta.equals("byId")) {
				ObjectId _id = (ObjectId) ois.readObject();
				return getFuncionarioById(_id);
			}
		} catch (Exception e) {
			e.printStackTrace();
		}
    	return doc;
    }
    
    private static Document getFuncionario(String login, Integer pwd) {
    	
    	System.out.println("Para pegar funcionario temos:");
    	System.out.println("login: " + login + ", pwd: " + pwd.toString());
    	
    	Document doc = new Document();
    	MongoClientURI connectionString = new MongoClientURI(Login.getURL());
    	try (MongoClient mongoClient = new MongoClient(connectionString)){
    		MongoDatabase mongoDB = mongoClient.getDatabase(Login.bd);
    		MongoCollection<Document> collection = mongoDB.getCollection("Funcionarios");
    		doc = collection.find(and(eq("login", login),eq("pwd", pwd))).first();
    	} catch (Exception e){
    		arquivaErro("Erro em ApoiosMongoADO.getFuncionario(login, pwd)", e);
    	}
    	
    	System.out.println("Pegou o funcionario com _id: " + doc.getObjectId("_id"));
    	
    	return doc;
    }
    
    private static Document getFuncionarioByAlias(String alias) {
    	System.out.println("procurando Funcionario by alias. Alias: " + alias);
    	Document doc = new Document();
    	MongoClientURI connectionString = new MongoClientURI(Login.getURL());
    	try (MongoClient mongoClient = new MongoClient(connectionString)){
    		MongoDatabase mongoDB = mongoClient.getDatabase(Login.bd);
    		MongoCollection<Document> collection = mongoDB.getCollection("Funcionarios");
    		Document basicQuery = new Document();
    		basicQuery.append("alias", new Document("$regex",alias).append("$options","i"));
    		doc = collection.find(basicQuery).first();
    	} catch (Exception e){
    		arquivaErro("Erro em ApoiosMongoADO.getFuncionarioByAlias(alias)", e);
    	}
    	System.out.println("Resultado: " + doc);
    	return doc;
    }
    
    private static Document getFuncionarioById(ObjectId _id) {
    	Document doc = new Document();
    	MongoClientURI connectionString = new MongoClientURI(Login.getURL());
    	try (MongoClient mongoClient = new MongoClient(connectionString)){
    		MongoDatabase mongoDB = mongoClient.getDatabase(Login.bd);
    		MongoCollection<Document> collection = mongoDB.getCollection("Funcionarios");
    		doc = collection.find(eq("_id", _id)).first();
    	} catch (Exception e){
    		arquivaErro("Erro em ApoiosMongoADO.getFuncionarioById(_id)", e);
    	}
    	System.out.println("Resultado: " + doc);
    	return doc;
    }
    
    private static Document getEnderecoByFF(ObjectOutputStream oos, ObjectInputStream ois) {
    	Document doc = new Document();
    	MongoClientURI connectionString = new MongoClientURI(Login.getURL());
    	try (MongoClient mongoClient = new MongoClient(connectionString)){
    		String ff = (String) ois.readObject();
    		MongoDatabase mongoDB = mongoClient.getDatabase(Login.bd);
    		MongoCollection<Document> collection = mongoDB.getCollection("Enderecos");
    		doc = collection.find(eq("ff", ff)).first();
    	} catch (Exception e){
    		arquivaErro("Erro em ApoiosMongoADO.getEnderecoByFF()", e);
    	}
    	return doc;
    }
    
    private static Document getPaciente(ObjectOutputStream oos, ObjectInputStream ois) {
    	Document doc = new Document();
    	// vai passar o que?
    	try {
			String resposta = (String) ois.readObject();
			if (resposta.equals("ObjectId")) {
				ObjectId oId = (ObjectId) ois.readObject();
				return getPaciente(oId);
			}
		} catch (Exception e) {
			e.printStackTrace();
		}
    	return doc;
    }
    
    private static Document getPaciente(ObjectId _id) {
    	Document doc = new Document();
    	MongoClientURI connectionString = new MongoClientURI(Login.getURL());
    	try (MongoClient mongoClient = new MongoClient(connectionString)){
    		MongoDatabase mongoDB = mongoClient.getDatabase(Login.bd);
    		MongoCollection<Document> collection = mongoDB.getCollection("Pacientes");
    		doc = collection.find(eq("_id", _id)).first();
    	} catch (Exception e){
    		arquivaErro("Erro em ApoiosMongoADO.getPacientes(_id)", e);
    	}
    	return doc;
    }
    
    private static ObjectId addPaciente(Document paciente) {
    	MongoClientURI connectionString = new MongoClientURI(Login.getURL());
    	try (MongoClient mongoClient = new MongoClient(connectionString)){
    		MongoDatabase mongoDB = mongoClient.getDatabase(Login.bd);
    		MongoCollection<Document> collection = mongoDB.getCollection("Pacientes");
    		collection.insertOne(paciente);
    		return paciente.getObjectId("_id");
    	} catch (Exception e){
    		arquivaErro("Erro em ApoiosMongoADO.addPaciente(paciente)", e);
    	}
    	return null;
    }
    
    private static Document getControleHas(ObjectOutputStream oos, ObjectInputStream ois) {
    	Document doc = new Document();
    	// vai passar o que?
    	try {
			String resposta = (String) ois.readObject();
			if (resposta.equals("ObjectId")) {
				ObjectId oId = (ObjectId) ois.readObject();
				return getControleHas(oId);
			} else if (resposta.equals("PacienteId")) {
				ObjectId oId = (ObjectId) ois.readObject();
				return getControleHasByPacienteId(oId);
			}
		} catch (Exception e) {
			e.printStackTrace();
		}
    	return doc;
    }
    
    private static Document getControleDm(ObjectOutputStream oos, ObjectInputStream ois) {
    	Document doc = new Document();
    	// vai passar o que?
    	try {
			String resposta = (String) ois.readObject();
			if (resposta.equals("ObjectId")) {
				ObjectId oId = (ObjectId) ois.readObject();
				return getControleDm(oId);
			} else if (resposta.equals("PacienteId")) {
				ObjectId PacienteId = (ObjectId) ois.readObject();
				return getControleDmByPacienteId(PacienteId);
			}
		} catch (Exception e) {
			e.printStackTrace();
		}
    	return doc;
    }
    
    private static ObjectId addControleHas(Document controleHas) {
    	MongoClientURI connectionString = new MongoClientURI(Login.getURL());
    	try (MongoClient mongoClient = new MongoClient(connectionString)){
    		MongoDatabase mongoDB = mongoClient.getDatabase(Login.bd);
    		MongoCollection<Document> collection = mongoDB.getCollection("HAS");
    		collection.insertOne(controleHas);
    		return controleHas.getObjectId("_id");
    	} catch (Exception e){
    		arquivaErro("Erro em ApoiosMongoADO.addControleHas(Document)", e);
    	}
    	return null;
    }
    
    private static ObjectId addControleDm(Document controleDm) {
    	MongoClientURI connectionString = new MongoClientURI(Login.getURL());
    	try (MongoClient mongoClient = new MongoClient(connectionString)){
    		MongoDatabase mongoDB = mongoClient.getDatabase(Login.bd);
    		MongoCollection<Document> collection = mongoDB.getCollection("DM");
    		collection.insertOne(controleDm);
    		return controleDm.getObjectId("_id");
    	} catch (Exception e){
    		arquivaErro("Erro em ApoiosMongoADO.addControleDm(Document)", e);
    	}
    	return null;
    }
    
    private static ObjectId addEndereco(Document endereco) {
    	MongoClientURI connectionString = new MongoClientURI(Login.getURL());
    	try (MongoClient mongoClient = new MongoClient(connectionString)){
    		MongoDatabase mongoDB = mongoClient.getDatabase(Login.bd);
    		MongoCollection<Document> collection = mongoDB.getCollection("Enderecos");
    		collection.insertOne(endereco);
    		return endereco.getObjectId("_id");
    	} catch (Exception e){
    		arquivaErro("Erro em ApoiosMongoADO.addEndereco(endereco)", e);
    	}
    	return null;
    }
    private static Boolean setPaciente(Document paciente) {
    	MongoClientURI connectionString = new MongoClientURI(Login.getURL());
    	try (MongoClient mongoClient = new MongoClient(connectionString)){
    		MongoDatabase mongoDB = mongoClient.getDatabase(Login.bd);
    		MongoCollection<Document> collection = mongoDB.getCollection("Pacientes");
    		Document d = collection.findOneAndReplace(eq("_id",paciente.getObjectId("_id")), paciente);
    		if (d != null) return true;
    	} catch (Exception e){
    		arquivaErro("Erro em ApoiosMongoADO.setPaciente(paciente)", e);
    	}
    	return false;
    }
    private static Boolean setEndereco(Document endereco) {
    	MongoClientURI connectionString = new MongoClientURI(Login.getURL());
    	try (MongoClient mongoClient = new MongoClient(connectionString)){
    		MongoDatabase mongoDB = mongoClient.getDatabase(Login.bd);
    		MongoCollection<Document> collection = mongoDB.getCollection("Enderecos");
    		Document d = collection.findOneAndReplace(eq("_id",endereco.getObjectId("_id")), endereco);
    		if (d != null) return true;
    	} catch (Exception e){
    		arquivaErro("Erro em ApoiosMongoADO.setEndereco(endereco)", e);
    	}
    	return false;
    }
    
    private static List<Document> getPessoa(String nome) {
    	List<Document> pessoas = new ArrayList<>();
    	MongoClientURI connectionString = new MongoClientURI(Login.getURL());
    	try (MongoClient mongoClient = new MongoClient(connectionString)){
    		MongoDatabase mongoDB = mongoClient.getDatabase(Login.bd);
    		MongoCollection<Document> collection = mongoDB.getCollection("Pessoas");
    		MongoCursor<Document> cursor = collection.find(eq("name", nome)).iterator();
    		cursor.forEachRemaining(d -> pessoas.add(d));
    	} catch (Exception e){
    		arquivaErro("Erro em ApoiosMongoADO.getPessoa(_id)", e);
    	}
    	return pessoas;
    }
    
    private static Document getPessoa(ObjectId _id) {
    	Document doc = new Document();
    	MongoClientURI connectionString = new MongoClientURI(Login.getURL());
    	try (MongoClient mongoClient = new MongoClient(connectionString)){
    		MongoDatabase mongoDB = mongoClient.getDatabase(Login.bd);
    		MongoCollection<Document> collection = mongoDB.getCollection("Pessoas");
    		doc = collection.find(eq("_id", _id)).first();
    	} catch (Exception e){
    		arquivaErro("Erro em ApoiosMongoADO.getPessoa(_id)", e);
    	}
    	return doc;
    }
    private static ObjectId addPessoa(Document pessoa) {
    	MongoClientURI connectionString = new MongoClientURI(Login.getURL());
    	try (MongoClient mongoClient = new MongoClient(connectionString)){
    		MongoDatabase mongoDB = mongoClient.getDatabase(Login.bd);
    		MongoCollection<Document> collection = mongoDB.getCollection("Pessoas");
    		collection.insertOne(pessoa);
    		return pessoa.getObjectId("_id");
    	} catch (Exception e){
    		arquivaErro("Erro em ApoiosMongoADO.addPessoa(Document pessoa)", e);
    	}
    	return null;
    }
    private static Boolean setPessoa(Document pessoa) {
    	MongoClientURI connectionString = new MongoClientURI(Login.getURL());
    	try (MongoClient mongoClient = new MongoClient(connectionString)){
    		MongoDatabase mongoDB = mongoClient.getDatabase(Login.bd);
    		MongoCollection<Document> collection = mongoDB.getCollection("Pessoas");
    		Document d = collection.findOneAndReplace(eq("_id",pessoa.getObjectId("_id")), pessoa);
    		if (d != null) return true;
    	} catch (Exception e){
    		arquivaErro("Erro em ApoiosMongoADO.setPessoa(pessoa)", e);
    	}
    	return false;
    }
    
    private static Document getControleHas(ObjectId _id) {
    	Document doc = new Document();
    	MongoClientURI connectionString = new MongoClientURI(Login.getURL());
    	try (MongoClient mongoClient = new MongoClient(connectionString)){
    		MongoDatabase mongoDB = mongoClient.getDatabase(Login.bd);
    		MongoCollection<Document> collection = mongoDB.getCollection("HAS");
    		doc = collection.find(eq("_id", _id)).first();
    	} catch (Exception e){
    		arquivaErro("Erro em ApoiosMongoADO.getControleHas(_id)", e);
    	}
    	return doc;
    }
    
    private static Document getControleHasByPacienteId(ObjectId paciente_id) {
    	Document doc = new Document();
    	MongoClientURI connectionString = new MongoClientURI(Login.getURL());
    	try (MongoClient mongoClient = new MongoClient(connectionString)){
    		MongoDatabase mongoDB = mongoClient.getDatabase(Login.bd);
    		MongoCollection<Document> collection = mongoDB.getCollection("HAS");
    		doc = collection.find(eq("paciente_id", paciente_id)).first();
    	} catch (Exception e){
    		arquivaErro("Erro em ApoiosMongoADO.getControleHasByPacienteId(_id)", e);
    	}
    	return doc;
    }
    
    private static Document getControleDm(ObjectId _id) {
    	Document doc = new Document();
    	MongoClientURI connectionString = new MongoClientURI(Login.getURL());
    	try (MongoClient mongoClient = new MongoClient(connectionString)){
    		MongoDatabase mongoDB = mongoClient.getDatabase(Login.bd);
    		MongoCollection<Document> collection = mongoDB.getCollection("DM");
    		doc = collection.find(eq("_id", _id)).first();
    	} catch (Exception e){
    		arquivaErro("Erro em ApoiosMongoADO.getControleDm(_id)", e);
    	}
    	return doc;
    }
    
    private static Document getControleDmByPacienteId(ObjectId paciente_id) {
    	Document doc = new Document();
    	MongoClientURI connectionString = new MongoClientURI(Login.getURL());
    	try (MongoClient mongoClient = new MongoClient(connectionString)){
    		MongoDatabase mongoDB = mongoClient.getDatabase(Login.bd);
    		MongoCollection<Document> collection = mongoDB.getCollection("DM");
    		doc = collection.find(eq("paciente_id", paciente_id)).first();
    	} catch (Exception e){
    		arquivaErro("Erro em ApoiosMongoADO.getControleDm(_id)", e);
    	}
    	return doc;
    }
    
    private static String getVersion() {
    	MongoClientURI connectionString = new MongoClientURI(Login.getURL());
    	try (MongoClient mongoClient = new MongoClient(connectionString)){
    		MongoDatabase mongoDB = mongoClient.getDatabase(Login.bd);
    		MongoCollection<Document> collection = mongoDB.getCollection("Funcionarios");
    		Document doc = collection.find(exists("versao")).first();
    		if (doc != null && !doc.isEmpty()) {
    			return doc.getString("versao");
    		} else {
    			return "sem versao definida1";
    		}
    	} catch (Exception e){
    		arquivaErro("Erro em ApoiosMongoADO.getVersion()", e);
    	}
    	return "sem versao definida2";
    }
    
    private static Object getDadosUsuario(ObjectOutputStream oos, ObjectInputStream ois) {
    	try {
			String login = (String) ois.readObject();
			int pwd = Integer.parseInt((String) ois.readObject());
			//agora vou ter que achar o usuario com esse login e password no BD
			MongoClientURI connectionString = new MongoClientURI(Login.getURL());
	    	try (MongoClient mongoClient = new MongoClient(connectionString)){
	    		MongoDatabase mongoDB = mongoClient.getDatabase(Login.bd);
	    		MongoCollection<Document> collection = mongoDB.getCollection("Funcionarios");
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
    		MongoDatabase mongoDB = mongoClient.getDatabase(Login.bd);
    		MongoCollection<Document> collection = mongoDB.getCollection("Pessoas");
    		MongoCursor<Document> cursor = collection.find(or(eq("posto","C.S.Nova América"),not(exists("posto"))))
    										.projection(and(eq("name",1),eq("sexo", 1), eq("paciente_id", 1), eq("funcionario_id", 1)))
    										.iterator();
    		while(cursor.hasNext()) {
    			Document d = cursor.next();
    			d.append("pessoa_id", d.getObjectId("_id"));
    			lista.add(d);
    		}
    	} catch (Exception e){
    		arquivaErro("Erro em ApoiosMongoADO.getListaPessoas()", e);
    	}
		return lista;
    }
    
    private static List<Document> getListaFuncionarios(){
    	List<Document> lista = new ArrayList<>();
    	MongoClientURI connectionString = new MongoClientURI(Login.getURL());
    	try (MongoClient mongoClient = new MongoClient(connectionString)){
    		MongoDatabase mongoDB = mongoClient.getDatabase(Login.bd);
    		MongoCollection<Document> collection = mongoDB.getCollection("Pessoas");
    		MongoCursor<Document> cursor = collection.find(exists("funcionario_id"))
    										.projection(and(eq("name",1),eq("funcionario_id", 1)))
    										.iterator();
    		while(cursor.hasNext()) {
    			Document d = cursor.next();
    			d.append("pessoa_id", d.getObjectId("_id"));
    			lista.add(d);
    		}
    	} catch (Exception e){
    		arquivaErro("Erro em ApoiosMongoADO.getListaPessoas()", e);
    	}
		return lista;
    }
    private static ObjectId addFuncionario(Document funcionario) {
    	MongoClientURI connectionString = new MongoClientURI(Login.getURL());
    	try (MongoClient mongoClient = new MongoClient(connectionString)){
    		MongoDatabase mongoDB = mongoClient.getDatabase(Login.bd);
    		MongoCollection<Document> collection = mongoDB.getCollection("Funcionarios");
    		collection.insertOne(funcionario);
    		return funcionario.getObjectId("_id");
    	} catch (Exception e){
    		arquivaErro("Erro em ApoiosMongoADO.addFuncionario(Document funcionario)", e);
    	}
    	return null;
    }
    private static Boolean setFuncionario(Document funcionario) {
    	MongoClientURI connectionString = new MongoClientURI(Login.getURL());
    	try (MongoClient mongoClient = new MongoClient(connectionString)){
    		MongoDatabase mongoDB = mongoClient.getDatabase(Login.bd);
    		MongoCollection<Document> collection = mongoDB.getCollection("Funcionarios");
    		Document d = collection.findOneAndReplace(eq("_id",funcionario.getObjectId("_id")), funcionario);
    		if (d != null) return true;
    	} catch (Exception e){
    		arquivaErro("Erro em ApoiosMongoADO.setFuncionario(funcionario)", e);
    	}
    	return false;
    }
    private static List<Document> getListaAlias(){
    	List<Document> lista = new ArrayList<>();
    	MongoClientURI connectionString = new MongoClientURI(Login.getURL());
    	try (MongoClient mongoClient = new MongoClient(connectionString)){
    		MongoDatabase mongoDB = mongoClient.getDatabase(Login.bd);
    		MongoCollection<Document> collection = mongoDB.getCollection("Funcionarios");
    		MongoCursor<Document> cursor = collection.find(exists("alias"))
    										.projection(eq("alias",1))
    										.iterator();
    		while(cursor.hasNext()) {
    			Document d = cursor.next();
    			d.append("funcionario_id", d.getObjectId("_id"));
    			lista.add(d);
    		}
    	} catch (Exception e){
    		arquivaErro("Erro em ApoiosMongoADO.getListaAlias()", e);
    	}
		return lista;
    }
    
    private static List<Document> getListaFuncoes(){
    	List<Document> lista = new ArrayList<>();
    	MongoClientURI connectionString = new MongoClientURI(Login.getURL());
    	try (MongoClient mongoClient = new MongoClient(connectionString)){
    		MongoDatabase mongoDB = mongoClient.getDatabase(Login.bd);
    		MongoCollection<Document> collection = mongoDB.getCollection("Funcionarios");
    		
    		DistinctIterable<String> distinctFuncoes = collection.distinct("funcao", String.class);
    		for (String funcao : distinctFuncoes) {
    			if (funcao != null && !funcao.equals("")) {
    				lista.add(new Document("funcao", funcao));
    			}
    		}
    	} catch (Exception e){
    		arquivaErro("Erro em ApoiosMongoADO.getListaFuncoes()", e);
    	}
		return lista;
    }
    
    private static List<String> getListaFFs(){
    	List<String> lista = new ArrayList<>();
    	MongoClientURI connectionString = new MongoClientURI(Login.getURL());
    	try (MongoClient mongoClient = new MongoClient(connectionString)){
    		MongoDatabase mongoDB = mongoClient.getDatabase(Login.bd);
    		MongoCollection<Document> collection = mongoDB.getCollection("Enderecos");
    		MongoCursor<Document> cursor = collection.find(or(eq("posto","C.S.Nova América"),not(exists("posto"))))
    										.projection(eq("ff",1))
    										.iterator();
    		while(cursor.hasNext()) {
    			lista.add(cursor.next().getString("ff"));
    		}
    	} catch (Exception e){
    		arquivaErro("Erro em ApoiosMongoADO.getListaFFs()", e);
    	}
		return lista;
    }
    
    private static List<String> getListaRuas(){
    	List<String> lista = new ArrayList<>();
    	MongoClientURI connectionString = new MongoClientURI(Login.getURL());
    	try (MongoClient mongoClient = new MongoClient(connectionString)){
    		MongoDatabase mongoDB = mongoClient.getDatabase(Login.bd);
    		MongoCollection<Document> collection = mongoDB.getCollection("Enderecos");
    		DistinctIterable<String> distinctRuas = collection.distinct("rua", String.class);
    		for (String rua : distinctRuas) {
    			if (rua != null && !rua.equals(""))
    				lista.add(rua);
    		}
    	} catch (Exception e){
    		arquivaErro("Erro em ApoiosMongoADO.getListaRuas()", e);
    	}
    	System.out.println("A lista de ruas tem " + lista.size() + " nomes");
		return lista;
    }
    
    private static List<Document> getListaMedicos(){
    	List<Document> lista = new ArrayList<>();
    	MongoClientURI connectionString = new MongoClientURI(Login.getURL());
    	try (MongoClient mongoClient = new MongoClient(connectionString)){
    		MongoDatabase mongoDB = mongoClient.getDatabase(Login.bd);
    		MongoCollection<Document> collectionPessoas = mongoDB.getCollection("Pessoas");
    		MongoCollection<Document> collectionFuncionarios = mongoDB.getCollection("Funcionarios");
    		MongoCursor<Document> cursor = collectionFuncionarios.find(eq("funcao", "Médico(a)")).iterator();
    		cursor.forEachRemaining(funcionario -> {
    			Document d = new Document();
    			d.append("funcionario_id", funcionario.getObjectId("_id"));
    			if (funcionario.containsKey("alias")) d.append("alias", funcionario.getString("alias"));
    			if (funcionario.containsKey("pessoa_id")) {
    				d.append("pessoa_id", funcionario.getObjectId("pessoa_id"));
    				Document p = collectionPessoas.find(eq("_id", funcionario.getObjectId("pessoa_id"))).first();
    				d.append("name", p.getString("name"));
    			}
    			lista.add(d);
    		});
    	} catch (Exception e){
    		arquivaErro("Erro em ApoiosMongoADO.getListaMedicos()", e);
    	}
		return lista;
    }
    
    private static List<Document> getListaReceitas(ObjectId _id){
    	List<Document> lista = new ArrayList<>();
    	MongoClientURI connectionString = new MongoClientURI(Login.getURL());
    	try (MongoClient mongoClient = new MongoClient(connectionString)){
    		MongoDatabase mongoDB = mongoClient.getDatabase(Login.bd);
    		MongoCollection<Document> collection = mongoDB.getCollection("Receitas");
    		MongoCursor<Document> cursor = collection.find(eq("paciente_id",_id))
    										.iterator();
    		while(cursor.hasNext()) {
    			lista.add(cursor.next());
    		}
    	} catch (Exception e){
    		arquivaErro("Erro em ApoiosMongoADO.getVersion()", e);
    	}
		return lista;
    }
    
    private static boolean delReceita(ObjectId _id) {
    	MongoClientURI connectionString = new MongoClientURI(Login.getURL());
    	try (MongoClient mongoClient = new MongoClient(connectionString)){
    		MongoDatabase mongoDB = mongoClient.getDatabase(Login.bd);
    		MongoCollection<Document> collection = mongoDB.getCollection("Receitas");
    		Document retorno = collection.findOneAndDelete(eq("_id",_id));
    		if (retorno != null) return true;
    	} catch (Exception e){
    		arquivaErro("Erro em ApoiosMongoADO.delReceita(Receita_Id)", e);
    	}
    	return false;
    }
    
    private static Boolean addReceita(Document receita) {
    	MongoClientURI connectionString = new MongoClientURI(Login.getURL());
    	try (MongoClient mongoClient = new MongoClient(connectionString)){
    		MongoDatabase mongoDB = mongoClient.getDatabase(Login.bd);
    		MongoCollection<Document> collection = mongoDB.getCollection("Receitas");
    		collection.insertOne(receita);
    		return true;
    	} catch (Exception e){
    		arquivaErro("Erro em ApoiosMongoADO.getPacientes(_id)", e);
    	}
    	return false;
    }
    
    private static Boolean setReceita(Document receita) {
    	MongoClientURI connectionString = new MongoClientURI(Login.getURL());
    	try (MongoClient mongoClient = new MongoClient(connectionString)){
    		MongoDatabase mongoDB = mongoClient.getDatabase(Login.bd);
    		MongoCollection<Document> collection = mongoDB.getCollection("Receitas");
    		Document d = collection.findOneAndReplace(eq("_id",receita.getObjectId("_id")), receita);
    		if (d != null) return true;
    	} catch (Exception e){
    		arquivaErro("Erro em ApoiosMongoADO.setReceita(receita)", e);
    	}
    	return false;
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
//                MongoDatabase database = mongoClient.getDatabase(Login.bd);
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
//            	MongoDatabase database = mongoClient.getDatabase(Login.bd);
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
            	MongoDatabase database = mongoClient.getDatabase(Login.bd);
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
/*                Platform.runLater(new Runnable(){
                    @Override public void run() {
                        Alert alert = new Alert(Alert.AlertType.ERROR,
                            "Erro gravado em errolog.txt",
                            ButtonType.OK);
                        alert.setTitle("ERRO");
                        alert.setHeaderText("Desculpa, mas ocorreu um erro.");
                        alert.showAndWait();
                    }
                });*/
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
    			MongoDatabase mongoDB = mongoClient.getDatabase(Login.bd);
    			MongoCollection<Document> collection = mongoDB.getCollection("Funcionarios");
    			Document doc = new Document();
    			doc.append("versao", Login.VERSAO);
    			collection.insertOne(doc);
    		}
    	} catch (Exception putz) {
    		arquivaErro("Erro em ApoiosMongoADO.bdUpdate()", putz);
    	}
    }
    
}
