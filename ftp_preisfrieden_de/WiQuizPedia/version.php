<?php
	header('Content-Type:text/plain'); 
	print(date("ymdHi",filemtime('app-release.apk')));
?>