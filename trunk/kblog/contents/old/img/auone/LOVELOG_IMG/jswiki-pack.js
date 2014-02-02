if (!Array.prototype.indexOf)
{
  Array.prototype.indexOf = function(elt /*, from*/)
  {
    var len = this.length;

    var from = Number(arguments[1]) || 0;
    from = (from < 0)
         ? Math.ceil(from)
         : Math.floor(from);
    if (from < 0)
      from += len;

    for (; from < len; from++)
    {
      if (from in this &&
          this[from] === elt)
        return from;
    }
    return -1;
  };
}
/*
	Cross-Browser Split 0.3
	By Steven Levithan <http://stevenlevithan.com>
	MIT license
	Provides a consistent cross-browser, ECMA-262 v3 compliant split method
*/

String.prototype._$$split = String.prototype._$$split || String.prototype.split;

String.prototype.split = function (s /* separator */, limit) {
	// if separator is not a regex, use the native split method
	if (!(s instanceof RegExp))
		return String.prototype._$$split.apply(this, arguments);

	var	flags = (s.global ? "g" : "") + (s.ignoreCase ? "i" : "") + (s.multiline ? "m" : ""),
		s2 = new RegExp("^" + s.source + "$", flags),
		output = [],
		origLastIndex = s.lastIndex,
		lastLastIndex = 0,
		i = 0, match, lastLength;

	/* behavior for limit: if it's...
	- undefined: no limit
	- NaN or zero: return an empty array
	- a positive number: use limit after dropping any decimal
	- a negative number: no limit
	- other: type-convert, then use the above rules
	*/
	if (limit === undefined || +limit < 0) {
		limit = false;
	} else {
		limit = Math.floor(+limit);
		if (!limit)
			return [];
	}

	if (s.global)
		s.lastIndex = 0;
	else
		s = new RegExp(s.source, "g" + flags);

	while ((!limit || i++ <= limit) && (match = s.exec(this))) {
		var emptyMatch = !match[0].length;

		// Fix IE's infinite-loop-resistant but incorrect lastIndex
		if (emptyMatch && s.lastIndex > match.index)
			s.lastIndex--;

		if (s.lastIndex > lastLastIndex) {
			// Fix browsers whose exec methods don't consistently return undefined for non-participating capturing groups
			if (match.length > 1) {
				match[0].replace(s2, function () {
					for (var j = 1; j < arguments.length - 2; j++) {
						if (arguments[j] === undefined)
							match[j] = undefined;
					}
				});
			}

			output = output.concat(this.slice(lastLastIndex, match.index));
			if (1 < match.length && match.index < this.length)
				output = output.concat(match.slice(1));
			lastLength = match[0].length; // only needed if s.lastIndex === this.length
			lastLastIndex = s.lastIndex;
		}

		if (emptyMatch)
			s.lastIndex++; // avoid an infinite loop
	}

	// since this uses test(), output must be generated before restoring lastIndex
	output = lastLastIndex === this.length ?
		(s.test("") && !lastLength ? output : output.concat("")) :
		(limit ? output : output.concat(this.slice(lastLastIndex)));
	s.lastIndex = origLastIndex; // only needed if s.global, else we're working with a copy of the regex
	return output;
};

/* This notice must be untouched at all times.

wz_jsgraphics.js    v. 3.03
The latest version is available at
http://www.walterzorn.com
or http://www.devira.com
or http://www.walterzorn.de

Copyright (c) 2002-2004 Walter Zorn. All rights reserved.
Created 3. 11. 2002 by Walter Zorn (Web: http://www.walterzorn.com )
Last modified: 28. 1. 2008

Performance optimizations for Internet Explorer
by Thomas Frank and John Holdsworth.
fillPolygon method implemented by Matthieu Haller.

High Performance JavaScript Graphics Library.
Provides methods
- to draw lines, rectangles, ellipses, polygons
	with specifiable line thickness,
- to fill rectangles, polygons, ellipses and arcs
- to draw text.
NOTE: Operations, functions and branching have rather been optimized
to efficiency and speed than to shortness of source code.

LICENSE: LGPL

This library is free software; you can redistribute it and/or
modify it under the terms of the GNU Lesser General Public
License (LGPL) as published by the Free Software Foundation; either
version 2.1 of the License, or (at your option) any later version.

This library is distributed in the hope that it will be useful,
but WITHOUT ANY WARRANTY; without even the implied warranty of
MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the GNU
Lesser General Public License for more details.

You should have received a copy of the GNU Lesser General Public
License along with this library; if not, write to the Free Software
Foundation, Inc., 59 Temple Place, Suite 330, Boston, MA  02111-1307  USA,
or see http://www.gnu.org/copyleft/lesser.html
*/


var jg_ok, jg_ie, jg_fast, jg_dom, jg_moz;


function _chkDHTM(x, i)
{
	x = document.body || null;
	jg_ie = x && typeof x.insertAdjacentHTML != "undefined" && document.createElement;
	jg_dom = (x && !jg_ie &&
		typeof x.appendChild != "undefined" &&
		typeof document.createRange != "undefined" &&
		typeof (i = document.createRange()).setStartBefore != "undefined" &&
		typeof i.createContextualFragment != "undefined");
	jg_fast = jg_ie && document.all && !window.opera;
	jg_moz = jg_dom && typeof x.style.MozOpacity != "undefined";
	jg_ok = !!(jg_ie || jg_dom);
}

function _pntCnvDom()
{
	var x = this.wnd.document.createRange();
	x.setStartBefore(this.cnv);
	x = x.createContextualFragment(jg_fast? this._htmRpc() : this.htm);
	if(this.cnv) this.cnv.appendChild(x);
	this.htm = "";
}

function _pntCnvIe()
{
	if(this.cnv) this.cnv.insertAdjacentHTML("BeforeEnd", jg_fast? this._htmRpc() : this.htm);
	this.htm = "";
}

function _pntDoc()
{
	this.wnd.document.write(jg_fast? this._htmRpc() : this.htm);
	this.htm = '';
}

function _pntN()
{
	;
}

function _mkDiv(x, y, w, h)
{
	this.htm += '<div style="position:absolute;'+
		'left:' + x + 'px;'+
		'top:' + y + 'px;'+
		'width:' + w + 'px;'+
		'height:' + h + 'px;'+
		'clip:rect(0,'+w+'px,'+h+'px,0);'+
		'background-color:' + this.color +
		(!jg_moz? ';overflow:hidden' : '')+
		';"><\/div>';
}

function _mkDivIe(x, y, w, h)
{
	this.htm += '%%'+this.color+';'+x+';'+y+';'+w+';'+h+';';
}

function _mkDivPrt(x, y, w, h)
{
	this.htm += '<div style="position:absolute;'+
		'border-left:' + w + 'px solid ' + this.color + ';'+
		'left:' + x + 'px;'+
		'top:' + y + 'px;'+
		'width:0px;'+
		'height:' + h + 'px;'+
		'clip:rect(0,'+w+'px,'+h+'px,0);'+
		'background-color:' + this.color +
		(!jg_moz? ';overflow:hidden' : '')+
		';"><\/div>';
}

var _regex =  /%%([^;]+);([^;]+);([^;]+);([^;]+);([^;]+);/g;
function _htmRpc()
{
	return this.htm.replace(
		_regex,
		'<div style="overflow:hidden;position:absolute;background-color:'+
		'$1;left:$2;top:$3;width:$4;height:$5"></div>\n');
}

function _htmPrtRpc()
{
	return this.htm.replace(
		_regex,
		'<div style="overflow:hidden;position:absolute;background-color:'+
		'$1;left:$2;top:$3;width:$4;height:$5;border-left:$4px solid $1"></div>\n');
}

