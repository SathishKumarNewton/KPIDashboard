package com.prodian.rsgirms.usermatrix.enums;

public enum UserMatrixMasterValueEnum {

	ALL("All"), SPECIFIC("Specific"), NA("NA");

	private String value;

	private UserMatrixMasterValueEnum(String value) {
		this.value = value;
	}

	public String getValue() {
		return value;
	}
	
	public static UserMatrixMasterValueEnum getUserMatrixMasterValue(String value) {
		UserMatrixMasterValueEnum enumList[] = UserMatrixMasterValueEnum.values();
		for (UserMatrixMasterValueEnum userMatrixMasterValueEnum : enumList) {
			if(userMatrixMasterValueEnum.getValue().equals(value)) {
				return userMatrixMasterValueEnum;
			}
		}
		return null;
	}

}
