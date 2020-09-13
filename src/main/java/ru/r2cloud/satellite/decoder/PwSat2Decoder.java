package ru.r2cloud.satellite.decoder;

import ru.r2cloud.jradio.Ax25G3ruhBeaconSource;
import ru.r2cloud.jradio.Beacon;
import ru.r2cloud.jradio.BeaconSource;
import ru.r2cloud.jradio.FloatInput;
import ru.r2cloud.jradio.demod.BpskDemodulator;
import ru.r2cloud.jradio.pwsat2.PwSat2Beacon;
import ru.r2cloud.model.ObservationRequest;
import ru.r2cloud.predict.PredictOreKit;
import ru.r2cloud.util.Configuration;

public class PwSat2Decoder extends TelemetryDecoder {

	public PwSat2Decoder(PredictOreKit predict, Configuration config) {
		super(predict, config);
	}

	@Override
	public BeaconSource<? extends Beacon> createBeaconSource(FloatInput source, ObservationRequest req) {
		BpskDemodulator bpsk = new BpskDemodulator(source, 9600, 1, 0.0, false);
		return new Ax25G3ruhBeaconSource<>(bpsk, PwSat2Beacon.class);
	}
	
	@Override
	public Class<? extends Beacon> getBeaconClass() {
		return PwSat2Beacon.class;
	}
}