function _mkLin(x1, y1, x2, y2)
{
	if(x1 > x2)
	{
		var _x2 = x2;
		var _y2 = y2;
		x2 = x1;
		y2 = y1;
		x1 = _x2;
		y1 = _y2;
	}
	var dx = x2-x1, dy = Math.abs(y2-y1),
	x = x1, y = y1,
	yIncr = (y1 > y2)? -1 : 1;

	if(dx >= dy)
	{
		var pr = dy<<1,
		pru = pr - (dx<<1),
		p = pr-dx,
		ox = x;
		while(dx > 0)
		{--dx;
			++x;
			if(p > 0)
			{
				this._mkDiv(ox, y, x-ox, 1);
				y += yIncr;
				p += pru;
				ox = x;
			}
			else p += pr;
		}
		this._mkDiv(ox, y, x2-ox+1, 1);
	}

	else
	{
		var pr = dx<<1,
		pru = pr - (dy<<1),
		p = pr-dy,
		oy = y;
		if(y2 <= y1)
		{
			while(dy > 0)
			{--dy;
				if(p > 0)
				{
					this._mkDiv(x++, y, 1, oy-y+1);
					y += yIncr;
					p += pru;
					oy = y;
				}
				else
				{
					y += yIncr;
					p += pr;
				}
			}
			this._mkDiv(x2, y2, 1, oy-y2+1);
		}
		else
		{
			while(dy > 0)
			{--dy;
				y += yIncr;
				if(p > 0)
				{
					this._mkDiv(x++, oy, 1, y-oy);
					p += pru;
					oy = y;
				}
				else p += pr;
			}
			this._mkDiv(x2, oy, 1, y2-oy+1);
		}
	}
}

function _mkLin2D(x1, y1, x2, y2)
{
	if(x1 > x2)
	{
		var _x2 = x2;
		var _y2 = y2;
		x2 = x1;
		y2 = y1;
		x1 = _x2;
		y1 = _y2;
	}
	var dx = x2-x1, dy = Math.abs(y2-y1),
	x = x1, y = y1,
	yIncr = (y1 > y2)? -1 : 1;

	var s = this.stroke;
	if(dx >= dy)
	{
		if(dx > 0 && s-3 > 0)
		{
			var _s = (s*dx*Math.sqrt(1+dy*dy/(dx*dx))-dx-(s>>1)*dy) / dx;
			_s = (!(s-4)? Math.ceil(_s) : Math.round(_s)) + 1;
		}
		else var _s = s;
		var ad = Math.ceil(s/2);

		var pr = dy<<1,
		pru = pr - (dx<<1),
		p = pr-dx,
		ox = x;
		while(dx > 0)
		{--dx;
			++x;
			if(p > 0)
			{
				this._mkDiv(ox, y, x-ox+ad, _s);
				y += yIncr;
				p += pru;
				ox = x;
			}
			else p += pr;
		}
		this._mkDiv(ox, y, x2-ox+ad+1, _s);
	}

	else
	{
		if(s-3 > 0)
		{
			var _s = (s*dy*Math.sqrt(1+dx*dx/(dy*dy))-(s>>1)*dx-dy) / dy;
			_s = (!(s-4)? Math.ceil(_s) : Math.round(_s)) + 1;
		}
		else var _s = s;
		var ad = Math.round(s/2);

		var pr = dx<<1,
		pru = pr - (dy<<1),
		p = pr-dy,
		oy = y;
		if(y2 <= y1)
		{
			++ad;
			while(dy > 0)
			{--dy;
				if(p > 0)
				{
					this._mkDiv(x++, y, _s, oy-y+ad);
					y += yIncr;
					p += pru;
					oy = y;
				}
				else
				{
					y += yIncr;
					p += pr;
				}
			}
			this._mkDiv(x2, y2, _s, oy-y2+ad);
		}
		else
		{
			while(dy > 0)
			{--dy;
				y += yIncr;
				if(p > 0)
				{
					this._mkDiv(x++, oy, _s, y-oy+ad);
					p += pru;
					oy = y;
				}
				else p += pr;
			}
			this._mkDiv(x2, oy, _s, y2-oy+ad+1);
		}
	}
}

function _mkLinDott(x1, y1, x2, y2)
{
	if(x1 > x2)
	{
		var _x2 = x2;
		var _y2 = y2;
		x2 = x1;
		y2 = y1;
		x1 = _x2;
		y1 = _y2;
	}
	var dx = x2-x1, dy = Math.abs(y2-y1),
	x = x1, y = y1,
	yIncr = (y1 > y2)? -1 : 1,
	drw = true;
	if(dx >= dy)
	{
		var pr = dy<<1,
		pru = pr - (dx<<1),
		p = pr-dx;
		while(dx > 0)
		{--dx;
			if(drw) this._mkDiv(x, y, 1, 1);
			drw = !drw;
			if(p > 0)
			{
				y += yIncr;
				p += pru;
			}
			else p += pr;
			++x;
		}
	}
	else
	{
		var pr = dx<<1,
		pru = pr - (dy<<1),
		p = pr-dy;
		while(dy > 0)
		{--dy;
			if(drw) this._mkDiv(x, y, 1, 1);
			drw = !drw;
			y += yIncr;
			if(p > 0)
			{
				++x;
				p += pru;
			}
			else p += pr;
		}
	}
	if(drw) this._mkDiv(x, y, 1, 1);
}

function _mkOv(left, top, width, height)
{
	var a = (++width)>>1, b = (++height)>>1,
	wod = width&1, hod = height&1,
	cx = left+a, cy = top+b,
	x = 0, y = b,
	ox = 0, oy = b,
	aa2 = (a*a)<<1, aa4 = aa2<<1, bb2 = (b*b)<<1, bb4 = bb2<<1,
	st = (aa2>>1)*(1-(b<<1)) + bb2,
	tt = (bb2>>1) - aa2*((b<<1)-1),
	w, h;
	while(y > 0)
	{
		if(st < 0)
		{
			st += bb2*((x<<1)+3);
			tt += bb4*(++x);
		}
		else if(tt < 0)
		{
			st += bb2*((x<<1)+3) - aa4*(y-1);
			tt += bb4*(++x) - aa2*(((y--)<<1)-3);
			w = x-ox;
			h = oy-y;
			if((w&2) && (h&2))
			{
				this._mkOvQds(cx, cy, x-2, y+2, 1, 1, wod, hod);
				this._mkOvQds(cx, cy, x-1, y+1, 1, 1, wod, hod);
			}
			else this._mkOvQds(cx, cy, x-1, oy, w, h, wod, hod);
			ox = x;
			oy = y;
		}
		else
		{
			tt -= aa2*((y<<1)-3);
			st -= aa4*(--y);
		}
	}
	w = a-ox+1;
	h = (oy<<1)+hod;
	y = cy-oy;
	this._mkDiv(cx-a, y, w, h);
	this._mkDiv(cx+ox+wod-1, y, w, h);
}

