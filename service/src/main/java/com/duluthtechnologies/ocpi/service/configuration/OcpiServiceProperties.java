package com.duluthtechnologies.ocpi.service.configuration;

import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.context.annotation.Configuration;

@Configuration
@ConfigurationProperties(prefix = "ocpi.service")
public class OcpiServiceProperties {

	private Emsp emsp = new Emsp();

	public class Emsp {

		private Sync sync = new Sync();

		public class Sync {

			private boolean enabled = true;

			private int intervalInSeconds = 30;

			public boolean isEnabled() {
				return enabled;
			}

			public void setEnabled(boolean enabled) {
				this.enabled = enabled;
			}

			public int getIntervalInSeconds() {
				return intervalInSeconds;
			}

			public void setIntervalInSeconds(int intervalInSeconds) {
				this.intervalInSeconds = intervalInSeconds;
			}

		}

		public Sync getSync() {
			return sync;
		}

		public void setSync(Sync sync) {
			this.sync = sync;
		}
	}

	public Emsp getEmsp() {
		return emsp;
	}

	public void setEmsp(Emsp emsp) {
		this.emsp = emsp;
	}
}
