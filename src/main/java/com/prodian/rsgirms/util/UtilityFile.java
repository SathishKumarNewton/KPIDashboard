package com.prodian.rsgirms.util;

import java.text.SimpleDateFormat;
import java.util.Date;

public class UtilityFile {

	public static String createSpecifiedDateFormat(String dateFormat) {
		String formatedDate = null;
		Date date = new Date();
		formatedDate = new SimpleDateFormat(dateFormat).format(date);
		return formatedDate;
	}

}
