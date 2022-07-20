
package com.prodian.rsgirms.scheduler.service;

import java.io.IOException;
import java.net.URISyntaxException;
import java.sql.Timestamp;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.List;
import java.util.TimeZone;

import org.json.JSONException;
import org.json.JSONObject;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Configuration;
import org.springframework.scheduling.annotation.EnableScheduling;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;

import com.prodian.rsgirms.constants.RMSConstants;
import com.prodian.rsgirms.scheduler.model.CubeStatus;
import com.prodian.rsgirms.scheduler.model.Cubes;
import com.prodian.rsgirms.scheduler.model.SchedulerInfo;
import com.prodian.rsgirms.scheduler.repository.CubeRepository;
import com.prodian.rsgirms.scheduler.repository.SchedulerInfoRepository;
import com.prodian.rsgirms.scheduler.repository.SchedulerStatusRepository;
import com.prodian.rsgirms.util.UtilityFile;

import okhttp3.MediaType;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.RequestBody;
import okhttp3.Response;

/**
 * @author CSS
 *
 */
@Configuration
public class KylinJobSchedulerService {

	private static final Logger logger = LoggerFactory.getLogger(KylinJobSchedulerService.class.getName());

	@Autowired
	private CubeRepository cubeRepository;

	@Autowired
	private SchedulerStatusRepository schedulerStatusRepository;

	@Autowired
	private SchedulerInfoRepository schedulerInfoRepository;

	/* @Scheduled(cron = "0 0/2 17 ? * *") */
//	@Scheduled(cron = "0 0/2 8,9 ? * *")
	public void getKylinCubeLists() throws IOException {
		OkHttpClient client = new OkHttpClient();

		Request request = new Request.Builder().url(RMSConstants.KYLIN_BASE_URL + "/kylin/api/cubes").get()
				.addHeader("authorization", "Basic  QURNSU46S1lMSU4=").addHeader("cache-control", "no-cache").build();

		Response response = client.newCall(request).execute();
		System.out.println("CubesListResponse-->" + response.body().string());
	}

	/* @Scheduled(cron = "0 0/2 * ? * *") */
	//@Scheduled(cron = "0 0 10 ? * *")
	public void buildKylinKube() throws IOException, JSONException, URISyntaxException {
		logger.info("::buildKylinKube() :: Kylin cube build method called ->>>>>>>>>>>");
		SchedulerInfo schedulerInfo = insertSchedulerInfo("CUBE_BUILD");
		String responseTxt = "", timeStamp = "", errorMsg = "";
		boolean isAtleatOneCubeBuild = false;
		int processCount = 0;
		try {
			OkHttpClient client = new OkHttpClient();
			List<Cubes> activeCubeList = cubeRepository.findByStatus(RMSConstants.ACTIVE);
			logger.info("::buildKylinKube() :: Active cube list count - " + activeCubeList.size());
			for (Cubes obj : activeCubeList) {
				logger.info("::buildKylinKube() :: Current cube - " + obj.getCubeName());
				if (!isAnyJobRunning(obj.getCubeName(),
						UtilityFile.createSpecifiedDateFormat(RMSConstants.dd_slash_MM_slash_yyyy))) {
					isAtleatOneCubeBuild = true;
					processCount++;
					logger.info("::buildKylinKube() :: Cube - " + obj.getCubeName()
							+ " buidling is in progress or already cube got build");
					CubeStatus schedulerObj = schedulerStatusRepository.findByProcessDateAndCubeName(
							UtilityFile.createSpecifiedDateFormat(RMSConstants.dd_slash_MM_slash_yyyy),
							obj.getCubeName());
					logger.info("::buildKylinKube() :: " + schedulerObj.getCubeName());
					schedulerObj.setRemarks("");
					try {
						int year = getYearFromDate(new Date());
						deleteSegment(obj.getCubeName(), year + "0401000000_" + (year + 1) + "0401000000");
						Date date = new Date();
						timeStamp = new SimpleDateFormat("dd/MM/yyyy hh:mm:ss:SSS").format(date);
						schedulerObj.setStartTime(timeStamp);
						schedulerObj.setIsBuildStarted(RMSConstants.ACTIVE);
						MediaType mediaType = MediaType.parse("application/json;charset=UTF-8");
						RequestBody body = RequestBody.create(mediaType,
								"{\"buildType\": \"BUILD\", \"startTime\": \"" + getFinyearStartDateinMilliSeconds()
										+ "\", \"endTime\": \"" + getFinyearEndDateinMilliSeconds()
										+ "\", \"forceMergeEmptySegment\": false}");
						logger.info("::buildKylinKube() :: request -->" + "{\"buildType\": \"BUILD\", \"startTime\": \""
								+ getFinyearStartDateinMilliSeconds() + "\", \"endTime\": \""
								+ getFinyearEndDateinMilliSeconds() + "\", \"forceMergeEmptySegment\": false}");
						Request request = new Request.Builder()
								.url(RMSConstants.KYLIN_BASE_URL + "//kylin/api/cubes/" + obj.getCubeName() + "/build")
								.put(body).addHeader("content-type", "application/json;charset=UTF-8")
								.addHeader("user-agent",
										"Mozilla/5.0 (Windows NT 10.0; Win64; x64) AppleWebKit/537.36 (KHTML, like Gecko) Chrome/83.0.4103.97 Safari/537.36")
								.addHeader("authorization", RMSConstants.KYLIN_AUTH_KEY)
								.addHeader("accept", "application/json, text/plain, */*")
								.addHeader("accept-language", "en-US,en;q=0.9").addHeader("cache-control", "no-cache")
								.build();

						Response response = client.newCall(request).execute();
						responseTxt = response.body().string();
						logger.info("::buildKylinKube() :: response -->" + responseTxt);
						schedulerObj.setResponse(responseTxt);
						JSONObject jsonObject = new JSONObject(responseTxt);
						if (jsonObject.has("uuid")) {
							schedulerObj.setRefKey(jsonObject.getString("uuid"));
							logger.info("::buildKylinKube() :: Cube Job id - " + jsonObject.getString("uuid"));
						} else {
							if (jsonObject.has("exception")) {
								schedulerObj.setRemarks(jsonObject.getString("exception"));
							}
						}
					} finally {
						schedulerStatusRepository.save(schedulerObj);
					}

				} else {
					logger.info("::buildKylinKube() :: Cube - " + obj.getCubeName()
							+ " buidling is in progress or already cube got build");
				}
			}
		} catch (Exception e) {
			e.printStackTrace();
			errorMsg = e.getMessage();
			schedulerInfo.setErrorMsg(errorMsg);
		} finally {
			if (!isAtleatOneCubeBuild)
				schedulerInfo.setRemarks("No Cubes Build this time");
			else {
				schedulerInfo.setRemarks("Cube Building is in prgoress");
				schedulerInfo.setProcessCount(String.valueOf(processCount));
			}
			updateSchedulerInfo(schedulerInfo);
		}

	}

