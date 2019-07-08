package org.magic.api.interfaces;

import java.io.File;

import javax.script.ScriptException;

public interface MTGScript extends MTGPlugin{

	public String getExtension();

	public Object run(File script) throws ScriptException;
	
	public Object run(String scriptName) throws ScriptException;
	
	public Object runContent(String content) throws ScriptException;

	public boolean isJsr223();
	
}