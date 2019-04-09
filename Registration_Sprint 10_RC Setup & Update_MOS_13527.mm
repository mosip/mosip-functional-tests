<map version="1.0.1">
<!-- To view this file, download free mind mapping software FreeMind from http://freemind.sourceforge.net -->
<node CREATED="1554096926941" ID="ID_1019371916" MODIFIED="1554096983697" TEXT="Registration - Download the setup kit">
<node CREATED="1554097003339" ID="ID_451115330" LINK="https://mosipid.atlassian.net/browse/MOS-13527" MODIFIED="1554097015875" POSITION="right" TEXT="MOS-13527">
<node COLOR="#00cc00" CREATED="1538552664561" ID="ID_1575628185" LINK="https://mosipid.atlassian.net/browse/MOS-14" MODIFIED="1540443212510" TEXT="Download setup kit">
<node COLOR="#00cc00" CREATED="1538546560614" HGAP="-99" ID="ID_1911580426" MODIFIED="1539002227629" TEXT="Setup Kit should be available and user must logged in as RO / Supervisor" VSHIFT="-139">
<icon BUILTIN="attach"/>
<node COLOR="#00cc00" CREATED="1538546638685" ID="ID_1493952769" MODIFIED="1538996448050" TEXT="RO / Supervisor should have access to the download portal">
<edge COLOR="#00cc33"/>
<arrowlink DESTINATION="ID_1493952769" ENDARROW="Default" ENDINCLINATION="0;0;" ID="Arrow_ID_933818428" STARTARROW="None" STARTINCLINATION="0;0;"/>
<linktarget COLOR="#b0b0b0" DESTINATION="ID_1493952769" ENDARROW="Default" ENDINCLINATION="0;0;" ID="Arrow_ID_933818428" SOURCE="ID_1493952769" STARTARROW="None" STARTINCLINATION="0;0;"/>
<node COLOR="#00cc00" CREATED="1538546900673" ID="ID_1414288116" MODIFIED="1539060159710" TEXT="Verify whether user is able to download the kit to the local client machine based on OS">
<node COLOR="#00cc00" CREATED="1538549281635" HGAP="11" ID="ID_150300847" MODIFIED="1539001972153" TEXT="Download status" VSHIFT="-44">
<node COLOR="#33cc00" CREATED="1539060206720" ID="ID_1167318419" MODIFIED="1539060220755" TEXT="Success - User should be able to download and store in local client"/>
<node COLOR="#ff0000" CREATED="1539329247495" ID="ID_599375420" MODIFIED="1539329306583" TEXT="Display &quot;Appropriate error message&#x201d; / raise a defect">
<edge COLOR="#ff0000"/>
</node>
</node>
</node>
<node COLOR="#33cc00" CREATED="1539060266124" HGAP="15" ID="ID_1976803277" MODIFIED="1539060312171" TEXT="Verify whether user is able to unzip the kit and verify the content as same as design document" VSHIFT="29">
<node COLOR="#33cc00" CREATED="1539060314962" ID="ID_211828018" MODIFIED="1539060324775" TEXT="Unzip status">
<node COLOR="#33cc00" CREATED="1539060330945" ID="ID_4476642" MODIFIED="1539060363835" TEXT="Success - User should be able to unzip without any issues"/>
<node COLOR="#ff0000" CREATED="1539329247495" ID="ID_1174896445" MODIFIED="1539329314329" TEXT="Failure - Display &quot;Appropriate error message&#x201d; / raise a defect">
<edge COLOR="#ff0000"/>
</node>
</node>
</node>
</node>
</node>
</node>
<node CREATED="1538564612523" ID="ID_1972480940" MODIFIED="1538565203523">
<richcontent TYPE="NODE"><html>
  <head>
    
  </head>
  <body>
    <p>
      Client app SW should be available on dongle and connected to client
    </p>
    <p>
      The client should be registered in client portal
    </p>
  </body>
