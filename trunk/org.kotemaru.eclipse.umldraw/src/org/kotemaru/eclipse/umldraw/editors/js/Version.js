

function Version(){this.initialize.apply(this, arguments)};
(function(_class){
	_class.CURRENT = "0.0.1";
	
	_class.getBrowserName = function() {
		var brow = "";
		for (var k in $.browser) {
			if (k != "version" && $.browser[k]) brow = brow+"."+k;
		}
		return brow.replace(/^[.]/,"")+"/"+$.browser.version;
	}
	
})(Version);


//EOF
