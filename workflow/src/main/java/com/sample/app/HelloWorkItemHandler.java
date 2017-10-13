package com.sample.app;

import javax.enterprise.context.ApplicationScoped;

import org.jbpm.process.workitem.AbstractLogOrThrowWorkItemHandler;
import org.kie.api.cdi.KSession;
import org.kie.api.runtime.KieSession;
import org.kie.api.runtime.process.WorkItem;
import org.kie.api.runtime.process.WorkItemManager;

@ApplicationScoped
public class HelloWorkItemHandler extends AbstractLogOrThrowWorkItemHandler {

	KieSession ksession = null;;

	public HelloWorkItemHandler() {
		super();
	}

	public HelloWorkItemHandler(KieSession s) {
		System.out.println(s);
		ksession = s;
	}

	public void executeWorkItem(String s) {

		System.out.println(ksession);
	}

	public void abortWorkItem(WorkItem wi, WorkItemManager wim) {
		System.out.println("Oh no, my item aborted..");

	}

	public void executeWorkItem(WorkItem wi, WorkItemManager wim) {
		System.out.println("Hello World!");
		wim.completeWorkItem(wi.getId(), null);
	}

}