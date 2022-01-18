package io.mosip.registration.processor.perf.packet.dto;

import java.util.List;

import io.mosip.registrationProcessor.perf.regPacket.dto.BiometricData;
import io.mosip.registrationProcessor.perf.regPacket.dto.DocumentData;
import io.mosip.registrationProcessor.perf.regPacket.dto.FieldData;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * This contains the attributes which have to be displayed in PacketMetaInfo
 * JSON
 * 
 * @author Balaji Sridharan
 * @since 1.0.0
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
public class Identity {

	private Biometric biometric;
	private Object exceptionBiometrics;
	private Photograph applicantPhotograph;
	private Photograph exceptionPhotograph;
	private List<Document> documents;
	private List<FieldValue> metaData;
	private List<FieldValue> osiData;
	private List<FieldValueArray> hashSequence1;
	private List<FieldValueArray> hashSequence2;
	private List<FieldValue> capturedRegisteredDevices;
	private List<FieldValue> capturedNonRegisteredDevices;
	private Object checkSum;
	private List<String> uinUpdatedFields;

}
