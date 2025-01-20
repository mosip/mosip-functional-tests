package io.mosip.testrig.apirig.dataprovider.mds;



import java.io.File;
import java.util.HashMap;
import java.util.Map;

import com.google.common.io.Files;

import io.mosip.biometrics.util.CommonUtil;
import io.mosip.biometrics.util.ConvertRequestDto;
import io.mosip.biometrics.util.face.FaceEncoder;
import io.mosip.biometrics.util.finger.FingerEncoder;
import io.mosip.biometrics.util.finger.FingerPosition;
import io.mosip.biometrics.util.iris.IrisEncoder;

public class ISOConverter {
	
	static Map<String,Integer> mapFingerPos = new HashMap<String, Integer>();
	static {
		mapFingerPos.put("Right Thumb", FingerPosition.RIGHT_THUMB);
		mapFingerPos.put("Right IndexFinger", FingerPosition.RIGHT_INDEX_FINGER);
		mapFingerPos.put("Right MiddleFinger", FingerPosition.RIGHT_MIDDLE_FINGER);
		mapFingerPos.put("Right RingFinger", FingerPosition.RIGHT_RING_FINGER);
		mapFingerPos.put("Right LittleFinger", FingerPosition.RIGHT_LITTLE_FINGER);
		mapFingerPos.put("Left Thumb", FingerPosition.LEFT_THUMB);
		mapFingerPos.put("Left IndexFinger", FingerPosition.LEFT_INDEX_FINGER);
		mapFingerPos.put("Left MiddleFinger", FingerPosition.LEFT_MIDDLE_FINGER);
		mapFingerPos.put("Left RingFinger", FingerPosition.LEFT_RING_FINGER);
		mapFingerPos.put("Left LittleFinger", FingerPosition.LEFT_LITTLE_FINGER);
				
	}
	public static int getFingerPos(String bioSubType) {
		Integer fingerPosition = mapFingerPos.get(bioSubType);
		return fingerPosition;
	}
	public byte [] convertFinger(byte[] inStream, String outFile, String biometricSubType,String purpose) throws Exception {
		
		byte[] jp2bytes= CommonUtil.convertJPEGToJP2UsingOpenCV(inStream, 95);
		ConvertRequestDto convertRequestDto=new ConvertRequestDto();
		convertRequestDto.setBiometricSubType(biometricSubType);
		convertRequestDto.setImageType(0);
		convertRequestDto.setInputBytes(jp2bytes);
		convertRequestDto.setModality("Finger");
		convertRequestDto.setPurpose(purpose);//R
		convertRequestDto.setVersion("ISO19794_4_2011");
		byte [] isoData = FingerEncoder.convertFingerImageToISO(convertRequestDto);
		if (isoData != null && outFile != null)
		{
			Files.write(isoData,new File(outFile));
		
		}
    	return isoData;		
	}
	public byte [] convertIris(byte[] inStream, String outFile, String biometricSubType) throws Exception {
		byte[] jp2bytes= CommonUtil.convertJPEGToJP2UsingOpenCV(inStream, 95);
		ConvertRequestDto convertRequestDto=new ConvertRequestDto();
		convertRequestDto.setBiometricSubType(biometricSubType);
		convertRequestDto.setImageType(0);
		convertRequestDto.setInputBytes(jp2bytes);
		convertRequestDto.setModality("Iris");
		convertRequestDto.setPurpose("Registration");
		convertRequestDto.setVersion("ISO19794_6_2011");
		byte [] isoData = IrisEncoder.convertIrisImageToISO(convertRequestDto); 
		if (isoData != null && outFile != null)
		{
			Files.write(isoData,new File(outFile));
		
		}
		return isoData;
	}

	public byte [] convertFace(byte[] inStream, String outFile) throws Exception {		
		
	byte[] jp2bytes= CommonUtil.convertJPEGToJP2UsingOpenCV(inStream, 95);
		ConvertRequestDto convertRequestDto=new ConvertRequestDto();
		convertRequestDto.setBiometricSubType("");
		convertRequestDto.setImageType(0);
		convertRequestDto.setInputBytes(jp2bytes);
		convertRequestDto.setModality("Face");
		convertRequestDto.setPurpose("Registration");
		convertRequestDto.setVersion("ISO19794_5_2011");	
		
		byte [] isoData = FaceEncoder.convertFaceImageToISO(convertRequestDto);
		
		if (isoData != null && outFile != null)
		{
			Files.write(isoData,new File(outFile));
		
		}
		return isoData;
	}
	

}
