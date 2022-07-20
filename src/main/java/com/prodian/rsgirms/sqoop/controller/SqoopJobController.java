
package com.prodian.rsgirms.sqoop.controller;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.servlet.ModelAndView;

import com.prodian.rsgirms.scheduler.model.SqoopJobResponse;

/**
 * @author Zakir Hussain Syed
 * @created September 1, 2020 11:34:23 AM
 * @version 1.0
 * @filename SqoopJobController.java
 * @package com.prodian.rsgirms.sqoop.controller
 */

@Controller
public class SqoopJobController {

	private static final Logger logger = LogManager.getLogger(SqoopJobController.class);

	@GetMapping(value = "/admin/runsqoopcmd")
	public String runSqoopCommand(Model model) {

//		String SHELL_SCRIPT = "/home/rsbiadmin/playbook/sqoop_workspace/sqoop_appimport.sh";
		String SHELL_SCRIPT = "/home/rsbiadmin/prodian/sqoop/sqoop_appimport.sh";		
		String DB_URL = "localhost:3306";
		String DB_NAME = "mydb";
		String DB_USER = "root";
		String DB_PASSWORD = "Prodian!@34";
		String S_TABLE = "users";
		String D_TABLE = "users";

		ProcessBuilder pb = new ProcessBuilder(SHELL_SCRIPT, DB_URL, DB_NAME, DB_USER, DB_PASSWORD, S_TABLE, D_TABLE);
		try {
			Process process = pb.start();
			int exitCode = process.waitFor();
			logger.info("The exitCode is :--------------------------------->" + exitCode);
			BufferedReader reader = new BufferedReader(new InputStreamReader(process.getInputStream()));
			StringBuilder builder = new StringBuilder();
			String line = null;
			while ((line = reader.readLine()) != null) {
				builder.append(line);
			}
			String result = builder.toString();
			logger.info("The result is :------------------->" + result);
			logger.info("End of script execution.");
			if(exitCode == 0) {
				model.addAttribute("message","The script has ran successfully and the Oracle table has been exported to Hive.");
			}else {
				model.addAttribute("message","Exported failed");
			}
		} catch (IOException e) {
			logger.info("----------------Error-------------------");
			logger.error("IOException occured", e);
			model.addAttribute("message","IO exception occured");			
		} catch (InterruptedException e) {
			logger.error("InterruptedException occured", e);
			model.addAttribute("message","InterruptedException occured");
		}
		return "admin/response";
	}
	
	@GetMapping("/admin/response")
	public ModelAndView sqoopResponse() {
		ModelAndView model = new ModelAndView("admin/response");
		return model;
	}
	
	@GetMapping("/admin/testSqoopJob")
	public @ResponseBody SqoopJobResponse testSqoopJob(@RequestParam String hiveTable,@RequestParam String lastModifiedDate,
			Integer fyear) {
		logger.info("sqoop job");
		String sqoopQuery = "\"select * from rsa_dwh_policy "
				+ " where to_char(last_modified_date,'ddMMyyyy')='"+lastModifiedDate+"' "
				+ "and (( financial_year = '"+fyear+"' "
				+ "and eff_fin_year_month in ('04','05','06','07','08','09','10','11','12') ) "
				+ "or ( financial_year = '"+(fyear+1)+"' and eff_fin_year_month in ('01','02','03') ) ) "
				+ "and \\$CONDITIONS\"";
		
//		String hiveTable = "rsdb.RSA_DWH_POLICY_SQOOP_3032021";
		String targetDir = "/user/hive/warehouse/rsdb.db/"+hiveTable;
		String scriptLocation = "/home/rsbiadmin/prodian/sqoop/sqoop_dailyimport.sh";
		SqoopJobResponse res = new SqoopJobResponse();
		try {
			
			ProcessBuilder pb = new ProcessBuilder(scriptLocation, sqoopQuery, hiveTable, targetDir);
			logger.info("command ---> {}",pb.command());
			Process process = pb.start();
			int exitCode = process.waitFor();
			logger.info("The exitCode is :--------------------------------->{}" , exitCode);
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
		}catch (IOException e) {
			logger.info("----------------Error-------------------");
			logger.error("IOException occured --> {}", e);
			res.setErrorMessage(e.getLocalizedMessage());
		} catch (InterruptedException e) {
			logger.error("InterruptedException occured --> {}", e);
			res.setErrorMessage(e.getLocalizedMessage());
		}
		return res;
	}
	
	@GetMapping("/admin/testSqoopJob1")
	public @ResponseBody SqoopJobResponse testSqoopJob1(@RequestParam String hiveTable,@RequestParam String createdDate,
			Integer fyear) {
		logger.info("sqoop job");
//		String sqoopQuery = "\"select * from rsa_dwh_policy "
//				+ " where to_char(last_modified_date,'ddMMyyyy')='"+lastModifiedDate+"' "
//				+ " and (( financial_year = '"+fyear+"' "
//				+ " and eff_fin_year_month in ('04','05','06','07','08','09','10','11','12') ) "
//				+ " or ( financial_year = '"+(fyear+1)+"' and eff_fin_year_month in ('01','02','03') ) ) "
//				+ " and \\$CONDITIONS\"";
		
		String whereCon = " to_char(created_date,'ddMMyyyy')='"+createdDate+"' " + 
				" and (( financial_year = '"+fyear+"' " + 
				" and eff_fin_year_month in ('04','05','06','07','08','09','10','11','12') ) " + 
				" or ( financial_year = '"+(fyear+1)+"' and eff_fin_year_month in ('01','02','03') ) ) ";
		
//		String hiveTable = "rsdb.RSA_DWH_POLICY_SQOOP_3032021";
		String targetDir = "/user/hive/warehouse/rsdb.db/"+hiveTable;
		String scriptLocation = "/home/rsbiadmin/prodian/sqoop/sqoop_dailyimport.sh";
		SqoopJobResponse res = new SqoopJobResponse();
		try {
			
//			CommandLine cmdLine = CommandLine.parse(command);
//
//			for (String comm : cmd)
//			{
//			    cmdLine.addArgument(comm);
//			}
//
//			DefaultExecutor exec = new DefaultExecutor();
//			exec.setExitValue(0);
//			exec.setWorkingDirectory(new File(codeDir));
//			exitCode = exec.execute(cmdLine);
			
			ProcessBuilder pb = new ProcessBuilder(scriptLocation);
			logger.info("command ---> {}",pb.command());
			pb.redirectErrorStream(true);
			pb.redirectOutput(ProcessBuilder.Redirect.INHERIT);
			Process process = pb.start();
			int exitCode = process.waitFor();
			
			logger.info("The exitCode is :--------------------------------->{}" , exitCode);
			BufferedReader reader = new BufferedReader(new InputStreamReader(process.getInputStream()));
			StringBuilder builder = new StringBuilder();
			String line = null;
			logger.info("------------------------------builder start-----------------------");
			while ((line = reader.readLine()) != null) {
				logger.info(line);
				builder.append(line);				
				builder.append(System.getProperty("line.separator"));
			}
			logger.info("-----------------------builder end---------------------------");
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
		}catch (IOException e) {
			logger.info("----------------Error-------------------");
			logger.error("IOException occured --> {}", e);
			res.setErrorMessage(e.getLocalizedMessage());
		} catch (InterruptedException e) {
			logger.error("InterruptedException occured --> {}", e);
			res.setErrorMessage(e.getLocalizedMessage());
		}
		return res;
	}

}
