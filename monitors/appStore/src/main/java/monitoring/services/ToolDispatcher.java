package monitoring.services;

import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;

import javax.ws.rs.DELETE;
import javax.ws.rs.POST;
import javax.ws.rs.Path;
import javax.ws.rs.PathParam;
import javax.ws.rs.QueryParam;

import org.json.JSONException;
import org.json.JSONObject;

import monitoring.kafka.KafkaCommunication;
import monitoring.model.MonitoringParams;

@Path("configuration")
public class ToolDispatcher {

	//Fake configuration id for testing purposes
	private int confId = 1;
	private final String toolPackageRoute = "monitoring.tools.";
	
	//A data structure storing all monitoring tool instances identified by configuration ID
	private Map<Integer, ToolInterface> monitoringInstances = new HashMap<>();
	
	@POST
	public String addConfiguration(@QueryParam("configurationJson") String jsonConf) {
		
		try {
			MonitoringParams params = parseJsonConfiguration(jsonConf);
			if (params.getToolName() == null) 
				return throwError("Missing tool name");
			
			Class monitor = Class.forName(toolPackageRoute + params.getToolName());
			ToolInterface toolInstance = (ToolInterface) monitor.newInstance();
			toolInstance.addConfiguration(params, KafkaCommunication.initProducer(params.getKafkaEndpoint()), confId);
			
			return getResponse(confId);
			
		} catch (JSONException e) {
			return throwError("Not a valid JSON configuration object");
		} catch (InstantiationException e) {
			return throwError("Monitor class must be concrete");
		} catch (IllegalAccessException e) {
			return throwError("Monitor class must have a constructor with no args");
		} catch (ClassNotFoundException e) {
			return throwError("Not existing tool");
		} catch (Exception e) {
			return throwError(e.getMessage());
		}
		
	}
	
	/**
	 * Deletes the implicit monitoring and stops the monitoring
	 * @param id		the configuration id
	 * @return			response
	 */
	@DELETE
	@Path("{id}")
	public String deleteConfiguration(@PathParam("id") Integer id) {
				
		try {
			if (!monitoringInstances.containsKey(id))
				return throwError("Not existing configuration with the specified ID");
			monitoringInstances.get(id).deleteConfiguration();
			monitoringInstances.remove(id);
		} catch (Exception e) {
			return throwError("There was an unexpected error");
		}
		
		return getResponse(id);
		
	}
	
	private MonitoringParams parseJsonConfiguration(String json) {
		
		MonitoringParams params = new MonitoringParams();
		
		JSONObject jsonParams = new JSONObject(json);
		jsonParams = jsonParams.getJSONObject("AppStoreConfProf");

		Iterator<?> keys = jsonParams.keys();
		params = new MonitoringParams();

		while( keys.hasNext() ) {
			
		    String key = (String)keys.next();
		    if (key.equals("toolName")) params.setToolName(jsonParams.getString(key).replaceAll("\"", ""));
		    else if (key.equals("timeSlot")) params.setTimeSlot(jsonParams.getString(key).replaceAll("\"", ""));
		    else if (key.equals("kafkaEndpoint")) params.setKafkaEndpoint(jsonParams.getString(key).replaceAll("\"", "").replace("http://", ""));
		    else if (key.equals("kafkaTopic")) params.setKafkaTopic(jsonParams.getString(key).replaceAll("\"", ""));
		    else if (key.equals("appId")) params.setAppId(jsonParams.getString(key).replaceAll("\"", ""));
		    else if (key.equals("toolParams")) {
		    	
		    	Iterator<?> toolKeys = jsonParams.getJSONObject(key).keys();
		    	while (toolKeys.hasNext()) {
		    		
		    		String toolKey = (String)keys.next();
		    		if (toolKey.equals("country")) params.setCountry(jsonParams.getJSONObject(key).getString(toolKey).replaceAll("\"", ""));
		    		else if (toolKey.equals("language")) params.setLanguage(jsonParams.getJSONObject(key).getString(toolKey).replaceAll("\"", ""));
		    	}
		    	
		    }
		}
		
		return params;
		
	}

	public String throwError(String error) {
		
		JSONObject response = new JSONObject();
		JSONObject resInfo = new JSONObject();
		
		try {
			resInfo.put("message", error);
			resInfo.put("status", "error");
			response.put("AppStoreConfProfResult", resInfo);
			++confId;		
		} catch(Exception e) {
			e.printStackTrace();
		}
		
		return response.toString();
		
	}
	
	public String getResponse(int id) {
		JSONObject response = new JSONObject();
		JSONObject resInfo = new JSONObject();
		
		try {
			resInfo.put("idConf", id);
			resInfo.put("status", "success");
			response.put("AppStoreConfProfResult", resInfo);
			++confId;		
		} catch(Exception e) {
			e.printStackTrace();
		}
		
		return response.toString();
	}
	
}