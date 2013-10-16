package org.vt.smssec;

public interface CmdMapping {

	/**
	 * 
	 * This method returns the component that is bound to a given
	 * type.
	 * 
	 * @param type - type type of component to retrieve
	 * @return
	 */
	public Class<?> getCommand(String cmdString);
	
	/**
	 * 
	 * Bind a component to a type.
	 * 
	 * @param type - the type to bind the component to
	 * @param component - the object instance to associate with the type key
	 */
	public  void setCommand(String cmdString, Class<?> cmd);
	
}
