/*
 * YuiCompressor Maven plugin
 *
 * Copyright 2012-2023 Hazendaz.
 *
 * Licensed under the GNU Lesser General Public License (LGPL),
 * version 2.1 or later (the "License").
 * You may not use this file except in compliance with the License.
 * You may read the licence in the 'lgpl.txt' file in the root folder of
 * project or obtain a copy at
 *
 *     https://www.gnu.org/licenses/lgpl-2.1.html
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" basis,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
// (C)2002 Douglas Crockford
// www.JSLint.com
// Rhino Edition
var JSLINT;
JSLINT = function() {
	var adsafe = {
		activexobject: true,
		alert: true,
		back: true,
		body: true,
		close: true,
		confirm: true,
		cookie: true,
		constructor: true,
		createpopup: true,
		defaultstatus: true,
		defaultview: true,
		document: true,
		documentelement: true,
		domain: true,
		'eval': true,
		execScript: true,
		external: true,
		forms: true,
		forward: true,
		frameelement: true,
		fromcharcode: true,
		history: true,
		home: true,
		location: true,
		moveby: true,
		moveto: true,
		navigate: true,
		opener: true,
		parent: true,
		print: true,
		prompt: true,
		prototype: true,
		referrer: true,
		resizeby: true,
		resizeto: true,
		self: true,
		showhelp: true,
		showmodaldialog: true,
		status: true,
		stop: true,
		top: true,
		window: true,
		write: true,
		writeln: true,
		__proto__: true
	},
	allOptions = {
		adsafe: true,
		bitwise: true,
		browser: true,
		cap: true,
		debug: true,
		eqeqeq: true,
		evil: true,
		fragment: true,
		laxbreak: true,
		nomen: true,
		passfail: true,
		plusplus: true,
		rhino: true,
		undef: true,
		white: true,
		widget: true
	},
	anonname,
	browser = {
		alert: true,
		blur: true,
		clearInterval: true,
		clearTimeout: true,
		close: true,
		closed: true,
		confirm: true,
		console: true,
		Debug: true,
		defaultStatus: true,
		document: true,
		event: true,
		focus: true,
		frames: true,
		getComputedStyle: true,
		history: true,
		Image: true,
		length: true,
		location: true,
		moveBy: true,
		moveTo: true,
		name: true,
		navigator: true,
		onblur: true,
		onerror: true,
		onfocus: true,
		onload: true,
		onresize: true,
		onunload: true,
		open: true,
		opener: true,
		opera: true,
		parent: true,
		print: true,
		prompt: true,
		resizeBy: true,
		resizeTo: true,
		screen: true,
		scroll: true,
		scrollBy: true,
		scrollTo: true,
		self: true,
		setInterval: true,
		setTimeout: true,
		status: true,
		top: true,
		window: true,
		XMLHttpRequest: true
	},
	escapes = {
		'\b': '\\b',
		'\t': '\\t',
		'\n': '\\n',
		'\f': '\\f',
		'\r': '\\r',
		'"': '\\"',
		'\\': '\\\\'
	},
	funct,
	functions,
	globals,
	implied,
	inblock,
	indent,
	jsonmode,
	lines,
	lookahead,
	member,
	membersOnly,
	nexttoken,
	noreach,
	option,
	prereg,
	prevtoken,
	rhino = {
		defineClass: true,
		deserialize: true,
		gc: true,
		help: true,
		load: true,
		loadClass: true,
		print: true,
		quit: true,
		readFile: true,
		readUrl: true,
		runCommand: true,
		seal: true,
		serialize: true,
		spawn: true,
		sync: true,
		toint32: true,
		version: true
	},
	scope,
	stack,
	standard = {
		Array: true,
		Boolean: true,
		Date: true,
		decodeURI: true,
		decodeURIComponent: true,
		encodeURI: true,
		encodeURIComponent: true,
		Error: true,
		escape: true,
		'eval': true,
		EvalError: true,
		Function: true,
		isFinite: true,
		isNaN: true,
		Math: true,
		Number: true,
		Object: true,
		parseInt: true,
		parseFloat: true,
		RangeError: true,
		ReferenceError: true,
		RegExp: true,
		String: true,
		SyntaxError: true,
		TypeError: true,
		unescape: true,
		URIError: true
	},
	syntax = {},
	token,
	verb,
	warnings,
	widget = {
		alert: true,
		appleScript: true,
		animator: true,
		appleScript: true,
		beep: true,
		bytesToUIString: true,
		Canvas: true,
		chooseColor: true,
		chooseFile: true,
		chooseFolder: true,
		convertPathToHFS: true,
		convertPathToPlatform: true,
		closeWidget: true,
		COM: true,
		CustomAnimation: true,
		escape: true,
		FadeAnimation: true,
		filesystem: true,
		focusWidget: true,
		form: true,
		Frame: true,
		HotKey: true,
		Image: true,
		include: true,
		isApplicationRunning: true,
		iTunes: true,
		konfabulatorVersion: true,
		log: true,
		MenuItem: true,
		MoveAnimation: true,
		openURL: true,
		play: true,
		Point: true,
		popupMenu: true,
		preferenceGroups: true,
		preferences: true,
		print: true,
		prompt: true,
		random: true,
		reloadWidget: true,
		resolvePath: true,
		resumeUpdates: true,
		RotateAnimation: true,
		runCommand: true,
		runCommandInBg: true,
		saveAs: true,
		savePreferences: true,
		screen: true,
		ScrollBar: true,
		showWidgetPreferences: true,
		sleep: true,
		speak: true,
		suppressUpdates: true,
		system: true,
		tellWidget: true,
		Text: true,
		TextArea: true,
		unescape: true,
		updateNow: true,
		URL: true,
		widget: true,
		Window: true,
		XMLDOM: true,
		XMLHttpRequest: true,
		yahooCheckLogin: true,
		yahooLogin: true,
		yahooLogout: true
	},
	wmode,
	xmode,
	xtype,
	tx = /^\s*([(){}[.,:;'"~]|\](\]>)?|\?>?|==?=?|\/(\*(global|extern|jslint|member|members)?|=|\/)?|\*[\/=]?|\+[+=]?|-[-=]?|%[=>]?|&[&=]?|\|[|=]?|>>?>?=?|<([\/=%\?]|\!(\[|--)?|<=?)?|\^=?|\!=?=?|[a-zA-Z_$][a-zA-Z0-9_$]*|[0-9]+([xX][0-9a-fA-F]+|\.[0-9]*)?([eE][+-]?[0-9]+)?)/,
	rx = /^(\\[^\x00-\x1f]|\[(\\[^\x00-\x1f]|[^\x00-\x1f\\\/])*\]|[^\x00-\x1f\\\/\[])+\/[gim]*/,
	lx = /\*\/|\/\*/,
	ix = /^([a-zA-Z_$][a-zA-Z0-9_$]*$)/,
	jx = /(javascript|jscript|ecmascript)\s*:/i;
	Object.prototype.begetObject = function() {
		function F() {}
		F.prototype = this;
		return new F();
	};
	Object.prototype.combine = function(o) {
		var n;
		for (n in o) {
			if (o.hasOwnProperty(n)) {
				this[n] = o[n];
			}
		}
	};
	String.prototype.entityify = function() {
		return this.replace(/&/g, '&amp;').replace(/</g, '&lt;').replace(/>/g, '&gt;');
	};
	String.prototype.isAlpha = function() {
		return (this >= 'a' && this <= 'z\uffff') || (this >= 'A' && this <= 'Z\uffff');
	};
	String.prototype.isDigit = function() {
		return (this >= '0' && this <= '9');
	};
	String.prototype.supplant = function(o) {
		return this.replace(/{([^{}]*)}/g, function(a, b) {
			var r = o[b];
			return typeof r === 'string' || typeof r === 'number' ? r: a;
		});
	};
	String.prototype.name = function() {
		if (ix.test(this)) {
			return this;
		}
		if (/[&<"\\\x00-\x1f]/.test(this)) {
			return '"' + this.replace(/[&<"\\\x00-\x1f]/g, function(a) {
				var c = escapes[a];
				if (c) {
					return c;
				}
				c = a.charCodeAt();
				return '\\u00' + Math.floor(c / 16).toString(16) + (c % 16).toString(16);
			}) + '"';
		}
		return '"' + this + '"';
	};
	function populateGlobals() {
		if (option.rhino) {
			globals.combine(rhino);
		}
		if (option.browser) {
			globals.combine(browser);
		}
		if (option.widget) {
			globals.combine(widget);
		}
	}
	function quit(m, l, ch) {
		throw {
			name: 'JSLintError',
			line: l,
			character: ch,
			message: m + " (" + Math.floor((l / lines.length) * 100) + "% scanned)."
		};
	}
	function warning(m, t, a, b, c, d) {
		var ch, l, w;
		t = t || nexttoken;
		if (t.id === '(end)') {
			t = token;
		}
		l = t.line || 0;
		ch = t.from || 0;
		w = {
			id: '(error)',
			raw: m,
			evidence: lines[l] || '',
			line: l,
			character: ch,
			a: a,
			b: b,
			c: c,
			d: d
		};
		w.reason = m.supplant(w);
		JSLINT.errors.push(w);
		if (option.passfail) {
			quit('Stopping. ', l, ch);
		}
		warnings += 1;
		if (warnings === 50) {
			quit("Too many errors.", l, ch);
		}
		return w;
	}
	function warningAt(m, l, ch, a, b, c, d) {
		return warning(m, {
			line: l,
			from: ch
		},
		a, b, c, d);
	}
	function error(m, t, a, b, c, d) {
		var w = warning(m, t, a, b, c, d);
		quit("Stopping, unable to continue.", w.line, w.character);
	}
	function errorAt(m, l, ch, a, b, c, d) {
		return error(m, {
			line: l,
			from: ch
		},
		a, b, c, d);
	}
	var lex = function() {
		var character, from, line, s;
		function nextLine() {
			line += 1;
			if (line >= lines.length) {
				return false;
			}
			character = 0;
			s = lines[line];
			return true;
		}
		function it(type, value) {
			var i, t;
			if (option.adsafe && adsafe[value.toLowerCase()] === true) {
				warning("Adsafe restricted word '{a}'.", {
					line: line,
					from: character
				},
				value);
			}
			if (type === '(punctuator)' || (type === '(identifier)' && syntax.hasOwnProperty(value))) {
				t = syntax[value];
				if (!t.id) {
					t = syntax[type];
				}
			} else {
				t = syntax[type];
			}
			t = t.begetObject();
			if (type === '(string)') {
				if (/(javascript|jscript|ecmascript)\s*:/i.test(value)) {
					warningAt("JavaScript URL.", line, from);
				}
			}
			t.value = value;
			t.line = line;
			t.character = character;
			t.from = from;
			i = t.id;
			prereg = i && (('(,=:[!&|?{};'.indexOf(i.charAt(i.length - 1)) >= 0) || i === 'return');
			return t;
		}
		return {
			init: function(source) {
				if (typeof source === 'string') {
					lines = source.split( /\r\n|\r|\n/ );
				} else {
					lines = source;
				}
				line = 0;
				character = 0;
				from = 0;
				s = lines[0];
			},
			token: function() {
				var c, d, i, l, r, t;
				function match(x) {
					var r = x.exec(s),
					r1;
					if (r) {
						l = r[0].length;
						r1 = r[1];
						c = r1.charAt(0);
						s = s.substr(l);
						character += l;
						from = character - r1.length;
						return r1;
					}
				}
				function string(x) {
					var c, j, r = '';
					if (jsonmode && x !== '"') {
						warningAt("Strings must use doublequote.", line, character);
					}
					if (xmode === x || xmode === 'string') {
						return it('(punctuator)', x);
					}
					function esc(n) {
						var i = parseInt(s.substr(j + 1, n), 16);
						j += n;
						if (i >= 32 && i <= 127 && i !== 34 && i !== 92 && i !== 39) {
							warningAt("Unnecessary escapement.", line, character);
						}
						character += n;
						c = String.fromCharCode(i);
					}
					for (j = 0; j < s.length; j += 1) {
						c = s.charAt(j);
						if (c === x) {
							character += 1;
							s = s.substr(j + 1);
							return it('(string)', r, x);
						}
						if (c < ' ') {
							if (c === '\n' || c === '\r') {
								break;
							}
							warningAt("Control character in string: {a}.", line, character + j, s.substring(0, j));
						} else if (c === '<') {
							if (s.charAt(j + 1) === '/' && xmode && xmode !== 'CDATA') {
								warningAt("Expected '<\\/' and instead saw '</'.", line, character);
							}
						} else if (c === '\\') {
							j += 1;
							character += 1;
							c = s.charAt(j);
							switch (c) {
							case '\\':
							case '\'':
							case '"':
							case '/':
								break;
							case 'b':
								c = '\b';
								break;
							case 'f':
								c = '\f';
								break;
							case 'n':
								c = '\n';
								break;
							case 'r':
								c = '\r';
								break;
							case 't':
								c = '\t';
								break;
							case 'u':
								esc(4);
								break;
							case 'v':
								c = '\v';
								break;
							case 'x':
								if (jsonmode) {
									warningAt("Avoid \\x-.", line, character);
								}
								esc(2);
								break;
							default:
								warningAt("Bad escapement.", line, character);
							}
						}
						r += c;
						character += 1;
					}
					errorAt("Unclosed string.", line, from);
				}
				for (;;) {
					if (!s) {
						return it(nextLine() ? '(endline)': '(end)', '');
					}
					t = match(tx);
					if (!t) {
						t = '';
						c = '';
						while (s && s < '!') {
							s = s.substr(1);
						}
						if (s) {
							errorAt("Unexpected '{a}'.", line, character, s.substr(0, 1));
						}
					}
					if (c.isAlpha() || c === '_' || c === '$') {
						return it('(identifier)', t);
					}
					if (c.isDigit()) {
						if (!isFinite(Number(t))) {
							warningAt("Bad number '{a}'.", line, character, t);
						}
						if (s.substr(0, 1).isAlpha()) {
							warningAt("Missing space after '{a}'.", line, character, t);
						}
						if (c === '0') {
							d = t.substr(1, 1);
							if (d.isDigit()) {
								warningAt("Don't use extra leading zeros '{a}'.", line, character, t);
							} else if (jsonmode && (d === 'x' || d === 'X')) {
								warningAt("Avoid 0x-. '{a}'.", line, character, t);
							}
						}
						if (t.substr(t.length - 1) === '.') {
							warningAt("A trailing decimal point can be confused with a dot '{a}'.", line, character, t);
						}
						return it('(number)', t);
					}
					switch (t) {
					case '"':
					case "'":
						return string(t);
					case '//':
						s = '';
						break;
					case '/*':
						for (;;) {
							i = s.search(lx);
							if (i >= 0) {
								break;
							}
							if (!nextLine()) {
								errorAt("Unclosed comment.", line, character);
							}
						}
						character += i + 2;
						if (s.substr(i, 1) === '/') {
							errorAt("Nested comment.", line, character);
						}
						s = s.substr(i + 2);
						break;
					case '/*extern':
					case '/*global':
					case '/*members':
					case '/*member':
					case '/*jslint':
					case '*/':
						return {
							value:
							t,
							type: 'special',
							line: line,
							character: character,
							from: from
						};
					case '':
						break;
					case '/':
						if (prereg) {
							r = rx.exec(s);
							if (r) {
								c = r[0];
								l = c.length;
								character += l;
								s = s.substr(l);
								return it('(regex)', c);
							}
							errorAt("Bad regular expression.", line, character);
						}
						return it('(punctuator)', t);
					default:
						return it('(punctuator)', t);
					}
				}
			},
			skip: function(p) {
				var i, t = p;
				if (nexttoken.id) {
					if (!t) {
						t = '';
						if (nexttoken.id.substr(0, 1) === '<') {
							lookahead.push(nexttoken);
							return true;
						}
					} else if (nexttoken.id.indexOf(t) >= 0) {
						return true;
					}
				}
				token = nexttoken;
				nexttoken = syntax['(end)'];
				for (;;) {
					i = s.indexOf(t || '<');
					if (i >= 0) {
						character += i + t.length;
						s = s.substr(i + t.length);
						return true;
					}
					if (!nextLine()) {
						break;
					}
				}
				return false;
			}
		};
	} ();
	function addlabel(t, type) {
		if (t === 'hasOwnProperty') {
			error("'hasOwnProperty' is a really bad name.");
		}
		if (funct === true) {
			scope[t] = true;
		} else {
			if (funct.hasOwnProperty(t)) {
				warning(funct[t] === true ? "'{a}' was used before it was defined.": "'{a}' is already defined.", nexttoken, t);
			}
			scope[t] = funct;
			funct[t] = type;
		}
	}
	function doOption() {
		var b, obj, filter, t, v;
		switch (nexttoken.value) {
		case '*/':
			error("Unbegun comment.");
			break;
		case '/*extern':
		case '/*global':
			obj = globals;
			break;
		case '/*members':
		case '/*member':
			if (!membersOnly) {
				membersOnly = {};
			}
			obj = membersOnly;
			break;
		case '/*jslint':
			if (option.adsafe) {
				error("Adsafe restriction.");
			}
			obj = option;
			filter = allOptions;
		}
		for (;;) {
			t = lex.token();
			if (t.id === ',') {
				t = lex.token();
			}
			while (t.id === '(endline)') {
				t = lex.token();
			}
			if (t.type === 'special' && t.value === '*/') {
				break;
			}
			if (t.type !== '(string)' && t.type !== '(identifier)') {
				error("Bad option.", t);
			}
			if (filter) {
				if (filter[t.value] !== true) {
					error("Bad option.", t);
				}
				v = lex.token();
				if (v.id !== ':') {
					error("Expected '{a}' and instead saw '{b}'.", t, ':', t.value);
				}
				v = lex.token();
				if (v.value === 'true') {
					b = true;
				} else if (v.value === 'false') {
					b = false;
				} else {
					error("Expected '{a}' and instead saw '{b}'.", t, 'true', t.value);
				}
			} else {
				b = true;
			}
			obj[t.value] = b;
		}
		if (filter) {
			populateGlobals();
		}
	}
	function peek(p) {
		var i = p || 0,
		j = 0,
		t;
		while (j <= i) {
			t = lookahead[j];
			if (!t) {
				t = lookahead[j] = lex.token();
			}
			j += 1;
		}
		return t;
	}
	var badbreak = {
		')': true,
		']': true,
		'++': true,
		'--': true
	};
	function advance(id, t) {
		var l;
		switch (token.id) {
		case '(number)':
			if (nexttoken.id === '.') {
				warning("A dot following a number can be confused with a decimal point.", token);
			}
			break;
		case '-':
			if (nexttoken.id === '-' || nexttoken.id === '--') {
				warning("Confusing minusses.");
			}
			break;
		case '+':
			if (nexttoken.id === '+' || nexttoken.id === '++') {
				warning("Confusing plusses.");
			}
			break;
		}
		if (token.type === '(string)' || token.identifier) {
			anonname = token.value;
		}
		if (id && nexttoken.id !== id) {
			if (t) {
				if (nexttoken.id === '(end)') {
					warning("Unmatched '{a}'.", t, t.id);
				} else {
					warning("Expected '{a}' to match '{b}' from line {c} and instead saw '{d}'.", nexttoken, id, t.id, t.line + 1, nexttoken.value);
				}
			} else {
				warning("Expected '{a}' and instead saw '{b}'.", nexttoken, id, nexttoken.value);
			}
		}
		prevtoken = token;
		token = nexttoken;
		for (;;) {
			nexttoken = lookahead.shift() || lex.token();
			if (nexttoken.type === 'special') {
				doOption();
			} else {
				if (nexttoken.id === '<![') {
					if (xtype === 'html') {
						error("Unexpected '{a}'.", nexttoken, '<![');
					}
					if (xmode === 'script') {
						nexttoken = lex.token();
						if (nexttoken.value !== 'CDATA') {
							error("Missing '{a}'.", nexttoken, 'CDATA');
						}
						nexttoken = lex.token();
						if (nexttoken.id !== '[') {
							error("Missing '{a}'.", nexttoken, '[');
						}
						xmode = 'CDATA';
					} else if (xmode === 'xml') {
						lex.skip(']]>');
					} else {
						error("Unexpected '{a}'.", nexttoken, '<![');
					}
				} else if (nexttoken.id === ']]>') {
					if (xmode === 'CDATA') {
						xmode = 'script';
					} else {
						error("Unexpected '{a}'.", nexttoken, ']]>');
					}
				} else if (nexttoken.id !== '(endline)') {
					break;
				}
				if (xmode === '"' || xmode === "'") {
					error("Missing '{a}'.", token, xmode);
				}
				l = ! xmode && ! option.laxbreak && (token.type === '(string)' || token.type === '(number)' || token.type === '(identifier)' || badbreak[token.id]);
			}
		}
		if (l) {
			switch (nexttoken.id) {
			case '{':
			case '}':
			case ']':
				break;
			case ')':
				switch (token.id) {
				case ')':
				case '}':
				case ']':
					break;
				default:
					warning("Line breaking error '{a}'.", token, ')');
				}
				break;
			default:
				warning("Line breaking error '{a}'.", token, token.value);
			}
		}
		if (xtype === 'widget' && xmode === 'script' && nexttoken.id) {
			l = nexttoken.id.charAt(0);
			if (l === '<' || l === '&') {
				nexttoken.nud = nexttoken.led = null;
				nexttoken.lbp = 0;
				nexttoken.reach = true;
			}
		}
	}
	function parse(rbp, initial) {
		var left;
		var o;
		if (nexttoken.id === '(end)') {
			error("Unexpected early end of program.", token);
		}
		advance();
		if (initial) {
			anonname = 'anonymous';
			verb = token.value;
		}
		if (initial && token.fud) {
			token.fud();
		} else {
			if (token.nud) {
				o = token.exps;
				left = token.nud();
			} else {
				if (nexttoken.type === '(number)' && token.id === '.') {
					warning("A leading decimal point can be confused with a dot: '.{a}'.", token, nexttoken.value);
					advance();
					return token;
				} else {
					error("Expected an identifier and instead saw '{a}'.", token, token.id);
				}
			}
			while (rbp < nexttoken.lbp) {
				o = nexttoken.exps;
				advance();
				if (token.led) {
					left = token.led(left);
				} else {
					error("Expected an operator and instead saw '{a}'.", token, token.id);
				}
			}
			if (initial && ! o) {
				warning("Expected an assignment or function call and instead saw an expression.", token);
			}
		}
		if (!option.evil && left && left.value === 'eval') {
			warning("eval is evil.", left);
		}
		return left;
	}
	function adjacent(left, right) {
		left = left || token;
		right = right || nexttoken;
		if (option.white) {
			if (left.character !== right.from) {
				warning("Unexpected space after '{a}'.", nexttoken, left.value);
			}
		}
	}
	function nospace(left, right) {
		left = left || token;
		right = right || nexttoken;
		if (option.white) {
			if (left.line === right.line) {
				adjacent(left, right);
			}
		}
	}
	function nonadjacent(left, right) {
		left = left || token;
		right = right || nexttoken;
		if (option.white) {
			if (left.character === right.from) {
				warning("Missing space after '{a}'.", nexttoken, left.value);
			}
		}
	}
	function indentation(bias) {
		var i;
		if (option.white && nexttoken.id !== '(end)') {
			i = indent + (bias || 0);
			if (nexttoken.from !== i) {
				warning("Expected '{a}' to have an indentation of {b} instead of {c}.", nexttoken, nexttoken.value, i, nexttoken.from);
			}
		}
	}
	function symbol(s, p) {
		return syntax[s] || (syntax[s] = {
			id: s,
			lbp: p,
			value: s
		});
	}
	function delim(s) {
		return symbol(s, 0);
	}
	function stmt(s, f) {
		var x = delim(s);
		x.identifier = x.reserved = true;
		x.fud = f;
		return x;
	}
	function blockstmt(s, f) {
		var x = stmt(s, f);
		x.block = true;
		return x;
	}
	function reserveName(x) {
		var c = x.id.charAt(0);
		if ((c >= 'a' && c <= 'z') || (c >= 'A' && c <= 'Z')) {
			x.identifier = x.reserved = true;
		}
		return x;
	}
	function prefix(s, f) {
		var x = symbol(s, 150);
		reserveName(x);
		x.nud = (typeof f === 'function') ? f: function() {
			if (option.plusplus && (this.id === '++' || this.id === '--')) {
				warning("Unexpected use of '{a}'.", this, this.id);
			}
			parse(150);
			return this;
		};
		return x;
	}
	function type(s, f) {
		var x = delim(s);
		x.type = s;
		x.nud = f;
		return x;
	}
	function reserve(s, f) {
		var x = type(s, f);
		x.identifier = x.reserved = true;
		return x;
	}
	function reservevar(s) {
		return reserve(s, function() {
			return this;
		});
	}
	function infix(s, f, p) {
		var x = symbol(s, p);
		reserveName(x);
		x.led = (typeof f === 'function') ? f: function(left) {
			nonadjacent(prevtoken, token);
			nonadjacent(token, nexttoken);
			return [this.id, left, parse(p)];
		};
		return x;
	}
	function relation(s, f) {
		var x = symbol(s, 100);
		x.led = function(left) {
			nonadjacent(prevtoken, token);
			nonadjacent(token, nexttoken);
			var right = parse(100);
			if ((left && left.id === 'NaN') || (right && right.id === 'NaN')) {
				warning("Use the isNaN function to compare with NaN.", this);
			} else if (f) {
				f.apply(this, [left, right]);
			}
			return [this.id, left, right];
		};
		return x;
	}
	function isPoorRelation(node) {
		return (node.type === '(number)' && ! + node.value) || (node.type === '(string)' && ! node.value) || node.type === 'true' || node.type === 'false' || node.type === 'undefined' || node.type === 'null';
	}
	function assignop(s, f) {
		symbol(s, 20).exps = true;
		return infix(s, function(left) {
			nonadjacent(prevtoken, token);
			nonadjacent(token, nexttoken);
			if (left) {
				if (left.id === '.' || left.id === '[' || (left.identifier && ! left.reserved)) {
					parse(19);
					return left;
				}
				if (left === syntax['function']) {
					warning("Expected an identifier in an assignment and instead saw a function invocation.", token);
				}
			}
			error("Bad assignment.", this);
		},
		20);
	}
	function bitwise(s, f, p) {
		var x = symbol(s, p);
		reserveName(x);
		x.led = (typeof f === 'function') ? f: function(left) {
			if (option.bitwise) {
				warning("Unexpected use of '{a}'.", this, this.id);
			}
			nonadjacent(prevtoken, token);
			nonadjacent(token, nexttoken);
			return [this.id, left, parse(p)];
		};
		return x;
	}
	function bitwiseassignop(s) {
		symbol(s, 20).exps = true;
		return infix(s, function(left) {
			if (option.bitwise) {
				warning("Unexpected use of '{a}'.", this, this.id);
			}
			nonadjacent(prevtoken, token);
			nonadjacent(token, nexttoken);
			if (left) {
				if (left.id === '.' || left.id === '[' || (left.identifier && ! left.reserved)) {
					parse(19);
					return left;
				}
				if (left === syntax['function']) {
					warning("Expected an identifier in an assignment, and instead saw a function invocation.", token);
				}
			}
			error("Bad assignment.", this);
		},
		20);
	}
	function suffix(s, f) {
		var x = symbol(s, 150);
		x.led = function(left) {
			if (option.plusplus) {
				warning("Unexpected use of '{a}'.", this, this.id);
			}
			return [f, left];
		};
		return x;
	}
	function optionalidentifier() {
		if (nexttoken.reserved) {
			warning("Expected an identifier and instead saw '{a}' (a reserved word).", nexttoken, nexttoken.id);
		}
		if (nexttoken.identifier) {
			if (option.nomen) {
				if (nexttoken.value.charAt(0) === '_' || nexttoken.value.indexOf('$') >= 0) {
					warning("Unexpected characters in '{a}'.", nexttoken, nexttoken.value);
				}
			}
			advance();
			return token.value;
		}
	}
	function identifier() {
		var i = optionalidentifier();
		if (i) {
			return i;
		}
		if (token.id === 'function' && nexttoken.id === '(') {
			warning("Missing name in function statement.");
		} else {
			error("Expected an identifier and instead saw '{a}'.", nexttoken, nexttoken.value);
		}
	}
	function reachable(s) {
		var i = 0;
		var t;
		if (nexttoken.id !== ';' || noreach) {
			return;
		}
		for (;;) {
			t = peek(i);
			if (t.reach) {
				return;
			}
			if (t.id !== '(endline)') {
				if (t.id === 'function') {
					warning("Inner functions should be listed at the top of the outer function.", t);
					break;
				}
				warning("Unreachable '{a}' after '{b}'.", t, t.value, s);
				break;
			}
			i += 1;
		}
	}
	function statement() {
		var i = indent,
		s = scope,
		t = nexttoken;
		if (t.id === ';') {
			warning("Unnecessary semicolon.", t);
			advance(';');
			return;
		}
		if (t.identifier && ! t.reserved && peek().id === ':') {
			advance();
			advance(':');
			scope = s.begetObject();
			addlabel(t.value, 'label');
			if (!nexttoken.labelled) {
				warning("Label '{a}' on {b} statement.", nexttoken, t.value, nexttoken.value);
			}
			if (jx.test(t.value + ':')) {
				warning("Label '{a}' looks like a javascript url.", t, t.value);
			}
			nexttoken.label = t.value;
			t = nexttoken;
		}
		parse(0, true);
		if (!t.block) {
			if (nexttoken.id !== ';') {
				warningAt("Missing semicolon.", token.line, token.from + token.value.length);
			} else {
				adjacent(token, nexttoken);
				advance(';');
				nonadjacent(token, nexttoken);
			}
		}
		indent = i;
		scope = s;
	}
	function statements() {
		while (!nexttoken.reach && nexttoken.id !== '(end)') {
			if (nexttoken.id === ';') {
				warning("Unnecessary semicolon.");
				advance(';');
			}
			indentation();
			statement();
		}
	}
	function block(f) {
		var b = inblock,
		s = scope;
		inblock = f;
		if (f) {
			scope = scope.begetObject();
		}
		nonadjacent(token, nexttoken);
		var t = nexttoken;
		if (nexttoken.id === '{') {
			advance('{');
			if (nexttoken.id !== '}' || token.line !== nexttoken.line) {
				indent += 4;
				if (!f && nexttoken.from === indent + 4) {
					indent += 4;
				}
				statements();
				indent -= 4;
				indentation();
			}
			advance('}', t);
		} else {
			warning("Expected '{a}' and instead saw '{b}'.", nexttoken, '{', nexttoken.value);
			noreach = true;
			statement();
			noreach = false;
		}
		verb = null;
		scope = s;
		inblock = b;
	}
	function idValue() {
		return this;
	}
	function countMember(m) {
		if (membersOnly && membersOnly[m] !== true) {
			warning("Unexpected member '{a}'.", nexttoken, m);
		}
		if (typeof member[m] === 'number') {
			member[m] += 1;
		} else {
			member[m] = 1;
		}
	}
	var scriptstring = {
		onblur: true,
		onchange: true,
		onclick: true,
		ondblclick: true,
		onfocus: true,
		onkeydown: true,
		onkeypress: true,
		onkeyup: true,
		onload: true,
		onmousedown: true,
		onmousemove: true,
		onmouseout: true,
		onmouseover: true,
		onmouseup: true,
		onreset: true,
		onselect: true,
		onsubmit: true,
		onunload: true
	};
	var xmltype = {
		HTML: {
			doBegin: function(n) {
				if (!option.cap) {
					warning("HTML case error.");
				}
				xmltype.html.doBegin();
			}
		},
		html: {
			doBegin: function(n) {
				xtype = 'html';
				xmltype.html.script = false;
				option.browser = true;
				populateGlobals();
			},
			doTagName: function(n, p) {
				var i;
				var t = xmltype.html.tag[n];
				var x;
				if (!t) {
					error("Unrecognized tag '<{a}>'.", nexttoken, n === n.toLowerCase() ? n: n + ' (capitalization error)');
				}
				x = t.parent;
				if (x) {
					if (x.indexOf(' ' + p + ' ') < 0) {
						error("A '<{a}>' must be within '<{b}>'.", token, n, x);
					}
				} else {
					i = stack.length;
					do {
						if (i <= 0) {
							error("A '<{a}>' must be within '<{b}>'.", token, n, 'body');
						}
						i -= 1;
					} while (stack[i].name !== 'body');
				}
				xmltype.html.script = n === 'script';
				return t.empty;
			},
			doAttribute: function(n, a) {
				if (n === 'script') {
					if (a === 'src') {
						xmltype.html.script = false;
						return 'string';
					} else if (a === 'language') {
						warning("The 'language' attribute is deprecated.", token);
						return false;
					}
				}
				if (a === 'href') {
					return 'href';
				}
				return scriptstring[a] && 'script';
			},
			doIt: function(n) {
				return xmltype.html.script ? 'script': n !== 'html' && xmltype.html.tag[n].special && 'special';
			},
			tag: {
				a: {},
				abbr: {},
				acronym: {},
				address: {},
				applet: {},
				area: {
					empty: true,
					parent: ' map '
				},
				b: {},
				base: {
					empty: true,
					parent: ' head '
				},
				bdo: {},
				big: {},
				blockquote: {},
				body: {
					parent: ' html noframes '
				},
				br: {
					empty: true
				},
				button: {},
				canvas: {
					parent: ' body p div th td '
				},
				caption: {
					parent: ' table '
				},
				center: {},
				cite: {},
				code: {},
				col: {
					empty: true,
					parent: ' table colgroup '
				},
				colgroup: {
					parent: ' table '
				},
				dd: {
					parent: ' dl '
				},
				del: {},
				dfn: {},
				dir: {},
				div: {},
				dl: {},
				dt: {
					parent: ' dl '
				},
				em: {},
				embed: {},
				fieldset: {},
				font: {},
				form: {},
				frame: {
					empty: true,
					parent: ' frameset '
				},
				frameset: {
					parent: ' html frameset '
				},
				h1: {},
				h2: {},
				h3: {},
				h4: {},
				h5: {},
				h6: {},
				head: {
					parent: ' html '
				},
				html: {},
				hr: {
					empty: true
				},
				i: {},
				iframe: {},
				img: {
					empty: true
				},
				input: {
					empty: true
				},
				ins: {},
				kbd: {},
				label: {},
				legend: {
					parent: ' fieldset '
				},
				li: {
					parent: ' dir menu ol ul '
				},
				link: {
					empty: true,
					parent: ' head '
				},
				map: {},
				menu: {},
				meta: {
					empty: true,
					parent: ' head noscript '
				},
				noframes: {
					parent: ' html body '
				},
				noscript: {
					parent: ' applet blockquote body button center dd del div fieldset form frameset head html iframe ins li map noframes noscript object td th '
				},
				object: {},
				ol: {},
				optgroup: {
					parent: ' select '
				},
				option: {
					parent: ' optgroup select '
				},
				p: {},
				param: {
					empty: true,
					parent: ' applet object '
				},
				pre: {},
				q: {},
				samp: {},
				script: {
					parent: ' head body p div span abbr acronym address bdo blockquote cite code del dfn em ins kbd pre samp strong table tbody td th tr var '
				},
				select: {},
				small: {},
				span: {},
				strong: {},
				style: {
					parent: ' head ',
					special: true
				},
				sub: {},
				sup: {},
				table: {},
				tbody: {
					parent: ' table '
				},
				td: {
					parent: ' tr '
				},
				textarea: {},
				tfoot: {
					parent: ' table '
				},
				th: {
					parent: ' tr '
				},
				thead: {
					parent: ' table '
				},
				title: {
					parent: ' head '
				},
				tr: {
					parent: ' table tbody thead tfoot '
				},
				tt: {},
				u: {},
				ul: {},
				'var': {}
			}
		},
		widget: {
			doBegin: function(n) {
				xtype = 'widget';
				option.widget = true;
				populateGlobals();
			},
			doTagName: function(n, p) {
				var t = xmltype.widget.tag[n];
				if (!t) {
					error("Unrecognized tag '<{a}>'.", nexttoken, n);
				}
				var x = t.parent;
				if (x.indexOf(' ' + p + ' ') < 0) {
					error("A '<{a}>' must be within '<{b}>'.", token, n, x);
				}
			},
			doAttribute: function(n, a) {
				var t = xmltype.widget.tag[a];
				if (!t) {
					error("Unrecognized attribute '<{a} {b}>'.", nexttoken, n, a);
				}
				var x = t.parent;
				if (x.indexOf(' ' + n + ' ') < 0) {
					error("Attribute '{a}' does not belong in '<{b}>'.", nexttoken, a, n);
				}
				return t.script ? 'script': a === 'name' && n !== 'setting' ? 'define': 'string';
			},
			doIt: function(n) {
				var x = xmltype.widget.tag[n];
				return x && x.script && 'script';
			},
			tag: {
				"about-box": {
					parent: ' widget '
				},
				"about-image": {
					parent: ' about-box '
				},
				"about-text": {
					parent: ' about-box '
				},
				"about-version": {
					parent: ' about-box '
				},
				action: {
					parent: ' widget ',
					script: true
				},
				alignment: {
					parent: ' canvas frame image scrollbar text textarea window '
				},
				anchorStyle: {
					parent: ' text '
				},
				author: {
					parent: ' widget '
				},
				autoHide: {
					parent: ' scrollbar '
				},
				beget: {
					parent: ' canvas frame image scrollbar text window '
				},
				bgColor: {
					parent: ' text textarea '
				},
				bgColour: {
					parent: ' text textarea '
				},
				bgOpacity: {
					parent: ' text textarea '
				},
				canvas: {
					parent: ' frame window '
				},
				checked: {
					parent: ' image menuItem '
				},
				clipRect: {
					parent: ' image '
				},
				color: {
					parent: ' about-text about-version shadow text textarea '
				},
				colorize: {
					parent: ' image '
				},
				colour: {
					parent: ' about-text about-version shadow text textarea '
				},
				columns: {
					parent: ' textarea '
				},
				company: {
					parent: ' widget '
				},
				contextMenuItems: {
					parent: ' canvas frame image scrollbar text textarea window '
				},
				copyright: {
					parent: ' widget '
				},
				data: {
					parent: ' about-text about-version text textarea '
				},
				debug: {
					parent: ' widget '
				},
				defaultValue: {
					parent: ' preference '
				},
				defaultTracking: {
					parent: ' widget '
				},
				description: {
					parent: ' preference '
				},
				directory: {
					parent: ' preference '
				},
				editable: {
					parent: ' textarea '
				},
				enabled: {
					parent: ' menuItem '
				},
				extension: {
					parent: ' preference '
				},
				file: {
					parent: ' action preference '
				},
				fillMode: {
					parent: ' image '
				},
				font: {
					parent: ' about-text about-version text textarea '
				},
				fontStyle: {
					parent: ' textarea '
				},
				frame: {
					parent: ' frame window '
				},
				group: {
					parent: ' preference '
				},
				hAlign: {
					parent: ' canvas frame image scrollbar text textarea '
				},
				handleLinks: {
					parent: ' textArea '
				},
				height: {
					parent: ' canvas frame image scrollbar text textarea window '
				},
				hidden: {
					parent: ' preference '
				},
				hLineSize: {
					parent: ' frame '
				},
				hOffset: {
					parent: ' about-text about-version canvas frame image scrollbar shadow text textarea window '
				},
				hotkey: {
					parent: ' widget '
				},
				hRegistrationPoint: {
					parent: ' canvas frame image scrollbar text '
				},
				hScrollBar: {
					parent: ' frame '
				},
				hslAdjustment: {
					parent: ' image '
				},
				hslTinting: {
					parent: ' image '
				},
				icon: {
					parent: ' preferenceGroup '
				},
				id: {
					parent: ' canvas frame hotkey image preference text textarea timer scrollbar widget window '
				},
				image: {
					parent: ' about-box frame window widget '
				},
				interval: {
					parent: ' action timer '
				},
				key: {
					parent: ' hotkey '
				},
				kind: {
					parent: ' preference '
				},
				level: {
					parent: ' window '
				},
				lines: {
					parent: ' textarea '
				},
				loadingSrc: {
					parent: ' image '
				},
				locked: {
					parent: ' window '
				},
				max: {
					parent: ' scrollbar '
				},
				maxLength: {
					parent: ' preference '
				},
				menuItem: {
					parent: ' contextMenuItems '
				},
				min: {
					parent: ' scrollbar '
				},
				minimumVersion: {
					parent: ' widget '
				},
				minLength: {
					parent: ' preference '
				},
				missingSrc: {
					parent: ' image '
				},
				modifier: {
					parent: ' hotkey '
				},
				name: {
					parent: ' canvas frame hotkey image preference preferenceGroup scrollbar setting text textarea timer widget window '
				},
				notSaved: {
					parent: ' preference '
				},
				onClick: {
					parent: ' canvas frame image scrollbar text textarea ',
					script: true
				},
				onContextMenu: {
					parent: ' canvas frame image scrollbar text textarea window ',
					script: true
				},
				onDragDrop: {
					parent: ' canvas frame image scrollbar text textarea ',
					script: true
				},
				onDragEnter: {
					parent: ' canvas frame image scrollbar text textarea ',
					script: true
				},
				onDragExit: {
					parent: ' canvas frame image scrollbar text textarea ',
					script: true
				},
				onFirstDisplay: {
					parent: ' window ',
					script: true
				},
				onGainFocus: {
					parent: ' textarea window ',
					script: true
				},
				onKeyDown: {
					parent: ' hotkey text textarea window ',
					script: true
				},
				onKeyPress: {
					parent: ' textarea window ',
					script: true
				},
				onKeyUp: {
					parent: ' hotkey text textarea window ',
					script: true
				},
				onImageLoaded: {
					parent: ' image ',
					script: true
				},
				onLoseFocus: {
					parent: ' textarea window ',
					script: true
				},
				onMouseDown: {
					parent: ' canvas frame image scrollbar text textarea window ',
					script: true
				},
				onMouseDrag: {
					parent: ' canvas frame image scrollbar text textArea window ',
					script: true
				},
				onMouseEnter: {
					parent: ' canvas frame image scrollbar text textarea window ',
					script: true
				},
				onMouseExit: {
					parent: ' canvas frame image scrollbar text textarea window ',
					script: true
				},
				onMouseMove: {
					parent: ' canvas frame image scrollbar text textarea window ',
					script: true
				},
				onMouseUp: {
					parent: ' canvas frame image scrollbar text textarea window ',
					script: true
				},
				onMouseWheel: {
					parent: ' frame ',
					script: true
				},
				onMultiClick: {
					parent: ' canvas frame image scrollbar text textarea window ',
					script: true
				},
				onSelect: {
					parent: ' menuItem ',
					script: true
				},
				onTextInput: {
					parent: ' window ',
					script: true
				},
				onTimerFired: {
					parent: ' timer ',
					script: true
				},
				onValueChanged: {
					parent: ' scrollbar ',
					script: true
				},
				opacity: {
					parent: ' canvas frame image scrollbar shadow text textarea window '
				},
				option: {
					parent: ' preference widget '
				},
				optionValue: {
					parent: ' preference '
				},
				order: {
					parent: ' preferenceGroup '
				},
				orientation: {
					parent: ' scrollbar '
				},
				pageSize: {
					parent: ' scrollbar '
				},
				preference: {
					parent: ' widget '
				},
				preferenceGroup: {
					parent: ' widget '
				},
				remoteAsync: {
					parent: ' image '
				},
				requiredPlatform: {
					parent: ' widget '
				},
				root: {
					parent: ' window '
				},
				rotation: {
					parent: ' canvas frame image scrollbar text '
				},
				scrollbar: {
					parent: ' frame text textarea window '
				},
				scrolling: {
					parent: ' text '
				},
				scrollX: {
					parent: ' frame '
				},
				scrollY: {
					parent: ' frame '
				},
				secure: {
					parent: ' preference textarea '
				},
				setting: {
					parent: ' settings '
				},
				settings: {
					parent: ' widget '
				},
				shadow: {
					parent: ' about-text about-version text window '
				},
				size: {
					parent: ' about-text about-version text textarea '
				},
				spellcheck: {
					parent: ' textarea '
				},
				src: {
					parent: ' image '
				},
				srcHeight: {
					parent: ' image '
				},
				srcWidth: {
					parent: ' image '
				},
				style: {
					parent: ' about-text about-version canvas frame image preference scrollbar text textarea window '
				},
				subviews: {
					parent: ' frame '
				},
				superview: {
					parent: ' canvas frame image scrollbar text textarea '
				},
				text: {
					parent: ' frame text textarea window '
				},
				textarea: {
					parent: ' frame window '
				},
				timer: {
					parent: ' widget '
				},
				thumbColor: {
					parent: ' scrollbar textarea '
				},
				ticking: {
					parent: ' timer '
				},
				ticks: {
					parent: ' preference '
				},
				tickLabel: {
					parent: ' preference '
				},
				tileOrigin: {
					parent: ' image '
				},
				title: {
					parent: ' menuItem preference preferenceGroup window '
				},
				tooltip: {
					parent: ' frame image text textarea '
				},
				tracking: {
					parent: ' canvas image '
				},
				trigger: {
					parent: ' action '
				},
				truncation: {
					parent: ' text '
				},
				type: {
					parent: ' preference '
				},
				url: {
					parent: ' about-box about-text about-version '
				},
				useFileIcon: {
					parent: ' image '
				},
				vAlign: {
					parent: ' canvas frame image scrollbar text textarea '
				},
				value: {
					parent: ' preference scrollbar setting '
				},
				version: {
					parent: ' widget '
				},
				visible: {
					parent: ' canvas frame image scrollbar text textarea window '
				},
				vLineSize: {
					parent: ' frame '
				},
				vOffset: {
					parent: ' about-text about-version canvas frame image scrollbar shadow text textarea window '
				},
				vRegistrationPoint: {
					parent: ' canvas frame image scrollbar text '
				},
				vScrollBar: {
					parent: ' frame '
				},
				width: {
					parent: ' canvas frame image scrollbar text textarea window '
				},
				window: {
					parent: ' canvas frame image scrollbar text textarea widget '
				},
				wrap: {
					parent: ' text '
				},
				zOrder: {
					parent: ' canvas frame image scrollbar text textarea window '
				}
			}
		}
	};
	function xmlword(tag) {
		var w = nexttoken.value;
		if (!nexttoken.identifier) {
			if (nexttoken.id === '<') {
				if (tag) {
					error("Expected '{a}' and instead saw '{b}'.", token, '&lt;', '<');
				} else {
					error("Missing '{a}'.", token, '>');
				}
			} else if (nexttoken.id === '(end)') {
				error("Bad structure.");
			} else {
				warning("Missing quote.", token);
			}
		}
		advance();
		while (nexttoken.id === '-' || nexttoken.id === ':') {
			w += nexttoken.id;
			advance();
			if (!nexttoken.identifier) {
				error("Bad name '{a}'.", nexttoken, w + nexttoken.value);
			}
			w += nexttoken.value;
			advance();
		}
		return w;
	}
	function closetag(n) {
		return '</' + n + '>';
	}
	function xml() {
		var a, e, n, q, t;
		xmode = 'xml';
		stack = null;
		for (;;) {
			switch (nexttoken.value) {
			case '<':
				if (!stack) {
					stack = [];
				}
				advance('<');
				t = nexttoken;
				n = xmlword(true);
				t.name = n;
				if (!xtype) {
					if (xmltype[n]) {
						xmltype[n].doBegin();
						n = xtype;
						e = false;
					} else {
						if (option.fragment) {
							xmltype.html.doBegin();
							stack = [{
								name: 'body'
							}];
							e = xmltype[xtype].doTagName(n, 'body');
						} else {
							error("Unrecognized tag '<{a}>'.", nexttoken, n);
						}
					}
				} else {
					if (option.cap && xtype === 'html') {
						n = n.toLowerCase();
					}
					if (stack.length === 0) {
						error("What the hell is this?");
					}
					e = xmltype[xtype].doTagName(n, stack[stack.length - 1].name);
				}
				t.type = n;
				for (;;) {
					if (nexttoken.id === '/') {
						advance('/');
						e = true;
						break;
					}
					if (nexttoken.id && nexttoken.id.substr(0, 1) === '>') {
						break;
					}
					a = xmlword();
					switch (xmltype[xtype].doAttribute(n, a)) {
					case 'script':
						xmode = 'string';
						advance('=');
						q = nexttoken.id;
						if (q !== '"' && q !== "'") {
							error("Missing quote.");
						}
						xmode = q;
						wmode = option.white;
						option.white = false;
						advance(q);
						statements();
						option.white = wmode;
						if (nexttoken.id !== q) {
							error("Missing close quote on script attribute.");
						}
						xmode = 'xml';
						advance(q);
						break;
					case 'value':
						advance('=');
						if (!nexttoken.identifier && nexttoken.type !== '(string)' && nexttoken.type !== '(number)') {
							error("Bad value '{a}'.", nexttoken, nexttoken.value);
						}
						advance();
						break;
					case 'string':
					case 'href':
						advance('=');
						if (nexttoken.type !== '(string)') {
							error("Bad value '{a}'.", nexttoken, nexttoken.value);
						}
						advance();
						break;
					case 'define':
						advance('=');
						if (nexttoken.type !== '(string)') {
							error("Bad value '{a}'.", nexttoken, nexttoken.value);
						}
						addlabel(nexttoken.value, 'var');
						advance();
						break;
					default:
						if (nexttoken.id === '=') {
							advance('=');
							if (!nexttoken.identifier && nexttoken.type !== '(string)' && nexttoken.type !== '(number)') {}
							advance();
						}
					}
				}
				switch (xmltype[xtype].doIt(n)) {
				case 'script':
					xmode = 'script';
					advance('>');
					indent = nexttoken.from;
					statements();
					if (nexttoken.id !== '</' && nexttoken.id !== '(end)') {
						warning("Unexpected '{a}'.", nexttoken, nexttoken.id);
					}
					xmode = 'xml';
					break;
				case 'special':
					e = true;
					n = closetag(t.name);
					if (!lex.skip(n)) {
						error("Missing '{a}'.", t, n);
					}
					break;
				default:
					lex.skip('>');
				}
				if (!e) {
					stack.push(t);
				}
				break;
			case '</':
				advance('</');
				n = xmlword(true);
				t = stack.pop();
				if (!t) {
					error("Unexpected '{a}'.", nexttoken, closetag(n));
				}
				if (t.name !== n) {
					error("Expected '{a}' and instead saw '{b}'.", nexttoken, closetag(t.name), closetag(n));
				}
				if (nexttoken.id !== '>') {
					error("Missing '{a}'.", nexttoken, '>');
				}
				if (stack.length > 0) {
					lex.skip('>');
				} else {
					advance('>');
				}
				break;
			case '<!':
				for (;;) {
					advance();
					if (nexttoken.id === '>') {
						break;
					}
					if (nexttoken.id === '<' || nexttoken.id === '(end)') {
						error("Missing '{a}'.", token, '>');
					}
				}
				lex.skip('>');
				break;
			case '<!--':
				lex.skip('-->');
				break;
			case '<%':
				lex.skip('%>');
				break;
			case '<?':
				for (;;) {
					advance();
					if (nexttoken.id === '?>') {
						break;
					}
					if (nexttoken.id === '<?' || nexttoken.id === '<' || nexttoken.id === '>' || nexttoken.id === '(end)') {
						error("Missing '{a}'.", token, '?>');
					}
				}
				lex.skip('?>');
				break;
			case '<=':
			case '<<':
			case '<<=':
				error("Missing '{a}'.", nexttoken, '&lt;');
				break;
			case '(end)':
				return;
			}
			if (stack && stack.length === 0) {
				return;
			}
			if (!lex.skip('')) {
				t = stack.pop();
				if (t.value) {
					error("Missing '{a}'.", t, closetag(t.name));
				} else {
					return;
				}
			}
			advance();
		}
	}
	type('(number)', idValue);
	type('(string)', idValue);
	syntax['(identifier)'] = {
		type: '(identifier)',
		lbp: 0,
		identifier: true,
		nud: function() {
			var v = this.value,
			s = scope[v];
			if (s === funct) {
				if (funct !== true) {
					switch (funct[v]) {
					case 'unused':
						funct[v] = 'var';
						break;
					case 'label':
						warning("'{a}' is a statement label.", token, v);
						break;
					}
				}
			} else if (funct === true) {
				if (option.undef) {
					warning("'{a}' is undefined.", token, v);
				} else {
					implied[v] = true;
					globals[v] = true;
				}
			} else {
				switch (funct[v]) {
				case 'closure':
				case 'function':
				case 'var':
				case 'unused':
					warning("'{a}' used out of scope.", token, v);
					break;
				case 'label':
					warning("'{a}' is a statement label.", token, v);
					break;
				case 'outer':
				case true:
					break;
				default:
					if (s === true) {
						funct[v] = true;
					} else if (typeof s !== 'object') {
						if (option.undef) {
							warning("'{a}' is undefined.", token, v);
						} else {
							implied[v] = true;
							globals[v] = true;
							funct[v] = true;
						}
					} else {
						switch (s[v]) {
						case 'function':
						case 'var':
						case 'unused':
							s[v] = 'closure';
							funct[v] = 'outer';
							break;
						case 'closure':
						case 'parameter':
							funct[v] = 'outer';
							break;
						case 'label':
							warning("'{a}' is a statement label.", token, v);
						}
					}
				}
			}
			return this;
		},
		led: function() {
			error("Expected an operator and instead saw '{a}'.", nexttoken, nexttoken.value);
		}
	};
	type('(regex)', function() {
		return [this.id, this.value, this.flags];
	});
	delim('(endline)');
	delim('(begin)');
	delim('(end)').reach = true;
	delim('</').reach = true;
	delim('<![').reach = true;
	delim('<%');
	delim('<?');
	delim('<!');
	delim('<!--');
	delim('%>');
	delim('?>');
	delim('(error)').reach = true;
	delim('}').reach = true;
	delim(')');
	delim(']');
	delim(']]>').reach = true;
	delim('"').reach = true;
	delim("'").reach = true;
	delim(';');
	delim(':').reach = true;
	delim(',');
	reserve('else');
	reserve('case').reach = true;
	reserve('catch');
	reserve('default').reach = true;
	reserve('finally');
	reservevar('arguments');
	reservevar('eval');
	reservevar('false');
	reservevar('Infinity');
	reservevar('NaN');
	reservevar('null');
	reservevar('this');
	reservevar('true');
	reservevar('undefined');
	assignop('=', 'assign', 20);
	assignop('+=', 'assignadd', 20);
	assignop('-=', 'assignsub', 20);
	assignop('*=', 'assignmult', 20);
	assignop('/=', 'assigndiv', 20).nud = function() {
		error("A regular expression literal can be confused with '/='.");
	};
	assignop('%=', 'assignmod', 20);
	bitwiseassignop('&=', 'assignbitand', 20);
	bitwiseassignop('|=', 'assignbitor', 20);
	bitwiseassignop('^=', 'assignbitxor', 20);
	bitwiseassignop('<<=', 'assignshiftleft', 20);
	bitwiseassignop('>>=', 'assignshiftright', 20);
	bitwiseassignop('>>>=', 'assignshiftrightunsigned', 20);
	infix('?', function(left) {
		parse(10);
		advance(':');
		parse(10);
	},
	30);
	infix('||', 'or', 40);
	infix('&&', 'and', 50);
	bitwise('|', 'bitor', 70);
	bitwise('^', 'bitxor', 80);
	bitwise('&', 'bitand', 90);
	relation('==', function(left, right) {
		if (option.eqeqeq) {
			warning("Expected '{a}' and instead saw '{b}'.", this, '===', '==');
		} else if (isPoorRelation(left)) {
			warning("Use '{a}' to compare with '{b}'.", this, '===', left.value);
		} else if (isPoorRelation(right)) {
			warning("Use '{a}' to compare with '{b}'.", this, '===', right.value);
		}
		return ['==', left, right];
	});
	relation('===');
	relation('!=', function(left, right) {
		if (option.eqeqeq) {
			warning("Expected '{a}' and instead saw '{b}'.", this, '!==', '!=');
		} else if (isPoorRelation(left)) {
			warning("Use '{a}' to compare with '{b}'.", this, '!==', left.value);
		} else if (isPoorRelation(right)) {
			warning("Use '{a}' to compare with '{b}'.", this, '!==', right.value);
		}
		return ['!=', left, right];
	});
	relation('!==');
	relation('<');
	relation('>');
	relation('<=');
	relation('>=');
	bitwise('<<', 'shiftleft', 120);
	bitwise('>>', 'shiftright', 120);
	bitwise('>>>', 'shiftrightunsigned', 120);
	infix('in', 'in', 120);
	infix('instanceof', 'instanceof', 120);
	infix('+', function(left) {
		nonadjacent(prevtoken, token);
		nonadjacent(token, nexttoken);
		var right = parse(130);
		if (left && right && left.id === '(string)' && right.id === '(string)') {
			left.value += right.value;
			left.character = right.character;
			if (option.adsafe && adsafe[left.value.toLowerCase()] === true) {
				warning("Adsafe restricted word '{a}'.", left, left.value);
			}
			if (jx.test(left.value)) {
				warning("JavaScript URL.", left);
			}
			return left;
		}
		return [this.id, left, right];
	},
	130);
	prefix('+', 'num');
	infix('-', 'sub', 130);
	prefix('-', 'neg');
	infix('*', 'mult', 140);
	infix('/', 'div', 140);
	infix('%', 'mod', 140);
	suffix('++', 'postinc');
	prefix('++', 'preinc');
	syntax['++'].exps = true;
	suffix('--', 'postdec');
	prefix('--', 'predec');
	syntax['--'].exps = true;
	prefix('delete', function() {
		var p = parse(0);
		if (p.id !== '.' && p.id !== '[') {
			warning("Expected '{a}' and instead saw '{b}'.", nexttoken, '.', nexttoken.value);
		}
	}).exps = true;
	prefix('~', function() {
		if (option.bitwise) {
			warning("Unexpected '{a}'.", this, '~');
		}
		parse(150);
		return this;
	});
	prefix('!', 'not');
	prefix('typeof', 'typeof');
	prefix('new', function() {
		var c = parse(155),
		i;
		if (c) {
			if (c.identifier) {
				c['new'] = true;
				switch (c.value) {
				case 'Object':
					warning("Use the object literal notation {}.", token);
					break;
				case 'Array':
					warning("Use the array literal notation [].", token);
					break;
				case 'Number':
				case 'String':
				case 'Boolean':
					warning("Do not use the {a} function as a constructor.", token, c.value);
					break;
				case 'Function':
					if (!option.evil) {
						warning("The Function constructor is eval.");
					}
					break;
				default:
					if (c.id !== 'function') {
						i = c.value.substr(0, 1);
						if (i < 'A' || i > 'Z') {
							warning("A constructor name should start with an uppercase letter.", token);
						}
					}
				}
			} else {
				if (c.id !== '.' && c.id !== '[' && c.id !== '(') {
					warning("Bad constructor.", token);
				}
			}
		} else {
			warning("Weird construction. Delete 'new'.", this);
		}
		adjacent(token, nexttoken);
		if (nexttoken.id === '(') {
			advance('(');
			nospace();
			if (nexttoken.id !== ')') {
				for (;;) {
					parse(10);
					if (nexttoken.id !== ',') {
						break;
					}
					advance(',');
				}
			}
			advance(')');
			nospace(prevtoken, token);
		} else {
			warning("Missing '()' invoking a constructor.");
		}
		return syntax['function'];
	});
	syntax['new'].exps = true;
	infix('.', function(left) {
		adjacent(prevtoken, token);
		var m = identifier();
		if (typeof m === 'string') {
			countMember(m);
		}
		if (!option.evil && left && left.value === 'document' && (m === 'write' || m === 'writeln')) {
			warning("document.write can be a form of eval.", left);
		}
		this.left = left;
		this.right = m;
		return this;
	},
	160);
	infix('(', function(left) {
		adjacent(prevtoken, token);
		nospace();
		var n = 0;
		var p = [];
		if (left && left.type === '(identifier)') {
			if (left.value.match(/^[A-Z](.*[a-z].*)?$/)) {
				if (left.value !== 'Number' && left.value !== 'String' && left.value !== 'Boolean' && left.value !== 'Date') {
					warning("Missing 'new' prefix when invoking a constructor.", left);
				}
			}
		}
		if (nexttoken.id !== ')') {
			for (;;) {
				p[p.length] = parse(10);
				n += 1;
				if (nexttoken.id !== ',') {
					break;
				}
				advance(',');
				nonadjacent(token, nexttoken);
			}
		}
		advance(')');
		nospace(prevtoken, token);
		if (typeof left === 'object') {
			if (left.value === 'parseInt' && n === 1) {
				warning("Missing radix parameter.", left);
			}
			if (!option.evil) {
				if (left.value === 'eval' || left.value === 'Function') {
					warning("eval is evil.", left);
				} else if (p[0] && p[0].id === '(string)' && (left.value === 'setTimeout' || left.value === 'setInterval')) {
					warning("Implied eval is evil. Pass a function instead of a string.", left);
				}
			}
			if (!left.identifier && left.id !== '.' && left.id !== '[' && left.id !== '(') {
				warning("Bad invocation.", left);
			}
		}
		return syntax['function'];
	},
	155).exps = true;
	prefix('(', function() {
		nospace();
		var v = parse(0);
		advance(')', this);
		nospace(prevtoken, token);
		return v;
	});
	infix('[', function(left) {
		nospace();
		var e = parse(0),
		s;
		if (e && e.type === '(string)') {
			countMember(e.value);
			if (ix.test(e.value)) {
				s = syntax[e.value];
				if (!s || ! s.reserved) {
					warning("['{a}'] is better written in dot notation.", e, e.value);
				}
			}
		}
		advance(']', this);
		nospace(prevtoken, token);
		this.left = left;
		this.right = e;
		return this;
	},
	160);
	prefix('[', function() {
		if (nexttoken.id === ']') {
			advance(']');
			return;
		}
		var b = token.line !== nexttoken.line;
		if (b) {
			indent += 4;
			if (nexttoken.from === indent + 4) {
				indent += 4;
			}
		}
		for (;;) {
			if (b && token.line !== nexttoken.line) {
				indentation();
			}
			parse(10);
			if (nexttoken.id === ',') {
				adjacent(token, nexttoken);
				advance(',');
				if (nexttoken.id === ',' || nexttoken.id === ']') {
					warning("Extra comma.", token);
				}
				nonadjacent(token, nexttoken);
			} else {
				if (b) {
					indent -= 4;
					indentation();
				}
				advance(']', this);
				return;
			}
		}
	},
	160);
	(function(x) {
		x.nud = function() {
			var i, s;
			if (nexttoken.id === '}') {
				advance('}');
				return;
			}
			var b = token.line !== nexttoken.line;
			if (b) {
				indent += 4;
				if (nexttoken.from === indent + 4) {
					indent += 4;
				}
			}
			for (;;) {
				if (b) {
					indentation();
				}
				i = optionalidentifier(true);
				if (!i) {
					if (nexttoken.id === '(string)') {
						i = nexttoken.value;
						if (ix.test(i)) {
							s = syntax[i];
						}
						advance();
					} else if (nexttoken.id === '(number)') {
						i = nexttoken.value.toString();
						advance();
					} else {
						error("Expected '{a}' and instead saw '{b}'.", nexttoken, '}', nexttoken.value);
					}
				}
				countMember(i);
				advance(':');
				nonadjacent(token, nexttoken);
				parse(10);
				if (nexttoken.id === ',') {
					adjacent(token, nexttoken);
					advance(',');
					if (nexttoken.id === ',' || nexttoken.id === '}') {
						warning("Extra comma.", token);
					}
					nonadjacent(token, nexttoken);
				} else {
					if (b) {
						indent -= 4;
						indentation();
					}
					advance('}', this);
					return;
				}
			}
		};
		x.fud = function() {
			error("Expected to see a statement and instead saw a block.");
		};
	})(delim('{'));
	function varstatement() {
		for (;;) {
			nonadjacent(token, nexttoken);
			addlabel(identifier(), 'unused');
			if (nexttoken.id === '=') {
				nonadjacent(token, nexttoken);
				advance('=');
				nonadjacent(token, nexttoken);
				if (peek(0).id === '=') {
					error("Variable {a} was not declared correctly.", nexttoken, nexttoken.value);
				}
				parse(20);
			}
			if (nexttoken.id !== ',') {
				return;
			}
			adjacent(token, nexttoken);
			advance(',');
			nonadjacent(token, nexttoken);
		}
	}
	stmt('var', varstatement);
	stmt('new', function() {
		error("'new' should not be used as a statement.");
	});
	function functionparams() {
		var i, t = nexttoken,
		p = [];
		advance('(');
		nospace();
		if (nexttoken.id === ')') {
			advance(')');
			nospace(prevtoken, token);
			return;
		}
		for (;;) {
			i = identifier();
			p.push(i);
			addlabel(i, 'parameter');
			if (nexttoken.id === ',') {
				advance(',');
				nonadjacent(token, nexttoken);
			} else {
				advance(')', t);
				nospace(prevtoken, token);
				return p.join(', ');
			}
		}
	}
	function doFunction(i) {
		var s = scope;
		scope = s.begetObject();
		funct = {
			'(name)': i || '"' + anonname + '"',
			'(line)': nexttoken.line + 1,
			'(context)': funct
		};
		functions.push(funct);
		if (i) {
			addlabel(i, 'function');
		}
		funct['(params)'] = functionparams();
		block(false);
		scope = s;
		funct = funct['(context)'];
	}
	blockstmt('function', function() {
		if (inblock) {
			warning("Function statements cannot be placed in blocks. Use a function expression or move the statement to the top of the outer function.", token);
		}
		var i = identifier();
		adjacent(token, nexttoken);
		addlabel(i, 'unused');
		doFunction(i);
		if (nexttoken.id === '(' && nexttoken.line === token.line) {
			error("Function statements are not invocable. Wrap the function expression in parens.");
		}
	});
	prefix('function', function() {
		var i = optionalidentifier();
		if (i) {
			adjacent(token, nexttoken);
		} else {
			nonadjacent(token, nexttoken);
		}
		doFunction(i);
	});
	blockstmt('if', function() {
		var t = nexttoken;
		advance('(');
		nonadjacent(this, t);
		nospace();
		parse(20);
		if (nexttoken.id === '=') {
			warning("Assignment in control part.");
			advance('=');
			parse(20);
		}
		advance(')', t);
		nospace(prevtoken, token);
		block(true);
		if (nexttoken.id === 'else') {
			nonadjacent(token, nexttoken);
			advance('else');
			if (nexttoken.id === 'if' || nexttoken.id === 'switch') {
				statement();
			} else {
				block(true);
			}
		}
	});
	blockstmt('try', function() {
		var b, e;
		block(true);
		if (nexttoken.id === 'catch') {
			advance('catch');
			nonadjacent(token, nexttoken);
			advance('(');
			e = nexttoken.value;
			if (nexttoken.type !== '(identifier)') {
				warning("Expected an identifier and instead saw '{a}'.", nexttoken, e);
			} else {
				addlabel(e, 'unused');
			}
			advance();
			advance(')');
			block(false);
			b = true;
		}
		if (nexttoken.id === 'finally') {
			advance('finally');
			block(false);
			return;
		} else if (!b) {
			error("Expected '{a}' and instead saw '{b}'.", nexttoken, 'catch', nexttoken.value);
		}
	});
	blockstmt('while', function() {
		var t = nexttoken;
		advance('(');
		nonadjacent(this, t);
		nospace();
		parse(20);
		if (nexttoken.id === '=') {
			warning("Assignment in control part.");
			advance('=');
			parse(20);
		}
		advance(')', t);
		nospace(prevtoken, token);
		block(true);
	}).labelled = true;
	reserve('with');
	blockstmt('switch', function() {
		var t = nexttoken;
		var g = false;
		advance('(');
		nonadjacent(this, t);
		nospace();
		this.condition = parse(20);
		advance(')', t);
		nospace(prevtoken, token);
		nonadjacent(token, nexttoken);
		t = nexttoken;
		advance('{');
		nonadjacent(token, nexttoken);
		indent += 4;
		this.cases = [];
		for (;;) {
			switch (nexttoken.id) {
			case 'case':
				switch (verb) {
				case 'break':
				case 'case':
				case 'continue':
				case 'return':
				case 'switch':
				case 'throw':
					break;
				default:
					warning("Expected a 'break' statement before 'case'.", token);
				}
				indentation( - 4);
				advance('case');
				this.cases.push(parse(20));
				g = true;
				advance(':');
				verb = 'case';
				break;
			case 'default':
				switch (verb) {
				case 'break':
				case 'continue':
				case 'return':
				case 'throw':
					break;
				default:
					warning("Expected a 'break' statement before 'default'.", token);
				}
				indentation( - 4);
				advance('default');
				g = true;
				advance(':');
				break;
			case '}':
				indent -= 4;
				indentation();
				advance('}', t);
				if (this.cases.length === 1 || this.condition.id === 'true' || this.condition.id === 'false') {
					warning("This 'switch' should be an 'if'.", this);
				}
				return;
			case '(end)':
				error("Missing '{a}'.", nexttoken, '}');
				return;
			default:
				if (g) {
					switch (token.id) {
					case ',':
						error("Each value should have its own case label.");
						return;
					case ':':
						statements();
						break;
					default:
						error("Missing ':' on a case clause.", token);
					}
				} else {
					error("Expected '{a}' and instead saw '{b}'.", nexttoken, 'case', nexttoken.value);
				}
			}
		}
	}).labelled = true;
	stmt('debugger', function() {
		if (!option.debug) {
			warning("All 'debugger' statements should be removed.");
		}
	});
	stmt('do', function() {
		block(true);
		advance('while');
		var t = nexttoken;
		nonadjacent(token, t);
		advance('(');
		nospace();
		parse(20);
		advance(')', t);
		nospace(prevtoken, token);
	}).labelled = true;
	blockstmt('for', function() {
		var t = nexttoken;
		advance('(');
		nonadjacent(this, t);
		nospace();
		if (peek(nexttoken.id === 'var' ? 1: 0).id === 'in') {
			if (nexttoken.id === 'var') {
				advance('var');
				addlabel(identifier(), 'var');
			} else {
				advance();
			}
			advance('in');
			parse(20);
			advance(')', t);
			block(true);
			return;
		} else {
			if (nexttoken.id !== ';') {
				if (nexttoken.id === 'var') {
					advance('var');
					varstatement();
				} else {
					for (;;) {
						parse(0);
						if (nexttoken.id !== ',') {
							break;
						}
						advance(',');
					}
				}
			}
			advance(';');
			if (nexttoken.id !== ';') {
				parse(20);
			}
			advance(';');
			if (nexttoken.id === ';') {
				error("Expected '{a}' and instead saw '{b}'.", nexttoken, ')', ';');
			}
			if (nexttoken.id !== ')') {
				for (;;) {
					parse(0);
					if (nexttoken.id !== ',') {
						break;
					}
					advance(',');
				}
			}
			advance(')', t);
			nospace(prevtoken, token);
			block(true);
		}
	}).labelled = true;
	function nolinebreak(t) {
		if (t.line !== nexttoken.line) {
			warning("Line breaking error '{a}'.", t, t.id);
		}
	}
	stmt('break', function() {
		var v = nexttoken.value;
		nolinebreak(this);
		if (nexttoken.id !== ';') {
			if (funct === true) {
				warning("Put '{a}' and the statement it labels in a function.", nexttoken, v);
			} else if (funct[v] !== 'label') {
				warning("'{a}' is not a statement label.", nexttoken, v);
			} else if (scope[v] !== funct) {
				warning("'{a}' is out of scope.", nexttoken, v);
			}
			advance();
		}
		reachable('break');
	});
	stmt('continue', function() {
		var v = nexttoken.value;
		nolinebreak(this);
		if (nexttoken.id !== ';') {
			if (funct === true) {
				warning("Put '{a}' and the statement it labels in a function.", nexttoken, v);
			} else if (funct[v] !== 'label') {
				warning("'{a}' is not a statement label.", nexttoken, v);
			} else if (scope[v] !== funct) {
				warning("'{a}' is out of scope.", nexttoken, v);
			}
			advance();
		}
		reachable('continue');
	});
	stmt('return', function() {
		nolinebreak(this);
		if (nexttoken.id !== ';' && ! nexttoken.reach) {
			nonadjacent(token, nexttoken);
			parse(20);
		}
		reachable('return');
	});
	stmt('throw', function() {
		nolinebreak(this);
		nonadjacent(token, nexttoken);
		parse(20);
		reachable('throw');
	});
	reserve('abstract');
	reserve('boolean');
	reserve('byte');
	reserve('char');
	reserve('class');
	reserve('const');
	reserve('double');
	reserve('enum');
	reserve('export');
	reserve('extends');
	reserve('final');
	reserve('float');
	reserve('goto');
	reserve('implements');
	reserve('import');
	reserve('int');
	reserve('interface');
	reserve('long');
	reserve('native');
	reserve('package');
	reserve('private');
	reserve('protected');
	reserve('public');
	reserve('short');
	reserve('static');
	reserve('super');
	reserve('synchronized');
	reserve('throws');
	reserve('transient');
	reserve('void');
	reserve('volatile');
	function jsonValue() {
		function jsonObject() {
			var t = nexttoken;
			advance('{');
			if (nexttoken.id !== '}') {
				for (;;) {
					if (nexttoken.id === '(end)') {
						error("Missing '}' to match '{' from line {a}.", nexttoken, t.line + 1);
					} else if (nexttoken.id === '}') {
						warning("Unexpected comma.", token);
						break;
					} else if (nexttoken.id === ',') {
						error("Unexpected comma.", nexttoken);
					} else if (nexttoken.id !== '(string)') {
						warning("Expected a string and instead saw {a}.", nexttoken, nexttoken.value);
					}
					advance();
					advance(':');
					jsonValue();
					if (nexttoken.id !== ',') {
						break;
					}
					advance(',');
				}
			}
			advance('}');
		}
		function jsonArray() {
			var t = nexttoken;
			advance('[');
			if (nexttoken.id !== ']') {
				for (;;) {
					if (nexttoken.id === '(end)') {
						error("Missing ']' to match '[' from line {a}.", nexttoken, t.line + 1);
					} else if (nexttoken.id === ']') {
						warning("Unexpected comma.", token);
						break;
					} else if (nexttoken.id === ',') {
						error("Unexpected comma.", nexttoken);
					}
					jsonValue();
					if (nexttoken.id !== ',') {
						break;
					}
					advance(',');
				}
			}
			advance(']');
		}
		switch (nexttoken.id) {
		case '{':
			jsonObject();
			break;
		case '[':
			jsonArray();
			break;
		case 'true':
		case 'false':
		case 'null':
		case '(number)':
		case '(string)':
			advance();
			break;
		case '-':
			advance('-');
			if (token.character !== nexttoken.from) {
				warning("Unexpected space after '-'.", token);
			}
			adjacent(token, nexttoken);
			advance('(number)');
			break;
		default:
			error("Expected a JSON value.", nexttoken);
		}
	}
	var itself = function(s, o) {
		option = o || {};
		JSLINT.errors = [];
		globals = standard.begetObject();
		scope = globals.begetObject();
		funct = true;
		functions = [];
		xmode = false;
		xtype = '';
		stack = null;
		member = {};
		membersOnly = null;
		implied = {};
		inblock = false;
		lookahead = [];
		indent = 0;
		jsonmode = false;
		warnings = 0;
		lex.init(s);
		prereg = true;
		prevtoken = token = nexttoken = syntax['(begin)'];
		populateGlobals();
		try {
			advance();
			if (nexttoken.value.charAt(0) === '<') {
				xml();
			} else if (nexttoken.id === '{' || nexttoken.id === '[') {
				option.laxbreak = true;
				jsonmode = true;
				jsonValue();
			} else {
				statements();
			}
			advance('(end)');
		} catch(e) {
			if (e) {
				JSLINT.errors.push({
					reason: e.message,
					line: e.line || nexttoken.line,
					character: e.character || nexttoken.from
				},
				null);
			}
		}
		return JSLINT.errors.length === 0;
	};
	itself.report = function(option) {
		var a = [],
		c,
		e,
		f,
		i,
		k,
		l,
		m = '',
		n,
		o = [],
		s,
		v,
		cl,
		va,
		un,
		ou,
		gl,
		la;
		function detail(h, s) {
			if (s.length) {
				o.push('<div><i>' + h + '</i> ' + s.sort().join(', ') + '</div>');
			}
		}
		s = [];
		for (k in implied) {
			if (implied.hasOwnProperty(k)) {
				s.push(k);
			}
		}
		k = JSLINT.errors.length;
		if (k || s.length > 0) {
			o.push('<div id=errors><i>Error:</i>');
			if (s.length > 0) {
				o.push('<p><i>Implied global:</i> ' + s.sort().join(', ') + '</p>');
				c = true;
			}
			for (i = 0; i < k; i += 1) {
				c = JSLINT.errors[i];
				if (c) {
					e = c.evidence || '';
					o.push('<p>Problem at line ' + (c.line + 1) + ' character ' + (c.character + 1) + ': ' + c.reason.entityify() + '</p><p class=evidence>' + (e && (e.length > 80 ? e.substring(0, 77) + '...': e).entityify()) + '</p>');
				}
			}
			o.push('</div>');
			if (!c) {
				return o.join('');
			}
		}
		if (!option) {
			o.push('<div id=functions>');
			s = [];
			for (k in scope) {
				if (scope.hasOwnProperty(k)) {
					s.push(k);
				}
			}
			if (s.length === 0) {
				o.push('<div><i>No new global variables introduced.</i></div>');
			} else {
				o.push('<div><i>Global</i> ' + s.join(', ') + '</div>');
			}
			for (i = 0; i < functions.length; i += 1) {
				f = functions[i];
				cl = [];
				va = [];
				un = [];
				ou = [];
				gl = [];
				la = [];
				for (k in f) {
					if (f.hasOwnProperty(k)) {
						v = f[k];
						switch (v) {
						case 'closure':
							cl.push(k);
							break;
						case 'var':
							va.push(k);
							break;
						case 'unused':
							un.push(k);
							break;
						case 'label':
							la.push(k);
							break;
						case 'outer':
							ou.push(k);
							break;
						case true:
							if (k !== '(context)') {
								gl.push(k);
							}
							break;
						}
					}
				}
				o.push('<br><div class=function><i>' + f['(line)'] + '</i> ' + (f['(name)'] || '') + '(' + (f['(params)'] || '') + ')</div>');
				detail('Closure', cl);
				detail('Variable', va);
				detail('Unused', un);
				detail('Label', la);
				detail('Outer', ou);
				detail('Global', gl);
			}
			for (k in member) {
				if (typeof member[k] === 'number') {
					a.push(k);
				}
			}
			if (a.length) {
				a = a.sort();
				m = '<br><div class=function>/*members ';
				l = 10;
				for (i = 0; i < a.length; i += 1) {
					k = a[i];
					n = k.name();
					if (l + n.length > 72) {
						o.push(m + '</div>');
						m = '<div> ';
						l = 1;
					}
					l += n.length + 2;
					if (member[k] === 1) {
						n = '<i>' + n + '</i>';
					}
					if (i < a.length - 1) {
						n += ', ';
					}
					m += n;
				}
				o.push(m + ' */</div>');
			}
			o.push('</div>');
		}
		return o.join('');
	};
	return itself;
} ();
(function(a) {
	if (!a[0]) {
		print("Usage: jslint.js file.js");
		quit(1);
	}
	var input = readFile(a[0]);
	if (!input) {
		print("jslint: Couldn't open file '" + a[0] + "'.");
		quit(1);
	}
	if (!JSLINT(input, {
		rhino: true,
		passfail: false
	})) {
		for (var i = 0; i < JSLINT.errors.length; i += 1) {
			var e = JSLINT.errors[i];
			if (e) {
        var source = (e.evidence || '').replace(/^\s*(\S*(\s+\S+)*)\s*$/, "$1");
        warn( e.reason, e.line + 1, source, e.character + 1 );
			}
		}
    quit(1);
	} else {
		// print("jslint: No problems found in " + a[0]);
		quit();
	}
})(arguments);

