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

import java.util.Map;
import java.util.Set;
import java.util.Map.Entry;

import org.apache.log4j.MDC;
import org.apache.log4j.NDC;
import org.apache.log4j.Priority;

public class Logger extends org.apache.log4j.Logger {
	
	private static final CtxDataProvider cdp = new CtxDataProvider();

	protected Logger(String name) {
		super(name);
	}
	
	@Override
	protected void forcedLog(String fqcn, Priority level, Object message, Throwable t) {
		Map<String, String> cd = cdp.supplyContextData();
		Set<Entry<String, String>> entrySet = cd.entrySet();
		for (Entry<String, String> entry : entrySet) {
			MDC.put(entry.getKey(), entry.getValue());
			NDC.push(entry.getKey()+"="+entry.getValue());
		}
		MDC.put("dynatrace", cd);
		super.forcedLog(fqcn, level, message, t);
		for (Entry<String, String> entry : entrySet) {
			MDC.remove(entry.getKey());
			NDC.pop();
		}
		MDC.remove("dynatrace");		
	}

}
