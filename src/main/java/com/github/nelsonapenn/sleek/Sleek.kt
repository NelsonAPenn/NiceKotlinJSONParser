package com.github.nelsonapenn.sleek

import java.lang.Integer.parseInt
import java.lang.Long.parseLong


class Sleek(private var source:String) {
    private data class JSONCharacter(val level:Int=0, val type:Char=' ')
    private var meta: Array<JSONCharacter>

    init {
        meta = Array(source.length) { JSONCharacter() }
        var i=0
        var flag=false
        var lvl=0
        while(i<source.length)
        {
            if(!flag)//if not in a string, determine lvl. Endpoints should not be a problem because they're "
            {
                var shouldContinue=true
                when(source[i])
                {
                    '['->{
                        meta[i++]=JSONCharacter(lvl++,'[')

                    }
                    '{'->{
                        meta[i++]=JSONCharacter(lvl++,'{')
                    }
                    ']'->{
                        meta[i++]=JSONCharacter(--lvl,']')
                    }
                    '}'->{
                        meta[i++]=JSONCharacter(--lvl,'}')
                    }
                    ','->{
                        meta[i++]=JSONCharacter(lvl,',')
                    }
                    ':'->{
                        meta[i++]=JSONCharacter(lvl,':')
                    }
                    else->{
                        shouldContinue=false
                    }
                }
                if(shouldContinue)
                    continue
            }
            if(source[i]=='\"' && (i==0 || i>0 && source[i-1]!='\\')) //string, flag it or stop flagging it if you already have
            {
                if(!flag) {
                    flag = true
                    meta[i++]=JSONCharacter(lvl,'s')
                    continue
                }
                else
                {
                    flag=false
                    meta[i++] = JSONCharacter(lvl,'s')
                    continue
                }
            }

            if(flag)//string
            {
                meta[i]=JSONCharacter(lvl,'s')
            }
            else
            {
                meta[i]=JSONCharacter(lvl,'i') //should be a number
            }

            i++

        }
    }


    var string:String=""
        get() {
            if(source.isEmpty())
                return ""
            if(meta[0].type=='[')
                throw AintNoJSONStringLiteralException("Attempt to convert JSON Array to string literal.")
            if(meta[0].type=='{')
                throw AintNoJSONStringLiteralException("Attempt to convert JSON Object to string literal.")
            if(meta[0].type=='i')
                throw AintNoJSONStringLiteralException("Attempt to convert JSON integer value to string literal.")
            var v = ""
            var flag = false
            var i=1
            while(i<source.length-1)
            {
                val c:Char=source[i]
                //remove escapes on escape characters
                if (flag) {
                    when(c)
                    {
                        //newline
                        'n'->v+="\n"
                        //unicode
                        'u'->{
                            v+=(Character.toChars(Integer.parseInt(source.substring(i+1,i+5),16)))[0]
                            i+=4
                        }
                        //tab
                        't'->v+="\t"
                        //other escaped characters can just be added into the string directly
                        else-> v+=c
                    }
                    flag = false
                    i++
                    continue
                }
                if (c == '\\') {
                    flag = true
                    i++
                    continue
                }
                v += c
                i++
            }
            return v
        }
    var int:Int=0
        get(){
            if(source.isEmpty())
                throw AintNoJSONIntLiteralException("Attempt to get an int value from JSON that doesn't represent an int.")
            else if(meta[0].type!='i')
                throw AintNoJSONIntLiteralException("Attempt to get an int value from JSON that doesn't represent an int.")
            return source.toInt()
        }
    var array:Array<Sleek> = Array(0){Sleek("")}
        get(){
            if(meta[0].type!='[')
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
            if(meta[0].type!='{')
                throw AintNoJSONObjectException("Attempt to convert a non JSON object string to a map.")
            var output=HashMap<String,Sleek>()
            var pos=1

            while(pos<source.length && pos!=-1)
            {
                var key=""
                var value=""
                key=grabValue(pos)
                key=key.substring(1,key.length-1)

                pos+=key.length+3 //account for quotes and :

                value=grabValue(pos)
                output[key]=Sleek(value)
                pos=seekNext(pos)
            }
            return output
        }
    var isJSONObject:Boolean=false
        get()
        {
            if(source.isEmpty())
                return false
            return meta[0].type=='{'
        }
    var isJSONArray:Boolean=false
        get()
        {
            if(source.isEmpty())
                return false
            return meta[0].type=='['
        }
    var isJSONLiteral:Boolean=false
        get()
        {
            return !isJSONObject && !isJSONArray
        }
    override fun toString():String=source
    private fun seekNext(pos: Int): Int {
        var p = pos
        val lvl=meta[pos].level
        while (p<source.length && (meta[p].type != ',' || meta[p].level!=lvl)){
            p++
        }
        p++
        if(p>=source.length)
            return -1
        return p
    }
    private fun grabValue(pos: Int): String {
        var p = pos
        var value: String=""
        val lvl=meta[pos].level
        val type= meta[pos].type
        when(type){
            'i'->{
                while(p<source.length && meta[p].type==type)
                {
                    value+=source[p++]
                }
            }
            's'->{
                while(p<source.length && meta[p].type==type)
                {
                    value+=source[p++]
                }
            }
            '{'-> {
                value+=source[p++]
                while (p < source.length && (meta[p - 1].type != '}' || meta[p - 1].level > lvl))
                {
                    value+=source[p++]
                }

            }
            '['->{
                value+=source[p++]
                while (p < source.length && (meta[p - 1].type != ']' || meta[p - 1].level > lvl))
                {
                    value+=source[p++]
                }
            }
        }
        return value
    }
    operator fun get(i: Int): Sleek {
        if(meta[0].type!='[')
            throw AintNoJSONArrayException("Attempt to access element of JSON string that is not a JSON Array.")
        var j=0
        var pos=1
        while(j<i) {
            pos = seekNext(pos)
            if(pos==-1)
                throw IndexOutOfBoundsException("")
            j++
        }
        var output:String=grabValue(pos)
        return Sleek(output)
    }
    operator fun rem(property:String): Sleek {
        if (meta[0].type != '{')
            throw AintNoJSONObjectException("Attempt to access property of JSON string that is not a JSON object.")
        var myProperty='\"'+property+'\"'
        var pos=1

        while(pos<source.length && pos!=-1)
        {
            var key:String=""
            var value:String=""
            key=grabValue(pos)
            if(myProperty.contentEquals(key)) {
                pos += key.length + 1 //account for quotes and :
                value = grabValue(pos)
                return Sleek(value)
            }
            pos=seekNext(pos)
        }
        return Sleek("")
    }
}
class AintNoJSONIntLiteralException(message:String):Exception(message)
class AintNoJSONStringLiteralException(message:String):Exception(message)
class AintNoJSONArrayException(message:String):Exception(message)
class AintNoJSONObjectException(message:String):Exception(message)