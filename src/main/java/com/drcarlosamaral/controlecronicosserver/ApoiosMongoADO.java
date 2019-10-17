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
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import org.bson.Document;
import org.bson.types.ObjectId;

import com.mongodb.MongoClient;
import com.mongodb.MongoClientURI;
import com.mongodb.client.DistinctIterable;
import com.mongodb.client.MongoCollection;
import com.mongodb.client.MongoCursor;
import com.mongodb.client.MongoDatabase;

import javafx.beans.property.BooleanProperty;
import javafx.beans.property.SimpleBooleanProperty;

import static com.mongodb.client.model.Filters.*;

/**
 *
 * @author Carlos Amaral
 */
public class ApoiosMongoADO {
	
    public ApoiosMongoADO(){};

    public Boolean coneccaoValida(){
        try {
	        MongoClientURI connectionString = new MongoClientURI(Login.getURL());
	        try (MongoClient mongoClient = new MongoClient(connectionString)){
	            MongoDatabase database = mongoClient.getDatabase(Login.bd);
	            MongoCollection<Document> collection = database.getCollection("Funcionarios");
	            Document documento = collection.find(eq("login", "usuario")).first();
	            if (documento == null) { //parece que não tem nem o banco de dados
	            	Document d1 = new Document();
	            	Document d2 = new Document();
	            	d1.append("nome", "Usuário");
	            	d1.append("login", "usuario");
	            	d1.append("pwd", 1766L);
	            	d1.append("grupo", "usuario");
	            	d2.append("grupo", "usuario");
	            	d2.append("versao", Login.VERSAO);
	            	// List<Document> documents = new ArrayList<>();
	            	collection.insertOne(d1);
	            	collection.insertOne(d2);
	            }
	            documento = collection.find(eq("Alias", "Administrador")).first();
	            if (documento == null) {
	            	Document d3 = new Document();
	            	d3.append("Alias", "Administrador");
	            	d3.append("login", "Admin");
	            	Long pwdL = encripted("Admin", "xyz123");
	            	d3.append("pwd", pwdL);
	            	d3.append("grupo", "adm");
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

    public Object get(String msg, ObjectOutputStream oos, ObjectInputStream ois) {
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
	    	} else if (msg.equals("consulta")) {
	    		return getConsulta(oos,ois);
	    	} else if (msg.equals("gestacao")) {
	    		return getGestacao(oos,ois);
	    	} else if (msg.equals("impresso")) {
	    		return getImpresso(oos,ois);
	    	} else if (msg.equals("paciente")) {
	    		return getPaciente(oos,ois);
	    	} else if (msg.equals("funcionario")) {
	    		return getFuncionario(oos,ois);
	    	} else if (msg.equals("enderecoByFF")) {
	    		return getEnderecoByFF(oos,ois);
	    	} else if (msg.equals("enderecoById")) {
	    		return getEnderecoById(oos,ois);
	    	} else if (msg.equals("controleHas")) {
	    		return getControleHas(oos,ois);
	    	} else if (msg.equals("controleDm")) {
	    		return getControleDm(oos,ois);
	    	} else if (msg.equals("controleGest")) {
	    		return getControleGest(oos,ois);
	    	} else if (msg.equals("controleComAlarmesAtivos")) {
	    		return getControleComAlarmesAtivos(oos,ois);
	    	} else if (msg.equals("controleSm")) {
	    		return getControleSm(oos,ois);
	    	} else {
	    		return new Document();
	    	}
    	} catch (Exception e) {
    		e.printStackTrace();
    		return new Document();
    	}
    }
    
    public List<Document> getLista(ObjectOutputStream oos, ObjectInputStream ois) {
    	
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
    		} else if (oque.equals("consultas")) {
    			String passandoOQue = (String) ois.readObject();
    			if (passandoOQue.equals("paciente_id")) {
    				ObjectId paciente_id = (ObjectId) ois.readObject();
    				return getListaConsultasByPaciente_id(paciente_id);
    			} else if (passandoOQue.equals("paciente_id, funcionario_id")) {
    				ObjectId paciente_id = (ObjectId) ois.readObject();
    				ObjectId funcionario_id = (ObjectId) ois.readObject();
    				return getListaConsultasByPacienteEFuncionario(paciente_id, funcionario_id);
    			} else if (passandoOQue.equals("paciente_id, funcaoDoFuncionario")) {
    				ObjectId paciente_id = (ObjectId) ois.readObject();
    				String funcaoDoFuncionario = (String) ois.readObject();
    				return getListaConsultasByPacienteEFuncao(paciente_id, funcaoDoFuncionario);
    			}
    		} else if (oque.equals("exames")) {
    			String passandoOQue = (String) ois.readObject();
    			if (passandoOQue.equals("nomeDoExame")) {
    				String nomeDoExame = (String) ois.readObject();
    				ObjectId paciente_id = (ObjectId) ois.readObject();
    				return getListaDeExames(nomeDoExame, paciente_id);
    			} else if (passandoOQue.equals("paciente_id")) {
    				ObjectId paciente_id = (ObjectId) ois.readObject();
    				return getListaDeExames(paciente_id);
    			}
    		}  else if (oque.equals("impressos")) {
    			String passandoOQue = (String) ois.readObject();
    			if (passandoOQue.equals("paciente_id")) {
    				ObjectId paciente_id = (ObjectId) ois.readObject();
    				return getListaImpressosByPaciente_id(paciente_id);
    			}
    		} else if (oque.equals("funcoes")) {
    			return getListaFuncoes();
    		} else if (oque.equals("especializacoes")) {
				return getListaEspecializacoes();
			}
    	} catch (Exception e) {
    		e.printStackTrace();
    	}
    	return new ArrayList<Document>();
    }
    
