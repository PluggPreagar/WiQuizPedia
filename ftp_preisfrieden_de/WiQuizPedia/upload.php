<?php
    $message = isset($_POST['stacktrace']) ? $_POST['stacktrace'] : "";
    $errorInfo= isset($_POST['errorInfo']) ? $_POST['errorInfo'] : "";


        $random = rand(1000,9999);
        $version = $_POST['package_version'];
        $package = $_POST['package_name'];
        $filenameGen = $package."-trace-".$version."-".time()."-".$random;

    $filename = isset($_POST['filename']) ? $_POST['filename'] : $filenameGen ;

    $filename = "z".sha1($message).".trc" ;

    if (!ereg('^[-a-zA-Z0-9_. ]+$', $filename) || $message == ""){
		file_put_contents("upload.log","Err:".filenameGen. "\n" , FILE_APPEND);
        die("This script is used to log debug data. Please send the "
                . "logging message and a filename as POST variables.");
    }
    if(!file_exists($filename)) {
		file_put_contents($filename, $message . "\n", FILE_APPEND);
	} elseif (filesize($filename) > 50000) {
		$handle = fopen($filename, 'r+');
		ftruncate($handle, 20000);
		fclose($handle);
	}
    file_put_contents($filename, date("Y-m-d H:i:s",time())." : ".$errorInfo. "\n", FILE_APPEND);
    if ( "Comment" != $message && "CommentDetail" == $message) {file_put_contents("upload.log", time().": ".$filename."  ".substr($message,0,100). "\n" , FILE_APPEND);}

	$filename="";
	if (ereg(' --- (B:|Bug) ', $errorInfo)) {$filename="z_bug.trc";}
	if (ereg(' --- (I:|Idee) ', $errorInfo)) {$filename="z_idea.trc";}
	if ( "" != $filename && "CommentDetail" == $message){
		if(!file_exists($filename)) {
		} elseif (filesize($filename) > 50000) {
			$handle = fopen($filename, 'r+');
			ftruncate($handle, 20000);
			fclose($handle);
		}
		file_put_contents($filename, date("Y-m-d H:i:s",time())." : ".$errorInfo. "\n", FILE_APPEND);
	}
?>
