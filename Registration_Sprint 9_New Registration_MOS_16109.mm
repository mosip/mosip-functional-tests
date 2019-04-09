<map version="1.0.1">
<!-- To view this file, download free mind mapping software FreeMind from http://freemind.sourceforge.net -->
<node CREATED="1553671545957" ID="ID_1189965564" MODIFIED="1553671571377" TEXT="Registration - Low score as biometric exception">
<node CREATED="1553671576560" ID="ID_1351586850" LINK="https://mosipid.atlassian.net/browse/MOS-16109" MODIFIED="1553672479980" POSITION="right" TEXT="MOS-16109">
<edge COLOR="#33cc00"/>
<node COLOR="#33cc00" CREATED="1553671615885" HGAP="16" ID="ID_1052743601" MODIFIED="1553672518269" TEXT="Biometric exception button available" VSHIFT="-22">
<node COLOR="#33cc00" CREATED="1553671637185" ID="ID_248929956" MODIFIED="1553672518268" TEXT="Yes">
<node COLOR="#33cc00" CREATED="1553671644608" HGAP="12" ID="ID_1939850572" MODIFIED="1553672518264" TEXT="Use it to mark the missing biometrics for a resident and system will mark them with reason as &quot;Missing biometrics&quot;(backend process)" VSHIFT="-8"/>
</node>
<node COLOR="#ff9900" CREATED="1553671638194" ID="ID_441163269" MODIFIED="1553672542821" TEXT="No">
<edge COLOR="#ff0000"/>
<node COLOR="#ff0000" CREATED="1553671640213" ID="ID_1588643999" MODIFIED="1553672536205" TEXT="Raise a defect"/>
</node>
</node>
<node COLOR="#33cc00" CREATED="1553671694625" ID="ID_1350006554" MODIFIED="1553672518264" TEXT="Quality score &gt;= threshold">
<node COLOR="#33cc00" CREATED="1553671705416" HGAP="17" ID="ID_1540264691" MODIFIED="1553672518263" TEXT="Yes" VSHIFT="-13">
<node COLOR="#33cc00" CREATED="1553671726295" ID="ID_640094326" MODIFIED="1553672518263" TEXT="Proceed to the next screen"/>
</node>
<node COLOR="#33cc00" CREATED="1553671706870" HGAP="16" ID="ID_1310045670" MODIFIED="1553672518263" TEXT="No" VSHIFT="17">
<node COLOR="#33cc00" CREATED="1553671710876" ID="ID_1664034338" MODIFIED="1553672518263" TEXT="Retry limit exceeded">
<node COLOR="#33cc00" CREATED="1553671747761" ID="ID_872655389" MODIFIED="1553672518262" TEXT="Yes">
<node COLOR="#33cc00" CREATED="1553672083053" HGAP="19" ID="ID_1880651899" MODIFIED="1553672518257" TEXT="System should mandate capture of exception photo and system should automatically flag the reason for exception as &#x2018;Low Quality of biometrics&#x2019;. (backend process)" VSHIFT="-7"/>
</node>
<node COLOR="#33cc00" CREATED="1553671761141" ID="ID_613164642" MODIFIED="1553672518257" TEXT="No">
<node COLOR="#33cc00" CREATED="1553671763962" ID="ID_350692804" MODIFIED="1553672518257" TEXT="Recapture the biometric and system should take the best one"/>
</node>
</node>
</node>
</node>
<node COLOR="#33cc00" CREATED="1553672240806" HGAP="8" ID="ID_504349252" MODIFIED="1553672518257" TEXT="Missing biometric and quality score is less than threshold together" VSHIFT="40">
<node COLOR="#33cc00" CREATED="1553672294647" ID="ID_951274090" MODIFIED="1553672525516" TEXT="Yes">
<node COLOR="#33cc00" CREATED="1553672299330" ID="ID_1539062558" MODIFIED="1553672518254" TEXT="System should mark the exception reason as design i.e., for missing info mark it as &quot;Missing biometrics&quot; and for low quality score &quot;Low qaulity of biometrics&quot; (backend process)"/>
</node>
</node>
<node COLOR="#33cc00" CREATED="1553672350346" HGAP="4" ID="ID_1660093076" MODIFIED="1553672518254" TEXT="Is biometric photo capture mandatory" VSHIFT="51">
<node COLOR="#33cc00" CREATED="1553672402315" HGAP="23" ID="ID_305955552" MODIFIED="1553672518254" TEXT="Missing biometrics" VSHIFT="-26">
<node COLOR="#33cc00" CREATED="1553672424771" ID="ID_458460090" MODIFIED="1553672518253" TEXT="Yes">
<linktarget COLOR="#b0b0b0" DESTINATION="ID_458460090" ENDARROW="Default" ENDINCLINATION="64;0;" ID="Arrow_ID_150288892" SOURCE="ID_1093063346" STARTARROW="None" STARTINCLINATION="64;0;"/>
<node COLOR="#33cc00" CREATED="1553672446737" ID="ID_1153636991" MODIFIED="1553672518253" TEXT="Capture both face and exception photo"/>
</node>
</node>
<node COLOR="#33cc00" CREATED="1553672408656" ID="ID_1093063346" MODIFIED="1554790157447" TEXT="Low quality biometrics">
<arrowlink DESTINATION="ID_458460090" ENDARROW="Default" ENDINCLINATION="64;0;" ID="Arrow_ID_150288892" STARTARROW="None" STARTINCLINATION="64;0;"/>
</node>
</node>
<node COLOR="#33cc00" CREATED="1538550071542" HGAP="9" ID="ID_455711452" MODIFIED="1550474395630" TEXT="Verification of Txn details for Audit purpose" VSHIFT="34">
<edge COLOR="#00cc00"/>
<node COLOR="#33cc00" CREATED="1539003087723" ID="ID_702214481" MODIFIED="1550474395629" TEXT="System capture all Txn details">
<node COLOR="#33cc00" CREATED="1539003122483" ID="ID_635478647" MODIFIED="1550474395625" TEXT="Store all the details under &quot;Audit_Log&quot; table such as User id or system account; Machine Details; Event Name; Application Name, and Event data including user entered fields."/>
</node>
<node COLOR="#ff0000" CREATED="1539060442854" ID="ID_1387756172" MODIFIED="1550474463694" TEXT="System fails to capture Txn details">
<edge COLOR="#ff0000"/>
<node COLOR="#ff0000" CREATED="1539089688847" ID="ID_1837414142" MODIFIED="1550474463695" TEXT="Display appropriate error message / Raise a defect"/>
</node>
</node>
</node>
</node>
</map>
