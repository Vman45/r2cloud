package ru.r2cloud.satellite.decoder;

import ru.r2cloud.jradio.Beacon;
import ru.r2cloud.jradio.BeaconSource;
import ru.r2cloud.jradio.FloatInput;
import ru.r2cloud.jradio.blocks.CorrelateAccessCodeTag;
import ru.r2cloud.jradio.blocks.FixedLengthTagger;
import ru.r2cloud.jradio.blocks.SoftToHard;
import ru.r2cloud.jradio.blocks.TaggedStreamToPdu;
import ru.r2cloud.jradio.demod.AfskDemodulator;
import ru.r2cloud.jradio.snet.Snet;
import ru.r2cloud.jradio.snet.SnetBeacon;
import ru.r2cloud.model.ObservationRequest;
import ru.r2cloud.predict.PredictOreKit;
import ru.r2cloud.util.Configuration;

public class SnetDecoder extends TelemetryDecoder {

	public SnetDecoder(PredictOreKit predict, Configuration config) {
		super(predict, config);
	}

	@Override
	public BeaconSource<? extends Beacon> createBeaconSource(FloatInput source, ObservationRequest req) {
		AfskDemodulator demod = new AfskDemodulator(source, 1200, -600, 1500, 1);
		CorrelateAccessCodeTag correlateTag = new CorrelateAccessCodeTag(new SoftToHard(demod), 4, "00000100110011110101111111001000", false);
		TaggedStreamToPdu pdu = new TaggedStreamToPdu(new FixedLengthTagger(correlateTag, 512 * 8));
		return new Snet(pdu);
	}

	@Override
	public Class<? extends Beacon> getBeaconClass() {
		return SnetBeacon.class;
	}
}
