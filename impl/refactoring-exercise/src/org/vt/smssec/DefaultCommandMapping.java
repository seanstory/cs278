package org.vt.smssec;
import java.util.HashMap;
public class DefaultCommandMapping implements CmdMapping {


	HashMap<String,Class<?>> map = new HashMap<String,Class<?>>();
	
	public DefaultCommandMapping(){
		map.put("say", TalkCommand.class);
		map.put("taunt", TauntCommand.class);
		map.put("photo", PhotoCommand.class);
		
	}

	@Override
	public  Class<?> getCommand(String cmdString) {
		
		return  map.get(cmdString);
	}

	@Override
	public void setCommand(String cmdString, Class<?> cmd) {
		map.put(cmdString, cmd);
	}

}

