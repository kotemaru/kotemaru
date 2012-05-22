BEGIN {
	x = 0;
	y = 0;
	cmd = "convert +append ";
	print "var CHIPS32_MAPPING = {";
}

$1 != ""{
	fname = $1;
	name = $1;
	gsub(/^[a-z]*\//,"",name);
	gsub(/[.]png$/,"",name);

	if (x>=8) {
		cmd = sprintf("%s /RAM/tmp%d.png\n",cmd,y);
		system(cmd);
		cmd = "convert +append ";
		x=0; y++;
	}

	cmd = sprintf("%s %s",cmd,fname);

	printf("\"%s\": {x:%d, y:%d, w:32,h:32},\n", name, x*32,y*32);
	x++;
}

END {
	print "};";

	{
		cmd = sprintf("%s /RAM/tmp%d.png\n",cmd,y);
		system(cmd);
		x=0; y++;
	}

	cmd = "convert -append "
	for (i=0; i<y; i++) {
		cmd = sprintf("%s /RAM/tmp%d.png",cmd,i);
	}
	cmd = sprintf("%s /RAM/all32.png\n",cmd);
	system(cmd);
}