function _mkOv2D(left, top, width, height)
{
	var s = this.stroke;
	width += s+1;
	height += s+1;
	var a = width>>1, b = height>>1,
	wod = width&1, hod = height&1,
	cx = left+a, cy = top+b,
	x = 0, y = b,
	aa2 = (a*a)<<1, aa4 = aa2<<1, bb2 = (b*b)<<1, bb4 = bb2<<1,
	st = (aa2>>1)*(1-(b<<1)) + bb2,
	tt = (bb2>>1) - aa2*((b<<1)-1);

	if(s-4 < 0 && (!(s-2) || width-51 > 0 && height-51 > 0))
	{
		var ox = 0, oy = b,
		w, h,
		pxw;
		while(y > 0)
		{
			if(st < 0)
			{
				st += bb2*((x<<1)+3);
				tt += bb4*(++x);
			}
			else if(tt < 0)
			{
				st += bb2*((x<<1)+3) - aa4*(y-1);
				tt += bb4*(++x) - aa2*(((y--)<<1)-3);
				w = x-ox;
				h = oy-y;

				if(w-1)
				{
					pxw = w+1+(s&1);
					h = s;
				}
				else if(h-1)
				{
					pxw = s;
					h += 1+(s&1);
				}
				else pxw = h = s;
				this._mkOvQds(cx, cy, x-1, oy, pxw, h, wod, hod);
				ox = x;
				oy = y;
			}
			else
			{
				tt -= aa2*((y<<1)-3);
				st -= aa4*(--y);
			}
		}
		this._mkDiv(cx-a, cy-oy, s, (oy<<1)+hod);
		this._mkDiv(cx+a+wod-s, cy-oy, s, (oy<<1)+hod);
	}

	else
	{
		var _a = (width-(s<<1))>>1,
		_b = (height-(s<<1))>>1,
		_x = 0, _y = _b,
		_aa2 = (_a*_a)<<1, _aa4 = _aa2<<1, _bb2 = (_b*_b)<<1, _bb4 = _bb2<<1,
		_st = (_aa2>>1)*(1-(_b<<1)) + _bb2,
		_tt = (_bb2>>1) - _aa2*((_b<<1)-1),

		pxl = new Array(),
		pxt = new Array(),
		_pxb = new Array();
		pxl[0] = 0;
		pxt[0] = b;
		_pxb[0] = _b-1;
		while(y > 0)
		{
			if(st < 0)
			{
				pxl[pxl.length] = x;
				pxt[pxt.length] = y;
				st += bb2*((x<<1)+3);
				tt += bb4*(++x);
			}
			else if(tt < 0)
			{
				pxl[pxl.length] = x;
				st += bb2*((x<<1)+3) - aa4*(y-1);
				tt += bb4*(++x) - aa2*(((y--)<<1)-3);
				pxt[pxt.length] = y;
			}
			else
			{
				tt -= aa2*((y<<1)-3);
				st -= aa4*(--y);
			}

			if(_y > 0)
			{
				if(_st < 0)
				{
					_st += _bb2*((_x<<1)+3);
					_tt += _bb4*(++_x);
					_pxb[_pxb.length] = _y-1;
				}
				else if(_tt < 0)
				{
					_st += _bb2*((_x<<1)+3) - _aa4*(_y-1);
					_tt += _bb4*(++_x) - _aa2*(((_y--)<<1)-3);
					_pxb[_pxb.length] = _y-1;
				}
				else
				{
					_tt -= _aa2*((_y<<1)-3);
					_st -= _aa4*(--_y);
					_pxb[_pxb.length-1]--;
				}
			}
		}

		var ox = -wod, oy = b,
		_oy = _pxb[0],
		l = pxl.length,
		w, h;
		for(var i = 0; i < l; i++)
		{
			if(typeof _pxb[i] != "undefined")
			{
				if(_pxb[i] < _oy || pxt[i] < oy)
				{
					x = pxl[i];
					this._mkOvQds(cx, cy, x, oy, x-ox, oy-_oy, wod, hod);
					ox = x;
					oy = pxt[i];
					_oy = _pxb[i];
				}
			}
			else
			{
				x = pxl[i];
				this._mkDiv(cx-x, cy-oy, 1, (oy<<1)+hod);
				this._mkDiv(cx+ox+wod, cy-oy, 1, (oy<<1)+hod);
				ox = x;
				oy = pxt[i];
			}
		}
		this._mkDiv(cx-a, cy-oy, 1, (oy<<1)+hod);
		this._mkDiv(cx+ox+wod, cy-oy, 1, (oy<<1)+hod);
	}
}

function _mkOvDott(left, top, width, height)
{
	var a = (++width)>>1, b = (++height)>>1,
	wod = width&1, hod = height&1, hodu = hod^1,
	cx = left+a, cy = top+b,
	x = 0, y = b,
	aa2 = (a*a)<<1, aa4 = aa2<<1, bb2 = (b*b)<<1, bb4 = bb2<<1,
	st = (aa2>>1)*(1-(b<<1)) + bb2,
	tt = (bb2>>1) - aa2*((b<<1)-1),
	drw = true;
	while(y > 0)
	{
		if(st < 0)
		{
			st += bb2*((x<<1)+3);
			tt += bb4*(++x);
		}
		else if(tt < 0)
		{
			st += bb2*((x<<1)+3) - aa4*(y-1);
			tt += bb4*(++x) - aa2*(((y--)<<1)-3);
		}
		else
		{
			tt -= aa2*((y<<1)-3);
			st -= aa4*(--y);
		}
		if(drw && y >= hodu) this._mkOvQds(cx, cy, x, y, 1, 1, wod, hod);
		drw = !drw;
	}
}

function _mkRect(x, y, w, h)
{
	var s = this.stroke;
	this._mkDiv(x, y, w, s);
	this._mkDiv(x+w, y, s, h);
	this._mkDiv(x, y+h, w+s, s);
	this._mkDiv(x, y+s, s, h-s);
}

function _mkRectDott(x, y, w, h)
{
	this.drawLine(x, y, x+w, y);
	this.drawLine(x+w, y, x+w, y+h);
	this.drawLine(x, y+h, x+w, y+h);
	this.drawLine(x, y, x, y+h);
}

function jsgFont()
{
	this.PLAIN = 'font-weight:normal;';
	this.BOLD = 'font-weight:bold;';
	this.ITALIC = 'font-style:italic;';
	this.ITALIC_BOLD = this.ITALIC + this.BOLD;
	this.BOLD_ITALIC = this.ITALIC_BOLD;
}
var Font = new jsgFont();

function jsgStroke()
{
	this.DOTTED = -1;
}
var Stroke = new jsgStroke();

