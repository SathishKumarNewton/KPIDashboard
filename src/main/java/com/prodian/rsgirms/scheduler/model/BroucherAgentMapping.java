package com.prodian.rsgirms.scheduler.model;

import java.io.Serializable;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.Table;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * @author Mohamed ismaiel.S
 * @created July 04, 2020 05:12:23 PM
 * @version 1.0
 * @filename Cubes.java
 * @package com.prodian.rsgirms.scheduler.model 
 */

@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
@Entity
@Table(name = "broucher_agent_mapping")
public class BroucherAgentMapping implements Serializable {
	
	@Id
	@Column(name = "id")
    private Integer id;
	
    @Column(name = "agent_id")
    private String agentId;
	
    @Column(name = "broucher_id")
    private Integer broucherId;
    
    @Column(name = "agent_name")
    private String agentName;
    
    @Column(name = "is_dynamic_html_created")
    private String isDynamicHtmlCreated;
    
    @Column(name = "is_html_to_pdf_converted")
    private String isHtmlToPdfConverted;
    
    @Column(name = "is_qr_code_generated")
    private String isQrCodeGenerated;
    
    @Column(name = "is_sms_snet")
    private String isSmsSent;
    
    @Column(name = "html_file_full_path")
    private String htmlFileFullPath;
    
    @Column(name = "pdf_file_full_path")
    private String pdfFileFullPath;
    
    @Column(name = "base_action_url")
    private String baseActionUrl;
    
    @Column(name = "dynamic_action_url")
    private String dynamicActionUrl;
    
    @Column(name = "is_contact_added")
    private String isContactAdded;
}
