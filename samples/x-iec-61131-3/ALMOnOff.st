INTERFACE
    USES Define;
    FUNCTION_BLOCK MC_HandleALM;
END_INTERFACE

IMPLEMENTATION
// ============================================================================
// ----------- Function Blocks ------------------------------------------------
// ============================================================================

FUNCTION_BLOCK MC_HandleALM
// ----------------------------------------------------------------------------
//  <division / place / author>
// ----------------------------------------------------------------------------
//  Functionality description: This function block switches the active line 
//                             module on/off. Also errors can be acknowledged.
// ----------------------------------------------------------------------------
//  Task assignment: This function block has to be used in cyclical tasks. 
// ----------------------------------------------------------------------------
//  change log table:
//  version   date        expert in charge        changes applied          
//  01.00.00  04.02.04    A. Krull A&D B18        created
//  02.00.00  08.11.04    S. Eisfeld A&D B18      alm not disabled when reset
//                                                occurs. With reset and enable
//                                                bit 1st error reset, 2nd
//                                                enable alm.
//  03.00.00  28.09.05    I. Hall SEA             Complete re-write to simplify operation


// ============================================================================
  VAR_INPUT
    ALMEnable    : BOOL; // rising edge 0->1: switch on active line module; falling edge: switch off
    PZDReceive   : WORD; // status word of active line module
  END_VAR
  
  VAR_OUTPUT
    
    PZDSend  : WORD; //command word of active line module
    ALMOn    : BOOL; //state of active line module (TRUE = On, FALSE = Off)
    ALMError : BOOL; //State of error (TRUE = error, FALSE = no error)

  END_VAR
  
  VAR
    ALMOnint    : BOOL; //state of active line module (TRUE = On, FALSE = Off)
    ALMErrorint : BOOL; //State of error (TRUE = error, FALSE = no error)
    retdint         :DINT ;
   
     END_VAR
  
//  get status of the ALM
   IF (PZDReceive AND 16#0017) = 16#0017 THEN 
      ALMOn := TRUE;
      ALMOnint := TRUE;

  ELSE 
      ALMOn := FALSE;
      ALMOnint := FALSE;
  END_IF;
  IF (PZDReceive AND 16#0008) = 16#0008 THEN
      ALMError := TRUE;
      ALMErrorint := TRUE;
  ELSE 
      ALMError := FALSE; 
      ALMErrorint := FALSE;
      //no error in status word -> reset acknowledge bit in command word
      PZDSend := PZDSend AND 16#FF7F;
 
  END_IF ;
 
 // Control of unit
    IF (ALMerrorint =  TRUE) AND (ALMENABLE  = TRUE) THEN 
      PZDSend := PZDSend OR 16#0080; //reset error
      
      RetDINT := _waittime(T#50ms); // wait 
      PZDSend := PZDSend OR 16#040A ;//AND 16#FFFE; // send disable command
      RetDINT := _waittime(T#100ms); // wait
      PZDSend := PZDSend OR 16#040b; // send enable command (see a rising edge)
     // stateNumber := 0;
END_IF ;
    IF ALMEnable = FALSE THEN
      PZDSend := PZDSend AND 16#FFFE; //disable ALM
      
  END_IF ;
  
     IF (ALMEnable = TRUE)  AND (PZDReceive = 16#0250)  THEN
      PZDSend := 16#040A ; //give ALM an off command so as to be able to give it a rising edge start command
      RetDINT := _waittime(T#200ms); // wait
  END_IF ;
  
      IF (PZDRECEIVE = 16#211)  THEN //wait for acknowledge of previous command
      RetDINT := _waittime(T#50ms); // wait
      PZDSend := PZDSend OR 16#040b ; // start alm
END_IF ;
     

    
END_FUNCTION_BLOCK

END_IMPLEMENTATION
