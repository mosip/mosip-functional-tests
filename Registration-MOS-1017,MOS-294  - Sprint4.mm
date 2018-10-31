<map version="1.0.1">
<!-- To view this file, download free mind mapping software FreeMind from http://freemind.sourceforge.net -->
<node COLOR="#006633" CREATED="1540459396575" ID="ID_1832777464" MODIFIED="1540461935819" TEXT="Registration">
<node COLOR="#006633" CREATED="1540460131384" ID="ID_802995512" MODIFIED="1540640371748" POSITION="right" TEXT="Individual Category Selection">
<edge COLOR="#33cc00"/>
<node COLOR="#009933" CREATED="1540462721202" HGAP="87" ID="ID_166885565" LINK="https://mosipid.atlassian.net/browse/MOS-1017" MODIFIED="1540641241591" TEXT="MOS-1017" VSHIFT="104">
<font NAME="SansSerif" SIZE="12"/>
<node COLOR="#009933" CREATED="1540462760778" ID="ID_1090190840" LINK="1017-Pre-requisites.txt" MODIFIED="1540464775839" TEXT="Pre-Requisites">
<font NAME="SansSerif" SIZE="12"/>
<node COLOR="#009933" CREATED="1540463080212" ID="ID_450037040" MODIFIED="1540464775838" TEXT="The system receives a request from the client UI to start a new registration">
<font NAME="SansSerif" SIZE="12"/>
<node COLOR="#009933" CREATED="1540463339810" ID="ID_224346609" MODIFIED="1540538865665" TEXT="System should determine the individuals age by using date of birth">
<linktarget COLOR="#b0b0b0" DESTINATION="ID_224346609" ENDARROW="Default" ENDINCLINATION="12;148;" ID="Arrow_ID_1011038644" SOURCE="ID_1301472824" STARTARROW="None" STARTINCLINATION="88;0;"/>
<font NAME="SansSerif" SIZE="12"/>
<node COLOR="#009933" CREATED="1540463422258" HGAP="18" ID="ID_1747643039" MODIFIED="1540532674232" TEXT="Age is above 5 years" VSHIFT="34">
<node COLOR="#009933" CREATED="1540532377370" ID="ID_1750426097" MODIFIED="1540536333273" TEXT="To be implemented"/>
</node>
<node COLOR="#009933" CREATED="1540463447554" ID="ID_1482216309" MODIFIED="1540464775837" TEXT="Age is below 5 years">
<font NAME="SansSerif" SIZE="12"/>
<node COLOR="#009933" CREATED="1540463543493" HGAP="25" ID="ID_1555227865" MODIFIED="1540464775837" TEXT="check for parent/guardian&#x2019;s UIN">
<font NAME="SansSerif" SIZE="12"/>
<node COLOR="#009933" CREATED="1540463562306" HGAP="18" ID="ID_291950784" MODIFIED="1540464894981" TEXT="UIN exist" VSHIFT="6">
<font NAME="SansSerif" SIZE="12"/>
<node COLOR="#009933" CREATED="1540463899001" ID="ID_926617215" MODIFIED="1540464775836" TEXT="The system should capture parent/guardian&apos;s Biometrics">
<font NAME="SansSerif" SIZE="12"/>
</node>
<node COLOR="#009933" CREATED="1540463899001" ID="ID_135823910" MODIFIED="1540464775836" TEXT="The system should capture parent/guardian&apos;s Proof of relationship">
<font NAME="SansSerif" SIZE="12"/>
</node>
<node COLOR="#009933" CREATED="1540463899001" ID="ID_1776798147" MODIFIED="1540464775836" TEXT="The system should capture parent/guardian&apos;s UIN">
<font NAME="SansSerif" SIZE="12"/>
</node>
</node>
<node COLOR="#009933" CREATED="1540463574921" ID="ID_1427673755" MODIFIED="1540531487501" TEXT="UIN not exist">
<node COLOR="#009933" CREATED="1540464370859" ID="ID_604960019" MODIFIED="1540464831451" TEXT="The system should ensure Parent/guardian is registered first">
<node COLOR="#ff0000" CREATED="1540464394731" HGAP="17" ID="ID_890389108" MODIFIED="1540464785428" TEXT="Parent/guardian is not registered" VSHIFT="9">
<node COLOR="#ff0000" CREATED="1540464428323" ID="ID_1001127336" MODIFIED="1540464785427" TEXT="Exception- Parent/guardian should be registered first"/>
</node>
<node COLOR="#009933" CREATED="1540464394731" ID="ID_801845741" MODIFIED="1540464775837" TEXT="Parent/guardian is registered">
<font NAME="SansSerif" SIZE="12"/>
<node COLOR="#009933" CREATED="1540463899001" ID="ID_1826890927" MODIFIED="1540464775837" TEXT="The system should capture parent/guardian&apos;s Registration ID"/>
<node COLOR="#009933" CREATED="1540463899001" ID="ID_555581740" MODIFIED="1540464775837" TEXT="The system should capture parent/guardian&apos;s Biometrics"/>
<node COLOR="#009933" CREATED="1540463899001" ID="ID_71344303" MODIFIED="1540464775837" TEXT="The system should capture parent/guardian&apos;s Proof of relationship"/>
</node>
</node>
</node>
</node>
</node>
<node COLOR="#009933" CREATED="1538550071542" HGAP="16" ID="ID_298688633" MODIFIED="1540533240100" TEXT="Verification of Txn details for Audit purpose" VSHIFT="86">
<edge COLOR="#00cc00"/>
<node COLOR="#009933" CREATED="1539003087723" ID="ID_778455006" MODIFIED="1540533240100" TEXT="System capture all Txn details">
<node COLOR="#009933" CREATED="1539003122483" ID="ID_1571064101" MODIFIED="1540533240100" TEXT="Store all the details under &quot;Audit_Log&quot; table"/>
</node>
<node COLOR="#ff0000" CREATED="1539060442854" ID="ID_1110172134" MODIFIED="1539330244952" TEXT="System fails to capture Txn details">
<edge COLOR="#ff0000"/>
<node COLOR="#ff0000" CREATED="1539089688847" ID="ID_708220719" MODIFIED="1539330406352" TEXT="Display appropriate error message / Raise a defect"/>
</node>
</node>
</node>
</node>
</node>
</node>
<node COLOR="#009933" CREATED="1540462407320" HGAP="79" ID="ID_1338733095" LINK="https://mosipid.atlassian.net/browse/MOS-294" MODIFIED="1540641272823" TEXT="MOS-294" VSHIFT="-211">
<font NAME="SansSerif" SIZE="12"/>
<node COLOR="#009933" CREATED="1540462449417" ID="ID_379230026" LINK="294-Pre-requisites.txt" MODIFIED="1540464775839" TEXT="Pre-requisites">
<font NAME="SansSerif" SIZE="12"/>
<node COLOR="#009933" CREATED="1540462605155" ID="ID_1301472824" MODIFIED="1540538865650" TEXT="The system receives a request to start new enrolment for a non-pre-enroled applicant">
<arrowlink DESTINATION="ID_224346609" ENDARROW="Default" ENDINCLINATION="12;148;" ID="Arrow_ID_1011038644" STARTARROW="None" STARTINCLINATION="88;0;"/>
<font NAME="SansSerif" SIZE="12"/>
</node>
</node>
</node>
</node>
</node>
</map>
