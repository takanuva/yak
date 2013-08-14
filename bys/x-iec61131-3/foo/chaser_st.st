




FUNCTION SCROLL_LEFT: INT
  (* Interface *)
  VAR_IN_OUT
    bits: INT;
  END_VAR  (* Local Vars. *)
  VAR_INPUT
    top_bit: INT;
  END_VAR

  bits := bits * 2;

  IF (bits >= top_bit)
  THEN
    bits := bits + 1 - top_bit;
  END_IF;

  SCROLL_LEFT := bits;
END_FUNCTION



FUNCTION SCROLL_RIGHT: INT
  VAR_IN_OUT
    bits: INT;
  END_VAR
  VAR_INPUT
    top_bit: INT;
  END_VAR

  IF ((bits mod 2) = 0)
    THEN top_bit := 0;
  END_IF;

  bits := bits / 2;
  bits := bits + top_bit/2;

  SCROLL_RIGHT := bits;

END_FUNCTION



PROGRAM SCROLL
  VAR_EXTERNAL
    left_bt  : BOOL;
    right_bt : BOOL;
    bits : INT;
  END_VAR
  VAR
    (* We don't suport passing external type variables to functions *)
    (* I (Mario) don't have time to fix this
     * and would like to commit something before I do go on holidays.
     * So for now, we use this workaround...
     *)
    local_bits : INT;
    direction : BOOL; (* FALSE is right, TRUE is left , we do not yet support enums *)
  END_VAR

  IF left_bt  THEN direction := FALSE; END_IF;
  IF right_bt THEN direction := TRUE;  END_IF;

  local_bits := bits;
  IF direction
    THEN bits := SCROLL_LEFT(local_bits, 16);
    ELSE bits := SCROLL_RIGHT(local_bits, 16);
  END_IF;

END_PROGRAM




CONFIGURATION config1
  VAR_GLOBAL
    bits AT %L : INT := 1;
    left_bt  AT %left  : BOOL;
    right_bt AT %right : BOOL;
   END_VAR

  PROGRAM main: SCROLL(left_bt := left_bt, right_bt := right_bt, bits := bits);
END_CONFIGURATION
