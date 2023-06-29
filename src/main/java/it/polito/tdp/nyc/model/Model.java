package it.polito.tdp.nyc.model;

import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

import org.jgrapht.Graphs;
import org.jgrapht.graph.DefaultWeightedEdge;
import org.jgrapht.graph.SimpleWeightedGraph;

import it.polito.tdp.nyc.db.NYCDao;

public class Model {
	
	private NYCDao dao ;
	
	private List<NTA> allNTA;
	
	//creare un grafo del soborgo selezionato con 
	//VERTICI = NTS
	//ARCHI = UNIONE ELEMENTI DEGLI SSID dei due NTA
	private SimpleWeightedGraph<NTA, DefaultWeightedEdge> graph;
	
	
	public Model() {
		dao = new NYCDao();
		allNTA = new ArrayList<>();
		graph = new SimpleWeightedGraph<>(DefaultWeightedEdge.class);
	}
	
	//metodo per creare lista di borough
	public List<String> getBorough() {
		
		List<String> lista = dao.getBorough();
		
		return lista;
	}
	
	//grafo --> vertici
	
	public void loadNodes(String b) {
		
		if (allNTA.isEmpty()) {
			allNTA = dao.getNTA(b);
		}
	}
	
	//pulizia grafo
	public void clearGraph() {
		allNTA = new ArrayList<>();
		graph = new SimpleWeightedGraph<>(DefaultWeightedEdge.class);
	}
	
	//creazione grafo
	
	public void buildGraph(String b) {
		
		clearGraph();
		loadNodes(b);
		
		//aggiungere i vertici al grafo
		Graphs.addAllVertices(this.graph, allNTA);
		
		//aggiungere gli archi e peso  --> peso == a count di SSDI distinti
		
		for (NTA n1 : allNTA) {
			for (NTA n2 : allNTA) {
				
				//devono essere diversi
				if (!n1.equals(n2)) {
					
					//creare un set unione = ssda1 + ssdi2 DISTINTI !!!
					Set<String> unione = new HashSet<>(n1.getSSDI());
					unione.addAll(n2.getSSDI());
					                                                //PESO dell'arco = #elenti presenti nel set
					Graphs.addEdgeWithVertices(this.graph, n1, n2, unione.size());
				}
			}
		}
	}

	public Integer getNVetici() {
		// TODO Auto-generated method stub
		return this.graph.vertexSet().size();
	}

	public Integer getNArchi() {
		// TODO Auto-generated method stub
		return this.graph.edgeSet().size();
	}
	
	
	//PUNTO 1 D : peso sia maggiore del peso medio 
	
	//metodo per calcolare il peso medio
	
	public double getPesoMedio() {
		
		double pesoMedio = 0.0;
		
		for (DefaultWeightedEdge e : this.graph.edgeSet()) {
			pesoMedio += this.graph.getEdgeWeight(e);
		}
		
		pesoMedio = pesoMedio/this.graph.edgeSet().size();
		
		return pesoMedio;
	}
	
	//METODO ANALISI ARCHI
	//input = niente
	//output == Lista di archi che rispettano condizione in ordine DESCRESCENTE di PESO
	
	public List<Archi> getAnalisiArchi() {
		
		List<Archi> archi = new ArrayList<Archi>();  //--> fare una classe a parte per gli archi e' necessario perche' si vuole avere una lista ordinata
 													// necessaro salvare l'informazione del peso da qualche parte
		
		for (DefaultWeightedEdge e : this.graph.edgeSet()) {
			double pesoArco = this.graph.getEdgeWeight(e);
			if (pesoArco > getPesoMedio()) {
				NTA n1 = this.graph.getEdgeSource(e);
				NTA n2 = this.graph.getEdgeTarget(e);
				archi.add(new Archi(n1, n2, pesoArco));
			}
		}
		Collections.sort(archi);
		
		return archi;
	}
	
	
	//PUNTO 2 : SIMULAZIONE
	
	public Map<NTA, Integer> simulazione(int durationShare, double probShare) {
		
		Simulator simulazione = new Simulator(graph, probShare, durationShare);
		
		simulazione.initiate();
		simulazione.run();
		
		return simulazione.getNumTotShare();
		
	}
	
}
