In questa cartella sono presenti le cartelle che contengono la configurazione delle liste per ciascun DATA_TYPE, 
le cartelle devono chiamarsi esttamente come il DATA_TYPE di riferimento.

ES:

FLIGHT_EXTFLIGHT
	 ARR.xml
	 ATSUCOPI.xml
	 ATSUSECI.xml
	 ATSUSECL.xml
	 COORDIN.xml

ENV_RWY_IN_USE
	 COORDIN.xml
	 COORDOUT.xml
	 DEP.xml
	 DIAL.xml
	 FIRENTRY.xml
	 ...........
..........
.........


Di seguito un esempio di file XML che configura la lista FLIGHT DIRECTORY LIST:

<msg>
	<list
		ROW_ID="@FLIGHT_NUM@" condition="@ISFLDI@">
		<attributes	ID="FLIGHT DIRECTORY LIST"	lat=""	lon=""	behavior_if_empty="delete"	behavior_if_full="visible"	side="SFL"		sort="CALLSIGN:ASC"		/>
		
		<col key="col1"		indata="@CALLSIGN@"				outdata="CALLSIGN"		row_fgr="#CECECE"	row_middleClick="SHOW_TRAJECTORY"	/>
		<col key="col2"		indata="@SSR_CODE@"				outdata="ASSR"		/>
		<col key="col3"		indata="@SFPL_STATE@"				outdata="FS"	col_state="filteredVisible"	/>
		<col key="col4"		indata="@SCT@"					outdata="SCT"		/>
		<col key="col5"		indata="@FR@ @FTYPE@"				outdata="FR"		choicefield="$FUSION"	/>
		<col key="col6"		indata="@GAT_OAT@"				outdata="G/O"		/>
		<col key="col7"		indata="@MDS@"					outdata="MDS"	col_state="filteredVisible"	/>
		<col key="col8"		indata="@DLE@"					outdata="DL"	col_state="filteredNotVisible"	/>
		<col key="col9"		indata="@EQ_833_STATUS@ @EQ_833_PERMISSION@"	outdata="8.33"	col_state="filteredNotVisible"	choicefield="$FUSION"	/>
		<col key="col10"	indata="@RVSM@"					outdata="RVSM"	col_state="filteredNotVisible"	/>
		<col key="col11"	indata="@ATY@"					outdata="TYPE"	col_state="filteredNotVisible"	/>
		<col key="col12"	indata="@NSSR_CODE@"				outdata="NSSR"	col_state="filteredNotVisible"	/>
	</list>
</msg>

L'attributo ROW_ID della tag "list" è utilizzato per dare un id univoco alle righe .
L'attributo condition della tag "list" è utilizzato per visualizzare la lista quando il relativo parametro dell msg è true

Nella tag attributes sono presenti i parametri comportamentali delle liste :

	.L'attributo ID		è il nomw della lista 
	.LATITUDE  e LOGITUDE	indicano la georeferenziazione della lista
	.BEHAVIOR_IF_EMPTY	indica il tipo di comportamento quando la lista si svuota
	.GROUP			indica con quali liste può essere raggruppata dello stesso gruppo
	.SWAP			indica se la lista può essere spostata
	.SIDE			indica il nome dell'aletta in cui deve essere agganciata
	.SORT			indica il tipo di ordinamento e su quale colonna
	.LIST_TYPE		indica se è trasparente oppure opaca

Nelle tag col sono presenti i parametri per creare le colonne e le righe della lista:

	indata		è il nome del campo del messaggio di input
	outdata		è il nome della colonna in presentazione
	col_..		tutti gli aatributi che hanno prefisso "col_" sono attributi delle colonne
	row_..		tutti gli attributi che hanno prefisso "row_" sono attributi di tutte le righe in quella colonna

il seguente esempio assegna una callback "SHOW_TRAJECTORY" sul tasto centrale del mouse per tutte le celle della colonna  "CALLSIGN": 

	<col key="col1"		indata="@CALLSIGN@"				outdata="CALLSIGN"		row_fgr="#CECECE"	row_middleClick="SHOW_TRAJECTORY"	/>

 
in ogni attributo sono consentite delle espressioni per recuperare il valore dalla memoria dell'applicazione nei messaggi in base ai data type

ESEMPI di ESPRESSIONI VALIDE:

 @ADES@							: recupera dal data_type a cui appartine la lista (nome della cartella in cui si trova) il valore del dato ADES
 @DAFIF_ARPT<ADES>.WGS_DLAT@				: recupera il valore del dato "WGS_DLAT" dal messaggio con identificativo "ADES" dalla blackboard dei data_type "DAFIF_ARPT"
 @DAFIF_ARPT< FLIGHT<FLIGHT_NUM>.ADES >.WGS_DLAT@	: recupera il valore del dato "WGS_DLAT" dalla blackboard "DAFIF_ARPT" utilizzando come ID il dato recuperato dalla blackboard FLIGHT nel volo con ID=<FLIGHT_NUM> nel campo: "ADES"
 @ADES?@ETL@;@IAF@					: se il campo "ADES" è presente allora ritorna il valore di "ETL" altrimenti recupeara il valore di "IAF"

utilizzando le blackboard di appoggio 
		 BB_STN_FLIGHT  è possibile recuperare tramite STN il FLIGHT_NUM del volo correlato es : @BB_STN_FLIGHT(STN).FLIGHT_NUM@
		 BB_CALLSIGN_FLIGHT è possibile recuperare tramite CALLSIGN il FLIGHT_NUM relativo : @BB_CALLSIGN_FLIGHT(CALLSIGN).FLIGHT_NUM@

 è possibile inserire piu dati come valore di ritorno ed inserire anche testo libero tra i valori :

	indata="@ATY@ - @IAF@"

in questo esempio il risultato sara una stringa che conterra : [valore di ATY]+" - "+[il valore di IAF]