function jsGraphics(cnv, wnd)
{
	this.setColor = function(x)
	{
		this.color = x.toLowerCase();
	};

	this.setStroke = function(x)
	{
		this.stroke = x;
		if(!(x+1))
		{
			this.drawLine = _mkLinDott;
			this._mkOv = _mkOvDott;
			this.drawRect = _mkRectDott;
		}
		else if(x-1 > 0)
		{
			this.drawLine = _mkLin2D;
			this._mkOv = _mkOv2D;
			this.drawRect = _mkRect;
		}
		else
		{
			this.drawLine = _mkLin;
			this._mkOv = _mkOv;
			this.drawRect = _mkRect;
		}
	};

	this.setPrintable = function(arg)
	{
		this.printable = arg;
		if(jg_fast)
		{
			this._mkDiv = _mkDivIe;
			this._htmRpc = arg? _htmPrtRpc : _htmRpc;
		}
		else this._mkDiv = arg? _mkDivPrt : _mkDiv;
	};

	this.setFont = function(fam, sz, sty)
	{
		this.ftFam = fam;
		this.ftSz = sz;
		this.ftSty = sty || Font.PLAIN;
	};

	this.drawPolyline = this.drawPolyLine = function(x, y)
	{
		for (var i=x.length - 1; i;)
		{--i;
			this.drawLine(x[i], y[i], x[i+1], y[i+1]);
		}
	};

	this.fillRect = function(x, y, w, h)
	{
		this._mkDiv(x, y, w, h);
	};

	this.drawPolygon = function(x, y)
	{
		this.drawPolyline(x, y);
		this.drawLine(x[x.length-1], y[x.length-1], x[0], y[0]);
	};

	this.drawEllipse = this.drawOval = function(x, y, w, h)
	{
		this._mkOv(x, y, w, h);
	};

	this.fillEllipse = this.fillOval = function(left, top, w, h)
	{
		var a = w>>1, b = h>>1,
		wod = w&1, hod = h&1,
		cx = left+a, cy = top+b,
		x = 0, y = b, oy = b,
		aa2 = (a*a)<<1, aa4 = aa2<<1, bb2 = (b*b)<<1, bb4 = bb2<<1,
		st = (aa2>>1)*(1-(b<<1)) + bb2,
		tt = (bb2>>1) - aa2*((b<<1)-1),
		xl, dw, dh;
		if(w) while(y > 0)
		{
			if(st < 0)
			{
				st += bb2*((x<<1)+3);
				tt += bb4*(++x);
			}
			else if(tt < 0)
			{
				st += bb2*((x<<1)+3) - aa4*(y-1);
				xl = cx-x;
				dw = (x<<1)+wod;
				tt += bb4*(++x) - aa2*(((y--)<<1)-3);
				dh = oy-y;
				this._mkDiv(xl, cy-oy, dw, dh);
				this._mkDiv(xl, cy+y+hod, dw, dh);
				oy = y;
			}
			else
			{
				tt -= aa2*((y<<1)-3);
				st -= aa4*(--y);
			}
		}
		this._mkDiv(cx-a, cy-oy, w, (oy<<1)+hod);
	};

	this.fillArc = function(iL, iT, iW, iH, fAngA, fAngZ)
	{
		var a = iW>>1, b = iH>>1,
		iOdds = (iW&1) | ((iH&1) << 16),
		cx = iL+a, cy = iT+b,
		x = 0, y = b, ox = x, oy = y,
		aa2 = (a*a)<<1, aa4 = aa2<<1, bb2 = (b*b)<<1, bb4 = bb2<<1,
		st = (aa2>>1)*(1-(b<<1)) + bb2,
		tt = (bb2>>1) - aa2*((b<<1)-1),
		// Vars for radial boundary lines
		xEndA, yEndA, xEndZ, yEndZ,
		iSects = (1 << (Math.floor((fAngA %= 360.0)/180.0) << 3))
				| (2 << (Math.floor((fAngZ %= 360.0)/180.0) << 3))
				| ((fAngA >= fAngZ) << 16),
		aBndA = new Array(b+1), aBndZ = new Array(b+1);
		
		// Set up radial boundary lines
		fAngA *= Math.PI/180.0;
		fAngZ *= Math.PI/180.0;
		xEndA = cx+Math.round(a*Math.cos(fAngA));
		yEndA = cy+Math.round(-b*Math.sin(fAngA));
		_mkLinVirt(aBndA, cx, cy, xEndA, yEndA);
		xEndZ = cx+Math.round(a*Math.cos(fAngZ));
		yEndZ = cy+Math.round(-b*Math.sin(fAngZ));
		_mkLinVirt(aBndZ, cx, cy, xEndZ, yEndZ);

		while(y > 0)
		{
			if(st < 0) // Advance x
			{
				st += bb2*((x<<1)+3);
				tt += bb4*(++x);
			}
			else if(tt < 0) // Advance x and y
			{
				st += bb2*((x<<1)+3) - aa4*(y-1);
				ox = x;
				tt += bb4*(++x) - aa2*(((y--)<<1)-3);
				this._mkArcDiv(ox, y, oy, cx, cy, iOdds, aBndA, aBndZ, iSects);
				oy = y;
			}
			else // Advance y
			{
				tt -= aa2*((y<<1)-3);
				st -= aa4*(--y);
				if(y && (aBndA[y] != aBndA[y-1] || aBndZ[y] != aBndZ[y-1]))
				{
					this._mkArcDiv(x, y, oy, cx, cy, iOdds, aBndA, aBndZ, iSects);
					ox = x;
					oy = y;
				}
			}
		}
		this._mkArcDiv(x, 0, oy, cx, cy, iOdds, aBndA, aBndZ, iSects);
		if(iOdds >> 16) // Odd height
		{
			if(iSects >> 16) // Start-angle > end-angle
			{
				var xl = (yEndA <= cy || yEndZ > cy)? (cx - x) : cx;
				this._mkDiv(xl, cy, x + cx - xl + (iOdds & 0xffff), 1);
			}
			else if((iSects & 0x01) && yEndZ > cy)
				this._mkDiv(cx - x, cy, x, 1);
		}
	};

/* fillPolygon method, implemented by Matthieu Haller.
This javascript function is an adaptation of the gdImageFilledPolygon for Walter Zorn lib.
C source of GD 1.8.4 found at http://www.boutell.com/gd/

THANKS to Kirsten Schulz for the polygon fixes!

The intersection finding technique of this code could be improved
by remembering the previous intertersection, and by using the slope.
That could help to adjust intersections to produce a nice
interior_extrema. */
	this.fillPolygon = function(array_x, array_y)
	{
		var i;
		var y;
		var miny, maxy;
		var x1, y1;
		var x2, y2;
		var ind1, ind2;
		var ints;

		var n = array_x.length;
		if(!n) return;

		miny = array_y[0];
		maxy = array_y[0];
		for(i = 1; i < n; i++)
		{
			if(array_y[i] < miny)
				miny = array_y[i];

			if(array_y[i] > maxy)
				maxy = array_y[i];
		}
		for(y = miny; y <= maxy; y++)
		{
			var polyInts = new Array();
			ints = 0;
			for(i = 0; i < n; i++)
			{
				if(!i)
				{
					ind1 = n-1;
					ind2 = 0;
				}
				else
				{
					ind1 = i-1;
					ind2 = i;
				}
				y1 = array_y[ind1];
				y2 = array_y[ind2];
				if(y1 < y2)
				{
					x1 = array_x[ind1];
					x2 = array_x[ind2];
				}
				else if(y1 > y2)
				{
					y2 = array_y[ind1];
					y1 = array_y[ind2];
					x2 = array_x[ind1];
					x1 = array_x[ind2];
				}
				else continue;

				 //  Modified 11. 2. 2004 Walter Zorn
				if((y >= y1) && (y < y2))
					polyInts[ints++] = Math.round((y-y1) * (x2-x1) / (y2-y1) + x1);

				else if((y == maxy) && (y > y1) && (y <= y2))
					polyInts[ints++] = Math.round((y-y1) * (x2-x1) / (y2-y1) + x1);
			}
			polyInts.sort(_CompInt);
			for(i = 0; i < ints; i+=2)
				this._mkDiv(polyInts[i], y, polyInts[i+1]-polyInts[i]+1, 1);
		}
	};

	this.drawString = function(txt, x, y)
	{
		this.htm += '<div style="position:absolute;white-space:nowrap;'+
			'left:' + x + 'px;'+
			'top:' + y + 'px;'+
			'font-family:' +  this.ftFam + ';'+
			'font-size:' + this.ftSz + ';'+
			'color:' + this.color + ';' + this.ftSty + '">'+
			txt +
			'<\/div>';
	};

/* drawStringRect() added by Rick Blommers.
Allows to specify the size of the text rectangle and to align the
text both horizontally (e.g. right) and vertically within that rectangle */
	this.drawStringRect = function(txt, x, y, width, halign)
	{
		this.htm += '<div style="position:absolute;overflow:hidden;'+
			'left:' + x + 'px;'+
			'top:' + y + 'px;'+
			'width:'+width +'px;'+
			'text-align:'+halign+';'+
			'font-family:' +  this.ftFam + ';'+
			'font-size:' + this.ftSz + ';'+
			'color:' + this.color + ';' + this.ftSty + '">'+
			txt +
			'<\/div>';
	};

	this.drawImage = function(imgSrc, x, y, w, h, a)
	{
		this.htm += '<div style="position:absolute;'+
			'left:' + x + 'px;'+
			'top:' + y + 'px;'+
			// w (width) and h (height) arguments are now optional.
			// Added by Mahmut Keygubatli, 14.1.2008
			(w? ('width:' +  w + 'px;') : '') +
			(h? ('height:' + h + 'px;'):'')+'">'+
			'<img src="' + imgSrc +'"'+ (w ? (' width="' + w + '"'):'')+ (h ? (' height="' + h + '"'):'') + (a? (' '+a) : '') + '>'+
			'<\/div>';
	};

	this.clear = function()
	{
		this.htm = "";
		if(this.cnv) this.cnv.innerHTML = "";
	};

	this._mkOvQds = function(cx, cy, x, y, w, h, wod, hod)
	{
		var xl = cx - x, xr = cx + x + wod - w, yt = cy - y, yb = cy + y + hod - h;
		if(xr > xl+w)
		{
			this._mkDiv(xr, yt, w, h);
			this._mkDiv(xr, yb, w, h);
		}
		else
			w = xr - xl + w;
		this._mkDiv(xl, yt, w, h);
		this._mkDiv(xl, yb, w, h);
	};
	
	this._mkArcDiv = function(x, y, oy, cx, cy, iOdds, aBndA, aBndZ, iSects)
	{
		var xrDef = cx + x + (iOdds & 0xffff), y2, h = oy - y, xl, xr, w;

		if(!h) h = 1;
		x = cx - x;

		if(iSects & 0xff0000) // Start-angle > end-angle
		{
			y2 = cy - y - h;
			if(iSects & 0x00ff)
			{
				if(iSects & 0x02)
				{
					xl = Math.max(x, aBndZ[y]);
					w = xrDef - xl;
					if(w > 0) this._mkDiv(xl, y2, w, h);
				}
				if(iSects & 0x01)
				{
					xr = Math.min(xrDef, aBndA[y]);
					w = xr - x;
					if(w > 0) this._mkDiv(x, y2, w, h);
				}
			}
			else
				this._mkDiv(x, y2, xrDef - x, h);
			y2 = cy + y + (iOdds >> 16);
			if(iSects & 0xff00)
			{
				if(iSects & 0x0100)
				{
					xl = Math.max(x, aBndA[y]);
					w = xrDef - xl;
					if(w > 0) this._mkDiv(xl, y2, w, h);
				}
				if(iSects & 0x0200)
				{
					xr = Math.min(xrDef, aBndZ[y]);
					w = xr - x;
					if(w > 0) this._mkDiv(x, y2, w, h);
				}
			}
			else
				this._mkDiv(x, y2, xrDef - x, h);
		}
		else
		{
			if(iSects & 0x00ff)
			{
				if(iSects & 0x02)
					xl = Math.max(x, aBndZ[y]);
				else
					xl = x;
				if(iSects & 0x01)
					xr = Math.min(xrDef, aBndA[y]);
				else
					xr = xrDef;
				y2 = cy - y - h;
				w = xr - xl;
				if(w > 0) this._mkDiv(xl, y2, w, h);
			}
			if(iSects & 0xff00)
			{
				if(iSects & 0x0100)
					xl = Math.max(x, aBndA[y]);
				else
					xl = x;
				if(iSects & 0x0200)
					xr = Math.min(xrDef, aBndZ[y]);
				else
					xr = xrDef;
				y2 = cy + y + (iOdds >> 16);
				w = xr - xl;
				if(w > 0) this._mkDiv(xl, y2, w, h);
			}
		}
	};

	this.setStroke(1);
	this.setFont("verdana,geneva,helvetica,sans-serif", "12px", Font.PLAIN);
	this.color = "#000000";
	this.htm = "";
	this.wnd = wnd || window;

	if(!jg_ok) _chkDHTM();
	if(jg_ok)
	{
		if(cnv)
		{
			if(typeof(cnv) == "string")
				this.cont = document.all? (this.wnd.document.all[cnv] || null)
					: document.getElementById? (this.wnd.document.getElementById(cnv) || null)
					: null;
			else if(cnv == window.document)
				this.cont = document.getElementsByTagName("body")[0];
			// If cnv is a direct reference to a canvas DOM node
			// (option suggested by Andreas Luleich)
			else this.cont = cnv;
			// Create new canvas inside container DIV. Thus the drawing and clearing
			// methods won't interfere with the container's inner html.
			// Solution suggested by Vladimir.
			this.cnv = this.wnd.document.createElement("div");
			this.cnv.style.fontSize=0;
			this.cont.appendChild(this.cnv);
			this.paint = jg_dom? _pntCnvDom : _pntCnvIe;
		}
		else
			this.paint = _pntDoc;
	}
	else
		this.paint = _pntN;

	this.setPrintable(false);
}

