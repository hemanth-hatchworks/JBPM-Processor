package com.hw.process.handlers;

import org.jbpm.process.workitem.AbstractLogOrThrowWorkItemHandler;
import org.kie.api.runtime.KieSession;
import org.kie.api.runtime.process.WorkItem;
import org.kie.api.runtime.process.WorkItemManager;

import com.hw.process.utils.Base;

// This is a template class used to create new workitem handlers
public class BaseHandler extends AbstractLogOrThrowWorkItemHandler implements Base {
	KieSession session;

	public BaseHandler() {
		log.info(getClass().getName() + "Init Service Handler");

	}

	public BaseHandler(KieSession ksession) {
		log.info(getClass().getName() + " : Init Service Handler w/Session");
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
