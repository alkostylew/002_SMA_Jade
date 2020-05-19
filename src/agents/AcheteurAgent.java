package agents;

import containers.AcheteurContainer;
import jade.core.AID;
import jade.core.behaviours.CyclicBehaviour;
import jade.core.behaviours.ParallelBehaviour;
import jade.core.behaviours.TickerBehaviour;
import jade.domain.DFService;
import jade.domain.FIPAException;
import jade.domain.FIPAAgentManagement.DFAgentDescription;
import jade.domain.FIPAAgentManagement.ServiceDescription;
import jade.gui.GuiAgent;
import jade.gui.GuiEvent;
import jade.lang.acl.ACLMessage;
import jade.lang.acl.MessageTemplate;

public class AcheteurAgent extends GuiAgent {

	protected AcheteurContainer gui; 
	protected AID[] vendeurs;
	
	@Override
	protected void setup() {
		if (getArguments().length==1) {
			gui = (AcheteurContainer) getArguments()[0];
			gui.setAcheteurAgent(this);
		}
		
		ParallelBehaviour parallelBehaviour = new ParallelBehaviour();
		addBehaviour(parallelBehaviour);
		
		parallelBehaviour.addSubBehaviour(new TickerBehaviour(this, 5000) {
			@Override
			protected void onTick() {
				DFAgentDescription dfAgentDescription = new DFAgentDescription();
				ServiceDescription serviceDescription = new ServiceDescription();
				serviceDescription.setType("transaction");
				serviceDescription.setName("vente-livres");
				dfAgentDescription.addServices(serviceDescription);
				try {
					DFAgentDescription[] results = DFService.search(myAgent, dfAgentDescription);
					vendeurs = new AID[results.length];
					for (int i = 0; i < vendeurs.length; i++) {
						vendeurs[i]=results[i].getName();
					}
					
				} catch (FIPAException e) {
					e.printStackTrace();
				}
			}
			
		});
		
		parallelBehaviour.addSubBehaviour(new CyclicBehaviour() {
			@Override
			public void action() {
				MessageTemplate messageTemplate= 
						MessageTemplate.or(
							MessageTemplate.MatchPerformative(ACLMessage.REQUEST), 
							MessageTemplate.or(
									MessageTemplate.MatchPerformative(ACLMessage.PROPOSE), 
									MessageTemplate.or(
											MessageTemplate.MatchPerformative(ACLMessage.AGREE),
											MessageTemplate.MatchPerformative(ACLMessage.REFUSE)))); 
				ACLMessage aclMessage = receive(messageTemplate);
				if (aclMessage != null) {
					switch (aclMessage.getPerformative()) {
					case ACLMessage.REQUEST:
						String livre = aclMessage.getContent();
						ACLMessage aclMessage2 = new ACLMessage(ACLMessage.CFP);
						aclMessage2.setContent(livre);
						for (AID aid: vendeurs) {
							aclMessage2.addReceiver(aid);
						}
						send(aclMessage2);
						break;
					case ACLMessage.PROPOSE:
						
						break;
					case ACLMessage.AGREE:
						
						break;
					case ACLMessage.REFUSE:
						
						break;

					default:
						break;
					}
					String livre = aclMessage.getContent();
					gui.logMessage(aclMessage);
					ACLMessage reply = aclMessage.createReply();
					reply.setContent("OK pour "+livre);
					send(reply);
					ACLMessage aclMessage3 = new ACLMessage(ACLMessage.CFP);
					aclMessage3.setContent(livre);
					aclMessage3.addReceiver(new AID("Vendeur", AID.ISLOCALNAME));
					send(aclMessage3);
				} else {
					block();
				}
			}
		});
	}
	
	@Override
	protected void onGuiEvent(GuiEvent arg0) {
		// TODO Auto-generated method stub
		
	}

}
