package com.openxc.sinks;

import java.util.HashMap;
import java.util.Map;

import org.junit.Before;
import org.junit.Test;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.equalTo;

import com.openxc.remote.RawMeasurement;
import com.openxc.remote.VehicleServiceListenerInterface;

public class RemoteCallbackSinkTest {
    Map<String, RawMeasurement> measurements;
    RemoteCallbackSink notifier;
    VehicleServiceListenerInterface listener;
    String measurementId = "the_measurement";
    String receivedId = null;

    @Before
    public void setUp() {
        // TODO what are the contractual guarantees that this class says about
        // this measurements map?
        measurements = new HashMap<String, RawMeasurement>();
        notifier = new RemoteCallbackSink();
        notifier.setMeasurements(measurements);
        listener = new VehicleServiceListenerInterface.Stub() {
            public void receive(String measurementId, RawMeasurement value) {
                receivedId = measurementId;
            }
        };
    }

    @Test
    public void testRegister() {
        assertThat(notifier.getListenerCount(), equalTo(0));
        notifier.register(listener);
        assertThat(notifier.getListenerCount(), equalTo(1));
    }

    @Test
    public void testUnregisterInvalid() {
        // this just shouldn't explode, it should ignore it...or should it?
        // failing silently is usually a bad thing
        assertThat(notifier.getListenerCount(), equalTo(0));
        notifier.unregister(listener);
        assertThat(notifier.getListenerCount(), equalTo(0));
    }

    @Test
    public void testUnregisterValid() {
        notifier.register(listener);
        assertThat(notifier.getListenerCount(), equalTo(1));
        notifier.unregister(listener);
        assertThat(notifier.getListenerCount(), equalTo(0));
    }

    @Test
    public void testReceiveCorrectId() {
        notifier.register(listener);
        assertThat(receivedId, equalTo(null));
        notifier.receive(measurementId, new RawMeasurement(1));
        assertThat(receivedId, equalTo(measurementId));
    }
}
