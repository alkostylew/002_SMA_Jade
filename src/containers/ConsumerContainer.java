package containers;

import agents.ConsumerAgent;
import jade.core.ProfileImpl;
import jade.core.Runtime;
import jade.gui.GuiEvent;
import jade.lang.acl.ACLMessage;
import jade.wrapper.AgentContainer;
import jade.wrapper.AgentController;
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

public class ConsumerContainer extends Application {
	
	protected ConsumerAgent consumerAgent;
	ObservableList<String> observableList;
	
	public static void main(String[] args) throws Exception {
		launch(args);
	}

	private void startContainer() throws Exception {
		Runtime runtime = Runtime.instance();
		ProfileImpl profileImpl = new ProfileImpl();
		profileImpl.setParameter(ProfileImpl.MAIN_HOST, "localhost");
		AgentContainer container = runtime.createAgentContainer(profileImpl);
		AgentController agentController = container
				.createNewAgent("Consumer", "agents.ConsumerAgent", new Object[] {this});
		agentController.start();
	}

	@Override
	public void start(Stage primaryStage) throws Exception {
		startContainer();
		
		primaryStage.setTitle("Cosumateur");
		HBox xBox = new HBox();
		xBox.setPadding(new Insets(10));
		xBox.setSpacing(10);
		Label label = new Label("Livre:");
		TextField textFieldLivre= new TextField();
		Button buttonAcheter= new Button("Acheter");
		xBox.getChildren().addAll(label, textFieldLivre, buttonAcheter);
		
		VBox vBox = new VBox(); vBox.setPadding(new Insets(10));
		observableList = FXCollections.observableArrayList();
		ListView<String> listViewMessage = new ListView<String>(observableList);
		vBox.getChildren().add(listViewMessage);
		
		BorderPane borderPane = new BorderPane();
		borderPane.setTop(xBox);
		borderPane.setCenter(vBox);
		Scene scene = new Scene(borderPane, 600, 400);
		primaryStage.setScene(scene);
		primaryStage.show();
		
		buttonAcheter.setOnAction(evt -> {
			String livre = textFieldLivre.getText();
//			observableList.add(livre);
			GuiEvent event = new GuiEvent(this, 1);
			event.addParameter(livre);
			consumerAgent.onGuiEvent(event);
		});
	}

	public void setConsumerAgent(ConsumerAgent consumerAgent) {
		this.consumerAgent = consumerAgent;
	}
	
	public void logMessage(ACLMessage aclMessage) {
		Platform.runLater(() -> {
			observableList.add(aclMessage.getContent()+", "+aclMessage.getSender().getName());
		});
	}
}
