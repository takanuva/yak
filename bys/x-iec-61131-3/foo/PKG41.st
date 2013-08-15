INTERFACE

    USEPACKAGE cam;

    // USES header;

    // USELIB testlib;

    VAR_GLOBAL CONSTANT
           PI : REAL := 3.1415;
           ARRAY_MAX : INT := 3;
    END_VAR


    TYPE
        array1dim : ARRAY [0..ARRAY_MAX] OF INT;
        array2dim : ARRAY [0..3] OF array1dim;
        enumTrafficLight : (RED, YELLOW, GREEN);
 
        structCollection : STRUCT
            toAxisX : posaxis;
            aInStruct1dim : array1dim;
            eTrafficInStruct : enumTrafficLight;
            iCounter : INT;
            bStatus : WORD;            
        END_STRUCT;     
    
        arrayOfStruct : ARRAY [0..5] OF structCollection;
    
    END_TYPE

    // *****************************************************
    // * Variablendeklaration im INTERFACE                 *
    // *****************************************************)
    
    VAR_GLOBAL

        g_aMyArray : ARRAY [0..11] OF REAL := [3 (2(4), 2(18))];

        g_aMy2dim : array2dim;

        g_aMy1dim : array1dim; 

        g_sMyStruct : structCollection;

        g_aMyArrayOfStruct : arrayOfStruct;

        g_tMyTime : TIME := T#0d_1h_5m_17s_4ms;

        g_eMyTraffic : enumTrafficLight := RED;

        g_iMyInt : INT := -17;
    END_VAR 

    VAR_GLOBAL RETAIN 
    END_VAR

    FUNCTION_BLOCK FB_myFirst;
    FUNCTION FC_myFirst;
    PROGRAM myPRG;

END_INTERFACE

// *****************************************************
// * IMPLEMENTATION-Teil                               *
// *****************************************************

IMPLEMENTATION

    VAR_GLOBAL CONSTANT
    END_VAR

    TYPE
    END_TYPE

    VAR_GLOBAL
        g_boDigInput1 : BOOL;
    END_VAR

    VAR_GLOBAL RETAIN 
    END_VAR

    EXPRESSION xCond
        xCond := g_boDigInput1;
    END_EXPRESSION


    // *****************************************************
    // * FUNCTION_BLOCK                                    *
    // *****************************************************)

    FUNCTION_BLOCK FB_myFirst

        VAR CONSTANT 
        END_VAR

        TYPE
        END_TYPE

        VAR_INPUT
        END_VAR

        VAR_IN_OUT
        END_VAR

        VAR_OUTPUT
        END_VAR

        VAR
        END_VAR

        VAR_TEMP
        END_VAR
    
        g_eMyTraffic := GREEN; // z.B. Umschalten der Ampel


    END_FUNCTION_BLOCK 

    // *****************************************************
    // * FUNCTION                                          *
    // *****************************************************)

    FUNCTION FC_myFirst : INT

        VAR CONSTANT
        END_VAR

        TYPE
        END_TYPE

        VAR_INPUT
        END_VAR

        VAR
        END_VAR

        g_eMyTraffic := YELLOW;

        FC_myFirst := 17;

    END_FUNCTION

    // *****************************************************
    // * PROGRAM                                           *
    // *****************************************************

    PROGRAM myPRG

        VAR CONSTANT
        END_VAR

        TYPE
        END_TYPE

        VAR
            instFBMyFirst : FB_myFirst;

            retFCMyFirst : INT;
        END_VAR

        VAR_TEMP
        END_VAR    

        instFBMyFirst ();

        retFCMyFirst := FC_myFirst ();

        WAITFORCONDITION xCond WITH TRUE DO
            ;
        END_WAITFORCONDITION;

    END_PROGRAM

END_IMPLEMENTATION