	private boolean isAnyJobRunning(String cubeName, String processDate) {
		CubeStatus schedulerStatusObj = schedulerStatusRepository.findByProcessDateAndCubeName(processDate, cubeName);
		if (schedulerStatusObj != null) {
			if (!schedulerStatusObj.getIsCompleted().equalsIgnoreCase(RMSConstants.ACTIVE)
					&& schedulerStatusObj.getIsBuildStarted().equalsIgnoreCase(RMSConstants.IN_ACTIVE))
				return false;
			else
				return true;
		} else {
			CubeStatus newObj = new CubeStatus();
			newObj.setProcessDate(processDate);
			newObj.setCubeName(cubeName);
			newObj.setIsCompleted(RMSConstants.IN_ACTIVE);
			newObj.setIsBuildStarted(RMSConstants.IN_ACTIVE);
			schedulerStatusRepository.save(newObj);
			return false;
		}
	}

	/* @Scheduled(cron = "0 0/3 * ? * *") */
	//utc 8 = ist 13.30
	//@Scheduled(cron = "0 0/3 * ? * *")
	public void getJobStatus() throws IOException, JSONException, ParseException, URISyntaxException {
		String errorMsg = "";
		SchedulerInfo schedulerInfo = insertSchedulerInfo("JOB_STATUS");
		try {
			OkHttpClient client = new OkHttpClient();
			List<CubeStatus> runningObjs = schedulerStatusRepository.findByProcessDateAndIsBuildStarted(
					UtilityFile.createSpecifiedDateFormat(RMSConstants.dd_slash_MM_slash_yyyy), RMSConstants.ACTIVE);
			String responseTxt = "";
			for (CubeStatus obj : runningObjs) {
				if (obj.getRefKey() != null && !obj.getRefKey().equals("")) {
					Request request = new Request.Builder()
							.url(RMSConstants.KYLIN_BASE_URL + "/kylin/api/jobs/" + obj.getRefKey()).get()
							.addHeader("cache-control", "no-cache")
							.addHeader("authorization", RMSConstants.KYLIN_AUTH_KEY).build();
					Response response = client.newCall(request).execute();
					responseTxt = response.body().string();
					System.out.println("getJobStatus-->" + responseTxt);
					JSONObject jsonObject = new JSONObject(responseTxt);
					if (jsonObject.has("job_status")) {
						obj.setStatus(jsonObject.getString("job_status"));

					}
					if (jsonObject.has("progress")) {
						obj.setProgress(jsonObject.getDouble("progress") + "");
						if (jsonObject.getDouble("progress") == 100) {
							obj.setIsCompleted(RMSConstants.ACTIVE);
							obj.setStatus("COMPLETED");
						} else if (jsonObject.getDouble("progress") < 100) {
							obj.setStatus("IN-PROGRESS");
						}
					}
					if (jsonObject.has("duration")) {
						if (jsonObject.getInt("duration") > 3600) {
							if (killJob(obj.getRefKey()))
								obj.setStatus("DISCARDED");
							/* trigger mail for discarding job */
						}
						obj.setDuration(String.valueOf((jsonObject.getInt("duration") / 60)));
					}

					schedulerStatusRepository.save(obj);
				} else {
					errorMsg = "Job id is not Available";
				}

			}
		} catch (Exception e) {
			errorMsg = e.getMessage();
			schedulerInfo.setErrorMsg(errorMsg);
		} finally {
			updateSchedulerInfo(schedulerInfo);
		}

	}