function _mkLinVirt(aLin, x1, y1, x2, y2)
{
	var dx = Math.abs(x2-x1), dy = Math.abs(y2-y1),
	x = x1, y = y1,
	xIncr = (x1 > x2)? -1 : 1,
	yIncr = (y1 > y2)? -1 : 1,
	p,
	i = 0;
	if(dx >= dy)
	{
		var pr = dy<<1,
		pru = pr - (dx<<1);
		p = pr-dx;
		while(dx > 0)
		{--dx;
			if(p > 0)    //  Increment y
			{
				aLin[i++] = x;
				y += yIncr;
				p += pru;
			}
			else p += pr;
			x += xIncr;
		}
	}
	else
	{
		var pr = dx<<1,
		pru = pr - (dy<<1);
		p = pr-dy;
		while(dy > 0)
		{--dy;
			y += yIncr;
			aLin[i++] = x;
			if(p > 0)    //  Increment x
			{
				x += xIncr;
				p += pru;
			}
			else p += pr;
		}
	}
	for(var len = aLin.length, i = len-i; i;)
		aLin[len-(i--)] = x;
};

function _CompInt(x, y)
{
	return(x - y);
}

//---------------------------------------------------------------------------
// Plugin
//---------------------------------------------------------------------------

Wiki.plugins = {};
Wiki.plugins.contents = function(parser, params, line) {
	return new WOM(parser,{
		type:'plugin',
		toHtml: function(){
			var anums = parser.anums;
			if (anums.length == 0) return "";
			var html = "<div class='contents'>";
			for (var i=0; i<anums.length; i++) {
				if (anums[i].wom.level <= 3) {
					html += "<div style='text-indent:"+(anums[i].wom.level)+"em;'>"
					html += "<a href='#"+anums[i].anum+"'>"
						+anums[i].wom.getBody() +"</a></div>\n";
				}
			}
			return html+"</div>";
		}
	});
}
Wiki.plugins.anum = function(parser, params, line) {
	return new WOM(parser,{
		type:'plugin',
		onParse: function(){
			var param = params.split(",");
			for (var i=0; i<param.length || i<4; i++) {
				parser.anum[i] = param[i];
			}
		}
	});
}
Wiki.plugins.title = function(parser, params, line) {
	return new WOM(parser,{
		type:'plugin',
		toHtml: function(){
			var param = params.split(",");
			return	""
				+"<div align='right'>"+param[1]+"</div>\n"
				+"<div align='right'>"+param[2]+"</div>\n"
				+"<div align='center'><h1>"+param[0]+"</h1></div>\n"
			;
		}
	});
}


Wiki.plugins.include = function(parser, params, line) {
	return new WOM(parser,{
		type:'plugin',
		toHtml: function(){
			var wiki = new Wiki(Wiki.getWikiText(params));
			var root = wiki.parse();
			return root.toHtml();
		}
	});
}
Wiki.plugins.javascript = function(parser, params, line) {
	return new WOM(parser,{
		type:'plugin',
		toHtmlBlock: function(){
			var html = "\n<script type='text/javascript'>\n";
			for (var i=0; i<this.children.length; i++) {
				html += this.children[i].body+"\n";
			}
			html	+= "</script>\n"
			return html;
		}
	});

}
Wiki.plugins.pre = function(parser, params, line) {
	return new WOM(parser,{
		type:'plugin', isRawBody:true,
		toHtmlBlock: function(){
			var html = "\n<pre>";
			for (var i=0; i<this.children.length; i++) {
				html += this.preText(this.children[i].body)+"\n";
			}
			html	+= "</pre>\n"
			return html;
		}
	});
}
Wiki.plugins.html = function(parser, params, line) {
	return new WOM(parser,{
		type:'plugin',  isEnableHtml:true
	});
}
Wiki.plugins.blockquote = function(parser, params, line) {
	return new WOM(parser,{
		type:'plugin',
		toHtmlBlock: function(){
			var html = "<blockquote>";
			for (var i=0; i<this.children.length; i++) {
				if (i>0) html += "\n";
				html += this.children[i].toHtml();
			}
			html	+= "</blockquote>"
			return html;
		}
	});
}

Wiki.plugins.img = function(parser, params, line) {
	return new WOM(parser,{
		type:'plugin',
		toHtml: function(){
			var param = params.split(",");
			var html = "<img src='"+param[0]+"'";
			if (param[1]) html += " alt='"+param[1]+"'";
			if (param[2]) html += " width='"+param[2]+"'";
			if (param[3]) html += " height='"+param[3]+"'";
			if (param[4]) html += " "+param[4];
			html += "/>";
			return html;
		}
	});
}

