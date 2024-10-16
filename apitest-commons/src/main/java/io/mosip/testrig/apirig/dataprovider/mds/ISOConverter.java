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
	/*	
		byte[] imageData = inStream;// Base64.getDecoder().decode(inStream);
		
		//ImageDataType imageDataType = ImageDataType.JPEG2000_LOSS_LESS;
		//	ByteArrayInputStream bis = new ByteArrayInputStream(imageData);
		
		// BufferedImage buffImg  = ImageIO.read(bis);


		int fingerPosition = mapFingerPos.get(biometricSubType);
		long formatIdentifier = FingerFormatIdentifier.FORMAT_FIR;
		int versionNumber = FingerVersionNumber.VERSION_020;
		int certificationFlag = FingerCertificationFlag.UNSPECIFIED;

		FingerCaptureDeviceTechnology sourceType = FingerCaptureDeviceTechnology.UNSPECIFIED;
		FingerCaptureDeviceVendor deviceVendor = FingerCaptureDeviceVendor.UNSPECIFIED;
		FingerCaptureDeviceType deviceType = FingerCaptureDeviceType.UNSPECIFIED;
		Date captureDate = new Date ();// the date instance		      		 
		int noOfRepresentations= (int)0x0001;
		FingerQualityAlgorithmVendorIdentifier  algorithmVendorIdentifier = FingerQualityAlgorithmVendorIdentifier.NIST;
		FingerQualityAlgorithmIdentifier qualityAlgorithmIdentifier = FingerQualityAlgorithmIdentifier.NIST;

		int quality = 80; 
		FingerQualityBlock [] qualityBlocks = new FingerQualityBlock [] { new FingerQualityBlock ((byte)quality , algorithmVendorIdentifier, qualityAlgorithmIdentifier)};
		FingerCertificationBlock[] certificationBlocks = null; 
	
		int representationNo = (int)0x0000; 
		FingerScaleUnitType scaleUnitType = FingerScaleUnitType.PIXELS_PER_INCH; 
		int captureDeviceSpatialSamplingRateHorizontal = 500; 
		int captureDeviceSpatialSamplingRateVertical = 500;
		int imageSpatialSamplingRateHorizontal = 500; 
		int imageSpatialSamplingRateVertical = 500;
		FingerImageBitDepth bitDepth = FingerImageBitDepth.BPP_08;
		FingerImpressionType impressionType = FingerImpressionType.UNKNOWN;
		int lineLengthHorizontal = 0;
		int lineLengthVertical = 0;
		
		int noOfFingerPresent = (int)0x0001;
		SegmentationBlock segmentationBlock = null;
		AnnotationBlock annotationBlock = null;
		CommentBlock commentBlock = null;
		
		FingerImageCompressionType compressionType = FingerImageCompressionType.JPEG_2000_LOSS_LESS;
		
		*/
		
		byte[] jp2bytes= CommonUtil.convertJPEGToJP2UsingOpenCV(inStream, 95);
		ConvertRequestDto convertRequestDto=new ConvertRequestDto();
		convertRequestDto.setBiometricSubType(biometricSubType);
		convertRequestDto.setImageType(0);
		convertRequestDto.setInputBytes(jp2bytes);
		convertRequestDto.setModality("Finger");
		convertRequestDto.setPurpose(purpose);//R
		convertRequestDto.setVersion("ISO19794_4_2011");
