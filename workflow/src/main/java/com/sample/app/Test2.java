package com.sample.app;

import java.util.AbstractMap;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.apache.http.Header;
import org.apache.http.message.BasicHeader;
import org.jbpm.process.workitem.rest.RESTWorkItemHandler;
import org.kie.api.KieBase;
import org.kie.api.io.ResourceType;
import org.kie.api.runtime.KieSession;
import org.kie.api.runtime.process.ProcessInstance;
import org.kie.internal.io.ResourceFactory;
import org.kie.internal.utils.KieHelper;

import com.google.common.cache.Cache;
import com.hw.process.montage.ApplicantStatusFromMontage;
import com.hw.process.utils.LookupData;
import com.thoughtworks.xstream.XStream;
import com.thoughtworks.xstream.converters.Converter;
import com.thoughtworks.xstream.converters.MarshallingContext;
import com.thoughtworks.xstream.converters.UnmarshallingContext;
import com.thoughtworks.xstream.io.HierarchicalStreamReader;
import com.thoughtworks.xstream.io.HierarchicalStreamWriter;

public class Test2 {

	// public void test(String[] args) {
	// KnowledgeBuilder kbuilder = KnowledgeBuilderFactory.newKnowledgeBuilder();
	// kbuilder.add(ResourceFactory.newClassPathResource("demo.bpmn"),
	// ResourceType.BPMN2);
	// KnowledgeBase kbase = kbuilder.newKieBase();
	// StatefulKnowledgeSession ksession = kbase.newStatefulKnowledgeSession();
	//
	// HashMap<String, Object> params = new HashMap<String, Object>();
	// params.put("name", "Francesco");
	//
	// ksession.startProcess("greeting.process", params);
	// ksession.dispose();
	// }

	public static void main(String[] args) {
//		Header[] headers = { new BasicHeader("Content-type", "application/xml") };
//		// String tokenResponse = requestToken(PropertyUtils.get("arya_auth_url"),
//		// headers );
//		Map<String, Object> payload = new HashMap<String, Object>();
//		payload.put("Username", "peoplescoutintegration@montageinterview.biz");
//		payload.put("Password", "hatchW0rks@atl");
//		payload.put("Hostname", "peoplescoutuat.montageinterview.biz");
//		payload.put("xmlns", "http://schemas.montagetalent.com/V2/authentication");
//		XStream magicApi = new XStream();
//		magicApi.processAnnotations(Map.class);
//		magicApi.registerConverter(new MapEntryConverter());
//		magicApi.alias("Authentication", Map.class);
//
//		String xml = magicApi.toXML(payload);
//		System.out.println("Result of tweaked XStream toXml()");
//		System.out.println(xml);
//		
//		LookupData.i().getApplicantStatus();
		new Test2().test();
		
	}

	public void test() {
		KieHelper kieHelper = new KieHelper();
		KieBase kieBase = kieHelper
				.addResource(ResourceFactory.newClassPathResource("New Process.bpmn2"), ResourceType.BPMN2).build();
		KieSession ksession = kieBase.newKieSession();
		HashMap<String, Object> params = new HashMap<String, Object>();
		params.put("json",
				"{'applicant':{'uniqueJobid':'JOB-001','emailAddress':'test01@zmail.com','status':'interviewed'},'common':{'clientID':'1234567890abcd'}}");
		ksession.getWorkItemManager().registerWorkItemHandler("Rest", new RESTWorkItemHandler());

		ProcessInstance processInstance = ksession.startProcess("defaultPackage.New_Process", params);
		ksession.dispose();

	}

	protected static class MapEntryConverter implements Converter {

		public boolean canConvert(Class clazz) {
			return AbstractMap.class.isAssignableFrom(clazz);
		}

		public void marshal(Object value, HierarchicalStreamWriter writer, MarshallingContext context) {

			AbstractMap map = (AbstractMap) value;
			if (map.containsKey("xmlns")) {
				writer.addAttribute("xmlns", (String) map.get("xmlns"));
			}
			for (Object obj : map.entrySet()) {

				Map.Entry entry = (Map.Entry) obj;
				Object val = entry.getValue();
				if (!((Map.Entry) obj).getKey().equals("xmlns")) {
					if (!entry.getKey().toString().equals("")) {
						writer.startNode(entry.getKey().toString());
					}
					if (val != null) {
						if (val.getClass() == HashMap.class) {
							marshal(val, writer, context);

						} else if (val.getClass() == ArrayList.class) {
							for (String s : (List<String>) val) {
								writer.startNode("string");
								writer.setValue(s);
								writer.endNode();
							}
						} else {
							writer.setValue(val.toString());
						}
					}
					if (!entry.getKey().toString().equals("")) {
						writer.endNode();
					}
				}
			}

		}

		public Object unmarshal(HierarchicalStreamReader reader, UnmarshallingContext context) {

			Map<String, String> map = new HashMap<String, String>();

			while (reader.hasMoreChildren()) {
				reader.moveDown();

				String key = reader.getNodeName(); // nodeName aka element's name
				String value = reader.getValue();
				map.put(key, value);

				reader.moveUp();
			}

			return map;
		}

	}

}