Wiki.plugins.exlink = function(parser, params, line) {
	return new WOM(parser,{
		type:'plugin',
		toHtml: function(){
			var param = params.split(",");
			var html = 
				"<a target='_blank' href='"+param[1]+"'>"
				+param[0]+"</a>";
			return html;
		}
	});
}

Wiki.plugins.drawing = function(parser, params, line) {
	return new WOM(parser,{
		type:'plugin',
		toHtml: function(){
			var param = params.split(",");
			var req = new XMLHttpRequest();
			req.open('GET', param[0], false); 
			req.send(null);
			var name = param[1];
			if (name == null) name = "drawing-"+(parser.drawing++);
			var html =
			 "<div id='"+name+"'  style='position:relative;height:50%;'>"
				+"<script type='text/javascript'>"
					+"var jg = new jsGraphics('"+name+"');"
					+"with (jg) {"	+req.responseText +"}"
					+"jg.paint();"
				+"</script>"
			+"</div>";
			return html;
		}
	});
}

Wiki.plugins.search = function(parser, params, line) {
	return new WOM(parser,{
		type:'plugin',
		toHtml: function(){
			var param = params.split(",");
			var dirName = "";
			var key = param[0];
			var isRecu = (param.length>=2) ? param[1] : true;
			var list = SsjsWiki.search("/wiki/","",key,isRecu);
			var html = "<ul>\n"
			for (var i=0; i<list.length; i++) {
				var name = list[i].name;
				var date = Wiki.Util.formatDate(new Date(list[i].date));
				html += "<li>"
					+"<a onclick='Wiki.manager.reload(\""+dirName+name+"\")'>"+name+"</a>"
					+"<spam style='float:right;'>("+date+")</span>";
					+"</li>";
			}
			html += "</ul>\n";
			return html;
		}
	});
}

Wiki.plugins.recent = function(parser, params, line) {
	return new WOM(parser,{
		type:'plugin',
		toHtml: function(){
			var count = params;
			var list = SsjsWiki.recent("/wiki/", count);
			var html = "<div>\n";
			var lastDate = "";
			for (var i=0; i<list.length; i++) {
				var name = list[i].name;
				var date = Wiki.Util.formatDate(new Date(list[i].date),"YY/MM/DD");
				if (date != lastDate) {
					html += "<div><b>"+date+"</b></div>";
					lastDate = date;
				}
				html += "<div style='padding-left:1em;'><a onclick='Wiki.manager.reload(\""+name+"\")'>"+name+"</a></div>";
			}
			html += "</div>\n";
			return html;
		}
	});
}

Wiki.plugins.dirList = function(parser, params, line) {
	return new WOM(parser,{
		type:'plugin',
		toHtml: function(){
			var param = params.split(",");
			var dirName = param[0];
			var isRecu = (param.length>=2) ? param[1] : false;

			var list = SsjsWiki.dirList("/wiki/",dirName,isRecu);

			var html = "<ul>\n"
			for (var i=0; i<list.length; i++) {
				var name = list[i].name;
				var date = Wiki.Util.formatDate(new Date(list[i].date));
				html += "<li>"
					+"<a onclick='Wiki.manager.reload(\""+dirName+name+"\")'>"+name+"</a>"
					+"<spam style='float:right;'>("+date+")</span>";
					+"</li>";
			}
			html += "</ul>\n";
			return html;
		}
	});
}
Wiki.plugins.includePlain = function(parser, params, line) {
	return new WOM(parser,{
		type:'plugin',
		toHtml: function(){
			var param = params.split(/,/);
			var text = Wiki.getWikiText(param[0]);
			return "<xmp style='"+param[1]+"'>"+text+"</xmp>";
		}
	});
}
Wiki.plugins.iframe = function(parser, params, line) {
	return new WOM(parser,{
		type:'plugin',
		toHtml: function(){
			var param = params.split(/,/);
			var width  = "100%";
			var height = "200px";
			if (param[1]) width = param[1];
			if (param[2]) height = param[2];
			return "<iframe width='"+width+"' height='"+height
							+"' src='"+param[0]+"'></iframe>";
		}
	});
}

//-----------------------------------------------------------------------------
// EOF
//-------------------------------------------------------------------------------
if(typeof ActiveXObject == "function" 
	&& typeof XMLHttpRequest == "undefined"){
    XMLHttpRequest = function(){
        return new ActiveXObject("Microsoft.XMLHTTP")
    }
}
unescapePageName = unescape;

//---------------------------------------------------------------------------
// WOM : Wiki Object Model : Wikiテキストを木構造で持つ為のクラス。
//---------------------------------------------------------------------------
function WOM(parser, params) {
	this.parser = parser;
	for (key in params) this[key] = params[key];
}
WOM.prototype.preText = function(body) {
	body = body.replace(/&/g, "&amp;");
	body = body.replace(/</g, "&lt;");
	body = body.replace(/>/g, "&gt;");
	return body;
}
WOM.prototype.getBody = function(body) {
	if (!body) body = this.body;
	if (body == null) return "";
	if (!this._isEnableHtml()) {
		body = body.replace(/</g, "&lt;");
		body = body.replace(/>/g, "&gt;");
	}

	body = body.replace(/&size\(([0-9]+)\)\{(.*)\};/g,
			"<span style='font-size:$1px;'/>$2</span>");
	body = body.replace(/%%(.*)%%/g, "<S/>$1</S>");
	body = body.replace(/'''(.*)'''/g, "<I/>$1</I>");
	body = body.replace(/''(.*)''/g, "<B/>$1</B>");

	body = body.replace(/&br;/g, "<br/>");
	if (this.anum) {
		body = body.replace("&anum;", this.anum);
	}

	var parser = this.parser;
	var matchs = body.match(); 
	body = body.replace(/\(\((.*)\)\)/g, function(str,$1) {
		var name = "(*"+(parser.annotate.length+1)+")";
		var view = "<font size='1'><sup>"+name+"</sup></font>";
		parser.annotate.push("<a name='"+name+"'></a>"+view+$1);
		return "<a href='#"+name+"'>"+view+"</a>";
	});

	body = body.replace(/\[\[([^:\]]*):([^\]]*)\]\]/g, function(str, $1, $2) {
		if ($1 == "http" || $1 == "https" || $1 == "ftp") {
			$1 = $1+":"+$2;
			$2 = $1;
		}
		if ($2 == "#") {
			var anums = parser.anums;
			for (var i=0; i<anums.length; i++) {
				var title = anums[i].wom.body.replace(/(&anum;)/,"");
				title = title.replace(/(^[ ]*)|([ ]*$)/g,"");
				if (title == $1) $2 = "#"+anums[i].anum;
			}
		}
		return "<a href='"+$2+"'>"+$1+"</a>";
	});

	body = body.replace(/\[\[([^\]]*)\]\]/g, "<a href='?$1'>$1</a>");
	body = body.replace(/~$/, "<br/>");

	// Plugin
	body = body.replace(/&([a-zA-Z_][a-zA-Z0-9_]*)\(([^)]*)\)({[^}]*})?;/g, function(str,$1,$2,$3) {
		if (Wiki.plugins[$1]) {
			return Wiki.plugins[$1](parser, $2, str).toHtml();
		} else {
			return str;
		}
	});

	return body;
}
WOM.prototype.toHtml = function() {
	var html = this.getBody();
	if (this.unitTag) {
		html = this.unitTag[0]+html+this.unitTag[1];
	}
//this.parser.log += "\n-->"+this._getParens()+":"+html+":"+this.body;
	return html;
}

