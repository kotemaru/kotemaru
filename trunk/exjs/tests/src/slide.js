

	if (!StopIteration) {
		var StopIteration = function(){};
		StopIteration.prototype = new Error();
		StopIteration.name = "StopIteration"
		StopIteration.message = "StopIteration";
	}
	

	var slide = 

(function(){
	var func = function(item) {
		var ite = new __iterator__(item);
		ite.__originThis = this;
		return ite;
	}
	var __iterator__ = 

	function(item) {
		// Arguments variable
		

			this.item = item;
		

		// Local variable
		

			this.x = undefined;
		

			this.xadd = undefined;
		

			this.xmax = undefined;
		


		// System variable
		this.__pc = this.__step.addr_1;
		this.__sp = [];
		this.__each = {};
		this.__error = null;
		this.__errorCopy = null;
		this.__isBreak = false;
		this.__isContinue = false;
		this.__isClose = false;
		this.__isStop = false;
		this.__isReturn = false;
	}

;
	__iterator__.prototype = 

	{
		next: function() {
			return this.send();
		},
		send: function(val) {
			if (this.__isStop) throw StopIteration;

			var rv = null;
			this.__isReturn = false;
			while (!this.__isReturn) {
				var func = this.__pc;
				this.__pc = null;
				while (func == null || this.__isExitMode()) {
					if (this.__sp.length == 0) {
						if (this.__error) throw this.__error;
						if (this.__isBreak) throw "break out range.";
						if (this.__isContinue) throw "continue out range.";
						break;
					}
					func = this.__getPoperNextFunc();
				}
				if (func != null) {
					try {
						rv = func.call(this.__originThis, this, val);
					} catch (__e) {
						this.__error = __e;
					}
				} else {
					this.__isStop = true;
					this.__isReturn = true;
				}
			}
			if (this.__error) {
				throw this.__error;
			}
			return rv;
		},
		__isExitMode: function() {
			return this.__isBreak || this.__isContinue || this.__isClose || this.__error;
		},
		__getPoperNextFunc: function() {

			var spItem = this.__sp.pop();
			while (spItem == null && this.__sp.length>0) {
				spItem = this.__sp.pop();
			}
			if (spItem == null) return null;

			if (this.__error && spItem.hasCatch) {
				this.__errorCopy = this.__error;
				this.__error = null;
				return spItem._catch;
			}
			if (this.__isBreak && spItem.isBreak) {
				this.__isBreak = false;
				return spItem._break;
			}
			if (this.__isContinue && spItem.isContinue) {
				this.__isContinue = false;
				return spItem._continue;
			}
			if (spItem.hasFinally) return spItem._finally;
			if (this.__isClose) return null;
			return spItem.next;
		},
		"throw": function(err) {
			this.__error = err;
			this.next();
		},
		close: function() {
			this.__isClose = true;
			if (!this.__isStop) this.next();
		},

		__step: {
			addr_1: function(__This, __var) {
		var __isNormally = false;
		try {
  

					with (__This) {
						 x = item.offsetLeft;
						

					}
					__This.__pc = __This.__step.addr_2;
					__isNormally = true;
  

		} finally {
			__This.__isReturn = !__isNormally;
		}
	},
addr_2: function(__This, __var) {
		var __isNormally = false;
		try {
  

					with (__This) {
						 xadd = 10;
						

					}
					__This.__pc = __This.__step.addr_3;
					__isNormally = true;
  

		} finally {
			__This.__isReturn = !__isNormally;
		}
	},
addr_3: function(__This, __var) {
		var __isNormally = false;
		try {
  

					with (__This) {
						 xmax = item.offsetParent.offsetWidth-item.offsetWidth;
						

					}
					__This.__pc = __This.__step.addr_4;
					__isNormally = true;
  

		} finally {
			__This.__isReturn = !__isNormally;
		}
	},
addr_4: function(__This, __var) {
		with (__This) {
			var __spItem = {
				debug:"while",
				isBreak: true,
				isContinue: true,
				_break: __This.__step.addr_5,
				_continue: __This.__step.addr_4
			};
			if (true) {
				__This.__pc = __This.__step.addr_4_1;
				__spItem.next = __spItem._continue;
			} else {
				__spItem.next = __spItem._break;
			}
			__This.__sp.push(__spItem);
		}
	},
	addr_4_1: function(__This, __var) {
		with (__This) {
			__This.__pc = __This.__step.addr_4y_1;
			__This.__isReturn = true;
			return  sleep(__This, 50);
		}
	},
	// -- YIELD --
	addr_4y_1: function(__This, __var) {
		with (__This) {
			
		

		__var
	;
;
			

		}
		__This.__pc = __This.__step.addr_4_2;
	},
addr_4_2: function(__This, __var) {
		var __isNormally = false;
		try {
  

					with (__This) {
						
		{
			x += xadd;
			if (x < 0 || x>xmax) xadd = -xadd;
			item.style.left = x+"px";
		}

					}
					__This.__pc = __This.__step.addr_4_3;
					__isNormally = true;
  

		} finally {
			__This.__isReturn = !__isNormally;
		}
	},


			dummy: null
		}
	}

;
	return func;
})()

;
		
function sleep(_this, wait) {
	setTimeout(function(){
		try{_this.next();}catch(e){
			if (e != StopIteration) alert(e.message);
		}
	}, wait);
}