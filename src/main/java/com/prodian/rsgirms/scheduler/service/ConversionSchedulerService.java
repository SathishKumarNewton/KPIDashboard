//
//package com.prodian.rsgirms.scheduler.service;
//
//import java.io.BufferedReader;
//import java.io.BufferedWriter;
//import java.io.File;
//import java.io.FileOutputStream;
//import java.io.FileWriter;
//import java.io.InputStreamReader;
//import java.util.ArrayList;
//import java.util.List;
//
//import org.jsoup.Jsoup;
//import org.jsoup.nodes.Element;
//import org.slf4j.Logger;
//import org.slf4j.LoggerFactory;
//import org.springframework.beans.factory.annotation.Autowired;
//import org.springframework.context.annotation.Configuration;
//import org.springframework.scheduling.annotation.EnableScheduling;
//import org.springframework.scheduling.annotation.Scheduled;
//
//import com.itextpdf.text.Document;
//import com.itextpdf.text.Image;
//import com.itextpdf.text.PageSize;
//import com.itextpdf.text.Rectangle;
//import com.itextpdf.text.pdf.PdfAction;
//import com.itextpdf.text.pdf.PdfAnnotation;
//import com.itextpdf.text.pdf.PdfReader;
//import com.itextpdf.text.pdf.PdfStamper;
//import com.itextpdf.text.pdf.PdfWriter;
//import com.prodian.rsgirms.scheduler.model.BroucherAgentMapping;
//import com.prodian.rsgirms.scheduler.repository.BroucherAgentMappingRepository;
//
///**
// * @author CSS
// *
// */
//@Configuration
//@EnableScheduling
//public class ConversionSchedulerService {
//	
//	@Autowired
//	BroucherAgentMappingRepository broucherAgentMappingRepository;
//
//	private static final Logger logger = LoggerFactory.getLogger(ConversionSchedulerService.class.getName());
//
//	/*@Scheduled(cron = "0 0/2 * ? * *")*/
//	public void convertHtmlToPDF() throws Exception {
//		logger.info("convertHtmlToPDF() -------------------------->");
//		String phantomjsPath="",rasterizeJsPath="",htmlPath="",pdfPath="",url="";
//		int index =0;
//		 phantomjsPath = "D:\\html_to_pdf\\converter\\phantomjs";
//		 rasterizeJsPath = "D:\\html_to_pdf\\converter\\rasterize.js";
//		
//		// htmlPath="file:///D:\\html_to_pdf\\converter\\Tranetech.html";
//		// pdfPath="D:\\html_to_pdf\\stack_home\\csk.pdf";
//		 
//		 List<BroucherAgentMapping> mappingList =  broucherAgentMappingRepository.getDynamicHtmlCreatedAndIsHtmlToPdfConvertedList();
//		 logger.info("convertHtmlToPDF() mappingList size --->"+mappingList.size());
//			for(BroucherAgentMapping  obj : mappingList){
//				htmlPath = obj.getHtmlFileFullPath();
//				index =htmlPath.lastIndexOf(".");
//				pdfPath = htmlPath.substring(0, index)+".pdf";
//				logger.info("convertHtmlToPDF() pdfPath --->"+pdfPath);
//				try{
//				if(convertToPdf(   phantomjsPath,  rasterizeJsPath,  "file:///"+htmlPath.replace("\\", "//"),
//						 pdfPath,  url)){
//					obj.setPdfFileFullPath(pdfPath);
//					obj.setIsHtmlToPdfConverted("Y");
//					broucherAgentMappingRepository.save(obj);
//				}
//				}catch(Exception e){
//					e.printStackTrace();
//					obj.setIsHtmlToPdfConverted("E");
//					broucherAgentMappingRepository.save(obj);
//				}
//				
//			}
//		 
//		 
//		 
//	}
//	
//	/*@Scheduled(cron = "0 0/2 * ? * *")*/
//	public void htmlManipulation() throws Exception {
//		logger.info("htmlManipulation() -------------------------->");
//		//String htmlPath ="D:\\html_to_pdf\\converter\\Tranetech.html";
//		String htmlPath ="";int index=0;
//		List<BroucherAgentMapping> mappingList =  broucherAgentMappingRepository.getDynamicHtmlCreatedList();
//		logger.info("htmlManipulation() mappingList --->"+mappingList.size());
//		for(BroucherAgentMapping  obj : mappingList){
//			htmlPath =obj.getHtmlFileFullPath();
//			logger.info("htmlManipulation() htmlPath --->"+htmlPath);
//			File file = new File(htmlPath);
//			org.jsoup.nodes.Document doc = Jsoup.parse(file, null);
//			List<Element> elements = doc.getElementsContainingText("Click Here");
//			//String dynamicurlStr= "java";
//			for(Element element : elements ){
//				//System.out.println("Outer HTML Before Modification :"  + element.outerHtml());
//				element.attr("href",obj.getBaseActionUrl()+obj.getDynamicActionUrl()+obj.getAgentId());
//			}
//			index = file.getAbsolutePath().lastIndexOf(".");
//			//String htmlPathNew ="D:\\html_to_pdf\\converter\\Tranetech_customised.html";
//			String htmlPathNew = file.getAbsolutePath().substring(0, index)+"_"+obj.getAgentId()+".html";
//			logger.info("htmlManipulation() htmlPathNew --->"+htmlPathNew);
//	        FileWriter fstream = new FileWriter(htmlPathNew);
//			BufferedWriter out = new BufferedWriter(fstream);
//			out.write(doc.toString());
//			out.close();
//			fstream.close();
//			obj.setHtmlFileFullPath(htmlPathNew);
//			obj.setIsDynamicHtmlCreated("Y");
//			broucherAgentMappingRepository.save(obj);
//			
//		}
//		
//		
//		 
//	}
//	
//	public File changeExtension(File file, String extension) {
//	    String filename = file.getName();
//
//	    if (filename.contains(".")) {
//	        filename = filename.substring(0, filename.lastIndexOf('.'));
//	    }
//	    filename += "." + extension;
//
//	    file.renameTo(new File(file.getParentFile(), filename));
//	    return file;
//	}
//
//	
//	
//	public boolean convertToPdf( String phantomjsPath, String rasterizeJsPath, String htmlPath,
//			String pdfPath, String url) throws Exception {
//		logger.info("HtmlToPdfSchedulerService - BEGIN downloadPdf() called");
//		boolean result = false;
//
//		//String cmd = phantomjsPath + " " + rasterizeJsPath + " file:///" + htmlPath + " " + pdfPath;
//		String cmd = phantomjsPath + " " + rasterizeJsPath + " " + htmlPath + " " + pdfPath;
//		logger.info("cmd to be executed ::" + cmd);
//		Process process = Runtime.getRuntime().exec(cmd);
//		int exitStatus = process.waitFor();
//		logger.info("Execution status ::" + exitStatus);
//
//		BufferedReader bufferedReader = new BufferedReader(new InputStreamReader(process.getInputStream()));
//
//		String currentLine = null;
//		StringBuilder stringBuilder = new StringBuilder(exitStatus == 0 ? "SUCCESS:" : "ERROR:");
//		currentLine = bufferedReader.readLine();
//		while (currentLine != null) {
//			stringBuilder.append(currentLine);
//			currentLine = bufferedReader.readLine();
//		}
//
//		if (exitStatus != 0) {
//			if (stringBuilder != null)
//				logger.info("Error in html to pdf conversion :: " + stringBuilder.toString());
//		}
//
//		if (exitStatus == 0) {
//			result = true;
//		} else {
//			result = false;
//		}
//		currentLine = null;
//		bufferedReader.close();
//		bufferedReader = null;
//		stringBuilder = null;
//		logger.info("marutipolicyPdfExtracter - END downloadPdf()");
//		return result;
//	}
//
//	public String removeFirstLetterIfStartsWithSlash(String letter) {
//		if (letter.startsWith("/")) {
//			letter = letter.substring(1);
//		}
//		if (letter.contains("/")) {
//			letter = letter.replace("//", "/");
//		}
//		return letter;
//	}
//
//	/*@Scheduled(cron = "0 0/2 * ? * *")*/
//	public void convertJPGToPDF() throws Exception {
//		
//
//		
//		PdfReader reader = new PdfReader("D:\\jpg_to_pdf\\jpg\\output.pdf");
//		PdfStamper stamper = new PdfStamper(reader, new FileOutputStream("D:\\jpg_to_pdf\\jpg\\output_link.pdf"));
//		PdfAnnotation link = PdfAnnotation.createLink(stamper.getWriter(),
//		    new Rectangle(36, 790, 559, 806), PdfAnnotation.HIGHLIGHT_INVERT,
//		    new PdfAction("http://goole.com", 1));
//		stamper.addAnnotation(link, 1);
//		stamper.close();
//		
//		 File root = new File("D:\\jpg_to_pdf\\jpg\\");
//	        String outputFile = "output.pdf";
//	        List<String> files = new ArrayList<String>();
//	        files.add("page1.jpg");
//	        files.add("page2.jpg");
//	        
//	        Document document = new Document();
//	        PdfWriter.getInstance(document, new FileOutputStream(new File(root, outputFile)));
//	        document.open();
//	        for (String f : files) {
//	            document.newPage();
//	            Image image = Image.getInstance(new File(root, f).getAbsolutePath());
//	            image.setAbsolutePosition(0, 0);
//	            image.setBorderWidth(0);
//	            image.scaleAbsolute(PageSize.A4);
//	            document.add(image);
//	        }
//	        document.close();
//		
//	}
//	
//	
//
//}
