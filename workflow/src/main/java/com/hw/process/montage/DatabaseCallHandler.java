package com.hw.process.montage;

import org.kie.api.runtime.KieSession;
import org.kie.api.runtime.process.WorkItem;
import org.kie.api.runtime.process.WorkItemManager;

import com.hw.process.handlers.BaseHandler;

//This class will take @json parameter from process and passes on to a 
//proxy database handler which executes and returns the resultset as json which can be passed to next calle
public class DatabaseCallHandler extends BaseHandler {
	KieSession session;

	public DatabaseCallHandler() {
		log.info(getClass().getName() + "Init Service Handler");

	}

	public DatabaseCallHandler(KieSession ksession) {
		log.info( getClass().getName() + " : Init Service Handler w/Session");
		session = ksession;
	}

	@Override
	public void executeWorkItem(WorkItem workItem, WorkItemManager manager) {

		manager.completeWorkItem(workItem.getId(), null);

	}

	@Override
	public void abortWorkItem(WorkItem workItem, WorkItemManager manager) {
		manager.abortWorkItem(workItem.getId());
	}

}
