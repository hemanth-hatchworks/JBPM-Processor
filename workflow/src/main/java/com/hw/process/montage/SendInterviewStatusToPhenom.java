package com.hw.process.montage;

import java.io.IOException;
import java.sql.CallableStatement;
import java.sql.Connection;
import java.sql.ResultSet;
import java.sql.ResultSetMetaData;
import java.util.HashMap;
import java.util.Map;

import org.jbpm.process.workitem.AbstractLogOrThrowWorkItemHandler;
import org.kie.api.runtime.KieSession;
import org.kie.api.runtime.process.WorkItem;
import org.kie.api.runtime.process.WorkItemManager;

import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.hw.process.utils.Base;
import com.hw.process.utils.DBUtility;
import com.hw.process.utils.PropertyUtils;

public class SendInterviewStatusToPhenom extends AbstractLogOrThrowWorkItemHandler implements Base {

	KieSession session;
	private final static int vendorid = 3; //montage

	public SendInterviewStatusToPhenom() {
		log.info("Init Service Handler");

	}

	public SendInterviewStatusToPhenom(KieSession ksession) {
		log.info("Init Service Handler w/Session");
		session = ksession;
	}

	@Override
	public void executeWorkItem(WorkItem workItem, WorkItemManager manager) {
		ObjectMapper mapper = new ObjectMapper();
		final String key = "json";
		final String data = (String)workItem.getParameter(key);
		JsonNode tree = null;

		if (data !=null) {

			try {


				tree = mapper.readTree((String) workItem.getParameter(key));
//				System.out.println(tree.path("applicant").path("uniqueJobid").asText());
//				System.out.println(tree.path("applicant").path("emailAddress").asText());
//				System.out.println(tree.path("common").path("clientID").asText());

			} catch (IOException e1) {
				log.error(e1.getMessage());
			}

			try (Connection conn = DBUtility.getDataSource().getConnection();) {
				
				
				//get applicant id for given input
				try (CallableStatement cs = conn.prepareCall(PropertyUtils.get("get_applicant_id"));){
					cs.setInt(1, vendorid); //Montage ID
					cs.setString(2, "75558be3-21bd-4cda-94ac-b6343b1b356c");
					
					if (cs.execute()) {
						
					}
					
					
				}
				
				//Update status
				try (CallableStatement cs = conn.prepareCall(PropertyUtils.get("montage_update_status"));) {

					cs.setInt(1, 1);
					cs.setObject(2, tree.path("applicant").path("uniqueJobid").asText());
					cs.setObject(3, tree.path("applicant").path("emailAddress").asText());
					cs.setObject(4, tree.path("common").path("clientID").asText());

					if (cs.execute()) {

						ResultSet rs = cs.getResultSet();
						ResultSetMetaData rsmd = rs.getMetaData();
						int columnsNumber = rsmd.getColumnCount();
						while (rs.next()) {
							for (int i = 1; i <= columnsNumber; i++) {
								if (i > 1)
									log.debug(",  ");
								String columnValue = rs.getString(i);
								log.debug(columnValue + "=" + rsmd.getColumnName(i));
							}
						}
					}

				} catch (Exception e) {

					log.error(e.getMessage());

				}

			} catch (Exception e) {
				log.error(e.getMessage());
				abortWorkItem(workItem, manager);
			}

		}
		manager.completeWorkItem(workItem.getId(), null);

	}

	@Override
	public void abortWorkItem(WorkItem workItem, WorkItemManager manager) {
		manager.abortWorkItem(workItem.getId());
	}

}
