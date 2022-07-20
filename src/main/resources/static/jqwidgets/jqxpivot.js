/*
jQWidgets v9.1.6 (2020-May)
Copyright (c) 2011-2020 jQWidgets.
License: https://jqwidgets.com/license/
*/
/* eslint-disable */

(function(a){(a.jqx.pivot=function(d,c){var b=this;b.dataAdapter=d;b.rows=[];b.columns=[];b.values=[];b.filters=[];b.pivotValuesOnRows=false;b.totals={rows:{subtotals:true,grandtotals:true},columns:{subtotals:true,grandtotals:true}};b.localization=null;b.aggregationFunctions={};b._initSettings={};a.extend(b.aggregationFunctions,b._defaultFunctions);if(c){b._initSettings=c;b.columns=c.columns||[];b.rows=c.rows||[];b.values=c.values||[];b.filters=c.filters||[];b.pivotValuesOnRows=c.pivotValuesOnRows||false;b.totals=c.totals;b.localization=c.localization;a.extend(b.aggregationFunctions,c.customAggregationFunctions||{});}
if(b.rows.length===0&&b.columns.length>0&&b.pivotValuesOnRows===false){b.pivotValuesOnRows=true;}
if(b.columns.length===0&&b.rows.length>0&&b.pivotValuesOnRows!==false){b.pivotValuesOnRows=false;}}),(a.jqx.pivot.prototype={_clear:function(){this._pivot={rows:[],columns:[],values:[],groups:[],filters:[]};this._rowsHierarchy={items:{},valueItems:{}};this._columnsHierarchy={items:{},valueItems:{}};this._hashPivotItemsToTableRows={};this._hashRefItems={};this._hierarchyItemKeyIndex=0;},_contains:function(b,e,c){if(!b){return false;}
for(var d=0;d<b.length;d++){if(c?b[d]===e:b[d].dataField===e){return true;}}
return false;},_getFieldIndex:function(b,d){if(!b){return-1;}
var c=0;for(;c<b.length;c++){if(b[c].name===d){break;}}
if(c===b.length){return-1;}
return c;},dataBind:function(){this._clear();if(!this.dataAdapter){return;}
this.dataAdapter.dataBind();var h=["columns","rows","values","filters"];for(var c in h){var f=h[c];for(var e=0;e<this[f].length;e++){var g=this[f][e];if(!g){continue;}
var b=false;if(f!=="values"&&f!=="filters"){for(var j in h){var d=h[j];if(f!==d){continue;}
b=this._contains(this._pivot[d],g.dataField);if(b){break;}}
if(b){continue;}}
this._pivot[f].push(g);}}
this._createPivot();},_isRowSkipped:function(d){for(var b in this.filters){var c=this.filters[b];if(!a.isFunction(c.filterFunction)){continue;}
if(c.dataField&&d[c.dataField]!==undefined){if(c.filterFunction(d[c.dataField])){return true;}}}
return false;},getItemsFilterStatus:function(k){var g={};var d=this.dataAdapter.records;for(var b=0;b<d.length;b++){var h=d[b];var j=h[k];if(g[j]){continue;}
for(var f in this.filters){var c=this.filters[f];if(c.dataField!==k){continue;}
var e=a.isFunction(c.filterFunction)&&c.filterFunction(j);g[j]=e;}}
return g;},_createPivot:function(){var h=this._pivot.values.length>0&&(this._pivot.rows.length>0||this._pivot.columns.length>0);if(h){var e={};var f={};this.hashSummaryPrefixes={};var b=this.dataAdapter.records;for(var d=0;d<b.length;d++){var g=b[d];if(this._isRowSkipped(g)){continue;}
var c=this.dataAdapter._source.datafields;this._appendPivotHierarchy(d,e,this._rowsHierarchy,c,this._pivot.rows,this.pivotValuesOnRows?this._pivot.values:null);this._appendPivotHierarchy(d,f,this._columnsHierarchy,c,this._pivot.columns,!this.pivotValuesOnRows?this._pivot.values:null);}
this._setupTotals(this._columnsHierarchy);this._setupTotals(this._rowsHierarchy);}
return;},_setupTotals:function(c){if(!this.totals){return;}
var b=c===this._columnsHierarchy?this.totals.columns:this.totals.rows;if(!b||(b.subtotals!==true&&b.grandtotals!==true)){return;}
this._addTotals(c.items,false,b);if((c===this._columnsHierarchy)===!this.pivotValuesOnRows){this._addvalueItemsToTotals(c.items);}},_addvalueItemsToTotals:function(e){for(var d in e){var m=e[d];if(m.isTotal){if(!this._hashRefItems[m.key]){continue;}
var n=this._hashRefItems[m.key].refItems;var g=[];for(var d=0;d<n.length;d++){var f=this._hashPivotItemsToTableRows[n[d].key];for(var b=0;b<f.length;b++){g.push(f[b]);}
if(d===0){m.valueItems={};for(var c in n[d].valueItems){var l=n[d].valueItems[c];var h={text:l.text,boundField:l.boundField,isTotal:true,key:this._hierarchyItemKeyIndex++};m.valueItems[c]=h;}}}
g.sort(function(j,i){return j-i;});for(var c in m.valueItems){this._hashPivotItemsToTableRows[m.valueItems[c].key]=g;}
delete this._hashRefItems[m.key];}else{this._addvalueItemsToTotals(m.items);}}},_clearTotals:function(d){for(var b in d){var c=d[b];if(c.isTotal){delete d[c];}else{this._clearTotals(d[b].items);}}},_addTotals:function(k,r,h){if(undefined===k){return;}
this._clearTotals(k);var q=[];var d=0;for(var g in k){var p=k[g];if(h.subtotals===true){this._addTotals(p.items,true,h);}
q.push(p);d++;}
if(d===0){return;}
if(!r&&h.grandtotals!=true){return;}
var l=r?"SubTotal":"Total";var b="_"+l+"_";var o=r?"SubTotal":"Total";var n=this.localization;if(n){if(r){o=a.jqx.getByPriority([n.subtotalstring,n.SubTotalString,o]);}else{o=a.jqx.getByPriority([n.grandtotalstring,n.GrandTotalString,o]);}}
var m=this._hierarchyItemKeyIndex++;var c=(k[b]={text:o,key:m});var e=[];for(var g=0;g<q.length;g++){for(var f=0;f<this._hashPivotItemsToTableRows[q[g].key].length;f++){e.push(this._hashPivotItemsToTableRows[q[g].key][f]);}}
this._hashRefItems[m]={item:c,refItems:q};this._hashPivotItemsToTableRows[m]=e.sort(function(j,i){return j-i;});c.isTotal=true;},_appendPivotHierarchy:function(l,b,k,n,j,e){var c=[];var g="";var m=this.dataAdapter.records[l];if(!this._getBoundFieldsValues(m,j,c)){return false;}
g.substring(0,g.length);var d=null;if(c.length==0&&e!=null){this._attachValueFieldAsSummaryItem(l,b,d,g,e);return true;}
for(var f=0;f<c.length;f++){g+="!_$%^&_";var h=c[f];g+=h;if(!b[g]){if(null==d){d=k.items[h]={text:h};}else{this._addSourceRecordToPivotItem(d.key,l);if(!d.items){d.items={};}
d=d.items[h]={text:h};}
this._applyBoundFieldProperties(j[f],d);b[g]=d;d.key=this._hierarchyItemKeyIndex++;}else{d=b[g];}
if(e==null||e.length==0){this._addSourceRecordToPivotItem(d.key,l);}else{this._addSourceRecordToPivotItem(d.key,l);if(e!=null){this._attachValueFieldAsSummaryItem(l,b,d,g,e);}}}
return true;},_attachValueFieldAsSummaryItem:function(h,b,j,d,c){for(var e=0;e<c.length;e++){var k=c[e];var l=null;var f=d+k.dataField+e;if(b[f]){l=b[f];}else{if(null!=j){if(!j.valueItems){j.valueItems={};}
l=j.valueItems[k.dataField+e]={text:k.text||k.dataField};}else{var g=this.pivotValuesOnRows?this._rowsHierarchy:this._columnsHierarchy;l=g.valueItems[k.dataField+e]={text:k.text||k.dataField};}
b[f]=l;l.key=this._hierarchyItemKeyIndex++;}
this._applyBoundFieldProperties(k,l);this._addSourceRecordToPivotItem(l.key,h);}},_applyBoundFieldProperties:function(b,c){c.boundField=b;if(b.text){c.boundFieldText=b.text;}},_addSourceRecordToPivotItem:function(b,d){var c=null;if(this._hashPivotItemsToTableRows[b]){c=this._hashPivotItemsToTableRows[b];}else{c=this._hashPivotItemsToTableRows[b]=[];}
if(c.length>0){if(c[c.length-1]==d){return;}}
c.push(d);},_sortedArrayIntersectAndDedup:function(d,b){var f=[];var e=0;var c=0;while(e<d.length&&c<b.length){if(d[e]<b[c]){e++;}else{if(d[e]>b[c]){c++;}else{if(f.length==0||f[f.length-1]!=d[e]){f.push(d[e]);}
e++;c++;}}}
return f;},_getBoundFieldValue:function(c,b){if(null==b){return null;}
return c[b];},_getBoundFieldsValues:function(e,b,f){for(var c=0;c<b.length;c++){var d=e[b[c].dataField];if(undefined===d){continue;}
f.push(d);}
return true;},_internalDrillThroughPivotCell:function(e,d,f){var c=this._hashPivotItemsToTableRows[e]||[];var b=this._hashPivotItemsToTableRows[d]||[];if(c.length==0||b.length==0){return[];}
return this._sortedArrayIntersectAndDedup(c,b);},drillThroughPivotCell:function(c,b){if(!c||undefined===c.key||!b||undefined===b.key){return[];}
var d=c.isTotal||b.isTotal;return this._internalDrillThroughPivotCell(c.key,b.key,d);},getFunctions:function(){return this.aggregationFunctions;},_defaultFunctions:{count:function(b){return b.length;},sum:function(b){var d=0;for(var c=0;c<b.length;c++){d+=b[c];}
return d;},min:function(b){var d=Infinity;for(var c=0;c<b.length;c++){if(b[c]<d){d=b[c];}}
return d;},max:function(c){var b=-Infinity;for(var d=0;d<c.length;d++){if(c[d]>b){b=c[d];}}
return b;},average:function(b){var c=a.jqx.pivot.prototype._defaultFunctions.count(b);if(c==0){return 0;}
var d=a.jqx.pivot.prototype._defaultFunctions.sum(b);return d / c;},percentPm:function(b){var c=$("#hiddenTotalPmGwp").val();var d=a.jqx.pivot.prototype._defaultFunctions.sum(b);return((d/c)*100);},percentCm:function(b){var c=$("#hiddenTotalCmGwp").val();var d=a.jqx.pivot.prototype._defaultFunctions.sum(b);return((d/c)*100);},product:function(b){var d=0;for(var c=0;c<b.length;c++){if(c==0){d=b[c];}else{d*=b[c];}}
return d;},},getCellValue:function(e,d){var f=this._getCellValueFromDataSource(e,d);if(isNaN(f)){return"";}
var b=this.pivotValuesOnRows?e.boundField:d.boundField;var c=this._formatValue(f,b.formatSettings,b.formatFunction);return{value:f,formattedValue:c};},getCellFormatSettings:function(d,c){var b=this.pivotValuesOnRows?d.boundField:c.boundField;if(b){return b.formatSettings;}
return undefined;},_getCellValueFromDataSource:function(p,c){if(p==undefined){throw"Invalid rowItem";}
if(c==undefined){throw"Invalid columnItem";}
var b=p.isTotal||c.isTotal;var g=this._internalDrillThroughPivotCell(p.key,c.key,b);if(g.length==0){return"";}
var h=this.pivotValuesOnRows?p.boundField:c.boundField;if(null==h){return undefined;}
var j=h["function"];var n=j||"count";if(typeof n=="String"){n=n.toLowerCase();}
try{var d=[];for(var k=0;k<g.length;k++){var f=g[k];var l=this.dataAdapter.records[f];var o=this._getBoundFieldValue(l,h.dataField);d.push(parseFloat(o));}
if(typeof n=="string"){n=this.aggregationFunctions[n];}
if(typeof n=="function"){return n(d);}}catch(m){return NaN;}
return NaN;},_formatValue:function(c,f,b){if(c==undefined){return"";}
if(this._isObject(c)&&!this._isDate(c)&&!b){return"";}
if(b){if(!a.isFunction(b)){return c.toString();}
try{return b(c);}catch(d){return d.message;}}
if(this._isNumber(c)){return this._formatNumber(c,f);}
if(this._isDate(c)){return this._formatDate(c,f);}
if(f){return(f.prefix||"")+c.toString()+(f.sufix||"");}
return c.toString();},_isNumberAsString:function(d){if(typeof d!="string"){return false;}
d=a.trim(d);for(var b=0;b<d.length;b++){var c=d.charAt(b);if((c>="0"&&c<="9")||c==","||c=="."){continue;}
if(c=="-"&&b==0){continue;}
if((c=="("&&b==0)||(c==")"&&b==d.length-1)){continue;}
return false;}
return true;},_castAsDate:function(f,c){if(f instanceof Date&&!isNaN(f)){return f;}
if(typeof f=="string"){var b;if(c){b=a.jqx.dataFormat.parsedate(f,c);if(this._isDate(b)){return b;}}
if(this._autoDateFormats){for(var e=0;e<this._autoDateFormats.length;e++){b=a.jqx.dataFormat.parsedate(f,this._autoDateFormats[e]);if(this._isDate(b)){return b;}}}else{this._autoDateFormats=[];}
var d=this._detectDateFormat(f);if(d){b=a.jqx.dataFormat.parsedate(f,d);if(this._isDate(b)){this._autoDateFormats.push(d);return b;}}
b=new Date(f);if(this._isDate(b)){if(f.indexOf(":")==-1){b.setHours(0,0,0,0);}}
return b;}
return undefined;},_castAsNumber:function(c){if(c instanceof Date&&!isNaN(c)){return c.valueOf();}
if(typeof c=="string"){if(this._isNumber(c)){c=parseFloat(c);}else{if(!/[a-zA-Z]/.test(c)){var b=new Date(c);if(b!=undefined){c=b.valueOf();}}}}
return c;},_isNumber:function(b){if(typeof b=="string"){if(this._isNumberAsString(b)){b=parseFloat(b);}}
return typeof b==="number"&&isFinite(b);},_isDate:function(b){return b instanceof Date&&!isNaN(b.getDate());},_isBoolean:function(b){return typeof b==="boolean";},_isObject:function(b){return(b&&(typeof b==="object"||a.isFunction(b)))||false;},_formatDate:function(d,c){var b=d.toString();if(c){if(c.dateFormat){b=a.jqx.dataFormat.formatDate(d,c.dateFormat);}
b=(c.prefix||"")+b+(c.sufix||"");}
return b;},_formatNumber:function(n,e){if(!this._isNumber(n)){return n;}
e=e||{};var q=".";var o="";var r=this;if(r.localization){q=r.localization.decimalSeparator||r.localization.decimalseparator||q;o=r.localization.thousandsSeparator||r.localization.thousandsseparator||o;}
if(e.decimalSeparator){q=e.decimalSeparator;}
if(e.thousandsSeparator){o=e.thousandsSeparator;}
var m=e.prefix||"";var p=e.sufix||"";var h=e.decimalPlaces;if(isNaN(h)){h=this._getDecimalPlaces([n],undefined,3);}
var l=e.negativeWithBrackets||false;var g=n<0;if(g&&l){n*=-1;}
var d=n.toString();var b;var k=Math.pow(10,h);d=(Math.round(n*k)/ k).toString();if(isNaN(d)){d="";}
b=d.lastIndexOf(".");if(h>0){if(b<0){d+=q;b=d.length-1;}else{if(q!=="."){d=d.replace(".",q);}}
while(d.length-1-b<h){d+="0";}}
b=d.lastIndexOf(q);b=b>-1?b:d.length;var f=d.substring(b);var c=0;for(var j=b;j>0;j--,c++){if(c%3===0&&j!==b&&(!g||j>1||(g&&l))){f=o+f;}
f=d.charAt(j-1)+f;}
d=f;if(g&&l){d="("+d+")";}
return m+d+p;},_getDecimalPlaces:function(b,g,c){var h=0;if(isNaN(c)){c=10;}
for(var f=0;f<b.length;f++){var k=g===undefined?b[f]:b[f][g];if(isNaN(k)){continue;}
var d=k.toString();for(var e=0;e<d.length;e++){if(d[e]<"0"||d[e]>"9"){h=d.length-(e+1);if(h>=0){return Math.min(h,c);}}}
if(h>0){k*=Math.pow(10,h);}
while(Math.round(k)!=k&&h<c){h++;k*=10;}}
return h;},_defaultNumberFormat:{prefix:"",sufix:"",decimalSeparator:".",thousandsSeparator:",",decimalPlaces:2,negativeWithBrackets:false},_detectDateFormat:function(g,c){var h={en_US_d:"M/d/yyyy",en_US_D:"dddd, MMMM dd, yyyy",en_US_t:"h:mm tt",en_US_T:"h:mm:ss tt",en_US_f:"dddd, MMMM dd, yyyy h:mm tt",en_US_F:"dddd, MMMM dd, yyyy h:mm:ss tt",en_US_M:"MMMM dd",en_US_Y:"yyyy MMMM",en_US_S:"yyyy\u0027-\u0027MM\u0027-\u0027dd\u0027T\u0027HH\u0027:\u0027mm\u0027:\u0027ss",en_CA_d:"dd/MM/yyyy",en_CA_D:"MMMM-dd-yy",en_CA_f:"MMMM-dd-yy h:mm tt",en_CA_F:"MMMM-dd-yy h:mm:ss tt",ISO:"yyyy-MM-dd hh:mm:ss",ISO2:"yyyy-MM-dd HH:mm:ss",d1:"dd.MM.yyyy",d2:"dd-MM-yyyy",zone1:"yyyy-MM-ddTHH:mm:ss-HH:mm",zone2:"yyyy-MM-ddTHH:mm:ss+HH:mm",custom:"yyyy-MM-ddTHH:mm:ss.fff",custom2:"yyyy-MM-dd HH:mm:ss.fff",de_DE_d:"dd.MM.yyyy",de_DE_D:"dddd, d. MMMM yyyy",de_DE_t:"HH:mm",de_DE_T:"HH:mm:ss",de_DE_f:"dddd, d. MMMM yyyy HH:mm",de_DE_F:"dddd, d. MMMM yyyy HH:mm:ss",de_DE_M:"dd MMMM",de_DE_Y:"MMMM yyyy",fr_FR_d:"dd/MM/yyyy",fr_FR_D:"dddd d MMMM yyyy",fr_FR_t:"HH:mm",fr_FR_T:"HH:mm:ss",fr_FR_f:"dddd d MMMM yyyy HH:mm",fr_FR_F:"dddd d MMMM yyyy HH:mm:ss",fr_FR_M:"d MMMM",fr_FR_Y:"MMMM yyyy",it_IT_d:"dd/MM/yyyy",it_IT_D:"dddd d MMMM yyyy",it_IT_t:"HH:mm",it_IT_T:"HH:mm:ss",it_IT_f:"dddd d MMMM yyyy HH:mm",it_IT_F:"dddd d MMMM yyyy HH:mm:ss",it_IT_M:"dd MMMM",it_IT_Y:"MMMM yyyy",ru_RU_d:"dd.MM.yyyy",ru_RU_D:"d MMMM yyyy '?.'",ru_RU_t:"H:mm",ru_RU_T:"H:mm:ss",ru_RU_f:"d MMMM yyyy '?.' H:mm",ru_RU_F:"d MMMM yyyy '?.' H:mm:ss",ru_RU_Y:"MMMM yyyy",cs_CZ_d:"d.M.yyyy",cs_CZ_D:"d. MMMM yyyy",cs_CZ_t:"H:mm",cs_CZ_T:"H:mm:ss",cs_CZ_f:"d. MMMM yyyy H:mm",cs_CZ_F:"d. MMMM yyyy H:mm:ss",cs_CZ_M:"dd MMMM",cs_CZ_Y:"MMMM yyyy",he_IL_d:"dd MMMM yyyy",he_IL_D:"dddd dd MMMM yyyy",he_IL_t:"HH:mm",he_IL_T:"HH:mm:ss",he_IL_f:"dddd dd MMMM yyyy HH:mm",he_IL_F:"dddd dd MMMM yyyy HH:mm:ss",he_IL_M:"dd MMMM",he_IL_Y:"MMMM yyyy",hr_HR_d:"d.M.yyyy.",hr_HR_D:"d. MMMM yyyy.",hr_HR_t:"H:mm",hr_HR_T:"H:mm:ss",hr_HR_f:"d. MMMM yyyy. H:mm",hr_HR_F:"d. MMMM yyyy. H:mm:ss",hr_HR_M:"d. MMMM",hu_HU_d:"yyyy.MM.dd.",hu_HU_D:"yyyy. MMMM d.",hu_HU_t:"H:mm",hu_HU_T:"H:mm:ss",hu_HU_f:"yyyy. MMMM d. H:mm",hu_HU_F:"yyyy. MMMM d. H:mm:ss",hu_HU_M:"MMMM d.",hu_HU_Y:"yyyy. MMMM",jp_JP_d:"gg y/M/d",jp_JP_D:"gg y'?'M'?'d'?'",jp_JP_t:"H:mm",jp_JP_T:"H:mm:ss",jp_JP_f:"gg y'?'M'?'d'?' H:mm",jp_JP_F:"gg y'?'M'?'d'?' H:mm:ss",jp_JP_M:"M'?'d'?'",jp_JP_Y:"gg y'?'M'?'",lt_LT_d:"yyyy.MM.dd",lt_LT_D:"yyyy 'm.' MMMM d 'd.'",lt_LT_t:"HH:mm",lt_LT_T:"HH:mm:ss",lt_LT_f:"yyyy 'm.' MMMM d 'd.' HH:mm",lt_LT_F:"yyyy 'm.' MMMM d 'd.' HH:mm:ss",lt_LT_M:"MMMM d 'd.'",lt_LT_Y:"yyyy 'm.' MMMM",sa_IN_d:"dd-MM-yyyy",sa_IN_D:"dd MMMM yyyy dddd",sa_IN_t:"HH:mm",sa_IN_T:"HH:mm:ss",sa_IN_f:"dd MMMM yyyy dddd HH:mm",sa_IN_F:"dd MMMM yyyy dddd HH:mm:ss",sa_IN_M:"dd MMMM",basic_y:"yyyy",basic_ym:"yyyy-MM",basic_d:"yyyy-MM-dd",basic_dhm:"yyyy-MM-dd hh:mm",basic_bhms:"yyyy-MM-dd hh:mm:ss",basic2_ym:"MM-yyyy",basic2_d:"MM-dd-yyyy",basic2_dhm:"MM-dd-yyyy hh:mm",basic2_dhms:"MM-dd-yyyy hh:mm:ss",basic3_ym:"yyyy/MM",basic3_d:"yyyy/MM/dd",basic3_dhm:"yyyy/MM/dd hh:mm",basic3_bhms:"yyyy/MM/dd hh:mm:ss",basic4_ym:"MM/yyyy",basic4_d:"MM/dd/yyyy",basic4_dhm:"MM/dd/yyyy hh:mm",basic4_dhms:"MM/dd/yyyy hh:mm:ss",};if(c){h=a.extend({},h,c);}
var f=[];if(!a.isArray(g)){f.push(g);}else{f=g;}
for(var d in h){h[d]={format:h[d],count:0};}
for(var e=0;e<f.length;e++){var k=f[e];if(k==null||k==undefined){continue;}
for(var d in h){var l=a.jqx.dataFormat.parsedate(k,h[d].format);if(l!=null){h[d].count++;}}}
var b={key:undefined,count:0};for(var d in h){if(h[d].count>b.count){b.key=d;b.count=h[d].count;}}
return b.key?h[b.key].format:"";},});})(jqxBaseFramework);