WOM.prototype.toHtmlBlock = function() {
//this.parser.log += "\n==>"+this._getParens()+":"+this.body;
	var html = [];
	if (this.blockTag) html.push(this.blockTag[0]);
	for (var i=0; i<this.children.length; i++) {
		html.push(this.children[i].toHtml());
	}
	if (this.blockTag) html.push(this.blockTag[1]);
	return html.join("");
}
WOM.prototype._isEnableHtml = function() {
	var wom = this;
	while (wom != null) {
		if (wom.isEnableHtml) return true;
		wom = wom.parent;
	}
	return false;
}
WOM.prototype.isBreaker = function(parent) {
	if (!this.breakIt || !parent.type) return false;
	//console.log(this.type+":"+parent.type+"="+(this.breakIt.indexOf(parent.type) >= 0));
	return (this.breakIt.indexOf(parent.type) >= 0);
}
WOM.prototype._getParens = function() {
	var str = ""
	var wom = this;
	while (wom != null) {
		str = wom.type+">"+str;
		wom = wom.parent;
	}
	return str;
}

//---------------------------------------------------------------------------
// Wiki parser
//---------------------------------------------------------------------------
function Wiki(wikiText) {
	wikiText = wikiText.replace(/<\?xml .*\?>\r?\n/g, "");
	//wikiText = wikiText.replace(/[~]\r?\n/g, "&br;");
	this.wikiText = wikiText;
	this.lines = wikiText.split(/\r?\n/);
	this.anum = [0,0,0,0];
	this.anums = [];
	this.annotate = [];
	this.drawing = 1;
	this.log = "";
}

Wiki.prototype.parse = function() {
	var root = new WOM(this,{
		type:"root", level:-1, children:[], isBlock: true,
		blockTag: ["<div>","</div>"],
		toHtml: function() {
			this.parser.annotate = [];
			return this.toHtmlBlock() + this.parser.getAnnotateHtml();
		}
	});
	this._parse(root, 0);
	return root;
}
Wiki.prototype.getAnnotateHtml = function() {
	if (this.annotate.length == 0) return "";
	var html = "<hr/>";
	for (var i=0; i<this.annotate.length; i++) {
		html = html + "<div>"+this.annotate[i]+"</div>";
	}
	return html + "<hr/>";
}

Wiki.prototype._parse = function(parent, pos) {
	while (pos<this.lines.length) {
		var current = this.parseLine(parent, pos);
//console.log("--->"+current.type+":"+this.lines[pos]);
//this.log += "\n--->"+current.type+":"+this.lines[pos];
		var isBlockIn  = false;
		var isBlockOut = false;

		if (current.isChild) {
			if (parent.type == current.type) {
				if (parent.level < current.level) {
					isBlockIn = true;
				} else if (parent.level > current.level) {
					isBlockOut = true;
				}
			} else {
				if (current.isBreaker(parent)) {
					isBlockOut = true;
				} else {
					isBlockIn = true;
				}
			}
		} else if (current.isBegin) {
			isBlockIn = true;
			pos++;
		} else if (current.isEnd) {
			isBlockOut = true;
			if (parent.type == current.type) pos++;
		} else if (current.isBreaker(parent)) {
			isBlockOut = true;
		}


		if (isBlockIn) {
			var block = this._newBlock(parent, current);
			block.index = parent.children.length;
			parent.children.push(block);
			block.parent = parent;
			pos = this._parse(block, pos); // lines[pos]はブロック内で再処理
		} else if (isBlockOut) {
			return pos; //  lines[pos]は上位ブロックで再処理
		} else {
			if (current.anum != null) current.anum = this.getAnum(current); 
			current.index = parent.children.length;
			parent.children.push(current);
//for (var i=0; i<parent.children.length;i++) {
//	this.log+="\n=>"+i+":"+parent.children[i].body;
//}
			current.parent = parent;
			pos++;
			if (current.onParse) current.onParse();
		}

	}
}

/**
 * 行を解析して WOM インスタンスを返す。
 * WOM パラメータの意味
 *  type         : 行の属性
 *  level        : ブロックのネストレベル。 -:1 --:2 ---:3
 *  body         : 行の本文
 *  isBegin      : ブロックの開始。 #plugin(){
 *  isEnd        : プロックの終了。 #}
 *  isChild      : ブロックの子要素。必要に応じてブロックを生成する。 -,*
 *  isBrittle    : 弱いブロック。isBreaker によってブロックが中断される。
 *  isBreaker    : 弱いブロックを中断する。自身の属性のブロックは中断しない。
 *  isRawBody    : Plugin用。ブロック内を生テキストで取得する。
 *  isEnableHtml : ブロック内の HTML タグを有効にする。
 */

