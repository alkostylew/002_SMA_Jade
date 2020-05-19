package containers;

import agents.VendeurAgent;
import jade.core.ProfileImpl;
import jade.core.Runtime;
import jade.lang.acl.ACLMessage;
import jade.wrapper.AgentContainer;
import jade.wrapper.AgentController;
import jade.wrapper.StaleProxyException;
import javafx.application.Application;
import javafx.application.Platform;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.geometry.Insets;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.ListView;
import javafx.scene.control.TextField;
import javafx.scene.layout.BorderPane;
import javafx.scene.layout.HBox;
import javafx.scene.layout.VBox;
import javafx.stage.Stage;

public class VendeurContainer extends Application {
	
	protected VendeurAgent vendeurAgent;
	private AgentContainer agentContainer;
	protected ObservableList<String> observableList;
	
	public static void main(String[] args) {
		launch(args);
	}
	
	@Override
	public void start(Stage primaryStage) throws Exception {
		startContainder();
	
		primaryStage.setTitle("Vendeur");
		HBox hBox = new HBox(); hBox.setPadding(new Insets(10));
		hBox.setSpacing(10);
		Label label = new Label("Agent name:");		
		TextField textFieldAgentName = new TextField();
		Button buttonDeploy = new Button("Deploy");
		hBox.getChildren().addAll(label, textFieldAgentName, buttonDeploy);
				
		BorderPane borderPane = new BorderPane();
		VBox vBox = new VBox();
		observableList = FXCollections.observableArrayList();
		ListView<String> listView = new ListView<String>(observableList);
		vBox.getChildren().add(listView);
		borderPane.setTop(hBox);
		borderPane.setCenter(vBox);
		Scene scene = new Scene(borderPane, 400, 300);
		primaryStage.setScene(scene);
		primaryStage.show();
		
		buttonDeploy.setOnAction((evt) -> {
			try {
				String name = textFieldAgentName.getText();
				AgentController agentController = agentContainer.createNewAgent(name, 
						"agents.VendeurAgent", new Object[] {this});
				agentController.start();
			} catch (StaleProxyException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
		});
	}

	private void startContainder() throws Exception {
		Runtime runtime = Runtime.instance();
		ProfileImpl profileImpl = new ProfileImpl();
		profileImpl.setParameter(ProfileImpl.MAIN_HOST, "localhost");
		agentContainer = runtime.createAgentContainer(profileImpl);
		agentContainer.start();
	}

	public void setVendeurAgent(VendeurAgent vendeurAgent) {
		this.vendeurAgent = vendeurAgent;
	}
	
	public void logMessage(ACLMessage aclMessage) {
		Platform.runLater(() -> {
			observableList.add(aclMessage.getContent());
		});
	}
}
