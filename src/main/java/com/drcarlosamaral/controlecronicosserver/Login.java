/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.drcarlosamaral.controlecronicosserver;

import com.mongodb.MongoClient;
import com.mongodb.MongoClientURI;
import com.mongodb.client.MongoCollection;
import com.mongodb.client.MongoCursor;
import com.mongodb.client.MongoDatabase;

import static com.mongodb.client.model.Filters.eq;
import static com.mongodb.client.model.Filters.exists;
import java.util.ArrayList;
import java.util.List;
import java.util.Objects;
import org.bson.Document;

/**
 *
 * @author Dr.Carlos Amaral
 */
public class Login {
    private static String name;
    private static String URL = "mongodb://usuario:xyz123@localhost:27017";
    public static final String bd = "ControleCronicosBD2";//"ControleCronicosBD2";//"ControleNovaAmerica"; //"ControleCronicosBD";
    public static final String VERSAO = "1.1.0 (void)";
    
    /**
     * Esquema de confirmação de log.
     * @param login String do login do usuário
     * @param pwd Long, password já encriptada (a criptografia deve ocorrer no client.)
     * @return true se estiver correto e false se não bater o login com a senha.
     */
    public static synchronized boolean log(String login, Long pwd){
        if (login.equals("Admin")) {
            if (Objects.equals(1623L, pwd)){
                name = "Administrador do Sistema";
                return true;
            }
        } else {
        	MongoClientURI connectionString = new MongoClientURI(Login.getURL());
            try (MongoClient mongoClient = new MongoClient(connectionString)){
                MongoDatabase mongoDB = mongoClient.getDatabase(Login.bd);
                MongoCollection<Document> collection = mongoDB.getCollection("Funcionarios");
                Document pDoc = collection.find(eq("login", login)).first();
                if (pDoc == null) return false;
                if (Objects.equals(pDoc.getLong("pwd"), pwd)){
                    Login.name = pDoc.getString("nome");
                } else {
                    return false;
                }
            } catch (Exception putz) {
                ApoiosMongoADO.arquivaErro("Erro em Login.log(login,pwd)", putz);
                return false;
            }
        }
        return true;
    }
    
    /**
     * Gravação de Novo Usuário no Banco de Dados
     * @param adminName String
     * @param adminPwd Long
     * @param userName String
     * @param userLog String
     * @param userPwd Long
     * @return true se criou corretamente e false se algo deu errado.
     */
    public synchronized boolean newUser(String adminName, Long adminPwd, String userName, String userLog, Long userPwd){
        if (log(adminName, adminPwd)){
            if (userName != null && !userName.isEmpty()){
                try (MongoClient mongoClient = new MongoClient(new MongoClientURI(URL))){
                    MongoDatabase mongoDB = mongoClient.getDatabase(Login.bd);
                    MongoCollection<Document> collection = mongoDB.getCollection("Funcionarios");
                    collection.insertOne(new Document("nome", userName)
                            .append("login",userLog)
                            .append("pwd", userPwd));
                    return true;
                } catch (Exception putz) {
                    ApoiosMongoADO.arquivaErro("Erro em Login.newUser()", putz);
                }
            }
        }
        return false;
    }
    
    public synchronized boolean deleteUser(String adminName, Long adminPwd, String userLog){
        if (log(adminName, adminPwd)){
            if (userLog != null && !userLog.isEmpty()){
                try (MongoClient mongoClient = new MongoClient(new MongoClientURI(URL))){
                    MongoDatabase mongoDB = mongoClient.getDatabase(Login.bd);
                    MongoCollection<Document> collection = mongoDB.getCollection("Funcionarios");
                    Document doc = collection.findOneAndDelete(eq("login", userLog));
                    if (doc != null) return true;
                } catch (Exception putz) {
                    ApoiosMongoADO.arquivaErro("Erro em Login.deleteUser()", putz);
                }
            }
        }
        return false;
    }
    
    public synchronized boolean alterarSenha(String userLog, String novaSenha){
        if (userLog != null && !userLog.isEmpty()){
            try (MongoClient mongoClient = new MongoClient(new MongoClientURI(URL))){
                MongoDatabase mongoDB = mongoClient.getDatabase(Login.bd);
                MongoCollection<Document> collection = mongoDB.getCollection("Funcionarios");
                Document doc = collection.find(eq("login", userLog)).first();
                if (doc == null) {
                    return false;
                } else {
                    doc.replace("pwd", novaSenha);
                    collection.replaceOne(eq("login",userLog), doc);
                    return true;
                }
            } catch (Exception putz) {
                ApoiosMongoADO.arquivaErro("Erro em Login.alterarSenha()", putz);
            }
        }
        return false;
    }
    
    public static String getUser(){
        return name;
    }
    
    public static List<String> getUsers(){
        List<String> users = new ArrayList<>();
        try (MongoClient mongoClient = new MongoClient(new MongoClientURI(URL))){
                MongoDatabase mongoDB = mongoClient.getDatabase(Login.bd);
                MongoCollection<Document> collection = mongoDB.getCollection("Funcionarios");
                MongoCursor<Document> cursor = collection.find(exists("login")).iterator();
                while (cursor.hasNext()){
                    users.add(cursor.next().getString("login"));
                }
                if (users.size() > 1){
                    users.sort((a,b) -> a.compareTo(b));
                }
            } catch (Exception putz) {
                ApoiosMongoADO.arquivaErro("Erro em Login.getUsers()", putz);
            }
        return users;
    }

	public static String getURL() {
		return URL;
	}

	public static void setURL(String uRL) {
		URL = uRL;
	}

}
