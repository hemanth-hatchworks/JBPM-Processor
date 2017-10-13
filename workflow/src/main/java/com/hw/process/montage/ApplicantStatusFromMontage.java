package com.hw.process.montage;

import java.io.IOException;
import java.sql.CallableStatement;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.SQLException;
import java.sql.Types;
import java.text.SimpleDateFormat;
import java.time.Instant;
import java.time.format.DateTimeFormatter;
import java.util.Date;
import java.util.UUID;

import org.kie.api.runtime.KieSession;
import org.kie.api.runtime.process.WorkItem;
import org.kie.api.runtime.process.WorkItemManager;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.google.gson.JsonElement;
import com.hw.process.handlers.BaseHandler;
import com.hw.process.utils.DBUtility;
import com.hw.process.utils.JavaUtils;
import com.hw.process.utils.JsonUtils;
import com.hw.process.utils.LookupData;
import com.hw.process.utils.PropertyUtils;

/*
 * Process Entry for handling push from Montage on status update on applicant interview details
 */
public class ApplicantStatusFromMontage extends BaseHandler {

	private final static int vendorid = 3; // montage

	public ApplicantStatusFromMontage() {
		super();
	}

	public ApplicantStatusFromMontage(KieSession ksession) {
		super(ksession);
	}

	@Override
	public void executeWorkItem(WorkItem workItem, WorkItemManager manager) {
		ObjectMapper mapper = new ObjectMapper();
		final String key = "json";
		final String data = (String) workItem.getParameter(key);
		JsonNode tree = null;

		if (data != null) {

			try {
				// read json as tree of nodes
				tree = mapper.readTree((String) workItem.getParameter(key));

			} catch (IOException e1) {
				log.error(e1.getMessage());
			}
			// get applicant id for given input

			try (Connection conn = DBUtility.getDataSource().getConnection();) {
				int applicantId = -1;
				try (CallableStatement cs = conn
						.prepareCall(PropertyUtils.get("fn_applicant_get_applicantid_by_interviewuniqueid"));) {

					cs.setObject(1, tree.path("ExternalIdentifier").asText(), Types.OTHER);

					if (cs.execute()) {
						JsonElement elt = JsonUtils.convertToJSON(cs.getResultSet()).get(0);
						applicantId = elt.getAsJsonObject().get("r_applicantid").getAsInt();
					}

					rest(workItem, manager, tree.path("ExternalIdentifier").asText());

				}
				// Update/Insert the interview details
				if (applicantId < 0) {
					// Update status
					try (CallableStatement cs = conn
							.prepareCall(PropertyUtils.get("fn_applicant_interview_info_insertupdate"));) {

						cs.setInt(1, vendorid); // Vendor ID
						cs.setInt(2, applicantId); // Applicant ID
						cs.setObject(3, UUID.fromString(tree.path("ExternalIdentifier").asText()), Types.OTHER); // Interview
																													// UNIQUEID
						cs.setString(4, tree.path("ReviewUrl").asText()); // InterviewURL
						cs.setString(5, "http://" + Instant.now()); // Interview Video URL
						cs.setString(6, null); // Assessment Results
						cs.setObject(7, null, Types.LONGVARCHAR); // Notes
						cs.setObject(8, LookupData.i().getInterviewType().get(tree.path("InterviewType").asText()),
								Types.SMALLINT); // InterviewType
						cs.setString(11, tree.path("InterviewId").asText()); // External interview ID
						cs.setObject(12, LookupData.i().getInterviewStatus().get(tree.path("Status").asText()),
								Types.SMALLINT);

						long d = Date.from(
								DateTimeFormatter.ISO_INSTANT.parse(tree.path("StartedDate").asText(), Instant::from))
								.getTime();
						cs.setDate(9, new java.sql.Date(d));
						d = Date.from(
								DateTimeFormatter.ISO_INSTANT.parse(tree.path("SubmittedDate").asText(), Instant::from))
								.getTime();

						cs.setDate(10, new java.sql.Date(d));
						SimpleDateFormat sf = new SimpleDateFormat("yyyy-mm-dd hh:mm:ss");
						d = Date.from(
								DateTimeFormatter.ISO_INSTANT.parse(tree.path("StatusDate").asText(), Instant::from))
								.getTime();
						cs.setDate(13, new java.sql.Date(d));
						// cs.setObject(13,
						// DateTimeFormatter.ISO_INSTANT.parse(tree.path("StatusDate").asText(),Instant::from));

						if (cs.execute()) {

							JavaUtils.printResultSet(cs.getResultSet());
						}

					} catch (Exception e) {

						log.error(e.getMessage());
						e.printStackTrace();

					}
				}

			} catch (Exception e) {
				log.error(e.getMessage());
				e.printStackTrace();
				abortWorkItem(workItem, manager);
			}

		}

		manager.completeWorkItem(workItem.getId(), null);

	}

	public void rest(WorkItem workItem, WorkItemManager manager, String interviewid) {
		System.out.println(interviewid);
		try (Connection conn = DBUtility.getDataSource().getConnection();) {

			try (CallableStatement cs = conn
					.prepareCall(PropertyUtils.get("fn_applicant_getapplicantstatus_baseoninterviewuniqueid"))) {

				cs.setInt(1, 1);
				cs.setObject(2, UUID.fromString(interviewid), Types.OTHER);
				
				if (cs.execute()) {
					for (JsonElement element : JsonUtils.convertToJSON(cs.getResultSet())) {
						System.out.println(element);
					}
				}

			}

		} catch (SQLException e) {
			e.printStackTrace();
		}

	}

	@Override
	public void abortWorkItem(WorkItem workItem, WorkItemManager manager) {
		manager.abortWorkItem(workItem.getId());
	}

}
