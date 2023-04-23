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
// Highlighter script By Ilija Studen - http://ilija.biz/
// Modified from Woofoo forms

/*--------------------------------------------------------------------------*/

//http://www.robertnyman.com/2005/11/07/the-ultimate-getelementsbyclassname/
function getElementsByClassName(oElm, strTagName, strClassName){
	var arrElements = (strTagName == "*" && oElm.all)? oElm.all : oElm.getElementsByTagName(strTagName);
	var arrReturnElements = new Array();
	strClassName = strClassName.replace(/\-/g, "\\-");
	var oRegExp = new RegExp("(^|\\s)" + strClassName + "(\\s|$)");
	var oElement;
	for(var i=0; i<arrElements.length; i++){
		oElement = arrElements[i];		
		if(oRegExp.test(oElement.className)){
			arrReturnElements.push(oElement);
		}	
	}
	return (arrReturnElements)
}

//http://www.bigbold.com/snippets/posts/show/2630
function addClassName(objElement, strClass, blnMayAlreadyExist){
   if ( objElement.className ){
      var arrList = objElement.className.split(' ');
      if ( blnMayAlreadyExist ){
         var strClassUpper = strClass.toUpperCase();
         for ( var i = 0; i < arrList.length; i++ ){
            if ( arrList[i].toUpperCase() == strClassUpper ){
               arrList.splice(i, 1);
               i--;
             }
           }
      }
      arrList[arrList.length] = strClass;
      objElement.className = arrList.join(' ');
   }
   else{  
      objElement.className = strClass;
      }
}

//http://www.bigbold.com/snippets/posts/show/2630
function removeClassName(objElement, strClass){
   if ( objElement.className ){
      var arrList = objElement.className.split(' ');
      var strClassUpper = strClass.toUpperCase();
      for ( var i = 0; i < arrList.length; i++ ){
         if ( arrList[i].toUpperCase() == strClassUpper ){
            arrList.splice(i, 1);
            i--;
         }
      }
      objElement.className = arrList.join(' ');
   }
}

// Highlighter
// Highlights the parent's parent of the focused form control (input, textarea, checkbox, select, radio)

var Highlighter = window.Highlighter || {};

Highlighter.settings = {
  'row_class'   : 'highlight_row',
  'field_class' : 'highlight_field',
  'focus_class' : 'focus'
}

Highlighter.init = function() {
  var fields = getElementsByClassName(document, '*', Highlighter.settings.field_class);
	for(i = 0; i < fields.length; i++) {
		if(fields[i].type == 'radio' || fields[i].type == 'checkbox' || fields[i].type == 'file') {
			fields[i].onclick = function() {
			  Highlighter.unhighlight();
			  addClassName(this.parentNode.parentNode, "focused", true);
			};
			fields[i].onfocus = function() {
			  Highlighter.unhighlight();
			  addClassName(this.parentNode.parentNode, "focused", true);
		  };
		} else {
			fields[i].onfocus = function() {
			  Highlighter.unhighlight();
			  addClassName(this.parentNode.parentNode, "focused", true);
		  };
			fields[i].onblur = function() {
			  removeClassName(this.parentNode.parentNode, "focused");
      };
		}
	} 
};

Highlighter.unhighlight = function() {
  var fields = getElementsByClassName(document, '*', Highlighter.settings.field_class);
	for(i = 0; i < fields.length; i++) {
	  removeClassName(fields[i].parentNode.parentNode, "focused");
	} 
};
