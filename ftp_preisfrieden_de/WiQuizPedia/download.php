<?php
//symlink("download.php", "app"); 

// https://stackoverflow.com/questions/11642684/how-to-set-header-to-download-file-apk-or-jar
// https://serverfault.com/questions/316814/php-serve-a-file-for-download-without-providing-the-direct-link
// We'll be outputting a apk
header('Content-type: application/vnd.android.package-archive');

// It will be called downloaded.pdf
header('Content-Disposition: attachment; filename="app-release.apk"');

// The PDF source is in original.pdf
readfile('app-release.apk');
?> 