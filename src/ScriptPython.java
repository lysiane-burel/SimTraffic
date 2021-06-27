/*
import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.nio.charset.StandardCharsets;

public class ScriptPython {
	
	Process mProcess;

	public void runScript(String[] pathAndArgs){
		// pathAndArgs should be ["python", "pythonfile.py", "arg1", "arg2", ...]
	       Process process;
	       try{
	             process = Runtime.getRuntime().exec(pathAndArgs);
	             mProcess = process;
	       }catch(Exception e) {
	          System.out.println("Exception Raised " + e.toString());
	       }
	       InputStream stdout = mProcess.getInputStream();
	       BufferedReader reader = new BufferedReader(new InputStreamReader(stdout,StandardCharsets.UTF_8));
	       String line;
	       try{
	          while((line = reader.readLine()) != null){
	               System.out.println("stdout: "+ line);
	          }
	       }catch(IOException e){
	             System.out.println("Exception in reading output"+ e.toString());
	       }
	       System.out.println(mProcess.exitValue());
	}
}*/

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.nio.charset.StandardCharsets;

public class ScriptPython {

	Process mProcess;
	
	public void runScript(String[] pathAndArgs){
		// pathAndArgs should be ["python", "pythonfile.py", "arg1", "arg2", ...]
		//System.out.println(pathAndArgs[1]);
		long start = System.nanoTime()/1000000000 ;
		try {   
			Process process = Runtime.getRuntime().exec(pathAndArgs);
			mProcess = process;
	        new Thread(new Runnable() {
	        	public void run() {
	        		//System.out.println(pathAndArgs[1]);
	        		BufferedReader input = new BufferedReader(new InputStreamReader(process.getInputStream()));
	                BufferedReader error= new BufferedReader(new InputStreamReader(process.getErrorStream()));
	                String line;
	                String errorLine;
	                try {
	                	while ((line = input.readLine()) != null) {
	                		System.out.println(line);
	                       }
	                   } catch (IOException e) {
	                       e.printStackTrace();
	                   }
	                 try {
	                    while ((errorLine = input.readLine()) != null) {
	                        System.out.println(errorLine);
	                       }
	                   } catch (IOException e) {
	                       e.printStackTrace();
	                   }
	               }
		    }).start();
	        /*
	        if(mProcess.exitValue()==0)
			{
				process.destroy();
			}*/
	        //process.destroy();
		    process.waitFor();
	    } catch (Exception e) {
	    	e.printStackTrace();
	        }		
		System.out.print("Run time : ");
		System.out.println(System.nanoTime()/1000000000 - start);
		//System.out.println(mProcess.exitValue()); 
	}
}
