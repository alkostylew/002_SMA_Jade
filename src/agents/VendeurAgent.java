package agents;

import java.util.Random;

import containers.VendeurContainer;
import jade.core.behaviours.CyclicBehaviour;
import jade.core.behaviours.OneShotBehaviour;
import jade.core.behaviours.ParallelBehaviour;
import jade.domain.DFService;
import jade.domain.FIPAException;
import jade.domain.FIPAAgentManagement.DFAgentDescription;
import jade.domain.FIPAAgentManagement.ServiceDescription;
import jade.gui.GuiAgent;
import jade.gui.GuiEvent;
import jade.lang.acl.ACLMessage;

public class VendeurAgent extends GuiAgent {

	protected VendeurContainer gui; 
	
	@Override
	protected void setup() {
		if (getArguments().length==1) {
			gui = (VendeurContainer) getArguments()[0];
			gui.setVendeurAgent(this);
		}
		
		ParallelBehaviour parallelBehaviour = new ParallelBehaviour();
		addBehaviour(parallelBehaviour);
		
		parallelBehaviour.addSubBehaviour(new OneShotBehaviour() {
			@Override
			public void action() {
				DFAgentDescription agentDescription = new DFAgentDescription();
				agentDescription.setName(getAID());
				ServiceDescription serviceDescription = new ServiceDescription();
				serviceDescription.setType("transaction");
				serviceDescription.setName("vente-livres");
				agentDescription.addServices(serviceDescription);
				try {
					DFService.register(myAgent, agentDescription);
				} catch (FIPAException e) {
					e.printStackTrace();
				}
			}
		});
		
		parallelBehaviour.addSubBehaviour(new CyclicBehaviour() {
			@Override
			public void action() {
				ACLMessage aclMessage = receive();
				if (aclMessage != null) {
					gui.logMessage(aclMessage);
					switch (aclMessage.getPerformative()) {
					case ACLMessage.CFP:
						ACLMessage reply = aclMessage.createReply();
						reply.setContent(String.valueOf(500 + new Random().nextInt(1000)));
						reply.setPerformative(ACLMessage.PROPOSE);
						send(reply);
						break;
					case ACLMessage.ACCEPT_PROPOSAL:
						ACLMessage aclMessage2 = aclMessage.createReply();
						aclMessage2.setPerformative(ACLMessage.AGREE);
						send(aclMessage2);
						break;
					default:
						break;
					}
				} else {
					block();
				}
			}
		});
	}
	
	@Override
	protected void onGuiEvent(GuiEvent event) {
	}

	@Override
	protected void takeDown() {
		try {
			DFService.deregister(this);
		} catch (FIPAException e) {
			e.printStackTrace();
		}
	}
}
