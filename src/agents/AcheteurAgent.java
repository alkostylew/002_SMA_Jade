package agents;

import containers.AcheteurContainer;
import jade.core.behaviours.CyclicBehaviour;
import jade.core.behaviours.ParallelBehaviour;
import jade.gui.GuiAgent;
import jade.gui.GuiEvent;
import jade.lang.acl.ACLMessage;

public class AcheteurAgent extends GuiAgent {

	protected AcheteurContainer gui; 
	
	@Override
	protected void setup() {
		if (getArguments().length==1) {
			gui = (AcheteurContainer) getArguments()[0];
			gui.setAcheteurAgent(this);
		}
		
		ParallelBehaviour parallelBehaviour = new ParallelBehaviour();
		addBehaviour(parallelBehaviour);
		parallelBehaviour.addSubBehaviour(new CyclicBehaviour() {
			@Override
			public void action() {
				ACLMessage aclMessage = receive();
				if (aclMessage != null) {
					gui.logMessage(aclMessage);
					ACLMessage reply = aclMessage.createReply();
					reply.setContent("OK pour "+aclMessage.getContent());
					send(reply);
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
