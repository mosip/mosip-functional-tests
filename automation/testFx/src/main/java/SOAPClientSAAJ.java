//import javax.xml.soap.*;
//
//public class SOAPClientSAAJ {
//
//    // SAAJ - SOAP Client Testing
//    public static void main(String args[]) {
//        /*
//            The example below requests from the Web Service at:
//             http://www.webservicex.net/uszip.asmx?op=GetInfoByCity
//
//
//            To call other WS, change the parameters below, which are:
//             - the SOAP Endpoint URL (that is, where the service is responding from)
//             - the SOAP Action
//
//            Also change the contents of the method createSoapEnvelope() in this class. It constructs
//             the inner part of the SOAP envelope that is actually sent.
//         */
//    	String soapEndpointUrl = "http://staging.siebelloyalty.westjet.priv/eai_idm/start.swe?SWEExtSource=SecureWebService&SWEExtCmd=Execute";
//        String soapAction = "http://siebel.com/asi/:WJ_spcMember_spcCreate_spcV2";
//
//        callSoapWebService(soapEndpointUrl, soapAction);
//    }
//
//    private static void createSoapEnvelope(SOAPMessage soapMessage) throws SOAPException {
//        SOAPPart soapPart = soapMessage.getSOAPPart();
//        soapMessage.setProperty(SOAPMessage.CHARACTER_SET_ENCODING, "UTF-8");
//        soapMessage.setProperty(SOAPMessage.WRITE_XML_DECLARATION, "true");
//        String myNamespace = "asi";
//        String myNamespaceURI = "http://siebel.com/asi/";
//        String myNamespace1="wj";
//        String myNamespaceURI1="http://www.siebel.com/xml/WJ%20Create%20Member%20Internal%20IO%20IDM%20V2";
//        String myNameSpace2= "wsse";
//        String myNameSpaceURI2="http://docs.oasis-open.org/wss/2004/01/oasis-200401-wss-wssecurity-secext-1.0.xsd";
//        // SOAP Envelope
//        SOAPEnvelope envelope = soapPart.getEnvelope();
//        
//        envelope.addNamespaceDeclaration(myNamespace, myNamespaceURI);
//        envelope.addNamespaceDeclaration(myNamespace1, myNamespaceURI1);
//        envelope.addNamespaceDeclaration(myNameSpace2, myNameSpaceURI2); 
//     SOAPHeader soapHeader = envelope.getHeader();
//      soapHeader.addNamespaceDeclaration("wsse", "http://docs.oasis-open.org/wss/2004/01/oasis-200401-wss-wssecurity-secext-1.0.xsd");
//      soapHeader.setValue("<wsse:Security><wsse:UsernameToken><wsse:Username>"+user+"</wsse:Username><wsse:Password>"+pwd+"</wsse:Password></wsse:UsernameToken></wsse:Security>");
//     // soapHeader.setTextContent(textContent);
//      soapHeader.getValue().replaceAll("&gt;", ">");
//      //        SOAPElement soapBodyElem =soapHeader.addChildElement("wsse", "Security");
////        SOAPElement  SOAPement1=soapBodyElem.addChildElement("wsse", "UsernameToken");
////       SOAPElement SOAPElement2=SOAPement1.addChildElement("wsse", "Username");
////       SOAPElement2.addTextNode(user);
////        SOAPElement SOAPElement3=SOAPement1.addChildElement("wsse", "Password");
////        SOAPElement3.addTextNode(pwd);
//      
//        
//        /*
//            Constructed SOAP Request Message:
//            <SOAP-ENV:Envelope xmlns:SOAP-ENV="http://schemas.xmlsoap.org/soap/envelope/" xmlns:myNamespace="http://www.webserviceX.NET">
//                <SOAP-ENV:Header/>
//                <SOAP-ENV:Body>
//                    <myNamespace:GetInfoByCity>
//                        <myNamespace:USCity>New York</myNamespace:USCity>
//                    </myNamespace:GetInfoByCity>
//                </SOAP-ENV:Body>
//            </SOAP-ENV:Envelope>
//            */
//        // SOAP Body
//        SOAPBody soapBody = envelope.getBody();
//        soapBody.setValue("<asi:WJ_spcMember_spcCreate_spcV2_Input><wj:WjContactIdm><wj:emailAddress>"+email+"</wj:emailAddress><wj:firstName>"+FName+"</wj:firstName>"+
//			"<wj:lastName>"+LName+"</wj:lastName>" +
//			"<wj:title>Mrs</wj:title>" +
//			"<wj:isMemberFlag>Y</wj:isMemberFlag>" +
//			"<wj:gender>F</wj:gender>" +
//			"<wj:loginName>"+MemId+"</wj:loginName>" +
//			"<wj:responsibility>"+responsibility+"</wj:responsibility>" +
//			"<wj:dateOfBirth>"+dob+"</wj:dateOfBirth>" +
//			"<wj:status>Active</wj:status>" +
//			"<wj:jetMailFlag>Y</wj:jetMailFlag>" +
//			"<wj:preferredLanguage>English</wj:preferredLanguage>" +
//		"</wj:WjContactIdm>" +
//	"</asi:WJ_spcMember_spcCreate_spcV2_Input>");
//        soapBody.setValue(soapBody.getValue().replace("&lt;", "<"));
//        soapBody.setValue(soapBody.getValue().replace("&gt;", ">"));
////        SOAPElement soapBodyElem = soapBody.addChildElement("GetInfoByCity", myNamespace);
////        SOAPElement soapBodyElem1 = soapBodyElem.addChildElement("USCity", myNamespace);
////        soapBodyElem1.addTextNode("New York");
//    }
//
//    private static void callSoapWebService(String soapEndpointUrl, String soapAction) {
//        try {
//            // Create SOAP Connection
//            SOAPConnectionFactory soapConnectionFactory = SOAPConnectionFactory.newInstance();
//            SOAPConnection soapConnection = soapConnectionFactory.createConnection();
//
//            // Send SOAP Message to SOAP Server
//            SOAPMessage soapResponse = soapConnection.call(createSOAPRequest(soapAction), soapEndpointUrl);
//
//            // Print the SOAP Response
//            System.out.println("Response SOAP Message:");
//            soapResponse.writeTo(System.out);
//            System.out.println();
//
//            soapConnection.close();
//        } catch (Exception e) {
//            System.err.println("\nError occurred while sending SOAP Request to Server!\nMake sure you have the correct endpoint URL and SOAPAction!\n");
//            e.printStackTrace();
//        }
//    }
//
//    private static SOAPMessage createSOAPRequest(String soapAction) throws Exception {
//        MessageFactory messageFactory = MessageFactory.newInstance();
//        SOAPMessage soapMessage = messageFactory.createMessage();
//
//        createSoapEnvelope(soapMessage);
//
//        MimeHeaders headers = soapMessage.getMimeHeaders();
//        headers.addHeader("SOAPAction", soapAction);
//
//        soapMessage.saveChanges();
//
//        /* Print the request message, just for debugging purposes */
//        System.out.println("Request SOAP Message:");
//        soapMessage.writeTo(System.out);
//        System.out.println("\n");
//
//        return soapMessage;
//    }
//
//}