	public boolean deleteSegment(String cubeName, String segmentName) throws IOException {
		OkHttpClient client = new OkHttpClient();
		String responseTxt = "";
		Request request = new Request.Builder()
				.url(RMSConstants.KYLIN_BASE_URL + "/kylin/api/cubes/" + cubeName + "/segs/" + segmentName).delete(null)
				.addHeader("cache-control", "no-cache").addHeader("authorization", RMSConstants.KYLIN_AUTH_KEY).build();
		Response response = client.newCall(request).execute();
		responseTxt = response.body().string();
		logger.info("::deleteSegment() :: responseTxt----->" + responseTxt);
		JSONObject jsonObject = new JSONObject(responseTxt);
		if (jsonObject.has("exception")) {
			if (!jsonObject.getString("exception").equals("")) {
				logger.info("::deleteSegment() :: " + cubeName + " delete segment ::" + segmentName + " exception ::"
						+ jsonObject.getString("exception"));
				return false;
			} else {
				return true;
			}
		} else {
			return true;
		}
	}

	public boolean killJob(String jobId) throws IOException, JSONException {
		OkHttpClient client = new OkHttpClient();
		String responseTxt = "";
		Request request = new Request.Builder()
				.url(RMSConstants.KYLIN_BASE_URL + "/kylin/api/jobs/" + jobId + "/cancel").put(null)
				.addHeader("cache-control", "no-cache").addHeader("authorization", RMSConstants.KYLIN_AUTH_KEY).build();
		Response response = client.newCall(request).execute();
		responseTxt = response.body().string();
		JSONObject jsonObject = new JSONObject(responseTxt);
		if (jsonObject.has("job_status")) {
			if (jsonObject.getString("job_status").equals("DISCARDED")) {
				return true;
			} else {
				return false;
			}
		} else {
			return false;
		}
	}

	public SchedulerInfo insertSchedulerInfo(String schedulerName) {
		logger.info("::insertSchedulerInfo() :: Scheduler info insert initiated for scheduler - " + schedulerName);
		SchedulerInfo transactionInfo = new SchedulerInfo();
		transactionInfo.setProcessName(schedulerName);
		transactionInfo.setSchedulerStatus(RMSConstants.InProgress);
		transactionInfo.setSchedulerStartDate(new Timestamp(System.currentTimeMillis()));
		transactionInfo = schedulerInfoRepository.save(transactionInfo);
		logger.info("::insertSchedulerInfo() :: Scheduler info inserted object " + transactionInfo + " for scheduler- "
				+ schedulerName);
		return transactionInfo;
	}

	public void updateSchedulerInfo(SchedulerInfo obj) throws IOException, URISyntaxException {
		logger.info(
				"::updateSchedulerInfo() :: Scheduler info update initiated for scheduler - " + obj.getProcessName());
		obj.setSchedulerEndDate(new Timestamp(System.currentTimeMillis()));
		obj.setSchedulerStatus(RMSConstants.Completed);
		if (obj.getErrorMsg() != null && obj.getErrorMsg().length() > 1000)
			obj.setRemarks(obj.getErrorMsg().substring(0, 1000));
		logger.info("::insertSchedulerInfo() :: Scheduler info updated object " + schedulerInfoRepository.save(obj)
				+ " for scheduler- " + obj.getProcessName());
	}

	public static int getYearFromDate(Date date) {
		int result = -1;
		if (date != null) {
			Calendar cal = Calendar.getInstance();
			cal.setTime(date);
			result = cal.get(Calendar.YEAR);
		}
		return result;
	}

	public long getFinyearStartDateinMilliSeconds() throws ParseException {

		String myDate = getYearFromDate(new Date()) + "/04/01 00:00:00";
		SimpleDateFormat sdf = new SimpleDateFormat("yyyy/MM/dd HH:mm:ss");
		sdf.setTimeZone(TimeZone.getTimeZone("UTC"));
		Date date = sdf.parse(myDate);
		long millis = date.getTime();

		return millis;
	}

	public long getFinyearEndDateinMilliSeconds() throws ParseException {

		String myDate = (getYearFromDate(new Date()) + 1) + "/04/01 00:00:00";
		SimpleDateFormat sdf = new SimpleDateFormat("yyyy/MM/dd HH:mm:ss");
		sdf.setTimeZone(TimeZone.getTimeZone("UTC"));
		Date date = sdf.parse(myDate);
		long millis = date.getTime();

		return millis;
	}
}
