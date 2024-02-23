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

import java.lang.reflect.Field;

import org.apache.log4j.Hierarchy;
import org.apache.log4j.LogManager;

public final class LoggerFactory implements org.apache.log4j.spi.LoggerFactory {
	
	public LoggerFactory() {
		Hierarchy loggerRepository = (Hierarchy)LogManager.getLoggerRepository();
		try {
			Field fldDefaultFactory = Hierarchy.class.getDeclaredField("defaultFactory");
			fldDefaultFactory.setAccessible(true);
			fldDefaultFactory.set(loggerRepository, this);
		} catch (Throwable t) {
			t.printStackTrace(System.err);
		}
	}
	
	@Override
	public org.apache.log4j.Logger makeNewLoggerInstance(String name) {
		return new Logger(name);
	}

}
