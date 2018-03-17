<?php
$fileName='./153d7a58b3a3e898fcbdd04c462af308414bd09d.trc';
 header('Content-Type:text/plain'); 
echo preg_replace( '/:[0-9]*:\s/' , " : " , str_replace( "2018-" , " ", file_get_contents($fileName, NULL, NULL, max(0, filesize($filename)-1000) , 1000)));
?> 