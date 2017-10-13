package com.sample.app;

import java.util.HashMap;

import org.kie.api.KieBase;
import org.kie.api.runtime.KieSession;
import org.kie.internal.io.ResourceFactory;
import org.kie.internal.utils.KieHelper;

import com.hw.process.montage.ApplicantStatusFromMontage;

public class Test1 {

	private static String bpmnFile = "montage/update.applicant.status";
	private static String processId = "update.applicant.status";
	private static String data1="{\"applicant\":{\"uniqueJobid\":\"JOB-001\",\"emailAddress\":\"test01@zmail.com\",\"status\":\"interviewed\"},\"common\":{\"clientID\":\"1234567890abcd\"}}";
	private static String data="{\r\n" + 
			"   \"InterviewId\": \"1\",                                 \r\n" + 
			"   \"InterviewType\": \"Assessment\",                         \r\n" + 
			"   \"StartedDate\":\"2015-07-16T20:15:49Z\",\r\n" + 
			"   \"SubmittedDate\":\"2015-07-17T07:42:16Z\",                                            \r\n" + 
			"   \"ReviewUrl\": \"test\",\r\n" + 
			"   \"ExternalIdentifier\": \"3eccbe21-5b06-4408-b365-eb6d915bb7cb\",                           \r\n" + 
			"   \"Status\": \"Invited\",                                             \r\n" + 
			"   \"StatusDate\": \"2015-07-17T07:42:30Z\"\r\n" + 
			"}";

	public static void main(String[] args) {

		KieHelper kieHelper = new KieHelper();
		KieBase kieBase = kieHelper.addResource(ResourceFactory.newClassPathResource(bpmnFile + ".bpmn")).build();
		KieSession ksession = kieBase.newKieSession();

		String pid = kieBase.getProcesses().stream().findFirst().get().getId();
		System.out.println("Invoking : " + pid);

		HashMap<String, Object> params = new HashMap<String, Object>();
		params.put("method", data);
		params.put("json", data);


		ksession.getWorkItemManager().registerWorkItemHandler("WorkItemHandler", new ApplicantStatusFromMontage());

		ksession.startProcess(pid, params);
		ksession.dispose();

	}
}