    public Boolean del(String msg, ObjectOutputStream oos, ObjectInputStream ois) {
    	try {
	    	if (msg.equals("receita")) {
	    		Object obj = ois.readObject();
	    		if (obj instanceof ObjectId) {
	    			return delReceita((ObjectId) obj);
	    		}
	    	} else if (msg.equals("consulta")) {
	    		Object obj = ois.readObject();
	    		if (obj instanceof ObjectId) {
	    			return delConsulta((ObjectId) obj);
	    		}
    		} else if (msg.equals("impresso")) {
	    		Object obj = ois.readObject();
	    		if (obj instanceof ObjectId) {
	    			return delImpresso((ObjectId) obj);
	    		}
			} else if (msg.equals("exame")) {
	    		Object obj = ois.readObject();
	    		if (obj instanceof ObjectId) {
	    			return delExame((ObjectId) obj);
	    		}
			} else if (msg.equals("gestacao")) {
	    		Object obj = ois.readObject();
	    		if (obj instanceof ObjectId) {
	    			return delGestacao((ObjectId) obj);
	    		}
			}
	    	return false;
    	} catch (Exception e) {
    		e.printStackTrace();
    		return false;
    	}
    }
    
    public Object add(String msg, ObjectOutputStream oos, ObjectInputStream ois) {
    	try {
	    	if (msg.equals("receita")) {
	    		Document receita = (Document) ois.readObject();
	    		return addReceita(receita);
	    	} else
	    	if (msg.equals("pessoa")) {
	    		Document pessoa = (Document) ois.readObject();
	    		return addPessoa(pessoa);
	    	} else
	    	if (msg.equals("consulta")) {
	    		Document consulta = (Document) ois.readObject();
	    		return addConsulta(consulta);
	    	} else
	    	if (msg.equals("impresso")) {
	    		Document impresso = (Document) ois.readObject();
	    		return addImpresso(impresso);
	    	} else
	    	if (msg.equals("exame")) {
	    		Document exame = (Document) ois.readObject();
	    		return addExame(exame);
	    	} else
	    	if (msg.equals("funcionario")) {
	    		Document funcionario = (Document) ois.readObject();
	    		return addFuncionario(funcionario);
	    	} else 
	    	if (msg.equals("paciente")) {
	    		Document paciente = (Document) ois.readObject();
	    		return addPaciente(paciente);
	    	} else
	    	if (msg.equals("controleHas")) {
	    		Document controleHas = (Document) ois.readObject();
	    		return addControleHas(controleHas);
	    	} else
	    	if (msg.equals("controleDm")) {
	    		Document controleDm = (Document) ois.readObject();
	    		return addControleDm(controleDm);
	    	} else
	    	if (msg.equals("controleGest")) {
	    		Document controleGest = (Document) ois.readObject();
	    		return addControleGest(controleGest);
	    	} else
	    	if (msg.equals("controleSm")) {
	    		Document controleSm = (Document) ois.readObject();
	    		return addControleSm(controleSm);
	    	} else
	    	if (msg.equals("gestacao")) {
	    		Document gestacao = (Document) ois.readObject();
	    		return addGestacao(gestacao);
	    	} else
	    	if (msg.equals("endereco")) {
	    		Document endereco = (Document) ois.readObject();
	    		return addEndereco(endereco);
	    	} else
	    	if (msg.equals("arquivo")) {
	    		Document arquivo = (Document) ois.readObject();
	    		return addArquivo(arquivo);
	    	}
	    	return false;
    	} catch (Exception e) {
    		e.printStackTrace();
    		return new Document();
    	}
    }
    
