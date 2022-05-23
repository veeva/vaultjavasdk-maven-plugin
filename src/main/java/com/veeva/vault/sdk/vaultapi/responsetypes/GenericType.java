package com.veeva.vault.sdk.vaultapi.responsetypes;

import java.lang.reflect.Field;


//Abstract object parent 
public abstract class GenericType<T> {
	protected String responseStatus;
	protected String responseMessage;

	//Uses reflection to match the String fieldName to a valid variable on the class and then return that variable's value. 
	@SuppressWarnings("unchecked")
	public T getField(String fieldName) throws NoSuchFieldException, SecurityException, IllegalArgumentException, IllegalAccessException {
		
		Class clazz = getClass();
		try {
			Field field = getClass().getDeclaredField(fieldName);
			field.setAccessible(true);
			return (T) field.get(this);
		} catch (NoSuchFieldException e) {
			try {
				Field field = clazz.getSuperclass().getDeclaredField(fieldName);
				field.setAccessible(true);
				return (T) field.get(this);
			} catch (NoSuchFieldException e1) {
				try {
					Field field = clazz.getSuperclass().getSuperclass().getDeclaredField(fieldName);
					field.setAccessible(true);
					return (T) field.get(this);
				} catch (NoSuchFieldException e2) {
					return (T) ((T) "GENERIC_TYPE ERROR - Invalid fieldname: " + fieldName);
				}
			}
		} 
	}
}