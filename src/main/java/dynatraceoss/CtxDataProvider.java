/*
 * Copyright 2024-2025 Dynatrace LLC
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 * http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package dynatraceoss;

import java.io.FileInputStream;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.HashMap;
import java.util.Map;
import java.util.Map.Entry;
import java.util.Properties;
import java.util.Set;

import com.dynatrace.oneagent.sdk.OneAgentSDKFactory;
import com.dynatrace.oneagent.sdk.api.OneAgentSDK;
import com.dynatrace.oneagent.sdk.api.enums.SDKState;
import com.dynatrace.oneagent.sdk.api.infos.TraceContextInfo;

public final class CtxDataProvider {

	private static final Object lock = new Object();
	
	private static OneAgentSDK oneAgentSDK = null;

	private static Map<String, String> META_DATA = loadMetaData();
	
	private static final String DEFAULT_PREFIX = "dt";
	
	private static final String PREFIX = resolvePrefix();
	
	private static OneAgentSDK getOneAgentSDK() {
		synchronized(lock) {
			if (oneAgentSDK == null) {
				oneAgentSDK = OneAgentSDKFactory.createInstance();	
			}			
		}
		return oneAgentSDK;
	}
	
	private static String resolvePrefix() {
		String prefix = System.getenv("DT_CONTEXT_PREFIX");
		if (prefix == null) {
			return DEFAULT_PREFIX;
		}
		if (prefix.endsWith(".")) {
			prefix = prefix.substring(0, prefix.length() - 1);
		}
		if (prefix.length() > 0) {
			return prefix;
		}
		return DEFAULT_PREFIX;
	}
	
	private static Map<String, String> loadMetaData() {
		synchronized(lock) {
			String prefix = resolvePrefix();
			HashMap<String, String> m = new HashMap<>();
			for (String name : new String[] { "dt_metadata_e617c525669e072eebe3d0f08212e8f2.properties",
					"/var/lib/dynatrace/enrichment/dt_metadata.properties" }) {
				try {
					Properties props = new Properties();
					props.load(name.startsWith("/var") ? new FileInputStream(name)
							: new FileInputStream(Files.readAllLines(Paths.get(name)).get(0)));
					Set<Entry<Object, Object>> entrySet = props.entrySet();
					for (Entry<Object, Object> entry : entrySet) {
						m.put(entry.getKey().toString().replace("dt.", prefix + "."), entry.getValue().toString());
					}
				} catch (IOException e) { }
			}
			return m;			
		}
	}

	public Map<String, String> supplyContextData() {
		TraceContextInfo traceContextInfo = getOneAgentSDK().getTraceContextInfo();
		HashMap<String, String> m = new HashMap<String, String>();
		if (traceContextInfo.isValid()) {
			m.put(PREFIX + ".trace_id", traceContextInfo.getTraceId());
			m.put(PREFIX + ".span_id", traceContextInfo.getSpanId());			
		}
		if (META_DATA.isEmpty() && getOneAgentSDK().getCurrentState() == SDKState.ACTIVE) {
			META_DATA = loadMetaData();
		}
		return m;
	}	

}
