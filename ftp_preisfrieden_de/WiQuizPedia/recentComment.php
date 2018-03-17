<?php
$fileName='./z153d7a58b3a3e898fcbdd04c462af308414bd09d.trc';
 header('Content-Type:text/plain'); 
 
 $msg = file_get_contents($fileName, NULL, NULL, -1000 , 1000);
 $msg = str_replace( "2018-" , " ", $msg);
 $msg = preg_replace( '/:[0-9]*\s?:\s([0-9.]+)\s[0-9.]+/' , " $1 " , $msg);
 
 # remove incomplete lines ..
 # sort reverse
 $array = preg_split ('/$\R?^/m', $msg);
 reset($array);
 unset($array[0]);
 $array = array_reverse($array);
 
 $msg=join("\n", $array);
 
	echo $msg;
?> 