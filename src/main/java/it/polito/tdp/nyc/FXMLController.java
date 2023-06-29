package it.polito.tdp.nyc;

import java.net.URL;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.ResourceBundle;

import it.polito.tdp.nyc.model.Archi;
import it.polito.tdp.nyc.model.Model;
import it.polito.tdp.nyc.model.NTA;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.scene.control.Button;
import javafx.scene.control.ComboBox;
import javafx.scene.control.TableColumn;
import javafx.scene.control.TableView;
import javafx.scene.control.TextArea;
import javafx.scene.control.TextField;

public class FXMLController {

	Model model;
	
    @FXML // ResourceBundle that was given to the FXMLLoader
    private ResourceBundle resources;

    @FXML // URL location of the FXML file that was given to the FXMLLoader
    private URL location;

    @FXML // fx:id="btnAdiacenti"
    private Button btnAdiacenti; // Value injected by FXMLLoader

    @FXML // fx:id="btnCreaGrafo"
    private Button btnCreaGrafo; // Value injected by FXMLLoader

    @FXML // fx:id="btnCreaLista"
    private Button btnCreaLista; // Value injected by FXMLLoader

    @FXML // fx:id="clPeso"
    private TableColumn<Archi, Double> clPeso; // Value injected by FXMLLoader

    @FXML // fx:id="clV1"
    private TableColumn<Archi, String> clV1; // Value injected by FXMLLoader

    @FXML // fx:id="clV2"
    private TableColumn<Archi, String> clV2; // Value injected by FXMLLoader

    @FXML // fx:id="cmbBorough"
    private ComboBox<String> cmbBorough; // Value injected by FXMLLoader

    @FXML // fx:id="tblArchi"
    private TableView<Archi> tblArchi; // Value injected by FXMLLoader

    @FXML // fx:id="txtDurata"
    private TextField txtDurata; // Value injected by FXMLLoader

    @FXML // fx:id="txtProb"
    private TextField txtProb; // Value injected by FXMLLoader

    @FXML // fx:id="txtResult"
    private TextArea txtResult; // Value injected by FXMLLoader
    
    //boolean
    private boolean grafoCreato = false;

    @FXML
    void doAnalisiArchi(ActionEvent event) {
    	
    	model.getAnalisiArchi();
    	txtResult.setText("PESO MEDIO: " + model.getPesoMedio() + "\nARCHI CON PESO MAGGIORE DEL PESO MEDIO: " + model.getAnalisiArchi().size());
    	for (Archi a : model.getAnalisiArchi()) 
    		txtResult.appendText("\n" + a + "\n");

    }

    @FXML
    void doCreaGrafo(ActionEvent event) {
    	
    	String input = cmbBorough.getValue();
    	
    	//controllo
    	if (input == "") {
    		txtResult.setText("Selezionare un valore.");
    	}
    	
    	model.buildGraph(input);
    	txtResult.setText("Grafo creato! \n #Vertici: " + model.getNVetici() + "\n #Archi: "+ model.getNArchi() );
    	grafoCreato = true;
    	
    }

    @FXML
    void doSimula(ActionEvent event) {
    	
    	txtResult.clear();
    	
    	//il grafo deve esistere
    	if (!this.grafoCreato){
    		txtResult.appendText("Errore:  Non e' stato creato un grafo. Riprovare");
    	}
    	
    	String duration = txtDurata.getText();
    	String probShare = txtProb.getText();
    	
    	//controlli input
    	if (duration =="" || probShare =="") {
    		txtResult.appendText("Errore. Non sono stati inseriti i valori. Riprovare");
    	}
    	
    	//conversione
    	double prob = 0.0;
    	int dur = 0;
    	
    	try {
    		
    		prob = Double.parseDouble(probShare);
    		dur = Integer.parseInt(duration);
    		
    	} catch(NumberFormatException e) {
    		txtResult.appendText("Errore: inserire dati numerici\n");
    		return ;
    	}
    	
    	//output
    	Map<NTA, Integer> output = model.simulazione(dur, prob);
    	
    	for (NTA n : output.keySet()) {
    		txtResult.appendText(n.getNTACode() + ": " + output.get(n) + "\n" );
    	}

    }

    @FXML // This method is called by the FXMLLoader when initialization is complete
    void initialize() {
        assert btnAdiacenti != null : "fx:id=\"btnAdiacenti\" was not injected: check your FXML file 'Scene.fxml'.";
        assert btnCreaGrafo != null : "fx:id=\"btnCreaGrafo\" was not injected: check your FXML file 'Scene.fxml'.";
        assert btnCreaLista != null : "fx:id=\"btnCreaLista\" was not injected: check your FXML file 'Scene.fxml'.";
        assert clPeso != null : "fx:id=\"clPeso\" was not injected: check your FXML file 'Scene.fxml'.";
        assert clV1 != null : "fx:id=\"clV1\" was not injected: check your FXML file 'Scene.fxml'.";
        assert clV2 != null : "fx:id=\"clV2\" was not injected: check your FXML file 'Scene.fxml'.";
        assert cmbBorough != null : "fx:id=\"cmbBorough\" was not injected: check your FXML file 'Scene.fxml'.";
        assert tblArchi != null : "fx:id=\"tblArchi\" was not injected: check your FXML file 'Scene.fxml'.";
        assert txtDurata != null : "fx:id=\"txtDurata\" was not injected: check your FXML file 'Scene.fxml'.";
        assert txtProb != null : "fx:id=\"txtProb\" was not injected: check your FXML file 'Scene.fxml'.";
        assert txtResult != null : "fx:id=\"txtResult\" was not injected: check your FXML file 'Scene.fxml'.";

        
    }
    
    public void setModel(Model model) {
    	this.model = model;
    	
    	//mettere valori nella tendina --> PUNTO 1 A
    	List<String> boroughs = model.getBorough();
    	cmbBorough.getItems().setAll(boroughs);
    }

}
