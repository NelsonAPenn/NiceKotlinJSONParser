package com.github.nelsonapenn.sleek

import java.lang.NumberFormatException


class Sleek(var source:String) {
    var string:String=""
        get() {
            if(source[0]=='[')
                throw AintNoJSONStringLiteralException("Attempt to convert JSON Array to string literal.")
            if(source[0]=='{')
                throw AintNoJSONStringLiteralException("Attempt to convert JSON Object to string literal.")
            var v: String = ""
            var flag: Boolean = false
            for (c: Char in source) {
                if (flag) {
                    v += c
                    flag = false
                    continue
                }
                if (c == '\\') {
                    flag = true
                    continue
                }
                if (c != '\"')
                    v += c
            }
            return v
        }
    var int:Int=0
        get(){
            try {
                return string.toInt()
            }
            catch (e:NumberFormatException)
            {
                throw AintNoJSONIntLiteralException("Attempt to get an int value from JSON that doesn't represent an int.")
            }
        }
    var array:Array<Sleek> = Array(0){Sleek("")}
        get(){
            if(source[0]!='[')
                throw AintNoJSONArrayException("Attempt to convert a non JSON Array string to an array.")
            var output=Array(0){Sleek("")}
            var i=0
            while(true)
            {
                try{
                    output+=get(i)
                    i++
                }
                catch(e:IndexOutOfBoundsException)
                {
                    break
                }
            }
            return output
        }
    var map:Map<String,Sleek> = HashMap()
        get(){
            if(source[0]!='{')
                throw AintNoJSONObjectException("Attempt to convert a non JSON object string to a map.")
            var output=HashMap<String,Sleek>()
            var i=0
            var pos=1

            while(pos<source.length && pos!=-1)
            {
                var key=""
                var value=""
                pos++
                while(source[pos]!='\"' || (pos>=1 && source[pos-1]=='\\'))
                {
                    key+=source[pos++]
                }
                // now at the "
                pos++
                // now at the :
                value=grabToNext(pos++)
                output.put(key,Sleek(value))
                pos=seekNext(pos)
            }
            return output
        }
    private fun seekNext(pos: Int): Int {
        var p = pos
        var lvl:Int=0
        while (p<source.length && (source[p] != ',' || lvl!=0)){
            if(source[p]=='{'||source[p]=='[')
                lvl++
            if(source[p]=='}' || source[p]==']')
                lvl--
            p++
        }
        p++
        if(p>=source.length)
            return -1
        return p
    }
    private fun grabToNext(pos: Int): String {
        var p = pos
        var value: String=""
        var lvl:Int=0
        while (p < source.length && (source[p] != ',' || lvl!=0 ))
        {
            if(source[p]=='{'||source[p]=='[')
                lvl++
            if(source[p]=='}' || source[p]==']')
                lvl--
            if(lvl<0)
                break
            if(source[p]!='\"' || (p>=1 && source[p-1]=='\\'))
                value+=source[p]
            p++
        }
        return value
    }
    operator fun get(i: Int): Sleek {
        if(source[0]!='[')
            throw AintNoJSONArrayException("Attempt to access element of JSON string that is not a JSON Array.")
        var j:Int=0
        var pos:Int=1
        while(j<i) {
            pos = seekNext(pos)
            if(pos==-1)
                throw IndexOutOfBoundsException("")
            j++
        }
        var output:String=grabToNext(pos)
        return Sleek(output)
    }
    operator fun rem(myProperty:String): Sleek {
        if (source[0] != '{')
            throw AintNoJSONObjectException("Attempt to access property of JSON string that is not a JSON object.")
        var property='\"'+myProperty+'\"'
        var i: Int = 1
        var lvl: Int = 1
        while (lvl >= 1 && i + property.length - 1 < source.length){
            if (source[i] == '{' || source[i] == '[')
                lvl++
            if (source[i] == '}' || source[i] == ']')
                lvl--
            if (lvl == 1 && source.substring(i, (i + property.length)).contentEquals(property) && source[i+property.length]==':') { //check for if it is a match and if it is actually a property.
                var output = grabToNext(i + property.length + 1)
                return Sleek(output)
            }
            i++
        }
        return Sleek("")
    }
}
class AintNoJSONIntLiteralException(message:String):Exception(message)
class AintNoJSONStringLiteralException(message:String):Exception(message)
class AintNoJSONArrayException(message:String):Exception(message)
class AintNoJSONObjectException(message:String):Exception(message)