</html></richcontent>
<arrowlink DESTINATION="ID_1972480940" ENDARROW="Default" ENDINCLINATION="0;0;" ID="Arrow_ID_484067575" STARTARROW="None" STARTINCLINATION="0;0;"/>
<linktarget COLOR="#b0b0b0" DESTINATION="ID_1972480940" ENDARROW="Default" ENDINCLINATION="0;0;" ID="Arrow_ID_484067575" SOURCE="ID_1972480940" STARTARROW="None" STARTINCLINATION="0;0;"/>
<node COLOR="#00cc00" CREATED="1538565206731" HGAP="12" ID="ID_1061807685" MODIFIED="1539000748767" STYLE="fork" TEXT="Launch client application" VSHIFT="-102">
<edge COLOR="#00cc00"/>
<node COLOR="#00cc00" CREATED="1538565561438" HGAP="8" ID="ID_1378208380" MODIFIED="1538997833517" TEXT="Double click on MOSIP and verify" VSHIFT="-45">
<node COLOR="#00cc00" CREATED="1539000761847" ID="ID_631374027" MODIFIED="1539000841075" TEXT="Success">
<node COLOR="#00cc00" CREATED="1539000817920" ID="ID_1992053656" MODIFIED="1539000841074" TEXT="Launch the application and validate the landing page "/>
</node>
<node COLOR="#ff0000" CREATED="1538571225478" ID="ID_1596581710" MODIFIED="1539002969905" TEXT="Failure">
<edge COLOR="#ff0000"/>
<node COLOR="#ff0000" CREATED="1539329247495" ID="ID_50682318" MODIFIED="1539329258739" TEXT="Display &quot;Appropriate error message&#x201d; / raise a defect"/>
</node>
</node>
</node>
<node COLOR="#00cc00" CREATED="1538565229182" ID="ID_788470298" MODIFIED="1538998183580" TEXT="System validation ">
<node COLOR="#00cc00" CREATED="1538566086300" HGAP="141" ID="ID_1027177221" MODIFIED="1554097159593" TEXT="Validate mode of startup is machine install and not dongle" VSHIFT="-40">
<node COLOR="#00cc00" CREATED="1539000761847" ID="ID_1914482417" MODIFIED="1554097165177" TEXT="Mode of startup is machine install">
<node COLOR="#00cc00" CREATED="1539000817920" ID="ID_329243463" MODIFIED="1539001161542" TEXT="Launch the application and display the default landing page "/>
</node>
<node COLOR="#ff3300" CREATED="1538571225478" ID="ID_349925225" MODIFIED="1554792487283" TEXT="Launch from dongle and observe">
<edge COLOR="#ff0000"/>
<node COLOR="#ff0000" CREATED="1538721028938" ID="ID_901623958" MODIFIED="1554097186248" TEXT="Display &quot;The client application must be launched from a machine. Please contact the Administrator for assistance.&#x201d;">
<edge COLOR="#ff0000"/>
</node>
</node>
</node>
<node COLOR="#00cc00" CREATED="1538566153837" HGAP="159" ID="ID_161264966" MODIFIED="1538998183579" TEXT="Validate the machine on which the app is launched is registered" VSHIFT="-27">
<arrowlink DESTINATION="ID_161264966" ENDARROW="Default" ENDINCLINATION="0;0;" ID="Arrow_ID_1403900718" STARTARROW="None" STARTINCLINATION="0;0;"/>
<linktarget COLOR="#b0b0b0" DESTINATION="ID_161264966" ENDARROW="Default" ENDINCLINATION="0;0;" ID="Arrow_ID_1403900718" SOURCE="ID_161264966" STARTARROW="None" STARTINCLINATION="0;0;"/>
<node COLOR="#00cc00" CREATED="1539000761847" ID="ID_324520922" MODIFIED="1539001079334" TEXT="Machine Registered">
<node COLOR="#00cc00" CREATED="1539000817920" ID="ID_450875205" MODIFIED="1539001171045" TEXT="Launch the application and display  the default landing page "/>
</node>
<node COLOR="#ff0000" CREATED="1538571242848" ID="ID_854709911" MODIFIED="1538998129392" TEXT="Machine is not registered">
<edge COLOR="#ff0000"/>
<node COLOR="#ff0000" CREATED="1538721067139" ID="ID_805474234" MODIFIED="1538998129401" TEXT="Display &quot;&#x201c;This machine is not a registered machine. Please contact the Administrator for assistance.&#x201d;">
<edge COLOR="#ff0000"/>
</node>
</node>
</node>
<node COLOR="#00cc00" CREATED="1538570793441" HGAP="177" ID="ID_1121481201" MODIFIED="1539001212300" TEXT="Verify whether the client machine is mapped to the registration center as per config" VSHIFT="2">
<node COLOR="#00cc00" CREATED="1539000761847" ID="ID_958963362" MODIFIED="1539001123972" TEXT="Machine Registered and Mapped">
<node COLOR="#00cc00" CREATED="1539000817920" ID="ID_1857336103" MODIFIED="1539001179348" TEXT="Launch the application and display  the default landing page "/>
</node>
<node COLOR="#ff0000" CREATED="1538571257894" ID="ID_733918630" MODIFIED="1538998129393" TEXT="Machine is registered but not mapped to a Registration Centre ">
<edge COLOR="#ff0000"/>
<node COLOR="#ff0000" CREATED="1538721106507" ID="ID_1377539359" MODIFIED="1538998129397" TEXT="Display &quot;This machine is not mapped to a Registration Centre. Please contact the Administrator for assistance.&#x201d;">
<edge COLOR="#ff0000"/>
</node>
</node>
</node>
<node COLOR="#00cc00" CREATED="1538570935041" HGAP="189" ID="ID_962271057" MODIFIED="1539253730477" TEXT="Skip the validation if the machine is not mapped as per config (use a machine that is not registered)" VSHIFT="22">
<node COLOR="#00cc00" CREATED="1539000761847" ID="ID_1657057728" MODIFIED="1539001123972" TEXT="Machine Registered and Mapped">
<node COLOR="#00cc00" CREATED="1539000817920" ID="ID_1293926119" MODIFIED="1539001179348" TEXT="Launch the application and display  the default landing page "/>
</node>
</node>
<node COLOR="#00cc00" CREATED="1538571004726" HGAP="198" ID="ID_1820930881" MODIFIED="1539001271437" TEXT="Verify the system&apos;s RAM matches as mentioned in Config" VSHIFT="30">
<node COLOR="#00cc00" CREATED="1539000761847" ID="ID_768293003" MODIFIED="1539001389663" TEXT="Sufficient RAM">
<node COLOR="#00cc00" CREATED="1539000817920" ID="ID_715795281" MODIFIED="1539001179348" TEXT="Launch the application and display  the default landing page "/>
</node>
<node COLOR="#ff0000" CREATED="1538571267847" ID="ID_508761647" MODIFIED="1538998129392" TEXT="Insufficient RAM ">
<edge COLOR="#ff0000"/>
<node COLOR="#ff0000" CREATED="1538721189253" ID="ID_442211633" MODIFIED="1538998129396" TEXT="Display &#x201c;A minimum of xx GB RAM is required to run the application. Please contact the Administrator for assistance.&#x201d; ">
<edge COLOR="#ff0000"/>
</node>
</node>
</node>
<node COLOR="#00cc00" CREATED="1539001234662" HGAP="203" ID="ID_780005378" MODIFIED="1539001328720" TEXT="Verify the system&apos;s Hard disk space matches as mentioned in Config" VSHIFT="22">
<node COLOR="#00cc00" CREATED="1539000761847" ID="ID_824082354" MODIFIED="1539001421847" TEXT="Sufficient hard disk space">
<node COLOR="#00cc00" CREATED="1539000817920" ID="ID_1222824306" MODIFIED="1539001179348" TEXT="Launch the application and display  the default landing page "/>
</node>
<node COLOR="#ff0000" CREATED="1538571279176" ID="ID_305841894" MODIFIED="1538998129393" TEXT="Insufficient hard disk space ">
<edge COLOR="#ff0000"/>
<node COLOR="#ff0000" CREATED="1538721217902" ID="ID_1304995752" MODIFIED="1538998129393" TEXT="Display &#x201c;A minimum of xx GB hard disk space must be free to run the application. Please contact the Administrator for assistance.&#x201d;">
<edge COLOR="#ff0000"/>
</node>
</node>
</node>
<node COLOR="#00cc00" CREATED="1538571071657" HGAP="210" ID="ID_979877115" MODIFIED="1539001247604" TEXT="Verify updates on the server are available" VSHIFT="17">
<node COLOR="#00cc00" CREATED="1538571097023" ID="ID_386406137" MODIFIED="1539263508487" TEXT="Updates available and download">
<node COLOR="#339900" CREATED="1539253782951" ID="ID_1644446162" MODIFIED="1539263561632" TEXT="Download status">
<node COLOR="#339900" CREATED="1539253860382" ID="ID_1463162379" MODIFIED="1539263561632" TEXT="Success - Display appropriate alert message"/>
<node COLOR="#ff0000" CREATED="1539329247495" ID="ID_1349599077" MODIFIED="1539329371624" TEXT="Display &quot;Appropriate error message&#x201d; / raise a defect">
<edge COLOR="#ff0000"/>
</node>
</node>
</node>
<node COLOR="#00cc00" CREATED="1538571116588" ID="ID_620133183" MODIFIED="1538998183578" TEXT="If no updates available / unable to connect, proceed to next page"/>
</node>
</node>
</node>
</node>
</node>
</map>