//		byte [] isoData = FingerEncoder.convertFingerImageToISO19794_4_2011 
//				(
//						formatIdentifier, versionNumber, certificationFlag, 
//						sourceType, deviceVendor, deviceType,
//						captureDate, noOfRepresentations,
//						qualityBlocks, certificationBlocks, 
//						fingerPosition, representationNo, scaleUnitType, 
//						captureDeviceSpatialSamplingRateHorizontal, captureDeviceSpatialSamplingRateVertical, 
//						imageSpatialSamplingRateHorizontal, imageSpatialSamplingRateVertical,
//						bitDepth, compressionType,
//						impressionType, lineLengthHorizontal, lineLengthVertical,
//						noOfFingerPresent, imageData, 
//						segmentationBlock, annotationBlock, commentBlock
//				);
		byte [] isoData = FingerEncoder.convertFingerImageToISO(convertRequestDto);
		if (isoData != null && outFile != null)
		{
			Files.write(isoData,new File(outFile));
		
		}
    	return isoData;		
	}
	public byte [] convertIris(byte[] inStream, String outFile, String biometricSubType) throws Exception {
	
		/*
		byte[] imageData =  inStream;//Base64.getDecoder().decode(inStream);
		
		//ImageDataType imageDataType = ImageDataType.JPEG2000_LOSS_LESS;
		ByteArrayInputStream bis = new ByteArrayInputStream(imageData);
		
		BufferedImage buffImg  = ImageIO.read(bis);
		int imageWidth = buffImg.getWidth();
		int imageHeight = buffImg.getHeight();
		long formatIdentifier = IrisFormatIdentifier.FORMAT_IIR;
		long versionNumber = IrisVersionNumber.VERSION_020;
		int certificationFlag = IrisCertificationFlag.UNSPECIFIED;

		Date captureDate = new Date ();// the date instance		      		 

		int algorithmVendorIdentifier = IrisQualityAlgorithmVendorIdentifier.ALGORITHM_VENDOR_IDENTIFIER_0001;
		int qualityAlgorithmIdentifier = IrisQualityAlgorithmIdentifier.ALGORITHM_IDENTIFIER_0001;
    		    	    	
		int eyeLabel = EyeLabel.UNSPECIFIED;
		if (biometricSubType.equals("Left"))
			eyeLabel = EyeLabel.LEFT;
		else if (biometricSubType.equals("Right"))
			eyeLabel = EyeLabel.RIGHT;
		int range = 0x0000;
		int rollAngleOfEye = 0xFFFF;//ANGLE_UNDEFINED
		int rollAngleUncertainty = 0xFFFF; //UNCERTAIN_UNDEFINED
		int irisCenterSmallestX = 0x0000; 	//COORDINATE_UNDEFINED
		int irisCenterLargestX = 0x0000; 	//COORDINATE_UNDEFINED 
		int irisCenterSmallestY = 0x0000; 	//COORDINATE_UNDEFINED 
		int irisCenterLargestY = 0x0000; 	//COORDINATE_UNDEFINED 
		int irisDiameterSmallest = 0x0000; 	//COORDINATE_UNDEFINED 
		int irisDiameterLargest = 0x0000; 	//COORDINATE_UNDEFINED
		int imageType = ImageType.CROPPED;
		int imageFormat = ImageFormat.MONO_JPEG2000;//0A
		int horizontalOrientation = Orientation.UNDEFINED;
		int verticalOrientation = Orientation.UNDEFINED;
		int compressionType = IrisImageCompressionType.UNDEFINED;
		int bitDepth = IrisImageBitDepth.BPP_08;
		int noOfRepresentations = (int)0x0001;
		int representationNo = (int)0x0001;
		int noOfEyesPresent = (int)0x0001;
		int sourceType = IrisCaptureDeviceTechnology.CMOS_OR_CCD;
		int deviceVendor = IrisCaptureDeviceVendor.UNSPECIFIED;
		int deviceType = IrisCaptureDeviceType.UNSPECIFIED;
		int quality = 80; 
		IrisQualityBlock [] qualityBlocks = new IrisQualityBlock [] { new IrisQualityBlock ((byte)quality , algorithmVendorIdentifier, qualityAlgorithmIdentifier)};
		*/
		byte[] jp2bytes= CommonUtil.convertJPEGToJP2UsingOpenCV(inStream, 95);
		ConvertRequestDto convertRequestDto=new ConvertRequestDto();
		convertRequestDto.setBiometricSubType(biometricSubType);
		convertRequestDto.setImageType(0);
		convertRequestDto.setInputBytes(jp2bytes);
		convertRequestDto.setModality("Iris");
		convertRequestDto.setPurpose("Registration");
		convertRequestDto.setVersion("ISO19794_6_2011");
//		byte [] isoData = IrisEncoder.convertIrisImageToISO19794_6_2011 
//			(
//				formatIdentifier, versionNumber,
//				certificationFlag, captureDate, 
//				noOfRepresentations, representationNo, noOfEyesPresent,
//				eyeLabel, imageType, imageFormat,
//				horizontalOrientation, verticalOrientation, compressionType,
//				imageWidth, imageHeight, bitDepth, range, rollAngleOfEye, rollAngleUncertainty, 
//				irisCenterSmallestX, irisCenterLargestX, irisCenterSmallestY, irisCenterLargestY, 
//				irisDiameterSmallest, irisDiameterLargest,
//				sourceType, deviceVendor, deviceType, 
//				qualityBlocks, 
//				imageData, imageWidth, imageHeight
//			);
		byte [] isoData = IrisEncoder.convertIrisImageToISO(convertRequestDto); 
		if (isoData != null && outFile != null)
		{
			Files.write(isoData,new File(outFile));
		
		}
		return isoData;
	}

	public byte [] convertFace(byte[] inStream, String outFile) throws Exception {
		
	/*	long formatIdentifier = FaceFormatIdentifier.FORMAT_FAC;
		long versionNumber = FaceVersionNumber.VERSION_030;
		int certificationFlag = FaceCertificationFlag.UNSPECIFIED;
		int temporalSequenceFlags = TemporalSequenceFlags.ONE_REPRESENTATION;
		Date captureDate = new Date ();// the date instance		      		 
		short noOfLandMarkPoints = 0x00;
		int algorithmVendorIdentifier = FaceQualityAlgorithmVendorIdentifier.ALGORITHM_VENDOR_IDENTIFIER_0001;
		int qualityAlgorithmIdentifier = FaceQualityAlgorithmIdentifier.ALGORITHM_IDENTIFIER_0001;
		int eyeColour = EyeColour.UNSPECIFIED;
		int featureMask = 0;
		int subjectHeight = HeightCodes.UNSPECIFIED;
		int hairColour = HairColour.UNSPECIFIED;
		int expression = 0;
		int features = Features.FEATURES_ARE_SPECIFIED;
		int noOfRepresentations = (int)0x0001;
		int gender = Gender.UNKNOWN;
		int [] poseAngle = { 0, 0, 0 };
		int [] poseAngleUncertainty = { 0, 0, 0 };
		int faceImageType = FaceImageType.FULL_FRONTAL;
		int imageColourSpace = ImageColourSpace.BIT_24_RGB;
		int sourceType = FaceCaptureDeviceTechnology.VIDEO_FRAME_ANALOG_CAMERA;
		int deviceVendor = FaceCaptureDeviceVendor.UNSPECIFIED;
		int deviceType = FaceCaptureDeviceType.UNSPECIFIED;
		
		int spatialSamplingRateLevel = SpatialSamplingRateLevel.SPATIAL_SAMPLING_RATE_LEVEL_180;
		int postAcquisitionProcessing = 0;
		int crossReference = CrossReference.BASIC;
		int quality = 80; 
		LandmarkPoints [] landmarkPoints = null;
		FaceQualityBlock [] qualityBlock = new FaceQualityBlock [] { new FaceQualityBlock ((byte)quality , algorithmVendorIdentifier, qualityAlgorithmIdentifier)};
		//Base64.getEncoder().encodeToString
		
		byte[] imageData = inStream;//Base64.getDecoder().decode(inStream);
		
		int imageDataType = ImageDataType.JPEG2000_LOSS_LESS; // In future  need to change
		
	//discuss	//int imageDataType = convertRequestDto.getPurpose().equalsIgnoreCase("AUTH") ? ImageDataType.JPEG2000_LOSSY :ImageDataType.JPEG2000_LOSS_LESS;
	
		ByteArrayInputStream bis = new ByteArrayInputStream(imageData);
		
		BufferedImage buffImg  = ImageIO.read(bis);
		int imageWidth = buffImg.getWidth();
		int imageHeight = buffImg.getHeight();
		
		*/
		
		
		
	byte[] jp2bytes= CommonUtil.convertJPEGToJP2UsingOpenCV(inStream, 95);
		ConvertRequestDto convertRequestDto=new ConvertRequestDto();
		convertRequestDto.setBiometricSubType("");
		convertRequestDto.setImageType(0);
		convertRequestDto.setInputBytes(jp2bytes);
		convertRequestDto.setModality("Face");
		convertRequestDto.setPurpose("Registration");
		convertRequestDto.setVersion("ISO19794_5_2011");
//		byte [] isoData = FaceEncoder.convertFaceImageToISO19794_5_2011 
//				(
//					formatIdentifier, versionNumber, 
//					certificationFlag, temporalSequenceFlags, 
//					captureDate, noOfRepresentations, noOfLandMarkPoints, 
//					gender, eyeColour, featureMask, 
//					hairColour, subjectHeight, expression, 
//					features, poseAngle, poseAngleUncertainty, 
//					faceImageType, sourceType, deviceVendor, deviceType, 
//					qualityBlock, imageData, imageWidth, imageHeight, 
//					imageDataType, spatialSamplingRateLevel, 
//					postAcquisitionProcessing, crossReference, 
//					imageColourSpace, landmarkPoints
//				); 
//	
		
		byte [] isoData = FaceEncoder.convertFaceImageToISO(convertRequestDto);
		
		if (isoData != null && outFile != null)
		{
			Files.write(isoData,new File(outFile));
		
		}
		return isoData;
	}
	

}
