package com.example.nelsonpenn.connect

import com.bumptech.glide.load.engine.bitmap_recycle.IntegerArrayAdapter
import kotlin.reflect.KProperty

class JSONData(var source:String) {
    var value:String=""
    get(){
        var v:String=""
        for(c:Char in source)
            if(c!='\"')
                v+=c
        return v
    }
    fun seekNext(pos: Int): Int {
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
        return when(p){
            source.length->-1
            else -> p
        }
    }

    fun grabToNext(pos: Int): String {
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
            value+=source[p]
            p++
        }
        return value

    }


    operator fun get(i: Int): JSONData {
        var j:Int=0
        var pos:Int=1
        while(j<i) {
            pos = seekNext(pos)
            j++
        }
        var output:String=grabToNext(pos)
        return JSONData(output)

    }

    operator fun rem(myProperty:String): JSONData {
        if (source[0] != '{')
            return JSONData("")
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
                return JSONData(output)
            }
            i++
        }
        return JSONData("")
    }


}