Wiki.prototype.parseLine = function(parent, pos) {
	if (pos >= this.lines.length) {
		return new WOM(this,{type:"EOF",body:"EOF", toHtml: function(){return "";}});
	}
	var line = this.lines[pos];

	// プラグインのブロック終端
	if (line.match(/^#\}/)) {
		return new WOM(this,{type:'plugin', isEnd:true});
	// プラグインブロック内の生テキスト。
	} else if (parent.isRawBody) {
		return new WOM(this,{type:'pre', level:0, body:line});
	// 整形済テキスト(pre)
	} else if (parent.isRawBody || line.match(/^[ \t]/)) {
		return new WOM(this,{
			type:'pre', level:0, body:line.substring(1), 
			isChild: true, breakIt:['plain','table','BQ'],
			toHtml: function(){return this.preText(this.body)+"\n";},
			blockTag: ["<pre>","</pre>"]
		});
	// 水平線
	} else if (line.match(/^-----------------*/)) {
		return new WOM(this,{
			type:'hline', level:0, isChild: false, breakIt:['plain','pre','table','BQ'],
			toHtml: function(){return "\n<hr/>\n";}
		});
	// リスト
	} else if (line.match(/^(-{1,16})/)) {
		return new WOM(this,{
			type:'list', level:RegExp.$1.length, body:RegExp.rightContext, 
			isChild: true, breakIt:['plain','pre','table','BQ'],
			unitTag: ["<li>","</li>"],	blockTag: ["<ul>","</ul>"]
		});
	// リスト番号付き
	} else if (line.match(/^([+]{1,16})/)) {
		return new WOM(this,{
			type:'list', level:RegExp.$1.length, body:RegExp.rightContext, 
			isChild: true, breakIt:['plain','pre','table','BQ'],
			unitTag: ["<li>","</li>"],	blockTag: ["<ol>","</ol>"]
		});
	// 見出し
	} else if (line.match(/^([*]{1,4})/)) {
		return new WOM(this,{
			type:'header', level:RegExp.$1.length, body:RegExp.rightContext,
			isChild: true, anum: "", breakIt:['plain','pre','list','term','table','BQ'],
			toHtml: function(){
				var tag = "h"+(this.level+1);
				return "<"+tag+"><a name='"+this.anum+"'></a>"+this.getBody()+"</"+tag+">\n";
			}
			//,blockTag: ["<div>","</div>"]
		});
	// 用語継続行
	} else if (line.match(/^(:{1,16})[|](.*)/)) {
		return new WOM(this,{
			type:'term', level:RegExp.$1.length, body:RegExp.$2,
			isChild: true, breakIt:['plain','pre','table','BQ'],
			toHtml: function(){return "<br/>"+this.getBody();}
		});
	// 用語
	} else if (line.match(/^(:{1,16})([^:|]+)[:|](.*)/)) {
		return new WOM(this,{
			type:'term', level:RegExp.$1.length, title:RegExp.$2, body:RegExp.$3,
			isChild: true, breakIt:['plain','pre','table','BQ'],
			toHtml: function(){return "<dt>"+this.title+":</dt><dd>"+this.getBody();},
			toHtmlBlock: function() {
				var html = "<dl>\n";
				for (var i=0; i<this.children.length; i++) {
					var child = this.children[i];
					if (i>0 && child.type == 'term' && child.isChild && child.title) {
						html += "</dd>"
					}
					html += child.toHtml();
				}
				return html +"</dl>\n";
			}
		});
	// テーブル
	} else if (line.match(/^[|]/)) {
		return new WOM(this,{
			type:'table', level:1, body:line, isChild: true, breakIt:['plain','pre','BQ'],
			toHtml: function(){
				var cells = this.body.split(/[|]/);
				var tag = 	(cells[cells.length-1] == "h") ? "th" : "td";
				var html = "<tr>";
				for (var i=1; i<cells.length-1; i++) {
					html += "<"+tag+">"+this.getBody(cells[i])+"</"+tag+">";
				}
				html += "</tr>\n";
				return html;
			},
			blockTag: ["<table class='style_table'>","</table>"]
		});
	// 引用
	} else if (line.match(/^(>{1,16})/)) {
		return new WOM(this,{
			type:'BQ', level:RegExp.$1.length, body:RegExp.rightContext, 
			isChild: true, breakIt:['plain','pre'],
			toHtml: function(){return this.getBody();},
			blockTag: ["<blockquote>","</blockquote>"]
		});
	// コメント
	} else if (line.match(/^\/\//)) {
		return new WOM(this,{
			type:'comment', level:0, isChild: false, breakIt:['pre'],
			unitTag: ["<!--","-->"]
		});
	// 空行
	} else if (line == "") {
		return new WOM(this,{
			type:'NIL', level:0, body:"NIL", isChild:false, 
			breakIt:['plain','pre','list','term','table','BQ'],
			toHtml: function(){return "\n";}
		});
	// プラグイン
	} else if (line.match(/^#([a-zA-Z_][^(]*)(\(([^)]*)\))?/)) {
		if (Wiki.plugins[RegExp.$1]) {
			var wom = Wiki.plugins[RegExp.$1](this, RegExp.$3, line);
			if (line.match(/[{][ \t]*$/)) {
				wom.isBegin = true;
			}
			return wom;
		} else {
			return new WOM(this,{
				type:'plain', level:0, body:line, isChild: false,
				toHtml: function(){return this.getBody();}
			});
		}
	// 通常文
	} else if (line.match(/^[~]/)) {
		return new WOM(this,{
			type:'plain', level:0, body:line.substring(1),
			isChild: true, breakIt:['pre','table','BQ'],
			toHtml: function(){return this.getBody();},
			blockTag: ["<div class='section'>","</div>"]
		});
	}
	// 通常文
	return new WOM(this,{
		type:'plain', level:0, body:line,
		isChild: true, breakIt:['pre','table','BQ'],
		toHtml: function(){return this.getBody();},
		blockTag: ["<div class='section'>","</div>"]
	});
}

Wiki.prototype._newBlock = function(parent, child) {
	var block = new WOM(this,{});
for (key in child) {
	if (key.match(/^(is.*)/)) {
		block[key] = child[key];
	}
}
	block.isChild = false;
	block.type = child.type;
	block.body = child.body;
	block.level = child.level;
	block.breakIt = child.breakIt;
	block.blockTag = child.blockTag;
	block.toHtml = child.toHtmlBlock;
	block.isBlock = true;
	block.children = [];
	return block;
}

Wiki.prototype._hasAncestor = function(parent, child) {
	parent = parent.parent;
	while (parent != null) {
		if (parent.type == child.type
				&& parent.level == child.level) {
			return true;
		}
		parent = parent.parent;
	}
	return false;
}

Wiki.prototype.getAnum = function(wom) {
	with (this) {
		var str = "";
		anum[wom.level-1]++;
		for (var i=wom.level; i<anum.length; i++) anum[i] = 0;
		if (wom.level == 1) str = anum[0]+".";
		if (wom.level == 2) str = anum[0]+"."+anum[1];
		if (wom.level == 3) str = anum[0]+"."+anum[1]+"."+anum[2];
		if (wom.level == 4) str = anum[0]+"."+anum[1]+"."+anum[2]+"."+anum[3];
		anums.push({anum:str, wom:wom});
		return str;
	}
}
//---------------------------------------------------------------------------

Wiki.doConvert = function(wikiText, dest) {
	var parser = new Wiki(wikiText);
	var wom = parser.parse();
	var html = wom.toHtml();
	Wiki.show(html, dest);
}
Wiki.show = function(html, dest) {
	dest.innerHTML = "";
	if (dest.insertAdjacentHTML) {
		dest.insertAdjacentHTML("BeforeEnd", html);
	} else {
		var range = document.createRange();
		range.setStartAfter(dest);
		var df = range.createContextualFragment(html);
		dest.appendChild(df);
	}

	var firstNode = dest.childNodes[0].childNodes ? dest.childNodes[0].childNodes[0] : null;
	if (firstNode && firstNode.tagName 
			&& firstNode.tagName.match(/^[hH][1-6]$/)) {
		firstNode.style.marginTop = 0;
	}

}


Wiki.getWikiText = function(pageName) {
	var req = new XMLHttpRequest();
	try {
		req.open('GET', pageName+"?"+new Date().getTime(), false);
		req.send(null);
		return req.responseText;
	} catch (e) {
		//console.log("-->"+e);
		return "Not found "+pageName;
	}
}

//---------------------------------------------------------------------------
Wiki.onloadTarget = function(srcId, dstId) {
	document.getElementById(srcId).style.display = "none";
	var src = document.getElementById(srcId).value;
	var dst = document.getElementById(dstId);
	Wiki.doConvert(src, dst);
	location.hash = location.hash;
}
Wiki.onload = function(className) {
	var texts = document.getElementsByTagName("textarea");
	for (var i=0; i<texts.length; i++) {
		var text = texts[i];
		if (text.className == className) {
			var dest = document.createElement("div");
			dest.className = "jswiki";
			text.parentNode.insertBefore(dest, text);
			text.style.display = "none";
			Wiki.doConvert(text.value, dest);
		}
	}
}




function WikiManager(labelId, srcId, destId) {
	this.label  = document.getElementById(labelId);
	this.editor = document.getElementById(srcId);
	this.dest   = document.getElementById(destId);
	this.currentPageName = null;
}
WikiManager.prototype.load = function(pageName, ancName) {
 
	var wikiText = null;
	if (pageName == null) {
		pageName = "FrontPage";
		if (location.href.match(/^.*[?]([^#]*)(#(.*))?/)) {
			pageName = RegExp.$1;
			ancName = RegExp.$2;
		}
	}
	if (pageName.match(/[/]$/)) {
		wikiText = "#dirList("+pageName+");";
	}
	this.currentPageName = pageName;

	if (wikiText == null) {
		wikiText = Wiki.getWikiText(pageName+".wiki");
	}

	if (this.label) this.label.value = unescapePageName(pageName);
	if (this.editor) this.editor.value = wikiText;

	Wiki.doConvert(wikiText, this.dest);

	if (ancName) {
		location.hash = ancName; 
	}
} 
WikiManager.prototype.doConvert = function(wikiText) {

	if (wikiText == null) {
		wikiText = this.editor.value;
	}
	Wiki.doConvert(wikiText, this.dest);
}

//---------------------------------------------------------------------------
// Util
//---------------------------------------------------------------------------

Wiki.Util = {};
Wiki.Util.zeroPadd = function(num, len) {
	var str = "0000000000000000000000000000000000"+num;
	return str.substr(str.length-len);
}
Wiki.Util.formatDate = function(date,fmt) {
	var str = fmt ? fmt : "YYYY/MM/DD hh:mm:ss" ;
	str = str.replace(/YYYY/, date.getFullYear());
	str = str.replace(/YY/, Wiki.Util.zeroPadd(date.getYear()%100, 2));
	str = str.replace(/MM/, Wiki.Util.zeroPadd(date.getMonth()+1, 2));
	str = str.replace(/DD/, Wiki.Util.zeroPadd(date.getDate(), 2));
	str = str.replace(/hh/, Wiki.Util.zeroPadd(date.getHours(), 2));
	str = str.replace(/mm/, Wiki.Util.zeroPadd(date.getMinutes(), 2));
	str = str.replace(/ss/, Wiki.Util.zeroPadd(date.getSeconds(), 2));
	return str;
}

//-----------------------------------------------------------------------------
// EOF
//-------------------------------------------------------------------------------
