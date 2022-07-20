package com.prodian.rsgirms.scheduler.service;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.util.Date;
import java.util.List;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Configuration;
import org.springframework.scheduling.annotation.EnableScheduling;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.util.StringUtils;

import com.prodian.rsgirms.constants.RMSConstants;
import com.prodian.rsgirms.dashboard.model.SqoopConfiguation;
import com.prodian.rsgirms.dashboard.repository.SqoopConfigRepository;
import com.prodian.rsgirms.scheduler.model.SqoopJobResponse;
import com.prodian.rsgirms.scheduler.repository.SqoopJobResponseRepository;

@Configuration
public class SqoopSchedulerService {

	@Autowired
	private SqoopConfigRepository sqoopConfigRepository;

	@Autowired
	private SqoopJobResponseRepository sqoopJobResponseRepository;

	Logger logger = LoggerFactory.getLogger(SqoopSchedulerService.class);

//	@Scheduled(cron = "0 0 12 1/1 * ? *")
//	@Scheduled(cron = "0 0/2 15,16,17,18 ? * *")
//	@Scheduled(cron = "0 0/5 11,16,17 ? * *")
	public void sqoopjobScheduler() {
		logger.info("Scoop job scheduler starting... ");
		//List<SqoopConfiguation> sqoops = sqoopConfigRepository.findAll();
		//logger.info("Total sqoops job configs --> {}",sqoops.size());
		//for (SqoopConfiguation sqoop : sqoops) {
//			String SHELL_SCRIPT = "/home/rsbiadmin/playbook/sqoop_workspace/sqoop_appimport.sh";
//			String DB_URL = "localhost:3306";
//			String DB_NAME = "mydb";
//			String DB_USER = "root";
//			String DB_PASSWORD = "Prodian!@34";
//			String S_TABLE = "users";
//			String D_TABLE = "users";
			String SHELL_SCRIPT = "";
			/*if (sqoop.getType() != null && !StringUtils.isEmpty(sqoop.getType())
					&& sqoop.getType().equalsIgnoreCase(RMSConstants.SQOOP_JOB_IMPORT)) {*/
				SHELL_SCRIPT = "/home/rsbiadmin/prodian/sqoop/sqoop_policy_updated.sh";
//				SHELL_SCRIPT = "D:\\sathish\\docs\\rms\\scripts\\sqoop_appimport.sh";
				logger.info("Import job :--------------------------------->");
				//String S_TABLE = sqoop.getSourceTableName();
				//String D_TABLE = sqoop.getDestinationTableName();

				//SqoopJobResponse res = new SqoopJobResponse();
				//res.setSqoopConfigId(sqoop.getId());
				//res.setType(sqoop.getType());
				ProcessBuilder pb = new ProcessBuilder(SHELL_SCRIPT);
				try {
					Process process = pb.start();
					int exitCode = process.waitFor();
					logger.info("The exitCode is :---------------------------------> {}" + exitCode);
					BufferedReader reader = new BufferedReader(new InputStreamReader(process.getInputStream()));
					StringBuilder builder = new StringBuilder();
					String line = null;
					while ((line = reader.readLine()) != null) {
						builder.append(line);
					}
					String result = builder.toString();
					logger.info("The result is :-------------------> {}" + result);				
					if (exitCode == 0) {
						logger.info("message --> {}",
								"The script has ran successfully and the Oracle table has been exported to Hive.");
					} else {
						logger.info("message --> {}", "Exported failed");
					}
					//res.setExitCode(exitCode);
					//res.setResponse(result);
				} catch (IOException e) {
					logger.info("----------------Error-------------------");
					logger.error("IOException occured --> {}", e);
					//res.setErrorMessage(e.getLocalizedMessage());
				} catch (InterruptedException e) {
					logger.error("InterruptedException occured --> {}", e);
					//res.setErrorMessage(e.getLocalizedMessage());
				}
				//res.setUpdatedAt(new Date());
				//sqoopJobResponseRepository.save(res);
				logger.info("End of script execution.");
				/*} 
			
			else if (sqoop.getType() != null && !StringUtils.isEmpty(sqoop.getType())
					&& sqoop.getType().equalsIgnoreCase(RMSConstants.SQOOP_JOB_EXPORT)) {
				SHELL_SCRIPT = "/home/rsbiadmin/prodian/sqoop/sqoop_appimport.sh";
//				SHELL_SCRIPT = "D:\\sathish\\docs\\rms\\scripts\\sqoop_appexport.sh";
				logger.info("Export job :--------------------------------->");
				String DB_URL = sqoop.getSourceSchemaUrl();
				String DB_NAME = sqoop.getDestinationSchemaName();
				String DB_USER = sqoop.getSourceSchemaUserName();
				String DB_PASSWORD = sqoop.getSourceSchemaPassword();
				String S_TABLE = sqoop.getSourceTableName();
				String D_TABLE = sqoop.getDestinationTableName();

				SqoopJobResponse res = new SqoopJobResponse();
				res.setSqoopConfigId(sqoop.getId());
				res.setType(sqoop.getType());
				ProcessBuilder pb = new ProcessBuilder(SHELL_SCRIPT, DB_URL, DB_NAME, DB_USER, DB_PASSWORD, S_TABLE,
						D_TABLE);
				try {
					Process process = pb.start();
					int exitCode = process.waitFor();
					logger.info("The exitCode is :---------------------------------> {}" + exitCode);
					BufferedReader reader = new BufferedReader(new InputStreamReader(process.getInputStream()));
					StringBuilder builder = new StringBuilder();
					String line = null;
					while ((line = reader.readLine()) != null) {
						builder.append(line);
					}
					String result = builder.toString();
					logger.info("The result is :-------------------> {}" + result);				
					if (exitCode == 0) {
						logger.info("message --> {}",
								"The script has ran successfully and the Oracle table has been exported to Hive.");
					} else {
						logger.info("message --> {}", "Exported failed");
					}
					res.setExitCode(exitCode);
					res.setResponse(result);
				} catch (IOException e) {
					logger.info("----------------Error-------------------");
					logger.error("IOException occured --> {}", e);
					res.setErrorMessage(e.getLocalizedMessage());
				} catch (InterruptedException e) {
					logger.error("InterruptedException occured --> {}", e);
					res.setErrorMessage(e.getLocalizedMessage());
				}
				res.setUpdatedAt(new Date());
				sqoopJobResponseRepository.save(res);
				logger.info("End of script execution.");
			}else {
				logger.info("No Import/Export job :--------------------------------->");
				continue;
			}
			
			
		}*/

	}
	
	

}
