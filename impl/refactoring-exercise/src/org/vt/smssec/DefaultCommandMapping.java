package org.vt.smssec;
import java.util.HashMap;
public class DefaultCommandMapping implements CmdMapping {


	HashMap<String,Class<? extends Command>> map = new HashMap<String,Class<? extends Command>>();
	
	public DefaultCommandMapping(){
		map.put("say", TalkCommand.class);
		map.put("taunt", TauntCommand.class);
		map.put("photo", PhotoCommand.class);
		
	}

	@Override
	public  Class<? extends Command> getCommand(String cmdString) {
		
		return  map.get(cmdString);
	}

	@Override
	public void setCommand(String cmdString, Class<? extends Command> cmd) {
		map.put(cmdString, cmd);
	}

}