    public Object set(String msg, ObjectOutputStream oos, ObjectInputStream ois) {
    	try {
	    	if (msg.equals("receita")) {
	    		Document receita = (Document) ois.readObject();
	    		return setReceita(receita);
	    	} else
	    	if (msg.equals("pessoa")) {
	    		Document pessoa = (Document) ois.readObject();
	    		return setPessoa(pessoa);
	    	} else
	    	if (msg.equals("paciente")) {
	    		Document paciente = (Document) ois.readObject();
	    		return setPaciente(paciente);
	    	} else
	    	if (msg.equals("controleHas")) {
	    		Document has = (Document) ois.readObject();
	    		return setControleHas(has);
	    	} else
	    	if (msg.equals("controleDm")) {
	    		Document dm = (Document) ois.readObject();
	    		return setControleDm(dm);
	    	} else
	    	if (msg.equals("controleGest")) {
	    		Document cGest = (Document) ois.readObject();
	    		return setControleGest(cGest);
	    	} else
	    	if (msg.equals("controleSm")) {
	    		Document cSm = (Document) ois.readObject();
	    		return setControleSm(cSm);
	    	} else
	    	if (msg.equals("gestacao")) {
	    		Document gestacao = (Document) ois.readObject();
	    		return setGestacao(gestacao);
	    	} else
	    	if (msg.equals("impresso")) {
	    		Document impresso = (Document) ois.readObject();
	    		return setImpresso(impresso);
	    	} else 
	    	if (msg.equals("exame")) {
	    		Document exame = (Document) ois.readObject();
	    		return setExame(exame);
	    	} else
	    	if (msg.equals("funcionario")) {
	    		Document funcionario = (Document) ois.readObject();
	    		return setFuncionario(funcionario);
	    	} else
	    	if (msg.equals("consulta")) {
	    		Document consulta = (Document) ois.readObject();
	    		return setConsulta(consulta);
	    	} else
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
    
    private Document getPessoa(ObjectOutputStream oos, ObjectInputStream ois) {
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

    private Document getConsulta(ObjectOutputStream oos, ObjectInputStream ois) {
    	Document doc = new Document();
    	// vai passar o que?
    	try {
			String resposta = (String) ois.readObject();
			if (resposta.equals("paciente_id, funcionario_id, data")) {
				ObjectId paciente_id = (ObjectId) ois.readObject();
				ObjectId funcionario_id = (ObjectId) ois.readObject();
				Date date = (Date) ois.readObject();
				return getConsulta(paciente_id, funcionario_id, date);
			}
		} catch (Exception e) {
			e.printStackTrace();
		}
    	return doc;
    }
    
    private Document getGestacao(ObjectOutputStream oos, ObjectInputStream ois) {
    	Document doc = new Document();
    	// vai passar o que?
    	try {
			String resposta = (String) ois.readObject();
			if (resposta.equals("ObjectId")) {
				ObjectId _id = (ObjectId) ois.readObject();
				return getGestacao(_id);
			}
		} catch (Exception e) {
			e.printStackTrace();
		}
    	return doc;
    }

    private Document getImpresso(ObjectOutputStream oos, ObjectInputStream ois) {
    	Document doc = new Document();
    	// vai passar o que?
    	try {
			String resposta = (String) ois.readObject();
			if (resposta.equals("_id")) {
				ObjectId _id = (ObjectId) ois.readObject();
				return getImpresso(_id);
			}
		} catch (Exception e) {
			e.printStackTrace();
		}
    	return doc;
    }
    
    private Document getFuncionario(ObjectOutputStream oos, ObjectInputStream ois) {
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
    
    private Document getFuncionario(String login, Integer pwd) {
    	
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
    
    private  Document getFuncionarioByAlias(String alias) {
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
    
    private  Document getFuncionarioById(ObjectId _id) {
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
    
    private  Document getEnderecoById(ObjectOutputStream oos, ObjectInputStream ois) {
    	Document doc = new Document();
    	MongoClientURI connectionString = new MongoClientURI(Login.getURL());
    	try (MongoClient mongoClient = new MongoClient(connectionString)){
    		ObjectId _id = (ObjectId) ois.readObject();
    		MongoDatabase mongoDB = mongoClient.getDatabase(Login.bd);
    		MongoCollection<Document> collection = mongoDB.getCollection("Enderecos");
    		doc = collection.find(eq("_id", _id)).first();
    	} catch (Exception e){
    		arquivaErro("Erro em ApoiosMongoADO.getEnderecoById()", e);
    	}
    	return doc;
    }
    
    private  Document getEnderecoByFF(ObjectOutputStream oos, ObjectInputStream ois) {
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
    
    private  Document getPaciente(ObjectOutputStream oos, ObjectInputStream ois) {
    	Document doc = new Document();
    	// vai passar o que?
    	try {
			String resposta = (String) ois.readObject();
			if (resposta.equals("ObjectId")) {
				Object obj = ois.readObject();
				if (obj instanceof ObjectId) {
					return getPaciente((ObjectId) obj);
				}
			} else if (resposta.equals("cns")) {
				Object obj = ois.readObject();
				if (obj instanceof Long) {
					return getPacienteByCns((Long) obj);
				}
			}
		} catch (Exception e) {
			e.printStackTrace();
		}
    	return doc;
    }
    
    private  Document getPaciente(ObjectId _id) {
    	Document doc = new Document();
    	MongoClientURI connectionString = new MongoClientURI(Login.getURL());
    	try (MongoClient mongoClient = new MongoClient(connectionString)){
    		MongoDatabase mongoDB = mongoClient.getDatabase(Login.bd);
    		MongoCollection<Document> collection = mongoDB.getCollection("Pacientes");
    		doc = collection.find(eq("_id", _id)).first();
    	} catch (Exception e){
    		arquivaErro("Erro em ApoiosMongoADO.getPaciente(_id)", e);
    	}
    	return doc;
    }
    
    private  Document getPacienteByCns(Long cns) {
    	Document doc = new Document();
    	MongoClientURI connectionString = new MongoClientURI(Login.getURL());
    	try (MongoClient mongoClient = new MongoClient(connectionString)){
    		MongoDatabase mongoDB = mongoClient.getDatabase(Login.bd);
    		MongoCollection<Document> collection = mongoDB.getCollection("Pacientes");
    		doc = collection.find(eq("cns", cns)).first();
    	} catch (Exception e){
    		arquivaErro("Erro em ApoiosMongoADO.getPacienteByCns(cns)", e);
    	}
    	return doc;
    }
    
    private  ObjectId addPaciente(Document paciente) {
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
    
    private  Document getControleHas(ObjectOutputStream oos, ObjectInputStream ois) {
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

    private  Document getControleDm(ObjectOutputStream oos, ObjectInputStream ois) {
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

    private  Document getControleGest(ObjectOutputStream oos, ObjectInputStream ois) {
    	Document doc = new Document();
    	// vai passar o que?
    	try {
			String resposta = (String) ois.readObject();
			if (resposta.equals("ObjectId")) {
				ObjectId oId = (ObjectId) ois.readObject();
				return getControleGest(oId);
			} else if (resposta.equals("PacienteId")) {
				ObjectId PacienteId = (ObjectId) ois.readObject();
				return getControleGestByPacienteId(PacienteId);
			} else if (resposta.equals("GestacaoId")) {
				ObjectId gestacaoId = (ObjectId) ois.readObject();
				return getControleGestByGestacaoId(gestacaoId);
			}
		} catch (Exception e) {
			e.printStackTrace();
		}
    	return doc;
    }

    private  Document getControleSm(ObjectOutputStream oos, ObjectInputStream ois) {
    	Document doc = new Document();
    	// vai passar o que?
    	try {
			String resposta = (String) ois.readObject();
			if (resposta.equals("ObjectId")) {
				ObjectId oId = (ObjectId) ois.readObject();
				return getControleSm(oId);
			} else if (resposta.equals("PacienteId")) {
				ObjectId PacienteId = (ObjectId) ois.readObject();
				return getControleSmByPacienteId(PacienteId);
			}
		} catch (Exception e) {
			e.printStackTrace();
		}
    	return doc;
    }
    
    private  ObjectId addControleHas(Document controleHas) {
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

    private  ObjectId addControleDm(Document controleDm) {
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

    private  ObjectId addControleGest(Document controleGest) {
    	MongoClientURI connectionString = new MongoClientURI(Login.getURL());
    	try (MongoClient mongoClient = new MongoClient(connectionString)){
    		MongoDatabase mongoDB = mongoClient.getDatabase(Login.bd);
    		MongoCollection<Document> collection = mongoDB.getCollection("ControleGest");
    		collection.insertOne(controleGest);
    		return controleGest.getObjectId("_id");
    	} catch (Exception e){
    		e.printStackTrace();
    		arquivaErro("Erro em ApoiosMongoADO.addControleGest(Document)", e);
    	}
    	return null;
    }

    private  ObjectId addControleSm(Document controleSm) {
    	MongoClientURI connectionString = new MongoClientURI(Login.getURL());
    	try (MongoClient mongoClient = new MongoClient(connectionString)){
    		MongoDatabase mongoDB = mongoClient.getDatabase(Login.bd);
    		MongoCollection<Document> collection = mongoDB.getCollection("SaudeDaMulher");
    		collection.insertOne(controleSm);
    		return controleSm.getObjectId("_id");
    	} catch (Exception e){
    		e.printStackTrace();
    		arquivaErro("Erro em ApoiosMongoADO.addControleSm(Document)", e);
    	}
    	return null;
    }
    
    private  ObjectId addGestacao(Document gestacao) {
    	if (gestacao.containsKey("_id") && gestacao.get("_id") == null) {
    		gestacao.remove("_id"); 
    	}
    	MongoClientURI connectionString = new MongoClientURI(Login.getURL());
    	try (MongoClient mongoClient = new MongoClient(connectionString)){
    		MongoDatabase mongoDB = mongoClient.getDatabase(Login.bd);
    		MongoCollection<Document> collection = mongoDB.getCollection("Gestacoes");
    		collection.insertOne(gestacao);
    		System.out.println("Acaba de anexar nova gestacao. _id = " + gestacao.getObjectId("_id"));
    		return gestacao.getObjectId("_id");
    	} catch (Exception e){
    		e.printStackTrace();
    		arquivaErro("Erro em ApoiosMongoADO.addGestacao(Document)", e);
    	}
    	return null;
    }

    private List<Document> getControleComAlarmesAtivos(ObjectOutputStream oos, ObjectInputStream ois) {
    	List<Document> lista = new ArrayList<Document>();
    	// Controle do quê?
    	try {
			String resposta = (String) ois.readObject();
			if (resposta.equals("gestacao")) {
				return getControlesAtivados("ControleGest");
			}
		} catch (Exception e) {
			e.printStackTrace();
		}
    	return lista;
    }
    
    private List<Document> getControlesAtivados(String collection) {
    	List<Document> lista = new ArrayList<Document>();
    	Date now = DateUtils.asDate(LocalDate.now());
    	MongoClientURI connectionString = new MongoClientURI(Login.getURL());
    	try (MongoClient mongoClient = new MongoClient(connectionString)){
    		MongoDatabase mongoDB = mongoClient.getDatabase(Login.bd);
    		MongoCollection<Document> mongoCollection = mongoDB.getCollection(collection);
    		
    		MongoCursor<Document> cursor = mongoCollection.find(and(exists("alarmes", true), lt("alarmes.data", now))).iterator();
    		cursor.forEachRemaining(controle -> {
    			lista.add(controle);
    		});
    	} catch (Exception e){
    		arquivaErro("Erro em ApoiosMongoADO.getControlesAtivados(String collection)", e);
    	}
    	return lista;
    }
    
    private  ObjectId addEndereco(Document endereco) {
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
    private ObjectId addArquivo(Document arquivo) {
    	MongoClientURI connectionString = new MongoClientURI(Login.getURL());
    	try (MongoClient mongoClient = new MongoClient(connectionString)){
    		MongoDatabase mongoDB = mongoClient.getDatabase(Login.bd);
    		MongoCollection<Document> collection = mongoDB.getCollection("Arquivos");
    		collection.insertOne(arquivo);
    		return arquivo.getObjectId("_id");
    	} catch (Exception e){
    		arquivaErro("Erro em ApoiosMongoADO.addArquivo(arquivo)", e);
    	}
    	return null;
    }
    private  Boolean setPaciente(Document paciente) {
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
    private  Boolean setControleHas(Document has) {
    	MongoClientURI connectionString = new MongoClientURI(Login.getURL());
    	try (MongoClient mongoClient = new MongoClient(connectionString)){
    		MongoDatabase mongoDB = mongoClient.getDatabase(Login.bd);
    		MongoCollection<Document> collection = mongoDB.getCollection("HAS");
    		Document d = collection.findOneAndReplace(eq("_id", has.getObjectId("_id")), has);
    		if (d != null) return true;
    	} catch (Exception e){
    		arquivaErro("Erro em ApoiosMongoADO.setControleHas(has)", e);
    	}
    	return false;
    }
    private  Boolean setControleDm(Document dm) {
    	MongoClientURI connectionString = new MongoClientURI(Login.getURL());
    	try (MongoClient mongoClient = new MongoClient(connectionString)){
    		MongoDatabase mongoDB = mongoClient.getDatabase(Login.bd);
    		MongoCollection<Document> collection = mongoDB.getCollection("DM");
    		Document d = collection.findOneAndReplace(eq("_id", dm.getObjectId("_id")), dm);
    		if (d != null) return true;
    	} catch (Exception e){
    		arquivaErro("Erro em ApoiosMongoADO.setControleDm(dm)", e);
    	}
    	return false;
    }
    private  Boolean setControleGest(Document cGest) {
    	MongoClientURI connectionString = new MongoClientURI(Login.getURL());
    	try (MongoClient mongoClient = new MongoClient(connectionString)){
    		MongoDatabase mongoDB = mongoClient.getDatabase(Login.bd);
    		MongoCollection<Document> collection = mongoDB.getCollection("ControleGest");
    		Document d = collection.findOneAndReplace(eq("_id", cGest.getObjectId("_id")), cGest);
    		if (d != null) return true;
    	} catch (Exception e){
    		e.printStackTrace();
    		arquivaErro("Erro em ApoiosMongoADO.setControleGest()", e);
    	}
    	return false;
    }

    private  Boolean setControleSm(Document cSm) {
    	MongoClientURI connectionString = new MongoClientURI(Login.getURL());
    	try (MongoClient mongoClient = new MongoClient(connectionString)){
    		MongoDatabase mongoDB = mongoClient.getDatabase(Login.bd);
    		MongoCollection<Document> collection = mongoDB.getCollection("SaudeDaMulher");
    		Document d = collection.findOneAndReplace(eq("_id", cSm.getObjectId("_id")), cSm);
    		if (d != null) return true;
    	} catch (Exception e){
    		e.printStackTrace();
    		arquivaErro("Erro em ApoiosMongoADO.setControleSm()", e);
    	}
    	return false;
    }
    
    private  Boolean setGestacao(Document gestacao) {
    	MongoClientURI connectionString = new MongoClientURI(Login.getURL());
    	try (MongoClient mongoClient = new MongoClient(connectionString)){
    		MongoDatabase mongoDB = mongoClient.getDatabase(Login.bd);
    		MongoCollection<Document> collection = mongoDB.getCollection("Gestacoes");
    		Document d = collection.findOneAndReplace(eq("_id", gestacao.getObjectId("_id")), gestacao);
    		if (d != null) return true;
    	} catch (Exception e) {
    		e.printStackTrace();
    		arquivaErro("Erro em ApoiosMongoADO.setGestacao()", e);
    	}
    	return false;
    }
    private  Boolean setEndereco(Document endereco) {
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
    
    private  List<Document> getPessoa(String nome) {
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
    
    private  Document getPessoa(ObjectId _id) {
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
    private  ObjectId addPessoa(Document pessoa) {
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
    private  Boolean setPessoa(Document pessoa) {
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
    private  Boolean setConsulta(Document consulta) {
    	MongoClientURI connectionString = new MongoClientURI(Login.getURL());
    	try (MongoClient mongoClient = new MongoClient(connectionString)){
    		MongoDatabase mongoDB = mongoClient.getDatabase(Login.bd);
    		MongoCollection<Document> collection = mongoDB.getCollection("Consultas");
    		Document d = collection.findOneAndReplace(eq("_id",consulta.getObjectId("_id")), consulta);
    		if (d != null) return true;
    	} catch (Exception e){
    		arquivaErro("Erro em ApoiosMongoADO.setConsulta(consulta)", e);
    	}
    	return false;
    }
    private  Boolean delConsulta(ObjectId _id) {
    	MongoClientURI connectionString = new MongoClientURI(Login.getURL());
    	try (MongoClient mongoClient = new MongoClient(connectionString)){
    		MongoDatabase mongoDB = mongoClient.getDatabase(Login.bd);
    		MongoCollection<Document> collection = mongoDB.getCollection("Consultas");
    		Document d = collection.findOneAndDelete(eq("_id", _id));
    		if (d != null) return true;
    	} catch (Exception e){
    		arquivaErro("Erro em ApoiosMongoADO.delConsulta(consulta_id)", e);
    	}
    	return false;
    }
    private  Document getConsulta(ObjectId paciente_id, ObjectId funcionario_id, Date data) {
    	Document doc = null;
    	MongoClientURI connectionString = new MongoClientURI(Login.getURL());
    	try (MongoClient mongoClient = new MongoClient(connectionString)){
    		MongoDatabase mongoDB = mongoClient.getDatabase(Login.bd);
    		MongoCollection<Document> collection = mongoDB.getCollection("Consultas");
    		doc = collection.find(and(eq("paciente_id", paciente_id), 
    				eq("funcionario_id", funcionario_id),
    				eq("data", data))).first();
    	} catch (Exception e){
    		arquivaErro("Erro em ApoiosMongoADO.getPessoa(_id)", e);
    	}
    	return doc;
    }
    private  Document getGestacao(ObjectId _id) {
    	Document doc = null;
    	MongoClientURI connectionString = new MongoClientURI(Login.getURL());
    	try (MongoClient mongoClient = new MongoClient(connectionString)){
    		MongoDatabase mongoDB = mongoClient.getDatabase(Login.bd);
    		MongoCollection<Document> collection = mongoDB.getCollection("Gestacoes");
    		doc = collection.find(eq("_id", _id)).first();
    	} catch (Exception e){
    		e.printStackTrace();
    		arquivaErro("Erro em ApoiosMongoADO.getGestacao(_id)", e);
    	}
    	return doc;
    }
    private  ObjectId addConsulta(Document consulta) {
    	MongoClientURI connectionString = new MongoClientURI(Login.getURL());
    	try (MongoClient mongoClient = new MongoClient(connectionString)){
    		MongoDatabase mongoDB = mongoClient.getDatabase(Login.bd);
    		MongoCollection<Document> collection = mongoDB.getCollection("Consultas");
    		collection.insertOne(consulta);
    		return consulta.getObjectId("_id");
    	} catch (Exception e){
    		arquivaErro("Erro em ApoiosMongoADO.addConsulta(Document consulta)", e);
    	}
    	return null;
    }
    
    public  List<Document> getListaConsultasByPaciente_id(ObjectId paciente_id) {
    	List<Document> docs = new ArrayList<Document>();
    	MongoClientURI connectionString = new MongoClientURI(Login.getURL());
    	try (MongoClient mongoClient = new MongoClient(connectionString)){
    		MongoDatabase mongoDB = mongoClient.getDatabase(Login.bd);
    		MongoCollection<Document> collection = mongoDB.getCollection("Consultas");
    		MongoCursor<Document> cursor = collection.find(eq("paciente_id", paciente_id)).iterator();
    		while (cursor.hasNext()) {
    			docs.add(cursor.next());
    		}
    	} catch (Exception e) {
    		arquivaErro("Erro em ApoiosMongoADO.getListaConsultasByPaciente(Object_id paciente_id)", e);
    	}
    	return docs;
    }
    
    public  List<Document> getListaConsultasByPacienteEFuncionario(ObjectId paciente_id, ObjectId funcionario_id) {
    	List<Document> docs = new ArrayList<Document>();
    	MongoClientURI connectionString = new MongoClientURI(Login.getURL());
    	try (MongoClient mongoClient = new MongoClient(connectionString)){
    		MongoDatabase mongoDB = mongoClient.getDatabase(Login.bd);
    		MongoCollection<Document> collection = mongoDB.getCollection("Consultas");
    		MongoCursor<Document> cursor = collection.find(and(
    				eq("paciente_id", paciente_id),
    				eq("funcionario_id", funcionario_id))).iterator();
    		while (cursor.hasNext()) {
    			docs.add(cursor.next());
    		}
    	} catch (Exception e) {
    		arquivaErro("Erro em ApoiosMongoADO.getListaConsultasByPacienteEMedico(paciente_id, medico_id)", e);
    	}
    	return docs;
    }
    
    public  List<Document> getListaConsultasByPacienteEFuncao(ObjectId paciente_id, String funcaoDoFuncionario) {
    	List<Document> docs = new ArrayList<Document>();
    	MongoClientURI connectionString = new MongoClientURI(Login.getURL());
    	try (MongoClient mongoClient = new MongoClient(connectionString)){
    		MongoDatabase mongoDB = mongoClient.getDatabase(Login.bd);
    		MongoCollection<Document> collection = mongoDB.getCollection("Consultas");
    		MongoCollection<Document> fCollection = mongoDB.getCollection("Funcionarios");
    		MongoCursor<Document> fcursor = fCollection.find(eq("funcao", funcaoDoFuncionario)).iterator();
    		while (fcursor.hasNext()) {
    			Document funcionarioDoc =  fcursor.next();
    			MongoCursor<Document> cursor = collection.find(and(
        				eq("paciente_id", paciente_id),
        				eq("funcionario_id", funcionarioDoc.getObjectId("_id")))).iterator();
        		while (cursor.hasNext()) {
        			docs.add(cursor.next());
        		}
    		}
    	} catch (Exception e) {
    		arquivaErro("Erro em ApoiosMongoADO.getListaConsultasByPacienteEFuncao(paciente_id, funcaoDoFuncionario)", e);
    		e.printStackTrace();
    	}
    	return docs;
    }
    
    private  ObjectId addImpresso(Document impresso) {
    	MongoClientURI connectionString = new MongoClientURI(Login.getURL());
    	try (MongoClient mongoClient = new MongoClient(connectionString)){
    		MongoDatabase mongoDB = mongoClient.getDatabase(Login.bd);
    		MongoCollection<Document> collection = mongoDB.getCollection("Impressos");
    		collection.insertOne(impresso);
    		return impresso.getObjectId("_id");
    	} catch (Exception e){
    		arquivaErro("Erro em ApoiosMongoADO.addImpresso(Document impresso)", e);
    		e.printStackTrace();
    	}
    	return null;
    }
    
    private  ObjectId addExame(Document exame) {
    	MongoClientURI connectionString = new MongoClientURI(Login.getURL());
    	try (MongoClient mongoClient = new MongoClient(connectionString)){
    		MongoDatabase mongoDB = mongoClient.getDatabase(Login.bd);
    		MongoCollection<Document> collection = mongoDB.getCollection("Exames");
    		collection.insertOne(exame);
    		return exame.getObjectId("_id");
    	} catch (Exception e){
    		arquivaErro("Erro em ApoiosMongoADO.addExame(Document exame)", e);
    		e.printStackTrace();
    	}
    	return null;
    }
    
    private  Boolean setImpresso(Document impresso) {
    	MongoClientURI connectionString = new MongoClientURI(Login.getURL());
    	try (MongoClient mongoClient = new MongoClient(connectionString)){
    		MongoDatabase mongoDB = mongoClient.getDatabase(Login.bd);
    		MongoCollection<Document> collection = mongoDB.getCollection("Impressos");
    		Document d = collection.findOneAndReplace(eq("_id",impresso.getObjectId("_id")), impresso);
    		if (d != null) return true;
    	} catch (Exception e){
    		arquivaErro("Erro em ApoiosMongoADO.setPaciente(paciente)", e);
    	}
    	return false;
    }
    
    private  Boolean setExame(Document exame) {
    	MongoClientURI connectionString = new MongoClientURI(Login.getURL());
    	try (MongoClient mongoClient = new MongoClient(connectionString)){
    		MongoDatabase mongoDB = mongoClient.getDatabase(Login.bd);
    		MongoCollection<Document> collection = mongoDB.getCollection("Exames");
    		Document d = collection.findOneAndReplace(eq("_id", exame.getObjectId("_id")), exame);
    		if (d != null) return true;
    	} catch (Exception e){
    		arquivaErro("Erro em ApoiosMongoADO.setexame(exame)", e);
    	}
    	return false;
    }
    
    private  Document getImpresso(ObjectId _id) {
    	Document doc = null;
    	MongoClientURI connectionString = new MongoClientURI(Login.getURL());
    	try (MongoClient mongoClient = new MongoClient(connectionString)){
    		MongoDatabase mongoDB = mongoClient.getDatabase(Login.bd);
    		MongoCollection<Document> collection = mongoDB.getCollection("Impressos");
    		doc = collection.find(and(eq("_id", _id))).first();
    	} catch (Exception e){
    		arquivaErro("Erro em ApoiosMongoADO.getImpresso(_id)", e);
    		e.printStackTrace();
    	}
    	return doc;
    }
    
    private Boolean delImpresso(ObjectId _id) {
    	MongoClientURI connectionString = new MongoClientURI(Login.getURL());
    	try (MongoClient mongoClient = new MongoClient(connectionString)){
    		MongoDatabase mongoDB = mongoClient.getDatabase(Login.bd);
    		MongoCollection<Document> collection = mongoDB.getCollection("Impressos");
    		Document d = collection.findOneAndDelete(eq("_id", _id));
    		if (d != null) return true;
    	} catch (Exception e){
    		arquivaErro("Erro em ApoiosMongoADO.delImpresso(impresso_id)", e);
    		e.printStackTrace();
    	}
    	return false;
    }
    
    private  Boolean delExame(ObjectId _id) {
    	MongoClientURI connectionString = new MongoClientURI(Login.getURL());
    	try (MongoClient mongoClient = new MongoClient(connectionString)){
    		MongoDatabase mongoDB = mongoClient.getDatabase(Login.bd);
    		MongoCollection<Document> collection = mongoDB.getCollection("Exames");
    		Document d = collection.findOneAndDelete(eq("_id", _id));
    		if (d != null) return true;
    	} catch (Exception e){
    		arquivaErro("Erro em ApoiosMongoADO.delExame(exame_id)", e);
    		e.printStackTrace();
    	}
    	return false;
    } 
    private  Boolean delGestacao(ObjectId _id) {
    	MongoClientURI connectionString = new MongoClientURI(Login.getURL());
    	try (MongoClient mongoClient = new MongoClient(connectionString)){
    		MongoDatabase mongoDB = mongoClient.getDatabase(Login.bd);
    		MongoCollection<Document> collection = mongoDB.getCollection("Gestacoes");
    		Document d = collection.findOneAndDelete(eq("_id", _id));
    		if (d != null) return true;
    	} catch (Exception e){
    		arquivaErro("Erro em ApoiosMongoADO.delGestacao()", e);
    		e.printStackTrace();
    	}
    	return false;
    }
    public  List<Document> getListaImpressosByPaciente_id(ObjectId paciente_id) {
    	List<Document> docs = new ArrayList<Document>();
    	MongoClientURI connectionString = new MongoClientURI(Login.getURL());
    	try (MongoClient mongoClient = new MongoClient(connectionString)){
    		MongoDatabase mongoDB = mongoClient.getDatabase(Login.bd);
    		MongoCollection<Document> collection = mongoDB.getCollection("Impressos");
    		MongoCursor<Document> cursor = collection.find(eq("paciente_id", paciente_id)).iterator();
    		while (cursor.hasNext()) {
    			docs.add(cursor.next());
    		}
    	} catch (Exception e) {
    		arquivaErro("Erro em ApoiosMongoADO.getListaConsultasByPaciente(Object_id paciente_id)", e);
    	}
    	return docs;
    }
    
    public  List<Document> getListaDeExames(ObjectId paciente_id) {
    	List<Document> docs = new ArrayList<Document>();
    	MongoClientURI connectionString = new MongoClientURI(Login.getURL());
    	try (MongoClient mongoClient = new MongoClient(connectionString)){
    		MongoDatabase mongoDB = mongoClient.getDatabase(Login.bd);
    		MongoCollection<Document> collection = mongoDB.getCollection("Exames");
    		MongoCursor<Document> cursor = collection.find(eq("paciente_id", paciente_id)).iterator();
    		while (cursor.hasNext()) {
    			docs.add(cursor.next());
    		}
    	} catch (Exception e) {
    		arquivaErro("Erro em ApoiosMongoADO.getListaDeExames(Object_id paciente_id)", e);
    	}
    	return docs;
    }
    
    public  List<Document> getListaDeExames(String nomeDoExame, ObjectId paciente_id) {
    	List<Document> docs = new ArrayList<Document>();
    	MongoClientURI connectionString = new MongoClientURI(Login.getURL());
    	try (MongoClient mongoClient = new MongoClient(connectionString)){
    		MongoDatabase mongoDB = mongoClient.getDatabase(Login.bd);
    		MongoCollection<Document> collection = mongoDB.getCollection("Exames");
    		MongoCursor<Document> cursor = collection.find(and(eq("paciente_id", paciente_id),eq("nome",nomeDoExame))).iterator();
    		while (cursor.hasNext()) {
    			docs.add(cursor.next());
    		}
    	} catch (Exception e) {
    		arquivaErro("Erro em ApoiosMongoADO.getListaDeExames(Object_id paciente_id)", e);
    	}
    	return docs;
    }
    
    private  Document getControleHas(ObjectId _id) {
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
    

    private  Document getControleDm(ObjectId _id) {
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

    private  Document getControleGest(ObjectId _id) {
    	Document doc = new Document();
    	MongoClientURI connectionString = new MongoClientURI(Login.getURL());
    	try (MongoClient mongoClient = new MongoClient(connectionString)){
    		MongoDatabase mongoDB = mongoClient.getDatabase(Login.bd);
    		MongoCollection<Document> collection = mongoDB.getCollection("ControleGest");
    		doc = collection.find(eq("_id", _id)).first();
    	} catch (Exception e){
    		arquivaErro("Erro em ApoiosMongoADO.getControleGest(_id)", e);
    	}
    	return doc;
    }

    private  Document getControleSm(ObjectId _id) {
    	Document doc = new Document();
    	MongoClientURI connectionString = new MongoClientURI(Login.getURL());
    	try (MongoClient mongoClient = new MongoClient(connectionString)){
    		MongoDatabase mongoDB = mongoClient.getDatabase(Login.bd);
    		MongoCollection<Document> collection = mongoDB.getCollection("SaudeDaMulher");
    		doc = collection.find(eq("_id", _id)).first();
    	} catch (Exception e){
    		arquivaErro("Erro em ApoiosMongoADO.getControleSm(_id)", e);
    	}
    	return doc;
    }
    
    private  Document getControleHasByPacienteId(ObjectId paciente_id) {
    	Document doc = new Document();
    	MongoClientURI connectionString = new MongoClientURI(Login.getURL());
    	try (MongoClient mongoClient = new MongoClient(connectionString)){
    		MongoDatabase mongoDB = mongoClient.getDatabase(Login.bd);
    		MongoCollection<Document> collection = mongoDB.getCollection("HAS");
    		doc = collection.find(eq("paciente_id", paciente_id)).first();
        	if (doc == null) {
        		doc = new Document("paciente_id", paciente_id);
        		doc.append("DataInscricao", DateUtils.asDate(LocalDate.now()));
        		collection.insertOne(doc);
        	}
    	} catch (Exception e){
    		arquivaErro("Erro em ApoiosMongoADO.getControleHasByPacienteId(_id)", e);
    	}
    	return doc;
    }
    
    private  Document getControleDmByPacienteId(ObjectId paciente_id) {
    	Document doc = new Document();
    	MongoClientURI connectionString = new MongoClientURI(Login.getURL());
    	try (MongoClient mongoClient = new MongoClient(connectionString)){
    		MongoDatabase mongoDB = mongoClient.getDatabase(Login.bd);
    		MongoCollection<Document> collection = mongoDB.getCollection("DM");
    		doc = collection.find(eq("paciente_id", paciente_id)).first();
        	if (doc == null) {
        		doc = new Document("paciente_id", paciente_id);
        		doc.append("DataInscricao", DateUtils.asDate(LocalDate.now()));
        		collection.insertOne(doc);
        	}
    	} catch (Exception e){
    		e.printStackTrace();
    		arquivaErro("Erro em ApoiosMongoADO.getControleDmByPacienteId(_id)", e);
    	}
    	return doc;
    }

    private  Document getControleGestByPacienteId(ObjectId paciente_id) {
    	Document doc = new Document();
    	MongoClientURI connectionString = new MongoClientURI(Login.getURL());
    	try (MongoClient mongoClient = new MongoClient(connectionString)){
    		MongoDatabase mongoDB = mongoClient.getDatabase(Login.bd);
    		MongoCollection<Document> collection = mongoDB.getCollection("ControleGest");
    		doc = collection.find(eq("paciente_id", paciente_id)).first();
        	if (doc == null) {
        		doc = new Document("paciente_id", paciente_id);
        		doc.append("DataInscricao", DateUtils.asDate(LocalDate.now()));
        		collection.insertOne(doc);
        	}
    	} catch (Exception e){
    		e.printStackTrace();
    		arquivaErro("Erro em ApoiosMongoADO.getControleGestByPacienteId(_id)", e);
    	}
    	return doc;
    }

    private  Document getControleGestByGestacaoId(ObjectId gestacaoId) {
    	Document doc = new Document();
    	MongoClientURI connectionString = new MongoClientURI(Login.getURL());
    	try (MongoClient mongoClient = new MongoClient(connectionString)){
    		MongoDatabase mongoDB = mongoClient.getDatabase(Login.bd);
    		MongoCollection<Document> collection = mongoDB.getCollection("ControleGest");
    		doc = collection.find(eq("gestacaoAtualId", gestacaoId)).first();
        	if (doc == null) {
        		return null;
        	}
    	} catch (Exception e){
    		e.printStackTrace();
    		arquivaErro("Erro em ApoiosMongoADO.getControleGestByGestacaoId(gestacaoId)", e);
    	}
    	return doc;
    }

    private  Document getControleSmByPacienteId(ObjectId paciente_id) {
    	Document doc = new Document();
    	MongoClientURI connectionString = new MongoClientURI(Login.getURL());
    	try (MongoClient mongoClient = new MongoClient(connectionString)){
    		MongoDatabase mongoDB = mongoClient.getDatabase(Login.bd);
    		MongoCollection<Document> collection = mongoDB.getCollection("SaudeDaMulher");
    		doc = collection.find(eq("paciente_id", paciente_id)).first();
        	if (doc == null) {
        		doc = new Document("paciente_id", paciente_id);
        		doc.append("DataInscricao", DateUtils.asDate(LocalDate.now()));
        		collection.insertOne(doc);
        	}
    	} catch (Exception e){
    		e.printStackTrace();
    		arquivaErro("Erro em ApoiosMongoADO.getControleSmByPacienteId(_id)", e);
    	}
    	return doc;
    }
    
    private  String getVersion() {
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
    
    private  Object getDadosUsuario(ObjectOutputStream oos, ObjectInputStream ois) {
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
    
    private List<Document> getListaFuncionarios(){
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
    private ObjectId addFuncionario(Document funcionario) {
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
    private Boolean setFuncionario(Document funcionario) {
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
    
    private List<Document> getListaAlias(){
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
    
    private List<Document> getListaFuncoes(){
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
    
    private List<Document> getListaEspecializacoes(){
    	List<Document> lista = new ArrayList<>();
    	MongoClientURI connectionString = new MongoClientURI(Login.getURL());
    	try (MongoClient mongoClient = new MongoClient(connectionString)){
    		MongoDatabase mongoDB = mongoClient.getDatabase(Login.bd);
    		MongoCollection<Document> collection = mongoDB.getCollection("Funcionarios");
    		
    		DistinctIterable<String> distinctEspecializacoes = collection.distinct("especializacao", String.class);
    		for (String especializacao : distinctEspecializacoes) {
    			if (especializacao != null && !especializacao.equals("")) {
    				lista.add(new Document("especializacao", especializacao));
    			}
    		}
    	} catch (Exception e){
    		arquivaErro("Erro em ApoiosMongoADO.getListaEspecializacoes()", e);
    	}
		return lista;
    }
    
    private List<String> getListaFFs(){
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
    
    private List<String> getListaRuas(){
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
    
    private List<Document> getListaMedicos(){
    	List<Document> lista = new ArrayList<>();
    	MongoClientURI connectionString = new MongoClientURI(Login.getURL());
    	try (MongoClient mongoClient = new MongoClient(connectionString)){
    		MongoDatabase mongoDB = mongoClient.getDatabase(Login.bd);
    		//MongoCollection<Document> collectionPessoas = mongoDB.getCollection("Pessoas");
    		MongoCollection<Document> collectionFuncionarios = mongoDB.getCollection("Funcionarios");
    		MongoCursor<Document> cursor = collectionFuncionarios.find(eq("funcao", "Médico(a)")).iterator();
    		cursor.forEachRemaining(funcionario -> {
    			lista.add(funcionario);
    		});
    	} catch (Exception e){
    		arquivaErro("Erro em ApoiosMongoADO.getListaMedicos()", e);
    	}
		return lista;
    }
    
    private List<Document> getListaReceitas(ObjectId _id){
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
    
    private boolean delReceita(ObjectId _id) {
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
    
    private ObjectId addReceita(Document receita) {
    	MongoClientURI connectionString = new MongoClientURI(Login.getURL());
    	try (MongoClient mongoClient = new MongoClient(connectionString)){
    		MongoDatabase mongoDB = mongoClient.getDatabase(Login.bd);
    		MongoCollection<Document> collection = mongoDB.getCollection("Receitas");
    		collection.insertOne(receita);
    		return receita.getObjectId("_id");
    	} catch (Exception e){
    		arquivaErro("Erro em ApoiosMongoADO.addReceita(receita)", e);
    	}
    	return null;
    }
    
    private Boolean setReceita(Document receita) {
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
    
    private Long encripted(String nome, String pwd){
        Long encrip = 0L;
        char[] charNome = nome.toCharArray();
        char[] charPwd = pwd.toCharArray();
        for (int i = 0; i < charNome.length; i++){
            int val = (int) charNome[i];
            encrip = encrip + val;
        }
        encrip = encrip * 2;
        for (int i = 0; i < charPwd.length; i++){
            int val = (int) charPwd[i];
            encrip = encrip + val + (i*2);
        }
        return encrip;
    }
}
