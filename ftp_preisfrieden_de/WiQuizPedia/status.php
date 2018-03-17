<?php

    # https://stackoverflow.com/questions/2667065/sort-files-by-date-in-php
	function cmpByDate($a, $b){
		return filemtime($a)<filemtime($b);
	}

    $myDirectory = opendir(".");
    while($entryName = readdir($myDirectory)) {
        $dirArray[] = $entryName;
    }
    closedir($myDirectory);
    $indexCount = count($dirArray);

	#sort($dirArray);
    usort($dirArray,"cmpByDate");

    print("<TABLE border=1 cellpadding=5 cellspacing=0 \n");
    print("<TR><TH>Filename</TH><TH>Filetype</th><th>FileTime</TH><th>FileTime</TH><th>Filesize</TH></TR>\n");
    for($index=0; $index < $indexCount; $index++) {
        if ((substr("$dirArray[$index]", 0, 1) != ".") 
                && (strrpos("$dirArray[$index]", ".trc") != false)){ 
            print("<TR><TD>");
            print("<a href=\"$dirArray[$index]\">$dirArray[$index]</a>");
            print("</TD><TD>");
            print(filetype($dirArray[$index]));
            print("</TD><TD>");
            print(date("F d Y H:i:s",fileatime($dirArray[$index])));
            print("</TD><TD>");
            print(date("F d Y H:i:s",filemtime($dirArray[$index])));
            print("</TD><TD>");
            print(filesize($dirArray[$index]));
            print("</TD></TR>\n");
        }
    }
    print("</TABLE>\n");
?>