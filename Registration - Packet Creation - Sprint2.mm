<map version="1.0.1">
<!-- To view this file, download free mind mapping software FreeMind from http://freemind.sourceforge.net -->
<node CREATED="1538571341803" ID="ID_878061260" MODIFIED="1539171602103" TEXT="Registration - Packet Creation">
<node COLOR="#00cc00" CREATED="1538571438716" HGAP="-24" ID="ID_724940782" MODIFIED="1539003357272" POSITION="right" TEXT="Creation of packet with Registration Data" VSHIFT="-186">
<edge COLOR="#00cc00"/>
<node COLOR="#00cc00" CREATED="1538571614519" HGAP="33" ID="ID_1569057125" MODIFIED="1539003403459" TEXT="Registration packet can be created once the registration is completed." VSHIFT="9">
<node COLOR="#00cc00" CREATED="1539003433456" ID="ID_1447272689" MODIFIED="1539003517275" TEXT="Yes">
<node COLOR="#00cc00" CREATED="1539003481631" ID="ID_1093571047" MODIFIED="1539003968443" TEXT="Store the packet in the default location with all captured details"/>
</node>
<node COLOR="#ff3300" CREATED="1539003475775" ID="ID_428947845" MODIFIED="1539329453017" TEXT="No">
<edge COLOR="#ff0000"/>
<node COLOR="#ff0000" CREATED="1539329247495" ID="ID_1251597489" MODIFIED="1539329453013" TEXT="Display &quot;Appropriate error message&#x201d; / raise a defect">
<edge COLOR="#ff0000"/>
</node>
</node>
</node>
<node COLOR="#00cc00" CREATED="1538571675763" HGAP="50" ID="ID_777431477" MODIFIED="1539003788030" TEXT="Verify the registration is complete" VSHIFT="35">
<node COLOR="#00cc00" CREATED="1539003433456" ID="ID_810701066" MODIFIED="1539003517275" TEXT="Yes">
<node COLOR="#00cc00" CREATED="1539003481631" ID="ID_451976118" MODIFIED="1539003828270" TEXT="Store the packet in the default location with all captured details and acknowledgement receipt"/>
</node>
<node COLOR="#ff3300" CREATED="1539003475775" ID="ID_296650220" MODIFIED="1539329453017" TEXT="No">
<edge COLOR="#ff0000"/>
<node COLOR="#ff0000" CREATED="1539329247495" ID="ID_1634226808" MODIFIED="1539329453013" TEXT="Display &quot;Appropriate error message&#x201d; / raise a defect">
<edge COLOR="#ff0000"/>
</node>
</node>
</node>
<node COLOR="#00cc00" CREATED="1538572088965" HGAP="49" ID="ID_196402287" MODIFIED="1539003943744" TEXT="Verify all the fields that are in User story attachment are included in packet" VSHIFT="64">
<node COLOR="#00cc00" CREATED="1539003433456" ID="ID_1107684310" MODIFIED="1539003517275" TEXT="Yes">
<node COLOR="#00cc00" CREATED="1539003481631" ID="ID_1266400526" MODIFIED="1539003957081" TEXT="Store the packet in the default location with all captured details"/>
</node>
<node COLOR="#ff3300" CREATED="1539003475775" ID="ID_673597371" MODIFIED="1539329453017" TEXT="No">
<edge COLOR="#ff0000"/>
<node COLOR="#ff0000" CREATED="1539329247495" ID="ID_1851475269" MODIFIED="1539329453013" TEXT="Display &quot;Appropriate error message&#x201d; / raise a defect">
<edge COLOR="#ff0000"/>
</node>
</node>
</node>
<node COLOR="#00cc00" CREATED="1538572169993" HGAP="33" ID="ID_544941086" MODIFIED="1539003378528" TEXT="Verify whether user is able to set the packet status" VSHIFT="43">
<node COLOR="#00cc00" CREATED="1539002638390" ID="ID_779374139" MODIFIED="1539002722607" TEXT="Yes">
<node COLOR="#00cc00" CREATED="1539002664245" ID="ID_1727202351" MODIFIED="1539252519929" TEXT="Set the packet status as mentioned by user - &quot;Pending Approval&quot;"/>
</node>
<node COLOR="#ff3300" CREATED="1539002641749" ID="ID_1208477006" MODIFIED="1539002837743" TEXT="No">
<edge COLOR="#ff0000"/>
<node COLOR="#ff0000" CREATED="1539329247495" ID="ID_131177200" MODIFIED="1539329453013" TEXT="Display &quot;Appropriate error message&#x201d; / raise a defect">
<edge COLOR="#ff0000"/>
</node>
</node>
</node>
<node COLOR="#00cc00" CREATED="1538572204428" HGAP="24" ID="ID_1041748699" MODIFIED="1539002801024" TEXT="Verify whether user is able to store the packet in client machine" VSHIFT="60">
<node COLOR="#00cc00" CREATED="1539002735612" ID="ID_1277911427" MODIFIED="1539002788271" TEXT="Yes">
<node COLOR="#00cc00" CREATED="1539002749084" ID="ID_344992522" MODIFIED="1539002788270" TEXT="Store the packet in the location selected by user"/>
</node>
<node COLOR="#ff3300" CREATED="1539002641749" ID="ID_1223110169" MODIFIED="1539002851815" TEXT="No">
<edge COLOR="#ff0000"/>
<node COLOR="#ff0000" CREATED="1539329247495" ID="ID_594485990" MODIFIED="1539329453013" TEXT="Display &quot;Appropriate error message&#x201d; / raise a defect">
<edge COLOR="#ff0000"/>
</node>
</node>
</node>
<node COLOR="#00cc00" CREATED="1538572228085" HGAP="12" ID="ID_1480198112" MODIFIED="1539003083629" TEXT="Verify whether sysytem captures and store the transaction details for audit purpose" VSHIFT="53">
<node COLOR="#00cc00" CREATED="1539003087723" ID="ID_648252266" MODIFIED="1539329487845" TEXT="System capture all Txn details ">
<node COLOR="#00cc00" CREATED="1539003122483" ID="ID_1079503882" MODIFIED="1539004893050" TEXT="Store all the details under &quot;Audit_Log&quot; table"/>
</node>
<node COLOR="#ff0000" CREATED="1539001463902" ID="ID_1895850955" MODIFIED="1539329437421" TEXT="System fails to capture and store Txn details">
<edge COLOR="#ff0000"/>
<node COLOR="#ff0000" CREATED="1539329247495" ID="ID_1349599077" MODIFIED="1539329371624" TEXT="Display &quot;Appropriate error message&#x201d; / raise a defect">
<edge COLOR="#ff0000"/>
</node>
</node>
</node>
</node>
<node COLOR="#009900" CREATED="1538573214093" ID="ID_1197490598" MODIFIED="1539171621545" POSITION="left" TEXT="Encrypt and store the registration packet.">
<node COLOR="#009900" CREATED="1538573239656" ID="ID_723528338" MODIFIED="1539171621545" TEXT="Registration packet should be created and available for encryption">
<node COLOR="#00cc00" CREATED="1538573309605" HGAP="28" ID="ID_491074271" MODIFIED="1539004579452" TEXT="Packet Encryption" VSHIFT="-94">
<node COLOR="#00cc00" CREATED="1538573415340" ID="ID_1865548013" MODIFIED="1539004579452" TEXT="Registrastion data encryption using AES and Key encryption using RSA">
<node COLOR="#00cc00" CREATED="1539004547019" ID="ID_502323922" MODIFIED="1539004606038" TEXT="Successfully Encrypted"/>
<node COLOR="#ff3300" CREATED="1539004554515" ID="ID_1787580777" MODIFIED="1539329512772" TEXT="Failed to Encrypt">
<edge COLOR="#ff0000"/>
<node COLOR="#ff0000" CREATED="1539329247495" ID="ID_166461788" MODIFIED="1539329453013" TEXT="Display &quot;Appropriate error message&#x201d; / raise a defect">
<edge COLOR="#ff0000"/>
</node>
</node>
</node>
</node>
<node COLOR="#00cc00" CREATED="1538573329258" HGAP="30" ID="ID_612545895" MODIFIED="1539004753011" TEXT="Storing the encrypted packets to the client machine" VSHIFT="-59">
<node COLOR="#00cc00" CREATED="1539004721414" ID="ID_1461043524" MODIFIED="1539004753011" TEXT="Yes">
<node COLOR="#00cc00" CREATED="1539004735318" ID="ID_1260587522" MODIFIED="1539329556472" TEXT="The file should be stored in the default / user specified location"/>
</node>
<node COLOR="#ff3300" CREATED="1539004729222" ID="ID_1159274074" MODIFIED="1539004777666" TEXT="No">
<node COLOR="#ff0000" CREATED="1539329247495" ID="ID_202576749" MODIFIED="1539329453013" TEXT="Display &quot;Appropriate error message&#x201d; / raise a defect">
<edge COLOR="#ff0000"/>
</node>
</node>
</node>
<node COLOR="#00cc00" CREATED="1538573357820" ID="ID_1023764139" MODIFIED="1539004821648" TEXT="System should capture and store the transaction details for audit purpose.">
<node COLOR="#00cc00" CREATED="1539003087723" ID="ID_1722282721" MODIFIED="1539004858202" TEXT="System capture all Txn details ">
<node COLOR="#00cc00" CREATED="1539003122483" ID="ID_350218038" MODIFIED="1539004872266" TEXT="Store all the details under &quot;Audit_Log&quot; table"/>
</node>
<node COLOR="#ff0000" CREATED="1539001463902" ID="ID_802214587" MODIFIED="1539329396602" TEXT="System fails to capture and store Txn details">
<edge COLOR="#ff0000"/>
<node COLOR="#ff0000" CREATED="1539329247495" ID="ID_376739726" MODIFIED="1539329371624" TEXT="Display &quot;Appropriate error message&#x201d; / raise a defect">
<edge COLOR="#ff0000"/>
</node>
</node>
</node>
</node>
</node>
</node>
</map>
