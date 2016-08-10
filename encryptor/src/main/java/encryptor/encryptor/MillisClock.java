package encryptor.encryptor;

import java.time.Clock;
import java.time.Instant;
import java.time.ZoneId;

/**
 * A clock which returns the instant according to the current time zone.
 * @author Sergey
 *
 */
public class MillisClock extends Clock {
	@Override
	public ZoneId getZone() {
		return ZoneId.systemDefault();
	}

	@Override
	public Instant instant() {
		return Instant.ofEpochMilli(System.currentTimeMillis());
	}

	@Override
	public Clock withZone(ZoneId zone) {
		return Clock.system(zone);
	}
}
